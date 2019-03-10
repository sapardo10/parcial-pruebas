package de.danoeh.antennapod.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebView.HitTestResult;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.widget.IconButton;
import de.danoeh.antennapod.activity.CastEnabledActivity;
import de.danoeh.antennapod.activity.MainActivity;
import de.danoeh.antennapod.adapter.DefaultActionButtonCallback;
import de.danoeh.antennapod.core.event.DownloadEvent;
import de.danoeh.antennapod.core.event.DownloaderUpdate;
import de.danoeh.antennapod.core.event.FeedItemEvent;
import de.danoeh.antennapod.core.feed.EventDistributor;
import de.danoeh.antennapod.core.feed.EventDistributor$EventListener;
import de.danoeh.antennapod.core.feed.FeedFile;
import de.danoeh.antennapod.core.feed.FeedItem;
import de.danoeh.antennapod.core.feed.FeedItem.State;
import de.danoeh.antennapod.core.feed.FeedMedia;
import de.danoeh.antennapod.core.glide.ApGlideSettings;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.service.download.Downloader;
import de.danoeh.antennapod.core.service.playback.PlaybackService;
import de.danoeh.antennapod.core.storage.DBReader;
import de.danoeh.antennapod.core.storage.DBTasks;
import de.danoeh.antennapod.core.storage.DBWriter;
import de.danoeh.antennapod.core.storage.DownloadRequester;
import de.danoeh.antennapod.core.util.Converter;
import de.danoeh.antennapod.core.util.DateUtils;
import de.danoeh.antennapod.core.util.Flavors;
import de.danoeh.antennapod.core.util.IntentUtils;
import de.danoeh.antennapod.core.util.LongList;
import de.danoeh.antennapod.core.util.NetworkUtils;
import de.danoeh.antennapod.core.util.ShareUtils;
import de.danoeh.antennapod.core.util.playback.Timeline;
import de.danoeh.antennapod.debug.R;
import de.danoeh.antennapod.menuhandler.FeedItemMenuHandler;
import de.danoeh.antennapod.menuhandler.FeedItemMenuHandler.MenuInterface;
import de.danoeh.antennapod.view.OnSwipeGesture;
import de.danoeh.antennapod.view.SwipeGestureDetector;
import de.greenrobot.event.EventBus;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;

public class ItemFragment extends Fragment implements OnSwipeGesture {
    private static final String ARG_FEEDITEMS = "feeditems";
    private static final String ARG_FEEDITEM_POS = "feeditem_pos";
    private static final int EVENTS = 2;
    private static final String TAG = "ItemFragment";
    private IconButton butAction1;
    private IconButton butAction2;
    private final EventDistributor$EventListener contentUpdate = new C10515();
    private Disposable disposable;
    private List<Downloader> downloaderList;
    private int feedItemPos;
    private long[] feedItems;
    private GestureDetectorCompat headerGestureDetector;
    private ImageView imgvCover;
    private FeedItem item;
    private boolean itemsLoaded = false;
    private Menu popupMenu;
    private final MenuInterface popupMenuInterface = new C10503();
    private ProgressBar progbarDownload;
    private ProgressBar progbarLoading;
    private ViewGroup root;
    private String selectedURL;
    private TextView txtvDuration;
    private TextView txtvPodcast;
    private TextView txtvPublished;
    private TextView txtvTitle;
    private final OnLongClickListener webViewLongClickListener = new C07874();
    private WebView webvDescription;
    private String webviewData;
    private GestureDetectorCompat webviewGestureDetector;

    /* renamed from: de.danoeh.antennapod.fragment.ItemFragment$2 */
    class C07862 extends WebViewClient {
        C07862() {
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(url));
            if (IntentUtils.isCallable(ItemFragment.this.getActivity(), intent)) {
                ItemFragment.this.startActivity(intent);
            }
            return true;
        }
    }

    /* renamed from: de.danoeh.antennapod.fragment.ItemFragment$4 */
    class C07874 implements OnLongClickListener {
        C07874() {
        }

        public boolean onLongClick(View v) {
            HitTestResult r = ItemFragment.this.webvDescription.getHitTestResult();
            if (r != null) {
                if (r.getType() == 7) {
                    String str = ItemFragment.TAG;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Link of webview was long-pressed. Extra: ");
                    stringBuilder.append(r.getExtra());
                    Log.d(str, stringBuilder.toString());
                    ItemFragment.this.selectedURL = r.getExtra();
                    ItemFragment.this.webvDescription.showContextMenu();
                    return true;
                }
            }
            ItemFragment.this.selectedURL = null;
            return false;
        }
    }

    /* renamed from: de.danoeh.antennapod.fragment.ItemFragment$3 */
    class C10503 implements MenuInterface {
        C10503() {
        }

        public void setItemVisibility(int id, boolean visible) {
            MenuItem item = ItemFragment.this.popupMenu.findItem(id);
            if (item != null) {
                item.setVisible(visible);
            }
        }
    }

    /* renamed from: de.danoeh.antennapod.fragment.ItemFragment$5 */
    class C10515 extends EventDistributor$EventListener {
        C10515() {
        }

        public void update(EventDistributor eventDistributor, Integer arg) {
            if ((arg.intValue() & 2) != 0) {
                ItemFragment.this.load();
            }
        }
    }

    public static ItemFragment newInstance(long feeditem) {
        return newInstance(new long[]{feeditem}, 0);
    }

    public static ItemFragment newInstance(long[] feeditems, int feedItemPos) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putLongArray(ARG_FEEDITEMS, feeditems);
        args.putInt(ARG_FEEDITEM_POS, feedItemPos);
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        this.feedItems = getArguments().getLongArray(ARG_FEEDITEMS);
        this.feedItemPos = getArguments().getInt(ARG_FEEDITEM_POS);
        this.headerGestureDetector = new GestureDetectorCompat(getActivity(), new SwipeGestureDetector(this));
        this.webviewGestureDetector = new GestureDetectorCompat(getActivity(), new SwipeGestureDetector(this) {
            public boolean onDown(MotionEvent e) {
                return false;
            }
        });
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View layout = inflater.inflate(R.layout.feeditem_fragment, container, false);
        this.root = (ViewGroup) layout.findViewById(R.id.content_root);
        LinearLayout header = (LinearLayout) this.root.findViewById(R.id.header);
        if (this.feedItems.length > 0) {
            header.setOnTouchListener(new -$$Lambda$ItemFragment$hTGNaG2RdHKEfdW98HAiV9sVlMA());
        }
        this.txtvPodcast = (TextView) layout.findViewById(R.id.txtvPodcast);
        this.txtvPodcast.setOnClickListener(new -$$Lambda$ItemFragment$FXoMEWWGTWpZWTmWtBI_QDxTNKk());
        this.txtvTitle = (TextView) layout.findViewById(R.id.txtvTitle);
        if (VERSION.SDK_INT >= 23) {
            this.txtvTitle.setHyphenationFrequency(2);
        }
        this.txtvDuration = (TextView) layout.findViewById(R.id.txtvDuration);
        this.txtvPublished = (TextView) layout.findViewById(R.id.txtvPublished);
        if (VERSION.SDK_INT >= 14) {
            this.txtvTitle.setEllipsize(TruncateAt.END);
        }
        this.webvDescription = (WebView) layout.findViewById(R.id.webvDescription);
        if (UserPreferences.getTheme() != R.style.Theme.AntennaPod.Dark) {
            if (UserPreferences.getTheme() != R.style.Theme.AntennaPod.TrueBlack) {
                if (!NetworkUtils.networkAvailable()) {
                    this.webvDescription.getSettings().setCacheMode(1);
                }
                this.webvDescription.getSettings().setUseWideViewPort(false);
                this.webvDescription.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
                this.webvDescription.getSettings().setLoadWithOverviewMode(true);
                if (this.feedItems.length > 0) {
                    this.webvDescription.setOnLongClickListener(this.webViewLongClickListener);
                }
                this.webvDescription.setOnTouchListener(new -$$Lambda$ItemFragment$Z63z4moTPcWXXZWlGQ4J4hgZWR8());
                this.webvDescription.setWebViewClient(new C07862());
                registerForContextMenu(this.webvDescription);
                this.imgvCover = (ImageView) layout.findViewById(R.id.imgvCover);
                this.imgvCover.setOnClickListener(new -$$Lambda$ItemFragment$-f7AHcp6C5wyfzvQaCLUtPyjnQg());
                this.progbarDownload = (ProgressBar) layout.findViewById(R.id.progbarDownload);
                this.progbarLoading = (ProgressBar) layout.findViewById(R.id.progbarLoading);
                this.butAction1 = (IconButton) layout.findViewById(R.id.butAction1);
                this.butAction2 = (IconButton) layout.findViewById(R.id.butAction2);
                this.butAction1.setOnClickListener(new -$$Lambda$ItemFragment$6EQCf35FoYVPvPZWekKZ-4ZsJcs());
                this.butAction2.setOnClickListener(new -$$Lambda$ItemFragment$4T9VBTo3iYEfm-LoVPjD27av29s());
                return layout;
            }
        }
        if (VERSION.SDK_INT <= 15) {
            this.webvDescription.setLayerType(1, null);
        }
        this.webvDescription.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.black));
        if (!NetworkUtils.networkAvailable()) {
            this.webvDescription.getSettings().setCacheMode(1);
        }
        this.webvDescription.getSettings().setUseWideViewPort(false);
        this.webvDescription.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
        this.webvDescription.getSettings().setLoadWithOverviewMode(true);
        if (this.feedItems.length > 0) {
            this.webvDescription.setOnLongClickListener(this.webViewLongClickListener);
        }
        this.webvDescription.setOnTouchListener(new -$$Lambda$ItemFragment$Z63z4moTPcWXXZWlGQ4J4hgZWR8());
        this.webvDescription.setWebViewClient(new C07862());
        registerForContextMenu(this.webvDescription);
        this.imgvCover = (ImageView) layout.findViewById(R.id.imgvCover);
        this.imgvCover.setOnClickListener(new -$$Lambda$ItemFragment$-f7AHcp6C5wyfzvQaCLUtPyjnQg());
        this.progbarDownload = (ProgressBar) layout.findViewById(R.id.progbarDownload);
        this.progbarLoading = (ProgressBar) layout.findViewById(R.id.progbarLoading);
        this.butAction1 = (IconButton) layout.findViewById(R.id.butAction1);
        this.butAction2 = (IconButton) layout.findViewById(R.id.butAction2);
        this.butAction1.setOnClickListener(new -$$Lambda$ItemFragment$6EQCf35FoYVPvPZWekKZ-4ZsJcs());
        this.butAction2.setOnClickListener(new -$$Lambda$ItemFragment$4T9VBTo3iYEfm-LoVPjD27av29s());
        return layout;
    }

    public static /* synthetic */ void lambda$onCreateView$4(ItemFragment itemFragment, View v) {
        if (itemFragment.item != null) {
            LongList of;
            DefaultActionButtonCallback actionButtonCallback = new DefaultActionButtonCallback(itemFragment.getActivity());
            FeedItem feedItem = itemFragment.item;
            if (feedItem.isTagged(FeedItem.TAG_QUEUE)) {
                of = LongList.of(itemFragment.item.getId());
            } else {
                of = new LongList(0);
            }
            actionButtonCallback.onActionButtonPressed(feedItem, of);
            FeedMedia media = itemFragment.item.getMedia();
            if (media != null && media.isDownloaded()) {
                ((MainActivity) itemFragment.getActivity()).dismissChildFragment();
            }
        }
    }

    public static /* synthetic */ void lambda$onCreateView$5(ItemFragment itemFragment, View v) {
        FeedItem feedItem = itemFragment.item;
        if (feedItem != null) {
            if (feedItem.hasMedia()) {
                FeedMedia media = itemFragment.item.getMedia();
                if (media.isDownloaded()) {
                    DBWriter.deleteFeedMediaOfItem(itemFragment.getActivity(), media.getId());
                } else {
                    DBTasks.playMedia(itemFragment.getActivity(), media, true, true, true);
                    ((MainActivity) itemFragment.getActivity()).dismissChildFragment();
                }
            } else if (itemFragment.item.getLink() != null) {
                itemFragment.getActivity().startActivity(new Intent("android.intent.action.VIEW", Uri.parse(itemFragment.item.getLink())));
            }
        }
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        load();
    }

    public void onResume() {
        super.onResume();
        EventDistributor.getInstance().register(this.contentUpdate);
        EventBus.getDefault().registerSticky(this);
        if (this.itemsLoaded) {
            this.progbarLoading.setVisibility(8);
            updateAppearance();
        }
    }

    public void onPause() {
        super.onPause();
        EventDistributor.getInstance().unregister(this.contentUpdate);
        EventBus.getDefault().unregister(this);
    }

    public void onDestroyView() {
        super.onDestroyView();
        Disposable disposable = this.disposable;
        if (disposable != null) {
            disposable.dispose();
        }
        View view = this.webvDescription;
        if (view != null) {
            ViewGroup viewGroup = this.root;
            if (viewGroup != null) {
                viewGroup.removeView(view);
                this.webvDescription.destroy();
            }
        }
    }

    public boolean onSwipeLeftToRight() {
        Log.d(TAG, "onSwipeLeftToRight()");
        this.feedItemPos--;
        if (this.feedItemPos < 0) {
            this.feedItemPos = this.feedItems.length - 1;
        }
        load();
        return true;
    }

    public boolean onSwipeRightToLeft() {
        Log.d(TAG, "onSwipeRightToLeft()");
        this.feedItemPos = (this.feedItemPos + 1) % this.feedItems.length;
        load();
        return true;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (isAdded()) {
            if (this.item != null) {
                super.onCreateOptionsMenu(menu, inflater);
                if (Flavors.FLAVOR == Flavors.PLAY) {
                    ((CastEnabledActivity) getActivity()).requestCastButton(2);
                }
                inflater.inflate(R.menu.feeditem_options, menu);
                this.popupMenu = menu;
                if (this.item.hasMedia()) {
                    FeedItemMenuHandler.onPrepareMenu(this.popupMenuInterface, this.item, true, null);
                } else {
                    FeedItemMenuHandler.onPrepareMenu(this.popupMenuInterface, this.item, true, null, R.id.mark_read_item, R.id.visit_website_item);
                }
            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != R.id.open_podcast) {
            return FeedItemMenuHandler.onMenuItemClicked(getActivity(), menuItem.getItemId(), this.item);
        }
        openPodcast();
        return true;
    }

    private void onFragmentLoaded() {
        String str = this.webviewData;
        if (str != null) {
            this.webvDescription.loadDataWithBaseURL(null, str, "text/html", "utf-8", "about:blank");
        }
        updateAppearance();
    }

    private void updateAppearance() {
        if (this.item == null) {
            Log.d(TAG, "updateAppearance item is null");
            return;
        }
        FeedFile media;
        String butAction1Icon;
        int butAction1Text;
        String butAction2Icon;
        int butAction2Text;
        boolean isDownloading;
        State state;
        getActivity().supportInvalidateOptionsMenu();
        this.txtvPodcast.setText(this.item.getFeed().getTitle());
        this.txtvTitle.setText(this.item.getTitle());
        if (this.item.getPubDate() != null) {
            this.txtvPublished.setText(DateUtils.formatAbbrev(getActivity(), this.item.getPubDate()));
        }
        Glide.with(getActivity()).load(this.item.getImageLocation()).apply(new RequestOptions().placeholder((int) R.color.light_gray).error((int) R.color.light_gray).diskCacheStrategy(ApGlideSettings.AP_DISK_CACHE_STRATEGY).fitCenter().dontAnimate()).into(this.imgvCover);
        this.progbarDownload.setVisibility(8);
        if (this.item.hasMedia()) {
            List<Downloader> list = this.downloaderList;
            if (list != null) {
                for (Downloader downloader : list) {
                    if (downloader.getDownloadRequest().getFeedfileType() == 2) {
                        if (downloader.getDownloadRequest().getFeedfileId() == this.item.getMedia().getId()) {
                            this.progbarDownload.setVisibility(0);
                            this.progbarDownload.setProgress(downloader.getDownloadRequest().getProgressPercent());
                        }
                    }
                }
                media = this.item.getMedia();
                butAction1Icon = null;
                butAction1Text = 0;
                butAction2Icon = null;
                butAction2Text = 0;
                if (media != null) {
                    if (!this.item.isPlayed()) {
                        butAction1Icon = "{fa-check 24sp}";
                        butAction1Text = R.string.mark_read_label;
                    }
                    if (this.item.getLink() != null) {
                        butAction2Icon = "{md-web 24sp}";
                        butAction2Text = R.string.visit_website_label;
                    }
                } else {
                    if (media.getDuration() > 0) {
                        this.txtvDuration.setText(Converter.getDurationStringLong(media.getDuration()));
                    }
                    isDownloading = DownloadRequester.getInstance().isDownloadingFile(media);
                    if (media.isDownloaded()) {
                        butAction2Icon = "{md-settings-input-antenna 24sp}";
                        butAction2Text = R.string.stream_label;
                    } else {
                        butAction2Icon = "{md-delete 24sp}";
                        butAction2Text = R.string.delete_label;
                    }
                    if (isDownloading) {
                        butAction1Icon = "{md-cancel 24sp}";
                        butAction1Text = R.string.cancel_label;
                    } else if (media.isDownloaded()) {
                        butAction1Icon = "{md-file-download 24sp}";
                        butAction1Text = R.string.download_label;
                    } else {
                        butAction1Icon = "{md-play-arrow 24sp}";
                        butAction1Text = R.string.play_label;
                    }
                }
                state = this.item.getState();
                if (butAction2Text != R.string.delete_label && state == State.PLAYING && PlaybackService.isRunning) {
                    this.butAction2.setEnabled(false);
                    this.butAction2.setAlpha(0.5f);
                } else {
                    this.butAction2.setEnabled(true);
                    this.butAction2.setAlpha(1.0f);
                }
                if (butAction1Icon != null || butAction1Text == 0) {
                    this.butAction1.setVisibility(4);
                } else {
                    IconButton iconButton = this.butAction1;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(butAction1Icon);
                    stringBuilder.append("  ");
                    stringBuilder.append(getActivity().getString(butAction1Text));
                    iconButton.setText(stringBuilder.toString());
                    Iconify.addIcons(new TextView[]{this.butAction1});
                    this.butAction1.setVisibility(0);
                }
                if (butAction2Icon != null || butAction2Text == 0) {
                    this.butAction2.setVisibility(4);
                } else {
                    IconButton iconButton2 = this.butAction2;
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(butAction2Icon);
                    stringBuilder2.append("  ");
                    stringBuilder2.append(getActivity().getString(butAction2Text));
                    iconButton2.setText(stringBuilder2.toString());
                    Iconify.addIcons(new TextView[]{this.butAction2});
                    this.butAction2.setVisibility(0);
                }
            }
        }
        media = this.item.getMedia();
        butAction1Icon = null;
        butAction1Text = 0;
        butAction2Icon = null;
        butAction2Text = 0;
        if (media != null) {
            if (media.getDuration() > 0) {
                this.txtvDuration.setText(Converter.getDurationStringLong(media.getDuration()));
            }
            isDownloading = DownloadRequester.getInstance().isDownloadingFile(media);
            if (media.isDownloaded()) {
                butAction2Icon = "{md-delete 24sp}";
                butAction2Text = R.string.delete_label;
            } else {
                butAction2Icon = "{md-settings-input-antenna 24sp}";
                butAction2Text = R.string.stream_label;
            }
            if (isDownloading) {
                butAction1Icon = "{md-cancel 24sp}";
                butAction1Text = R.string.cancel_label;
            } else if (media.isDownloaded()) {
                butAction1Icon = "{md-file-download 24sp}";
                butAction1Text = R.string.download_label;
            } else {
                butAction1Icon = "{md-play-arrow 24sp}";
                butAction1Text = R.string.play_label;
            }
        } else {
            if (!this.item.isPlayed()) {
                butAction1Icon = "{fa-check 24sp}";
                butAction1Text = R.string.mark_read_label;
            }
            if (this.item.getLink() != null) {
                butAction2Icon = "{md-web 24sp}";
                butAction2Text = R.string.visit_website_label;
            }
        }
        state = this.item.getState();
        if (butAction2Text != R.string.delete_label) {
        }
        this.butAction2.setEnabled(true);
        this.butAction2.setAlpha(1.0f);
        if (butAction1Icon != null) {
        }
        this.butAction1.setVisibility(4);
        if (butAction2Icon != null) {
        }
        this.butAction2.setVisibility(4);
    }

    public boolean onContextItemSelected(MenuItem item) {
        boolean handled = this.selectedURL != null;
        if (this.selectedURL != null) {
            int itemId = item.getItemId();
            if (itemId == R.id.copy_url_item) {
                ClipData clipData = this.selectedURL;
                ((ClipboardManager) getActivity().getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText(clipData, clipData));
                Toast.makeText(getActivity(), R.string.copied_url_msg, null).show();
            } else if (itemId == R.id.open_in_browser_item) {
                Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(this.selectedURL));
                if (IntentUtils.isCallable(getActivity(), intent)) {
                    getActivity().startActivity(intent);
                }
            } else if (itemId != R.id.share_url_item) {
                handled = false;
            } else {
                ShareUtils.shareLink(getActivity(), this.selectedURL);
            }
            this.selectedURL = null;
        }
        return handled;
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        if (this.selectedURL != null) {
            super.onCreateContextMenu(menu, v, menuInfo);
            if (IntentUtils.isCallable(getActivity(), new Intent("android.intent.action.VIEW", Uri.parse(this.selectedURL)))) {
                menu.add(0, R.id.open_in_browser_item, 0, R.string.open_in_browser_label);
            }
            menu.add(0, R.id.copy_url_item, 0, R.string.copy_url_label);
            menu.add(0, R.id.share_url_item, 0, R.string.share_url_label);
            menu.setHeaderTitle(this.selectedURL);
        }
    }

    private void openPodcast() {
        ((MainActivity) getActivity()).loadChildFragment(ItemlistFragment.newInstance(this.item.getFeedId()));
    }

    public void onEventMainThread(FeedItemEvent event) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("onEventMainThread() called with: event = [");
        stringBuilder.append(event);
        stringBuilder.append("]");
        Log.d(str, stringBuilder.toString());
        for (FeedItem item : event.items) {
            if (this.feedItems[this.feedItemPos] == item.getId()) {
                load();
                return;
            }
        }
    }

    public void onEventMainThread(DownloadEvent event) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("onEventMainThread() called with: event = [");
        stringBuilder.append(event);
        stringBuilder.append("]");
        Log.d(str, stringBuilder.toString());
        DownloaderUpdate update = event.update;
        this.downloaderList = update.downloaders;
        FeedItem feedItem = this.item;
        if (feedItem != null) {
            if (feedItem.getMedia() != null) {
                if (ArrayUtils.contains(update.mediaIds, this.item.getMedia().getId())) {
                    if (this.itemsLoaded && getActivity() != null) {
                        updateAppearance();
                    }
                }
            }
        }
    }

    private void load() {
        Disposable disposable = this.disposable;
        if (disposable != null) {
            disposable.dispose();
        }
        this.progbarLoading.setVisibility(0);
        this.disposable = Observable.fromCallable(new -$$Lambda$ItemFragment$zZoO0Jhe3_MmwfJvvjCAMENerGQ()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new -$$Lambda$ItemFragment$Dt1yjnZ_FSGw3i0jmjhW0Q-RHIU(), -$$Lambda$ItemFragment$0l-HpH3wvpcjx9eUhcncC3vEQMk.INSTANCE);
    }

    public static /* synthetic */ void lambda$load$6(ItemFragment itemFragment, FeedItem result) throws Exception {
        itemFragment.progbarLoading.setVisibility(8);
        itemFragment.item = result;
        itemFragment.itemsLoaded = true;
        itemFragment.onFragmentLoaded();
    }

    private FeedItem loadInBackground() {
        FeedItem feedItem = DBReader.getFeedItem(this.feedItems[this.feedItemPos]);
        if (feedItem != null) {
            this.webviewData = new Timeline(getActivity(), feedItem).processShownotes(false);
        }
        return feedItem;
    }
}
