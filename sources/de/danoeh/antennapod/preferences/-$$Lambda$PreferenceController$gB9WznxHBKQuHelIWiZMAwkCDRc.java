package de.danoeh.antennapod.preferences;

import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import de.danoeh.antennapod.core.export.html.HtmlWriter;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PreferenceController$gB9WznxHBKQuHelIWiZMAwkCDRc implements OnPreferenceClickListener {
    private final /* synthetic */ PreferenceController f$0;

    public /* synthetic */ -$$Lambda$PreferenceController$gB9WznxHBKQuHelIWiZMAwkCDRc(PreferenceController preferenceController) {
        this.f$0 = preferenceController;
    }

    public final boolean onPreferenceClick(Preference preference) {
        return this.f$0.export(new HtmlWriter());
    }
}
