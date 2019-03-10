package de.danoeh.antennapod.activity;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$OnlineFeedViewActivity$0ruhzTbTxij3R71tZCke3tRvsCQ implements OnCancelListener {
    private final /* synthetic */ OnlineFeedViewActivity f$0;

    public /* synthetic */ -$$Lambda$OnlineFeedViewActivity$0ruhzTbTxij3R71tZCke3tRvsCQ(OnlineFeedViewActivity onlineFeedViewActivity) {
        this.f$0 = onlineFeedViewActivity;
    }

    public final void onCancel(DialogInterface dialogInterface) {
        OnlineFeedViewActivity.lambda$showErrorDialog$8(this.f$0, dialogInterface);
    }
}
