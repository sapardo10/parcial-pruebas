package de.danoeh.antennapod.core.storage;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DBWriter$1miaRFjOtpoFsnrjoNISdu8Cx04 implements Runnable {
    private final /* synthetic */ int f$0;
    private final /* synthetic */ long[] f$1;
    private final /* synthetic */ boolean f$2;

    public /* synthetic */ -$$Lambda$DBWriter$1miaRFjOtpoFsnrjoNISdu8Cx04(int i, long[] jArr, boolean z) {
        this.f$0 = i;
        this.f$1 = jArr;
        this.f$2 = z;
    }

    public final void run() {
        DBWriter.lambda$markItemPlayed$16(this.f$0, this.f$1, this.f$2);
    }
}
