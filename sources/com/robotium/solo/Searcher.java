package com.robotium.solo;

import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

class Searcher {
    private final String LOG_TAG = "Robotium";
    private final int TIMEOUT = 5000;
    private int numberOfUniqueViews;
    private final Scroller scroller;
    private final Sleeper sleeper;
    Set<TextView> uniqueTextViews;
    private final ViewFetcher viewFetcher;
    List<WebElement> webElements;
    private final WebUtils webUtils;

    public Searcher(ViewFetcher viewFetcher, WebUtils webUtils, Scroller scroller, Sleeper sleeper) {
        this.viewFetcher = viewFetcher;
        this.webUtils = webUtils;
        this.scroller = scroller;
        this.sleeper = sleeper;
        this.webElements = new ArrayList();
        this.uniqueTextViews = new HashSet();
    }

    public boolean searchWithTimeoutFor(Class<? extends TextView> viewClass, String regex, int expectedMinimumNumberOfMatches, boolean scroll, boolean onlyVisible) {
        long endTime = SystemClock.uptimeMillis() + DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS;
        while (SystemClock.uptimeMillis() < endTime) {
            this.sleeper.sleep();
            if (searchFor(viewClass, regex, expectedMinimumNumberOfMatches, 0, scroll, onlyVisible) != null) {
                return true;
            }
        }
        Searcher searcher = this;
        return false;
    }

    public <T extends TextView> T searchFor(final Class<T> viewClass, String regex, int expectedMinimumNumberOfMatches, long timeout, boolean scroll, final boolean onlyVisible) {
        if (expectedMinimumNumberOfMatches < 1) {
            expectedMinimumNumberOfMatches = 1;
        }
        try {
            return searchFor(new Callable<Collection<T>>() {
                public Collection<T> call() throws Exception {
                    Searcher.this.sleeper.sleep();
                    ArrayList<T> viewsToReturn = Searcher.this.viewFetcher.getCurrentViews(viewClass, true);
                    if (onlyVisible) {
                        viewsToReturn = RobotiumUtils.removeInvisibleViews(viewsToReturn);
                    }
                    if (viewClass.isAssignableFrom(TextView.class)) {
                        viewsToReturn.addAll(Searcher.this.webUtils.getTextViewsFromWebView());
                    }
                    return viewsToReturn;
                }
            }, regex, expectedMinimumNumberOfMatches, timeout, scroll);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T extends View> boolean searchFor(Set<T> uniqueViews, Class<T> viewClass, int index) {
        int uniqueViewsFound = getNumberOfUniqueViews(uniqueViews, RobotiumUtils.removeInvisibleViews(this.viewFetcher.getCurrentViews(viewClass, true)));
        if (uniqueViewsFound > 0 && index < uniqueViewsFound) {
            return true;
        }
        if (uniqueViewsFound <= 0 || index != 0) {
            return false;
        }
        return true;
    }

    public <T extends View> boolean searchFor(View view) {
        Iterator i$ = this.viewFetcher.getAllViews(true).iterator();
        while (i$.hasNext()) {
            if (((View) i$.next()).equals(view)) {
                return true;
            }
        }
        return false;
    }

    public <T extends TextView> T searchFor(Callable<Collection<T>> viewFetcherCallback, String regex, int expectedMinimumNumberOfMatches, long timeout, boolean scroll) throws Exception {
        long endTime = SystemClock.uptimeMillis() + timeout;
        while (true) {
            boolean timedOut = timeout > 0 && SystemClock.uptimeMillis() > endTime;
            if (timedOut) {
                logMatchesFound(regex);
                return null;
            }
            for (T view : (Collection) viewFetcherCallback.call()) {
                if (RobotiumUtils.getNumberOfMatches(regex, view, this.uniqueTextViews) == expectedMinimumNumberOfMatches) {
                    this.uniqueTextViews.clear();
                    return view;
                }
            }
            if (scroll && !this.scroller.scrollDown()) {
                logMatchesFound(regex);
                return null;
            } else if (!scroll) {
                logMatchesFound(regex);
                return null;
            }
        }
    }

    public WebElement searchForWebElement(By by, int minimumNumberOfMatches) {
        if (minimumNumberOfMatches < 1) {
            minimumNumberOfMatches = 1;
        }
        addViewsToList(this.webElements, this.webUtils.getWebElements(by, true));
        return getViewFromList(this.webElements, minimumNumberOfMatches);
    }

    private void addViewsToList(List<WebElement> allWebElements, List<WebElement> webElementsOnScreen) {
        int[] xyViewFromSet = new int[2];
        int[] xyViewFromScreen = new int[2];
        for (WebElement textFromScreen : webElementsOnScreen) {
            boolean foundView = false;
            textFromScreen.getLocationOnScreen(xyViewFromScreen);
            for (WebElement textFromList : allWebElements) {
                textFromList.getLocationOnScreen(xyViewFromSet);
                if (textFromScreen.getText().equals(textFromList.getText()) && xyViewFromScreen[0] == xyViewFromSet[0] && xyViewFromScreen[1] == xyViewFromSet[1]) {
                    foundView = true;
                }
            }
            if (!foundView) {
                allWebElements.add(textFromScreen);
            }
        }
    }

    private WebElement getViewFromList(List<WebElement> webElements, int match) {
        WebElement webElementToReturn = null;
        if (webElements.size() >= match) {
            try {
                webElementToReturn = (WebElement) webElements.get(match - 1);
            } catch (Exception e) {
            }
        }
        if (webElementToReturn != null) {
            webElements.clear();
        }
        return webElementToReturn;
    }

    public <T extends View> int getNumberOfUniqueViews(Set<T> uniqueViews, ArrayList<T> views) {
        for (int i = 0; i < views.size(); i++) {
            uniqueViews.add(views.get(i));
        }
        this.numberOfUniqueViews = uniqueViews.size();
        return this.numberOfUniqueViews;
    }

    public int getNumberOfUniqueViews() {
        return this.numberOfUniqueViews;
    }

    public void logMatchesFound(String regex) {
        StringBuilder stringBuilder;
        if (this.uniqueTextViews.size() > 0) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(" There are only ");
            stringBuilder.append(this.uniqueTextViews.size());
            stringBuilder.append(" matches of '");
            stringBuilder.append(regex);
            stringBuilder.append("'");
            Log.d("Robotium", stringBuilder.toString());
        } else if (this.webElements.size() > 0) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(" There are only ");
            stringBuilder.append(this.webElements.size());
            stringBuilder.append(" matches of '");
            stringBuilder.append(regex);
            stringBuilder.append("'");
            Log.d("Robotium", stringBuilder.toString());
        }
        this.uniqueTextViews.clear();
        this.webElements.clear();
    }
}
