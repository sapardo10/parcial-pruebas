package de.danoeh.antennapod.core.service.playback;

import org.antennapod.audio.MediaPlayer;
import org.antennapod.audio.MediaPlayer.OnCompletionListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocalPSMP$p_TOTFdKxkrPqgwevRM_wdhMKew implements OnCompletionListener {
    private final /* synthetic */ LocalPSMP f$0;

    public /* synthetic */ -$$Lambda$LocalPSMP$p_TOTFdKxkrPqgwevRM_wdhMKew(LocalPSMP localPSMP) {
        this.f$0 = localPSMP;
    }

    public final void onCompletion(MediaPlayer mediaPlayer) {
        this.f$0.genericOnCompletion();
    }
}
