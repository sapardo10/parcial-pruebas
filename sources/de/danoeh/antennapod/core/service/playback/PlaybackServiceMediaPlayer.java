package de.danoeh.antennapod.core.service.playback;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.util.Log;
import android.util.Pair;
import android.view.SurfaceHolder;
import de.danoeh.antennapod.core.feed.MediaType;
import de.danoeh.antennapod.core.util.playback.Playable;
import java.util.concurrent.Future;

public abstract class PlaybackServiceMediaPlayer {
    static final int INVALID_TIME = -1;
    private static final String TAG = "PlaybackSvcMediaPlayer";
    final PSMPCallback callback;
    final Context context;
    private volatile PlayerStatus oldPlayerStatus;
    volatile PlayerStatus playerStatus = PlayerStatus.STOPPED;
    private WifiLock wifiLock;

    public interface PSMPCallback {
        Playable getNextInQueue(Playable playable);

        void onBufferingUpdate(int i);

        void onMediaChanged(boolean z);

        boolean onMediaPlayerError(Object obj, int i, int i2);

        boolean onMediaPlayerInfo(int i, @StringRes int i2);

        void onPlaybackEnded(MediaType mediaType, boolean z);

        void onPlaybackPause(Playable playable, int i);

        void onPlaybackStart(@NonNull Playable playable, int i);

        void onPostPlayback(@NonNull Playable playable, boolean z, boolean z2, boolean z3);

        void playbackSpeedChanged(float f);

        void setSpeedAbilityChanged();

        void shouldStop();

        void statusChanged(PSMPInfo pSMPInfo);
    }

    public static class PSMPInfo {
        public final PlayerStatus oldPlayerStatus;
        public Playable playable;
        public PlayerStatus playerStatus;

        PSMPInfo(PlayerStatus oldPlayerStatus, PlayerStatus playerStatus, Playable playable) {
            this.oldPlayerStatus = oldPlayerStatus;
            this.playerStatus = playerStatus;
            this.playable = playable;
        }
    }

    public abstract boolean canDownmix();

    public abstract boolean canSetSpeed();

    protected abstract Future<?> endPlayback(boolean z, boolean z2, boolean z3, boolean z4);

    public abstract MediaType getCurrentMediaType();

    public abstract int getDuration();

    public abstract Playable getPlayable();

    public abstract float getPlaybackSpeed();

    public abstract int getPosition();

    public abstract Pair<Integer, Integer> getVideoSize();

    public abstract boolean isStartWhenPrepared();

    public abstract boolean isStreaming();

    public abstract void pause(boolean z, boolean z2);

    public abstract void playMediaObject(@NonNull Playable playable, boolean z, boolean z2, boolean z3);

    public abstract void prepare();

    public abstract void reinit();

    public abstract void resetVideoSurface();

    public abstract void resume();

    public abstract void seekDelta(int i);

    public abstract void seekTo(int i);

    public abstract void setDownmix(boolean z);

    protected abstract void setPlayable(Playable playable);

    public abstract void setPlaybackParams(float f, boolean z);

    public abstract void setStartWhenPrepared(boolean z);

    public abstract void setVideoSurface(SurfaceHolder surfaceHolder);

    public abstract void setVolume(float f, float f2);

    protected abstract boolean shouldLockWifi();

    public abstract void shutdown();

    public abstract void shutdownQuietly();

    PlaybackServiceMediaPlayer(@NonNull Context context, @NonNull PSMPCallback callback) {
        this.context = context;
        this.callback = callback;
    }

    public final synchronized PSMPInfo getPSMPInfo() {
        return new PSMPInfo(this.oldPlayerStatus, this.playerStatus, getPlayable());
    }

    public PlayerStatus getPlayerStatus() {
        return this.playerStatus;
    }

    public void skip() {
        endPlayback(false, true, true, true);
    }

    public Future<?> stopPlayback(boolean toStoppedState) {
        return endPlayback(false, false, false, toStoppedState);
    }

    final synchronized void acquireWifiLockIfNecessary() {
        if (shouldLockWifi()) {
            if (this.wifiLock == null) {
                this.wifiLock = ((WifiManager) this.context.getApplicationContext().getSystemService("wifi")).createWifiLock(1, TAG);
                this.wifiLock.setReferenceCounted(false);
            }
            this.wifiLock.acquire();
        }
    }

    final synchronized void releaseWifiLockIfNecessary() {
        if (this.wifiLock != null && this.wifiLock.isHeld()) {
            this.wifiLock.release();
        }
    }

    final synchronized void setPlayerStatus(@NonNull PlayerStatus newStatus, Playable newMedia, int position) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getClass().getSimpleName());
        stringBuilder.append(": Setting player status to ");
        stringBuilder.append(newStatus);
        Log.d(str, stringBuilder.toString());
        this.oldPlayerStatus = this.playerStatus;
        this.playerStatus = newStatus;
        setPlayable(newMedia);
        if (newMedia != null && newStatus != PlayerStatus.INDETERMINATE) {
            if (this.oldPlayerStatus == PlayerStatus.PLAYING && newStatus != PlayerStatus.PLAYING) {
                this.callback.onPlaybackPause(newMedia, position);
            } else if (this.oldPlayerStatus != PlayerStatus.PLAYING && newStatus == PlayerStatus.PLAYING) {
                this.callback.onPlaybackStart(newMedia, position);
            }
        }
        this.callback.statusChanged(new PSMPInfo(this.oldPlayerStatus, this.playerStatus, getPlayable()));
    }

    final void setPlayerStatus(@NonNull PlayerStatus newStatus, Playable newMedia) {
        setPlayerStatus(newStatus, newMedia, -1);
    }
}
