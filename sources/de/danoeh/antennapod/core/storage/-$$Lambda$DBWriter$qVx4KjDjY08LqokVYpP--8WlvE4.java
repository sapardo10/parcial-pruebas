package de.danoeh.antennapod.core.storage;

import de.danoeh.antennapod.core.feed.FeedMedia;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DBWriter$qVx4KjDjY08LqokVYpP--8WlvE4 implements Runnable {
    private final /* synthetic */ FeedMedia f$0;

    public /* synthetic */ -$$Lambda$DBWriter$qVx4KjDjY08LqokVYpP--8WlvE4(FeedMedia feedMedia) {
        this.f$0 = feedMedia;
    }

    public final void run() {
        DBWriter.lambda$setFeedMedia$24(this.f$0);
    }
}
