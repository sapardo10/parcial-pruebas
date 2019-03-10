package de.danoeh.antennapod.preferences;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PreferenceController$lLEcGf8ZXapXa1oeY9xv9J2BcJU implements SingleButtonCallback {
    private final /* synthetic */ PreferenceController f$0;

    public /* synthetic */ -$$Lambda$PreferenceController$lLEcGf8ZXapXa1oeY9xv9J2BcJU(PreferenceController preferenceController) {
        this.f$0 = preferenceController;
    }

    public final void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
        PreferenceController.lambda$showUpdateIntervalTimePreferencesDialog$59(this.f$0, materialDialog, dialogAction);
    }
}
