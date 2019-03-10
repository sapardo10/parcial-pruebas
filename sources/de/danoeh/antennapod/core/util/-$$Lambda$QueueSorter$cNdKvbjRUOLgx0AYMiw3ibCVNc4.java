package de.danoeh.antennapod.core.util;

import de.danoeh.antennapod.core.feed.FeedItem;
import java.util.Comparator;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$QueueSorter$cNdKvbjRUOLgx0AYMiw3ibCVNc4 implements Comparator {
    public static final /* synthetic */ -$$Lambda$QueueSorter$cNdKvbjRUOLgx0AYMiw3ibCVNc4 INSTANCE = new -$$Lambda$QueueSorter$cNdKvbjRUOLgx0AYMiw3ibCVNc4();

    private /* synthetic */ -$$Lambda$QueueSorter$cNdKvbjRUOLgx0AYMiw3ibCVNc4() {
    }

    public final int compare(Object obj, Object obj2) {
        return ((FeedItem) obj).getFeed().getTitle().compareTo(((FeedItem) obj2).getFeed().getTitle());
    }
}
