package de.danoeh.antennapod.core.service.playback;

import org.antennapod.audio.MediaPlayer;
import org.antennapod.audio.MediaPlayer.OnSpeedAdjustmentAvailableChangedListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocalPSMP$xNHo6PbNpLlMjMJXXuLzOUMBPKQ implements OnSpeedAdjustmentAvailableChangedListener {
    private final /* synthetic */ LocalPSMP f$0;

    public /* synthetic */ -$$Lambda$LocalPSMP$xNHo6PbNpLlMjMJXXuLzOUMBPKQ(LocalPSMP localPSMP) {
        this.f$0 = localPSMP;
    }

    public final void onSpeedAdjustmentAvailableChanged(MediaPlayer mediaPlayer, boolean z) {
        this.f$0.callback.setSpeedAbilityChanged();
    }
}
