package de.danoeh.antennapod.activity;

import de.danoeh.antennapod.core.syndication.handler.FeedHandlerResult;
import io.reactivex.functions.Consumer;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$OnlineFeedViewActivity$FGRsngQ0GPHU48sk50fr5rxWtKU implements Consumer {
    private final /* synthetic */ OnlineFeedViewActivity f$0;

    public /* synthetic */ -$$Lambda$OnlineFeedViewActivity$FGRsngQ0GPHU48sk50fr5rxWtKU(OnlineFeedViewActivity onlineFeedViewActivity) {
        this.f$0 = onlineFeedViewActivity;
    }

    public final void accept(Object obj) {
        OnlineFeedViewActivity.lambda$parseFeed$4(this.f$0, (FeedHandlerResult) obj);
    }
}
