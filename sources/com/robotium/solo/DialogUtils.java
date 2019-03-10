package com.robotium.solo;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.os.SystemClock;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

class DialogUtils {
    private static final int TIMEOUT_DIALOG_TO_CLOSE = 1000;
    private final int MINISLEEP = 200;
    private final ActivityUtils activityUtils;
    private final Instrumentation instrumentation;
    private final Sleeper sleeper;
    private final ViewFetcher viewFetcher;

    public DialogUtils(Instrumentation instrumentation, ActivityUtils activityUtils, ViewFetcher viewFetcher, Sleeper sleeper) {
        this.instrumentation = instrumentation;
        this.activityUtils = activityUtils;
        this.viewFetcher = viewFetcher;
        this.sleeper = sleeper;
    }

    public boolean waitForDialogToClose(long timeout) {
        waitForDialogToOpen(1000, false);
        long endTime = SystemClock.uptimeMillis() + timeout;
        while (SystemClock.uptimeMillis() < endTime) {
            if (!isDialogOpen()) {
                return true;
            }
            this.sleeper.sleep(200);
        }
        return false;
    }

    public boolean waitForDialogToOpen(long timeout, boolean sleepFirst) {
        long endTime = SystemClock.uptimeMillis() + timeout;
        boolean dialogIsOpen = isDialogOpen();
        if (sleepFirst) {
            this.sleeper.sleep();
        }
        if (dialogIsOpen) {
            return true;
        }
        while (SystemClock.uptimeMillis() < endTime) {
            if (isDialogOpen()) {
                return true;
            }
            this.sleeper.sleepMini();
        }
        return false;
    }

    private boolean isDialogOpen() {
        Activity activity = this.activityUtils.getCurrentActivity(false);
        View[] views = this.viewFetcher.getWindowDecorViews();
        if (isDialog(activity, this.viewFetcher.getRecentDecorView(views))) {
            return true;
        }
        for (View v : views) {
            if (isDialog(activity, v)) {
                return true;
            }
        }
        return false;
    }

    private boolean isDialog(Activity activity, View decorView) {
        boolean z = false;
        if (decorView != null && decorView.isShown()) {
            if (activity != null) {
                Context viewContext = null;
                if (decorView != null) {
                    viewContext = decorView.getContext();
                }
                if (viewContext instanceof ContextThemeWrapper) {
                    viewContext = ((ContextThemeWrapper) viewContext).getBaseContext();
                }
                Context activityContext = activity;
                Context activityBaseContext = activity.getBaseContext();
                if ((activityContext.equals(viewContext) || activityBaseContext.equals(viewContext)) && decorView != activity.getWindow().getDecorView()) {
                    z = true;
                }
                return z;
            }
        }
        return false;
    }

    public void hideSoftKeyboard(EditText editText, boolean shouldSleepFirst, boolean shouldSleepAfter) {
        InputMethodManager inputMethodManager;
        Activity activity = this.activityUtils.getCurrentActivity(shouldSleepFirst);
        if (activity == null) {
            inputMethodManager = (InputMethodManager) this.instrumentation.getTargetContext().getSystemService("input_method");
        } else {
            inputMethodManager = (InputMethodManager) activity.getSystemService("input_method");
        }
        if (editText != null) {
            inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            return;
        }
        View focusedView = activity.getCurrentFocus();
        if (!(focusedView instanceof EditText)) {
            ViewFetcher viewFetcher = this.viewFetcher;
            View freshestEditText = (EditText) viewFetcher.getFreshestView(viewFetcher.getCurrentViews(EditText.class, true));
            if (freshestEditText != null) {
                focusedView = freshestEditText;
            }
        }
        if (focusedView != null) {
            inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        }
        if (shouldSleepAfter) {
            this.sleeper.sleep();
        }
    }
}
