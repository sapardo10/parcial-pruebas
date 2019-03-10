package de.danoeh.antennapod.activity;

import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import de.danoeh.antennapod.core.util.Converter;

class MediaplayerActivity$5 implements OnSeekBarChangeListener {
    final /* synthetic */ MediaplayerActivity this$0;
    final /* synthetic */ SeekBar val$barLeftVolume;

    MediaplayerActivity$5(MediaplayerActivity this$0, SeekBar seekBar) {
        this.this$0 = this$0;
        this.val$barLeftVolume = seekBar;
    }

    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        this.this$0.controller.setVolume(Converter.getVolumeFromPercentage(this.val$barLeftVolume.getProgress()), Converter.getVolumeFromPercentage(progress));
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}
