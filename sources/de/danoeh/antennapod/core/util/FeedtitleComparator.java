package de.danoeh.antennapod.core.util;

import de.danoeh.antennapod.core.feed.Feed;
import java.util.Comparator;

class FeedtitleComparator implements Comparator<Feed> {
    FeedtitleComparator() {
    }

    public int compare(Feed lhs, Feed rhs) {
        return lhs.getTitle().compareToIgnoreCase(rhs.getTitle());
    }
}
