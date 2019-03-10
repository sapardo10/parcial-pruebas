package de.danoeh.antennapod.core.util.vorbiscommentreader;

import android.util.Log;
import de.danoeh.antennapod.core.BuildConfig;
import de.danoeh.antennapod.core.feed.Chapter;
import de.danoeh.antennapod.core.feed.VorbisCommentChapter;
import java.util.ArrayList;
import java.util.List;

public class VorbisCommentChapterReader extends VorbisCommentReader {
    private static final String CHAPTER_ATTRIBUTE_LINK = "url";
    private static final String CHAPTER_ATTRIBUTE_TITLE = "name";
    private static final String CHAPTER_KEY = "chapter\\d\\d\\d.*";
    private static final String TAG = "VorbisCommentChptrReadr";
    private List<Chapter> chapters;

    public void onVorbisCommentFound() {
        System.out.println("Vorbis comment found");
    }

    public void onVorbisCommentHeaderFound(VorbisCommentHeader header) {
        this.chapters = new ArrayList();
        System.out.println(header.toString());
    }

    public boolean onContentVectorKey(String content) {
        return content.matches(CHAPTER_KEY);
    }

    public void onContentVectorValue(String key, String value) throws VorbisCommentReaderException {
        String str;
        if (BuildConfig.DEBUG) {
            str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Key: ");
            stringBuilder.append(key);
            stringBuilder.append(", value: ");
            stringBuilder.append(value);
            Log.d(str, stringBuilder.toString());
        }
        str = VorbisCommentChapter.getAttributeTypeFromKey(key);
        int id = VorbisCommentChapter.getIDFromKey(key);
        Chapter chapter = getChapterById((long) id);
        if (str == null) {
            if (getChapterById((long) id) == null) {
                long start = VorbisCommentChapter.getStartTimeFromValue(value);
                chapter = new VorbisCommentChapter(id);
                chapter.setStart(start);
                this.chapters.add(chapter);
                return;
            }
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Found chapter with duplicate ID (");
            stringBuilder2.append(key);
            stringBuilder2.append(", ");
            stringBuilder2.append(value);
            stringBuilder2.append(")");
            throw new VorbisCommentReaderException(stringBuilder2.toString());
        } else if (str.equals("name")) {
            if (chapter != null) {
                chapter.setTitle(value);
            }
        } else if (!str.equals(CHAPTER_ATTRIBUTE_LINK)) {
        } else {
            if (chapter != null) {
                chapter.setLink(value);
            }
        }
    }

    public void onNoVorbisCommentFound() {
        System.out.println("No vorbis comment found");
    }

    public void onEndOfComment() {
        System.out.println("End of comment");
        for (Chapter c : this.chapters) {
            System.out.println(c.toString());
        }
    }

    public void onError(VorbisCommentReaderException exception) {
        exception.printStackTrace();
    }

    private Chapter getChapterById(long id) {
        for (Chapter c : this.chapters) {
            if (((long) ((VorbisCommentChapter) c).getVorbisCommentId()) == id) {
                return c;
            }
        }
        return null;
    }

    public List<Chapter> getChapters() {
        return this.chapters;
    }
}
