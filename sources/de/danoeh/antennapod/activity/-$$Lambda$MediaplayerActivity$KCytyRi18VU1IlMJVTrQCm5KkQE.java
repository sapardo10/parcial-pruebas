package de.danoeh.antennapod.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaplayerActivity$KCytyRi18VU1IlMJVTrQCm5KkQE implements OnClickListener {
    private final /* synthetic */ int[] f$0;
    private final /* synthetic */ MediaplayerActivity$SkipDirection f$1;
    private final /* synthetic */ Activity f$2;

    public /* synthetic */ -$$Lambda$MediaplayerActivity$KCytyRi18VU1IlMJVTrQCm5KkQE(int[] iArr, MediaplayerActivity$SkipDirection mediaplayerActivity$SkipDirection, Activity activity) {
        this.f$0 = iArr;
        this.f$1 = mediaplayerActivity$SkipDirection;
        this.f$2 = activity;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        MediaplayerActivity.lambda$showSkipPreference$7(this.f$0, this.f$1, this.f$2, dialogInterface, i);
    }
}
