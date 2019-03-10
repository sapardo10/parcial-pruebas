package de.danoeh.antennapod.core.util.playback;

import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PlaybackController$L8zj4uoBiiJK6R-QY62x9IyOQq0 implements MaybeOnSubscribe {
    private final /* synthetic */ PlaybackController f$0;

    public /* synthetic */ -$$Lambda$PlaybackController$L8zj4uoBiiJK6R-QY62x9IyOQq0(PlaybackController playbackController) {
        this.f$0 = playbackController;
    }

    public final void subscribe(MaybeEmitter maybeEmitter) {
        PlaybackController.lambda$initServiceNotRunning$4(this.f$0, maybeEmitter);
    }
}
