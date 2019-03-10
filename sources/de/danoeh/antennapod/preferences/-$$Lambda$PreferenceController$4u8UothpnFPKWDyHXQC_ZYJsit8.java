package de.danoeh.antennapod.preferences;

import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import de.danoeh.antennapod.dialog.ProxyDialog;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PreferenceController$4u8UothpnFPKWDyHXQC_ZYJsit8 implements OnPreferenceClickListener {
    private final /* synthetic */ PreferenceController f$0;

    public /* synthetic */ -$$Lambda$PreferenceController$4u8UothpnFPKWDyHXQC_ZYJsit8(PreferenceController preferenceController) {
        this.f$0 = preferenceController;
    }

    public final boolean onPreferenceClick(Preference preference) {
        return new ProxyDialog(this.f$0.ui.getActivity()).createDialog().show();
    }
}
