package de.danoeh.antennapod.preferences;

import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceClickListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PreferenceController$c96QhSzC2IhXzeb601JLE3-9RMc implements OnPreferenceClickListener {
    private final /* synthetic */ PreferenceController f$0;

    public /* synthetic */ -$$Lambda$PreferenceController$c96QhSzC2IhXzeb601JLE3-9RMc(PreferenceController preferenceController) {
        this.f$0 = preferenceController;
    }

    public final boolean onPreferenceClick(Preference preference) {
        return PreferenceController.lambda$setupGpodderScreen$21(this.f$0, preference);
    }
}
