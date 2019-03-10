package de.danoeh.antennapod.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import de.danoeh.antennapod.core.service.playback.PlaybackService;
import java.util.concurrent.atomic.AtomicBoolean;

public class CastplayerActivity extends MediaplayerInfoActivity {
    private static final String TAG = "CastPlayerActivity";
    private final AtomicBoolean isSetup = new AtomicBoolean(false);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!PlaybackService.isCasting()) {
            Intent intent = PlaybackService.getPlayerActivityIntent(this);
            if (!intent.getComponent().getClassName().equals(CastplayerActivity.class.getName())) {
                finish();
                startActivity(intent);
            }
        }
    }

    protected void onReloadNotification(int notificationCode) {
        if (notificationCode == 1) {
            Log.d(TAG, "ReloadNotification received, switching to Audioplayer now");
            saveCurrentFragment();
            finish();
            startActivity(new Intent(this, AudioplayerActivity.class));
            return;
        }
        super.onReloadNotification(notificationCode);
    }

    protected void setupGUI() {
        if (!this.isSetup.getAndSet(true)) {
            super.setupGUI();
            if (this.butPlaybackSpeed != null) {
                this.butPlaybackSpeed.setVisibility(8);
            }
        }
    }

    protected void onResume() {
        if (!PlaybackService.isCasting()) {
            Intent intent = PlaybackService.getPlayerActivityIntent(this);
            if (!intent.getComponent().getClassName().equals(CastplayerActivity.class.getName())) {
                saveCurrentFragment();
                finish();
                startActivity(intent);
            }
        }
        super.onResume();
    }

    protected void onBufferStart() {
        this.sbPosition.setEnabled(false);
    }

    protected void onBufferEnd() {
        this.sbPosition.setEnabled(true);
    }
}
