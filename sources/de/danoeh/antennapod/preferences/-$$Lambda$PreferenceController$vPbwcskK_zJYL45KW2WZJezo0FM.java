package de.danoeh.antennapod.preferences;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import de.danoeh.antennapod.activity.OpmlImportFromPathActivity;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PreferenceController$vPbwcskK_zJYL45KW2WZJezo0FM implements OnPreferenceClickListener {
    private final /* synthetic */ Activity f$0;

    public /* synthetic */ -$$Lambda$PreferenceController$vPbwcskK_zJYL45KW2WZJezo0FM(Activity activity) {
        this.f$0 = activity;
    }

    public final boolean onPreferenceClick(Preference preference) {
        return this.f$0.startActivity(new Intent(this.f$0, OpmlImportFromPathActivity.class));
    }
}
