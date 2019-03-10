package com.google.android.exoplayer2.source.dash;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DashMediaSource$QbzYvqCY1TT8f0KClkalovG-Oxc implements Runnable {
    private final /* synthetic */ DashMediaSource f$0;

    public /* synthetic */ -$$Lambda$DashMediaSource$QbzYvqCY1TT8f0KClkalovG-Oxc(DashMediaSource dashMediaSource) {
        this.f$0 = dashMediaSource;
    }

    public final void run() {
        this.f$0.startLoadingManifest();
    }
}
