package de.danoeh.antennapod.fragment;

import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ItunesSearchFragment$cpqkFax07yhvoIxgFRFUjvetwkw implements SingleOnSubscribe {
    private final /* synthetic */ ItunesSearchFragment f$0;

    public /* synthetic */ -$$Lambda$ItunesSearchFragment$cpqkFax07yhvoIxgFRFUjvetwkw(ItunesSearchFragment itunesSearchFragment) {
        this.f$0 = itunesSearchFragment;
    }

    public final void subscribe(SingleEmitter singleEmitter) {
        ItunesSearchFragment.lambda$loadToplist$4(this.f$0, singleEmitter);
    }
}
