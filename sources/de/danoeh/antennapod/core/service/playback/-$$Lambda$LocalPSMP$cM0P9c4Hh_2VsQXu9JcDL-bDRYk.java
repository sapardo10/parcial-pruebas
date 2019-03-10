package de.danoeh.antennapod.core.service.playback;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnInfoListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocalPSMP$cM0P9c4Hh_2VsQXu9JcDL-bDRYk implements OnInfoListener {
    private final /* synthetic */ LocalPSMP f$0;

    public /* synthetic */ -$$Lambda$LocalPSMP$cM0P9c4Hh_2VsQXu9JcDL-bDRYk(LocalPSMP localPSMP) {
        this.f$0 = localPSMP;
    }

    public final boolean onInfo(MediaPlayer mediaPlayer, int i, int i2) {
        return this.f$0.genericInfoListener(i);
    }
}
