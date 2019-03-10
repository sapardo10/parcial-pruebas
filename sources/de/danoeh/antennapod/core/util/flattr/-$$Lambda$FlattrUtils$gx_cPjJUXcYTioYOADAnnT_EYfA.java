package de.danoeh.antennapod.core.util.flattr;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import de.danoeh.antennapod.core.ClientConfig;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$FlattrUtils$gx_cPjJUXcYTioYOADAnnT_EYfA implements OnClickListener {
    private final /* synthetic */ Context f$0;

    public /* synthetic */ -$$Lambda$FlattrUtils$gx_cPjJUXcYTioYOADAnnT_EYfA(Context context) {
        this.f$0 = context;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.startActivity(ClientConfig.flattrCallbacks.getFlattrAuthenticationActivityIntent(this.f$0));
    }
}
