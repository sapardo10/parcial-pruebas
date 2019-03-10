package de.danoeh.antennapod.preferences;

import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import java.util.List;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PreferenceController$hBl-ppZ0dlOiswwu4sY0LaxzAcA implements OnMultiChoiceClickListener {
    private final /* synthetic */ List f$0;
    private final /* synthetic */ String[] f$1;

    public /* synthetic */ -$$Lambda$PreferenceController$hBl-ppZ0dlOiswwu4sY0LaxzAcA(List list, String[] strArr) {
        this.f$0 = list;
        this.f$1 = strArr;
    }

    public final void onClick(DialogInterface dialogInterface, int i, boolean z) {
        PreferenceController.lambda$showDrawerPreferencesDialog$51(this.f$0, this.f$1, dialogInterface, i, z);
    }
}
