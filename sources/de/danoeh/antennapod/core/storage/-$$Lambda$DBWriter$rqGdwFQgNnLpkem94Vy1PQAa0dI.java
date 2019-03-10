package de.danoeh.antennapod.core.storage;

import android.content.Context;
import de.danoeh.antennapod.core.feed.FeedItem;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DBWriter$rqGdwFQgNnLpkem94Vy1PQAa0dI implements Runnable {
    private final /* synthetic */ Context f$0;
    private final /* synthetic */ FeedItem f$1;
    private final /* synthetic */ boolean f$2;

    public /* synthetic */ -$$Lambda$DBWriter$rqGdwFQgNnLpkem94Vy1PQAa0dI(Context context, FeedItem feedItem, boolean z) {
        this.f$0 = context;
        this.f$1 = feedItem;
        this.f$2 = z;
    }

    public final void run() {
        DBWriter.removeQueueItemSynchronous(this.f$0, this.f$1, this.f$2);
    }
}
