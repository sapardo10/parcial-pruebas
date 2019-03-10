package de.danoeh.antennapod.preferences;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import de.danoeh.antennapod.core.preferences.GpodnetPreferences;
import de.danoeh.antennapod.debug.R;
import de.danoeh.antennapod.dialog.AuthenticationDialog;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PreferenceController$kjbJW0F64sUFwbfYt6TUZeCDsqU implements OnPreferenceClickListener {
    private final /* synthetic */ PreferenceController f$0;
    private final /* synthetic */ AppCompatActivity f$1;

    public /* synthetic */ -$$Lambda$PreferenceController$kjbJW0F64sUFwbfYt6TUZeCDsqU(PreferenceController preferenceController, AppCompatActivity appCompatActivity) {
        this.f$0 = preferenceController;
        this.f$1 = appCompatActivity;
    }

    public final boolean onPreferenceClick(Preference preference) {
        return new AuthenticationDialog(this.f$1, R.string.pref_gpodnet_setlogin_information_title, false, false, GpodnetPreferences.getUsername(), null) {
            protected void onConfirmed(String username, String password, boolean saveUsernamePassword) {
                GpodnetPreferences.setPassword(password);
            }
        }.show();
    }
}
