package de.danoeh.antennapod.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import de.danoeh.antennapod.adapter.FeedItemlistDescriptionAdapter;
import de.danoeh.antennapod.core.dialog.DownloadRequestErrorDialogCreator;
import de.danoeh.antennapod.core.event.DownloadEvent;
import de.danoeh.antennapod.core.feed.EventDistributor;
import de.danoeh.antennapod.core.feed.EventDistributor$EventListener;
import de.danoeh.antennapod.core.feed.Feed;
import de.danoeh.antennapod.core.feed.FeedItem;
import de.danoeh.antennapod.core.feed.FeedPreferences;
import de.danoeh.antennapod.core.feed.FeedPreferences.AutoDeleteAction;
import de.danoeh.antennapod.core.glide.ApGlideSettings;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.service.download.DownloadRequest;
import de.danoeh.antennapod.core.service.download.DownloadStatus;
import de.danoeh.antennapod.core.service.download.Downloader;
import de.danoeh.antennapod.core.service.download.HttpDownloader;
import de.danoeh.antennapod.core.storage.DBReader;
import de.danoeh.antennapod.core.storage.DownloadRequestException;
import de.danoeh.antennapod.core.storage.DownloadRequester;
import de.danoeh.antennapod.core.storage.PodDBAdapter;
import de.danoeh.antennapod.core.syndication.handler.FeedHandler;
import de.danoeh.antennapod.core.syndication.handler.FeedHandlerResult;
import de.danoeh.antennapod.core.syndication.handler.UnsupportedFeedtypeException;
import de.danoeh.antennapod.core.util.DownloadError;
import de.danoeh.antennapod.core.util.FileNameGenerator;
import de.danoeh.antennapod.core.util.StorageUtils;
import de.danoeh.antennapod.core.util.URLChecker;
import de.danoeh.antennapod.core.util.syndication.FeedDiscoverer;
import de.danoeh.antennapod.core.util.syndication.HtmlToPlainText;
import de.danoeh.antennapod.debug.R;
import de.danoeh.antennapod.dialog.AuthenticationDialog;
import de.greenrobot.event.EventBus;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;

public class OnlineFeedViewActivity extends AppCompatActivity {
    public static final String ARG_FEEDURL = "arg.feedurl";
    public static final String ARG_TITLE = "title";
    private static final int EVENTS = 1;
    private static final int RESULT_ERROR = 2;
    private static final String TAG = "OnlineFeedViewActivity";
    private Dialog dialog;
    private Disposable download;
    private Downloader downloader;
    private Feed feed;
    private volatile List<Feed> feeds;
    private boolean isPaused;
    private final EventDistributor$EventListener listener = new C10171();
    private Disposable parser;
    private String selectedDownloadUrl;
    private Button subscribeButton;
    private Disposable updater;

    /* renamed from: de.danoeh.antennapod.activity.OnlineFeedViewActivity$1 */
    class C10171 extends EventDistributor$EventListener {
        C10171() {
        }

        public void update(EventDistributor eventDistributor, Integer arg) {
            if ((arg.intValue() & 1) != 0) {
                OnlineFeedViewActivity.this.updater = Observable.fromCallable(-$$Lambda$sFAh8ju2XgeDH1Wr8ACqP9v8HJs.INSTANCE).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new -$$Lambda$OnlineFeedViewActivity$1$s8ni8lJThq-jhWbHNwxBNx5u9oE(), -$$Lambda$OnlineFeedViewActivity$1$PNygMH_t6JqO7OHxnX1-9skumhA.INSTANCE);
            } else if ((arg.intValue() & 1) != 0) {
                OnlineFeedViewActivity onlineFeedViewActivity = OnlineFeedViewActivity.this;
                onlineFeedViewActivity.setSubscribeButtonState(onlineFeedViewActivity.feed);
            }
        }

        public static /* synthetic */ void lambda$update$0(C10171 c10171, List feeds) throws Exception {
            OnlineFeedViewActivity.this.feeds = feeds;
            OnlineFeedViewActivity onlineFeedViewActivity = OnlineFeedViewActivity.this;
            onlineFeedViewActivity.setSubscribeButtonState(onlineFeedViewActivity.feed);
        }
    }

    private class FeedViewAuthenticationDialog extends AuthenticationDialog {
        private final String feedUrl;

        FeedViewAuthenticationDialog(Context context, int titleRes, String feedUrl) {
            super(context, titleRes, true, false, null, null);
            this.feedUrl = feedUrl;
        }

        protected void onCancelled() {
            super.onCancelled();
            OnlineFeedViewActivity.this.finish();
        }

        protected void onConfirmed(String username, String password, boolean saveUsernamePassword) {
            OnlineFeedViewActivity.this.startFeedDownload(this.feedUrl, username, password);
        }
    }

    public void onEventMainThread(DownloadEvent event) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("onEventMainThread() called with: event = [");
        stringBuilder.append(event);
        stringBuilder.append("]");
        Log.d(str, stringBuilder.toString());
        setSubscribeButtonState(this.feed);
    }

    protected void onCreate(Bundle savedInstanceState) {
        setTheme(UserPreferences.getTheme());
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if (actionBar != null && getIntent() != null && getIntent().hasExtra("title")) {
            actionBar.setTitle(getIntent().getStringExtra("title"));
        }
        StorageUtils.checkStorageAvailability(this);
        String feedUrl = null;
        if (getIntent().hasExtra(ARG_FEEDURL)) {
            feedUrl = getIntent().getStringExtra(ARG_FEEDURL);
        } else {
            if (!TextUtils.equals(getIntent().getAction(), "android.intent.action.SEND")) {
                if (TextUtils.equals(getIntent().getAction(), "android.intent.action.VIEW")) {
                }
            }
            feedUrl = TextUtils.equals(getIntent().getAction(), "android.intent.action.SEND") ? getIntent().getStringExtra("android.intent.extra.TEXT") : getIntent().getDataString();
            if (actionBar != null) {
                actionBar.setTitle((int) R.string.add_feed_label);
            }
        }
        if (feedUrl == null) {
            Log.e(TAG, "feedUrl is null.");
            new Builder(this).setNeutralButton(17039370, new -$$Lambda$OnlineFeedViewActivity$7I6H6KPzllmzcC5qGQ3hWnakbY0()).setTitle((int) R.string.error_label).setMessage((int) R.string.null_value_podcast_error).create().show();
            return;
        }
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Activity was started with url ");
        stringBuilder.append(feedUrl);
        Log.d(str, stringBuilder.toString());
        setLoadingLayout();
        if (savedInstanceState == null) {
            startFeedDownload(feedUrl, null, null);
        } else {
            startFeedDownload(feedUrl, savedInstanceState.getString(PodDBAdapter.KEY_USERNAME), savedInstanceState.getString(PodDBAdapter.KEY_PASSWORD));
        }
    }

    private void setLoadingLayout() {
        RelativeLayout rl = new RelativeLayout(this);
        LayoutParams rlLayoutParams = new LayoutParams(-1, -1);
        ProgressBar pb = new ProgressBar(this);
        pb.setIndeterminate(true);
        LayoutParams pbLayoutParams = new LayoutParams(-2, -2);
        pbLayoutParams.addRule(13);
        rl.addView(pb, pbLayoutParams);
        addContentView(rl, rlLayoutParams);
    }

    protected void onResume() {
        super.onResume();
        this.isPaused = false;
        EventDistributor.getInstance().register(this.listener);
        EventBus.getDefault().register(this);
    }

    protected void onPause() {
        super.onPause();
        this.isPaused = true;
        EventDistributor.getInstance().unregister(this.listener);
        EventBus.getDefault().unregister(this);
    }

    protected void onStop() {
        super.onStop();
        Downloader downloader = this.downloader;
        if (downloader != null && !downloader.isFinished()) {
            this.downloader.cancel();
        }
        Dialog dialog = this.dialog;
        if (dialog != null && dialog.isShowing()) {
            this.dialog.dismiss();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        Disposable disposable = this.updater;
        if (disposable != null) {
            disposable.dispose();
        }
        disposable = this.download;
        if (disposable != null) {
            disposable.dispose();
        }
        disposable = this.parser;
        if (disposable != null) {
            disposable.dispose();
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Feed feed = this.feed;
        if (feed != null && feed.getPreferences() != null) {
            outState.putString(PodDBAdapter.KEY_USERNAME, this.feed.getPreferences().getUsername());
            outState.putString(PodDBAdapter.KEY_PASSWORD, this.feed.getPreferences().getPassword());
        }
    }

    private void resetIntent(String url, String title) {
        Intent intent = new Intent();
        intent.putExtra(ARG_FEEDURL, url);
        intent.putExtra("title", title);
        setIntent(intent);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != 16908332) {
            return super.onOptionsItemSelected(item);
        }
        Intent destIntent = new Intent(this, MainActivity.class);
        if (NavUtils.shouldUpRecreateTask(this, destIntent)) {
            startActivity(destIntent);
        } else {
            NavUtils.navigateUpFromSameTask(this);
        }
        return true;
    }

    private void startFeedDownload(String url, String username, String password) {
        Log.d(TAG, "Starting feed download");
        this.feed = new Feed(URLChecker.prepareURL(url), null);
        if (username != null && password != null) {
            r0.feed.setPreferences(new FeedPreferences(0, false, AutoDeleteAction.GLOBAL, username, password));
        }
        r0.feed.setFile_url(new File(getExternalCacheDir(), FileNameGenerator.generateFileName(r0.feed.getDownload_url())).toString());
        r0.download = Observable.fromCallable(new -$$Lambda$OnlineFeedViewActivity$ctXKxnY_nxnkAzy9sK3azvlhqZ8(this, new DownloadRequest(r0.feed.getFile_url(), r0.feed.getDownload_url(), "OnlineFeed", 0, 0, username, password, true, null))).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new -$$Lambda$OnlineFeedViewActivity$5ytEz3yQ6VCuzWZ_KDZqgWSjAtk(), -$$Lambda$OnlineFeedViewActivity$PVZDlaveDJBPPE-dCjF0VjkthIY.INSTANCE);
    }

    public static /* synthetic */ DownloadStatus lambda$startFeedDownload$1(OnlineFeedViewActivity onlineFeedViewActivity, DownloadRequest request) throws Exception {
        onlineFeedViewActivity.feeds = DBReader.getFeedList();
        onlineFeedViewActivity.downloader = new HttpDownloader(request);
        onlineFeedViewActivity.downloader.call();
        return onlineFeedViewActivity.downloader.getResult();
    }

    private void checkDownloadResult(DownloadStatus status) {
        if (status == null) {
            Log.wtf(TAG, "DownloadStatus returned by Downloader was null");
            finish();
        } else if (!status.isCancelled()) {
            if (status.isSuccessful()) {
                parseFeed();
            } else if (status.getReason() != DownloadError.ERROR_UNAUTHORIZED) {
                String errorMsg = status.getReason().getErrorString(this);
                if (status.getReasonDetailed() != null) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(errorMsg);
                    stringBuilder.append(" (");
                    stringBuilder.append(status.getReasonDetailed());
                    stringBuilder.append(")");
                    errorMsg = stringBuilder.toString();
                }
                showErrorDialog(errorMsg);
            } else if (!isFinishing() && !this.isPaused) {
                this.dialog = new FeedViewAuthenticationDialog(this, R.string.authentication_notification_title, this.downloader.getDownloadRequest().getSource());
                this.dialog.show();
            }
        }
    }

    private void parseFeed() {
        Feed feed = this.feed;
        if (feed == null || (feed.getFile_url() == null && this.feed.isDownloaded())) {
            throw new IllegalStateException("feed must be non-null and downloaded when parseFeed is called");
        }
        Log.d(TAG, "Parsing feed");
        this.parser = Observable.fromCallable(new -$$Lambda$OnlineFeedViewActivity$fqOlZgE8h-iWsRRpLnY5dZhp__g()).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe(new -$$Lambda$OnlineFeedViewActivity$FGRsngQ0GPHU48sk50fr5rxWtKU(), new -$$Lambda$OnlineFeedViewActivity$M87foL2fWByM2QpoTj_uxwJPxzQ());
    }

    public static /* synthetic */ FeedHandlerResult lambda$parseFeed$3(OnlineFeedViewActivity onlineFeedViewActivity) throws Exception {
        boolean rc;
        try {
            FeedHandlerResult parseFeed = new FeedHandler().parseFeed(onlineFeedViewActivity.feed);
            rc = new File(onlineFeedViewActivity.feed.getFile_url()).delete();
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Deleted feed source file. Result: ");
            stringBuilder.append(rc);
            Log.d(str, stringBuilder.toString());
            return parseFeed;
        } catch (UnsupportedFeedtypeException e) {
            Log.d(TAG, "Unsupported feed type detected");
            if ("html".equalsIgnoreCase(e.getRootElement())) {
                onlineFeedViewActivity.showFeedDiscoveryDialog(new File(onlineFeedViewActivity.feed.getFile_url()), onlineFeedViewActivity.feed.getDownload_url());
                boolean rc2 = new File(onlineFeedViewActivity.feed.getFile_url()).delete();
                String str2 = TAG;
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Deleted feed source file. Result: ");
                stringBuilder2.append(rc2);
                Log.d(str2, stringBuilder2.toString());
                return null;
            }
            throw e;
        } catch (Exception e2) {
            Log.e(TAG, Log.getStackTraceString(e2));
            throw e2;
        } catch (Throwable th) {
            rc = new File(onlineFeedViewActivity.feed.getFile_url()).delete();
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append("Deleted feed source file. Result: ");
            stringBuilder3.append(rc);
            Log.d(TAG, stringBuilder3.toString());
        }
    }

    public static /* synthetic */ void lambda$parseFeed$4(OnlineFeedViewActivity onlineFeedViewActivity, FeedHandlerResult result) throws Exception {
        if (result != null) {
            onlineFeedViewActivity.beforeShowFeedInformation(result.feed);
            onlineFeedViewActivity.showFeedInformation(result.feed, result.alternateFeedUrls);
        }
    }

    public static /* synthetic */ void lambda$parseFeed$5(OnlineFeedViewActivity onlineFeedViewActivity, Throwable error) throws Exception {
        String errorMsg = new StringBuilder();
        errorMsg.append(DownloadError.ERROR_PARSER_EXCEPTION.getErrorString(onlineFeedViewActivity));
        errorMsg.append(" (");
        errorMsg.append(error.getMessage());
        errorMsg.append(")");
        onlineFeedViewActivity.showErrorDialog(errorMsg.toString());
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Feed parser exception: ");
        stringBuilder.append(Log.getStackTraceString(error));
        Log.d(str, stringBuilder.toString());
    }

    private void beforeShowFeedInformation(Feed feed) {
        HtmlToPlainText formatter = new HtmlToPlainText();
        if ("atom".equals(feed.getType()) && feed.getDescription() != null) {
            Log.d(TAG, "Removing HTML from feed description");
            feed.setDescription(StringUtils.trim(formatter.getPlainText(Jsoup.parse(feed.getDescription()))));
        }
        Log.d(TAG, "Removing HTML from shownotes");
        if (feed.getItems() != null) {
            for (FeedItem item : feed.getItems()) {
                if (item.getDescription() != null) {
                    item.setDescription(StringUtils.trim(formatter.getPlainText(Jsoup.parse(item.getDescription()))));
                }
            }
        }
    }

    private void showFeedInformation(Feed feed, Map<String, String> alternateFeedUrls) {
        setContentView(R.layout.listview_activity);
        this.feed = feed;
        this.selectedDownloadUrl = feed.getDownload_url();
        EventDistributor.getInstance().register(this.listener);
        ListView listView = (ListView) findViewById(R.id.listview);
        View header = LayoutInflater.from(this).inflate(R.layout.onlinefeedview_header, listView, false);
        listView.addHeaderView(header);
        listView.setAdapter(new FeedItemlistDescriptionAdapter(this, 0, feed.getItems()));
        ImageView cover = (ImageView) header.findViewById(R.id.imgvCover);
        TextView title = (TextView) header.findViewById(R.id.txtvTitle);
        TextView author = (TextView) header.findViewById(R.id.txtvAuthor);
        TextView description = (TextView) header.findViewById(R.id.txtvDescription);
        Spinner spAlternateUrls = (Spinner) header.findViewById(R.id.spinnerAlternateUrls);
        this.subscribeButton = (Button) header.findViewById(R.id.butSubscribe);
        if (StringUtils.isNotBlank(feed.getImageUrl())) {
            Glide.with((FragmentActivity) this).load(feed.getImageUrl()).apply(new RequestOptions().placeholder((int) R.color.light_gray).error((int) R.color.light_gray).diskCacheStrategy(ApGlideSettings.AP_DISK_CACHE_STRATEGY).fitCenter().dontAnimate()).into(cover);
        }
        title.setText(feed.getTitle());
        author.setText(feed.getAuthor());
        description.setText(feed.getDescription());
        this.subscribeButton.setOnClickListener(new -$$Lambda$OnlineFeedViewActivity$gGGmFGBJPDHoYeWGsqf9rJBmm-k(this, feed));
        if (alternateFeedUrls.isEmpty()) {
            spAlternateUrls.setVisibility(8);
        } else {
            spAlternateUrls.setVisibility(0);
            final List<String> alternateUrlsList = new ArrayList();
            List<String> alternateUrlsTitleList = new ArrayList();
            alternateUrlsList.add(feed.getDownload_url());
            alternateUrlsTitleList.add(feed.getTitle());
            alternateUrlsList.addAll(alternateFeedUrls.keySet());
            for (String url : alternateFeedUrls.keySet()) {
                alternateUrlsTitleList.add(alternateFeedUrls.get(url));
            }
            ArrayAdapter<String> adapter = new ArrayAdapter(this, 17367048, alternateUrlsTitleList);
            adapter.setDropDownViewResource(17367049);
            spAlternateUrls.setAdapter(adapter);
            spAlternateUrls.setOnItemSelectedListener(new OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                    OnlineFeedViewActivity.this.selectedDownloadUrl = (String) alternateUrlsList.get(position);
                }

                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
        }
        setSubscribeButtonState(feed);
    }

    public static /* synthetic */ void lambda$showFeedInformation$6(OnlineFeedViewActivity onlineFeedViewActivity, Feed feed, View v) {
        if (onlineFeedViewActivity.feedInFeedlist(feed)) {
            Intent intent = new Intent(onlineFeedViewActivity, MainActivity.class);
            intent.putExtra(MainActivity.EXTRA_FEED_ID, onlineFeedViewActivity.getFeedId(feed));
            intent.addFlags(67108864);
            onlineFeedViewActivity.startActivity(intent);
            return;
        }
        Feed f = new Feed(onlineFeedViewActivity.selectedDownloadUrl, null, feed.getTitle());
        f.setPreferences(feed.getPreferences());
        onlineFeedViewActivity.feed = f;
        try {
            DownloadRequester.getInstance().downloadFeed(onlineFeedViewActivity, f);
        } catch (DownloadRequestException e) {
            Log.e(TAG, Log.getStackTraceString(e));
            DownloadRequestErrorDialogCreator.newRequestErrorDialog(onlineFeedViewActivity, e.getMessage());
        }
        onlineFeedViewActivity.setSubscribeButtonState(feed);
    }

    private void setSubscribeButtonState(Feed feed) {
        if (this.subscribeButton != null && feed != null) {
            if (DownloadRequester.getInstance().isDownloadingFile(feed.getDownload_url())) {
                this.subscribeButton.setEnabled(false);
                this.subscribeButton.setText(R.string.downloading_label);
            } else if (feedInFeedlist(feed)) {
                this.subscribeButton.setEnabled(true);
                this.subscribeButton.setText(R.string.open_podcast);
            } else {
                this.subscribeButton.setEnabled(true);
                this.subscribeButton.setText(R.string.subscribe_label);
            }
        }
    }

    private boolean feedInFeedlist(Feed feed) {
        if (this.feeds != null) {
            if (feed != null) {
                for (Feed f : this.feeds) {
                    if (f.getIdentifyingValue().equals(feed.getIdentifyingValue())) {
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }

    private long getFeedId(Feed feed) {
        if (this.feeds != null) {
            if (feed != null) {
                for (Feed f : this.feeds) {
                    if (f.getIdentifyingValue().equals(feed.getIdentifyingValue())) {
                        return f.getId();
                    }
                }
                return 0;
            }
        }
        return 0;
    }

    @UiThread
    private void showErrorDialog(String errorMsg) {
        if (!isFinishing() && !this.isPaused) {
            Builder builder = new Builder(this);
            builder.setTitle((int) R.string.error_label);
            if (errorMsg != null) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(getString(R.string.error_msg_prefix));
                stringBuilder.append(errorMsg);
                builder.setMessage(stringBuilder.toString());
            } else {
                builder.setMessage((int) R.string.error_msg_prefix);
            }
            builder.setNeutralButton(17039370, -$$Lambda$OnlineFeedViewActivity$vpP9AP2QMC7TkvlHTYfcEg65kMs.INSTANCE);
            builder.setOnCancelListener(new -$$Lambda$OnlineFeedViewActivity$0ruhzTbTxij3R71tZCke3tRvsCQ());
            Dialog dialog = this.dialog;
            if (dialog != null && dialog.isShowing()) {
                this.dialog.dismiss();
            }
            this.dialog = builder.show();
        }
    }

    public static /* synthetic */ void lambda$showErrorDialog$8(OnlineFeedViewActivity onlineFeedViewActivity, DialogInterface dialog) {
        onlineFeedViewActivity.setResult(2);
        onlineFeedViewActivity.finish();
    }

    private void showFeedDiscoveryDialog(File feedFile, String baseUrl) {
        try {
            Map<String, String> urlsMap = new FeedDiscoverer().findLinks(feedFile, baseUrl);
            if (urlsMap != null) {
                if (!urlsMap.isEmpty()) {
                    if (!this.isPaused) {
                        if (!isFinishing()) {
                            List<String> titles = new ArrayList();
                            List<String> urls = new ArrayList();
                            urls.addAll(urlsMap.keySet());
                            for (String url : urls) {
                                titles.add(urlsMap.get(url));
                            }
                            runOnUiThread(new -$$Lambda$OnlineFeedViewActivity$RFVS_Y4dRXUVrxc1SJ0MkNtspz4(this, new Builder(this).setTitle((int) R.string.feeds_label).setCancelable(true).setOnCancelListener(new -$$Lambda$OnlineFeedViewActivity$D9hz9flWjFYkCvkvXjYzcUiMIJM()).setAdapter(new ArrayAdapter(this, R.layout.ellipsize_start_listitem, R.id.txtvTitle, titles), new -$$Lambda$OnlineFeedViewActivity$vL4h2Yl5zSxPIvSxkt_fo8wjbEE(this, urls, titles))));
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static /* synthetic */ void lambda$showFeedDiscoveryDialog$9(OnlineFeedViewActivity onlineFeedViewActivity, List urls, List titles, DialogInterface dialog, int which) {
        String selectedUrl = (String) urls.get(which);
        dialog.dismiss();
        onlineFeedViewActivity.resetIntent(selectedUrl, (String) titles.get(which));
        FeedPreferences prefs = onlineFeedViewActivity.feed.getPreferences();
        if (prefs != null) {
            onlineFeedViewActivity.startFeedDownload(selectedUrl, prefs.getUsername(), prefs.getPassword());
        } else {
            onlineFeedViewActivity.startFeedDownload(selectedUrl, null, null);
        }
    }

    public static /* synthetic */ void lambda$showFeedDiscoveryDialog$11(OnlineFeedViewActivity onlineFeedViewActivity, Builder ab) {
        Dialog dialog = onlineFeedViewActivity.dialog;
        if (dialog != null && dialog.isShowing()) {
            onlineFeedViewActivity.dialog.dismiss();
        }
        onlineFeedViewActivity.dialog = ab.show();
    }
}
