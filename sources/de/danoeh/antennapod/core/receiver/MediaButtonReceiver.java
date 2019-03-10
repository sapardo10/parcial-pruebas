package de.danoeh.antennapod.core.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import de.danoeh.antennapod.core.ClientConfig;
import de.danoeh.antennapod.core.service.playback.PlaybackService;

public class MediaButtonReceiver extends BroadcastReceiver {
    public static final String EXTRA_KEYCODE = "de.danoeh.antennapod.core.service.extra.MediaButtonReceiver.KEYCODE";
    public static final String EXTRA_SOURCE = "de.danoeh.antennapod.core.service.extra.MediaButtonReceiver.SOURCE";
    public static final String NOTIFY_BUTTON_RECEIVER = "de.danoeh.antennapod.NOTIFY_BUTTON_RECEIVER";
    private static final String TAG = "MediaButtonReceiver";

    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Received intent");
        if (intent != null) {
            if (intent.getExtras() != null) {
                KeyEvent event = (KeyEvent) intent.getExtras().get("android.intent.extra.KEY_EVENT");
                if (event != null && event.getAction() == 0 && event.getRepeatCount() == 0) {
                    ClientConfig.initialize(context);
                    Intent serviceIntent = new Intent(context, PlaybackService.class);
                    serviceIntent.putExtra(EXTRA_KEYCODE, event.getKeyCode());
                    serviceIntent.putExtra(EXTRA_SOURCE, event.getSource());
                    ContextCompat.startForegroundService(context, serviceIntent);
                }
            }
        }
    }
}
