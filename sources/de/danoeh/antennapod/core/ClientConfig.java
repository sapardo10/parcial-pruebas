package de.danoeh.antennapod.core;

import android.content.Context;
import de.danoeh.antennapod.core.preferences.PlaybackPreferences;
import de.danoeh.antennapod.core.preferences.SleepTimerPreferences;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.storage.PodDBAdapter;
import de.danoeh.antennapod.core.util.NetworkUtils;

public class ClientConfig {
    public static String USER_AGENT;
    public static ApplicationCallbacks applicationCallbacks;
    public static CastCallbacks castCallbacks;
    public static DBTasksCallbacks dbTasksCallbacks;
    public static DownloadServiceCallbacks downloadServiceCallbacks;
    public static FlattrCallbacks flattrCallbacks;
    public static GpodnetCallbacks gpodnetCallbacks;
    private static boolean initialized = false;
    public static PlaybackServiceCallbacks playbackServiceCallbacks;

    public static synchronized void initialize(Context context) {
        synchronized (ClientConfig.class) {
            if (initialized) {
                return;
            }
            PodDBAdapter.init(context);
            UserPreferences.init(context);
            UpdateManager.init(context);
            PlaybackPreferences.init(context);
            NetworkUtils.init(context);
            SleepTimerPreferences.init(context);
            initialized = true;
        }
    }
}
