package de.danoeh.antennapod.preferences;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import de.danoeh.antennapod.debug.R;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PreferenceController$ZbxbQRhOw_W6zKUwyCHJs9jtz3k implements OnPreferenceClickListener {
    private final /* synthetic */ PreferenceController f$0;
    private final /* synthetic */ AppCompatActivity f$1;

    public /* synthetic */ -$$Lambda$PreferenceController$ZbxbQRhOw_W6zKUwyCHJs9jtz3k(PreferenceController preferenceController, AppCompatActivity appCompatActivity) {
        this.f$0 = preferenceController;
        this.f$1 = appCompatActivity;
    }

    public final boolean onPreferenceClick(Preference preference) {
        return this.f$0.openScreen(R.xml.preferences_network, this.f$1);
    }
}
