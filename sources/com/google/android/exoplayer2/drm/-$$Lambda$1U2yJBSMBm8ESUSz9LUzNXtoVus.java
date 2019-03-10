package com.google.android.exoplayer2.drm;

import com.google.android.exoplayer2.util.EventDispatcher.Event;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$1U2yJBSMBm8ESUSz9LUzNXtoVus implements Event {
    public static final /* synthetic */ -$$Lambda$1U2yJBSMBm8ESUSz9LUzNXtoVus INSTANCE = new -$$Lambda$1U2yJBSMBm8ESUSz9LUzNXtoVus();

    private /* synthetic */ -$$Lambda$1U2yJBSMBm8ESUSz9LUzNXtoVus() {
    }

    public final void sendTo(Object obj) {
        ((DefaultDrmSessionEventListener) obj).onDrmSessionReleased();
    }
}
