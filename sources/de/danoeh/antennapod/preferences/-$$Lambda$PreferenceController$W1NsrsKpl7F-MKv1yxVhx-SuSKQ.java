package de.danoeh.antennapod.preferences;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PreferenceController$W1NsrsKpl7F-MKv1yxVhx-SuSKQ implements OnDismissListener {
    private final /* synthetic */ PreferenceController f$0;

    public /* synthetic */ -$$Lambda$PreferenceController$W1NsrsKpl7F-MKv1yxVhx-SuSKQ(PreferenceController preferenceController) {
        this.f$0 = preferenceController;
    }

    public final void onDismiss(DialogInterface dialogInterface) {
        this.f$0.updateGpodnetPreferenceScreen();
    }
}
