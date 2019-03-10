package de.danoeh.antennapod.core.util.playback;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.SurfaceHolder;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import org.antennapod.audio.MediaPlayer;

public class AudioPlayer extends MediaPlayer implements IPlayer {
    private static final String TAG = "AudioPlayer";
    private final OnSharedPreferenceChangeListener sonicListener = new -$$Lambda$AudioPlayer$hpxl0M_qiq8z7t8luDmZI9McnQA();

    public AudioPlayer(Context context) {
        super(context);
        PreferenceManager.getDefaultSharedPreferences(context).registerOnSharedPreferenceChangeListener(this.sonicListener);
    }

    public static /* synthetic */ void lambda$new$0(AudioPlayer audioPlayer, SharedPreferences sharedPreferences, String key) {
        if (key.equals(UserPreferences.PREF_MEDIA_PLAYER)) {
            audioPlayer.checkMpi();
        }
    }

    public void setDisplay(SurfaceHolder sh) {
        if (sh != null) {
            Log.e(TAG, "Setting display not supported in Audio Player");
            throw new UnsupportedOperationException("Setting display not supported in Audio Player");
        }
    }

    public void setPlaybackParams(float speed, boolean skipSilence) {
        if (canSetSpeed()) {
            setPlaybackSpeed(speed);
        }
    }

    protected boolean useSonic() {
        return UserPreferences.useSonic();
    }

    protected boolean downmix() {
        return UserPreferences.stereoToMono();
    }
}
