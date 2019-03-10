package de.danoeh.antennapod.core.storage;

import de.danoeh.antennapod.core.feed.FeedItem;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DBWriter$OkPvoEuxvF1RBIjmvy0_TvbuLKM implements Runnable {
    private final /* synthetic */ FeedItem f$0;

    public /* synthetic */ -$$Lambda$DBWriter$OkPvoEuxvF1RBIjmvy0_TvbuLKM(FeedItem feedItem) {
        this.f$0 = feedItem;
    }

    public final void run() {
        DBWriter.lambda$addFavoriteItem$11(this.f$0);
    }
}
