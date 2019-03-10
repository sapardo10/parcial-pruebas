package com.robotium.solo;

import android.app.Instrumentation;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ScrollView;
import com.robotium.solo.Solo.Config;
import java.util.ArrayList;
import junit.framework.Assert;

class Scroller {
    public static final int DOWN = 0;
    public static final int UP = 1;
    private boolean canScroll = false;
    private final Config config;
    private final Instrumentation inst;
    private final Sleeper sleeper;
    private final ViewFetcher viewFetcher;

    public enum Side {
        LEFT,
        RIGHT
    }

    public Scroller(Config config, Instrumentation inst, ViewFetcher viewFetcher, Sleeper sleeper) {
        this.config = config;
        this.inst = inst;
        this.viewFetcher = viewFetcher;
        this.sleeper = sleeper;
    }

    public void drag(float fromX, float toX, float fromY, float toY, int stepCount) {
        Scroller scroller = this;
        int i = stepCount;
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis();
        float y = fromY;
        float x = fromX;
        float yStep = (toY - fromY) / ((float) i);
        float xStep = (toX - fromX) / ((float) i);
        MotionEvent event = MotionEvent.obtain(downTime, eventTime, 0, fromX, fromY, 0);
        try {
            scroller.inst.sendPointerSync(event);
        } catch (SecurityException e) {
        }
        long j = eventTime;
        int i2 = 0;
        long eventTime2 = j;
        while (i2 < i) {
            y += yStep;
            x += xStep;
            long eventTime3 = SystemClock.uptimeMillis();
            event = MotionEvent.obtain(downTime, eventTime3, 2, x, y, 0);
            try {
                scroller.inst.sendPointerSync(event);
            } catch (SecurityException e2) {
            }
            i2++;
            MotionEvent motionEvent = event;
        }
        try {
            scroller.inst.sendPointerSync(MotionEvent.obtain(downTime, SystemClock.uptimeMillis(), 1, toX, toY, 0));
        } catch (SecurityException e3) {
        }
    }

    public boolean scrollView(final View view, int direction) {
        if (view == null) {
            return false;
        }
        int height = view.getHeight() - 1;
        int scrollTo = -1;
        if (direction == 0) {
            scrollTo = height;
        } else if (direction == 1) {
            scrollTo = -height;
        }
        int originalY = view.getScrollY();
        final int scrollAmount = scrollTo;
        this.inst.runOnMainSync(new Runnable() {
            public void run() {
                view.scrollBy(0, scrollAmount);
            }
        });
        if (originalY == view.getScrollY()) {
            return false;
        }
        return true;
    }

    public void scrollViewAllTheWay(View view, int direction) {
        while (scrollView(view, direction)) {
        }
    }

    public boolean scroll(int direction) {
        return scroll(direction, false);
    }

    public boolean scrollDown() {
        if (this.config.shouldScroll) {
            return scroll(0);
        }
        return false;
    }

    public boolean scroll(int direction, boolean allTheWay) {
        ArrayList<View> filteredViews = new Class[]{ListView.class, ScrollView.class, GridView.class, WebView.class};
        filteredViews = RobotiumUtils.filterViewsToSet(filteredViews, RobotiumUtils.removeInvisibleViews(this.viewFetcher.getAllViews(true)));
        for (View viewToScroll : this.viewFetcher.getScrollableSupportPackageViews(true)) {
            filteredViews.add(viewToScroll);
        }
        View view = this.viewFetcher.getFreshestView(filteredViews);
        if (view == null) {
            return false;
        }
        if (view instanceof AbsListView) {
            return scrollList((AbsListView) view, direction, allTheWay);
        }
        if (view instanceof WebView) {
            return scrollWebView((WebView) view, direction, allTheWay);
        }
        if (!allTheWay) {
            return scrollView(view, direction);
        }
        scrollViewAllTheWay(view, direction);
        return false;
    }

    public boolean scrollWebView(final WebView webView, int direction, final boolean allTheWay) {
        if (direction == 0) {
            this.inst.runOnMainSync(new Runnable() {
                public void run() {
                    Scroller.this.canScroll = webView.pageDown(allTheWay);
                }
            });
        }
        if (direction == 1) {
            this.inst.runOnMainSync(new Runnable() {
                public void run() {
                    Scroller.this.canScroll = webView.pageUp(allTheWay);
                }
            });
        }
        return this.canScroll;
    }

    public <T extends AbsListView> boolean scrollList(T absListView, int direction, boolean allTheWay) {
        if (absListView == null) {
            return false;
        }
        int listCount;
        int firstVisiblePosition;
        if (direction == 0) {
            listCount = absListView.getCount();
            int lastVisiblePosition = absListView.getLastVisiblePosition();
            if (allTheWay) {
                scrollListToLine(absListView, listCount - 1);
                return false;
            } else if (lastVisiblePosition >= listCount - 1) {
                if (lastVisiblePosition > 0) {
                    scrollListToLine(absListView, lastVisiblePosition);
                }
                return false;
            } else {
                firstVisiblePosition = absListView.getFirstVisiblePosition();
                if (firstVisiblePosition != lastVisiblePosition) {
                    scrollListToLine(absListView, lastVisiblePosition);
                } else {
                    scrollListToLine(absListView, firstVisiblePosition + 1);
                }
            }
        } else if (direction == 1) {
            listCount = absListView.getFirstVisiblePosition();
            if (!allTheWay) {
                if (listCount >= 2) {
                    firstVisiblePosition = absListView.getLastVisiblePosition();
                    int lineToScrollTo = listCount - (firstVisiblePosition - listCount);
                    if (lineToScrollTo == firstVisiblePosition) {
                        lineToScrollTo--;
                    }
                    if (lineToScrollTo < 0) {
                        lineToScrollTo = 0;
                    }
                    scrollListToLine(absListView, lineToScrollTo);
                    this.sleeper.sleep();
                    return true;
                }
            }
            scrollListToLine(absListView, 0);
            return false;
        }
        this.sleeper.sleep();
        return true;
    }

    public <T extends AbsListView> void scrollListToLine(final T view, int line) {
        int lineToMoveTo;
        if (view == null) {
            Assert.fail("AbsListView is null!");
        }
        if (view instanceof GridView) {
            lineToMoveTo = line + 1;
        } else {
            lineToMoveTo = line;
        }
        this.inst.runOnMainSync(new Runnable() {
            public void run() {
                view.setSelection(lineToMoveTo);
            }
        });
    }

    public void scrollToSide(Side side, float scrollPosition, int stepCount) {
        WindowManager windowManager = (WindowManager) this.inst.getTargetContext().getSystemService("window");
        float x = ((float) windowManager.getDefaultDisplay().getWidth()) * scrollPosition;
        float y = ((float) windowManager.getDefaultDisplay().getHeight()) / 2.0f;
        if (side == Side.LEFT) {
            drag(70.0f, x, y, y, stepCount);
        } else if (side == Side.RIGHT) {
            drag(x, 0.0f, y, y, stepCount);
        }
    }

    public void scrollViewToSide(View view, Side side, float scrollPosition, int stepCount) {
        Side side2 = side;
        int[] corners = new int[2];
        View view2 = view;
        view.getLocationOnScreen(corners);
        int viewHeight = view.getHeight();
        float x = ((float) corners[0]) + (((float) view.getWidth()) * scrollPosition);
        float y = ((float) corners[1]) + (((float) viewHeight) / 2.0f);
        if (side2 == Side.LEFT) {
            drag((float) corners[0], x, y, y, stepCount);
        } else if (side2 == Side.RIGHT) {
            drag(x, (float) corners[0], y, y, stepCount);
        }
    }
}
