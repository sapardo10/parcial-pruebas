package de.danoeh.antennapod.core.storage;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DBWriter$gE7GwuBOR8tGanMW0pRRpGpurQI implements Runnable {
    private final /* synthetic */ int f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ boolean f$2;

    public /* synthetic */ -$$Lambda$DBWriter$gE7GwuBOR8tGanMW0pRRpGpurQI(int i, int i2, boolean z) {
        this.f$0 = i;
        this.f$1 = i2;
        this.f$2 = z;
    }

    public final void run() {
        DBWriter.moveQueueItemHelper(this.f$0, this.f$1, this.f$2);
    }
}
