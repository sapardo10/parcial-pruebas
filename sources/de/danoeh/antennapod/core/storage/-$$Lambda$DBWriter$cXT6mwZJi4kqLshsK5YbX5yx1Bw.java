package de.danoeh.antennapod.core.storage;

import de.danoeh.antennapod.core.feed.FeedItem;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DBWriter$cXT6mwZJi4kqLshsK5YbX5yx1Bw implements Runnable {
    private final /* synthetic */ FeedItem f$0;
    private final /* synthetic */ boolean f$1;

    public /* synthetic */ -$$Lambda$DBWriter$cXT6mwZJi4kqLshsK5YbX5yx1Bw(FeedItem feedItem, boolean z) {
        this.f$0 = feedItem;
        this.f$1 = z;
    }

    public final void run() {
        DBWriter.lambda$setFeedItemAutoDownload$37(this.f$0, this.f$1);
    }
}
