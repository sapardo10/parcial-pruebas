package de.danoeh.antennapod.core.feed;

public class SimpleChapter extends Chapter {
    public static final int CHAPTERTYPE_SIMPLECHAPTER = 0;

    public SimpleChapter(long start, String title, FeedItem item, String link) {
        super(start, title, item, link);
    }

    public int getChapterType() {
        return 0;
    }

    public void updateFromOther(SimpleChapter other) {
        super.updateFromOther(other);
        this.start = other.start;
        if (other.title != null) {
            this.title = other.title;
        }
        if (other.link != null) {
            this.link = other.link;
        }
    }
}
