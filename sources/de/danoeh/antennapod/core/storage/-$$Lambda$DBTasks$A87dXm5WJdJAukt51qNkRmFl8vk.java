package de.danoeh.antennapod.core.storage;

import android.content.Context;
import java.util.List;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DBTasks$A87dXm5WJdJAukt51qNkRmFl8vk implements Runnable {
    private final /* synthetic */ List f$0;
    private final /* synthetic */ Context f$1;
    private final /* synthetic */ Runnable f$2;

    public /* synthetic */ -$$Lambda$DBTasks$A87dXm5WJdJAukt51qNkRmFl8vk(List list, Context context, Runnable runnable) {
        this.f$0 = list;
        this.f$1 = context;
        this.f$2 = runnable;
    }

    public final void run() {
        DBTasks.lambda$refreshAllFeeds$1(this.f$0, this.f$1, this.f$2);
    }
}
