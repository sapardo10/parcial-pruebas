package de.danoeh.antennapod.core.storage;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DBWriter$xzYTSnk211oD80eUcTXYHkVl2sU implements Runnable {
    private final /* synthetic */ long f$0;
    private final /* synthetic */ boolean f$1;

    public /* synthetic */ -$$Lambda$DBWriter$xzYTSnk211oD80eUcTXYHkVl2sU(long j, boolean z) {
        this.f$0 = j;
        this.f$1 = z;
    }

    public final void run() {
        DBWriter.lambda$moveQueueItemToBottom$14(this.f$0, this.f$1);
    }
}
