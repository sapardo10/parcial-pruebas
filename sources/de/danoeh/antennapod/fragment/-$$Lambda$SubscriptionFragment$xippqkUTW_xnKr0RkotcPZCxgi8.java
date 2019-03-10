package de.danoeh.antennapod.fragment;

import de.danoeh.antennapod.core.storage.DBReader.NavDrawerData;
import io.reactivex.functions.Consumer;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SubscriptionFragment$xippqkUTW_xnKr0RkotcPZCxgi8 implements Consumer {
    private final /* synthetic */ SubscriptionFragment f$0;

    public /* synthetic */ -$$Lambda$SubscriptionFragment$xippqkUTW_xnKr0RkotcPZCxgi8(SubscriptionFragment subscriptionFragment) {
        this.f$0 = subscriptionFragment;
    }

    public final void accept(Object obj) {
        SubscriptionFragment.lambda$loadSubscriptions$0(this.f$0, (NavDrawerData) obj);
    }
}
