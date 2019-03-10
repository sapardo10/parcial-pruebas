package de.danoeh.antennapod.preferences;

import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PreferenceController$LDAoqaotcFe9imjC5s7AUNqIQvI implements OnPreferenceChangeListener {
    private final /* synthetic */ PreferenceController f$0;

    public /* synthetic */ -$$Lambda$PreferenceController$LDAoqaotcFe9imjC5s7AUNqIQvI(PreferenceController preferenceController) {
        this.f$0 = preferenceController;
    }

    public final boolean onPreferenceChange(Preference preference, Object obj) {
        return PreferenceController.lambda$setupAutoDownloadScreen$29(this.f$0, preference, obj);
    }
}
