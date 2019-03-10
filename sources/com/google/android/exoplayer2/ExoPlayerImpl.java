package com.google.android.exoplayer2;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.ExoPlayer.ExoPlayerMessage;
import com.google.android.exoplayer2.Player.AudioComponent;
import com.google.android.exoplayer2.Player.MetadataComponent;
import com.google.android.exoplayer2.Player.TextComponent;
import com.google.android.exoplayer2.Player.VideoComponent;
import com.google.android.exoplayer2.PlayerMessage.Target;
import com.google.android.exoplayer2.Timeline.Period;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSource.MediaPeriodId;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectorResult;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Clock;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

final class ExoPlayerImpl extends BasePlayer implements ExoPlayer {
    private static final String TAG = "ExoPlayerImpl";
    final TrackSelectorResult emptyTrackSelectorResult;
    private final Handler eventHandler;
    private boolean hasPendingPrepare;
    private boolean hasPendingSeek;
    private boolean internalPlayWhenReady;
    private final ExoPlayerImplInternal internalPlayer;
    private final Handler internalPlayerHandler;
    private final CopyOnWriteArraySet<Player$EventListener> listeners;
    private int maskingPeriodIndex;
    private int maskingWindowIndex;
    private long maskingWindowPositionMs;
    private MediaSource mediaSource;
    private int pendingOperationAcks;
    private final ArrayDeque<PlaybackInfoUpdate> pendingPlaybackInfoUpdates;
    private final Period period;
    private boolean playWhenReady;
    @Nullable
    private ExoPlaybackException playbackError;
    private PlaybackInfo playbackInfo;
    private PlaybackParameters playbackParameters;
    private final Renderer[] renderers;
    private int repeatMode;
    private SeekParameters seekParameters;
    private boolean shuffleModeEnabled;
    private final TrackSelector trackSelector;

    private static final class PlaybackInfoUpdate {
        private final boolean isLoadingChanged;
        private final Set<Player$EventListener> listeners;
        private final boolean playWhenReady;
        private final PlaybackInfo playbackInfo;
        private final boolean playbackStateOrPlayWhenReadyChanged;
        private final boolean positionDiscontinuity;
        private final int positionDiscontinuityReason;
        private final boolean seekProcessed;
        private final int timelineChangeReason;
        private final boolean timelineOrManifestChanged;
        private final TrackSelector trackSelector;
        private final boolean trackSelectorResultChanged;

        public PlaybackInfoUpdate(PlaybackInfo playbackInfo, PlaybackInfo previousPlaybackInfo, Set<Player$EventListener> listeners, TrackSelector trackSelector, boolean positionDiscontinuity, int positionDiscontinuityReason, int timelineChangeReason, boolean seekProcessed, boolean playWhenReady, boolean playWhenReadyChanged) {
            boolean z;
            this.playbackInfo = playbackInfo;
            this.listeners = listeners;
            this.trackSelector = trackSelector;
            this.positionDiscontinuity = positionDiscontinuity;
            this.positionDiscontinuityReason = positionDiscontinuityReason;
            this.timelineChangeReason = timelineChangeReason;
            this.seekProcessed = seekProcessed;
            this.playWhenReady = playWhenReady;
            boolean z2 = false;
            if (!playWhenReadyChanged) {
                if (previousPlaybackInfo.playbackState == playbackInfo.playbackState) {
                    z = false;
                    this.playbackStateOrPlayWhenReadyChanged = z;
                    if (previousPlaybackInfo.timeline == playbackInfo.timeline) {
                        if (previousPlaybackInfo.manifest != playbackInfo.manifest) {
                            z = false;
                            this.timelineOrManifestChanged = z;
                            this.isLoadingChanged = previousPlaybackInfo.isLoading == playbackInfo.isLoading;
                            if (previousPlaybackInfo.trackSelectorResult != playbackInfo.trackSelectorResult) {
                                z2 = true;
                            }
                            this.trackSelectorResultChanged = z2;
                        }
                    }
                    z = true;
                    this.timelineOrManifestChanged = z;
                    if (previousPlaybackInfo.isLoading == playbackInfo.isLoading) {
                    }
                    this.isLoadingChanged = previousPlaybackInfo.isLoading == playbackInfo.isLoading;
                    if (previousPlaybackInfo.trackSelectorResult != playbackInfo.trackSelectorResult) {
                        z2 = true;
                    }
                    this.trackSelectorResultChanged = z2;
                }
            }
            z = true;
            this.playbackStateOrPlayWhenReadyChanged = z;
            if (previousPlaybackInfo.timeline == playbackInfo.timeline) {
                if (previousPlaybackInfo.manifest != playbackInfo.manifest) {
                    z = false;
                    this.timelineOrManifestChanged = z;
                    if (previousPlaybackInfo.isLoading == playbackInfo.isLoading) {
                    }
                    this.isLoadingChanged = previousPlaybackInfo.isLoading == playbackInfo.isLoading;
                    if (previousPlaybackInfo.trackSelectorResult != playbackInfo.trackSelectorResult) {
                        z2 = true;
                    }
                    this.trackSelectorResultChanged = z2;
                }
            }
            z = true;
            this.timelineOrManifestChanged = z;
            if (previousPlaybackInfo.isLoading == playbackInfo.isLoading) {
            }
            this.isLoadingChanged = previousPlaybackInfo.isLoading == playbackInfo.isLoading;
            if (previousPlaybackInfo.trackSelectorResult != playbackInfo.trackSelectorResult) {
                z2 = true;
            }
            this.trackSelectorResultChanged = z2;
        }

        public void notifyListeners() {
            if (!this.timelineOrManifestChanged) {
                if (this.timelineChangeReason != 0) {
                    if (this.positionDiscontinuity) {
                        for (Player$EventListener listener : this.listeners) {
                            listener.onPositionDiscontinuity(this.positionDiscontinuityReason);
                        }
                    }
                    if (this.trackSelectorResultChanged) {
                        this.trackSelector.onSelectionActivated(this.playbackInfo.trackSelectorResult.info);
                        for (Player$EventListener listener2 : this.listeners) {
                            listener2.onTracksChanged(this.playbackInfo.trackGroups, this.playbackInfo.trackSelectorResult.selections);
                        }
                    }
                    if (this.isLoadingChanged) {
                        for (Player$EventListener listener22 : this.listeners) {
                            listener22.onLoadingChanged(this.playbackInfo.isLoading);
                        }
                    }
                    if (this.playbackStateOrPlayWhenReadyChanged) {
                        for (Player$EventListener listener222 : this.listeners) {
                            listener222.onPlayerStateChanged(this.playWhenReady, this.playbackInfo.playbackState);
                        }
                    }
                    if (this.seekProcessed) {
                        for (Player$EventListener listener2222 : this.listeners) {
                            listener2222.onSeekProcessed();
                        }
                    }
                }
            }
            for (Player$EventListener listener22222 : this.listeners) {
                listener22222.onTimelineChanged(this.playbackInfo.timeline, this.playbackInfo.manifest, this.timelineChangeReason);
            }
            if (this.positionDiscontinuity) {
                while (r0.hasNext()) {
                    listener22222.onPositionDiscontinuity(this.positionDiscontinuityReason);
                }
            }
            if (this.trackSelectorResultChanged) {
                this.trackSelector.onSelectionActivated(this.playbackInfo.trackSelectorResult.info);
                while (r0.hasNext()) {
                    listener22222.onTracksChanged(this.playbackInfo.trackGroups, this.playbackInfo.trackSelectorResult.selections);
                }
            }
            if (this.isLoadingChanged) {
                while (r0.hasNext()) {
                    listener22222.onLoadingChanged(this.playbackInfo.isLoading);
                }
            }
            if (this.playbackStateOrPlayWhenReadyChanged) {
                while (r0.hasNext()) {
                    listener22222.onPlayerStateChanged(this.playWhenReady, this.playbackInfo.playbackState);
                }
            }
            if (this.seekProcessed) {
                while (r0.hasNext()) {
                    listener22222.onSeekProcessed();
                }
            }
        }
    }

    public void seekTo(int r9, long r10) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:29:0x00a3 in {5, 9, 14, 15, 16, 19, 20, 21, 25, 26, 28} preds:[]
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
        r8 = this;
        r0 = r8.playbackInfo;
        r0 = r0.timeline;
        if (r9 < 0) goto L_0x009c;
    L_0x0006:
        r1 = r0.isEmpty();
        if (r1 != 0) goto L_0x0012;
    L_0x000c:
        r1 = r0.getWindowCount();
        if (r9 >= r1) goto L_0x009c;
    L_0x0012:
        r7 = 1;
        r8.hasPendingSeek = r7;
        r1 = r8.pendingOperationAcks;
        r1 = r1 + r7;
        r8.pendingOperationAcks = r1;
        r1 = r8.isPlayingAd();
        r2 = 0;
        if (r1 == 0) goto L_0x0036;
    L_0x0021:
        r1 = "ExoPlayerImpl";
        r3 = "seekTo ignored because an ad is playing";
        com.google.android.exoplayer2.util.Log.m10w(r1, r3);
        r1 = r8.eventHandler;
        r3 = -1;
        r4 = r8.playbackInfo;
        r1 = r1.obtainMessage(r2, r7, r3, r4);
        r1.sendToTarget();
        return;
    L_0x0036:
        r8.maskingWindowIndex = r9;
        r1 = r0.isEmpty();
        r3 = -9223372036854775807; // 0x8000000000000001 float:1.4E-45 double:-4.9E-324;
        if (r1 == 0) goto L_0x0050;
    L_0x0043:
        r1 = (r10 > r3 ? 1 : (r10 == r3 ? 0 : -1));
        if (r1 != 0) goto L_0x004a;
    L_0x0047:
        r3 = 0;
        goto L_0x004b;
    L_0x004a:
        r3 = r10;
    L_0x004b:
        r8.maskingWindowPositionMs = r3;
        r8.maskingPeriodIndex = r2;
        goto L_0x007c;
    L_0x0050:
        r1 = (r10 > r3 ? 1 : (r10 == r3 ? 0 : -1));
        if (r1 != 0) goto L_0x005f;
    L_0x0054:
        r1 = r8.window;
        r1 = r0.getWindow(r9, r1);
        r1 = r1.getDefaultPositionUs();
        goto L_0x0063;
    L_0x005f:
        r1 = com.google.android.exoplayer2.C0555C.msToUs(r10);
    L_0x0063:
        r5 = r1;
        r2 = r8.window;
        r3 = r8.period;
        r1 = r0;
        r4 = r9;
        r1 = r1.getPeriodPosition(r2, r3, r4, r5);
        r2 = com.google.android.exoplayer2.C0555C.usToMs(r5);
        r8.maskingWindowPositionMs = r2;
        r2 = r1.first;
        r2 = r0.getIndexOfPeriod(r2);
        r8.maskingPeriodIndex = r2;
    L_0x007c:
        r1 = r8.internalPlayer;
        r2 = com.google.android.exoplayer2.C0555C.msToUs(r10);
        r1.seekTo(r0, r9, r2);
        r1 = r8.listeners;
        r1 = r1.iterator();
    L_0x008b:
        r2 = r1.hasNext();
        if (r2 == 0) goto L_0x009b;
    L_0x0091:
        r2 = r1.next();
        r2 = (com.google.android.exoplayer2.Player$EventListener) r2;
        r2.onPositionDiscontinuity(r7);
        goto L_0x008b;
    L_0x009b:
        return;
        r1 = new com.google.android.exoplayer2.IllegalSeekPositionException;
        r1.<init>(r0, r9, r10);
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.ExoPlayerImpl.seekTo(int, long):void");
    }

    @SuppressLint({"HandlerLeak"})
    public ExoPlayerImpl(Renderer[] renderers, TrackSelector trackSelector, LoadControl loadControl, BandwidthMeter bandwidthMeter, Clock clock, Looper looper) {
        ExoPlayerImpl exoPlayerImpl = this;
        Renderer[] rendererArr = renderers;
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Init ");
        stringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
        stringBuilder.append(" [");
        stringBuilder.append(ExoPlayerLibraryInfo.VERSION_SLASHY);
        stringBuilder.append("] [");
        stringBuilder.append(Util.DEVICE_DEBUG_INFO);
        stringBuilder.append("]");
        Log.m8i(str, stringBuilder.toString());
        Assertions.checkState(rendererArr.length > 0);
        exoPlayerImpl.renderers = (Renderer[]) Assertions.checkNotNull(renderers);
        exoPlayerImpl.trackSelector = (TrackSelector) Assertions.checkNotNull(trackSelector);
        exoPlayerImpl.playWhenReady = false;
        exoPlayerImpl.repeatMode = 0;
        exoPlayerImpl.shuffleModeEnabled = false;
        exoPlayerImpl.listeners = new CopyOnWriteArraySet();
        exoPlayerImpl.emptyTrackSelectorResult = new TrackSelectorResult(new RendererConfiguration[rendererArr.length], new TrackSelection[rendererArr.length], null);
        exoPlayerImpl.period = new Period();
        exoPlayerImpl.playbackParameters = PlaybackParameters.DEFAULT;
        exoPlayerImpl.seekParameters = SeekParameters.DEFAULT;
        exoPlayerImpl.eventHandler = new Handler(looper) {
            public void handleMessage(Message msg) {
                ExoPlayerImpl.this.handleEvent(msg);
            }
        };
        exoPlayerImpl.playbackInfo = PlaybackInfo.createDummy(0, exoPlayerImpl.emptyTrackSelectorResult);
        exoPlayerImpl.pendingPlaybackInfoUpdates = new ArrayDeque();
        exoPlayerImpl.internalPlayer = new ExoPlayerImplInternal(renderers, trackSelector, exoPlayerImpl.emptyTrackSelectorResult, loadControl, bandwidthMeter, exoPlayerImpl.playWhenReady, exoPlayerImpl.repeatMode, exoPlayerImpl.shuffleModeEnabled, exoPlayerImpl.eventHandler, this, clock);
        exoPlayerImpl.internalPlayerHandler = new Handler(exoPlayerImpl.internalPlayer.getPlaybackLooper());
    }

    @Nullable
    public AudioComponent getAudioComponent() {
        return null;
    }

    @Nullable
    public VideoComponent getVideoComponent() {
        return null;
    }

    @Nullable
    public TextComponent getTextComponent() {
        return null;
    }

    @Nullable
    public MetadataComponent getMetadataComponent() {
        return null;
    }

    public Looper getPlaybackLooper() {
        return this.internalPlayer.getPlaybackLooper();
    }

    public Looper getApplicationLooper() {
        return this.eventHandler.getLooper();
    }

    public void addListener(Player$EventListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(Player$EventListener listener) {
        this.listeners.remove(listener);
    }

    public int getPlaybackState() {
        return this.playbackInfo.playbackState;
    }

    @Nullable
    public ExoPlaybackException getPlaybackError() {
        return this.playbackError;
    }

    public void retry() {
        if (this.mediaSource != null && (this.playbackError != null || this.playbackInfo.playbackState == 1)) {
            prepare(this.mediaSource, false, false);
        }
    }

    public void prepare(MediaSource mediaSource) {
        prepare(mediaSource, true, true);
    }

    public void prepare(MediaSource mediaSource, boolean resetPosition, boolean resetState) {
        this.playbackError = null;
        this.mediaSource = mediaSource;
        PlaybackInfo playbackInfo = getResetPlaybackInfo(resetPosition, resetState, 2);
        this.hasPendingPrepare = true;
        this.pendingOperationAcks++;
        this.internalPlayer.prepare(mediaSource, resetPosition, resetState);
        updatePlaybackInfo(playbackInfo, false, 4, 1, false, false);
    }

    public void setPlayWhenReady(boolean playWhenReady) {
        setPlayWhenReady(playWhenReady, false);
    }

    public void setPlayWhenReady(boolean playWhenReady, boolean suppressPlayback) {
        boolean internalPlayWhenReady = playWhenReady && !suppressPlayback;
        if (this.internalPlayWhenReady != internalPlayWhenReady) {
            this.internalPlayWhenReady = internalPlayWhenReady;
            this.internalPlayer.setPlayWhenReady(internalPlayWhenReady);
        }
        if (this.playWhenReady != playWhenReady) {
            this.playWhenReady = playWhenReady;
            updatePlaybackInfo(this.playbackInfo, false, 4, 1, false, true);
        }
    }

    public boolean getPlayWhenReady() {
        return this.playWhenReady;
    }

    public void setRepeatMode(int repeatMode) {
        if (this.repeatMode != repeatMode) {
            this.repeatMode = repeatMode;
            this.internalPlayer.setRepeatMode(repeatMode);
            Iterator it = this.listeners.iterator();
            while (it.hasNext()) {
                ((Player$EventListener) it.next()).onRepeatModeChanged(repeatMode);
            }
        }
    }

    public int getRepeatMode() {
        return this.repeatMode;
    }

    public void setShuffleModeEnabled(boolean shuffleModeEnabled) {
        if (this.shuffleModeEnabled != shuffleModeEnabled) {
            this.shuffleModeEnabled = shuffleModeEnabled;
            this.internalPlayer.setShuffleModeEnabled(shuffleModeEnabled);
            Iterator it = this.listeners.iterator();
            while (it.hasNext()) {
                ((Player$EventListener) it.next()).onShuffleModeEnabledChanged(shuffleModeEnabled);
            }
        }
    }

    public boolean getShuffleModeEnabled() {
        return this.shuffleModeEnabled;
    }

    public boolean isLoading() {
        return this.playbackInfo.isLoading;
    }

    public void setPlaybackParameters(@Nullable PlaybackParameters playbackParameters) {
        if (playbackParameters == null) {
            playbackParameters = PlaybackParameters.DEFAULT;
        }
        this.internalPlayer.setPlaybackParameters(playbackParameters);
    }

    public PlaybackParameters getPlaybackParameters() {
        return this.playbackParameters;
    }

    public void setSeekParameters(@Nullable SeekParameters seekParameters) {
        if (seekParameters == null) {
            seekParameters = SeekParameters.DEFAULT;
        }
        if (!this.seekParameters.equals(seekParameters)) {
            this.seekParameters = seekParameters;
            this.internalPlayer.setSeekParameters(seekParameters);
        }
    }

    public SeekParameters getSeekParameters() {
        return this.seekParameters;
    }

    public void stop(boolean reset) {
        if (reset) {
            this.playbackError = null;
            this.mediaSource = null;
        }
        PlaybackInfo playbackInfo = getResetPlaybackInfo(reset, reset, 1);
        this.pendingOperationAcks++;
        this.internalPlayer.stop(reset);
        updatePlaybackInfo(playbackInfo, false, 4, 1, false, false);
    }

    public void release() {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Release ");
        stringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
        stringBuilder.append(" [");
        stringBuilder.append(ExoPlayerLibraryInfo.VERSION_SLASHY);
        stringBuilder.append("] [");
        stringBuilder.append(Util.DEVICE_DEBUG_INFO);
        stringBuilder.append("] [");
        stringBuilder.append(ExoPlayerLibraryInfo.registeredModules());
        stringBuilder.append("]");
        Log.m8i(str, stringBuilder.toString());
        this.mediaSource = null;
        this.internalPlayer.release();
        this.eventHandler.removeCallbacksAndMessages(null);
    }

    @Deprecated
    public void sendMessages(ExoPlayerMessage... messages) {
        for (ExoPlayerMessage message : messages) {
            createMessage(message.target).setType(message.messageType).setPayload(message.message).send();
        }
    }

    public PlayerMessage createMessage(Target target) {
        return new PlayerMessage(this.internalPlayer, target, this.playbackInfo.timeline, getCurrentWindowIndex(), this.internalPlayerHandler);
    }

    @Deprecated
    public void blockingSendMessages(ExoPlayerMessage... messages) {
        List<PlayerMessage> playerMessages = new ArrayList();
        for (ExoPlayerMessage message : messages) {
            playerMessages.add(createMessage(message.target).setType(message.messageType).setPayload(message.message).send());
        }
        boolean wasInterrupted = false;
        for (PlayerMessage message2 : playerMessages) {
            boolean blockMessage = true;
            while (blockMessage) {
                try {
                    message2.blockUntilDelivered();
                    blockMessage = false;
                } catch (InterruptedException e) {
                    wasInterrupted = true;
                }
            }
        }
        if (wasInterrupted) {
            Thread.currentThread().interrupt();
        }
    }

    public int getCurrentPeriodIndex() {
        if (shouldMaskPosition()) {
            return this.maskingPeriodIndex;
        }
        return this.playbackInfo.timeline.getIndexOfPeriod(this.playbackInfo.periodId.periodUid);
    }

    public int getCurrentWindowIndex() {
        if (shouldMaskPosition()) {
            return this.maskingWindowIndex;
        }
        return this.playbackInfo.timeline.getPeriodByUid(this.playbackInfo.periodId.periodUid, this.period).windowIndex;
    }

    public long getDuration() {
        if (!isPlayingAd()) {
            return getContentDuration();
        }
        MediaPeriodId periodId = this.playbackInfo.periodId;
        this.playbackInfo.timeline.getPeriodByUid(periodId.periodUid, this.period);
        return C0555C.usToMs(this.period.getAdDurationUs(periodId.adGroupIndex, periodId.adIndexInAdGroup));
    }

    public long getCurrentPosition() {
        if (shouldMaskPosition()) {
            return this.maskingWindowPositionMs;
        }
        if (this.playbackInfo.periodId.isAd()) {
            return C0555C.usToMs(this.playbackInfo.positionUs);
        }
        return periodPositionUsToWindowPositionMs(this.playbackInfo.periodId, this.playbackInfo.positionUs);
    }

    public long getBufferedPosition() {
        if (!isPlayingAd()) {
            return getContentBufferedPosition();
        }
        long usToMs;
        if (this.playbackInfo.loadingMediaPeriodId.equals(this.playbackInfo.periodId)) {
            usToMs = C0555C.usToMs(this.playbackInfo.bufferedPositionUs);
        } else {
            usToMs = getDuration();
        }
        return usToMs;
    }

    public long getTotalBufferedDuration() {
        return Math.max(0, C0555C.usToMs(this.playbackInfo.totalBufferedDurationUs));
    }

    public boolean isPlayingAd() {
        return !shouldMaskPosition() && this.playbackInfo.periodId.isAd();
    }

    public int getCurrentAdGroupIndex() {
        return isPlayingAd() ? this.playbackInfo.periodId.adGroupIndex : -1;
    }

    public int getCurrentAdIndexInAdGroup() {
        return isPlayingAd() ? this.playbackInfo.periodId.adIndexInAdGroup : -1;
    }

    public long getContentPosition() {
        if (!isPlayingAd()) {
            return getCurrentPosition();
        }
        this.playbackInfo.timeline.getPeriodByUid(this.playbackInfo.periodId.periodUid, this.period);
        return this.period.getPositionInWindowMs() + C0555C.usToMs(this.playbackInfo.contentPositionUs);
    }

    public long getContentBufferedPosition() {
        if (shouldMaskPosition()) {
            return this.maskingWindowPositionMs;
        }
        if (this.playbackInfo.loadingMediaPeriodId.windowSequenceNumber != this.playbackInfo.periodId.windowSequenceNumber) {
            return this.playbackInfo.timeline.getWindow(getCurrentWindowIndex(), this.window).getDurationMs();
        }
        long contentBufferedPositionUs = this.playbackInfo.bufferedPositionUs;
        if (this.playbackInfo.loadingMediaPeriodId.isAd()) {
            Period loadingPeriod = this.playbackInfo.timeline.getPeriodByUid(this.playbackInfo.loadingMediaPeriodId.periodUid, this.period);
            contentBufferedPositionUs = loadingPeriod.getAdGroupTimeUs(this.playbackInfo.loadingMediaPeriodId.adGroupIndex);
            if (contentBufferedPositionUs == Long.MIN_VALUE) {
                contentBufferedPositionUs = loadingPeriod.durationUs;
            }
        }
        return periodPositionUsToWindowPositionMs(this.playbackInfo.loadingMediaPeriodId, contentBufferedPositionUs);
    }

    public int getRendererCount() {
        return this.renderers.length;
    }

    public int getRendererType(int index) {
        return this.renderers[index].getTrackType();
    }

    public TrackGroupArray getCurrentTrackGroups() {
        return this.playbackInfo.trackGroups;
    }

    public TrackSelectionArray getCurrentTrackSelections() {
        return this.playbackInfo.trackSelectorResult.selections;
    }

    public Timeline getCurrentTimeline() {
        return this.playbackInfo.timeline;
    }

    public Object getCurrentManifest() {
        return this.playbackInfo.manifest;
    }

    void handleEvent(Message msg) {
        Iterator it;
        switch (msg.what) {
            case 0:
                handlePlaybackInfo((PlaybackInfo) msg.obj, msg.arg1, msg.arg2 != -1, msg.arg2);
                return;
            case 1:
                PlaybackParameters playbackParameters = msg.obj;
                if (!this.playbackParameters.equals(playbackParameters)) {
                    this.playbackParameters = playbackParameters;
                    it = this.listeners.iterator();
                    while (it.hasNext()) {
                        ((Player$EventListener) it.next()).onPlaybackParametersChanged(playbackParameters);
                    }
                    return;
                }
                return;
            case 2:
                ExoPlaybackException playbackError = msg.obj;
                this.playbackError = playbackError;
                it = this.listeners.iterator();
                while (it.hasNext()) {
                    ((Player$EventListener) it.next()).onPlayerError(playbackError);
                }
                return;
            default:
                throw new IllegalStateException();
        }
    }

    private void handlePlaybackInfo(PlaybackInfo playbackInfo, int operationAcks, boolean positionDiscontinuity, int positionDiscontinuityReason) {
        this.pendingOperationAcks -= operationAcks;
        if (this.pendingOperationAcks == 0) {
            int timelineChangeReason;
            boolean seekProcessed;
            if (playbackInfo.startPositionUs == C0555C.TIME_UNSET) {
                playbackInfo = playbackInfo.resetToNewPosition(playbackInfo.periodId, 0, playbackInfo.contentPositionUs);
            }
            if (this.playbackInfo.timeline.isEmpty()) {
                if (!this.hasPendingPrepare) {
                    timelineChangeReason = this.hasPendingPrepare ? 0 : 2;
                    seekProcessed = this.hasPendingSeek;
                    this.hasPendingPrepare = false;
                    this.hasPendingSeek = false;
                    updatePlaybackInfo(playbackInfo, positionDiscontinuity, positionDiscontinuityReason, timelineChangeReason, seekProcessed, false);
                }
            }
            if (playbackInfo.timeline.isEmpty()) {
                this.maskingPeriodIndex = 0;
                this.maskingWindowIndex = 0;
                this.maskingWindowPositionMs = 0;
            }
            if (this.hasPendingPrepare) {
            }
            seekProcessed = this.hasPendingSeek;
            this.hasPendingPrepare = false;
            this.hasPendingSeek = false;
            updatePlaybackInfo(playbackInfo, positionDiscontinuity, positionDiscontinuityReason, timelineChangeReason, seekProcessed, false);
        }
    }

    private PlaybackInfo getResetPlaybackInfo(boolean resetPosition, boolean resetState, int playbackState) {
        ExoPlayerImpl exoPlayerImpl = this;
        long j = 0;
        if (resetPosition) {
            exoPlayerImpl.maskingWindowIndex = 0;
            exoPlayerImpl.maskingPeriodIndex = 0;
            exoPlayerImpl.maskingWindowPositionMs = 0;
        } else {
            exoPlayerImpl.maskingWindowIndex = getCurrentWindowIndex();
            exoPlayerImpl.maskingPeriodIndex = getCurrentPeriodIndex();
            exoPlayerImpl.maskingWindowPositionMs = getCurrentPosition();
        }
        MediaPeriodId mediaPeriodId = resetPosition ? exoPlayerImpl.playbackInfo.getDummyFirstMediaPeriodId(exoPlayerImpl.shuffleModeEnabled, exoPlayerImpl.window) : exoPlayerImpl.playbackInfo.periodId;
        if (!resetPosition) {
            j = exoPlayerImpl.playbackInfo.positionUs;
        }
        long startPositionUs = j;
        return new PlaybackInfo(resetState ? Timeline.EMPTY : exoPlayerImpl.playbackInfo.timeline, resetState ? null : exoPlayerImpl.playbackInfo.manifest, mediaPeriodId, startPositionUs, resetPosition ? C0555C.TIME_UNSET : exoPlayerImpl.playbackInfo.contentPositionUs, playbackState, false, resetState ? TrackGroupArray.EMPTY : exoPlayerImpl.playbackInfo.trackGroups, resetState ? exoPlayerImpl.emptyTrackSelectorResult : exoPlayerImpl.playbackInfo.trackSelectorResult, mediaPeriodId, startPositionUs, 0, startPositionUs);
    }

    private void updatePlaybackInfo(PlaybackInfo playbackInfo, boolean positionDiscontinuity, int positionDiscontinuityReason, int timelineChangeReason, boolean seekProcessed, boolean playWhenReadyChanged) {
        boolean isRunningRecursiveListenerNotification = this.pendingPlaybackInfoUpdates.isEmpty() ^ 1;
        this.pendingPlaybackInfoUpdates.addLast(new PlaybackInfoUpdate(playbackInfo, this.playbackInfo, this.listeners, this.trackSelector, positionDiscontinuity, positionDiscontinuityReason, timelineChangeReason, seekProcessed, this.playWhenReady, playWhenReadyChanged));
        this.playbackInfo = playbackInfo;
        if (!isRunningRecursiveListenerNotification) {
            while (!r0.pendingPlaybackInfoUpdates.isEmpty()) {
                ((PlaybackInfoUpdate) r0.pendingPlaybackInfoUpdates.peekFirst()).notifyListeners();
                r0.pendingPlaybackInfoUpdates.removeFirst();
            }
        }
    }

    private long periodPositionUsToWindowPositionMs(MediaPeriodId periodId, long positionUs) {
        long positionMs = C0555C.usToMs(positionUs);
        this.playbackInfo.timeline.getPeriodByUid(periodId.periodUid, this.period);
        return positionMs + this.period.getPositionInWindowMs();
    }

    private boolean shouldMaskPosition() {
        if (!this.playbackInfo.timeline.isEmpty()) {
            if (this.pendingOperationAcks <= 0) {
                return false;
            }
        }
        return true;
    }
}
