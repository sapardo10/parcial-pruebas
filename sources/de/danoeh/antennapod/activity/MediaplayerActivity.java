package de.danoeh.antennapod.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.Builder;
import com.bumptech.glide.Glide;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import de.danoeh.antennapod.core.event.ServiceEvent;
import de.danoeh.antennapod.core.event.ServiceEvent.Action;
import de.danoeh.antennapod.core.feed.FeedItem;
import de.danoeh.antennapod.core.feed.FeedMedia;
import de.danoeh.antennapod.core.feed.MediaType;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.storage.DBTasks;
import de.danoeh.antennapod.core.storage.DBWriter;
import de.danoeh.antennapod.core.util.Converter;
import de.danoeh.antennapod.core.util.FeedItemUtil;
import de.danoeh.antennapod.core.util.Flavors;
import de.danoeh.antennapod.core.util.ShareUtils;
import de.danoeh.antennapod.core.util.StorageUtils;
import de.danoeh.antennapod.core.util.gui.PictureInPictureUtil;
import de.danoeh.antennapod.core.util.playback.ExternalMedia;
import de.danoeh.antennapod.core.util.playback.MediaPlayerError;
import de.danoeh.antennapod.core.util.playback.Playable;
import de.danoeh.antennapod.core.util.playback.PlaybackController;
import de.danoeh.antennapod.core.util.playback.PlaybackServiceStarter;
import de.danoeh.antennapod.debug.R;
import de.danoeh.antennapod.dialog.VariableSpeedDialog;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.apache.commons.lang3.StringUtils;

public abstract class MediaplayerActivity extends CastEnabledActivity implements OnSeekBarChangeListener {
    private static final float DEFAULT_MAX_PLAYBACK_SPEED = 2.5f;
    private static final float DEFAULT_MIN_PLAYBACK_SPEED = 0.5f;
    private static final float PLAYBACK_SPEED_STEP = 0.05f;
    private static final String PREFS = "MediaPlayerActivityPreferences";
    private static final String PREF_SHOW_TIME_LEFT = "showTimeLeft";
    private static final int REQUEST_CODE_STORAGE = 42;
    private static final String TAG = "MediaplayerActivity";
    private ImageButton butFF;
    private ImageButton butPlay;
    private ImageButton butRev;
    private ImageButton butSkip;
    PlaybackController controller;
    private Disposable disposable;
    private boolean isFavorite = false;
    private float prog;
    SeekBar sbPosition;
    private boolean showTimeLeft = false;
    private TextView txtvFF;
    private TextView txtvLength;
    private TextView txtvPosition;
    private TextView txtvRev;

    protected abstract void clearStatusMsg();

    protected abstract int getContentViewResourceId();

    protected abstract void onAwaitingVideoSurface();

    protected abstract void onBufferEnd();

    protected abstract void onBufferStart();

    protected abstract void onReloadNotification(int i);

    protected abstract void postStatusMsg(int i, boolean z);

    private PlaybackController newPlaybackController() {
        return new MediaplayerActivity$1(this, this, false);
    }

    private static TextView getTxtvFFFromActivity(MediaplayerActivity activity) {
        return activity.txtvFF;
    }

    private static TextView getTxtvRevFromActivity(MediaplayerActivity activity) {
        return activity.txtvRev;
    }

    private void onSetSpeedAbilityChanged() {
        Log.d(TAG, "onSetSpeedAbilityChanged()");
        updatePlaybackSpeedButton();
    }

    private void onPlaybackSpeedChange() {
        updatePlaybackSpeedButtonText();
    }

    private void onServiceQueried() {
        supportInvalidateOptionsMenu();
    }

    void chooseTheme() {
        setTheme(UserPreferences.getTheme());
    }

    void setScreenOn(boolean enable) {
    }

    protected void onCreate(Bundle savedInstanceState) {
        chooseTheme();
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        StorageUtils.checkStorageAvailability(this);
        getWindow().setFormat(-2);
        setupGUI();
        loadMediaInfo();
    }

    protected void onPause() {
        if (!PictureInPictureUtil.isInPictureInPictureMode(this)) {
            PlaybackController playbackController = this.controller;
            if (playbackController != null) {
                playbackController.reinitServiceIfPaused();
                this.controller.pause();
            }
        }
        super.onPause();
    }

    private void onBufferUpdate(float progress) {
        SeekBar seekBar = this.sbPosition;
        if (seekBar != null) {
            seekBar.setSecondaryProgress((int) (((float) seekBar.getMax()) * progress));
        }
    }

    protected void onStart() {
        super.onStart();
        PlaybackController playbackController = this.controller;
        if (playbackController != null) {
            playbackController.release();
        }
        this.controller = newPlaybackController();
        onPositionObserverUpdate();
    }

    protected void onStop() {
        Log.d(TAG, "onStop()");
        PlaybackController playbackController = this.controller;
        if (playbackController != null) {
            playbackController.release();
            this.controller = null;
        }
        Disposable disposable = this.disposable;
        if (disposable != null) {
            disposable.dispose();
        }
        super.onStop();
    }

    @TargetApi(14)
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Glide.get(this).trimMemory(level);
    }

    public void onLowMemory() {
        super.onLowMemory();
        Glide.get(this).clearMemory();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (Flavors.FLAVOR == Flavors.PLAY) {
            requestCastButton(2);
        }
        getMenuInflater().inflate(R.menu.mediaplayer, menu);
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        Playable media = this.controller;
        if (media == null) {
            return false;
        }
        boolean z;
        boolean hasWebsiteLink;
        boolean isItemHasDownloadLink;
        MenuItem findItem;
        boolean z2;
        boolean sleepTimerSet;
        TypedArray ta;
        int textColor;
        media = media.getMedia();
        boolean isFeedMedia = media != null && (media instanceof FeedMedia);
        MenuItem findItem2 = menu.findItem(R.id.support_item);
        if (isFeedMedia && media.getPaymentLink() != null) {
            if (((FeedMedia) media).getItem() != null) {
                if (((FeedMedia) media).getItem().getFlattrStatus().flattrable()) {
                    z = true;
                    findItem2.setVisible(z);
                    hasWebsiteLink = getWebsiteLinkWithFallback(media) == null;
                    menu.findItem(R.id.visit_website_item).setVisible(hasWebsiteLink);
                    if (isFeedMedia) {
                        if (ShareUtils.hasLinkToShare(((FeedMedia) media).getItem())) {
                            z = true;
                            menu.findItem(R.id.share_link_item).setVisible(z);
                            menu.findItem(R.id.share_link_with_position_item).setVisible(z);
                            isItemHasDownloadLink = isFeedMedia && ((FeedMedia) media).getDownload_url() != null;
                            menu.findItem(R.id.share_download_url_item).setVisible(isItemHasDownloadLink);
                            menu.findItem(R.id.share_download_url_with_position_item).setVisible(isItemHasDownloadLink);
                            findItem = menu.findItem(R.id.share_file);
                            z2 = isFeedMedia && ((FeedMedia) media).fileExists();
                            findItem.setVisible(z2);
                            findItem = menu.findItem(R.id.share_item);
                            if (!(hasWebsiteLink || z)) {
                                if (isItemHasDownloadLink) {
                                    z2 = false;
                                    findItem.setVisible(z2);
                                    menu.findItem(R.id.add_to_favorites_item).setVisible(false);
                                    menu.findItem(R.id.remove_from_favorites_item).setVisible(false);
                                    if (!isFeedMedia) {
                                        menu.findItem(R.id.add_to_favorites_item).setVisible(this.isFavorite ^ true);
                                        menu.findItem(R.id.remove_from_favorites_item).setVisible(this.isFavorite);
                                    }
                                    sleepTimerSet = this.controller.sleepTimerActive();
                                    menu.findItem(R.id.set_sleeptimer_item).setVisible(this.controller.sleepTimerNotActive());
                                    menu.findItem(R.id.disable_sleeptimer_item).setVisible(sleepTimerSet);
                                    if (this instanceof AudioplayerActivity) {
                                        menu.findItem(R.id.audio_controls).setVisible(false);
                                    } else {
                                        ta = obtainStyledAttributes(UserPreferences.getTheme(), new int[]{R.attr.action_bar_icon_color});
                                        textColor = ta.getColor(0, -7829368);
                                        ta.recycle();
                                        menu.findItem(R.id.audio_controls).setIcon(new IconDrawable(this, FontAwesomeIcons.fa_sliders).color(textColor).actionBarSize());
                                    }
                                    return true;
                                }
                            }
                            z2 = true;
                            findItem.setVisible(z2);
                            menu.findItem(R.id.add_to_favorites_item).setVisible(false);
                            menu.findItem(R.id.remove_from_favorites_item).setVisible(false);
                            if (!isFeedMedia) {
                                menu.findItem(R.id.add_to_favorites_item).setVisible(this.isFavorite ^ true);
                                menu.findItem(R.id.remove_from_favorites_item).setVisible(this.isFavorite);
                            }
                            sleepTimerSet = this.controller.sleepTimerActive();
                            menu.findItem(R.id.set_sleeptimer_item).setVisible(this.controller.sleepTimerNotActive());
                            menu.findItem(R.id.disable_sleeptimer_item).setVisible(sleepTimerSet);
                            if (this instanceof AudioplayerActivity) {
                                menu.findItem(R.id.audio_controls).setVisible(false);
                            } else {
                                ta = obtainStyledAttributes(UserPreferences.getTheme(), new int[]{R.attr.action_bar_icon_color});
                                textColor = ta.getColor(0, -7829368);
                                ta.recycle();
                                menu.findItem(R.id.audio_controls).setIcon(new IconDrawable(this, FontAwesomeIcons.fa_sliders).color(textColor).actionBarSize());
                            }
                            return true;
                        }
                    }
                    z = false;
                    menu.findItem(R.id.share_link_item).setVisible(z);
                    menu.findItem(R.id.share_link_with_position_item).setVisible(z);
                    if (!isFeedMedia) {
                    }
                    menu.findItem(R.id.share_download_url_item).setVisible(isItemHasDownloadLink);
                    menu.findItem(R.id.share_download_url_with_position_item).setVisible(isItemHasDownloadLink);
                    findItem = menu.findItem(R.id.share_file);
                    if (!isFeedMedia) {
                    }
                    findItem.setVisible(z2);
                    findItem = menu.findItem(R.id.share_item);
                    if (isItemHasDownloadLink) {
                        z2 = false;
                        findItem.setVisible(z2);
                        menu.findItem(R.id.add_to_favorites_item).setVisible(false);
                        menu.findItem(R.id.remove_from_favorites_item).setVisible(false);
                        if (!isFeedMedia) {
                            menu.findItem(R.id.add_to_favorites_item).setVisible(this.isFavorite ^ true);
                            menu.findItem(R.id.remove_from_favorites_item).setVisible(this.isFavorite);
                        }
                        sleepTimerSet = this.controller.sleepTimerActive();
                        menu.findItem(R.id.set_sleeptimer_item).setVisible(this.controller.sleepTimerNotActive());
                        menu.findItem(R.id.disable_sleeptimer_item).setVisible(sleepTimerSet);
                        if (this instanceof AudioplayerActivity) {
                            ta = obtainStyledAttributes(UserPreferences.getTheme(), new int[]{R.attr.action_bar_icon_color});
                            textColor = ta.getColor(0, -7829368);
                            ta.recycle();
                            menu.findItem(R.id.audio_controls).setIcon(new IconDrawable(this, FontAwesomeIcons.fa_sliders).color(textColor).actionBarSize());
                        } else {
                            menu.findItem(R.id.audio_controls).setVisible(false);
                        }
                        return true;
                    }
                    z2 = true;
                    findItem.setVisible(z2);
                    menu.findItem(R.id.add_to_favorites_item).setVisible(false);
                    menu.findItem(R.id.remove_from_favorites_item).setVisible(false);
                    if (!isFeedMedia) {
                        menu.findItem(R.id.add_to_favorites_item).setVisible(this.isFavorite ^ true);
                        menu.findItem(R.id.remove_from_favorites_item).setVisible(this.isFavorite);
                    }
                    sleepTimerSet = this.controller.sleepTimerActive();
                    menu.findItem(R.id.set_sleeptimer_item).setVisible(this.controller.sleepTimerNotActive());
                    menu.findItem(R.id.disable_sleeptimer_item).setVisible(sleepTimerSet);
                    if (this instanceof AudioplayerActivity) {
                        menu.findItem(R.id.audio_controls).setVisible(false);
                    } else {
                        ta = obtainStyledAttributes(UserPreferences.getTheme(), new int[]{R.attr.action_bar_icon_color});
                        textColor = ta.getColor(0, -7829368);
                        ta.recycle();
                        menu.findItem(R.id.audio_controls).setIcon(new IconDrawable(this, FontAwesomeIcons.fa_sliders).color(textColor).actionBarSize());
                    }
                    return true;
                }
            }
        }
        z = false;
        findItem2.setVisible(z);
        if (getWebsiteLinkWithFallback(media) == null) {
        }
        menu.findItem(R.id.visit_website_item).setVisible(hasWebsiteLink);
        if (isFeedMedia) {
            if (ShareUtils.hasLinkToShare(((FeedMedia) media).getItem())) {
                z = true;
                menu.findItem(R.id.share_link_item).setVisible(z);
                menu.findItem(R.id.share_link_with_position_item).setVisible(z);
                if (isFeedMedia) {
                }
                menu.findItem(R.id.share_download_url_item).setVisible(isItemHasDownloadLink);
                menu.findItem(R.id.share_download_url_with_position_item).setVisible(isItemHasDownloadLink);
                findItem = menu.findItem(R.id.share_file);
                if (isFeedMedia) {
                }
                findItem.setVisible(z2);
                findItem = menu.findItem(R.id.share_item);
                if (isItemHasDownloadLink) {
                    z2 = true;
                    findItem.setVisible(z2);
                    menu.findItem(R.id.add_to_favorites_item).setVisible(false);
                    menu.findItem(R.id.remove_from_favorites_item).setVisible(false);
                    if (!isFeedMedia) {
                        menu.findItem(R.id.add_to_favorites_item).setVisible(this.isFavorite ^ true);
                        menu.findItem(R.id.remove_from_favorites_item).setVisible(this.isFavorite);
                    }
                    sleepTimerSet = this.controller.sleepTimerActive();
                    menu.findItem(R.id.set_sleeptimer_item).setVisible(this.controller.sleepTimerNotActive());
                    menu.findItem(R.id.disable_sleeptimer_item).setVisible(sleepTimerSet);
                    if (this instanceof AudioplayerActivity) {
                        ta = obtainStyledAttributes(UserPreferences.getTheme(), new int[]{R.attr.action_bar_icon_color});
                        textColor = ta.getColor(0, -7829368);
                        ta.recycle();
                        menu.findItem(R.id.audio_controls).setIcon(new IconDrawable(this, FontAwesomeIcons.fa_sliders).color(textColor).actionBarSize());
                    } else {
                        menu.findItem(R.id.audio_controls).setVisible(false);
                    }
                    return true;
                }
                z2 = false;
                findItem.setVisible(z2);
                menu.findItem(R.id.add_to_favorites_item).setVisible(false);
                menu.findItem(R.id.remove_from_favorites_item).setVisible(false);
                if (!isFeedMedia) {
                    menu.findItem(R.id.add_to_favorites_item).setVisible(this.isFavorite ^ true);
                    menu.findItem(R.id.remove_from_favorites_item).setVisible(this.isFavorite);
                }
                sleepTimerSet = this.controller.sleepTimerActive();
                menu.findItem(R.id.set_sleeptimer_item).setVisible(this.controller.sleepTimerNotActive());
                menu.findItem(R.id.disable_sleeptimer_item).setVisible(sleepTimerSet);
                if (this instanceof AudioplayerActivity) {
                    menu.findItem(R.id.audio_controls).setVisible(false);
                } else {
                    ta = obtainStyledAttributes(UserPreferences.getTheme(), new int[]{R.attr.action_bar_icon_color});
                    textColor = ta.getColor(0, -7829368);
                    ta.recycle();
                    menu.findItem(R.id.audio_controls).setIcon(new IconDrawable(this, FontAwesomeIcons.fa_sliders).color(textColor).actionBarSize());
                }
                return true;
            }
        }
        z = false;
        menu.findItem(R.id.share_link_item).setVisible(z);
        menu.findItem(R.id.share_link_with_position_item).setVisible(z);
        if (isFeedMedia) {
        }
        menu.findItem(R.id.share_download_url_item).setVisible(isItemHasDownloadLink);
        menu.findItem(R.id.share_download_url_with_position_item).setVisible(isItemHasDownloadLink);
        findItem = menu.findItem(R.id.share_file);
        if (isFeedMedia) {
        }
        findItem.setVisible(z2);
        findItem = menu.findItem(R.id.share_item);
        if (isItemHasDownloadLink) {
            z2 = false;
            findItem.setVisible(z2);
            menu.findItem(R.id.add_to_favorites_item).setVisible(false);
            menu.findItem(R.id.remove_from_favorites_item).setVisible(false);
            if (!isFeedMedia) {
                menu.findItem(R.id.add_to_favorites_item).setVisible(this.isFavorite ^ true);
                menu.findItem(R.id.remove_from_favorites_item).setVisible(this.isFavorite);
            }
            sleepTimerSet = this.controller.sleepTimerActive();
            menu.findItem(R.id.set_sleeptimer_item).setVisible(this.controller.sleepTimerNotActive());
            menu.findItem(R.id.disable_sleeptimer_item).setVisible(sleepTimerSet);
            if (this instanceof AudioplayerActivity) {
                ta = obtainStyledAttributes(UserPreferences.getTheme(), new int[]{R.attr.action_bar_icon_color});
                textColor = ta.getColor(0, -7829368);
                ta.recycle();
                menu.findItem(R.id.audio_controls).setIcon(new IconDrawable(this, FontAwesomeIcons.fa_sliders).color(textColor).actionBarSize());
            } else {
                menu.findItem(R.id.audio_controls).setVisible(false);
            }
            return true;
        }
        z2 = true;
        findItem.setVisible(z2);
        menu.findItem(R.id.add_to_favorites_item).setVisible(false);
        menu.findItem(R.id.remove_from_favorites_item).setVisible(false);
        if (!isFeedMedia) {
            menu.findItem(R.id.add_to_favorites_item).setVisible(this.isFavorite ^ true);
            menu.findItem(R.id.remove_from_favorites_item).setVisible(this.isFavorite);
        }
        sleepTimerSet = this.controller.sleepTimerActive();
        menu.findItem(R.id.set_sleeptimer_item).setVisible(this.controller.sleepTimerNotActive());
        menu.findItem(R.id.disable_sleeptimer_item).setVisible(sleepTimerSet);
        if (this instanceof AudioplayerActivity) {
            menu.findItem(R.id.audio_controls).setVisible(false);
        } else {
            ta = obtainStyledAttributes(UserPreferences.getTheme(), new int[]{R.attr.action_bar_icon_color});
            textColor = ta.getColor(0, -7829368);
            ta.recycle();
            menu.findItem(R.id.audio_controls).setIcon(new IconDrawable(this, FontAwesomeIcons.fa_sliders).color(textColor).actionBarSize());
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        PlaybackController playbackController = this.controller;
        if (playbackController == null) {
            return false;
        }
        Playable media = playbackController.getMedia();
        if (item.getItemId() == 16908332) {
            Intent intent = new Intent(r1, MainActivity.class);
            intent.addFlags(335544320);
            View cover = findViewById(R.id.imgvCover);
            if (cover == null || VERSION.SDK_INT < 16) {
                startActivity(intent);
            } else {
                startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(r1, cover, "coverTransition").toBundle());
            }
            finish();
            return true;
        } else if (media == null) {
            return false;
        } else {
            FeedItem feedItem;
            switch (item.getItemId()) {
                case R.id.add_to_favorites_item:
                    if (!(media instanceof FeedMedia)) {
                        break;
                    }
                    feedItem = ((FeedMedia) media).getItem();
                    if (feedItem != null) {
                        DBWriter.addFavoriteItem(feedItem);
                        r1.isFavorite = true;
                        invalidateOptionsMenu();
                        Toast.makeText(r1, R.string.added_to_favorites, 0).show();
                    }
                    break;
                case R.id.audio_controls:
                    String sonicOnly;
                    MaterialDialog dialog = new Builder(r1).title(R.string.audio_controls).customView(R.layout.audio_controls, true).neutralText(R.string.close_label).onNeutral(-$$Lambda$MediaplayerActivity$zA9bThKGpXmpLOwUCee1KHEL-DI.INSTANCE).show();
                    SeekBar barPlaybackSpeed = (SeekBar) dialog.findViewById(R.id.playback_speed);
                    ((Button) dialog.findViewById(R.id.butDecSpeed)).setOnClickListener(new -$$Lambda$MediaplayerActivity$KOnMUXoGP3_aAn2BMJZG9Ihoe_U(r1, barPlaybackSpeed));
                    ((Button) dialog.findViewById(R.id.butIncSpeed)).setOnClickListener(new -$$Lambda$MediaplayerActivity$bgA_kHKEQ9rQcgYzZNbHaerj6K4(r1, barPlaybackSpeed));
                    TextView txtvPlaybackSpeed = (TextView) dialog.findViewById(R.id.txtvPlaybackSpeed);
                    float currentSpeed = 1.0f;
                    try {
                        currentSpeed = Float.parseFloat(UserPreferences.getPlaybackSpeed());
                    } catch (NumberFormatException e) {
                        Log.e(TAG, Log.getStackTraceString(e));
                        UserPreferences.setPlaybackSpeed(String.valueOf(1.0f));
                    }
                    String[] availableSpeeds = UserPreferences.getPlaybackSpeedArray();
                    float minPlaybackSpeed = availableSpeeds.length > 1 ? Float.valueOf(availableSpeeds[0]).floatValue() : DEFAULT_MIN_PLAYBACK_SPEED;
                    barPlaybackSpeed.setMax((int) (((availableSpeeds.length > 1 ? Float.valueOf(availableSpeeds[availableSpeeds.length - 1]).floatValue() : DEFAULT_MAX_PLAYBACK_SPEED) - minPlaybackSpeed) / 1028443341));
                    txtvPlaybackSpeed.setText(String.format("%.2fx", new Object[]{Float.valueOf(currentSpeed)}));
                    barPlaybackSpeed.setOnSeekBarChangeListener(new MediaplayerActivity$3(r1, minPlaybackSpeed, txtvPlaybackSpeed, barPlaybackSpeed));
                    barPlaybackSpeed.setProgress((int) ((currentSpeed - minPlaybackSpeed) / PLAYBACK_SPEED_STEP));
                    SeekBar barLeftVolume = (SeekBar) dialog.findViewById(R.id.volume_left);
                    barLeftVolume.setProgress(UserPreferences.getLeftVolumePercentage());
                    SeekBar barRightVolume = (SeekBar) dialog.findViewById(R.id.volume_right);
                    barRightVolume.setProgress(UserPreferences.getRightVolumePercentage());
                    CheckBox stereoToMono = (CheckBox) dialog.findViewById(R.id.stereo_to_mono);
                    stereoToMono.setChecked(UserPreferences.stereoToMono());
                    PlaybackController playbackController2 = r1.controller;
                    if (playbackController2 == null || playbackController2.canDownmix()) {
                        SeekBar seekBar = barPlaybackSpeed;
                    } else {
                        stereoToMono.setEnabled(false);
                        sonicOnly = getString(R.string.sonic_only);
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(stereoToMono.getText());
                        stringBuilder.append(" [");
                        stringBuilder.append(sonicOnly);
                        stringBuilder.append("]");
                        stereoToMono.setText(stringBuilder.toString());
                    }
                    if (UserPreferences.useExoplayer()) {
                        barRightVolume.setEnabled(false);
                    }
                    CheckBox skipSilence = (CheckBox) dialog.findViewById(R.id.skipSilence);
                    skipSilence.setChecked(UserPreferences.isSkipSilence());
                    if (UserPreferences.useExoplayer()) {
                    } else {
                        skipSilence.setEnabled(false);
                        sonicOnly = getString(R.string.exoplayer_only);
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append(skipSilence.getText());
                        stringBuilder2.append(" [");
                        stringBuilder2.append(sonicOnly);
                        stringBuilder2.append("]");
                        skipSilence.setText(stringBuilder2.toString());
                    }
                    skipSilence.setOnCheckedChangeListener(new -$$Lambda$MediaplayerActivity$gaYybKj63Ck5g2WGrtrDUyEFgdk(r1));
                    barLeftVolume.setOnSeekBarChangeListener(new MediaplayerActivity$4(r1, barRightVolume));
                    barRightVolume.setOnSeekBarChangeListener(new MediaplayerActivity$5(r1, barLeftVolume));
                    stereoToMono.setOnCheckedChangeListener(new -$$Lambda$MediaplayerActivity$oszDuV6AxbRI6ikgn6Btp7PDkMg(r1));
                    break;
                case R.id.disable_sleeptimer_item:
                    if (!r1.controller.serviceAvailable()) {
                        break;
                    }
                    Builder stDialog = new Builder(r1);
                    stDialog.title(R.string.sleep_timer_label);
                    StringBuilder stringBuilder3 = new StringBuilder();
                    stringBuilder3.append(getString(R.string.time_left_label));
                    stringBuilder3.append(Converter.getDurationStringLong((int) r1.controller.getSleepTimerTimeLeft()));
                    stDialog.content(stringBuilder3.toString());
                    stDialog.positiveText(R.string.disable_sleeptimer_label);
                    stDialog.negativeText(R.string.cancel_label);
                    stDialog.onPositive(new -$$Lambda$MediaplayerActivity$4uaMuPZnbs6OX7VV3LXFAGHdGnQ(r1));
                    stDialog.onNegative(-$$Lambda$MediaplayerActivity$bIVVF1kpyyTTY6bVwU2LSENj1ng.INSTANCE);
                    stDialog.build().show();
                    break;
                case R.id.remove_from_favorites_item:
                    if (!(media instanceof FeedMedia)) {
                        break;
                    }
                    feedItem = ((FeedMedia) media).getItem();
                    if (feedItem != null) {
                        DBWriter.removeFavoriteItem(feedItem);
                        r1.isFavorite = false;
                        invalidateOptionsMenu();
                        Toast.makeText(r1, R.string.removed_from_favorites, 0).show();
                    }
                    break;
                case R.id.set_sleeptimer_item:
                    if (!r1.controller.serviceAvailable()) {
                        break;
                    }
                    new MediaplayerActivity$2(r1, r1).createNewDialog().show();
                    break;
                case R.id.share_download_url_item:
                    if (!(media instanceof FeedMedia)) {
                        break;
                    }
                    ShareUtils.shareFeedItemDownloadLink(r1, ((FeedMedia) media).getItem());
                    break;
                case R.id.share_download_url_with_position_item:
                    if (!(media instanceof FeedMedia)) {
                        break;
                    }
                    ShareUtils.shareFeedItemDownloadLink(r1, ((FeedMedia) media).getItem(), true);
                    break;
                case R.id.share_file:
                    if (!(media instanceof FeedMedia)) {
                        break;
                    }
                    ShareUtils.shareFeedItemFile(r1, (FeedMedia) media);
                    break;
                case R.id.share_link_item:
                    if (!(media instanceof FeedMedia)) {
                        break;
                    }
                    ShareUtils.shareFeedItemLink(r1, ((FeedMedia) media).getItem());
                    break;
                case R.id.share_link_with_position_item:
                    if (!(media instanceof FeedMedia)) {
                        break;
                    }
                    ShareUtils.shareFeedItemLink(r1, ((FeedMedia) media).getItem(), true);
                    break;
                case R.id.support_item:
                    if (!(media instanceof FeedMedia)) {
                        break;
                    }
                    DBTasks.flattrItemIfLoggedIn(r1, ((FeedMedia) media).getItem());
                    break;
                case R.id.visit_website_item:
                    startActivity(new Intent("android.intent.action.VIEW", Uri.parse(getWebsiteLinkWithFallback(media))));
                    break;
                default:
                    return false;
            }
            return true;
        }
    }

    public static /* synthetic */ void lambda$onOptionsItemSelected$0(MediaplayerActivity mediaplayerActivity, MaterialDialog dialog, DialogAction which) {
        dialog.dismiss();
        mediaplayerActivity.controller.disableSleepTimer();
    }

    public static /* synthetic */ void lambda$onOptionsItemSelected$3(MediaplayerActivity mediaplayerActivity, SeekBar barPlaybackSpeed, View v) {
        PlaybackController playbackController = mediaplayerActivity.controller;
        if (playbackController == null || !playbackController.canSetPlaybackSpeed()) {
            VariableSpeedDialog.showGetPluginDialog(mediaplayerActivity);
        } else {
            barPlaybackSpeed.setProgress(barPlaybackSpeed.getProgress() - 1);
        }
    }

    public static /* synthetic */ void lambda$onOptionsItemSelected$4(MediaplayerActivity mediaplayerActivity, SeekBar barPlaybackSpeed, View v) {
        PlaybackController playbackController = mediaplayerActivity.controller;
        if (playbackController == null || !playbackController.canSetPlaybackSpeed()) {
            VariableSpeedDialog.showGetPluginDialog(mediaplayerActivity);
        } else {
            barPlaybackSpeed.setProgress(barPlaybackSpeed.getProgress() + 1);
        }
    }

    public static /* synthetic */ void lambda$onOptionsItemSelected$5(MediaplayerActivity mediaplayerActivity, CompoundButton buttonView, boolean isChecked) {
        UserPreferences.setSkipSilence(isChecked);
        mediaplayerActivity.controller.setSkipSilence(isChecked);
    }

    public static /* synthetic */ void lambda$onOptionsItemSelected$6(MediaplayerActivity mediaplayerActivity, CompoundButton buttonView, boolean isChecked) {
        UserPreferences.stereoToMono(isChecked);
        PlaybackController playbackController = mediaplayerActivity.controller;
        if (playbackController != null) {
            playbackController.setDownmix(isChecked);
        }
    }

    private static String getWebsiteLinkWithFallback(Playable media) {
        if (media == null) {
            return null;
        }
        if (media.getWebsiteLink() != null) {
            return media.getWebsiteLink();
        }
        if (media instanceof FeedMedia) {
            return FeedItemUtil.getLinkWithFallback(((FeedMedia) media).getItem());
        }
        return null;
    }

    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        StorageUtils.checkStorageAvailability(this);
        PlaybackController playbackController = this.controller;
        if (playbackController != null) {
            playbackController.init();
        }
    }

    public void onEventMainThread(ServiceEvent event) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("onEvent(");
        stringBuilder.append(event);
        stringBuilder.append(")");
        Log.d(str, stringBuilder.toString());
        if (event.action == Action.SERVICE_STARTED) {
            PlaybackController playbackController = this.controller;
            if (playbackController != null) {
                playbackController.init();
            }
        }
    }

    void onPositionObserverUpdate() {
        int currentPosition = this.controller;
        if (!(currentPosition == 0 || this.txtvPosition == null)) {
            if (this.txtvLength != null) {
                currentPosition = currentPosition.getPosition();
                int duration = this.controller.getDuration();
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("currentPosition ");
                stringBuilder.append(Converter.getDurationStringLong(currentPosition));
                Log.d(str, stringBuilder.toString());
                if (currentPosition != -1) {
                    if (duration != -1) {
                        this.txtvPosition.setText(Converter.getDurationStringLong(currentPosition));
                        if (this.showTimeLeft) {
                            TextView textView = this.txtvLength;
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("-");
                            stringBuilder.append(Converter.getDurationStringLong(duration - currentPosition));
                            textView.setText(stringBuilder.toString());
                        } else {
                            this.txtvLength.setText(Converter.getDurationStringLong(duration));
                        }
                        updateProgressbarPosition(currentPosition, duration);
                        return;
                    }
                }
                Log.w(TAG, "Could not react to position observer update because of invalid time");
            }
        }
    }

    private void updateProgressbarPosition(int position, int duration) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("updateProgressbarPosition(");
        stringBuilder.append(position);
        stringBuilder.append(", ");
        stringBuilder.append(duration);
        stringBuilder.append(")");
        Log.d(str, stringBuilder.toString());
        SeekBar seekBar = this.sbPosition;
        if (seekBar != null) {
            seekBar.setProgress((int) (((float) seekBar.getMax()) * (((float) position) / ((float) duration))));
        }
    }

    boolean loadMediaInfo() {
        Log.d(TAG, "loadMediaInfo()");
        PlaybackController playbackController = this.controller;
        if (playbackController != null) {
            if (playbackController.getMedia() != null) {
                this.showTimeLeft = getSharedPreferences(PREFS, 0).getBoolean(PREF_SHOW_TIME_LEFT, false);
                onPositionObserverUpdate();
                checkFavorite();
                updatePlaybackSpeedButton();
                return true;
            }
        }
        return false;
    }

    void updatePlaybackSpeedButton() {
    }

    void updatePlaybackSpeedButtonText() {
    }

    public static void showSkipPreference(Activity activity, MediaplayerActivity$SkipDirection direction) {
        int checked = 0;
        int skipSecs = direction.getPrefSkipSeconds();
        int[] values = activity.getResources().getIntArray(R.array.seek_delta_values);
        String[] choices = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            if (skipSecs == values[i]) {
                checked = i;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(String.valueOf(values[i]));
            stringBuilder.append(StringUtils.SPACE);
            stringBuilder.append(activity.getString(R.string.time_seconds));
            choices[i] = stringBuilder.toString();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(direction.getTitleResourceID());
        builder.setSingleChoiceItems(choices, checked, null);
        builder.setNegativeButton(R.string.cancel_label, null);
        builder.setPositiveButton(R.string.confirm_label, new -$$Lambda$MediaplayerActivity$KCytyRi18VU1IlMJVTrQCm5KkQE(values, direction, activity));
        builder.create().show();
    }

    static /* synthetic */ void lambda$showSkipPreference$7(int[] values, MediaplayerActivity$SkipDirection direction, Activity activity, DialogInterface dialog, int which) {
        int choice = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
        if (choice >= 0) {
            if (choice < values.length) {
                direction.setPrefSkipSeconds(values[choice], activity);
                return;
            }
        }
        System.err.printf("Choice in showSkipPreference is out of bounds %d", new Object[]{Integer.valueOf(choice)});
    }

    void setupGUI() {
        setContentView(getContentViewResourceId());
        this.sbPosition = (SeekBar) findViewById(R.id.sbPosition);
        this.txtvPosition = (TextView) findViewById(R.id.txtvPosition);
        SharedPreferences prefs = getSharedPreferences(PREFS, 0);
        this.showTimeLeft = prefs.getBoolean(PREF_SHOW_TIME_LEFT, false);
        Log.d("timeleft", this.showTimeLeft ? "true" : "false");
        this.txtvLength = (TextView) findViewById(R.id.txtvLength);
        TextView textView = this.txtvLength;
        if (textView != null) {
            textView.setOnClickListener(new -$$Lambda$MediaplayerActivity$7EvhBkMefeeyI2ZRMOeDjNHb1DY(this, prefs));
        }
        this.butRev = (ImageButton) findViewById(R.id.butRev);
        this.txtvRev = (TextView) findViewById(R.id.txtvRev);
        textView = this.txtvRev;
        if (textView != null) {
            textView.setText(String.valueOf(UserPreferences.getRewindSecs()));
        }
        this.butPlay = (ImageButton) findViewById(R.id.butPlay);
        this.butFF = (ImageButton) findViewById(R.id.butFF);
        this.txtvFF = (TextView) findViewById(R.id.txtvFF);
        textView = this.txtvFF;
        if (textView != null) {
            textView.setText(String.valueOf(UserPreferences.getFastForwardSecs()));
        }
        this.butSkip = (ImageButton) findViewById(R.id.butSkip);
        this.sbPosition.setOnSeekBarChangeListener(this);
        ImageButton imageButton = this.butRev;
        if (imageButton != null) {
            imageButton.setOnClickListener(new -$$Lambda$MediaplayerActivity$XnEyRgymduigYuev3Nhqn8kqdEM());
            this.butRev.setOnLongClickListener(new -$$Lambda$MediaplayerActivity$Y-8fvJEGdhpSz_HPFF50Ws6vPRM());
        }
        this.butPlay.setOnClickListener(new -$$Lambda$MediaplayerActivity$SGyWYC0ENzkBg8hPUBquJ6-NyWA());
        imageButton = this.butFF;
        if (imageButton != null) {
            imageButton.setOnClickListener(new -$$Lambda$MediaplayerActivity$lbQos3uh77aWGXJ6umt05357kYE());
            this.butFF.setOnLongClickListener(new -$$Lambda$MediaplayerActivity$Y9ICVStmskEbYducvpUpeCXP87k());
        }
        imageButton = this.butSkip;
        if (imageButton != null) {
            imageButton.setOnClickListener(new -$$Lambda$MediaplayerActivity$v-9wZNkdkHBwhzjPeUeJEkZetSs());
        }
    }

    public static /* synthetic */ void lambda$setupGUI$8(MediaplayerActivity mediaplayerActivity, SharedPreferences prefs, View v) {
        mediaplayerActivity.showTimeLeft ^= 1;
        Playable media = mediaplayerActivity.controller.getMedia();
        if (media != null) {
            String length;
            if (mediaplayerActivity.showTimeLeft) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("-");
                stringBuilder.append(Converter.getDurationStringLong(media.getDuration() - media.getPosition()));
                length = stringBuilder.toString();
            } else {
                length = Converter.getDurationStringLong(media.getDuration());
            }
            mediaplayerActivity.txtvLength.setText(length);
            Editor editor = prefs.edit();
            editor.putBoolean(PREF_SHOW_TIME_LEFT, mediaplayerActivity.showTimeLeft);
            editor.apply();
            Log.d("timeleft on click", mediaplayerActivity.showTimeLeft ? "true" : "false");
        }
    }

    void onRewind() {
        int curr = this.controller;
        if (curr != 0) {
            this.controller.seekTo(curr.getPosition() - (UserPreferences.getRewindSecs() * 1000));
        }
    }

    void onPlayPause() {
        PlaybackController playbackController = this.controller;
        if (playbackController != null) {
            playbackController.init();
            this.controller.playPause();
        }
    }

    void onFastForward() {
        int curr = this.controller;
        if (curr != 0) {
            this.controller.seekTo((UserPreferences.getFastForwardSecs() * 1000) + curr.getPosition());
        }
    }

    private void handleError(int errorCode) {
        AlertDialog.Builder errorDialog = new AlertDialog.Builder(this);
        errorDialog.setTitle(R.string.error_label);
        errorDialog.setMessage(MediaPlayerError.getErrorString(this, errorCode));
        errorDialog.setNeutralButton("OK", new -$$Lambda$MediaplayerActivity$PNo04KOYrcGytUYVthxsBr4Ix8U());
        errorDialog.create().show();
    }

    public static /* synthetic */ void lambda$handleError$15(MediaplayerActivity mediaplayerActivity, DialogInterface dialog, int which) {
        dialog.dismiss();
        mediaplayerActivity.finish();
    }

    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        PlaybackController playbackController = this.controller;
        if (playbackController != null) {
            if (this.txtvLength != null) {
                this.prog = playbackController.onSeekBarProgressChanged(seekBar, progress, fromUser, this.txtvPosition);
                if (this.showTimeLeft && this.prog != 0.0f) {
                    int duration = this.controller.getDuration();
                    String length = new StringBuilder();
                    length.append("-");
                    length.append(Converter.getDurationStringLong(duration - ((int) (this.prog * ((float) duration)))));
                    this.txtvLength.setText(length.toString());
                }
            }
        }
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
        PlaybackController playbackController = this.controller;
        if (playbackController != null) {
            playbackController.onSeekBarStartTrackingTouch(seekBar);
        }
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        PlaybackController playbackController = this.controller;
        if (playbackController != null) {
            playbackController.onSeekBarStopTrackingTouch(seekBar, this.prog);
        }
    }

    private void checkFavorite() {
        Playable playable = this.controller.getMedia();
        if (playable instanceof FeedMedia) {
            FeedItem feedItem = ((FeedMedia) playable).getItem();
            if (feedItem != null) {
                Disposable disposable = this.disposable;
                if (disposable != null) {
                    disposable.dispose();
                }
                this.disposable = Observable.fromCallable(new -$$Lambda$MediaplayerActivity$dtSlqSlCDl4EQkSYoSmABaY8EH8(feedItem)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new -$$Lambda$MediaplayerActivity$R4Iss22D_b5H7EzxzVFAUEH-U3M(), -$$Lambda$MediaplayerActivity$u-dVKr9-rznbk97LYkaovevybdM.INSTANCE);
            }
        }
    }

    public static /* synthetic */ void lambda$checkFavorite$17(MediaplayerActivity mediaplayerActivity, FeedItem item) throws Exception {
        boolean isFav = item.isTagged(FeedItem.TAG_FAVORITE);
        if (mediaplayerActivity.isFavorite != isFav) {
            mediaplayerActivity.isFavorite = isFav;
            mediaplayerActivity.invalidateOptionsMenu();
        }
    }

    void playExternalMedia(Intent intent, MediaType type) {
        if (intent != null) {
            if (intent.getData() != null) {
                if (VERSION.SDK_INT >= 23) {
                    if (ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE") != 0) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.READ_EXTERNAL_STORAGE")) {
                            Toast.makeText(this, R.string.needs_storage_permission, 1).show();
                        } else {
                            ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 42);
                        }
                        return;
                    }
                }
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Received VIEW intent: ");
                stringBuilder.append(intent.getData().getPath());
                Log.d(str, stringBuilder.toString());
                new PlaybackServiceStarter(this, new ExternalMedia(intent.getData().getPath(), type)).startWhenPrepared(true).shouldStream(false).prepareImmediately(true).start();
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 42) {
            if (grantResults.length > 0) {
                if (grantResults[0] == 0) {
                    return;
                }
            }
            Toast.makeText(this, R.string.needs_storage_permission, 1).show();
        }
    }
}
