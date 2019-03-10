package de.danoeh.antennapod.activity;

import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import de.danoeh.antennapod.core.util.Converter;

class MediaplayerActivity$4 implements OnSeekBarChangeListener {
    final /* synthetic */ MediaplayerActivity this$0;
    final /* synthetic */ SeekBar val$barRightVolume;

    MediaplayerActivity$4(MediaplayerActivity this$0, SeekBar seekBar) {
        this.this$0 = this$0;
        this.val$barRightVolume = seekBar;
    }

    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        this.this$0.controller.setVolume(Converter.getVolumeFromPercentage(progress), Converter.getVolumeFromPercentage(this.val$barRightVolume.getProgress()));
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}
