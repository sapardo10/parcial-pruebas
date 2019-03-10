package de.danoeh.antennapod.core.storage;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DBWriter$LDJfA8KBd4C10b0oVofAN8I11Sw implements Runnable {
    private final /* synthetic */ long f$0;
    private final /* synthetic */ boolean f$1;

    public /* synthetic */ -$$Lambda$DBWriter$LDJfA8KBd4C10b0oVofAN8I11Sw(long j, boolean z) {
        this.f$0 = j;
        this.f$1 = z;
    }

    public final void run() {
        DBWriter.lambda$moveQueueItemToTop$13(this.f$0, this.f$1);
    }
}
