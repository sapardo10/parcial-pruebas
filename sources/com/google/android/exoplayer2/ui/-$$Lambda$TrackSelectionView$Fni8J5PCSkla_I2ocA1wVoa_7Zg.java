package com.google.android.exoplayer2.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TrackSelectionView$Fni8J5PCSkla_I2ocA1wVoa_7Zg implements OnClickListener {
    private final /* synthetic */ TrackSelectionView f$0;

    public /* synthetic */ -$$Lambda$TrackSelectionView$Fni8J5PCSkla_I2ocA1wVoa_7Zg(TrackSelectionView trackSelectionView) {
        this.f$0 = trackSelectionView;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.applySelection();
    }
}
