package android.support.v7.app;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.support.v7.media.MediaRouter.Callback;
import android.support.v7.media.MediaRouter.ProviderInfo;
import android.support.v7.media.MediaRouter.RouteInfo;
import android.support.v7.mediarouter.C0299R;
import android.support.v7.widget.TooltipCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.View.MeasureSpec;

public class MediaRouteButton extends View {
    private static final int[] CHECKABLE_STATE_SET = new int[]{16842911};
    private static final int[] CHECKED_STATE_SET = new int[]{16842912};
    private static final String CHOOSER_FRAGMENT_TAG = "android.support.v7.mediarouter:MediaRouteChooserDialogFragment";
    private static final String CONTROLLER_FRAGMENT_TAG = "android.support.v7.mediarouter:MediaRouteControllerDialogFragment";
    private static final String TAG = "MediaRouteButton";
    private static final SparseArray<ConstantState> sRemoteIndicatorCache = new SparseArray(2);
    private boolean mAttachedToWindow;
    private ColorStateList mButtonTint;
    private final MediaRouterCallback mCallback;
    private MediaRouteDialogFactory mDialogFactory;
    private boolean mIsConnecting;
    private int mMinHeight;
    private int mMinWidth;
    private boolean mRemoteActive;
    private Drawable mRemoteIndicator;
    private RemoteIndicatorLoader mRemoteIndicatorLoader;
    private final MediaRouter mRouter;
    private MediaRouteSelector mSelector;

    private final class RemoteIndicatorLoader extends AsyncTask<Void, Void, Drawable> {
        private final int mResId;

        RemoteIndicatorLoader(int resId) {
            this.mResId = resId;
        }

        protected Drawable doInBackground(Void... params) {
            return MediaRouteButton.this.getContext().getResources().getDrawable(this.mResId);
        }

        protected void onPostExecute(Drawable remoteIndicator) {
            cacheAndReset(remoteIndicator);
            MediaRouteButton.this.setRemoteIndicatorDrawable(remoteIndicator);
        }

        protected void onCancelled(Drawable remoteIndicator) {
            cacheAndReset(remoteIndicator);
        }

        private void cacheAndReset(Drawable remoteIndicator) {
            if (remoteIndicator != null) {
                MediaRouteButton.sRemoteIndicatorCache.put(this.mResId, remoteIndicator.getConstantState());
            }
            MediaRouteButton.this.mRemoteIndicatorLoader = null;
        }
    }

    private final class MediaRouterCallback extends Callback {
        MediaRouterCallback() {
        }

        public void onRouteAdded(MediaRouter router, RouteInfo info) {
            MediaRouteButton.this.refreshRoute();
        }

        public void onRouteRemoved(MediaRouter router, RouteInfo info) {
            MediaRouteButton.this.refreshRoute();
        }

        public void onRouteChanged(MediaRouter router, RouteInfo info) {
            MediaRouteButton.this.refreshRoute();
        }

        public void onRouteSelected(MediaRouter router, RouteInfo info) {
            MediaRouteButton.this.refreshRoute();
        }

        public void onRouteUnselected(MediaRouter router, RouteInfo info) {
            MediaRouteButton.this.refreshRoute();
        }

        public void onProviderAdded(MediaRouter router, ProviderInfo provider) {
            MediaRouteButton.this.refreshRoute();
        }

        public void onProviderRemoved(MediaRouter router, ProviderInfo provider) {
            MediaRouteButton.this.refreshRoute();
        }

        public void onProviderChanged(MediaRouter router, ProviderInfo provider) {
            MediaRouteButton.this.refreshRoute();
        }
    }

    public MediaRouteButton(Context context) {
        this(context, null);
    }

    public MediaRouteButton(Context context, AttributeSet attrs) {
        this(context, attrs, C0299R.attr.mediaRouteButtonStyle);
    }

    public MediaRouteButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(MediaRouterThemeHelper.createThemedButtonContext(context), attrs, defStyleAttr);
        this.mSelector = MediaRouteSelector.EMPTY;
        this.mDialogFactory = MediaRouteDialogFactory.getDefault();
        context = getContext();
        this.mRouter = MediaRouter.getInstance(context);
        this.mCallback = new MediaRouterCallback();
        TypedArray a = context.obtainStyledAttributes(attrs, C0299R.styleable.MediaRouteButton, defStyleAttr, 0);
        this.mButtonTint = a.getColorStateList(C0299R.styleable.MediaRouteButton_mediaRouteButtonTint);
        this.mMinWidth = a.getDimensionPixelSize(C0299R.styleable.MediaRouteButton_android_minWidth, 0);
        this.mMinHeight = a.getDimensionPixelSize(C0299R.styleable.MediaRouteButton_android_minHeight, 0);
        int remoteIndicatorResId = a.getResourceId(C0299R.styleable.MediaRouteButton_externalRouteEnabledDrawable, 0);
        a.recycle();
        if (remoteIndicatorResId != 0) {
            ConstantState remoteIndicatorState = (ConstantState) sRemoteIndicatorCache.get(remoteIndicatorResId);
            if (remoteIndicatorState != null) {
                setRemoteIndicatorDrawable(remoteIndicatorState.newDrawable());
            } else {
                this.mRemoteIndicatorLoader = new RemoteIndicatorLoader(remoteIndicatorResId);
                this.mRemoteIndicatorLoader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
            }
        }
        updateContentDescription();
        setClickable(true);
    }

    @NonNull
    public MediaRouteSelector getRouteSelector() {
        return this.mSelector;
    }

    public void setRouteSelector(MediaRouteSelector selector) {
        if (selector == null) {
            throw new IllegalArgumentException("selector must not be null");
        } else if (!this.mSelector.equals(selector)) {
            if (this.mAttachedToWindow) {
                if (!this.mSelector.isEmpty()) {
                    this.mRouter.removeCallback(this.mCallback);
                }
                if (!selector.isEmpty()) {
                    this.mRouter.addCallback(selector, this.mCallback);
                }
            }
            this.mSelector = selector;
            refreshRoute();
        }
    }

    @NonNull
    public MediaRouteDialogFactory getDialogFactory() {
        return this.mDialogFactory;
    }

    public void setDialogFactory(@NonNull MediaRouteDialogFactory factory) {
        if (factory != null) {
            this.mDialogFactory = factory;
            return;
        }
        throw new IllegalArgumentException("factory must not be null");
    }

    public boolean showDialog() {
        if (!this.mAttachedToWindow) {
            return false;
        }
        FragmentManager fm = getFragmentManager();
        if (fm != null) {
            RouteInfo route = this.mRouter.getSelectedRoute();
            if (!route.isDefaultOrBluetooth()) {
                if (route.matchesSelector(this.mSelector)) {
                    if (fm.findFragmentByTag(CONTROLLER_FRAGMENT_TAG) != null) {
                        Log.w(TAG, "showDialog(): Route controller dialog already showing!");
                        return false;
                    }
                    this.mDialogFactory.onCreateControllerDialogFragment().show(fm, CONTROLLER_FRAGMENT_TAG);
                    return true;
                }
            }
            if (fm.findFragmentByTag(CHOOSER_FRAGMENT_TAG) != null) {
                Log.w(TAG, "showDialog(): Route chooser dialog already showing!");
                return false;
            }
            MediaRouteChooserDialogFragment f = this.mDialogFactory.onCreateChooserDialogFragment();
            f.setRouteSelector(this.mSelector);
            f.show(fm, CHOOSER_FRAGMENT_TAG);
            return true;
        }
        throw new IllegalStateException("The activity must be a subclass of FragmentActivity");
    }

    private FragmentManager getFragmentManager() {
        Activity activity = getActivity();
        if (activity instanceof FragmentActivity) {
            return ((FragmentActivity) activity).getSupportFragmentManager();
        }
        return null;
    }

    private Activity getActivity() {
        for (Context context = getContext(); context instanceof ContextWrapper; context = ((ContextWrapper) context).getBaseContext()) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
        }
        return null;
    }

    void setCheatSheetEnabled(boolean enable) {
        TooltipCompat.setTooltipText(this, enable ? getContext().getString(C0299R.string.mr_button_content_description) : null);
    }

    public boolean performClick() {
        boolean handled = super.performClick();
        if (!handled) {
            playSoundEffect(0);
        }
        if (!showDialog()) {
            if (!handled) {
                return false;
            }
        }
        return true;
    }

    protected int[] onCreateDrawableState(int extraSpace) {
        int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (this.mIsConnecting) {
            mergeDrawableStates(drawableState, CHECKABLE_STATE_SET);
        } else if (this.mRemoteActive) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (this.mRemoteIndicator != null) {
            this.mRemoteIndicator.setState(getDrawableState());
            invalidate();
        }
    }

    public void setRemoteIndicatorDrawable(Drawable d) {
        RemoteIndicatorLoader remoteIndicatorLoader = this.mRemoteIndicatorLoader;
        if (remoteIndicatorLoader != null) {
            remoteIndicatorLoader.cancel(false);
        }
        Drawable drawable = this.mRemoteIndicator;
        if (drawable != null) {
            drawable.setCallback(null);
            unscheduleDrawable(this.mRemoteIndicator);
        }
        if (d != null) {
            if (this.mButtonTint != null) {
                d = DrawableCompat.wrap(d.mutate());
                DrawableCompat.setTintList(d, this.mButtonTint);
            }
            d.setCallback(this);
            d.setState(getDrawableState());
            d.setVisible(getVisibility() == 0, false);
        }
        this.mRemoteIndicator = d;
        refreshDrawableState();
        if (this.mAttachedToWindow) {
            Drawable drawable2 = this.mRemoteIndicator;
            if (drawable2 != null) {
                if (drawable2.getCurrent() instanceof AnimationDrawable) {
                    AnimationDrawable curDrawable = (AnimationDrawable) this.mRemoteIndicator.getCurrent();
                    if (this.mIsConnecting) {
                        if (!curDrawable.isRunning()) {
                            curDrawable.start();
                        }
                    } else if (this.mRemoteActive) {
                        if (curDrawable.isRunning()) {
                            curDrawable.stop();
                        }
                        curDrawable.selectDrawable(curDrawable.getNumberOfFrames() - 1);
                    }
                }
            }
        }
    }

    protected boolean verifyDrawable(Drawable who) {
        if (!super.verifyDrawable(who)) {
            if (who != this.mRemoteIndicator) {
                return false;
            }
        }
        return true;
    }

    public void jumpDrawablesToCurrentState() {
        if (getBackground() != null) {
            DrawableCompat.jumpToCurrentState(getBackground());
        }
        Drawable drawable = this.mRemoteIndicator;
        if (drawable != null) {
            DrawableCompat.jumpToCurrentState(drawable);
        }
    }

    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        Drawable drawable = this.mRemoteIndicator;
        if (drawable != null) {
            drawable.setVisible(getVisibility() == 0, false);
        }
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mAttachedToWindow = true;
        if (!this.mSelector.isEmpty()) {
            this.mRouter.addCallback(this.mSelector, this.mCallback);
        }
        refreshRoute();
    }

    public void onDetachedFromWindow() {
        this.mAttachedToWindow = false;
        if (!this.mSelector.isEmpty()) {
            this.mRouter.removeCallback(this.mCallback);
        }
        super.onDetachedFromWindow();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidth;
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int width = this.mMinWidth;
        Drawable drawable = this.mRemoteIndicator;
        int i = 0;
        width = Math.max(width, drawable != null ? (drawable.getIntrinsicWidth() + getPaddingLeft()) + getPaddingRight() : 0);
        int height = this.mMinHeight;
        Drawable drawable2 = this.mRemoteIndicator;
        if (drawable2 != null) {
            i = (drawable2.getIntrinsicHeight() + getPaddingTop()) + getPaddingBottom();
        }
        height = Math.max(height, i);
        if (widthMode == Integer.MIN_VALUE) {
            measuredWidth = Math.min(widthSize, width);
        } else if (widthMode != 1073741824) {
            measuredWidth = width;
        } else {
            measuredWidth = widthSize;
        }
        if (heightMode == Integer.MIN_VALUE) {
            i = Math.min(heightSize, height);
        } else if (heightMode != 1073741824) {
            i = height;
        } else {
            i = heightSize;
        }
        setMeasuredDimension(measuredWidth, i);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mRemoteIndicator != null) {
            int left = getPaddingLeft();
            int right = getWidth() - getPaddingRight();
            int top = getPaddingTop();
            int bottom = getHeight() - getPaddingBottom();
            int drawWidth = this.mRemoteIndicator.getIntrinsicWidth();
            int drawHeight = this.mRemoteIndicator.getIntrinsicHeight();
            int drawLeft = (((right - left) - drawWidth) / 2) + left;
            int drawTop = (((bottom - top) - drawHeight) / 2) + top;
            this.mRemoteIndicator.setBounds(drawLeft, drawTop, drawLeft + drawWidth, drawTop + drawHeight);
            this.mRemoteIndicator.draw(canvas);
        }
    }

    void refreshRoute() {
        RouteInfo route = this.mRouter.getSelectedRoute();
        boolean isConnecting = false;
        boolean isRemote = !route.isDefaultOrBluetooth() && route.matchesSelector(this.mSelector);
        if (isRemote && route.isConnecting()) {
            isConnecting = true;
        }
        boolean needsRefresh = false;
        if (this.mRemoteActive != isRemote) {
            this.mRemoteActive = isRemote;
            needsRefresh = true;
        }
        if (this.mIsConnecting != isConnecting) {
            this.mIsConnecting = isConnecting;
            needsRefresh = true;
        }
        if (needsRefresh) {
            updateContentDescription();
            refreshDrawableState();
        }
        if (this.mAttachedToWindow) {
            setEnabled(this.mRouter.isRouteAvailable(this.mSelector, 1));
        }
        Drawable drawable = this.mRemoteIndicator;
        if (drawable == null) {
            return;
        }
        if (drawable.getCurrent() instanceof AnimationDrawable) {
            AnimationDrawable curDrawable = (AnimationDrawable) this.mRemoteIndicator.getCurrent();
            if (this.mAttachedToWindow) {
                if ((needsRefresh || isConnecting) && !curDrawable.isRunning()) {
                    curDrawable.start();
                }
            } else if (isRemote && !isConnecting) {
                if (curDrawable.isRunning()) {
                    curDrawable.stop();
                }
                curDrawable.selectDrawable(curDrawable.getNumberOfFrames() - 1);
            }
        }
    }

    private void updateContentDescription() {
        int resId;
        if (this.mIsConnecting) {
            resId = C0299R.string.mr_cast_button_connecting;
        } else if (this.mRemoteActive) {
            resId = C0299R.string.mr_cast_button_connected;
        } else {
            resId = C0299R.string.mr_cast_button_disconnected;
        }
        setContentDescription(getContext().getString(resId));
    }
}
