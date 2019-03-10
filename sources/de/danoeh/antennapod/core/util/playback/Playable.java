package de.danoeh.antennapod.core.util.playback;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import de.danoeh.antennapod.core.asynctask.ImageResource;
import de.danoeh.antennapod.core.feed.Chapter;
import de.danoeh.antennapod.core.feed.FeedMedia;
import de.danoeh.antennapod.core.feed.MediaType;
import de.danoeh.antennapod.core.preferences.PlaybackPreferences;
import de.danoeh.antennapod.core.storage.DBReader;
import de.danoeh.antennapod.core.util.ShownotesProvider;
import java.util.List;

public interface Playable extends Parcelable, ShownotesProvider, ImageResource {

    public static class PlayableException extends Exception {
        private static final long serialVersionUID = 1;

        public PlayableException(String detailMessage, Throwable throwable) {
            super(detailMessage, throwable);
        }

        public PlayableException(String detailMessage) {
            super(detailMessage);
        }

        public PlayableException(Throwable throwable) {
            super(throwable);
        }
    }

    public static class PlayableUtils {
        private static final String TAG = "PlayableUtils";

        private PlayableUtils() {
        }

        @Nullable
        public static Playable createInstanceFromPreferences(Context context) {
            long currentlyPlayingMedia = PlaybackPreferences.getCurrentlyPlayingMedia();
            if (currentlyPlayingMedia == -1) {
                return null;
            }
            return createInstanceFromPreferences(context, (int) currentlyPlayingMedia, PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()));
        }

        public static Playable createInstanceFromPreferences(Context context, int type, SharedPreferences pref) {
            Playable result = null;
            switch (type) {
                case 1:
                    result = createFeedMediaInstance(pref);
                    break;
                case 2:
                    result = createExternalMediaInstance(pref);
                    break;
                default:
                    break;
            }
            if (result == null) {
                Log.e(TAG, "Could not restore Playable object from preferences");
            }
            return result;
        }

        private static Playable createFeedMediaInstance(SharedPreferences pref) {
            long mediaId = pref.getLong(FeedMedia.PREF_MEDIA_ID, -1);
            if (mediaId != -1) {
                return DBReader.getFeedMedia(mediaId);
            }
            return null;
        }

        private static Playable createExternalMediaInstance(SharedPreferences pref) {
            String source = pref.getString(ExternalMedia.PREF_SOURCE_URL, null);
            String mediaType = pref.getString(ExternalMedia.PREF_MEDIA_TYPE, null);
            if (source == null || mediaType == null) {
                return null;
            }
            return new ExternalMedia(source, MediaType.valueOf(mediaType), pref.getInt(ExternalMedia.PREF_POSITION, 0), pref.getLong(ExternalMedia.PREF_LAST_PLAYED_TIME, 0));
        }
    }

    List<Chapter> getChapters();

    int getDuration();

    String getEpisodeTitle();

    String getFeedTitle();

    Object getIdentifier();

    long getLastPlayedTime();

    String getLocalMediaUrl();

    MediaType getMediaType();

    String getPaymentLink();

    int getPlayableType();

    int getPosition();

    String getStreamUrl();

    String getWebsiteLink();

    void loadChapterMarks();

    void loadMetadata() throws PlayableException;

    boolean localFileAvailable();

    void onPlaybackCompleted(Context context);

    void onPlaybackPause(Context context);

    void onPlaybackStart();

    void saveCurrentPosition(SharedPreferences sharedPreferences, int i, long j);

    void setChapters(List<Chapter> list);

    void setDuration(int i);

    void setLastPlayedTime(long j);

    void setPosition(int i);

    boolean streamAvailable();

    void writeToPreferences(Editor editor);
}
