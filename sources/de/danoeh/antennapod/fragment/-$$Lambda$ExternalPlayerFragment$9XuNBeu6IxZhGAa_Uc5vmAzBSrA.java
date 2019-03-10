package de.danoeh.antennapod.fragment;

import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ExternalPlayerFragment$9XuNBeu6IxZhGAa_Uc5vmAzBSrA implements MaybeOnSubscribe {
    private final /* synthetic */ ExternalPlayerFragment f$0;

    public /* synthetic */ -$$Lambda$ExternalPlayerFragment$9XuNBeu6IxZhGAa_Uc5vmAzBSrA(ExternalPlayerFragment externalPlayerFragment) {
        this.f$0 = externalPlayerFragment;
    }

    public final void subscribe(MaybeEmitter maybeEmitter) {
        ExternalPlayerFragment.lambda$loadMediaInfo$3(this.f$0, maybeEmitter);
    }
}
