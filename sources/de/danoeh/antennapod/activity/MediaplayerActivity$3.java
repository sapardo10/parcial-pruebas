package de.danoeh.antennapod.activity;

import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.dialog.VariableSpeedDialog;
import java.util.Locale;

class MediaplayerActivity$3 implements OnSeekBarChangeListener {
    final /* synthetic */ MediaplayerActivity this$0;
    final /* synthetic */ SeekBar val$barPlaybackSpeed;
    final /* synthetic */ float val$minPlaybackSpeed;
    final /* synthetic */ TextView val$txtvPlaybackSpeed;

    MediaplayerActivity$3(MediaplayerActivity this$0, float f, TextView textView, SeekBar seekBar) {
        this.this$0 = this$0;
        this.val$minPlaybackSpeed = f;
        this.val$txtvPlaybackSpeed = textView;
        this.val$barPlaybackSpeed = seekBar;
    }

    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        float playbackSpeed;
        if (this.this$0.controller != null && this.this$0.controller.canSetPlaybackSpeed()) {
            this.this$0.controller.setPlaybackSpeed((((float) progress) * 0.05f) + this.val$minPlaybackSpeed);
            UserPreferences.setPlaybackSpeed(String.format(Locale.US, "%.2f", new Object[]{Float.valueOf(playbackSpeed)}));
            this.val$txtvPlaybackSpeed.setText(String.format("%.2fx", new Object[]{Float.valueOf(playbackSpeed)}));
        } else if (fromUser) {
            playbackSpeed = Float.valueOf(UserPreferences.getPlaybackSpeed()).floatValue();
            SeekBar seekBar2 = this.val$barPlaybackSpeed;
            seekBar2.post(new -$$Lambda$MediaplayerActivity$3$M3c24AkvoREySER4uTVoWjzQA7Q(seekBar2, playbackSpeed, this.val$minPlaybackSpeed));
        }
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
        if (this.this$0.controller != null && !this.this$0.controller.canSetPlaybackSpeed()) {
            VariableSpeedDialog.showGetPluginDialog(this.this$0);
        }
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}
