package com.google.android.exoplayer2.source.hls;

import android.util.Pair;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.extractor.DefaultExtractorInput;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.Metadata.Entry;
import com.google.android.exoplayer2.metadata.id3.Id3Decoder;
import com.google.android.exoplayer2.metadata.id3.PrivFrame;
import com.google.android.exoplayer2.source.chunk.MediaChunk;
import com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist.HlsUrl;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.TimestampAdjuster;
import com.google.android.exoplayer2.util.Util;
import java.io.EOFException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

final class HlsMediaChunk extends MediaChunk {
    public static final String PRIV_TIMESTAMP_FRAME_OWNER = "com.apple.streaming.transportStreamTimestamp";
    private static final AtomicInteger uidSource = new AtomicInteger();
    public final int discontinuitySequenceNumber;
    private final DrmInitData drmInitData;
    private Extractor extractor;
    private final HlsExtractorFactory extractorFactory;
    private final boolean hasGapTag;
    public final HlsUrl hlsUrl;
    private final ParsableByteArray id3Data;
    private final Id3Decoder id3Decoder;
    private final DataSource initDataSource;
    private final DataSpec initDataSpec;
    private boolean initLoadCompleted;
    private int initSegmentBytesLoaded;
    private final boolean isEncrypted;
    private final boolean isMasterTimestampSource;
    private volatile boolean loadCanceled;
    private boolean loadCompleted;
    private final List<Format> muxedCaptionFormats;
    private int nextLoadPosition;
    private HlsSampleStreamWrapper output;
    private final Extractor previousExtractor;
    private final boolean shouldSpliceIn;
    private final TimestampAdjuster timestampAdjuster;
    public final int uid;

    private void loadMedia() throws java.io.IOException, java.lang.InterruptedException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:38:0x0084 in {4, 5, 6, 7, 10, 13, 14, 19, 20, 27, 31, 34, 37} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r8 = this;
        r0 = r8.isEncrypted;
        if (r0 == 0) goto L_0x000e;
    L_0x0004:
        r0 = r8.dataSpec;
        r1 = r8.nextLoadPosition;
        if (r1 == 0) goto L_0x000c;
    L_0x000a:
        r1 = 1;
        goto L_0x000d;
    L_0x000c:
        r1 = 0;
    L_0x000d:
        goto L_0x0018;
    L_0x000e:
        r0 = r8.dataSpec;
        r1 = r8.nextLoadPosition;
        r1 = (long) r1;
        r0 = r0.subrange(r1);
        r1 = 0;
    L_0x0018:
        r2 = r8.isMasterTimestampSource;
        if (r2 != 0) goto L_0x0022;
    L_0x001c:
        r2 = r8.timestampAdjuster;
        r2.waitUntilInitialized();
        goto L_0x003a;
    L_0x0022:
        r2 = r8.timestampAdjuster;
        r2 = r2.getFirstSampleTimestampUs();
        r4 = 9223372036854775807; // 0x7fffffffffffffff float:NaN double:NaN;
        r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r6 != 0) goto L_0x0039;
    L_0x0031:
        r2 = r8.timestampAdjuster;
        r3 = r8.startTimeUs;
        r2.setFirstSampleTimestampUs(r3);
        goto L_0x003a;
    L_0x003a:
        r2 = r8.dataSource;	 Catch:{ all -> 0x007d }
        r2 = r8.prepareExtraction(r2, r0);	 Catch:{ all -> 0x007d }
        if (r1 == 0) goto L_0x0048;	 Catch:{ all -> 0x007d }
    L_0x0042:
        r3 = r8.nextLoadPosition;	 Catch:{ all -> 0x007d }
        r2.skipFully(r3);	 Catch:{ all -> 0x007d }
        goto L_0x0049;
    L_0x0049:
        r3 = 0;
    L_0x004a:
        if (r3 != 0) goto L_0x0068;
    L_0x004c:
        r4 = r8.loadCanceled;	 Catch:{ all -> 0x0059 }
        if (r4 != 0) goto L_0x0068;	 Catch:{ all -> 0x0059 }
    L_0x0050:
        r4 = r8.extractor;	 Catch:{ all -> 0x0059 }
        r5 = 0;	 Catch:{ all -> 0x0059 }
        r4 = r4.read(r2, r5);	 Catch:{ all -> 0x0059 }
        r3 = r4;
        goto L_0x004a;
    L_0x0059:
        r3 = move-exception;
        r4 = r2.getPosition();	 Catch:{ all -> 0x007d }
        r6 = r8.dataSpec;	 Catch:{ all -> 0x007d }
        r6 = r6.absoluteStreamPosition;	 Catch:{ all -> 0x007d }
        r4 = r4 - r6;	 Catch:{ all -> 0x007d }
        r4 = (int) r4;	 Catch:{ all -> 0x007d }
        r8.nextLoadPosition = r4;	 Catch:{ all -> 0x007d }
        throw r3;	 Catch:{ all -> 0x007d }
        r3 = r2.getPosition();	 Catch:{ all -> 0x007d }
        r5 = r8.dataSpec;	 Catch:{ all -> 0x007d }
        r5 = r5.absoluteStreamPosition;	 Catch:{ all -> 0x007d }
        r3 = r3 - r5;	 Catch:{ all -> 0x007d }
        r3 = (int) r3;	 Catch:{ all -> 0x007d }
        r8.nextLoadPosition = r3;	 Catch:{ all -> 0x007d }
        r2 = r8.dataSource;
        com.google.android.exoplayer2.util.Util.closeQuietly(r2);
        return;
    L_0x007d:
        r2 = move-exception;
        r3 = r8.dataSource;
        com.google.android.exoplayer2.util.Util.closeQuietly(r3);
        throw r2;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.hls.HlsMediaChunk.loadMedia():void");
    }

    public HlsMediaChunk(HlsExtractorFactory extractorFactory, DataSource dataSource, DataSpec dataSpec, DataSpec initDataSpec, HlsUrl hlsUrl, List<Format> muxedCaptionFormats, int trackSelectionReason, Object trackSelectionData, long startTimeUs, long endTimeUs, long chunkMediaSequence, int discontinuitySequenceNumber, boolean hasGapTag, boolean isMasterTimestampSource, TimestampAdjuster timestampAdjuster, HlsMediaChunk previousChunk, DrmInitData drmInitData, byte[] fullSegmentEncryptionKey, byte[] encryptionIv) {
        HlsUrl hlsUrl2 = hlsUrl;
        int i = discontinuitySequenceNumber;
        HlsMediaChunk hlsMediaChunk = previousChunk;
        super(buildDataSource(dataSource, fullSegmentEncryptionKey, encryptionIv), dataSpec, hlsUrl2.format, trackSelectionReason, trackSelectionData, startTimeUs, endTimeUs, chunkMediaSequence);
        this.discontinuitySequenceNumber = i;
        this.initDataSpec = initDataSpec;
        this.hlsUrl = hlsUrl2;
        this.isMasterTimestampSource = isMasterTimestampSource;
        this.timestampAdjuster = timestampAdjuster;
        boolean z = true;
        r12.isEncrypted = fullSegmentEncryptionKey != null;
        r12.hasGapTag = hasGapTag;
        r12.extractorFactory = extractorFactory;
        r12.muxedCaptionFormats = muxedCaptionFormats;
        r12.drmInitData = drmInitData;
        Extractor previousExtractor = null;
        if (hlsMediaChunk != null) {
            Extractor extractor;
            r12.id3Decoder = hlsMediaChunk.id3Decoder;
            r12.id3Data = hlsMediaChunk.id3Data;
            if (hlsMediaChunk.hlsUrl == hlsUrl2) {
                if (hlsMediaChunk.loadCompleted) {
                    z = false;
                }
            }
            r12.shouldSpliceIn = z;
            if (hlsMediaChunk.discontinuitySequenceNumber == i) {
                if (!r12.shouldSpliceIn) {
                    extractor = hlsMediaChunk.extractor;
                    previousExtractor = extractor;
                }
            }
            extractor = null;
            previousExtractor = extractor;
        } else {
            r12.id3Decoder = new Id3Decoder();
            r12.id3Data = new ParsableByteArray(10);
            r12.shouldSpliceIn = false;
        }
        r12.previousExtractor = previousExtractor;
        r12.initDataSource = dataSource;
        r12.uid = uidSource.getAndIncrement();
    }

    public void init(HlsSampleStreamWrapper output) {
        this.output = output;
    }

    public boolean isLoadCompleted() {
        return this.loadCompleted;
    }

    public void cancelLoad() {
        this.loadCanceled = true;
    }

    public void load() throws IOException, InterruptedException {
        maybeLoadInitData();
        if (!this.loadCanceled) {
            if (!this.hasGapTag) {
                loadMedia();
            }
            this.loadCompleted = true;
        }
    }

    private void maybeLoadInitData() throws IOException, InterruptedException {
        if (!this.initLoadCompleted) {
            DataSpec initSegmentDataSpec = this.initDataSpec;
            if (initSegmentDataSpec != null) {
                DefaultExtractorInput input;
                try {
                    input = prepareExtraction(this.initDataSource, initSegmentDataSpec.subrange((long) this.initSegmentBytesLoaded));
                    int result = 0;
                    while (result == 0) {
                        if (this.loadCanceled) {
                            break;
                        }
                        result = this.extractor.read(input, null);
                    }
                    this.initSegmentBytesLoaded = (int) (input.getPosition() - this.initDataSpec.absoluteStreamPosition);
                    Util.closeQuietly(this.initDataSource);
                    this.initLoadCompleted = true;
                } catch (Throwable th) {
                    Util.closeQuietly(this.initDataSource);
                }
            }
        }
    }

    private DefaultExtractorInput prepareExtraction(DataSource dataSource, DataSpec dataSpec) throws IOException, InterruptedException {
        DataSpec dataSpec2 = dataSpec;
        DefaultExtractorInput extractorInput = new DefaultExtractorInput(dataSource, dataSpec2.absoluteStreamPosition, dataSource.open(dataSpec));
        if (this.extractor == null) {
            long id3Timestamp = peekId3PrivTimestamp(extractorInput);
            extractorInput.resetPeekPosition();
            Pair<Extractor, Boolean> extractorData = r0.extractorFactory.createExtractor(r0.previousExtractor, dataSpec2.uri, r0.trackFormat, r0.muxedCaptionFormats, r0.drmInitData, r0.timestampAdjuster, dataSource.getResponseHeaders(), extractorInput);
            r0.extractor = (Extractor) extractorData.first;
            boolean z = true;
            boolean reusingExtractor = r0.extractor == r0.previousExtractor;
            if (((Boolean) extractorData.second).booleanValue()) {
                r0.output.setSampleOffsetUs(id3Timestamp != C0555C.TIME_UNSET ? r0.timestampAdjuster.adjustTsTimestamp(id3Timestamp) : r0.startTimeUs);
            }
            if (!reusingExtractor || r0.initDataSpec == null) {
                z = false;
            }
            r0.initLoadCompleted = z;
            r0.output.init(r0.uid, r0.shouldSpliceIn, reusingExtractor);
            if (!reusingExtractor) {
                r0.extractor.init(r0.output);
            }
        }
        return extractorInput;
    }

    private long peekId3PrivTimestamp(ExtractorInput input) throws IOException, InterruptedException {
        input.resetPeekPosition();
        try {
            input.peekFully(this.id3Data.data, 0, 10);
            this.id3Data.reset(10);
            if (this.id3Data.readUnsignedInt24() != Id3Decoder.ID3_TAG) {
                return C0555C.TIME_UNSET;
            }
            this.id3Data.skipBytes(3);
            int id3Size = this.id3Data.readSynchSafeInt();
            int requiredCapacity = id3Size + 10;
            if (requiredCapacity > this.id3Data.capacity()) {
                byte[] data = this.id3Data.data;
                this.id3Data.reset(requiredCapacity);
                System.arraycopy(data, 0, this.id3Data.data, 0, 10);
            }
            input.peekFully(this.id3Data.data, 10, id3Size);
            Metadata metadata = this.id3Decoder.decode(this.id3Data.data, id3Size);
            if (metadata == null) {
                return C0555C.TIME_UNSET;
            }
            int metadataLength = metadata.length();
            for (int i = 0; i < metadataLength; i++) {
                Entry frame = metadata.get(i);
                if (frame instanceof PrivFrame) {
                    PrivFrame privFrame = (PrivFrame) frame;
                    if (PRIV_TIMESTAMP_FRAME_OWNER.equals(privFrame.owner)) {
                        System.arraycopy(privFrame.privateData, 0, this.id3Data.data, 0, 8);
                        this.id3Data.reset(8);
                        return this.id3Data.readLong() & 8589934591L;
                    }
                }
            }
            return C0555C.TIME_UNSET;
        } catch (EOFException e) {
            return C0555C.TIME_UNSET;
        }
    }

    private static DataSource buildDataSource(DataSource dataSource, byte[] fullSegmentEncryptionKey, byte[] encryptionIv) {
        if (fullSegmentEncryptionKey != null) {
            return new Aes128DataSource(dataSource, fullSegmentEncryptionKey, encryptionIv);
        }
        return dataSource;
    }
}
