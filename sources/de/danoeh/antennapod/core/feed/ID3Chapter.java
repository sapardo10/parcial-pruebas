package de.danoeh.antennapod.core.feed;

public class ID3Chapter extends Chapter {
    public static final int CHAPTERTYPE_ID3CHAPTER = 2;
    private String id3ID;

    public ID3Chapter(String id3ID, long start) {
        super(start);
        this.id3ID = id3ID;
    }

    public ID3Chapter(long start, String title, FeedItem item, String link) {
        super(start, title, item, link);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ID3Chapter [id3ID=");
        stringBuilder.append(this.id3ID);
        stringBuilder.append(", title=");
        stringBuilder.append(this.title);
        stringBuilder.append(", start=");
        stringBuilder.append(this.start);
        stringBuilder.append(", url=");
        stringBuilder.append(this.link);
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    public int getChapterType() {
        return 2;
    }

    public String getId3ID() {
        return this.id3ID;
    }
}
