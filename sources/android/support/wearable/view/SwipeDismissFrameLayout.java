package android.support.wearable.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.UiThread;
import android.support.wearable.internal.view.SwipeDismissLayout;
import android.support.wearable.internal.view.SwipeDismissLayout.OnDismissedListener;
import android.support.wearable.internal.view.SwipeDismissLayout.OnPreSwipeListener;
import android.support.wearable.internal.view.SwipeDismissLayout.OnSwipeProgressChangedListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import java.util.ArrayList;
import java.util.Iterator;

@TargetApi(20)
@Deprecated
public class SwipeDismissFrameLayout extends SwipeDismissLayout {
    private static final float DEFAULT_INTERPOLATION_FACTOR = 1.5f;
    private static final String TAG = "SwipeDismissFrameLayout";
    private static final float TRANSLATION_MIN_ALPHA = 0.5f;
    private final int mAnimationTime;
    private final ArrayList<Callback> mCallbacks;
    private final DecelerateInterpolator mCancelInterpolator;
    private final DecelerateInterpolator mCompleteDismissGestureInterpolator;
    private final AccelerateInterpolator mDismissInterpolator;
    private final OnDismissedListener mOnDismissedListener;
    private final OnPreSwipeListener mOnPreSwipeListener;
    private final OnSwipeProgressChangedListener mOnSwipeProgressListener;
    private boolean mStarted;

    public static abstract class Callback {
        @UiThread
        public boolean onPreSwipeStart(float xDown, float yDown) {
            return true;
        }

        @UiThread
        public void onSwipeStart() {
        }

        @UiThread
        public void onSwipeCancelled() {
        }

        @UiThread
        public void onDismissed(SwipeDismissFrameLayout layout) {
        }
    }

    private final class MyOnDismissedListener implements OnDismissedListener {

        /* renamed from: android.support.wearable.view.SwipeDismissFrameLayout$MyOnDismissedListener$1 */
        class C04551 implements Runnable {
            C04551() {
            }

            public void run() {
                for (int i = SwipeDismissFrameLayout.this.mCallbacks.size() - 1; i >= 0; i--) {
                    ((Callback) SwipeDismissFrameLayout.this.mCallbacks.get(i)).onDismissed(SwipeDismissFrameLayout.this);
                }
            }
        }

        private MyOnDismissedListener() {
        }

        @SuppressLint({"NewApi"})
        public void onDismissed(SwipeDismissLayout layout) {
            if (Log.isLoggable(SwipeDismissFrameLayout.TAG, 3)) {
                Log.d(SwipeDismissFrameLayout.TAG, "onDismissed()");
            }
            SwipeDismissFrameLayout.this.animate().translationX((float) SwipeDismissFrameLayout.this.getWidth()).alpha(0.0f).setDuration((long) SwipeDismissFrameLayout.this.mAnimationTime).setInterpolator(SwipeDismissFrameLayout.this.mStarted ? SwipeDismissFrameLayout.this.mCompleteDismissGestureInterpolator : SwipeDismissFrameLayout.this.mDismissInterpolator).withEndAction(new C04551());
        }
    }

    private final class MyOnPreSwipeListener implements OnPreSwipeListener {
        private MyOnPreSwipeListener() {
        }

        public boolean onPreSwipe(float xDown, float yDown) {
            Iterator it = SwipeDismissFrameLayout.this.mCallbacks.iterator();
            while (it.hasNext()) {
                if (!((Callback) it.next()).onPreSwipeStart(xDown, yDown)) {
                    return false;
                }
            }
            return true;
        }
    }

    private final class MyOnSwipeProgressChangedListener implements OnSwipeProgressChangedListener {

        /* renamed from: android.support.wearable.view.SwipeDismissFrameLayout$MyOnSwipeProgressChangedListener$1 */
        class C04561 implements Runnable {
            C04561() {
            }

            public void run() {
                for (int i = SwipeDismissFrameLayout.this.mCallbacks.size() - 1; i >= 0; i--) {
                    ((Callback) SwipeDismissFrameLayout.this.mCallbacks.get(i)).onSwipeCancelled();
                }
            }
        }

        private MyOnSwipeProgressChangedListener() {
        }

        public void onSwipeProgressChanged(SwipeDismissLayout layout, float progress, float translate) {
            if (Log.isLoggable(SwipeDismissFrameLayout.TAG, 3)) {
                String str = SwipeDismissFrameLayout.TAG;
                StringBuilder stringBuilder = new StringBuilder(42);
                stringBuilder.append("onSwipeProgressChanged() - ");
                stringBuilder.append(translate);
                Log.d(str, stringBuilder.toString());
            }
            SwipeDismissFrameLayout.this.setTranslationX(translate);
            SwipeDismissFrameLayout.this.setAlpha(1.0f - (SwipeDismissFrameLayout.TRANSLATION_MIN_ALPHA * progress));
            if (!SwipeDismissFrameLayout.this.mStarted) {
                for (int i = SwipeDismissFrameLayout.this.mCallbacks.size() - 1; i >= 0; i--) {
                    ((Callback) SwipeDismissFrameLayout.this.mCallbacks.get(i)).onSwipeStart();
                }
                SwipeDismissFrameLayout.this.mStarted = true;
            }
        }

        public void onSwipeCancelled(SwipeDismissLayout layout) {
            if (Log.isLoggable(SwipeDismissFrameLayout.TAG, 3)) {
                Log.d(SwipeDismissFrameLayout.TAG, "onSwipeCancelled() run swipe cancel animation");
            }
            SwipeDismissFrameLayout.this.mStarted = false;
            SwipeDismissFrameLayout.this.animate().translationX(0.0f).alpha(1.0f).setDuration((long) SwipeDismissFrameLayout.this.mAnimationTime).setInterpolator(SwipeDismissFrameLayout.this.mCancelInterpolator).withEndAction(new C04561());
        }
    }

    @UiThread
    public SwipeDismissFrameLayout(Context context) {
        this(context, null, 0);
    }

    @UiThread
    public SwipeDismissFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @UiThread
    public SwipeDismissFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mOnPreSwipeListener = new MyOnPreSwipeListener();
        this.mOnDismissedListener = new MyOnDismissedListener();
        this.mOnSwipeProgressListener = new MyOnSwipeProgressChangedListener();
        this.mCallbacks = new ArrayList();
        setOnPreSwipeListener(this.mOnPreSwipeListener);
        setOnDismissedListener(this.mOnDismissedListener);
        setOnSwipeProgressChangedListener(this.mOnSwipeProgressListener);
        this.mAnimationTime = getContext().getResources().getInteger(17694720);
        this.mCancelInterpolator = new DecelerateInterpolator(DEFAULT_INTERPOLATION_FACTOR);
        this.mDismissInterpolator = new AccelerateInterpolator(DEFAULT_INTERPOLATION_FACTOR);
        this.mCompleteDismissGestureInterpolator = new DecelerateInterpolator(DEFAULT_INTERPOLATION_FACTOR);
    }

    @UiThread
    public void setDismissEnabled(boolean isDismissEnabled) {
        setSwipeable(isDismissEnabled);
    }

    @UiThread
    public boolean isDismissEnabled() {
        return isSwipeable();
    }

    @UiThread
    public void addCallback(Callback callback) {
        if (callback != null) {
            this.mCallbacks.add(callback);
            return;
        }
        throw new NullPointerException("addCallback called with null callback");
    }

    @UiThread
    public void removeCallback(Callback callback) {
        if (callback == null) {
            throw new NullPointerException("removeCallback called with null callback");
        } else if (!this.mCallbacks.remove(callback)) {
            throw new IllegalStateException("removeCallback called with nonexistent callback");
        }
    }

    @UiThread
    public void reset() {
        animate().cancel();
        setTranslationX(0.0f);
        setAlpha(1.0f);
        this.mStarted = false;
    }

    @UiThread
    public void dismiss(boolean decelerate) {
        for (int i = this.mCallbacks.size() - 1; i >= 0; i--) {
            ((Callback) this.mCallbacks.get(i)).onSwipeStart();
        }
        if (getVisibility() == 0) {
            if (decelerate) {
                this.mStarted = true;
            }
            this.mOnDismissedListener.onDismissed(this);
        }
    }
}
