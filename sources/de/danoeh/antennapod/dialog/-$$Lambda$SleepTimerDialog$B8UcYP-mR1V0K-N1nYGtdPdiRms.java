package de.danoeh.antennapod.dialog;

import android.view.inputmethod.InputMethodManager;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SleepTimerDialog$B8UcYP-mR1V0K-N1nYGtdPdiRms implements Runnable {
    private final /* synthetic */ SleepTimerDialog f$0;

    public /* synthetic */ -$$Lambda$SleepTimerDialog$B8UcYP-mR1V0K-N1nYGtdPdiRms(SleepTimerDialog sleepTimerDialog) {
        this.f$0 = sleepTimerDialog;
    }

    public final void run() {
        ((InputMethodManager) this.f$0.context.getSystemService("input_method")).showSoftInput(this.f$0.etxtTime, 1);
    }
}
