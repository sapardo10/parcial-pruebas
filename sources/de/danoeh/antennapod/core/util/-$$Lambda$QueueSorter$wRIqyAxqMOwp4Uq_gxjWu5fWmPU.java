package de.danoeh.antennapod.core.util;

import de.danoeh.antennapod.core.feed.FeedItem;
import java.util.Comparator;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$QueueSorter$wRIqyAxqMOwp4Uq_gxjWu5fWmPU implements Comparator {
    public static final /* synthetic */ -$$Lambda$QueueSorter$wRIqyAxqMOwp4Uq_gxjWu5fWmPU INSTANCE = new -$$Lambda$QueueSorter$wRIqyAxqMOwp4Uq_gxjWu5fWmPU();

    private /* synthetic */ -$$Lambda$QueueSorter$wRIqyAxqMOwp4Uq_gxjWu5fWmPU() {
    }

    public final int compare(Object obj, Object obj2) {
        return ((FeedItem) obj2).getTitle().compareTo(((FeedItem) obj).getTitle());
    }
}
