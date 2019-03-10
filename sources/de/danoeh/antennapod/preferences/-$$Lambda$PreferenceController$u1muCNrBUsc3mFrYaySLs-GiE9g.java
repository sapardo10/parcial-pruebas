package de.danoeh.antennapod.preferences;

import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceClickListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PreferenceController$u1muCNrBUsc3mFrYaySLs-GiE9g implements OnPreferenceClickListener {
    private final /* synthetic */ PreferenceController f$0;

    public /* synthetic */ -$$Lambda$PreferenceController$u1muCNrBUsc3mFrYaySLs-GiE9g(PreferenceController preferenceController) {
        this.f$0 = preferenceController;
    }

    public final boolean onPreferenceClick(Preference preference) {
        return this.f$0.showUpdateIntervalTimePreferencesDialog();
    }
}
