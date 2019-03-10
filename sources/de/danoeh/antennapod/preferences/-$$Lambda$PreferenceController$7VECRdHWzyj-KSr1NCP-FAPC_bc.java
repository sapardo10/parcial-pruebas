package de.danoeh.antennapod.preferences;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import de.danoeh.antennapod.core.preferences.UserPreferences;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PreferenceController$7VECRdHWzyj-KSr1NCP-FAPC_bc implements OnClickListener {
    private final /* synthetic */ String[] f$0;

    public /* synthetic */ -$$Lambda$PreferenceController$7VECRdHWzyj-KSr1NCP-FAPC_bc(String[] strArr) {
        this.f$0 = strArr;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        UserPreferences.setBackButtonGoToPage(this.f$0[0]);
    }
}
