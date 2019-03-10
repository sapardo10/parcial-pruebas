package de.danoeh.antennapod.core.dialog;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ConfirmationDialog$tf2Kp94g-5DgxIN1o-Sgp7V53U4 implements OnCancelListener {
    private final /* synthetic */ ConfirmationDialog f$0;

    public /* synthetic */ -$$Lambda$ConfirmationDialog$tf2Kp94g-5DgxIN1o-Sgp7V53U4(ConfirmationDialog confirmationDialog) {
        this.f$0 = confirmationDialog;
    }

    public final void onCancel(DialogInterface dialogInterface) {
        this.f$0.onCancelButtonPressed(dialogInterface);
    }
}
