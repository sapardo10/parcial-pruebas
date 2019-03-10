package de.danoeh.antennapod.adapter;

import android.view.View;
import android.view.View.OnLongClickListener;
import de.danoeh.antennapod.core.feed.FeedItem;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AllEpisodesRecycleAdapter$IRWLtnYZ4oI8csKpZXWsJ1YLjM0 implements OnLongClickListener {
    private final /* synthetic */ AllEpisodesRecycleAdapter f$0;
    private final /* synthetic */ FeedItem f$1;

    public /* synthetic */ -$$Lambda$AllEpisodesRecycleAdapter$IRWLtnYZ4oI8csKpZXWsJ1YLjM0(AllEpisodesRecycleAdapter allEpisodesRecycleAdapter, FeedItem feedItem) {
        this.f$0 = allEpisodesRecycleAdapter;
        this.f$1 = feedItem;
    }

    public final boolean onLongClick(View view) {
        return this.f$0.selectedItem = this.f$1;
    }
}
