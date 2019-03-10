package de.danoeh.antennapod.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.LightingColorFilter;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.widget.IconTextView;
import de.danoeh.antennapod.activity.FeedInfoActivity;
import de.danoeh.antennapod.activity.FeedSettingsActivity;
import de.danoeh.antennapod.activity.MainActivity;
import de.danoeh.antennapod.adapter.DefaultActionButtonCallback;
import de.danoeh.antennapod.adapter.FeedItemlistAdapter;
import de.danoeh.antennapod.adapter.FeedItemlistAdapter.ItemAccess;
import de.danoeh.antennapod.core.asynctask.FeedRemover;
import de.danoeh.antennapod.core.dialog.ConfirmationDialog;
import de.danoeh.antennapod.core.dialog.DownloadRequestErrorDialogCreator;
import de.danoeh.antennapod.core.event.DownloadEvent;
import de.danoeh.antennapod.core.event.DownloaderUpdate;
import de.danoeh.antennapod.core.event.FeedItemEvent;
import de.danoeh.antennapod.core.feed.EventDistributor;
import de.danoeh.antennapod.core.feed.EventDistributor$EventListener;
import de.danoeh.antennapod.core.feed.Feed;
import de.danoeh.antennapod.core.feed.FeedEvent;
import de.danoeh.antennapod.core.feed.FeedItem;
import de.danoeh.antennapod.core.glide.ApGlideSettings;
import de.danoeh.antennapod.core.glide.FastBlurTransformation;
import de.danoeh.antennapod.core.menuhandler.MenuItemUtils.UpdateRefreshMenuItemChecker;
import de.danoeh.antennapod.core.service.download.DownloadService;
import de.danoeh.antennapod.core.service.download.Downloader;
import de.danoeh.antennapod.core.storage.DBReader;
import de.danoeh.antennapod.core.storage.DBTasks;
import de.danoeh.antennapod.core.storage.DownloadRequestException;
import de.danoeh.antennapod.core.storage.DownloadRequester;
import de.danoeh.antennapod.core.util.FeedItemUtil;
import de.danoeh.antennapod.core.util.LongList;
import de.danoeh.antennapod.core.util.gui.MoreContentListFooterUtil;
import de.danoeh.antennapod.debug.R;
import de.danoeh.antennapod.dialog.EpisodesApplyActionFragment;
import de.danoeh.antennapod.dialog.RenameFeedDialog;
import de.danoeh.antennapod.menuhandler.FeedItemMenuHandler;
import de.danoeh.antennapod.menuhandler.FeedItemMenuHandler.MenuInterface;
import de.danoeh.antennapod.menuhandler.FeedMenuHandler;
import de.danoeh.antennapod.menuhandler.MenuItemUtils;
import de.greenrobot.event.EventBus;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import org.apache.commons.lang3.Validate;

@SuppressLint({"ValidFragment"})
public class ItemlistFragment extends ListFragment {
    private static final String ARGUMENT_FEED_ID = "argument.de.danoeh.antennapod.feed_id";
    private static final int EVENTS = 131;
    public static final String EXTRA_SELECTED_FEEDITEM = "extra.de.danoeh.antennapod.activity.selected_feeditem";
    private static final String TAG = "ItemlistFragment";
    private FeedItemlistAdapter adapter;
    private final EventDistributor$EventListener contentUpdate = new C10576();
    private ContextMenu contextMenu;
    private final MenuInterface contextMenuInterface = new C10565();
    private Disposable disposable;
    private List<Downloader> downloaderList;
    private Feed feed;
    private long feedID;
    private boolean headerCreated = false;
    private ImageView imgvBackground;
    private ImageView imgvCover;
    private boolean isUpdatingFeed;
    private final ItemAccess itemAccess = new C10587();
    private boolean itemsLoaded = false;
    private AdapterContextMenuInfo lastMenuInfo = null;
    private MoreContentListFooterUtil listFooter;
    private IconTextView txtvFailure;
    private TextView txtvInformation;
    private TextView txtvTitle;
    private final UpdateRefreshMenuItemChecker updateRefreshMenuItemChecker = new C10521();
    private boolean viewsCreated = false;

    /* renamed from: de.danoeh.antennapod.fragment.ItemlistFragment$1 */
    class C10521 implements UpdateRefreshMenuItemChecker {
        C10521() {
        }

        public boolean isRefreshing() {
            return ItemlistFragment.this.feed != null && DownloadService.isRunning && DownloadRequester.getInstance().isDownloadingFile(ItemlistFragment.this.feed);
        }
    }

    /* renamed from: de.danoeh.antennapod.fragment.ItemlistFragment$5 */
    class C10565 implements MenuInterface {
        C10565() {
        }

        public void setItemVisibility(int id, boolean visible) {
            if (ItemlistFragment.this.contextMenu != null) {
                MenuItem item = ItemlistFragment.this.contextMenu.findItem(id);
                if (item != null) {
                    item.setVisible(visible);
                }
            }
        }
    }

    /* renamed from: de.danoeh.antennapod.fragment.ItemlistFragment$6 */
    class C10576 extends EventDistributor$EventListener {
        C10576() {
        }

        public void update(EventDistributor eventDistributor, Integer arg) {
            if ((arg.intValue() & ItemlistFragment.EVENTS) != 0) {
                String str = ItemlistFragment.TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Received contentUpdate Intent. arg ");
                stringBuilder.append(arg);
                Log.d(str, stringBuilder.toString());
                ItemlistFragment.this.refreshHeaderView();
                ItemlistFragment.this.loadItems();
                ItemlistFragment.this.updateProgressBarVisibility();
            }
        }
    }

    /* renamed from: de.danoeh.antennapod.fragment.ItemlistFragment$7 */
    class C10587 implements ItemAccess {
        C10587() {
        }

        public FeedItem getItem(int position) {
            if (ItemlistFragment.this.feed == null || position < 0 || position >= ItemlistFragment.this.feed.getNumOfItems()) {
                return null;
            }
            return ItemlistFragment.this.feed.getItemAtIndex(position);
        }

        public LongList getQueueIds() {
            LongList queueIds = new LongList();
            if (ItemlistFragment.this.feed == null) {
                return queueIds;
            }
            for (FeedItem item : ItemlistFragment.this.feed.getItems()) {
                if (item.isTagged(FeedItem.TAG_QUEUE)) {
                    queueIds.add(item.getId());
                }
            }
            return queueIds;
        }

        public int getCount() {
            return ItemlistFragment.this.feed != null ? ItemlistFragment.this.feed.getNumOfItems() : 0;
        }

        public int getItemDownloadProgressPercent(FeedItem item) {
            if (ItemlistFragment.this.downloaderList != null) {
                for (Downloader downloader : ItemlistFragment.this.downloaderList) {
                    if (downloader.getDownloadRequest().getFeedfileType() == 2) {
                        if (downloader.getDownloadRequest().getFeedfileId() == item.getMedia().getId()) {
                            return downloader.getDownloadRequest().getProgressPercent();
                        }
                    }
                }
            }
            return 0;
        }
    }

    public static ItemlistFragment newInstance(long feedId) {
        ItemlistFragment i = new ItemlistFragment();
        Bundle b = new Bundle();
        b.putLong(ARGUMENT_FEED_ID, feedId);
        i.setArguments(b);
        return i;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        Bundle args = getArguments();
        Validate.notNull(args);
        this.feedID = args.getLong(ARGUMENT_FEED_ID);
    }

    public void onStart() {
        super.onStart();
        if (this.viewsCreated && this.itemsLoaded) {
            onFragmentLoaded();
        }
    }

    public void onResume() {
        super.onResume();
        EventDistributor.getInstance().register(this.contentUpdate);
        EventBus.getDefault().registerSticky(this);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle((CharSequence) "");
        updateProgressBarVisibility();
        loadItems();
    }

    public void onPause() {
        super.onPause();
        EventDistributor.getInstance().unregister(this.contentUpdate);
        EventBus.getDefault().unregister(this);
        Disposable disposable = this.disposable;
        if (disposable != null) {
            disposable.dispose();
        }
    }

    public void onDestroyView() {
        super.onDestroyView();
        resetViewState();
    }

    private void resetViewState() {
        this.adapter = null;
        this.viewsCreated = false;
        this.listFooter = null;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (isAdded()) {
            super.onCreateOptionsMenu(menu, inflater);
            if (this.itemsLoaded) {
                FeedMenuHandler.onCreateOptionsMenu(inflater, menu);
                final SearchView sv = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
                MenuItemUtils.adjustTextColor(getActivity(), sv);
                sv.setQueryHint(getString(R.string.search_hint));
                sv.setOnQueryTextListener(new OnQueryTextListener() {
                    public boolean onQueryTextSubmit(String s) {
                        sv.clearFocus();
                        if (ItemlistFragment.this.itemsLoaded) {
                            ((MainActivity) ItemlistFragment.this.getActivity()).loadChildFragment(SearchFragment.newInstance(s, ItemlistFragment.this.feed.getId()));
                        }
                        return true;
                    }

                    public boolean onQueryTextChange(String s) {
                        return false;
                    }
                });
                Feed feed = this.feed;
                if (feed != null) {
                    if (feed.getLink() != null) {
                        this.isUpdatingFeed = de.danoeh.antennapod.core.menuhandler.MenuItemUtils.updateRefreshMenuItem(menu, R.id.refresh_item, this.updateRefreshMenuItemChecker);
                    }
                }
                menu.findItem(R.id.share_link_item).setVisible(false);
                menu.findItem(R.id.visit_website_item).setVisible(false);
                this.isUpdatingFeed = de.danoeh.antennapod.core.menuhandler.MenuItemUtils.updateRefreshMenuItem(menu, R.id.refresh_item, this.updateRefreshMenuItemChecker);
            }
        }
    }

    public void onPrepareOptionsMenu(Menu menu) {
        if (this.itemsLoaded) {
            FeedMenuHandler.onPrepareOptionsMenu(menu, this.feed);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (super.onOptionsItemSelected(item)) {
            return true;
        }
        try {
            if (FeedMenuHandler.onOptionsItemClicked(getActivity(), item, this.feed)) {
                return true;
            }
            int itemId = item.getItemId();
            if (itemId != R.id.episode_actions) {
                switch (itemId) {
                    case R.id.remove_item:
                        final FeedRemover remover = new FeedRemover(getActivity(), this.feed) {
                            protected void onPostExecute(Void result) {
                                super.onPostExecute(result);
                                ((MainActivity) ItemlistFragment.this.getActivity()).loadFragment(EpisodesFragment.TAG, null);
                            }
                        };
                        new ConfirmationDialog(getActivity(), R.string.remove_feed_label, getString(R.string.feed_delete_confirmation_msg, this.feed.getTitle())) {
                            public void onConfirmButtonPressed(DialogInterface dialog) {
                                dialog.dismiss();
                                remover.executeAsync();
                            }
                        }.createNewDialog().show();
                        return true;
                    case R.id.rename_item:
                        new RenameFeedDialog(getActivity(), this.feed).show();
                        return true;
                    default:
                        return false;
                }
            }
            ((MainActivity) getActivity()).loadChildFragment(EpisodesApplyActionFragment.newInstance(this.feed.getItems()));
            return true;
        } catch (DownloadRequestException e) {
            e.printStackTrace();
            DownloadRequestErrorDialogCreator.newRequestErrorDialog(getActivity(), e.getMessage());
            return true;
        }
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        FeedItem item = this.itemAccess.getItem(((AdapterContextMenuInfo) menuInfo).position - 1);
        getActivity().getMenuInflater().inflate(R.menu.feeditemlist_context, menu);
        if (item != null) {
            menu.setHeaderTitle(item.getTitle());
        }
        this.contextMenu = menu;
        this.lastMenuInfo = (AdapterContextMenuInfo) menuInfo;
        FeedItemMenuHandler.onPrepareMenu(this.contextMenuInterface, item, true, null);
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
        if (menuInfo == null) {
            menuInfo = this.lastMenuInfo;
        }
        FeedItem selectedItem = this.itemAccess.getItem(menuInfo.position - 1);
        if (selectedItem != null) {
            return FeedItemMenuHandler.onMenuItemClicked(getActivity(), item.getItemId(), selectedItem);
        }
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Selected item at position ");
        stringBuilder.append(menuInfo.position);
        stringBuilder.append(" was null, ignoring selection");
        Log.i(str, stringBuilder.toString());
        return super.onContextItemSelected(item);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        registerForContextMenu(getListView());
        this.viewsCreated = true;
        if (this.itemsLoaded) {
            onFragmentLoaded();
        }
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        if (this.adapter != null) {
            MainActivity activity = (MainActivity) getActivity();
            activity.loadChildFragment(ItemFragment.newInstance(FeedItemUtil.getIds(this.feed.getItems()), position - l.getHeaderViewsCount()));
            activity.getSupportActionBar().setTitle(this.feed.getTitle());
        }
    }

    public void onEvent(FeedEvent event) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("onEvent() called with: event = [");
        stringBuilder.append(event);
        stringBuilder.append("]");
        Log.d(str, stringBuilder.toString());
        if (event.feedId == this.feedID) {
            loadItems();
        }
    }

    public void onEventMainThread(FeedItemEvent event) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("onEventMainThread() called with: event = [");
        stringBuilder.append(event);
        stringBuilder.append("]");
        Log.d(str, stringBuilder.toString());
        Feed feed = this.feed;
        if (!(feed == null || feed.getItems() == null)) {
            if (this.adapter != null) {
                for (FeedItem item : event.items) {
                    if (FeedItemUtil.indexOfItemWithId(this.feed.getItems(), item.getId()) >= 0) {
                        loadItems();
                        return;
                    }
                }
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
        if (this.isUpdatingFeed != (event.update.feedIds.length > 0)) {
            updateProgressBarVisibility();
        }
        if (this.adapter != null && update.mediaIds.length > 0) {
            this.adapter.notifyDataSetChanged();
        }
    }

    private void updateProgressBarVisibility() {
        if (this.isUpdatingFeed != this.updateRefreshMenuItemChecker.isRefreshing()) {
            getActivity().supportInvalidateOptionsMenu();
        }
        MoreContentListFooterUtil moreContentListFooterUtil = this.listFooter;
        if (moreContentListFooterUtil != null) {
            moreContentListFooterUtil.setLoadingState(DownloadRequester.getInstance().isDownloadingFeeds());
        }
    }

    private void onFragmentLoaded() {
        if (isVisible()) {
            if (this.adapter == null) {
                setListAdapter(null);
                setupHeaderView();
                setupFooterView();
                this.adapter = new FeedItemlistAdapter(getActivity(), this.itemAccess, new DefaultActionButtonCallback(getActivity()), false, true);
                setListAdapter(this.adapter);
            }
            refreshHeaderView();
            setListShown(true);
            this.adapter.notifyDataSetChanged();
            getActivity().supportInvalidateOptionsMenu();
            Feed feed = this.feed;
            if (feed != null && feed.getNextPageLink() == null && this.listFooter != null) {
                getListView().removeFooterView(this.listFooter.getRoot());
            }
        }
    }

    private void refreshHeaderView() {
        if (!(getListView() == null || this.feed == null)) {
            if (this.headerCreated) {
                loadFeedImage();
                if (this.feed.hasLastUpdateFailed()) {
                    this.txtvFailure.setVisibility(0);
                } else {
                    this.txtvFailure.setVisibility(8);
                }
                this.txtvTitle.setText(this.feed.getTitle());
                if (this.feed.getItemFilter() == null) {
                    this.txtvInformation.setVisibility(8);
                } else if (this.feed.getItemFilter().getValues().length > 0) {
                    if (this.feed.hasLastUpdateFailed()) {
                        ((LayoutParams) this.txtvInformation.getLayoutParams()).addRule(3, R.id.txtvFailure);
                    }
                    TextView textView = this.txtvInformation;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("{fa-info-circle} ");
                    stringBuilder.append(getString(R.string.filtered_label));
                    textView.setText(stringBuilder.toString());
                    Iconify.addIcons(new TextView[]{this.txtvInformation});
                    this.txtvInformation.setVisibility(0);
                } else {
                    this.txtvInformation.setVisibility(8);
                }
                return;
            }
        }
        Log.e(TAG, "Unable to refresh header view");
    }

    private void setupHeaderView() {
        if (getListView() != null) {
            if (this.feed != null) {
                ListView lv = getListView();
                View header = ((LayoutInflater) getActivity().getSystemService("layout_inflater")).inflate(R.layout.feeditemlist_header, lv, false);
                lv.addHeaderView(header);
                this.txtvTitle = (TextView) header.findViewById(R.id.txtvTitle);
                TextView txtvAuthor = (TextView) header.findViewById(R.id.txtvAuthor);
                this.imgvBackground = (ImageView) header.findViewById(R.id.imgvBackground);
                this.imgvCover = (ImageView) header.findViewById(R.id.imgvCover);
                ImageButton butShowInfo = (ImageButton) header.findViewById(R.id.butShowInfo);
                ImageButton butShowSettings = (ImageButton) header.findViewById(R.id.butShowSettings);
                this.txtvInformation = (TextView) header.findViewById(R.id.txtvInformation);
                this.txtvFailure = (IconTextView) header.findViewById(R.id.txtvFailure);
                this.txtvTitle.setText(this.feed.getTitle());
                txtvAuthor.setText(this.feed.getAuthor());
                this.imgvBackground.setColorFilter(new LightingColorFilter(-8224126, 0));
                loadFeedImage();
                butShowInfo.setOnClickListener(new -$$Lambda$ItemlistFragment$rAyhc1_WrcrcpM7GLJy05lOsRCM());
                this.imgvCover.setOnClickListener(new -$$Lambda$ItemlistFragment$28XA6PqCu_FL7NCLEVc8U6t-gJU());
                butShowSettings.setOnClickListener(new -$$Lambda$ItemlistFragment$4DGbY6krCHzMc8h8FDQFqeEMykU());
                this.headerCreated = true;
                return;
            }
        }
        Log.e(TAG, "Unable to setup listview: recyclerView = null or feed = null");
    }

    public static /* synthetic */ void lambda$setupHeaderView$2(ItemlistFragment itemlistFragment, View v) {
        if (itemlistFragment.viewsCreated && itemlistFragment.itemsLoaded) {
            Intent startIntent = new Intent(itemlistFragment.getActivity(), FeedSettingsActivity.class);
            startIntent.putExtra("de.danoeh.antennapod.extra.feedId", itemlistFragment.feed.getId());
            itemlistFragment.startActivity(startIntent);
        }
    }

    private void showFeedInfo() {
        if (this.viewsCreated && this.itemsLoaded) {
            Intent startIntent = new Intent(getActivity(), FeedInfoActivity.class);
            startIntent.putExtra("de.danoeh.antennapod.extra.feedId", this.feed.getId());
            startActivity(startIntent);
        }
    }

    private void loadFeedImage() {
        Glide.with(getActivity()).load(this.feed.getImageLocation()).apply(new RequestOptions().placeholder((int) R.color.image_readability_tint).error((int) R.color.image_readability_tint).diskCacheStrategy(ApGlideSettings.AP_DISK_CACHE_STRATEGY).transform(new FastBlurTransformation()).dontAnimate()).into(this.imgvBackground);
        Glide.with(getActivity()).load(this.feed.getImageLocation()).apply(new RequestOptions().placeholder((int) R.color.light_gray).error((int) R.color.light_gray).diskCacheStrategy(ApGlideSettings.AP_DISK_CACHE_STRATEGY).fitCenter().dontAnimate()).into(this.imgvCover);
    }

    private void setupFooterView() {
        if (getListView() != null) {
            Feed feed = this.feed;
            if (feed != null) {
                if (feed.isPaged() && this.feed.getNextPageLink() != null) {
                    ListView lv = getListView();
                    View header = ((LayoutInflater) getActivity().getSystemService("layout_inflater")).inflate(R.layout.more_content_list_footer, lv, false);
                    lv.addFooterView(header);
                    this.listFooter = new MoreContentListFooterUtil(header);
                    this.listFooter.setClickListener(new -$$Lambda$ItemlistFragment$6vQz_IRMSu5cgOEFZmfBEEeUrg8());
                }
                return;
            }
        }
        Log.e(TAG, "Unable to setup listview: recyclerView = null or feed = null");
    }

    public static /* synthetic */ void lambda$setupFooterView$3(ItemlistFragment itemlistFragment) {
        if (itemlistFragment.feed != null) {
            try {
                DBTasks.loadNextPageOfFeed(itemlistFragment.getActivity(), itemlistFragment.feed, false);
            } catch (DownloadRequestException e) {
                e.printStackTrace();
                DownloadRequestErrorDialogCreator.newRequestErrorDialog(itemlistFragment.getActivity(), e.getMessage());
            }
        }
    }

    private void loadItems() {
        Disposable disposable = this.disposable;
        if (disposable != null) {
            disposable.dispose();
        }
        this.disposable = Observable.fromCallable(new -$$Lambda$ItemlistFragment$QgEssAXcvBuXqfZdbRCOm4qISew()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new -$$Lambda$ItemlistFragment$cMOdqE0qnBc4HsZL93cFVeJU6CE(), -$$Lambda$ItemlistFragment$OiADSVXRtF9gpnzae1lrVzbOjkE.INSTANCE);
    }

    public static /* synthetic */ void lambda$loadItems$4(ItemlistFragment itemlistFragment, Feed result) throws Exception {
        if (result != null) {
            itemlistFragment.feed = result;
            itemlistFragment.itemsLoaded = true;
            if (itemlistFragment.viewsCreated) {
                itemlistFragment.onFragmentLoaded();
            }
        }
    }

    private Feed loadData() {
        Feed feed = DBReader.getFeed(this.feedID);
        DBReader.loadAdditionalFeedItemListData(feed.getItems());
        if (feed != null && feed.getItemFilter() != null) {
            feed.setItems(feed.getItemFilter().filter(feed.getItems()));
        }
        return feed;
    }
}
