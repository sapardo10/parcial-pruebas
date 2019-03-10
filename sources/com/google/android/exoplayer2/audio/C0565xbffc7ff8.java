package com.google.android.exoplayer2.audio;

import com.google.android.exoplayer2.decoder.DecoderCounters;

/* compiled from: lambda */
/* renamed from: com.google.android.exoplayer2.audio.-$$Lambda$AudioRendererEventListener$EventDispatcher$jb22FSnmUl2pGG0LguQS_Wd-LWk */
public final /* synthetic */ class C0565xbffc7ff8 implements Runnable {
    private final /* synthetic */ AudioRendererEventListener$EventDispatcher f$0;
    private final /* synthetic */ DecoderCounters f$1;

    public /* synthetic */ C0565xbffc7ff8(AudioRendererEventListener$EventDispatcher audioRendererEventListener$EventDispatcher, DecoderCounters decoderCounters) {
        this.f$0 = audioRendererEventListener$EventDispatcher;
        this.f$1 = decoderCounters;
    }

    public final void run() {
        AudioRendererEventListener$EventDispatcher.lambda$disabled$4(this.f$0, this.f$1);
    }
}
