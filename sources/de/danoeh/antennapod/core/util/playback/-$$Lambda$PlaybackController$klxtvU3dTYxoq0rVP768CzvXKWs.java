package de.danoeh.antennapod.core.util.playback;

import java.util.concurrent.Callable;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PlaybackController$klxtvU3dTYxoq0rVP768CzvXKWs implements Callable {
    private final /* synthetic */ PlaybackController f$0;

    public /* synthetic */ -$$Lambda$PlaybackController$klxtvU3dTYxoq0rVP768CzvXKWs(PlaybackController playbackController) {
        this.f$0 = playbackController;
    }

    public final Object call() {
        return this.f$0.getPlayLastPlayedMediaIntent();
    }
}
