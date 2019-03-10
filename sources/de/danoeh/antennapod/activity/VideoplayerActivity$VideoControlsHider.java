package de.danoeh.antennapod.activity;

import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.util.Log;
import java.lang.ref.WeakReference;

class VideoplayerActivity$VideoControlsHider extends Handler {
    private static final int DELAY = 2500;
    private WeakReference<VideoplayerActivity> activity;
    private final Runnable hideVideoControls = new C0709x29044b51();

    VideoplayerActivity$VideoControlsHider(VideoplayerActivity activity) {
        this.activity = new WeakReference(activity);
    }

    public static /* synthetic */ void lambda$new$0(VideoplayerActivity$VideoControlsHider videoplayerActivity$VideoControlsHider) {
        WeakReference weakReference = videoplayerActivity$VideoControlsHider.activity;
        VideoplayerActivity vpa = weakReference != null ? (VideoplayerActivity) weakReference.get() : null;
        if (vpa != null) {
            if (VideoplayerActivity.access$300(vpa)) {
                Log.d("VideoplayerActivity", "Hiding video controls");
                ActionBar actionBar = vpa.getSupportActionBar();
                if (actionBar != null) {
                    actionBar.hide();
                }
                VideoplayerActivity.access$400(vpa);
                VideoplayerActivity.access$302(vpa, false);
            }
        }
    }

    public void start() {
        postDelayed(this.hideVideoControls, 2500);
    }

    void stop() {
        removeCallbacks(this.hideVideoControls);
    }
}
