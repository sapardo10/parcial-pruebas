package com.google.android.exoplayer2;

import android.support.annotation.Nullable;
import com.google.android.exoplayer2.util.Clock;
import com.google.android.exoplayer2.util.MediaClock;
import com.google.android.exoplayer2.util.StandaloneMediaClock;

final class DefaultMediaClock implements MediaClock {
    private final PlaybackParameterListener listener;
    @Nullable
    private MediaClock rendererClock;
    @Nullable
    private Renderer rendererClockSource;
    private final StandaloneMediaClock standaloneMediaClock;

    public interface PlaybackParameterListener {
        void onPlaybackParametersChanged(PlaybackParameters playbackParameters);
    }

    public DefaultMediaClock(PlaybackParameterListener listener, Clock clock) {
        this.listener = listener;
        this.standaloneMediaClock = new StandaloneMediaClock(clock);
    }

    public void start() {
        this.standaloneMediaClock.start();
    }

    public void stop() {
        this.standaloneMediaClock.stop();
    }

    public void resetPosition(long positionUs) {
        this.standaloneMediaClock.resetPosition(positionUs);
    }

    public void onRendererEnabled(Renderer renderer) throws ExoPlaybackException {
        MediaClock rendererMediaClock = renderer.getMediaClock();
        if (rendererMediaClock != null) {
            MediaClock mediaClock = this.rendererClock;
            if (rendererMediaClock != mediaClock) {
                if (mediaClock == null) {
                    this.rendererClock = rendererMediaClock;
                    this.rendererClockSource = renderer;
                    this.rendererClock.setPlaybackParameters(this.standaloneMediaClock.getPlaybackParameters());
                    ensureSynced();
                    return;
                }
                throw ExoPlaybackException.createForUnexpected(new IllegalStateException("Multiple renderer media clocks enabled."));
            }
        }
    }

    public void onRendererDisabled(Renderer renderer) {
        if (renderer == this.rendererClockSource) {
            this.rendererClock = null;
            this.rendererClockSource = null;
        }
    }

    public long syncAndGetPositionUs() {
        if (!isUsingRendererClock()) {
            return this.standaloneMediaClock.getPositionUs();
        }
        ensureSynced();
        return this.rendererClock.getPositionUs();
    }

    public long getPositionUs() {
        if (isUsingRendererClock()) {
            return this.rendererClock.getPositionUs();
        }
        return this.standaloneMediaClock.getPositionUs();
    }

    public PlaybackParameters setPlaybackParameters(PlaybackParameters playbackParameters) {
        MediaClock mediaClock = this.rendererClock;
        if (mediaClock != null) {
            playbackParameters = mediaClock.setPlaybackParameters(playbackParameters);
        }
        this.standaloneMediaClock.setPlaybackParameters(playbackParameters);
        this.listener.onPlaybackParametersChanged(playbackParameters);
        return playbackParameters;
    }

    public PlaybackParameters getPlaybackParameters() {
        MediaClock mediaClock = this.rendererClock;
        if (mediaClock != null) {
            return mediaClock.getPlaybackParameters();
        }
        return this.standaloneMediaClock.getPlaybackParameters();
    }

    private void ensureSynced() {
        this.standaloneMediaClock.resetPosition(this.rendererClock.getPositionUs());
        PlaybackParameters playbackParameters = this.rendererClock.getPlaybackParameters();
        if (!playbackParameters.equals(this.standaloneMediaClock.getPlaybackParameters())) {
            this.standaloneMediaClock.setPlaybackParameters(playbackParameters);
            this.listener.onPlaybackParametersChanged(playbackParameters);
        }
    }

    private boolean isUsingRendererClock() {
        Renderer renderer = this.rendererClockSource;
        if (renderer != null && !renderer.isEnded()) {
            if (!this.rendererClockSource.isReady()) {
                if (this.rendererClockSource.hasReadStreamToEnd()) {
                }
            }
            return true;
        }
        return false;
    }
}
