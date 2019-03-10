package de.danoeh.antennapod.core.storage;

import java.util.Comparator;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DBWriter$Lp-CH6KjyWC6BPIvHOslY-DcmR4 implements Runnable {
    private final /* synthetic */ Comparator f$0;
    private final /* synthetic */ boolean f$1;

    public /* synthetic */ -$$Lambda$DBWriter$Lp-CH6KjyWC6BPIvHOslY-DcmR4(Comparator comparator, boolean z) {
        this.f$0 = comparator;
        this.f$1 = z;
    }

    public final void run() {
        DBWriter.lambda$sortQueue$35(this.f$0, this.f$1);
    }
}
