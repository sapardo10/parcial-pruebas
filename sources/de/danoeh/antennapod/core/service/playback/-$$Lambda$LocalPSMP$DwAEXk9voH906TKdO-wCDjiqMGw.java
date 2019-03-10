package de.danoeh.antennapod.core.service.playback;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocalPSMP$DwAEXk9voH906TKdO-wCDjiqMGw implements OnBufferingUpdateListener {
    private final /* synthetic */ LocalPSMP f$0;

    public /* synthetic */ -$$Lambda$LocalPSMP$DwAEXk9voH906TKdO-wCDjiqMGw(LocalPSMP localPSMP) {
        this.f$0 = localPSMP;
    }

    public final void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
        this.f$0.genericOnBufferingUpdate(i);
    }
}
