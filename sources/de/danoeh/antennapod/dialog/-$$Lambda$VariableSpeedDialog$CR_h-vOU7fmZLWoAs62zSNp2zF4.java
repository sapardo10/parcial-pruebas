package de.danoeh.antennapod.dialog;

import android.content.Context;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$VariableSpeedDialog$CR_h-vOU7fmZLWoAs62zSNp2zF4 implements SingleButtonCallback {
    private final /* synthetic */ Context f$0;

    public /* synthetic */ -$$Lambda$VariableSpeedDialog$CR_h-vOU7fmZLWoAs62zSNp2zF4(Context context) {
        this.f$0 = context;
    }

    public final void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
        VariableSpeedDialog.lambda$showGetPluginDialog$1(this.f$0, materialDialog, dialogAction);
    }
}
