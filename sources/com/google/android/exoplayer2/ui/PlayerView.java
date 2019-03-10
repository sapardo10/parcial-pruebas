package com.google.android.exoplayer2.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.exoplayer2.ControlDispatcher;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.PlaybackPreparer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Player$EventListener;
import com.google.android.exoplayer2.Player$EventListener.-CC;
import com.google.android.exoplayer2.Player.TextComponent;
import com.google.android.exoplayer2.Player.VideoComponent;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.Metadata.Entry;
import com.google.android.exoplayer2.metadata.id3.ApicFrame;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.TextOutput;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout.AspectRatioListener;
import com.google.android.exoplayer2.ui.PlayerControlView.VisibilityListener;
import com.google.android.exoplayer2.ui.spherical.SingleTapListener;
import com.google.android.exoplayer2.ui.spherical.SphericalSurfaceView;
import com.google.android.exoplayer2.ui.spherical.SphericalSurfaceView.SurfaceListener;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.ErrorMessageProvider;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoListener;
import java.util.List;

public class PlayerView extends FrameLayout {
    public static final int SHOW_BUFFERING_ALWAYS = 2;
    public static final int SHOW_BUFFERING_NEVER = 0;
    public static final int SHOW_BUFFERING_WHEN_PLAYING = 1;
    private static final int SURFACE_TYPE_MONO360_VIEW = 3;
    private static final int SURFACE_TYPE_NONE = 0;
    private static final int SURFACE_TYPE_SURFACE_VIEW = 1;
    private static final int SURFACE_TYPE_TEXTURE_VIEW = 2;
    private final ImageView artworkView;
    @Nullable
    private final View bufferingView;
    private final ComponentListener componentListener;
    @Nullable
    private final AspectRatioFrameLayout contentFrame;
    private final PlayerControlView controller;
    private boolean controllerAutoShow;
    private boolean controllerHideDuringAds;
    private boolean controllerHideOnTouch;
    private int controllerShowTimeoutMs;
    @Nullable
    private CharSequence customErrorMessage;
    @Nullable
    private Drawable defaultArtwork;
    @Nullable
    private ErrorMessageProvider<? super ExoPlaybackException> errorMessageProvider;
    @Nullable
    private final TextView errorMessageView;
    private boolean keepContentOnPlayerReset;
    private final FrameLayout overlayFrameLayout;
    private Player player;
    private int showBuffering;
    private final View shutterView;
    private final SubtitleView subtitleView;
    @Nullable
    private final View surfaceView;
    private int textureViewRotation;
    private boolean useArtwork;
    private boolean useController;

    private final class ComponentListener implements Player$EventListener, TextOutput, VideoListener, OnLayoutChangeListener, SurfaceListener, SingleTapListener {
        public /* synthetic */ void onLoadingChanged(boolean z) {
            -CC.$default$onLoadingChanged(this, z);
        }

        public /* synthetic */ void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            -CC.$default$onPlaybackParametersChanged(this, playbackParameters);
        }

        public /* synthetic */ void onPlayerError(ExoPlaybackException exoPlaybackException) {
            -CC.$default$onPlayerError(this, exoPlaybackException);
        }

        public /* synthetic */ void onRepeatModeChanged(int i) {
            -CC.$default$onRepeatModeChanged(this, i);
        }

        public /* synthetic */ void onSeekProcessed() {
            -CC.$default$onSeekProcessed(this);
        }

        public /* synthetic */ void onShuffleModeEnabledChanged(boolean z) {
            -CC.$default$onShuffleModeEnabledChanged(this, z);
        }

        public /* synthetic */ void onSurfaceSizeChanged(int i, int i2) {
            VideoListener.-CC.$default$onSurfaceSizeChanged(this, i, i2);
        }

        public /* synthetic */ void onTimelineChanged(Timeline timeline, @Nullable Object obj, int i) {
            -CC.$default$onTimelineChanged(this, timeline, obj, i);
        }

        private ComponentListener() {
        }

        public void onCues(List<Cue> cues) {
            if (PlayerView.this.subtitleView != null) {
                PlayerView.this.subtitleView.onCues(cues);
            }
        }

        public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
            float videoAspectRatio;
            PlayerView playerView;
            if (height != 0) {
                if (width != 0) {
                    videoAspectRatio = (((float) width) * pixelWidthHeightRatio) / ((float) height);
                    if (PlayerView.this.surfaceView instanceof TextureView) {
                        if (unappliedRotationDegrees != 90) {
                            if (unappliedRotationDegrees == 270) {
                                if (PlayerView.this.textureViewRotation == 0) {
                                    PlayerView.this.surfaceView.removeOnLayoutChangeListener(this);
                                }
                                PlayerView.this.textureViewRotation = unappliedRotationDegrees;
                                if (PlayerView.this.textureViewRotation == 0) {
                                    PlayerView.this.surfaceView.addOnLayoutChangeListener(this);
                                }
                                PlayerView.applyTextureViewRotation((TextureView) PlayerView.this.surfaceView, PlayerView.this.textureViewRotation);
                            }
                        }
                        videoAspectRatio = 1.0f / videoAspectRatio;
                        if (PlayerView.this.textureViewRotation == 0) {
                            PlayerView.this.surfaceView.removeOnLayoutChangeListener(this);
                        }
                        PlayerView.this.textureViewRotation = unappliedRotationDegrees;
                        if (PlayerView.this.textureViewRotation == 0) {
                            PlayerView.this.surfaceView.addOnLayoutChangeListener(this);
                        }
                        PlayerView.applyTextureViewRotation((TextureView) PlayerView.this.surfaceView, PlayerView.this.textureViewRotation);
                    }
                    playerView = PlayerView.this;
                    playerView.onContentAspectRatioChanged(videoAspectRatio, playerView.contentFrame, PlayerView.this.surfaceView);
                }
            }
            videoAspectRatio = 1.0f;
            if (PlayerView.this.surfaceView instanceof TextureView) {
                if (unappliedRotationDegrees != 90) {
                    if (unappliedRotationDegrees == 270) {
                        if (PlayerView.this.textureViewRotation == 0) {
                            PlayerView.this.surfaceView.removeOnLayoutChangeListener(this);
                        }
                        PlayerView.this.textureViewRotation = unappliedRotationDegrees;
                        if (PlayerView.this.textureViewRotation == 0) {
                            PlayerView.this.surfaceView.addOnLayoutChangeListener(this);
                        }
                        PlayerView.applyTextureViewRotation((TextureView) PlayerView.this.surfaceView, PlayerView.this.textureViewRotation);
                    }
                }
                videoAspectRatio = 1.0f / videoAspectRatio;
                if (PlayerView.this.textureViewRotation == 0) {
                    PlayerView.this.surfaceView.removeOnLayoutChangeListener(this);
                }
                PlayerView.this.textureViewRotation = unappliedRotationDegrees;
                if (PlayerView.this.textureViewRotation == 0) {
                    PlayerView.this.surfaceView.addOnLayoutChangeListener(this);
                }
                PlayerView.applyTextureViewRotation((TextureView) PlayerView.this.surfaceView, PlayerView.this.textureViewRotation);
            }
            playerView = PlayerView.this;
            playerView.onContentAspectRatioChanged(videoAspectRatio, playerView.contentFrame, PlayerView.this.surfaceView);
        }

        public void onRenderedFirstFrame() {
            if (PlayerView.this.shutterView != null) {
                PlayerView.this.shutterView.setVisibility(4);
            }
        }

        public void onTracksChanged(TrackGroupArray tracks, TrackSelectionArray selections) {
            PlayerView.this.updateForCurrentTrackSelections(false);
        }

        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            PlayerView.this.updateBuffering();
            PlayerView.this.updateErrorMessage();
            if (PlayerView.this.isPlayingAd() && PlayerView.this.controllerHideDuringAds) {
                PlayerView.this.hideController();
            } else {
                PlayerView.this.maybeShowController(false);
            }
        }

        public void onPositionDiscontinuity(int reason) {
            if (PlayerView.this.isPlayingAd() && PlayerView.this.controllerHideDuringAds) {
                PlayerView.this.hideController();
            }
        }

        public void onLayoutChange(View view, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            PlayerView.applyTextureViewRotation((TextureView) view, PlayerView.this.textureViewRotation);
        }

        public void surfaceChanged(@Nullable Surface surface) {
            if (PlayerView.this.player != null) {
                VideoComponent videoComponent = PlayerView.this.player.getVideoComponent();
                if (videoComponent != null) {
                    videoComponent.setVideoSurface(surface);
                }
            }
        }

        public boolean onSingleTapUp(MotionEvent e) {
            return PlayerView.this.toggleControllerVisibility();
        }
    }

    public PlayerView(Context context) {
        this(context, null);
    }

    public PlayerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        Throwable th;
        ViewGroup viewGroup = this;
        Context context2 = context;
        AttributeSet attributeSet = attrs;
        super(context, attrs, defStyleAttr);
        if (isInEditMode()) {
            viewGroup.contentFrame = null;
            viewGroup.shutterView = null;
            viewGroup.surfaceView = null;
            viewGroup.artworkView = null;
            viewGroup.subtitleView = null;
            viewGroup.bufferingView = null;
            viewGroup.errorMessageView = null;
            viewGroup.controller = null;
            viewGroup.componentListener = null;
            viewGroup.overlayFrameLayout = null;
            ImageView logo = new ImageView(context2);
            if (Util.SDK_INT >= 23) {
                configureEditModeLogoV23(getResources(), logo);
            } else {
                configureEditModeLogo(getResources(), logo);
            }
            addView(logo);
            return;
        }
        int controllerShowTimeoutMs;
        Context context3;
        int shutterColor = 0;
        int playerLayoutId = C0649R.layout.exo_player_view;
        boolean useArtwork = true;
        int defaultArtworkId = 0;
        boolean useController = true;
        int surfaceType = 1;
        int resizeMode = 0;
        boolean controllerHideOnTouch = true;
        boolean controllerAutoShow = true;
        boolean controllerHideDuringAds = true;
        boolean shutterColorSet = false;
        int showBuffering = 0;
        if (attributeSet != null) {
            boolean controllerHideDuringAds2 = true;
            TypedArray a = context.getTheme().obtainStyledAttributes(attributeSet, C0649R.styleable.PlayerView, 0, 0);
            try {
                controllerHideDuringAds = a.hasValue(C0649R.styleable.PlayerView_shutter_background_color);
                try {
                    shutterColor = a.getColor(C0649R.styleable.PlayerView_shutter_background_color, 0);
                    playerLayoutId = a.getResourceId(C0649R.styleable.PlayerView_player_layout_id, playerLayoutId);
                    useArtwork = a.getBoolean(C0649R.styleable.PlayerView_use_artwork, true);
                    defaultArtworkId = a.getResourceId(C0649R.styleable.PlayerView_default_artwork, 0);
                    useController = a.getBoolean(C0649R.styleable.PlayerView_use_controller, true);
                    surfaceType = a.getInt(C0649R.styleable.PlayerView_surface_type, 1);
                    resizeMode = a.getInt(C0649R.styleable.PlayerView_resize_mode, 0);
                    int controllerShowTimeoutMs2 = a.getInt(C0649R.styleable.PlayerView_show_timeout, 5000);
                    controllerHideOnTouch = a.getBoolean(C0649R.styleable.PlayerView_hide_on_touch, true);
                    controllerAutoShow = a.getBoolean(C0649R.styleable.PlayerView_auto_show, true);
                    showBuffering = a.getInteger(C0649R.styleable.PlayerView_show_buffering, 0);
                    shutterColorSet = controllerHideDuringAds;
                    viewGroup.keepContentOnPlayerReset = a.getBoolean(C0649R.styleable.PlayerView_keep_content_on_player_reset, viewGroup.keepContentOnPlayerReset);
                    try {
                        controllerHideDuringAds = a.getBoolean(C0649R.styleable.PlayerView_hide_during_ads, controllerHideDuringAds2);
                        a.recycle();
                        int controllerShowTimeoutMs3 = controllerShowTimeoutMs2;
                    } catch (Throwable th2) {
                        th = th2;
                        a.recycle();
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    shutterColorSet = controllerHideDuringAds;
                    a.recycle();
                    throw th;
                }
            } catch (Throwable th4) {
                th = th4;
                controllerHideDuringAds = controllerHideDuringAds2;
                a.recycle();
                throw th;
            }
        }
        controllerShowTimeoutMs3 = 5000;
        LayoutInflater.from(context).inflate(playerLayoutId, viewGroup);
        viewGroup.componentListener = new ComponentListener();
        setDescendantFocusability(262144);
        viewGroup.contentFrame = (AspectRatioFrameLayout) findViewById(C0649R.id.exo_content_frame);
        AspectRatioFrameLayout aspectRatioFrameLayout = viewGroup.contentFrame;
        if (aspectRatioFrameLayout != null) {
            setResizeModeRaw(aspectRatioFrameLayout, resizeMode);
        }
        viewGroup.shutterView = findViewById(C0649R.id.exo_shutter);
        View view = viewGroup.shutterView;
        if (view != null && shutterColorSet) {
            view.setBackgroundColor(shutterColor);
        }
        int i;
        if (viewGroup.contentFrame == null || surfaceType == 0) {
            controllerShowTimeoutMs = controllerShowTimeoutMs3;
            i = shutterColor;
            context3 = context;
            viewGroup.surfaceView = null;
        } else {
            LayoutParams params = new LayoutParams(-1, -1);
            switch (surfaceType) {
                case 2:
                    controllerShowTimeoutMs = controllerShowTimeoutMs3;
                    i = shutterColor;
                    context3 = context;
                    viewGroup.surfaceView = new TextureView(context3);
                    break;
                case 3:
                    controllerShowTimeoutMs = controllerShowTimeoutMs3;
                    Assertions.checkState(Util.SDK_INT >= 15 ? 1 : 0);
                    context3 = context;
                    controllerShowTimeoutMs3 = new SphericalSurfaceView(context3);
                    i = shutterColor;
                    controllerShowTimeoutMs3.setSurfaceListener(viewGroup.componentListener);
                    controllerShowTimeoutMs3.setSingleTapListener(viewGroup.componentListener);
                    viewGroup.surfaceView = controllerShowTimeoutMs3;
                    break;
                default:
                    controllerShowTimeoutMs = controllerShowTimeoutMs3;
                    context3 = context;
                    viewGroup.surfaceView = new SurfaceView(context3);
                    break;
            }
            viewGroup.surfaceView.setLayoutParams(params);
            viewGroup.contentFrame.addView(viewGroup.surfaceView, null);
        }
        viewGroup.overlayFrameLayout = (FrameLayout) findViewById(C0649R.id.exo_overlay);
        viewGroup.artworkView = (ImageView) findViewById(C0649R.id.exo_artwork);
        boolean z = useArtwork && viewGroup.artworkView != null;
        viewGroup.useArtwork = z;
        if (defaultArtworkId != 0) {
            viewGroup.defaultArtwork = ContextCompat.getDrawable(getContext(), defaultArtworkId);
        }
        viewGroup.subtitleView = (SubtitleView) findViewById(C0649R.id.exo_subtitles);
        SubtitleView subtitleView = viewGroup.subtitleView;
        if (subtitleView != null) {
            subtitleView.setUserDefaultStyle();
            viewGroup.subtitleView.setUserDefaultTextSize();
        }
        viewGroup.bufferingView = findViewById(C0649R.id.exo_buffering);
        view = viewGroup.bufferingView;
        if (view != null) {
            view.setVisibility(8);
        }
        viewGroup.showBuffering = showBuffering;
        viewGroup.errorMessageView = (TextView) findViewById(C0649R.id.exo_error_message);
        TextView textView = viewGroup.errorMessageView;
        if (textView != null) {
            textView.setVisibility(8);
        }
        PlayerControlView customController = (PlayerControlView) findViewById(C0649R.id.exo_controller);
        View controllerPlaceholder = findViewById(C0649R.id.exo_controller_placeholder);
        int i2;
        if (customController != null) {
            viewGroup.controller = customController;
            PlayerControlView playerControlView = customController;
            i2 = showBuffering;
        } else if (controllerPlaceholder != null) {
            viewGroup.controller = new PlayerControlView(context3, null, 0, attributeSet);
            viewGroup.controller.setLayoutParams(controllerPlaceholder.getLayoutParams());
            ViewGroup parent = (ViewGroup) controllerPlaceholder.getParent();
            customController = parent.indexOfChild(controllerPlaceholder);
            parent.removeView(controllerPlaceholder);
            parent.addView(viewGroup.controller, customController);
        } else {
            i2 = showBuffering;
            viewGroup.controller = null;
        }
        viewGroup.controllerShowTimeoutMs = viewGroup.controller != null ? controllerShowTimeoutMs : 0;
        viewGroup.controllerHideOnTouch = controllerHideOnTouch;
        viewGroup.controllerAutoShow = controllerAutoShow;
        viewGroup.controllerHideDuringAds = controllerHideDuringAds;
        boolean z2 = useController && viewGroup.controller != null;
        viewGroup.useController = z2;
        hideController();
    }

    public static void switchTargetView(Player player, @Nullable PlayerView oldPlayerView, @Nullable PlayerView newPlayerView) {
        if (oldPlayerView != newPlayerView) {
            if (newPlayerView != null) {
                newPlayerView.setPlayer(player);
            }
            if (oldPlayerView != null) {
                oldPlayerView.setPlayer(null);
            }
        }
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(@Nullable Player player) {
        boolean z;
        Player player2;
        VideoComponent oldVideoComponent;
        View view;
        TextComponent oldTextComponent;
        SubtitleView subtitleView;
        View view2;
        TextComponent newTextComponent;
        Assertions.checkState(Looper.myLooper() == Looper.getMainLooper());
        if (player != null) {
            if (player.getApplicationLooper() != Looper.getMainLooper()) {
                z = false;
                Assertions.checkArgument(z);
                player2 = this.player;
                if (player2 == player) {
                    if (player2 != null) {
                        player2.removeListener(this.componentListener);
                        oldVideoComponent = this.player.getVideoComponent();
                        if (oldVideoComponent != null) {
                            oldVideoComponent.removeVideoListener(this.componentListener);
                            view = this.surfaceView;
                            if (view instanceof TextureView) {
                                oldVideoComponent.clearVideoTextureView((TextureView) view);
                            } else if (view instanceof SphericalSurfaceView) {
                                ((SphericalSurfaceView) view).setVideoComponent(null);
                            } else if (view instanceof SurfaceView) {
                                oldVideoComponent.clearVideoSurfaceView((SurfaceView) view);
                            }
                        }
                        oldTextComponent = this.player.getTextComponent();
                        if (oldTextComponent != null) {
                            oldTextComponent.removeTextOutput(this.componentListener);
                        }
                    }
                    this.player = player;
                    if (this.useController) {
                        this.controller.setPlayer(player);
                    }
                    subtitleView = this.subtitleView;
                    if (subtitleView != null) {
                        subtitleView.setCues(null);
                    }
                    updateBuffering();
                    updateErrorMessage();
                    updateForCurrentTrackSelections(true);
                    if (player == null) {
                        oldVideoComponent = player.getVideoComponent();
                        if (oldVideoComponent != null) {
                            view2 = this.surfaceView;
                            if (view2 instanceof TextureView) {
                                oldVideoComponent.setVideoTextureView((TextureView) view2);
                            } else if (view2 instanceof SphericalSurfaceView) {
                                ((SphericalSurfaceView) view2).setVideoComponent(oldVideoComponent);
                            } else if (view2 instanceof SurfaceView) {
                                oldVideoComponent.setVideoSurfaceView((SurfaceView) view2);
                            }
                            oldVideoComponent.addVideoListener(this.componentListener);
                        }
                        newTextComponent = player.getTextComponent();
                        if (newTextComponent != null) {
                            newTextComponent.addTextOutput(this.componentListener);
                        }
                        player.addListener(this.componentListener);
                        maybeShowController(false);
                    } else {
                        hideController();
                    }
                }
            }
        }
        z = true;
        Assertions.checkArgument(z);
        player2 = this.player;
        if (player2 == player) {
            if (player2 != null) {
                player2.removeListener(this.componentListener);
                oldVideoComponent = this.player.getVideoComponent();
                if (oldVideoComponent != null) {
                    oldVideoComponent.removeVideoListener(this.componentListener);
                    view = this.surfaceView;
                    if (view instanceof TextureView) {
                        oldVideoComponent.clearVideoTextureView((TextureView) view);
                    } else if (view instanceof SphericalSurfaceView) {
                        ((SphericalSurfaceView) view).setVideoComponent(null);
                    } else if (view instanceof SurfaceView) {
                        oldVideoComponent.clearVideoSurfaceView((SurfaceView) view);
                    }
                }
                oldTextComponent = this.player.getTextComponent();
                if (oldTextComponent != null) {
                    oldTextComponent.removeTextOutput(this.componentListener);
                }
            }
            this.player = player;
            if (this.useController) {
                this.controller.setPlayer(player);
            }
            subtitleView = this.subtitleView;
            if (subtitleView != null) {
                subtitleView.setCues(null);
            }
            updateBuffering();
            updateErrorMessage();
            updateForCurrentTrackSelections(true);
            if (player == null) {
                hideController();
            } else {
                oldVideoComponent = player.getVideoComponent();
                if (oldVideoComponent != null) {
                    view2 = this.surfaceView;
                    if (view2 instanceof TextureView) {
                        oldVideoComponent.setVideoTextureView((TextureView) view2);
                    } else if (view2 instanceof SphericalSurfaceView) {
                        ((SphericalSurfaceView) view2).setVideoComponent(oldVideoComponent);
                    } else if (view2 instanceof SurfaceView) {
                        oldVideoComponent.setVideoSurfaceView((SurfaceView) view2);
                    }
                    oldVideoComponent.addVideoListener(this.componentListener);
                }
                newTextComponent = player.getTextComponent();
                if (newTextComponent != null) {
                    newTextComponent.addTextOutput(this.componentListener);
                }
                player.addListener(this.componentListener);
                maybeShowController(false);
            }
        }
    }

    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        View view = this.surfaceView;
        if (view instanceof SurfaceView) {
            view.setVisibility(visibility);
        }
    }

    public void setResizeMode(int resizeMode) {
        Assertions.checkState(this.contentFrame != null);
        this.contentFrame.setResizeMode(resizeMode);
    }

    public int getResizeMode() {
        Assertions.checkState(this.contentFrame != null);
        return this.contentFrame.getResizeMode();
    }

    public boolean getUseArtwork() {
        return this.useArtwork;
    }

    public void setUseArtwork(boolean useArtwork) {
        boolean z;
        if (useArtwork) {
            if (this.artworkView == null) {
                z = false;
                Assertions.checkState(z);
                if (this.useArtwork != useArtwork) {
                    this.useArtwork = useArtwork;
                    updateForCurrentTrackSelections(false);
                }
            }
        }
        z = true;
        Assertions.checkState(z);
        if (this.useArtwork != useArtwork) {
            this.useArtwork = useArtwork;
            updateForCurrentTrackSelections(false);
        }
    }

    @Nullable
    public Drawable getDefaultArtwork() {
        return this.defaultArtwork;
    }

    @Deprecated
    public void setDefaultArtwork(@Nullable Bitmap defaultArtwork) {
        Drawable drawable;
        if (defaultArtwork == null) {
            drawable = null;
        } else {
            drawable = new BitmapDrawable(getResources(), defaultArtwork);
        }
        setDefaultArtwork(drawable);
    }

    public void setDefaultArtwork(@Nullable Drawable defaultArtwork) {
        if (this.defaultArtwork != defaultArtwork) {
            this.defaultArtwork = defaultArtwork;
            updateForCurrentTrackSelections(false);
        }
    }

    public boolean getUseController() {
        return this.useController;
    }

    public void setUseController(boolean useController) {
        boolean z;
        PlayerControlView playerControlView;
        if (useController) {
            if (this.controller == null) {
                z = false;
                Assertions.checkState(z);
                if (this.useController == useController) {
                    this.useController = useController;
                    if (useController) {
                        playerControlView = this.controller;
                        if (playerControlView != null) {
                            playerControlView.hide();
                            this.controller.setPlayer(null);
                        }
                    } else {
                        this.controller.setPlayer(this.player);
                    }
                }
            }
        }
        z = true;
        Assertions.checkState(z);
        if (this.useController == useController) {
            this.useController = useController;
            if (useController) {
                playerControlView = this.controller;
                if (playerControlView != null) {
                    playerControlView.hide();
                    this.controller.setPlayer(null);
                }
            } else {
                this.controller.setPlayer(this.player);
            }
        }
    }

    public void setShutterBackgroundColor(int color) {
        View view = this.shutterView;
        if (view != null) {
            view.setBackgroundColor(color);
        }
    }

    public void setKeepContentOnPlayerReset(boolean keepContentOnPlayerReset) {
        if (this.keepContentOnPlayerReset != keepContentOnPlayerReset) {
            this.keepContentOnPlayerReset = keepContentOnPlayerReset;
            updateForCurrentTrackSelections(false);
        }
    }

    @Deprecated
    public void setShowBuffering(boolean showBuffering) {
        setShowBuffering((int) showBuffering);
    }

    public void setShowBuffering(int showBuffering) {
        if (this.showBuffering != showBuffering) {
            this.showBuffering = showBuffering;
            updateBuffering();
        }
    }

    public void setErrorMessageProvider(@Nullable ErrorMessageProvider<? super ExoPlaybackException> errorMessageProvider) {
        if (this.errorMessageProvider != errorMessageProvider) {
            this.errorMessageProvider = errorMessageProvider;
            updateErrorMessage();
        }
    }

    public void setCustomErrorMessage(@Nullable CharSequence message) {
        Assertions.checkState(this.errorMessageView != null);
        this.customErrorMessage = message;
        updateErrorMessage();
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        Player player = this.player;
        if (player == null || !player.isPlayingAd()) {
            boolean handled = false;
            boolean isDpadWhenControlHidden = isDpadKey(event.getKeyCode()) && this.useController && !this.controller.isVisible();
            if (!isDpadWhenControlHidden) {
                if (!dispatchMediaKeyEvent(event)) {
                    if (!super.dispatchKeyEvent(event)) {
                        if (handled) {
                            maybeShowController(true);
                        }
                        return handled;
                    }
                }
            }
            handled = true;
            if (handled) {
                maybeShowController(true);
            }
            return handled;
        }
        this.overlayFrameLayout.requestFocus();
        return super.dispatchKeyEvent(event);
    }

    public boolean dispatchMediaKeyEvent(KeyEvent event) {
        return this.useController && this.controller.dispatchMediaKeyEvent(event);
    }

    public boolean isControllerVisible() {
        PlayerControlView playerControlView = this.controller;
        return playerControlView != null && playerControlView.isVisible();
    }

    public void showController() {
        showController(shouldShowControllerIndefinitely());
    }

    public void hideController() {
        PlayerControlView playerControlView = this.controller;
        if (playerControlView != null) {
            playerControlView.hide();
        }
    }

    public int getControllerShowTimeoutMs() {
        return this.controllerShowTimeoutMs;
    }

    public void setControllerShowTimeoutMs(int controllerShowTimeoutMs) {
        Assertions.checkState(this.controller != null);
        this.controllerShowTimeoutMs = controllerShowTimeoutMs;
        if (this.controller.isVisible()) {
            showController();
        }
    }

    public boolean getControllerHideOnTouch() {
        return this.controllerHideOnTouch;
    }

    public void setControllerHideOnTouch(boolean controllerHideOnTouch) {
        Assertions.checkState(this.controller != null);
        this.controllerHideOnTouch = controllerHideOnTouch;
    }

    public boolean getControllerAutoShow() {
        return this.controllerAutoShow;
    }

    public void setControllerAutoShow(boolean controllerAutoShow) {
        this.controllerAutoShow = controllerAutoShow;
    }

    public void setControllerHideDuringAds(boolean controllerHideDuringAds) {
        this.controllerHideDuringAds = controllerHideDuringAds;
    }

    public void setControllerVisibilityListener(VisibilityListener listener) {
        Assertions.checkState(this.controller != null);
        this.controller.setVisibilityListener(listener);
    }

    public void setPlaybackPreparer(@Nullable PlaybackPreparer playbackPreparer) {
        Assertions.checkState(this.controller != null);
        this.controller.setPlaybackPreparer(playbackPreparer);
    }

    public void setControlDispatcher(@Nullable ControlDispatcher controlDispatcher) {
        Assertions.checkState(this.controller != null);
        this.controller.setControlDispatcher(controlDispatcher);
    }

    public void setRewindIncrementMs(int rewindMs) {
        Assertions.checkState(this.controller != null);
        this.controller.setRewindIncrementMs(rewindMs);
    }

    public void setFastForwardIncrementMs(int fastForwardMs) {
        Assertions.checkState(this.controller != null);
        this.controller.setFastForwardIncrementMs(fastForwardMs);
    }

    public void setRepeatToggleModes(int repeatToggleModes) {
        Assertions.checkState(this.controller != null);
        this.controller.setRepeatToggleModes(repeatToggleModes);
    }

    public void setShowShuffleButton(boolean showShuffleButton) {
        Assertions.checkState(this.controller != null);
        this.controller.setShowShuffleButton(showShuffleButton);
    }

    public void setShowMultiWindowTimeBar(boolean showMultiWindowTimeBar) {
        Assertions.checkState(this.controller != null);
        this.controller.setShowMultiWindowTimeBar(showMultiWindowTimeBar);
    }

    public void setExtraAdGroupMarkers(@Nullable long[] extraAdGroupTimesMs, @Nullable boolean[] extraPlayedAdGroups) {
        Assertions.checkState(this.controller != null);
        this.controller.setExtraAdGroupMarkers(extraAdGroupTimesMs, extraPlayedAdGroups);
    }

    public void setAspectRatioListener(AspectRatioListener listener) {
        Assertions.checkState(this.contentFrame != null);
        this.contentFrame.setAspectRatioListener(listener);
    }

    public View getVideoSurfaceView() {
        return this.surfaceView;
    }

    public FrameLayout getOverlayFrameLayout() {
        return this.overlayFrameLayout;
    }

    public SubtitleView getSubtitleView() {
        return this.subtitleView;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getActionMasked() != 0) {
            return false;
        }
        return toggleControllerVisibility();
    }

    public boolean onTrackballEvent(MotionEvent ev) {
        if (this.useController) {
            if (this.player != null) {
                maybeShowController(true);
                return true;
            }
        }
        return false;
    }

    public void onResume() {
        View view = this.surfaceView;
        if (view instanceof SphericalSurfaceView) {
            ((SphericalSurfaceView) view).onResume();
        }
    }

    public void onPause() {
        View view = this.surfaceView;
        if (view instanceof SphericalSurfaceView) {
            ((SphericalSurfaceView) view).onPause();
        }
    }

    protected void onContentAspectRatioChanged(float contentAspectRatio, @Nullable AspectRatioFrameLayout contentFrame, @Nullable View contentView) {
        if (contentFrame != null) {
            contentFrame.setAspectRatio(contentView instanceof SphericalSurfaceView ? 0.0f : contentAspectRatio);
        }
    }

    private boolean toggleControllerVisibility() {
        if (this.useController) {
            if (this.player != null) {
                if (!this.controller.isVisible()) {
                    maybeShowController(true);
                } else if (this.controllerHideOnTouch) {
                    this.controller.hide();
                }
                return true;
            }
        }
        return false;
    }

    private void maybeShowController(boolean isForced) {
        if (!isPlayingAd() || !this.controllerHideDuringAds) {
            if (this.useController) {
                boolean wasShowingIndefinitely = this.controller.isVisible() && this.controller.getShowTimeoutMs() <= 0;
                boolean shouldShowIndefinitely = shouldShowControllerIndefinitely();
                if (!(isForced || wasShowingIndefinitely)) {
                    if (shouldShowIndefinitely) {
                    }
                }
                showController(shouldShowIndefinitely);
            }
        }
    }

    private boolean shouldShowControllerIndefinitely() {
        int playbackState = this.player;
        boolean z = true;
        if (playbackState == 0) {
            return true;
        }
        playbackState = playbackState.getPlaybackState();
        if (this.controllerAutoShow) {
            if (playbackState != 1 && playbackState != 4) {
                if (this.player.getPlayWhenReady()) {
                }
            }
            return z;
        }
        z = false;
        return z;
    }

    private void showController(boolean showIndefinitely) {
        if (this.useController) {
            this.controller.setShowTimeoutMs(showIndefinitely ? 0 : this.controllerShowTimeoutMs);
            this.controller.show();
        }
    }

    private boolean isPlayingAd() {
        Player player = this.player;
        return player != null && player.isPlayingAd() && this.player.getPlayWhenReady();
    }

    private void updateForCurrentTrackSelections(boolean isNewPlayer) {
        Player player = this.player;
        if (player != null) {
            if (!player.getCurrentTrackGroups().isEmpty()) {
                if (isNewPlayer && !this.keepContentOnPlayerReset) {
                    closeShutter();
                }
                TrackSelectionArray selections = this.player.getCurrentTrackSelections();
                int i = 0;
                while (i < selections.length) {
                    if (this.player.getRendererType(i) != 2 || selections.get(i) == null) {
                        i++;
                    } else {
                        hideArtwork();
                        return;
                    }
                }
                closeShutter();
                if (this.useArtwork) {
                    for (i = 0; i < selections.length; i++) {
                        TrackSelection selection = selections.get(i);
                        if (selection != null) {
                            int j = 0;
                            while (j < selection.length()) {
                                Metadata metadata = selection.getFormat(j).metadata;
                                if (metadata == null || !setArtworkFromMetadata(metadata)) {
                                    j++;
                                } else {
                                    return;
                                }
                            }
                        }
                    }
                    if (setDrawableArtwork(this.defaultArtwork)) {
                        return;
                    }
                }
                hideArtwork();
                return;
            }
        }
        if (!this.keepContentOnPlayerReset) {
            hideArtwork();
            closeShutter();
        }
    }

    private boolean setArtworkFromMetadata(Metadata metadata) {
        for (int i = 0; i < metadata.length(); i++) {
            Entry metadataEntry = metadata.get(i);
            if (metadataEntry instanceof ApicFrame) {
                byte[] bitmapData = ((ApicFrame) metadataEntry).pictureData;
                return setDrawableArtwork(new BitmapDrawable(getResources(), BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length)));
            }
        }
        return false;
    }

    private boolean setDrawableArtwork(@Nullable Drawable drawable) {
        if (drawable != null) {
            int drawableWidth = drawable.getIntrinsicWidth();
            int drawableHeight = drawable.getIntrinsicHeight();
            if (drawableWidth > 0 && drawableHeight > 0) {
                onContentAspectRatioChanged(((float) drawableWidth) / ((float) drawableHeight), this.contentFrame, this.artworkView);
                this.artworkView.setImageDrawable(drawable);
                this.artworkView.setVisibility(0);
                return true;
            }
        }
        return false;
    }

    private void hideArtwork() {
        ImageView imageView = this.artworkView;
        if (imageView != null) {
            imageView.setImageResource(17170445);
            this.artworkView.setVisibility(4);
        }
    }

    private void closeShutter() {
        View view = this.shutterView;
        if (view != null) {
            view.setVisibility(0);
        }
    }

    private void updateBuffering() {
        if (this.bufferingView != null) {
            boolean showBufferingSpinner;
            View view;
            Player player = this.player;
            boolean z = true;
            int i = 0;
            if (player != null) {
                if (player.getPlaybackState() == 2) {
                    int i2 = this.showBuffering;
                    if (i2 != 2) {
                        if (i2 == 1) {
                            if (this.player.getPlayWhenReady()) {
                            }
                        }
                    }
                    showBufferingSpinner = z;
                    view = this.bufferingView;
                    if (showBufferingSpinner) {
                        i = 8;
                    }
                    view.setVisibility(i);
                }
            }
            z = false;
            showBufferingSpinner = z;
            view = this.bufferingView;
            if (showBufferingSpinner) {
                i = 8;
            }
            view.setVisibility(i);
        }
    }

    private void updateErrorMessage() {
        TextView textView = this.errorMessageView;
        if (textView != null) {
            CharSequence charSequence = this.customErrorMessage;
            if (charSequence != null) {
                textView.setText(charSequence);
                this.errorMessageView.setVisibility(0);
                return;
            }
            ExoPlaybackException error = null;
            Player player = this.player;
            if (player != null) {
                if (player.getPlaybackState() == 1 && this.errorMessageProvider != null) {
                    error = this.player.getPlaybackError();
                }
            }
            if (error != null) {
                this.errorMessageView.setText(this.errorMessageProvider.getErrorMessage(error).second);
                this.errorMessageView.setVisibility(0);
            } else {
                this.errorMessageView.setVisibility(8);
            }
        }
    }

    @TargetApi(23)
    private static void configureEditModeLogoV23(Resources resources, ImageView logo) {
        logo.setImageDrawable(resources.getDrawable(C0649R.drawable.exo_edit_mode_logo, null));
        logo.setBackgroundColor(resources.getColor(C0649R.color.exo_edit_mode_background_color, null));
    }

    private static void configureEditModeLogo(Resources resources, ImageView logo) {
        logo.setImageDrawable(resources.getDrawable(C0649R.drawable.exo_edit_mode_logo));
        logo.setBackgroundColor(resources.getColor(C0649R.color.exo_edit_mode_background_color));
    }

    private static void setResizeModeRaw(AspectRatioFrameLayout aspectRatioFrame, int resizeMode) {
        aspectRatioFrame.setResizeMode(resizeMode);
    }

    private static void applyTextureViewRotation(TextureView textureView, int textureViewRotation) {
        float textureViewWidth = (float) textureView.getWidth();
        float textureViewHeight = (float) textureView.getHeight();
        if (!(textureViewWidth == 0.0f || textureViewHeight == 0.0f)) {
            if (textureViewRotation != 0) {
                Matrix transformMatrix = new Matrix();
                float pivotX = textureViewWidth / 2.0f;
                float pivotY = textureViewHeight / 2.0f;
                transformMatrix.postRotate((float) textureViewRotation, pivotX, pivotY);
                RectF originalTextureRect = new RectF(0.0f, 0.0f, textureViewWidth, textureViewHeight);
                RectF rotatedTextureRect = new RectF();
                transformMatrix.mapRect(rotatedTextureRect, originalTextureRect);
                transformMatrix.postScale(textureViewWidth / rotatedTextureRect.width(), textureViewHeight / rotatedTextureRect.height(), pivotX, pivotY);
                textureView.setTransform(transformMatrix);
                return;
            }
        }
        textureView.setTransform(null);
    }

    @SuppressLint({"InlinedApi"})
    private boolean isDpadKey(int keyCode) {
        if (!(keyCode == 19 || keyCode == 270 || keyCode == 22 || keyCode == 271 || keyCode == 20 || keyCode == 269 || keyCode == 21 || keyCode == 268)) {
            if (keyCode != 23) {
                return false;
            }
        }
        return true;
    }
}
