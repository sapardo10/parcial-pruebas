package de.danoeh.antennapod.core.util;

import de.danoeh.antennapod.core.feed.FeedItem;
import java.util.Comparator;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$QueueSorter$vrQ-zZlU3AabIhLAKR9ClDzKCRo implements Comparator {
    public static final /* synthetic */ -$$Lambda$QueueSorter$vrQ-zZlU3AabIhLAKR9ClDzKCRo INSTANCE = new -$$Lambda$QueueSorter$vrQ-zZlU3AabIhLAKR9ClDzKCRo();

    private /* synthetic */ -$$Lambda$QueueSorter$vrQ-zZlU3AabIhLAKR9ClDzKCRo() {
    }

    public final int compare(Object obj, Object obj2) {
        return ((FeedItem) obj).getPubDate().compareTo(((FeedItem) obj2).getPubDate());
    }
}
