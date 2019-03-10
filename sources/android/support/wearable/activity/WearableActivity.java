package android.support.wearable.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.wearable.activity.WearableActivityDelegate.AmbientCallback;
import android.util.Log;
import java.io.FileDescriptor;
import java.io.PrintWriter;

@TargetApi(21)
public abstract class WearableActivity extends Activity {
    public static final String EXTRA_BURN_IN_PROTECTION = "com.google.android.wearable.compat.extra.BURN_IN_PROTECTION";
    public static final String EXTRA_LOWBIT_AMBIENT = "com.google.android.wearable.compat.extra.LOWBIT_AMBIENT";
    private static final String TAG = "WearableActivity";
    private final AmbientCallback callback = new C09091();
    private final WearableActivityDelegate mDelegate = new WearableActivityDelegate(this.callback);
    private boolean mSuperCalled;

    /* renamed from: android.support.wearable.activity.WearableActivity$1 */
    class C09091 implements AmbientCallback {
        C09091() {
        }

        public void onEnterAmbient(Bundle ambientDetails) {
            WearableActivity.this.mSuperCalled = false;
            WearableActivity.this.onEnterAmbient(ambientDetails);
            if (!WearableActivity.this.mSuperCalled) {
                String str = WearableActivity.TAG;
                String valueOf = String.valueOf(WearableActivity.this);
                StringBuilder stringBuilder = new StringBuilder(String.valueOf(valueOf).length() + 56);
                stringBuilder.append("Activity ");
                stringBuilder.append(valueOf);
                stringBuilder.append(" did not call through to super.onEnterAmbient()");
                Log.w(str, stringBuilder.toString());
            }
        }

        public void onExitAmbient() {
            WearableActivity.this.mSuperCalled = false;
            WearableActivity.this.onExitAmbient();
            if (!WearableActivity.this.mSuperCalled) {
                String str = WearableActivity.TAG;
                String valueOf = String.valueOf(WearableActivity.this);
                StringBuilder stringBuilder = new StringBuilder(String.valueOf(valueOf).length() + 55);
                stringBuilder.append("Activity ");
                stringBuilder.append(valueOf);
                stringBuilder.append(" did not call through to super.onExitAmbient()");
                Log.w(str, stringBuilder.toString());
            }
        }

        public void onUpdateAmbient() {
            WearableActivity.this.mSuperCalled = false;
            WearableActivity.this.onUpdateAmbient();
            if (!WearableActivity.this.mSuperCalled) {
                String str = WearableActivity.TAG;
                String valueOf = String.valueOf(WearableActivity.this);
                StringBuilder stringBuilder = new StringBuilder(String.valueOf(valueOf).length() + 57);
                stringBuilder.append("Activity ");
                stringBuilder.append(valueOf);
                stringBuilder.append(" did not call through to super.onUpdateAmbient()");
                Log.w(str, stringBuilder.toString());
            }
        }
    }

    @CallSuper
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mDelegate.onCreate(this);
    }

    @CallSuper
    protected void onResume() {
        super.onResume();
        this.mDelegate.onResume();
    }

    @CallSuper
    protected void onPause() {
        this.mDelegate.onPause();
        super.onPause();
    }

    @CallSuper
    protected void onStop() {
        this.mDelegate.onStop();
        super.onStop();
    }

    @CallSuper
    protected void onDestroy() {
        this.mDelegate.onDestroy();
        super.onDestroy();
    }

    public final void setAmbientEnabled() {
        this.mDelegate.setAmbientEnabled();
    }

    public final void setAutoResumeEnabled(boolean enabled) {
        this.mDelegate.setAutoResumeEnabled(enabled);
    }

    public final boolean isAmbient() {
        return this.mDelegate.isAmbient();
    }

    public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        this.mDelegate.dump(prefix, fd, writer, args);
    }

    @CallSuper
    public void onEnterAmbient(Bundle ambientDetails) {
        this.mSuperCalled = true;
    }

    @CallSuper
    public void onUpdateAmbient() {
        this.mSuperCalled = true;
    }

    @CallSuper
    public void onExitAmbient() {
        this.mSuperCalled = true;
    }
}
