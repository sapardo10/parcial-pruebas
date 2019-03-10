package com.google.android.exoplayer2.ui.spherical;

import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SceneRenderer$4ClzwyHXabRJX89l_xvhRW1IBQs implements OnFrameAvailableListener {
    private final /* synthetic */ SceneRenderer f$0;

    public /* synthetic */ -$$Lambda$SceneRenderer$4ClzwyHXabRJX89l_xvhRW1IBQs(SceneRenderer sceneRenderer) {
        this.f$0 = sceneRenderer;
    }

    public final void onFrameAvailable(SurfaceTexture surfaceTexture) {
        this.f$0.frameAvailable.set(true);
    }
}
