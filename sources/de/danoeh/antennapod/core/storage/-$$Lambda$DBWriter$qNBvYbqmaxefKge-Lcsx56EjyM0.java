package de.danoeh.antennapod.core.storage;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DBWriter$qNBvYbqmaxefKge-Lcsx56EjyM0 implements Runnable {
    private final /* synthetic */ long f$0;
    private final /* synthetic */ boolean f$1;

    public /* synthetic */ -$$Lambda$DBWriter$qNBvYbqmaxefKge-Lcsx56EjyM0(long j, boolean z) {
        this.f$0 = j;
        this.f$1 = z;
    }

    public final void run() {
        DBWriter.lambda$setFeedLastUpdateFailed$31(this.f$0, this.f$1);
    }
}
