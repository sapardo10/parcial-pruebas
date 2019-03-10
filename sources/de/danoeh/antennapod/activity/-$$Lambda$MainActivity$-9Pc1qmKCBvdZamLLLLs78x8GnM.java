package de.danoeh.antennapod.activity;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import java.util.List;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MainActivity$-9Pc1qmKCBvdZamLLLLs78x8GnM implements OnClickListener {
    private final /* synthetic */ List f$0;

    public /* synthetic */ -$$Lambda$MainActivity$-9Pc1qmKCBvdZamLLLLs78x8GnM(List list) {
        this.f$0 = list;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        UserPreferences.setHiddenDrawerItems(this.f$0);
    }
}
