package de.danoeh.antennapod.fragment;

import de.danoeh.antennapod.fragment.SubscriptionFragment.C10711;
import io.reactivex.functions.Consumer;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SubscriptionFragment$1$4r8_TSwRtIE0teREH5ip5lKBhN8 implements Consumer {
    private final /* synthetic */ C10711 f$0;

    public /* synthetic */ -$$Lambda$SubscriptionFragment$1$4r8_TSwRtIE0teREH5ip5lKBhN8(C10711 c10711) {
        this.f$0 = c10711;
    }

    public final void accept(Object obj) {
        this.f$0.this$0.loadSubscriptions();
    }
}
