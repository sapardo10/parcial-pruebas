package com.google.android.exoplayer2.source.hls;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$HlsSampleStreamWrapper$8JyeEr0irIOShv9LlAxAmgzl5vY implements Runnable {
    private final /* synthetic */ HlsSampleStreamWrapper f$0;

    public /* synthetic */ -$$Lambda$HlsSampleStreamWrapper$8JyeEr0irIOShv9LlAxAmgzl5vY(HlsSampleStreamWrapper hlsSampleStreamWrapper) {
        this.f$0 = hlsSampleStreamWrapper;
    }

    public final void run() {
        this.f$0.maybeFinishPrepare();
    }
}
