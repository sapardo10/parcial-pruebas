package de.danoeh.antennapod.core.storage;

import android.content.Context;
import de.danoeh.antennapod.core.feed.Feed;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DBWriter$iEuRzTUK5pMZYfOq9wRO7ZLcYAE implements Runnable {
    private final /* synthetic */ Feed[] f$0;
    private final /* synthetic */ Context f$1;

    public /* synthetic */ -$$Lambda$DBWriter$iEuRzTUK5pMZYfOq9wRO7ZLcYAE(Feed[] feedArr, Context context) {
        this.f$0 = feedArr;
        this.f$1 = context;
    }

    public final void run() {
        DBWriter.lambda$addNewFeed$22(this.f$0, this.f$1);
    }
}
