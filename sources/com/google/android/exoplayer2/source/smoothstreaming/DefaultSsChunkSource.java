package com.google.android.exoplayer2.source.smoothstreaming;

import android.net.Uri;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.extractor.mp4.FragmentedMp4Extractor;
import com.google.android.exoplayer2.extractor.mp4.Track;
import com.google.android.exoplayer2.extractor.mp4.TrackEncryptionBox;
import com.google.android.exoplayer2.source.BehindLiveWindowException;
import com.google.android.exoplayer2.source.chunk.BaseMediaChunkIterator;
import com.google.android.exoplayer2.source.chunk.Chunk;
import com.google.android.exoplayer2.source.chunk.ChunkExtractorWrapper;
import com.google.android.exoplayer2.source.chunk.ChunkHolder;
import com.google.android.exoplayer2.source.chunk.ContainerMediaChunk;
import com.google.android.exoplayer2.source.chunk.MediaChunk;
import com.google.android.exoplayer2.source.chunk.MediaChunkIterator;
import com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifest;
import com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifest.StreamElement;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.LoaderErrorThrower;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.List;

public class DefaultSsChunkSource implements SsChunkSource {
    private int currentManifestChunkOffset;
    private final DataSource dataSource;
    private final ChunkExtractorWrapper[] extractorWrappers;
    private IOException fatalError;
    private SsManifest manifest;
    private final LoaderErrorThrower manifestLoaderErrorThrower;
    private final int streamElementIndex;
    private final TrackSelection trackSelection;

    public static final class Factory implements com.google.android.exoplayer2.source.smoothstreaming.SsChunkSource.Factory {
        private final com.google.android.exoplayer2.upstream.DataSource.Factory dataSourceFactory;

        public Factory(com.google.android.exoplayer2.upstream.DataSource.Factory dataSourceFactory) {
            this.dataSourceFactory = dataSourceFactory;
        }

        public SsChunkSource createChunkSource(LoaderErrorThrower manifestLoaderErrorThrower, SsManifest manifest, int elementIndex, TrackSelection trackSelection, TrackEncryptionBox[] trackEncryptionBoxes, @Nullable TransferListener transferListener) {
            DataSource dataSource = this.dataSourceFactory.createDataSource();
            if (transferListener != null) {
                dataSource.addTransferListener(transferListener);
            }
            return new DefaultSsChunkSource(manifestLoaderErrorThrower, manifest, elementIndex, trackSelection, dataSource, trackEncryptionBoxes);
        }
    }

    private static final class StreamElementIterator extends BaseMediaChunkIterator {
        private final StreamElement streamElement;
        private final int trackIndex;

        public StreamElementIterator(StreamElement streamElement, int trackIndex, int chunkIndex) {
            super((long) chunkIndex, (long) (streamElement.chunkCount - 1));
            this.streamElement = streamElement;
            this.trackIndex = trackIndex;
        }

        public DataSpec getDataSpec() {
            checkInBounds();
            return new DataSpec(this.streamElement.buildRequestUri(this.trackIndex, (int) getCurrentIndex()));
        }

        public long getChunkStartTimeUs() {
            checkInBounds();
            return this.streamElement.getStartTimeUs((int) getCurrentIndex());
        }

        public long getChunkEndTimeUs() {
            return this.streamElement.getChunkDurationUs((int) getCurrentIndex()) + getChunkStartTimeUs();
        }
    }

    public DefaultSsChunkSource(LoaderErrorThrower manifestLoaderErrorThrower, SsManifest manifest, int streamElementIndex, TrackSelection trackSelection, DataSource dataSource, TrackEncryptionBox[] trackEncryptionBoxes) {
        SsManifest ssManifest = manifest;
        int i = streamElementIndex;
        TrackSelection trackSelection2 = trackSelection;
        this.manifestLoaderErrorThrower = manifestLoaderErrorThrower;
        this.manifest = ssManifest;
        this.streamElementIndex = i;
        this.trackSelection = trackSelection2;
        this.dataSource = dataSource;
        StreamElement streamElement = ssManifest.streamElements[i];
        this.extractorWrappers = new ChunkExtractorWrapper[trackSelection.length()];
        for (int i2 = 0; i2 < r0.extractorWrappers.length; i2++) {
            int manifestTrackIndex = trackSelection2.getIndexInTrackGroup(i2);
            Format format = streamElement.formats[manifestTrackIndex];
            r0.extractorWrappers[i2] = new ChunkExtractorWrapper(new FragmentedMp4Extractor(3, null, new Track(manifestTrackIndex, streamElement.type, streamElement.timescale, C0555C.TIME_UNSET, ssManifest.durationUs, format, 0, trackEncryptionBoxes, streamElement.type == 2 ? 4 : 0, null, null), null), streamElement.type, format);
        }
    }

    public long getAdjustedSeekPositionUs(long positionUs, SeekParameters seekParameters) {
        StreamElement streamElement = this.manifest.streamElements[this.streamElementIndex];
        int chunkIndex = streamElement.getChunkIndex(positionUs);
        long firstSyncUs = streamElement.getStartTimeUs(chunkIndex);
        long secondSyncUs = (firstSyncUs >= positionUs || chunkIndex >= streamElement.chunkCount - 1) ? firstSyncUs : streamElement.getStartTimeUs(chunkIndex + 1);
        return Util.resolveSeekPositionUs(positionUs, seekParameters, firstSyncUs, secondSyncUs);
    }

    public void updateManifest(SsManifest newManifest) {
        StreamElement currentElement = this.manifest.streamElements[this.streamElementIndex];
        int currentElementChunkCount = currentElement.chunkCount;
        StreamElement newElement = newManifest.streamElements[this.streamElementIndex];
        if (currentElementChunkCount != 0) {
            if (newElement.chunkCount != 0) {
                long currentElementEndTimeUs = currentElement.getStartTimeUs(currentElementChunkCount - 1) + currentElement.getChunkDurationUs(currentElementChunkCount - 1);
                long newElementStartTimeUs = newElement.getStartTimeUs(0);
                if (currentElementEndTimeUs <= newElementStartTimeUs) {
                    this.currentManifestChunkOffset += currentElementChunkCount;
                } else {
                    this.currentManifestChunkOffset += currentElement.getChunkIndex(newElementStartTimeUs);
                }
                this.manifest = newManifest;
            }
        }
        this.currentManifestChunkOffset += currentElementChunkCount;
        this.manifest = newManifest;
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

    public final void getNextChunk(long playbackPositionUs, long loadPositionUs, List<? extends MediaChunk> queue, ChunkHolder out) {
        long j = loadPositionUs;
        ChunkHolder chunkHolder = out;
        if (this.fatalError == null) {
            StreamElement streamElement = r0.manifest.streamElements[r0.streamElementIndex];
            if (streamElement.chunkCount == 0) {
                chunkHolder.endOfStream = r0.manifest.isLive ^ 1;
                return;
            }
            int chunkIndex;
            if (queue.isEmpty()) {
                chunkIndex = streamElement.getChunkIndex(j);
                List<? extends MediaChunk> list = queue;
            } else {
                chunkIndex = (int) (((MediaChunk) queue.get(queue.size() - 1)).getNextChunkIndex() - ((long) r0.currentManifestChunkOffset));
                if (chunkIndex < 0) {
                    r0.fatalError = new BehindLiveWindowException();
                    return;
                }
            }
            if (chunkIndex >= streamElement.chunkCount) {
                chunkHolder.endOfStream = r0.manifest.isLive ^ 1;
                return;
            }
            long bufferedDurationUs = j - playbackPositionUs;
            long timeToLiveEdgeUs = resolveTimeToLiveEdgeUs(playbackPositionUs);
            MediaChunkIterator[] chunkIterators = new MediaChunkIterator[r0.trackSelection.length()];
            for (int i = 0; i < chunkIterators.length; i++) {
                chunkIterators[i] = new StreamElementIterator(streamElement, r0.trackSelection.getIndexInTrackGroup(i), chunkIndex);
            }
            r0.trackSelection.updateSelectedTrack(playbackPositionUs, bufferedDurationUs, timeToLiveEdgeUs, queue, chunkIterators);
            long chunkStartTimeUs = streamElement.getStartTimeUs(chunkIndex);
            long chunkEndTimeUs = streamElement.getChunkDurationUs(chunkIndex) + chunkStartTimeUs;
            long chunkSeekTimeUs = queue.isEmpty() ? j : C0555C.TIME_UNSET;
            int currentAbsoluteChunkIndex = r0.currentManifestChunkOffset + chunkIndex;
            int trackSelectionIndex = r0.trackSelection.getSelectedIndex();
            ChunkExtractorWrapper extractorWrapper = r0.extractorWrappers[trackSelectionIndex];
            chunkHolder.chunk = newMediaChunk(r0.trackSelection.getSelectedFormat(), r0.dataSource, streamElement.buildRequestUri(r0.trackSelection.getIndexInTrackGroup(trackSelectionIndex), chunkIndex), null, currentAbsoluteChunkIndex, chunkStartTimeUs, chunkEndTimeUs, chunkSeekTimeUs, r0.trackSelection.getSelectionReason(), r0.trackSelection.getSelectionData(), extractorWrapper);
        }
    }

    public void onChunkLoadCompleted(Chunk chunk) {
    }

    public boolean onChunkLoadError(Chunk chunk, boolean cancelable, Exception e, long blacklistDurationMs) {
        if (cancelable && blacklistDurationMs != C0555C.TIME_UNSET) {
            TrackSelection trackSelection = this.trackSelection;
            if (trackSelection.blacklist(trackSelection.indexOf(chunk.trackFormat), blacklistDurationMs)) {
                return true;
            }
        }
        return false;
    }

    private static MediaChunk newMediaChunk(Format format, DataSource dataSource, Uri uri, String cacheKey, int chunkIndex, long chunkStartTimeUs, long chunkEndTimeUs, long chunkSeekTimeUs, int trackSelectionReason, Object trackSelectionData, ChunkExtractorWrapper extractorWrapper) {
        ChunkExtractorWrapper chunkExtractorWrapper = extractorWrapper;
        long j = (long) chunkIndex;
        return new ContainerMediaChunk(dataSource, new DataSpec(uri, 0, -1, cacheKey), format, trackSelectionReason, trackSelectionData, chunkStartTimeUs, chunkEndTimeUs, chunkSeekTimeUs, C0555C.TIME_UNSET, j, 1, chunkStartTimeUs, chunkExtractorWrapper);
    }

    private long resolveTimeToLiveEdgeUs(long playbackPositionUs) {
        if (!this.manifest.isLive) {
            return C0555C.TIME_UNSET;
        }
        StreamElement currentElement = this.manifest.streamElements[this.streamElementIndex];
        int lastChunkIndex = currentElement.chunkCount - 1;
        return (currentElement.getStartTimeUs(lastChunkIndex) + currentElement.getChunkDurationUs(lastChunkIndex)) - playbackPositionUs;
    }
}
