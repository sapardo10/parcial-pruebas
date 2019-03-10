package de.danoeh.antennapod.core.util;

import de.danoeh.antennapod.core.feed.FeedItem;
import java.util.Comparator;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$QueueSorter$mzXKVfyKouyVMqSt_uBG3NeNUuY implements Comparator {
    public static final /* synthetic */ -$$Lambda$QueueSorter$mzXKVfyKouyVMqSt_uBG3NeNUuY INSTANCE = new -$$Lambda$QueueSorter$mzXKVfyKouyVMqSt_uBG3NeNUuY();

    private /* synthetic */ -$$Lambda$QueueSorter$mzXKVfyKouyVMqSt_uBG3NeNUuY() {
    }

    public final int compare(Object obj, Object obj2) {
        return ((FeedItem) obj2).getPubDate().compareTo(((FeedItem) obj).getPubDate());
    }
}
