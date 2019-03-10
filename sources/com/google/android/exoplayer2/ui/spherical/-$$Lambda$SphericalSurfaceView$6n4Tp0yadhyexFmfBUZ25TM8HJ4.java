package com.google.android.exoplayer2.ui.spherical;

import android.graphics.SurfaceTexture;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SphericalSurfaceView$6n4Tp0yadhyexFmfBUZ25TM8HJ4 implements Runnable {
    private final /* synthetic */ SphericalSurfaceView f$0;
    private final /* synthetic */ SurfaceTexture f$1;

    public /* synthetic */ -$$Lambda$SphericalSurfaceView$6n4Tp0yadhyexFmfBUZ25TM8HJ4(SphericalSurfaceView sphericalSurfaceView, SurfaceTexture surfaceTexture) {
        this.f$0 = sphericalSurfaceView;
        this.f$1 = surfaceTexture;
    }

    public final void run() {
        SphericalSurfaceView.lambda$onSurfaceTextureAvailable$1(this.f$0, this.f$1);
    }
}
