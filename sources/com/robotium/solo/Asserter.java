package com.robotium.solo;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import junit.framework.Assert;

class Asserter {
    private final ActivityUtils activityUtils;
    private final Waiter waiter;

    public Asserter(ActivityUtils activityUtils, Waiter waiter) {
        this.activityUtils = activityUtils;
        this.waiter = waiter;
    }

    public void assertCurrentActivity(String message, String name) {
        if (!this.waiter.waitForActivity(name)) {
            Activity activity = this.activityUtils.getCurrentActivity();
            if (activity != null) {
                Assert.assertEquals(message, name, activity.getClass().getSimpleName());
            } else {
                Assert.assertEquals(message, name, "No actvity found");
            }
        }
    }

    public void assertCurrentActivity(String message, Class<? extends Activity> expectedClass) {
        if (expectedClass == null) {
            Assert.fail("The specified Activity is null!");
        }
        if (!this.waiter.waitForActivity((Class) expectedClass)) {
            Activity activity = this.activityUtils.getCurrentActivity();
            if (activity != null) {
                Assert.assertEquals(message, expectedClass.getName(), activity.getClass().getName());
            } else {
                Assert.assertEquals(message, expectedClass.getName(), "No activity found");
            }
        }
    }

    public void assertCurrentActivity(String message, String name, boolean isNewInstance) {
        assertCurrentActivity(message, name);
        Activity activity = this.activityUtils.getCurrentActivity();
        if (activity != null) {
            assertCurrentActivity(message, activity.getClass(), isNewInstance);
        }
    }

    public void assertCurrentActivity(String message, Class<? extends Activity> expectedClass, boolean isNewInstance) {
        boolean found = false;
        assertCurrentActivity(message, (Class) expectedClass);
        Activity activity = this.activityUtils.getCurrentActivity(false);
        if (activity == null) {
            Assert.assertNotSame(message, Boolean.valueOf(isNewInstance), Boolean.valueOf(false));
            return;
        }
        for (int i = 0; i < this.activityUtils.getAllOpenedActivities().size() - 1; i++) {
            if (((Activity) this.activityUtils.getAllOpenedActivities().get(i)).toString().equals(activity.toString())) {
                found = true;
            }
        }
        Assert.assertNotSame(message, Boolean.valueOf(isNewInstance), Boolean.valueOf(found));
    }

    public void assertMemoryNotLow() {
        MemoryInfo mi = new MemoryInfo();
        ((ActivityManager) this.activityUtils.getCurrentActivity().getSystemService("activity")).getMemoryInfo(mi);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Low memory available: ");
        stringBuilder.append(mi.availMem);
        stringBuilder.append(" bytes!");
        Assert.assertFalse(stringBuilder.toString(), mi.lowMemory);
    }
}
