package de.danoeh.antennapod.fragment;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemAnimator;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration.Builder;
import de.danoeh.antennapod.activity.MainActivity;
import de.danoeh.antennapod.adapter.AllEpisodesRecycleAdapter;
import de.danoeh.antennapod.adapter.AllEpisodesRecycleAdapter.ItemAccess;
import de.danoeh.antennapod.adapter.DefaultActionButtonCallback;
import de.danoeh.antennapod.core.dialog.ConfirmationDialog;
import de.danoeh.antennapod.core.event.DownloadEvent;
import de.danoeh.antennapod.core.event.DownloaderUpdate;
import de.danoeh.antennapod.core.event.FeedItemEvent;
import de.danoeh.antennapod.core.feed.EventDistributor;
import de.danoeh.antennapod.core.feed.EventDistributor$EventListener;
import de.danoeh.antennapod.core.feed.Feed;
import de.danoeh.antennapod.core.feed.FeedItem;
import de.danoeh.antennapod.core.feed.FeedMedia;
import de.danoeh.antennapod.core.menuhandler.MenuItemUtils.UpdateRefreshMenuItemChecker;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.service.download.DownloadService;
import de.danoeh.antennapod.core.service.download.Downloader;
import de.danoeh.antennapod.core.storage.DBReader;
import de.danoeh.antennapod.core.storage.DBTasks;
import de.danoeh.antennapod.core.storage.DBWriter;
import de.danoeh.antennapod.core.storage.DownloadRequester;
import de.danoeh.antennapod.core.util.FeedItemUtil;
import de.danoeh.antennapod.core.util.LongList;
import de.danoeh.antennapod.debug.R;
import de.danoeh.antennapod.menuhandler.FeedItemMenuHandler;
import de.danoeh.antennapod.menuhandler.MenuItemUtils;
import de.greenrobot.event.EventBus;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.List;

public class AllEpisodesFragment extends Fragment {
    private static final String DEFAULT_PREF_NAME = "PrefAllEpisodesFragment";
    private static final int EVENTS = 131;
    private static final String PREF_SCROLL_OFFSET = "scroll_offset";
    private static final String PREF_SCROLL_POSITION = "scroll_position";
    private static final int RECENT_EPISODES_LIMIT = 150;
    public static final String TAG = "AllEpisodesFragment";
    private final EventDistributor$EventListener contentUpdate = new C10415();
    Disposable disposable;
    private List<Downloader> downloaderList;
    List<FeedItem> episodes;
    boolean isMenuInvalidationAllowed = false;
    private boolean isUpdatingFeeds;
    private final ItemAccess itemAccess = new C10404();
    private boolean itemsLoaded = false;
    private LinearLayoutManager layoutManager;
    AllEpisodesRecycleAdapter listAdapter;
    private ProgressBar progLoading;
    RecyclerView recyclerView;
    private final UpdateRefreshMenuItemChecker updateRefreshMenuItemChecker = -$$Lambda$AllEpisodesFragment$C5IHWEf6dBK43UogIvNemPohFhU.INSTANCE;
    private boolean viewsCreated = false;

    /* renamed from: de.danoeh.antennapod.fragment.AllEpisodesFragment$4 */
    class C10404 implements ItemAccess {
        C10404() {
        }

        public int getCount() {
            if (AllEpisodesFragment.this.episodes != null) {
                return AllEpisodesFragment.this.episodes.size();
            }
            return 0;
        }

        public FeedItem getItem(int position) {
            if (AllEpisodesFragment.this.episodes == null || position < 0 || position >= AllEpisodesFragment.this.episodes.size()) {
                return null;
            }
            return (FeedItem) AllEpisodesFragment.this.episodes.get(position);
        }

        public LongList getItemsIds() {
            if (AllEpisodesFragment.this.episodes == null) {
                return new LongList(0);
            }
            LongList ids = new LongList(AllEpisodesFragment.this.episodes.size());
            for (FeedItem episode : AllEpisodesFragment.this.episodes) {
                ids.add(episode.getId());
            }
            return ids;
        }

        public int getItemDownloadProgressPercent(FeedItem item) {
            if (AllEpisodesFragment.this.downloaderList != null) {
                for (Downloader downloader : AllEpisodesFragment.this.downloaderList) {
                    if (downloader.getDownloadRequest().getFeedfileType() == 2) {
                        if (downloader.getDownloadRequest().getFeedfileId() == item.getMedia().getId()) {
                            return downloader.getDownloadRequest().getProgressPercent();
                        }
                    }
                }
            }
            return 0;
        }

        public boolean isInQueue(FeedItem item) {
            return item != null && item.isTagged(FeedItem.TAG_QUEUE);
        }

        public LongList getQueueIds() {
            LongList queueIds = new LongList();
            if (AllEpisodesFragment.this.episodes == null) {
                return queueIds;
            }
            for (FeedItem item : AllEpisodesFragment.this.episodes) {
                if (item.isTagged(FeedItem.TAG_QUEUE)) {
                    queueIds.add(item.getId());
                }
            }
            return queueIds;
        }
    }

    /* renamed from: de.danoeh.antennapod.fragment.AllEpisodesFragment$5 */
    class C10415 extends EventDistributor$EventListener {
        C10415() {
        }

        public void update(EventDistributor eventDistributor, Integer arg) {
            if ((arg.intValue() & AllEpisodesFragment.EVENTS) != 0) {
                AllEpisodesFragment.this.loadItems();
                if (AllEpisodesFragment.this.isUpdatingFeeds != AllEpisodesFragment.this.updateRefreshMenuItemChecker.isRefreshing()) {
                    AllEpisodesFragment.this.getActivity().supportInvalidateOptionsMenu();
                }
            }
        }
    }

    boolean showOnlyNewEpisodes() {
        return false;
    }

    String getPrefName() {
        return DEFAULT_PREF_NAME;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void onStart() {
        super.onStart();
        EventDistributor.getInstance().register(this.contentUpdate);
        if (this.viewsCreated && this.itemsLoaded) {
            onFragmentLoaded();
        }
    }

    public void onResume() {
        super.onResume();
        EventBus.getDefault().registerSticky(this);
        loadItems();
        registerForContextMenu(this.recyclerView);
    }

    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        saveScrollPosition();
        unregisterForContextMenu(this.recyclerView);
    }

    public void onStop() {
        super.onStop();
        EventDistributor.getInstance().unregister(this.contentUpdate);
        Disposable disposable = this.disposable;
        if (disposable != null) {
            disposable.dispose();
        }
    }

    public void onDestroyView() {
        super.onDestroyView();
        resetViewState();
    }

    private void saveScrollPosition() {
        float topOffset;
        int firstItem = this.layoutManager.findFirstVisibleItemPosition();
        View firstItemView = this.layoutManager.findViewByPosition(firstItem);
        if (firstItemView == null) {
            topOffset = 0.0f;
        } else {
            topOffset = (float) firstItemView.getTop();
        }
        Editor editor = getActivity().getSharedPreferences(getPrefName(), 0).edit();
        editor.putInt(PREF_SCROLL_POSITION, firstItem);
        editor.putFloat(PREF_SCROLL_OFFSET, topOffset);
        editor.commit();
    }

    private void restoreScrollPosition() {
        SharedPreferences prefs = getActivity().getSharedPreferences(getPrefName(), 0);
        int position = prefs.getInt(PREF_SCROLL_POSITION, 0);
        float offset = prefs.getFloat(PREF_SCROLL_OFFSET, 0.0f);
        if (position <= 0) {
            if (offset <= 0.0f) {
                return;
            }
        }
        this.layoutManager.scrollToPositionWithOffset(position, (int) offset);
        Editor editor = prefs.edit();
        editor.putInt(PREF_SCROLL_POSITION, 0);
        editor.putFloat(PREF_SCROLL_OFFSET, 0.0f);
        editor.commit();
    }

    void resetViewState() {
        this.viewsCreated = false;
        this.listAdapter = null;
    }

    static /* synthetic */ boolean lambda$new$0() {
        return DownloadService.isRunning && DownloadRequester.getInstance().isDownloadingFeeds();
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (isAdded()) {
            super.onCreateOptionsMenu(menu, inflater);
            if (this.itemsLoaded) {
                inflater.inflate(R.menu.episodes, menu);
                final SearchView sv = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
                MenuItemUtils.adjustTextColor(getActivity(), sv);
                sv.setQueryHint(getString(R.string.search_hint));
                sv.setOnQueryTextListener(new OnQueryTextListener() {
                    public boolean onQueryTextSubmit(String s) {
                        sv.clearFocus();
                        ((MainActivity) AllEpisodesFragment.this.getActivity()).loadChildFragment(SearchFragment.newInstance(s));
                        return true;
                    }

                    public boolean onQueryTextChange(String s) {
                        return false;
                    }
                });
                this.isUpdatingFeeds = de.danoeh.antennapod.core.menuhandler.MenuItemUtils.updateRefreshMenuItem(menu, R.id.refresh_item, this.updateRefreshMenuItemChecker);
            }
        }
    }

    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem markAllRead = menu.findItem(R.id.mark_all_read_item);
        boolean z = true;
        if (markAllRead != null) {
            boolean z2;
            if (!showOnlyNewEpisodes()) {
                List list = this.episodes;
                if (!(list == null || list.isEmpty())) {
                    z2 = true;
                    markAllRead.setVisible(z2);
                }
            }
            z2 = false;
            markAllRead.setVisible(z2);
        }
        MenuItem markAllSeen = menu.findItem(R.id.mark_all_seen_item);
        if (markAllSeen != null) {
            if (showOnlyNewEpisodes()) {
                List list2 = this.episodes;
                if (!(list2 == null || list2.isEmpty())) {
                    markAllSeen.setVisible(z);
                }
            }
            z = false;
            markAllSeen.setVisible(z);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (super.onOptionsItemSelected(item)) {
            return true;
        }
        int itemId = item.getItemId();
        if (itemId != R.id.refresh_item) {
            switch (itemId) {
                case R.id.mark_all_read_item:
                    new ConfirmationDialog(getActivity(), R.string.mark_all_read_label, R.string.mark_all_read_confirmation_msg) {
                        public void onConfirmButtonPressed(DialogInterface dialog) {
                            dialog.dismiss();
                            DBWriter.markAllItemsRead();
                            Toast.makeText(AllEpisodesFragment.this.getActivity(), R.string.mark_all_read_msg, 0).show();
                        }
                    }.createNewDialog().show();
                    return true;
                case R.id.mark_all_seen_item:
                    new ConfirmationDialog(getActivity(), R.string.mark_all_seen_label, R.string.mark_all_seen_confirmation_msg) {
                        public void onConfirmButtonPressed(DialogInterface dialog) {
                            dialog.dismiss();
                            DBWriter.markNewItemsSeen();
                            Toast.makeText(AllEpisodesFragment.this.getActivity(), R.string.mark_all_seen_msg, 0).show();
                        }
                    }.createNewDialog().show();
                    return true;
                default:
                    return false;
            }
        }
        List<Feed> feeds = ((MainActivity) getActivity()).getFeeds();
        if (feeds != null) {
            DBTasks.refreshAllFeeds(getActivity(), feeds);
        }
        return true;
    }

    public boolean onContextItemSelected(MenuItem item) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("onContextItemSelected() called with: item = [");
        stringBuilder.append(item);
        stringBuilder.append("]");
        Log.d(str, stringBuilder.toString());
        if (!isVisible()) {
            return false;
        }
        if (item.getItemId() == R.id.share_item) {
            return true;
        }
        FeedItem selectedItem = this.listAdapter.getSelectedItem();
        if (selectedItem == null) {
            Log.i(TAG, "Selected item was null, ignoring selection");
            return super.onContextItemSelected(item);
        } else if (R.id.mark_as_seen_item != item.getItemId()) {
            return FeedItemMenuHandler.onMenuItemClicked(getActivity(), item.getItemId(), selectedItem);
        } else {
            markItemAsSeenWithUndo(selectedItem);
            return true;
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return onCreateViewHelper(inflater, container, savedInstanceState, R.layout.all_episodes_fragment);
    }

    View onCreateViewHelper(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, int fragmentResource) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(fragmentResource, container, false);
        this.recyclerView = (RecyclerView) root.findViewById(16908298);
        ItemAnimator animator = this.recyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        this.layoutManager = new LinearLayoutManager(getActivity());
        this.recyclerView.setLayoutManager(this.layoutManager);
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.addItemDecoration(new Builder(getActivity()).build());
        this.progLoading = (ProgressBar) root.findViewById(R.id.progLoading);
        if (!this.itemsLoaded) {
            this.progLoading.setVisibility(0);
        }
        this.viewsCreated = true;
        if (this.itemsLoaded) {
            onFragmentLoaded();
        }
        return root;
    }

    private void onFragmentLoaded() {
        if (this.listAdapter == null) {
            MainActivity mainActivity = (MainActivity) getActivity();
            this.listAdapter = new AllEpisodesRecycleAdapter(mainActivity, this.itemAccess, new DefaultActionButtonCallback(mainActivity), showOnlyNewEpisodes());
            this.listAdapter.setHasStableIds(true);
            this.recyclerView.setAdapter(this.listAdapter);
        }
        this.listAdapter.notifyDataSetChanged();
        restoreScrollPosition();
        getActivity().supportInvalidateOptionsMenu();
        updateShowOnlyEpisodesListViewState();
    }

    public void onEventMainThread(FeedItemEvent event) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("onEventMainThread() called with: event = [");
        stringBuilder.append(event);
        stringBuilder.append("]");
        Log.d(str, stringBuilder.toString());
        if (this.episodes != null) {
            if (this.listAdapter != null) {
                for (FeedItem item : event.items) {
                    int pos = FeedItemUtil.indexOfItemWithId(this.episodes, item.getId());
                    if (pos >= 0) {
                        this.episodes.remove(pos);
                        if (shouldUpdatedItemRemainInList(item)) {
                            this.episodes.add(pos, item);
                            this.listAdapter.notifyItemChanged(pos);
                        } else {
                            this.listAdapter.notifyItemRemoved(pos);
                        }
                    }
                }
            }
        }
    }

    protected boolean shouldUpdatedItemRemainInList(FeedItem item) {
        return true;
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
        if (this.isMenuInvalidationAllowed) {
            if (this.isUpdatingFeeds != (update.feedIds.length > 0)) {
                getActivity().supportInvalidateOptionsMenu();
                if (this.listAdapter == null && update.mediaIds.length > 0) {
                    for (long mediaId : update.mediaIds) {
                        int pos = FeedItemUtil.indexOfItemWithMediaId(this.episodes, mediaId);
                        if (pos >= 0) {
                            this.listAdapter.notifyItemChanged(pos);
                        }
                    }
                    return;
                }
            }
        }
        if (this.listAdapter == null) {
        }
    }

    private void updateShowOnlyEpisodesListViewState() {
    }

    void loadItems() {
        Disposable disposable = this.disposable;
        if (disposable != null) {
            disposable.dispose();
        }
        if (this.viewsCreated && !this.itemsLoaded) {
            this.recyclerView.setVisibility(8);
            this.progLoading.setVisibility(0);
        }
        this.disposable = Observable.fromCallable(new -$$Lambda$wyqltIxaRvqGRuhOuBjt1rJ0WQ8()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new -$$Lambda$AllEpisodesFragment$g8d035Ah8kbjtTnMsiFF2IKJ_Dg(), -$$Lambda$AllEpisodesFragment$JZlz8IOtfmK1BscGvXR70m5l5Ws.INSTANCE);
    }

    public static /* synthetic */ void lambda$loadItems$1(AllEpisodesFragment allEpisodesFragment, List data) throws Exception {
        allEpisodesFragment.recyclerView.setVisibility(0);
        allEpisodesFragment.progLoading.setVisibility(8);
        if (data != null) {
            allEpisodesFragment.episodes = data;
            allEpisodesFragment.itemsLoaded = true;
            if (allEpisodesFragment.viewsCreated) {
                allEpisodesFragment.onFragmentLoaded();
            }
        }
    }

    List<FeedItem> loadData() {
        return DBReader.getRecentlyPublishedEpisodes(RECENT_EPISODES_LIMIT);
    }

    void markItemAsSeenWithUndo(FeedItem item) {
        if (item != null) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("markItemAsSeenWithUndo(");
            stringBuilder.append(item.getId());
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
            Disposable disposable = this.disposable;
            if (disposable != null) {
                disposable.dispose();
            }
            DBWriter.markItemPlayed(0, item.getId());
            Handler h = new Handler(getActivity().getMainLooper());
            Runnable r = new -$$Lambda$AllEpisodesFragment$2lpEjn5TsMsGKfi1IvwQwEmRrPQ(this, item);
            Snackbar snackbar = Snackbar.make(getView(), getString(R.string.marked_as_seen_label), 0);
            snackbar.setAction(getString(R.string.undo), new -$$Lambda$AllEpisodesFragment$jMrfY7XnrY0AO_SWKH1CwR9EJ6k(item, h, r));
            snackbar.show();
            h.postDelayed(r, (long) ((int) Math.ceil((double) (((float) snackbar.getDuration()) * 1.05f))));
        }
    }

    public static /* synthetic */ void lambda$markItemAsSeenWithUndo$3(AllEpisodesFragment allEpisodesFragment, FeedItem item) {
        FeedMedia media = item.getMedia();
        if (media != null && media.hasAlmostEnded() && UserPreferences.isAutoDelete()) {
            DBWriter.deleteFeedMediaOfItem(allEpisodesFragment.getActivity(), media.getId());
        }
    }

    static /* synthetic */ void lambda$markItemAsSeenWithUndo$4(FeedItem item, Handler h, Runnable r, View v) {
        DBWriter.markItemPlayed(-1, item.getId());
        h.removeCallbacks(r);
    }
}
