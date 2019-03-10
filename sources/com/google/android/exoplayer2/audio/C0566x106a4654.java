package com.google.android.exoplayer2.audio;

/* compiled from: lambda */
/* renamed from: com.google.android.exoplayer2.audio.-$$Lambda$AudioRendererEventListener$EventDispatcher$oPQKly422CpX1mqIU2N6d76OGxk */
public final /* synthetic */ class C0566x106a4654 implements Runnable {
    private final /* synthetic */ AudioRendererEventListener$EventDispatcher f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ long f$2;
    private final /* synthetic */ long f$3;

    public /* synthetic */ C0566x106a4654(AudioRendererEventListener$EventDispatcher audioRendererEventListener$EventDispatcher, int i, long j, long j2) {
        this.f$0 = audioRendererEventListener$EventDispatcher;
        this.f$1 = i;
        this.f$2 = j;
        this.f$3 = j2;
    }

    public final void run() {
        this.f$0.listener.onAudioSinkUnderrun(this.f$1, this.f$2, this.f$3);
    }
}
