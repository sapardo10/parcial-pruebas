package com.google.android.exoplayer2;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import com.google.android.exoplayer2.DefaultMediaClock.PlaybackParameterListener;
import com.google.android.exoplayer2.PlayerMessage.Sender;
import com.google.android.exoplayer2.Timeline.Period;
import com.google.android.exoplayer2.Timeline.Window;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSource.MediaPeriodId;
import com.google.android.exoplayer2.source.MediaSource.SourceInfoRefreshListener;
import com.google.android.exoplayer2.source.SampleStream;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector.InvalidationListener;
import com.google.android.exoplayer2.trackselection.TrackSelectorResult;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Clock;
import com.google.android.exoplayer2.util.HandlerWrapper;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

final class ExoPlayerImplInternal implements Callback, MediaPeriod.Callback, InvalidationListener, SourceInfoRefreshListener, PlaybackParameterListener, Sender {
    private static final int IDLE_INTERVAL_MS = 1000;
    private static final int MSG_DO_SOME_WORK = 2;
    public static final int MSG_ERROR = 2;
    private static final int MSG_PERIOD_PREPARED = 9;
    public static final int MSG_PLAYBACK_INFO_CHANGED = 0;
    public static final int MSG_PLAYBACK_PARAMETERS_CHANGED = 1;
    private static final int MSG_PLAYBACK_PARAMETERS_CHANGED_INTERNAL = 16;
    private static final int MSG_PREPARE = 0;
    private static final int MSG_REFRESH_SOURCE_INFO = 8;
    private static final int MSG_RELEASE = 7;
    private static final int MSG_SEEK_TO = 3;
    private static final int MSG_SEND_MESSAGE = 14;
    private static final int MSG_SEND_MESSAGE_TO_TARGET_THREAD = 15;
    private static final int MSG_SET_PLAYBACK_PARAMETERS = 4;
    private static final int MSG_SET_PLAY_WHEN_READY = 1;
    private static final int MSG_SET_REPEAT_MODE = 12;
    private static final int MSG_SET_SEEK_PARAMETERS = 5;
    private static final int MSG_SET_SHUFFLE_ENABLED = 13;
    private static final int MSG_SOURCE_CONTINUE_LOADING_REQUESTED = 10;
    private static final int MSG_STOP = 6;
    private static final int MSG_TRACK_SELECTION_INVALIDATED = 11;
    private static final int PREPARING_SOURCE_INTERVAL_MS = 10;
    private static final int RENDERING_INTERVAL_MS = 10;
    private static final String TAG = "ExoPlayerImplInternal";
    private final long backBufferDurationUs;
    private final BandwidthMeter bandwidthMeter;
    private final Clock clock;
    private final TrackSelectorResult emptyTrackSelectorResult;
    private Renderer[] enabledRenderers;
    private final Handler eventHandler;
    private final HandlerWrapper handler;
    private final HandlerThread internalPlaybackThread;
    private final LoadControl loadControl;
    private final DefaultMediaClock mediaClock;
    private MediaSource mediaSource;
    private int nextPendingMessageIndex;
    private SeekPosition pendingInitialSeekPosition;
    private final ArrayList<PendingMessageInfo> pendingMessages;
    private int pendingPrepareCount;
    private final Period period;
    private boolean playWhenReady;
    private PlaybackInfo playbackInfo;
    private final PlaybackInfoUpdate playbackInfoUpdate;
    private final ExoPlayer player;
    private final MediaPeriodQueue queue = new MediaPeriodQueue();
    private boolean rebuffering;
    private boolean released;
    private final RendererCapabilities[] rendererCapabilities;
    private long rendererPositionUs;
    private final Renderer[] renderers;
    private int repeatMode;
    private final boolean retainBackBufferFromKeyframe;
    private SeekParameters seekParameters;
    private boolean shuffleModeEnabled;
    private final TrackSelector trackSelector;
    private final Window window;

    private static final class MediaSourceRefreshInfo {
        public final Object manifest;
        public final MediaSource source;
        public final Timeline timeline;

        public MediaSourceRefreshInfo(MediaSource source, Timeline timeline, Object manifest) {
            this.source = source;
            this.timeline = timeline;
            this.manifest = manifest;
        }
    }

    private static final class PendingMessageInfo implements Comparable<PendingMessageInfo> {
        public final PlayerMessage message;
        public int resolvedPeriodIndex;
        public long resolvedPeriodTimeUs;
        @Nullable
        public Object resolvedPeriodUid;

        public PendingMessageInfo(PlayerMessage message) {
            this.message = message;
        }

        public void setResolvedPosition(int periodIndex, long periodTimeUs, Object periodUid) {
            this.resolvedPeriodIndex = periodIndex;
            this.resolvedPeriodTimeUs = periodTimeUs;
            this.resolvedPeriodUid = periodUid;
        }

        public int compareTo(@NonNull PendingMessageInfo other) {
            int i = 1;
            if ((this.resolvedPeriodUid == null ? 1 : null) != (other.resolvedPeriodUid == null ? 1 : null)) {
                if (this.resolvedPeriodUid != null) {
                    i = -1;
                }
                return i;
            } else if (this.resolvedPeriodUid == null) {
                return 0;
            } else {
                int comparePeriodIndex = this.resolvedPeriodIndex - other.resolvedPeriodIndex;
                if (comparePeriodIndex != 0) {
                    return comparePeriodIndex;
                }
                return Util.compareLong(this.resolvedPeriodTimeUs, other.resolvedPeriodTimeUs);
            }
        }
    }

    private static final class PlaybackInfoUpdate {
        private int discontinuityReason;
        private PlaybackInfo lastPlaybackInfo;
        private int operationAcks;
        private boolean positionDiscontinuity;

        private PlaybackInfoUpdate() {
        }

        public boolean hasPendingUpdate(PlaybackInfo playbackInfo) {
            if (playbackInfo == this.lastPlaybackInfo && this.operationAcks <= 0) {
                if (!this.positionDiscontinuity) {
                    return false;
                }
            }
            return true;
        }

        public void reset(PlaybackInfo playbackInfo) {
            this.lastPlaybackInfo = playbackInfo;
            this.operationAcks = 0;
            this.positionDiscontinuity = false;
        }

        public void incrementPendingOperationAcks(int operationAcks) {
            this.operationAcks += operationAcks;
        }

        public void setPositionDiscontinuity(int discontinuityReason) {
            boolean z = true;
            if (!this.positionDiscontinuity || this.discontinuityReason == 4) {
                this.positionDiscontinuity = true;
                this.discontinuityReason = discontinuityReason;
                return;
            }
            if (discontinuityReason != 4) {
                z = false;
            }
            Assertions.checkArgument(z);
        }
    }

    private static final class SeekPosition {
        public final Timeline timeline;
        public final int windowIndex;
        public final long windowPositionUs;

        public SeekPosition(Timeline timeline, int windowIndex, long windowPositionUs) {
            this.timeline = timeline;
            this.windowIndex = windowIndex;
            this.windowPositionUs = windowPositionUs;
        }
    }

    public synchronized void release() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:25:0x0029 in {5, 12, 14, 18, 19, 21, 24} preds:[]
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
        r2 = this;
        monitor-enter(r2);
        r0 = r2.released;	 Catch:{ all -> 0x0026 }
        if (r0 == 0) goto L_0x0007;
    L_0x0005:
        monitor-exit(r2);
        return;
    L_0x0007:
        r0 = r2.handler;	 Catch:{ all -> 0x0026 }
        r1 = 7;	 Catch:{ all -> 0x0026 }
        r0.sendEmptyMessage(r1);	 Catch:{ all -> 0x0026 }
        r0 = 0;	 Catch:{ all -> 0x0026 }
    L_0x000e:
        r1 = r2.released;	 Catch:{ all -> 0x0026 }
        if (r1 != 0) goto L_0x0019;
    L_0x0012:
        r2.wait();	 Catch:{ InterruptedException -> 0x0016 }
        goto L_0x000e;
    L_0x0016:
        r1 = move-exception;
        r0 = 1;
        goto L_0x000e;
    L_0x0019:
        if (r0 == 0) goto L_0x0023;
    L_0x001b:
        r1 = java.lang.Thread.currentThread();	 Catch:{ all -> 0x0026 }
        r1.interrupt();	 Catch:{ all -> 0x0026 }
        goto L_0x0024;
    L_0x0024:
        monitor-exit(r2);
        return;
    L_0x0026:
        r0 = move-exception;
        monitor-exit(r2);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.ExoPlayerImplInternal.release():void");
    }

    public ExoPlayerImplInternal(Renderer[] renderers, TrackSelector trackSelector, TrackSelectorResult emptyTrackSelectorResult, LoadControl loadControl, BandwidthMeter bandwidthMeter, boolean playWhenReady, int repeatMode, boolean shuffleModeEnabled, Handler eventHandler, ExoPlayer player, Clock clock) {
        this.renderers = renderers;
        this.trackSelector = trackSelector;
        this.emptyTrackSelectorResult = emptyTrackSelectorResult;
        this.loadControl = loadControl;
        this.bandwidthMeter = bandwidthMeter;
        this.playWhenReady = playWhenReady;
        this.repeatMode = repeatMode;
        this.shuffleModeEnabled = shuffleModeEnabled;
        this.eventHandler = eventHandler;
        this.player = player;
        this.clock = clock;
        this.backBufferDurationUs = loadControl.getBackBufferDurationUs();
        this.retainBackBufferFromKeyframe = loadControl.retainBackBufferFromKeyframe();
        this.seekParameters = SeekParameters.DEFAULT;
        this.playbackInfo = PlaybackInfo.createDummy(C0555C.TIME_UNSET, emptyTrackSelectorResult);
        this.playbackInfoUpdate = new PlaybackInfoUpdate();
        this.rendererCapabilities = new RendererCapabilities[renderers.length];
        for (int i = 0; i < renderers.length; i++) {
            renderers[i].setIndex(i);
            this.rendererCapabilities[i] = renderers[i].getCapabilities();
        }
        this.mediaClock = new DefaultMediaClock(this, clock);
        this.pendingMessages = new ArrayList();
        this.enabledRenderers = new Renderer[0];
        this.window = new Window();
        this.period = new Period();
        trackSelector.init(this, bandwidthMeter);
        this.internalPlaybackThread = new HandlerThread("ExoPlayerImplInternal:Handler", -16);
        this.internalPlaybackThread.start();
        this.handler = clock.createHandler(this.internalPlaybackThread.getLooper(), this);
    }

    public void prepare(MediaSource mediaSource, boolean resetPosition, boolean resetState) {
        this.handler.obtainMessage(0, resetPosition, resetState, mediaSource).sendToTarget();
    }

    public void setPlayWhenReady(boolean playWhenReady) {
        this.handler.obtainMessage(1, playWhenReady, 0).sendToTarget();
    }

    public void setRepeatMode(int repeatMode) {
        this.handler.obtainMessage(12, repeatMode, 0).sendToTarget();
    }

    public void setShuffleModeEnabled(boolean shuffleModeEnabled) {
        this.handler.obtainMessage(13, shuffleModeEnabled, 0).sendToTarget();
    }

    public void seekTo(Timeline timeline, int windowIndex, long positionUs) {
        this.handler.obtainMessage(3, new SeekPosition(timeline, windowIndex, positionUs)).sendToTarget();
    }

    public void setPlaybackParameters(PlaybackParameters playbackParameters) {
        this.handler.obtainMessage(4, playbackParameters).sendToTarget();
    }

    public void setSeekParameters(SeekParameters seekParameters) {
        this.handler.obtainMessage(5, seekParameters).sendToTarget();
    }

    public void stop(boolean reset) {
        this.handler.obtainMessage(6, reset, 0).sendToTarget();
    }

    public synchronized void sendMessage(PlayerMessage message) {
        if (this.released) {
            Log.m10w(TAG, "Ignoring messages sent after release.");
            message.markAsProcessed(false);
            return;
        }
        this.handler.obtainMessage(14, message).sendToTarget();
    }

    public Looper getPlaybackLooper() {
        return this.internalPlaybackThread.getLooper();
    }

    public void onSourceInfoRefreshed(MediaSource source, Timeline timeline, Object manifest) {
        this.handler.obtainMessage(8, new MediaSourceRefreshInfo(source, timeline, manifest)).sendToTarget();
    }

    public void onPrepared(MediaPeriod source) {
        this.handler.obtainMessage(9, source).sendToTarget();
    }

    public void onContinueLoadingRequested(MediaPeriod source) {
        this.handler.obtainMessage(10, source).sendToTarget();
    }

    public void onTrackSelectionsInvalidated() {
        this.handler.sendEmptyMessage(11);
    }

    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
        this.handler.obtainMessage(16, playbackParameters).sendToTarget();
    }

    public boolean handleMessage(Message msg) {
        try {
            switch (msg.what) {
                case 0:
                    prepareInternal((MediaSource) msg.obj, msg.arg1 != 0, msg.arg2 != 0);
                    break;
                case 1:
                    setPlayWhenReadyInternal(msg.arg1 != 0);
                    break;
                case 2:
                    doSomeWork();
                    break;
                case 3:
                    seekToInternal((SeekPosition) msg.obj);
                    break;
                case 4:
                    setPlaybackParametersInternal((PlaybackParameters) msg.obj);
                    break;
                case 5:
                    setSeekParametersInternal((SeekParameters) msg.obj);
                    break;
                case 6:
                    stopInternal(msg.arg1 != 0, true);
                    break;
                case 7:
                    releaseInternal();
                    return true;
                case 8:
                    handleSourceInfoRefreshed((MediaSourceRefreshInfo) msg.obj);
                    break;
                case 9:
                    handlePeriodPrepared((MediaPeriod) msg.obj);
                    break;
                case 10:
                    handleContinueLoadingRequested((MediaPeriod) msg.obj);
                    break;
                case 11:
                    reselectTracksInternal();
                    break;
                case 12:
                    setRepeatModeInternal(msg.arg1);
                    break;
                case 13:
                    setShuffleModeEnabledInternal(msg.arg1 != 0);
                    break;
                case 14:
                    sendMessageInternal((PlayerMessage) msg.obj);
                    break;
                case 15:
                    sendMessageToTargetThread((PlayerMessage) msg.obj);
                    break;
                case 16:
                    handlePlaybackParameters((PlaybackParameters) msg.obj);
                    break;
                default:
                    return false;
            }
            maybeNotifyPlaybackInfoChanged();
        } catch (ExoPlaybackException e) {
            Log.m7e(TAG, "Playback error.", e);
            stopInternal(false, false);
            this.eventHandler.obtainMessage(2, e).sendToTarget();
            maybeNotifyPlaybackInfoChanged();
        } catch (IOException e2) {
            Log.m7e(TAG, "Source error.", e2);
            stopInternal(false, false);
            this.eventHandler.obtainMessage(2, ExoPlaybackException.createForSource(e2)).sendToTarget();
            maybeNotifyPlaybackInfoChanged();
        } catch (RuntimeException e3) {
            Log.m7e(TAG, "Internal runtime error.", e3);
            stopInternal(false, false);
            this.eventHandler.obtainMessage(2, ExoPlaybackException.createForUnexpected(e3)).sendToTarget();
            maybeNotifyPlaybackInfoChanged();
        }
        return true;
    }

    private void setState(int state) {
        if (this.playbackInfo.playbackState != state) {
            this.playbackInfo = this.playbackInfo.copyWithPlaybackState(state);
        }
    }

    private void setIsLoading(boolean isLoading) {
        if (this.playbackInfo.isLoading != isLoading) {
            this.playbackInfo = this.playbackInfo.copyWithIsLoading(isLoading);
        }
    }

    private void maybeNotifyPlaybackInfoChanged() {
        if (this.playbackInfoUpdate.hasPendingUpdate(this.playbackInfo)) {
            this.eventHandler.obtainMessage(0, this.playbackInfoUpdate.operationAcks, this.playbackInfoUpdate.positionDiscontinuity ? this.playbackInfoUpdate.discontinuityReason : -1, this.playbackInfo).sendToTarget();
            this.playbackInfoUpdate.reset(this.playbackInfo);
        }
    }

    private void prepareInternal(MediaSource mediaSource, boolean resetPosition, boolean resetState) {
        this.pendingPrepareCount++;
        resetInternal(true, resetPosition, resetState);
        this.loadControl.onPrepared();
        this.mediaSource = mediaSource;
        setState(2);
        mediaSource.prepareSource(this.player, true, this, this.bandwidthMeter.getTransferListener());
        this.handler.sendEmptyMessage(2);
    }

    private void setPlayWhenReadyInternal(boolean playWhenReady) throws ExoPlaybackException {
        this.rebuffering = false;
        this.playWhenReady = playWhenReady;
        if (!playWhenReady) {
            stopRenderers();
            updatePlaybackPositions();
        } else if (this.playbackInfo.playbackState == 3) {
            startRenderers();
            this.handler.sendEmptyMessage(2);
        } else if (this.playbackInfo.playbackState == 2) {
            this.handler.sendEmptyMessage(2);
        }
    }

    private void setRepeatModeInternal(int repeatMode) throws ExoPlaybackException {
        this.repeatMode = repeatMode;
        if (!this.queue.updateRepeatMode(repeatMode)) {
            seekToCurrentPosition(true);
        }
        handleLoadingMediaPeriodChanged(false);
    }

    private void setShuffleModeEnabledInternal(boolean shuffleModeEnabled) throws ExoPlaybackException {
        this.shuffleModeEnabled = shuffleModeEnabled;
        if (!this.queue.updateShuffleModeEnabled(shuffleModeEnabled)) {
            seekToCurrentPosition(true);
        }
        handleLoadingMediaPeriodChanged(false);
    }

    private void seekToCurrentPosition(boolean sendDiscontinuity) throws ExoPlaybackException {
        MediaPeriodId periodId = this.queue.getPlayingPeriod().info.id;
        long newPositionUs = seekToPeriodPosition(periodId, this.playbackInfo.positionUs, true);
        if (newPositionUs != this.playbackInfo.positionUs) {
            PlaybackInfo playbackInfo = this.playbackInfo;
            this.playbackInfo = playbackInfo.copyWithNewPosition(periodId, newPositionUs, playbackInfo.contentPositionUs, getTotalBufferedDurationUs());
            if (sendDiscontinuity) {
                this.playbackInfoUpdate.setPositionDiscontinuity(4);
            }
        }
    }

    private void startRenderers() throws ExoPlaybackException {
        int i = 0;
        this.rebuffering = false;
        this.mediaClock.start();
        Renderer[] rendererArr = this.enabledRenderers;
        int length = rendererArr.length;
        while (i < length) {
            rendererArr[i].start();
            i++;
        }
    }

    private void stopRenderers() throws ExoPlaybackException {
        this.mediaClock.stop();
        for (Renderer renderer : this.enabledRenderers) {
            ensureStopped(renderer);
        }
    }

    private void updatePlaybackPositions() throws ExoPlaybackException {
        if (this.queue.hasPlayingPeriod()) {
            MediaPeriodHolder playingPeriodHolder = this.queue.getPlayingPeriod();
            long periodPositionUs = playingPeriodHolder.mediaPeriod.readDiscontinuity();
            if (periodPositionUs != C0555C.TIME_UNSET) {
                resetRendererPosition(periodPositionUs);
                if (periodPositionUs != this.playbackInfo.positionUs) {
                    PlaybackInfo playbackInfo = this.playbackInfo;
                    this.playbackInfo = playbackInfo.copyWithNewPosition(playbackInfo.periodId, periodPositionUs, this.playbackInfo.contentPositionUs, getTotalBufferedDurationUs());
                    this.playbackInfoUpdate.setPositionDiscontinuity(4);
                }
            } else {
                this.rendererPositionUs = this.mediaClock.syncAndGetPositionUs();
                periodPositionUs = playingPeriodHolder.toPeriodTime(this.rendererPositionUs);
                maybeTriggerPendingMessages(this.playbackInfo.positionUs, periodPositionUs);
                this.playbackInfo.positionUs = periodPositionUs;
            }
            MediaPeriodHolder loadingPeriod = this.queue.getLoadingPeriod();
            this.playbackInfo.bufferedPositionUs = loadingPeriod.getBufferedPositionUs();
            this.playbackInfo.totalBufferedDurationUs = getTotalBufferedDurationUs();
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void doSomeWork() throws com.google.android.exoplayer2.ExoPlaybackException, java.io.IOException {
        /*
        r19 = this;
        r0 = r19;
        r1 = r0.clock;
        r1 = r1.uptimeMillis();
        r19.updatePeriods();
        r3 = r0.queue;
        r3 = r3.hasPlayingPeriod();
        r4 = 10;
        if (r3 != 0) goto L_0x001c;
    L_0x0015:
        r19.maybeThrowPeriodPrepareError();
        r0.scheduleNextWork(r1, r4);
        return;
    L_0x001c:
        r3 = r0.queue;
        r3 = r3.getPlayingPeriod();
        r6 = "doSomeWork";
        com.google.android.exoplayer2.util.TraceUtil.beginSection(r6);
        r19.updatePlaybackPositions();
        r6 = android.os.SystemClock.elapsedRealtime();
        r8 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r6 = r6 * r8;
        r10 = r3.mediaPeriod;
        r11 = r0.playbackInfo;
        r11 = r11.positionUs;
        r13 = r0.backBufferDurationUs;
        r11 = r11 - r13;
        r13 = r0.retainBackBufferFromKeyframe;
        r10.discardBuffer(r11, r13);
        r10 = 1;
        r11 = 1;
        r12 = r0.enabledRenderers;
        r13 = r12.length;
        r15 = r10;
        r10 = 0;
    L_0x0047:
        if (r10 >= r13) goto L_0x0087;
    L_0x0049:
        r14 = r12[r10];
        r8 = r0.rendererPositionUs;
        r14.render(r8, r6);
        r8 = 1;
        if (r15 == 0) goto L_0x005b;
    L_0x0053:
        r9 = r14.isEnded();
        if (r9 == 0) goto L_0x005b;
    L_0x0059:
        r9 = 1;
        goto L_0x005c;
    L_0x005b:
        r9 = 0;
    L_0x005c:
        r15 = r9;
        r9 = r14.isReady();
        if (r9 != 0) goto L_0x0072;
    L_0x0063:
        r9 = r14.isEnded();
        if (r9 != 0) goto L_0x0072;
    L_0x0069:
        r9 = r0.rendererWaitingForNextStream(r14);
        if (r9 == 0) goto L_0x0070;
    L_0x006f:
        goto L_0x0073;
    L_0x0070:
        r9 = 0;
        goto L_0x0074;
    L_0x0073:
        r9 = 1;
    L_0x0074:
        if (r9 != 0) goto L_0x007a;
    L_0x0076:
        r14.maybeThrowStreamError();
        goto L_0x007b;
    L_0x007b:
        if (r11 == 0) goto L_0x0080;
    L_0x007d:
        if (r9 == 0) goto L_0x0080;
    L_0x007f:
        goto L_0x0081;
    L_0x0080:
        r8 = 0;
    L_0x0081:
        r11 = r8;
        r10 = r10 + 1;
        r8 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        goto L_0x0047;
    L_0x0087:
        if (r11 != 0) goto L_0x008d;
    L_0x0089:
        r19.maybeThrowPeriodPrepareError();
        goto L_0x008e;
    L_0x008e:
        r8 = r3.info;
        r8 = r8.durationUs;
        r10 = 4;
        r12 = 3;
        r13 = 2;
        if (r15 == 0) goto L_0x00b5;
    L_0x0097:
        r17 = -9223372036854775807; // 0x8000000000000001 float:1.4E-45 double:-4.9E-324;
        r14 = (r8 > r17 ? 1 : (r8 == r17 ? 0 : -1));
        if (r14 == 0) goto L_0x00a8;
    L_0x00a0:
        r14 = r0.playbackInfo;
        r4 = r14.positionUs;
        r14 = (r8 > r4 ? 1 : (r8 == r4 ? 0 : -1));
        if (r14 > 0) goto L_0x00b5;
    L_0x00a8:
        r4 = r3.info;
        r4 = r4.isFinal;
        if (r4 == 0) goto L_0x00b5;
    L_0x00ae:
        r0.setState(r10);
        r19.stopRenderers();
        goto L_0x00f1;
        r4 = r0.playbackInfo;
        r4 = r4.playbackState;
        if (r4 != r13) goto L_0x00cf;
    L_0x00bc:
        r4 = r0.shouldTransitionToReadyState(r11);
        if (r4 == 0) goto L_0x00ce;
    L_0x00c2:
        r0.setState(r12);
        r4 = r0.playWhenReady;
        if (r4 == 0) goto L_0x00cd;
    L_0x00c9:
        r19.startRenderers();
        goto L_0x00f1;
    L_0x00cd:
        goto L_0x00f1;
    L_0x00ce:
        goto L_0x00d0;
    L_0x00d0:
        r4 = r0.playbackInfo;
        r4 = r4.playbackState;
        if (r4 != r12) goto L_0x00f0;
    L_0x00d6:
        r4 = r0.enabledRenderers;
        r4 = r4.length;
        if (r4 != 0) goto L_0x00e2;
    L_0x00db:
        r4 = r19.isTimelineReady();
        if (r4 == 0) goto L_0x00e4;
    L_0x00e1:
        goto L_0x00ef;
    L_0x00e2:
        if (r11 != 0) goto L_0x00ef;
    L_0x00e4:
        r4 = r0.playWhenReady;
        r0.rebuffering = r4;
        r0.setState(r13);
        r19.stopRenderers();
        goto L_0x00f1;
    L_0x00ef:
        goto L_0x00f1;
    L_0x00f1:
        r4 = r0.playbackInfo;
        r4 = r4.playbackState;
        if (r4 != r13) goto L_0x0106;
    L_0x00f7:
        r4 = r0.enabledRenderers;
        r5 = r4.length;
        r14 = 0;
    L_0x00fb:
        if (r14 >= r5) goto L_0x0105;
    L_0x00fd:
        r16 = r4[r14];
        r16.maybeThrowStreamError();
        r14 = r14 + 1;
        goto L_0x00fb;
    L_0x0105:
        goto L_0x0107;
    L_0x0107:
        r4 = r0.playWhenReady;
        if (r4 == 0) goto L_0x0111;
    L_0x010b:
        r4 = r0.playbackInfo;
        r4 = r4.playbackState;
        if (r4 == r12) goto L_0x0117;
    L_0x0111:
        r4 = r0.playbackInfo;
        r4 = r4.playbackState;
        if (r4 != r13) goto L_0x011d;
    L_0x0117:
        r4 = 10;
        r0.scheduleNextWork(r1, r4);
        goto L_0x0134;
    L_0x011d:
        r4 = r0.enabledRenderers;
        r4 = r4.length;
        if (r4 == 0) goto L_0x012e;
    L_0x0122:
        r4 = r0.playbackInfo;
        r4 = r4.playbackState;
        if (r4 == r10) goto L_0x012e;
    L_0x0128:
        r4 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r0.scheduleNextWork(r1, r4);
        goto L_0x0134;
        r4 = r0.handler;
        r4.removeMessages(r13);
    L_0x0134:
        com.google.android.exoplayer2.util.TraceUtil.endSection();
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.ExoPlayerImplInternal.doSomeWork():void");
    }

    private void scheduleNextWork(long thisOperationStartTimeMs, long intervalMs) {
        this.handler.removeMessages(2);
        this.handler.sendEmptyMessageAtTime(2, thisOperationStartTimeMs + intervalMs);
    }

    private void seekToInternal(SeekPosition seekPosition) throws ExoPlaybackException {
        long contentPositionUs;
        MediaPeriodId periodId;
        boolean seekPositionAdjusted;
        long periodPositionUs;
        Throwable th;
        SeekPosition seekPosition2 = seekPosition;
        this.playbackInfoUpdate.incrementPendingOperationAcks(1);
        Pair<Object, Long> resolvedSeekPosition = resolveSeekPosition(seekPosition2, true);
        if (resolvedSeekPosition == null) {
            MediaPeriodId periodId2 = r1.playbackInfo.getDummyFirstMediaPeriodId(r1.shuffleModeEnabled, r1.window);
            contentPositionUs = C0555C.TIME_UNSET;
            periodId = periodId2;
            seekPositionAdjusted = true;
            periodPositionUs = C0555C.TIME_UNSET;
        } else {
            Object periodUid = resolvedSeekPosition.first;
            contentPositionUs = ((Long) resolvedSeekPosition.second).longValue();
            periodId = r1.queue.resolveMediaPeriodIdForAds(periodUid, contentPositionUs);
            if (periodId.isAd()) {
                periodPositionUs = 0;
                seekPositionAdjusted = true;
            } else {
                periodPositionUs = ((Long) resolvedSeekPosition.second).longValue();
                seekPositionAdjusted = seekPosition2.windowPositionUs == C0555C.TIME_UNSET;
            }
        }
        Pair<Object, Long> pair;
        try {
            if (r1.mediaSource == null) {
            } else if (r1.pendingPrepareCount > 0) {
                pair = resolvedSeekPosition;
            } else if (periodPositionUs == C0555C.TIME_UNSET) {
                try {
                    setState(4);
                    resetInternal(false, true, false);
                    pair = resolvedSeekPosition;
                    r1.playbackInfo = r1.playbackInfo.copyWithNewPosition(periodId, periodPositionUs, contentPositionUs, getTotalBufferedDurationUs());
                    if (!seekPositionAdjusted) {
                        r1.playbackInfoUpdate.setPositionDiscontinuity(2);
                    }
                } catch (Throwable th2) {
                    th = th2;
                    pair = resolvedSeekPosition;
                    r1.playbackInfo = r1.playbackInfo.copyWithNewPosition(periodId, periodPositionUs, contentPositionUs, getTotalBufferedDurationUs());
                    if (!seekPositionAdjusted) {
                        r1.playbackInfoUpdate.setPositionDiscontinuity(2);
                    }
                    throw th;
                }
            } else {
                long newPeriodPositionUs = periodPositionUs;
                if (periodId.equals(r1.playbackInfo.periodId)) {
                    MediaPeriodHolder playingPeriodHolder = r1.queue.getPlayingPeriod();
                    if (playingPeriodHolder != null && newPeriodPositionUs != 0) {
                        newPeriodPositionUs = playingPeriodHolder.mediaPeriod.getAdjustedSeekPositionUs(newPeriodPositionUs, r1.seekParameters);
                    }
                    try {
                        if (C0555C.usToMs(newPeriodPositionUs) == C0555C.usToMs(r1.playbackInfo.positionUs)) {
                            r1.playbackInfo = r1.playbackInfo.copyWithNewPosition(periodId, r1.playbackInfo.positionUs, contentPositionUs, getTotalBufferedDurationUs());
                            if (seekPositionAdjusted) {
                                r1.playbackInfoUpdate.setPositionDiscontinuity(2);
                            }
                            return;
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        r1.playbackInfo = r1.playbackInfo.copyWithNewPosition(periodId, periodPositionUs, contentPositionUs, getTotalBufferedDurationUs());
                        if (!seekPositionAdjusted) {
                            r1.playbackInfoUpdate.setPositionDiscontinuity(2);
                        }
                        throw th;
                    }
                }
                long newPeriodPositionUs2 = seekToPeriodPosition(periodId, newPeriodPositionUs);
                seekPositionAdjusted |= periodPositionUs != newPeriodPositionUs2 ? 1 : 0;
                periodPositionUs = newPeriodPositionUs2;
                r1.playbackInfo = r1.playbackInfo.copyWithNewPosition(periodId, periodPositionUs, contentPositionUs, getTotalBufferedDurationUs());
                if (!seekPositionAdjusted) {
                    r1.playbackInfoUpdate.setPositionDiscontinuity(2);
                }
            }
            r1.pendingInitialSeekPosition = seekPosition2;
            r1.playbackInfo = r1.playbackInfo.copyWithNewPosition(periodId, periodPositionUs, contentPositionUs, getTotalBufferedDurationUs());
            if (!seekPositionAdjusted) {
                r1.playbackInfoUpdate.setPositionDiscontinuity(2);
            }
        } catch (Throwable th4) {
            th = th4;
            pair = resolvedSeekPosition;
            r1.playbackInfo = r1.playbackInfo.copyWithNewPosition(periodId, periodPositionUs, contentPositionUs, getTotalBufferedDurationUs());
            if (!seekPositionAdjusted) {
                r1.playbackInfoUpdate.setPositionDiscontinuity(2);
            }
            throw th;
        }
    }

    private long seekToPeriodPosition(MediaPeriodId periodId, long periodPositionUs) throws ExoPlaybackException {
        return seekToPeriodPosition(periodId, periodPositionUs, this.queue.getPlayingPeriod() != this.queue.getReadingPeriod());
    }

    private long seekToPeriodPosition(MediaPeriodId periodId, long periodPositionUs, boolean forceDisableRenderers) throws ExoPlaybackException {
        stopRenderers();
        this.rebuffering = false;
        setState(2);
        MediaPeriodHolder oldPlayingPeriodHolder = this.queue.getPlayingPeriod();
        MediaPeriodHolder newPlayingPeriodHolder = oldPlayingPeriodHolder;
        while (newPlayingPeriodHolder != null) {
            if (periodId.equals(newPlayingPeriodHolder.info.id) && newPlayingPeriodHolder.prepared) {
                this.queue.removeAfter(newPlayingPeriodHolder);
                break;
            }
            newPlayingPeriodHolder = this.queue.advancePlayingPeriod();
        }
        if (oldPlayingPeriodHolder == newPlayingPeriodHolder) {
            if (!forceDisableRenderers) {
                if (newPlayingPeriodHolder == null) {
                    updatePlayingPeriodRenderers(oldPlayingPeriodHolder);
                    if (newPlayingPeriodHolder.hasEnabledTracks) {
                        periodPositionUs = newPlayingPeriodHolder.mediaPeriod.seekToUs(periodPositionUs);
                        newPlayingPeriodHolder.mediaPeriod.discardBuffer(periodPositionUs - this.backBufferDurationUs, this.retainBackBufferFromKeyframe);
                    }
                    resetRendererPosition(periodPositionUs);
                    maybeContinueLoading();
                } else {
                    this.queue.clear(true);
                    this.playbackInfo = this.playbackInfo.copyWithTrackInfo(TrackGroupArray.EMPTY, this.emptyTrackSelectorResult);
                    resetRendererPosition(periodPositionUs);
                }
                handleLoadingMediaPeriodChanged(false);
                this.handler.sendEmptyMessage(2);
                return periodPositionUs;
            }
        }
        for (Renderer renderer : this.enabledRenderers) {
            disableRenderer(renderer);
        }
        this.enabledRenderers = new Renderer[0];
        oldPlayingPeriodHolder = null;
        if (newPlayingPeriodHolder == null) {
            this.queue.clear(true);
            this.playbackInfo = this.playbackInfo.copyWithTrackInfo(TrackGroupArray.EMPTY, this.emptyTrackSelectorResult);
            resetRendererPosition(periodPositionUs);
        } else {
            updatePlayingPeriodRenderers(oldPlayingPeriodHolder);
            if (newPlayingPeriodHolder.hasEnabledTracks) {
                periodPositionUs = newPlayingPeriodHolder.mediaPeriod.seekToUs(periodPositionUs);
                newPlayingPeriodHolder.mediaPeriod.discardBuffer(periodPositionUs - this.backBufferDurationUs, this.retainBackBufferFromKeyframe);
            }
            resetRendererPosition(periodPositionUs);
            maybeContinueLoading();
        }
        handleLoadingMediaPeriodChanged(false);
        this.handler.sendEmptyMessage(2);
        return periodPositionUs;
    }

    private void resetRendererPosition(long periodPositionUs) throws ExoPlaybackException {
        long toRendererTime;
        if (this.queue.hasPlayingPeriod()) {
            toRendererTime = this.queue.getPlayingPeriod().toRendererTime(periodPositionUs);
        } else {
            toRendererTime = periodPositionUs;
        }
        this.rendererPositionUs = toRendererTime;
        this.mediaClock.resetPosition(this.rendererPositionUs);
        for (Renderer renderer : this.enabledRenderers) {
            renderer.resetPosition(this.rendererPositionUs);
        }
    }

    private void setPlaybackParametersInternal(PlaybackParameters playbackParameters) {
        this.mediaClock.setPlaybackParameters(playbackParameters);
    }

    private void setSeekParametersInternal(SeekParameters seekParameters) {
        this.seekParameters = seekParameters;
    }

    private void stopInternal(boolean reset, boolean acknowledgeStop) {
        resetInternal(true, reset, reset);
        this.playbackInfoUpdate.incrementPendingOperationAcks(this.pendingPrepareCount + acknowledgeStop);
        this.pendingPrepareCount = 0;
        this.loadControl.onStopped();
        setState(1);
    }

    private void releaseInternal() {
        resetInternal(true, true, true);
        this.loadControl.onReleased();
        setState(1);
        this.internalPlaybackThread.quit();
        synchronized (this) {
            this.released = true;
            notifyAll();
        }
    }

    private void resetInternal(boolean releaseMediaSource, boolean resetPosition, boolean resetState) {
        this.handler.removeMessages(2);
        this.rebuffering = false;
        this.mediaClock.stop();
        this.rendererPositionUs = 0;
        for (Renderer renderer : this.enabledRenderers) {
            try {
                disableRenderer(renderer);
            } catch (Exception e) {
                Log.m7e(TAG, "Stop failed.", e);
            }
        }
        r1.enabledRenderers = new Renderer[0];
        r1.queue.clear(resetPosition ^ 1);
        setIsLoading(false);
        if (resetPosition) {
            r1.pendingInitialSeekPosition = null;
        }
        if (resetState) {
            r1.queue.setTimeline(Timeline.EMPTY);
            Iterator it = r1.pendingMessages.iterator();
            while (it.hasNext()) {
                ((PendingMessageInfo) it.next()).message.markAsProcessed(false);
            }
            r1.pendingMessages.clear();
            r1.nextPendingMessageIndex = 0;
        }
        MediaPeriodId mediaPeriodId = resetPosition ? r1.playbackInfo.getDummyFirstMediaPeriodId(r1.shuffleModeEnabled, r1.window) : r1.playbackInfo.periodId;
        long j = C0555C.TIME_UNSET;
        long startPositionUs = resetPosition ? C0555C.TIME_UNSET : r1.playbackInfo.positionUs;
        if (!resetPosition) {
            j = r1.playbackInfo.contentPositionUs;
        }
        r1.playbackInfo = new PlaybackInfo(resetState ? Timeline.EMPTY : r1.playbackInfo.timeline, resetState ? null : r1.playbackInfo.manifest, mediaPeriodId, startPositionUs, j, r1.playbackInfo.playbackState, false, resetState ? TrackGroupArray.EMPTY : r1.playbackInfo.trackGroups, resetState ? r1.emptyTrackSelectorResult : r1.playbackInfo.trackSelectorResult, mediaPeriodId, startPositionUs, 0, startPositionUs);
        if (releaseMediaSource) {
            MediaSource mediaSource = r1.mediaSource;
            if (mediaSource != null) {
                mediaSource.releaseSource(r1);
                r1.mediaSource = null;
            }
        }
    }

    private void sendMessageInternal(PlayerMessage message) throws ExoPlaybackException {
        if (message.getPositionMs() == C0555C.TIME_UNSET) {
            sendMessageToTarget(message);
            return;
        }
        if (this.mediaSource != null) {
            if (this.pendingPrepareCount <= 0) {
                PendingMessageInfo pendingMessageInfo = new PendingMessageInfo(message);
                if (resolvePendingMessagePosition(pendingMessageInfo)) {
                    this.pendingMessages.add(pendingMessageInfo);
                    Collections.sort(this.pendingMessages);
                    return;
                }
                message.markAsProcessed(false);
                return;
            }
        }
        this.pendingMessages.add(new PendingMessageInfo(message));
    }

    private void sendMessageToTarget(PlayerMessage message) throws ExoPlaybackException {
        if (message.getHandler().getLooper() == this.handler.getLooper()) {
            deliverMessage(message);
            if (this.playbackInfo.playbackState != 3) {
                if (this.playbackInfo.playbackState != 2) {
                    return;
                }
            }
            this.handler.sendEmptyMessage(2);
            return;
        }
        this.handler.obtainMessage(15, message).sendToTarget();
    }

    private void sendMessageToTargetThread(PlayerMessage message) {
        message.getHandler().post(new -$$Lambda$ExoPlayerImplInternal$XwFxncwlyfAWA4k618O8BNtCsr0(this, message));
    }

    public static /* synthetic */ void lambda$sendMessageToTargetThread$0(ExoPlayerImplInternal exoPlayerImplInternal, PlayerMessage message) {
        try {
            exoPlayerImplInternal.deliverMessage(message);
        } catch (ExoPlaybackException e) {
            Log.m7e(TAG, "Unexpected error delivering message on external thread.", e);
            throw new RuntimeException(e);
        }
    }

    private void deliverMessage(PlayerMessage message) throws ExoPlaybackException {
        if (!message.isCanceled()) {
            try {
                message.getTarget().handleMessage(message.getType(), message.getPayload());
            } finally {
                message.markAsProcessed(true);
            }
        }
    }

    private void resolvePendingMessagePositions() {
        for (int i = this.pendingMessages.size() - 1; i >= 0; i--) {
            if (!resolvePendingMessagePosition((PendingMessageInfo) this.pendingMessages.get(i))) {
                ((PendingMessageInfo) this.pendingMessages.get(i)).message.markAsProcessed(false);
                this.pendingMessages.remove(i);
            }
        }
        Collections.sort(this.pendingMessages);
    }

    private boolean resolvePendingMessagePosition(PendingMessageInfo pendingMessageInfo) {
        if (pendingMessageInfo.resolvedPeriodUid == null) {
            Pair<Object, Long> periodPosition = resolveSeekPosition(new SeekPosition(pendingMessageInfo.message.getTimeline(), pendingMessageInfo.message.getWindowIndex(), C0555C.msToUs(pendingMessageInfo.message.getPositionMs())), false);
            if (periodPosition == null) {
                return false;
            }
            pendingMessageInfo.setResolvedPosition(this.playbackInfo.timeline.getIndexOfPeriod(periodPosition.first), ((Long) periodPosition.second).longValue(), periodPosition.first);
        } else {
            int index = this.playbackInfo.timeline.getIndexOfPeriod(pendingMessageInfo.resolvedPeriodUid);
            if (index == -1) {
                return false;
            }
            pendingMessageInfo.resolvedPeriodIndex = index;
        }
        return true;
    }

    private void maybeTriggerPendingMessages(long oldPeriodPositionUs, long newPeriodPositionUs) throws ExoPlaybackException {
        if (!this.pendingMessages.isEmpty()) {
            if (!this.playbackInfo.periodId.isAd()) {
                if (this.playbackInfo.startPositionUs == oldPeriodPositionUs) {
                    oldPeriodPositionUs--;
                }
                int currentPeriodIndex = this.playbackInfo.timeline.getIndexOfPeriod(this.playbackInfo.periodId.periodUid);
                int i = this.nextPendingMessageIndex;
                PendingMessageInfo previousInfo = i > 0 ? (PendingMessageInfo) this.pendingMessages.get(i - 1) : null;
                while (previousInfo != null && (previousInfo.resolvedPeriodIndex > currentPeriodIndex || (previousInfo.resolvedPeriodIndex == currentPeriodIndex && previousInfo.resolvedPeriodTimeUs > oldPeriodPositionUs))) {
                    this.nextPendingMessageIndex--;
                    int i2 = this.nextPendingMessageIndex;
                    previousInfo = i2 > 0 ? (PendingMessageInfo) this.pendingMessages.get(i2 - 1) : null;
                }
                PendingMessageInfo nextInfo = this.nextPendingMessageIndex < this.pendingMessages.size() ? (PendingMessageInfo) this.pendingMessages.get(this.nextPendingMessageIndex) : null;
                while (nextInfo != null && nextInfo.resolvedPeriodUid != null && (nextInfo.resolvedPeriodIndex < currentPeriodIndex || (nextInfo.resolvedPeriodIndex == currentPeriodIndex && nextInfo.resolvedPeriodTimeUs <= oldPeriodPositionUs))) {
                    this.nextPendingMessageIndex++;
                    nextInfo = this.nextPendingMessageIndex < this.pendingMessages.size() ? (PendingMessageInfo) this.pendingMessages.get(this.nextPendingMessageIndex) : null;
                }
                while (nextInfo != null && nextInfo.resolvedPeriodUid != null && nextInfo.resolvedPeriodIndex == currentPeriodIndex && nextInfo.resolvedPeriodTimeUs > oldPeriodPositionUs && nextInfo.resolvedPeriodTimeUs <= newPeriodPositionUs) {
                    sendMessageToTarget(nextInfo.message);
                    if (!nextInfo.message.getDeleteAfterDelivery()) {
                        if (!nextInfo.message.isCanceled()) {
                            this.nextPendingMessageIndex++;
                            nextInfo = this.nextPendingMessageIndex >= this.pendingMessages.size() ? (PendingMessageInfo) this.pendingMessages.get(this.nextPendingMessageIndex) : null;
                        }
                    }
                    this.pendingMessages.remove(this.nextPendingMessageIndex);
                    if (this.nextPendingMessageIndex >= this.pendingMessages.size()) {
                    }
                    nextInfo = this.nextPendingMessageIndex >= this.pendingMessages.size() ? (PendingMessageInfo) this.pendingMessages.get(this.nextPendingMessageIndex) : null;
                }
            }
        }
    }

    private void ensureStopped(Renderer renderer) throws ExoPlaybackException {
        if (renderer.getState() == 2) {
            renderer.stop();
        }
    }

    private void disableRenderer(Renderer renderer) throws ExoPlaybackException {
        this.mediaClock.onRendererDisabled(renderer);
        ensureStopped(renderer);
        renderer.disable();
    }

    private void reselectTracksInternal() throws ExoPlaybackException {
        if (this.queue.hasPlayingPeriod()) {
            boolean z;
            float playbackSpeed = r0.mediaClock.getPlaybackParameters().speed;
            MediaPeriodHolder periodHolder = r0.queue.getPlayingPeriod();
            MediaPeriodHolder readingPeriodHolder = r0.queue.getReadingPeriod();
            boolean selectionsChangedForReadPeriod = true;
            while (periodHolder != null) {
                if (!periodHolder.prepared) {
                    z = selectionsChangedForReadPeriod;
                    break;
                } else if (periodHolder.selectTracks(playbackSpeed)) {
                    if (selectionsChangedForReadPeriod) {
                        MediaPeriodHolder mediaPeriodHolder;
                        MediaPeriodHolder playingPeriodHolder;
                        MediaPeriodHolder playingPeriodHolder2 = r0.queue.getPlayingPeriod();
                        boolean[] streamResetFlags = new boolean[r0.renderers.length];
                        long periodPositionUs = playingPeriodHolder2.applyTrackSelection(r0.playbackInfo.positionUs, r0.queue.removeAfter(playingPeriodHolder2), streamResetFlags);
                        if (r0.playbackInfo.playbackState == 4 || periodPositionUs == r0.playbackInfo.positionUs) {
                            mediaPeriodHolder = playingPeriodHolder2;
                        } else {
                            PlaybackInfo playbackInfo = r0.playbackInfo;
                            mediaPeriodHolder = playingPeriodHolder2;
                            r0.playbackInfo = playbackInfo.copyWithNewPosition(playbackInfo.periodId, periodPositionUs, r0.playbackInfo.contentPositionUs, getTotalBufferedDurationUs());
                            r0.playbackInfoUpdate.setPositionDiscontinuity(4);
                            resetRendererPosition(periodPositionUs);
                        }
                        int enabledRendererCount = 0;
                        boolean[] rendererWasEnabledFlags = new boolean[r0.renderers.length];
                        int i = 0;
                        while (true) {
                            Renderer renderer = r0.renderers;
                            if (i >= renderer.length) {
                                break;
                            }
                            renderer = renderer[i];
                            rendererWasEnabledFlags[i] = renderer.getState() != 0;
                            playingPeriodHolder = mediaPeriodHolder;
                            SampleStream sampleStream = playingPeriodHolder.sampleStreams[i];
                            if (sampleStream != null) {
                                enabledRendererCount++;
                            }
                            if (!rendererWasEnabledFlags[i]) {
                                z = selectionsChangedForReadPeriod;
                            } else if (sampleStream != renderer.getStream()) {
                                disableRenderer(renderer);
                                z = selectionsChangedForReadPeriod;
                            } else if (streamResetFlags[i]) {
                                z = selectionsChangedForReadPeriod;
                                renderer.resetPosition(r0.rendererPositionUs);
                            } else {
                                z = selectionsChangedForReadPeriod;
                            }
                            i++;
                            mediaPeriodHolder = playingPeriodHolder;
                            selectionsChangedForReadPeriod = z;
                        }
                        playingPeriodHolder = mediaPeriodHolder;
                        r0.playbackInfo = r0.playbackInfo.copyWithTrackInfo(playingPeriodHolder.trackGroups, playingPeriodHolder.trackSelectorResult);
                        enableRenderers(rendererWasEnabledFlags, enabledRendererCount);
                    } else {
                        r0.queue.removeAfter(periodHolder);
                        if (periodHolder.prepared) {
                            periodHolder.applyTrackSelection(Math.max(periodHolder.info.startPositionUs, periodHolder.toPeriodTime(r0.rendererPositionUs)), false);
                        }
                    }
                    handleLoadingMediaPeriodChanged(true);
                    if (!r0.playbackInfo.playbackState) {
                        maybeContinueLoading();
                        updatePlaybackPositions();
                        r0.handler.sendEmptyMessage(2);
                    }
                    return;
                } else {
                    z = selectionsChangedForReadPeriod;
                    if (periodHolder == readingPeriodHolder) {
                        selectionsChangedForReadPeriod = false;
                    } else {
                        selectionsChangedForReadPeriod = z;
                    }
                    periodHolder = periodHolder.next;
                }
            }
            z = selectionsChangedForReadPeriod;
        }
    }

    private void updateTrackSelectionPlaybackSpeed(float playbackSpeed) {
        for (MediaPeriodHolder periodHolder = this.queue.getFrontPeriod(); periodHolder != null; periodHolder = periodHolder.next) {
            if (periodHolder.trackSelectorResult != null) {
                for (TrackSelection trackSelection : periodHolder.trackSelectorResult.selections.getAll()) {
                    if (trackSelection != null) {
                        trackSelection.onPlaybackSpeed(playbackSpeed);
                    }
                }
            }
        }
    }

    private boolean shouldTransitionToReadyState(boolean renderersReadyOrEnded) {
        if (this.enabledRenderers.length == 0) {
            return isTimelineReady();
        }
        boolean z = false;
        if (!renderersReadyOrEnded) {
            return false;
        }
        if (!this.playbackInfo.isLoading) {
            return true;
        }
        MediaPeriodHolder loadingHolder = this.queue.getLoadingPeriod();
        boolean bufferedToEnd = loadingHolder.isFullyBuffered() && loadingHolder.info.isFinal;
        if (!bufferedToEnd) {
            if (!this.loadControl.shouldStartPlayback(getTotalBufferedDurationUs(), this.mediaClock.getPlaybackParameters().speed, this.rebuffering)) {
                return z;
            }
        }
        z = true;
        return z;
    }

    private boolean isTimelineReady() {
        MediaPeriodHolder playingPeriodHolder = this.queue.getPlayingPeriod();
        long playingPeriodDurationUs = playingPeriodHolder.info.durationUs;
        if (playingPeriodDurationUs != C0555C.TIME_UNSET && this.playbackInfo.positionUs >= playingPeriodDurationUs) {
            if (playingPeriodHolder.next != null) {
                if (!playingPeriodHolder.next.prepared) {
                    if (playingPeriodHolder.next.info.id.isAd()) {
                        return true;
                    }
                }
            }
            return false;
        }
        return true;
    }

    private void maybeThrowSourceInfoRefreshError() throws IOException {
        if (this.queue.getLoadingPeriod() != null) {
            Renderer[] rendererArr = this.enabledRenderers;
            int length = rendererArr.length;
            int i = 0;
            while (i < length) {
                if (rendererArr[i].hasReadStreamToEnd()) {
                    i++;
                } else {
                    return;
                }
            }
        }
        this.mediaSource.maybeThrowSourceInfoRefreshError();
    }

    private void maybeThrowPeriodPrepareError() throws IOException {
        MediaPeriodHolder loadingPeriodHolder = this.queue.getLoadingPeriod();
        MediaPeriodHolder readingPeriodHolder = this.queue.getReadingPeriod();
        if (loadingPeriodHolder != null && !loadingPeriodHolder.prepared && (readingPeriodHolder == null || readingPeriodHolder.next == loadingPeriodHolder)) {
            Renderer[] rendererArr = this.enabledRenderers;
            int length = rendererArr.length;
            int i = 0;
            while (i < length) {
                if (rendererArr[i].hasReadStreamToEnd()) {
                    i++;
                } else {
                    return;
                }
            }
            loadingPeriodHolder.mediaPeriod.maybeThrowPrepareError();
        }
    }

    private void handleSourceInfoRefreshed(MediaSourceRefreshInfo sourceRefreshInfo) throws ExoPlaybackException {
        MediaSourceRefreshInfo mediaSourceRefreshInfo = sourceRefreshInfo;
        if (mediaSourceRefreshInfo.source == this.mediaSource) {
            Timeline oldTimeline = r1.playbackInfo.timeline;
            Timeline timeline = mediaSourceRefreshInfo.timeline;
            Object manifest = mediaSourceRefreshInfo.manifest;
            r1.queue.setTimeline(timeline);
            r1.playbackInfo = r1.playbackInfo.copyWithTimeline(timeline, manifest);
            resolvePendingMessagePositions();
            int i = r1.pendingPrepareCount;
            Pair<Object, Long> periodPosition;
            Object periodUid;
            long positionUs;
            MediaPeriodId periodId;
            if (i > 0) {
                r1.playbackInfoUpdate.incrementPendingOperationAcks(i);
                r1.pendingPrepareCount = 0;
                periodPosition = r1.pendingInitialSeekPosition;
                if (periodPosition != null) {
                    try {
                        periodPosition = resolveSeekPosition(periodPosition, true);
                        r1.pendingInitialSeekPosition = null;
                        if (periodPosition == null) {
                            handleSourceInfoRefreshEndedPlayback();
                        } else {
                            periodUid = periodPosition.first;
                            positionUs = ((Long) periodPosition.second).longValue();
                            periodId = r1.queue.resolveMediaPeriodIdForAds(periodUid, positionUs);
                            r1.playbackInfo = r1.playbackInfo.resetToNewPosition(periodId, periodId.isAd() ? 0 : positionUs, positionUs);
                        }
                    } catch (IllegalSeekPositionException e) {
                        IllegalSeekPositionException e2 = e2;
                        r1.playbackInfo = r1.playbackInfo.resetToNewPosition(r1.playbackInfo.getDummyFirstMediaPeriodId(r1.shuffleModeEnabled, r1.window), C0555C.TIME_UNSET, C0555C.TIME_UNSET);
                        throw e2;
                    }
                } else if (r1.playbackInfo.startPositionUs == C0555C.TIME_UNSET) {
                    if (timeline.isEmpty()) {
                        handleSourceInfoRefreshEndedPlayback();
                    } else {
                        periodPosition = getPeriodPosition(timeline, timeline.getFirstWindowIndex(r1.shuffleModeEnabled), C0555C.TIME_UNSET);
                        periodUid = periodPosition.first;
                        positionUs = ((Long) periodPosition.second).longValue();
                        periodId = r1.queue.resolveMediaPeriodIdForAds(periodUid, positionUs);
                        r1.playbackInfo = r1.playbackInfo.resetToNewPosition(periodId, periodId.isAd() ? 0 : positionUs, positionUs);
                    }
                }
            } else if (oldTimeline.isEmpty()) {
                if (!timeline.isEmpty()) {
                    periodPosition = getPeriodPosition(timeline, timeline.getFirstWindowIndex(r1.shuffleModeEnabled), C0555C.TIME_UNSET);
                    periodUid = periodPosition.first;
                    positionUs = ((Long) periodPosition.second).longValue();
                    periodId = r1.queue.resolveMediaPeriodIdForAds(periodUid, positionUs);
                    r1.playbackInfo = r1.playbackInfo.resetToNewPosition(periodId, periodId.isAd() ? 0 : positionUs, positionUs);
                }
            } else {
                MediaPeriodHolder periodHolder = r1.queue.getFrontPeriod();
                long contentPositionUs = r1.playbackInfo.contentPositionUs;
                Object playingPeriodUid = periodHolder == null ? r1.playbackInfo.periodId.periodUid : periodHolder.uid;
                int periodIndex = timeline.getIndexOfPeriod(playingPeriodUid);
                MediaPeriodId periodId2;
                if (periodIndex == -1) {
                    periodUid = resolveSubsequentPeriod(playingPeriodUid, oldTimeline, timeline);
                    if (periodUid == null) {
                        handleSourceInfoRefreshEndedPlayback();
                        return;
                    }
                    Pair<Object, Long> defaultPosition = getPeriodPosition(timeline, timeline.getPeriodByUid(periodUid, r1.period).windowIndex, C0555C.TIME_UNSET);
                    periodUid = defaultPosition.first;
                    contentPositionUs = ((Long) defaultPosition.second).longValue();
                    periodId2 = r1.queue.resolveMediaPeriodIdForAds(periodUid, contentPositionUs);
                    if (periodHolder != null) {
                        while (periodHolder.next != null) {
                            periodHolder = periodHolder.next;
                            if (periodHolder.info.id.equals(periodId2)) {
                                periodHolder.info = r1.queue.getUpdatedMediaPeriodInfo(periodHolder.info);
                            }
                        }
                    }
                    r1.playbackInfo = r1.playbackInfo.copyWithNewPosition(periodId2, seekToPeriodPosition(periodId2, periodId2.isAd() ? 0 : contentPositionUs), contentPositionUs, getTotalBufferedDurationUs());
                    return;
                }
                MediaPeriodId playingPeriodId = r1.playbackInfo.periodId;
                long j;
                if (playingPeriodId.isAd()) {
                    periodId2 = r1.queue.resolveMediaPeriodIdForAds(playingPeriodUid, contentPositionUs);
                    if (periodId2.equals(playingPeriodId)) {
                        j = contentPositionUs;
                    } else {
                        r1.playbackInfo = r1.playbackInfo.copyWithNewPosition(periodId2, seekToPeriodPosition(periodId2, periodId2.isAd() ? 0 : contentPositionUs), contentPositionUs, getTotalBufferedDurationUs());
                        return;
                    }
                }
                j = contentPositionUs;
                if (!r1.queue.updateQueuedPeriods(playingPeriodId, r1.rendererPositionUs)) {
                    seekToCurrentPosition(false);
                }
                handleLoadingMediaPeriodChanged(false);
            }
        }
    }

    private void handleSourceInfoRefreshEndedPlayback() {
        setState(4);
        resetInternal(false, true, false);
    }

    @Nullable
    private Object resolveSubsequentPeriod(Object oldPeriodUid, Timeline oldTimeline, Timeline newTimeline) {
        int oldPeriodIndex = oldTimeline.getIndexOfPeriod(oldPeriodUid);
        int newPeriodIndex = -1;
        int maxIterations = oldTimeline.getPeriodCount();
        for (int i = 0; i < maxIterations && newPeriodIndex == -1; i++) {
            oldPeriodIndex = oldTimeline.getNextPeriodIndex(oldPeriodIndex, this.period, this.window, this.repeatMode, this.shuffleModeEnabled);
            if (oldPeriodIndex == -1) {
                break;
            }
            newPeriodIndex = newTimeline.getIndexOfPeriod(oldTimeline.getUidOfPeriod(oldPeriodIndex));
        }
        return newPeriodIndex == -1 ? null : newTimeline.getUidOfPeriod(newPeriodIndex);
    }

    private Pair<Object, Long> resolveSeekPosition(SeekPosition seekPosition, boolean trySubsequentPeriods) {
        Timeline timeline = this.playbackInfo.timeline;
        Timeline seekTimeline = seekPosition.timeline;
        if (timeline.isEmpty()) {
            return null;
        }
        if (seekTimeline.isEmpty()) {
            seekTimeline = timeline;
        }
        try {
            Pair<Object, Long> periodPosition = seekTimeline.getPeriodPosition(this.window, this.period, seekPosition.windowIndex, seekPosition.windowPositionUs);
            if (timeline == seekTimeline) {
                return periodPosition;
            }
            int periodIndex = timeline.getIndexOfPeriod(periodPosition.first);
            if (periodIndex != -1) {
                return periodPosition;
            }
            if (trySubsequentPeriods) {
                if (resolveSubsequentPeriod(periodPosition.first, seekTimeline, timeline) != null) {
                    return getPeriodPosition(timeline, timeline.getPeriod(periodIndex, this.period).windowIndex, C0555C.TIME_UNSET);
                }
            }
            return null;
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalSeekPositionException(timeline, seekPosition.windowIndex, seekPosition.windowPositionUs);
        }
    }

    private Pair<Object, Long> getPeriodPosition(Timeline timeline, int windowIndex, long windowPositionUs) {
        return timeline.getPeriodPosition(this.window, this.period, windowIndex, windowPositionUs);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updatePeriods() throws com.google.android.exoplayer2.ExoPlaybackException, java.io.IOException {
        /*
        r19 = this;
        r0 = r19;
        r1 = r0.mediaSource;
        if (r1 != 0) goto L_0x0007;
    L_0x0006:
        return;
    L_0x0007:
        r2 = r0.pendingPrepareCount;
        if (r2 <= 0) goto L_0x000f;
    L_0x000b:
        r1.maybeThrowSourceInfoRefreshError();
        return;
    L_0x000f:
        r19.maybeUpdateLoadingPeriod();
        r1 = r0.queue;
        r1 = r1.getLoadingPeriod();
        r2 = 0;
        if (r1 == 0) goto L_0x002d;
    L_0x001b:
        r3 = r1.isFullyBuffered();
        if (r3 == 0) goto L_0x0022;
    L_0x0021:
        goto L_0x002d;
    L_0x0022:
        r3 = r0.playbackInfo;
        r3 = r3.isLoading;
        if (r3 != 0) goto L_0x002c;
    L_0x0028:
        r19.maybeContinueLoading();
        goto L_0x0031;
    L_0x002c:
        goto L_0x0031;
        r0.setIsLoading(r2);
    L_0x0031:
        r3 = r0.queue;
        r3 = r3.hasPlayingPeriod();
        if (r3 != 0) goto L_0x003a;
    L_0x0039:
        return;
    L_0x003a:
        r3 = r0.queue;
        r3 = r3.getPlayingPeriod();
        r4 = r0.queue;
        r4 = r4.getReadingPeriod();
        r5 = 0;
    L_0x0047:
        r6 = r0.playWhenReady;
        if (r6 == 0) goto L_0x0096;
    L_0x004b:
        if (r3 == r4) goto L_0x0096;
    L_0x004d:
        r6 = r0.rendererPositionUs;
        r8 = r3.next;
        r8 = r8.getStartPositionRendererTime();
        r10 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r10 < 0) goto L_0x0095;
    L_0x0059:
        if (r5 == 0) goto L_0x005f;
    L_0x005b:
        r19.maybeNotifyPlaybackInfoChanged();
        goto L_0x0060;
    L_0x0060:
        r6 = r3.info;
        r6 = r6.isLastInTimelinePeriod;
        if (r6 == 0) goto L_0x0068;
    L_0x0066:
        r6 = 0;
        goto L_0x0069;
    L_0x0068:
        r6 = 3;
    L_0x0069:
        r7 = r3;
        r8 = r0.queue;
        r3 = r8.advancePlayingPeriod();
        r0.updatePlayingPeriodRenderers(r7);
        r8 = r0.playbackInfo;
        r9 = r3.info;
        r9 = r9.id;
        r10 = r3.info;
        r10 = r10.startPositionUs;
        r12 = r3.info;
        r12 = r12.contentPositionUs;
        r14 = r19.getTotalBufferedDurationUs();
        r8 = r8.copyWithNewPosition(r9, r10, r12, r14);
        r0.playbackInfo = r8;
        r8 = r0.playbackInfoUpdate;
        r8.setPositionDiscontinuity(r6);
        r19.updatePlaybackPositions();
        r5 = 1;
        goto L_0x0047;
    L_0x0095:
        goto L_0x0097;
    L_0x0097:
        r6 = r4.info;
        r6 = r6.isFinal;
        if (r6 == 0) goto L_0x00c1;
    L_0x009d:
        r2 = 0;
    L_0x009e:
        r6 = r0.renderers;
        r7 = r6.length;
        if (r2 >= r7) goto L_0x00c0;
    L_0x00a3:
        r6 = r6[r2];
        r7 = r4.sampleStreams;
        r7 = r7[r2];
        if (r7 == 0) goto L_0x00bc;
    L_0x00ab:
        r8 = r6.getStream();
        if (r8 != r7) goto L_0x00bc;
    L_0x00b1:
        r8 = r6.hasReadStreamToEnd();
        if (r8 == 0) goto L_0x00bb;
    L_0x00b7:
        r6.setCurrentStreamFinal();
        goto L_0x00bd;
    L_0x00bb:
        goto L_0x00bd;
    L_0x00bd:
        r2 = r2 + 1;
        goto L_0x009e;
    L_0x00c0:
        return;
    L_0x00c1:
        r6 = r4.next;
        if (r6 != 0) goto L_0x00c6;
    L_0x00c5:
        return;
    L_0x00c6:
        r6 = 0;
    L_0x00c7:
        r7 = r0.renderers;
        r8 = r7.length;
        if (r6 >= r8) goto L_0x00e8;
    L_0x00cc:
        r7 = r7[r6];
        r8 = r4.sampleStreams;
        r8 = r8[r6];
        r9 = r7.getStream();
        if (r9 != r8) goto L_0x00e6;
    L_0x00d8:
        if (r8 == 0) goto L_0x00e2;
    L_0x00da:
        r9 = r7.hasReadStreamToEnd();
        if (r9 != 0) goto L_0x00e1;
    L_0x00e0:
        goto L_0x00e7;
    L_0x00e1:
        goto L_0x00e3;
    L_0x00e3:
        r6 = r6 + 1;
        goto L_0x00c7;
    L_0x00e7:
        return;
        r6 = r4.next;
        r6 = r6.prepared;
        if (r6 != 0) goto L_0x00f3;
    L_0x00ef:
        r19.maybeThrowPeriodPrepareError();
        return;
    L_0x00f3:
        r6 = r4.trackSelectorResult;
        r7 = r0.queue;
        r4 = r7.advanceReadingPeriod();
        r7 = r4.trackSelectorResult;
        r8 = r4.mediaPeriod;
        r8 = r8.readDiscontinuity();
        r10 = -9223372036854775807; // 0x8000000000000001 float:1.4E-45 double:-4.9E-324;
        r13 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1));
        if (r13 == 0) goto L_0x010e;
    L_0x010c:
        r8 = 1;
        goto L_0x010f;
    L_0x010e:
        r8 = 0;
    L_0x010f:
        r9 = 0;
    L_0x0110:
        r10 = r0.renderers;
        r11 = r10.length;
        if (r9 >= r11) goto L_0x018a;
    L_0x0115:
        r10 = r10[r9];
        r11 = r6.isRendererEnabled(r9);
        if (r11 != 0) goto L_0x0122;
    L_0x011d:
        r16 = r1;
        r17 = r3;
        goto L_0x0180;
    L_0x0122:
        if (r8 == 0) goto L_0x012c;
    L_0x0124:
        r10.setCurrentStreamFinal();
        r16 = r1;
        r17 = r3;
        goto L_0x0180;
    L_0x012c:
        r13 = r10.isCurrentStreamFinal();
        if (r13 != 0) goto L_0x017c;
    L_0x0132:
        r13 = r7.selections;
        r13 = r13.get(r9);
        r14 = r7.isRendererEnabled(r9);
        r15 = r0.rendererCapabilities;
        r15 = r15[r9];
        r15 = r15.getTrackType();
        r2 = 6;
        if (r15 != r2) goto L_0x0149;
    L_0x0147:
        r2 = 1;
        goto L_0x014a;
    L_0x0149:
        r2 = 0;
    L_0x014a:
        r15 = r6.rendererConfigurations;
        r15 = r15[r9];
        r12 = r7.rendererConfigurations;
        r12 = r12[r9];
        if (r14 == 0) goto L_0x0172;
    L_0x0154:
        r16 = r12.equals(r15);
        if (r16 == 0) goto L_0x0172;
    L_0x015a:
        if (r2 != 0) goto L_0x0172;
    L_0x015c:
        r0 = getFormats(r13);
        r16 = r1;
        r1 = r4.sampleStreams;
        r1 = r1[r9];
        r18 = r2;
        r17 = r3;
        r2 = r4.getRendererOffset();
        r10.replaceStream(r0, r1, r2);
        goto L_0x0180;
    L_0x0172:
        r16 = r1;
        r18 = r2;
        r17 = r3;
        r10.setCurrentStreamFinal();
        goto L_0x0180;
    L_0x017c:
        r16 = r1;
        r17 = r3;
    L_0x0180:
        r9 = r9 + 1;
        r1 = r16;
        r3 = r17;
        r0 = r19;
        r2 = 0;
        goto L_0x0110;
    L_0x018a:
        r16 = r1;
        r17 = r3;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.ExoPlayerImplInternal.updatePeriods():void");
    }

    private void maybeUpdateLoadingPeriod() throws IOException {
        this.queue.reevaluateBuffer(this.rendererPositionUs);
        if (this.queue.shouldLoadNextMediaPeriod()) {
            MediaPeriodInfo info = this.queue.getNextMediaPeriodInfo(this.rendererPositionUs, this.playbackInfo);
            if (info == null) {
                maybeThrowSourceInfoRefreshError();
                return;
            }
            this.queue.enqueueNextMediaPeriod(this.rendererCapabilities, this.trackSelector, this.loadControl.getAllocator(), this.mediaSource, info).prepare(this, info.startPositionUs);
            setIsLoading(true);
            handleLoadingMediaPeriodChanged(false);
        }
    }

    private void handlePeriodPrepared(MediaPeriod mediaPeriod) throws ExoPlaybackException {
        if (this.queue.isLoading(mediaPeriod)) {
            MediaPeriodHolder loadingPeriodHolder = this.queue.getLoadingPeriod();
            loadingPeriodHolder.handlePrepared(this.mediaClock.getPlaybackParameters().speed);
            updateLoadControlTrackSelection(loadingPeriodHolder.trackGroups, loadingPeriodHolder.trackSelectorResult);
            if (!this.queue.hasPlayingPeriod()) {
                resetRendererPosition(this.queue.advancePlayingPeriod().info.startPositionUs);
                updatePlayingPeriodRenderers(null);
            }
            maybeContinueLoading();
        }
    }

    private void handleContinueLoadingRequested(MediaPeriod mediaPeriod) {
        if (this.queue.isLoading(mediaPeriod)) {
            this.queue.reevaluateBuffer(this.rendererPositionUs);
            maybeContinueLoading();
        }
    }

    private void handlePlaybackParameters(PlaybackParameters playbackParameters) throws ExoPlaybackException {
        this.eventHandler.obtainMessage(1, playbackParameters).sendToTarget();
        updateTrackSelectionPlaybackSpeed(playbackParameters.speed);
        for (Renderer renderer : this.renderers) {
            if (renderer != null) {
                renderer.setOperatingRate(playbackParameters.speed);
            }
        }
    }

    private void maybeContinueLoading() {
        MediaPeriodHolder loadingPeriodHolder = this.queue.getLoadingPeriod();
        long nextLoadPositionUs = loadingPeriodHolder.getNextLoadPositionUs();
        if (nextLoadPositionUs == Long.MIN_VALUE) {
            setIsLoading(false);
            return;
        }
        boolean continueLoading = this.loadControl.shouldContinueLoading(getTotalBufferedDurationUs(nextLoadPositionUs), this.mediaClock.getPlaybackParameters().speed);
        setIsLoading(continueLoading);
        if (continueLoading) {
            loadingPeriodHolder.continueLoading(this.rendererPositionUs);
        }
    }

    private void updatePlayingPeriodRenderers(@Nullable MediaPeriodHolder oldPlayingPeriodHolder) throws ExoPlaybackException {
        MediaPeriodHolder newPlayingPeriodHolder = this.queue.getPlayingPeriod();
        if (newPlayingPeriodHolder != null) {
            if (oldPlayingPeriodHolder != newPlayingPeriodHolder) {
                int enabledRendererCount = 0;
                boolean[] rendererWasEnabledFlags = new boolean[this.renderers.length];
                int i = 0;
                while (true) {
                    Renderer renderer = this.renderers;
                    if (i < renderer.length) {
                        renderer = renderer[i];
                        rendererWasEnabledFlags[i] = renderer.getState() != 0;
                        if (newPlayingPeriodHolder.trackSelectorResult.isRendererEnabled(i)) {
                            enabledRendererCount++;
                        }
                        if (rendererWasEnabledFlags[i]) {
                            if (newPlayingPeriodHolder.trackSelectorResult.isRendererEnabled(i)) {
                                if (renderer.isCurrentStreamFinal()) {
                                    if (renderer.getStream() == oldPlayingPeriodHolder.sampleStreams[i]) {
                                    }
                                }
                            }
                            disableRenderer(renderer);
                        }
                        i++;
                    } else {
                        this.playbackInfo = this.playbackInfo.copyWithTrackInfo(newPlayingPeriodHolder.trackGroups, newPlayingPeriodHolder.trackSelectorResult);
                        enableRenderers(rendererWasEnabledFlags, enabledRendererCount);
                        return;
                    }
                }
            }
        }
    }

    private void enableRenderers(boolean[] rendererWasEnabledFlags, int totalEnabledRendererCount) throws ExoPlaybackException {
        this.enabledRenderers = new Renderer[totalEnabledRendererCount];
        int enabledRendererCount = 0;
        MediaPeriodHolder playingPeriodHolder = this.queue.getPlayingPeriod();
        for (int i = 0; i < this.renderers.length; i++) {
            if (playingPeriodHolder.trackSelectorResult.isRendererEnabled(i)) {
                int enabledRendererCount2 = enabledRendererCount + 1;
                enableRenderer(i, rendererWasEnabledFlags[i], enabledRendererCount);
                enabledRendererCount = enabledRendererCount2;
            }
        }
    }

    private void enableRenderer(int rendererIndex, boolean wasRendererEnabled, int enabledRendererIndex) throws ExoPlaybackException {
        int i = rendererIndex;
        MediaPeriodHolder playingPeriodHolder = this.queue.getPlayingPeriod();
        Renderer renderer = this.renderers[i];
        this.enabledRenderers[enabledRendererIndex] = renderer;
        if (renderer.getState() == 0) {
            RendererConfiguration rendererConfiguration = playingPeriodHolder.trackSelectorResult.rendererConfigurations[i];
            Format[] formats = getFormats(playingPeriodHolder.trackSelectorResult.selections.get(i));
            boolean z = r0.playWhenReady && r0.playbackInfo.playbackState == 3;
            boolean playing = z;
            boolean joining = !wasRendererEnabled && playing;
            renderer.enable(rendererConfiguration, formats, playingPeriodHolder.sampleStreams[i], r0.rendererPositionUs, joining, playingPeriodHolder.getRendererOffset());
            r0.mediaClock.onRendererEnabled(renderer);
            if (playing) {
                renderer.start();
            }
        }
    }

    private boolean rendererWaitingForNextStream(Renderer renderer) {
        MediaPeriodHolder readingPeriodHolder = this.queue.getReadingPeriod();
        if (readingPeriodHolder.next != null && readingPeriodHolder.next.prepared) {
            if (renderer.hasReadStreamToEnd()) {
                return true;
            }
        }
        return false;
    }

    private void handleLoadingMediaPeriodChanged(boolean loadingTrackSelectionChanged) {
        long j;
        MediaPeriodHolder loadingMediaPeriodHolder = this.queue.getLoadingPeriod();
        MediaPeriodId loadingMediaPeriodId = loadingMediaPeriodHolder == null ? this.playbackInfo.periodId : loadingMediaPeriodHolder.info.id;
        boolean loadingMediaPeriodChanged = this.playbackInfo.loadingMediaPeriodId.equals(loadingMediaPeriodId) ^ 1;
        if (loadingMediaPeriodChanged) {
            this.playbackInfo = this.playbackInfo.copyWithLoadingMediaPeriodId(loadingMediaPeriodId);
        }
        PlaybackInfo playbackInfo = this.playbackInfo;
        if (loadingMediaPeriodHolder == null) {
            j = playbackInfo.positionUs;
        } else {
            j = loadingMediaPeriodHolder.getBufferedPositionUs();
        }
        playbackInfo.bufferedPositionUs = j;
        this.playbackInfo.totalBufferedDurationUs = getTotalBufferedDurationUs();
        if ((loadingMediaPeriodChanged || loadingTrackSelectionChanged) && loadingMediaPeriodHolder != null && loadingMediaPeriodHolder.prepared) {
            updateLoadControlTrackSelection(loadingMediaPeriodHolder.trackGroups, loadingMediaPeriodHolder.trackSelectorResult);
        }
    }

    private long getTotalBufferedDurationUs() {
        return getTotalBufferedDurationUs(this.playbackInfo.bufferedPositionUs);
    }

    private long getTotalBufferedDurationUs(long bufferedPositionInLoadingPeriodUs) {
        MediaPeriodHolder loadingPeriodHolder = this.queue.getLoadingPeriod();
        if (loadingPeriodHolder == null) {
            return 0;
        }
        return bufferedPositionInLoadingPeriodUs - loadingPeriodHolder.toPeriodTime(this.rendererPositionUs);
    }

    private void updateLoadControlTrackSelection(TrackGroupArray trackGroups, TrackSelectorResult trackSelectorResult) {
        this.loadControl.onTracksSelected(this.renderers, trackGroups, trackSelectorResult.selections);
    }

    private static Format[] getFormats(TrackSelection newSelection) {
        int length = newSelection != null ? newSelection.length() : 0;
        Format[] formats = new Format[length];
        for (int i = 0; i < length; i++) {
            formats[i] = newSelection.getFormat(i);
        }
        return formats;
    }
}
