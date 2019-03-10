package de.danoeh.antennapod.preferences;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import de.danoeh.antennapod.dialog.GpodnetSetHostnameDialog;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PreferenceController$MIHS5QQPK9xyeLYv4_LAulz7ziQ implements OnPreferenceClickListener {
    private final /* synthetic */ PreferenceController f$0;
    private final /* synthetic */ AppCompatActivity f$1;

    public /* synthetic */ -$$Lambda$PreferenceController$MIHS5QQPK9xyeLYv4_LAulz7ziQ(PreferenceController preferenceController, AppCompatActivity appCompatActivity) {
        this.f$0 = preferenceController;
        this.f$1 = appCompatActivity;
    }

    public final boolean onPreferenceClick(Preference preference) {
        return GpodnetSetHostnameDialog.createDialog(this.f$1).setOnDismissListener(new -$$Lambda$PreferenceController$W1NsrsKpl7F-MKv1yxVhx-SuSKQ(this.f$0));
    }
}
