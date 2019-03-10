package de.danoeh.antennapod.core.util.playback;

import io.reactivex.functions.Consumer;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PlaybackController$unPf-KZ3N6z90Zv3wDvGi-e8ZiQ implements Consumer {
    private final /* synthetic */ PlaybackController f$0;

    public /* synthetic */ -$$Lambda$PlaybackController$unPf-KZ3N6z90Zv3wDvGi-e8ZiQ(PlaybackController playbackController) {
        this.f$0 = playbackController;
    }

    public final void accept(Object obj) {
        PlaybackController.lambda$initServiceNotRunning$5(this.f$0, (Playable) obj);
    }
}
