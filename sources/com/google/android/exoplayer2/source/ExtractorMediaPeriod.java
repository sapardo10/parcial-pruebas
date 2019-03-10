package com.google.android.exoplayer2.source;

import android.net.Uri;
import android.os.Handler;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.FormatHolder;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.extractor.DefaultExtractorInput;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.SeekMap.SeekPoints;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.source.SampleQueue.UpstreamFormatChangedListener;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.Loader;
import com.google.android.exoplayer2.upstream.Loader.Callback;
import com.google.android.exoplayer2.upstream.Loader.LoadErrorAction;
import com.google.android.exoplayer2.upstream.Loader.Loadable;
import com.google.android.exoplayer2.upstream.Loader.ReleaseCallback;
import com.google.android.exoplayer2.upstream.StatsDataSource;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.ConditionVariable;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.Arrays;

final class ExtractorMediaPeriod implements MediaPeriod, ExtractorOutput, Callback<ExtractingLoadable>, ReleaseCallback, UpstreamFormatChangedListener {
    private static final long DEFAULT_LAST_SAMPLE_DURATION_US = 10000;
    private final Allocator allocator;
    @Nullable
    private MediaPeriod.Callback callback;
    private final long continueLoadingCheckIntervalBytes;
    @Nullable
    private final String customCacheKey;
    private final DataSource dataSource;
    private int dataType;
    private long durationUs;
    private int enabledTrackCount;
    private final MediaSourceEventListener$EventDispatcher eventDispatcher;
    private int extractedSamplesCountAtStartOfLoad;
    private final ExtractorHolder extractorHolder;
    private final Handler handler;
    private boolean haveAudioVideoTracks;
    private long lastSeekPositionUs;
    private long length;
    private final Listener listener;
    private final ConditionVariable loadCondition;
    private final LoadErrorHandlingPolicy loadErrorHandlingPolicy;
    private final Loader loader = new Loader("Loader:ExtractorMediaPeriod");
    private boolean loadingFinished;
    private final Runnable maybeFinishPrepareRunnable;
    private boolean notifiedReadingStarted;
    private boolean notifyDiscontinuity;
    private final Runnable onContinueLoadingRequestedRunnable;
    private boolean pendingDeferredRetry;
    private long pendingResetPositionUs;
    private boolean prepared;
    @Nullable
    private PreparedState preparedState;
    private boolean released;
    private int[] sampleQueueTrackIds;
    private SampleQueue[] sampleQueues;
    private boolean sampleQueuesBuilt;
    @Nullable
    private SeekMap seekMap;
    private boolean seenFirstTrackSelection;
    private final Uri uri;

    private static final class ExtractorHolder {
        @Nullable
        private Extractor extractor;
        private final Extractor[] extractors;

        public com.google.android.exoplayer2.extractor.Extractor selectExtractor(com.google.android.exoplayer2.extractor.ExtractorInput r6, com.google.android.exoplayer2.extractor.ExtractorOutput r7, android.net.Uri r8) throws java.io.IOException, java.lang.InterruptedException {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:22:0x0052 in {2, 10, 13, 14, 15, 19, 21} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
            /*
            r5 = this;
            r0 = r5.extractor;
            if (r0 == 0) goto L_0x0005;
        L_0x0004:
            return r0;
        L_0x0005:
            r0 = r5.extractors;
            r1 = r0.length;
            r2 = 0;
        L_0x0009:
            if (r2 >= r1) goto L_0x0026;
        L_0x000b:
            r3 = r0[r2];
            r4 = r3.sniff(r6);	 Catch:{ EOFException -> 0x001e, all -> 0x0019 }
            if (r4 == 0) goto L_0x001f;	 Catch:{ EOFException -> 0x001e, all -> 0x0019 }
        L_0x0013:
            r5.extractor = r3;	 Catch:{ EOFException -> 0x001e, all -> 0x0019 }
            r6.resetPeekPosition();
            goto L_0x0026;
        L_0x0019:
            r0 = move-exception;
            r6.resetPeekPosition();
            throw r0;
        L_0x001e:
            r4 = move-exception;
        L_0x001f:
            r6.resetPeekPosition();
            r2 = r2 + 1;
            goto L_0x0009;
        L_0x0026:
            r0 = r5.extractor;
            if (r0 == 0) goto L_0x0030;
        L_0x002a:
            r0.init(r7);
            r0 = r5.extractor;
            return r0;
        L_0x0030:
            r0 = new com.google.android.exoplayer2.source.UnrecognizedInputFormatException;
            r1 = new java.lang.StringBuilder;
            r1.<init>();
            r2 = "None of the available extractors (";
            r1.append(r2);
            r2 = r5.extractors;
            r2 = com.google.android.exoplayer2.util.Util.getCommaDelimitedSimpleClassNames(r2);
            r1.append(r2);
            r2 = ") could read the stream.";
            r1.append(r2);
            r1 = r1.toString();
            r0.<init>(r1, r8);
            throw r0;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.ExtractorMediaPeriod.ExtractorHolder.selectExtractor(com.google.android.exoplayer2.extractor.ExtractorInput, com.google.android.exoplayer2.extractor.ExtractorOutput, android.net.Uri):com.google.android.exoplayer2.extractor.Extractor");
        }

        public ExtractorHolder(Extractor[] extractors) {
            this.extractors = extractors;
        }

        public void release() {
            Extractor extractor = this.extractor;
            if (extractor != null) {
                extractor.release();
                this.extractor = null;
            }
        }
    }

    interface Listener {
        void onSourceInfoRefreshed(long j, boolean z);
    }

    private static final class PreparedState {
        public final SeekMap seekMap;
        public final boolean[] trackEnabledStates;
        public final boolean[] trackIsAudioVideoFlags;
        public final boolean[] trackNotifiedDownstreamFormats;
        public final TrackGroupArray tracks;

        public PreparedState(SeekMap seekMap, TrackGroupArray tracks, boolean[] trackIsAudioVideoFlags) {
            this.seekMap = seekMap;
            this.tracks = tracks;
            this.trackIsAudioVideoFlags = trackIsAudioVideoFlags;
            this.trackEnabledStates = new boolean[tracks.length];
            this.trackNotifiedDownstreamFormats = new boolean[tracks.length];
        }
    }

    final class ExtractingLoadable implements Loadable {
        private final StatsDataSource dataSource;
        private DataSpec dataSpec;
        private final ExtractorHolder extractorHolder;
        private final ExtractorOutput extractorOutput;
        private long length = -1;
        private volatile boolean loadCanceled;
        private final ConditionVariable loadCondition;
        private boolean pendingExtractorSeek = true;
        private final PositionHolder positionHolder = new PositionHolder();
        private long seekTimeUs;
        private final Uri uri;

        public ExtractingLoadable(Uri uri, DataSource dataSource, ExtractorHolder extractorHolder, ExtractorOutput extractorOutput, ConditionVariable loadCondition) {
            this.uri = uri;
            this.dataSource = new StatsDataSource(dataSource);
            this.extractorHolder = extractorHolder;
            this.extractorOutput = extractorOutput;
            this.loadCondition = loadCondition;
            this.dataSpec = new DataSpec(uri, this.positionHolder.position, -1, ExtractorMediaPeriod.this.customCacheKey);
        }

        public void cancelLoad() {
            this.loadCanceled = true;
        }

        public void load() throws IOException, InterruptedException {
            int result = 0;
            while (result == 0 && !this.loadCanceled) {
                ExtractorInput input = null;
                try {
                    long position = this.positionHolder.position;
                    this.dataSpec = new DataSpec(this.uri, position, -1, ExtractorMediaPeriod.this.customCacheKey);
                    this.length = this.dataSource.open(this.dataSpec);
                    if (this.length != -1) {
                        this.length += position;
                    }
                    Uri uri = (Uri) Assertions.checkNotNull(this.dataSource.getUri());
                    DefaultExtractorInput input2 = new DefaultExtractorInput(this.dataSource, position, this.length);
                    Extractor extractor = this.extractorHolder.selectExtractor(input2, this.extractorOutput, uri);
                    if (this.pendingExtractorSeek) {
                        extractor.seek(position, this.seekTimeUs);
                        this.pendingExtractorSeek = false;
                    }
                    while (result == 0 && !this.loadCanceled) {
                        this.loadCondition.block();
                        result = extractor.read(input2, this.positionHolder);
                        if (input2.getPosition() > ExtractorMediaPeriod.this.continueLoadingCheckIntervalBytes + position) {
                            position = input2.getPosition();
                            this.loadCondition.close();
                            ExtractorMediaPeriod.this.handler.post(ExtractorMediaPeriod.this.onContinueLoadingRequestedRunnable);
                        }
                    }
                    if (result == 1) {
                        result = 0;
                    } else {
                        this.positionHolder.position = input2.getPosition();
                    }
                    Util.closeQuietly(this.dataSource);
                } catch (Throwable th) {
                    if (result != 1) {
                        if (input != null) {
                            this.positionHolder.position = input.getPosition();
                        }
                    }
                    Util.closeQuietly(this.dataSource);
                }
            }
        }

        private void setLoadPosition(long position, long timeUs) {
            this.positionHolder.position = position;
            this.seekTimeUs = timeUs;
            this.pendingExtractorSeek = true;
        }
    }

    private final class SampleStreamImpl implements SampleStream {
        private final int track;

        public SampleStreamImpl(int track) {
            this.track = track;
        }

        public boolean isReady() {
            return ExtractorMediaPeriod.this.isReady(this.track);
        }

        public void maybeThrowError() throws IOException {
            ExtractorMediaPeriod.this.maybeThrowError();
        }

        public int readData(FormatHolder formatHolder, DecoderInputBuffer buffer, boolean formatRequired) {
            return ExtractorMediaPeriod.this.readData(this.track, formatHolder, buffer, formatRequired);
        }

        public int skipData(long positionUs) {
            return ExtractorMediaPeriod.this.skipData(this.track, positionUs);
        }
    }

    public ExtractorMediaPeriod(Uri uri, DataSource dataSource, Extractor[] extractors, LoadErrorHandlingPolicy loadErrorHandlingPolicy, MediaSourceEventListener$EventDispatcher eventDispatcher, Listener listener, Allocator allocator, @Nullable String customCacheKey, int continueLoadingCheckIntervalBytes) {
        this.uri = uri;
        this.dataSource = dataSource;
        this.loadErrorHandlingPolicy = loadErrorHandlingPolicy;
        this.eventDispatcher = eventDispatcher;
        this.listener = listener;
        this.allocator = allocator;
        this.customCacheKey = customCacheKey;
        this.continueLoadingCheckIntervalBytes = (long) continueLoadingCheckIntervalBytes;
        this.extractorHolder = new ExtractorHolder(extractors);
        this.loadCondition = new ConditionVariable();
        this.maybeFinishPrepareRunnable = new -$$Lambda$ExtractorMediaPeriod$Ll7lI30pD07GZk92Lo8XgkQMAAY();
        this.onContinueLoadingRequestedRunnable = new -$$Lambda$ExtractorMediaPeriod$Hd-sBytb6cpkhM49l8dYCND3wmk();
        this.handler = new Handler();
        this.sampleQueueTrackIds = new int[0];
        this.sampleQueues = new SampleQueue[0];
        this.pendingResetPositionUs = C0555C.TIME_UNSET;
        this.length = -1;
        this.durationUs = C0555C.TIME_UNSET;
        this.dataType = 1;
        eventDispatcher.mediaPeriodCreated();
    }

    public static /* synthetic */ void lambda$new$0(ExtractorMediaPeriod extractorMediaPeriod) {
        if (!extractorMediaPeriod.released) {
            ((MediaPeriod.Callback) Assertions.checkNotNull(extractorMediaPeriod.callback)).onContinueLoadingRequested(extractorMediaPeriod);
        }
    }

    public void release() {
        if (this.prepared) {
            for (SampleQueue sampleQueue : this.sampleQueues) {
                sampleQueue.discardToEnd();
            }
        }
        this.loader.release(this);
        this.handler.removeCallbacksAndMessages(null);
        this.callback = null;
        this.released = true;
        this.eventDispatcher.mediaPeriodReleased();
    }

    public void onLoaderReleased() {
        for (SampleQueue sampleQueue : this.sampleQueues) {
            sampleQueue.reset();
        }
        this.extractorHolder.release();
    }

    public void prepare(MediaPeriod.Callback callback, long positionUs) {
        this.callback = callback;
        this.loadCondition.open();
        startLoading();
    }

    public void maybeThrowPrepareError() throws IOException {
        maybeThrowError();
    }

    public TrackGroupArray getTrackGroups() {
        return getPreparedState().tracks;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public long selectTracks(com.google.android.exoplayer2.trackselection.TrackSelection[] r17, boolean[] r18, com.google.android.exoplayer2.source.SampleStream[] r19, boolean[] r20, long r21) {
        /*
        r16 = this;
        r0 = r16;
        r1 = r17;
        r2 = r19;
        r3 = r21;
        r5 = r16.getPreparedState();
        r6 = r5.tracks;
        r7 = r5.trackEnabledStates;
        r8 = r0.enabledTrackCount;
        r9 = 0;
    L_0x0013:
        r10 = r1.length;
        r11 = 0;
        r12 = 1;
        if (r9 >= r10) goto L_0x0040;
    L_0x0018:
        r10 = r2[r9];
        if (r10 == 0) goto L_0x003c;
    L_0x001c:
        r10 = r1[r9];
        if (r10 == 0) goto L_0x0024;
    L_0x0020:
        r10 = r18[r9];
        if (r10 != 0) goto L_0x003c;
    L_0x0024:
        r10 = r2[r9];
        r10 = (com.google.android.exoplayer2.source.ExtractorMediaPeriod.SampleStreamImpl) r10;
        r10 = r10.track;
        r13 = r7[r10];
        com.google.android.exoplayer2.util.Assertions.checkState(r13);
        r13 = r0.enabledTrackCount;
        r13 = r13 - r12;
        r0.enabledTrackCount = r13;
        r7[r10] = r11;
        r11 = 0;
        r2[r9] = r11;
        goto L_0x003d;
    L_0x003d:
        r9 = r9 + 1;
        goto L_0x0013;
    L_0x0040:
        r9 = r0.seenFirstTrackSelection;
        if (r9 == 0) goto L_0x0047;
    L_0x0044:
        if (r8 != 0) goto L_0x004f;
    L_0x0046:
        goto L_0x004d;
    L_0x0047:
        r9 = 0;
        r13 = (r3 > r9 ? 1 : (r3 == r9 ? 0 : -1));
        if (r13 == 0) goto L_0x004f;
    L_0x004d:
        r9 = 1;
        goto L_0x0050;
    L_0x004f:
        r9 = 0;
    L_0x0050:
        r10 = 0;
    L_0x0051:
        r13 = r1.length;
        if (r10 >= r13) goto L_0x00b8;
    L_0x0054:
        r13 = r2[r10];
        if (r13 != 0) goto L_0x00b2;
    L_0x0058:
        r13 = r1[r10];
        if (r13 == 0) goto L_0x00b2;
    L_0x005c:
        r13 = r1[r10];
        r14 = r13.length();
        if (r14 != r12) goto L_0x0066;
    L_0x0064:
        r14 = 1;
        goto L_0x0067;
    L_0x0066:
        r14 = 0;
    L_0x0067:
        com.google.android.exoplayer2.util.Assertions.checkState(r14);
        r14 = r13.getIndexInTrackGroup(r11);
        if (r14 != 0) goto L_0x0072;
    L_0x0070:
        r14 = 1;
        goto L_0x0073;
    L_0x0072:
        r14 = 0;
    L_0x0073:
        com.google.android.exoplayer2.util.Assertions.checkState(r14);
        r14 = r13.getTrackGroup();
        r14 = r6.indexOf(r14);
        r15 = r7[r14];
        r15 = r15 ^ r12;
        com.google.android.exoplayer2.util.Assertions.checkState(r15);
        r15 = r0.enabledTrackCount;
        r15 = r15 + r12;
        r0.enabledTrackCount = r15;
        r7[r14] = r12;
        r15 = new com.google.android.exoplayer2.source.ExtractorMediaPeriod$SampleStreamImpl;
        r15.<init>(r14);
        r2[r10] = r15;
        r20[r10] = r12;
        if (r9 != 0) goto L_0x00b1;
    L_0x0096:
        r15 = r0.sampleQueues;
        r15 = r15[r14];
        r15.rewind();
        r11 = r15.advanceTo(r3, r12, r12);
        r12 = -1;
        if (r11 != r12) goto L_0x00ad;
    L_0x00a4:
        r11 = r15.getReadIndex();
        if (r11 == 0) goto L_0x00ac;
    L_0x00aa:
        r11 = 1;
        goto L_0x00af;
    L_0x00ac:
        goto L_0x00ae;
    L_0x00ae:
        r11 = 0;
    L_0x00af:
        r9 = r11;
        goto L_0x00b3;
    L_0x00b1:
        goto L_0x00b3;
    L_0x00b3:
        r10 = r10 + 1;
        r11 = 0;
        r12 = 1;
        goto L_0x0051;
    L_0x00b8:
        r10 = r0.enabledTrackCount;
        if (r10 != 0) goto L_0x00ea;
    L_0x00bc:
        r10 = 0;
        r0.pendingDeferredRetry = r10;
        r0.notifyDiscontinuity = r10;
        r11 = r0.loader;
        r11 = r11.isLoading();
        if (r11 == 0) goto L_0x00dc;
    L_0x00c9:
        r11 = r0.sampleQueues;
        r12 = r11.length;
    L_0x00cc:
        if (r10 >= r12) goto L_0x00d6;
    L_0x00ce:
        r13 = r11[r10];
        r13.discardToEnd();
        r10 = r10 + 1;
        goto L_0x00cc;
    L_0x00d6:
        r10 = r0.loader;
        r10.cancelLoading();
        goto L_0x0102;
    L_0x00dc:
        r11 = r0.sampleQueues;
        r12 = r11.length;
    L_0x00df:
        if (r10 >= r12) goto L_0x00e9;
    L_0x00e1:
        r13 = r11[r10];
        r13.reset();
        r10 = r10 + 1;
        goto L_0x00df;
    L_0x00e9:
        goto L_0x0102;
    L_0x00ea:
        if (r9 == 0) goto L_0x0101;
    L_0x00ec:
        r3 = r0.seekToUs(r3);
        r10 = 0;
    L_0x00f1:
        r11 = r2.length;
        if (r10 >= r11) goto L_0x0100;
    L_0x00f4:
        r11 = r2[r10];
        if (r11 == 0) goto L_0x00fc;
    L_0x00f8:
        r11 = 1;
        r20[r10] = r11;
        goto L_0x00fd;
    L_0x00fd:
        r10 = r10 + 1;
        goto L_0x00f1;
    L_0x0100:
        goto L_0x0102;
    L_0x0102:
        r10 = 1;
        r0.seenFirstTrackSelection = r10;
        return r3;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.ExtractorMediaPeriod.selectTracks(com.google.android.exoplayer2.trackselection.TrackSelection[], boolean[], com.google.android.exoplayer2.source.SampleStream[], boolean[], long):long");
    }

    public void discardBuffer(long positionUs, boolean toKeyframe) {
        if (!isPendingReset()) {
            boolean[] trackEnabledStates = getPreparedState().trackEnabledStates;
            int trackCount = this.sampleQueues.length;
            for (int i = 0; i < trackCount; i++) {
                this.sampleQueues[i].discardTo(positionUs, toKeyframe, trackEnabledStates[i]);
            }
        }
    }

    public void reevaluateBuffer(long positionUs) {
    }

    public boolean continueLoading(long playbackPositionUs) {
        if (!(this.loadingFinished || this.pendingDeferredRetry)) {
            if (!this.prepared || this.enabledTrackCount != 0) {
                boolean continuedLoading = this.loadCondition.open();
                if (!this.loader.isLoading()) {
                    startLoading();
                    continuedLoading = true;
                }
                return continuedLoading;
            }
        }
        return false;
    }

    public long getNextLoadPositionUs() {
        return this.enabledTrackCount == 0 ? Long.MIN_VALUE : getBufferedPositionUs();
    }

    public long readDiscontinuity() {
        if (!this.notifiedReadingStarted) {
            this.eventDispatcher.readingStarted();
            this.notifiedReadingStarted = true;
        }
        if (this.notifyDiscontinuity) {
            if (!this.loadingFinished) {
                if (getExtractedSamplesCount() > this.extractedSamplesCountAtStartOfLoad) {
                }
            }
            this.notifyDiscontinuity = false;
            return this.lastSeekPositionUs;
        }
        return C0555C.TIME_UNSET;
    }

    public long getBufferedPositionUs() {
        boolean[] trackIsAudioVideoFlags = getPreparedState().trackIsAudioVideoFlags;
        if (this.loadingFinished) {
            return Long.MIN_VALUE;
        }
        if (isPendingReset()) {
            return this.pendingResetPositionUs;
        }
        long largestQueuedTimestampUs;
        if (this.haveAudioVideoTracks) {
            largestQueuedTimestampUs = Long.MAX_VALUE;
            int trackCount = this.sampleQueues.length;
            for (int i = 0; i < trackCount; i++) {
                if (trackIsAudioVideoFlags[i]) {
                    largestQueuedTimestampUs = Math.min(largestQueuedTimestampUs, this.sampleQueues[i].getLargestQueuedTimestampUs());
                }
            }
        } else {
            largestQueuedTimestampUs = getLargestQueuedTimestampUs();
        }
        return largestQueuedTimestampUs == Long.MIN_VALUE ? this.lastSeekPositionUs : largestQueuedTimestampUs;
    }

    public long seekToUs(long positionUs) {
        PreparedState preparedState = getPreparedState();
        SeekMap seekMap = preparedState.seekMap;
        boolean[] trackIsAudioVideoFlags = preparedState.trackIsAudioVideoFlags;
        positionUs = seekMap.isSeekable() ? positionUs : 0;
        int i = 0;
        this.notifyDiscontinuity = false;
        this.lastSeekPositionUs = positionUs;
        if (isPendingReset()) {
            this.pendingResetPositionUs = positionUs;
            return positionUs;
        }
        if (this.dataType != 7) {
            if (seekInsideBufferUs(trackIsAudioVideoFlags, positionUs)) {
                return positionUs;
            }
        }
        this.pendingDeferredRetry = false;
        this.pendingResetPositionUs = positionUs;
        this.loadingFinished = false;
        if (this.loader.isLoading()) {
            this.loader.cancelLoading();
        } else {
            SampleQueue[] sampleQueueArr = this.sampleQueues;
            int length = sampleQueueArr.length;
            while (i < length) {
                sampleQueueArr[i].reset();
                i++;
            }
        }
        return positionUs;
    }

    public long getAdjustedSeekPositionUs(long positionUs, SeekParameters seekParameters) {
        SeekMap seekMap = getPreparedState().seekMap;
        if (!seekMap.isSeekable()) {
            return 0;
        }
        SeekPoints seekPoints = seekMap.getSeekPoints(positionUs);
        return Util.resolveSeekPositionUs(positionUs, seekParameters, seekPoints.first.timeUs, seekPoints.second.timeUs);
    }

    boolean isReady(int track) {
        return !suppressRead() && (this.loadingFinished || this.sampleQueues[track].hasNextSample());
    }

    void maybeThrowError() throws IOException {
        this.loader.maybeThrowError(this.loadErrorHandlingPolicy.getMinimumLoadableRetryCount(this.dataType));
    }

    int readData(int track, FormatHolder formatHolder, DecoderInputBuffer buffer, boolean formatRequired) {
        if (suppressRead()) {
            return -3;
        }
        maybeNotifyDownstreamFormat(track);
        int result = this.sampleQueues[track].read(formatHolder, buffer, formatRequired, this.loadingFinished, this.lastSeekPositionUs);
        if (result == -3) {
            maybeStartDeferredRetry(track);
        }
        return result;
    }

    int skipData(int track, long positionUs) {
        if (suppressRead()) {
            return 0;
        }
        int skipCount;
        maybeNotifyDownstreamFormat(track);
        SampleQueue sampleQueue = this.sampleQueues[track];
        if (!this.loadingFinished || positionUs <= sampleQueue.getLargestQueuedTimestampUs()) {
            skipCount = sampleQueue.advanceTo(positionUs, true, true);
            if (skipCount == -1) {
                skipCount = 0;
            }
        } else {
            skipCount = sampleQueue.advanceToEnd();
        }
        if (skipCount == 0) {
            maybeStartDeferredRetry(track);
        }
        return skipCount;
    }

    private void maybeNotifyDownstreamFormat(int track) {
        PreparedState preparedState = getPreparedState();
        boolean[] trackNotifiedDownstreamFormats = preparedState.trackNotifiedDownstreamFormats;
        if (!trackNotifiedDownstreamFormats[track]) {
            Format trackFormat = preparedState.tracks.get(track).getFormat(0);
            this.eventDispatcher.downstreamFormatChanged(MimeTypes.getTrackType(trackFormat.sampleMimeType), trackFormat, 0, null, this.lastSeekPositionUs);
            trackNotifiedDownstreamFormats[track] = true;
        }
    }

    private void maybeStartDeferredRetry(int track) {
        boolean[] trackIsAudioVideoFlags = getPreparedState().trackIsAudioVideoFlags;
        if (this.pendingDeferredRetry && trackIsAudioVideoFlags[track]) {
            if (!this.sampleQueues[track].hasNextSample()) {
                this.pendingResetPositionUs = 0;
                int i = 0;
                this.pendingDeferredRetry = false;
                this.notifyDiscontinuity = true;
                this.lastSeekPositionUs = 0;
                this.extractedSamplesCountAtStartOfLoad = 0;
                SampleQueue[] sampleQueueArr = this.sampleQueues;
                int length = sampleQueueArr.length;
                while (i < length) {
                    sampleQueueArr[i].reset();
                    i++;
                }
                ((MediaPeriod.Callback) Assertions.checkNotNull(this.callback)).onContinueLoadingRequested(this);
            }
        }
    }

    private boolean suppressRead() {
        if (!this.notifyDiscontinuity) {
            if (!isPendingReset()) {
                return false;
            }
        }
        return true;
    }

    public void onLoadCompleted(ExtractingLoadable loadable, long elapsedRealtimeMs, long loadDurationMs) {
        if (this.durationUs == C0555C.TIME_UNSET) {
            SeekMap seekMap = (SeekMap) Assertions.checkNotNull(r0.seekMap);
            long largestQueuedTimestampUs = getLargestQueuedTimestampUs();
            r0.durationUs = largestQueuedTimestampUs == Long.MIN_VALUE ? 0 : DEFAULT_LAST_SAMPLE_DURATION_US + largestQueuedTimestampUs;
            r0.listener.onSourceInfoRefreshed(r0.durationUs, seekMap.isSeekable());
        }
        r0.eventDispatcher.loadCompleted(loadable.dataSpec, loadable.dataSource.getLastOpenedUri(), loadable.dataSource.getLastResponseHeaders(), 1, -1, null, 0, null, loadable.seekTimeUs, r0.durationUs, elapsedRealtimeMs, loadDurationMs, loadable.dataSource.getBytesRead());
        copyLengthFromLoader(loadable);
        r0.loadingFinished = true;
        ((MediaPeriod.Callback) Assertions.checkNotNull(r0.callback)).onContinueLoadingRequested(r0);
    }

    public void onLoadCanceled(ExtractingLoadable loadable, long elapsedRealtimeMs, long loadDurationMs, boolean released) {
        this.eventDispatcher.loadCanceled(loadable.dataSpec, loadable.dataSource.getLastOpenedUri(), loadable.dataSource.getLastResponseHeaders(), 1, -1, null, 0, null, loadable.seekTimeUs, this.durationUs, elapsedRealtimeMs, loadDurationMs, loadable.dataSource.getBytesRead());
        if (!released) {
            copyLengthFromLoader(loadable);
            for (SampleQueue sampleQueue : r0.sampleQueues) {
                sampleQueue.reset();
            }
            if (r0.enabledTrackCount > 0) {
                ((MediaPeriod.Callback) Assertions.checkNotNull(r0.callback)).onContinueLoadingRequested(r0);
            }
        }
    }

    public LoadErrorAction onLoadError(ExtractingLoadable loadable, long elapsedRealtimeMs, long loadDurationMs, IOException error, int errorCount) {
        LoadErrorAction loadErrorAction;
        copyLengthFromLoader(loadable);
        long retryDelayMs = this.loadErrorHandlingPolicy.getRetryDelayMsFor(this.dataType, this.durationUs, error, errorCount);
        if (retryDelayMs == C0555C.TIME_UNSET) {
            loadErrorAction = Loader.DONT_RETRY_FATAL;
            ExtractingLoadable extractingLoadable = loadable;
        } else {
            int extractedSamplesCount = getExtractedSamplesCount();
            loadErrorAction = configureRetry(loadable, extractedSamplesCount) ? Loader.createRetryAction(extractedSamplesCount > r0.extractedSamplesCountAtStartOfLoad, retryDelayMs) : Loader.DONT_RETRY;
        }
        r0.eventDispatcher.loadError(loadable.dataSpec, loadable.dataSource.getLastOpenedUri(), loadable.dataSource.getLastResponseHeaders(), 1, -1, null, 0, null, loadable.seekTimeUs, r0.durationUs, elapsedRealtimeMs, loadDurationMs, loadable.dataSource.getBytesRead(), error, loadErrorAction.isRetry() ^ 1);
        return loadErrorAction;
    }

    public TrackOutput track(int id, int type) {
        int trackCount = this.sampleQueues.length;
        for (int i = 0; i < trackCount; i++) {
            if (this.sampleQueueTrackIds[i] == id) {
                return this.sampleQueues[i];
            }
        }
        SampleQueue trackOutput = new SampleQueue(this.allocator);
        trackOutput.setUpstreamFormatChangeListener(this);
        this.sampleQueueTrackIds = Arrays.copyOf(this.sampleQueueTrackIds, trackCount + 1);
        this.sampleQueueTrackIds[trackCount] = id;
        SampleQueue[] sampleQueues = (SampleQueue[]) Arrays.copyOf(this.sampleQueues, trackCount + 1);
        sampleQueues[trackCount] = trackOutput;
        this.sampleQueues = (SampleQueue[]) Util.castNonNullTypeArray(sampleQueues);
        return trackOutput;
    }

    public void endTracks() {
        this.sampleQueuesBuilt = true;
        this.handler.post(this.maybeFinishPrepareRunnable);
    }

    public void seekMap(SeekMap seekMap) {
        this.seekMap = seekMap;
        this.handler.post(this.maybeFinishPrepareRunnable);
    }

    public void onUpstreamFormatChanged(Format format) {
        this.handler.post(this.maybeFinishPrepareRunnable);
    }

    private void maybeFinishPrepare() {
        SeekMap seekMap = this.seekMap;
        if (!(this.released || this.prepared || !this.sampleQueuesBuilt)) {
            if (seekMap != null) {
                int i;
                SampleQueue[] sampleQueueArr = this.sampleQueues;
                int length = sampleQueueArr.length;
                int i2 = 0;
                while (i2 < length) {
                    if (sampleQueueArr[i2].getUpstreamFormat() != null) {
                        i2++;
                    } else {
                        return;
                    }
                }
                this.loadCondition.close();
                int trackCount = this.sampleQueues.length;
                TrackGroup[] trackArray = new TrackGroup[trackCount];
                boolean[] trackIsAudioVideoFlags = new boolean[trackCount];
                this.durationUs = seekMap.getDurationUs();
                int i3 = 0;
                while (true) {
                    boolean isAudioVideo = true;
                    if (i3 >= trackCount) {
                        break;
                    }
                    trackArray[i3] = new TrackGroup(this.sampleQueues[i3].getUpstreamFormat());
                    String mimeType = trackFormat.sampleMimeType;
                    if (!MimeTypes.isVideo(mimeType)) {
                        if (!MimeTypes.isAudio(mimeType)) {
                            isAudioVideo = false;
                        }
                    }
                    trackIsAudioVideoFlags[i3] = isAudioVideo;
                    this.haveAudioVideoTracks |= isAudioVideo;
                    i3++;
                }
                if (this.length == -1) {
                    if (seekMap.getDurationUs() == C0555C.TIME_UNSET) {
                        i = 7;
                        this.dataType = i;
                        this.preparedState = new PreparedState(seekMap, new TrackGroupArray(trackArray), trackIsAudioVideoFlags);
                        this.prepared = true;
                        this.listener.onSourceInfoRefreshed(this.durationUs, seekMap.isSeekable());
                        ((MediaPeriod.Callback) Assertions.checkNotNull(this.callback)).onPrepared(this);
                    }
                }
                i = 1;
                this.dataType = i;
                this.preparedState = new PreparedState(seekMap, new TrackGroupArray(trackArray), trackIsAudioVideoFlags);
                this.prepared = true;
                this.listener.onSourceInfoRefreshed(this.durationUs, seekMap.isSeekable());
                ((MediaPeriod.Callback) Assertions.checkNotNull(this.callback)).onPrepared(this);
            }
        }
    }

    private PreparedState getPreparedState() {
        return (PreparedState) Assertions.checkNotNull(this.preparedState);
    }

    private void copyLengthFromLoader(ExtractingLoadable loadable) {
        if (this.length == -1) {
            this.length = loadable.length;
        }
    }

    private void startLoading() {
        ExtractingLoadable loadable = new ExtractingLoadable(this.uri, this.dataSource, this.extractorHolder, this, this.loadCondition);
        if (this.prepared) {
            SeekMap seekMap = getPreparedState().seekMap;
            Assertions.checkState(isPendingReset());
            long j = r7.durationUs;
            if (j == C0555C.TIME_UNSET || r7.pendingResetPositionUs < j) {
                loadable.setLoadPosition(seekMap.getSeekPoints(r7.pendingResetPositionUs).first.position, r7.pendingResetPositionUs);
                r7.pendingResetPositionUs = C0555C.TIME_UNSET;
            } else {
                r7.loadingFinished = true;
                r7.pendingResetPositionUs = C0555C.TIME_UNSET;
                return;
            }
        }
        r7.extractedSamplesCountAtStartOfLoad = getExtractedSamplesCount();
        r7.eventDispatcher.loadStarted(loadable.dataSpec, 1, -1, null, 0, null, loadable.seekTimeUs, r7.durationUs, r7.loader.startLoading(loadable, r7, r7.loadErrorHandlingPolicy.getMinimumLoadableRetryCount(r7.dataType)));
    }

    private boolean configureRetry(ExtractingLoadable loadable, int currentExtractedSampleCount) {
        if (this.length == -1) {
            SeekMap seekMap = this.seekMap;
            if (seekMap != null) {
                if (seekMap.getDurationUs() != C0555C.TIME_UNSET) {
                }
            }
            int i = 0;
            if (!this.prepared || suppressRead()) {
                this.notifyDiscontinuity = this.prepared;
                this.lastSeekPositionUs = 0;
                this.extractedSamplesCountAtStartOfLoad = 0;
                SampleQueue[] sampleQueueArr = this.sampleQueues;
                int length = sampleQueueArr.length;
                while (i < length) {
                    sampleQueueArr[i].reset();
                    i++;
                }
                loadable.setLoadPosition(0, 0);
                return true;
            }
            this.pendingDeferredRetry = true;
            return false;
        }
        this.extractedSamplesCountAtStartOfLoad = currentExtractedSampleCount;
        return true;
    }

    private boolean seekInsideBufferUs(boolean[] trackIsAudioVideoFlags, long positionUs) {
        int trackCount = this.sampleQueues.length;
        int i = 0;
        while (true) {
            boolean seekInsideQueue = true;
            if (i >= trackCount) {
                return true;
            }
            SampleQueue sampleQueue = this.sampleQueues[i];
            sampleQueue.rewind();
            if (sampleQueue.advanceTo(positionUs, true, false) == -1) {
                seekInsideQueue = false;
            }
            if (seekInsideQueue || (!trackIsAudioVideoFlags[i] && this.haveAudioVideoTracks)) {
                i++;
            }
        }
        return false;
    }

    private int getExtractedSamplesCount() {
        int extractedSamplesCount = 0;
        for (SampleQueue sampleQueue : this.sampleQueues) {
            extractedSamplesCount += sampleQueue.getWriteIndex();
        }
        return extractedSamplesCount;
    }

    private long getLargestQueuedTimestampUs() {
        long largestQueuedTimestampUs = Long.MIN_VALUE;
        for (SampleQueue sampleQueue : this.sampleQueues) {
            largestQueuedTimestampUs = Math.max(largestQueuedTimestampUs, sampleQueue.getLargestQueuedTimestampUs());
        }
        return largestQueuedTimestampUs;
    }

    private boolean isPendingReset() {
        return this.pendingResetPositionUs != C0555C.TIME_UNSET;
    }
}
