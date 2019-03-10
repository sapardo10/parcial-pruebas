package de.danoeh.antennapod.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.LightingColorFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import de.danoeh.antennapod.core.dialog.ConfirmationDialog;
import de.danoeh.antennapod.core.dialog.DownloadRequestErrorDialogCreator;
import de.danoeh.antennapod.core.feed.Feed;
import de.danoeh.antennapod.core.feed.FeedFilter;
import de.danoeh.antennapod.core.feed.FeedPreferences;
import de.danoeh.antennapod.core.feed.FeedPreferences.AutoDeleteAction;
import de.danoeh.antennapod.core.glide.ApGlideSettings;
import de.danoeh.antennapod.core.glide.FastBlurTransformation;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.storage.DBReader;
import de.danoeh.antennapod.core.storage.DBWriter;
import de.danoeh.antennapod.core.storage.DownloadRequestException;
import de.danoeh.antennapod.core.util.IntentUtils;
import de.danoeh.antennapod.debug.R;
import de.danoeh.antennapod.menuhandler.FeedMenuHandler;
import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class FeedSettingsActivity extends AppCompatActivity {
    public static final String EXTRA_FEED_ID = "de.danoeh.antennapod.extra.feedId";
    private static final String TAG = "FeedSettingsActivity";
    private boolean authInfoChanged = false;
    private final TextWatcher authTextWatcher = new C07141();
    private boolean autoDeleteChanged = false;
    private CheckBox cbxAutoDownload;
    private CheckBox cbxKeepUpdated;
    private Disposable disposable;
    private EditText etxtFilterText;
    private EditText etxtPassword;
    private EditText etxtUsername;
    private Feed feed;
    private boolean filterInclude = true;
    private boolean filterTextChanged = false;
    private final TextWatcher filterTextWatcher = new C07152();
    private ImageView imgvCover;
    private RadioButton rdoFilterExclude;
    private RadioButton rdoFilterInclude;
    private Spinner spnAutoDelete;
    private TextView txtvTitle;

    /* renamed from: de.danoeh.antennapod.activity.FeedSettingsActivity$1 */
    class C07141 implements TextWatcher {
        C07141() {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            FeedSettingsActivity.this.authInfoChanged = true;
        }
    }

    /* renamed from: de.danoeh.antennapod.activity.FeedSettingsActivity$2 */
    class C07152 implements TextWatcher {
        C07152() {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            FeedSettingsActivity.this.filterTextChanged = true;
        }
    }

    /* renamed from: de.danoeh.antennapod.activity.FeedSettingsActivity$3 */
    class C07163 implements OnItemSelectedListener {
        C07163() {
        }

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            AutoDeleteAction auto_delete_action;
            switch (parent.getSelectedItemPosition()) {
                case 0:
                    auto_delete_action = AutoDeleteAction.GLOBAL;
                    break;
                case 1:
                    auto_delete_action = AutoDeleteAction.YES;
                    break;
                case 2:
                    auto_delete_action = AutoDeleteAction.NO;
                    break;
                default:
                    return;
            }
            FeedSettingsActivity.this.feed.getPreferences().setAutoDeleteAction(auto_delete_action);
            FeedSettingsActivity.this.autoDeleteChanged = true;
        }

        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    private static class ApplyToEpisodesDialog extends ConfirmationDialog {
        private final boolean autoDownload;
        private final Feed feed;

        ApplyToEpisodesDialog(Context context, Feed feed, boolean autoDownload) {
            super(context, (int) R.string.auto_download_apply_to_items_title, (int) R.string.auto_download_apply_to_items_message);
            this.feed = feed;
            this.autoDownload = autoDownload;
            setPositiveText(R.string.yes);
            setNegativeText(R.string.no);
        }

        public void onConfirmButtonPressed(DialogInterface dialog) {
            DBWriter.setFeedsItemsAutoDownload(this.feed, this.autoDownload);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        setTheme(UserPreferences.getTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedsettings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        long feedId = getIntent().getLongExtra("de.danoeh.antennapod.extra.feedId", -1);
        this.imgvCover = (ImageView) findViewById(R.id.imgvCover);
        this.txtvTitle = (TextView) findViewById(R.id.txtvTitle);
        TextView txtvAuthorHeader = (TextView) findViewById(R.id.txtvAuthor);
        ImageView imgvBackground = (ImageView) findViewById(R.id.imgvBackground);
        findViewById(R.id.butShowInfo).setVisibility(4);
        findViewById(R.id.butShowSettings).setVisibility(4);
        imgvBackground.setColorFilter(new LightingColorFilter(-8224126, 0));
        this.cbxAutoDownload = (CheckBox) findViewById(R.id.cbxAutoDownload);
        this.cbxKeepUpdated = (CheckBox) findViewById(R.id.cbxKeepUpdated);
        this.spnAutoDelete = (Spinner) findViewById(R.id.spnAutoDelete);
        this.etxtUsername = (EditText) findViewById(R.id.etxtUsername);
        this.etxtPassword = (EditText) findViewById(R.id.etxtPassword);
        this.etxtFilterText = (EditText) findViewById(R.id.etxtEpisodeFilterText);
        this.rdoFilterInclude = (RadioButton) findViewById(R.id.radio_filter_include);
        this.rdoFilterInclude.setOnClickListener(new -$$Lambda$FeedSettingsActivity$r2Yzr_-c3YjrqKHmLqddRYSJOWg());
        this.rdoFilterExclude = (RadioButton) findViewById(R.id.radio_filter_exclude);
        this.rdoFilterExclude.setOnClickListener(new -$$Lambda$FeedSettingsActivity$MHJo3yiOimrPO_36AUQllX2yxYQ());
        this.disposable = Maybe.create(new -$$Lambda$FeedSettingsActivity$VTefPD9wFwuIcDfvgrPk6VqjhrU(feedId)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new -$$Lambda$FeedSettingsActivity$tHvev_0PaM7NIxCI2Lg5wxFppTg(this, imgvBackground, txtvAuthorHeader), new -$$Lambda$FeedSettingsActivity$fGLenQqWcNySlXlqNXUt1k-ymQE(), new -$$Lambda$FeedSettingsActivity$ZaNt95jCmFP6IY9Ikw2khro-TAo());
    }

    public static /* synthetic */ void lambda$onCreate$0(FeedSettingsActivity feedSettingsActivity, View v) {
        feedSettingsActivity.filterInclude = true;
        feedSettingsActivity.filterTextChanged = true;
    }

    public static /* synthetic */ void lambda$onCreate$1(FeedSettingsActivity feedSettingsActivity, View v) {
        feedSettingsActivity.filterInclude = false;
        feedSettingsActivity.filterTextChanged = true;
    }

    static /* synthetic */ void lambda$onCreate$2(long feedId, MaybeEmitter emitter) throws Exception {
        Feed feed = DBReader.getFeed(feedId);
        if (feed != null) {
            emitter.onSuccess(feed);
        } else {
            emitter.onComplete();
        }
    }

    public static /* synthetic */ void lambda$onCreate$5(FeedSettingsActivity feedSettingsActivity, ImageView imgvBackground, TextView txtvAuthorHeader, Feed result) throws Exception {
        feedSettingsActivity.feed = result;
        FeedPreferences prefs = feedSettingsActivity.feed.getPreferences();
        Glide.with((FragmentActivity) feedSettingsActivity).load(feedSettingsActivity.feed.getImageLocation()).apply(new RequestOptions().placeholder((int) R.color.light_gray).error((int) R.color.light_gray).diskCacheStrategy(ApGlideSettings.AP_DISK_CACHE_STRATEGY).fitCenter().dontAnimate()).into(feedSettingsActivity.imgvCover);
        Glide.with((FragmentActivity) feedSettingsActivity).load(feedSettingsActivity.feed.getImageLocation()).apply(new RequestOptions().placeholder((int) R.color.image_readability_tint).error((int) R.color.image_readability_tint).diskCacheStrategy(ApGlideSettings.AP_DISK_CACHE_STRATEGY).transform(new FastBlurTransformation()).dontAnimate()).into(imgvBackground);
        feedSettingsActivity.txtvTitle.setText(feedSettingsActivity.feed.getTitle());
        if (!TextUtils.isEmpty(feedSettingsActivity.feed.getAuthor())) {
            txtvAuthorHeader.setText(feedSettingsActivity.feed.getAuthor());
        }
        feedSettingsActivity.cbxAutoDownload.setEnabled(UserPreferences.isEnableAutodownload());
        feedSettingsActivity.cbxAutoDownload.setChecked(prefs.getAutoDownload());
        feedSettingsActivity.cbxAutoDownload.setOnCheckedChangeListener(new -$$Lambda$FeedSettingsActivity$zut01-6afVpZAbXZXEZJCNxYEi0(feedSettingsActivity));
        feedSettingsActivity.cbxKeepUpdated.setChecked(prefs.getKeepUpdated());
        feedSettingsActivity.cbxKeepUpdated.setOnCheckedChangeListener(new -$$Lambda$FeedSettingsActivity$N6VyOYjcxPRSzPmaavuijA3s6Fg(feedSettingsActivity));
        feedSettingsActivity.spnAutoDelete.setOnItemSelectedListener(new C07163());
        feedSettingsActivity.spnAutoDelete.setSelection(prefs.getAutoDeleteAction().ordinal());
        feedSettingsActivity.etxtUsername.setText(prefs.getUsername());
        feedSettingsActivity.etxtPassword.setText(prefs.getPassword());
        feedSettingsActivity.etxtUsername.addTextChangedListener(feedSettingsActivity.authTextWatcher);
        feedSettingsActivity.etxtPassword.addTextChangedListener(feedSettingsActivity.authTextWatcher);
        FeedFilter filter = prefs.getFilter();
        if (filter.includeOnly()) {
            feedSettingsActivity.etxtFilterText.setText(filter.getIncludeFilter());
            feedSettingsActivity.rdoFilterInclude.setChecked(true);
            feedSettingsActivity.rdoFilterExclude.setChecked(false);
            feedSettingsActivity.filterInclude = true;
        } else if (filter.excludeOnly()) {
            feedSettingsActivity.etxtFilterText.setText(filter.getExcludeFilter());
            feedSettingsActivity.rdoFilterInclude.setChecked(false);
            feedSettingsActivity.rdoFilterExclude.setChecked(true);
            feedSettingsActivity.filterInclude = false;
        } else {
            Log.d(TAG, "No filter set");
            feedSettingsActivity.rdoFilterInclude.setChecked(false);
            feedSettingsActivity.rdoFilterExclude.setChecked(false);
            feedSettingsActivity.etxtFilterText.setText("");
        }
        feedSettingsActivity.etxtFilterText.addTextChangedListener(feedSettingsActivity.filterTextWatcher);
        feedSettingsActivity.supportInvalidateOptionsMenu();
        feedSettingsActivity.updateAutoDownloadSettings();
    }

    public static /* synthetic */ void lambda$null$3(FeedSettingsActivity feedSettingsActivity, CompoundButton compoundButton, boolean checked) {
        feedSettingsActivity.feed.getPreferences().setAutoDownload(checked);
        feedSettingsActivity.feed.savePreferences();
        feedSettingsActivity.updateAutoDownloadSettings();
        new ApplyToEpisodesDialog(feedSettingsActivity, feedSettingsActivity.feed, checked).createNewDialog().show();
    }

    public static /* synthetic */ void lambda$null$4(FeedSettingsActivity feedSettingsActivity, CompoundButton compoundButton, boolean checked) {
        feedSettingsActivity.feed.getPreferences().setKeepUpdated(checked);
        feedSettingsActivity.feed.savePreferences();
    }

    public static /* synthetic */ void lambda$onCreate$6(FeedSettingsActivity feedSettingsActivity, Throwable error) throws Exception {
        Log.d(TAG, Log.getStackTraceString(error));
        feedSettingsActivity.finish();
    }

    public static /* synthetic */ void lambda$onCreate$7(FeedSettingsActivity feedSettingsActivity) throws Exception {
        Log.e(TAG, "Activity was started with invalid arguments");
        feedSettingsActivity.finish();
    }

    protected void onPause() {
        super.onPause();
        FeedPreferences prefs = this.feed;
        if (prefs != null) {
            prefs = prefs.getPreferences();
            if (this.authInfoChanged) {
                Log.d(TAG, "Auth info changed, saving credentials");
                prefs.setUsername(this.etxtUsername.getText().toString());
                prefs.setPassword(this.etxtPassword.getText().toString());
            }
            if (this.filterTextChanged) {
                Log.d(TAG, "Filter info changed, saving...");
                String filterText = this.etxtFilterText.getText().toString();
                String includeString = "";
                String excludeString = "";
                if (this.filterInclude) {
                    includeString = filterText;
                } else {
                    excludeString = filterText;
                }
                prefs.setFilter(new FeedFilter(includeString, excludeString));
            }
            if (!(this.authInfoChanged || this.autoDeleteChanged)) {
                if (!this.filterTextChanged) {
                    this.authInfoChanged = false;
                    this.autoDeleteChanged = false;
                    this.filterTextChanged = false;
                }
            }
            DBWriter.setFeedPreferences(prefs);
            this.authInfoChanged = false;
            this.autoDeleteChanged = false;
            this.filterTextChanged = false;
        }
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

    private void updateAutoDownloadSettings() {
        Feed feed = this.feed;
        if (feed != null && feed.getPreferences() != null) {
            boolean enabled = this.feed.getPreferences().getAutoDownload() && UserPreferences.isEnableAutodownload();
            this.rdoFilterInclude.setEnabled(enabled);
            this.rdoFilterExclude.setEnabled(enabled);
            this.etxtFilterText.setEnabled(enabled);
        }
    }
}
