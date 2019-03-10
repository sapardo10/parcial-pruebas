package de.danoeh.antennapod.preferences;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import de.danoeh.antennapod.activity.StatisticsActivity;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PreferenceController$jI5-KI0FQHGcRHbJZN_HZiJ0HYI implements OnPreferenceClickListener {
    private final /* synthetic */ AppCompatActivity f$0;

    public /* synthetic */ -$$Lambda$PreferenceController$jI5-KI0FQHGcRHbJZN_HZiJ0HYI(AppCompatActivity appCompatActivity) {
        this.f$0 = appCompatActivity;
    }

    public final boolean onPreferenceClick(Preference preference) {
        return this.f$0.startActivity(new Intent(this.f$0, StatisticsActivity.class));
    }
}
