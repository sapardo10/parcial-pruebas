package com.google.android.exoplayer2.drm;

import com.google.android.exoplayer2.util.EventDispatcher.Event;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DefaultDrmSession$-nKOJC1w2998gRg4Cg4l2mjlp30 implements Event {
    private final /* synthetic */ Exception f$0;

    public /* synthetic */ -$$Lambda$DefaultDrmSession$-nKOJC1w2998gRg4Cg4l2mjlp30(Exception exception) {
        this.f$0 = exception;
    }

    public final void sendTo(Object obj) {
        ((DefaultDrmSessionEventListener) obj).onDrmSessionManagerError(this.f$0);
    }
}
