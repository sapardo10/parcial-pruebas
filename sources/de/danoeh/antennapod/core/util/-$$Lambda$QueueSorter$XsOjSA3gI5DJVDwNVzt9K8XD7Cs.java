package de.danoeh.antennapod.core.util;

import de.danoeh.antennapod.core.feed.FeedItem;
import java.util.Comparator;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$QueueSorter$XsOjSA3gI5DJVDwNVzt9K8XD7Cs implements Comparator {
    public static final /* synthetic */ -$$Lambda$QueueSorter$XsOjSA3gI5DJVDwNVzt9K8XD7Cs INSTANCE = new -$$Lambda$QueueSorter$XsOjSA3gI5DJVDwNVzt9K8XD7Cs();

    private /* synthetic */ -$$Lambda$QueueSorter$XsOjSA3gI5DJVDwNVzt9K8XD7Cs() {
    }

    public final int compare(Object obj, Object obj2) {
        return ((FeedItem) obj).getTitle().compareTo(((FeedItem) obj2).getTitle());
    }
}
