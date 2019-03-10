package de.danoeh.antennapod.preferences;

import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceClickListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PreferenceController$15t1PAKba7ruLaeg5KL2CHE3r2w implements OnPreferenceClickListener {
    private final /* synthetic */ PreferenceController f$0;

    public /* synthetic */ -$$Lambda$PreferenceController$15t1PAKba7ruLaeg5KL2CHE3r2w(PreferenceController preferenceController) {
        this.f$0 = preferenceController;
    }

    public final boolean onPreferenceClick(Preference preference) {
        return this.f$0.showNotificationButtonsDialog();
    }
}
