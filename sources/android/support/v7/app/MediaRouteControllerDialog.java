package android.support.v7.app;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaControllerCompat.Callback;
import android.support.v4.media.session.MediaSessionCompat.Token;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.util.ObjectsCompat;
import android.support.v7.app.OverlayListView.OverlayObject;
import android.support.v7.app.OverlayListView.OverlayObject.OnAnimationEndListener;
import android.support.v7.graphics.Palette;
import android.support.v7.graphics.Palette.Builder;
import android.support.v7.graphics.Palette.Swatch;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.support.v7.media.MediaRouter.RouteGroup;
import android.support.v7.media.MediaRouter.RouteInfo;
import android.support.v7.mediarouter.C0299R;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import de.danoeh.antennapod.core.syndication.namespace.NSContent;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class MediaRouteControllerDialog extends AlertDialog {
    static final int BUTTON_DISCONNECT_RES_ID = 16908314;
    private static final int BUTTON_NEUTRAL_RES_ID = 16908315;
    static final int BUTTON_STOP_RES_ID = 16908313;
    static final int CONNECTION_TIMEOUT_MILLIS = ((int) TimeUnit.SECONDS.toMillis(30));
    static final boolean DEBUG = Log.isLoggable(TAG, 3);
    static final String TAG = "MediaRouteCtrlDialog";
    static final int VOLUME_UPDATE_DELAY_MILLIS = 500;
    private Interpolator mAccelerateDecelerateInterpolator;
    final AccessibilityManager mAccessibilityManager;
    int mArtIconBackgroundColor;
    Bitmap mArtIconBitmap;
    boolean mArtIconIsLoaded;
    Bitmap mArtIconLoadedBitmap;
    Uri mArtIconUri;
    private ImageView mArtView;
    private boolean mAttachedToWindow;
    private final MediaRouterCallback mCallback;
    private ImageButton mCloseButton;
    Context mContext;
    MediaControllerCallback mControllerCallback;
    private boolean mCreated;
    private FrameLayout mCustomControlLayout;
    private View mCustomControlView;
    FrameLayout mDefaultControlLayout;
    MediaDescriptionCompat mDescription;
    private LinearLayout mDialogAreaLayout;
    private int mDialogContentWidth;
    private Button mDisconnectButton;
    private View mDividerView;
    private FrameLayout mExpandableAreaLayout;
    private Interpolator mFastOutSlowInInterpolator;
    FetchArtTask mFetchArtTask;
    private MediaRouteExpandCollapseButton mGroupExpandCollapseButton;
    int mGroupListAnimationDurationMs;
    Runnable mGroupListFadeInAnimation;
    private int mGroupListFadeInDurationMs;
    private int mGroupListFadeOutDurationMs;
    private List<RouteInfo> mGroupMemberRoutes;
    Set<RouteInfo> mGroupMemberRoutesAdded;
    Set<RouteInfo> mGroupMemberRoutesAnimatingWithBitmap;
    private Set<RouteInfo> mGroupMemberRoutesRemoved;
    boolean mHasPendingUpdate;
    private Interpolator mInterpolator;
    boolean mIsGroupExpanded;
    boolean mIsGroupListAnimating;
    boolean mIsGroupListAnimationPending;
    private Interpolator mLinearOutSlowInInterpolator;
    MediaControllerCompat mMediaController;
    private LinearLayout mMediaMainControlLayout;
    boolean mPendingUpdateAnimationNeeded;
    private ImageButton mPlaybackControlButton;
    private RelativeLayout mPlaybackControlLayout;
    final RouteInfo mRoute;
    RouteInfo mRouteInVolumeSliderTouched;
    private TextView mRouteNameTextView;
    final MediaRouter mRouter;
    PlaybackStateCompat mState;
    private Button mStopCastingButton;
    private TextView mSubtitleView;
    private TextView mTitleView;
    VolumeChangeListener mVolumeChangeListener;
    private boolean mVolumeControlEnabled;
    private LinearLayout mVolumeControlLayout;
    VolumeGroupAdapter mVolumeGroupAdapter;
    OverlayListView mVolumeGroupList;
    private int mVolumeGroupListItemHeight;
    private int mVolumeGroupListItemIconSize;
    private int mVolumeGroupListMaxHeight;
    private final int mVolumeGroupListPaddingTop;
    SeekBar mVolumeSlider;
    Map<RouteInfo, SeekBar> mVolumeSliderMap;

    /* renamed from: android.support.v7.app.MediaRouteControllerDialog$1 */
    class C02741 implements Runnable {
        C02741() {
        }

        public void run() {
            MediaRouteControllerDialog.this.startGroupListFadeInAnimation();
        }
    }

    /* renamed from: android.support.v7.app.MediaRouteControllerDialog$2 */
    class C02752 implements OnClickListener {
        C02752() {
        }

        public void onClick(View v) {
            MediaRouteControllerDialog.this.dismiss();
        }
    }

    /* renamed from: android.support.v7.app.MediaRouteControllerDialog$3 */
    class C02763 implements OnClickListener {
        C02763() {
        }

        public void onClick(View v) {
        }
    }

    /* renamed from: android.support.v7.app.MediaRouteControllerDialog$4 */
    class C02774 implements OnClickListener {
        C02774() {
        }

        public void onClick(View v) {
            if (MediaRouteControllerDialog.this.mMediaController != null) {
                PendingIntent pi = MediaRouteControllerDialog.this.mMediaController.getSessionActivity();
                if (pi != null) {
                    try {
                        pi.send();
                        MediaRouteControllerDialog.this.dismiss();
                    } catch (CanceledException e) {
                        String str = MediaRouteControllerDialog.TAG;
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(pi);
                        stringBuilder.append(" was not sent, it had been canceled.");
                        Log.e(str, stringBuilder.toString());
                    }
                }
            }
        }
    }

    /* renamed from: android.support.v7.app.MediaRouteControllerDialog$5 */
    class C02785 implements OnClickListener {
        C02785() {
        }

        public void onClick(View v) {
            MediaRouteControllerDialog mediaRouteControllerDialog = MediaRouteControllerDialog.this;
            mediaRouteControllerDialog.mIsGroupExpanded ^= true;
            if (MediaRouteControllerDialog.this.mIsGroupExpanded) {
                MediaRouteControllerDialog.this.mVolumeGroupList.setVisibility(0);
            }
            MediaRouteControllerDialog.this.loadInterpolator();
            MediaRouteControllerDialog.this.updateLayoutHeight(true);
        }
    }

    /* renamed from: android.support.v7.app.MediaRouteControllerDialog$9 */
    class C02829 implements AnimationListener {
        C02829() {
        }

        public void onAnimationStart(Animation animation) {
            MediaRouteControllerDialog.this.mVolumeGroupList.startAnimationAll();
            MediaRouteControllerDialog.this.mVolumeGroupList.postDelayed(MediaRouteControllerDialog.this.mGroupListFadeInAnimation, (long) MediaRouteControllerDialog.this.mGroupListAnimationDurationMs);
        }

        public void onAnimationEnd(Animation animation) {
        }

        public void onAnimationRepeat(Animation animation) {
        }
    }

    private final class ClickListener implements OnClickListener {
        ClickListener() {
        }

        public void onClick(View v) {
            int id = v.getId();
            int isPlaying = 1;
            if (id != MediaRouteControllerDialog.BUTTON_STOP_RES_ID) {
                if (id != MediaRouteControllerDialog.BUTTON_DISCONNECT_RES_ID) {
                    if (id == C0299R.id.mr_control_playback_ctrl) {
                        if (MediaRouteControllerDialog.this.mMediaController != null && MediaRouteControllerDialog.this.mState != null) {
                            boolean isPlaying2;
                            if (MediaRouteControllerDialog.this.mState.getState() != 3) {
                                isPlaying2 = false;
                            }
                            int actionDescResId = 0;
                            if (isPlaying2 && MediaRouteControllerDialog.this.isPauseActionSupported()) {
                                MediaRouteControllerDialog.this.mMediaController.getTransportControls().pause();
                                actionDescResId = C0299R.string.mr_controller_pause;
                            } else if (isPlaying2 && MediaRouteControllerDialog.this.isStopActionSupported()) {
                                MediaRouteControllerDialog.this.mMediaController.getTransportControls().stop();
                                actionDescResId = C0299R.string.mr_controller_stop;
                            } else if (!isPlaying2 && MediaRouteControllerDialog.this.isPlayActionSupported()) {
                                MediaRouteControllerDialog.this.mMediaController.getTransportControls().play();
                                actionDescResId = C0299R.string.mr_controller_play;
                            }
                            if (MediaRouteControllerDialog.this.mAccessibilityManager != null && MediaRouteControllerDialog.this.mAccessibilityManager.isEnabled() && actionDescResId != 0) {
                                AccessibilityEvent event = AccessibilityEvent.obtain(16384);
                                event.setPackageName(MediaRouteControllerDialog.this.mContext.getPackageName());
                                event.setClassName(getClass().getName());
                                event.getText().add(MediaRouteControllerDialog.this.mContext.getString(actionDescResId));
                                MediaRouteControllerDialog.this.mAccessibilityManager.sendAccessibilityEvent(event);
                            }
                            return;
                        }
                        return;
                    } else if (id == C0299R.id.mr_close) {
                        MediaRouteControllerDialog.this.dismiss();
                        return;
                    } else {
                        return;
                    }
                }
            }
            if (MediaRouteControllerDialog.this.mRoute.isSelected()) {
                MediaRouter mediaRouter = MediaRouteControllerDialog.this.mRouter;
                if (id == MediaRouteControllerDialog.BUTTON_STOP_RES_ID) {
                    isPlaying = 2;
                }
                mediaRouter.unselect(isPlaying);
            }
            MediaRouteControllerDialog.this.dismiss();
        }
    }

    private class FetchArtTask extends AsyncTask<Void, Void, Bitmap> {
        private static final long SHOW_ANIM_TIME_THRESHOLD_MILLIS = 120;
        private int mBackgroundColor;
        private final Bitmap mIconBitmap;
        private final Uri mIconUri;
        private long mStartTimeMillis;

        FetchArtTask() {
            Uri uri = null;
            Bitmap bitmap = MediaRouteControllerDialog.this.mDescription == null ? null : MediaRouteControllerDialog.this.mDescription.getIconBitmap();
            if (MediaRouteControllerDialog.this.isBitmapRecycled(bitmap)) {
                Log.w(MediaRouteControllerDialog.TAG, "Can't fetch the given art bitmap because it's already recycled.");
                bitmap = null;
            }
            this.mIconBitmap = bitmap;
            if (MediaRouteControllerDialog.this.mDescription != null) {
                uri = MediaRouteControllerDialog.this.mDescription.getIconUri();
            }
            this.mIconUri = uri;
        }

        public Bitmap getIconBitmap() {
            return this.mIconBitmap;
        }

        public Uri getIconUri() {
            return this.mIconUri;
        }

        protected void onPreExecute() {
            this.mStartTimeMillis = SystemClock.uptimeMillis();
            MediaRouteControllerDialog.this.clearLoadedBitmap();
        }

        protected Bitmap doInBackground(Void... arg) {
            Bitmap art = null;
            int i = 0;
            if (this.mIconBitmap != null) {
                art = this.mIconBitmap;
            } else {
                Uri uri = this.mIconUri;
                if (uri != null) {
                    InputStream stream = null;
                    try {
                        InputStream openInputStreamByScheme = openInputStreamByScheme(uri);
                        stream = openInputStreamByScheme;
                        if (openInputStreamByScheme == null) {
                            String str = MediaRouteControllerDialog.TAG;
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("Unable to open: ");
                            stringBuilder.append(this.mIconUri);
                            Log.w(str, stringBuilder.toString());
                            if (stream != null) {
                                try {
                                    stream.close();
                                } catch (IOException e) {
                                }
                            }
                            return null;
                        }
                        Options options = new Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeStream(stream, null, options);
                        if (options.outWidth != 0) {
                            if (options.outHeight != 0) {
                                try {
                                    stream.reset();
                                } catch (IOException e2) {
                                    stream.close();
                                    InputStream openInputStreamByScheme2 = openInputStreamByScheme(this.mIconUri);
                                    stream = openInputStreamByScheme2;
                                    if (openInputStreamByScheme2 == null) {
                                        String str2 = MediaRouteControllerDialog.TAG;
                                        StringBuilder stringBuilder2 = new StringBuilder();
                                        stringBuilder2.append("Unable to open: ");
                                        stringBuilder2.append(this.mIconUri);
                                        Log.w(str2, stringBuilder2.toString());
                                        if (stream != null) {
                                            try {
                                                stream.close();
                                            } catch (IOException e3) {
                                            }
                                        }
                                        return null;
                                    }
                                }
                                options.inJustDecodeBounds = false;
                                options.inSampleSize = Math.max(1, Integer.highestOneBit(options.outHeight / MediaRouteControllerDialog.this.getDesiredArtHeight(options.outWidth, options.outHeight)));
                                if (isCancelled()) {
                                    if (stream != null) {
                                        try {
                                            stream.close();
                                        } catch (IOException e4) {
                                        }
                                    }
                                    return null;
                                }
                                art = BitmapFactory.decodeStream(stream, null, options);
                                if (stream != null) {
                                    try {
                                        stream.close();
                                    } catch (IOException e5) {
                                    }
                                }
                            }
                        }
                        if (stream != null) {
                            try {
                                stream.close();
                            } catch (IOException e6) {
                            }
                        }
                        return null;
                    } catch (IOException e7) {
                        String str3 = MediaRouteControllerDialog.TAG;
                        StringBuilder stringBuilder3 = new StringBuilder();
                        stringBuilder3.append("Unable to open: ");
                        stringBuilder3.append(this.mIconUri);
                        Log.w(str3, stringBuilder3.toString(), e7);
                        if (stream != null) {
                            stream.close();
                        }
                    } catch (Throwable th) {
                        if (stream != null) {
                            try {
                                stream.close();
                            } catch (IOException e8) {
                            }
                        }
                    }
                }
            }
            if (MediaRouteControllerDialog.this.isBitmapRecycled(art)) {
                str = MediaRouteControllerDialog.TAG;
                StringBuilder stringBuilder4 = new StringBuilder();
                stringBuilder4.append("Can't use recycled bitmap: ");
                stringBuilder4.append(art);
                Log.w(str, stringBuilder4.toString());
                return null;
            }
            if (art != null && art.getWidth() < art.getHeight()) {
                Palette palette = new Builder(art).maximumColorCount(1).generate();
                if (!palette.getSwatches().isEmpty()) {
                    i = ((Swatch) palette.getSwatches().get(0)).getRgb();
                }
                this.mBackgroundColor = i;
            }
            return art;
        }

        protected void onPostExecute(Bitmap art) {
            MediaRouteControllerDialog mediaRouteControllerDialog = MediaRouteControllerDialog.this;
            mediaRouteControllerDialog.mFetchArtTask = null;
            if (ObjectsCompat.equals(mediaRouteControllerDialog.mArtIconBitmap, this.mIconBitmap)) {
                if (ObjectsCompat.equals(MediaRouteControllerDialog.this.mArtIconUri, this.mIconUri)) {
                    return;
                }
            }
            mediaRouteControllerDialog = MediaRouteControllerDialog.this;
            mediaRouteControllerDialog.mArtIconBitmap = this.mIconBitmap;
            mediaRouteControllerDialog.mArtIconLoadedBitmap = art;
            mediaRouteControllerDialog.mArtIconUri = this.mIconUri;
            mediaRouteControllerDialog.mArtIconBackgroundColor = this.mBackgroundColor;
            boolean z = true;
            mediaRouteControllerDialog.mArtIconIsLoaded = true;
            long elapsedTimeMillis = SystemClock.uptimeMillis() - this.mStartTimeMillis;
            mediaRouteControllerDialog = MediaRouteControllerDialog.this;
            if (elapsedTimeMillis <= SHOW_ANIM_TIME_THRESHOLD_MILLIS) {
                z = false;
            }
            mediaRouteControllerDialog.update(z);
        }

        private InputStream openInputStreamByScheme(Uri uri) throws IOException {
            InputStream stream;
            String scheme = uri.getScheme().toLowerCase();
            if (!"android.resource".equals(scheme)) {
                if (!NSContent.NSTAG.equals(scheme)) {
                    if (!"file".equals(scheme)) {
                        URLConnection conn = new URL(uri.toString()).openConnection();
                        conn.setConnectTimeout(MediaRouteControllerDialog.CONNECTION_TIMEOUT_MILLIS);
                        conn.setReadTimeout(MediaRouteControllerDialog.CONNECTION_TIMEOUT_MILLIS);
                        stream = conn.getInputStream();
                        return stream != null ? null : new BufferedInputStream(stream);
                    }
                }
            }
            stream = MediaRouteControllerDialog.this.mContext.getContentResolver().openInputStream(uri);
            if (stream != null) {
            }
        }
    }

    private class VolumeChangeListener implements OnSeekBarChangeListener {
        private final Runnable mStopTrackingTouch = new C02831();

        /* renamed from: android.support.v7.app.MediaRouteControllerDialog$VolumeChangeListener$1 */
        class C02831 implements Runnable {
            C02831() {
            }

            public void run() {
                if (MediaRouteControllerDialog.this.mRouteInVolumeSliderTouched != null) {
                    MediaRouteControllerDialog.this.mRouteInVolumeSliderTouched = null;
                    if (MediaRouteControllerDialog.this.mHasPendingUpdate) {
                        MediaRouteControllerDialog.this.update(MediaRouteControllerDialog.this.mPendingUpdateAnimationNeeded);
                    }
                }
            }
        }

        VolumeChangeListener() {
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            if (MediaRouteControllerDialog.this.mRouteInVolumeSliderTouched != null) {
                MediaRouteControllerDialog.this.mVolumeSlider.removeCallbacks(this.mStopTrackingTouch);
            }
            MediaRouteControllerDialog.this.mRouteInVolumeSliderTouched = (RouteInfo) seekBar.getTag();
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            MediaRouteControllerDialog.this.mVolumeSlider.postDelayed(this.mStopTrackingTouch, 500);
        }

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                RouteInfo route = (RouteInfo) seekBar.getTag();
                if (MediaRouteControllerDialog.DEBUG) {
                    String str = MediaRouteControllerDialog.TAG;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("onProgressChanged(): calling MediaRouter.RouteInfo.requestSetVolume(");
                    stringBuilder.append(progress);
                    stringBuilder.append(")");
                    Log.d(str, stringBuilder.toString());
                }
                route.requestSetVolume(progress);
            }
        }
    }

    private class VolumeGroupAdapter extends ArrayAdapter<RouteInfo> {
        final float mDisabledAlpha;

        public VolumeGroupAdapter(Context context, List<RouteInfo> objects) {
            super(context, null, objects);
            this.mDisabledAlpha = MediaRouterThemeHelper.getDisabledAlpha(context);
        }

        public boolean isEnabled(int position) {
            return false;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            int i = 0;
            if (v == null) {
                v = LayoutInflater.from(parent.getContext()).inflate(C0299R.layout.mr_controller_volume_item, parent, false);
            } else {
                MediaRouteControllerDialog.this.updateVolumeGroupItemHeight(v);
            }
            RouteInfo route = (RouteInfo) getItem(position);
            if (route != null) {
                boolean isEnabled = route.isEnabled();
                TextView routeName = (TextView) v.findViewById(C0299R.id.mr_name);
                routeName.setEnabled(isEnabled);
                routeName.setText(route.getName());
                MediaRouteVolumeSlider volumeSlider = (MediaRouteVolumeSlider) v.findViewById(C0299R.id.mr_volume_slider);
                MediaRouterThemeHelper.setVolumeSliderColor(parent.getContext(), volumeSlider, MediaRouteControllerDialog.this.mVolumeGroupList);
                volumeSlider.setTag(route);
                MediaRouteControllerDialog.this.mVolumeSliderMap.put(route, volumeSlider);
                volumeSlider.setHideThumb(isEnabled ^ 1);
                volumeSlider.setEnabled(isEnabled);
                if (isEnabled) {
                    if (MediaRouteControllerDialog.this.isVolumeControlAvailable(route)) {
                        volumeSlider.setMax(route.getVolumeMax());
                        volumeSlider.setProgress(route.getVolume());
                        volumeSlider.setOnSeekBarChangeListener(MediaRouteControllerDialog.this.mVolumeChangeListener);
                    } else {
                        volumeSlider.setMax(100);
                        volumeSlider.setProgress(100);
                        volumeSlider.setEnabled(false);
                    }
                }
                ((ImageView) v.findViewById(C0299R.id.mr_volume_item_icon)).setAlpha(isEnabled ? 255 : (int) (this.mDisabledAlpha * 255.0f));
                LinearLayout container = (LinearLayout) v.findViewById(C0299R.id.volume_item_container);
                if (MediaRouteControllerDialog.this.mGroupMemberRoutesAnimatingWithBitmap.contains(route)) {
                    i = 4;
                }
                container.setVisibility(i);
                if (MediaRouteControllerDialog.this.mGroupMemberRoutesAdded != null && MediaRouteControllerDialog.this.mGroupMemberRoutesAdded.contains(route)) {
                    Animation alphaAnim = new AlphaAnimation(0.0f, 0.0f);
                    alphaAnim.setDuration(0);
                    alphaAnim.setFillEnabled(true);
                    alphaAnim.setFillAfter(true);
                    v.clearAnimation();
                    v.startAnimation(alphaAnim);
                }
            }
            return v;
        }
    }

    private final class MediaControllerCallback extends Callback {
        MediaControllerCallback() {
        }

        public void onSessionDestroyed() {
            if (MediaRouteControllerDialog.this.mMediaController != null) {
                MediaRouteControllerDialog.this.mMediaController.unregisterCallback(MediaRouteControllerDialog.this.mControllerCallback);
                MediaRouteControllerDialog.this.mMediaController = null;
            }
        }

        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            MediaRouteControllerDialog mediaRouteControllerDialog = MediaRouteControllerDialog.this;
            mediaRouteControllerDialog.mState = state;
            mediaRouteControllerDialog.update(false);
        }

        public void onMetadataChanged(MediaMetadataCompat metadata) {
            MediaRouteControllerDialog.this.mDescription = metadata == null ? null : metadata.getDescription();
            MediaRouteControllerDialog.this.updateArtIconIfNeeded();
            MediaRouteControllerDialog.this.update(false);
        }
    }

    private final class MediaRouterCallback extends MediaRouter.Callback {
        MediaRouterCallback() {
        }

        public void onRouteUnselected(MediaRouter router, RouteInfo route) {
            MediaRouteControllerDialog.this.update(false);
        }

        public void onRouteChanged(MediaRouter router, RouteInfo route) {
            MediaRouteControllerDialog.this.update(true);
        }

        public void onRouteVolumeChanged(MediaRouter router, RouteInfo route) {
            SeekBar volumeSlider = (SeekBar) MediaRouteControllerDialog.this.mVolumeSliderMap.get(route);
            int volume = route.getVolume();
            if (MediaRouteControllerDialog.DEBUG) {
                String str = MediaRouteControllerDialog.TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("onRouteVolumeChanged(), route.getVolume:");
                stringBuilder.append(volume);
                Log.d(str, stringBuilder.toString());
            }
            if (volumeSlider != null && MediaRouteControllerDialog.this.mRouteInVolumeSliderTouched != route) {
                volumeSlider.setProgress(volume);
            }
        }
    }

    public MediaRouteControllerDialog(Context context) {
        this(context, 0);
    }

    public MediaRouteControllerDialog(Context context, int theme) {
        Context createThemedDialogContext = MediaRouterThemeHelper.createThemedDialogContext(context, theme, true);
        context = createThemedDialogContext;
        super(createThemedDialogContext, MediaRouterThemeHelper.createThemedDialogStyle(context));
        this.mVolumeControlEnabled = true;
        this.mGroupListFadeInAnimation = new C02741();
        this.mContext = getContext();
        this.mControllerCallback = new MediaControllerCallback();
        this.mRouter = MediaRouter.getInstance(this.mContext);
        this.mCallback = new MediaRouterCallback();
        this.mRoute = this.mRouter.getSelectedRoute();
        setMediaSession(this.mRouter.getMediaSessionToken());
        this.mVolumeGroupListPaddingTop = this.mContext.getResources().getDimensionPixelSize(C0299R.dimen.mr_controller_volume_group_list_padding_top);
        this.mAccessibilityManager = (AccessibilityManager) this.mContext.getSystemService("accessibility");
        if (VERSION.SDK_INT >= 21) {
            this.mLinearOutSlowInInterpolator = AnimationUtils.loadInterpolator(context, C0299R.interpolator.mr_linear_out_slow_in);
            this.mFastOutSlowInInterpolator = AnimationUtils.loadInterpolator(context, C0299R.interpolator.mr_fast_out_slow_in);
        }
        this.mAccelerateDecelerateInterpolator = new AccelerateDecelerateInterpolator();
    }

    public RouteInfo getRoute() {
        return this.mRoute;
    }

    private RouteGroup getGroup() {
        RouteInfo routeInfo = this.mRoute;
        if (routeInfo instanceof RouteGroup) {
            return (RouteGroup) routeInfo;
        }
        return null;
    }

    public View onCreateMediaControlView(Bundle savedInstanceState) {
        return null;
    }

    public View getMediaControlView() {
        return this.mCustomControlView;
    }

    public void setVolumeControlEnabled(boolean enable) {
        if (this.mVolumeControlEnabled != enable) {
            this.mVolumeControlEnabled = enable;
            if (this.mCreated) {
                update(false);
            }
        }
    }

    public boolean isVolumeControlEnabled() {
        return this.mVolumeControlEnabled;
    }

    private void setMediaSession(Token sessionToken) {
        MediaControllerCompat mediaControllerCompat = this.mMediaController;
        PlaybackStateCompat playbackStateCompat = null;
        if (mediaControllerCompat != null) {
            mediaControllerCompat.unregisterCallback(this.mControllerCallback);
            this.mMediaController = null;
        }
        if (sessionToken != null && this.mAttachedToWindow) {
            MediaMetadataCompat metadata;
            try {
                this.mMediaController = new MediaControllerCompat(this.mContext, sessionToken);
            } catch (RemoteException e) {
                Log.e(TAG, "Error creating media controller in setMediaSession.", e);
            }
            mediaControllerCompat = this.mMediaController;
            if (mediaControllerCompat != null) {
                mediaControllerCompat.registerCallback(this.mControllerCallback);
            }
            mediaControllerCompat = this.mMediaController;
            if (mediaControllerCompat == null) {
                metadata = null;
            } else {
                metadata = mediaControllerCompat.getMetadata();
            }
            this.mDescription = metadata == null ? null : metadata.getDescription();
            MediaControllerCompat mediaControllerCompat2 = this.mMediaController;
            if (mediaControllerCompat2 != null) {
                playbackStateCompat = mediaControllerCompat2.getPlaybackState();
            }
            this.mState = playbackStateCompat;
            updateArtIconIfNeeded();
            update(false);
        }
    }

    public Token getMediaSession() {
        MediaControllerCompat mediaControllerCompat = this.mMediaController;
        return mediaControllerCompat == null ? null : mediaControllerCompat.getSessionToken();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(17170445);
        setContentView(C0299R.layout.mr_controller_material_dialog_b);
        findViewById(BUTTON_NEUTRAL_RES_ID).setVisibility(8);
        ClickListener listener = new ClickListener();
        this.mExpandableAreaLayout = (FrameLayout) findViewById(C0299R.id.mr_expandable_area);
        this.mExpandableAreaLayout.setOnClickListener(new C02752());
        this.mDialogAreaLayout = (LinearLayout) findViewById(C0299R.id.mr_dialog_area);
        this.mDialogAreaLayout.setOnClickListener(new C02763());
        int color = MediaRouterThemeHelper.getButtonTextColor(this.mContext);
        this.mDisconnectButton = (Button) findViewById(BUTTON_DISCONNECT_RES_ID);
        this.mDisconnectButton.setText(C0299R.string.mr_controller_disconnect);
        this.mDisconnectButton.setTextColor(color);
        this.mDisconnectButton.setOnClickListener(listener);
        this.mStopCastingButton = (Button) findViewById(BUTTON_STOP_RES_ID);
        this.mStopCastingButton.setText(C0299R.string.mr_controller_stop_casting);
        this.mStopCastingButton.setTextColor(color);
        this.mStopCastingButton.setOnClickListener(listener);
        this.mRouteNameTextView = (TextView) findViewById(C0299R.id.mr_name);
        this.mCloseButton = (ImageButton) findViewById(C0299R.id.mr_close);
        this.mCloseButton.setOnClickListener(listener);
        this.mCustomControlLayout = (FrameLayout) findViewById(C0299R.id.mr_custom_control);
        this.mDefaultControlLayout = (FrameLayout) findViewById(C0299R.id.mr_default_control);
        OnClickListener onClickListener = new C02774();
        this.mArtView = (ImageView) findViewById(C0299R.id.mr_art);
        this.mArtView.setOnClickListener(onClickListener);
        findViewById(C0299R.id.mr_control_title_container).setOnClickListener(onClickListener);
        this.mMediaMainControlLayout = (LinearLayout) findViewById(C0299R.id.mr_media_main_control);
        this.mDividerView = findViewById(C0299R.id.mr_control_divider);
        this.mPlaybackControlLayout = (RelativeLayout) findViewById(C0299R.id.mr_playback_control);
        this.mTitleView = (TextView) findViewById(C0299R.id.mr_control_title);
        this.mSubtitleView = (TextView) findViewById(C0299R.id.mr_control_subtitle);
        this.mPlaybackControlButton = (ImageButton) findViewById(C0299R.id.mr_control_playback_ctrl);
        this.mPlaybackControlButton.setOnClickListener(listener);
        this.mVolumeControlLayout = (LinearLayout) findViewById(C0299R.id.mr_volume_control);
        this.mVolumeControlLayout.setVisibility(8);
        this.mVolumeSlider = (SeekBar) findViewById(C0299R.id.mr_volume_slider);
        this.mVolumeSlider.setTag(this.mRoute);
        this.mVolumeChangeListener = new VolumeChangeListener();
        this.mVolumeSlider.setOnSeekBarChangeListener(this.mVolumeChangeListener);
        this.mVolumeGroupList = (OverlayListView) findViewById(C0299R.id.mr_volume_group_list);
        this.mGroupMemberRoutes = new ArrayList();
        this.mVolumeGroupAdapter = new VolumeGroupAdapter(this.mVolumeGroupList.getContext(), this.mGroupMemberRoutes);
        this.mVolumeGroupList.setAdapter(this.mVolumeGroupAdapter);
        this.mGroupMemberRoutesAnimatingWithBitmap = new HashSet();
        MediaRouterThemeHelper.setMediaControlsBackgroundColor(this.mContext, this.mMediaMainControlLayout, this.mVolumeGroupList, getGroup() != null);
        MediaRouterThemeHelper.setVolumeSliderColor(this.mContext, (MediaRouteVolumeSlider) this.mVolumeSlider, this.mMediaMainControlLayout);
        this.mVolumeSliderMap = new HashMap();
        this.mVolumeSliderMap.put(this.mRoute, this.mVolumeSlider);
        this.mGroupExpandCollapseButton = (MediaRouteExpandCollapseButton) findViewById(C0299R.id.mr_group_expand_collapse);
        this.mGroupExpandCollapseButton.setOnClickListener(new C02785());
        loadInterpolator();
        this.mGroupListAnimationDurationMs = this.mContext.getResources().getInteger(C0299R.integer.mr_controller_volume_group_list_animation_duration_ms);
        this.mGroupListFadeInDurationMs = this.mContext.getResources().getInteger(C0299R.integer.mr_controller_volume_group_list_fade_in_duration_ms);
        this.mGroupListFadeOutDurationMs = this.mContext.getResources().getInteger(C0299R.integer.mr_controller_volume_group_list_fade_out_duration_ms);
        this.mCustomControlView = onCreateMediaControlView(savedInstanceState);
        View view = this.mCustomControlView;
        if (view != null) {
            this.mCustomControlLayout.addView(view);
            this.mCustomControlLayout.setVisibility(0);
        }
        this.mCreated = true;
        updateLayout();
    }

    void updateLayout() {
        int width = MediaRouteDialogHelper.getDialogWidth(this.mContext);
        getWindow().setLayout(width, -2);
        View decorView = getWindow().getDecorView();
        this.mDialogContentWidth = (width - decorView.getPaddingLeft()) - decorView.getPaddingRight();
        Resources res = this.mContext.getResources();
        this.mVolumeGroupListItemIconSize = res.getDimensionPixelSize(C0299R.dimen.mr_controller_volume_group_list_item_icon_size);
        this.mVolumeGroupListItemHeight = res.getDimensionPixelSize(C0299R.dimen.mr_controller_volume_group_list_item_height);
        this.mVolumeGroupListMaxHeight = res.getDimensionPixelSize(C0299R.dimen.mr_controller_volume_group_list_max_height);
        this.mArtIconBitmap = null;
        this.mArtIconUri = null;
        updateArtIconIfNeeded();
        update(false);
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mAttachedToWindow = true;
        this.mRouter.addCallback(MediaRouteSelector.EMPTY, this.mCallback, 2);
        setMediaSession(this.mRouter.getMediaSessionToken());
    }

    public void onDetachedFromWindow() {
        this.mRouter.removeCallback(this.mCallback);
        setMediaSession(null);
        this.mAttachedToWindow = false;
        super.onDetachedFromWindow();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 25) {
            if (keyCode != 24) {
                return super.onKeyDown(keyCode, event);
            }
        }
        this.mRoute.requestUpdateVolume(keyCode == 25 ? -1 : 1);
        return true;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode != 25) {
            if (keyCode != 24) {
                return super.onKeyUp(keyCode, event);
            }
        }
        return true;
    }

    void update(boolean animate) {
        if (this.mRouteInVolumeSliderTouched != null) {
            this.mHasPendingUpdate = true;
            this.mPendingUpdateAnimationNeeded |= animate;
            return;
        }
        int i = 0;
        this.mHasPendingUpdate = false;
        this.mPendingUpdateAnimationNeeded = false;
        if (this.mRoute.isSelected()) {
            if (!this.mRoute.isDefaultOrBluetooth()) {
                if (this.mCreated) {
                    this.mRouteNameTextView.setText(this.mRoute.getName());
                    Button button = this.mDisconnectButton;
                    if (!this.mRoute.canDisconnect()) {
                        i = 8;
                    }
                    button.setVisibility(i);
                    if (this.mCustomControlView == null && this.mArtIconIsLoaded) {
                        if (isBitmapRecycled(this.mArtIconLoadedBitmap)) {
                            String str = TAG;
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("Can't set artwork image with recycled bitmap: ");
                            stringBuilder.append(this.mArtIconLoadedBitmap);
                            Log.w(str, stringBuilder.toString());
                        } else {
                            this.mArtView.setImageBitmap(this.mArtIconLoadedBitmap);
                            this.mArtView.setBackgroundColor(this.mArtIconBackgroundColor);
                        }
                        clearLoadedBitmap();
                    }
                    updateVolumeControlLayout();
                    updatePlaybackControlLayout();
                    updateLayoutHeight(animate);
                    return;
                }
                return;
            }
        }
        dismiss();
    }

    private boolean isBitmapRecycled(Bitmap bitmap) {
        return bitmap != null && bitmap.isRecycled();
    }

    private boolean canShowPlaybackControlLayout() {
        return this.mCustomControlView == null && !(this.mDescription == null && this.mState == null);
    }

    private int getMainControllerHeight(boolean showPlaybackControl) {
        if (!showPlaybackControl) {
            if (this.mVolumeControlLayout.getVisibility() != 0) {
                return 0;
            }
        }
        int height = 0 + (this.mMediaMainControlLayout.getPaddingTop() + this.mMediaMainControlLayout.getPaddingBottom());
        if (showPlaybackControl) {
            height += this.mPlaybackControlLayout.getMeasuredHeight();
        }
        if (this.mVolumeControlLayout.getVisibility() == 0) {
            height += this.mVolumeControlLayout.getMeasuredHeight();
        }
        if (showPlaybackControl && this.mVolumeControlLayout.getVisibility() == 0) {
            return height + this.mDividerView.getMeasuredHeight();
        }
        return height;
    }

    private void updateMediaControlVisibility(boolean canShowPlaybackControlLayout) {
        View view = this.mDividerView;
        int i = 0;
        int i2 = (this.mVolumeControlLayout.getVisibility() == 0 && canShowPlaybackControlLayout) ? 0 : 8;
        view.setVisibility(i2);
        LinearLayout linearLayout = this.mMediaMainControlLayout;
        if (this.mVolumeControlLayout.getVisibility() == 8 && !canShowPlaybackControlLayout) {
            i = 8;
        }
        linearLayout.setVisibility(i);
    }

    void updateLayoutHeight(final boolean animate) {
        this.mDefaultControlLayout.requestLayout();
        this.mDefaultControlLayout.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                MediaRouteControllerDialog.this.mDefaultControlLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                if (MediaRouteControllerDialog.this.mIsGroupListAnimating) {
                    MediaRouteControllerDialog.this.mIsGroupListAnimationPending = true;
                } else {
                    MediaRouteControllerDialog.this.updateLayoutHeightInternal(animate);
                }
            }
        });
    }

    void updateLayoutHeightInternal(boolean animate) {
        int expandedGroupListHeight;
        int oldHeight = getLayoutHeight(this.mMediaMainControlLayout);
        setLayoutHeight(this.mMediaMainControlLayout, -1);
        updateMediaControlVisibility(canShowPlaybackControlLayout());
        View decorView = getWindow().getDecorView();
        decorView.measure(MeasureSpec.makeMeasureSpec(getWindow().getAttributes().width, 1073741824), 0);
        setLayoutHeight(this.mMediaMainControlLayout, oldHeight);
        int artViewHeight = 0;
        if (this.mCustomControlView == null && (r0.mArtView.getDrawable() instanceof BitmapDrawable)) {
            Bitmap art = ((BitmapDrawable) r0.mArtView.getDrawable()).getBitmap();
            if (art != null) {
                artViewHeight = getDesiredArtHeight(art.getWidth(), art.getHeight());
                r0.mArtView.setScaleType(art.getWidth() >= art.getHeight() ? ScaleType.FIT_XY : ScaleType.FIT_CENTER);
            }
        }
        int mainControllerHeight = getMainControllerHeight(canShowPlaybackControlLayout());
        int volumeGroupListCount = r0.mGroupMemberRoutes.size();
        if (getGroup() == null) {
            expandedGroupListHeight = 0;
        } else {
            expandedGroupListHeight = r0.mVolumeGroupListItemHeight * getGroup().getRoutes().size();
        }
        if (volumeGroupListCount > 0) {
            expandedGroupListHeight += r0.mVolumeGroupListPaddingTop;
        }
        int visibleGroupListHeight = r0.mIsGroupExpanded ? Math.min(expandedGroupListHeight, r0.mVolumeGroupListMaxHeight) : 0;
        int desiredControlLayoutHeight = Math.max(artViewHeight, visibleGroupListHeight) + mainControllerHeight;
        Rect visibleRect = new Rect();
        decorView.getWindowVisibleDisplayFrame(visibleRect);
        int maximumControlViewHeight = visibleRect.height() - (r0.mDialogAreaLayout.getMeasuredHeight() - r0.mDefaultControlLayout.getMeasuredHeight());
        if (r0.mCustomControlView != null || artViewHeight <= 0 || desiredControlLayoutHeight > maximumControlViewHeight) {
            if (getLayoutHeight(r0.mVolumeGroupList) + r0.mMediaMainControlLayout.getMeasuredHeight() >= r0.mDefaultControlLayout.getMeasuredHeight()) {
                r0.mArtView.setVisibility(8);
            }
            artViewHeight = 0;
            desiredControlLayoutHeight = visibleGroupListHeight + mainControllerHeight;
        } else {
            r0.mArtView.setVisibility(0);
            setLayoutHeight(r0.mArtView, artViewHeight);
        }
        if (!canShowPlaybackControlLayout() || desiredControlLayoutHeight > maximumControlViewHeight) {
            r0.mPlaybackControlLayout.setVisibility(8);
        } else {
            r0.mPlaybackControlLayout.setVisibility(0);
        }
        boolean z = true;
        updateMediaControlVisibility(r0.mPlaybackControlLayout.getVisibility() == 0);
        if (r0.mPlaybackControlLayout.getVisibility() != 0) {
            z = false;
        }
        int mainControllerHeight2 = getMainControllerHeight(z);
        mainControllerHeight = Math.max(artViewHeight, visibleGroupListHeight) + mainControllerHeight2;
        if (mainControllerHeight > maximumControlViewHeight) {
            visibleGroupListHeight -= mainControllerHeight - maximumControlViewHeight;
            mainControllerHeight = maximumControlViewHeight;
        }
        r0.mMediaMainControlLayout.clearAnimation();
        r0.mVolumeGroupList.clearAnimation();
        r0.mDefaultControlLayout.clearAnimation();
        if (animate) {
            animateLayoutHeight(r0.mMediaMainControlLayout, mainControllerHeight2);
            animateLayoutHeight(r0.mVolumeGroupList, visibleGroupListHeight);
            animateLayoutHeight(r0.mDefaultControlLayout, mainControllerHeight);
        } else {
            setLayoutHeight(r0.mMediaMainControlLayout, mainControllerHeight2);
            setLayoutHeight(r0.mVolumeGroupList, visibleGroupListHeight);
            setLayoutHeight(r0.mDefaultControlLayout, mainControllerHeight);
        }
        setLayoutHeight(r0.mExpandableAreaLayout, visibleRect.height());
        rebuildVolumeGroupList(animate);
    }

    void updateVolumeGroupItemHeight(View item) {
        setLayoutHeight((LinearLayout) item.findViewById(C0299R.id.volume_item_container), this.mVolumeGroupListItemHeight);
        View icon = item.findViewById(C0299R.id.mr_volume_item_icon);
        LayoutParams lp = icon.getLayoutParams();
        int i = this.mVolumeGroupListItemIconSize;
        lp.width = i;
        lp.height = i;
        icon.setLayoutParams(lp);
    }

    private void animateLayoutHeight(final View view, int targetHeight) {
        final int startValue = getLayoutHeight(view);
        final int endValue = targetHeight;
        Animation anim = new Animation() {
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                int height = startValue;
                MediaRouteControllerDialog.setLayoutHeight(view, height - ((int) (((float) (height - endValue)) * interpolatedTime)));
            }
        };
        anim.setDuration((long) this.mGroupListAnimationDurationMs);
        if (VERSION.SDK_INT >= 21) {
            anim.setInterpolator(this.mInterpolator);
        }
        view.startAnimation(anim);
    }

    void loadInterpolator() {
        if (VERSION.SDK_INT >= 21) {
            this.mInterpolator = this.mIsGroupExpanded ? this.mLinearOutSlowInInterpolator : this.mFastOutSlowInInterpolator;
        } else {
            this.mInterpolator = this.mAccelerateDecelerateInterpolator;
        }
    }

    private void updateVolumeControlLayout() {
        int i = 8;
        if (!isVolumeControlAvailable(this.mRoute)) {
            this.mVolumeControlLayout.setVisibility(8);
        } else if (this.mVolumeControlLayout.getVisibility() == 8) {
            this.mVolumeControlLayout.setVisibility(0);
            this.mVolumeSlider.setMax(this.mRoute.getVolumeMax());
            this.mVolumeSlider.setProgress(this.mRoute.getVolume());
            MediaRouteExpandCollapseButton mediaRouteExpandCollapseButton = this.mGroupExpandCollapseButton;
            if (getGroup() != null) {
                i = 0;
            }
            mediaRouteExpandCollapseButton.setVisibility(i);
        }
    }

    private void rebuildVolumeGroupList(boolean animate) {
        List<RouteInfo> routes = getGroup() == null ? null : getGroup().getRoutes();
        if (routes == null) {
            this.mGroupMemberRoutes.clear();
            this.mVolumeGroupAdapter.notifyDataSetChanged();
        } else if (MediaRouteDialogHelper.listUnorderedEquals(this.mGroupMemberRoutes, routes)) {
            this.mVolumeGroupAdapter.notifyDataSetChanged();
        } else {
            HashMap<RouteInfo, Rect> previousRouteBoundMap = animate ? MediaRouteDialogHelper.getItemBoundMap(this.mVolumeGroupList, this.mVolumeGroupAdapter) : null;
            HashMap<RouteInfo, BitmapDrawable> previousRouteBitmapMap = animate ? MediaRouteDialogHelper.getItemBitmapMap(this.mContext, this.mVolumeGroupList, this.mVolumeGroupAdapter) : null;
            this.mGroupMemberRoutesAdded = MediaRouteDialogHelper.getItemsAdded(this.mGroupMemberRoutes, routes);
            this.mGroupMemberRoutesRemoved = MediaRouteDialogHelper.getItemsRemoved(this.mGroupMemberRoutes, routes);
            this.mGroupMemberRoutes.addAll(0, this.mGroupMemberRoutesAdded);
            this.mGroupMemberRoutes.removeAll(this.mGroupMemberRoutesRemoved);
            this.mVolumeGroupAdapter.notifyDataSetChanged();
            if (animate && this.mIsGroupExpanded) {
                if (this.mGroupMemberRoutesAdded.size() + this.mGroupMemberRoutesRemoved.size() > 0) {
                    animateGroupListItems(previousRouteBoundMap, previousRouteBitmapMap);
                    return;
                }
            }
            this.mGroupMemberRoutesAdded = null;
            this.mGroupMemberRoutesRemoved = null;
        }
    }

    private void animateGroupListItems(final Map<RouteInfo, Rect> previousRouteBoundMap, final Map<RouteInfo, BitmapDrawable> previousRouteBitmapMap) {
        this.mVolumeGroupList.setEnabled(false);
        this.mVolumeGroupList.requestLayout();
        this.mIsGroupListAnimating = true;
        this.mVolumeGroupList.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                MediaRouteControllerDialog.this.mVolumeGroupList.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                MediaRouteControllerDialog.this.animateGroupListItemsInternal(previousRouteBoundMap, previousRouteBitmapMap);
            }
        });
    }

    void animateGroupListItemsInternal(Map<RouteInfo, Rect> previousRouteBoundMap, Map<RouteInfo, BitmapDrawable> previousRouteBitmapMap) {
        Map<RouteInfo, Rect> map = previousRouteBoundMap;
        Set set = this.mGroupMemberRoutesAdded;
        Map<RouteInfo, BitmapDrawable> map2;
        if (set == null) {
            map2 = previousRouteBitmapMap;
        } else if (r0.mGroupMemberRoutesRemoved == null) {
            map2 = previousRouteBitmapMap;
        } else {
            Rect previousBounds;
            AnimationListener listener;
            int groupSizeDelta = set.size() - r0.mGroupMemberRoutesRemoved.size();
            boolean listenerRegistered = false;
            AnimationListener listener2 = new C02829();
            int first = r0.mVolumeGroupList.getFirstVisiblePosition();
            for (int i = 0; i < r0.mVolumeGroupList.getChildCount(); i++) {
                View view = r0.mVolumeGroupList.getChildAt(i);
                int position = first + i;
                RouteInfo route = (RouteInfo) r0.mVolumeGroupAdapter.getItem(position);
                previousBounds = (Rect) map.get(route);
                int currentTop = view.getTop();
                int previousTop = previousBounds != null ? previousBounds.top : (r0.mVolumeGroupListItemHeight * groupSizeDelta) + currentTop;
                AnimationSet animSet = new AnimationSet(true);
                Set set2 = r0.mGroupMemberRoutesAdded;
                if (set2 == null || !set2.contains(route)) {
                } else {
                    previousTop = currentTop;
                    Animation alphaAnim = new AlphaAnimation(0.0f, 0.0f);
                    alphaAnim.setDuration((long) r0.mGroupListFadeInDurationMs);
                    animSet.addAnimation(alphaAnim);
                }
                Animation translationAnim = new TranslateAnimation(0.0f, 0.0f, (float) (previousTop - currentTop), 0.0f);
                translationAnim.setDuration((long) r0.mGroupListAnimationDurationMs);
                animSet.addAnimation(translationAnim);
                animSet.setFillAfter(true);
                animSet.setFillEnabled(true);
                animSet.setInterpolator(r0.mInterpolator);
                if (!listenerRegistered) {
                    listenerRegistered = true;
                    animSet.setAnimationListener(listener2);
                }
                view.clearAnimation();
                view.startAnimation(animSet);
                map.remove(route);
                previousRouteBitmapMap.remove(route);
            }
            map2 = previousRouteBitmapMap;
            for (Entry<RouteInfo, BitmapDrawable> item : previousRouteBitmapMap.entrySet()) {
                boolean listenerRegistered2;
                final RouteInfo route2 = (RouteInfo) item.getKey();
                BitmapDrawable bitmap = (BitmapDrawable) item.getValue();
                previousBounds = (Rect) map.get(route2);
                if (r0.mGroupMemberRoutesRemoved.contains(route2)) {
                    listenerRegistered2 = listenerRegistered;
                    listener = listener2;
                    listenerRegistered = new OverlayObject(bitmap, previousBounds).setAlphaAnimation(1.0f, 0.0f).setDuration((long) r0.mGroupListFadeOutDurationMs).setInterpolator(r0.mInterpolator);
                } else {
                    listenerRegistered2 = listenerRegistered;
                    listener = listener2;
                    OverlayObject object = new OverlayObject(bitmap, previousBounds).setTranslateYAnimation(r0.mVolumeGroupListItemHeight * groupSizeDelta).setDuration((long) r0.mGroupListAnimationDurationMs).setInterpolator(r0.mInterpolator).setAnimationEndListener(new OnAnimationEndListener() {
                        public void onAnimationEnd() {
                            MediaRouteControllerDialog.this.mGroupMemberRoutesAnimatingWithBitmap.remove(route2);
                            MediaRouteControllerDialog.this.mVolumeGroupAdapter.notifyDataSetChanged();
                        }
                    });
                    r0.mGroupMemberRoutesAnimatingWithBitmap.add(route2);
                    listenerRegistered = object;
                }
                r0.mVolumeGroupList.addOverlayObject(listenerRegistered);
                listener2 = listener;
                listenerRegistered = listenerRegistered2;
            }
            listener = listener2;
        }
    }

    void startGroupListFadeInAnimation() {
        clearGroupListAnimation(true);
        this.mVolumeGroupList.requestLayout();
        this.mVolumeGroupList.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                MediaRouteControllerDialog.this.mVolumeGroupList.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                MediaRouteControllerDialog.this.startGroupListFadeInAnimationInternal();
            }
        });
    }

    void startGroupListFadeInAnimationInternal() {
        Set set = this.mGroupMemberRoutesAdded;
        if (set == null || set.size() == 0) {
            finishAnimation(true);
        } else {
            fadeInAddedRoutes();
        }
    }

    void finishAnimation(boolean animate) {
        this.mGroupMemberRoutesAdded = null;
        this.mGroupMemberRoutesRemoved = null;
        this.mIsGroupListAnimating = false;
        if (this.mIsGroupListAnimationPending) {
            this.mIsGroupListAnimationPending = false;
            updateLayoutHeight(animate);
        }
        this.mVolumeGroupList.setEnabled(true);
    }

    private void fadeInAddedRoutes() {
        AnimationListener listener = new AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                MediaRouteControllerDialog.this.finishAnimation(true);
            }

            public void onAnimationRepeat(Animation animation) {
            }
        };
        boolean listenerRegistered = false;
        int first = this.mVolumeGroupList.getFirstVisiblePosition();
        for (int i = 0; i < this.mVolumeGroupList.getChildCount(); i++) {
            View view = this.mVolumeGroupList.getChildAt(i);
            if (this.mGroupMemberRoutesAdded.contains((RouteInfo) this.mVolumeGroupAdapter.getItem(first + i))) {
                Animation alphaAnim = new AlphaAnimation(0.0f, 1.0f);
                alphaAnim.setDuration((long) this.mGroupListFadeInDurationMs);
                alphaAnim.setFillEnabled(true);
                alphaAnim.setFillAfter(true);
                if (!listenerRegistered) {
                    listenerRegistered = true;
                    alphaAnim.setAnimationListener(listener);
                }
                view.clearAnimation();
                view.startAnimation(alphaAnim);
            }
        }
    }

    void clearGroupListAnimation(boolean exceptAddedRoutes) {
        int first = this.mVolumeGroupList.getFirstVisiblePosition();
        for (int i = 0; i < this.mVolumeGroupList.getChildCount(); i++) {
            AnimationSet animSet;
            Animation alphaAnim;
            View view = this.mVolumeGroupList.getChildAt(i);
            RouteInfo route = (RouteInfo) this.mVolumeGroupAdapter.getItem(first + i);
            if (exceptAddedRoutes) {
                Set set = this.mGroupMemberRoutesAdded;
                if (set != null) {
                    if (set.contains(route)) {
                    } else {
                        ((LinearLayout) view.findViewById(C0299R.id.volume_item_container)).setVisibility(0);
                        animSet = new AnimationSet(true);
                        alphaAnim = new AlphaAnimation(1.0f, 1.0f);
                        alphaAnim.setDuration(0);
                        animSet.addAnimation(alphaAnim);
                        new TranslateAnimation(0.0f, 0.0f, 0.0f, 0.0f).setDuration(0);
                        animSet.setFillAfter(true);
                        animSet.setFillEnabled(true);
                        view.clearAnimation();
                        view.startAnimation(animSet);
                    }
                }
            }
            ((LinearLayout) view.findViewById(C0299R.id.volume_item_container)).setVisibility(0);
            animSet = new AnimationSet(true);
            alphaAnim = new AlphaAnimation(1.0f, 1.0f);
            alphaAnim.setDuration(0);
            animSet.addAnimation(alphaAnim);
            new TranslateAnimation(0.0f, 0.0f, 0.0f, 0.0f).setDuration(0);
            animSet.setFillAfter(true);
            animSet.setFillEnabled(true);
            view.clearAnimation();
            view.startAnimation(animSet);
        }
        this.mVolumeGroupList.stopAnimationAll();
        if (!exceptAddedRoutes) {
            finishAnimation(false);
        }
    }

    private void updatePlaybackControlLayout() {
        if (canShowPlaybackControlLayout()) {
            PlaybackStateCompat playbackStateCompat;
            MediaDescriptionCompat mediaDescriptionCompat = this.mDescription;
            CharSequence subtitle = null;
            CharSequence title = mediaDescriptionCompat == null ? null : mediaDescriptionCompat.getTitle();
            boolean isPlaying = true;
            boolean hasTitle = TextUtils.isEmpty(title) ^ true;
            MediaDescriptionCompat mediaDescriptionCompat2 = this.mDescription;
            if (mediaDescriptionCompat2 != null) {
                subtitle = mediaDescriptionCompat2.getSubtitle();
            }
            boolean hasSubtitle = TextUtils.isEmpty(subtitle) ^ true;
            boolean showTitle = false;
            boolean showSubtitle = false;
            if (this.mRoute.getPresentationDisplayId() != -1) {
                this.mTitleView.setText(C0299R.string.mr_controller_casting_screen);
                showTitle = true;
            } else {
                playbackStateCompat = this.mState;
                if (playbackStateCompat != null) {
                    if (playbackStateCompat.getState() != 0) {
                        if (hasTitle || hasSubtitle) {
                            if (hasTitle) {
                                this.mTitleView.setText(title);
                                showTitle = true;
                            }
                            if (hasSubtitle) {
                                this.mSubtitleView.setText(subtitle);
                                showSubtitle = true;
                            }
                        } else {
                            this.mTitleView.setText(C0299R.string.mr_controller_no_info_available);
                            showTitle = true;
                        }
                    }
                }
                this.mTitleView.setText(C0299R.string.mr_controller_no_media_selected);
                showTitle = true;
            }
            int i = 8;
            this.mTitleView.setVisibility(showTitle ? 0 : 8);
            this.mSubtitleView.setVisibility(showSubtitle ? 0 : 8);
            playbackStateCompat = this.mState;
            if (playbackStateCompat != null) {
                Context playbackControlButtonContext;
                boolean visible;
                int iconDrawableAttr;
                int iconDescResId;
                ImageButton imageButton;
                if (playbackStateCompat.getState() != 6) {
                    if (this.mState.getState() != 3) {
                        isPlaying = false;
                        playbackControlButtonContext = this.mPlaybackControlButton.getContext();
                        visible = true;
                        iconDrawableAttr = 0;
                        iconDescResId = 0;
                        if (!isPlaying && isPauseActionSupported()) {
                            iconDrawableAttr = C0299R.attr.mediaRoutePauseDrawable;
                            iconDescResId = C0299R.string.mr_controller_pause;
                        } else if (!isPlaying && isStopActionSupported()) {
                            iconDrawableAttr = C0299R.attr.mediaRouteStopDrawable;
                            iconDescResId = C0299R.string.mr_controller_stop;
                        } else if (isPlaying && isPlayActionSupported()) {
                            iconDrawableAttr = C0299R.attr.mediaRoutePlayDrawable;
                            iconDescResId = C0299R.string.mr_controller_play;
                        } else {
                            visible = false;
                        }
                        imageButton = this.mPlaybackControlButton;
                        if (visible) {
                            i = 0;
                        }
                        imageButton.setVisibility(i);
                        if (visible) {
                            this.mPlaybackControlButton.setImageResource(MediaRouterThemeHelper.getThemeResource(playbackControlButtonContext, iconDrawableAttr));
                            this.mPlaybackControlButton.setContentDescription(playbackControlButtonContext.getResources().getText(iconDescResId));
                        }
                    }
                }
                playbackControlButtonContext = this.mPlaybackControlButton.getContext();
                visible = true;
                iconDrawableAttr = 0;
                iconDescResId = 0;
                if (!isPlaying) {
                }
                if (!isPlaying) {
                }
                if (isPlaying) {
                }
                visible = false;
                imageButton = this.mPlaybackControlButton;
                if (visible) {
                    i = 0;
                }
                imageButton.setVisibility(i);
                if (visible) {
                    this.mPlaybackControlButton.setImageResource(MediaRouterThemeHelper.getThemeResource(playbackControlButtonContext, iconDrawableAttr));
                    this.mPlaybackControlButton.setContentDescription(playbackControlButtonContext.getResources().getText(iconDescResId));
                }
            }
        }
    }

    private boolean isPlayActionSupported() {
        return (this.mState.getActions() & 516) != 0;
    }

    private boolean isPauseActionSupported() {
        return (this.mState.getActions() & 514) != 0;
    }

    private boolean isStopActionSupported() {
        return (this.mState.getActions() & 1) != 0;
    }

    boolean isVolumeControlAvailable(RouteInfo route) {
        return this.mVolumeControlEnabled && route.getVolumeHandling() == 1;
    }

    private static int getLayoutHeight(View view) {
        return view.getLayoutParams().height;
    }

    static void setLayoutHeight(View view, int height) {
        LayoutParams lp = view.getLayoutParams();
        lp.height = height;
        view.setLayoutParams(lp);
    }

    private static boolean uriEquals(Uri uri1, Uri uri2) {
        if (uri1 != null && uri1.equals(uri2)) {
            return true;
        }
        if (uri1 == null && uri2 == null) {
            return true;
        }
        return false;
    }

    int getDesiredArtHeight(int originalWidth, int originalHeight) {
        if (originalWidth >= originalHeight) {
            return (int) (((((float) this.mDialogContentWidth) * ((float) originalHeight)) / ((float) originalWidth)) + 0.5f);
        }
        return (int) (((((float) this.mDialogContentWidth) * 9.0f) / 16.0f) + 0.5f);
    }

    void updateArtIconIfNeeded() {
        if (this.mCustomControlView == null) {
            if (isIconChanged()) {
                FetchArtTask fetchArtTask = this.mFetchArtTask;
                if (fetchArtTask != null) {
                    fetchArtTask.cancel(true);
                }
                this.mFetchArtTask = new FetchArtTask();
                this.mFetchArtTask.execute(new Void[0]);
            }
        }
    }

    void clearLoadedBitmap() {
        this.mArtIconIsLoaded = false;
        this.mArtIconLoadedBitmap = null;
        this.mArtIconBackgroundColor = 0;
    }

    private boolean isIconChanged() {
        Bitmap newBitmap = this.mDescription;
        Uri newUri = null;
        newBitmap = newBitmap == null ? null : newBitmap.getIconBitmap();
        MediaDescriptionCompat mediaDescriptionCompat = this.mDescription;
        if (mediaDescriptionCompat != null) {
            newUri = mediaDescriptionCompat.getIconUri();
        }
        Bitmap oldBitmap = this.mFetchArtTask;
        oldBitmap = oldBitmap == null ? this.mArtIconBitmap : oldBitmap.getIconBitmap();
        Uri oldUri = this.mFetchArtTask;
        oldUri = oldUri == null ? this.mArtIconUri : oldUri.getIconUri();
        if (oldBitmap != newBitmap) {
            return true;
        }
        if (oldBitmap != null || uriEquals(oldUri, newUri)) {
            return false;
        }
        return true;
    }
}
