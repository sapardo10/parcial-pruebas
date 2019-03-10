package de.danoeh.antennapod.activity;

import de.danoeh.antennapod.core.feed.FeedItem;
import de.danoeh.antennapod.core.storage.DBReader;
import java.util.concurrent.Callable;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaplayerActivity$dtSlqSlCDl4EQkSYoSmABaY8EH8 implements Callable {
    private final /* synthetic */ FeedItem f$0;

    public /* synthetic */ -$$Lambda$MediaplayerActivity$dtSlqSlCDl4EQkSYoSmABaY8EH8(FeedItem feedItem) {
        this.f$0 = feedItem;
    }

    public final Object call() {
        return DBReader.getFeedItem(this.f$0.getId());
    }
}
