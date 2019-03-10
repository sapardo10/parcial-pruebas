package com.google.android.exoplayer2.source;

import android.support.annotation.Nullable;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.FormatHolder;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSource.Factory;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.Loader;
import com.google.android.exoplayer2.upstream.Loader.Callback;
import com.google.android.exoplayer2.upstream.Loader.LoadErrorAction;
import com.google.android.exoplayer2.upstream.Loader.Loadable;
import com.google.android.exoplayer2.upstream.StatsDataSource;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.MimeTypes;
import java.io.IOException;
import java.util.ArrayList;

final class SingleSampleMediaPeriod implements MediaPeriod, Callback<SourceLoadable> {
    private static final int INITIAL_SAMPLE_SIZE = 1024;
    private final Factory dataSourceFactory;
    private final DataSpec dataSpec;
    private final long durationUs;
    private final MediaSourceEventListener$EventDispatcher eventDispatcher;
    final Format format;
    private final LoadErrorHandlingPolicy loadErrorHandlingPolicy;
    final Loader loader = new Loader("Loader:SingleSampleMediaPeriod");
    boolean loadingFinished;
    boolean loadingSucceeded;
    boolean notifiedReadingStarted;
    byte[] sampleData;
    int sampleSize;
    private final ArrayList<SampleStreamImpl> sampleStreams = new ArrayList();
    private final TrackGroupArray tracks;
    @Nullable
    private final TransferListener transferListener;
    final boolean treatLoadErrorsAsEndOfStream;

    private final class SampleStreamImpl implements SampleStream {
        private static final int STREAM_STATE_END_OF_STREAM = 2;
        private static final int STREAM_STATE_SEND_FORMAT = 0;
        private static final int STREAM_STATE_SEND_SAMPLE = 1;
        private boolean notifiedDownstreamFormat;
        private int streamState;

        private SampleStreamImpl() {
        }

        public void reset() {
            if (this.streamState == 2) {
                this.streamState = 1;
            }
        }

        public boolean isReady() {
            return SingleSampleMediaPeriod.this.loadingFinished;
        }

        public void maybeThrowError() throws IOException {
            if (!SingleSampleMediaPeriod.this.treatLoadErrorsAsEndOfStream) {
                SingleSampleMediaPeriod.this.loader.maybeThrowError();
            }
        }

        public int readData(FormatHolder formatHolder, DecoderInputBuffer buffer, boolean requireFormat) {
            maybeNotifyDownstreamFormat();
            int i = this.streamState;
            if (i == 2) {
                buffer.addFlag(4);
                return -4;
            }
            if (!requireFormat) {
                if (i != 0) {
                    if (!SingleSampleMediaPeriod.this.loadingFinished) {
                        return -3;
                    }
                    if (SingleSampleMediaPeriod.this.loadingSucceeded) {
                        buffer.timeUs = 0;
                        buffer.addFlag(1);
                        buffer.ensureSpaceForWrite(SingleSampleMediaPeriod.this.sampleSize);
                        buffer.data.put(SingleSampleMediaPeriod.this.sampleData, 0, SingleSampleMediaPeriod.this.sampleSize);
                    } else {
                        buffer.addFlag(4);
                    }
                    this.streamState = 2;
                    return -4;
                }
            }
            formatHolder.format = SingleSampleMediaPeriod.this.format;
            this.streamState = 1;
            return -5;
        }

        public int skipData(long positionUs) {
            maybeNotifyDownstreamFormat();
            if (positionUs <= 0 || this.streamState == 2) {
                return 0;
            }
            this.streamState = 2;
            return 1;
        }

        private void maybeNotifyDownstreamFormat() {
            if (!this.notifiedDownstreamFormat) {
                SingleSampleMediaPeriod.this.eventDispatcher.downstreamFormatChanged(MimeTypes.getTrackType(SingleSampleMediaPeriod.this.format.sampleMimeType), SingleSampleMediaPeriod.this.format, 0, null, 0);
                this.notifiedDownstreamFormat = true;
            }
        }
    }

    static final class SourceLoadable implements Loadable {
        private final StatsDataSource dataSource;
        public final DataSpec dataSpec;
        private byte[] sampleData;

        public void load() throws java.io.IOException, java.lang.InterruptedException {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:19:0x0053 in {7, 10, 11, 13, 15, 18} preds:[]
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
            r5 = this;
            r0 = r5.dataSource;
            r0.resetBytesRead();
            r0 = r5.dataSource;	 Catch:{ all -> 0x004c }
            r1 = r5.dataSpec;	 Catch:{ all -> 0x004c }
            r0.open(r1);	 Catch:{ all -> 0x004c }
            r0 = 0;	 Catch:{ all -> 0x004c }
        L_0x000d:
            r1 = -1;	 Catch:{ all -> 0x004c }
            if (r0 == r1) goto L_0x0044;	 Catch:{ all -> 0x004c }
        L_0x0010:
            r1 = r5.dataSource;	 Catch:{ all -> 0x004c }
            r1 = r1.getBytesRead();	 Catch:{ all -> 0x004c }
            r1 = (int) r1;	 Catch:{ all -> 0x004c }
            r2 = r5.sampleData;	 Catch:{ all -> 0x004c }
            if (r2 != 0) goto L_0x0022;	 Catch:{ all -> 0x004c }
        L_0x001b:
            r2 = 1024; // 0x400 float:1.435E-42 double:5.06E-321;	 Catch:{ all -> 0x004c }
            r2 = new byte[r2];	 Catch:{ all -> 0x004c }
            r5.sampleData = r2;	 Catch:{ all -> 0x004c }
            goto L_0x0036;	 Catch:{ all -> 0x004c }
        L_0x0022:
            r2 = r5.sampleData;	 Catch:{ all -> 0x004c }
            r2 = r2.length;	 Catch:{ all -> 0x004c }
            if (r1 != r2) goto L_0x0035;	 Catch:{ all -> 0x004c }
        L_0x0027:
            r2 = r5.sampleData;	 Catch:{ all -> 0x004c }
            r3 = r5.sampleData;	 Catch:{ all -> 0x004c }
            r3 = r3.length;	 Catch:{ all -> 0x004c }
            r3 = r3 * 2;	 Catch:{ all -> 0x004c }
            r2 = java.util.Arrays.copyOf(r2, r3);	 Catch:{ all -> 0x004c }
            r5.sampleData = r2;	 Catch:{ all -> 0x004c }
            goto L_0x0036;	 Catch:{ all -> 0x004c }
        L_0x0036:
            r2 = r5.dataSource;	 Catch:{ all -> 0x004c }
            r3 = r5.sampleData;	 Catch:{ all -> 0x004c }
            r4 = r5.sampleData;	 Catch:{ all -> 0x004c }
            r4 = r4.length;	 Catch:{ all -> 0x004c }
            r4 = r4 - r1;	 Catch:{ all -> 0x004c }
            r2 = r2.read(r3, r1, r4);	 Catch:{ all -> 0x004c }
            r0 = r2;
            goto L_0x000d;
            r0 = r5.dataSource;
            com.google.android.exoplayer2.util.Util.closeQuietly(r0);
            return;
        L_0x004c:
            r0 = move-exception;
            r1 = r5.dataSource;
            com.google.android.exoplayer2.util.Util.closeQuietly(r1);
            throw r0;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.SingleSampleMediaPeriod.SourceLoadable.load():void");
        }

        public SourceLoadable(DataSpec dataSpec, DataSource dataSource) {
            this.dataSpec = dataSpec;
            this.dataSource = new StatsDataSource(dataSource);
        }

        public void cancelLoad() {
        }
    }

    public SingleSampleMediaPeriod(DataSpec dataSpec, Factory dataSourceFactory, @Nullable TransferListener transferListener, Format format, long durationUs, LoadErrorHandlingPolicy loadErrorHandlingPolicy, MediaSourceEventListener$EventDispatcher eventDispatcher, boolean treatLoadErrorsAsEndOfStream) {
        this.dataSpec = dataSpec;
        this.dataSourceFactory = dataSourceFactory;
        this.transferListener = transferListener;
        this.format = format;
        this.durationUs = durationUs;
        this.loadErrorHandlingPolicy = loadErrorHandlingPolicy;
        this.eventDispatcher = eventDispatcher;
        this.treatLoadErrorsAsEndOfStream = treatLoadErrorsAsEndOfStream;
        TrackGroup[] trackGroupArr = new TrackGroup[1];
        trackGroupArr[0] = new TrackGroup(format);
        this.tracks = new TrackGroupArray(trackGroupArr);
        eventDispatcher.mediaPeriodCreated();
    }

    public void release() {
        this.loader.release();
        this.eventDispatcher.mediaPeriodReleased();
    }

    public void prepare(MediaPeriod.Callback callback, long positionUs) {
        callback.onPrepared(this);
    }

    public void maybeThrowPrepareError() throws IOException {
    }

    public TrackGroupArray getTrackGroups() {
        return this.tracks;
    }

    public long selectTracks(TrackSelection[] selections, boolean[] mayRetainStreamFlags, SampleStream[] streams, boolean[] streamResetFlags, long positionUs) {
        int i = 0;
        while (i < selections.length) {
            if (streams[i] != null && (selections[i] == null || !mayRetainStreamFlags[i])) {
                this.sampleStreams.remove(streams[i]);
                streams[i] = null;
            }
            if (streams[i] == null && selections[i] != null) {
                SampleStreamImpl stream = new SampleStreamImpl();
                this.sampleStreams.add(stream);
                streams[i] = stream;
                streamResetFlags[i] = true;
            }
            i++;
        }
        return positionUs;
    }

    public void discardBuffer(long positionUs, boolean toKeyframe) {
    }

    public void reevaluateBuffer(long positionUs) {
    }

    public boolean continueLoading(long positionUs) {
        if (!this.loadingFinished) {
            if (!r0.loader.isLoading()) {
                DataSource dataSource = r0.dataSourceFactory.createDataSource();
                TransferListener transferListener = r0.transferListener;
                if (transferListener != null) {
                    dataSource.addTransferListener(transferListener);
                }
                r0.eventDispatcher.loadStarted(r0.dataSpec, 1, -1, r0.format, 0, null, 0, r0.durationUs, r0.loader.startLoading(new SourceLoadable(r0.dataSpec, dataSource), r0, r0.loadErrorHandlingPolicy.getMinimumLoadableRetryCount(1)));
                return true;
            }
        }
        return false;
    }

    public long readDiscontinuity() {
        if (!this.notifiedReadingStarted) {
            this.eventDispatcher.readingStarted();
            this.notifiedReadingStarted = true;
        }
        return C0555C.TIME_UNSET;
    }

    public long getNextLoadPositionUs() {
        if (!this.loadingFinished) {
            if (!this.loader.isLoading()) {
                return 0;
            }
        }
        return Long.MIN_VALUE;
    }

    public long getBufferedPositionUs() {
        return this.loadingFinished ? Long.MIN_VALUE : 0;
    }

    public long seekToUs(long positionUs) {
        for (int i = 0; i < this.sampleStreams.size(); i++) {
            ((SampleStreamImpl) this.sampleStreams.get(i)).reset();
        }
        return positionUs;
    }

    public long getAdjustedSeekPositionUs(long positionUs, SeekParameters seekParameters) {
        return positionUs;
    }

    public void onLoadCompleted(SourceLoadable loadable, long elapsedRealtimeMs, long loadDurationMs) {
        long j = elapsedRealtimeMs;
        long j2 = loadDurationMs;
        this.sampleSize = (int) loadable.dataSource.getBytesRead();
        this.sampleData = loadable.sampleData;
        this.loadingFinished = true;
        this.loadingSucceeded = true;
        this.eventDispatcher.loadCompleted(loadable.dataSpec, loadable.dataSource.getLastOpenedUri(), loadable.dataSource.getLastResponseHeaders(), 1, -1, this.format, 0, null, 0, this.durationUs, j, j2, (long) this.sampleSize);
    }

    public void onLoadCanceled(SourceLoadable loadable, long elapsedRealtimeMs, long loadDurationMs, boolean released) {
        long j = 0;
        this.eventDispatcher.loadCanceled(loadable.dataSpec, loadable.dataSource.getLastOpenedUri(), loadable.dataSource.getLastResponseHeaders(), 1, -1, null, 0, null, j, this.durationUs, elapsedRealtimeMs, loadDurationMs, loadable.dataSource.getBytesRead());
    }

    public LoadErrorAction onLoadError(SourceLoadable loadable, long elapsedRealtimeMs, long loadDurationMs, IOException error, int errorCount) {
        boolean errorCanBePropagated;
        LoadErrorAction action;
        long retryDelay = this.loadErrorHandlingPolicy.getRetryDelayMsFor(1, this.durationUs, error, errorCount);
        if (retryDelay == C0555C.TIME_UNSET) {
            int i = errorCount;
        } else if (errorCount < r0.loadErrorHandlingPolicy.getMinimumLoadableRetryCount(1)) {
            errorCanBePropagated = false;
            if (r0.treatLoadErrorsAsEndOfStream || !errorCanBePropagated) {
                action = retryDelay == C0555C.TIME_UNSET ? Loader.createRetryAction(false, retryDelay) : Loader.DONT_RETRY_FATAL;
            } else {
                r0.loadingFinished = true;
                action = Loader.DONT_RETRY;
            }
            r0.eventDispatcher.loadError(loadable.dataSpec, loadable.dataSource.getLastOpenedUri(), loadable.dataSource.getLastResponseHeaders(), 1, -1, r0.format, 0, null, 0, r0.durationUs, elapsedRealtimeMs, loadDurationMs, loadable.dataSource.getBytesRead(), error, action.isRetry() ^ 1);
            return action;
        }
        errorCanBePropagated = true;
        if (r0.treatLoadErrorsAsEndOfStream) {
        }
        if (retryDelay == C0555C.TIME_UNSET) {
        }
        r0.eventDispatcher.loadError(loadable.dataSpec, loadable.dataSource.getLastOpenedUri(), loadable.dataSource.getLastResponseHeaders(), 1, -1, r0.format, 0, null, 0, r0.durationUs, elapsedRealtimeMs, loadDurationMs, loadable.dataSource.getBytesRead(), error, action.isRetry() ^ 1);
        return action;
    }
}
