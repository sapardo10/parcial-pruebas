package com.google.android.exoplayer2.audio;

import com.google.android.exoplayer2.Format;

/* compiled from: lambda */
/* renamed from: com.google.android.exoplayer2.audio.-$$Lambda$AudioRendererEventListener$EventDispatcher$D7KvJbrpXrnWw4qzd_LI9ZtQytw */
public final /* synthetic */ class C0561x2218d907 implements Runnable {
    private final /* synthetic */ AudioRendererEventListener$EventDispatcher f$0;
    private final /* synthetic */ Format f$1;

    public /* synthetic */ C0561x2218d907(AudioRendererEventListener$EventDispatcher audioRendererEventListener$EventDispatcher, Format format) {
        this.f$0 = audioRendererEventListener$EventDispatcher;
        this.f$1 = format;
    }

    public final void run() {
        this.f$0.listener.onAudioInputFormatChanged(this.f$1);
    }
}
