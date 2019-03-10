package com.google.android.exoplayer2.source.ads;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$2Zac3B-Whc_7swHmcnO1d7h-1Gc implements Runnable {
    private final /* synthetic */ AdsLoader f$0;

    public /* synthetic */ -$$Lambda$2Zac3B-Whc_7swHmcnO1d7h-1Gc(AdsLoader adsLoader) {
        this.f$0 = adsLoader;
    }

    public final void run() {
        this.f$0.detachPlayer();
    }
}
