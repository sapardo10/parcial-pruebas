package de.danoeh.antennapod.preferences;

import android.app.Activity;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import de.danoeh.antennapod.activity.MediaplayerActivity;
import de.danoeh.antennapod.activity.MediaplayerActivity$SkipDirection;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PreferenceController$pVFpvCjNZrM_GiQ7nZRMCbgi9YA implements OnPreferenceClickListener {
    private final /* synthetic */ Activity f$0;

    public /* synthetic */ -$$Lambda$PreferenceController$pVFpvCjNZrM_GiQ7nZRMCbgi9YA(Activity activity) {
        this.f$0 = activity;
    }

    public final boolean onPreferenceClick(Preference preference) {
        return MediaplayerActivity.showSkipPreference(this.f$0, MediaplayerActivity$SkipDirection.SKIP_REWIND);
    }
}
