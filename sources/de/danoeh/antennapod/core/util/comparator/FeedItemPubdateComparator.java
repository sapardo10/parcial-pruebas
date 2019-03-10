package de.danoeh.antennapod.core.util.comparator;

import de.danoeh.antennapod.core.feed.FeedItem;
import java.util.Comparator;

public class FeedItemPubdateComparator implements Comparator<FeedItem> {
    public int compare(FeedItem lhs, FeedItem rhs) {
        return rhs.getPubDate().compareTo(lhs.getPubDate());
    }
}
