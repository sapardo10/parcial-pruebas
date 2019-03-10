package com.google.android.exoplayer2.audio;

/* compiled from: lambda */
/* renamed from: com.google.android.exoplayer2.audio.-$$Lambda$AudioRendererEventListener$EventDispatcher$F29t8_xYSK7h_6CpLRlp2y2yb1E */
public final /* synthetic */ class C0562x951d9860 implements Runnable {
    private final /* synthetic */ AudioRendererEventListener$EventDispatcher f$0;
    private final /* synthetic */ String f$1;
    private final /* synthetic */ long f$2;
    private final /* synthetic */ long f$3;

    public /* synthetic */ C0562x951d9860(AudioRendererEventListener$EventDispatcher audioRendererEventListener$EventDispatcher, String str, long j, long j2) {
        this.f$0 = audioRendererEventListener$EventDispatcher;
        this.f$1 = str;
        this.f$2 = j;
        this.f$3 = j2;
    }

    public final void run() {
        this.f$0.listener.onAudioDecoderInitialized(this.f$1, this.f$2, this.f$3);
    }
}
