package de.danoeh.antennapod.dialog;

import de.danoeh.antennapod.core.feed.FeedItem;
import java.util.Comparator;

/* compiled from: lambda */
/* renamed from: de.danoeh.antennapod.dialog.-$$Lambda$EpisodesApplyActionFragment$1qujNBltTpR2g5NLBRDQvntZUA0 */
public final /* synthetic */ class C0768x383d62b8 implements Comparator {
    private final /* synthetic */ boolean f$0;

    public /* synthetic */ C0768x383d62b8(boolean z) {
        this.f$0 = z;
    }

    public final int compare(Object obj, Object obj2) {
        return EpisodesApplyActionFragment.lambda$sortByTitle$9(this.f$0, (FeedItem) obj, (FeedItem) obj2);
    }
}
