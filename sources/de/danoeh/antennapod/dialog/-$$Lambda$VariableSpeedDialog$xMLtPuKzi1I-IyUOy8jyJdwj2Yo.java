package de.danoeh.antennapod.dialog;

import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$VariableSpeedDialog$xMLtPuKzi1I-IyUOy8jyJdwj2Yo implements OnMultiChoiceClickListener {
    private final /* synthetic */ boolean[] f$0;

    public /* synthetic */ -$$Lambda$VariableSpeedDialog$xMLtPuKzi1I-IyUOy8jyJdwj2Yo(boolean[] zArr) {
        this.f$0 = zArr;
    }

    public final void onClick(DialogInterface dialogInterface, int i, boolean z) {
        this.f$0[i] = z;
    }
}
