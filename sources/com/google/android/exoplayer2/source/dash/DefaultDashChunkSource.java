package com.google.android.exoplayer2.source.dash;

import android.net.Uri;
import android.os.SystemClock;
import android.support.annotation.CheckResult;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.extractor.ChunkIndex;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.extractor.mkv.MatroskaExtractor;
import com.google.android.exoplayer2.extractor.mp4.FragmentedMp4Extractor;
import com.google.android.exoplayer2.extractor.rawcc.RawCcExtractor;
import com.google.android.exoplayer2.source.BehindLiveWindowException;
import com.google.android.exoplayer2.source.chunk.BaseMediaChunkIterator;
import com.google.android.exoplayer2.source.chunk.Chunk;
import com.google.android.exoplayer2.source.chunk.ChunkExtractorWrapper;
import com.google.android.exoplayer2.source.chunk.ChunkHolder;
import com.google.android.exoplayer2.source.chunk.ContainerMediaChunk;
import com.google.android.exoplayer2.source.chunk.InitializationChunk;
import com.google.android.exoplayer2.source.chunk.MediaChunk;
import com.google.android.exoplayer2.source.chunk.MediaChunkIterator;
import com.google.android.exoplayer2.source.chunk.SingleSampleMediaChunk;
import com.google.android.exoplayer2.source.dash.PlayerEmsgHandler.PlayerTrackEmsgHandler;
import com.google.android.exoplayer2.source.dash.manifest.AdaptationSet;
import com.google.android.exoplayer2.source.dash.manifest.DashManifest;
import com.google.android.exoplayer2.source.dash.manifest.RangedUri;
import com.google.android.exoplayer2.source.dash.manifest.Representation;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.HttpDataSource.InvalidResponseCodeException;
import com.google.android.exoplayer2.upstream.LoaderErrorThrower;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DefaultDashChunkSource implements DashChunkSource {
    private final int[] adaptationSetIndices;
    private final DataSource dataSource;
    private final long elapsedRealtimeOffsetMs;
    private IOException fatalError;
    private long liveEdgeTimeUs = C0555C.TIME_UNSET;
    private DashManifest manifest;
    private final LoaderErrorThrower manifestLoaderErrorThrower;
    private final int maxSegmentsPerLoad;
    private boolean missingLastSegment;
    private int periodIndex;
    @Nullable
    private final PlayerTrackEmsgHandler playerTrackEmsgHandler;
    protected final RepresentationHolder[] representationHolders;
    private final TrackSelection trackSelection;
    private final int trackType;

    protected static final class RepresentationHolder {
        @Nullable
        final ChunkExtractorWrapper extractorWrapper;
        private final long periodDurationUs;
        public final Representation representation;
        @Nullable
        public final DashSegmentIndex segmentIndex;
        private final long segmentNumShift;

        RepresentationHolder(long periodDurationUs, int trackType, Representation representation, boolean enableEventMessageTrack, boolean enableCea608Track, TrackOutput playerEmsgTrackOutput) {
            this(periodDurationUs, representation, createExtractorWrapper(trackType, representation, enableEventMessageTrack, enableCea608Track, playerEmsgTrackOutput), 0, representation.getIndex());
        }

        private RepresentationHolder(long periodDurationUs, Representation representation, @Nullable ChunkExtractorWrapper extractorWrapper, long segmentNumShift, @Nullable DashSegmentIndex segmentIndex) {
            this.periodDurationUs = periodDurationUs;
            this.representation = representation;
            this.segmentNumShift = segmentNumShift;
            this.extractorWrapper = extractorWrapper;
            this.segmentIndex = segmentIndex;
        }

        @CheckResult
        RepresentationHolder copyWithNewRepresentation(long newPeriodDurationUs, Representation newRepresentation) throws BehindLiveWindowException {
            long j = newPeriodDurationUs;
            DashSegmentIndex oldIndex = this.representation.getIndex();
            DashSegmentIndex newIndex = newRepresentation.getIndex();
            if (oldIndex == null) {
                return new RepresentationHolder(newPeriodDurationUs, newRepresentation, r0.extractorWrapper, r0.segmentNumShift, oldIndex);
            } else if (oldIndex.isExplicit()) {
                int oldIndexSegmentCount = oldIndex.getSegmentCount(j);
                if (oldIndexSegmentCount == 0) {
                    return new RepresentationHolder(newPeriodDurationUs, newRepresentation, r0.extractorWrapper, r0.segmentNumShift, newIndex);
                }
                long newSegmentNumShift;
                long oldIndexLastSegmentNum = (oldIndex.getFirstSegmentNum() + ((long) oldIndexSegmentCount)) - 1;
                long oldIndexEndTimeUs = oldIndex.getTimeUs(oldIndexLastSegmentNum) + oldIndex.getDurationUs(oldIndexLastSegmentNum, j);
                long newIndexFirstSegmentNum = newIndex.getFirstSegmentNum();
                long newIndexStartTimeUs = newIndex.getTimeUs(newIndexFirstSegmentNum);
                long newSegmentNumShift2 = r0.segmentNumShift;
                if (oldIndexEndTimeUs == newIndexStartTimeUs) {
                    newSegmentNumShift = newSegmentNumShift2 + ((oldIndexLastSegmentNum + 1) - newIndexFirstSegmentNum);
                } else if (oldIndexEndTimeUs >= newIndexStartTimeUs) {
                    newSegmentNumShift = newSegmentNumShift2 + (oldIndex.getSegmentNum(newIndexStartTimeUs, j) - newIndexFirstSegmentNum);
                } else {
                    long j2 = newIndexFirstSegmentNum;
                    throw new BehindLiveWindowException();
                }
                return new RepresentationHolder(newPeriodDurationUs, newRepresentation, r0.extractorWrapper, newSegmentNumShift, newIndex);
            } else {
                return new RepresentationHolder(newPeriodDurationUs, newRepresentation, r0.extractorWrapper, r0.segmentNumShift, newIndex);
            }
        }

        @CheckResult
        RepresentationHolder copyWithNewSegmentIndex(DashSegmentIndex segmentIndex) {
            return new RepresentationHolder(this.periodDurationUs, this.representation, this.extractorWrapper, this.segmentNumShift, segmentIndex);
        }

        public long getFirstSegmentNum() {
            return this.segmentIndex.getFirstSegmentNum() + this.segmentNumShift;
        }

        public int getSegmentCount() {
            return this.segmentIndex.getSegmentCount(this.periodDurationUs);
        }

        public long getSegmentStartTimeUs(long segmentNum) {
            return this.segmentIndex.getTimeUs(segmentNum - this.segmentNumShift);
        }

        public long getSegmentEndTimeUs(long segmentNum) {
            return getSegmentStartTimeUs(segmentNum) + this.segmentIndex.getDurationUs(segmentNum - this.segmentNumShift, this.periodDurationUs);
        }

        public long getSegmentNum(long positionUs) {
            return this.segmentIndex.getSegmentNum(positionUs, this.periodDurationUs) + this.segmentNumShift;
        }

        public RangedUri getSegmentUrl(long segmentNum) {
            return this.segmentIndex.getSegmentUrl(segmentNum - this.segmentNumShift);
        }

        public long getFirstAvailableSegmentNum(DashManifest manifest, int periodIndex, long nowUnixTimeUs) {
            DashManifest dashManifest = manifest;
            if (getSegmentCount() != -1 || dashManifest.timeShiftBufferDepthMs == C0555C.TIME_UNSET) {
                RepresentationHolder representationHolder = this;
                return getFirstSegmentNum();
            }
            representationHolder = this;
            return Math.max(getFirstSegmentNum(), getSegmentNum(((nowUnixTimeUs - C0555C.msToUs(dashManifest.availabilityStartTimeMs)) - C0555C.msToUs(manifest.getPeriod(periodIndex).startMs)) - C0555C.msToUs(dashManifest.timeShiftBufferDepthMs)));
        }

        public long getLastAvailableSegmentNum(DashManifest manifest, int periodIndex, long nowUnixTimeUs) {
            int availableSegmentCount = getSegmentCount();
            if (availableSegmentCount == -1) {
                return getSegmentNum((nowUnixTimeUs - C0555C.msToUs(manifest.availabilityStartTimeMs)) - C0555C.msToUs(manifest.getPeriod(periodIndex).startMs)) - 1;
            }
            return (getFirstSegmentNum() + ((long) availableSegmentCount)) - 1;
        }

        private static boolean mimeTypeIsWebm(String mimeType) {
            if (!mimeType.startsWith(MimeTypes.VIDEO_WEBM) && !mimeType.startsWith(MimeTypes.AUDIO_WEBM)) {
                if (!mimeType.startsWith(MimeTypes.APPLICATION_WEBM)) {
                    return false;
                }
            }
            return true;
        }

        private static boolean mimeTypeIsRawText(String mimeType) {
            if (!MimeTypes.isText(mimeType)) {
                if (!MimeTypes.APPLICATION_TTML.equals(mimeType)) {
                    return false;
                }
            }
            return true;
        }

        @Nullable
        private static ChunkExtractorWrapper createExtractorWrapper(int trackType, Representation representation, boolean enableEventMessageTrack, boolean enableCea608Track, TrackOutput playerEmsgTrackOutput) {
            String containerMimeType = representation.format.containerMimeType;
            if (mimeTypeIsRawText(containerMimeType)) {
                return null;
            }
            Extractor extractor;
            if (MimeTypes.APPLICATION_RAWCC.equals(containerMimeType)) {
                extractor = new RawCcExtractor(representation.format);
            } else if (mimeTypeIsWebm(containerMimeType)) {
                extractor = new MatroskaExtractor(1);
            } else {
                List<Format> closedCaptionFormats;
                int flags = 0;
                if (enableEventMessageTrack) {
                    flags = 0 | 4;
                }
                if (enableCea608Track) {
                    closedCaptionFormats = Collections.singletonList(Format.createTextSampleFormat(null, MimeTypes.APPLICATION_CEA608, 0, null));
                } else {
                    closedCaptionFormats = Collections.emptyList();
                }
                extractor = new FragmentedMp4Extractor(flags, null, null, null, closedCaptionFormats, playerEmsgTrackOutput);
            }
            return new ChunkExtractorWrapper(extractor, trackType, representation.format);
        }
    }

    public static final class Factory implements com.google.android.exoplayer2.source.dash.DashChunkSource.Factory {
        private final com.google.android.exoplayer2.upstream.DataSource.Factory dataSourceFactory;
        private final int maxSegmentsPerLoad;

        public Factory(com.google.android.exoplayer2.upstream.DataSource.Factory dataSourceFactory) {
            this(dataSourceFactory, 1);
        }

        public Factory(com.google.android.exoplayer2.upstream.DataSource.Factory dataSourceFactory, int maxSegmentsPerLoad) {
            this.dataSourceFactory = dataSourceFactory;
            this.maxSegmentsPerLoad = maxSegmentsPerLoad;
        }

        public DashChunkSource createDashChunkSource(LoaderErrorThrower manifestLoaderErrorThrower, DashManifest manifest, int periodIndex, int[] adaptationSetIndices, TrackSelection trackSelection, int trackType, long elapsedRealtimeOffsetMs, boolean enableEventMessageTrack, boolean enableCea608Track, @Nullable PlayerTrackEmsgHandler playerEmsgHandler, @Nullable TransferListener transferListener) {
            TransferListener transferListener2 = transferListener;
            DataSource dataSource = this.dataSourceFactory.createDataSource();
            if (transferListener2 != null) {
                dataSource.addTransferListener(transferListener2);
            }
            return new DefaultDashChunkSource(manifestLoaderErrorThrower, manifest, periodIndex, adaptationSetIndices, trackSelection, trackType, dataSource, elapsedRealtimeOffsetMs, r0.maxSegmentsPerLoad, enableEventMessageTrack, enableCea608Track, playerEmsgHandler);
        }
    }

    protected static final class RepresentationSegmentIterator extends BaseMediaChunkIterator {
        private final RepresentationHolder representationHolder;

        public RepresentationSegmentIterator(RepresentationHolder representation, long segmentNum, long lastAvailableSegmentNum) {
            super(segmentNum, lastAvailableSegmentNum);
            this.representationHolder = representation;
        }

        public DataSpec getDataSpec() {
            checkInBounds();
            Representation representation = this.representationHolder.representation;
            RangedUri segmentUri = this.representationHolder.getSegmentUrl(getCurrentIndex());
            Uri resolvedUri = segmentUri.resolveUri(representation.baseUrl);
            return new DataSpec(resolvedUri, segmentUri.start, segmentUri.length, representation.getCacheKey());
        }

        public long getChunkStartTimeUs() {
            checkInBounds();
            return this.representationHolder.getSegmentStartTimeUs(getCurrentIndex());
        }

        public long getChunkEndTimeUs() {
            checkInBounds();
            return this.representationHolder.getSegmentEndTimeUs(getCurrentIndex());
        }
    }

    public DefaultDashChunkSource(LoaderErrorThrower manifestLoaderErrorThrower, DashManifest manifest, int periodIndex, int[] adaptationSetIndices, TrackSelection trackSelection, int trackType, DataSource dataSource, long elapsedRealtimeOffsetMs, int maxSegmentsPerLoad, boolean enableEventMessageTrack, boolean enableCea608Track, @Nullable PlayerTrackEmsgHandler playerTrackEmsgHandler) {
        List<Representation> representations;
        TrackSelection trackSelection2 = trackSelection;
        this.manifestLoaderErrorThrower = manifestLoaderErrorThrower;
        this.manifest = manifest;
        this.adaptationSetIndices = adaptationSetIndices;
        this.trackSelection = trackSelection2;
        this.trackType = trackType;
        this.dataSource = dataSource;
        this.periodIndex = periodIndex;
        this.elapsedRealtimeOffsetMs = elapsedRealtimeOffsetMs;
        this.maxSegmentsPerLoad = maxSegmentsPerLoad;
        this.playerTrackEmsgHandler = playerTrackEmsgHandler;
        long periodDurationUs = manifest.getPeriodDurationUs(periodIndex);
        List<Representation> representations2 = getRepresentations();
        this.representationHolders = new RepresentationHolder[trackSelection.length()];
        int i = 0;
        while (i < r0.representationHolders.length) {
            int i2 = i;
            representations = representations2;
            r0.representationHolders[i2] = new RepresentationHolder(periodDurationUs, trackType, (Representation) representations2.get(trackSelection2.getIndexInTrackGroup(i)), enableEventMessageTrack, enableCea608Track, playerTrackEmsgHandler);
            i = i2 + 1;
            long j = elapsedRealtimeOffsetMs;
            int i3 = maxSegmentsPerLoad;
            PlayerTrackEmsgHandler playerTrackEmsgHandler2 = playerTrackEmsgHandler;
            representations2 = representations;
        }
        representations = representations2;
    }

    public long getAdjustedSeekPositionUs(long positionUs, SeekParameters seekParameters) {
        long j = positionUs;
        for (RepresentationHolder representationHolder : this.representationHolders) {
            if (representationHolder.segmentIndex != null) {
                long secondSyncUs;
                long segmentNum = representationHolder.getSegmentNum(j);
                long firstSyncUs = representationHolder.getSegmentStartTimeUs(segmentNum);
                if (firstSyncUs < j) {
                    if (segmentNum < ((long) (representationHolder.getSegmentCount() - 1))) {
                        secondSyncUs = representationHolder.getSegmentStartTimeUs(1 + segmentNum);
                        return Util.resolveSeekPositionUs(positionUs, seekParameters, firstSyncUs, secondSyncUs);
                    }
                }
                secondSyncUs = firstSyncUs;
                return Util.resolveSeekPositionUs(positionUs, seekParameters, firstSyncUs, secondSyncUs);
            }
        }
        return j;
    }

    public void updateManifest(DashManifest newManifest, int newPeriodIndex) {
        try {
            this.manifest = newManifest;
            this.periodIndex = newPeriodIndex;
            long periodDurationUs = this.manifest.getPeriodDurationUs(this.periodIndex);
            List<Representation> representations = getRepresentations();
            for (int i = 0; i < this.representationHolders.length; i++) {
                this.representationHolders[i] = this.representationHolders[i].copyWithNewRepresentation(periodDurationUs, (Representation) representations.get(this.trackSelection.getIndexInTrackGroup(i)));
            }
        } catch (BehindLiveWindowException e) {
            this.fatalError = e;
        }
    }

    public void maybeThrowError() throws IOException {
        IOException iOException = this.fatalError;
        if (iOException == null) {
            this.manifestLoaderErrorThrower.maybeThrowError();
            return;
        }
        throw iOException;
    }

    public int getPreferredQueueSize(long playbackPositionUs, List<? extends MediaChunk> queue) {
        if (this.fatalError == null) {
            if (this.trackSelection.length() >= 2) {
                return this.trackSelection.evaluateQueueSize(playbackPositionUs, queue);
            }
        }
        return queue.size();
    }

    public void getNextChunk(long playbackPositionUs, long loadPositionUs, List<? extends MediaChunk> queue, ChunkHolder out) {
        ChunkHolder chunkHolder = out;
        if (this.fatalError == null) {
            MediaChunk mediaChunk;
            List<? extends MediaChunk> list;
            RepresentationHolder representationHolder;
            long segmentNum;
            long bufferedDurationUs = loadPositionUs - playbackPositionUs;
            long timeToLiveEdgeUs = resolveTimeToLiveEdgeUs(playbackPositionUs);
            long presentationPositionUs = (C0555C.msToUs(r15.manifest.availabilityStartTimeMs) + C0555C.msToUs(r15.manifest.getPeriod(r15.periodIndex).startMs)) + loadPositionUs;
            PlayerTrackEmsgHandler playerTrackEmsgHandler = r15.playerTrackEmsgHandler;
            if (playerTrackEmsgHandler != null) {
                if (playerTrackEmsgHandler.maybeRefreshManifestBeforeLoadingNextChunk(presentationPositionUs)) {
                    return;
                }
            }
            long nowUnixTimeUs = getNowUnixTimeUs();
            if (queue.isEmpty()) {
                mediaChunk = null;
                list = queue;
            } else {
                mediaChunk = (MediaChunk) queue.get(queue.size() - 1);
            }
            MediaChunk previous = mediaChunk;
            MediaChunkIterator[] chunkIterators = new MediaChunkIterator[r15.trackSelection.length()];
            int i = 0;
            while (i < chunkIterators.length) {
                int i2;
                MediaChunkIterator[] chunkIterators2;
                RepresentationHolder representationHolder2 = r15.representationHolders[i];
                if (representationHolder2.segmentIndex == null) {
                    chunkIterators[i] = MediaChunkIterator.EMPTY;
                    i2 = i;
                    chunkIterators2 = chunkIterators;
                } else {
                    long firstAvailableSegmentNum = representationHolder2.getFirstAvailableSegmentNum(r15.manifest, r15.periodIndex, nowUnixTimeUs);
                    long lastAvailableSegmentNum = representationHolder2.getLastAvailableSegmentNum(r15.manifest, r15.periodIndex, nowUnixTimeUs);
                    i2 = i;
                    representationHolder = representationHolder2;
                    chunkIterators2 = chunkIterators;
                    segmentNum = getSegmentNum(representationHolder2, previous, loadPositionUs, firstAvailableSegmentNum, lastAvailableSegmentNum);
                    if (segmentNum < firstAvailableSegmentNum) {
                        chunkIterators2[i2] = MediaChunkIterator.EMPTY;
                    } else {
                        chunkIterators2[i2] = new RepresentationSegmentIterator(representationHolder, segmentNum, lastAvailableSegmentNum);
                    }
                }
                i = i2 + 1;
                list = queue;
                chunkIterators = chunkIterators2;
            }
            segmentNum = nowUnixTimeUs;
            r15.trackSelection.updateSelectedTrack(playbackPositionUs, bufferedDurationUs, timeToLiveEdgeUs, queue, chunkIterators);
            RepresentationHolder representationHolder3 = r15.representationHolders[r15.trackSelection.getSelectedIndex()];
            if (representationHolder3.extractorWrapper != null) {
                RangedUri pendingInitializationUri;
                RangedUri pendingIndexUri;
                Representation selectedRepresentation = representationHolder3.representation;
                if (representationHolder3.extractorWrapper.getSampleFormats() == null) {
                    pendingInitializationUri = selectedRepresentation.getInitializationUri();
                } else {
                    pendingInitializationUri = null;
                }
                if (representationHolder3.segmentIndex == null) {
                    pendingIndexUri = selectedRepresentation.getIndexUri();
                } else {
                    pendingIndexUri = null;
                }
                if (pendingInitializationUri == null) {
                    if (pendingIndexUri != null) {
                    }
                }
                chunkHolder.chunk = newInitializationChunk(representationHolder3, r15.dataSource, r15.trackSelection.getSelectedFormat(), r15.trackSelection.getSelectionReason(), r15.trackSelection.getSelectionData(), pendingInitializationUri, pendingIndexUri);
                return;
            }
            long periodDurationUs = representationHolder3.periodDurationUs;
            long j = C0555C.TIME_UNSET;
            boolean periodEnded = periodDurationUs != C0555C.TIME_UNSET;
            if (representationHolder3.getSegmentCount() == 0) {
                chunkHolder.endOfStream = periodEnded;
                return;
            }
            long firstAvailableSegmentNum2 = representationHolder3.getFirstAvailableSegmentNum(r15.manifest, r15.periodIndex, segmentNum);
            long lastAvailableSegmentNum2 = representationHolder3.getLastAvailableSegmentNum(r15.manifest, r15.periodIndex, segmentNum);
            updateLiveEdgeTimeUs(representationHolder3, lastAvailableSegmentNum2);
            long lastAvailableSegmentNum3 = lastAvailableSegmentNum2;
            boolean periodEnded2 = periodEnded;
            RepresentationHolder representationHolder4 = representationHolder3;
            nowUnixTimeUs = getSegmentNum(representationHolder3, previous, loadPositionUs, firstAvailableSegmentNum2, lastAvailableSegmentNum3);
            if (nowUnixTimeUs < firstAvailableSegmentNum2) {
                r15.fatalError = new BehindLiveWindowException();
                return;
            }
            long j2;
            if (nowUnixTimeUs > lastAvailableSegmentNum3) {
                j = nowUnixTimeUs;
                representationHolder4 = chunkHolder;
                j2 = presentationPositionUs;
            } else if (r15.missingLastSegment && nowUnixTimeUs >= lastAvailableSegmentNum3) {
                representationHolder = representationHolder4;
                j = nowUnixTimeUs;
                representationHolder4 = chunkHolder;
                j2 = presentationPositionUs;
            } else if (!periodEnded2 || representationHolder4.getSegmentStartTimeUs(nowUnixTimeUs) < periodDurationUs) {
                int maxSegmentCount;
                int maxSegmentCount2 = (int) Math.min((long) r15.maxSegmentsPerLoad, (lastAvailableSegmentNum3 - nowUnixTimeUs) + 1);
                if (periodDurationUs != C0555C.TIME_UNSET) {
                    while (maxSegmentCount2 > 1) {
                        if (representationHolder4.getSegmentStartTimeUs((((long) maxSegmentCount2) + nowUnixTimeUs) - 1) < periodDurationUs) {
                            break;
                        }
                        maxSegmentCount2--;
                    }
                    maxSegmentCount = maxSegmentCount2;
                } else {
                    maxSegmentCount = maxSegmentCount2;
                }
                if (queue.isEmpty()) {
                    j = loadPositionUs;
                }
                presentationPositionUs = j;
                RepresentationHolder representationHolder5 = representationHolder4;
                representationHolder = representationHolder4;
                chunkHolder.chunk = newMediaChunk(representationHolder5, r15.dataSource, r15.trackType, r15.trackSelection.getSelectedFormat(), r15.trackSelection.getSelectionReason(), r15.trackSelection.getSelectionData(), nowUnixTimeUs, maxSegmentCount, presentationPositionUs);
                return;
            } else {
                chunkHolder.endOfStream = true;
                return;
            }
            representationHolder4.endOfStream = periodEnded2;
        }
    }

    public void onChunkLoadCompleted(Chunk chunk) {
        if (chunk instanceof InitializationChunk) {
            int trackIndex = this.trackSelection.indexOf(((InitializationChunk) chunk).trackFormat);
            RepresentationHolder representationHolder = this.representationHolders[trackIndex];
            if (representationHolder.segmentIndex == null) {
                SeekMap seekMap = representationHolder.extractorWrapper.getSeekMap();
                if (seekMap != null) {
                    this.representationHolders[trackIndex] = representationHolder.copyWithNewSegmentIndex(new DashWrappingSegmentIndex((ChunkIndex) seekMap, representationHolder.representation.presentationTimeOffsetUs));
                }
            }
        }
        PlayerTrackEmsgHandler playerTrackEmsgHandler = this.playerTrackEmsgHandler;
        if (playerTrackEmsgHandler != null) {
            playerTrackEmsgHandler.onChunkLoadCompleted(chunk);
        }
    }

    public boolean onChunkLoadError(Chunk chunk, boolean cancelable, Exception e, long blacklistDurationMs) {
        boolean z = false;
        if (!cancelable) {
            return false;
        }
        PlayerTrackEmsgHandler playerTrackEmsgHandler = this.playerTrackEmsgHandler;
        if (playerTrackEmsgHandler != null) {
            if (playerTrackEmsgHandler.maybeRefreshManifestOnLoadingError(chunk)) {
                return true;
            }
        }
        if (!this.manifest.dynamic && (chunk instanceof MediaChunk) && (e instanceof InvalidResponseCodeException) && ((InvalidResponseCodeException) e).responseCode == 404) {
            RepresentationHolder representationHolder = this.representationHolders[this.trackSelection.indexOf(chunk.trackFormat)];
            int segmentCount = representationHolder.getSegmentCount();
            if (segmentCount != -1 && segmentCount != 0) {
                if (((MediaChunk) chunk).getNextChunkIndex() > (representationHolder.getFirstSegmentNum() + ((long) segmentCount)) - 1) {
                    this.missingLastSegment = true;
                    return true;
                }
            }
        }
        if (blacklistDurationMs != C0555C.TIME_UNSET) {
            TrackSelection trackSelection = this.trackSelection;
            if (trackSelection.blacklist(trackSelection.indexOf(chunk.trackFormat), blacklistDurationMs)) {
                z = true;
                return z;
            }
        }
        return z;
    }

    private long getSegmentNum(RepresentationHolder representationHolder, @Nullable MediaChunk previousChunk, long loadPositionUs, long firstAvailableSegmentNum, long lastAvailableSegmentNum) {
        if (previousChunk != null) {
            RepresentationHolder representationHolder2 = representationHolder;
            long j = loadPositionUs;
            return previousChunk.getNextChunkIndex();
        }
        representationHolder2 = representationHolder;
        j = loadPositionUs;
        return Util.constrainValue(representationHolder.getSegmentNum(loadPositionUs), firstAvailableSegmentNum, lastAvailableSegmentNum);
    }

    private ArrayList<Representation> getRepresentations() {
        List<AdaptationSet> manifestAdapationSets = this.manifest.getPeriod(this.periodIndex).adaptationSets;
        ArrayList<Representation> representations = new ArrayList();
        for (int adaptationSetIndex : this.adaptationSetIndices) {
            representations.addAll(((AdaptationSet) manifestAdapationSets.get(adaptationSetIndex)).representations);
        }
        return representations;
    }

    private void updateLiveEdgeTimeUs(RepresentationHolder representationHolder, long lastAvailableSegmentNum) {
        this.liveEdgeTimeUs = this.manifest.dynamic ? representationHolder.getSegmentEndTimeUs(lastAvailableSegmentNum) : C0555C.TIME_UNSET;
    }

    private long getNowUnixTimeUs() {
        if (this.elapsedRealtimeOffsetMs != 0) {
            return (SystemClock.elapsedRealtime() + this.elapsedRealtimeOffsetMs) * 1000;
        }
        return System.currentTimeMillis() * 1000;
    }

    private long resolveTimeToLiveEdgeUs(long playbackPositionUs) {
        boolean resolveTimeToLiveEdgePossible = this.manifest.dynamic && this.liveEdgeTimeUs != C0555C.TIME_UNSET;
        if (resolveTimeToLiveEdgePossible) {
            return this.liveEdgeTimeUs - playbackPositionUs;
        }
        return C0555C.TIME_UNSET;
    }

    protected Chunk newInitializationChunk(RepresentationHolder representationHolder, DataSource dataSource, Format trackFormat, int trackSelectionReason, Object trackSelectionData, RangedUri initializationUri, RangedUri indexUri) {
        RangedUri requestUri;
        RepresentationHolder representationHolder2 = representationHolder;
        RangedUri rangedUri = initializationUri;
        String baseUrl = representationHolder2.representation.baseUrl;
        if (rangedUri != null) {
            requestUri = rangedUri.attemptMerge(indexUri, baseUrl);
            if (requestUri == null) {
                requestUri = initializationUri;
            }
        } else {
            RangedUri rangedUri2 = indexUri;
            requestUri = indexUri;
        }
        return new InitializationChunk(dataSource, new DataSpec(requestUri.resolveUri(baseUrl), requestUri.start, requestUri.length, representationHolder2.representation.getCacheKey()), trackFormat, trackSelectionReason, trackSelectionData, representationHolder2.extractorWrapper);
    }

    protected Chunk newMediaChunk(RepresentationHolder representationHolder, DataSource dataSource, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long firstSegmentNum, int maxSegmentCount, long seekTimeUs) {
        RepresentationHolder representationHolder2 = representationHolder;
        long j = firstSegmentNum;
        Representation representation = representationHolder2.representation;
        long startTimeUs = representationHolder2.getSegmentStartTimeUs(j);
        RangedUri segmentUri = representationHolder2.getSegmentUrl(j);
        String baseUrl = representation.baseUrl;
        if (representationHolder2.extractorWrapper == null) {
            return new SingleSampleMediaChunk(dataSource, new DataSpec(segmentUri.resolveUri(baseUrl), segmentUri.start, segmentUri.length, representation.getCacheKey()), trackFormat, trackSelectionReason, trackSelectionData, startTimeUs, representationHolder2.getSegmentEndTimeUs(j), firstSegmentNum, trackType, trackFormat);
        }
        String baseUrl2 = baseUrl;
        Representation representation2 = representation;
        int segmentCount = 1;
        for (int i = 1; i < maxSegmentCount; i++) {
            RangedUri mergedSegmentUri = segmentUri.attemptMerge(representationHolder2.getSegmentUrl(firstSegmentNum + ((long) i)), baseUrl2);
            if (mergedSegmentUri == null) {
                break;
            }
            segmentUri = mergedSegmentUri;
            segmentCount++;
        }
        long endTimeUs = representationHolder2.getSegmentEndTimeUs((firstSegmentNum + ((long) segmentCount)) - 1);
        long periodDurationUs = representationHolder.periodDurationUs;
        long clippedEndTimeUs = (periodDurationUs == C0555C.TIME_UNSET || periodDurationUs > endTimeUs) ? C0555C.TIME_UNSET : periodDurationUs;
        Representation representation3 = representation2;
        long sampleOffsetUs = -representation3.presentationTimeOffsetUs;
        return new ContainerMediaChunk(dataSource, new DataSpec(segmentUri.resolveUri(baseUrl2), segmentUri.start, segmentUri.length, representation2.getCacheKey()), trackFormat, trackSelectionReason, trackSelectionData, startTimeUs, endTimeUs, seekTimeUs, clippedEndTimeUs, firstSegmentNum, segmentCount, sampleOffsetUs, representationHolder2.extractorWrapper);
    }
}
