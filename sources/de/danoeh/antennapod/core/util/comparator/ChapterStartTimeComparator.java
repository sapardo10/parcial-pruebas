package de.danoeh.antennapod.core.util.comparator;

import de.danoeh.antennapod.core.feed.Chapter;
import java.util.Comparator;

public class ChapterStartTimeComparator implements Comparator<Chapter> {
    public int compare(Chapter lhs, Chapter rhs) {
        if (lhs.getStart() == rhs.getStart()) {
            return 0;
        }
        if (lhs.getStart() < rhs.getStart()) {
            return -1;
        }
        return 1;
    }
}
