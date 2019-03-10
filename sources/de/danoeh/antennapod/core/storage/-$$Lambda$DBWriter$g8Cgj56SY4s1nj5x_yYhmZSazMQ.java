package de.danoeh.antennapod.core.storage;

import de.danoeh.antennapod.core.feed.FeedItem;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DBWriter$g8Cgj56SY4s1nj5x_yYhmZSazMQ implements Runnable {
    private final /* synthetic */ FeedItem f$0;

    public /* synthetic */ -$$Lambda$DBWriter$g8Cgj56SY4s1nj5x_yYhmZSazMQ(FeedItem feedItem) {
        this.f$0 = feedItem;
    }

    public final void run() {
        DBWriter.lambda$saveFeedItemAutoDownloadFailed$38(this.f$0);
    }
}
