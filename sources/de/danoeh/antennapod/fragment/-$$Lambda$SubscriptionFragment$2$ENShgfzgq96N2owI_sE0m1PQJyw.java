package de.danoeh.antennapod.fragment;

import de.danoeh.antennapod.fragment.SubscriptionFragment.C10722;
import io.reactivex.functions.Consumer;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SubscriptionFragment$2$ENShgfzgq96N2owI_sE0m1PQJyw implements Consumer {
    private final /* synthetic */ C10722 f$0;

    public /* synthetic */ -$$Lambda$SubscriptionFragment$2$ENShgfzgq96N2owI_sE0m1PQJyw(C10722 c10722) {
        this.f$0 = c10722;
    }

    public final void accept(Object obj) {
        this.f$0.this$0.loadSubscriptions();
    }
}
