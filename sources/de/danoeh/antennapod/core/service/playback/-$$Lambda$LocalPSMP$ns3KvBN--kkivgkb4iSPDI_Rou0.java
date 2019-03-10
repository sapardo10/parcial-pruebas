package de.danoeh.antennapod.core.service.playback;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnSeekCompleteListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocalPSMP$ns3KvBN--kkivgkb4iSPDI_Rou0 implements OnSeekCompleteListener {
    private final /* synthetic */ LocalPSMP f$0;

    public /* synthetic */ -$$Lambda$LocalPSMP$ns3KvBN--kkivgkb4iSPDI_Rou0(LocalPSMP localPSMP) {
        this.f$0 = localPSMP;
    }

    public final void onSeekComplete(MediaPlayer mediaPlayer) {
        this.f$0.genericSeekCompleteListener();
    }
}
