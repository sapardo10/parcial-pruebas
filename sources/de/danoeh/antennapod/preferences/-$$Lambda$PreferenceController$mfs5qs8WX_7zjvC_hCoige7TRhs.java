package de.danoeh.antennapod.preferences;

import android.app.Activity;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import de.danoeh.antennapod.dialog.VariableSpeedDialog;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PreferenceController$mfs5qs8WX_7zjvC_hCoige7TRhs implements OnPreferenceClickListener {
    private final /* synthetic */ Activity f$0;

    public /* synthetic */ -$$Lambda$PreferenceController$mfs5qs8WX_7zjvC_hCoige7TRhs(Activity activity) {
        this.f$0 = activity;
    }

    public final boolean onPreferenceClick(Preference preference) {
        return VariableSpeedDialog.showDialog(this.f$0);
    }
}
