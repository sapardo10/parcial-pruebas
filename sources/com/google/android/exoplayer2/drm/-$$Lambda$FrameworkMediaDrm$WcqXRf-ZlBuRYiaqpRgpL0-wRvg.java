package com.google.android.exoplayer2.drm;

import android.media.MediaDrm;
import android.media.MediaDrm.OnKeyStatusChangeListener;
import java.util.List;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$FrameworkMediaDrm$WcqXRf-ZlBuRYiaqpRgpL0-wRvg implements OnKeyStatusChangeListener {
    private final /* synthetic */ FrameworkMediaDrm f$0;
    private final /* synthetic */ ExoMediaDrm.OnKeyStatusChangeListener f$1;

    public /* synthetic */ -$$Lambda$FrameworkMediaDrm$WcqXRf-ZlBuRYiaqpRgpL0-wRvg(FrameworkMediaDrm frameworkMediaDrm, ExoMediaDrm.OnKeyStatusChangeListener onKeyStatusChangeListener) {
        this.f$0 = frameworkMediaDrm;
        this.f$1 = onKeyStatusChangeListener;
    }

    public final void onKeyStatusChange(MediaDrm mediaDrm, byte[] bArr, List list, boolean z) {
        FrameworkMediaDrm.lambda$setOnKeyStatusChangeListener$1(this.f$0, this.f$1, mediaDrm, bArr, list, z);
    }
}
