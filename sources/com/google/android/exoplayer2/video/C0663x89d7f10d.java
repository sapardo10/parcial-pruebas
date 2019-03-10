package com.google.android.exoplayer2.video;

/* compiled from: lambda */
/* renamed from: com.google.android.exoplayer2.video.-$$Lambda$VideoRendererEventListener$EventDispatcher$wpJzum9Nim-WREQi3I6t6RZgGzs */
public final /* synthetic */ class C0663x89d7f10d implements Runnable {
    private final /* synthetic */ VideoRendererEventListener$EventDispatcher f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ long f$2;

    public /* synthetic */ C0663x89d7f10d(VideoRendererEventListener$EventDispatcher videoRendererEventListener$EventDispatcher, int i, long j) {
        this.f$0 = videoRendererEventListener$EventDispatcher;
        this.f$1 = i;
        this.f$2 = j;
    }

    public final void run() {
        this.f$0.listener.onDroppedFrames(this.f$1, this.f$2);
    }
}
