package de.danoeh.antennapod.core.service.playback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$fGp36j-wgbN6mIRD6IFVkCdiAKY implements Runnable {
    private final /* synthetic */ PlaybackService f$0;

    public /* synthetic */ -$$Lambda$fGp36j-wgbN6mIRD6IFVkCdiAKY(PlaybackService playbackService) {
        this.f$0 = playbackService;
    }

    public final void run() {
        this.f$0.disableSleepTimer();
    }
}
