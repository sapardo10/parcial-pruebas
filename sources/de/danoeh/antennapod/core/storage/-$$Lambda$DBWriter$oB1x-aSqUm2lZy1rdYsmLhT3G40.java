package de.danoeh.antennapod.core.storage;

import android.content.Context;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DBWriter$oB1x-aSqUm2lZy1rdYsmLhT3G40 implements Runnable {
    private final /* synthetic */ long f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ boolean f$2;
    private final /* synthetic */ Context f$3;

    public /* synthetic */ -$$Lambda$DBWriter$oB1x-aSqUm2lZy1rdYsmLhT3G40(long j, int i, boolean z, Context context) {
        this.f$0 = j;
        this.f$1 = i;
        this.f$2 = z;
        this.f$3 = context;
    }

    public final void run() {
        DBWriter.lambda$addQueueItemAt$7(this.f$0, this.f$1, this.f$2, this.f$3);
    }
}
