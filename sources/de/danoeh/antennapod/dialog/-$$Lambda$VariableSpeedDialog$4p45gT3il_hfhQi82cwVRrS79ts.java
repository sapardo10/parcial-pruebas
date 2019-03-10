package de.danoeh.antennapod.dialog;

import android.content.Context;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$VariableSpeedDialog$4p45gT3il_hfhQi82cwVRrS79ts implements SingleButtonCallback {
    private final /* synthetic */ boolean f$0;
    private final /* synthetic */ Context f$1;

    public /* synthetic */ -$$Lambda$VariableSpeedDialog$4p45gT3il_hfhQi82cwVRrS79ts(boolean z, Context context) {
        this.f$0 = z;
        this.f$1 = context;
    }

    public final void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
        VariableSpeedDialog.lambda$showGetPluginDialog$0(this.f$0, this.f$1, materialDialog, dialogAction);
    }
}
