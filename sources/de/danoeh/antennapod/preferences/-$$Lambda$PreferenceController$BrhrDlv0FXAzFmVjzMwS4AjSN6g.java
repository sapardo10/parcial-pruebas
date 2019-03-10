package de.danoeh.antennapod.preferences;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import java.util.List;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PreferenceController$BrhrDlv0FXAzFmVjzMwS4AjSN6g implements OnClickListener {
    private final /* synthetic */ List f$0;

    public /* synthetic */ -$$Lambda$PreferenceController$BrhrDlv0FXAzFmVjzMwS4AjSN6g(List list) {
        this.f$0 = list;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        UserPreferences.setHiddenDrawerItems(this.f$0);
    }
}
