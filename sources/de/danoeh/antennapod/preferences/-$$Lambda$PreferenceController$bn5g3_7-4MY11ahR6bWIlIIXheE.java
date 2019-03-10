package de.danoeh.antennapod.preferences;

import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import de.danoeh.antennapod.core.export.opml.OpmlWriter;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PreferenceController$bn5g3_7-4MY11ahR6bWIlIIXheE implements OnPreferenceClickListener {
    private final /* synthetic */ PreferenceController f$0;

    public /* synthetic */ -$$Lambda$PreferenceController$bn5g3_7-4MY11ahR6bWIlIIXheE(PreferenceController preferenceController) {
        this.f$0 = preferenceController;
    }

    public final boolean onPreferenceClick(Preference preference) {
        return this.f$0.export(new OpmlWriter());
    }
}
