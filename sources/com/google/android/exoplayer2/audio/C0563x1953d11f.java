package com.google.android.exoplayer2.audio;

import com.google.android.exoplayer2.decoder.DecoderCounters;

/* compiled from: lambda */
/* renamed from: com.google.android.exoplayer2.audio.-$$Lambda$AudioRendererEventListener$EventDispatcher$MUMUaHcEfIpwDLi9gxmScOQxifc */
public final /* synthetic */ class C0563x1953d11f implements Runnable {
    private final /* synthetic */ AudioRendererEventListener$EventDispatcher f$0;
    private final /* synthetic */ DecoderCounters f$1;

    public /* synthetic */ C0563x1953d11f(AudioRendererEventListener$EventDispatcher audioRendererEventListener$EventDispatcher, DecoderCounters decoderCounters) {
        this.f$0 = audioRendererEventListener$EventDispatcher;
        this.f$1 = decoderCounters;
    }

    public final void run() {
        this.f$0.listener.onAudioEnabled(this.f$1);
    }
}
