package de.danoeh.antennapod.core.util;

import de.danoeh.antennapod.core.feed.FeedItem;
import java.util.Comparator;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$QueueSorter$DNX58fiuVxEu9pfx2FlKy2oCeq0 implements Comparator {
    public static final /* synthetic */ -$$Lambda$QueueSorter$DNX58fiuVxEu9pfx2FlKy2oCeq0 INSTANCE = new -$$Lambda$QueueSorter$DNX58fiuVxEu9pfx2FlKy2oCeq0();

    private /* synthetic */ -$$Lambda$QueueSorter$DNX58fiuVxEu9pfx2FlKy2oCeq0() {
    }

    public final int compare(Object obj, Object obj2) {
        return ((FeedItem) obj2).getFeed().getTitle().compareTo(((FeedItem) obj).getFeed().getTitle());
    }
}
