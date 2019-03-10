package de.danoeh.antennapod.core.service.playback;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocalPSMP$RFboA3aLMXI6EAWVLRMU6Hw75sI implements OnCompletionListener {
    private final /* synthetic */ LocalPSMP f$0;

    public /* synthetic */ -$$Lambda$LocalPSMP$RFboA3aLMXI6EAWVLRMU6Hw75sI(LocalPSMP localPSMP) {
        this.f$0 = localPSMP;
    }

    public final void onCompletion(MediaPlayer mediaPlayer) {
        this.f$0.genericOnCompletion();
    }
}
