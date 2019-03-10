package com.robotium.solo;

import android.os.SystemClock;
import android.webkit.WebView;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

class WebElementCreator {
    private boolean isFinished = false;
    private Sleeper sleeper;
    private List<WebElement> webElements;

    public WebElementCreator(Sleeper sleeper) {
        this.sleeper = sleeper;
        this.webElements = new CopyOnWriteArrayList();
    }

    public void prepareForStart() {
        setFinished(false);
        this.webElements.clear();
    }

    public ArrayList<WebElement> getWebElementsFromWebViews() {
        waitForWebElementsToBeCreated();
        return new ArrayList(this.webElements);
    }

    public boolean isFinished() {
        return this.isFinished;
    }

    public void setFinished(boolean isFinished) {
        this.isFinished = isFinished;
    }

    public void createWebElementAndAddInList(String webData, WebView webView) {
        WebElement webElement = createWebElementAndSetLocation(webData, webView);
        if (webElement != null) {
            this.webElements.add(webElement);
        }
    }

    private void setLocation(WebElement webElement, WebView webView, int x, int y, int width, int height) {
        float scale = webView.getScale();
        int[] locationOfWebViewXY = new int[2];
        webView.getLocationOnScreen(locationOfWebViewXY);
        double d = (double) locationOfWebViewXY[0];
        double d2 = (double) x;
        double floor = Math.floor((double) (width / 2));
        Double.isNaN(d2);
        d2 += floor;
        floor = (double) scale;
        Double.isNaN(floor);
        d2 *= floor;
        Double.isNaN(d);
        int locationX = (int) (d + d2);
        double d3 = (double) locationOfWebViewXY[1];
        double d4 = (double) y;
        double floor2 = Math.floor((double) (height / 2));
        Double.isNaN(d4);
        d4 += floor2;
        floor2 = (double) scale;
        Double.isNaN(floor2);
        d4 *= floor2;
        Double.isNaN(d3);
        int locationY = (int) (d3 + d4);
        webElement.setLocationX(locationX);
        webElement.setLocationY(locationY);
    }

    private WebElement createWebElementAndSetLocation(String information, WebView webView) {
        String[] data = information.split(";,");
        String[] elements = null;
        int x = 0;
        int y = 0;
        int width = 0;
        int height = 0;
        Hashtable<String, String> attributes = new Hashtable();
        try {
            x = Math.round(Float.valueOf(data[5]).floatValue());
            y = Math.round(Float.valueOf(data[6]).floatValue());
            width = Math.round(Float.valueOf(data[7]).floatValue());
            height = Math.round(Float.valueOf(data[8]).floatValue());
            elements = data[9].split("\\#\\$");
        } catch (Exception e) {
        }
        int x2 = x;
        int y2 = y;
        int width2 = width;
        int height2 = height;
        if (elements != null) {
            for (String[] element : elements) {
                String[] element2 = element2.split("::");
                if (element2.length > 1) {
                    attributes.put(element2[0], element2[1]);
                } else {
                    attributes.put(element2[0], element2[0]);
                }
            }
        }
        WebElement webElement = null;
        try {
            webElement = new WebElement(data[0], data[1], data[2], data[3], data[4], attributes);
            setLocation(webElement, webView, x2, y2, width2, height2);
        } catch (Exception e2) {
        }
        return webElement;
    }

    private boolean waitForWebElementsToBeCreated() {
        long endTime = SystemClock.uptimeMillis() + DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS;
        while (SystemClock.uptimeMillis() < endTime) {
            if (this.isFinished) {
                return true;
            }
            this.sleeper.sleepMini();
        }
        return false;
    }
}
