package de.danoeh.antennapod.core.service.playback;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat.Builder;

class PlaybackServiceFlavorHelper {
    private final FlavorHelperCallback callback;

    PlaybackServiceFlavorHelper(Context context, FlavorHelperCallback callback) {
        this.callback = callback;
    }

    void initializeMediaPlayer(Context context) {
        FlavorHelperCallback flavorHelperCallback = this.callback;
        flavorHelperCallback.setMediaPlayer(new LocalPSMP(context, flavorHelperCallback.getMediaPlayerCallback()));
    }

    void removeCastConsumer() {
    }

    boolean castDisconnect(boolean castDisconnect) {
        return false;
    }

    boolean onMediaPlayerInfo(Context context, int code, @StringRes int resourceId) {
        return false;
    }

    void registerWifiBroadcastReceiver() {
    }

    void unregisterWifiBroadcastReceiver() {
    }

    boolean onSharedPreference(String key) {
        return false;
    }

    void sessionStateAddActionForWear(Builder sessionState, String actionName, CharSequence name, int icon) {
    }

    void mediaSessionSetExtraForWear(MediaSessionCompat mediaSession) {
    }
}
