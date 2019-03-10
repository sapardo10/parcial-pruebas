package de.danoeh.antennapod.config;

import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import de.danoeh.antennapod.activity.AudioplayerActivity;
import de.danoeh.antennapod.activity.CastplayerActivity;
import de.danoeh.antennapod.activity.VideoplayerActivity;
import de.danoeh.antennapod.core.PlaybackServiceCallbacks;
import de.danoeh.antennapod.core.feed.MediaType;
import de.danoeh.antennapod.debug.R;

public class PlaybackServiceCallbacksImpl implements PlaybackServiceCallbacks {
    public Intent getPlayerActivityIntent(Context context, MediaType mediaType, boolean remotePlayback) {
        if (remotePlayback) {
            return new Intent(context, CastplayerActivity.class);
        }
        if (mediaType != MediaType.VIDEO) {
            return new Intent(context, AudioplayerActivity.class);
        }
        Intent i = new Intent(context, VideoplayerActivity.class);
        if (VERSION.SDK_INT >= 21) {
            i.addFlags(524288);
        }
        return i;
    }

    public boolean useQueue() {
        return true;
    }

    public int getNotificationIconResource(Context context) {
        return R.drawable.ic_stat_antenna_default;
    }
}
