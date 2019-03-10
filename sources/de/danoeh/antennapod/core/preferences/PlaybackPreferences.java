package de.danoeh.antennapod.core.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import de.danoeh.antennapod.core.feed.EventDistributor;

public class PlaybackPreferences implements OnSharedPreferenceChangeListener {
    public static final long NO_MEDIA_PLAYING = -1;
    public static final int PLAYER_STATUS_OTHER = 3;
    public static final int PLAYER_STATUS_PAUSED = 2;
    public static final int PLAYER_STATUS_PLAYING = 1;
    public static final String PREF_CURRENTLY_PLAYING_FEEDMEDIA_ID = "de.danoeh.antennapod.preferences.lastPlayedFeedMediaId";
    public static final String PREF_CURRENTLY_PLAYING_FEED_ID = "de.danoeh.antennapod.preferences.lastPlayedFeedId";
    public static final String PREF_CURRENTLY_PLAYING_MEDIA = "de.danoeh.antennapod.preferences.currentlyPlayingMedia";
    public static final String PREF_CURRENT_EPISODE_IS_STREAM = "de.danoeh.antennapod.preferences.lastIsStream";
    public static final String PREF_CURRENT_EPISODE_IS_VIDEO = "de.danoeh.antennapod.preferences.lastIsVideo";
    public static final String PREF_CURRENT_PLAYER_STATUS = "de.danoeh.antennapod.preferences.currentPlayerStatus";
    private static final String TAG = "PlaybackPreferences";
    private static PlaybackPreferences instance;
    private static SharedPreferences prefs;

    private PlaybackPreferences() {
    }

    public static void init(Context context) {
        instance = new PlaybackPreferences();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.registerOnSharedPreferenceChangeListener(instance);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(PREF_CURRENT_PLAYER_STATUS)) {
            EventDistributor.getInstance().sendPlayerStatusUpdateBroadcast();
        }
    }

    public static long getLastPlayedFeedId() {
        return prefs.getLong(PREF_CURRENTLY_PLAYING_FEED_ID, -1);
    }

    public static long getCurrentlyPlayingMedia() {
        return prefs.getLong(PREF_CURRENTLY_PLAYING_MEDIA, -1);
    }

    public static long getCurrentlyPlayingFeedMediaId() {
        return prefs.getLong(PREF_CURRENTLY_PLAYING_FEEDMEDIA_ID, -1);
    }

    public static boolean getCurrentEpisodeIsStream() {
        return prefs.getBoolean(PREF_CURRENT_EPISODE_IS_STREAM, true);
    }

    public static boolean getCurrentEpisodeIsVideo() {
        return prefs.getBoolean(PREF_CURRENT_EPISODE_IS_VIDEO, false);
    }

    public static int getCurrentPlayerStatus() {
        return prefs.getInt(PREF_CURRENT_PLAYER_STATUS, 3);
    }
}
