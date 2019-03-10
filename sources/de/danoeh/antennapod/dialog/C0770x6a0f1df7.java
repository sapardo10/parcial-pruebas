package de.danoeh.antennapod.dialog;

import de.danoeh.antennapod.core.feed.FeedItem;
import java.util.Comparator;

/* compiled from: lambda */
/* renamed from: de.danoeh.antennapod.dialog.-$$Lambda$EpisodesApplyActionFragment$7pnm2FkNFJPujzs2r5cCSzhlD-M */
public final /* synthetic */ class C0770x6a0f1df7 implements Comparator {
    private final /* synthetic */ boolean f$0;

    public /* synthetic */ C0770x6a0f1df7(boolean z) {
        this.f$0 = z;
    }

    public final int compare(Object obj, Object obj2) {
        return EpisodesApplyActionFragment.lambda$sortByDuration$11(this.f$0, (FeedItem) obj, (FeedItem) obj2);
    }
}
