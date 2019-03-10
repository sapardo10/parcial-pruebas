package de.danoeh.antennapod.preferences;

import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PreferenceController$p0MymLeGzjrLWe4bRU6hnoDe0lg implements OnPreferenceChangeListener {
    private final /* synthetic */ PreferenceController f$0;

    public /* synthetic */ -$$Lambda$PreferenceController$p0MymLeGzjrLWe4bRU6hnoDe0lg(PreferenceController preferenceController) {
        this.f$0 = preferenceController;
    }

    public final boolean onPreferenceChange(Preference preference, Object obj) {
        return PreferenceController.lambda$setupAutoDownloadScreen$30(this.f$0, preference, obj);
    }
}
