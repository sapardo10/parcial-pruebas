package de.danoeh.antennapod.fragment;

import de.danoeh.antennapod.core.feed.Feed;
import io.reactivex.functions.Consumer;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ItemlistFragment$cMOdqE0qnBc4HsZL93cFVeJU6CE implements Consumer {
    private final /* synthetic */ ItemlistFragment f$0;

    public /* synthetic */ -$$Lambda$ItemlistFragment$cMOdqE0qnBc4HsZL93cFVeJU6CE(ItemlistFragment itemlistFragment) {
        this.f$0 = itemlistFragment;
    }

    public final void accept(Object obj) {
        ItemlistFragment.lambda$loadItems$4(this.f$0, (Feed) obj);
    }
}
