package de.danoeh.antennapod.core.storage;

import de.danoeh.antennapod.core.feed.Feed;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DBWriter$Sg2cNLArkctDxT9L6nE3i3Nlykk implements Runnable {
    private final /* synthetic */ Feed f$0;
    private final /* synthetic */ boolean f$1;

    public /* synthetic */ -$$Lambda$DBWriter$Sg2cNLArkctDxT9L6nE3i3Nlykk(Feed feed, boolean z) {
        this.f$0 = feed;
        this.f$1 = z;
    }

    public final void run() {
        DBWriter.lambda$setFeedsItemsAutoDownload$39(this.f$0, this.f$1);
    }
}
