package de.danoeh.antennapod.core.service.playback;

import org.antennapod.audio.MediaPlayer;
import org.antennapod.audio.MediaPlayer.OnInfoListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocalPSMP$dUL1B5dMKdXZmJOkWpvIIZ6vpN0 implements OnInfoListener {
    private final /* synthetic */ LocalPSMP f$0;

    public /* synthetic */ -$$Lambda$LocalPSMP$dUL1B5dMKdXZmJOkWpvIIZ6vpN0(LocalPSMP localPSMP) {
        this.f$0 = localPSMP;
    }

    public final boolean onInfo(MediaPlayer mediaPlayer, int i, int i2) {
        return this.f$0.genericInfoListener(i);
    }
}
