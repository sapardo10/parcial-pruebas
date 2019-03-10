package de.danoeh.antennapod.fragment;

import de.danoeh.antennapod.adapter.itunes.ItunesAdapter.Podcast;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ItunesSearchFragment$8SKiker3uTBCeDwaX4bzXtWDgUc implements SingleOnSubscribe {
    private final /* synthetic */ ItunesSearchFragment f$0;
    private final /* synthetic */ Podcast f$1;

    public /* synthetic */ -$$Lambda$ItunesSearchFragment$8SKiker3uTBCeDwaX4bzXtWDgUc(ItunesSearchFragment itunesSearchFragment, Podcast podcast) {
        this.f$0 = itunesSearchFragment;
        this.f$1 = podcast;
    }

    public final void subscribe(SingleEmitter singleEmitter) {
        ItunesSearchFragment.lambda$null$0(this.f$0, this.f$1, singleEmitter);
    }
}
