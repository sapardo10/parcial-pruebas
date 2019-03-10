package de.danoeh.antennapod.fragment;

import de.danoeh.antennapod.core.feed.FeedItem;
import io.reactivex.functions.Consumer;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ItemFragment$Dt1yjnZ_FSGw3i0jmjhW0Q-RHIU implements Consumer {
    private final /* synthetic */ ItemFragment f$0;

    public /* synthetic */ -$$Lambda$ItemFragment$Dt1yjnZ_FSGw3i0jmjhW0Q-RHIU(ItemFragment itemFragment) {
        this.f$0 = itemFragment;
    }

    public final void accept(Object obj) {
        ItemFragment.lambda$load$6(this.f$0, (FeedItem) obj);
    }
}
