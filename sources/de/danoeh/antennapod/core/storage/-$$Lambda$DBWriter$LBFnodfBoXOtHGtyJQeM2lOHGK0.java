package de.danoeh.antennapod.core.storage;

import android.content.Context;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DBWriter$LBFnodfBoXOtHGtyJQeM2lOHGK0 implements Runnable {
    private final /* synthetic */ Context f$0;
    private final /* synthetic */ long f$1;

    public /* synthetic */ -$$Lambda$DBWriter$LBFnodfBoXOtHGtyJQeM2lOHGK0(Context context, long j) {
        this.f$0 = context;
        this.f$1 = j;
    }

    public final void run() {
        DBWriter.lambda$deleteFeed$2(this.f$0, this.f$1);
    }
}
