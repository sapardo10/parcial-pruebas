package android.support.wearable.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager.OnBackStackChangedListener;
import android.app.FragmentTransaction;
import android.os.Build.VERSION;
import android.support.annotation.IdRes;
import android.util.Log;
import android.view.View;

@TargetApi(23)
public class FullscreenFragmentHelper {
    public static final String TAG = "FullscreenFragHelper";
    private final Activity mActivity;
    private final int mContainerId;

    /* renamed from: android.support.wearable.view.FullscreenFragmentHelper$1 */
    class C04461 implements OnBackStackChangedListener {
        C04461() {
        }

        public void onBackStackChanged() {
            if (FullscreenFragmentHelper.this.mActivity.getFragmentManager().getBackStackEntryCount() == 0) {
                FullscreenFragmentHelper.this.mActivity.finish();
            } else {
                FullscreenFragmentHelper.this.focusCurrentFragment();
            }
        }
    }

    public FullscreenFragmentHelper(Activity activity, @IdRes int containerId) {
        this.mActivity = activity;
        this.mContainerId = containerId;
        activity.getFragmentManager().addOnBackStackChangedListener(new C04461());
    }

    private void focusCurrentFragment() {
        Fragment curFragment = this.mActivity.getFragmentManager().findFragmentById(this.mContainerId);
        if (Log.isLoggable(TAG, 3)) {
            String str = TAG;
            String valueOf = String.valueOf(curFragment);
            StringBuilder stringBuilder = new StringBuilder(String.valueOf(valueOf).length() + 22);
            stringBuilder.append("focusCurrentFragment: ");
            stringBuilder.append(valueOf);
            Log.d(str, stringBuilder.toString());
        }
        if (curFragment != null) {
            View view = curFragment.getView();
            if (view != null) {
                view.setImportantForAccessibility(1);
            } else if (Log.isLoggable(TAG, 5)) {
                Log.w(TAG, "Could not load root view of fragment");
            }
        }
        this.mActivity.getWindow().getDecorView().sendAccessibilityEvent(32);
    }

    public void showFragment(Fragment fragment) {
        Fragment curFragment = this.mActivity.getFragmentManager().findFragmentById(this.mContainerId);
        if (curFragment != null && curFragment.getView() != null) {
            curFragment.getView().setImportantForAccessibility(4);
        }
        this.mActivity.getFragmentManager().beginTransaction().add(this.mContainerId, fragment).addToBackStack(null).commit();
    }

    public void removeFragment(Fragment fragment) {
        if (!this.mActivity.isFinishing()) {
            if (!this.mActivity.isDestroyed()) {
                FragmentTransaction transaction = this.mActivity.getFragmentManager().beginTransaction().remove(fragment);
                if (VERSION.SDK_INT > 23) {
                    transaction.commitNow();
                } else {
                    transaction.commit();
                }
                focusCurrentFragment();
            }
        }
    }
}
