package de.danoeh.antennapod.core.service.playback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocalPSMP$yS0GXDf86RojNPV0IdEaSv2wqlw implements Runnable {
    private final /* synthetic */ LocalPSMP f$0;
    private final /* synthetic */ int f$1;

    public /* synthetic */ -$$Lambda$LocalPSMP$yS0GXDf86RojNPV0IdEaSv2wqlw(LocalPSMP localPSMP, int i) {
        this.f$0 = localPSMP;
        this.f$1 = i;
    }

    public final void run() {
        this.f$0.seekToSync(this.f$1);
    }
}
