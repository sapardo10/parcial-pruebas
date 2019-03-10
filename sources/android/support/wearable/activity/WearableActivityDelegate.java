package android.support.wearable.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.wearable.internal.SharedLibraryVersion;
import android.util.Log;
import com.google.android.wearable.compat.WearableActivityController;
import java.io.FileDescriptor;
import java.io.PrintWriter;

@TargetApi(21)
public final class WearableActivityDelegate {
    private static volatile boolean sAmbientCallbacksVerifiedPresent;
    private static boolean sHasAutoResumeEnabledMethod;
    private static boolean sInitAutoResumeEnabledMethod;
    private final AmbientCallback mCallback;
    private WearableActivityController mWearableController;

    /* renamed from: android.support.wearable.activity.WearableActivityDelegate$1 */
    class C03961 extends com.google.android.wearable.compat.WearableActivityController.AmbientCallback {
        C03961() {
        }

        public void onEnterAmbient(Bundle ambientDetails) {
            WearableActivityDelegate.this.mCallback.onEnterAmbient(ambientDetails);
        }

        public void onUpdateAmbient() {
            WearableActivityDelegate.this.mCallback.onUpdateAmbient();
        }

        public void onExitAmbient() {
            WearableActivityDelegate.this.mCallback.onExitAmbient();
        }
    }

    public interface AmbientCallback {
        void onEnterAmbient(Bundle bundle);

        void onExitAmbient();

        void onUpdateAmbient();
    }

    public WearableActivityDelegate(AmbientCallback callback) {
        this.mCallback = callback;
    }

    public void onCreate(Activity activity) {
        initAmbientSupport(activity);
        WearableActivityController wearableActivityController = this.mWearableController;
        if (wearableActivityController != null) {
            wearableActivityController.onCreate();
        }
    }

    public void onResume() {
        WearableActivityController wearableActivityController = this.mWearableController;
        if (wearableActivityController != null) {
            wearableActivityController.onResume();
        }
    }

    public void onPause() {
        WearableActivityController wearableActivityController = this.mWearableController;
        if (wearableActivityController != null) {
            wearableActivityController.onPause();
        }
    }

    public void onStop() {
        WearableActivityController wearableActivityController = this.mWearableController;
        if (wearableActivityController != null) {
            wearableActivityController.onStop();
        }
    }

    public void onDestroy() {
        WearableActivityController wearableActivityController = this.mWearableController;
        if (wearableActivityController != null) {
            wearableActivityController.onDestroy();
        }
    }

    public void setAmbientEnabled() {
        WearableActivityController wearableActivityController = this.mWearableController;
        if (wearableActivityController != null) {
            wearableActivityController.setAmbientEnabled();
        }
    }

    public void setAutoResumeEnabled(boolean enabled) {
        if (this.mWearableController == null) {
            return;
        }
        if (hasSetAutoResumeEnabledMethod()) {
            this.mWearableController.setAutoResumeEnabled(enabled);
        }
    }

    public boolean isAmbient() {
        WearableActivityController wearableActivityController = this.mWearableController;
        if (wearableActivityController != null) {
            return wearableActivityController.isAmbient();
        }
        return false;
    }

    public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        WearableActivityController wearableActivityController = this.mWearableController;
        if (wearableActivityController != null) {
            wearableActivityController.dump(prefix, fd, writer, args);
        }
    }

    private void initAmbientSupport(Activity activity) {
        if (VERSION.SDK_INT > 21) {
            SharedLibraryVersion.verifySharedLibraryPresent();
            com.google.android.wearable.compat.WearableActivityController.AmbientCallback callbackBridge = new C03961();
            String str = "WearActivity[";
            String valueOf = String.valueOf(getClass().getSimpleName());
            str = valueOf.length() != 0 ? str.concat(valueOf) : new String(str);
            this.mWearableController = new WearableActivityController(String.valueOf(str.substring(0, Math.min(str.length(), 22))).concat("]"), activity, callbackBridge);
            verifyAmbientCallbacksPresent();
        }
    }

    private static void verifyAmbientCallbacksPresent() {
        if (!sAmbientCallbacksVerifiedPresent) {
            try {
                String str = ".onEnterAmbient";
                String str2 = ".";
                String valueOf = String.valueOf(com.google.android.wearable.compat.WearableActivityController.AmbientCallback.class.getDeclaredMethod("onEnterAmbient", new Class[]{Bundle.class}).getName());
                if (str.equals(valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2))) {
                    sAmbientCallbacksVerifiedPresent = true;
                    return;
                }
                throw new NoSuchMethodException();
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException("Could not find a required method for ambient support, likely due to proguard optimization. Please add com.google.android.wearable:wearable jar to the list of library jars for your project");
            }
        }
    }

    private boolean hasSetAutoResumeEnabledMethod() {
        if (!sInitAutoResumeEnabledMethod) {
            sInitAutoResumeEnabledMethod = true;
            try {
                String str = ".setAutoResumeEnabled";
                String str2 = ".";
                String valueOf = String.valueOf(WearableActivityController.class.getDeclaredMethod("setAutoResumeEnabled", new Class[]{Boolean.TYPE}).getName());
                if (str.equals(valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2))) {
                    sHasAutoResumeEnabledMethod = true;
                } else {
                    throw new NoSuchMethodException();
                }
            } catch (NoSuchMethodException e) {
                Log.w("WearableActivity", "Could not find a required method for auto-resume support, likely due to proguard optimization. Please add com.google.android.wearable:wearable jar to the list of library jars for your project");
                sHasAutoResumeEnabledMethod = false;
            }
        }
        return sHasAutoResumeEnabledMethod;
    }
}
