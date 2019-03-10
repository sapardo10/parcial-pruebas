package de.danoeh.antennapod.activity;

import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.debug.R;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import org.apache.commons.io.IOUtils;

public class AboutActivity extends AppCompatActivity {
    private static final String TAG = AboutActivity.class.getSimpleName();
    private Disposable disposable;
    private WebView webView;
    private LinearLayout webViewContainer;

    /* renamed from: de.danoeh.antennapod.activity.AboutActivity$1 */
    class C07101 extends WebViewClient {
        C07101() {
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("http")) {
                return false;
            }
            AboutActivity.this.loadAsset(url.replace("file:///android_asset/", ""));
            return true;
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        setTheme(UserPreferences.getTheme());
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setContentView(R.layout.about);
        this.webViewContainer = (LinearLayout) findViewById(R.id.webViewContainer);
        this.webView = (WebView) findViewById(R.id.webViewAbout);
        this.webView.getSettings().setCacheMode(2);
        if (UserPreferences.getTheme() == R.style.Theme.AntennaPod.Dark) {
            if (VERSION.SDK_INT <= 15) {
                this.webView.setLayerType(1, null);
            }
            this.webView.setBackgroundColor(0);
        }
        this.webView.setWebViewClient(new C07101());
        loadAsset("about.html");
    }

    private void loadAsset(String filename) {
        this.disposable = Single.create(new -$$Lambda$AboutActivity$4mHdNVmGvIYIEZaim457ElT8K_g(this, filename)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new -$$Lambda$AboutActivity$HyD-_Bz__3VP7zZj_EY8LJIN7Uc(this, filename), -$$Lambda$AboutActivity$HuzRVQGG0NAJp8CVFySLazqc0jg.INSTANCE);
    }

    public static /* synthetic */ void lambda$loadAsset$0(AboutActivity aboutActivity, String filename, SingleEmitter subscriber) throws Exception {
        InputStream input = null;
        try {
            String fontColor = String.format("#%06X", new Object[]{Integer.valueOf(aboutActivity.getTheme().obtainStyledAttributes(new int[]{R.attr.about_screen_font_color, R.attr.about_screen_background, R.attr.about_screen_card_background, R.attr.about_screen_card_border}).getColor(0, 0) & ViewCompat.MEASURED_SIZE_MASK)});
            String backgroundColor = String.format("#%06X", new Object[]{Integer.valueOf(res.getColor(1, 0) & ViewCompat.MEASURED_SIZE_MASK)});
            String cardBackground = String.format("#%06X", new Object[]{Integer.valueOf(res.getColor(2, 0) & ViewCompat.MEASURED_SIZE_MASK)});
            String cardBorder = String.format("#%06X", new Object[]{Integer.valueOf(ViewCompat.MEASURED_SIZE_MASK & res.getColor(3, 0))});
            res.recycle();
            input = aboutActivity.getAssets().open(filename);
            String webViewData = IOUtils.toString(input, Charset.defaultCharset());
            if (!webViewData.startsWith("<!DOCTYPE html>")) {
                webViewData = webViewData.replace("%", "&#37;");
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("<!DOCTYPE html><html><head>    <meta http-equiv=\"Content-Type\" content=\"text/html;charset=UTF-8\">    <style type=\"text/css\">        @font-face {        font-family: 'Roboto-Light';           src: url('file:///android_asset/Roboto-Light.ttf');        }        * {           color: @fontcolor@;           font-family: roboto-Light;           font-size: 8pt;        }    </style></head><body><p>");
                stringBuilder.append(webViewData);
                stringBuilder.append("</p></body></html>");
                webViewData = stringBuilder.toString().replace("\n", "<br/>");
            }
            subscriber.onSuccess(webViewData.replace("@fontcolor@", fontColor).replace("@background@", backgroundColor).replace("@card_background@", cardBackground).replace("@card_border@", cardBorder));
        } catch (IOException e) {
            Log.e(TAG, Log.getStackTraceString(e));
            subscriber.onError(e);
        } catch (Throwable th) {
            IOUtils.closeQuietly(null);
        }
        IOUtils.closeQuietly(input);
    }

    public static /* synthetic */ void lambda$loadAsset$1(AboutActivity aboutActivity, String filename, Object webViewData) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("file:///android_asset/");
        stringBuilder.append(filename.toString());
        aboutActivity.webView.loadDataWithBaseURL("file:///android_asset/", webViewData.toString(), "text/html", "utf-8", stringBuilder.toString());
    }

    public void onBackPressed() {
        if (this.webView.canGoBack()) {
            this.webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != 16908332) {
            return super.onOptionsItemSelected(item);
        }
        onBackPressed();
        return true;
    }

    protected void onDestroy() {
        super.onDestroy();
        Disposable disposable = this.disposable;
        if (disposable != null) {
            disposable.dispose();
        }
        LinearLayout linearLayout = this.webViewContainer;
        if (linearLayout != null && this.webView != null) {
            linearLayout.removeAllViews();
            this.webView.destroy();
        }
    }
}
