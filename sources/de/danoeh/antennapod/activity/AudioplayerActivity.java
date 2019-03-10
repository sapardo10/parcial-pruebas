package de.danoeh.antennapod.activity;

import android.content.Intent;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import de.danoeh.antennapod.core.feed.MediaType;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.service.playback.PlaybackService;
import de.danoeh.antennapod.dialog.VariableSpeedDialog;
import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicBoolean;

public class AudioplayerActivity extends MediaplayerInfoActivity {
    private static final String TAG = "AudioPlayerActivity";
    private final AtomicBoolean isSetup = new AtomicBoolean(false);

    protected void onResume() {
        super.onResume();
        if (TextUtils.equals(getIntent().getAction(), "android.intent.action.VIEW")) {
            playExternalMedia(getIntent(), MediaType.AUDIO);
        } else if (PlaybackService.isCasting()) {
            Intent intent = PlaybackService.getPlayerActivityIntent(this);
            if (intent.getComponent() == null) {
                return;
            }
            if (!intent.getComponent().getClassName().equals(AudioplayerActivity.class.getName())) {
                saveCurrentFragment();
                finish();
                startActivity(intent);
            }
        }
    }

    protected void onReloadNotification(int notificationCode) {
        if (notificationCode == 3) {
            Log.d(TAG, "ReloadNotification received, switching to Castplayer now");
            saveCurrentFragment();
            finish();
            startActivity(new Intent(this, CastplayerActivity.class));
            return;
        }
        super.onReloadNotification(notificationCode);
    }

    protected void updatePlaybackSpeedButton() {
        if (this.butPlaybackSpeed != null) {
            if (this.controller == null) {
                this.butPlaybackSpeed.setVisibility(8);
                return;
            }
            updatePlaybackSpeedButtonText();
            ViewCompat.setAlpha(this.butPlaybackSpeed, this.controller.canSetPlaybackSpeed() ? 1.0f : 0.5f);
            this.butPlaybackSpeed.setVisibility(0);
        }
    }

    protected void updatePlaybackSpeedButtonText() {
        if (this.butPlaybackSpeed != null) {
            if (this.controller == null) {
                this.butPlaybackSpeed.setVisibility(8);
                return;
            }
            float speed = 1.0f;
            if (this.controller.canSetPlaybackSpeed()) {
                try {
                    speed = Float.parseFloat(UserPreferences.getPlaybackSpeed());
                } catch (NumberFormatException e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                    UserPreferences.setPlaybackSpeed(String.valueOf(1.0f));
                }
            }
            this.butPlaybackSpeed.setText(new DecimalFormat("0.00x").format((double) speed));
        }
    }

    protected void setupGUI() {
        if (!this.isSetup.getAndSet(true)) {
            super.setupGUI();
            if (this.butCastDisconnect != null) {
                this.butCastDisconnect.setVisibility(8);
            }
            if (this.butPlaybackSpeed != null) {
                this.butPlaybackSpeed.setOnClickListener(new -$$Lambda$AudioplayerActivity$syyXYTwAQYm7z2YM6phJAL2oB2E());
                this.butPlaybackSpeed.setOnLongClickListener(new -$$Lambda$AudioplayerActivity$AOnmPMy6XmOMBv2lRwkkxi71acI());
                this.butPlaybackSpeed.setVisibility(0);
            }
        }
    }

    public static /* synthetic */ void lambda$setupGUI$0(AudioplayerActivity audioplayerActivity, View v) {
        if (audioplayerActivity.controller != null) {
            if (audioplayerActivity.controller.canSetPlaybackSpeed()) {
                String str;
                String[] availableSpeeds = UserPreferences.getPlaybackSpeedArray();
                String currentSpeed = UserPreferences.getPlaybackSpeed();
                if (availableSpeeds.length > 0) {
                    str = availableSpeeds[0];
                } else {
                    str = "1.00";
                }
                int i = 0;
                while (i < availableSpeeds.length) {
                    if (availableSpeeds[i].equals(currentSpeed)) {
                        if (i == availableSpeeds.length - 1) {
                            str = availableSpeeds[0];
                        } else {
                            str = availableSpeeds[i + 1];
                        }
                        UserPreferences.setPlaybackSpeed(str);
                        audioplayerActivity.controller.setPlaybackSpeed(Float.parseFloat(str));
                    } else {
                        i++;
                    }
                }
                UserPreferences.setPlaybackSpeed(str);
                audioplayerActivity.controller.setPlaybackSpeed(Float.parseFloat(str));
            } else {
                VariableSpeedDialog.showGetPluginDialog(audioplayerActivity);
            }
        }
    }
}
