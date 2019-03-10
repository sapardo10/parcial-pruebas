package de.danoeh.antennapod.preferences;

import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PreferenceController$1A8X3xZERkjjzlmnyvgx2CV6WRY implements OnPreferenceChangeListener {
    private final /* synthetic */ PreferenceController f$0;

    public /* synthetic */ -$$Lambda$PreferenceController$1A8X3xZERkjjzlmnyvgx2CV6WRY(PreferenceController preferenceController) {
        this.f$0 = preferenceController;
    }

    public final boolean onPreferenceChange(Preference preference, Object obj) {
        return PreferenceController.lambda$setupNetworkScreen$33(this.f$0, preference, obj);
    }
}
