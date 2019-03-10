package android.support.wearable.view.drawer;

import android.annotation.TargetApi;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.VisibleForTesting;
import android.view.View;

@TargetApi(23)
@Deprecated
public class FlingWatcher {
    private static final int POLLING_DELAY_MS = 100;
    private final Runnable mCheckForChangeRunnable = new C04621();
    private final FlingListener mFlingListener;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private boolean mIsRunning = false;
    private int mLastScrollY;
    private View mView;

    /* renamed from: android.support.wearable.view.drawer.FlingWatcher$1 */
    class C04621 implements Runnable {
        C04621() {
        }

        public void run() {
            FlingWatcher.this.checkForChange();
        }
    }

    public interface FlingListener {
        void onFlingComplete(View view);
    }

    public FlingWatcher(FlingListener flingListener) {
        this.mFlingListener = flingListener;
    }

    public void start(View view) {
        if (!this.mIsRunning) {
            this.mIsRunning = true;
            this.mView = view;
            this.mLastScrollY = view.getScrollY();
            scheduleNextCheckForChange();
        }
    }

    @VisibleForTesting
    void scheduleNextCheckForChange() {
        this.mHandler.postDelayed(this.mCheckForChangeRunnable, 100);
    }

    @VisibleForTesting
    void checkForChange() {
        if (this.mIsRunning) {
            int currentScrollY = this.mView;
            if (currentScrollY != 0) {
                currentScrollY = currentScrollY.getScrollY();
                if (currentScrollY == this.mLastScrollY) {
                    this.mIsRunning = false;
                    this.mFlingListener.onFlingComplete(this.mView);
                } else {
                    this.mLastScrollY = currentScrollY;
                    scheduleNextCheckForChange();
                }
            }
        }
    }
}
