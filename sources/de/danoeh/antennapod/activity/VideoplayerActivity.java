package de.danoeh.antennapod.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder.Callback;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import de.danoeh.antennapod.core.feed.MediaType;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.preferences.UserPreferences$VideoBackgroundBehavior;
import de.danoeh.antennapod.core.service.playback.PlaybackService;
import de.danoeh.antennapod.core.service.playback.PlayerStatus;
import de.danoeh.antennapod.core.util.gui.PictureInPictureUtil;
import de.danoeh.antennapod.core.util.playback.Playable;
import de.danoeh.antennapod.debug.R;
import de.danoeh.antennapod.view.AspectRatioVideoView;
import java.util.concurrent.atomic.AtomicBoolean;

public class VideoplayerActivity extends MediaplayerActivity {
    private static final String TAG = "VideoplayerActivity";
    private LinearLayout controls;
    private boolean destroyingDueToReload = false;
    private final AtomicBoolean isSetup = new AtomicBoolean(false);
    private final OnTouchListener onVideoviewTouched = new -$$Lambda$VideoplayerActivity$Lag48gpqUBbMmo2XTSNZc6bbmwM();
    private ProgressBar progressIndicator;
    private final Callback surfaceHolderCallback = new VideoplayerActivity$1(this);
    private VideoplayerActivity$VideoControlsHider videoControlsHider = new VideoplayerActivity$VideoControlsHider(this);
    private boolean videoControlsShowing = true;
    private LinearLayout videoOverlay;
    private boolean videoSurfaceCreated = false;
    private FrameLayout videoframe;
    private AspectRatioVideoView videoview;

    protected void chooseTheme() {
        setTheme(R.style.Theme.AntennaPod.VideoPlayer);
    }

    @SuppressLint({"AppCompatMethod"})
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(128);
        getWindow().addFlags(256);
        supportRequestWindowFeature(9);
        super.onCreate(savedInstanceState);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Integer.MIN_VALUE));
    }

    protected void onResume() {
        super.onResume();
        if (TextUtils.equals(getIntent().getAction(), "android.intent.action.VIEW")) {
            playExternalMedia(getIntent(), MediaType.VIDEO);
        } else if (PlaybackService.isCasting()) {
            Intent intent = PlaybackService.getPlayerActivityIntent(this);
            if (!intent.getComponent().getClassName().equals(VideoplayerActivity.class.getName())) {
                this.destroyingDueToReload = true;
                finish();
                startActivity(intent);
            }
        }
    }

    protected void onStop() {
        super.onStop();
        if (!PictureInPictureUtil.isInPictureInPictureMode(this)) {
            this.videoControlsHider.stop();
        }
    }

    public void onUserLeaveHint() {
        if (!PictureInPictureUtil.isInPictureInPictureMode(this) && UserPreferences.getVideoBackgroundBehavior() == UserPreferences$VideoBackgroundBehavior.PICTURE_IN_PICTURE) {
            compatEnterPictureInPicture();
        }
    }

    protected void onPause() {
        if (!PictureInPictureUtil.isInPictureInPictureMode(this)) {
            if (this.controller != null && this.controller.getStatus() == PlayerStatus.PLAYING) {
                this.controller.pause();
            }
        }
        super.onPause();
    }

    protected void onDestroy() {
        this.videoControlsHider.stop();
        this.videoControlsHider = null;
        super.onDestroy();
    }

    protected boolean loadMediaInfo() {
        if (super.loadMediaInfo()) {
            if (this.controller != null) {
                Playable media = this.controller.getMedia();
                if (media == null) {
                    return false;
                }
                getSupportActionBar().setSubtitle(media.getEpisodeTitle());
                getSupportActionBar().setTitle(media.getFeedTitle());
                return true;
            }
        }
        return false;
    }

    protected void setupGUI() {
        if (!this.isSetup.getAndSet(true)) {
            super.setupGUI();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            this.controls = (LinearLayout) findViewById(R.id.controls);
            this.videoOverlay = (LinearLayout) findViewById(R.id.overlay);
            this.videoview = (AspectRatioVideoView) findViewById(R.id.videoview);
            this.videoframe = (FrameLayout) findViewById(R.id.videoframe);
            this.progressIndicator = (ProgressBar) findViewById(R.id.progressIndicator);
            this.videoview.getHolder().addCallback(this.surfaceHolderCallback);
            this.videoframe.setOnTouchListener(this.onVideoviewTouched);
            this.videoOverlay.setOnTouchListener(-$$Lambda$VideoplayerActivity$E9MGGFXe53ec6yPIG2J6b0E9ij0.INSTANCE);
            if (VERSION.SDK_INT >= 16) {
                this.videoview.setSystemUiVisibility(512);
            }
            this.videoOverlay.setFitsSystemWindows(true);
            setupVideoControlsToggler();
            getWindow().setFlags(1024, 1024);
            this.videoframe.getViewTreeObserver().addOnGlobalLayoutListener(new -$$Lambda$VideoplayerActivity$fqbpuLBPv2dBHnvxa6vB8ldYNZc());
        }
    }

    protected void onAwaitingVideoSurface() {
        setupVideoAspectRatio();
        if (this.videoSurfaceCreated && this.controller != null) {
            Log.d(TAG, "Videosurface already created, setting videosurface now");
            this.controller.setVideoSurface(this.videoview.getHolder());
        }
    }

    protected void postStatusMsg(int resId, boolean showToast) {
        if (resId == R.string.player_preparing_msg) {
            this.progressIndicator.setVisibility(0);
        } else {
            this.progressIndicator.setVisibility(4);
        }
    }

    protected void clearStatusMsg() {
        this.progressIndicator.setVisibility(4);
    }

    public static /* synthetic */ boolean lambda$new$2(VideoplayerActivity videoplayerActivity, View v, MotionEvent event) {
        if (event.getAction() != 0) {
            return false;
        }
        if (PictureInPictureUtil.isInPictureInPictureMode(videoplayerActivity)) {
            return true;
        }
        videoplayerActivity.videoControlsHider.stop();
        videoplayerActivity.toggleVideoControlsVisibility();
        if (videoplayerActivity.videoControlsShowing) {
            videoplayerActivity.setupVideoControlsToggler();
        }
        return true;
    }

    @SuppressLint({"NewApi"})
    private void setupVideoControlsToggler() {
        this.videoControlsHider.stop();
        this.videoControlsHider.start();
    }

    private void setupVideoAspectRatio() {
        if (this.videoSurfaceCreated && this.controller != null) {
            Pair<Integer, Integer> videoSize = this.controller.getVideoSize();
            if (videoSize == null || ((Integer) videoSize.first).intValue() <= 0 || ((Integer) videoSize.second).intValue() <= 0) {
                Log.e(TAG, "Could not determine video size");
                return;
            }
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Width,height of video: ");
            stringBuilder.append(videoSize.first);
            stringBuilder.append(", ");
            stringBuilder.append(videoSize.second);
            Log.d(str, stringBuilder.toString());
            this.videoview.setVideoSize(((Integer) videoSize.first).intValue(), ((Integer) videoSize.second).intValue());
        }
    }

    private void toggleVideoControlsVisibility() {
        if (this.videoControlsShowing) {
            getSupportActionBar().hide();
            hideVideoControls();
        } else {
            getSupportActionBar().show();
            showVideoControls();
        }
        this.videoControlsShowing ^= 1;
    }

    protected void onRewind() {
        super.onRewind();
        setupVideoControlsToggler();
    }

    protected void onPlayPause() {
        super.onPlayPause();
        setupVideoControlsToggler();
    }

    protected void onFastForward() {
        super.onFastForward();
        setupVideoControlsToggler();
    }

    protected void onReloadNotification(int notificationCode) {
        if (VERSION.SDK_INT < 24 || !PictureInPictureUtil.isInPictureInPictureMode(this)) {
            if (notificationCode == 1) {
                Log.d(TAG, "ReloadNotification received, switching to Audioplayer now");
                this.destroyingDueToReload = true;
                finish();
                startActivity(new Intent(this, AudioplayerActivity.class));
            } else if (notificationCode == 3) {
                Log.d(TAG, "ReloadNotification received, switching to Castplayer now");
                this.destroyingDueToReload = true;
                finish();
                startActivity(new Intent(this, CastplayerActivity.class));
            }
            return;
        }
        if (notificationCode != 1) {
            if (notificationCode != 3) {
            }
        }
        finish();
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
        super.onStartTrackingTouch(seekBar);
        this.videoControlsHider.stop();
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        super.onStopTrackingTouch(seekBar);
        setupVideoControlsToggler();
    }

    protected void onBufferStart() {
        this.progressIndicator.setVisibility(0);
    }

    protected void onBufferEnd() {
        this.progressIndicator.setVisibility(4);
    }

    @SuppressLint({"NewApi"})
    private void showVideoControls() {
        this.videoOverlay.setVisibility(0);
        this.controls.setVisibility(0);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        if (animation != null) {
            this.videoOverlay.startAnimation(animation);
            this.controls.startAnimation(animation);
        }
        this.videoview.setSystemUiVisibility(0);
    }

    @SuppressLint({"NewApi"})
    private void hideVideoControls(boolean showAnimation) {
        if (showAnimation) {
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_out);
            if (animation != null) {
                this.videoOverlay.startAnimation(animation);
                this.controls.startAnimation(animation);
            }
        }
        getWindow().getDecorView().setSystemUiVisibility((VERSION.SDK_INT >= 16 ? 512 : 0) | 7);
        this.videoOverlay.setFitsSystemWindows(true);
        this.videoOverlay.setVisibility(8);
        this.controls.setVisibility(8);
    }

    private void hideVideoControls() {
        hideVideoControls(true);
    }

    protected int getContentViewResourceId() {
        return R.layout.videoplayer_activity;
    }

    protected void setScreenOn(boolean enable) {
        super.setScreenOn(enable);
        if (enable) {
            getWindow().addFlags(128);
        } else {
            getWindow().clearFlags(128);
        }
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (PictureInPictureUtil.supportsPictureInPicture(this)) {
            menu.findItem(R.id.player_go_to_picture_in_picture).setVisible(true);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != R.id.player_go_to_picture_in_picture) {
            return super.onOptionsItemSelected(item);
        }
        compatEnterPictureInPicture();
        return true;
    }

    private void compatEnterPictureInPicture() {
        if (PictureInPictureUtil.supportsPictureInPicture(this) && VERSION.SDK_INT >= 24) {
            getSupportActionBar().hide();
            hideVideoControls(false);
            enterPictureInPictureMode();
        }
    }
}
