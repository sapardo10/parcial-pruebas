package de.danoeh.antennapod.core.service.playback;

import org.antennapod.audio.MediaPlayer;
import org.antennapod.audio.MediaPlayer.OnSeekCompleteListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocalPSMP$pIP2fYSS5cCg2ky6sVLlFFEQ1Fs implements OnSeekCompleteListener {
    private final /* synthetic */ LocalPSMP f$0;

    public /* synthetic */ -$$Lambda$LocalPSMP$pIP2fYSS5cCg2ky6sVLlFFEQ1Fs(LocalPSMP localPSMP) {
        this.f$0 = localPSMP;
    }

    public final void onSeekComplete(MediaPlayer mediaPlayer) {
        this.f$0.genericSeekCompleteListener();
    }
}
