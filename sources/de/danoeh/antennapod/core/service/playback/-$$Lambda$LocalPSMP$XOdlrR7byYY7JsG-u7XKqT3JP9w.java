package de.danoeh.antennapod.core.service.playback;

import org.antennapod.audio.MediaPlayer;
import org.antennapod.audio.MediaPlayer.OnBufferingUpdateListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocalPSMP$XOdlrR7byYY7JsG-u7XKqT3JP9w implements OnBufferingUpdateListener {
    private final /* synthetic */ LocalPSMP f$0;

    public /* synthetic */ -$$Lambda$LocalPSMP$XOdlrR7byYY7JsG-u7XKqT3JP9w(LocalPSMP localPSMP) {
        this.f$0 = localPSMP;
    }

    public final void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
        this.f$0.genericOnBufferingUpdate(i);
    }
}
