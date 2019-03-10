package de.danoeh.antennapod.fragment;

import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ItunesSearchFragment$eG3e11TmbFYWWwAoIgEqIx3Om8Y implements SingleOnSubscribe {
    private final /* synthetic */ ItunesSearchFragment f$0;
    private final /* synthetic */ String f$1;

    public /* synthetic */ -$$Lambda$ItunesSearchFragment$eG3e11TmbFYWWwAoIgEqIx3Om8Y(ItunesSearchFragment itunesSearchFragment, String str) {
        this.f$0 = itunesSearchFragment;
        this.f$1 = str;
    }

    public final void subscribe(SingleEmitter singleEmitter) {
        ItunesSearchFragment.lambda$search$8(this.f$0, this.f$1, singleEmitter);
    }
}
