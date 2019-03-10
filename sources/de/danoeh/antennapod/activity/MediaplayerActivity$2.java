package de.danoeh.antennapod.activity;

import android.content.Context;
import de.danoeh.antennapod.dialog.SleepTimerDialog;

class MediaplayerActivity$2 extends SleepTimerDialog {
    final /* synthetic */ MediaplayerActivity this$0;

    MediaplayerActivity$2(MediaplayerActivity this$0, Context context) {
        this.this$0 = this$0;
        super(context);
    }

    public void onTimerSet(long millis, boolean shakeToReset, boolean vibrate) {
        this.this$0.controller.setSleepTimer(millis, shakeToReset, vibrate);
    }
}
