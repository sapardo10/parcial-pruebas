package de.danoeh.antennapod.core.service.playback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocalPSMP$pKZpLUVTrUQlCXzC-7SYlI3JJAs implements Runnable {
    private final /* synthetic */ LocalPSMP f$0;
    private final /* synthetic */ float f$1;
    private final /* synthetic */ boolean f$2;

    public /* synthetic */ -$$Lambda$LocalPSMP$pKZpLUVTrUQlCXzC-7SYlI3JJAs(LocalPSMP localPSMP, float f, boolean z) {
        this.f$0 = localPSMP;
        this.f$1 = f;
        this.f$2 = z;
    }

    public final void run() {
        this.f$0.setSpeedSyncAndSkipSilence(this.f$1, this.f$2);
    }
}
