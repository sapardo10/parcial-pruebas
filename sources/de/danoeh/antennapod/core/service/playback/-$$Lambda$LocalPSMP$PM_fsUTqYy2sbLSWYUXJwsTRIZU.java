package de.danoeh.antennapod.core.service.playback;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocalPSMP$PM_fsUTqYy2sbLSWYUXJwsTRIZU implements OnErrorListener {
    private final /* synthetic */ LocalPSMP f$0;

    public /* synthetic */ -$$Lambda$LocalPSMP$PM_fsUTqYy2sbLSWYUXJwsTRIZU(LocalPSMP localPSMP) {
        this.f$0 = localPSMP;
    }

    public final boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
        return this.f$0.genericOnError(mediaPlayer, i, i2);
    }
}
