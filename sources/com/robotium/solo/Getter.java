package com.robotium.solo;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import junit.framework.Assert;

class Getter {
    private final int TIMEOUT = 1000;
    private final ActivityUtils activityUtils;
    private final Instrumentation instrumentation;
    private final Waiter waiter;

    public Getter(Instrumentation instrumentation, ActivityUtils activityUtils, Waiter waiter) {
        this.instrumentation = instrumentation;
        this.activityUtils = activityUtils;
        this.waiter = waiter;
    }

    public <T extends View> T getView(Class<T> classToFilterBy, int index) {
        return this.waiter.waitForAndGetView(index, classToFilterBy);
    }

    public <T extends TextView> T getView(Class<T> classToFilterBy, String text, boolean onlyVisible) {
        T viewToReturn = this.waiter.waitForText(classToFilterBy, text, 0, (long) Timeout.getSmallTimeout(), false, onlyVisible, false);
        if (viewToReturn == null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(classToFilterBy.getSimpleName());
            stringBuilder.append(" with text: '");
            stringBuilder.append(text);
            stringBuilder.append("' is not found!");
            Assert.fail(stringBuilder.toString());
        }
        return viewToReturn;
    }

    public String getString(int id) {
        Activity activity = this.activityUtils.getCurrentActivity(false);
        if (activity == null) {
            return "";
        }
        return activity.getString(id);
    }

    public String getString(String id) {
        Context targetContext = this.instrumentation.getTargetContext();
        int viewId = targetContext.getResources().getIdentifier(id, "string", targetContext.getPackageName());
        if (viewId == 0) {
            viewId = targetContext.getResources().getIdentifier(id, "string", SystemMediaRouteProvider.PACKAGE_NAME);
        }
        return getString(viewId);
    }

    public View getView(int id, int index, int timeout) {
        return this.waiter.waitForView(id, index, timeout);
    }

    public View getView(int id, int index) {
        return getView(id, index, 0);
    }

    public View getView(String id, int index) {
        View viewToReturn = null;
        Context targetContext = this.instrumentation.getTargetContext();
        int viewId = targetContext.getResources().getIdentifier(id, "id", targetContext.getPackageName());
        if (viewId != 0) {
            viewToReturn = getView(viewId, index, 1000);
        }
        if (viewToReturn == null) {
            int androidViewId = targetContext.getResources().getIdentifier(id, "id", SystemMediaRouteProvider.PACKAGE_NAME);
            if (androidViewId != 0) {
                viewToReturn = getView(androidViewId, index, 1000);
            }
        }
        if (viewToReturn != null) {
            return viewToReturn;
        }
        return getView(viewId, index);
    }

    public View getView(Object tag, int index, int timeout) {
        if (tag == null) {
            return null;
        }
        Activity activity = this.activityUtils.getCurrentActivity(false);
        View viewToReturn = null;
        if (index < 1) {
            index = 0;
            if (activity != null) {
                Window window = activity.getWindow();
                if (window != null) {
                    View decorView = window.getDecorView();
                    if (decorView != null) {
                        viewToReturn = decorView.findViewWithTag(tag);
                    }
                }
            }
        }
        if (viewToReturn != null) {
            return viewToReturn;
        }
        return this.waiter.waitForView(tag, index, timeout);
    }

    public View getView(Object tag, int index) {
        return getView(tag, index, 0);
    }
}
