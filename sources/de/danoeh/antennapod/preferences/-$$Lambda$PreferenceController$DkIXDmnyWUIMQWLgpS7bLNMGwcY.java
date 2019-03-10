package de.danoeh.antennapod.preferences;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceClickListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PreferenceController$DkIXDmnyWUIMQWLgpS7bLNMGwcY implements OnPreferenceClickListener {
    private final /* synthetic */ PreferenceController f$0;
    private final /* synthetic */ AppCompatActivity f$1;

    public /* synthetic */ -$$Lambda$PreferenceController$DkIXDmnyWUIMQWLgpS7bLNMGwcY(PreferenceController preferenceController, AppCompatActivity appCompatActivity) {
        this.f$0 = preferenceController;
        this.f$1 = appCompatActivity;
    }

    public final boolean onPreferenceClick(Preference preference) {
        return PreferenceController.lambda$setupGpodderScreen$22(this.f$0, this.f$1, preference);
    }
}
