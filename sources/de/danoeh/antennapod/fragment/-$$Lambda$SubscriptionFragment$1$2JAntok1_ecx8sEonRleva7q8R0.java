package de.danoeh.antennapod.fragment;

import de.danoeh.antennapod.core.feed.Feed;
import de.danoeh.antennapod.core.storage.DBWriter;
import java.util.concurrent.Callable;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SubscriptionFragment$1$2JAntok1_ecx8sEonRleva7q8R0 implements Callable {
    private final /* synthetic */ Feed f$0;

    public /* synthetic */ -$$Lambda$SubscriptionFragment$1$2JAntok1_ecx8sEonRleva7q8R0(Feed feed) {
        this.f$0 = feed;
    }

    public final Object call() {
        return DBWriter.markFeedSeen(this.f$0.getId());
    }
}
