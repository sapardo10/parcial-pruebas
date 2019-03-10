package com.google.android.exoplayer2.source.chunk;

import android.support.annotation.Nullable;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.FormatHolder;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.source.MediaSourceEventListener$EventDispatcher;
import com.google.android.exoplayer2.source.SampleQueue;
import com.google.android.exoplayer2.source.SampleStream;
import com.google.android.exoplayer2.source.SequenceableLoader;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.Loader;
import com.google.android.exoplayer2.upstream.Loader.Callback;
import com.google.android.exoplayer2.upstream.Loader.LoadErrorAction;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChunkSampleStream<T extends ChunkSource> implements SampleStream, SequenceableLoader, Callback<Chunk>, com.google.android.exoplayer2.upstream.Loader.ReleaseCallback {
    private static final String TAG = "ChunkSampleStream";
    private final SequenceableLoader.Callback<ChunkSampleStream<T>> callback;
    private final T chunkSource;
    long decodeOnlyUntilPositionUs;
    private final SampleQueue[] embeddedSampleQueues;
    private final Format[] embeddedTrackFormats;
    private final int[] embeddedTrackTypes;
    private final boolean[] embeddedTracksSelected;
    private final MediaSourceEventListener$EventDispatcher eventDispatcher;
    private long lastSeekPositionUs;
    private final LoadErrorHandlingPolicy loadErrorHandlingPolicy;
    private final Loader loader;
    boolean loadingFinished;
    private final BaseMediaChunkOutput mediaChunkOutput;
    private final ArrayList<BaseMediaChunk> mediaChunks;
    private final ChunkHolder nextChunkHolder;
    private int nextNotifyPrimaryFormatMediaChunkIndex;
    private long pendingResetPositionUs;
    private Format primaryDownstreamTrackFormat;
    private final SampleQueue primarySampleQueue;
    public final int primaryTrackType;
    private final List<BaseMediaChunk> readOnlyMediaChunks;
    @Nullable
    private ReleaseCallback<T> releaseCallback;

    public interface ReleaseCallback<T extends ChunkSource> {
        void onSampleStreamReleased(ChunkSampleStream<T> chunkSampleStream);
    }

    public final class EmbeddedSampleStream implements SampleStream {
        private final int index;
        private boolean notifiedDownstreamFormat;
        public final ChunkSampleStream<T> parent;
        private final SampleQueue sampleQueue;

        public EmbeddedSampleStream(ChunkSampleStream<T> parent, SampleQueue sampleQueue, int index) {
            this.parent = parent;
            this.sampleQueue = sampleQueue;
            this.index = index;
        }

        public boolean isReady() {
            if (!ChunkSampleStream.this.loadingFinished) {
                if (ChunkSampleStream.this.isPendingReset() || !this.sampleQueue.hasNextSample()) {
                    return false;
                }
            }
            return true;
        }

        public int skipData(long positionUs) {
            if (ChunkSampleStream.this.isPendingReset()) {
                return 0;
            }
            int skipCount;
            maybeNotifyDownstreamFormat();
            if (!ChunkSampleStream.this.loadingFinished || positionUs <= this.sampleQueue.getLargestQueuedTimestampUs()) {
                skipCount = this.sampleQueue.advanceTo(positionUs, true, true);
                if (skipCount == -1) {
                    skipCount = 0;
                }
            } else {
                skipCount = this.sampleQueue.advanceToEnd();
            }
            return skipCount;
        }

        public void maybeThrowError() throws IOException {
        }

        public int readData(FormatHolder formatHolder, DecoderInputBuffer buffer, boolean formatRequired) {
            if (ChunkSampleStream.this.isPendingReset()) {
                return -3;
            }
            maybeNotifyDownstreamFormat();
            return this.sampleQueue.read(formatHolder, buffer, formatRequired, ChunkSampleStream.this.loadingFinished, ChunkSampleStream.this.decodeOnlyUntilPositionUs);
        }

        public void release() {
            Assertions.checkState(ChunkSampleStream.this.embeddedTracksSelected[this.index]);
            ChunkSampleStream.this.embeddedTracksSelected[this.index] = false;
        }

        private void maybeNotifyDownstreamFormat() {
            if (!this.notifiedDownstreamFormat) {
                ChunkSampleStream.this.eventDispatcher.downstreamFormatChanged(ChunkSampleStream.this.embeddedTrackTypes[this.index], ChunkSampleStream.this.embeddedTrackFormats[this.index], 0, null, ChunkSampleStream.this.lastSeekPositionUs);
                this.notifiedDownstreamFormat = true;
            }
        }
    }

    public com.google.android.exoplayer2.source.chunk.ChunkSampleStream.EmbeddedSampleStream selectEmbeddedTrack(long r4, int r6) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:10:0x003a in {6, 7, 9} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r3 = this;
        r0 = 0;
    L_0x0001:
        r1 = r3.embeddedSampleQueues;
        r1 = r1.length;
        if (r0 >= r1) goto L_0x0034;
    L_0x0006:
        r1 = r3.embeddedTrackTypes;
        r1 = r1[r0];
        if (r1 != r6) goto L_0x0031;
    L_0x000c:
        r1 = r3.embeddedTracksSelected;
        r1 = r1[r0];
        r2 = 1;
        r1 = r1 ^ r2;
        com.google.android.exoplayer2.util.Assertions.checkState(r1);
        r1 = r3.embeddedTracksSelected;
        r1[r0] = r2;
        r1 = r3.embeddedSampleQueues;
        r1 = r1[r0];
        r1.rewind();
        r1 = r3.embeddedSampleQueues;
        r1 = r1[r0];
        r1.advanceTo(r4, r2, r2);
        r1 = new com.google.android.exoplayer2.source.chunk.ChunkSampleStream$EmbeddedSampleStream;
        r2 = r3.embeddedSampleQueues;
        r2 = r2[r0];
        r1.<init>(r3, r2, r0);
        return r1;
    L_0x0031:
        r0 = r0 + 1;
        goto L_0x0001;
    L_0x0034:
        r0 = new java.lang.IllegalStateException;
        r0.<init>();
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.chunk.ChunkSampleStream.selectEmbeddedTrack(long, int):com.google.android.exoplayer2.source.chunk.ChunkSampleStream$EmbeddedSampleStream<>");
    }

    @Deprecated
    public ChunkSampleStream(int primaryTrackType, int[] embeddedTrackTypes, Format[] embeddedTrackFormats, T chunkSource, SequenceableLoader.Callback<ChunkSampleStream<T>> callback, Allocator allocator, long positionUs, int minLoadableRetryCount, MediaSourceEventListener$EventDispatcher eventDispatcher) {
        this(primaryTrackType, embeddedTrackTypes, embeddedTrackFormats, (ChunkSource) chunkSource, (SequenceableLoader.Callback) callback, allocator, positionUs, new DefaultLoadErrorHandlingPolicy(minLoadableRetryCount), eventDispatcher);
    }

    public ChunkSampleStream(int primaryTrackType, int[] embeddedTrackTypes, Format[] embeddedTrackFormats, T chunkSource, SequenceableLoader.Callback<ChunkSampleStream<T>> callback, Allocator allocator, long positionUs, LoadErrorHandlingPolicy loadErrorHandlingPolicy, MediaSourceEventListener$EventDispatcher eventDispatcher) {
        int i = primaryTrackType;
        int[] iArr = embeddedTrackTypes;
        Allocator allocator2 = allocator;
        long j = positionUs;
        this.primaryTrackType = i;
        this.embeddedTrackTypes = iArr;
        this.embeddedTrackFormats = embeddedTrackFormats;
        this.chunkSource = chunkSource;
        this.callback = callback;
        this.eventDispatcher = eventDispatcher;
        this.loadErrorHandlingPolicy = loadErrorHandlingPolicy;
        this.loader = new Loader("Loader:ChunkSampleStream");
        this.nextChunkHolder = new ChunkHolder();
        this.mediaChunks = new ArrayList();
        this.readOnlyMediaChunks = Collections.unmodifiableList(this.mediaChunks);
        int embeddedTrackCount = iArr == null ? 0 : iArr.length;
        r0.embeddedSampleQueues = new SampleQueue[embeddedTrackCount];
        r0.embeddedTracksSelected = new boolean[embeddedTrackCount];
        int[] trackTypes = new int[(embeddedTrackCount + 1)];
        SampleQueue[] sampleQueues = new SampleQueue[(embeddedTrackCount + 1)];
        r0.primarySampleQueue = new SampleQueue(allocator2);
        trackTypes[0] = i;
        sampleQueues[0] = r0.primarySampleQueue;
        int i2 = 0;
        while (i2 < embeddedTrackCount) {
            SampleQueue sampleQueue = new SampleQueue(allocator2);
            r0.embeddedSampleQueues[i2] = sampleQueue;
            sampleQueues[i2 + 1] = sampleQueue;
            trackTypes[i2 + 1] = iArr[i2];
            i2++;
            i = primaryTrackType;
        }
        r0.mediaChunkOutput = new BaseMediaChunkOutput(trackTypes, sampleQueues);
        r0.pendingResetPositionUs = j;
        r0.lastSeekPositionUs = j;
    }

    public void discardBuffer(long positionUs, boolean toKeyframe) {
        if (!isPendingReset()) {
            int oldFirstSampleIndex = this.primarySampleQueue.getFirstIndex();
            this.primarySampleQueue.discardTo(positionUs, toKeyframe, true);
            int newFirstSampleIndex = this.primarySampleQueue.getFirstIndex();
            if (newFirstSampleIndex > oldFirstSampleIndex) {
                long discardToUs = this.primarySampleQueue.getFirstTimestampUs();
                int i = 0;
                while (true) {
                    SampleQueue[] sampleQueueArr = this.embeddedSampleQueues;
                    if (i >= sampleQueueArr.length) {
                        break;
                    }
                    sampleQueueArr[i].discardTo(discardToUs, toKeyframe, this.embeddedTracksSelected[i]);
                    i++;
                }
            }
            discardDownstreamMediaChunks(newFirstSampleIndex);
        }
    }

    public T getChunkSource() {
        return this.chunkSource;
    }

    public long getBufferedPositionUs() {
        if (this.loadingFinished) {
            return Long.MIN_VALUE;
        }
        if (isPendingReset()) {
            return this.pendingResetPositionUs;
        }
        BaseMediaChunk lastCompletedMediaChunk;
        long bufferedPositionUs = this.lastSeekPositionUs;
        BaseMediaChunk lastMediaChunk = getLastMediaChunk();
        if (lastMediaChunk.isLoadCompleted()) {
            lastCompletedMediaChunk = lastMediaChunk;
        } else if (this.mediaChunks.size() > 1) {
            ArrayList arrayList = this.mediaChunks;
            lastCompletedMediaChunk = (BaseMediaChunk) arrayList.get(arrayList.size() - 2);
        } else {
            lastCompletedMediaChunk = null;
        }
        if (lastCompletedMediaChunk != null) {
            bufferedPositionUs = Math.max(bufferedPositionUs, lastCompletedMediaChunk.endTimeUs);
        }
        return Math.max(bufferedPositionUs, this.primarySampleQueue.getLargestQueuedTimestampUs());
    }

    public long getAdjustedSeekPositionUs(long positionUs, SeekParameters seekParameters) {
        return this.chunkSource.getAdjustedSeekPositionUs(positionUs, seekParameters);
    }

    public void seekToUs(long positionUs) {
        this.lastSeekPositionUs = positionUs;
        if (isPendingReset()) {
            this.pendingResetPositionUs = positionUs;
            return;
        }
        boolean seekInsideBuffer;
        BaseMediaChunk seekToMediaChunk = null;
        int i = 0;
        while (i < this.mediaChunks.size()) {
            BaseMediaChunk mediaChunk = (BaseMediaChunk) this.mediaChunks.get(i);
            long mediaChunkStartTimeUs = mediaChunk.startTimeUs;
            if (mediaChunkStartTimeUs == positionUs && mediaChunk.clippedStartTimeUs == C0555C.TIME_UNSET) {
                seekToMediaChunk = mediaChunk;
                break;
            } else if (mediaChunkStartTimeUs > positionUs) {
                break;
            } else {
                i++;
            }
        }
        this.primarySampleQueue.rewind();
        int i2 = 0;
        if (seekToMediaChunk != null) {
            seekInsideBuffer = this.primarySampleQueue.setReadPosition(seekToMediaChunk.getFirstSampleIndex(0));
            this.decodeOnlyUntilPositionUs = 0;
        } else {
            seekInsideBuffer = this.primarySampleQueue.advanceTo(positionUs, true, (positionUs > getNextLoadPositionUs() ? 1 : (positionUs == getNextLoadPositionUs() ? 0 : -1)) < 0) != -1;
            this.decodeOnlyUntilPositionUs = this.lastSeekPositionUs;
        }
        if (seekInsideBuffer) {
            this.nextNotifyPrimaryFormatMediaChunkIndex = primarySampleIndexToMediaChunkIndex(this.primarySampleQueue.getReadIndex(), 0);
            for (SampleQueue embeddedSampleQueue : this.embeddedSampleQueues) {
                embeddedSampleQueue.rewind();
                embeddedSampleQueue.advanceTo(positionUs, true, false);
            }
        } else {
            this.pendingResetPositionUs = positionUs;
            this.loadingFinished = false;
            this.mediaChunks.clear();
            this.nextNotifyPrimaryFormatMediaChunkIndex = 0;
            if (this.loader.isLoading()) {
                this.loader.cancelLoading();
            } else {
                this.primarySampleQueue.reset();
                SampleQueue[] sampleQueueArr = this.embeddedSampleQueues;
                int length = sampleQueueArr.length;
                while (i2 < length) {
                    sampleQueueArr[i2].reset();
                    i2++;
                }
            }
        }
    }

    public void release() {
        release(null);
    }

    public void release(@Nullable ReleaseCallback<T> callback) {
        this.releaseCallback = callback;
        this.primarySampleQueue.discardToEnd();
        for (SampleQueue embeddedSampleQueue : this.embeddedSampleQueues) {
            embeddedSampleQueue.discardToEnd();
        }
        this.loader.release(this);
    }

    public void onLoaderReleased() {
        this.primarySampleQueue.reset();
        for (SampleQueue embeddedSampleQueue : this.embeddedSampleQueues) {
            embeddedSampleQueue.reset();
        }
        ReleaseCallback releaseCallback = this.releaseCallback;
        if (releaseCallback != null) {
            releaseCallback.onSampleStreamReleased(this);
        }
    }

    public boolean isReady() {
        if (!this.loadingFinished) {
            if (isPendingReset() || !this.primarySampleQueue.hasNextSample()) {
                return false;
            }
        }
        return true;
    }

    public void maybeThrowError() throws IOException {
        this.loader.maybeThrowError();
        if (!this.loader.isLoading()) {
            this.chunkSource.maybeThrowError();
        }
    }

    public int readData(FormatHolder formatHolder, DecoderInputBuffer buffer, boolean formatRequired) {
        if (isPendingReset()) {
            return -3;
        }
        maybeNotifyPrimaryTrackFormatChanged();
        return this.primarySampleQueue.read(formatHolder, buffer, formatRequired, this.loadingFinished, this.decodeOnlyUntilPositionUs);
    }

    public int skipData(long positionUs) {
        if (isPendingReset()) {
            return 0;
        }
        int skipCount;
        if (!this.loadingFinished || positionUs <= this.primarySampleQueue.getLargestQueuedTimestampUs()) {
            skipCount = this.primarySampleQueue.advanceTo(positionUs, true, true);
            if (skipCount == -1) {
                skipCount = 0;
            }
        } else {
            skipCount = this.primarySampleQueue.advanceToEnd();
        }
        maybeNotifyPrimaryTrackFormatChanged();
        return skipCount;
    }

    public void onLoadCompleted(Chunk loadable, long elapsedRealtimeMs, long loadDurationMs) {
        Chunk chunk = loadable;
        long j = elapsedRealtimeMs;
        long j2 = loadDurationMs;
        this.chunkSource.onChunkLoadCompleted(chunk);
        this.eventDispatcher.loadCompleted(chunk.dataSpec, loadable.getUri(), loadable.getResponseHeaders(), chunk.type, this.primaryTrackType, chunk.trackFormat, chunk.trackSelectionReason, chunk.trackSelectionData, chunk.startTimeUs, chunk.endTimeUs, j, j2, loadable.bytesLoaded());
        this.callback.onContinueLoadingRequested(this);
    }

    public void onLoadCanceled(Chunk loadable, long elapsedRealtimeMs, long loadDurationMs, boolean released) {
        Chunk chunk = loadable;
        this.eventDispatcher.loadCanceled(chunk.dataSpec, loadable.getUri(), loadable.getResponseHeaders(), chunk.type, this.primaryTrackType, chunk.trackFormat, chunk.trackSelectionReason, chunk.trackSelectionData, chunk.startTimeUs, chunk.endTimeUs, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded());
        if (!released) {
            r0.primarySampleQueue.reset();
            for (SampleQueue embeddedSampleQueue : r0.embeddedSampleQueues) {
                embeddedSampleQueue.reset();
            }
            r0.callback.onContinueLoadingRequested(r0);
        }
    }

    public LoadErrorAction onLoadError(Chunk loadable, long elapsedRealtimeMs, long loadDurationMs, IOException error, int errorCount) {
        boolean z;
        boolean cancelable;
        LoadErrorAction loadErrorAction;
        long retryDelayMs;
        LoadErrorAction createRetryAction;
        LoadErrorAction loadErrorAction2;
        boolean canceled;
        Chunk chunk = loadable;
        long bytesLoaded = loadable.bytesLoaded();
        boolean isMediaChunk = isMediaChunk(loadable);
        int lastChunkIndex = this.mediaChunks.size() - 1;
        if (bytesLoaded != 0 && isMediaChunk) {
            if (haveReadFromMediaChunk(lastChunkIndex)) {
                z = false;
                cancelable = z;
                loadErrorAction = null;
                if (r0.chunkSource.onChunkLoadError(loadable, cancelable, error, cancelable ? r0.loadErrorHandlingPolicy.getBlacklistDurationMsFor(chunk.type, loadDurationMs, error, errorCount) : C0555C.TIME_UNSET)) {
                    if (cancelable) {
                        Log.m10w(TAG, "Ignoring attempt to cancel non-cancelable load.");
                    } else {
                        loadErrorAction = Loader.DONT_RETRY;
                        if (isMediaChunk) {
                            Assertions.checkState(discardUpstreamMediaChunksFromIndex(lastChunkIndex) != chunk);
                            if (r0.mediaChunks.isEmpty()) {
                                r0.pendingResetPositionUs = r0.lastSeekPositionUs;
                            }
                        }
                    }
                }
                if (loadErrorAction != null) {
                    retryDelayMs = r0.loadErrorHandlingPolicy.getRetryDelayMsFor(chunk.type, loadDurationMs, error, errorCount);
                    if (retryDelayMs == C0555C.TIME_UNSET) {
                        createRetryAction = Loader.createRetryAction(false, retryDelayMs);
                    } else {
                        createRetryAction = Loader.DONT_RETRY_FATAL;
                    }
                    loadErrorAction2 = createRetryAction;
                } else {
                    loadErrorAction2 = loadErrorAction;
                }
                canceled = loadErrorAction2.isRetry() ^ true;
                r0.eventDispatcher.loadError(chunk.dataSpec, loadable.getUri(), loadable.getResponseHeaders(), chunk.type, r0.primaryTrackType, chunk.trackFormat, chunk.trackSelectionReason, chunk.trackSelectionData, chunk.startTimeUs, chunk.endTimeUs, elapsedRealtimeMs, loadDurationMs, bytesLoaded, error, canceled);
                if (canceled) {
                    r0.callback.onContinueLoadingRequested(r0);
                }
                return loadErrorAction2;
            }
        }
        z = true;
        cancelable = z;
        if (cancelable) {
        }
        loadErrorAction = null;
        if (r0.chunkSource.onChunkLoadError(loadable, cancelable, error, cancelable ? r0.loadErrorHandlingPolicy.getBlacklistDurationMsFor(chunk.type, loadDurationMs, error, errorCount) : C0555C.TIME_UNSET)) {
            if (cancelable) {
                Log.m10w(TAG, "Ignoring attempt to cancel non-cancelable load.");
            } else {
                loadErrorAction = Loader.DONT_RETRY;
                if (isMediaChunk) {
                    if (discardUpstreamMediaChunksFromIndex(lastChunkIndex) != chunk) {
                    }
                    Assertions.checkState(discardUpstreamMediaChunksFromIndex(lastChunkIndex) != chunk);
                    if (r0.mediaChunks.isEmpty()) {
                        r0.pendingResetPositionUs = r0.lastSeekPositionUs;
                    }
                }
            }
        }
        if (loadErrorAction != null) {
            loadErrorAction2 = loadErrorAction;
        } else {
            retryDelayMs = r0.loadErrorHandlingPolicy.getRetryDelayMsFor(chunk.type, loadDurationMs, error, errorCount);
            if (retryDelayMs == C0555C.TIME_UNSET) {
                createRetryAction = Loader.DONT_RETRY_FATAL;
            } else {
                createRetryAction = Loader.createRetryAction(false, retryDelayMs);
            }
            loadErrorAction2 = createRetryAction;
        }
        canceled = loadErrorAction2.isRetry() ^ true;
        r0.eventDispatcher.loadError(chunk.dataSpec, loadable.getUri(), loadable.getResponseHeaders(), chunk.type, r0.primaryTrackType, chunk.trackFormat, chunk.trackSelectionReason, chunk.trackSelectionData, chunk.startTimeUs, chunk.endTimeUs, elapsedRealtimeMs, loadDurationMs, bytesLoaded, error, canceled);
        if (canceled) {
            r0.callback.onContinueLoadingRequested(r0);
        }
        return loadErrorAction2;
    }

    public boolean continueLoading(long positionUs) {
        boolean resetToMediaChunk = false;
        if (!this.loadingFinished) {
            if (!r0.loader.isLoading()) {
                List<BaseMediaChunk> chunkQueue;
                long loadPositionUs;
                boolean pendingReset = isPendingReset();
                if (pendingReset) {
                    chunkQueue = Collections.emptyList();
                    loadPositionUs = r0.pendingResetPositionUs;
                } else {
                    chunkQueue = r0.readOnlyMediaChunks;
                    loadPositionUs = getLastMediaChunk().endTimeUs;
                }
                r0.chunkSource.getNextChunk(positionUs, loadPositionUs, chunkQueue, r0.nextChunkHolder);
                boolean endOfStream = r0.nextChunkHolder.endOfStream;
                Chunk loadable = r0.nextChunkHolder.chunk;
                r0.nextChunkHolder.clear();
                if (endOfStream) {
                    r0.pendingResetPositionUs = C0555C.TIME_UNSET;
                    r0.loadingFinished = true;
                    return true;
                } else if (loadable == null) {
                    return false;
                } else {
                    if (isMediaChunk(loadable)) {
                        BaseMediaChunk mediaChunk = (BaseMediaChunk) loadable;
                        if (pendingReset) {
                            if (mediaChunk.startTimeUs == r0.pendingResetPositionUs) {
                                resetToMediaChunk = true;
                            }
                            r0.decodeOnlyUntilPositionUs = resetToMediaChunk ? 0 : r0.pendingResetPositionUs;
                            r0.pendingResetPositionUs = C0555C.TIME_UNSET;
                        }
                        mediaChunk.init(r0.mediaChunkOutput);
                        r0.mediaChunks.add(mediaChunk);
                    }
                    r0.eventDispatcher.loadStarted(loadable.dataSpec, loadable.type, r0.primaryTrackType, loadable.trackFormat, loadable.trackSelectionReason, loadable.trackSelectionData, loadable.startTimeUs, loadable.endTimeUs, r0.loader.startLoading(loadable, r0, r0.loadErrorHandlingPolicy.getMinimumLoadableRetryCount(loadable.type)));
                    return true;
                }
            }
        }
        return false;
    }

    public long getNextLoadPositionUs() {
        if (isPendingReset()) {
            return this.pendingResetPositionUs;
        }
        return this.loadingFinished ? Long.MIN_VALUE : getLastMediaChunk().endTimeUs;
    }

    public void reevaluateBuffer(long positionUs) {
        if (!this.loader.isLoading()) {
            if (!isPendingReset()) {
                int currentQueueSize = this.mediaChunks.size();
                int preferredQueueSize = this.chunkSource.getPreferredQueueSize(positionUs, this.readOnlyMediaChunks);
                if (currentQueueSize > preferredQueueSize) {
                    int newQueueSize = currentQueueSize;
                    for (int i = preferredQueueSize; i < currentQueueSize; i++) {
                        if (!haveReadFromMediaChunk(i)) {
                            newQueueSize = i;
                            break;
                        }
                    }
                    if (newQueueSize != currentQueueSize) {
                        long endTimeUs = getLastMediaChunk().endTimeUs;
                        BaseMediaChunk firstRemovedChunk = discardUpstreamMediaChunksFromIndex(newQueueSize);
                        if (this.mediaChunks.isEmpty()) {
                            this.pendingResetPositionUs = this.lastSeekPositionUs;
                        }
                        this.loadingFinished = false;
                        this.eventDispatcher.upstreamDiscarded(this.primaryTrackType, firstRemovedChunk.startTimeUs, endTimeUs);
                    }
                }
            }
        }
    }

    private boolean isMediaChunk(Chunk chunk) {
        return chunk instanceof BaseMediaChunk;
    }

    private boolean haveReadFromMediaChunk(int mediaChunkIndex) {
        BaseMediaChunk mediaChunk = (BaseMediaChunk) this.mediaChunks.get(mediaChunkIndex);
        if (this.primarySampleQueue.getReadIndex() > mediaChunk.getFirstSampleIndex(0)) {
            return true;
        }
        int i = 0;
        while (true) {
            SampleQueue[] sampleQueueArr = this.embeddedSampleQueues;
            if (i >= sampleQueueArr.length) {
                return false;
            }
            if (sampleQueueArr[i].getReadIndex() > mediaChunk.getFirstSampleIndex(i + 1)) {
                return true;
            }
            i++;
        }
    }

    boolean isPendingReset() {
        return this.pendingResetPositionUs != C0555C.TIME_UNSET;
    }

    private void discardDownstreamMediaChunks(int discardToSampleIndex) {
        int discardToMediaChunkIndex = Math.min(primarySampleIndexToMediaChunkIndex(discardToSampleIndex, 0), this.nextNotifyPrimaryFormatMediaChunkIndex);
        if (discardToMediaChunkIndex > 0) {
            Util.removeRange(this.mediaChunks, 0, discardToMediaChunkIndex);
            this.nextNotifyPrimaryFormatMediaChunkIndex -= discardToMediaChunkIndex;
        }
    }

    private void maybeNotifyPrimaryTrackFormatChanged() {
        int notifyToMediaChunkIndex = primarySampleIndexToMediaChunkIndex(this.primarySampleQueue.getReadIndex(), this.nextNotifyPrimaryFormatMediaChunkIndex - 1);
        while (true) {
            int i = this.nextNotifyPrimaryFormatMediaChunkIndex;
            if (i <= notifyToMediaChunkIndex) {
                this.nextNotifyPrimaryFormatMediaChunkIndex = i + 1;
                maybeNotifyPrimaryTrackFormatChanged(i);
            } else {
                return;
            }
        }
    }

    private void maybeNotifyPrimaryTrackFormatChanged(int mediaChunkReadIndex) {
        BaseMediaChunk currentChunk = (BaseMediaChunk) this.mediaChunks.get(mediaChunkReadIndex);
        Format trackFormat = currentChunk.trackFormat;
        if (!trackFormat.equals(this.primaryDownstreamTrackFormat)) {
            this.eventDispatcher.downstreamFormatChanged(this.primaryTrackType, trackFormat, currentChunk.trackSelectionReason, currentChunk.trackSelectionData, currentChunk.startTimeUs);
        }
        this.primaryDownstreamTrackFormat = trackFormat;
    }

    private int primarySampleIndexToMediaChunkIndex(int primarySampleIndex, int minChunkIndex) {
        for (int i = minChunkIndex + 1; i < this.mediaChunks.size(); i++) {
            if (((BaseMediaChunk) this.mediaChunks.get(i)).getFirstSampleIndex(0) > primarySampleIndex) {
                return i - 1;
            }
        }
        return this.mediaChunks.size() - 1;
    }

    private BaseMediaChunk getLastMediaChunk() {
        ArrayList arrayList = this.mediaChunks;
        return (BaseMediaChunk) arrayList.get(arrayList.size() - 1);
    }

    private BaseMediaChunk discardUpstreamMediaChunksFromIndex(int chunkIndex) {
        BaseMediaChunk firstRemovedChunk = (BaseMediaChunk) this.mediaChunks.get(chunkIndex);
        List list = this.mediaChunks;
        Util.removeRange(list, chunkIndex, list.size());
        this.nextNotifyPrimaryFormatMediaChunkIndex = Math.max(this.nextNotifyPrimaryFormatMediaChunkIndex, this.mediaChunks.size());
        this.primarySampleQueue.discardUpstreamSamples(firstRemovedChunk.getFirstSampleIndex(0));
        int i = 0;
        while (true) {
            SampleQueue[] sampleQueueArr = this.embeddedSampleQueues;
            if (i >= sampleQueueArr.length) {
                return firstRemovedChunk;
            }
            sampleQueueArr[i].discardUpstreamSamples(firstRemovedChunk.getFirstSampleIndex(i + 1));
            i++;
        }
    }
}
