package de.danoeh.antennapod.preferences;

import android.app.Activity;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PreferenceController$i5akq6ullj5k-wtK3H_NZWWWo14 implements OnPreferenceChangeListener {
    private final /* synthetic */ Activity f$0;

    public /* synthetic */ -$$Lambda$PreferenceController$i5akq6ullj5k-wtK3H_NZWWWo14(Activity activity) {
        this.f$0 = activity;
    }

    public final boolean onPreferenceChange(Preference preference, Object obj) {
        return PreferenceController.lambda$setupInterfaceScreen$2(this.f$0, preference, obj);
    }
}
