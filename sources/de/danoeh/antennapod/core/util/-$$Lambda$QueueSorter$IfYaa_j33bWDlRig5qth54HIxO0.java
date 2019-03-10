package de.danoeh.antennapod.core.util;

import de.danoeh.antennapod.core.feed.FeedItem;
import java.util.Comparator;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$QueueSorter$IfYaa_j33bWDlRig5qth54HIxO0 implements Comparator {
    public static final /* synthetic */ -$$Lambda$QueueSorter$IfYaa_j33bWDlRig5qth54HIxO0 INSTANCE = new -$$Lambda$QueueSorter$IfYaa_j33bWDlRig5qth54HIxO0();

    private /* synthetic */ -$$Lambda$QueueSorter$IfYaa_j33bWDlRig5qth54HIxO0() {
    }

    public final int compare(Object obj, Object obj2) {
        return ((FeedItem) obj2).getPubDate().compareTo(((FeedItem) obj).getPubDate());
    }
}
