package de.danoeh.antennapod.preferences;

import android.content.Context;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PreferenceController$vElVXMfIDk9ZtS2NRLo7kwcicnk implements SingleButtonCallback {
    private final /* synthetic */ PreferenceController f$0;
    private final /* synthetic */ Context f$1;

    public /* synthetic */ -$$Lambda$PreferenceController$vElVXMfIDk9ZtS2NRLo7kwcicnk(PreferenceController preferenceController, Context context) {
        this.f$0 = preferenceController;
        this.f$1 = context;
    }

    public final void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
        PreferenceController.lambda$showUpdateIntervalTimePreferencesDialog$58(this.f$0, this.f$1, materialDialog, dialogAction);
    }
}
