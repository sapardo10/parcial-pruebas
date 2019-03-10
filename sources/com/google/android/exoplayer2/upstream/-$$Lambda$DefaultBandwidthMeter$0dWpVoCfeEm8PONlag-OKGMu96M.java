package com.google.android.exoplayer2.upstream;

import com.google.android.exoplayer2.util.EventDispatcher.Event;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DefaultBandwidthMeter$0dWpVoCfeEm8PONlag-OKGMu96M implements Event {
    private final /* synthetic */ int f$0;
    private final /* synthetic */ long f$1;
    private final /* synthetic */ long f$2;

    public /* synthetic */ -$$Lambda$DefaultBandwidthMeter$0dWpVoCfeEm8PONlag-OKGMu96M(int i, long j, long j2) {
        this.f$0 = i;
        this.f$1 = j;
        this.f$2 = j2;
    }

    public final void sendTo(Object obj) {
        ((BandwidthMeter$EventListener) obj).onBandwidthSample(this.f$0, this.f$1, this.f$2);
    }
}
