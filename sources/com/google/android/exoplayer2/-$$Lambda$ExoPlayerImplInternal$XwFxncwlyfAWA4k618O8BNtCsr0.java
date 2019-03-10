package com.google.android.exoplayer2;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ExoPlayerImplInternal$XwFxncwlyfAWA4k618O8BNtCsr0 implements Runnable {
    private final /* synthetic */ ExoPlayerImplInternal f$0;
    private final /* synthetic */ PlayerMessage f$1;

    public /* synthetic */ -$$Lambda$ExoPlayerImplInternal$XwFxncwlyfAWA4k618O8BNtCsr0(ExoPlayerImplInternal exoPlayerImplInternal, PlayerMessage playerMessage) {
        this.f$0 = exoPlayerImplInternal;
        this.f$1 = playerMessage;
    }

    public final void run() {
        ExoPlayerImplInternal.lambda$sendMessageToTargetThread$0(this.f$0, this.f$1);
    }
}
