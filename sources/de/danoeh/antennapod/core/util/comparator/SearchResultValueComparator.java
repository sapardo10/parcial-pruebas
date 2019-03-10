package de.danoeh.antennapod.core.util.comparator;

import de.danoeh.antennapod.core.feed.FeedItem;
import de.danoeh.antennapod.core.feed.SearchResult;
import java.util.Comparator;

public class SearchResultValueComparator implements Comparator<SearchResult> {
    public int compare(SearchResult lhs, SearchResult rhs) {
        int value = rhs.getValue() - lhs.getValue();
        if (value == 0 && (lhs.getComponent() instanceof FeedItem) && (rhs.getComponent() instanceof FeedItem)) {
            return ((FeedItem) lhs.getComponent()).getTitle().compareTo(((FeedItem) rhs.getComponent()).getTitle());
        }
        return value;
    }
}
