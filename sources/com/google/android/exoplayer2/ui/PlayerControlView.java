package com.google.android.exoplayer2.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.ControlDispatcher;
import com.google.android.exoplayer2.DefaultControlDispatcher;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerLibraryInfo;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.PlaybackPreparer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Player$EventListener;
import com.google.android.exoplayer2.Player$EventListener.-CC;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.Timeline.Period;
import com.google.android.exoplayer2.Timeline.Window;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.TimeBar.OnScrubListener;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.RepeatModeUtil;
import com.google.android.exoplayer2.util.Util;
import java.util.Arrays;
import java.util.Formatter;
import java.util.Locale;

public class PlayerControlView extends FrameLayout {
    public static final int DEFAULT_FAST_FORWARD_MS = 15000;
    public static final int DEFAULT_REPEAT_TOGGLE_MODES = 0;
    public static final int DEFAULT_REWIND_MS = 5000;
    public static final int DEFAULT_SHOW_TIMEOUT_MS = 5000;
    private static final long MAX_POSITION_FOR_SEEK_TO_PREVIOUS = 3000;
    public static final int MAX_WINDOWS_FOR_MULTI_WINDOW_TIME_BAR = 100;
    private long[] adGroupTimesMs;
    private final ComponentListener componentListener;
    private ControlDispatcher controlDispatcher;
    private final TextView durationView;
    private long[] extraAdGroupTimesMs;
    private boolean[] extraPlayedAdGroups;
    private final View fastForwardButton;
    private int fastForwardMs;
    private final StringBuilder formatBuilder;
    private final Formatter formatter;
    private final Runnable hideAction;
    private long hideAtMs;
    private boolean isAttachedToWindow;
    private boolean multiWindowTimeBar;
    private final View nextButton;
    private final View pauseButton;
    private final Period period;
    private final View playButton;
    @Nullable
    private PlaybackPreparer playbackPreparer;
    private boolean[] playedAdGroups;
    private Player player;
    private final TextView positionView;
    private final View previousButton;
    private final String repeatAllButtonContentDescription;
    private final Drawable repeatAllButtonDrawable;
    private final String repeatOffButtonContentDescription;
    private final Drawable repeatOffButtonDrawable;
    private final String repeatOneButtonContentDescription;
    private final Drawable repeatOneButtonDrawable;
    private final ImageView repeatToggleButton;
    private int repeatToggleModes;
    private final View rewindButton;
    private int rewindMs;
    private boolean scrubbing;
    private boolean showMultiWindowTimeBar;
    private boolean showShuffleButton;
    private int showTimeoutMs;
    private final View shuffleButton;
    private final TimeBar timeBar;
    private final Runnable updateProgressAction;
    private VisibilityListener visibilityListener;
    private final Window window;

    public interface VisibilityListener {
        void onVisibilityChange(int i);
    }

    private final class ComponentListener implements Player$EventListener, OnScrubListener, OnClickListener {
        public /* synthetic */ void onLoadingChanged(boolean z) {
            -CC.$default$onLoadingChanged(this, z);
        }

        public /* synthetic */ void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            -CC.$default$onPlaybackParametersChanged(this, playbackParameters);
        }

        public /* synthetic */ void onPlayerError(ExoPlaybackException exoPlaybackException) {
            -CC.$default$onPlayerError(this, exoPlaybackException);
        }

        public /* synthetic */ void onSeekProcessed() {
            -CC.$default$onSeekProcessed(this);
        }

        public /* synthetic */ void onTracksChanged(TrackGroupArray trackGroupArray, TrackSelectionArray trackSelectionArray) {
            -CC.$default$onTracksChanged(this, trackGroupArray, trackSelectionArray);
        }

        private ComponentListener() {
        }

        public void onScrubStart(TimeBar timeBar, long position) {
            PlayerControlView.this.scrubbing = true;
        }

        public void onScrubMove(TimeBar timeBar, long position) {
            if (PlayerControlView.this.positionView != null) {
                PlayerControlView.this.positionView.setText(Util.getStringForTime(PlayerControlView.this.formatBuilder, PlayerControlView.this.formatter, position));
            }
        }

        public void onScrubStop(TimeBar timeBar, long position, boolean canceled) {
            PlayerControlView.this.scrubbing = false;
            if (!canceled && PlayerControlView.this.player != null) {
                PlayerControlView.this.seekToTimeBarPosition(position);
            }
        }

        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            PlayerControlView.this.updatePlayPauseButton();
            PlayerControlView.this.updateProgress();
        }

        public void onRepeatModeChanged(int repeatMode) {
            PlayerControlView.this.updateRepeatModeButton();
            PlayerControlView.this.updateNavigation();
        }

        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
            PlayerControlView.this.updateShuffleButton();
            PlayerControlView.this.updateNavigation();
        }

        public void onPositionDiscontinuity(int reason) {
            PlayerControlView.this.updateNavigation();
            PlayerControlView.this.updateProgress();
        }

        public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {
            PlayerControlView.this.updateNavigation();
            PlayerControlView.this.updateTimeBarMode();
            PlayerControlView.this.updateProgress();
        }

        public void onClick(View view) {
            if (PlayerControlView.this.player == null) {
                return;
            }
            if (PlayerControlView.this.nextButton == view) {
                PlayerControlView.this.next();
            } else if (PlayerControlView.this.previousButton == view) {
                PlayerControlView.this.previous();
            } else if (PlayerControlView.this.fastForwardButton == view) {
                PlayerControlView.this.fastForward();
            } else if (PlayerControlView.this.rewindButton == view) {
                PlayerControlView.this.rewind();
            } else if (PlayerControlView.this.playButton == view) {
                if (PlayerControlView.this.player.getPlaybackState() == 1) {
                    if (PlayerControlView.this.playbackPreparer != null) {
                        PlayerControlView.this.playbackPreparer.preparePlayback();
                    }
                } else if (PlayerControlView.this.player.getPlaybackState() == 4) {
                    PlayerControlView.this.controlDispatcher.dispatchSeekTo(PlayerControlView.this.player, PlayerControlView.this.player.getCurrentWindowIndex(), C0555C.TIME_UNSET);
                }
                PlayerControlView.this.controlDispatcher.dispatchSetPlayWhenReady(PlayerControlView.this.player, true);
            } else if (PlayerControlView.this.pauseButton == view) {
                PlayerControlView.this.controlDispatcher.dispatchSetPlayWhenReady(PlayerControlView.this.player, false);
            } else if (PlayerControlView.this.repeatToggleButton == view) {
                PlayerControlView.this.controlDispatcher.dispatchSetRepeatMode(PlayerControlView.this.player, RepeatModeUtil.getNextRepeatMode(PlayerControlView.this.player.getRepeatMode(), PlayerControlView.this.repeatToggleModes));
            } else if (PlayerControlView.this.shuffleButton == view) {
                PlayerControlView.this.controlDispatcher.dispatchSetShuffleModeEnabled(PlayerControlView.this.player, true ^ PlayerControlView.this.player.getShuffleModeEnabled());
            }
        }
    }

    static {
        ExoPlayerLibraryInfo.registerModule("goog.exo.ui");
    }

    public PlayerControlView(Context context) {
        this(context, null);
    }

    public PlayerControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayerControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, attrs);
    }

    public PlayerControlView(Context context, AttributeSet attrs, int defStyleAttr, AttributeSet playbackAttrs) {
        super(context, attrs, defStyleAttr);
        int controllerLayoutId = C0649R.layout.exo_player_control_view;
        this.rewindMs = 5000;
        this.fastForwardMs = 15000;
        this.showTimeoutMs = 5000;
        this.repeatToggleModes = 0;
        this.hideAtMs = C0555C.TIME_UNSET;
        this.showShuffleButton = false;
        if (playbackAttrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(playbackAttrs, C0649R.styleable.PlayerControlView, 0, 0);
            try {
                this.rewindMs = a.getInt(C0649R.styleable.PlayerControlView_rewind_increment, this.rewindMs);
                this.fastForwardMs = a.getInt(C0649R.styleable.PlayerControlView_fastforward_increment, this.fastForwardMs);
                this.showTimeoutMs = a.getInt(C0649R.styleable.PlayerControlView_show_timeout, this.showTimeoutMs);
                controllerLayoutId = a.getResourceId(C0649R.styleable.PlayerControlView_controller_layout_id, controllerLayoutId);
                this.repeatToggleModes = getRepeatToggleModes(a, this.repeatToggleModes);
                this.showShuffleButton = a.getBoolean(C0649R.styleable.PlayerControlView_show_shuffle_button, this.showShuffleButton);
            } finally {
                a.recycle();
            }
        }
        this.period = new Period();
        this.window = new Window();
        this.formatBuilder = new StringBuilder();
        this.formatter = new Formatter(this.formatBuilder, Locale.getDefault());
        this.adGroupTimesMs = new long[0];
        this.playedAdGroups = new boolean[0];
        this.extraAdGroupTimesMs = new long[0];
        this.extraPlayedAdGroups = new boolean[0];
        this.componentListener = new ComponentListener();
        this.controlDispatcher = new DefaultControlDispatcher();
        this.updateProgressAction = new -$$Lambda$PlayerControlView$UNnS0kV7Qp5A4iJshVHLVqmqwTE();
        this.hideAction = new -$$Lambda$1vmvJI4HM5BSJdnh7cGvyaODZdE();
        LayoutInflater.from(context).inflate(controllerLayoutId, this);
        setDescendantFocusability(262144);
        this.durationView = (TextView) findViewById(C0649R.id.exo_duration);
        this.positionView = (TextView) findViewById(C0649R.id.exo_position);
        this.timeBar = (TimeBar) findViewById(C0649R.id.exo_progress);
        TimeBar timeBar = this.timeBar;
        if (timeBar != null) {
            timeBar.addListener(this.componentListener);
        }
        this.playButton = findViewById(C0649R.id.exo_play);
        View view = this.playButton;
        if (view != null) {
            view.setOnClickListener(this.componentListener);
        }
        this.pauseButton = findViewById(C0649R.id.exo_pause);
        view = this.pauseButton;
        if (view != null) {
            view.setOnClickListener(this.componentListener);
        }
        this.previousButton = findViewById(C0649R.id.exo_prev);
        view = this.previousButton;
        if (view != null) {
            view.setOnClickListener(this.componentListener);
        }
        this.nextButton = findViewById(C0649R.id.exo_next);
        view = this.nextButton;
        if (view != null) {
            view.setOnClickListener(this.componentListener);
        }
        this.rewindButton = findViewById(C0649R.id.exo_rew);
        view = this.rewindButton;
        if (view != null) {
            view.setOnClickListener(this.componentListener);
        }
        this.fastForwardButton = findViewById(C0649R.id.exo_ffwd);
        view = this.fastForwardButton;
        if (view != null) {
            view.setOnClickListener(this.componentListener);
        }
        this.repeatToggleButton = (ImageView) findViewById(C0649R.id.exo_repeat_toggle);
        ImageView imageView = this.repeatToggleButton;
        if (imageView != null) {
            imageView.setOnClickListener(this.componentListener);
        }
        this.shuffleButton = findViewById(C0649R.id.exo_shuffle);
        view = this.shuffleButton;
        if (view != null) {
            view.setOnClickListener(this.componentListener);
        }
        Resources resources = context.getResources();
        this.repeatOffButtonDrawable = resources.getDrawable(C0649R.drawable.exo_controls_repeat_off);
        this.repeatOneButtonDrawable = resources.getDrawable(C0649R.drawable.exo_controls_repeat_one);
        this.repeatAllButtonDrawable = resources.getDrawable(C0649R.drawable.exo_controls_repeat_all);
        this.repeatOffButtonContentDescription = resources.getString(C0649R.string.exo_controls_repeat_off_description);
        this.repeatOneButtonContentDescription = resources.getString(C0649R.string.exo_controls_repeat_one_description);
        this.repeatAllButtonContentDescription = resources.getString(C0649R.string.exo_controls_repeat_all_description);
    }

    private static int getRepeatToggleModes(TypedArray a, int repeatToggleModes) {
        return a.getInt(C0649R.styleable.PlayerControlView_repeat_toggle_modes, repeatToggleModes);
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(@Nullable Player player) {
        Player player2;
        boolean z = true;
        Assertions.checkState(Looper.myLooper() == Looper.getMainLooper());
        if (player != null) {
            if (player.getApplicationLooper() != Looper.getMainLooper()) {
                z = false;
                Assertions.checkArgument(z);
                player2 = this.player;
                if (player2 == player) {
                    if (player2 != null) {
                        player2.removeListener(this.componentListener);
                    }
                    this.player = player;
                    if (player != null) {
                        player.addListener(this.componentListener);
                    }
                    updateAll();
                }
            }
        }
        Assertions.checkArgument(z);
        player2 = this.player;
        if (player2 == player) {
            if (player2 != null) {
                player2.removeListener(this.componentListener);
            }
            this.player = player;
            if (player != null) {
                player.addListener(this.componentListener);
            }
            updateAll();
        }
    }

    public void setShowMultiWindowTimeBar(boolean showMultiWindowTimeBar) {
        this.showMultiWindowTimeBar = showMultiWindowTimeBar;
        updateTimeBarMode();
    }

    public void setExtraAdGroupMarkers(@Nullable long[] extraAdGroupTimesMs, @Nullable boolean[] extraPlayedAdGroups) {
        boolean z = false;
        if (extraAdGroupTimesMs == null) {
            this.extraAdGroupTimesMs = new long[0];
            this.extraPlayedAdGroups = new boolean[0];
        } else {
            if (extraAdGroupTimesMs.length == extraPlayedAdGroups.length) {
                z = true;
            }
            Assertions.checkArgument(z);
            this.extraAdGroupTimesMs = extraAdGroupTimesMs;
            this.extraPlayedAdGroups = extraPlayedAdGroups;
        }
        updateProgress();
    }

    public void setVisibilityListener(VisibilityListener listener) {
        this.visibilityListener = listener;
    }

    public void setPlaybackPreparer(@Nullable PlaybackPreparer playbackPreparer) {
        this.playbackPreparer = playbackPreparer;
    }

    public void setControlDispatcher(@Nullable ControlDispatcher controlDispatcher) {
        this.controlDispatcher = controlDispatcher == null ? new DefaultControlDispatcher() : controlDispatcher;
    }

    public void setRewindIncrementMs(int rewindMs) {
        this.rewindMs = rewindMs;
        updateNavigation();
    }

    public void setFastForwardIncrementMs(int fastForwardMs) {
        this.fastForwardMs = fastForwardMs;
        updateNavigation();
    }

    public int getShowTimeoutMs() {
        return this.showTimeoutMs;
    }

    public void setShowTimeoutMs(int showTimeoutMs) {
        this.showTimeoutMs = showTimeoutMs;
        if (isVisible()) {
            hideAfterTimeout();
        }
    }

    public int getRepeatToggleModes() {
        return this.repeatToggleModes;
    }

    public void setRepeatToggleModes(int repeatToggleModes) {
        this.repeatToggleModes = repeatToggleModes;
        int currentMode = this.player;
        if (currentMode != 0) {
            currentMode = currentMode.getRepeatMode();
            if (repeatToggleModes == 0 && currentMode != 0) {
                this.controlDispatcher.dispatchSetRepeatMode(this.player, 0);
            } else if (repeatToggleModes == 1 && currentMode == 2) {
                this.controlDispatcher.dispatchSetRepeatMode(this.player, 1);
            } else if (repeatToggleModes == 2 && currentMode == 1) {
                this.controlDispatcher.dispatchSetRepeatMode(this.player, 2);
            }
        }
        updateRepeatModeButton();
    }

    public boolean getShowShuffleButton() {
        return this.showShuffleButton;
    }

    public void setShowShuffleButton(boolean showShuffleButton) {
        this.showShuffleButton = showShuffleButton;
        updateShuffleButton();
    }

    public void show() {
        if (!isVisible()) {
            setVisibility(0);
            VisibilityListener visibilityListener = this.visibilityListener;
            if (visibilityListener != null) {
                visibilityListener.onVisibilityChange(getVisibility());
            }
            updateAll();
            requestPlayPauseFocus();
        }
        hideAfterTimeout();
    }

    public void hide() {
        if (isVisible()) {
            setVisibility(8);
            VisibilityListener visibilityListener = this.visibilityListener;
            if (visibilityListener != null) {
                visibilityListener.onVisibilityChange(getVisibility());
            }
            removeCallbacks(this.updateProgressAction);
            removeCallbacks(this.hideAction);
            this.hideAtMs = C0555C.TIME_UNSET;
        }
    }

    public boolean isVisible() {
        return getVisibility() == 0;
    }

    private void hideAfterTimeout() {
        removeCallbacks(this.hideAction);
        if (this.showTimeoutMs > 0) {
            long uptimeMillis = SystemClock.uptimeMillis();
            int i = this.showTimeoutMs;
            this.hideAtMs = uptimeMillis + ((long) i);
            if (this.isAttachedToWindow) {
                postDelayed(this.hideAction, (long) i);
                return;
            }
            return;
        }
        this.hideAtMs = C0555C.TIME_UNSET;
    }

    private void updateAll() {
        updatePlayPauseButton();
        updateNavigation();
        updateRepeatModeButton();
        updateShuffleButton();
        updateProgress();
    }

    private void updatePlayPauseButton() {
        if (isVisible()) {
            if (this.isAttachedToWindow) {
                boolean requestPlayPauseFocus = false;
                boolean playing = isPlaying();
                View view = this.playButton;
                int i = 8;
                int i2 = 1;
                if (view != null) {
                    int i3 = (playing && view.isFocused()) ? 1 : 0;
                    requestPlayPauseFocus = false | i3;
                    this.playButton.setVisibility(playing ? 8 : 0);
                }
                view = this.pauseButton;
                if (view != null) {
                    if (playing || !view.isFocused()) {
                        i2 = 0;
                    }
                    requestPlayPauseFocus |= i2;
                    view = this.pauseButton;
                    if (playing) {
                        i = 0;
                    }
                    view.setVisibility(i);
                }
                if (requestPlayPauseFocus) {
                    requestPlayPauseFocus();
                }
            }
        }
    }

    private void updateNavigation() {
        if (isVisible()) {
            if (this.isAttachedToWindow) {
                Player player = this.player;
                Timeline timeline = player != null ? player.getCurrentTimeline() : null;
                boolean z = true;
                boolean haveNonEmptyTimeline = (timeline == null || timeline.isEmpty()) ? false : true;
                boolean isSeekable = false;
                boolean enablePrevious = false;
                boolean enableNext = false;
                if (haveNonEmptyTimeline && !this.player.isPlayingAd()) {
                    boolean z2;
                    timeline.getWindow(this.player.getCurrentWindowIndex(), this.window);
                    isSeekable = this.window.isSeekable;
                    if (!isSeekable && this.window.isDynamic) {
                        if (!this.player.hasPrevious()) {
                            z2 = false;
                            enablePrevious = z2;
                            if (!this.window.isDynamic) {
                                if (this.player.hasNext()) {
                                    z2 = false;
                                    enableNext = z2;
                                }
                            }
                            z2 = true;
                            enableNext = z2;
                        }
                    }
                    z2 = true;
                    enablePrevious = z2;
                    if (this.window.isDynamic) {
                        if (this.player.hasNext()) {
                            z2 = false;
                            enableNext = z2;
                        }
                    }
                    z2 = true;
                    enableNext = z2;
                }
                setButtonEnabled(enablePrevious, this.previousButton);
                setButtonEnabled(enableNext, this.nextButton);
                boolean z3 = this.fastForwardMs > 0 && isSeekable;
                setButtonEnabled(z3, this.fastForwardButton);
                if (this.rewindMs <= 0 || !isSeekable) {
                    z = false;
                }
                setButtonEnabled(z, this.rewindButton);
                TimeBar timeBar = this.timeBar;
                if (timeBar != null) {
                    timeBar.setEnabled(isSeekable);
                }
            }
        }
    }

    private void updateRepeatModeButton() {
        if (isVisible() && this.isAttachedToWindow) {
            View view = this.repeatToggleButton;
            if (view != null) {
                if (this.repeatToggleModes == 0) {
                    view.setVisibility(8);
                } else if (this.player == null) {
                    setButtonEnabled(false, view);
                } else {
                    setButtonEnabled(true, view);
                    switch (this.player.getRepeatMode()) {
                        case 0:
                            this.repeatToggleButton.setImageDrawable(this.repeatOffButtonDrawable);
                            this.repeatToggleButton.setContentDescription(this.repeatOffButtonContentDescription);
                            break;
                        case 1:
                            this.repeatToggleButton.setImageDrawable(this.repeatOneButtonDrawable);
                            this.repeatToggleButton.setContentDescription(this.repeatOneButtonContentDescription);
                            break;
                        case 2:
                            this.repeatToggleButton.setImageDrawable(this.repeatAllButtonDrawable);
                            this.repeatToggleButton.setContentDescription(this.repeatAllButtonContentDescription);
                            break;
                        default:
                            break;
                    }
                    this.repeatToggleButton.setVisibility(0);
                }
            }
        }
    }

    private void updateShuffleButton() {
        if (isVisible() && this.isAttachedToWindow) {
            View view = this.shuffleButton;
            if (view != null) {
                if (this.showShuffleButton) {
                    Player player = this.player;
                    if (player == null) {
                        setButtonEnabled(false, view);
                    } else {
                        view.setAlpha(player.getShuffleModeEnabled() ? 1.0f : 0.3f);
                        this.shuffleButton.setEnabled(true);
                        this.shuffleButton.setVisibility(0);
                    }
                } else {
                    view.setVisibility(8);
                }
            }
        }
    }

    private void updateTimeBarMode() {
        Player player = this.player;
        if (player != null) {
            boolean z;
            if (this.showMultiWindowTimeBar) {
                if (canShowMultiWindowTimeBar(player.getCurrentTimeline(), this.window)) {
                    z = true;
                    this.multiWindowTimeBar = z;
                }
            }
            z = false;
            this.multiWindowTimeBar = z;
        }
    }

    private void updateProgress() {
        PlayerControlView playerControlView = this;
        if (isVisible()) {
            if (playerControlView.isAttachedToWindow) {
                long durationUs;
                long currentWindowTimeBarOffsetMs;
                long position = 0;
                long bufferedPosition = 0;
                long duration = 0;
                Timeline timeline = playerControlView.player;
                long position2;
                long j;
                if (timeline != null) {
                    long currentWindowTimeBarOffsetMs2;
                    int lastWindowIndex;
                    durationUs = 0;
                    int adGroupCount = 0;
                    timeline = timeline.getCurrentTimeline();
                    if (timeline.isEmpty()) {
                        position2 = 0;
                        j = 0;
                        currentWindowTimeBarOffsetMs2 = 0;
                    } else {
                        int currentWindowIndex = playerControlView.player.getCurrentWindowIndex();
                        int firstWindowIndex = playerControlView.multiWindowTimeBar ? 0 : currentWindowIndex;
                        lastWindowIndex = playerControlView.multiWindowTimeBar ? timeline.getWindowCount() - 1 : currentWindowIndex;
                        currentWindowTimeBarOffsetMs = firstWindowIndex;
                        currentWindowTimeBarOffsetMs2 = 0;
                        while (currentWindowTimeBarOffsetMs <= lastWindowIndex) {
                            if (currentWindowTimeBarOffsetMs == currentWindowIndex) {
                                currentWindowTimeBarOffsetMs2 = C0555C.usToMs(durationUs);
                            }
                            timeline.getWindow(currentWindowTimeBarOffsetMs, playerControlView.window);
                            position2 = position;
                            if (playerControlView.window.durationUs == C0555C.TIME_UNSET) {
                                Assertions.checkState(playerControlView.multiWindowTimeBar ^ 1);
                                j = bufferedPosition;
                                break;
                            }
                            for (position = playerControlView.window.firstPeriodIndex; position <= playerControlView.window.lastPeriodIndex; position++) {
                                timeline.getPeriod(position, playerControlView.period);
                                int periodAdGroupCount = playerControlView.period.getAdGroupCount();
                                int adGroupIndex = 0;
                                while (adGroupIndex < periodAdGroupCount) {
                                    int periodAdGroupCount2 = periodAdGroupCount;
                                    long adGroupTimeInPeriodUs = playerControlView.period.getAdGroupTimeUs(adGroupIndex);
                                    if (adGroupTimeInPeriodUs == Long.MIN_VALUE) {
                                        j = bufferedPosition;
                                        if (playerControlView.period.durationUs == 1) {
                                            adGroupIndex++;
                                            periodAdGroupCount = periodAdGroupCount2;
                                            bufferedPosition = j;
                                        } else {
                                            periodAdGroupCount = playerControlView.period.durationUs;
                                        }
                                    } else {
                                        j = bufferedPosition;
                                        periodAdGroupCount = adGroupTimeInPeriodUs;
                                    }
                                    adGroupTimeInPeriodUs = periodAdGroupCount + playerControlView.period.getPositionInWindowUs();
                                    long adGroupTimeInPeriodUs2;
                                    if (adGroupTimeInPeriodUs >= 0) {
                                        adGroupTimeInPeriodUs2 = periodAdGroupCount;
                                        if (adGroupTimeInPeriodUs <= playerControlView.window.durationUs) {
                                            periodAdGroupCount = playerControlView.adGroupTimesMs;
                                            if (adGroupCount == periodAdGroupCount.length) {
                                                periodAdGroupCount = periodAdGroupCount.length == 0 ? 1 : periodAdGroupCount.length * 2;
                                                playerControlView.adGroupTimesMs = Arrays.copyOf(playerControlView.adGroupTimesMs, periodAdGroupCount);
                                                playerControlView.playedAdGroups = Arrays.copyOf(playerControlView.playedAdGroups, periodAdGroupCount);
                                            }
                                            playerControlView.adGroupTimesMs[adGroupCount] = C0555C.usToMs(durationUs + adGroupTimeInPeriodUs);
                                            playerControlView.playedAdGroups[adGroupCount] = playerControlView.period.hasPlayedAdGroup(adGroupIndex);
                                            adGroupCount++;
                                        }
                                    } else {
                                        adGroupTimeInPeriodUs2 = periodAdGroupCount;
                                    }
                                    adGroupIndex++;
                                    periodAdGroupCount = periodAdGroupCount2;
                                    bufferedPosition = j;
                                }
                                j = bufferedPosition;
                            }
                            durationUs += playerControlView.window.durationUs;
                            currentWindowTimeBarOffsetMs++;
                            position = position2;
                        }
                        j = bufferedPosition;
                    }
                    duration = C0555C.usToMs(durationUs);
                    position = currentWindowTimeBarOffsetMs2 + playerControlView.player.getContentPosition();
                    bufferedPosition = currentWindowTimeBarOffsetMs2 + playerControlView.player.getContentBufferedPosition();
                    if (playerControlView.timeBar != null) {
                        lastWindowIndex = playerControlView.extraAdGroupTimesMs.length;
                        int totalAdGroupCount = adGroupCount + lastWindowIndex;
                        long[] jArr = playerControlView.adGroupTimesMs;
                        if (totalAdGroupCount > jArr.length) {
                            playerControlView.adGroupTimesMs = Arrays.copyOf(jArr, totalAdGroupCount);
                            playerControlView.playedAdGroups = Arrays.copyOf(playerControlView.playedAdGroups, totalAdGroupCount);
                        }
                        System.arraycopy(playerControlView.extraAdGroupTimesMs, 0, playerControlView.adGroupTimesMs, adGroupCount, lastWindowIndex);
                        System.arraycopy(playerControlView.extraPlayedAdGroups, 0, playerControlView.playedAdGroups, adGroupCount, lastWindowIndex);
                        playerControlView.timeBar.setAdGroupTimesMs(playerControlView.adGroupTimesMs, playerControlView.playedAdGroups, totalAdGroupCount);
                    }
                } else {
                    position2 = 0;
                    j = 0;
                }
                TextView textView = playerControlView.durationView;
                if (textView != null) {
                    textView.setText(Util.getStringForTime(playerControlView.formatBuilder, playerControlView.formatter, duration));
                }
                textView = playerControlView.positionView;
                if (textView != null && !playerControlView.scrubbing) {
                    textView.setText(Util.getStringForTime(playerControlView.formatBuilder, playerControlView.formatter, position));
                }
                TimeBar timeBar = playerControlView.timeBar;
                if (timeBar != null) {
                    timeBar.setPosition(position);
                    playerControlView.timeBar.setBufferedPosition(bufferedPosition);
                    playerControlView.timeBar.setDuration(duration);
                }
                removeCallbacks(playerControlView.updateProgressAction);
                Player player = playerControlView.player;
                int playbackState = player == null ? 1 : player.getPlaybackState();
                if (playbackState != 1 && playbackState != 4) {
                    if (playerControlView.player.getPlayWhenReady() && playbackState == 3) {
                        float playbackSpeed = playerControlView.player.getPlaybackParameters().speed;
                        if (playbackSpeed <= 0.1f) {
                            currentWindowTimeBarOffsetMs = 1000;
                        } else if (playbackSpeed <= 5.0f) {
                            durationUs = (long) (1000 / Math.max(1, Math.round(1.0f / playbackSpeed)));
                            long mediaTimeDelayMs = durationUs - (position % durationUs);
                            if (mediaTimeDelayMs < durationUs / 5) {
                                mediaTimeDelayMs += durationUs;
                            }
                            currentWindowTimeBarOffsetMs = playbackSpeed == 1.0f ? mediaTimeDelayMs : (long) (((float) mediaTimeDelayMs) / playbackSpeed);
                        } else {
                            currentWindowTimeBarOffsetMs = 200;
                        }
                    } else {
                        currentWindowTimeBarOffsetMs = 1000;
                    }
                    postDelayed(playerControlView.updateProgressAction, currentWindowTimeBarOffsetMs);
                }
            }
        }
    }

    private void requestPlayPauseFocus() {
        View view;
        boolean playing = isPlaying();
        if (!playing) {
            view = this.playButton;
            if (view != null) {
                view.requestFocus();
                return;
            }
        }
        if (playing) {
            view = this.pauseButton;
            if (view != null) {
                view.requestFocus();
            }
        }
    }

    private void setButtonEnabled(boolean enabled, View view) {
        if (view != null) {
            view.setEnabled(enabled);
            view.setAlpha(enabled ? 1.0f : 0.3f);
            view.setVisibility(0);
        }
    }

    private void previous() {
        Timeline timeline = this.player.getCurrentTimeline();
        if (!timeline.isEmpty()) {
            if (!this.player.isPlayingAd()) {
                timeline.getWindow(this.player.getCurrentWindowIndex(), this.window);
                int previousWindowIndex = this.player.getPreviousWindowIndex();
                if (previousWindowIndex != -1) {
                    if (this.player.getCurrentPosition() > MAX_POSITION_FOR_SEEK_TO_PREVIOUS) {
                        if (!this.window.isDynamic || this.window.isSeekable) {
                        }
                    }
                    seekTo(previousWindowIndex, C0555C.TIME_UNSET);
                }
                seekTo(0);
            }
        }
    }

    private void next() {
        Timeline timeline = this.player.getCurrentTimeline();
        if (!timeline.isEmpty()) {
            if (!this.player.isPlayingAd()) {
                int windowIndex = this.player.getCurrentWindowIndex();
                int nextWindowIndex = this.player.getNextWindowIndex();
                if (nextWindowIndex != -1) {
                    seekTo(nextWindowIndex, C0555C.TIME_UNSET);
                } else if (timeline.getWindow(windowIndex, this.window).isDynamic) {
                    seekTo(windowIndex, C0555C.TIME_UNSET);
                }
            }
        }
    }

    private void rewind() {
        if (this.rewindMs > 0) {
            seekTo(Math.max(this.player.getCurrentPosition() - ((long) this.rewindMs), 0));
        }
    }

    private void fastForward() {
        if (this.fastForwardMs > 0) {
            long durationMs = this.player.getDuration();
            long seekPositionMs = this.player.getCurrentPosition() + ((long) this.fastForwardMs);
            if (durationMs != C0555C.TIME_UNSET) {
                seekPositionMs = Math.min(seekPositionMs, durationMs);
            }
            seekTo(seekPositionMs);
        }
    }

    private void seekTo(long positionMs) {
        seekTo(this.player.getCurrentWindowIndex(), positionMs);
    }

    private void seekTo(int windowIndex, long positionMs) {
        if (!this.controlDispatcher.dispatchSeekTo(this.player, windowIndex, positionMs)) {
            updateProgress();
        }
    }

    private void seekToTimeBarPosition(long positionMs) {
        int windowIndex;
        Timeline timeline = this.player.getCurrentTimeline();
        if (!this.multiWindowTimeBar || timeline.isEmpty()) {
            windowIndex = this.player.getCurrentWindowIndex();
        } else {
            long windowDurationMs;
            int windowCount = timeline.getWindowCount();
            windowIndex = 0;
            while (true) {
                windowDurationMs = timeline.getWindow(windowIndex, this.window).getDurationMs();
                if (positionMs < windowDurationMs) {
                    break;
                } else if (windowIndex == windowCount - 1) {
                    break;
                } else {
                    positionMs -= windowDurationMs;
                    windowIndex++;
                }
            }
            positionMs = windowDurationMs;
        }
        seekTo(windowIndex, positionMs);
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.isAttachedToWindow = true;
        long delayMs = this.hideAtMs;
        if (delayMs != C0555C.TIME_UNSET) {
            delayMs -= SystemClock.uptimeMillis();
            if (delayMs <= 0) {
                hide();
            } else {
                postDelayed(this.hideAction, delayMs);
            }
        } else if (isVisible()) {
            hideAfterTimeout();
            updateAll();
        }
        updateAll();
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.isAttachedToWindow = false;
        removeCallbacks(this.updateProgressAction);
        removeCallbacks(this.hideAction);
    }

    public final boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == 0) {
            removeCallbacks(this.hideAction);
        } else if (ev.getAction() == 1) {
            hideAfterTimeout();
        }
        return super.dispatchTouchEvent(ev);
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (!dispatchMediaKeyEvent(event)) {
            if (!super.dispatchKeyEvent(event)) {
                return false;
            }
        }
        return true;
    }

    public boolean dispatchMediaKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (this.player != null) {
            if (isHandledMediaKey(keyCode)) {
                if (event.getAction() == 0) {
                    if (keyCode == 90) {
                        fastForward();
                    } else if (keyCode == 89) {
                        rewind();
                    } else if (event.getRepeatCount() == 0) {
                        switch (keyCode) {
                            case 85:
                                ControlDispatcher controlDispatcher = this.controlDispatcher;
                                Player player = this.player;
                                controlDispatcher.dispatchSetPlayWhenReady(player, player.getPlayWhenReady() ^ true);
                                break;
                            case 87:
                                next();
                                break;
                            case 88:
                                previous();
                                break;
                            case 126:
                                this.controlDispatcher.dispatchSetPlayWhenReady(this.player, true);
                                break;
                            case 127:
                                this.controlDispatcher.dispatchSetPlayWhenReady(this.player, false);
                                break;
                            default:
                                break;
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    private boolean isPlaying() {
        Player player = this.player;
        if (player != null) {
            if (player.getPlaybackState() != 4) {
                if (this.player.getPlaybackState() != 1) {
                    if (this.player.getPlayWhenReady()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @SuppressLint({"InlinedApi"})
    private static boolean isHandledMediaKey(int keyCode) {
        if (!(keyCode == 90 || keyCode == 89 || keyCode == 85 || keyCode == 126 || keyCode == 127 || keyCode == 87)) {
            if (keyCode != 88) {
                return false;
            }
        }
        return true;
    }

    private static boolean canShowMultiWindowTimeBar(Timeline timeline, Window window) {
        if (timeline.getWindowCount() > 100) {
            return false;
        }
        int windowCount = timeline.getWindowCount();
        for (int i = 0; i < windowCount; i++) {
            if (timeline.getWindow(i, window).durationUs == C0555C.TIME_UNSET) {
                return false;
            }
        }
        return true;
    }
}
