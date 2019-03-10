package de.danoeh.antennapod.preferences;

import android.app.Activity;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.widget.Toast;
import de.danoeh.antennapod.debug.R;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PreferenceController$9lubbDLWUEbJ6cOEw2gVWFpkENg implements OnPreferenceClickListener {
    private final /* synthetic */ Activity f$0;

    public /* synthetic */ -$$Lambda$PreferenceController$9lubbDLWUEbJ6cOEw2gVWFpkENg(Activity activity) {
        this.f$0 = activity;
    }

    public final boolean onPreferenceClick(Preference preference) {
        return Toast.makeText(this.f$0, R.string.pref_expand_notify_unsupport_toast, 0).show();
    }
}
