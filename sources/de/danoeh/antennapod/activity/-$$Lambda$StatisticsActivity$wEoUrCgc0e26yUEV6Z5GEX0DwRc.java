package de.danoeh.antennapod.activity;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$StatisticsActivity$wEoUrCgc0e26yUEV6Z5GEX0DwRc implements OnClickListener {
    private final /* synthetic */ StatisticsActivity f$0;
    private final /* synthetic */ View f$1;

    public /* synthetic */ -$$Lambda$StatisticsActivity$wEoUrCgc0e26yUEV6Z5GEX0DwRc(StatisticsActivity statisticsActivity, View view) {
        this.f$0 = statisticsActivity;
        this.f$1 = view;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        StatisticsActivity.lambda$selectStatisticsMode$0(this.f$0, this.f$1, dialogInterface, i);
    }
}
