package de.danoeh.antennapod.preferences;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import java.util.List;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PreferenceController$s_CUTHnHR3HkKr9Xn35aTmDDky4 implements OnMultiChoiceClickListener {
    private final /* synthetic */ boolean[] f$0;
    private final /* synthetic */ List f$1;
    private final /* synthetic */ Context f$2;

    public /* synthetic */ -$$Lambda$PreferenceController$s_CUTHnHR3HkKr9Xn35aTmDDky4(boolean[] zArr, List list, Context context) {
        this.f$0 = zArr;
        this.f$1 = list;
        this.f$2 = context;
    }

    public final void onClick(DialogInterface dialogInterface, int i, boolean z) {
        PreferenceController.lambda$showNotificationButtonsDialog$53(this.f$0, this.f$1, this.f$2, dialogInterface, i, z);
    }
}
