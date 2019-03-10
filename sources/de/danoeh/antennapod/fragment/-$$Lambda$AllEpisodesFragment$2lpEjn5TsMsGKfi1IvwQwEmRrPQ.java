package de.danoeh.antennapod.fragment;

import de.danoeh.antennapod.core.feed.FeedItem;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AllEpisodesFragment$2lpEjn5TsMsGKfi1IvwQwEmRrPQ implements Runnable {
    private final /* synthetic */ AllEpisodesFragment f$0;
    private final /* synthetic */ FeedItem f$1;

    public /* synthetic */ -$$Lambda$AllEpisodesFragment$2lpEjn5TsMsGKfi1IvwQwEmRrPQ(AllEpisodesFragment allEpisodesFragment, FeedItem feedItem) {
        this.f$0 = allEpisodesFragment;
        this.f$1 = feedItem;
    }

    public final void run() {
        AllEpisodesFragment.lambda$markItemAsSeenWithUndo$3(this.f$0, this.f$1);
    }
}
