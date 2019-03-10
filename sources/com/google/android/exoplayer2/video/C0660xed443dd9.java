package com.google.android.exoplayer2.video;

/* compiled from: lambda */
/* renamed from: com.google.android.exoplayer2.video.-$$Lambda$VideoRendererEventListener$EventDispatcher$Y232CA7hogfrRJjYu2VeUSxg0VQ */
public final /* synthetic */ class C0660xed443dd9 implements Runnable {
    private final /* synthetic */ VideoRendererEventListener$EventDispatcher f$0;
    private final /* synthetic */ String f$1;
    private final /* synthetic */ long f$2;
    private final /* synthetic */ long f$3;

    public /* synthetic */ C0660xed443dd9(VideoRendererEventListener$EventDispatcher videoRendererEventListener$EventDispatcher, String str, long j, long j2) {
        this.f$0 = videoRendererEventListener$EventDispatcher;
        this.f$1 = str;
        this.f$2 = j;
        this.f$3 = j2;
    }

    public final void run() {
        this.f$0.listener.onVideoDecoderInitialized(this.f$1, this.f$2, this.f$3);
    }
}
