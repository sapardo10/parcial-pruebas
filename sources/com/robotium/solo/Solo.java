package com.robotium.solo;

import android.app.Activity;
import android.app.Instrumentation;
import android.app.Instrumentation.ActivityMonitor;
import android.graphics.PointF;
import android.os.Build.VERSION;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;
import com.robotium.solo.Illustration.Builder;
import com.robotium.solo.Scroller.Side;
import java.util.ArrayList;
import junit.framework.Assert;

public class Solo {
    public static final int CLOSED = 0;
    public static final int DELETE = 67;
    public static final int DOWN = 20;
    public static final int ENTER = 66;
    public static final int LANDSCAPE = 0;
    public static final int LEFT = 21;
    public static final int MENU = 82;
    public static final int OPENED = 1;
    public static final int PORTRAIT = 1;
    public static final int RIGHT = 22;
    public static final int UP = 19;
    protected final ActivityUtils activityUtils;
    protected final Asserter asserter;
    protected final Checker checker;
    protected final Clicker clicker;
    private final Config config;
    protected final DialogUtils dialogUtils;
    protected final Getter getter;
    protected final Illustrator illustrator;
    protected final Instrumentation instrumentation;
    protected final Presser presser;
    protected final Rotator rotator;
    protected final ScreenshotTaker screenshotTaker;
    protected final Scroller scroller;
    protected final Searcher searcher;
    protected final Sender sender;
    protected final Setter setter;
    protected final Sleeper sleeper;
    protected final Swiper swiper;
    protected final SystemUtils systemUtils;
    protected final Tapper tapper;
    protected final TextEnterer textEnterer;
    protected final ViewFetcher viewFetcher;
    protected final Waiter waiter;
    protected String webUrl;
    protected final WebUtils webUtils;
    protected final Zoomer zoomer;

    /* renamed from: com.robotium.solo.Solo$1 */
    class C06831 implements Runnable {
        C06831() {
        }

        public void run() {
            Solo.this.clicker.clickOnActionBarHomeButton();
        }
    }

    public static class Config {
        public boolean commandLogging;
        public String commandLoggingTag;
        public ScreenshotFileType screenshotFileType;
        public String screenshotSavePath;
        public boolean shouldScroll;
        public int sleepDuration;
        public int sleepMiniDuration;
        public int timeout_large = 20000;
        public int timeout_small = 10000;
        public boolean trackActivities;
        public boolean useJavaScriptToClickWebElements;
        public String webFrame;

        public enum ScreenshotFileType {
            JPEG,
            PNG
        }

        public Config() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(Environment.getExternalStorageDirectory());
            stringBuilder.append("/Robotium-Screenshots/");
            this.screenshotSavePath = stringBuilder.toString();
            this.screenshotFileType = ScreenshotFileType.JPEG;
            this.shouldScroll = true;
            this.useJavaScriptToClickWebElements = false;
            this.trackActivities = true;
            this.webFrame = "document";
            this.commandLogging = false;
            this.commandLoggingTag = "Robotium";
            this.sleepDuration = 500;
            this.sleepMiniDuration = 300;
        }
    }

    /* renamed from: com.robotium.solo.Solo$4 */
    class C09884 implements Condition {
        C09884() {
        }

        public boolean isSatisfied() {
            return Solo.this.activityUtils.isActivityStackEmpty();
        }
    }

    public Solo(Instrumentation instrumentation, Activity activity) {
        this(new Config(), instrumentation, activity);
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Solo(");
            stringBuilder.append(instrumentation);
            stringBuilder.append(", ");
            stringBuilder.append(activity);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
    }

    public Solo(Instrumentation instrumentation, Config config) {
        this(config, instrumentation, null);
        if (config.commandLogging) {
            String str = config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Solo(");
            stringBuilder.append(instrumentation);
            stringBuilder.append(", ");
            stringBuilder.append(config);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
    }

    public Solo(Instrumentation instrumentation, Config config, Activity activity) {
        this(config, instrumentation, activity);
        if (config.commandLogging) {
            String str = config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Solo(");
            stringBuilder.append(instrumentation);
            stringBuilder.append(", ");
            stringBuilder.append(config);
            stringBuilder.append(", ");
            stringBuilder.append(activity);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
    }

    private Solo(Config config, Instrumentation instrumentation, Activity activity) {
        this.webUrl = null;
        if (config.commandLogging) {
            String str = config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Solo(");
            stringBuilder.append(config);
            stringBuilder.append(", ");
            stringBuilder.append(instrumentation);
            stringBuilder.append(", ");
            stringBuilder.append(activity);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        this.config = config == null ? new Config() : config;
        this.instrumentation = instrumentation;
        this.sleeper = new Sleeper(config.sleepDuration, config.sleepMiniDuration);
        this.sender = new Sender(instrumentation, this.sleeper);
        this.activityUtils = new ActivityUtils(config, instrumentation, activity, this.sleeper);
        this.viewFetcher = new ViewFetcher(instrumentation, this.sleeper);
        this.screenshotTaker = new ScreenshotTaker(config, instrumentation, this.activityUtils, this.viewFetcher, this.sleeper);
        this.dialogUtils = new DialogUtils(instrumentation, this.activityUtils, this.viewFetcher, this.sleeper);
        this.webUtils = new WebUtils(config, instrumentation, this.viewFetcher, this.sleeper);
        this.scroller = new Scroller(config, instrumentation, this.viewFetcher, this.sleeper);
        this.searcher = new Searcher(this.viewFetcher, this.webUtils, this.scroller, this.sleeper);
        this.waiter = new Waiter(instrumentation, this.activityUtils, this.viewFetcher, this.searcher, this.scroller, this.sleeper);
        this.getter = new Getter(instrumentation, this.activityUtils, this.waiter);
        this.clicker = new Clicker(this.activityUtils, this.viewFetcher, this.sender, instrumentation, this.sleeper, this.waiter, this.webUtils, this.dialogUtils);
        this.setter = new Setter(this.activityUtils, this.getter, this.clicker, this.waiter);
        this.asserter = new Asserter(this.activityUtils, this.waiter);
        this.checker = new Checker(this.viewFetcher, this.waiter);
        this.zoomer = new Zoomer(instrumentation);
        this.swiper = new Swiper(instrumentation);
        this.tapper = new Tapper(instrumentation);
        this.illustrator = new Illustrator(instrumentation);
        this.rotator = new Rotator(instrumentation);
        this.presser = new Presser(this.viewFetcher, this.clicker, instrumentation, this.sleeper, this.waiter, this.dialogUtils);
        this.textEnterer = new TextEnterer(instrumentation, this.clicker, this.dialogUtils);
        this.systemUtils = new SystemUtils(instrumentation);
        initialize();
    }

    public Solo(Instrumentation instrumentation) {
        this(new Config(), instrumentation, null);
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Solo(");
            stringBuilder.append(instrumentation);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
    }

    public ActivityMonitor getActivityMonitor() {
        if (this.config.commandLogging) {
            Log.d(this.config.commandLoggingTag, "getActivityMonitor()");
        }
        return this.activityUtils.getActivityMonitor();
    }

    public Config getConfig() {
        if (this.config.commandLogging) {
            Log.d(this.config.commandLoggingTag, "getConfig()");
        }
        return this.config;
    }

    public ArrayList<View> getViews() {
        try {
            if (this.config.commandLogging) {
                Log.d(this.config.commandLoggingTag, "getViews()");
            }
            return this.viewFetcher.getViews(null, false);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<View> getViews(View parent) {
        try {
            if (this.config.commandLogging) {
                Log.d(this.config.commandLoggingTag, "getViews()");
            }
            return this.viewFetcher.getViews(parent, false);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public View getTopParent(View view) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("getTopParent(");
            stringBuilder.append(view);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.viewFetcher.getTopParent(view);
    }

    public boolean waitForText(String text) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("waitForText(\"");
            stringBuilder.append(text);
            stringBuilder.append("\")");
            Log.d(str, stringBuilder.toString());
        }
        return this.waiter.waitForText(text) != null;
    }

    public boolean waitForText(String text, int minimumNumberOfMatches, long timeout) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("waitForText(\"");
            stringBuilder.append(text);
            stringBuilder.append("\", ");
            stringBuilder.append(minimumNumberOfMatches);
            stringBuilder.append(", ");
            stringBuilder.append(timeout);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.waiter.waitForText(text, minimumNumberOfMatches, timeout) != null;
    }

    public boolean waitForText(String text, int minimumNumberOfMatches, long timeout, boolean scroll) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("waitForText(\"");
            stringBuilder.append(text);
            stringBuilder.append("\", ");
            stringBuilder.append(minimumNumberOfMatches);
            stringBuilder.append(", ");
            stringBuilder.append(timeout);
            stringBuilder.append(", ");
            stringBuilder.append(scroll);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.waiter.waitForText(text, minimumNumberOfMatches, timeout, scroll) != null;
    }

    public boolean waitForText(String text, int minimumNumberOfMatches, long timeout, boolean scroll, boolean onlyVisible) {
        if (this.config.commandLogging) {
            String str = r0.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("waitForText(\"");
            stringBuilder.append(text);
            stringBuilder.append("\", ");
            stringBuilder.append(minimumNumberOfMatches);
            stringBuilder.append(", ");
            stringBuilder.append(timeout);
            stringBuilder.append(", ");
            stringBuilder.append(scroll);
            stringBuilder.append(", ");
            stringBuilder.append(onlyVisible);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        } else {
            String str2 = text;
            int i = minimumNumberOfMatches;
            long j = timeout;
            boolean z = scroll;
            boolean z2 = onlyVisible;
        }
        return r0.waiter.waitForText(text, minimumNumberOfMatches, timeout, scroll, onlyVisible, true) != null;
    }

    public boolean waitForView(int id) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("waitForView(");
            stringBuilder.append(id);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return waitForView(id, 0, Timeout.getLargeTimeout(), true);
    }

    public boolean waitForView(int id, int minimumNumberOfMatches, int timeout) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("waitForView(");
            stringBuilder.append(id);
            stringBuilder.append(", ");
            stringBuilder.append(minimumNumberOfMatches);
            stringBuilder.append(", ");
            stringBuilder.append(timeout);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return waitForView(id, minimumNumberOfMatches, timeout, true);
    }

    public boolean waitForView(int id, int minimumNumberOfMatches, int timeout, boolean scroll) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("waitForView(");
            stringBuilder.append(id);
            stringBuilder.append(", ");
            stringBuilder.append(minimumNumberOfMatches);
            stringBuilder.append(", ");
            stringBuilder.append(timeout);
            stringBuilder.append(", ");
            stringBuilder.append(scroll);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        int index = minimumNumberOfMatches - 1;
        if (index < 1) {
            index = 0;
        }
        if (this.waiter.waitForView(id, index, timeout, scroll) != null) {
            return true;
        }
        return false;
    }

    public boolean waitForView(Object tag) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("waitForView(");
            stringBuilder.append(tag);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return waitForView(tag, 0, Timeout.getLargeTimeout(), true);
    }

    public boolean waitForView(Object tag, int minimumNumberOfMatches, int timeout) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("waitForView(");
            stringBuilder.append(tag);
            stringBuilder.append(", ");
            stringBuilder.append(minimumNumberOfMatches);
            stringBuilder.append(", ");
            stringBuilder.append(timeout);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return waitForView(tag, minimumNumberOfMatches, timeout, true);
    }

    public boolean waitForView(Object tag, int minimumNumberOfMatches, int timeout, boolean scroll) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("waitForView(");
            stringBuilder.append(tag);
            stringBuilder.append(", ");
            stringBuilder.append(minimumNumberOfMatches);
            stringBuilder.append(", ");
            stringBuilder.append(timeout);
            stringBuilder.append(", ");
            stringBuilder.append(scroll);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        int index = minimumNumberOfMatches - 1;
        if (index < 1) {
            index = 0;
        }
        if (this.waiter.waitForView(tag, index, timeout, scroll) != null) {
            return true;
        }
        return false;
    }

    public <T extends View> boolean waitForView(Class<T> viewClass) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("waitForView(");
            stringBuilder.append(viewClass);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.waiter.waitForView((Class) viewClass, 0, Timeout.getLargeTimeout(), true);
    }

    public <T extends View> boolean waitForView(View view) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("waitForView(");
            stringBuilder.append(view);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.waiter.waitForView(view);
    }

    public <T extends View> boolean waitForView(View view, int timeout, boolean scroll) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("waitForView(");
            stringBuilder.append(view);
            stringBuilder.append(", ");
            stringBuilder.append(timeout);
            stringBuilder.append(", ");
            stringBuilder.append(scroll);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        boolean checkIsShown = false;
        if (!scroll) {
            checkIsShown = true;
        }
        if (this.waiter.waitForView(view, timeout, scroll, checkIsShown) != null) {
            return true;
        }
        return false;
    }

    public <T extends View> boolean waitForView(Class<T> viewClass, int minimumNumberOfMatches, int timeout) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("waitForView(");
            stringBuilder.append(viewClass);
            stringBuilder.append(", ");
            stringBuilder.append(minimumNumberOfMatches);
            stringBuilder.append(", ");
            stringBuilder.append(timeout);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        int index = minimumNumberOfMatches - 1;
        if (index < 1) {
            index = 0;
        }
        return this.waiter.waitForView((Class) viewClass, index, timeout, true);
    }

    public <T extends View> boolean waitForView(Class<T> viewClass, int minimumNumberOfMatches, int timeout, boolean scroll) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("waitForView(");
            stringBuilder.append(viewClass);
            stringBuilder.append(", ");
            stringBuilder.append(minimumNumberOfMatches);
            stringBuilder.append(", ");
            stringBuilder.append(timeout);
            stringBuilder.append(", ");
            stringBuilder.append(scroll);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        int index = minimumNumberOfMatches - 1;
        if (index < 1) {
            index = 0;
        }
        return this.waiter.waitForView((Class) viewClass, index, timeout, scroll);
    }

    public boolean waitForWebElement(By by) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("waitForWebElement(");
            stringBuilder.append(by);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.waiter.waitForWebElement(by, 0, Timeout.getLargeTimeout(), true) != null;
    }

    public boolean waitForWebElement(By by, int timeout, boolean scroll) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("waitForWebElement(");
            stringBuilder.append(by);
            stringBuilder.append(", ");
            stringBuilder.append(timeout);
            stringBuilder.append(", ");
            stringBuilder.append(scroll);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.waiter.waitForWebElement(by, 0, timeout, scroll) != null;
    }

    public boolean waitForWebElement(By by, int minimumNumberOfMatches, int timeout, boolean scroll) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("waitForWebElement(");
            stringBuilder.append(by);
            stringBuilder.append(", ");
            stringBuilder.append(minimumNumberOfMatches);
            stringBuilder.append(",");
            stringBuilder.append(timeout);
            stringBuilder.append(", ");
            stringBuilder.append(scroll);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.waiter.waitForWebElement(by, minimumNumberOfMatches, timeout, scroll) != null;
    }

    public boolean waitForCondition(Condition condition, int timeout) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("waitForCondition(");
            stringBuilder.append(condition);
            stringBuilder.append(",");
            stringBuilder.append(timeout);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.waiter.waitForCondition(condition, timeout);
    }

    public boolean searchEditText(String text) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("searchEditText(\"");
            stringBuilder.append(text);
            stringBuilder.append("\")");
            Log.d(str, stringBuilder.toString());
        }
        return this.searcher.searchWithTimeoutFor(EditText.class, text, 1, true, false);
    }

    public boolean searchButton(String text) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("searchButton(\"");
            stringBuilder.append(text);
            stringBuilder.append("\")");
            Log.d(str, stringBuilder.toString());
        }
        return this.searcher.searchWithTimeoutFor(Button.class, text, 0, true, false);
    }

    public boolean searchButton(String text, boolean onlyVisible) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("searchButton(\"");
            stringBuilder.append(text);
            stringBuilder.append("\", ");
            stringBuilder.append(onlyVisible);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.searcher.searchWithTimeoutFor(Button.class, text, 0, true, onlyVisible);
    }

    public boolean searchToggleButton(String text) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("searchToggleButton(\"");
            stringBuilder.append(text);
            stringBuilder.append("\")");
            Log.d(str, stringBuilder.toString());
        }
        return this.searcher.searchWithTimeoutFor(ToggleButton.class, text, 0, true, false);
    }

    public boolean searchButton(String text, int minimumNumberOfMatches) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("searchButton(\"");
            stringBuilder.append(text);
            stringBuilder.append("\", ");
            stringBuilder.append(minimumNumberOfMatches);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.searcher.searchWithTimeoutFor(Button.class, text, minimumNumberOfMatches, true, false);
    }

    public boolean searchButton(String text, int minimumNumberOfMatches, boolean onlyVisible) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("searchButton(\"");
            stringBuilder.append(text);
            stringBuilder.append("\", ");
            stringBuilder.append(minimumNumberOfMatches);
            stringBuilder.append(", ");
            stringBuilder.append(onlyVisible);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.searcher.searchWithTimeoutFor(Button.class, text, minimumNumberOfMatches, true, onlyVisible);
    }

    public boolean searchToggleButton(String text, int minimumNumberOfMatches) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("searchToggleButton(\"");
            stringBuilder.append(text);
            stringBuilder.append("\", ");
            stringBuilder.append(minimumNumberOfMatches);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.searcher.searchWithTimeoutFor(ToggleButton.class, text, minimumNumberOfMatches, true, false);
    }

    public boolean searchText(String text) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("searchText(\"");
            stringBuilder.append(text);
            stringBuilder.append("\")");
            Log.d(str, stringBuilder.toString());
        }
        return this.searcher.searchWithTimeoutFor(TextView.class, text, 0, true, false);
    }

    public boolean searchText(String text, boolean onlyVisible) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("searchText(\"");
            stringBuilder.append(text);
            stringBuilder.append("\", ");
            stringBuilder.append(onlyVisible);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.searcher.searchWithTimeoutFor(TextView.class, text, 0, true, onlyVisible);
    }

    public boolean searchText(String text, int minimumNumberOfMatches) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("searchText(\"");
            stringBuilder.append(text);
            stringBuilder.append("\", ");
            stringBuilder.append(minimumNumberOfMatches);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.searcher.searchWithTimeoutFor(TextView.class, text, minimumNumberOfMatches, true, false);
    }

    public boolean searchText(String text, int minimumNumberOfMatches, boolean scroll) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("searchText(\"");
            stringBuilder.append(text);
            stringBuilder.append("\", ");
            stringBuilder.append(minimumNumberOfMatches);
            stringBuilder.append(", ");
            stringBuilder.append(scroll);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.searcher.searchWithTimeoutFor(TextView.class, text, minimumNumberOfMatches, scroll, false);
    }

    public boolean searchText(String text, int minimumNumberOfMatches, boolean scroll, boolean onlyVisible) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("searchText(\"");
            stringBuilder.append(text);
            stringBuilder.append("\", ");
            stringBuilder.append(minimumNumberOfMatches);
            stringBuilder.append(", ");
            stringBuilder.append(scroll);
            stringBuilder.append(", ");
            stringBuilder.append(onlyVisible);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.searcher.searchWithTimeoutFor(TextView.class, text, minimumNumberOfMatches, scroll, onlyVisible);
    }

    public void setActivityOrientation(int orientation) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("setActivityOrientation(");
            stringBuilder.append(orientation);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        this.activityUtils.setActivityOrientation(orientation);
    }

    public Activity getCurrentActivity() {
        if (this.config.commandLogging) {
            Log.d(this.config.commandLoggingTag, "getCurrentActivity()");
        }
        return this.activityUtils.getCurrentActivity(false);
    }

    public void assertCurrentActivity(String message, String name) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("assertCurrentActivity(\"");
            stringBuilder.append(message);
            stringBuilder.append("\", \"");
            stringBuilder.append(name);
            stringBuilder.append("\")");
            Log.d(str, stringBuilder.toString());
        }
        this.asserter.assertCurrentActivity(message, name);
    }

    public void assertCurrentActivity(String message, Class activityClass) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("assertCurrentActivity(");
            stringBuilder.append(message);
            stringBuilder.append(", ");
            stringBuilder.append(activityClass);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        this.asserter.assertCurrentActivity(message, activityClass);
    }

    public void assertCurrentActivity(String message, String name, boolean isNewInstance) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("assertCurrentActivity(");
            stringBuilder.append(message);
            stringBuilder.append(", ");
            stringBuilder.append(name);
            stringBuilder.append(", ");
            stringBuilder.append(isNewInstance);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        this.asserter.assertCurrentActivity(message, name, isNewInstance);
    }

    public void assertCurrentActivity(String message, Class activityClass, boolean isNewInstance) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("assertCurrentActivity(\"");
            stringBuilder.append(message);
            stringBuilder.append("\", ");
            stringBuilder.append(activityClass);
            stringBuilder.append(", ");
            stringBuilder.append(isNewInstance);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        this.asserter.assertCurrentActivity(message, activityClass, isNewInstance);
    }

    public void assertMemoryNotLow() {
        if (this.config.commandLogging) {
            Log.d(this.config.commandLoggingTag, "assertMemoryNotLow()");
        }
        this.asserter.assertMemoryNotLow();
    }

    public boolean waitForDialogToOpen() {
        if (this.config.commandLogging) {
            Log.d(this.config.commandLoggingTag, "waitForDialogToOpen()");
        }
        return this.dialogUtils.waitForDialogToOpen((long) Timeout.getLargeTimeout(), true);
    }

    public boolean waitForDialogToClose() {
        if (this.config.commandLogging) {
            Log.d(this.config.commandLoggingTag, "waitForDialogToClose()");
        }
        return this.dialogUtils.waitForDialogToClose((long) Timeout.getLargeTimeout());
    }

    public boolean waitForDialogToOpen(long timeout) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("waitForDialogToOpen(");
            stringBuilder.append(timeout);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.dialogUtils.waitForDialogToOpen(timeout, true);
    }

    public boolean waitForDialogToClose(long timeout) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("waitForDialogToClose(");
            stringBuilder.append(timeout);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.dialogUtils.waitForDialogToClose(timeout);
    }

    public void goBack() {
        if (this.config.commandLogging) {
            Log.d(this.config.commandLoggingTag, "goBack()");
        }
        hideSoftKeyboard();
        this.sender.goBack();
    }

    public void clickOnScreen(float x, float y) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("clickOnScreen(");
            stringBuilder.append(x);
            stringBuilder.append(", ");
            stringBuilder.append(y);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        this.sleeper.sleep();
        this.clicker.clickOnScreen(x, y, null);
    }

    public void clickOnScreen(float x, float y, int numberOfClicks) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("clickOnScreen(");
            stringBuilder.append(x);
            stringBuilder.append(", ");
            stringBuilder.append(y);
            stringBuilder.append(", ");
            stringBuilder.append(numberOfClicks);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        if (VERSION.SDK_INT >= 14) {
            this.tapper.generateTapGesture(numberOfClicks, new PointF(x, y));
            return;
        }
        throw new RuntimeException("clickOnScreen(float x, float y, int numberOfClicks) requires API level >= 14");
    }

    public void clickLongOnScreen(float x, float y) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("clickLongOnScreen(");
            stringBuilder.append(x);
            stringBuilder.append(", ");
            stringBuilder.append(y);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        this.clicker.clickLongOnScreen(x, y, 0, null);
    }

    public void clickLongOnScreen(float x, float y, int time) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("clickLongOnScreen(");
            stringBuilder.append(x);
            stringBuilder.append(", ");
            stringBuilder.append(y);
            stringBuilder.append(", ");
            stringBuilder.append(time);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        this.clicker.clickLongOnScreen(x, y, time, null);
    }

    public void clickOnButton(String text) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("clickOnButton(\"");
            stringBuilder.append(text);
            stringBuilder.append("\")");
            Log.d(str, stringBuilder.toString());
        }
        this.clicker.clickOn(Button.class, text);
    }

    public void clickOnImageButton(int index) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("clickOnImageButton(");
            stringBuilder.append(index);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        this.clicker.clickOn(ImageButton.class, index);
    }

    public void clickOnToggleButton(String text) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("clickOnToggleButton(\"");
            stringBuilder.append(text);
            stringBuilder.append("\")");
            Log.d(str, stringBuilder.toString());
        }
        this.clicker.clickOn(ToggleButton.class, text);
    }

    public void clickOnMenuItem(String text) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("clickOnMenuItem(\"");
            stringBuilder.append(text);
            stringBuilder.append("\")");
            Log.d(str, stringBuilder.toString());
        }
        this.clicker.clickOnMenuItem(text);
    }

    public void clickOnMenuItem(String text, boolean subMenu) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("clickOnMenuItem(\"");
            stringBuilder.append(text);
            stringBuilder.append("\", ");
            stringBuilder.append(subMenu);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        this.clicker.clickOnMenuItem(text, subMenu);
    }

    public void clickOnWebElement(WebElement webElement) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("clickOnWebElement(");
            stringBuilder.append(webElement);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        if (webElement == null) {
            Assert.fail("WebElement is null and can therefore not be clicked!");
        }
        this.clicker.clickOnScreen((float) webElement.getLocationX(), (float) webElement.getLocationY(), null);
    }

    public void clickOnWebElement(By by) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("clickOnWebElement(");
            stringBuilder.append(by);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        clickOnWebElement(by, 0, true);
    }

    public void clickOnWebElement(By by, int match) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("clickOnWebElement(");
            stringBuilder.append(by);
            stringBuilder.append(", ");
            stringBuilder.append(match);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        clickOnWebElement(by, match, true);
    }

    public void clickOnWebElement(By by, int match, boolean scroll) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("clickOnWebElement(");
            stringBuilder.append(by);
            stringBuilder.append(", ");
            stringBuilder.append(match);
            stringBuilder.append(", ");
            stringBuilder.append(scroll);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        this.clicker.clickOnWebElement(by, match, scroll, this.config.useJavaScriptToClickWebElements);
    }

    public void pressMenuItem(int index) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("pressMenuItem(");
            stringBuilder.append(index);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        this.presser.pressMenuItem(index);
    }

    public void pressMenuItem(int index, int itemsPerRow) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("pressMenuItem(");
            stringBuilder.append(index);
            stringBuilder.append(", ");
            stringBuilder.append(itemsPerRow);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        this.presser.pressMenuItem(index, itemsPerRow);
    }

    public void pressSoftKeyboardNextButton() {
        if (this.config.commandLogging) {
            Log.d(this.config.commandLoggingTag, "pressSoftKeyboardNextButton()");
        }
        this.presser.pressSoftKeyboard(5);
    }

    public void pressSoftKeyboardSearchButton() {
        if (this.config.commandLogging) {
            Log.d(this.config.commandLoggingTag, "pressSoftKeyboardSearchButton()");
        }
        this.presser.pressSoftKeyboard(3);
    }

    public void pressSoftKeyboardGoButton() {
        if (this.config.commandLogging) {
            Log.d(this.config.commandLoggingTag, "pressSoftKeyboardGoButton()");
        }
        this.presser.pressSoftKeyboard(2);
    }

    public void pressSoftKeyboardDoneButton() {
        if (this.config.commandLogging) {
            Log.d(this.config.commandLoggingTag, "pressSoftKeyboardDoneButton()");
        }
        this.presser.pressSoftKeyboard(6);
    }

    public void pressSpinnerItem(int spinnerIndex, int itemIndex) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("pressSpinnerItem(");
            stringBuilder.append(spinnerIndex);
            stringBuilder.append(", ");
            stringBuilder.append(itemIndex);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        this.presser.pressSpinnerItem(spinnerIndex, itemIndex);
    }

    public void clickOnView(View view) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("clickOnView(");
            stringBuilder.append(view);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        this.clicker.clickOnScreen(this.waiter.waitForView(view, Timeout.getSmallTimeout()));
    }

    public void clickOnView(View view, boolean immediately) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("clickOnView(");
            stringBuilder.append(view);
            stringBuilder.append(", ");
            stringBuilder.append(immediately);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        if (immediately) {
            this.clicker.clickOnScreen(view);
            return;
        }
        this.clicker.clickOnScreen(this.waiter.waitForView(view, Timeout.getSmallTimeout()));
    }

    public void clickLongOnView(View view) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("clickLongOnView(");
            stringBuilder.append(view);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        this.clicker.clickOnScreen(this.waiter.waitForView(view, Timeout.getSmallTimeout()), true, 0);
    }

    public void clickLongOnView(View view, int time) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("clickLongOnView(");
            stringBuilder.append(view);
            stringBuilder.append(", ");
            stringBuilder.append(time);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        this.clicker.clickOnScreen(view, true, time);
    }

    public void clickOnText(String text) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("clickOnText(\"");
            stringBuilder.append(text);
            stringBuilder.append("\")");
            Log.d(str, stringBuilder.toString());
        }
        this.clicker.clickOnText(text, false, 1, true, 0);
    }

    public void clickOnText(String text, int match) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("clickOnText(\"");
            stringBuilder.append(text);
            stringBuilder.append("\", ");
            stringBuilder.append(match);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        this.clicker.clickOnText(text, false, match, true, 0);
    }

    public void clickOnText(String text, int match, boolean scroll) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("clickOnText(\"");
            stringBuilder.append(text);
            stringBuilder.append("\", ");
            stringBuilder.append(match);
            stringBuilder.append(", ");
            stringBuilder.append(scroll);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        this.clicker.clickOnText(text, false, match, scroll, 0);
    }

    public void clickLongOnText(String text) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("clickLongOnText(\"");
            stringBuilder.append(text);
            stringBuilder.append("\")");
            Log.d(str, stringBuilder.toString());
        }
        this.clicker.clickOnText(text, true, 1, true, 0);
    }

    public void clickLongOnText(String text, int match) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("clickLongOnText(\"");
            stringBuilder.append(text);
            stringBuilder.append("\", ");
            stringBuilder.append(match);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        this.clicker.clickOnText(text, true, match, true, 0);
    }

    public void clickLongOnText(String text, int match, boolean scroll) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("clickLongOnText(\"");
            stringBuilder.append(text);
            stringBuilder.append("\", ");
            stringBuilder.append(match);
            stringBuilder.append(", ");
            stringBuilder.append(scroll);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        this.clicker.clickOnText(text, true, match, scroll, 0);
    }

    public void clickLongOnText(String text, int match, int time) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("clickLongOnText(\"");
            stringBuilder.append(text);
            stringBuilder.append("\", ");
            stringBuilder.append(match);
            stringBuilder.append(", ");
            stringBuilder.append(time);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        this.clicker.clickOnText(text, true, match, true, time);
    }

    public void clickLongOnTextAndPress(String text, int index) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("clickLongOnTextAndPress(\"");
            stringBuilder.append(text);
            stringBuilder.append("\", ");
            stringBuilder.append(index);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        this.clicker.clickLongOnTextAndPress(text, index);
    }

    public void clickOnButton(int index) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("clickOnButton(");
            stringBuilder.append(index);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        this.clicker.clickOn(Button.class, index);
    }

    public void clickOnRadioButton(int index) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("clickOnRadioButton(");
            stringBuilder.append(index);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        this.clicker.clickOn(RadioButton.class, index);
    }

    public void clickOnCheckBox(int index) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("clickOnCheckBox(");
            stringBuilder.append(index);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        this.clicker.clickOn(CheckBox.class, index);
    }

    public void clickOnEditText(int index) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("clickOnEditText(");
            stringBuilder.append(index);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        this.clicker.clickOn(EditText.class, index);
    }

    public ArrayList<TextView> clickInList(int line) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("clickInList(");
            stringBuilder.append(line);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.clicker.clickInList(line);
    }

    public ArrayList<TextView> clickInList(int line, int index) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("clickInList(");
            stringBuilder.append(line);
            stringBuilder.append(", ");
            stringBuilder.append(index);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.clicker.clickInList(line, index, 0, false, 0);
    }

    public void clickInList(int line, int index, int id) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("clickInList(");
            stringBuilder.append(line);
            stringBuilder.append(", ");
            stringBuilder.append(index);
            stringBuilder.append(", ");
            stringBuilder.append(id);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        this.clicker.clickInList(line, index, id, false, 0);
    }

    public ArrayList<TextView> clickLongInList(int line) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("clickLongInList(");
            stringBuilder.append(line);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.clicker.clickInList(line, 0, 0, true, 0);
    }

    public ArrayList<TextView> clickLongInList(int line, int index) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("clickLongInList(");
            stringBuilder.append(line);
            stringBuilder.append(", ");
            stringBuilder.append(index);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.clicker.clickInList(line, index, 0, true, 0);
    }

    public ArrayList<TextView> clickLongInList(int line, int index, int time) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("clickLongInList(");
            stringBuilder.append(line);
            stringBuilder.append(", ");
            stringBuilder.append(index);
            stringBuilder.append(", ");
            stringBuilder.append(time);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.clicker.clickInList(line, index, 0, true, time);
    }

    public ArrayList<TextView> clickInRecyclerView(int itemIndex) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("clickInRecyclerView(");
            stringBuilder.append(itemIndex);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.clicker.clickInRecyclerView(itemIndex);
    }

    public ArrayList<TextView> clickInRecyclerView(int itemIndex, int recyclerViewIndex) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("clickInRecyclerView(");
            stringBuilder.append(itemIndex);
            stringBuilder.append(", ");
            stringBuilder.append(recyclerViewIndex);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.clicker.clickInRecyclerView(itemIndex, recyclerViewIndex, 0, false, 0);
    }

    public void clickInRecyclerView(int itemIndex, int recyclerViewIndex, int id) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("clickInRecyclerView(");
            stringBuilder.append(itemIndex);
            stringBuilder.append(", ");
            stringBuilder.append(recyclerViewIndex);
            stringBuilder.append(", ");
            stringBuilder.append(id);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        this.clicker.clickInRecyclerView(itemIndex, recyclerViewIndex, id, false, 0);
    }

    public ArrayList<TextView> clickLongInRecycleView(int itemIndex) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("clickLongInRecycleView(");
            stringBuilder.append(itemIndex);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.clicker.clickInRecyclerView(itemIndex, 0, 0, true, 0);
    }

    public ArrayList<TextView> clickLongInRecycleView(int itemIndex, int recyclerViewIndex) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("clickLongInRecycleView(");
            stringBuilder.append(itemIndex);
            stringBuilder.append(", ");
            stringBuilder.append(recyclerViewIndex);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.clicker.clickInRecyclerView(itemIndex, recyclerViewIndex, 0, true, 0);
    }

    public ArrayList<TextView> clickLongInRecycleView(int itemIndex, int recyclerViewIndex, int time) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("clickLongInRecycleView(");
            stringBuilder.append(itemIndex);
            stringBuilder.append(", ");
            stringBuilder.append(recyclerViewIndex);
            stringBuilder.append(", ");
            stringBuilder.append(time);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.clicker.clickInRecyclerView(itemIndex, recyclerViewIndex, 0, true, time);
    }

    public void clickOnActionBarItem(int id) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("clickOnActionBarItem(");
            stringBuilder.append(id);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        this.clicker.clickOnActionBarItem(id);
    }

    public void clickOnActionBarHomeButton() {
        if (this.config.commandLogging) {
            Log.d(this.config.commandLoggingTag, "clickOnActionBarHomeButton()");
        }
        this.instrumentation.runOnMainSync(new C06831());
    }

    public Builder createIllustrationBuilder() {
        if (this.config.commandLogging) {
            Log.d(this.config.commandLoggingTag, "createIllustrationBuilder()");
        }
        return new Builder();
    }

    public void illustrate(Illustration illustration) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("illustrate(");
            stringBuilder.append(illustration);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        this.illustrator.illustrate(illustration);
    }

    public void drag(float fromX, float toX, float fromY, float toY, int stepCount) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("drag(");
            stringBuilder.append(fromX);
            stringBuilder.append(", ");
            stringBuilder.append(toX);
            stringBuilder.append(", ");
            stringBuilder.append(fromY);
            stringBuilder.append(", ");
            stringBuilder.append(toY);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        this.dialogUtils.hideSoftKeyboard(null, false, true);
        this.scroller.drag(fromX, toX, fromY, toY, stepCount);
    }

    public boolean scrollDown() {
        if (this.config.commandLogging) {
            Log.d(this.config.commandLoggingTag, "scrollDown()");
        }
        if (this.viewFetcher.getRecyclerView(true, 0) != null) {
            this.waiter.waitForViews(true, AbsListView.class, ScrollView.class, WebView.class, recyclerView.getClass());
        } else {
            this.waiter.waitForViews(true, AbsListView.class, ScrollView.class, WebView.class);
        }
        return this.scroller.scroll(0);
    }

    public void scrollToBottom() {
        if (this.config.commandLogging) {
            Log.d(this.config.commandLoggingTag, "scrollToBottom()");
        }
        if (this.viewFetcher.getRecyclerView(true, 0) != null) {
            this.waiter.waitForViews(true, AbsListView.class, ScrollView.class, WebView.class, recyclerView.getClass());
        } else {
            this.waiter.waitForViews(true, AbsListView.class, ScrollView.class, WebView.class);
        }
        this.scroller.scroll(0, true);
    }

    public boolean scrollUp() {
        if (this.config.commandLogging) {
            Log.d(this.config.commandLoggingTag, "scrollUp()");
        }
        if (this.viewFetcher.getRecyclerView(true, 0) != null) {
            this.waiter.waitForViews(true, AbsListView.class, ScrollView.class, WebView.class, recyclerView.getClass());
        } else {
            this.waiter.waitForViews(true, AbsListView.class, ScrollView.class, WebView.class);
        }
        return this.scroller.scroll(1);
    }

    public void scrollToTop() {
        if (this.config.commandLogging) {
            Log.d(this.config.commandLoggingTag, "scrollToTop()");
        }
        if (this.viewFetcher.getRecyclerView(true, 0) != null) {
            this.waiter.waitForViews(true, AbsListView.class, ScrollView.class, WebView.class, recyclerView.getClass());
        } else {
            this.waiter.waitForViews(true, AbsListView.class, ScrollView.class, WebView.class);
        }
        this.scroller.scroll(1, true);
    }

    public boolean scrollDownList(AbsListView list) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("scrollDownList(");
            stringBuilder.append(list);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.scroller.scrollList(list, 0, false);
    }

    public boolean scrollListToBottom(AbsListView list) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("scrollListToBottom(");
            stringBuilder.append(list);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.scroller.scrollList(list, 0, true);
    }

    public boolean scrollUpList(AbsListView list) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("scrollUpList(");
            stringBuilder.append(list);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.scroller.scrollList(list, 1, false);
    }

    public boolean scrollListToTop(AbsListView list) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("scrollListToTop(");
            stringBuilder.append(list);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.scroller.scrollList(list, 1, true);
    }

    public boolean scrollDownList(int index) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("scrollDownList(");
            stringBuilder.append(index);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.scroller.scrollList((AbsListView) this.waiter.waitForAndGetView(index, ListView.class), 0, false);
    }

    public boolean scrollListToBottom(int index) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("scrollListToBottom(");
            stringBuilder.append(index);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.scroller.scrollList((AbsListView) this.waiter.waitForAndGetView(index, ListView.class), 0, true);
    }

    public boolean scrollUpList(int index) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("scrollUpList(");
            stringBuilder.append(index);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.scroller.scrollList((AbsListView) this.waiter.waitForAndGetView(index, ListView.class), 1, false);
    }

    public boolean scrollListToTop(int index) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("scrollListToTop(");
            stringBuilder.append(index);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.scroller.scrollList((AbsListView) this.waiter.waitForAndGetView(index, ListView.class), 1, true);
    }

    public void scrollListToLine(AbsListView absListView, int line) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("scrollListToLine(");
            stringBuilder.append(absListView);
            stringBuilder.append(", ");
            stringBuilder.append(line);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        this.scroller.scrollListToLine(absListView, line);
    }

    public void scrollListToLine(int index, int line) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("scrollListToLine(");
            stringBuilder.append(index);
            stringBuilder.append(", ");
            stringBuilder.append(line);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        this.scroller.scrollListToLine((AbsListView) this.waiter.waitForAndGetView(index, AbsListView.class), line);
    }

    public boolean scrollDownRecyclerView(int index) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("scrollDownRecyclerView(");
            stringBuilder.append(index);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        if (!this.config.shouldScroll) {
            return true;
        }
        return this.scroller.scrollView(this.viewFetcher.getRecyclerView(index, Timeout.getSmallTimeout()), 0);
    }

    public boolean scrollRecyclerViewToBottom(int index) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("scrollRecyclerViewToBottom(");
            stringBuilder.append(index);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        if (!this.config.shouldScroll) {
            return true;
        }
        this.scroller.scrollViewAllTheWay(this.viewFetcher.getRecyclerView(index, Timeout.getSmallTimeout()), 0);
        return false;
    }

    public boolean scrollUpRecyclerView(int index) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("scrollUpRecyclerView(");
            stringBuilder.append(index);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        if (!this.config.shouldScroll) {
            return true;
        }
        return this.scroller.scrollView(this.viewFetcher.getRecyclerView(index, Timeout.getSmallTimeout()), 1);
    }

    public boolean scrollRecyclerViewToTop(int index) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("scrollRecyclerViewToTop(");
            stringBuilder.append(index);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        if (!this.config.shouldScroll) {
            return false;
        }
        this.scroller.scrollViewAllTheWay(this.viewFetcher.getRecyclerView(index, Timeout.getSmallTimeout()), 1);
        return false;
    }

    public void scrollToSide(int side, float scrollPosition, int stepCount) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("scrollToSide(");
            stringBuilder.append(side);
            stringBuilder.append(", ");
            stringBuilder.append(scrollPosition);
            stringBuilder.append(", ");
            stringBuilder.append(stepCount);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        switch (side) {
            case 21:
                this.scroller.scrollToSide(Side.LEFT, scrollPosition, stepCount);
                return;
            case 22:
                this.scroller.scrollToSide(Side.RIGHT, scrollPosition, stepCount);
                return;
            default:
                return;
        }
    }

    public void scrollToSide(int side, float scrollPosition) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("scrollToSide(");
            stringBuilder.append(scrollPosition);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        scrollToSide(side, scrollPosition, 20);
    }

    public void scrollToSide(int side) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("scrollToSide(");
            stringBuilder.append(side);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        scrollToSide(side, 0.75f);
    }

    public void scrollViewToSide(View view, int side, float scrollPosition, int stepCount) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("scrollViewToSide(");
            stringBuilder.append(view);
            stringBuilder.append(", ");
            stringBuilder.append(side);
            stringBuilder.append(", ");
            stringBuilder.append(scrollPosition);
            stringBuilder.append(", ");
            stringBuilder.append(stepCount);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        waitForView(view);
        this.sleeper.sleep();
        switch (side) {
            case 21:
                this.scroller.scrollViewToSide(view, Side.LEFT, scrollPosition, stepCount);
                return;
            case 22:
                this.scroller.scrollViewToSide(view, Side.RIGHT, scrollPosition, stepCount);
                return;
            default:
                return;
        }
    }

    public void scrollViewToSide(View view, int side, float scrollPosition) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("scrollViewToSide(");
            stringBuilder.append(view);
            stringBuilder.append(", ");
            stringBuilder.append(side);
            stringBuilder.append(", ");
            stringBuilder.append(scrollPosition);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        scrollViewToSide(view, side, scrollPosition, 20);
    }

    public void scrollViewToSide(View view, int side) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("scrollViewToSide(");
            stringBuilder.append(view);
            stringBuilder.append(", ");
            stringBuilder.append(side);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        scrollViewToSide(view, side, 0.7f);
    }

    public void pinchToZoom(PointF startPoint1, PointF startPoint2, PointF endPoint1, PointF endPoint2) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("pinchToZoom(");
            stringBuilder.append(startPoint1);
            stringBuilder.append(", ");
            stringBuilder.append(startPoint2);
            stringBuilder.append(", ");
            stringBuilder.append(endPoint1);
            stringBuilder.append(", ");
            stringBuilder.append(endPoint2);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        if (VERSION.SDK_INT >= 14) {
            this.zoomer.generateZoomGesture(startPoint1, startPoint2, endPoint1, endPoint2);
            return;
        }
        throw new RuntimeException("pinchToZoom() requires API level >= 14");
    }

    public void swipe(PointF startPoint1, PointF startPoint2, PointF endPoint1, PointF endPoint2) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("swipe(");
            stringBuilder.append(startPoint1);
            stringBuilder.append(", ");
            stringBuilder.append(startPoint2);
            stringBuilder.append(", ");
            stringBuilder.append(endPoint1);
            stringBuilder.append(", ");
            stringBuilder.append(endPoint2);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        if (VERSION.SDK_INT >= 14) {
            this.swiper.generateSwipeGesture(startPoint1, startPoint2, endPoint1, endPoint2);
            return;
        }
        throw new RuntimeException("swipe() requires API level >= 14");
    }

    public void rotateLarge(PointF center1, PointF center2) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("rotateLarge(");
            stringBuilder.append(center1);
            stringBuilder.append(", ");
            stringBuilder.append(center2);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        if (VERSION.SDK_INT >= 14) {
            this.rotator.generateRotateGesture(0, center1, center2);
            return;
        }
        throw new RuntimeException("rotateLarge(PointF center1, PointF center2) requires API level >= 14");
    }

    public void rotateSmall(PointF center1, PointF center2) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("rotateSmall(");
            stringBuilder.append(center1);
            stringBuilder.append(", ");
            stringBuilder.append(center2);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        if (VERSION.SDK_INT >= 14) {
            this.rotator.generateRotateGesture(1, center1, center2);
            return;
        }
        throw new RuntimeException("rotateSmall(PointF center1, PointF center2) requires API level >= 14");
    }

    public void setMobileData(Boolean turnedOn) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("setMobileData(");
            stringBuilder.append(turnedOn);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        this.systemUtils.setMobileData(turnedOn);
    }

    public void setWiFiData(Boolean turnedOn) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("setWiFiData(");
            stringBuilder.append(turnedOn);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        this.systemUtils.setWiFiData(turnedOn);
    }

    public void setDatePicker(int index, int year, int monthOfYear, int dayOfMonth) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("setDatePicker(");
            stringBuilder.append(index);
            stringBuilder.append(", ");
            stringBuilder.append(year);
            stringBuilder.append(", ");
            stringBuilder.append(monthOfYear);
            stringBuilder.append(", ");
            stringBuilder.append(dayOfMonth);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        setDatePicker((DatePicker) this.waiter.waitForAndGetView(index, DatePicker.class), year, monthOfYear, dayOfMonth);
    }

    public void setDatePicker(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("setDatePicker(");
            stringBuilder.append(datePicker);
            stringBuilder.append(", ");
            stringBuilder.append(year);
            stringBuilder.append(", ");
            stringBuilder.append(monthOfYear);
            stringBuilder.append(", ");
            stringBuilder.append(dayOfMonth);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        this.setter.setDatePicker((DatePicker) this.waiter.waitForView(datePicker, Timeout.getSmallTimeout()), year, monthOfYear, dayOfMonth);
    }

    public void setTimePicker(int index, int hour, int minute) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("setTimePicker(");
            stringBuilder.append(index);
            stringBuilder.append(", ");
            stringBuilder.append(hour);
            stringBuilder.append(", ");
            stringBuilder.append(minute);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        setTimePicker((TimePicker) this.waiter.waitForAndGetView(index, TimePicker.class), hour, minute);
    }

    public void setTimePicker(TimePicker timePicker, int hour, int minute) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("setTimePicker(");
            stringBuilder.append(timePicker);
            stringBuilder.append(", ");
            stringBuilder.append(hour);
            stringBuilder.append(", ");
            stringBuilder.append(minute);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        this.setter.setTimePicker((TimePicker) this.waiter.waitForView(timePicker, Timeout.getSmallTimeout()), hour, minute);
    }

    public void setProgressBar(int index, int progress) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("setProgressBar(");
            stringBuilder.append(index);
            stringBuilder.append(", ");
            stringBuilder.append(progress);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        setProgressBar((ProgressBar) this.waiter.waitForAndGetView(index, ProgressBar.class), progress);
    }

    public void setProgressBar(ProgressBar progressBar, int progress) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("setProgressBar(");
            stringBuilder.append(progressBar);
            stringBuilder.append(", ");
            stringBuilder.append(progress);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        this.setter.setProgressBar((ProgressBar) this.waiter.waitForView(progressBar, Timeout.getSmallTimeout()), progress);
    }

    public void setNavigationDrawer(int status) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("setNavigationDrawer(");
            stringBuilder.append(status);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        this.setter.setNavigationDrawer(status);
    }

    public void setSlidingDrawer(int index, int status) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("setSlidingDrawer(");
            stringBuilder.append(index);
            stringBuilder.append(", ");
            stringBuilder.append(status);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        setSlidingDrawer((SlidingDrawer) this.waiter.waitForAndGetView(index, SlidingDrawer.class), status);
    }

    public void setSlidingDrawer(SlidingDrawer slidingDrawer, int status) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("setSlidingDrawer(");
            stringBuilder.append(slidingDrawer);
            stringBuilder.append(", ");
            stringBuilder.append(status);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        this.setter.setSlidingDrawer((SlidingDrawer) this.waiter.waitForView(slidingDrawer, Timeout.getSmallTimeout()), status);
    }

    public void enterText(int index, String text) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("enterText(");
            stringBuilder.append(index);
            stringBuilder.append(", \"");
            stringBuilder.append(text);
            stringBuilder.append("\")");
            Log.d(str, stringBuilder.toString());
        }
        this.textEnterer.setEditText((EditText) this.waiter.waitForAndGetView(index, EditText.class), text);
    }

    public void enterText(EditText editText, String text) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("enterText(");
            stringBuilder.append(editText);
            stringBuilder.append(", \"");
            stringBuilder.append(text);
            stringBuilder.append("\")");
            Log.d(str, stringBuilder.toString());
        }
        this.textEnterer.setEditText((EditText) this.waiter.waitForView(editText, Timeout.getSmallTimeout()), text);
    }

    public void enterTextInWebElement(By by, String text) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("enterTextInWebElement(");
            stringBuilder.append(by);
            stringBuilder.append(", \"");
            stringBuilder.append(text);
            stringBuilder.append("\")");
            Log.d(str, stringBuilder.toString());
        }
        if (this.waiter.waitForWebElement(by, 0, Timeout.getSmallTimeout(), false) == null) {
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("WebElement with ");
            stringBuilder2.append(this.webUtils.splitNameByUpperCase(by.getClass().getSimpleName()));
            stringBuilder2.append(": '");
            stringBuilder2.append(by.getValue());
            stringBuilder2.append("' is not found!");
            Assert.fail(stringBuilder2.toString());
        }
        this.webUtils.enterTextIntoWebElement(by, text);
    }

    public void typeText(int index, String text) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("typeText(");
            stringBuilder.append(index);
            stringBuilder.append(", \"");
            stringBuilder.append(text);
            stringBuilder.append("\")");
            Log.d(str, stringBuilder.toString());
        }
        this.textEnterer.typeText((EditText) this.waiter.waitForAndGetView(index, EditText.class), text);
    }

    public void typeText(EditText editText, String text) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("typeText(");
            stringBuilder.append(editText);
            stringBuilder.append(", \"");
            stringBuilder.append(text);
            stringBuilder.append("\")");
            Log.d(str, stringBuilder.toString());
        }
        this.textEnterer.typeText((EditText) this.waiter.waitForView(editText, Timeout.getSmallTimeout()), text);
    }

    public void typeTextInWebElement(By by, String text) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("typeTextInWebElement(");
            stringBuilder.append(by);
            stringBuilder.append(", \"");
            stringBuilder.append(text);
            stringBuilder.append("\")");
            Log.d(str, stringBuilder.toString());
        }
        typeTextInWebElement(by, text, 0);
    }

    public void typeTextInWebElement(By by, String text, int match) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("typeTextInWebElement(");
            stringBuilder.append(by);
            stringBuilder.append(", \"");
            stringBuilder.append(text);
            stringBuilder.append("\", ");
            stringBuilder.append(match);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        this.clicker.clickOnWebElement(by, match, true, false);
        this.dialogUtils.hideSoftKeyboard(null, true, true);
        this.instrumentation.sendStringSync(text);
    }

    public void typeTextInWebElement(WebElement webElement, String text) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("typeTextInWebElement(");
            stringBuilder.append(webElement);
            stringBuilder.append(", \"");
            stringBuilder.append(text);
            stringBuilder.append("\")");
            Log.d(str, stringBuilder.toString());
        }
        clickOnWebElement(webElement);
        this.dialogUtils.hideSoftKeyboard(null, true, true);
        this.instrumentation.sendStringSync(text);
    }

    public void clearEditText(int index) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("clearEditText(");
            stringBuilder.append(index);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        this.textEnterer.setEditText((EditText) this.waiter.waitForAndGetView(index, EditText.class), "");
    }

    public void clearEditText(EditText editText) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("clearEditText(");
            stringBuilder.append(editText);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        this.textEnterer.setEditText((EditText) this.waiter.waitForView(editText, Timeout.getSmallTimeout()), "");
    }

    public void clearTextInWebElement(By by) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("clearTextInWebElement(");
            stringBuilder.append(by);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        this.webUtils.enterTextIntoWebElement(by, "");
    }

    public void clickOnImage(int index) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("clickOnImage(");
            stringBuilder.append(index);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        this.clicker.clickOn(ImageView.class, index);
    }

    public EditText getEditText(int index) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("getEditText(");
            stringBuilder.append(index);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return (EditText) this.getter.getView(EditText.class, index);
    }

    public Button getButton(int index) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("getButton(");
            stringBuilder.append(index);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return (Button) this.getter.getView(Button.class, index);
    }

    public TextView getText(int index) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("getText(");
            stringBuilder.append(index);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return (TextView) this.getter.getView(TextView.class, index);
    }

    public ImageView getImage(int index) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("getImage(");
            stringBuilder.append(index);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return (ImageView) this.getter.getView(ImageView.class, index);
    }

    public ImageButton getImageButton(int index) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("getImageButton(");
            stringBuilder.append(index);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return (ImageButton) this.getter.getView(ImageButton.class, index);
    }

    public TextView getText(String text) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("getText(\"");
            stringBuilder.append(text);
            stringBuilder.append("\")");
            Log.d(str, stringBuilder.toString());
        }
        return this.getter.getView(TextView.class, text, false);
    }

    public TextView getText(String text, boolean onlyVisible) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("getText(\"");
            stringBuilder.append(text);
            stringBuilder.append("\", ");
            stringBuilder.append(onlyVisible);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.getter.getView(TextView.class, text, onlyVisible);
    }

    public Button getButton(String text) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("getButton(\"");
            stringBuilder.append(text);
            stringBuilder.append("\")");
            Log.d(str, stringBuilder.toString());
        }
        return (Button) this.getter.getView(Button.class, text, false);
    }

    public Button getButton(String text, boolean onlyVisible) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("getButton(\"");
            stringBuilder.append(text);
            stringBuilder.append("\", ");
            stringBuilder.append(onlyVisible);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return (Button) this.getter.getView(Button.class, text, onlyVisible);
    }

    public EditText getEditText(String text) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("getEditText(\"");
            stringBuilder.append(text);
            stringBuilder.append("\")");
            Log.d(str, stringBuilder.toString());
        }
        return (EditText) this.getter.getView(EditText.class, text, false);
    }

    public EditText getEditText(String text, boolean onlyVisible) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("getEditText(\"");
            stringBuilder.append(text);
            stringBuilder.append("\", ");
            stringBuilder.append(onlyVisible);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return (EditText) this.getter.getView(EditText.class, text, onlyVisible);
    }

    public View getView(int id) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("getView(");
            stringBuilder.append(id);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return getView(id, 0);
    }

    public View getView(int id, int index) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("getView(");
            stringBuilder.append(id);
            stringBuilder.append(", ");
            stringBuilder.append(index);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        View viewToReturn = this.getter.getView(id, index);
        if (viewToReturn == null) {
            String resourceName = "";
            try {
                resourceName = this.instrumentation.getTargetContext().getResources().getResourceEntryName(id);
            } catch (Exception e) {
                String str2 = this.config.commandLoggingTag;
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("unable to get resource entry name for (");
                stringBuilder2.append(id);
                stringBuilder2.append(")");
                Log.d(str2, stringBuilder2.toString());
            }
            int match = index + 1;
            StringBuilder stringBuilder3;
            if (match > 1) {
                stringBuilder3 = new StringBuilder();
                stringBuilder3.append(match);
                stringBuilder3.append(" Views with id: '");
                stringBuilder3.append(id);
                stringBuilder3.append("', resource name: '");
                stringBuilder3.append(resourceName);
                stringBuilder3.append("' are not found!");
                Assert.fail(stringBuilder3.toString());
            } else {
                stringBuilder3 = new StringBuilder();
                stringBuilder3.append("View with id: '");
                stringBuilder3.append(id);
                stringBuilder3.append("', resource name: '");
                stringBuilder3.append(resourceName);
                stringBuilder3.append("' is not found!");
                Assert.fail(stringBuilder3.toString());
            }
        }
        return viewToReturn;
    }

    public View getView(Object tag) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("getView(");
            stringBuilder.append(tag);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return getView(tag, 0);
    }

    public View getView(Object tag, int index) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("getView(");
            stringBuilder.append(tag);
            stringBuilder.append(", ");
            stringBuilder.append(index);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        View viewToReturn = this.getter.getView(tag, index);
        if (viewToReturn == null) {
            int match = index + 1;
            StringBuilder stringBuilder2;
            if (match > 1) {
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append(match);
                stringBuilder2.append(" Views with id: '");
                stringBuilder2.append(tag);
                stringBuilder2.append("' are not found!");
                Assert.fail(stringBuilder2.toString());
            } else {
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append("View with id: '");
                stringBuilder2.append(tag);
                stringBuilder2.append("' is not found!");
                Assert.fail(stringBuilder2.toString());
            }
        }
        return viewToReturn;
    }

    public View getView(String id) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("getView(\"");
            stringBuilder.append(id);
            stringBuilder.append("\")");
            Log.d(str, stringBuilder.toString());
        }
        return getView(id, 0);
    }

    public View getView(String id, int index) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("getView(\"");
            stringBuilder.append(id);
            stringBuilder.append("\", ");
            stringBuilder.append(index);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        View viewToReturn = this.getter.getView(id, index);
        if (viewToReturn == null) {
            int match = index + 1;
            StringBuilder stringBuilder2;
            if (match > 1) {
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append(match);
                stringBuilder2.append(" Views with id: '");
                stringBuilder2.append(id);
                stringBuilder2.append("' are not found!");
                Assert.fail(stringBuilder2.toString());
            } else {
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append("View with id: '");
                stringBuilder2.append(id);
                stringBuilder2.append("' is not found!");
                Assert.fail(stringBuilder2.toString());
            }
        }
        return viewToReturn;
    }

    public <T extends View> T getView(Class<T> viewClass, int index) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("getView(");
            stringBuilder.append(viewClass);
            stringBuilder.append(", ");
            stringBuilder.append(index);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.waiter.waitForAndGetView(index, viewClass);
    }

    public WebElement getWebElement(By by, int index) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("getWebElement(");
            stringBuilder.append(by);
            stringBuilder.append(", ");
            stringBuilder.append(index);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        int match = index + 1;
        WebElement webElement = this.waiter.waitForWebElement(by, match, Timeout.getSmallTimeout(), true);
        if (webElement == null) {
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
        return webElement;
    }

    public String getWebUrl() {
        if (this.config.commandLogging) {
            Log.d(this.config.commandLoggingTag, "getWebUrl()");
        }
        final WebView webView = (WebView) this.waiter.waitForAndGetView(0, WebView.class);
        if (webView == null) {
            Assert.fail("WebView is not found!");
        }
        this.instrumentation.runOnMainSync(new Runnable() {
            public void run() {
                Solo.this.webUrl = webView.getUrl();
            }
        });
        return this.webUrl;
    }

    public ArrayList<View> getCurrentViews() {
        if (this.config.commandLogging) {
            Log.d(this.config.commandLoggingTag, "getCurrentViews()");
        }
        return this.viewFetcher.getViews(null, true);
    }

    public <T extends View> ArrayList<T> getCurrentViews(Class<T> classToFilterBy) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("getCurrentViews(");
            stringBuilder.append(classToFilterBy);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.viewFetcher.getCurrentViews(classToFilterBy, true);
    }

    public <T extends View> ArrayList<T> getCurrentViews(Class<T> classToFilterBy, boolean includeSubclasses) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("getCurrentViews(");
            stringBuilder.append(classToFilterBy);
            stringBuilder.append(", ");
            stringBuilder.append(includeSubclasses);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.viewFetcher.getCurrentViews(classToFilterBy, includeSubclasses);
    }

    public <T extends View> ArrayList<T> getCurrentViews(Class<T> classToFilterBy, View parent) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("getCurrentViews(");
            stringBuilder.append(classToFilterBy);
            stringBuilder.append(", ");
            stringBuilder.append(parent);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.viewFetcher.getCurrentViews(classToFilterBy, true, parent);
    }

    public <T extends View> ArrayList<T> getCurrentViews(Class<T> classToFilterBy, boolean includeSubclasses, View parent) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("getCurrentViews(");
            stringBuilder.append(classToFilterBy);
            stringBuilder.append(", ");
            stringBuilder.append(includeSubclasses);
            stringBuilder.append(", ");
            stringBuilder.append(parent);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.viewFetcher.getCurrentViews(classToFilterBy, includeSubclasses, parent);
    }

    public ArrayList<WebElement> getWebElements() {
        if (this.config.commandLogging) {
            Log.d(this.config.commandLoggingTag, "getWebElements()");
        }
        return this.webUtils.getWebElements(false);
    }

    public ArrayList<WebElement> getWebElements(By by) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("getWebElements(");
            stringBuilder.append(by);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.webUtils.getWebElements(by, false);
    }

    public ArrayList<WebElement> getCurrentWebElements() {
        if (this.config.commandLogging) {
            Log.d(this.config.commandLoggingTag, "getCurrentWebElements()");
        }
        return this.webUtils.getWebElements(true);
    }

    public ArrayList<WebElement> getCurrentWebElements(By by) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("getCurrentWebElements(");
            stringBuilder.append(by);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.webUtils.getWebElements(by, true);
    }

    public boolean isRadioButtonChecked(int index) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("isRadioButtonChecked(");
            stringBuilder.append(index);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.checker.isButtonChecked(RadioButton.class, index);
    }

    public boolean isRadioButtonChecked(String text) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("isRadioButtonChecked(\"");
            stringBuilder.append(text);
            stringBuilder.append("\")");
            Log.d(str, stringBuilder.toString());
        }
        return this.checker.isButtonChecked(RadioButton.class, text);
    }

    public boolean isCheckBoxChecked(int index) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("isCheckBoxChecked(");
            stringBuilder.append(index);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.checker.isButtonChecked(CheckBox.class, index);
    }

    public boolean isToggleButtonChecked(String text) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("isToggleButtonChecked(\"");
            stringBuilder.append(text);
            stringBuilder.append("\")");
            Log.d(str, stringBuilder.toString());
        }
        return this.checker.isButtonChecked(ToggleButton.class, text);
    }

    public boolean isToggleButtonChecked(int index) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("isToggleButtonChecked(");
            stringBuilder.append(index);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.checker.isButtonChecked(ToggleButton.class, index);
    }

    public boolean isCheckBoxChecked(String text) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("isCheckBoxChecked(\"");
            stringBuilder.append(text);
            stringBuilder.append("\")");
            Log.d(str, stringBuilder.toString());
        }
        return this.checker.isButtonChecked(CheckBox.class, text);
    }

    public boolean isTextChecked(String text) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("isTextChecked(\"");
            stringBuilder.append(text);
            stringBuilder.append("\")");
            Log.d(str, stringBuilder.toString());
        }
        this.waiter.waitForViews(false, CheckedTextView.class, CompoundButton.class);
        if (this.viewFetcher.getCurrentViews(CheckedTextView.class, true).size() <= 0 || !this.checker.isCheckedTextChecked(text)) {
            return this.viewFetcher.getCurrentViews(CompoundButton.class, true).size() > 0 && this.checker.isButtonChecked(CompoundButton.class, text);
        } else {
            return true;
        }
    }

    public boolean isSpinnerTextSelected(String text) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("isSpinnerTextSelected(\"");
            stringBuilder.append(text);
            stringBuilder.append("\")");
            Log.d(str, stringBuilder.toString());
        }
        return this.checker.isSpinnerTextSelected(text);
    }

    public boolean isSpinnerTextSelected(int index, String text) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("isSpinnerTextSelected(");
            stringBuilder.append(index);
            stringBuilder.append(",\"");
            stringBuilder.append(text);
            stringBuilder.append("\")");
            Log.d(str, stringBuilder.toString());
        }
        return this.checker.isSpinnerTextSelected(index, text);
    }

    public void hideSoftKeyboard() {
        if (this.config.commandLogging) {
            Log.d(this.config.commandLoggingTag, "hideSoftKeyboard()");
        }
        this.dialogUtils.hideSoftKeyboard(null, true, false);
    }

    public void unlockScreen() {
        if (this.config.commandLogging) {
            Log.d(this.config.commandLoggingTag, "unlockScreen()");
        }
        final Activity activity = this.activityUtils.getCurrentActivity(false);
        this.instrumentation.runOnMainSync(new Runnable() {
            public void run() {
                Activity activity = activity;
                if (activity != null) {
                    activity.getWindow().addFlags(4194304);
                }
            }
        });
    }

    public void sendKey(int key) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("sendKey(");
            stringBuilder.append(key);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        this.sender.sendKeyCode(key);
    }

    public void goBackToActivity(String name) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("goBackToActivity(\"");
            stringBuilder.append(name);
            stringBuilder.append("\")");
            Log.d(str, stringBuilder.toString());
        }
        this.activityUtils.goBackToActivity(name);
    }

    public boolean waitForActivity(String name) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("waitForActivity(\"");
            stringBuilder.append(name);
            stringBuilder.append("\")");
            Log.d(str, stringBuilder.toString());
        }
        return this.waiter.waitForActivity(name, Timeout.getLargeTimeout());
    }

    public boolean waitForActivity(String name, int timeout) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("waitForActivity(\"");
            stringBuilder.append(name);
            stringBuilder.append("\", ");
            stringBuilder.append(timeout);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.waiter.waitForActivity(name, timeout);
    }

    public boolean waitForActivity(Class<? extends Activity> activityClass) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("waitForActivity(");
            stringBuilder.append(activityClass);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.waiter.waitForActivity((Class) activityClass, Timeout.getLargeTimeout());
    }

    public boolean waitForActivity(Class<? extends Activity> activityClass, int timeout) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("waitForActivity(");
            stringBuilder.append(activityClass);
            stringBuilder.append(", ");
            stringBuilder.append(timeout);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.waiter.waitForActivity((Class) activityClass, timeout);
    }

    public boolean waitForEmptyActivityStack(int timeout) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("waitForEmptyActivityStack(");
            stringBuilder.append(timeout);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.waiter.waitForCondition(new C09884(), timeout);
    }

    public boolean waitForFragmentByTag(String tag) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("waitForFragmentByTag(\"");
            stringBuilder.append(tag);
            stringBuilder.append("\")");
            Log.d(str, stringBuilder.toString());
        }
        return this.waiter.waitForFragment(tag, 0, Timeout.getLargeTimeout());
    }

    public boolean waitForFragmentByTag(String tag, int timeout) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("waitForFragmentByTag(\"");
            stringBuilder.append(tag);
            stringBuilder.append("\", ");
            stringBuilder.append(timeout);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.waiter.waitForFragment(tag, 0, timeout);
    }

    public boolean waitForFragmentById(int id) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("waitForFragmentById(");
            stringBuilder.append(id);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.waiter.waitForFragment(null, id, Timeout.getLargeTimeout());
    }

    public boolean waitForFragmentById(int id, int timeout) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("waitForFragmentById(");
            stringBuilder.append(id);
            stringBuilder.append(", ");
            stringBuilder.append(timeout);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.waiter.waitForFragment(null, id, timeout);
    }

    public boolean waitForLogMessage(String logMessage) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("waitForLogMessage(\"");
            stringBuilder.append(logMessage);
            stringBuilder.append("\")");
            Log.d(str, stringBuilder.toString());
        }
        return this.waiter.waitForLogMessage(logMessage, Timeout.getLargeTimeout());
    }

    public boolean waitForLogMessage(String logMessage, int timeout) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("waitForLogMessage(\"");
            stringBuilder.append(logMessage);
            stringBuilder.append("\", ");
            stringBuilder.append(timeout);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.waiter.waitForLogMessage(logMessage, timeout);
    }

    public void clearLog() {
        if (this.config.commandLogging) {
            Log.d(this.config.commandLoggingTag, "clearLog()");
        }
        this.waiter.clearLog();
    }

    public String getString(int id) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("getString(");
            stringBuilder.append(id);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        return this.getter.getString(id);
    }

    public String getString(String id) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("getString(\"");
            stringBuilder.append(id);
            stringBuilder.append("\")");
            Log.d(str, stringBuilder.toString());
        }
        return this.getter.getString(id);
    }

    public void sleep(int time) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("sleep(");
            stringBuilder.append(time);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        this.sleeper.sleep(time);
    }

    public void finalize() throws Throwable {
        if (this.config.commandLogging) {
            Log.d(this.config.commandLoggingTag, "finalize()");
        }
        this.activityUtils.finalize();
    }

    public void finishOpenedActivities() {
        if (this.config.commandLogging) {
            Log.d(this.config.commandLoggingTag, "finishOpenedActivities()");
        }
        this.activityUtils.finishOpenedActivities();
    }

    public void takeScreenshot() {
        if (this.config.commandLogging) {
            Log.d(this.config.commandLoggingTag, "takeScreenshot()");
        }
        takeScreenshot(null);
    }

    public void takeScreenshot(String name) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("takeScreenshot(\"");
            stringBuilder.append(name);
            stringBuilder.append("\")");
            Log.d(str, stringBuilder.toString());
        }
        takeScreenshot(name, 100);
    }

    public void takeScreenshot(String name, int quality) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("takeScreenshot(\"");
            stringBuilder.append(name);
            stringBuilder.append("\", ");
            stringBuilder.append(quality);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        this.screenshotTaker.takeScreenshot(name, quality);
    }

    public void startScreenshotSequence(String name) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("startScreenshotSequence(\"");
            stringBuilder.append(name);
            stringBuilder.append("\")");
            Log.d(str, stringBuilder.toString());
        }
        startScreenshotSequence(name, 80, 400, 100);
    }

    public void startScreenshotSequence(String name, int quality, int frameDelay, int maxFrames) {
        if (this.config.commandLogging) {
            String str = this.config.commandLoggingTag;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("startScreenshotSequence(\"");
            stringBuilder.append(name);
            stringBuilder.append("\", ");
            stringBuilder.append(quality);
            stringBuilder.append(", ");
            stringBuilder.append(frameDelay);
            stringBuilder.append(", ");
            stringBuilder.append(maxFrames);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
        }
        this.screenshotTaker.startScreenshotSequence(name, quality, frameDelay, maxFrames);
    }

    public void stopScreenshotSequence() {
        if (this.config.commandLogging) {
            Log.d(this.config.commandLoggingTag, "stopScreenshotSequence()");
        }
        this.screenshotTaker.stopScreenshotSequence();
    }

    private void initialize() {
        if (this.config.commandLogging) {
            Log.d(this.config.commandLoggingTag, "initialize()");
        }
        Timeout.setLargeTimeout(initializeTimeout("solo_large_timeout", this.config.timeout_large));
        Timeout.setSmallTimeout(initializeTimeout("solo_small_timeout", this.config.timeout_small));
    }

    private static int initializeTimeout(String property, int defaultValue) {
        try {
            return Integer.parseInt((String) Class.forName("android.os.SystemProperties").getDeclaredMethod("get", new Class[]{String.class}).invoke(null, new Object[]{property}));
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
