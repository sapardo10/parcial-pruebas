package com.robotium.solo;

import android.app.Instrumentation;
import android.graphics.Bitmap;
import android.os.Message;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions.Callback;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebStorage.QuotaUpdater;
import android.webkit.WebView;
import java.util.List;

class RobotiumWebClient extends WebChromeClient {
    private Instrumentation inst;
    private WebChromeClient originalWebChromeClient = null;
    private WebChromeClient robotiumWebClient;
    WebElementCreator webElementCreator;

    public RobotiumWebClient(Instrumentation inst, WebElementCreator webElementCreator) {
        this.inst = inst;
        this.webElementCreator = webElementCreator;
        this.robotiumWebClient = this;
    }

    public void enableJavascriptAndSetRobotiumWebClient(List<WebView> webViews, WebChromeClient originalWebChromeClient) {
        this.originalWebChromeClient = originalWebChromeClient;
        for (final WebView webView : webViews) {
            if (webView != null) {
                this.inst.runOnMainSync(new Runnable() {
                    public void run() {
                        webView.getSettings().setJavaScriptEnabled(true);
                        webView.setWebChromeClient(RobotiumWebClient.this.robotiumWebClient);
                    }
                });
            }
        }
    }

    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult r) {
        if (message == null || !(message.contains(";,") || message.contains("robotium-finished"))) {
            WebChromeClient webChromeClient = this.originalWebChromeClient;
            if (webChromeClient != null) {
                return webChromeClient.onJsPrompt(view, url, message, defaultValue, r);
            }
            return true;
        }
        if (message.equals("robotium-finished")) {
            this.webElementCreator.setFinished(true);
        } else {
            this.webElementCreator.createWebElementAndAddInList(message, view);
        }
        r.confirm();
        return true;
    }

    public Bitmap getDefaultVideoPoster() {
        WebChromeClient webChromeClient = this.originalWebChromeClient;
        if (webChromeClient != null) {
            return webChromeClient.getDefaultVideoPoster();
        }
        return null;
    }

    public View getVideoLoadingProgressView() {
        WebChromeClient webChromeClient = this.originalWebChromeClient;
        if (webChromeClient != null) {
            return webChromeClient.getVideoLoadingProgressView();
        }
        return null;
    }

    public void getVisitedHistory(ValueCallback<String[]> callback) {
        WebChromeClient webChromeClient = this.originalWebChromeClient;
        if (webChromeClient != null) {
            webChromeClient.getVisitedHistory(callback);
        }
    }

    public void onCloseWindow(WebView window) {
        WebChromeClient webChromeClient = this.originalWebChromeClient;
        if (webChromeClient != null) {
            webChromeClient.onCloseWindow(window);
        }
    }

    public void onConsoleMessage(String message, int lineNumber, String sourceID) {
        WebChromeClient webChromeClient = this.originalWebChromeClient;
        if (webChromeClient != null) {
            webChromeClient.onConsoleMessage(message, lineNumber, sourceID);
        }
    }

    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        WebChromeClient webChromeClient = this.originalWebChromeClient;
        if (webChromeClient != null) {
            return webChromeClient.onConsoleMessage(consoleMessage);
        }
        return true;
    }

    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
        WebChromeClient webChromeClient = this.originalWebChromeClient;
        if (webChromeClient != null) {
            return webChromeClient.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
        }
        return true;
    }

    public void onExceededDatabaseQuota(String url, String databaseIdentifier, long quota, long estimatedDatabaseSize, long totalQuota, QuotaUpdater quotaUpdater) {
        WebChromeClient webChromeClient = this.originalWebChromeClient;
        if (webChromeClient != null) {
            webChromeClient.onExceededDatabaseQuota(url, databaseIdentifier, quota, estimatedDatabaseSize, totalQuota, quotaUpdater);
        }
    }

    public void onGeolocationPermissionsHidePrompt() {
        WebChromeClient webChromeClient = this.originalWebChromeClient;
        if (webChromeClient != null) {
            webChromeClient.onGeolocationPermissionsHidePrompt();
        }
    }

    public void onGeolocationPermissionsShowPrompt(String origin, Callback callback) {
        WebChromeClient webChromeClient = this.originalWebChromeClient;
        if (webChromeClient != null) {
            webChromeClient.onGeolocationPermissionsShowPrompt(origin, callback);
        }
    }

    public void onHideCustomView() {
        WebChromeClient webChromeClient = this.originalWebChromeClient;
        if (webChromeClient != null) {
            webChromeClient.onHideCustomView();
        }
    }

    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        WebChromeClient webChromeClient = this.originalWebChromeClient;
        if (webChromeClient != null) {
            return webChromeClient.onJsAlert(view, url, message, result);
        }
        return true;
    }

    public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
        if (this.originalWebChromeClient.onJsBeforeUnload(view, url, message, result)) {
            return this.originalWebChromeClient.onJsBeforeUnload(view, url, message, result);
        }
        return true;
    }

    public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
        WebChromeClient webChromeClient = this.originalWebChromeClient;
        if (webChromeClient != null) {
            return webChromeClient.onJsConfirm(view, url, message, result);
        }
        return true;
    }

    public boolean onJsTimeout() {
        WebChromeClient webChromeClient = this.originalWebChromeClient;
        if (webChromeClient != null) {
            return webChromeClient.onJsTimeout();
        }
        return true;
    }

    public void onProgressChanged(WebView view, int newProgress) {
        WebChromeClient webChromeClient = this.originalWebChromeClient;
        if (webChromeClient != null) {
            webChromeClient.onProgressChanged(view, newProgress);
        }
    }

    public void onReachedMaxAppCacheSize(long requiredStorage, long quota, QuotaUpdater quotaUpdater) {
        WebChromeClient webChromeClient = this.originalWebChromeClient;
        if (webChromeClient != null) {
            webChromeClient.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater);
        }
    }

    public void onReceivedIcon(WebView view, Bitmap icon) {
        WebChromeClient webChromeClient = this.originalWebChromeClient;
        if (webChromeClient != null) {
            webChromeClient.onReceivedIcon(view, icon);
        }
    }

    public void onReceivedTitle(WebView view, String title) {
        WebChromeClient webChromeClient = this.originalWebChromeClient;
        if (webChromeClient != null) {
            webChromeClient.onReceivedTitle(view, title);
        }
    }

    public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
        WebChromeClient webChromeClient = this.originalWebChromeClient;
        if (webChromeClient != null) {
            webChromeClient.onReceivedTouchIconUrl(view, url, precomposed);
        }
    }

    public void onRequestFocus(WebView view) {
        WebChromeClient webChromeClient = this.originalWebChromeClient;
        if (webChromeClient != null) {
            webChromeClient.onRequestFocus(view);
        }
    }

    public void onShowCustomView(View view, CustomViewCallback callback) {
        WebChromeClient webChromeClient = this.originalWebChromeClient;
        if (webChromeClient != null) {
            webChromeClient.onShowCustomView(view, callback);
        }
    }
}
