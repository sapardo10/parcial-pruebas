package de.danoeh.antennapod.activity;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import java.util.List;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$OnlineFeedViewActivity$vL4h2Yl5zSxPIvSxkt_fo8wjbEE implements OnClickListener {
    private final /* synthetic */ OnlineFeedViewActivity f$0;
    private final /* synthetic */ List f$1;
    private final /* synthetic */ List f$2;

    public /* synthetic */ -$$Lambda$OnlineFeedViewActivity$vL4h2Yl5zSxPIvSxkt_fo8wjbEE(OnlineFeedViewActivity onlineFeedViewActivity, List list, List list2) {
        this.f$0 = onlineFeedViewActivity;
        this.f$1 = list;
        this.f$2 = list2;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        OnlineFeedViewActivity.lambda$showFeedDiscoveryDialog$9(this.f$0, this.f$1, this.f$2, dialogInterface, i);
    }
}
