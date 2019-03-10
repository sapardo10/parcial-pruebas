package de.danoeh.antennapod.core.storage;

import de.danoeh.antennapod.core.feed.FeedMedia;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DBWriter$p2J-J0P4qHVbGpiW27CB5E6bB6w implements Runnable {
    private final /* synthetic */ FeedMedia f$0;

    public /* synthetic */ -$$Lambda$DBWriter$p2J-J0P4qHVbGpiW27CB5E6bB6w(FeedMedia feedMedia) {
        this.f$0 = feedMedia;
    }

    public final void run() {
        DBWriter.lambda$addItemToPlaybackHistory$5(this.f$0);
    }
}
