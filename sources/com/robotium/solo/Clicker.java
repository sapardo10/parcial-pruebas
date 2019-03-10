package com.robotium.solo;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import junit.framework.Assert;

class Clicker {
    private final String LOG_TAG = "Robotium";
    private final int MINI_WAIT = 300;
    private final int WAIT_TIME = 1500;
    private final ActivityUtils activityUtils;
    private final DialogUtils dialogUtils;
    private final Instrumentation inst;
    private final Sender sender;
    private final Sleeper sleeper;
    private final ViewFetcher viewFetcher;
    private final Waiter waiter;
    private final WebUtils webUtils;

    public Clicker(ActivityUtils activityUtils, ViewFetcher viewFetcher, Sender sender, Instrumentation inst, Sleeper sleeper, Waiter waiter, WebUtils webUtils, DialogUtils dialogUtils) {
        this.activityUtils = activityUtils;
        this.viewFetcher = viewFetcher;
        this.sender = sender;
        this.inst = inst;
        this.sleeper = sleeper;
        this.waiter = waiter;
        this.webUtils = webUtils;
        this.dialogUtils = dialogUtils;
    }

    public void clickOnScreen(float x, float y, View view) {
        View view2;
        Clicker clicker = this;
        float x2 = x;
        float y2 = y;
        SecurityException ex = null;
        int retry = 0;
        boolean successfull = false;
        while (!successfull && retry < 20) {
            long downTime = SystemClock.uptimeMillis();
            MotionEvent event2 = downTime;
            long uptimeMillis = SystemClock.uptimeMillis();
            float f = x2;
            float f2 = y2;
            MotionEvent event = MotionEvent.obtain(event2, uptimeMillis, 0, f, f2, null);
            event2 = MotionEvent.obtain(event2, uptimeMillis, 1, f, f2, 0);
            try {
                clicker.inst.sendPointerSync(event);
                clicker.inst.sendPointerSync(event2);
                view2 = view;
                successfull = true;
            } catch (SecurityException e) {
                SecurityException ex2 = e;
                clicker.dialogUtils.hideSoftKeyboard(null, false, true);
                clicker.sleeper.sleep(300);
                retry++;
                View identicalView = clicker.viewFetcher.getIdenticalView(view);
                if (identicalView != null) {
                    float[] xyToClick = getClickCoordinates(identicalView);
                    ex = ex2;
                    x2 = xyToClick[0];
                    y2 = xyToClick[1];
                } else {
                    ex = ex2;
                }
            }
        }
        view2 = view;
        if (!successfull) {
            String stringBuilder;
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Click at (");
            stringBuilder2.append(x2);
            stringBuilder2.append(", ");
            stringBuilder2.append(y2);
            stringBuilder2.append(") can not be completed! (");
            if (ex != null) {
                StringBuilder stringBuilder3 = new StringBuilder();
                stringBuilder3.append(ex.getClass().getName());
                stringBuilder3.append(": ");
                stringBuilder3.append(ex.getMessage());
                stringBuilder = stringBuilder3.toString();
            } else {
                stringBuilder = "null";
            }
            stringBuilder2.append(stringBuilder);
            stringBuilder2.append(")");
            Assert.fail(stringBuilder2.toString());
        }
    }

    public void clickLongOnScreen(float x, float y, int time, View view) {
        Clicker clicker = this;
        int i = time;
        long downTime = SystemClock.uptimeMillis();
        MotionEvent event = MotionEvent.obtain(downTime, SystemClock.uptimeMillis(), 0, x, y, null);
        float x2 = x;
        float y2 = y;
        SecurityException ex = null;
        int retry = 0;
        boolean successfull = false;
        while (!successfull && retry < 20) {
            try {
                clicker.inst.sendPointerSync(event);
                successfull = true;
                clicker.sleeper.sleep(300);
            } catch (SecurityException e) {
                ex = e;
                clicker.dialogUtils.hideSoftKeyboard(null, false, true);
                clicker.sleeper.sleep(300);
                retry++;
                View identicalView = clicker.viewFetcher.getIdenticalView(view);
                if (identicalView != null) {
                    float[] xyToClick = getClickCoordinates(identicalView);
                    x2 = xyToClick[0];
                    y2 = xyToClick[1];
                }
            }
        }
        View view2 = view;
        if (!successfull) {
            String stringBuilder;
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Long click at (");
            stringBuilder2.append(x2);
            stringBuilder2.append(", ");
            stringBuilder2.append(y2);
            stringBuilder2.append(") can not be completed! (");
            if (ex != null) {
                StringBuilder stringBuilder3 = new StringBuilder();
                stringBuilder3.append(ex.getClass().getName());
                stringBuilder3.append(": ");
                stringBuilder3.append(ex.getMessage());
                stringBuilder = stringBuilder3.toString();
            } else {
                stringBuilder = "null";
            }
            stringBuilder2.append(stringBuilder);
            stringBuilder2.append(")");
            Assert.fail(stringBuilder2.toString());
        }
        float f = 1.0f + y2;
        float y3 = y2;
        float x3 = x2;
        clicker.inst.sendPointerSync(MotionEvent.obtain(downTime, SystemClock.uptimeMillis(), 2, x2 + 1.0f, f, 0));
        if (i > 0) {
            clicker.sleeper.sleep(i);
        } else {
            clicker.sleeper.sleep((int) (((float) ViewConfiguration.getLongPressTimeout()) * 2.5f));
        }
        clicker.inst.sendPointerSync(MotionEvent.obtain(downTime, SystemClock.uptimeMillis(), 1, x3, y3, 0));
        clicker.sleeper.sleep();
    }

    public void clickOnScreen(View view) {
        clickOnScreen(view, false, 0);
    }

    public void clickOnScreen(View view, boolean longClick, int time) {
        if (view == null) {
            Assert.fail("View is null and can therefore not be clicked!");
        }
        float[] xyToClick = getClickCoordinates(view);
        float x = xyToClick[0];
        float y = xyToClick[1];
        if (x != 0.0f) {
            if (y != 0.0f) {
                this.sleeper.sleep(300);
                if (longClick) {
                    clickOnScreen(x, y, view);
                } else {
                    clickLongOnScreen(x, y, time, view);
                }
            }
        }
        this.sleeper.sleepMini();
        try {
            view = this.viewFetcher.getIdenticalView(view);
        } catch (Exception e) {
        }
        if (view != null) {
            xyToClick = getClickCoordinates(view);
            x = xyToClick[0];
            y = xyToClick[1];
        }
        this.sleeper.sleep(300);
        if (longClick) {
            clickOnScreen(x, y, view);
        } else {
            clickLongOnScreen(x, y, time, view);
        }
    }

    private float[] getClickCoordinates(View view) {
        int[] xyLocation = new int[2];
        float[] xyToClick = new float[2];
        int trialCount = 0;
        view.getLocationOnScreen(xyLocation);
        while (xyLocation[0] == 0 && xyLocation[1] == 0 && trialCount < 10) {
            this.sleeper.sleep(300);
            view.getLocationOnScreen(xyLocation);
            trialCount++;
        }
        int viewWidth = view.getWidth();
        float y = ((float) xyLocation[1]) + (((float) view.getHeight()) / 2.0f);
        xyToClick[0] = ((float) xyLocation[0]) + (((float) viewWidth) / 2.0f);
        xyToClick[1] = y;
        return xyToClick;
    }

    public void clickLongOnTextAndPress(String text, int index) {
        clickOnText(text, true, 0, true, 0);
        this.dialogUtils.waitForDialogToOpen((long) Timeout.getSmallTimeout(), true);
        try {
            this.inst.sendKeyDownUpSync(20);
        } catch (SecurityException e) {
            Assert.fail("Can not press the context menu!");
        }
        for (int i = 0; i < index; i++) {
            this.sleeper.sleepMini();
            this.inst.sendKeyDownUpSync(20);
        }
        this.inst.sendKeyDownUpSync(66);
    }

    private void openMenu() {
        this.sleeper.sleepMini();
        if (!this.dialogUtils.waitForDialogToOpen(300, false)) {
            try {
                this.sender.sendKeyCode(82);
                this.dialogUtils.waitForDialogToOpen(1500, true);
            } catch (SecurityException e) {
                Assert.fail("Can not open the menu!");
            }
        }
    }

    public void clickOnMenuItem(String text) {
        openMenu();
        clickOnText(text, false, 1, true, 0);
    }

    public void clickOnMenuItem(String text, boolean subMenu) {
        TextView textMore;
        this.sleeper.sleepMini();
        TextView textMore2 = null;
        int[] xy = new int[2];
        int x = 0;
        int y = 0;
        if (!this.dialogUtils.waitForDialogToOpen(300, false)) {
            try {
                r7.sender.sendKeyCode(82);
                r7.dialogUtils.waitForDialogToOpen(1500, true);
            } catch (SecurityException e) {
                Assert.fail("Can not open the menu!");
            }
        }
        boolean textShown = r7.waiter.waitForText(text, 1, 1500, true) != null;
        int x2;
        int y2;
        if (!subMenu || r7.viewFetcher.getCurrentViews(TextView.class, true).size() <= 5 || textShown) {
            textMore = null;
            x2 = 0;
            y2 = 0;
        } else {
            Iterator i$ = r7.viewFetcher.getCurrentViews(TextView.class, true).iterator();
            while (i$.hasNext()) {
                textMore = (TextView) i$.next();
                x = xy[0];
                y = xy[1];
                textMore.getLocationOnScreen(xy);
                if (xy[0] <= x) {
                    if (xy[1] > y) {
                    }
                }
                textMore2 = textMore;
            }
            textMore = textMore2;
            x2 = x;
            y2 = y;
        }
        if (textMore != null) {
            clickOnScreen(textMore);
        }
        clickOnText(text, false, 1, true, 0);
    }

    public void clickOnActionBarItem(int resourceId) {
        this.sleeper.sleep();
        Activity activity = this.activityUtils.getCurrentActivity();
        if (activity != null) {
            this.inst.invokeMenuActionSync(activity, resourceId, 0);
        }
    }

    public void clickOnActionBarHomeButton() {
        Activity activity = this.activityUtils.getCurrentActivity();
        MenuItem homeMenuItem = null;
        try {
            homeMenuItem = (MenuItem) Class.forName("com.android.internal.view.menu.ActionMenuItem").getConstructor(new Class[]{Context.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, CharSequence.class}).newInstance(new Object[]{activity, Integer.valueOf(0), Integer.valueOf(16908332), Integer.valueOf(0), Integer.valueOf(0), ""});
        } catch (Exception e) {
            Log.d("Robotium", "Can not find methods to invoke Home button!");
        }
        if (homeMenuItem != null) {
            try {
                activity.getWindow().getCallback().onMenuItemSelected(0, homeMenuItem);
            } catch (Exception e2) {
            }
        }
    }

    public void clickOnWebElement(By by, int match, boolean scroll, boolean useJavaScriptToClick) {
        if (useJavaScriptToClick) {
            if (this.waiter.waitForWebElement(by, match, Timeout.getSmallTimeout(), false) == null) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("WebElement with ");
                stringBuilder.append(this.webUtils.splitNameByUpperCase(by.getClass().getSimpleName()));
                stringBuilder.append(": '");
                stringBuilder.append(by.getValue());
                stringBuilder.append("' is not found!");
                Assert.fail(stringBuilder.toString());
            }
            this.webUtils.executeJavaScript(by, true);
            return;
        }
        WebElement webElementToClick = this.waiter.waitForWebElement(by, match, Timeout.getSmallTimeout(), scroll);
        if (webElementToClick == null) {
            StringBuilder stringBuilder2;
            if (match > 1) {
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append(match);
                stringBuilder2.append(" WebElements with ");
                stringBuilder2.append(this.webUtils.splitNameByUpperCase(by.getClass().getSimpleName()));
                stringBuilder2.append(": '");
                stringBuilder2.append(by.getValue());
                stringBuilder2.append("' are not found!");
                Assert.fail(stringBuilder2.toString());
            } else {
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append("WebElement with ");
                stringBuilder2.append(this.webUtils.splitNameByUpperCase(by.getClass().getSimpleName()));
                stringBuilder2.append(": '");
                stringBuilder2.append(by.getValue());
                stringBuilder2.append("' is not found!");
                Assert.fail(stringBuilder2.toString());
            }
        }
        clickOnScreen((float) webElementToClick.getLocationX(), (float) webElementToClick.getLocationY(), null);
    }

    public void clickOnText(String regex, boolean longClick, int match, boolean scroll, int time) {
        View textToClick = this.waiter.waitForText(regex, match, (long) Timeout.getSmallTimeout(), scroll, true, false);
        if (textToClick != null) {
            clickOnScreen(textToClick, longClick, time);
        } else if (match > 1) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(match);
            stringBuilder.append(" matches of text string: '");
            stringBuilder.append(regex);
            stringBuilder.append("' are not found!");
            Assert.fail(stringBuilder.toString());
        } else {
            ArrayList<TextView> allTextViews = RobotiumUtils.removeInvisibleViews(this.viewFetcher.getCurrentViews(TextView.class, true));
            allTextViews.addAll(this.webUtils.getTextViewsFromWebView());
            Iterator i$ = allTextViews.iterator();
            while (i$.hasNext()) {
                TextView textView = (TextView) i$.next();
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("'");
                stringBuilder2.append(regex);
                stringBuilder2.append("' not found. Have found: '");
                stringBuilder2.append(textView.getText());
                stringBuilder2.append("'");
                Log.d("Robotium", stringBuilder2.toString());
            }
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append("Text string: '");
            stringBuilder3.append(regex);
            stringBuilder3.append("' is not found!");
            Assert.fail(stringBuilder3.toString());
        }
    }

    public <T extends TextView> void clickOn(Class<T> viewClass, String nameRegex) {
        T viewToClick = this.waiter.waitForText(viewClass, nameRegex, 0, (long) Timeout.getSmallTimeout(), true, true, false);
        if (viewToClick != null) {
            clickOnScreen(viewToClick);
            return;
        }
        Iterator i$ = RobotiumUtils.removeInvisibleViews(this.viewFetcher.getCurrentViews(viewClass, true)).iterator();
        while (i$.hasNext()) {
            TextView view = (TextView) i$.next();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("'");
            stringBuilder.append(nameRegex);
            stringBuilder.append("' not found. Have found: '");
            stringBuilder.append(view.getText());
            stringBuilder.append("'");
            Log.d("Robotium", stringBuilder.toString());
        }
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(viewClass.getSimpleName());
        stringBuilder2.append(" with text: '");
        stringBuilder2.append(nameRegex);
        stringBuilder2.append("' is not found!");
        Assert.fail(stringBuilder2.toString());
    }

    public <T extends View> void clickOn(Class<T> viewClass, int index) {
        clickOnScreen(this.waiter.waitForAndGetView(index, viewClass));
    }

    public ArrayList<TextView> clickInList(int line) {
        return clickInList(line, 0, 0, false, 0);
    }

    public void clickInList(int line, int id) {
        clickInList(line, 0, id, false, 0);
    }

    public ArrayList<TextView> clickInList(int line, int index, int id, boolean longClick, int time) {
        long endTime = SystemClock.uptimeMillis() + ((long) Timeout.getSmallTimeout());
        int lineIndex = line - 1;
        if (lineIndex < 0) {
            lineIndex = 0;
        }
        ArrayList<View> views = new ArrayList();
        AbsListView absListView = (AbsListView) this.waiter.waitForAndGetView(index, AbsListView.class);
        if (absListView == null) {
            Assert.fail("AbsListView is null!");
        }
        failIfIndexHigherThenChildCount(absListView, lineIndex, endTime);
        View viewOnLine = getViewOnAbsListLine(absListView, index, lineIndex);
        if (viewOnLine != null) {
            views = RobotiumUtils.removeInvisibleViews(this.viewFetcher.getViews(viewOnLine, true));
            if (id == 0) {
                clickOnScreen(viewOnLine, longClick, time);
            } else {
                clickOnScreen(getView(id, views));
            }
        }
        return RobotiumUtils.filterViews(TextView.class, views);
    }

    public ArrayList<TextView> clickInRecyclerView(int line) {
        return clickInRecyclerView(line, 0, 0, false, 0);
    }

    public void clickInRecyclerView(int itemIndex, int id) {
        clickInRecyclerView(itemIndex, 0, id, false, 0);
    }

    public ArrayList<TextView> clickInRecyclerView(int itemIndex, int recyclerViewIndex, int id, boolean longClick, int time) {
        View viewOnLine = null;
        long endTime = SystemClock.uptimeMillis() + ((long) Timeout.getSmallTimeout());
        if (itemIndex < 0) {
            itemIndex = 0;
        }
        ArrayList<View> views = new ArrayList();
        ViewGroup recyclerView = this.viewFetcher.getRecyclerView(recyclerViewIndex, Timeout.getSmallTimeout());
        if (recyclerView == null) {
            Assert.fail("RecyclerView is not found!");
        } else {
            failIfIndexHigherThenChildCount(recyclerView, itemIndex, endTime);
            viewOnLine = getViewOnRecyclerItemIndex(recyclerView, recyclerViewIndex, itemIndex);
        }
        if (viewOnLine != null) {
            views = RobotiumUtils.removeInvisibleViews(this.viewFetcher.getViews(viewOnLine, true));
            if (id == 0) {
                clickOnScreen(viewOnLine, longClick, time);
            } else {
                clickOnScreen(getView(id, views));
            }
        }
        return RobotiumUtils.filterViews(TextView.class, views);
    }

    private View getView(int id, List<View> views) {
        for (View view : views) {
            if (id == view.getId()) {
                return view;
            }
        }
        return null;
    }

    private void failIfIndexHigherThenChildCount(ViewGroup viewGroup, int index, long endTime) {
        while (index > viewGroup.getChildCount()) {
            if (SystemClock.uptimeMillis() > endTime) {
                int numberOfIndexes = viewGroup.getChildCount();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Can not click on index ");
                stringBuilder.append(index);
                stringBuilder.append(" as there are only ");
                stringBuilder.append(numberOfIndexes);
                stringBuilder.append(" indexes available");
                Assert.fail(stringBuilder.toString());
            }
            this.sleeper.sleep();
        }
    }

    private View getViewOnAbsListLine(AbsListView absListView, int index, int lineIndex) {
        long endTime = SystemClock.uptimeMillis() + ((long) Timeout.getSmallTimeout());
        View view = absListView.getChildAt(lineIndex);
        while (view == null) {
            if (SystemClock.uptimeMillis() > endTime) {
                Assert.fail("View is null and can therefore not be clicked!");
            }
            this.sleeper.sleep();
            absListView = (AbsListView) this.viewFetcher.getIdenticalView(absListView);
            if (absListView == null) {
                absListView = (AbsListView) this.waiter.waitForAndGetView(index, AbsListView.class);
            }
            view = absListView.getChildAt(lineIndex);
        }
        return view;
    }

    private View getViewOnRecyclerItemIndex(ViewGroup recyclerView, int recyclerViewIndex, int itemIndex) {
        long endTime = SystemClock.uptimeMillis() + ((long) Timeout.getSmallTimeout());
        View view = recyclerView.getChildAt(itemIndex);
        while (view == null) {
            if (SystemClock.uptimeMillis() > endTime) {
                Assert.fail("View is null and can therefore not be clicked!");
            }
            this.sleeper.sleep();
            recyclerView = (ViewGroup) this.viewFetcher.getIdenticalView(recyclerView);
            if (recyclerView == null) {
                recyclerView = (ViewGroup) this.viewFetcher.getRecyclerView(false, recyclerViewIndex);
            }
            if (recyclerView != null) {
                view = recyclerView.getChildAt(itemIndex);
            }
        }
        return view;
    }
}
