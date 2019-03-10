package de.danoeh.antennapod.core.storage;

import android.content.Context;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DBWriter$sktGi0w4HVfv5xxvUlAGyq3boVQ implements Runnable {
    private final /* synthetic */ long[] f$0;
    private final /* synthetic */ boolean f$1;
    private final /* synthetic */ Context f$2;

    public /* synthetic */ -$$Lambda$DBWriter$sktGi0w4HVfv5xxvUlAGyq3boVQ(long[] jArr, boolean z, Context context) {
        this.f$0 = jArr;
        this.f$1 = z;
        this.f$2 = context;
    }

    public final void run() {
        DBWriter.lambda$addQueueItem$8(this.f$0, this.f$1, this.f$2);
    }
}
