package de.danoeh.antennapod.core.storage;

import de.danoeh.antennapod.core.feed.Feed;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DBWriter$5QekNLgAxlef4fGkx52_WJvjhhw implements Runnable {
    private final /* synthetic */ Feed[] f$0;

    public /* synthetic */ -$$Lambda$DBWriter$5QekNLgAxlef4fGkx52_WJvjhhw(Feed[] feedArr) {
        this.f$0 = feedArr;
    }

    public final void run() {
        DBWriter.lambda$setCompleteFeed$23(this.f$0);
    }
}
