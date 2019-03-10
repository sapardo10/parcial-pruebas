package de.danoeh.antennapod.preferences;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import de.danoeh.antennapod.dialog.AutoFlattrPreferenceDialog;
import de.danoeh.antennapod.preferences.PreferenceController.C10811;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PreferenceController$EAbcOKuDICtPDC22GmUfDZ2COzE implements OnPreferenceClickListener {
    private final /* synthetic */ PreferenceController f$0;
    private final /* synthetic */ AppCompatActivity f$1;

    public /* synthetic */ -$$Lambda$PreferenceController$EAbcOKuDICtPDC22GmUfDZ2COzE(PreferenceController preferenceController, AppCompatActivity appCompatActivity) {
        this.f$0 = preferenceController;
        this.f$1 = appCompatActivity;
    }

    public final boolean onPreferenceClick(Preference preference) {
        return AutoFlattrPreferenceDialog.newAutoFlattrPreferenceDialog(this.f$1, new C10811());
    }
}
