package android.support.wearable.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.ContextCompat;
import android.support.wearable.C0395R;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Locale;

@TargetApi(21)
public class ConfirmationOverlay implements OnTouchListener {
    public static final int DEFAULT_ANIMATION_DURATION_MS = 1000;
    public static final int FAILURE_ANIMATION = 1;
    public static final int OPEN_ON_PHONE_ANIMATION = 2;
    public static final int SUCCESS_ANIMATION = 0;
    private int mDurationMillis = 1000;
    private final Runnable mHideRunnable = new C04401();
    private boolean mIsShowing = false;
    private FinishedAnimationListener mListener;
    private final Handler mMainThreadHandler = new Handler(Looper.getMainLooper());
    private String mMessage;
    private Drawable mOverlayDrawable;
    private View mOverlayView;
    private int mType = 0;

    /* renamed from: android.support.wearable.view.ConfirmationOverlay$1 */
    class C04401 implements Runnable {
        C04401() {
        }

        public void run() {
            ConfirmationOverlay.this.hide();
        }
    }

    /* renamed from: android.support.wearable.view.ConfirmationOverlay$2 */
    class C04412 implements AnimationListener {
        C04412() {
        }

        public void onAnimationStart(Animation animation) {
        }

        public void onAnimationEnd(Animation animation) {
            ((ViewGroup) ConfirmationOverlay.this.mOverlayView.getParent()).removeView(ConfirmationOverlay.this.mOverlayView);
            ConfirmationOverlay.this.mIsShowing = false;
            if (ConfirmationOverlay.this.mListener != null) {
                ConfirmationOverlay.this.mListener.onAnimationFinished();
            }
        }

        public void onAnimationRepeat(Animation animation) {
        }
    }

    public interface FinishedAnimationListener {
        void onAnimationFinished();
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }

    public ConfirmationOverlay setMessage(String message) {
        this.mMessage = message;
        return this;
    }

    public ConfirmationOverlay setType(int type) {
        this.mType = type;
        return this;
    }

    public ConfirmationOverlay setDuration(int millis) {
        this.mDurationMillis = millis;
        return this;
    }

    public ConfirmationOverlay setFinishedAnimationListener(@Nullable FinishedAnimationListener listener) {
        this.mListener = listener;
        return this;
    }

    @MainThread
    public void showAbove(View view) {
        if (!this.mIsShowing) {
            this.mIsShowing = true;
            updateOverlayView(view.getContext());
            ((ViewGroup) view.getRootView()).addView(this.mOverlayView);
            animateAndHideAfterDelay();
        }
    }

    @MainThread
    public void showOn(Activity activity) {
        if (!this.mIsShowing) {
            this.mIsShowing = true;
            updateOverlayView(activity);
            Window window = activity.getWindow();
            View view = this.mOverlayView;
            window.addContentView(view, view.getLayoutParams());
            animateAndHideAfterDelay();
        }
    }

    @MainThread
    private void animateAndHideAfterDelay() {
        Animatable animatable = this.mOverlayDrawable;
        if (animatable instanceof Animatable) {
            animatable.start();
        }
        this.mMainThreadHandler.postDelayed(this.mHideRunnable, (long) this.mDurationMillis);
    }

    @VisibleForTesting
    @MainThread
    public void hide() {
        Animation fadeOut = AnimationUtils.loadAnimation(this.mOverlayView.getContext(), 17432577);
        fadeOut.setAnimationListener(new C04412());
        this.mOverlayView.startAnimation(fadeOut);
    }

    @MainThread
    private void updateOverlayView(Context context) {
        if (this.mOverlayView == null) {
            this.mOverlayView = LayoutInflater.from(context).inflate(C0395R.layout.overlay_confirmation, null);
        }
        this.mOverlayView.setOnTouchListener(this);
        this.mOverlayView.setLayoutParams(new LayoutParams(-1, -1));
        updateImageView(context, this.mOverlayView);
        updateMessageView(context, this.mOverlayView);
    }

    @MainThread
    private void updateMessageView(Context context, View overlayView) {
        TextView messageView = (TextView) overlayView.findViewById(C0395R.id.wearable_support_confirmation_overlay_message);
        if (this.mMessage != null) {
            int screenWidthPx = ResourcesUtil.getScreenWidthPx(context);
            int topMarginPx = ResourcesUtil.getFractionOfScreenPx(context, screenWidthPx, C0395R.fraction.confirmation_overlay_margin_above_text);
            int sideMarginPx = ResourcesUtil.getFractionOfScreenPx(context, screenWidthPx, C0395R.fraction.confirmation_overlay_margin_side);
            MarginLayoutParams layoutParams = (MarginLayoutParams) messageView.getLayoutParams();
            layoutParams.topMargin = topMarginPx;
            layoutParams.leftMargin = sideMarginPx;
            layoutParams.rightMargin = sideMarginPx;
            messageView.setLayoutParams(layoutParams);
            messageView.setText(this.mMessage);
            messageView.setVisibility(0);
            return;
        }
        messageView.setVisibility(8);
    }

    @MainThread
    private void updateImageView(Context context, View overlayView) {
        switch (this.mType) {
            case 0:
                this.mOverlayDrawable = ContextCompat.getDrawable(context, C0395R.drawable.generic_confirmation_animation);
                break;
            case 1:
                this.mOverlayDrawable = ContextCompat.getDrawable(context, C0395R.drawable.ic_full_sad);
                break;
            case 2:
                this.mOverlayDrawable = ContextCompat.getDrawable(context, C0395R.drawable.open_on_phone_animation);
                break;
            default:
                throw new IllegalStateException(String.format(Locale.US, "Invalid ConfirmationOverlay type [%d]", new Object[]{Integer.valueOf(this.mType)}));
        }
        ((ImageView) overlayView.findViewById(C0395R.id.wearable_support_confirmation_overlay_image)).setImageDrawable(this.mOverlayDrawable);
    }
}
