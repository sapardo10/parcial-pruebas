package de.danoeh.antennapod.activity;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$OpmlImportBaseActivity$xR1-Oy9k22CPx73hEflPhnObSig implements SingleButtonCallback {
    private final /* synthetic */ OpmlImportBaseActivity f$0;

    public /* synthetic */ -$$Lambda$OpmlImportBaseActivity$xR1-Oy9k22CPx73hEflPhnObSig(OpmlImportBaseActivity opmlImportBaseActivity) {
        this.f$0 = opmlImportBaseActivity;
    }

    public final void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
        this.f$0.requestPermission();
    }
}
