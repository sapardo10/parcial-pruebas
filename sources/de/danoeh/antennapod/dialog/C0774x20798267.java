package de.danoeh.antennapod.dialog;

import de.danoeh.antennapod.core.feed.FeedItem;
import java.util.Comparator;

/* compiled from: lambda */
/* renamed from: de.danoeh.antennapod.dialog.-$$Lambda$EpisodesApplyActionFragment$aa3mTvtUPSK-A9LL29DB_L9Efg8 */
public final /* synthetic */ class C0774x20798267 implements Comparator {
    private final /* synthetic */ boolean f$0;

    public /* synthetic */ C0774x20798267(boolean z) {
        this.f$0 = z;
    }

    public final int compare(Object obj, Object obj2) {
        return EpisodesApplyActionFragment.lambda$sortByDate$10(this.f$0, (FeedItem) obj, (FeedItem) obj2);
    }
}
