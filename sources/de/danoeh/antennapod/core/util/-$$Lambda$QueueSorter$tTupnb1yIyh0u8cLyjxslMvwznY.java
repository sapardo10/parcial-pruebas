package de.danoeh.antennapod.core.util;

import de.danoeh.antennapod.core.feed.FeedItem;
import java.util.Comparator;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$QueueSorter$tTupnb1yIyh0u8cLyjxslMvwznY implements Comparator {
    public static final /* synthetic */ -$$Lambda$QueueSorter$tTupnb1yIyh0u8cLyjxslMvwznY INSTANCE = new -$$Lambda$QueueSorter$tTupnb1yIyh0u8cLyjxslMvwznY();

    private /* synthetic */ -$$Lambda$QueueSorter$tTupnb1yIyh0u8cLyjxslMvwznY() {
    }

    public final int compare(Object obj, Object obj2) {
        return ((FeedItem) obj).getPubDate().compareTo(((FeedItem) obj2).getPubDate());
    }
}
