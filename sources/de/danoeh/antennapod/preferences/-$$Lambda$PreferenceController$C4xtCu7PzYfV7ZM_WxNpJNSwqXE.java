package de.danoeh.antennapod.preferences;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PreferenceController$C4xtCu7PzYfV7ZM_WxNpJNSwqXE implements OnSharedPreferenceChangeListener {
    private final /* synthetic */ PreferenceController f$0;

    public /* synthetic */ -$$Lambda$PreferenceController$C4xtCu7PzYfV7ZM_WxNpJNSwqXE(PreferenceController preferenceController) {
        this.f$0 = preferenceController;
    }

    public final void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
        PreferenceController.lambda$new$0(this.f$0, sharedPreferences, str);
    }
}
