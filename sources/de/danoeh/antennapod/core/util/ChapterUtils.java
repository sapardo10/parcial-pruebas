package de.danoeh.antennapod.core.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import de.danoeh.antennapod.core.feed.Chapter;
import de.danoeh.antennapod.core.util.comparator.ChapterStartTimeComparator;
import de.danoeh.antennapod.core.util.id3reader.ChapterReader;
import de.danoeh.antennapod.core.util.id3reader.ID3ReaderException;
import de.danoeh.antennapod.core.util.playback.Playable;
import de.danoeh.antennapod.core.util.vorbiscommentreader.VorbisCommentChapterReader;
import de.danoeh.antennapod.core.util.vorbiscommentreader.VorbisCommentReaderException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import org.apache.commons.io.IOUtils;

public class ChapterUtils {
    private static final String TAG = "ChapterUtils";

    private ChapterUtils() {
    }

    @Nullable
    public static Chapter getCurrentChapter(Playable media) {
        if (media.getChapters() == null) {
            return null;
        }
        List<Chapter> chapters = media.getChapters();
        if (chapters == null) {
            return null;
        }
        Chapter current = (Chapter) chapters.get(0);
        for (Chapter sc : chapters) {
            if (sc.getStart() > ((long) media.getPosition())) {
                break;
            }
            current = sc;
        }
        return current;
    }

    public static void loadChaptersFromStreamUrl(Playable media) {
        readID3ChaptersFromPlayableStreamUrl(media);
        if (media.getChapters() == null) {
            readOggChaptersFromPlayableStreamUrl(media);
        }
    }

    public static void loadChaptersFromFileUrl(Playable media) {
        if (media.localFileAvailable()) {
            readID3ChaptersFromPlayableFileUrl(media);
            if (media.getChapters() == null) {
                readOggChaptersFromPlayableFileUrl(media);
            }
            return;
        }
        Log.e(TAG, "Could not load chapters from file url: local file not available");
    }

    private static void readID3ChaptersFromPlayableStreamUrl(Playable p) {
        if (p != null) {
            if (p.getStreamUrl() != null) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Reading id3 chapters from item ");
                stringBuilder.append(p.getEpisodeTitle());
                Log.d(str, stringBuilder.toString());
                InputStream in = null;
                try {
                    in = new URL(p.getStreamUrl()).openStream();
                    List<Chapter> chapters = readChaptersFrom(in);
                    if (!chapters.isEmpty()) {
                        p.setChapters(chapters);
                    }
                    Log.i(TAG, "Chapters loaded");
                } catch (Exception e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                } catch (Throwable th) {
                    IOUtils.closeQuietly(null);
                }
                IOUtils.closeQuietly(in);
                return;
            }
        }
        Log.e(TAG, "Unable to read ID3 chapters: media or download URL was null");
    }

    private static void readID3ChaptersFromPlayableFileUrl(Playable p) {
        if (p != null && p.localFileAvailable()) {
            if (p.getLocalMediaUrl() != null) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Reading id3 chapters from item ");
                stringBuilder.append(p.getEpisodeTitle());
                Log.d(str, stringBuilder.toString());
                File source = new File(p.getLocalMediaUrl());
                if (source.exists()) {
                    InputStream in = null;
                    try {
                        in = new BufferedInputStream(new FileInputStream(source));
                        List<Chapter> chapters = readChaptersFrom(in);
                        if (!chapters.isEmpty()) {
                            p.setChapters(chapters);
                        }
                        Log.i(TAG, "Chapters loaded");
                    } catch (Exception e) {
                        Log.e(TAG, Log.getStackTraceString(e));
                    } catch (Throwable th) {
                        IOUtils.closeQuietly(null);
                    }
                    IOUtils.closeQuietly(in);
                    return;
                }
                Log.e(TAG, "Unable to read id3 chapters: Source doesn't exist");
            }
        }
    }

    @NonNull
    private static List<Chapter> readChaptersFrom(InputStream in) throws IOException, ID3ReaderException {
        ChapterReader reader = new ChapterReader();
        reader.readInputStream(in);
        List<Chapter> chapters = reader.getChapters();
        if (chapters == null) {
            Log.i(TAG, "ChapterReader could not find any ID3 chapters");
            return Collections.emptyList();
        }
        Collections.sort(chapters, new ChapterStartTimeComparator());
        enumerateEmptyChapterTitles(chapters);
        if (chaptersValid(chapters)) {
            return chapters;
        }
        Log.e(TAG, "Chapter data was invalid");
        return Collections.emptyList();
    }

    private static void readOggChaptersFromPlayableStreamUrl(Playable media) {
        if (media != null) {
            if (media.streamAvailable()) {
                InputStream input = null;
                try {
                    input = new URL(media.getStreamUrl()).openStream();
                    if (input != null) {
                        readOggChaptersFromInputStream(media, input);
                    }
                } catch (IOException e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                } catch (Throwable th) {
                    IOUtils.closeQuietly(null);
                }
                IOUtils.closeQuietly(input);
            }
        }
    }

    private static void readOggChaptersFromPlayableFileUrl(Playable media) {
        if (media != null) {
            if (media.getLocalMediaUrl() != null) {
                File source = new File(media.getLocalMediaUrl());
                if (source.exists()) {
                    InputStream input = null;
                    try {
                        input = new BufferedInputStream(new FileInputStream(source));
                        readOggChaptersFromInputStream(media, input);
                    } catch (FileNotFoundException e) {
                        Log.e(TAG, Log.getStackTraceString(e));
                    } catch (Throwable th) {
                        IOUtils.closeQuietly(input);
                    }
                    IOUtils.closeQuietly(input);
                }
            }
        }
    }

    private static void readOggChaptersFromInputStream(Playable p, InputStream input) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Trying to read chapters from item with title ");
        stringBuilder.append(p.getEpisodeTitle());
        Log.d(str, stringBuilder.toString());
        try {
            VorbisCommentChapterReader reader = new VorbisCommentChapterReader();
            reader.readInputStream(input);
            List<Chapter> chapters = reader.getChapters();
            if (chapters == null) {
                Log.i(TAG, "ChapterReader could not find any Ogg vorbis chapters");
                return;
            }
            Collections.sort(chapters, new ChapterStartTimeComparator());
            enumerateEmptyChapterTitles(chapters);
            if (chaptersValid(chapters)) {
                p.setChapters(chapters);
                Log.i(TAG, "Chapters loaded");
            } else {
                Log.e(TAG, "Chapter data was invalid");
            }
        } catch (VorbisCommentReaderException e) {
            e.printStackTrace();
        }
    }

    private static void enumerateEmptyChapterTitles(List<Chapter> chapters) {
        for (int i = 0; i < chapters.size(); i++) {
            Chapter c = (Chapter) chapters.get(i);
            if (c.getTitle() == null) {
                c.setTitle(Integer.toString(i));
            }
        }
    }

    private static boolean chaptersValid(List<Chapter> chapters) {
        if (chapters.isEmpty()) {
            return false;
        }
        for (Chapter c : chapters) {
            if (c.getStart() < 0) {
                return false;
            }
        }
        return true;
    }
}
