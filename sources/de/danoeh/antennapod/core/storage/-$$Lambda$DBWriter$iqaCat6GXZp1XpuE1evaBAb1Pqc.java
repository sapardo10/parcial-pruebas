package de.danoeh.antennapod.core.storage;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DBWriter$iqaCat6GXZp1XpuE1evaBAb1Pqc implements Runnable {
    private final /* synthetic */ int f$0;
    private final /* synthetic */ long f$1;
    private final /* synthetic */ long f$2;
    private final /* synthetic */ boolean f$3;

    public /* synthetic */ -$$Lambda$DBWriter$iqaCat6GXZp1XpuE1evaBAb1Pqc(int i, long j, long j2, boolean z) {
        this.f$0 = i;
        this.f$1 = j;
        this.f$2 = j2;
        this.f$3 = z;
    }

    public final void run() {
        DBWriter.lambda$markItemPlayed$17(this.f$0, this.f$1, this.f$2, this.f$3);
    }
}
