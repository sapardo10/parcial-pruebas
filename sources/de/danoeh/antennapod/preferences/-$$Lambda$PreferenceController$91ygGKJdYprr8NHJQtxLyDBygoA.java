package de.danoeh.antennapod.preferences;

import android.app.Activity;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceClickListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PreferenceController$91ygGKJdYprr8NHJQtxLyDBygoA implements OnPreferenceClickListener {
    private final /* synthetic */ PreferenceController f$0;
    private final /* synthetic */ Activity f$1;

    public /* synthetic */ -$$Lambda$PreferenceController$91ygGKJdYprr8NHJQtxLyDBygoA(PreferenceController preferenceController, Activity activity) {
        this.f$0 = preferenceController;
        this.f$1 = activity;
    }

    public final boolean onPreferenceClick(Preference preference) {
        return PreferenceController.lambda$setupStorageScreen$12(this.f$0, this.f$1, preference);
    }
}
