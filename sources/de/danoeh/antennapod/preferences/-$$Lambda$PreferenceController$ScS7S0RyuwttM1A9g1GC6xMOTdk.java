package de.danoeh.antennapod.preferences;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import java.io.File;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PreferenceController$ScS7S0RyuwttM1A9g1GC6xMOTdk implements OnClickListener {
    private final /* synthetic */ Context f$0;
    private final /* synthetic */ File f$1;

    public /* synthetic */ -$$Lambda$PreferenceController$ScS7S0RyuwttM1A9g1GC6xMOTdk(Context context, File file) {
        this.f$0 = context;
        this.f$1 = file;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        PreferenceController.lambda$null$46(this.f$0, this.f$1, dialogInterface, i);
    }
}
