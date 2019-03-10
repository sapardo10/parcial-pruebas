package de.danoeh.antennapod.core.dialog;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ConfirmationDialog$Vplom3vxt3FIdlWqiENHduZboYs implements OnClickListener {
    private final /* synthetic */ ConfirmationDialog f$0;

    public /* synthetic */ -$$Lambda$ConfirmationDialog$Vplom3vxt3FIdlWqiENHduZboYs(ConfirmationDialog confirmationDialog) {
        this.f$0 = confirmationDialog;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.onConfirmButtonPressed(dialogInterface);
    }
}
