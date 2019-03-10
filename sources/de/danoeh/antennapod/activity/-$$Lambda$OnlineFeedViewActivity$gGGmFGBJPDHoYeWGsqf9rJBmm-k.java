package de.danoeh.antennapod.activity;

import android.view.View;
import android.view.View.OnClickListener;
import de.danoeh.antennapod.core.feed.Feed;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$OnlineFeedViewActivity$gGGmFGBJPDHoYeWGsqf9rJBmm-k implements OnClickListener {
    private final /* synthetic */ OnlineFeedViewActivity f$0;
    private final /* synthetic */ Feed f$1;

    public /* synthetic */ -$$Lambda$OnlineFeedViewActivity$gGGmFGBJPDHoYeWGsqf9rJBmm-k(OnlineFeedViewActivity onlineFeedViewActivity, Feed feed) {
        this.f$0 = onlineFeedViewActivity;
        this.f$1 = feed;
    }

    public final void onClick(View view) {
        OnlineFeedViewActivity.lambda$showFeedInformation$6(this.f$0, this.f$1, view);
    }
}
