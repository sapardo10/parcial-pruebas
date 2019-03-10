package com.google.android.exoplayer2.ui;

import android.annotation.SuppressLint;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.os.EnvironmentCompat;
import android.widget.TextView;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player$EventListener;
import com.google.android.exoplayer2.Player$EventListener.-CC;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.util.Assertions;
import java.util.Locale;

public class DebugTextViewHelper implements Player$EventListener, Runnable {
    private static final int REFRESH_INTERVAL_MS = 1000;
    private final SimpleExoPlayer player;
    private boolean started;
    private final TextView textView;

    public /* synthetic */ void onLoadingChanged(boolean z) {
        -CC.$default$onLoadingChanged(this, z);
    }

    public /* synthetic */ void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
        -CC.$default$onPlaybackParametersChanged(this, playbackParameters);
    }

    public /* synthetic */ void onPlayerError(ExoPlaybackException exoPlaybackException) {
        -CC.$default$onPlayerError(this, exoPlaybackException);
    }

    public /* synthetic */ void onRepeatModeChanged(int i) {
        -CC.$default$onRepeatModeChanged(this, i);
    }

    public /* synthetic */ void onSeekProcessed() {
        -CC.$default$onSeekProcessed(this);
    }

    public /* synthetic */ void onShuffleModeEnabledChanged(boolean z) {
        -CC.$default$onShuffleModeEnabledChanged(this, z);
    }

    public /* synthetic */ void onTimelineChanged(Timeline timeline, @Nullable Object obj, int i) {
        -CC.$default$onTimelineChanged(this, timeline, obj, i);
    }

    public /* synthetic */ void onTracksChanged(TrackGroupArray trackGroupArray, TrackSelectionArray trackSelectionArray) {
        -CC.$default$onTracksChanged(this, trackGroupArray, trackSelectionArray);
    }

    public DebugTextViewHelper(SimpleExoPlayer player, TextView textView) {
        Assertions.checkArgument(player.getApplicationLooper() == Looper.getMainLooper());
        this.player = player;
        this.textView = textView;
    }

    public final void start() {
        if (!this.started) {
            this.started = true;
            this.player.addListener(this);
            updateAndPost();
        }
    }

    public final void stop() {
        if (this.started) {
            this.started = false;
            this.player.removeListener(this);
            this.textView.removeCallbacks(this);
        }
    }

    public final void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        updateAndPost();
    }

    public final void onPositionDiscontinuity(int reason) {
        updateAndPost();
    }

    public final void run() {
        updateAndPost();
    }

    @SuppressLint({"SetTextI18n"})
    protected final void updateAndPost() {
        this.textView.setText(getDebugString());
        this.textView.removeCallbacks(this);
        this.textView.postDelayed(this, 1000);
    }

    protected String getDebugString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getPlayerStateString());
        stringBuilder.append(getVideoString());
        stringBuilder.append(getAudioString());
        return stringBuilder.toString();
    }

    protected String getPlayerStateString() {
        String playbackStateString;
        switch (this.player.getPlaybackState()) {
            case 1:
                playbackStateString = "idle";
                break;
            case 2:
                playbackStateString = "buffering";
                break;
            case 3:
                playbackStateString = "ready";
                break;
            case 4:
                playbackStateString = "ended";
                break;
            default:
                playbackStateString = EnvironmentCompat.MEDIA_UNKNOWN;
                break;
        }
        return String.format("playWhenReady:%s playbackState:%s window:%s", new Object[]{Boolean.valueOf(this.player.getPlayWhenReady()), playbackStateString, Integer.valueOf(this.player.getCurrentWindowIndex())});
    }

    protected String getVideoString() {
        Format format = this.player.getVideoFormat();
        if (format == null) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n");
        stringBuilder.append(format.sampleMimeType);
        stringBuilder.append("(id:");
        stringBuilder.append(format.id);
        stringBuilder.append(" r:");
        stringBuilder.append(format.width);
        stringBuilder.append("x");
        stringBuilder.append(format.height);
        stringBuilder.append(getPixelAspectRatioString(format.pixelWidthHeightRatio));
        stringBuilder.append(getDecoderCountersBufferCountString(this.player.getVideoDecoderCounters()));
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    protected String getAudioString() {
        Format format = this.player.getAudioFormat();
        if (format == null) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n");
        stringBuilder.append(format.sampleMimeType);
        stringBuilder.append("(id:");
        stringBuilder.append(format.id);
        stringBuilder.append(" hz:");
        stringBuilder.append(format.sampleRate);
        stringBuilder.append(" ch:");
        stringBuilder.append(format.channelCount);
        stringBuilder.append(getDecoderCountersBufferCountString(this.player.getAudioDecoderCounters()));
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    private static String getDecoderCountersBufferCountString(DecoderCounters counters) {
        if (counters == null) {
            return "";
        }
        counters.ensureUpdated();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" sib:");
        stringBuilder.append(counters.skippedInputBufferCount);
        stringBuilder.append(" sb:");
        stringBuilder.append(counters.skippedOutputBufferCount);
        stringBuilder.append(" rb:");
        stringBuilder.append(counters.renderedOutputBufferCount);
        stringBuilder.append(" db:");
        stringBuilder.append(counters.droppedBufferCount);
        stringBuilder.append(" mcdb:");
        stringBuilder.append(counters.maxConsecutiveDroppedBufferCount);
        stringBuilder.append(" dk:");
        stringBuilder.append(counters.droppedToKeyframeCount);
        return stringBuilder.toString();
    }

    private static String getPixelAspectRatioString(float pixelAspectRatio) {
        String stringBuilder;
        if (pixelAspectRatio != -1.0f) {
            if (pixelAspectRatio != 1.0f) {
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append(" par:");
                stringBuilder2.append(String.format(Locale.US, "%.02f", new Object[]{Float.valueOf(pixelAspectRatio)}));
                stringBuilder = stringBuilder2.toString();
                return stringBuilder;
            }
        }
        stringBuilder = "";
        return stringBuilder;
    }
}
