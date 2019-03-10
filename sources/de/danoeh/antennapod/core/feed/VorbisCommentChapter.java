package de.danoeh.antennapod.core.feed;

import de.danoeh.antennapod.core.util.vorbiscommentreader.VorbisCommentReaderException;
import java.util.concurrent.TimeUnit;

public class VorbisCommentChapter extends Chapter {
    public static final int CHAPTERTYPE_VORBISCOMMENT_CHAPTER = 3;
    private static final int CHAPTERXXX_LENGTH = "chapterxxx".length();
    private int vorbisCommentId;

    public VorbisCommentChapter(int vorbisCommentId) {
        this.vorbisCommentId = vorbisCommentId;
    }

    public VorbisCommentChapter(long start, String title, FeedItem item, String link) {
        super(start, title, item, link);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("VorbisCommentChapter [id=");
        stringBuilder.append(this.id);
        stringBuilder.append(", title=");
        stringBuilder.append(this.title);
        stringBuilder.append(", link=");
        stringBuilder.append(this.link);
        stringBuilder.append(", start=");
        stringBuilder.append(this.start);
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    public static long getStartTimeFromValue(String value) throws VorbisCommentReaderException {
        String[] parts = value.split(":");
        if (parts.length >= 3) {
            try {
                long hours = TimeUnit.MILLISECONDS.convert(Long.parseLong(parts[0]), TimeUnit.HOURS);
                long minutes = TimeUnit.MILLISECONDS.convert(Long.parseLong(parts[1]), TimeUnit.MINUTES);
                if (parts[2].contains("-->")) {
                    parts[2] = parts[2].substring(0, parts[2].indexOf("-->"));
                }
                return (hours + minutes) + TimeUnit.MILLISECONDS.convert((long) Float.parseFloat(parts[2]), TimeUnit.SECONDS);
            } catch (Throwable e) {
                throw new VorbisCommentReaderException(e);
            }
        }
        throw new VorbisCommentReaderException("Invalid time string");
    }

    public static int getIDFromKey(String key) throws VorbisCommentReaderException {
        if (key.length() >= CHAPTERXXX_LENGTH) {
            try {
                return Integer.parseInt(key.substring(8, 10));
            } catch (Throwable e) {
                throw new VorbisCommentReaderException(e);
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("key is too short (");
        stringBuilder.append(key);
        stringBuilder.append(")");
        throw new VorbisCommentReaderException(stringBuilder.toString());
    }

    public static String getAttributeTypeFromKey(String key) {
        int length = key.length();
        int i = CHAPTERXXX_LENGTH;
        if (length > i) {
            return key.substring(i, key.length());
        }
        return null;
    }

    public int getChapterType() {
        return 3;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public int getVorbisCommentId() {
        return this.vorbisCommentId;
    }

    public void setVorbisCommentId(int vorbisCommentId) {
        this.vorbisCommentId = vorbisCommentId;
    }
}
