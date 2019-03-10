package de.danoeh.antennapod.core.util.playback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$wq-FOi-2ySG-7Qedk62-EKlrTkk implements Runnable {
    private final /* synthetic */ PlaybackController f$0;

    public /* synthetic */ -$$Lambda$wq-FOi-2ySG-7Qedk62-EKlrTkk(PlaybackController playbackController) {
        this.f$0 = playbackController;
    }

    public final void run() {
        this.f$0.onPositionObserverUpdate();
    }
}
