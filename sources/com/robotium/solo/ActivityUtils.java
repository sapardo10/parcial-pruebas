package com.robotium.solo;

import android.app.Activity;
import android.app.Instrumentation;
import android.app.Instrumentation.ActivityMonitor;
import android.os.SystemClock;
import android.util.Log;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.robotium.solo.Solo.Config;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;
import java.util.Timer;
import junit.framework.Assert;

class ActivityUtils {
    private final String LOG_TAG = "Robotium";
    private final int MINISLEEP = 100;
    private Stack<String> activitiesStoredInActivityStack;
    private Activity activity;
    private ActivityMonitor activityMonitor;
    private Stack<WeakReference<Activity>> activityStack;
    private Timer activitySyncTimer;
    Thread activityThread;
    private final Config config;
    private final Instrumentation inst;
    private volatile boolean registerActivities;
    private final Sleeper sleeper;
    private WeakReference<Activity> weakActivityReference;

    private static final class RegisterActivitiesThread extends Thread {
        public static final long REGISTER_ACTIVITY_THREAD_SLEEP_MS = 16;
        private final WeakReference<ActivityUtils> activityUtilsWR;

        RegisterActivitiesThread(ActivityUtils activityUtils) {
            super("activityMonitorThread");
            this.activityUtilsWR = new WeakReference(activityUtils);
            setPriority(1);
        }

        public void run() {
            while (shouldMonitor()) {
                monitorActivities();
                SystemClock.sleep(16);
            }
        }

        private boolean shouldMonitor() {
            ActivityUtils activityUtils = (ActivityUtils) this.activityUtilsWR.get();
            return activityUtils != null && activityUtils.shouldRegisterActivities();
        }

        private void monitorActivities() {
            ActivityUtils activityUtils = (ActivityUtils) this.activityUtilsWR.get();
            if (activityUtils != null) {
                activityUtils.monitorActivities();
            }
        }
    }

    public ActivityUtils(Config config, Instrumentation inst, Activity activity, Sleeper sleeper) {
        this.config = config;
        this.inst = inst;
        this.activity = activity;
        this.sleeper = sleeper;
        createStackAndPushStartActivity();
        this.activitySyncTimer = new Timer();
        this.activitiesStoredInActivityStack = new Stack();
        setupActivityMonitor();
        setupActivityStackListener();
    }

    private void createStackAndPushStartActivity() {
        this.activityStack = new Stack();
        if (this.activity != null && this.config.trackActivities) {
            WeakReference<Activity> weakReference = new WeakReference(this.activity);
            this.activity = null;
            this.activityStack.push(weakReference);
        }
    }

    public ArrayList<Activity> getAllOpenedActivities() {
        ArrayList<Activity> activities = new ArrayList();
        Iterator<WeakReference<Activity>> activityStackIterator = this.activityStack.iterator();
        while (activityStackIterator.hasNext()) {
            Activity activity = (Activity) ((WeakReference) activityStackIterator.next()).get();
            if (activity != null) {
                activities.add(activity);
            }
        }
        return activities;
    }

    private void setupActivityMonitor() {
        if (this.config.trackActivities) {
            try {
                this.activityMonitor = this.inst.addMonitor(null, null, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean shouldRegisterActivities() {
        return this.registerActivities;
    }

    public void setRegisterActivities(boolean registerActivities) {
        this.registerActivities = registerActivities;
    }

    private void setupActivityStackListener() {
        if (this.activityMonitor != null) {
            setRegisterActivities(true);
            this.activityThread = new RegisterActivitiesThread(this);
            this.activityThread.start();
        }
    }

    void monitorActivities() {
        Activity activity = this.activityMonitor;
        if (activity != null) {
            activity = activity.waitForActivityWithTimeout(AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
            if (activity != null) {
                if (this.activitiesStoredInActivityStack.remove(activity.toString())) {
                    removeActivityFromStack(activity);
                }
                if (!activity.isFinishing()) {
                    addActivityToStack(activity);
                }
            }
        }
    }

    private void removeActivityFromStack(Activity activity) {
        Iterator<WeakReference<Activity>> activityStackIterator = this.activityStack.iterator();
        while (activityStackIterator.hasNext()) {
            Activity activityFromWeakReference = (Activity) ((WeakReference) activityStackIterator.next()).get();
            if (activityFromWeakReference == null) {
                activityStackIterator.remove();
            }
            if (activity != null && activityFromWeakReference != null && activityFromWeakReference.equals(activity)) {
                activityStackIterator.remove();
            }
        }
    }

    public ActivityMonitor getActivityMonitor() {
        return this.activityMonitor;
    }

    public void setActivityOrientation(int orientation) {
        Activity activity = getCurrentActivity();
        if (activity != null) {
            activity.setRequestedOrientation(orientation);
        }
    }

    public Activity getCurrentActivity(boolean shouldSleepFirst) {
        return getCurrentActivity(shouldSleepFirst, true);
    }

    public Activity getCurrentActivity() {
        return getCurrentActivity(true, true);
    }

    private void addActivityToStack(Activity activity) {
        this.activitiesStoredInActivityStack.push(activity.toString());
        this.weakActivityReference = new WeakReference(activity);
        this.activityStack.push(this.weakActivityReference);
    }

    private final void waitForActivityIfNotAvailable() {
        if (!this.activityStack.isEmpty()) {
            if (((WeakReference) this.activityStack.peek()).get() != null) {
                return;
            }
        }
        ActivityMonitor activityMonitor = this.activityMonitor;
        if (activityMonitor != null) {
            Activity activity = activityMonitor.getLastActivity();
            while (activity == null) {
                this.sleeper.sleepMini();
                activity = this.activityMonitor.getLastActivity();
            }
            addActivityToStack(activity);
        } else if (this.config.trackActivities) {
            this.sleeper.sleepMini();
            setupActivityMonitor();
            waitForActivityIfNotAvailable();
        }
    }

    public String getCurrentActivityName() {
        if (this.activitiesStoredInActivityStack.isEmpty()) {
            return "";
        }
        return (String) this.activitiesStoredInActivityStack.peek();
    }

    public Activity getCurrentActivity(boolean shouldSleepFirst, boolean waitForActivity) {
        if (shouldSleepFirst) {
            this.sleeper.sleep();
        }
        if (!this.config.trackActivities) {
            return this.activity;
        }
        if (waitForActivity) {
            waitForActivityIfNotAvailable();
        }
        if (!this.activityStack.isEmpty()) {
            this.activity = (Activity) ((WeakReference) this.activityStack.peek()).get();
        }
        return this.activity;
    }

    public boolean isActivityStackEmpty() {
        return this.activityStack.isEmpty();
    }

    public void goBackToActivity(String name) {
        int i;
        ArrayList<Activity> activitiesOpened = getAllOpenedActivities();
        boolean found = false;
        for (i = 0; i < activitiesOpened.size(); i++) {
            if (((Activity) activitiesOpened.get(i)).getClass().getSimpleName().equals(name)) {
                found = true;
                break;
            }
        }
        if (found) {
            while (!getCurrentActivity().getClass().getSimpleName().equals(name)) {
                try {
                    this.inst.sendKeyDownUpSync(4);
                } catch (SecurityException e) {
                }
            }
            return;
        }
        for (i = 0; i < activitiesOpened.size(); i++) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Activity priorly opened: ");
            stringBuilder.append(((Activity) activitiesOpened.get(i)).getClass().getSimpleName());
            Log.d("Robotium", stringBuilder.toString());
        }
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("No Activity named: '");
        stringBuilder2.append(name);
        stringBuilder2.append("' has been priorly opened");
        Assert.fail(stringBuilder2.toString());
    }

    public String getString(int resId) {
        Activity activity = getCurrentActivity(null);
        if (activity == null) {
            return "";
        }
        return activity.getString(resId);
    }

    public void finalize() throws Throwable {
        this.activitySyncTimer.cancel();
        stopActivityMonitor();
        super.finalize();
    }

    private void stopActivityMonitor() {
        try {
            if (this.activityMonitor != null) {
                this.inst.removeMonitor(this.activityMonitor);
                this.activityMonitor = null;
            }
        } catch (Exception e) {
        }
    }

    public void finishOpenedActivities() {
        this.activitySyncTimer.cancel();
        if (this.config.trackActivities) {
            ArrayList<Activity> activitiesOpened = getAllOpenedActivities();
            for (int i = activitiesOpened.size() - 1; i >= 0; i--) {
                this.sleeper.sleep(100);
                finishActivity((Activity) activitiesOpened.get(i));
            }
            this.sleeper.sleep(100);
            finishActivity(getCurrentActivity(true, false));
            stopActivityMonitor();
            setRegisterActivities(false);
            this.activity = null;
            this.sleeper.sleepMini();
            useGoBack(1);
            clearActivityStack();
            return;
        }
        useGoBack(3);
    }

    private void useGoBack(int numberOfTimes) {
        for (int i = 0; i < numberOfTimes; i++) {
            try {
                this.inst.sendKeyDownUpSync(4);
                this.sleeper.sleep(100);
                this.inst.sendKeyDownUpSync(4);
            } catch (Throwable th) {
            }
        }
    }

    private void clearActivityStack() {
        this.activityStack.clear();
        this.activitiesStoredInActivityStack.clear();
    }

    private void finishActivity(Activity activity) {
        if (activity != null) {
            try {
                activity.finish();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
}
