package com.google.android.exoplayer2.video;

/* compiled from: lambda */
/* renamed from: com.google.android.exoplayer2.video.-$$Lambda$VideoRendererEventListener$EventDispatcher$TaBV3X3b5lKElsQ7tczViKAyQ3w */
public final /* synthetic */ class C0659x3b67d6b6 implements Runnable {
    private final /* synthetic */ VideoRendererEventListener$EventDispatcher f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ int f$3;
    private final /* synthetic */ float f$4;

    public /* synthetic */ C0659x3b67d6b6(VideoRendererEventListener$EventDispatcher videoRendererEventListener$EventDispatcher, int i, int i2, int i3, float f) {
        this.f$0 = videoRendererEventListener$EventDispatcher;
        this.f$1 = i;
        this.f$2 = i2;
        this.f$3 = i3;
        this.f$4 = f;
    }

    public final void run() {
        this.f$0.listener.onVideoSizeChanged(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
