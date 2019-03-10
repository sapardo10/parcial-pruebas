package com.robotium.solo;

import android.app.Activity;
import android.app.Instrumentation;
import android.app.Instrumentation.ActivityMonitor;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import junit.framework.Assert;
import org.apache.commons.lang3.StringUtils;

class Waiter {
    private final ActivityUtils activityUtils;
    private final Instrumentation instrumentation;
    private final Scroller scroller;
    private final Searcher searcher;
    private final Sleeper sleeper;
    private final ViewFetcher viewFetcher;

    public Waiter(Instrumentation instrumentation, ActivityUtils activityUtils, ViewFetcher viewFetcher, Searcher searcher, Scroller scroller, Sleeper sleeper) {
        this.instrumentation = instrumentation;
        this.activityUtils = activityUtils;
        this.viewFetcher = viewFetcher;
        this.searcher = searcher;
        this.scroller = scroller;
        this.sleeper = sleeper;
    }

    public boolean waitForActivity(String name) {
        return waitForActivity(name, Timeout.getSmallTimeout());
    }

    public boolean waitForActivity(String name, int timeout) {
        if (isActivityMatching(this.activityUtils.getCurrentActivity(false, false), name)) {
            return true;
        }
        boolean foundActivity = false;
        ActivityMonitor activityMonitor = getActivityMonitor();
        long currentTime = SystemClock.uptimeMillis();
        long endTime = ((long) timeout) + currentTime;
        while (currentTime < endTime) {
            if (isActivityMatching(activityMonitor.waitForActivityWithTimeout(endTime - currentTime), name)) {
                foundActivity = true;
                break;
            }
            currentTime = SystemClock.uptimeMillis();
        }
        removeMonitor(activityMonitor);
        return foundActivity;
    }

    private boolean isActivityMatching(Activity currentActivity, String name) {
        if (currentActivity == null || !currentActivity.getClass().getSimpleName().equals(name)) {
            return false;
        }
        return true;
    }

    public boolean waitForActivity(Class<? extends Activity> activityClass) {
        return waitForActivity((Class) activityClass, Timeout.getSmallTimeout());
    }

    public boolean waitForActivity(Class<? extends Activity> activityClass, int timeout) {
        if (isActivityMatching((Class) activityClass, this.activityUtils.getCurrentActivity(false, false))) {
            return true;
        }
        boolean foundActivity = false;
        ActivityMonitor activityMonitor = getActivityMonitor();
        long currentTime = SystemClock.uptimeMillis();
        long endTime = ((long) timeout) + currentTime;
        while (currentTime < endTime) {
            Activity currentActivity = activityMonitor.waitForActivityWithTimeout(endTime - currentTime);
            if (currentActivity != null && currentActivity.getClass().equals(activityClass)) {
                foundActivity = true;
                break;
            }
            currentTime = SystemClock.uptimeMillis();
        }
        removeMonitor(activityMonitor);
        return foundActivity;
    }

    private boolean isActivityMatching(Class<? extends Activity> activityClass, Activity currentActivity) {
        if (currentActivity == null || !currentActivity.getClass().equals(activityClass)) {
            return false;
        }
        return true;
    }

    private ActivityMonitor getActivityMonitor() {
        return this.instrumentation.addMonitor(null, null, false);
    }

    private void removeMonitor(ActivityMonitor activityMonitor) {
        try {
            this.instrumentation.removeMonitor(activityMonitor);
        } catch (Exception e) {
        }
    }

    public <T extends View> boolean waitForView(Class<T> viewClass, int index, boolean sleep, boolean scroll) {
        Set<T> uniqueViews = new HashSet();
        while (true) {
            if (sleep) {
                this.sleeper.sleep();
            }
            if (this.searcher.searchFor(uniqueViews, viewClass, index)) {
                return true;
            }
            if ((scroll && !this.scroller.scrollDown()) || !scroll) {
                return false;
            }
        }
    }

    public <T extends View> boolean waitForView(Class<T> viewClass, int index, int timeout, boolean scroll) {
        Set<T> uniqueViews = new HashSet();
        long endTime = SystemClock.uptimeMillis() + ((long) timeout);
        while (SystemClock.uptimeMillis() < endTime) {
            this.sleeper.sleep();
            if (this.searcher.searchFor(uniqueViews, viewClass, index)) {
                return true;
            }
            if (scroll) {
                this.scroller.scrollDown();
            }
        }
        return false;
    }

    public <T extends View> boolean waitForViews(boolean scrollMethod, Class<? extends T>... classes) {
        long endTime = SystemClock.uptimeMillis() + ((long) Timeout.getSmallTimeout());
        while (SystemClock.uptimeMillis() < endTime) {
            for (Class classToWaitFor : classes) {
                if (waitForView(classToWaitFor, 0, false, false)) {
                    return true;
                }
            }
            if (scrollMethod) {
                this.scroller.scroll(0);
            } else {
                this.scroller.scrollDown();
            }
            this.sleeper.sleep();
        }
        return false;
    }

    public boolean waitForView(View view) {
        if (waitForView(view, Timeout.getLargeTimeout(), true, true) != null) {
            return true;
        }
        return false;
    }

    public View waitForView(View view, int timeout) {
        return waitForView(view, timeout, true, true);
    }

    public View waitForView(View view, int timeout, boolean scroll, boolean checkIsShown) {
        long endTime = SystemClock.uptimeMillis() + ((long) timeout);
        int retry = 0;
        if (view == null) {
            return null;
        }
        while (SystemClock.uptimeMillis() < endTime) {
            boolean foundAnyMatchingView = this.searcher.searchFor(view);
            if (checkIsShown && foundAnyMatchingView && !view.isShown()) {
                this.sleeper.sleepMini();
                retry++;
                View identicalView = this.viewFetcher.getIdenticalView(view);
                if (identicalView != null && !view.equals(identicalView)) {
                    view = identicalView;
                }
                if (retry > 5) {
                    return view;
                }
            } else if (foundAnyMatchingView) {
                return view;
            } else {
                if (scroll) {
                    this.scroller.scrollDown();
                }
                this.sleeper.sleep();
            }
        }
        return view;
    }

    public View waitForView(int id, int index, int timeout) {
        if (timeout == 0) {
            timeout = Timeout.getSmallTimeout();
        }
        return waitForView(id, index, timeout, false);
    }

    public View waitForView(int id, int index, int timeout, boolean scroll) {
        Set<View> uniqueViewsMatchingId = new HashSet();
        long endTime = SystemClock.uptimeMillis() + ((long) timeout);
        while (SystemClock.uptimeMillis() <= endTime) {
            this.sleeper.sleep();
            Iterator i$ = this.viewFetcher.getAllViews(false).iterator();
            while (i$.hasNext()) {
                View view = (View) i$.next();
                if (Integer.valueOf(view.getId()).equals(Integer.valueOf(id))) {
                    uniqueViewsMatchingId.add(view);
                    if (uniqueViewsMatchingId.size() > index) {
                        return view;
                    }
                }
            }
            if (scroll) {
                this.scroller.scrollDown();
            }
        }
        return null;
    }

    public View waitForView(Object tag, int index, int timeout) {
        if (timeout == 0) {
            timeout = Timeout.getSmallTimeout();
        }
        return waitForView(tag, index, timeout, false);
    }

    public View waitForView(Object tag, int index, int timeout, boolean scroll) {
        if (tag == null) {
            return null;
        }
        Set<View> uniqueViewsMatchingId = new HashSet();
        long endTime = SystemClock.uptimeMillis() + ((long) timeout);
        while (SystemClock.uptimeMillis() <= endTime) {
            this.sleeper.sleep();
            Iterator i$ = this.viewFetcher.getAllViews(false).iterator();
            while (i$.hasNext()) {
                View view = (View) i$.next();
                if (tag.equals(view.getTag())) {
                    uniqueViewsMatchingId.add(view);
                    if (uniqueViewsMatchingId.size() > index) {
                        return view;
                    }
                }
            }
            if (scroll) {
                this.scroller.scrollDown();
            }
        }
        return null;
    }

    public WebElement waitForWebElement(By by, int minimumNumberOfMatches, int timeout, boolean scroll) {
        long endTime = SystemClock.uptimeMillis() + ((long) timeout);
        while (true) {
            if (SystemClock.uptimeMillis() > endTime) {
                this.searcher.logMatchesFound(by.getValue());
                return null;
            }
            this.sleeper.sleep();
            WebElement webElementToReturn = this.searcher.searchForWebElement(by, minimumNumberOfMatches);
            if (webElementToReturn != null) {
                return webElementToReturn;
            }
            if (scroll) {
                this.scroller.scrollDown();
            }
        }
    }

    public boolean waitForCondition(Condition condition, int timeout) {
        long endTime = SystemClock.uptimeMillis() + ((long) timeout);
        while (true) {
            if (SystemClock.uptimeMillis() > endTime) {
                return false;
            }
            this.sleeper.sleep();
            if (condition.isSatisfied()) {
                return true;
            }
        }
    }

    public TextView waitForText(String text) {
        return waitForText(text, 0, (long) Timeout.getLargeTimeout(), true);
    }

    public TextView waitForText(String text, int expectedMinimumNumberOfMatches, long timeout) {
        return waitForText(text, expectedMinimumNumberOfMatches, timeout, true);
    }

    public TextView waitForText(String text, int expectedMinimumNumberOfMatches, long timeout, boolean scroll) {
        return waitForText(TextView.class, text, expectedMinimumNumberOfMatches, timeout, scroll, false, true);
    }

    public <T extends TextView> T waitForText(Class<T> classToFilterBy, String text, int expectedMinimumNumberOfMatches, long timeout, boolean scroll) {
        return waitForText(classToFilterBy, text, expectedMinimumNumberOfMatches, timeout, scroll, false, true);
    }

    public TextView waitForText(String text, int expectedMinimumNumberOfMatches, long timeout, boolean scroll, boolean onlyVisible, boolean hardStoppage) {
        return waitForText(TextView.class, text, expectedMinimumNumberOfMatches, timeout, scroll, onlyVisible, hardStoppage);
    }

    public <T extends TextView> T waitForText(Class<T> classToFilterBy, String text, int expectedMinimumNumberOfMatches, long timeout, boolean scroll, boolean onlyVisible, boolean hardStoppage) {
        Waiter waiter = this;
        long endTime = SystemClock.uptimeMillis() + timeout;
        long timeout2 = timeout;
        while (true) {
            if (SystemClock.uptimeMillis() > endTime) {
                return null;
            }
            waiter.sleeper.sleep();
            if (!hardStoppage) {
                timeout2 = 0;
            }
            T textViewToReturn = waiter.searcher.searchFor(classToFilterBy, text, expectedMinimumNumberOfMatches, timeout2, scroll, onlyVisible);
            if (textViewToReturn != null) {
                return textViewToReturn;
            }
        }
    }

    public <T extends View> T waitForAndGetView(int index, Class<T> classToFilterBy) {
        long endTime = SystemClock.uptimeMillis() + ((long) Timeout.getSmallTimeout());
        while (SystemClock.uptimeMillis() <= endTime && !waitForView((Class) classToFilterBy, index, true, true)) {
        }
        int numberOfUniqueViews = this.searcher.getNumberOfUniqueViews();
        ArrayList<T> views = RobotiumUtils.removeInvisibleViews(this.viewFetcher.getCurrentViews(classToFilterBy, true));
        if (views.size() < numberOfUniqueViews) {
            int newIndex = index - (numberOfUniqueViews - views.size());
            if (newIndex >= 0) {
                index = newIndex;
            }
        }
        T view = null;
        try {
            view = (View) views.get(index);
        } catch (IndexOutOfBoundsException e) {
            int match = index + 1;
            StringBuilder stringBuilder;
            if (match > 1) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(match);
                stringBuilder.append(StringUtils.SPACE);
                stringBuilder.append(classToFilterBy.getSimpleName());
                stringBuilder.append("s");
                stringBuilder.append(" are not found!");
                Assert.fail(stringBuilder.toString());
            } else {
                stringBuilder = new StringBuilder();
                stringBuilder.append(classToFilterBy.getSimpleName());
                stringBuilder.append(" is not found!");
                Assert.fail(stringBuilder.toString());
            }
        }
        return view;
    }

    public boolean waitForFragment(String tag, int id, int timeout) {
        long endTime = SystemClock.uptimeMillis() + ((long) timeout);
        while (SystemClock.uptimeMillis() <= endTime) {
            if (getSupportFragment(tag, id) != null || getFragment(tag, id) != null) {
                return true;
            }
        }
        return false;
    }

    private Fragment getSupportFragment(String tag, int id) {
        FragmentActivity fragmentActivity = null;
        try {
            fragmentActivity = (FragmentActivity) this.activityUtils.getCurrentActivity(false);
        } catch (Throwable th) {
        }
        if (fragmentActivity == null) {
            return null;
        }
        if (tag != null) {
            return fragmentActivity.getSupportFragmentManager().findFragmentByTag(tag);
        }
        try {
            return fragmentActivity.getSupportFragmentManager().findFragmentById(id);
        } catch (NoSuchMethodError e) {
        }
    }

    public boolean waitForLogMessage(String logMessage, int timeout) {
        StringBuilder stringBuilder = new StringBuilder();
        long endTime = SystemClock.uptimeMillis() + ((long) timeout);
        while (SystemClock.uptimeMillis() <= endTime) {
            if (getLog(stringBuilder).lastIndexOf(logMessage) != -1) {
                return true;
            }
            this.sleeper.sleep();
        }
        return false;
    }

    private StringBuilder getLog(StringBuilder stringBuilder) {
        Process p = null;
        BufferedReader reader = null;
        try {
            String line;
            p = Runtime.getRuntime().exec("logcat -d");
            reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            stringBuilder.setLength(0);
            while (true) {
                String readLine = reader.readLine();
                line = readLine;
                if (readLine == null) {
                    break;
                }
                stringBuilder.append(line);
            }
            reader.close();
            StringBuilder errorLog = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            errorLog.append("logcat returns error: ");
            while (true) {
                String readLine2 = reader.readLine();
                line = readLine2;
                if (readLine2 == null) {
                    break;
                }
                errorLog.append(line);
            }
            reader.close();
            p.waitFor();
            if (p.exitValue() == 0) {
                destroy(p, reader);
                return stringBuilder;
            }
            destroy(p, reader);
            throw new Exception(errorLog.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e2) {
            e2.printStackTrace();
        } catch (Exception e3) {
            e3.printStackTrace();
        }
    }

    public void clearLog() {
        try {
            Process p = Runtime.getRuntime().exec("logcat -c");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void destroy(Process p, BufferedReader reader) {
        p.destroy();
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private android.app.Fragment getFragment(String tag, int id) {
        if (tag != null) {
            return this.activityUtils.getCurrentActivity().getFragmentManager().findFragmentByTag(tag);
        }
        try {
            return this.activityUtils.getCurrentActivity().getFragmentManager().findFragmentById(id);
        } catch (Throwable th) {
            return null;
        }
    }
}
