package de.danoeh.antennapod.fragment;

import android.view.View;
import android.view.View.OnClickListener;
import de.danoeh.antennapod.core.feed.FeedItem;
import de.danoeh.antennapod.fragment.QueueFragment.C11223;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$QueueFragment$3$8oEcf0dZ0vuGCv-7_MEwd2k_HGk implements OnClickListener {
    private final /* synthetic */ C11223 f$0;
    private final /* synthetic */ FeedItem f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ boolean f$3;

    public /* synthetic */ -$$Lambda$QueueFragment$3$8oEcf0dZ0vuGCv-7_MEwd2k_HGk(C11223 c11223, FeedItem feedItem, int i, boolean z) {
        this.f$0 = c11223;
        this.f$1 = feedItem;
        this.f$2 = i;
        this.f$3 = z;
    }

    public final void onClick(View view) {
        C11223.lambda$onSwiped$0(this.f$0, this.f$1, this.f$2, this.f$3, view);
    }
}
