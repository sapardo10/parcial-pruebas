package de.danoeh.antennapod.core.storage;

import de.danoeh.antennapod.core.feed.FeedMedia;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DBWriter$uC6f8_T9J04gSPGxx-QN-ezcgIo implements Runnable {
    private final /* synthetic */ FeedMedia f$0;

    public /* synthetic */ -$$Lambda$DBWriter$uC6f8_T9J04gSPGxx-QN-ezcgIo(FeedMedia feedMedia) {
        this.f$0 = feedMedia;
    }

    public final void run() {
        DBWriter.lambda$setFeedMediaPlaybackInformation$25(this.f$0);
    }
}
