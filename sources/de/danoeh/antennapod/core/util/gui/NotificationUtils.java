package de.danoeh.antennapod.core.util.gui;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build.VERSION;
import android.support.annotation.RequiresApi;
import de.danoeh.antennapod.core.C0734R;

public class NotificationUtils {
    public static final String CHANNEL_ID_DOWNLOADING = "downloading";
    public static final String CHANNEL_ID_ERROR = "error";
    public static final String CHANNEL_ID_PLAYING = "playing";
    public static final String CHANNEL_ID_USER_ACTION = "user_action";

    public static void createChannels(Context context) {
        if (VERSION.SDK_INT >= 26) {
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService("notification");
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(createChannelUserAction(context));
                mNotificationManager.createNotificationChannel(createChannelDownloading(context));
                mNotificationManager.createNotificationChannel(createChannelPlaying(context));
                mNotificationManager.createNotificationChannel(createChannelError(context));
            }
        }
    }

    @RequiresApi(api = 26)
    private static NotificationChannel createChannelUserAction(Context c) {
        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID_USER_ACTION, c.getString(C0734R.string.notification_channel_user_action), 4);
        mChannel.setDescription(c.getString(C0734R.string.notification_channel_user_action_description));
        return mChannel;
    }

    @RequiresApi(api = 26)
    private static NotificationChannel createChannelDownloading(Context c) {
        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID_DOWNLOADING, c.getString(C0734R.string.notification_channel_downloading), 2);
        mChannel.setDescription(c.getString(C0734R.string.notification_channel_downloading_description));
        mChannel.setShowBadge(false);
        return mChannel;
    }

    @RequiresApi(api = 26)
    private static NotificationChannel createChannelPlaying(Context c) {
        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID_PLAYING, c.getString(C0734R.string.notification_channel_playing), 2);
        mChannel.setDescription(c.getString(C0734R.string.notification_channel_playing_description));
        mChannel.setShowBadge(false);
        return mChannel;
    }

    @RequiresApi(api = 26)
    private static NotificationChannel createChannelError(Context c) {
        NotificationChannel mChannel = new NotificationChannel("error", c.getString(C0734R.string.notification_channel_error), 4);
        mChannel.setDescription(c.getString(C0734R.string.notification_channel_error_description));
        return mChannel;
    }
}
