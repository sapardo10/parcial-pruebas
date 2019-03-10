package de.danoeh.antennapod.fragment;

import de.danoeh.antennapod.core.feed.Feed;
import de.danoeh.antennapod.core.storage.DBWriter;
import java.util.concurrent.Callable;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SubscriptionFragment$2$59e9OUcGZu6-eWGr0GZ8XYi8zZ8 implements Callable {
    private final /* synthetic */ Feed f$0;

    public /* synthetic */ -$$Lambda$SubscriptionFragment$2$59e9OUcGZu6-eWGr0GZ8XYi8zZ8(Feed feed) {
        this.f$0 = feed;
    }

    public final Object call() {
        return DBWriter.markFeedRead(this.f$0.getId());
    }
}
