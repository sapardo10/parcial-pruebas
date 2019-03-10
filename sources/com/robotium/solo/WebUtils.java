package com.robotium.solo;

import android.app.Instrumentation;
import android.os.Build.VERSION;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.TextView;
import com.robotium.solo.Solo.Config;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

class WebUtils {
    private Config config;
    private Instrumentation inst;
    WebChromeClient originalWebChromeClient = null;
    RobotiumWebClient robotiumWebCLient;
    private ViewFetcher viewFetcher;
    WebElementCreator webElementCreator;

    private java.lang.String getJavaScriptAsString() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:12:0x003a in {5, 8, 11} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r5 = this;
        r0 = r5.getClass();
        r1 = "RobotiumWeb.js";
        r0 = r0.getResourceAsStream(r1);
        r1 = new java.lang.StringBuffer;
        r1.<init>();
        r2 = new java.io.BufferedReader;	 Catch:{ IOException -> 0x0033 }
        r3 = new java.io.InputStreamReader;	 Catch:{ IOException -> 0x0033 }
        r3.<init>(r0);	 Catch:{ IOException -> 0x0033 }
        r2.<init>(r3);	 Catch:{ IOException -> 0x0033 }
        r3 = 0;	 Catch:{ IOException -> 0x0033 }
    L_0x001a:
        r4 = r2.readLine();	 Catch:{ IOException -> 0x0033 }
        r3 = r4;	 Catch:{ IOException -> 0x0033 }
        if (r4 == 0) goto L_0x002a;	 Catch:{ IOException -> 0x0033 }
    L_0x0021:
        r1.append(r3);	 Catch:{ IOException -> 0x0033 }
        r4 = "\n";	 Catch:{ IOException -> 0x0033 }
        r1.append(r4);	 Catch:{ IOException -> 0x0033 }
        goto L_0x001a;	 Catch:{ IOException -> 0x0033 }
    L_0x002a:
        r2.close();	 Catch:{ IOException -> 0x0033 }
        r2 = r1.toString();
        return r2;
    L_0x0033:
        r2 = move-exception;
        r3 = new java.lang.RuntimeException;
        r3.<init>(r2);
        throw r3;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.robotium.solo.WebUtils.getJavaScriptAsString():java.lang.String");
    }

    public WebUtils(Config config, Instrumentation instrumentation, ViewFetcher viewFetcher, Sleeper sleeper) {
        this.config = config;
        this.inst = instrumentation;
        this.viewFetcher = viewFetcher;
        this.webElementCreator = new WebElementCreator(sleeper);
        this.robotiumWebCLient = new RobotiumWebClient(instrumentation, this.webElementCreator);
    }

    public ArrayList<TextView> getTextViewsFromWebView() {
        return createAndReturnTextViewsFromWebElements(executeJavaScriptFunction("allTexts();"));
    }

    private ArrayList<TextView> createAndReturnTextViewsFromWebElements(boolean javaScriptWasExecuted) {
        ArrayList<TextView> webElementsAsTextViews = new ArrayList();
        if (javaScriptWasExecuted) {
            Iterator i$ = this.webElementCreator.getWebElementsFromWebViews().iterator();
            while (i$.hasNext()) {
                WebElement webElement = (WebElement) i$.next();
                if (isWebElementSufficientlyShown(webElement)) {
                    webElementsAsTextViews.add(new RobotiumTextView(this.inst.getContext(), webElement.getText(), webElement.getLocationX(), webElement.getLocationY()));
                }
            }
        }
        return webElementsAsTextViews;
    }

    public ArrayList<WebElement> getWebElements(boolean onlySufficientlyVisible) {
        return getWebElements(executeJavaScriptFunction("allWebElements();"), onlySufficientlyVisible);
    }

    public ArrayList<WebElement> getWebElements(By by, boolean onlySufficientlyVisbile) {
        boolean javaScriptWasExecuted = executeJavaScript(by, false);
        if (!this.config.useJavaScriptToClickWebElements) {
            return getWebElements(javaScriptWasExecuted, onlySufficientlyVisbile);
        }
        if (javaScriptWasExecuted) {
            return this.webElementCreator.getWebElementsFromWebViews();
        }
        return new ArrayList();
    }

    private ArrayList<WebElement> getWebElements(boolean javaScriptWasExecuted, boolean onlySufficientlyVisbile) {
        ArrayList<WebElement> webElements = new ArrayList();
        if (javaScriptWasExecuted) {
            Iterator i$ = this.webElementCreator.getWebElementsFromWebViews().iterator();
            while (i$.hasNext()) {
                WebElement webElement = (WebElement) i$.next();
                if (!onlySufficientlyVisbile) {
                    webElements.add(webElement);
                } else if (isWebElementSufficientlyShown(webElement)) {
                    webElements.add(webElement);
                }
            }
        }
        return webElements;
    }

    private String prepareForStartOfJavascriptExecution(List<WebView> webViews) {
        this.webElementCreator.prepareForStart();
        WebChromeClient currentWebChromeClient = getCurrentWebChromeClient();
        if (currentWebChromeClient != null && !currentWebChromeClient.getClass().isAssignableFrom(RobotiumWebClient.class)) {
            this.originalWebChromeClient = currentWebChromeClient;
        }
        this.robotiumWebCLient.enableJavascriptAndSetRobotiumWebClient(webViews, this.originalWebChromeClient);
        return getJavaScriptAsString();
    }

    private WebChromeClient getCurrentWebChromeClient() {
        WebChromeClient currentWebChromeClient = null;
        Object currentWebView = this.viewFetcher;
        currentWebView = currentWebView.getFreshestView(currentWebView.getCurrentViews(WebView.class, true));
        if (VERSION.SDK_INT >= 16) {
            try {
                currentWebView = new Reflect(currentWebView).field("mProvider").out(Object.class);
            } catch (IllegalArgumentException e) {
            }
        }
        try {
            if (VERSION.SDK_INT >= 19) {
                currentWebChromeClient = (WebChromeClient) new Reflect(new Reflect(currentWebView).field("mContentsClientAdapter").out(Object.class)).field("mWebChromeClient").out(WebChromeClient.class);
            } else {
                currentWebChromeClient = (WebChromeClient) new Reflect(new Reflect(currentWebView).field("mCallbackProxy").out(Object.class)).field("mWebChromeClient").out(WebChromeClient.class);
            }
        } catch (Exception e2) {
        }
        return currentWebChromeClient;
    }

    public void enterTextIntoWebElement(By by, String text) {
        StringBuilder stringBuilder;
        if (by instanceof Id) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("enterTextById(\"");
            stringBuilder.append(by.getValue());
            stringBuilder.append("\", \"");
            stringBuilder.append(text);
            stringBuilder.append("\");");
            executeJavaScriptFunction(stringBuilder.toString());
        } else if (by instanceof Xpath) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("enterTextByXpath(\"");
            stringBuilder.append(by.getValue());
            stringBuilder.append("\", \"");
            stringBuilder.append(text);
            stringBuilder.append("\");");
            executeJavaScriptFunction(stringBuilder.toString());
        } else if (by instanceof CssSelector) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("enterTextByCssSelector(\"");
            stringBuilder.append(by.getValue());
            stringBuilder.append("\", \"");
            stringBuilder.append(text);
            stringBuilder.append("\");");
            executeJavaScriptFunction(stringBuilder.toString());
        } else if (by instanceof Name) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("enterTextByName(\"");
            stringBuilder.append(by.getValue());
            stringBuilder.append("\", \"");
            stringBuilder.append(text);
            stringBuilder.append("\");");
            executeJavaScriptFunction(stringBuilder.toString());
        } else if (by instanceof ClassName) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("enterTextByClassName(\"");
            stringBuilder.append(by.getValue());
            stringBuilder.append("\", \"");
            stringBuilder.append(text);
            stringBuilder.append("\");");
            executeJavaScriptFunction(stringBuilder.toString());
        } else if (by instanceof Text) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("enterTextByTextContent(\"");
            stringBuilder.append(by.getValue());
            stringBuilder.append("\", \"");
            stringBuilder.append(text);
            stringBuilder.append("\");");
            executeJavaScriptFunction(stringBuilder.toString());
        } else if (by instanceof TagName) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("enterTextByTagName(\"");
            stringBuilder.append(by.getValue());
            stringBuilder.append("\", \"");
            stringBuilder.append(text);
            stringBuilder.append("\");");
            executeJavaScriptFunction(stringBuilder.toString());
        }
    }

    public boolean executeJavaScript(By by, boolean shouldClick) {
        StringBuilder stringBuilder;
        if (by instanceof Id) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("id(\"");
            stringBuilder.append(by.getValue());
            stringBuilder.append("\", \"");
            stringBuilder.append(String.valueOf(shouldClick));
            stringBuilder.append("\");");
            return executeJavaScriptFunction(stringBuilder.toString());
        } else if (by instanceof Xpath) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("xpath(\"");
            stringBuilder.append(by.getValue());
            stringBuilder.append("\", \"");
            stringBuilder.append(String.valueOf(shouldClick));
            stringBuilder.append("\");");
            return executeJavaScriptFunction(stringBuilder.toString());
        } else if (by instanceof CssSelector) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("cssSelector(\"");
            stringBuilder.append(by.getValue());
            stringBuilder.append("\", \"");
            stringBuilder.append(String.valueOf(shouldClick));
            stringBuilder.append("\");");
            return executeJavaScriptFunction(stringBuilder.toString());
        } else if (by instanceof Name) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("name(\"");
            stringBuilder.append(by.getValue());
            stringBuilder.append("\", \"");
            stringBuilder.append(String.valueOf(shouldClick));
            stringBuilder.append("\");");
            return executeJavaScriptFunction(stringBuilder.toString());
        } else if (by instanceof ClassName) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("className(\"");
            stringBuilder.append(by.getValue());
            stringBuilder.append("\", \"");
            stringBuilder.append(String.valueOf(shouldClick));
            stringBuilder.append("\");");
            return executeJavaScriptFunction(stringBuilder.toString());
        } else if (by instanceof Text) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("textContent(\"");
            stringBuilder.append(by.getValue());
            stringBuilder.append("\", \"");
            stringBuilder.append(String.valueOf(shouldClick));
            stringBuilder.append("\");");
            return executeJavaScriptFunction(stringBuilder.toString());
        } else if (!(by instanceof TagName)) {
            return false;
        } else {
            stringBuilder = new StringBuilder();
            stringBuilder.append("tagName(\"");
            stringBuilder.append(by.getValue());
            stringBuilder.append("\", \"");
            stringBuilder.append(String.valueOf(shouldClick));
            stringBuilder.append("\");");
            return executeJavaScriptFunction(stringBuilder.toString());
        }
    }

    private boolean executeJavaScriptFunction(final String function) {
        List<WebView> webViews = this.viewFetcher.getCurrentViews(WebView.class, true);
        final WebView webView = (WebView) this.viewFetcher.getFreshestView((ArrayList) webViews);
        if (webView == null) {
            return false;
        }
        final String javaScript = setWebFrame(prepareForStartOfJavascriptExecution(webViews));
        this.inst.runOnMainSync(new Runnable() {
            public void run() {
                WebView webView = webView;
                if (webView != null) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("javascript:");
                    stringBuilder.append(javaScript);
                    stringBuilder.append(function);
                    webView.loadUrl(stringBuilder.toString());
                }
            }
        });
        return true;
    }

    private String setWebFrame(String javascript) {
        String frame = this.config.webFrame;
        if (!frame.isEmpty()) {
            if (!frame.equals("document")) {
                String quote = Pattern.quote("document, ");
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("document.getElementById(\"");
                stringBuilder.append(frame);
                stringBuilder.append("\").contentDocument, ");
                javascript = javascript.replaceAll(quote, stringBuilder.toString());
                quote = Pattern.quote("document.body, ");
                stringBuilder = new StringBuilder();
                stringBuilder.append("document.getElementById(\"");
                stringBuilder.append(frame);
                stringBuilder.append("\").contentDocument, ");
                return javascript.replaceAll(quote, stringBuilder.toString());
            }
        }
        return javascript;
    }

    public final boolean isWebElementSufficientlyShown(WebElement webElement) {
        ViewFetcher viewFetcher = this.viewFetcher;
        WebView webView = (WebView) viewFetcher.getFreshestView(viewFetcher.getCurrentViews(WebView.class, true));
        int[] xyWebView = new int[2];
        if (webView != null && webElement != null) {
            webView.getLocationOnScreen(xyWebView);
            if (xyWebView[1] + webView.getHeight() > webElement.getLocationY()) {
                return true;
            }
        }
        return false;
    }

    public String splitNameByUpperCase(String name) {
        String[] texts = name.split("(?=\\p{Upper})");
        StringBuilder stringToReturn = new StringBuilder();
        for (String string : texts) {
            if (stringToReturn.length() > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(StringUtils.SPACE);
                stringBuilder.append(string.toLowerCase());
                stringToReturn.append(stringBuilder.toString());
            } else {
                stringToReturn.append(string.toLowerCase());
            }
        }
        return stringToReturn.toString();
    }
}
