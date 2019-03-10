package de.greenrobot.event.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import de.greenrobot.event.EventBus;

public class ErrorDialogManager {
    public static final String KEY_EVENT_TYPE_ON_CLOSE = "de.greenrobot.eventbus.errordialog.event_type_on_close";
    public static final String KEY_FINISH_AFTER_DIALOG = "de.greenrobot.eventbus.errordialog.finish_after_dialog";
    public static final String KEY_ICON_ID = "de.greenrobot.eventbus.errordialog.icon_id";
    public static final String KEY_MESSAGE = "de.greenrobot.eventbus.errordialog.message";
    public static final String KEY_TITLE = "de.greenrobot.eventbus.errordialog.title";
    protected static final String TAG_ERROR_DIALOG = "de.greenrobot.eventbus.error_dialog";
    protected static final String TAG_ERROR_DIALOG_MANAGER = "de.greenrobot.eventbus.error_dialog_manager";
    public static ErrorDialogFragmentFactory<?> factory;

    @TargetApi(11)
    public static class HoneycombManagerFragment extends Fragment {
        protected Bundle argumentsForErrorDialog;
        private EventBus eventBus;
        private Object executionScope;
        protected boolean finishAfterDialog;

        public void onResume() {
            super.onResume();
            this.eventBus = ErrorDialogManager.factory.config.getEventBus();
            this.eventBus.register(this);
        }

        public void onPause() {
            this.eventBus.unregister(this);
            super.onPause();
        }

        public void onEventMainThread(ThrowableFailureEvent event) {
            if (ErrorDialogManager.isInExecutionScope(this.executionScope, event)) {
                ErrorDialogManager.checkLogException(event);
                FragmentManager fm = getFragmentManager();
                fm.executePendingTransactions();
                DialogFragment existingFragment = (DialogFragment) fm.findFragmentByTag(ErrorDialogManager.TAG_ERROR_DIALOG);
                if (existingFragment != null) {
                    existingFragment.dismiss();
                }
                DialogFragment errorFragment = (DialogFragment) ErrorDialogManager.factory.prepareErrorFragment(event, this.finishAfterDialog, this.argumentsForErrorDialog);
                if (errorFragment != null) {
                    errorFragment.show(fm, ErrorDialogManager.TAG_ERROR_DIALOG);
                }
            }
        }

        public static void attachTo(Activity activity, Object executionScope, boolean finishAfterDialog, Bundle argumentsForErrorDialog) {
            FragmentManager fm = activity.getFragmentManager();
            HoneycombManagerFragment fragment = (HoneycombManagerFragment) fm.findFragmentByTag(ErrorDialogManager.TAG_ERROR_DIALOG_MANAGER);
            if (fragment == null) {
                fragment = new HoneycombManagerFragment();
                fm.beginTransaction().add(fragment, ErrorDialogManager.TAG_ERROR_DIALOG_MANAGER).commit();
                fm.executePendingTransactions();
            }
            fragment.finishAfterDialog = finishAfterDialog;
            fragment.argumentsForErrorDialog = argumentsForErrorDialog;
            fragment.executionScope = executionScope;
        }
    }

    public static class SupportManagerFragment extends android.support.v4.app.Fragment {
        protected Bundle argumentsForErrorDialog;
        private EventBus eventBus;
        private Object executionScope;
        protected boolean finishAfterDialog;
        private boolean skipRegisterOnNextResume;

        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.eventBus = ErrorDialogManager.factory.config.getEventBus();
            this.eventBus.register(this);
            this.skipRegisterOnNextResume = true;
        }

        public void onResume() {
            super.onResume();
            if (this.skipRegisterOnNextResume) {
                this.skipRegisterOnNextResume = false;
                return;
            }
            this.eventBus = ErrorDialogManager.factory.config.getEventBus();
            this.eventBus.register(this);
        }

        public void onPause() {
            this.eventBus.unregister(this);
            super.onPause();
        }

        public void onEventMainThread(ThrowableFailureEvent event) {
            if (ErrorDialogManager.isInExecutionScope(this.executionScope, event)) {
                ErrorDialogManager.checkLogException(event);
                android.support.v4.app.FragmentManager fm = getFragmentManager();
                fm.executePendingTransactions();
                android.support.v4.app.DialogFragment existingFragment = (android.support.v4.app.DialogFragment) fm.findFragmentByTag(ErrorDialogManager.TAG_ERROR_DIALOG);
                if (existingFragment != null) {
                    existingFragment.dismiss();
                }
                android.support.v4.app.DialogFragment errorFragment = (android.support.v4.app.DialogFragment) ErrorDialogManager.factory.prepareErrorFragment(event, this.finishAfterDialog, this.argumentsForErrorDialog);
                if (errorFragment != null) {
                    errorFragment.show(fm, ErrorDialogManager.TAG_ERROR_DIALOG);
                }
            }
        }

        public static void attachTo(Activity activity, Object executionScope, boolean finishAfterDialog, Bundle argumentsForErrorDialog) {
            android.support.v4.app.FragmentManager fm = ((FragmentActivity) activity).getSupportFragmentManager();
            SupportManagerFragment fragment = (SupportManagerFragment) fm.findFragmentByTag(ErrorDialogManager.TAG_ERROR_DIALOG_MANAGER);
            if (fragment == null) {
                fragment = new SupportManagerFragment();
                fm.beginTransaction().add((android.support.v4.app.Fragment) fragment, ErrorDialogManager.TAG_ERROR_DIALOG_MANAGER).commit();
                fm.executePendingTransactions();
            }
            fragment.finishAfterDialog = finishAfterDialog;
            fragment.argumentsForErrorDialog = argumentsForErrorDialog;
            fragment.executionScope = executionScope;
        }
    }

    private static boolean isSupportActivity(android.app.Activity r6) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:26:0x008a in {4, 13, 15, 20, 22, 23, 25} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r0 = 0;
        r1 = r6.getClass();
        r1 = r1.getSuperclass();
    L_0x0009:
        if (r1 == 0) goto L_0x006f;
    L_0x000b:
        r2 = r1.getName();
        r3 = "android.support.v4.app.FragmentActivity";
        r3 = r2.equals(r3);
        if (r3 == 0) goto L_0x0019;
    L_0x0017:
        r0 = 1;
        goto L_0x0060;
    L_0x0019:
        r3 = "com.actionbarsherlock.app";
        r3 = r2.startsWith(r3);
        if (r3 == 0) goto L_0x0051;
    L_0x0021:
        r3 = ".SherlockActivity";
        r3 = r2.endsWith(r3);
        if (r3 != 0) goto L_0x003a;
    L_0x0029:
        r3 = ".SherlockListActivity";
        r3 = r2.endsWith(r3);
        if (r3 != 0) goto L_0x003a;
    L_0x0031:
        r3 = ".SherlockPreferenceActivity";
        r3 = r2.endsWith(r3);
        if (r3 != 0) goto L_0x003a;
    L_0x0039:
        goto L_0x0051;
    L_0x003a:
        r3 = new java.lang.RuntimeException;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "Please use SherlockFragmentActivity. Illegal activity: ";
        r4.append(r5);
        r4.append(r2);
        r4 = r4.toString();
        r3.<init>(r4);
        throw r3;
        r3 = "android.app.Activity";
        r3 = r2.equals(r3);
        if (r3 == 0) goto L_0x0069;
    L_0x005a:
        r3 = android.os.Build.VERSION.SDK_INT;
        r4 = 11;
        if (r3 < r4) goto L_0x0061;
    L_0x0060:
        return r0;
    L_0x0061:
        r3 = new java.lang.RuntimeException;
        r4 = "Illegal activity without fragment support. Either use Android 3.0+ or android.support.v4.app.FragmentActivity.";
        r3.<init>(r4);
        throw r3;
        r1 = r1.getSuperclass();
        goto L_0x0009;
    L_0x006f:
        r2 = new java.lang.RuntimeException;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "Illegal activity type: ";
        r3.append(r4);
        r4 = r6.getClass();
        r3.append(r4);
        r3 = r3.toString();
        r2.<init>(r3);
        throw r2;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: de.greenrobot.event.util.ErrorDialogManager.isSupportActivity(android.app.Activity):boolean");
    }

    public static void attachTo(Activity activity) {
        attachTo(activity, false, null);
    }

    public static void attachTo(Activity activity, boolean finishAfterDialog) {
        attachTo(activity, finishAfterDialog, null);
    }

    public static void attachTo(Activity activity, boolean finishAfterDialog, Bundle argumentsForErrorDialog) {
        attachTo(activity, activity.getClass(), finishAfterDialog, argumentsForErrorDialog);
    }

    public static void attachTo(Activity activity, Object executionScope, boolean finishAfterDialog, Bundle argumentsForErrorDialog) {
        if (factory == null) {
            throw new RuntimeException("You must set the static factory field to configure error dialogs for your app.");
        } else if (isSupportActivity(activity)) {
            SupportManagerFragment.attachTo(activity, executionScope, finishAfterDialog, argumentsForErrorDialog);
        } else {
            HoneycombManagerFragment.attachTo(activity, executionScope, finishAfterDialog, argumentsForErrorDialog);
        }
    }

    protected static void checkLogException(ThrowableFailureEvent event) {
        if (factory.config.logExceptions) {
            String tag = factory.config.tagForLoggingExceptions;
            if (tag == null) {
                tag = EventBus.TAG;
            }
            Log.i(tag, "Error dialog manager received exception", event.throwable);
        }
    }

    private static boolean isInExecutionScope(Object executionScope, ThrowableFailureEvent event) {
        if (event != null) {
            Object eventExecutionScope = event.getExecutionScope();
            if (eventExecutionScope != null && !eventExecutionScope.equals(executionScope)) {
                return false;
            }
        }
        return true;
    }
}
