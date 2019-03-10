package de.danoeh.antennapod.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebView.HitTestResult;
import android.webkit.WebViewClient;
import android.widget.Toast;
import de.danoeh.antennapod.activity.MediaplayerInfoActivity;
import de.danoeh.antennapod.activity.MediaplayerInfoActivity$MediaplayerInfoContentFragment;
import de.danoeh.antennapod.core.feed.FeedItem;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.util.Converter;
import de.danoeh.antennapod.core.util.IntentUtils;
import de.danoeh.antennapod.core.util.NetworkUtils;
import de.danoeh.antennapod.core.util.ShareUtils;
import de.danoeh.antennapod.core.util.ShownotesProvider;
import de.danoeh.antennapod.core.util.playback.Playable;
import de.danoeh.antennapod.core.util.playback.PlaybackController;
import de.danoeh.antennapod.core.util.playback.Timeline;
import de.danoeh.antennapod.debug.R;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ItemDescriptionFragment extends Fragment implements MediaplayerInfoActivity$MediaplayerInfoContentFragment {
    private static final String ARG_FEEDITEM_ID = "arg.feeditem";
    private static final String ARG_HIGHLIGHT_TIMECODES = "arg.highlightTimecodes";
    private static final String ARG_PLAYABLE = "arg.playable";
    private static final String ARG_SAVE_STATE = "arg.saveState";
    private static final String PREF = "ItemDescriptionFragmentPrefs";
    private static final String PREF_PLAYABLE_ID = "prefPlayableId";
    private static final String PREF_SCROLL_Y = "prefScrollY";
    private static final String TAG = "ItemDescriptionFragment";
    private boolean highlightTimecodes;
    private Playable media;
    private boolean saveState;
    private String selectedURL;
    private ShownotesProvider shownotesProvider;
    private Disposable webViewLoader;
    private final OnLongClickListener webViewLongClickListener = new C07852();
    private WebView webvDescription;

    /* renamed from: de.danoeh.antennapod.fragment.ItemDescriptionFragment$1 */
    class C07841 extends WebViewClient {
        C07841() {
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (Timeline.isTimecodeLink(url)) {
                ItemDescriptionFragment.this.onTimecodeLinkSelected(url);
            } else {
                try {
                    ItemDescriptionFragment.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(url)));
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    return true;
                }
            }
            return true;
        }

        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.d(ItemDescriptionFragment.TAG, "Page finished");
            view.postDelayed(new -$$Lambda$ItemDescriptionFragment$1$JcsoZKXg2QRUiBfzzixdHt8UhIc(ItemDescriptionFragment.this), 50);
        }
    }

    /* renamed from: de.danoeh.antennapod.fragment.ItemDescriptionFragment$2 */
    class C07852 implements OnLongClickListener {
        C07852() {
        }

        public boolean onLongClick(View v) {
            HitTestResult r = ItemDescriptionFragment.this.webvDescription.getHitTestResult();
            if (r != null) {
                if (r.getType() == 7) {
                    String str = ItemDescriptionFragment.TAG;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Link of webview was long-pressed. Extra: ");
                    stringBuilder.append(r.getExtra());
                    Log.d(str, stringBuilder.toString());
                    ItemDescriptionFragment.this.selectedURL = r.getExtra();
                    ItemDescriptionFragment.this.webvDescription.showContextMenu();
                    return true;
                }
            }
            ItemDescriptionFragment.this.selectedURL = null;
            return false;
        }
    }

    public static ItemDescriptionFragment newInstance(Playable media, boolean saveState, boolean highlightTimecodes) {
        ItemDescriptionFragment f = new ItemDescriptionFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PLAYABLE, media);
        args.putBoolean(ARG_SAVE_STATE, saveState);
        args.putBoolean(ARG_HIGHLIGHT_TIMECODES, highlightTimecodes);
        f.setArguments(args);
        return f;
    }

    public static ItemDescriptionFragment newInstance(FeedItem item, boolean saveState, boolean highlightTimecodes) {
        ItemDescriptionFragment f = new ItemDescriptionFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_FEEDITEM_ID, item.getId());
        args.putBoolean(ARG_SAVE_STATE, saveState);
        args.putBoolean(ARG_HIGHLIGHT_TIMECODES, highlightTimecodes);
        f.setArguments(args);
        return f;
    }

    @SuppressLint({"NewApi"})
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        boolean black;
        int backgroundColor;
        Log.d(TAG, "Creating view");
        this.webvDescription = new WebView(getActivity().getApplicationContext());
        this.webvDescription.setLayerType(1, null);
        TypedArray ta = getActivity().getTheme().obtainStyledAttributes(new int[]{16842801});
        if (UserPreferences.getTheme() != R.style.Theme.AntennaPod.Dark) {
            if (UserPreferences.getTheme() != R.style.Theme.AntennaPod.TrueBlack) {
                black = false;
                backgroundColor = ta.getColor(0, black ? ViewCompat.MEASURED_STATE_MASK : -1);
                ta.recycle();
                this.webvDescription.setBackgroundColor(backgroundColor);
                if (!NetworkUtils.networkAvailable()) {
                    this.webvDescription.getSettings().setCacheMode(1);
                }
                this.webvDescription.getSettings().setUseWideViewPort(false);
                this.webvDescription.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
                this.webvDescription.getSettings().setLoadWithOverviewMode(true);
                this.webvDescription.setOnLongClickListener(this.webViewLongClickListener);
                this.webvDescription.setWebViewClient(new C07841());
                registerForContextMenu(this.webvDescription);
                return this.webvDescription;
            }
        }
        black = true;
        if (black) {
        }
        backgroundColor = ta.getColor(0, black ? ViewCompat.MEASURED_STATE_MASK : -1);
        ta.recycle();
        this.webvDescription.setBackgroundColor(backgroundColor);
        if (!NetworkUtils.networkAvailable()) {
            this.webvDescription.getSettings().setCacheMode(1);
        }
        this.webvDescription.getSettings().setUseWideViewPort(false);
        this.webvDescription.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        this.webvDescription.getSettings().setLoadWithOverviewMode(true);
        this.webvDescription.setOnLongClickListener(this.webViewLongClickListener);
        this.webvDescription.setWebViewClient(new C07841());
        registerForContextMenu(this.webvDescription);
        return this.webvDescription;
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Fragment destroyed");
        Disposable disposable = this.webViewLoader;
        if (disposable != null) {
            disposable.dispose();
        }
        WebView webView = this.webvDescription;
        if (webView != null) {
            webView.removeAllViews();
            this.webvDescription.destroy();
        }
    }

    @SuppressLint({"NewApi"})
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Creating fragment");
        Bundle args = getArguments();
        this.saveState = args.getBoolean(ARG_SAVE_STATE, false);
        this.highlightTimecodes = args.getBoolean(ARG_HIGHLIGHT_TIMECODES, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        if (args.containsKey(ARG_PLAYABLE)) {
            if (this.media == null) {
                this.media = (Playable) args.getParcelable(ARG_PLAYABLE);
                this.shownotesProvider = this.media;
            }
            load();
        } else if (args.containsKey(ARG_FEEDITEM_ID)) {
            Observable.defer(new -$$Lambda$ItemDescriptionFragment$GzfFCrNltlhhgAVQyW62dUF9Fis(getArguments().getLong(ARG_FEEDITEM_ID))).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new -$$Lambda$ItemDescriptionFragment$koNkIgUKT7oQZ_IpcJMgt0AKbdU(), -$$Lambda$ItemDescriptionFragment$Ro1WH8bHRcAcmGJzV0YgArTIxas.INSTANCE);
        }
    }

    public static /* synthetic */ void lambda$onViewCreated$1(ItemDescriptionFragment itemDescriptionFragment, FeedItem feedItem) throws Exception {
        itemDescriptionFragment.shownotesProvider = feedItem;
        itemDescriptionFragment.load();
    }

    @SuppressLint({"NewApi"})
    public boolean onContextItemSelected(MenuItem item) {
        boolean handled = this.selectedURL != null;
        if (this.selectedURL != null) {
            int itemId = item.getItemId();
            if (itemId == R.id.copy_url_item) {
                ClipData clipData = this.selectedURL;
                ((ClipboardManager) getActivity().getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText(clipData, clipData));
                Toast.makeText(getActivity(), R.string.copied_url_msg, null).show();
            } else if (itemId != R.id.go_to_position_item) {
                if (itemId == R.id.open_in_browser_item) {
                    Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(this.selectedURL));
                    if (IntentUtils.isCallable(getActivity(), intent)) {
                        getActivity().startActivity(intent);
                    }
                } else if (itemId != R.id.share_url_item) {
                    handled = false;
                } else {
                    ShareUtils.shareLink(getActivity(), this.selectedURL);
                }
            } else if (Timeline.isTimecodeLink(this.selectedURL)) {
                onTimecodeLinkSelected(this.selectedURL);
            } else {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Selected go_to_position_item, but URL was no timecode link: ");
                stringBuilder.append(this.selectedURL);
                Log.e(str, stringBuilder.toString());
            }
            this.selectedURL = null;
        }
        return handled;
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        if (this.selectedURL != null) {
            super.onCreateContextMenu(menu, v, menuInfo);
            if (Timeline.isTimecodeLink(this.selectedURL)) {
                menu.add(0, R.id.go_to_position_item, 0, R.string.go_to_position_label);
                menu.setHeaderTitle(Converter.getDurationStringLong(Timeline.getTimecodeLinkTime(this.selectedURL)));
                return;
            }
            if (IntentUtils.isCallable(getActivity(), new Intent("android.intent.action.VIEW", Uri.parse(this.selectedURL)))) {
                menu.add(0, R.id.open_in_browser_item, 0, R.string.open_in_browser_label);
            }
            menu.add(0, R.id.copy_url_item, 0, R.string.copy_url_label);
            menu.add(0, R.id.share_url_item, 0, R.string.share_url_label);
            menu.setHeaderTitle(this.selectedURL);
        }
    }

    private void load() {
        Log.d(TAG, "load()");
        Disposable disposable = this.webViewLoader;
        if (disposable != null) {
            disposable.dispose();
        }
        if (this.shownotesProvider != null) {
            this.webViewLoader = Observable.fromCallable(new -$$Lambda$ItemDescriptionFragment$_4WT8TEnWgDVqAVayeJb4qUkUqU()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new -$$Lambda$ItemDescriptionFragment$-p2LOodGMEuRFQl-quAjcYROY7M(), -$$Lambda$ItemDescriptionFragment$-gZrUn7M7DW6UWFDZuTDdxLXz0A.INSTANCE);
        }
    }

    public static /* synthetic */ void lambda$load$3(ItemDescriptionFragment itemDescriptionFragment, String data) throws Exception {
        itemDescriptionFragment.webvDescription.loadDataWithBaseURL(null, data, "text/html", "utf-8", "about:blank");
        Log.d(TAG, "Webview loaded");
    }

    private String loadData() {
        return new Timeline(getActivity(), this.shownotesProvider).processShownotes(this.highlightTimecodes);
    }

    public void onPause() {
        super.onPause();
        savePreference();
    }

    private void savePreference() {
        if (this.saveState) {
            Log.d(TAG, "Saving preferences");
            Editor editor = getActivity().getSharedPreferences(PREF, 0).edit();
            if (this.media == null || this.webvDescription == null) {
                Log.d(TAG, "savePreferences was called while media or webview was null");
                editor.putInt(PREF_SCROLL_Y, -1);
                editor.putString(PREF_PLAYABLE_ID, "");
            } else {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Saving scroll position: ");
                stringBuilder.append(this.webvDescription.getScrollY());
                Log.d(str, stringBuilder.toString());
                editor.putInt(PREF_SCROLL_Y, this.webvDescription.getScrollY());
                editor.putString(PREF_PLAYABLE_ID, this.media.getIdentifier().toString());
            }
            editor.commit();
        }
    }

    private boolean restoreFromPreference() {
        if (this.saveState) {
            Log.d(TAG, "Restoring from preferences");
            Activity activity = getActivity();
            if (activity != null) {
                SharedPreferences prefs = activity.getSharedPreferences(PREF, 0);
                String id = prefs.getString(PREF_PLAYABLE_ID, "");
                int scrollY = prefs.getInt(PREF_SCROLL_Y, -1);
                if (scrollY != -1) {
                    Playable playable = this.media;
                    if (playable != null) {
                        if (id.equals(playable.getIdentifier().toString()) && this.webvDescription != null) {
                            String str = TAG;
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("Restored scroll Position: ");
                            stringBuilder.append(scrollY);
                            Log.d(str, stringBuilder.toString());
                            WebView webView = this.webvDescription;
                            webView.scrollTo(webView.getScrollX(), scrollY);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private void onTimecodeLinkSelected(String link) {
        int time = Timeline.getTimecodeLinkTime(link);
        if (getActivity() != null && (getActivity() instanceof MediaplayerInfoActivity)) {
            PlaybackController pc = ((MediaplayerInfoActivity) getActivity()).getPlaybackController();
            if (pc != null) {
                pc.seekTo(time);
            }
        }
    }

    public void onMediaChanged(Playable media) {
        if (this.media != media) {
            this.media = media;
            this.shownotesProvider = media;
            if (this.webvDescription != null) {
                load();
            }
        }
    }
}
