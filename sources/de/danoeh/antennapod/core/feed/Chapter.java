package de.danoeh.antennapod.core.feed;

import android.database.Cursor;
import de.danoeh.antennapod.core.storage.PodDBAdapter;

public abstract class Chapter extends FeedComponent {
    String link;
    long start;
    String title;

    public abstract int getChapterType();

    Chapter() {
    }

    Chapter(long start) {
        this.start = start;
    }

    Chapter(long start, String title, FeedItem item, String link) {
        this.start = start;
        this.title = title;
        this.link = link;
    }

    public static Chapter fromCursor(Cursor cursor, FeedItem item) {
        Chapter chapter;
        Cursor cursor2 = cursor;
        int indexId = cursor2.getColumnIndex("id");
        int indexTitle = cursor2.getColumnIndex("title");
        int indexStart = cursor2.getColumnIndex("start");
        int indexLink = cursor2.getColumnIndex(PodDBAdapter.KEY_LINK);
        int indexChapterType = cursor2.getColumnIndex("type");
        long id = cursor2.getLong(indexId);
        String title = cursor2.getString(indexTitle);
        long start = cursor2.getLong(indexStart);
        String link = cursor2.getString(indexLink);
        int chapterType = cursor2.getInt(indexChapterType);
        if (chapterType != 0) {
            switch (chapterType) {
                case 2:
                    chapter = new ID3Chapter(start, title, item, link);
                    break;
                case 3:
                    chapter = new VorbisCommentChapter(start, title, item, link);
                    break;
                default:
                    chapter = null;
                    break;
            }
        }
        chapter = new SimpleChapter(start, title, item, link);
        chapter.setId(id);
        return chapter;
    }

    public long getStart() {
        return this.start;
    }

    public String getTitle() {
        return this.title;
    }

    public String getLink() {
        return this.link;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getHumanReadableIdentifier() {
        return this.title;
    }
}
