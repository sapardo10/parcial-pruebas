package de.danoeh.antennapod.fragment;

import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import de.danoeh.antennapod.core.feed.FeedItem;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AllEpisodesFragment$jMrfY7XnrY0AO_SWKH1CwR9EJ6k implements OnClickListener {
    private final /* synthetic */ FeedItem f$0;
    private final /* synthetic */ Handler f$1;
    private final /* synthetic */ Runnable f$2;

    public /* synthetic */ -$$Lambda$AllEpisodesFragment$jMrfY7XnrY0AO_SWKH1CwR9EJ6k(FeedItem feedItem, Handler handler, Runnable runnable) {
        this.f$0 = feedItem;
        this.f$1 = handler;
        this.f$2 = runnable;
    }

    public final void onClick(View view) {
        AllEpisodesFragment.lambda$markItemAsSeenWithUndo$4(this.f$0, this.f$1, this.f$2, view);
    }
}
