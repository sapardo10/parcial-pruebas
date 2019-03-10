package de.danoeh.antennapod.activity;

import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.preferences.UserPreferences$VideoBackgroundBehavior;
import de.danoeh.antennapod.core.service.playback.PlayerStatus;

class VideoplayerActivity$1 implements Callback {
    final /* synthetic */ VideoplayerActivity this$0;

    VideoplayerActivity$1(VideoplayerActivity this$0) {
        this.this$0 = this$0;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        holder.setFixedSize(width, height);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("VideoplayerActivity", "Videoview holder created");
        VideoplayerActivity.access$002(this.this$0, true);
        if (this.this$0.controller != null && this.this$0.controller.getStatus() == PlayerStatus.PLAYING) {
            if (this.this$0.controller.serviceAvailable()) {
                this.this$0.controller.setVideoSurface(holder);
            } else {
                Log.e("VideoplayerActivity", "Couldn't attach surface to mediaplayer - reference to service was null");
            }
        }
        VideoplayerActivity.access$100(this.this$0);
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d("VideoplayerActivity", "Videosurface was destroyed");
        VideoplayerActivity.access$002(this.this$0, false);
        if (this.this$0.controller != null && !VideoplayerActivity.access$200(this.this$0)) {
            if (UserPreferences.getVideoBackgroundBehavior() != UserPreferences$VideoBackgroundBehavior.CONTINUE_PLAYING) {
                this.this$0.controller.notifyVideoSurfaceAbandoned();
            }
        }
    }
}
