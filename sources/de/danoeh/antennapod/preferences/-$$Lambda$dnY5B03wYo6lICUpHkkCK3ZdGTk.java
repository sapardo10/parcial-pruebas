package de.danoeh.antennapod.preferences;

import android.app.ProgressDialog;
import io.reactivex.functions.Action;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$dnY5B03wYo6lICUpHkkCK3ZdGTk implements Action {
    private final /* synthetic */ ProgressDialog f$0;

    public /* synthetic */ -$$Lambda$dnY5B03wYo6lICUpHkkCK3ZdGTk(ProgressDialog progressDialog) {
        this.f$0 = progressDialog;
    }

    public final void run() {
        this.f$0.dismiss();
    }
}
