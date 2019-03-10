package de.danoeh.antennapod.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.LightingColorFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.joanzapata.iconify.Iconify;
import de.danoeh.antennapod.core.dialog.DownloadRequestErrorDialogCreator;
import de.danoeh.antennapod.core.feed.Feed;
import de.danoeh.antennapod.core.glide.ApGlideSettings;
import de.danoeh.antennapod.core.glide.FastBlurTransformation;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.storage.DBReader;
import de.danoeh.antennapod.core.storage.DownloadRequestException;
import de.danoeh.antennapod.core.util.IntentUtils;
import de.danoeh.antennapod.core.util.LangUtils;
import de.danoeh.antennapod.core.util.syndication.HtmlToPlainText;
import de.danoeh.antennapod.debug.R;
import de.danoeh.antennapod.menuhandler.FeedMenuHandler;
import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;

public class FeedInfoActivity extends AppCompatActivity {
    public static final String EXTRA_FEED_ID = "de.danoeh.antennapod.extra.feedId";
    private static final String TAG = "FeedInfoActivity";
    private final OnClickListener copyUrlToClipboard = new C07131();
    private Disposable disposable;
    private Feed feed;
    private ImageView imgvCover;
    private TextView lblAuthor;
    private TextView lblLanguage;
    private TextView txtvAuthor;
    private TextView txtvDescription;
    private TextView txtvLanguage;
    private TextView txtvTitle;
    private TextView txtvUrl;

    /* renamed from: de.danoeh.antennapod.activity.FeedInfoActivity$1 */
    class C07131 implements OnClickListener {
        C07131() {
        }

        public void onClick(View v) {
            if (FeedInfoActivity.this.feed != null && FeedInfoActivity.this.feed.getDownload_url() != null) {
                String url = FeedInfoActivity.this.feed.getDownload_url();
                ((ClipboardManager) FeedInfoActivity.this.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText(url, url));
                Toast.makeText(FeedInfoActivity.this, R.string.copied_url_msg, 0).show();
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        setTheme(UserPreferences.getTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedinfo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        long feedId = getIntent().getLongExtra("de.danoeh.antennapod.extra.feedId", -1);
        this.imgvCover = (ImageView) findViewById(R.id.imgvCover);
        this.txtvTitle = (TextView) findViewById(R.id.txtvTitle);
        TextView txtvAuthorHeader = (TextView) findViewById(R.id.txtvAuthor);
        ImageView imgvBackground = (ImageView) findViewById(R.id.imgvBackground);
        findViewById(R.id.butShowInfo).setVisibility(4);
        findViewById(R.id.butShowSettings).setVisibility(4);
        imgvBackground.setColorFilter(new LightingColorFilter(-8224126, 0));
        this.txtvDescription = (TextView) findViewById(R.id.txtvDescription);
        this.lblLanguage = (TextView) findViewById(R.id.lblLanguage);
        this.txtvLanguage = (TextView) findViewById(R.id.txtvLanguage);
        this.lblAuthor = (TextView) findViewById(R.id.lblAuthor);
        this.txtvAuthor = (TextView) findViewById(R.id.txtvDetailsAuthor);
        this.txtvUrl = (TextView) findViewById(R.id.txtvUrl);
        this.txtvUrl.setOnClickListener(this.copyUrlToClipboard);
        this.disposable = Maybe.create(new -$$Lambda$FeedInfoActivity$QK-r_FoqD2YB0aVmXxL9AY0cD0E(feedId)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new -$$Lambda$FeedInfoActivity$aPsxmgKLrSqLLfH7zmCmSyyMnhU(this, imgvBackground, txtvAuthorHeader), new -$$Lambda$FeedInfoActivity$64ibQQz5VSa4Mu0dW9m-FVE6IO4(), new -$$Lambda$FeedInfoActivity$p05eR9715y81MOvXvN1qn5aa53g());
    }

    static /* synthetic */ void lambda$onCreate$0(long feedId, MaybeEmitter emitter) throws Exception {
        Feed feed = DBReader.getFeed(feedId);
        if (feed != null) {
            emitter.onSuccess(feed);
        } else {
            emitter.onComplete();
        }
    }

    public static /* synthetic */ void lambda$onCreate$1(FeedInfoActivity feedInfoActivity, ImageView imgvBackground, TextView txtvAuthorHeader, Feed result) throws Exception {
        feedInfoActivity.feed = result;
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Language is ");
        stringBuilder.append(feedInfoActivity.feed.getLanguage());
        Log.d(str, stringBuilder.toString());
        str = TAG;
        stringBuilder = new StringBuilder();
        stringBuilder.append("Author is ");
        stringBuilder.append(feedInfoActivity.feed.getAuthor());
        Log.d(str, stringBuilder.toString());
        str = TAG;
        stringBuilder = new StringBuilder();
        stringBuilder.append("URL is ");
        stringBuilder.append(feedInfoActivity.feed.getDownload_url());
        Log.d(str, stringBuilder.toString());
        Glide.with((FragmentActivity) feedInfoActivity).load(feedInfoActivity.feed.getImageLocation()).apply(new RequestOptions().placeholder((int) R.color.light_gray).error((int) R.color.light_gray).diskCacheStrategy(ApGlideSettings.AP_DISK_CACHE_STRATEGY).fitCenter().dontAnimate()).into(feedInfoActivity.imgvCover);
        Glide.with((FragmentActivity) feedInfoActivity).load(feedInfoActivity.feed.getImageLocation()).apply(new RequestOptions().placeholder((int) R.color.image_readability_tint).error((int) R.color.image_readability_tint).diskCacheStrategy(ApGlideSettings.AP_DISK_CACHE_STRATEGY).transform(new FastBlurTransformation()).dontAnimate()).into(imgvBackground);
        feedInfoActivity.txtvTitle.setText(feedInfoActivity.feed.getTitle());
        str = feedInfoActivity.feed.getDescription();
        if (str == null) {
            str = "";
        } else if ("atom".equals(feedInfoActivity.feed.getType())) {
            str = StringUtils.trim(new HtmlToPlainText().getPlainText(Jsoup.parse(feedInfoActivity.feed.getDescription())));
        }
        feedInfoActivity.txtvDescription.setText(str);
        if (TextUtils.isEmpty(feedInfoActivity.feed.getAuthor())) {
            feedInfoActivity.lblAuthor.setVisibility(8);
            feedInfoActivity.txtvAuthor.setVisibility(8);
        } else {
            feedInfoActivity.txtvAuthor.setText(feedInfoActivity.feed.getAuthor());
            txtvAuthorHeader.setText(feedInfoActivity.feed.getAuthor());
        }
        if (TextUtils.isEmpty(feedInfoActivity.feed.getLanguage())) {
            feedInfoActivity.lblLanguage.setVisibility(8);
            feedInfoActivity.txtvLanguage.setVisibility(8);
        } else {
            feedInfoActivity.txtvLanguage.setText(LangUtils.getLanguageString(feedInfoActivity.feed.getLanguage()));
        }
        TextView textView = feedInfoActivity.txtvUrl;
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(feedInfoActivity.feed.getDownload_url());
        stringBuilder2.append(" {fa-paperclip}");
        textView.setText(stringBuilder2.toString());
        Iconify.addIcons(new TextView[]{feedInfoActivity.txtvUrl});
        feedInfoActivity.supportInvalidateOptionsMenu();
    }

    public static /* synthetic */ void lambda$onCreate$2(FeedInfoActivity feedInfoActivity, Throwable error) throws Exception {
        Log.d(TAG, Log.getStackTraceString(error));
        feedInfoActivity.finish();
    }

    public static /* synthetic */ void lambda$onCreate$3(FeedInfoActivity feedInfoActivity) throws Exception {
        Log.e(TAG, "Activity was started with invalid arguments");
        feedInfoActivity.finish();
    }

    public void onDestroy() {
        super.onDestroy();
        Disposable disposable = this.disposable;
        if (disposable != null) {
            disposable.dispose();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.feedinfo, menu);
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean z;
        super.onPrepareOptionsMenu(menu);
        MenuItem findItem = menu.findItem(R.id.support_item);
        Feed feed = this.feed;
        boolean z2 = false;
        if (feed != null) {
            if (feed.getPaymentLink() != null) {
                z = true;
                findItem.setVisible(z);
                findItem = menu.findItem(R.id.share_link_item);
                feed = this.feed;
                z = feed == null && feed.getLink() != null;
                findItem.setVisible(z);
                findItem = menu.findItem(R.id.visit_website_item);
                feed = this.feed;
                if (feed == null && feed.getLink() != null) {
                    if (IntentUtils.isCallable(this, new Intent("android.intent.action.VIEW", Uri.parse(this.feed.getLink())))) {
                        z2 = true;
                        findItem.setVisible(z2);
                        return true;
                    }
                }
                findItem.setVisible(z2);
                return true;
            }
        }
        z = false;
        findItem.setVisible(z);
        findItem = menu.findItem(R.id.share_link_item);
        feed = this.feed;
        if (feed == null) {
        }
        findItem.setVisible(z);
        findItem = menu.findItem(R.id.visit_website_item);
        feed = this.feed;
        if (feed == null) {
        }
        findItem.setVisible(z2);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != 16908332) {
            try {
                return FeedMenuHandler.onOptionsItemClicked(this, item, this.feed);
            } catch (DownloadRequestException e) {
                e.printStackTrace();
                DownloadRequestErrorDialogCreator.newRequestErrorDialog(this, e.getMessage());
                return super.onOptionsItemSelected(item);
            }
        }
        finish();
        return true;
    }
}
