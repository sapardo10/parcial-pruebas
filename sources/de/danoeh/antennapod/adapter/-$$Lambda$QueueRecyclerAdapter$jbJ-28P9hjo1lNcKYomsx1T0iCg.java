package de.danoeh.antennapod.adapter;

import android.view.View;
import android.view.View.OnLongClickListener;
import de.danoeh.antennapod.core.feed.FeedItem;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$QueueRecyclerAdapter$jbJ-28P9hjo1lNcKYomsx1T0iCg implements OnLongClickListener {
    private final /* synthetic */ QueueRecyclerAdapter f$0;
    private final /* synthetic */ FeedItem f$1;

    public /* synthetic */ -$$Lambda$QueueRecyclerAdapter$jbJ-28P9hjo1lNcKYomsx1T0iCg(QueueRecyclerAdapter queueRecyclerAdapter, FeedItem feedItem) {
        this.f$0 = queueRecyclerAdapter;
        this.f$1 = feedItem;
    }

    public final boolean onLongClick(View view) {
        return this.f$0.selectedItem = this.f$1;
    }
}
