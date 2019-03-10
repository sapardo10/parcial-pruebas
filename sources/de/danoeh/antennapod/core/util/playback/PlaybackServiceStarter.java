package de.danoeh.antennapod.core.util.playback;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import de.danoeh.antennapod.core.preferences.PlaybackPreferences;
import de.danoeh.antennapod.core.service.playback.PlaybackService;

public class PlaybackServiceStarter {
    private boolean callEvenIfRunning = false;
    private final Context context;
    private final Playable media;
    private boolean prepareImmediately = true;
    private boolean shouldStream = false;
    private boolean startWhenPrepared = false;

    public PlaybackServiceStarter(Context context, Playable media) {
        this.context = context;
        this.media = media;
    }

    public PlaybackServiceStarter shouldStream(boolean shouldStream) {
        this.shouldStream = shouldStream;
        return this;
    }

    public PlaybackServiceStarter streamIfLastWasStream() {
        return shouldStream(PlaybackPreferences.getCurrentEpisodeIsStream());
    }

    public PlaybackServiceStarter startWhenPrepared(boolean startWhenPrepared) {
        this.startWhenPrepared = startWhenPrepared;
        return this;
    }

    public PlaybackServiceStarter callEvenIfRunning(boolean callEvenIfRunning) {
        this.callEvenIfRunning = callEvenIfRunning;
        return this;
    }

    public PlaybackServiceStarter prepareImmediately(boolean prepareImmediately) {
        this.prepareImmediately = prepareImmediately;
        return this;
    }

    public Intent getIntent() {
        Intent launchIntent = new Intent(this.context, PlaybackService.class);
        launchIntent.putExtra(PlaybackService.EXTRA_PLAYABLE, this.media);
        launchIntent.putExtra(PlaybackService.EXTRA_START_WHEN_PREPARED, this.startWhenPrepared);
        launchIntent.putExtra(PlaybackService.EXTRA_SHOULD_STREAM, this.shouldStream);
        launchIntent.putExtra(PlaybackService.EXTRA_PREPARE_IMMEDIATELY, this.prepareImmediately);
        return launchIntent;
    }

    public void start() {
        if (!PlaybackService.isRunning || this.callEvenIfRunning) {
            ContextCompat.startForegroundService(this.context, getIntent());
        }
    }
}
