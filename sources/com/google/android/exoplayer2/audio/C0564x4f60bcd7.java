package com.google.android.exoplayer2.audio;

/* compiled from: lambda */
/* renamed from: com.google.android.exoplayer2.audio.-$$Lambda$AudioRendererEventListener$EventDispatcher$a1B1YBHhPRCtc1MQAc2fSVEo22I */
public final /* synthetic */ class C0564x4f60bcd7 implements Runnable {
    private final /* synthetic */ AudioRendererEventListener$EventDispatcher f$0;
    private final /* synthetic */ int f$1;

    public /* synthetic */ C0564x4f60bcd7(AudioRendererEventListener$EventDispatcher audioRendererEventListener$EventDispatcher, int i) {
        this.f$0 = audioRendererEventListener$EventDispatcher;
        this.f$1 = i;
    }

    public final void run() {
        this.f$0.listener.onAudioSessionId(this.f$1);
    }
}
