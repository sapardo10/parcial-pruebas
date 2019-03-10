package com.google.android.exoplayer2.ui;

import android.support.annotation.Nullable;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player$EventListener;
import com.google.android.exoplayer2.Player$EventListener.-CC;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;

class PlayerNotificationManager$PlayerListener implements Player$EventListener {
    final /* synthetic */ PlayerNotificationManager this$0;

    public /* synthetic */ void onLoadingChanged(boolean z) {
        -CC.$default$onLoadingChanged(this, z);
    }

    public /* synthetic */ void onPlayerError(ExoPlaybackException exoPlaybackException) {
        -CC.$default$onPlayerError(this, exoPlaybackException);
    }

    public /* synthetic */ void onSeekProcessed() {
        -CC.$default$onSeekProcessed(this);
    }

    public /* synthetic */ void onShuffleModeEnabledChanged(boolean z) {
        -CC.$default$onShuffleModeEnabledChanged(this, z);
    }

    public /* synthetic */ void onTracksChanged(TrackGroupArray trackGroupArray, TrackSelectionArray trackSelectionArray) {
        -CC.$default$onTracksChanged(this, trackGroupArray, trackSelectionArray);
    }

    private PlayerNotificationManager$PlayerListener(PlayerNotificationManager playerNotificationManager) {
        this.this$0 = playerNotificationManager;
    }

    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (PlayerNotificationManager.access$700(this.this$0) != playWhenReady) {
            if (playbackState != 1) {
                PlayerNotificationManager.access$900(this.this$0);
                PlayerNotificationManager.access$702(this.this$0, playWhenReady);
                PlayerNotificationManager.access$802(this.this$0, playbackState);
            }
        }
        if (PlayerNotificationManager.access$800(this.this$0) == playbackState) {
            PlayerNotificationManager.access$702(this.this$0, playWhenReady);
            PlayerNotificationManager.access$802(this.this$0, playbackState);
        }
        PlayerNotificationManager.access$900(this.this$0);
        PlayerNotificationManager.access$702(this.this$0, playWhenReady);
        PlayerNotificationManager.access$802(this.this$0, playbackState);
    }

    public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {
        if (PlayerNotificationManager.access$100(this.this$0) != null) {
            if (PlayerNotificationManager.access$100(this.this$0).getPlaybackState() != 1) {
                PlayerNotificationManager.access$900(this.this$0);
            }
        }
    }

    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
        if (PlayerNotificationManager.access$100(this.this$0) != null) {
            if (PlayerNotificationManager.access$100(this.this$0).getPlaybackState() != 1) {
                PlayerNotificationManager.access$900(this.this$0);
            }
        }
    }

    public void onPositionDiscontinuity(int reason) {
        PlayerNotificationManager.access$900(this.this$0);
    }

    public void onRepeatModeChanged(int repeatMode) {
        if (PlayerNotificationManager.access$100(this.this$0) != null) {
            if (PlayerNotificationManager.access$100(this.this$0).getPlaybackState() != 1) {
                PlayerNotificationManager.access$900(this.this$0);
            }
        }
    }
}
