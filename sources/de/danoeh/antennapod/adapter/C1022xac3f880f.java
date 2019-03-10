package de.danoeh.antennapod.adapter;

import android.content.Context;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback;
import de.danoeh.antennapod.core.feed.FeedItem;

/* compiled from: lambda */
/* renamed from: de.danoeh.antennapod.adapter.-$$Lambda$DefaultActionButtonCallback$_cMrNC4CN3PMaS1plt09rPrxa8A */
public final /* synthetic */ class C1022xac3f880f implements SingleButtonCallback {
    private final /* synthetic */ Context f$0;
    private final /* synthetic */ FeedItem f$1;

    public /* synthetic */ C1022xac3f880f(Context context, FeedItem feedItem) {
        this.f$0 = context;
        this.f$1 = feedItem;
    }

    public final void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
        DefaultActionButtonCallback.lambda$confirmMobileDownload$1(this.f$0, this.f$1, materialDialog, dialogAction);
    }
}
