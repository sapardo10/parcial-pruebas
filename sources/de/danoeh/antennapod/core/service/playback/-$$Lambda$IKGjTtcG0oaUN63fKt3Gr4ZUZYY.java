package de.danoeh.antennapod.core.service.playback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$IKGjTtcG0oaUN63fKt3Gr4ZUZYY implements Runnable {
    private final /* synthetic */ LocalPSMP f$0;

    public /* synthetic */ -$$Lambda$IKGjTtcG0oaUN63fKt3Gr4ZUZYY(LocalPSMP localPSMP) {
        this.f$0 = localPSMP;
    }

    public final void run() {
        this.f$0.shutdown();
    }
}
