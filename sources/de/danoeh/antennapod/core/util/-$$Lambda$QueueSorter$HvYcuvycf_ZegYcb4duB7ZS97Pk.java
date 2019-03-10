package de.danoeh.antennapod.core.util;

import de.danoeh.antennapod.core.feed.FeedItem;
import java.util.Comparator;
import java.util.List;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$QueueSorter$HvYcuvycf_ZegYcb4duB7ZS97Pk implements Comparator {
    public static final /* synthetic */ -$$Lambda$QueueSorter$HvYcuvycf_ZegYcb4duB7ZS97Pk INSTANCE = new -$$Lambda$QueueSorter$HvYcuvycf_ZegYcb4duB7ZS97Pk();

    private /* synthetic */ -$$Lambda$QueueSorter$HvYcuvycf_ZegYcb4duB7ZS97Pk() {
    }

    public final int compare(Object obj, Object obj2) {
        return ((FeedItem) ((List) obj2).get(0)).getFeed().getTitle().compareTo(((FeedItem) ((List) obj).get(0)).getFeed().getTitle());
    }
}
