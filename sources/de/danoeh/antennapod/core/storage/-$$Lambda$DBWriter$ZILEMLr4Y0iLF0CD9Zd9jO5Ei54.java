package de.danoeh.antennapod.core.storage;

import android.content.Context;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DBWriter$ZILEMLr4Y0iLF0CD9Zd9jO5Ei54 implements Runnable {
    private final /* synthetic */ long f$0;
    private final /* synthetic */ Context f$1;

    public /* synthetic */ -$$Lambda$DBWriter$ZILEMLr4Y0iLF0CD9Zd9jO5Ei54(long j, Context context) {
        this.f$0 = j;
        this.f$1 = context;
    }

    public final void run() {
        DBWriter.lambda$deleteFeedMediaOfItem$1(this.f$0, this.f$1);
    }
}
