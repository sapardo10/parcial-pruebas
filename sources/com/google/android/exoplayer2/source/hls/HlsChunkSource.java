package com.google.android.exoplayer2.source.hls;

import android.net.Uri;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.source.BehindLiveWindowException;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.chunk.BaseMediaChunkIterator;
import com.google.android.exoplayer2.source.chunk.Chunk;
import com.google.android.exoplayer2.source.chunk.DataChunk;
import com.google.android.exoplayer2.source.chunk.MediaChunkIterator;
import com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist.HlsUrl;
import com.google.android.exoplayer2.source.hls.playlist.HlsMediaPlaylist;
import com.google.android.exoplayer2.source.hls.playlist.HlsMediaPlaylist.Segment;
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistTracker;
import com.google.android.exoplayer2.trackselection.BaseTrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.TimestampAdjuster;
import com.google.android.exoplayer2.util.UriUtil;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

class HlsChunkSource {
    private final DataSource encryptionDataSource;
    private byte[] encryptionIv;
    private String encryptionIvString;
    private byte[] encryptionKey;
    private Uri encryptionKeyUri;
    private HlsUrl expectedPlaylistUrl;
    private final HlsExtractorFactory extractorFactory;
    private IOException fatalError;
    private boolean independentSegments;
    private boolean isTimestampMaster;
    private long liveEdgeInPeriodTimeUs = C0555C.TIME_UNSET;
    private final DataSource mediaDataSource;
    private final List<Format> muxedCaptionFormats;
    private final HlsPlaylistTracker playlistTracker;
    private byte[] scratchSpace;
    private boolean seenExpectedPlaylistError;
    private final TimestampAdjusterProvider timestampAdjusterProvider;
    private final TrackGroup trackGroup;
    private TrackSelection trackSelection;
    private final HlsUrl[] variants;

    public static final class HlsChunkHolder {
        public Chunk chunk;
        public boolean endOfStream;
        public HlsUrl playlist;

        public HlsChunkHolder() {
            clear();
        }

        public void clear() {
            this.chunk = null;
            this.endOfStream = false;
            this.playlist = null;
        }
    }

    private static final class HlsMediaPlaylistSegmentIterator extends BaseMediaChunkIterator {
        private final HlsMediaPlaylist playlist;
        private final long startOfPlaylistInPeriodUs;

        public HlsMediaPlaylistSegmentIterator(HlsMediaPlaylist playlist, long startOfPlaylistInPeriodUs, int chunkIndex) {
            super((long) chunkIndex, (long) (playlist.segments.size() - 1));
            this.playlist = playlist;
            this.startOfPlaylistInPeriodUs = startOfPlaylistInPeriodUs;
        }

        public DataSpec getDataSpec() {
            checkInBounds();
            Segment segment = (Segment) this.playlist.segments.get((int) getCurrentIndex());
            return new DataSpec(UriUtil.resolveToUri(this.playlist.baseUri, segment.url), segment.byterangeOffset, segment.byterangeLength, null);
        }

        public long getChunkStartTimeUs() {
            checkInBounds();
            return this.startOfPlaylistInPeriodUs + ((Segment) this.playlist.segments.get((int) getCurrentIndex())).relativeStartTimeUs;
        }

        public long getChunkEndTimeUs() {
            checkInBounds();
            Segment segment = (Segment) this.playlist.segments.get((int) getCurrentIndex());
            return segment.durationUs + (this.startOfPlaylistInPeriodUs + segment.relativeStartTimeUs);
        }
    }

    private static final class InitializationTrackSelection extends BaseTrackSelection {
        private int selectedIndex;

        public void updateSelectedTrack(long r5, long r7, long r9, java.util.List<? extends com.google.android.exoplayer2.source.chunk.MediaChunk> r11, com.google.android.exoplayer2.source.chunk.MediaChunkIterator[] r12) {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:12:0x0025 in {2, 8, 9, 11} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
            /*
            r4 = this;
            r0 = android.os.SystemClock.elapsedRealtime();
            r2 = r4.selectedIndex;
            r2 = r4.isBlacklisted(r2, r0);
            if (r2 != 0) goto L_0x000d;
        L_0x000c:
            return;
        L_0x000d:
            r2 = r4.length;
            r2 = r2 + -1;
        L_0x0011:
            if (r2 < 0) goto L_0x001f;
        L_0x0013:
            r3 = r4.isBlacklisted(r2, r0);
            if (r3 != 0) goto L_0x001c;
        L_0x0019:
            r4.selectedIndex = r2;
            return;
        L_0x001c:
            r2 = r2 + -1;
            goto L_0x0011;
        L_0x001f:
            r2 = new java.lang.IllegalStateException;
            r2.<init>();
            throw r2;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.hls.HlsChunkSource.InitializationTrackSelection.updateSelectedTrack(long, long, long, java.util.List, com.google.android.exoplayer2.source.chunk.MediaChunkIterator[]):void");
        }

        public InitializationTrackSelection(TrackGroup group, int[] tracks) {
            super(group, tracks);
            this.selectedIndex = indexOf(group.getFormat(0));
        }

        public int getSelectedIndex() {
            return this.selectedIndex;
        }

        public int getSelectionReason() {
            return 0;
        }

        public Object getSelectionData() {
            return null;
        }
    }

    private static final class EncryptionKeyChunk extends DataChunk {
        public final String iv;
        private byte[] result;

        public EncryptionKeyChunk(DataSource dataSource, DataSpec dataSpec, Format trackFormat, int trackSelectionReason, Object trackSelectionData, byte[] scratchSpace, String iv) {
            super(dataSource, dataSpec, 3, trackFormat, trackSelectionReason, trackSelectionData, scratchSpace);
            this.iv = iv;
        }

        protected void consume(byte[] data, int limit) throws IOException {
            this.result = Arrays.copyOf(data, limit);
        }

        public byte[] getResult() {
            return this.result;
        }
    }

    public HlsChunkSource(HlsExtractorFactory extractorFactory, HlsPlaylistTracker playlistTracker, HlsUrl[] variants, HlsDataSourceFactory dataSourceFactory, @Nullable TransferListener mediaTransferListener, TimestampAdjusterProvider timestampAdjusterProvider, List<Format> muxedCaptionFormats) {
        this.extractorFactory = extractorFactory;
        this.playlistTracker = playlistTracker;
        this.variants = variants;
        this.timestampAdjusterProvider = timestampAdjusterProvider;
        this.muxedCaptionFormats = muxedCaptionFormats;
        Format[] variantFormats = new Format[variants.length];
        int[] initialTrackSelection = new int[variants.length];
        for (int i = 0; i < variants.length; i++) {
            variantFormats[i] = variants[i].format;
            initialTrackSelection[i] = i;
        }
        this.mediaDataSource = dataSourceFactory.createDataSource(1);
        if (mediaTransferListener != null) {
            this.mediaDataSource.addTransferListener(mediaTransferListener);
        }
        this.encryptionDataSource = dataSourceFactory.createDataSource(3);
        this.trackGroup = new TrackGroup(variantFormats);
        this.trackSelection = new InitializationTrackSelection(this.trackGroup, initialTrackSelection);
    }

    public void maybeThrowError() throws IOException {
        IOException iOException = this.fatalError;
        if (iOException == null) {
            HlsUrl hlsUrl = this.expectedPlaylistUrl;
            if (hlsUrl != null && this.seenExpectedPlaylistError) {
                this.playlistTracker.maybeThrowPlaylistRefreshError(hlsUrl);
                return;
            }
            return;
        }
        throw iOException;
    }

    public TrackGroup getTrackGroup() {
        return this.trackGroup;
    }

    public void selectTracks(TrackSelection trackSelection) {
        this.trackSelection = trackSelection;
    }

    public TrackSelection getTrackSelection() {
        return this.trackSelection;
    }

    public void reset() {
        this.fatalError = null;
    }

    public void setIsTimestampMaster(boolean isTimestampMaster) {
        this.isTimestampMaster = isTimestampMaster;
    }

    public void getNextChunk(long playbackPositionUs, long loadPositionUs, List<HlsMediaChunk> queue, HlsChunkHolder out) {
        HlsMediaChunk hlsMediaChunk;
        int i;
        long timeToLiveEdgeUs;
        long bufferedDurationUs;
        HlsChunkSource hlsChunkSource = this;
        long j = loadPositionUs;
        HlsChunkHolder hlsChunkHolder = out;
        if (queue.isEmpty()) {
            List<HlsMediaChunk> list = queue;
            hlsMediaChunk = null;
        } else {
            hlsMediaChunk = (HlsMediaChunk) queue.get(queue.size() - 1);
        }
        HlsMediaChunk previous = hlsMediaChunk;
        if (previous == null) {
            i = -1;
        } else {
            i = hlsChunkSource.trackGroup.indexOf(previous.trackFormat);
        }
        int oldVariantIndex = i;
        long bufferedDurationUs2 = j - playbackPositionUs;
        long timeToLiveEdgeUs2 = resolveTimeToLiveEdgeUs(playbackPositionUs);
        if (previous == null || hlsChunkSource.independentSegments) {
            timeToLiveEdgeUs = timeToLiveEdgeUs2;
            bufferedDurationUs = bufferedDurationUs2;
        } else {
            long subtractedDurationUs = previous.getDurationUs();
            bufferedDurationUs = Math.max(0, bufferedDurationUs2 - subtractedDurationUs);
            if (timeToLiveEdgeUs2 != C0555C.TIME_UNSET) {
                timeToLiveEdgeUs = Math.max(0, timeToLiveEdgeUs2 - subtractedDurationUs);
            } else {
                timeToLiveEdgeUs = timeToLiveEdgeUs2;
            }
        }
        hlsChunkSource.trackSelection.updateSelectedTrack(playbackPositionUs, bufferedDurationUs, timeToLiveEdgeUs, queue, createMediaChunkIterators(previous, j));
        int selectedVariantIndex = hlsChunkSource.trackSelection.getSelectedIndexInTrackGroup();
        boolean switchingVariant = oldVariantIndex != selectedVariantIndex;
        HlsUrl selectedUrl = hlsChunkSource.variants[selectedVariantIndex];
        if (hlsChunkSource.playlistTracker.isSnapshotValid(selectedUrl)) {
            long chunkMediaSequence;
            int selectedVariantIndex2;
            long startOfPlaylistInPeriodUs;
            HlsUrl selectedUrl2;
            HlsMediaPlaylist mediaPlaylist = hlsChunkSource.playlistTracker.getPlaylistSnapshot(selectedUrl, true);
            hlsChunkSource.independentSegments = mediaPlaylist.hasIndependentSegments;
            updateLiveEdgeTimeUs(mediaPlaylist);
            long startOfPlaylistInPeriodUs2 = mediaPlaylist.startTimeUs - hlsChunkSource.playlistTracker.getInitialStartTimeUs();
            int oldVariantIndex2 = oldVariantIndex;
            HlsMediaPlaylist mediaPlaylist2 = mediaPlaylist;
            HlsUrl selectedUrl3 = selectedUrl;
            HlsMediaChunk previous2 = previous;
            bufferedDurationUs2 = getChunkMediaSequence(previous, switchingVariant, mediaPlaylist, startOfPlaylistInPeriodUs2, loadPositionUs);
            if (bufferedDurationUs2 >= mediaPlaylist2.mediaSequence) {
                chunkMediaSequence = bufferedDurationUs2;
                selectedVariantIndex2 = selectedVariantIndex;
                startOfPlaylistInPeriodUs = startOfPlaylistInPeriodUs2;
                selectedUrl2 = selectedUrl3;
            } else if (previous2 == null || !switchingVariant) {
                hlsChunkSource.fatalError = new BehindLiveWindowException();
                return;
            } else {
                int selectedVariantIndex3 = oldVariantIndex2;
                HlsUrl selectedUrl4 = hlsChunkSource.variants[selectedVariantIndex3];
                HlsMediaPlaylist mediaPlaylist3 = hlsChunkSource.playlistTracker.getPlaylistSnapshot(selectedUrl4, true);
                startOfPlaylistInPeriodUs2 = mediaPlaylist3.startTimeUs - hlsChunkSource.playlistTracker.getInitialStartTimeUs();
                chunkMediaSequence = previous2.getNextChunkIndex();
                selectedVariantIndex2 = selectedVariantIndex3;
                selectedUrl2 = selectedUrl4;
                mediaPlaylist2 = mediaPlaylist3;
                startOfPlaylistInPeriodUs = startOfPlaylistInPeriodUs2;
            }
            int chunkIndex = (int) (chunkMediaSequence - mediaPlaylist2.mediaSequence);
            if (chunkIndex >= mediaPlaylist2.segments.size()) {
                if (mediaPlaylist2.hasEndTag) {
                    hlsChunkHolder.endOfStream = true;
                } else {
                    i = 1;
                    hlsChunkHolder.playlist = selectedUrl2;
                    boolean z = hlsChunkSource.seenExpectedPlaylistError;
                    if (hlsChunkSource.expectedPlaylistUrl != selectedUrl2) {
                        i = 0;
                    }
                    hlsChunkSource.seenExpectedPlaylistError = i & z;
                    hlsChunkSource.expectedPlaylistUrl = selectedUrl2;
                }
                return;
            }
            Segment segment;
            hlsChunkSource.seenExpectedPlaylistError = false;
            hlsChunkSource.expectedPlaylistUrl = null;
            Segment segment2 = (Segment) mediaPlaylist2.segments.get(chunkIndex);
            int i2;
            if (segment2.fullSegmentEncryptionKeyUri != null) {
                Uri keyUri = UriUtil.resolveToUri(mediaPlaylist2.baseUri, segment2.fullSegmentEncryptionKeyUri);
                if (keyUri.equals(hlsChunkSource.encryptionKeyUri)) {
                    Uri keyUri2 = keyUri;
                    segment = segment2;
                    i2 = chunkIndex;
                    if (!Util.areEqual(segment.encryptionIV, hlsChunkSource.encryptionIvString)) {
                        setEncryptionData(keyUri2, segment.encryptionIV, hlsChunkSource.encryptionKey);
                    }
                } else {
                    hlsChunkHolder.chunk = newEncryptionKeyChunk(keyUri, segment2.encryptionIV, selectedVariantIndex2, hlsChunkSource.trackSelection.getSelectionReason(), hlsChunkSource.trackSelection.getSelectionData());
                    return;
                }
            }
            segment = segment2;
            i2 = chunkIndex;
            clearEncryptionData();
            DataSpec initDataSpec = null;
            Segment initSegment = segment.initializationSegment;
            if (initSegment != null) {
                initDataSpec = new DataSpec(UriUtil.resolveToUri(mediaPlaylist2.baseUri, initSegment.url), initSegment.byterangeOffset, initSegment.byterangeLength, null);
            }
            timeToLiveEdgeUs2 = startOfPlaylistInPeriodUs + segment.relativeStartTimeUs;
            long j2 = timeToLiveEdgeUs2;
            oldVariantIndex = mediaPlaylist2.discontinuitySequence + segment.relativeDiscontinuitySequence;
            int i3 = oldVariantIndex;
            TimestampAdjuster timestampAdjuster = hlsChunkSource.timestampAdjusterProvider.getAdjuster(oldVariantIndex);
            hlsChunkHolder.chunk = new HlsMediaChunk(hlsChunkSource.extractorFactory, hlsChunkSource.mediaDataSource, new DataSpec(UriUtil.resolveToUri(mediaPlaylist2.baseUri, segment.url), segment.byterangeOffset, segment.byterangeLength, null), initDataSpec, selectedUrl2, hlsChunkSource.muxedCaptionFormats, hlsChunkSource.trackSelection.getSelectionReason(), hlsChunkSource.trackSelection.getSelectionData(), j2, timeToLiveEdgeUs2 + segment.durationUs, chunkMediaSequence, i3, segment.hasGapTag, hlsChunkSource.isTimestampMaster, timestampAdjuster, previous2, segment.drmInitData, hlsChunkSource.encryptionKey, hlsChunkSource.encryptionIv);
            return;
        }
        hlsChunkHolder.playlist = selectedUrl;
        hlsChunkSource.seenExpectedPlaylistError &= hlsChunkSource.expectedPlaylistUrl == selectedUrl ? 1 : 0;
        hlsChunkSource.expectedPlaylistUrl = selectedUrl;
    }

    public void onChunkLoadCompleted(Chunk chunk) {
        if (chunk instanceof EncryptionKeyChunk) {
            EncryptionKeyChunk encryptionKeyChunk = (EncryptionKeyChunk) chunk;
            this.scratchSpace = encryptionKeyChunk.getDataHolder();
            setEncryptionData(encryptionKeyChunk.dataSpec.uri, encryptionKeyChunk.iv, encryptionKeyChunk.getResult());
        }
    }

    public boolean maybeBlacklistTrack(Chunk chunk, long blacklistDurationMs) {
        TrackSelection trackSelection = this.trackSelection;
        return trackSelection.blacklist(trackSelection.indexOf(this.trackGroup.indexOf(chunk.trackFormat)), blacklistDurationMs);
    }

    public boolean onPlaylistError(HlsUrl url, long blacklistDurationMs) {
        int trackGroupIndex = this.trackGroup.indexOf(url.format);
        boolean z = true;
        if (trackGroupIndex == -1) {
            return true;
        }
        int trackSelectionIndex = this.trackSelection.indexOf(trackGroupIndex);
        if (trackSelectionIndex == -1) {
            return true;
        }
        this.seenExpectedPlaylistError |= this.expectedPlaylistUrl == url ? 1 : 0;
        if (blacklistDurationMs != C0555C.TIME_UNSET) {
            if (!this.trackSelection.blacklist(trackSelectionIndex, blacklistDurationMs)) {
                z = false;
                return z;
            }
        }
        return z;
    }

    public MediaChunkIterator[] createMediaChunkIterators(@Nullable HlsMediaChunk previous, long loadPositionUs) {
        int i;
        HlsChunkSource hlsChunkSource = this;
        HlsMediaChunk hlsMediaChunk = previous;
        if (hlsMediaChunk == null) {
            i = -1;
        } else {
            i = hlsChunkSource.trackGroup.indexOf(hlsMediaChunk.trackFormat);
        }
        int oldVariantIndex = i;
        MediaChunkIterator[] chunkIterators = new MediaChunkIterator[hlsChunkSource.trackSelection.length()];
        int i2 = 0;
        while (i2 < chunkIterators.length) {
            int variantIndex = hlsChunkSource.trackSelection.getIndexInTrackGroup(i2);
            HlsUrl variantUrl = hlsChunkSource.variants[variantIndex];
            if (hlsChunkSource.playlistTracker.isSnapshotValid(variantUrl)) {
                HlsMediaPlaylist playlist = hlsChunkSource.playlistTracker.getPlaylistSnapshot(variantUrl, false);
                long startOfPlaylistInPeriodUs = playlist.startTimeUs - hlsChunkSource.playlistTracker.getInitialStartTimeUs();
                long startOfPlaylistInPeriodUs2 = startOfPlaylistInPeriodUs;
                long chunkMediaSequence = getChunkMediaSequence(previous, variantIndex != oldVariantIndex, playlist, startOfPlaylistInPeriodUs, loadPositionUs);
                if (chunkMediaSequence < playlist.mediaSequence) {
                    chunkIterators[i2] = MediaChunkIterator.EMPTY;
                } else {
                    chunkIterators[i2] = new HlsMediaPlaylistSegmentIterator(playlist, startOfPlaylistInPeriodUs2, (int) (chunkMediaSequence - playlist.mediaSequence));
                }
            } else {
                chunkIterators[i2] = MediaChunkIterator.EMPTY;
            }
            i2++;
            hlsChunkSource = this;
            hlsMediaChunk = previous;
        }
        return chunkIterators;
    }

    private long getChunkMediaSequence(@Nullable HlsMediaChunk previous, boolean switchingVariant, HlsMediaPlaylist mediaPlaylist, long startOfPlaylistInPeriodUs, long loadPositionUs) {
        long targetPositionInPeriodUs;
        long targetPositionInPlaylistUs;
        List list;
        Comparable valueOf;
        boolean z;
        HlsChunkSource hlsChunkSource = this;
        HlsMediaChunk hlsMediaChunk = previous;
        HlsMediaPlaylist hlsMediaPlaylist = mediaPlaylist;
        if (hlsMediaChunk != null) {
            if (!switchingVariant) {
                return previous.getNextChunkIndex();
            }
        }
        long endOfPlaylistInPeriodUs = startOfPlaylistInPeriodUs + hlsMediaPlaylist.durationUs;
        if (hlsMediaChunk != null) {
            if (!hlsChunkSource.independentSegments) {
                targetPositionInPeriodUs = hlsMediaChunk.startTimeUs;
                if (hlsMediaPlaylist.hasEndTag && targetPositionInPeriodUs >= endOfPlaylistInPeriodUs) {
                    return hlsMediaPlaylist.mediaSequence + ((long) hlsMediaPlaylist.segments.size());
                }
                targetPositionInPlaylistUs = targetPositionInPeriodUs - startOfPlaylistInPeriodUs;
                list = hlsMediaPlaylist.segments;
                valueOf = Long.valueOf(targetPositionInPlaylistUs);
                if (hlsChunkSource.playlistTracker.isLive()) {
                    if (hlsMediaChunk == null) {
                        z = false;
                        return ((long) Util.binarySearchFloor(list, valueOf, true, z)) + hlsMediaPlaylist.mediaSequence;
                    }
                }
                z = true;
                return ((long) Util.binarySearchFloor(list, valueOf, true, z)) + hlsMediaPlaylist.mediaSequence;
            }
        }
        targetPositionInPeriodUs = loadPositionUs;
        if (hlsMediaPlaylist.hasEndTag) {
        }
        targetPositionInPlaylistUs = targetPositionInPeriodUs - startOfPlaylistInPeriodUs;
        list = hlsMediaPlaylist.segments;
        valueOf = Long.valueOf(targetPositionInPlaylistUs);
        if (hlsChunkSource.playlistTracker.isLive()) {
            if (hlsMediaChunk == null) {
                z = false;
                return ((long) Util.binarySearchFloor(list, valueOf, true, z)) + hlsMediaPlaylist.mediaSequence;
            }
        }
        z = true;
        return ((long) Util.binarySearchFloor(list, valueOf, true, z)) + hlsMediaPlaylist.mediaSequence;
    }

    private long resolveTimeToLiveEdgeUs(long playbackPositionUs) {
        return (this.liveEdgeInPeriodTimeUs > C0555C.TIME_UNSET ? 1 : (this.liveEdgeInPeriodTimeUs == C0555C.TIME_UNSET ? 0 : -1)) != 0 ? this.liveEdgeInPeriodTimeUs - playbackPositionUs : C0555C.TIME_UNSET;
    }

    private void updateLiveEdgeTimeUs(HlsMediaPlaylist mediaPlaylist) {
        long j;
        if (mediaPlaylist.hasEndTag) {
            j = C0555C.TIME_UNSET;
        } else {
            j = mediaPlaylist.getEndTimeUs() - this.playlistTracker.getInitialStartTimeUs();
        }
        this.liveEdgeInPeriodTimeUs = j;
    }

    private EncryptionKeyChunk newEncryptionKeyChunk(Uri keyUri, String iv, int variantIndex, int trackSelectionReason, Object trackSelectionData) {
        return new EncryptionKeyChunk(this.encryptionDataSource, new DataSpec(keyUri, 0, -1, null, 1), this.variants[variantIndex].format, trackSelectionReason, trackSelectionData, this.scratchSpace, iv);
    }

    private void setEncryptionData(Uri keyUri, String iv, byte[] secretKey) {
        String trimmedIv;
        if (Util.toLowerInvariant(iv).startsWith("0x")) {
            trimmedIv = iv.substring(2);
        } else {
            trimmedIv = iv;
        }
        byte[] ivData = new BigInteger(trimmedIv, 16).toByteArray();
        byte[] ivDataWithPadding = new byte[16];
        int offset = ivData.length > 16 ? ivData.length - 16 : 0;
        System.arraycopy(ivData, offset, ivDataWithPadding, (ivDataWithPadding.length - ivData.length) + offset, ivData.length - offset);
        this.encryptionKeyUri = keyUri;
        this.encryptionKey = secretKey;
        this.encryptionIvString = iv;
        this.encryptionIv = ivDataWithPadding;
    }

    private void clearEncryptionData() {
        this.encryptionKeyUri = null;
        this.encryptionKey = null;
        this.encryptionIvString = null;
        this.encryptionIv = null;
    }
}
