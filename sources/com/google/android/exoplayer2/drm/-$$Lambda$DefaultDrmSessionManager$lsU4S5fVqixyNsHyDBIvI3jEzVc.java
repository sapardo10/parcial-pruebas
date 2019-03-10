package com.google.android.exoplayer2.drm;

import com.google.android.exoplayer2.drm.DefaultDrmSessionManager.MissingSchemeDataException;
import com.google.android.exoplayer2.util.EventDispatcher.Event;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DefaultDrmSessionManager$lsU4S5fVqixyNsHyDBIvI3jEzVc implements Event {
    private final /* synthetic */ MissingSchemeDataException f$0;

    public /* synthetic */ -$$Lambda$DefaultDrmSessionManager$lsU4S5fVqixyNsHyDBIvI3jEzVc(MissingSchemeDataException missingSchemeDataException) {
        this.f$0 = missingSchemeDataException;
    }

    public final void sendTo(Object obj) {
        ((DefaultDrmSessionEventListener) obj).onDrmSessionManagerError(this.f$0);
    }
}
