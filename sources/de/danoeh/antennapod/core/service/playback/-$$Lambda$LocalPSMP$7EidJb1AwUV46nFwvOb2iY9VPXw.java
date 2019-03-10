package de.danoeh.antennapod.core.service.playback;

import android.view.SurfaceHolder;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocalPSMP$7EidJb1AwUV46nFwvOb2iY9VPXw implements Runnable {
    private final /* synthetic */ LocalPSMP f$0;
    private final /* synthetic */ SurfaceHolder f$1;

    public /* synthetic */ -$$Lambda$LocalPSMP$7EidJb1AwUV46nFwvOb2iY9VPXw(LocalPSMP localPSMP, SurfaceHolder surfaceHolder) {
        this.f$0 = localPSMP;
        this.f$1 = surfaceHolder;
    }

    public final void run() {
        LocalPSMP.lambda$setVideoSurface$14(this.f$0, this.f$1);
    }
}
