package de.danoeh.antennapod.dialog;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$RenameFeedDialog$fM7j8FvgpF4I69YD88k_Au0RGo0 implements SingleButtonCallback {
    private final /* synthetic */ RenameFeedDialog f$0;

    public /* synthetic */ -$$Lambda$RenameFeedDialog$fM7j8FvgpF4I69YD88k_Au0RGo0(RenameFeedDialog renameFeedDialog) {
        this.f$0 = renameFeedDialog;
    }

    public final void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
        materialDialog.getInputEditText().setText(this.f$0.feed.getFeedTitle());
    }
}
