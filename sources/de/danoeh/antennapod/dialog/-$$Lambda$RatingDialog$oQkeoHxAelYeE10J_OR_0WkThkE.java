package de.danoeh.antennapod.dialog;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$RatingDialog$oQkeoHxAelYeE10J_OR_0WkThkE implements SingleButtonCallback {
    public static final /* synthetic */ -$$Lambda$RatingDialog$oQkeoHxAelYeE10J_OR_0WkThkE INSTANCE = new -$$Lambda$RatingDialog$oQkeoHxAelYeE10J_OR_0WkThkE();

    private /* synthetic */ -$$Lambda$RatingDialog$oQkeoHxAelYeE10J_OR_0WkThkE() {
    }

    public final void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
        RatingDialog.rateNow();
    }
}
