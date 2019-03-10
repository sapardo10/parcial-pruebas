package de.danoeh.antennapod.dialog;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$RatingDialog$UXlhRD8jLtRAGPc1YKaO-MIAWs0 implements SingleButtonCallback {
    public static final /* synthetic */ -$$Lambda$RatingDialog$UXlhRD8jLtRAGPc1YKaO-MIAWs0 INSTANCE = new -$$Lambda$RatingDialog$UXlhRD8jLtRAGPc1YKaO-MIAWs0();

    private /* synthetic */ -$$Lambda$RatingDialog$UXlhRD8jLtRAGPc1YKaO-MIAWs0() {
    }

    public final void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
        RatingDialog.saveRated();
    }
}
