package de.danoeh.antennapod.activity;

import de.danoeh.antennapod.core.feed.FeedItem;
import io.reactivex.functions.Consumer;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaplayerActivity$R4Iss22D_b5H7EzxzVFAUEH-U3M implements Consumer {
    private final /* synthetic */ MediaplayerActivity f$0;

    public /* synthetic */ -$$Lambda$MediaplayerActivity$R4Iss22D_b5H7EzxzVFAUEH-U3M(MediaplayerActivity mediaplayerActivity) {
        this.f$0 = mediaplayerActivity;
    }

    public final void accept(Object obj) {
        MediaplayerActivity.lambda$checkFavorite$17(this.f$0, (FeedItem) obj);
    }
}
