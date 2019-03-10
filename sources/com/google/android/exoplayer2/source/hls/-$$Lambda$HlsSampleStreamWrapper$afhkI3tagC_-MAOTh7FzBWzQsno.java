package com.google.android.exoplayer2.source.hls;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$HlsSampleStreamWrapper$afhkI3tagC_-MAOTh7FzBWzQsno implements Runnable {
    private final /* synthetic */ HlsSampleStreamWrapper f$0;

    public /* synthetic */ -$$Lambda$HlsSampleStreamWrapper$afhkI3tagC_-MAOTh7FzBWzQsno(HlsSampleStreamWrapper hlsSampleStreamWrapper) {
        this.f$0 = hlsSampleStreamWrapper;
    }

    public final void run() {
        this.f$0.onTracksEnded();
    }
}
