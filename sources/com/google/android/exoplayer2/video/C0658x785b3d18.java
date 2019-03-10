package com.google.android.exoplayer2.video;

import android.view.Surface;

/* compiled from: lambda */
/* renamed from: com.google.android.exoplayer2.video.-$$Lambda$VideoRendererEventListener$EventDispatcher$SFK5uUI0PHTm3Dg6Wdc1eRaQ9xk */
public final /* synthetic */ class C0658x785b3d18 implements Runnable {
    private final /* synthetic */ VideoRendererEventListener$EventDispatcher f$0;
    private final /* synthetic */ Surface f$1;

    public /* synthetic */ C0658x785b3d18(VideoRendererEventListener$EventDispatcher videoRendererEventListener$EventDispatcher, Surface surface) {
        this.f$0 = videoRendererEventListener$EventDispatcher;
        this.f$1 = surface;
    }

    public final void run() {
        this.f$0.listener.onRenderedFirstFrame(this.f$1);
    }
}
