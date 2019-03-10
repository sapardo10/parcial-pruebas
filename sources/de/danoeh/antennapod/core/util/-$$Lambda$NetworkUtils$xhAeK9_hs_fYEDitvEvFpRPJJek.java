package de.danoeh.antennapod.core.util;

import de.danoeh.antennapod.core.feed.FeedMedia;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$NetworkUtils$xhAeK9_hs_fYEDitvEvFpRPJJek implements SingleOnSubscribe {
    private final /* synthetic */ FeedMedia f$0;

    public /* synthetic */ -$$Lambda$NetworkUtils$xhAeK9_hs_fYEDitvEvFpRPJJek(FeedMedia feedMedia) {
        this.f$0 = feedMedia;
    }

    public final void subscribe(SingleEmitter singleEmitter) {
        NetworkUtils.lambda$getFeedMediaSizeObservable$0(this.f$0, singleEmitter);
    }
}
