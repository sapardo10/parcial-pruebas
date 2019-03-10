package de.danoeh.antennapod.core.storage;

import de.danoeh.antennapod.core.feed.FeedPreferences;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DBWriter$ISwu78Yorq2vs6N56FscktE0BlA implements Runnable {
    private final /* synthetic */ FeedPreferences f$0;

    public /* synthetic */ -$$Lambda$DBWriter$ISwu78Yorq2vs6N56FscktE0BlA(FeedPreferences feedPreferences) {
        this.f$0 = feedPreferences;
    }

    public final void run() {
        DBWriter.lambda$setFeedPreferences$28(this.f$0);
    }
}
