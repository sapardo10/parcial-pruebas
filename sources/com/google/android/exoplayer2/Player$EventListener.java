package com.google.android.exoplayer2;

import android.support.annotation.Nullable;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;

public interface Player$EventListener {

    /* renamed from: com.google.android.exoplayer2.Player$EventListener$-CC */
    public final /* synthetic */ class -CC {
        public static void $default$onTimelineChanged(Player$EventListener -this, @Nullable Timeline timeline, Object manifest, int reason) {
        }

        public static void $default$onTracksChanged(Player$EventListener -this, TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        }

        public static void $default$onLoadingChanged(Player$EventListener -this, boolean isLoading) {
        }

        public static void $default$onPlayerStateChanged(Player$EventListener -this, boolean playWhenReady, int playbackState) {
        }

        public static void $default$onRepeatModeChanged(Player$EventListener -this, int repeatMode) {
        }

        public static void $default$onShuffleModeEnabledChanged(Player$EventListener -this, boolean shuffleModeEnabled) {
        }

        public static void $default$onPlayerError(Player$EventListener -this, ExoPlaybackException error) {
        }

        public static void $default$onPositionDiscontinuity(Player$EventListener -this, int reason) {
        }

        public static void $default$onPlaybackParametersChanged(Player$EventListener -this, PlaybackParameters playbackParameters) {
        }

        public static void $default$onSeekProcessed(Player$EventListener -this) {
        }
    }

    void onLoadingChanged(boolean z);

    void onPlaybackParametersChanged(PlaybackParameters playbackParameters);

    void onPlayerError(ExoPlaybackException exoPlaybackException);

    void onPlayerStateChanged(boolean z, int i);

    void onPositionDiscontinuity(int i);

    void onRepeatModeChanged(int i);

    void onSeekProcessed();

    void onShuffleModeEnabledChanged(boolean z);

    void onTimelineChanged(Timeline timeline, @Nullable Object obj, int i);

    void onTracksChanged(TrackGroupArray trackGroupArray, TrackSelectionArray trackSelectionArray);
}
