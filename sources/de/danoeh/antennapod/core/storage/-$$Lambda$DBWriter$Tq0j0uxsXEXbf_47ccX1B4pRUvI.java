package de.danoeh.antennapod.core.storage;

import java.util.Set;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DBWriter$Tq0j0uxsXEXbf_47ccX1B4pRUvI implements Runnable {
    private final /* synthetic */ long f$0;
    private final /* synthetic */ Set f$1;

    public /* synthetic */ -$$Lambda$DBWriter$Tq0j0uxsXEXbf_47ccX1B4pRUvI(long j, Set set) {
        this.f$0 = j;
        this.f$1 = set;
    }

    public final void run() {
        DBWriter.lambda$setFeedItemsFilter$40(this.f$0, this.f$1);
    }
}
