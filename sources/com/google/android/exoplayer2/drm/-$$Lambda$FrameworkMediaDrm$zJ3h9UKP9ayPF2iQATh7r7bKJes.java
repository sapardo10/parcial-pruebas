package com.google.android.exoplayer2.drm;

import android.media.MediaDrm;
import android.media.MediaDrm.OnEventListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$FrameworkMediaDrm$zJ3h9UKP9ayPF2iQATh7r7bKJes implements OnEventListener {
    private final /* synthetic */ FrameworkMediaDrm f$0;
    private final /* synthetic */ ExoMediaDrm.OnEventListener f$1;

    public /* synthetic */ -$$Lambda$FrameworkMediaDrm$zJ3h9UKP9ayPF2iQATh7r7bKJes(FrameworkMediaDrm frameworkMediaDrm, ExoMediaDrm.OnEventListener onEventListener) {
        this.f$0 = frameworkMediaDrm;
        this.f$1 = onEventListener;
    }

    public final void onEvent(MediaDrm mediaDrm, byte[] bArr, int i, int i2, byte[] bArr2) {
        this.f$1.onEvent(this.f$0, bArr, i, i2, bArr2);
    }
}
