package de.danoeh.antennapod.core.service.playback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocalPSMP$PX3WlUoWRWh48sjL2XnAOp155XE implements Runnable {
    private final /* synthetic */ LocalPSMP f$0;
    private final /* synthetic */ float f$1;
    private final /* synthetic */ float f$2;

    public /* synthetic */ -$$Lambda$LocalPSMP$PX3WlUoWRWh48sjL2XnAOp155XE(LocalPSMP localPSMP, float f, float f2) {
        this.f$0 = localPSMP;
        this.f$1 = f;
        this.f$2 = f2;
    }

    public final void run() {
        this.f$0.setVolumeSync(this.f$1, this.f$2);
    }
}
