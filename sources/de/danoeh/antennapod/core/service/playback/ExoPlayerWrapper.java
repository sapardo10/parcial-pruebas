package de.danoeh.antennapod.core.service.playback;

import android.content.Context;
import android.net.Uri;
import android.view.SurfaceHolder;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player$EventListener;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.audio.AudioAttributes.Builder;
import com.google.android.exoplayer2.source.ExtractorMediaSource.Factory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import de.danoeh.antennapod.core.util.playback.IPlayer;
import org.antennapod.audio.MediaPlayer.OnCompletionListener;
import org.antennapod.audio.MediaPlayer.OnErrorListener;
import org.antennapod.audio.MediaPlayer.OnSeekCompleteListener;

public class ExoPlayerWrapper implements IPlayer {
    private OnCompletionListener audioCompletionListener;
    private OnErrorListener audioErrorListener;
    private OnSeekCompleteListener audioSeekCompleteListener;
    private final Context mContext;
    private SimpleExoPlayer mExoPlayer = createPlayer();
    private MediaSource mediaSource;

    /* renamed from: de.danoeh.antennapod.core.service.playback.ExoPlayerWrapper$1 */
    class C10271 implements Player$EventListener {
        C10271() {
        }

        public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
        }

        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        }

        public void onLoadingChanged(boolean isLoading) {
        }

        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            if (playbackState == 4) {
                ExoPlayerWrapper.this.audioCompletionListener.onCompletion(null);
            }
        }

        public void onRepeatModeChanged(int repeatMode) {
        }

        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
        }

        public void onPlayerError(ExoPlaybackException error) {
            if (ExoPlayerWrapper.this.audioErrorListener != null) {
                ExoPlayerWrapper.this.audioErrorListener.onError(null, 0, 0);
            }
        }

        public void onPositionDiscontinuity(int reason) {
        }

        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
        }

        public void onSeekProcessed() {
            ExoPlayerWrapper.this.audioSeekCompleteListener.onSeekComplete(null);
        }
    }

    ExoPlayerWrapper(Context context) {
        this.mContext = context;
    }

    private SimpleExoPlayer createPlayer() {
        Context p = this.mContext;
        SimpleExoPlayer p2 = ExoPlayerFactory.newSimpleInstance(p, new DefaultRenderersFactory(p), new DefaultTrackSelector(), new DefaultLoadControl());
        p2.setSeekParameters(SeekParameters.PREVIOUS_SYNC);
        p2.addListener(new C10271());
        return p2;
    }

    public boolean canSetSpeed() {
        return true;
    }

    public boolean canDownmix() {
        return false;
    }

    public int getCurrentPosition() {
        return (int) this.mExoPlayer.getCurrentPosition();
    }

    public float getCurrentSpeedMultiplier() {
        return this.mExoPlayer.getPlaybackParameters().speed;
    }

    public int getDuration() {
        if (this.mExoPlayer.getDuration() == C0555C.TIME_UNSET) {
            return -1;
        }
        return (int) this.mExoPlayer.getDuration();
    }

    public boolean isPlaying() {
        return this.mExoPlayer.getPlayWhenReady();
    }

    public void pause() {
        this.mExoPlayer.setPlayWhenReady(false);
    }

    public void prepare() throws IllegalStateException {
        this.mExoPlayer.prepare(this.mediaSource);
    }

    public void release() {
        SimpleExoPlayer simpleExoPlayer = this.mExoPlayer;
        if (simpleExoPlayer != null) {
            simpleExoPlayer.release();
        }
        this.audioSeekCompleteListener = null;
        this.audioCompletionListener = null;
        this.audioErrorListener = null;
    }

    public void reset() {
        this.mExoPlayer.release();
        this.mExoPlayer = createPlayer();
    }

    public void seekTo(int i) throws IllegalStateException {
        this.mExoPlayer.seekTo((long) i);
    }

    public void setAudioStreamType(int i) {
        AudioAttributes a = this.mExoPlayer.getAudioAttributes();
        Builder b = new Builder();
        b.setContentType(i);
        b.setFlags(a.flags);
        b.setUsage(a.usage);
        this.mExoPlayer.setAudioAttributes(b.build());
    }

    public void setDataSource(String s) throws IllegalArgumentException, IllegalStateException {
        Context context = this.mContext;
        this.mediaSource = new Factory(new DefaultDataSourceFactory(context, Util.getUserAgent(context, context.getPackageName()), null)).createMediaSource(Uri.parse(s));
    }

    public void setDisplay(SurfaceHolder sh) {
        this.mExoPlayer.setVideoSurfaceHolder(sh);
    }

    public void setPlaybackParams(float speed, boolean skipSilence) {
        this.mExoPlayer.setPlaybackParameters(new PlaybackParameters(speed, this.mExoPlayer.getPlaybackParameters().pitch, skipSilence));
    }

    public void setDownmix(boolean b) {
    }

    public void setVolume(float v, float v1) {
        this.mExoPlayer.setVolume(v);
    }

    public void setWakeMode(Context context, int i) {
    }

    public void start() {
        this.mExoPlayer.setPlayWhenReady(true);
    }

    public void stop() {
        this.mExoPlayer.stop();
    }

    void setOnCompletionListener(OnCompletionListener audioCompletionListener) {
        this.audioCompletionListener = audioCompletionListener;
    }

    void setOnSeekCompleteListener(OnSeekCompleteListener audioSeekCompleteListener) {
        this.audioSeekCompleteListener = audioSeekCompleteListener;
    }

    void setOnErrorListener(OnErrorListener audioErrorListener) {
        this.audioErrorListener = audioErrorListener;
    }

    int getVideoWidth() {
        if (this.mExoPlayer.getVideoFormat() == null) {
            return 0;
        }
        return this.mExoPlayer.getVideoFormat().width;
    }

    int getVideoHeight() {
        if (this.mExoPlayer.getVideoFormat() == null) {
            return 0;
        }
        return this.mExoPlayer.getVideoFormat().height;
    }
}
