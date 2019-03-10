package com.google.android.exoplayer2.source.dash;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DashMediaSource$e1nzB-O4m3YSG1BkxQDKPaNvDa8 implements Runnable {
    private final /* synthetic */ DashMediaSource f$0;

    public /* synthetic */ -$$Lambda$DashMediaSource$e1nzB-O4m3YSG1BkxQDKPaNvDa8(DashMediaSource dashMediaSource) {
        this.f$0 = dashMediaSource;
    }

    public final void run() {
        this.f$0.processManifest(false);
    }
}
