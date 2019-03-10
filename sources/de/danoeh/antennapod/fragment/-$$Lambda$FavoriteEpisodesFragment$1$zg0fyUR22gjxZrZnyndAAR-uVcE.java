package de.danoeh.antennapod.fragment;

import android.view.View;
import android.view.View.OnClickListener;
import de.danoeh.antennapod.core.feed.FeedItem;
import de.danoeh.antennapod.core.storage.DBWriter;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$FavoriteEpisodesFragment$1$zg0fyUR22gjxZrZnyndAAR-uVcE implements OnClickListener {
    private final /* synthetic */ FeedItem f$0;

    public /* synthetic */ -$$Lambda$FavoriteEpisodesFragment$1$zg0fyUR22gjxZrZnyndAAR-uVcE(FeedItem feedItem) {
        this.f$0 = feedItem;
    }

    public final void onClick(View view) {
        DBWriter.addFavoriteItem(this.f$0);
    }
}
