package de.danoeh.antennapod.dialog;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SleepTimerDialog$XsjWyT2mrBjGBlHZO3YIH8KrYTo implements SingleButtonCallback {
    private final /* synthetic */ SleepTimerDialog f$0;

    public /* synthetic */ -$$Lambda$SleepTimerDialog$XsjWyT2mrBjGBlHZO3YIH8KrYTo(SleepTimerDialog sleepTimerDialog) {
        this.f$0 = sleepTimerDialog;
    }

    public final void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
        SleepTimerDialog.lambda$createNewDialog$1(this.f$0, materialDialog, dialogAction);
    }
}
