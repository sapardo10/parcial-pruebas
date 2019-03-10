package de.danoeh.antennapod.core.service.playback;

import org.antennapod.audio.MediaPlayer;
import org.antennapod.audio.MediaPlayer.OnErrorListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocalPSMP$adQikeYyVzVQogfsntepKgfVU7E implements OnErrorListener {
    private final /* synthetic */ LocalPSMP f$0;

    public /* synthetic */ -$$Lambda$LocalPSMP$adQikeYyVzVQogfsntepKgfVU7E(LocalPSMP localPSMP) {
        this.f$0 = localPSMP;
    }

    public final boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
        return LocalPSMP.lambda$new$26(this.f$0, mediaPlayer, i, i2);
    }
}
