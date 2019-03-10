package de.danoeh.antennapod.activity;

import android.app.Activity;
import android.widget.ImageButton;
import de.danoeh.antennapod.core.util.playback.PlaybackController;

class MediaplayerActivity$1 extends PlaybackController {
    final /* synthetic */ MediaplayerActivity this$0;

    MediaplayerActivity$1(MediaplayerActivity this$0, Activity arg0, boolean arg1) {
        this.this$0 = this$0;
        super(arg0, arg1);
    }

    public void setupGUI() {
        this.this$0.setupGUI();
    }

    public void onPositionObserverUpdate() {
        this.this$0.onPositionObserverUpdate();
    }

    public void onBufferStart() {
        this.this$0.onBufferStart();
    }

    public void onBufferEnd() {
        this.this$0.onBufferEnd();
    }

    public void onBufferUpdate(float progress) {
        MediaplayerActivity.access$000(this.this$0, progress);
    }

    public void handleError(int code) {
        MediaplayerActivity.access$100(this.this$0, code);
    }

    public void onReloadNotification(int code) {
        this.this$0.onReloadNotification(code);
    }

    public void onSleepTimerUpdate() {
        this.this$0.supportInvalidateOptionsMenu();
    }

    public ImageButton getPlayButton() {
        return MediaplayerActivity.access$200(this.this$0);
    }

    public void postStatusMsg(int msg, boolean showToast) {
        this.this$0.postStatusMsg(msg, showToast);
    }

    public void clearStatusMsg() {
        this.this$0.clearStatusMsg();
    }

    public boolean loadMediaInfo() {
        return this.this$0.loadMediaInfo();
    }

    public void onAwaitingVideoSurface() {
        this.this$0.onAwaitingVideoSurface();
    }

    public void onServiceQueried() {
        MediaplayerActivity.access$300(this.this$0);
    }

    public void onShutdownNotification() {
        this.this$0.finish();
    }

    public void onPlaybackEnd() {
        this.this$0.finish();
    }

    public void onPlaybackSpeedChange() {
        MediaplayerActivity.access$400(this.this$0);
    }

    protected void setScreenOn(boolean enable) {
        super.setScreenOn(enable);
        this.this$0.setScreenOn(enable);
    }

    public void onSetSpeedAbilityChanged() {
        MediaplayerActivity.access$500(this.this$0);
    }
}
