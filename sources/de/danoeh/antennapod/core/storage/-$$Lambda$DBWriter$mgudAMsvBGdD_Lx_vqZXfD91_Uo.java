package de.danoeh.antennapod.core.storage;

import android.content.Context;
import de.danoeh.antennapod.core.feed.Feed;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DBWriter$mgudAMsvBGdD_Lx_vqZXfD91_Uo implements Runnable {
    private final /* synthetic */ Feed f$0;
    private final /* synthetic */ boolean f$1;
    private final /* synthetic */ Context f$2;

    public /* synthetic */ -$$Lambda$DBWriter$mgudAMsvBGdD_Lx_vqZXfD91_Uo(Feed feed, boolean z, Context context) {
        this.f$0 = feed;
        this.f$1 = z;
        this.f$2 = context;
    }

    public final void run() {
        DBWriter.lambda$setFeedFlattrStatus$30(this.f$0, this.f$1, this.f$2);
    }
}
