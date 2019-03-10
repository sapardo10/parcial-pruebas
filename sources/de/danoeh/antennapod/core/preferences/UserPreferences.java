package de.danoeh.antennapod.core.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v4.internal.view.SupportMenu;
import android.text.TextUtils;
import android.util.Log;
import de.danoeh.antennapod.core.C0734R;
import de.danoeh.antennapod.core.service.download.ProxyConfig;
import de.danoeh.antennapod.core.storage.APCleanupAlgorithm;
import de.danoeh.antennapod.core.storage.APNullCleanupAlgorithm;
import de.danoeh.antennapod.core.storage.APQueueCleanupAlgorithm;
import de.danoeh.antennapod.core.storage.EpisodeCleanupAlgorithm;
import de.danoeh.antennapod.core.util.Converter;
import de.danoeh.antennapod.core.util.download.AutoUpdateManager;
import de.danoeh.antennapod.fragment.QueueFragment;
import java.io.File;
import java.io.IOException;
import java.net.Proxy.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;
import org.json.JSONException;

public class UserPreferences {
    private static final int EPISODE_CACHE_SIZE_UNLIMITED = -1;
    public static final int EPISODE_CLEANUP_DEFAULT = 0;
    public static final int EPISODE_CLEANUP_NULL = -2;
    public static final int EPISODE_CLEANUP_QUEUE = -1;
    public static final int FEED_COUNTER_SHOW_DOWNLOADED = 4;
    public static final int FEED_COUNTER_SHOW_NEW = 1;
    public static final int FEED_COUNTER_SHOW_NEW_UNPLAYED_SUM = 0;
    public static final int FEED_COUNTER_SHOW_NONE = 3;
    public static final int FEED_COUNTER_SHOW_UNPLAYED = 2;
    public static final int FEED_ORDER_ALPHABETICAL = 1;
    public static final int FEED_ORDER_COUNTER = 0;
    public static final int FEED_ORDER_MOST_PLAYED = 3;
    private static final String IMAGE_CACHE_DEFAULT_VALUE = "100";
    private static final int IMAGE_CACHE_SIZE_MINIMUM = 20;
    private static final String IMPORT_DIR = "import/";
    private static final int NOTIFICATION_BUTTON_FAST_FORWARD = 1;
    private static final int NOTIFICATION_BUTTON_REWIND = 0;
    private static final int NOTIFICATION_BUTTON_SKIP = 2;
    private static final String PREF_AUTODL_SELECTED_NETWORKS = "prefAutodownloadSelectedNetworks";
    private static final String PREF_AUTO_DELETE = "prefAutoDelete";
    private static final String PREF_AUTO_FLATTR = "pref_auto_flattr";
    private static final String PREF_AUTO_FLATTR_PLAYED_DURATION_THRESHOLD = "prefAutoFlattrPlayedDurationThreshold";
    public static final String PREF_BACK_BUTTON_BEHAVIOR = "prefBackButtonBehavior";
    private static final String PREF_BACK_BUTTON_GO_TO_PAGE = "prefBackButtonGoToPage";
    public static final String PREF_CAST_ENABLED = "prefCast";
    public static final String PREF_COMPACT_NOTIFICATION_BUTTONS = "prefCompactNotificationButtons";
    private static final String PREF_DATA_FOLDER = "prefDataFolder";
    public static final String PREF_DELETE_REMOVES_FROM_QUEUE = "prefDeleteRemovesFromQueue";
    private static final String PREF_DRAWER_FEED_COUNTER = "prefDrawerFeedIndicator";
    private static final String PREF_DRAWER_FEED_ORDER = "prefDrawerFeedOrder";
    public static final String PREF_ENABLE_AUTODL = "prefEnableAutoDl";
    public static final String PREF_ENABLE_AUTODL_ON_BATTERY = "prefEnableAutoDownloadOnBattery";
    public static final String PREF_ENABLE_AUTODL_ON_MOBILE = "prefEnableAutoDownloadOnMobile";
    public static final String PREF_ENABLE_AUTODL_WIFI_FILTER = "prefEnableAutoDownloadWifiFilter";
    private static final String PREF_ENQUEUE_DOWNLOADED = "prefEnqueueDownloaded";
    public static final String PREF_EPISODE_CACHE_SIZE = "prefEpisodeCacheSize";
    public static final String PREF_EPISODE_CLEANUP = "prefEpisodeCleanup";
    public static final String PREF_EXPANDED_NOTIFICATION = "prefExpandNotify";
    private static final String PREF_FAST_FORWARD_SECS = "prefFastForwardSecs";
    private static final String PREF_FAVORITE_KEEPS_EPISODE = "prefFavoriteKeepsEpisode";
    public static final String PREF_FOLLOW_QUEUE = "prefFollowQueue";
    private static final String PREF_GPODNET_NOTIFICATIONS = "pref_gpodnet_notifications";
    private static final String PREF_HARDWARE_FOWARD_BUTTON_SKIPS = "prefHardwareForwardButtonSkips";
    private static final String PREF_HARDWARE_PREVIOUS_BUTTON_RESTARTS = "prefHardwarePreviousButtonRestarts";
    public static final String PREF_HIDDEN_DRAWER_ITEMS = "prefHiddenDrawerItems";
    public static final String PREF_IMAGE_CACHE_SIZE = "prefImageCacheSize";
    private static final String PREF_LEFT_VOLUME = "prefLeftVolume";
    public static final String PREF_LOCKSCREEN_BACKGROUND = "prefLockscreenBackground";
    public static final String PREF_MEDIA_PLAYER = "prefMediaPlayer";
    public static final String PREF_MEDIA_PLAYER_EXOPLAYER = "exoplayer";
    private static final String PREF_MOBILE_UPDATE = "prefMobileUpdate";
    public static final String PREF_PARALLEL_DOWNLOADS = "prefParallelDownloads";
    public static final String PREF_PAUSE_ON_HEADSET_DISCONNECT = "prefPauseOnHeadsetDisconnect";
    private static final String PREF_PAUSE_PLAYBACK_FOR_FOCUS_LOSS = "prefPauseForFocusLoss";
    private static final String PREF_PERSISTENT_NOTIFICATION = "prefPersistNotify";
    public static final String PREF_PLAYBACK_SKIP_SILENCE = "prefSkipSilence";
    private static final String PREF_PLAYBACK_SPEED = "prefPlaybackSpeed";
    private static final String PREF_PLAYBACK_SPEED_ARRAY = "prefPlaybackSpeedArray";
    private static final String PREF_PROXY_HOST = "prefProxyHost";
    private static final String PREF_PROXY_PASSWORD = "prefProxyPassword";
    private static final String PREF_PROXY_PORT = "prefProxyPort";
    private static final String PREF_PROXY_TYPE = "prefProxyType";
    private static final String PREF_PROXY_USER = "prefProxyUser";
    private static final String PREF_QUEUE_ADD_TO_FRONT = "prefQueueAddToFront";
    private static final String PREF_QUEUE_LOCKED = "prefQueueLocked";
    private static final String PREF_RESUME_AFTER_CALL = "prefResumeAfterCall";
    private static final String PREF_REWIND_SECS = "prefRewindSecs";
    private static final String PREF_RIGHT_VOLUME = "prefRightVolume";
    private static final String PREF_SHOW_DOWNLOAD_REPORT = "prefShowDownloadReport";
    private static final String PREF_SKIP_KEEPS_EPISODE = "prefSkipKeepsEpisode";
    public static final String PREF_SMART_MARK_AS_PLAYED_SECS = "prefSmartMarkAsPlayedSecs";
    private static final String PREF_STEREO_TO_MONO = "PrefStereoToMono";
    public static final String PREF_THEME = "prefTheme";
    private static final String PREF_UNPAUSE_ON_BLUETOOTH_RECONNECT = "prefUnpauseOnBluetoothReconnect";
    public static final String PREF_UNPAUSE_ON_HEADSET_RECONNECT = "prefUnpauseOnHeadsetReconnect";
    public static final String PREF_UPDATE_INTERVAL = "prefAutoUpdateIntervall";
    public static final String PREF_VIDEO_BEHAVIOR = "prefVideoBehavior";
    private static final String TAG = "UserPreferences";
    private static Context context;
    private static SharedPreferences prefs;

    private UserPreferences() {
    }

    public static void init(@NonNull Context context) {
        Log.d(TAG, "Creating new instance of UserPreferences");
        context = context.getApplicationContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        createImportDirectory();
        createNoMediaFile();
    }

    public static int getTheme() {
        return readThemeValue(prefs.getString(PREF_THEME, "0"));
    }

    public static int getNoTitleTheme() {
        int theme = getTheme();
        if (theme == C0734R.style.Theme_AntennaPod_Dark) {
            return C0734R.style.Theme_AntennaPod_Dark_NoTitle;
        }
        if (theme == C0734R.style.Theme_AntennaPod_TrueBlack) {
            return C0734R.style.Theme_AntennaPod_TrueBlack_NoTitle;
        }
        return C0734R.style.Theme_AntennaPod_Light_NoTitle;
    }

    public static List<String> getHiddenDrawerItems() {
        return new ArrayList(Arrays.asList(TextUtils.split(prefs.getString(PREF_HIDDEN_DRAWER_ITEMS, ""), ",")));
    }

    public static List<Integer> getCompactNotificationButtons() {
        String[] buttons = TextUtils.split(prefs.getString(PREF_COMPACT_NOTIFICATION_BUTTONS, String.valueOf(2)), ",");
        List<Integer> notificationButtons = new ArrayList();
        for (String button : buttons) {
            notificationButtons.add(Integer.valueOf(Integer.parseInt(button)));
        }
        return notificationButtons;
    }

    private static boolean showButtonOnCompactNotification(int buttonId) {
        return getCompactNotificationButtons().contains(Integer.valueOf(buttonId));
    }

    public static boolean showRewindOnCompactNotification() {
        return showButtonOnCompactNotification(0);
    }

    public static boolean showFastForwardOnCompactNotification() {
        return showButtonOnCompactNotification(1);
    }

    public static boolean showSkipOnCompactNotification() {
        return showButtonOnCompactNotification(2);
    }

    public static int getFeedOrder() {
        return Integer.parseInt(prefs.getString(PREF_DRAWER_FEED_ORDER, "0"));
    }

    public static int getFeedCounterSetting() {
        return Integer.parseInt(prefs.getString(PREF_DRAWER_FEED_COUNTER, "0"));
    }

    public static int getNotifyPriority() {
        if (prefs.getBoolean(PREF_EXPANDED_NOTIFICATION, false)) {
            return 2;
        }
        return 0;
    }

    public static boolean isPersistNotify() {
        return prefs.getBoolean(PREF_PERSISTENT_NOTIFICATION, true);
    }

    public static boolean setLockscreenBackground() {
        return prefs.getBoolean(PREF_LOCKSCREEN_BACKGROUND, true);
    }

    public static boolean showDownloadReport() {
        return prefs.getBoolean(PREF_SHOW_DOWNLOAD_REPORT, true);
    }

    public static boolean enqueueDownloadedEpisodes() {
        return prefs.getBoolean(PREF_ENQUEUE_DOWNLOADED, true);
    }

    public static boolean enqueueAtFront() {
        return prefs.getBoolean(PREF_QUEUE_ADD_TO_FRONT, false);
    }

    public static boolean isPauseOnHeadsetDisconnect() {
        return prefs.getBoolean(PREF_PAUSE_ON_HEADSET_DISCONNECT, true);
    }

    public static boolean isUnpauseOnHeadsetReconnect() {
        return prefs.getBoolean(PREF_UNPAUSE_ON_HEADSET_RECONNECT, true);
    }

    public static boolean isUnpauseOnBluetoothReconnect() {
        return prefs.getBoolean(PREF_UNPAUSE_ON_BLUETOOTH_RECONNECT, false);
    }

    public static boolean shouldHardwareButtonSkip() {
        return prefs.getBoolean(PREF_HARDWARE_FOWARD_BUTTON_SKIPS, false);
    }

    public static boolean shouldHardwarePreviousButtonRestart() {
        return prefs.getBoolean(PREF_HARDWARE_PREVIOUS_BUTTON_RESTARTS, false);
    }

    public static boolean isFollowQueue() {
        return prefs.getBoolean(PREF_FOLLOW_QUEUE, true);
    }

    public static boolean shouldSkipKeepEpisode() {
        return prefs.getBoolean(PREF_SKIP_KEEPS_EPISODE, true);
    }

    public static boolean shouldFavoriteKeepEpisode() {
        return prefs.getBoolean(PREF_FAVORITE_KEEPS_EPISODE, true);
    }

    public static boolean isAutoDelete() {
        return prefs.getBoolean(PREF_AUTO_DELETE, false);
    }

    public static int getSmartMarkAsPlayedSecs() {
        return Integer.parseInt(prefs.getString(PREF_SMART_MARK_AS_PLAYED_SECS, "30"));
    }

    public static boolean shouldDeleteRemoveFromQueue() {
        return prefs.getBoolean(PREF_DELETE_REMOVES_FROM_QUEUE, false);
    }

    public static boolean isAutoFlattr() {
        return prefs.getBoolean(PREF_AUTO_FLATTR, false);
    }

    public static String getPlaybackSpeed() {
        return prefs.getString(PREF_PLAYBACK_SPEED, "1.00");
    }

    public static boolean isSkipSilence() {
        return prefs.getBoolean(PREF_PLAYBACK_SKIP_SILENCE, false);
    }

    public static String[] getPlaybackSpeedArray() {
        return readPlaybackSpeedArray(prefs.getString(PREF_PLAYBACK_SPEED_ARRAY, null));
    }

    public static float getLeftVolume() {
        return Converter.getVolumeFromPercentage(prefs.getInt(PREF_LEFT_VOLUME, 100));
    }

    public static float getRightVolume() {
        return Converter.getVolumeFromPercentage(prefs.getInt(PREF_RIGHT_VOLUME, 100));
    }

    public static int getLeftVolumePercentage() {
        return prefs.getInt(PREF_LEFT_VOLUME, 100);
    }

    public static int getRightVolumePercentage() {
        return prefs.getInt(PREF_RIGHT_VOLUME, 100);
    }

    public static boolean shouldPauseForFocusLoss() {
        return prefs.getBoolean(PREF_PAUSE_PLAYBACK_FOR_FOCUS_LOSS, false);
    }

    public static long getUpdateInterval() {
        String updateInterval = prefs.getString(PREF_UPDATE_INTERVAL, "0");
        if (updateInterval.contains(":")) {
            return 0;
        }
        return readUpdateInterval(updateInterval);
    }

    public static int[] getUpdateTimeOfDay() {
        String datetime = prefs.getString(PREF_UPDATE_INTERVAL, "");
        if (datetime.length() < 3 || !datetime.contains(":")) {
            return new int[0];
        }
        String[] parts = datetime.split(":");
        int hourOfDay = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        return new int[]{hourOfDay, minute};
    }

    public static boolean isAllowMobileUpdate() {
        return prefs.getBoolean(PREF_MOBILE_UPDATE, false);
    }

    public static int getParallelDownloads() {
        return Integer.parseInt(prefs.getString(PREF_PARALLEL_DOWNLOADS, "4"));
    }

    public static int getEpisodeCacheSizeUnlimited() {
        return context.getResources().getInteger(C0734R.integer.episode_cache_size_unlimited);
    }

    public static int getEpisodeCacheSize() {
        return readEpisodeCacheSizeInternal(prefs.getString(PREF_EPISODE_CACHE_SIZE, "20"));
    }

    public static boolean isEnableAutodownload() {
        return prefs.getBoolean(PREF_ENABLE_AUTODL, false);
    }

    public static boolean isEnableAutodownloadOnBattery() {
        return prefs.getBoolean(PREF_ENABLE_AUTODL_ON_BATTERY, true);
    }

    public static boolean isEnableAutodownloadWifiFilter() {
        return prefs.getBoolean(PREF_ENABLE_AUTODL_WIFI_FILTER, false);
    }

    public static boolean isEnableAutodownloadOnMobile() {
        return prefs.getBoolean(PREF_ENABLE_AUTODL_ON_MOBILE, false);
    }

    public static int getImageCacheSize() {
        int cacheSizeInt = Integer.parseInt(prefs.getString(PREF_IMAGE_CACHE_SIZE, IMAGE_CACHE_DEFAULT_VALUE));
        if (cacheSizeInt < 20) {
            prefs.edit().putString(PREF_IMAGE_CACHE_SIZE, IMAGE_CACHE_DEFAULT_VALUE).apply();
            cacheSizeInt = Integer.parseInt(IMAGE_CACHE_DEFAULT_VALUE);
        }
        return (cacheSizeInt * 1024) * 1024;
    }

    public static int getFastForwardSecs() {
        return prefs.getInt(PREF_FAST_FORWARD_SECS, 30);
    }

    public static int getRewindSecs() {
        return prefs.getInt(PREF_REWIND_SECS, 30);
    }

    public static float getAutoFlattrPlayedDurationThreshold() {
        return prefs.getFloat(PREF_AUTO_FLATTR_PLAYED_DURATION_THRESHOLD, 0.8f);
    }

    public static String[] getAutodownloadSelectedNetworks() {
        return TextUtils.split(prefs.getString(PREF_AUTODL_SELECTED_NETWORKS, ""), ",");
    }

    public static void setProxyConfig(ProxyConfig config) {
        Editor editor = prefs.edit();
        editor.putString(PREF_PROXY_TYPE, config.type.name());
        if (TextUtils.isEmpty(config.host)) {
            editor.remove(PREF_PROXY_HOST);
        } else {
            editor.putString(PREF_PROXY_HOST, config.host);
        }
        if (config.port > 0) {
            if (config.port <= SupportMenu.USER_MASK) {
                editor.putInt(PREF_PROXY_PORT, config.port);
                if (TextUtils.isEmpty(config.username)) {
                    editor.putString(PREF_PROXY_USER, config.username);
                } else {
                    editor.remove(PREF_PROXY_USER);
                }
                if (TextUtils.isEmpty(config.password)) {
                    editor.putString(PREF_PROXY_PASSWORD, config.password);
                } else {
                    editor.remove(PREF_PROXY_PASSWORD);
                }
                editor.apply();
            }
        }
        editor.remove(PREF_PROXY_PORT);
        if (TextUtils.isEmpty(config.username)) {
            editor.putString(PREF_PROXY_USER, config.username);
        } else {
            editor.remove(PREF_PROXY_USER);
        }
        if (TextUtils.isEmpty(config.password)) {
            editor.putString(PREF_PROXY_PASSWORD, config.password);
        } else {
            editor.remove(PREF_PROXY_PASSWORD);
        }
        editor.apply();
    }

    public static ProxyConfig getProxyConfig() {
        return new ProxyConfig(Type.valueOf(prefs.getString(PREF_PROXY_TYPE, Type.DIRECT.name())), prefs.getString(PREF_PROXY_HOST, null), prefs.getInt(PREF_PROXY_PORT, 0), prefs.getString(PREF_PROXY_USER, null), prefs.getString(PREF_PROXY_PASSWORD, null));
    }

    public static boolean shouldResumeAfterCall() {
        return prefs.getBoolean(PREF_RESUME_AFTER_CALL, true);
    }

    public static boolean isQueueLocked() {
        return prefs.getBoolean(PREF_QUEUE_LOCKED, false);
    }

    public static void setFastForwardSecs(int secs) {
        prefs.edit().putInt(PREF_FAST_FORWARD_SECS, secs).apply();
    }

    public static void setRewindSecs(int secs) {
        prefs.edit().putInt(PREF_REWIND_SECS, secs).apply();
    }

    public static void setPlaybackSpeed(String speed) {
        prefs.edit().putString(PREF_PLAYBACK_SPEED, speed).apply();
    }

    public static void setSkipSilence(boolean skipSilence) {
        prefs.edit().putBoolean(PREF_PLAYBACK_SKIP_SILENCE, skipSilence).apply();
    }

    public static void setPlaybackSpeedArray(String[] speeds) {
        JSONArray jsonArray = new JSONArray();
        for (String speed : speeds) {
            jsonArray.put(speed);
        }
        prefs.edit().putString(PREF_PLAYBACK_SPEED_ARRAY, jsonArray.toString()).apply();
    }

    public static void setVolume(@IntRange(from = 0, to = 100) int leftVolume, @IntRange(from = 0, to = 100) int rightVolume) {
        prefs.edit().putInt(PREF_LEFT_VOLUME, leftVolume).putInt(PREF_RIGHT_VOLUME, rightVolume).apply();
    }

    public static void setAutodownloadSelectedNetworks(String[] value) {
        prefs.edit().putString(PREF_AUTODL_SELECTED_NETWORKS, TextUtils.join(",", value)).apply();
    }

    public static void setUpdateInterval(long hours) {
        prefs.edit().putString(PREF_UPDATE_INTERVAL, String.valueOf(hours)).apply();
        restartUpdateAlarm(true);
    }

    public static void setUpdateTimeOfDay(int hourOfDay, int minute) {
        Editor edit = prefs.edit();
        String str = PREF_UPDATE_INTERVAL;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(hourOfDay);
        stringBuilder.append(":");
        stringBuilder.append(minute);
        edit.putString(str, stringBuilder.toString()).apply();
        restartUpdateAlarm(false);
    }

    public static void setAutoFlattrSettings(boolean enabled, float autoFlattrThreshold) {
        if (((double) autoFlattrThreshold) < 0.0d || ((double) autoFlattrThreshold) > 1.0d) {
            throw new IllegalArgumentException("Flattr threshold must be in range [0.0, 1.0]");
        }
        prefs.edit().putBoolean(PREF_AUTO_FLATTR, enabled).putFloat(PREF_AUTO_FLATTR_PLAYED_DURATION_THRESHOLD, autoFlattrThreshold).apply();
    }

    public static boolean gpodnetNotificationsEnabled() {
        return prefs.getBoolean(PREF_GPODNET_NOTIFICATIONS, true);
    }

    public static void setGpodnetNotificationsEnabled() {
        prefs.edit().putBoolean(PREF_GPODNET_NOTIFICATIONS, true).apply();
    }

    public static void setHiddenDrawerItems(List<String> items) {
        prefs.edit().putString(PREF_HIDDEN_DRAWER_ITEMS, TextUtils.join(",", items)).apply();
    }

    public static void setCompactNotificationButtons(List<Integer> items) {
        prefs.edit().putString(PREF_COMPACT_NOTIFICATION_BUTTONS, TextUtils.join(",", items)).apply();
    }

    public static void setQueueLocked(boolean locked) {
        prefs.edit().putBoolean(PREF_QUEUE_LOCKED, locked).apply();
    }

    private static int readThemeValue(String valueFromPrefs) {
        switch (Integer.parseInt(valueFromPrefs)) {
            case 0:
                return C0734R.style.Theme_AntennaPod_Light;
            case 1:
                return C0734R.style.Theme_AntennaPod_Dark;
            case 2:
                return C0734R.style.Theme_AntennaPod_TrueBlack;
            default:
                return C0734R.style.Theme_AntennaPod_Light;
        }
    }

    private static long readUpdateInterval(String valueFromPrefs) {
        return TimeUnit.HOURS.toMillis((long) Integer.parseInt(valueFromPrefs));
    }

    private static int readEpisodeCacheSizeInternal(String valueFromPrefs) {
        if (valueFromPrefs.equals(context.getString(C0734R.string.pref_episode_cache_unlimited))) {
            return -1;
        }
        return Integer.parseInt(valueFromPrefs);
    }

    private static String[] readPlaybackSpeedArray(String valueFromPrefs) {
        int i = 0;
        if (valueFromPrefs == null) {
            return new String[]{"1.00", "1.25", "1.50", "1.75", "2.00"};
        }
        try {
            JSONArray jsonArray = new JSONArray(valueFromPrefs);
            String[] selectedSpeeds = new String[jsonArray.length()];
            while (i < jsonArray.length()) {
                selectedSpeeds[i] = jsonArray.getString(i);
                i++;
            }
            return selectedSpeeds;
        } catch (JSONException e) {
            Log.e(TAG, "Got JSON error when trying to get speeds from JSONArray");
            e.printStackTrace();
            return null;
        }
    }

    public static boolean useSonic() {
        return prefs.getString(PREF_MEDIA_PLAYER, "sonic").equals("sonic");
    }

    public static boolean useExoplayer() {
        return prefs.getString(PREF_MEDIA_PLAYER, "sonic").equals(PREF_MEDIA_PLAYER_EXOPLAYER);
    }

    public static void enableSonic() {
        prefs.edit().putString(PREF_MEDIA_PLAYER, "sonic").apply();
    }

    public static boolean stereoToMono() {
        return prefs.getBoolean(PREF_STEREO_TO_MONO, false);
    }

    public static void stereoToMono(boolean enable) {
        prefs.edit().putBoolean(PREF_STEREO_TO_MONO, enable).apply();
    }

    public static UserPreferences$VideoBackgroundBehavior getVideoBackgroundBehavior() {
        Object obj;
        String string = prefs.getString(PREF_VIDEO_BEHAVIOR, "stop");
        int hashCode = string.hashCode();
        if (hashCode != -567202649) {
            if (hashCode != 110999) {
                if (hashCode == 3540994 && string.equals("stop")) {
                    obj = null;
                    switch (obj) {
                        case null:
                            return UserPreferences$VideoBackgroundBehavior.STOP;
                        case 1:
                            return UserPreferences$VideoBackgroundBehavior.PICTURE_IN_PICTURE;
                        case 2:
                            return UserPreferences$VideoBackgroundBehavior.CONTINUE_PLAYING;
                        default:
                            return UserPreferences$VideoBackgroundBehavior.STOP;
                    }
                }
            } else if (string.equals("pip")) {
                obj = 1;
                switch (obj) {
                    case null:
                        return UserPreferences$VideoBackgroundBehavior.STOP;
                    case 1:
                        return UserPreferences$VideoBackgroundBehavior.PICTURE_IN_PICTURE;
                    case 2:
                        return UserPreferences$VideoBackgroundBehavior.CONTINUE_PLAYING;
                    default:
                        return UserPreferences$VideoBackgroundBehavior.STOP;
                }
            }
        } else if (string.equals("continue")) {
            obj = 2;
            switch (obj) {
                case null:
                    return UserPreferences$VideoBackgroundBehavior.STOP;
                case 1:
                    return UserPreferences$VideoBackgroundBehavior.PICTURE_IN_PICTURE;
                case 2:
                    return UserPreferences$VideoBackgroundBehavior.CONTINUE_PLAYING;
                default:
                    return UserPreferences$VideoBackgroundBehavior.STOP;
            }
        }
        obj = -1;
        switch (obj) {
            case null:
                return UserPreferences$VideoBackgroundBehavior.STOP;
            case 1:
                return UserPreferences$VideoBackgroundBehavior.PICTURE_IN_PICTURE;
            case 2:
                return UserPreferences$VideoBackgroundBehavior.CONTINUE_PLAYING;
            default:
                return UserPreferences$VideoBackgroundBehavior.STOP;
        }
    }

    public static EpisodeCleanupAlgorithm getEpisodeCleanupAlgorithm() {
        int cleanupValue = getEpisodeCleanupValue();
        if (cleanupValue == -1) {
            return new APQueueCleanupAlgorithm();
        }
        if (cleanupValue == -2) {
            return new APNullCleanupAlgorithm();
        }
        return new APCleanupAlgorithm(cleanupValue);
    }

    public static int getEpisodeCleanupValue() {
        return Integer.parseInt(prefs.getString(PREF_EPISODE_CLEANUP, "-1"));
    }

    public static void setEpisodeCleanupValue(int episodeCleanupValue) {
        prefs.edit().putString(PREF_EPISODE_CLEANUP, Integer.toString(episodeCleanupValue)).apply();
    }

    public static File getDataFolder(String type) {
        String strDir = prefs.getString(PREF_DATA_FOLDER, null);
        if (strDir == null) {
            Log.d(TAG, "Using default data folder");
            return context.getExternalFilesDir(type);
        }
        File dataDir = new File(strDir);
        if (!dataDir.exists()) {
            if (!dataDir.mkdir()) {
                Log.w(TAG, "Could not create data folder");
                return null;
            }
        }
        if (type == null) {
            return dataDir;
        }
        String[] dirs = type.split("/");
        for (int i = 0; i < dirs.length; i++) {
            if (dirs.length > 0) {
                if (i < dirs.length - 1) {
                    dataDir = getDataFolder(dirs[i]);
                    if (dataDir == null) {
                        return null;
                    }
                }
                type = dirs[i];
            }
        }
        File typeDir = new File(dataDir, type);
        if (!typeDir.exists()) {
            if (dataDir.canWrite()) {
                if (!typeDir.mkdir()) {
                    String str = TAG;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Could not create data folder named ");
                    stringBuilder.append(type);
                    Log.e(str, stringBuilder.toString());
                    return null;
                }
            }
        }
        return typeDir;
    }

    public static void setDataFolder(String dir) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("setDataFolder(dir: ");
        stringBuilder.append(dir);
        stringBuilder.append(")");
        Log.d(str, stringBuilder.toString());
        prefs.edit().putString(PREF_DATA_FOLDER, dir).apply();
        createImportDirectory();
    }

    private static void createNoMediaFile() {
        File f = new File(context.getExternalFilesDir(null), ".nomedia");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                Log.e(TAG, "Could not create .nomedia file");
                e.printStackTrace();
            }
            Log.d(TAG, ".nomedia file created");
        }
    }

    private static void createImportDirectory() {
        File importDir = getDataFolder(IMPORT_DIR);
        if (importDir == null) {
            Log.d(TAG, "Could not access external storage.");
        } else if (importDir.exists()) {
            Log.d(TAG, "Import directory already exists");
        } else {
            Log.d(TAG, "Creating import directory");
            importDir.mkdir();
        }
    }

    public static void restartUpdateAlarm(boolean now) {
        int[] timeOfDay = getUpdateTimeOfDay();
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("timeOfDay: ");
        stringBuilder.append(Arrays.toString(timeOfDay));
        Log.d(str, stringBuilder.toString());
        if (timeOfDay.length == 2) {
            AutoUpdateManager.restartUpdateTimeOfDayAlarm(context, timeOfDay[0], timeOfDay[1]);
            return;
        }
        long milliseconds = getUpdateInterval();
        long startTrigger = milliseconds;
        if (now) {
            startTrigger = TimeUnit.SECONDS.toMillis(10);
        }
        AutoUpdateManager.restartUpdateIntervalAlarm(context, startTrigger, milliseconds);
    }

    public static int readEpisodeCacheSize(String valueFromPrefs) {
        return readEpisodeCacheSizeInternal(valueFromPrefs);
    }

    public static boolean isCastEnabled() {
        return prefs.getBoolean(PREF_CAST_ENABLED, false);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static de.danoeh.antennapod.core.preferences.UserPreferences$BackButtonBehavior getBackButtonBehavior() {
        /*
        r0 = prefs;
        r1 = "prefBackButtonBehavior";
        r2 = "default";
        r0 = r0.getString(r1, r2);
        r1 = r0.hashCode();
        switch(r1) {
            case -1323763471: goto L_0x003a;
            case -979805852: goto L_0x0030;
            case -806132174: goto L_0x0026;
            case 3433103: goto L_0x001c;
            case 1544803905: goto L_0x0012;
            default: goto L_0x0011;
        };
    L_0x0011:
        goto L_0x0044;
    L_0x0012:
        r1 = "default";
        r0 = r0.equals(r1);
        if (r0 == 0) goto L_0x0011;
    L_0x001a:
        r0 = 0;
        goto L_0x0045;
    L_0x001c:
        r1 = "page";
        r0 = r0.equals(r1);
        if (r0 == 0) goto L_0x0011;
    L_0x0024:
        r0 = 4;
        goto L_0x0045;
    L_0x0026:
        r1 = "doubletap";
        r0 = r0.equals(r1);
        if (r0 == 0) goto L_0x0011;
    L_0x002e:
        r0 = 2;
        goto L_0x0045;
    L_0x0030:
        r1 = "prompt";
        r0 = r0.equals(r1);
        if (r0 == 0) goto L_0x0011;
    L_0x0038:
        r0 = 3;
        goto L_0x0045;
    L_0x003a:
        r1 = "drawer";
        r0 = r0.equals(r1);
        if (r0 == 0) goto L_0x0011;
    L_0x0042:
        r0 = 1;
        goto L_0x0045;
    L_0x0044:
        r0 = -1;
    L_0x0045:
        switch(r0) {
            case 0: goto L_0x0057;
            case 1: goto L_0x0054;
            case 2: goto L_0x0051;
            case 3: goto L_0x004e;
            case 4: goto L_0x004b;
            default: goto L_0x0048;
        };
    L_0x0048:
        r0 = de.danoeh.antennapod.core.preferences.UserPreferences$BackButtonBehavior.DEFAULT;
        return r0;
    L_0x004b:
        r0 = de.danoeh.antennapod.core.preferences.UserPreferences$BackButtonBehavior.GO_TO_PAGE;
        return r0;
    L_0x004e:
        r0 = de.danoeh.antennapod.core.preferences.UserPreferences$BackButtonBehavior.SHOW_PROMPT;
        return r0;
    L_0x0051:
        r0 = de.danoeh.antennapod.core.preferences.UserPreferences$BackButtonBehavior.DOUBLE_TAP;
        return r0;
    L_0x0054:
        r0 = de.danoeh.antennapod.core.preferences.UserPreferences$BackButtonBehavior.OPEN_DRAWER;
        return r0;
    L_0x0057:
        r0 = de.danoeh.antennapod.core.preferences.UserPreferences$BackButtonBehavior.DEFAULT;
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: de.danoeh.antennapod.core.preferences.UserPreferences.getBackButtonBehavior():de.danoeh.antennapod.core.preferences.UserPreferences$BackButtonBehavior");
    }

    public static String getBackButtonGoToPage() {
        return prefs.getString(PREF_BACK_BUTTON_GO_TO_PAGE, QueueFragment.TAG);
    }

    public static void setBackButtonGoToPage(String tag) {
        prefs.edit().putString(PREF_BACK_BUTTON_GO_TO_PAGE, tag).apply();
    }
}
