package de.danoeh.antennapod.core;

import android.content.Context;
import android.content.Intent;
import de.danoeh.antennapod.core.feed.MediaType;

public interface PlaybackServiceCallbacks {
    int getNotificationIconResource(Context context);

    Intent getPlayerActivityIntent(Context context, MediaType mediaType, boolean z);

    boolean useQueue();
}
