package de.danoeh.antennapod.core.storage;

import de.danoeh.antennapod.core.feed.Feed;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DBWriter$VIxkeDkv4nuCHzPOkGY8KHiNppI implements Runnable {
    private final /* synthetic */ Feed f$0;

    public /* synthetic */ -$$Lambda$DBWriter$VIxkeDkv4nuCHzPOkGY8KHiNppI(Feed feed) {
        this.f$0 = feed;
    }

    public final void run() {
        DBWriter.lambda$setFeedCustomTitle$32(this.f$0);
    }
}
