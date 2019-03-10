package de.danoeh.antennapod.core.util.playback;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AudioPlayer$hpxl0M_qiq8z7t8luDmZI9McnQA implements OnSharedPreferenceChangeListener {
    private final /* synthetic */ AudioPlayer f$0;

    public /* synthetic */ -$$Lambda$AudioPlayer$hpxl0M_qiq8z7t8luDmZI9McnQA(AudioPlayer audioPlayer) {
        this.f$0 = audioPlayer;
    }

    public final void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
        AudioPlayer.lambda$new$0(this.f$0, sharedPreferences, str);
    }
}
