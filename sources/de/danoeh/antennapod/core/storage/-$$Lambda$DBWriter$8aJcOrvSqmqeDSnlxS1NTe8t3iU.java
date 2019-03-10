package de.danoeh.antennapod.core.storage;

import android.content.Context;
import de.danoeh.antennapod.core.feed.FeedItem;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DBWriter$8aJcOrvSqmqeDSnlxS1NTe8t3iU implements Runnable {
    private final /* synthetic */ FeedItem f$0;
    private final /* synthetic */ boolean f$1;
    private final /* synthetic */ Context f$2;

    public /* synthetic */ -$$Lambda$DBWriter$8aJcOrvSqmqeDSnlxS1NTe8t3iU(FeedItem feedItem, boolean z, Context context) {
        this.f$0 = feedItem;
        this.f$1 = z;
        this.f$2 = context;
    }

    public final void run() {
        DBWriter.lambda$setFeedItemFlattrStatus$29(this.f$0, this.f$1, this.f$2);
    }
}
