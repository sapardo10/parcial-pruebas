package de.danoeh.antennapod.core.preferences;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.text.TextUtils;
import android.util.Log;
import de.danoeh.antennapod.core.BuildConfig;
import de.danoeh.antennapod.core.ClientConfig;
import de.danoeh.antennapod.core.gpoddernet.model.GpodnetEpisodeAction;
import de.danoeh.antennapod.core.service.GpodnetSyncService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.commons.lang3.StringUtils;

public class GpodnetPreferences {
    private static final String PREF_GPODNET_DEVICEID = "de.danoeh.antennapod.preferences.gpoddernet.deviceID";
    private static final String PREF_GPODNET_HOSTNAME = "prefGpodnetHostname";
    private static final String PREF_GPODNET_PASSWORD = "de.danoeh.antennapod.preferences.gpoddernet.password";
    private static final String PREF_GPODNET_USERNAME = "de.danoeh.antennapod.preferences.gpoddernet.username";
    private static final String PREF_LAST_EPISODE_ACTIONS_SYNC_TIMESTAMP = "de.danoeh.antennapod.preferences.gpoddernet.last_episode_actions_sync_timestamp";
    private static final String PREF_LAST_SUBSCRIPTION_SYNC_TIMESTAMP = "de.danoeh.antennapod.preferences.gpoddernet.last_sync_timestamp";
    private static final String PREF_LAST_SYNC_ATTEMPT_RESULT = "de.danoeh.antennapod.preferences.gpoddernet.last_sync_attempt_result";
    public static final String PREF_LAST_SYNC_ATTEMPT_TIMESTAMP = "de.danoeh.antennapod.preferences.gpoddernet.last_sync_attempt_timestamp";
    private static final String PREF_NAME = "gpodder.net";
    private static final String PREF_SYNC_ADDED = "de.danoeh.antennapod.preferences.gpoddernet.sync_added";
    private static final String PREF_SYNC_EPISODE_ACTIONS = "de.danoeh.antennapod.preferences.gpoddernet.sync_queued_episode_actions";
    private static final String PREF_SYNC_REMOVED = "de.danoeh.antennapod.preferences.gpoddernet.sync_removed";
    private static final String TAG = "GpodnetPreferences";
    private static Set<String> addedFeeds;
    private static String deviceID;
    private static final ReentrantLock feedListLock = new ReentrantLock();
    private static String hostname;
    private static long lastEpisodeActionsSyncTimeStamp;
    private static long lastSubscriptionSyncTimestamp;
    private static boolean lastSyncAttemptResult;
    private static long lastSyncAttemptTimestamp;
    private static String password;
    private static boolean preferencesLoaded = false;
    private static List<GpodnetEpisodeAction> queuedEpisodeActions;
    private static Set<String> removedFeeds;
    private static String username;

    private GpodnetPreferences() {
    }

    private static SharedPreferences getPreferences() {
        return ClientConfig.applicationCallbacks.getApplicationInstance().getSharedPreferences("gpodder.net", 0);
    }

    public static void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        getPreferences().registerOnSharedPreferenceChangeListener(listener);
    }

    public static void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        getPreferences().unregisterOnSharedPreferenceChangeListener(listener);
    }

    private static synchronized void ensurePreferencesLoaded() {
        synchronized (GpodnetPreferences.class) {
            if (!preferencesLoaded) {
                SharedPreferences prefs = getPreferences();
                username = prefs.getString(PREF_GPODNET_USERNAME, null);
                password = prefs.getString(PREF_GPODNET_PASSWORD, null);
                deviceID = prefs.getString(PREF_GPODNET_DEVICEID, null);
                lastSubscriptionSyncTimestamp = prefs.getLong(PREF_LAST_SUBSCRIPTION_SYNC_TIMESTAMP, 0);
                lastEpisodeActionsSyncTimeStamp = prefs.getLong(PREF_LAST_EPISODE_ACTIONS_SYNC_TIMESTAMP, 0);
                lastSyncAttemptTimestamp = prefs.getLong(PREF_LAST_SYNC_ATTEMPT_TIMESTAMP, 0);
                lastSyncAttemptResult = prefs.getBoolean(PREF_LAST_SYNC_ATTEMPT_RESULT, false);
                addedFeeds = readListFromString(prefs.getString(PREF_SYNC_ADDED, ""));
                removedFeeds = readListFromString(prefs.getString(PREF_SYNC_REMOVED, ""));
                queuedEpisodeActions = readEpisodeActionsFromString(prefs.getString(PREF_SYNC_EPISODE_ACTIONS, ""));
                hostname = checkGpodnetHostname(prefs.getString(PREF_GPODNET_HOSTNAME, "gpodder.net"));
                preferencesLoaded = true;
            }
        }
    }

    private static void writePreference(String key, String value) {
        Editor editor = getPreferences().edit();
        editor.putString(key, value);
        editor.apply();
    }

    private static void writePreference(String key, long value) {
        Editor editor = getPreferences().edit();
        editor.putLong(key, value);
        editor.apply();
    }

    private static void writePreference(String key, Collection<String> value) {
        Editor editor = getPreferences().edit();
        editor.putString(key, writeListToString(value));
        editor.apply();
    }

    private static void writePreference(String key, boolean value) {
        Editor editor = getPreferences().edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static String getUsername() {
        ensurePreferencesLoaded();
        return username;
    }

    public static void setUsername(String username) {
        username = username;
        writePreference(PREF_GPODNET_USERNAME, username);
    }

    public static String getPassword() {
        ensurePreferencesLoaded();
        return password;
    }

    public static void setPassword(String password) {
        password = password;
        writePreference(PREF_GPODNET_PASSWORD, password);
    }

    public static String getDeviceID() {
        ensurePreferencesLoaded();
        return deviceID;
    }

    public static void setDeviceID(String deviceID) {
        deviceID = deviceID;
        writePreference(PREF_GPODNET_DEVICEID, deviceID);
    }

    public static long getLastSubscriptionSyncTimestamp() {
        ensurePreferencesLoaded();
        return lastSubscriptionSyncTimestamp;
    }

    public static void setLastSubscriptionSyncTimestamp(long timestamp) {
        lastSubscriptionSyncTimestamp = timestamp;
        writePreference(PREF_LAST_SUBSCRIPTION_SYNC_TIMESTAMP, timestamp);
    }

    public static long getLastEpisodeActionsSyncTimestamp() {
        ensurePreferencesLoaded();
        return lastEpisodeActionsSyncTimeStamp;
    }

    public static void setLastEpisodeActionsSyncTimestamp(long timestamp) {
        lastEpisodeActionsSyncTimeStamp = timestamp;
        writePreference(PREF_LAST_EPISODE_ACTIONS_SYNC_TIMESTAMP, timestamp);
    }

    public static long getLastSyncAttemptTimestamp() {
        ensurePreferencesLoaded();
        return lastSyncAttemptTimestamp;
    }

    public static boolean getLastSyncAttemptResult() {
        ensurePreferencesLoaded();
        return lastSyncAttemptResult;
    }

    public static void setLastSyncAttempt(boolean result, long timestamp) {
        lastSyncAttemptResult = result;
        lastSyncAttemptTimestamp = timestamp;
        writePreference(PREF_LAST_SYNC_ATTEMPT_RESULT, result);
        writePreference(PREF_LAST_SYNC_ATTEMPT_TIMESTAMP, timestamp);
    }

    public static String getHostname() {
        ensurePreferencesLoaded();
        return hostname;
    }

    public static void setHostname(String value) {
        value = checkGpodnetHostname(value);
        if (!value.equals(hostname)) {
            logout();
            writePreference(PREF_GPODNET_HOSTNAME, value);
            hostname = value;
        }
    }

    public static void addAddedFeed(String feed) {
        ensurePreferencesLoaded();
        feedListLock.lock();
        if (addedFeeds.add(feed)) {
            writePreference(PREF_SYNC_ADDED, addedFeeds);
        }
        if (removedFeeds.remove(feed)) {
            writePreference(PREF_SYNC_REMOVED, removedFeeds);
        }
        feedListLock.unlock();
        GpodnetSyncService.sendSyncSubscriptionsIntent(ClientConfig.applicationCallbacks.getApplicationInstance());
    }

    public static void addRemovedFeed(String feed) {
        ensurePreferencesLoaded();
        feedListLock.lock();
        if (removedFeeds.add(feed)) {
            writePreference(PREF_SYNC_REMOVED, removedFeeds);
        }
        if (addedFeeds.remove(feed)) {
            writePreference(PREF_SYNC_ADDED, addedFeeds);
        }
        feedListLock.unlock();
        GpodnetSyncService.sendSyncSubscriptionsIntent(ClientConfig.applicationCallbacks.getApplicationInstance());
    }

    public static Set<String> getAddedFeedsCopy() {
        ensurePreferencesLoaded();
        Set<String> copy = new HashSet();
        feedListLock.lock();
        copy.addAll(addedFeeds);
        feedListLock.unlock();
        return copy;
    }

    public static void removeAddedFeeds(Collection<String> removed) {
        ensurePreferencesLoaded();
        feedListLock.lock();
        addedFeeds.removeAll(removed);
        writePreference(PREF_SYNC_ADDED, addedFeeds);
        feedListLock.unlock();
    }

    public static Set<String> getRemovedFeedsCopy() {
        ensurePreferencesLoaded();
        Set<String> copy = new HashSet();
        feedListLock.lock();
        copy.addAll(removedFeeds);
        feedListLock.unlock();
        return copy;
    }

    public static void removeRemovedFeeds(Collection<String> removed) {
        ensurePreferencesLoaded();
        feedListLock.lock();
        removedFeeds.removeAll(removed);
        writePreference(PREF_SYNC_REMOVED, removedFeeds);
        feedListLock.unlock();
    }

    public static void enqueueEpisodeAction(GpodnetEpisodeAction action) {
        ensurePreferencesLoaded();
        feedListLock.lock();
        queuedEpisodeActions.add(action);
        writePreference(PREF_SYNC_EPISODE_ACTIONS, writeEpisodeActionsToString(queuedEpisodeActions));
        feedListLock.unlock();
        GpodnetSyncService.sendSyncActionsIntent(ClientConfig.applicationCallbacks.getApplicationInstance());
    }

    public static List<GpodnetEpisodeAction> getQueuedEpisodeActions() {
        ensurePreferencesLoaded();
        List<GpodnetEpisodeAction> copy = new ArrayList();
        feedListLock.lock();
        copy.addAll(queuedEpisodeActions);
        feedListLock.unlock();
        return copy;
    }

    public static void removeQueuedEpisodeActions(Collection<GpodnetEpisodeAction> queued) {
        ensurePreferencesLoaded();
        feedListLock.lock();
        queuedEpisodeActions.removeAll(queued);
        writePreference(PREF_SYNC_EPISODE_ACTIONS, writeEpisodeActionsToString(queuedEpisodeActions));
        feedListLock.unlock();
    }

    public static boolean loggedIn() {
        ensurePreferencesLoaded();
        return (deviceID == null || username == null || password == null) ? false : true;
    }

    public static synchronized void logout() {
        synchronized (GpodnetPreferences.class) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Logout: Clearing preferences");
            }
            setUsername(null);
            setPassword(null);
            setDeviceID(null);
            feedListLock.lock();
            addedFeeds.clear();
            writePreference(PREF_SYNC_ADDED, addedFeeds);
            removedFeeds.clear();
            writePreference(PREF_SYNC_REMOVED, removedFeeds);
            queuedEpisodeActions.clear();
            writePreference(PREF_SYNC_EPISODE_ACTIONS, writeEpisodeActionsToString(queuedEpisodeActions));
            feedListLock.unlock();
            setLastSubscriptionSyncTimestamp(0);
            setLastSyncAttempt(false, 0);
            UserPreferences.setGpodnetNotificationsEnabled();
        }
    }

    private static Set<String> readListFromString(String s) {
        Set<String> result = new HashSet();
        Collections.addAll(result, s.split(StringUtils.SPACE));
        return result;
    }

    private static String writeListToString(Collection<String> c) {
        StringBuilder result = new StringBuilder();
        for (String item : c) {
            result.append(item);
            result.append(StringUtils.SPACE);
        }
        return result.toString().trim();
    }

    private static List<GpodnetEpisodeAction> readEpisodeActionsFromString(String s) {
        String[] lines = s.split("\n");
        List<GpodnetEpisodeAction> result = new ArrayList(lines.length);
        for (String line : lines) {
            if (TextUtils.isEmpty(line)) {
                if (GpodnetEpisodeAction.readFromString(line) != null) {
                    result.add(GpodnetEpisodeAction.readFromString(line));
                }
            }
        }
        return result;
    }

    private static String writeEpisodeActionsToString(Collection<GpodnetEpisodeAction> c) {
        StringBuilder result = new StringBuilder();
        for (GpodnetEpisodeAction item : c) {
            result.append(item.writeToString());
            result.append("\n");
        }
        return result.toString();
    }

    private static String checkGpodnetHostname(String value) {
        int startIndex = 0;
        if (value.startsWith("http://")) {
            startIndex = "http://".length();
        } else if (value.startsWith("https://")) {
            startIndex = "https://".length();
        }
        return value.substring(startIndex);
    }
}
