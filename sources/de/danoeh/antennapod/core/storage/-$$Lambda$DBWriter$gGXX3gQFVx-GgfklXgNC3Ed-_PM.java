package de.danoeh.antennapod.core.storage;

import de.danoeh.antennapod.core.feed.FeedItem;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DBWriter$gGXX3gQFVx-GgfklXgNC3Ed-_PM implements Runnable {
    private final /* synthetic */ FeedItem f$0;

    public /* synthetic */ -$$Lambda$DBWriter$gGXX3gQFVx-GgfklXgNC3Ed-_PM(FeedItem feedItem) {
        this.f$0 = feedItem;
    }

    public final void run() {
        DBWriter.lambda$removeFavoriteItem$12(this.f$0);
    }
}
