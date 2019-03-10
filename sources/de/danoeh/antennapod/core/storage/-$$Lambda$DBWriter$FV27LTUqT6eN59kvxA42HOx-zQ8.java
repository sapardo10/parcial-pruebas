package de.danoeh.antennapod.core.storage;

import de.danoeh.antennapod.core.feed.FeedItem;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DBWriter$FV27LTUqT6eN59kvxA42HOx-zQ8 implements Runnable {
    private final /* synthetic */ FeedItem f$0;

    public /* synthetic */ -$$Lambda$DBWriter$FV27LTUqT6eN59kvxA42HOx-zQ8(FeedItem feedItem) {
        this.f$0 = feedItem;
    }

    public final void run() {
        DBWriter.lambda$setFeedItem$26(this.f$0);
    }
}
