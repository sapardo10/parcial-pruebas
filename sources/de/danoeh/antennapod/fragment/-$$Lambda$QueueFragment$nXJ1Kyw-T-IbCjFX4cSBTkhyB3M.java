package de.danoeh.antennapod.fragment;

import io.reactivex.functions.Consumer;
import java.util.List;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$QueueFragment$nXJ1Kyw-T-IbCjFX4cSBTkhyB3M implements Consumer {
    private final /* synthetic */ QueueFragment f$0;
    private final /* synthetic */ boolean f$1;

    public /* synthetic */ -$$Lambda$QueueFragment$nXJ1Kyw-T-IbCjFX4cSBTkhyB3M(QueueFragment queueFragment, boolean z) {
        this.f$0 = queueFragment;
        this.f$1 = z;
    }

    public final void accept(Object obj) {
        QueueFragment.lambda$loadItems$1(this.f$0, this.f$1, (List) obj);
    }
}
