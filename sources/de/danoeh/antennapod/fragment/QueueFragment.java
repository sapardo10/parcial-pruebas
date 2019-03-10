package de.danoeh.antennapod.fragment;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemAnimator;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.support.v7.widget.helper.ItemTouchHelper.SimpleCallback;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration.Builder;
import de.danoeh.antennapod.activity.MainActivity;
import de.danoeh.antennapod.adapter.DefaultActionButtonCallback;
import de.danoeh.antennapod.adapter.QueueRecyclerAdapter;
import de.danoeh.antennapod.adapter.QueueRecyclerAdapter.ItemAccess;
import de.danoeh.antennapod.adapter.QueueRecyclerAdapter.ItemTouchHelperViewHolder;
import de.danoeh.antennapod.core.dialog.ConfirmationDialog;
import de.danoeh.antennapod.core.event.DownloadEvent;
import de.danoeh.antennapod.core.event.DownloaderUpdate;
import de.danoeh.antennapod.core.event.FeedItemEvent;
import de.danoeh.antennapod.core.event.QueueEvent;
import de.danoeh.antennapod.core.feed.EventDistributor;
import de.danoeh.antennapod.core.feed.EventDistributor$EventListener;
import de.danoeh.antennapod.core.feed.Feed;
import de.danoeh.antennapod.core.feed.FeedItem;
import de.danoeh.antennapod.core.menuhandler.MenuItemUtils.UpdateRefreshMenuItemChecker;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.service.download.DownloadService;
import de.danoeh.antennapod.core.service.download.Downloader;
import de.danoeh.antennapod.core.storage.DBTasks;
import de.danoeh.antennapod.core.storage.DBWriter;
import de.danoeh.antennapod.core.storage.DownloadRequester;
import de.danoeh.antennapod.core.util.Converter;
import de.danoeh.antennapod.core.util.FeedItemUtil;
import de.danoeh.antennapod.core.util.LongList;
import de.danoeh.antennapod.core.util.QueueSorter;
import de.danoeh.antennapod.core.util.QueueSorter.Rule;
import de.danoeh.antennapod.debug.R;
import de.danoeh.antennapod.menuhandler.FeedItemMenuHandler;
import de.danoeh.antennapod.menuhandler.MenuItemUtils;
import de.greenrobot.event.EventBus;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.List;

public class QueueFragment extends Fragment {
    private static final int EVENTS = 194;
    private static final String PREFS = "QueueFragment";
    private static final String PREF_SCROLL_OFFSET = "scroll_offset";
    private static final String PREF_SCROLL_POSITION = "scroll_position";
    public static final String TAG = "QueueFragment";
    private final EventDistributor$EventListener contentUpdate = new C10665();
    private Disposable disposable;
    private List<Downloader> downloaderList;
    private TextView infoBar;
    private boolean isUpdatingFeeds = false;
    private final ItemAccess itemAccess = new C10654();
    private ItemTouchHelper itemTouchHelper;
    private LinearLayoutManager layoutManager;
    private ProgressBar progLoading;
    private List<FeedItem> queue;
    private QueueRecyclerAdapter recyclerAdapter;
    private RecyclerView recyclerView;
    private TextView txtvEmpty;
    private final UpdateRefreshMenuItemChecker updateRefreshMenuItemChecker = -$$Lambda$QueueFragment$SZg68xpQi5nqCncRyJWn_ZwwSPQ.INSTANCE;

    /* renamed from: de.danoeh.antennapod.fragment.QueueFragment$4 */
    class C10654 implements ItemAccess {
        C10654() {
        }

        public int getCount() {
            return QueueFragment.this.queue != null ? QueueFragment.this.queue.size() : 0;
        }

        public FeedItem getItem(int position) {
            if (QueueFragment.this.queue == null || position < 0 || position >= QueueFragment.this.queue.size()) {
                return null;
            }
            return (FeedItem) QueueFragment.this.queue.get(position);
        }

        public long getItemDownloadedBytes(FeedItem item) {
            if (QueueFragment.this.downloaderList != null) {
                for (Downloader downloader : QueueFragment.this.downloaderList) {
                    if (downloader.getDownloadRequest().getFeedfileType() == 2) {
                        if (downloader.getDownloadRequest().getFeedfileId() == item.getMedia().getId()) {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("downloaded bytes: ");
                            stringBuilder.append(downloader.getDownloadRequest().getSoFar());
                            Log.d("QueueFragment", stringBuilder.toString());
                            return downloader.getDownloadRequest().getSoFar();
                        }
                    }
                }
            }
            return 0;
        }

        public long getItemDownloadSize(FeedItem item) {
            if (QueueFragment.this.downloaderList != null) {
                for (Downloader downloader : QueueFragment.this.downloaderList) {
                    if (downloader.getDownloadRequest().getFeedfileType() == 2) {
                        if (downloader.getDownloadRequest().getFeedfileId() == item.getMedia().getId()) {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("downloaded size: ");
                            stringBuilder.append(downloader.getDownloadRequest().getSize());
                            Log.d("QueueFragment", stringBuilder.toString());
                            return downloader.getDownloadRequest().getSize();
                        }
                    }
                }
            }
            return 0;
        }

        public int getItemDownloadProgressPercent(FeedItem item) {
            if (QueueFragment.this.downloaderList != null) {
                for (Downloader downloader : QueueFragment.this.downloaderList) {
                    if (downloader.getDownloadRequest().getFeedfileType() == 2) {
                        if (downloader.getDownloadRequest().getFeedfileId() == item.getMedia().getId()) {
                            return downloader.getDownloadRequest().getProgressPercent();
                        }
                    }
                }
            }
            return 0;
        }

        public LongList getQueueIds() {
            return QueueFragment.this.queue != null ? LongList.of(FeedItemUtil.getIds(QueueFragment.this.queue)) : new LongList(0);
        }
    }

    /* renamed from: de.danoeh.antennapod.fragment.QueueFragment$5 */
    class C10665 extends EventDistributor$EventListener {
        C10665() {
        }

        public void update(EventDistributor eventDistributor, Integer arg) {
            if ((arg.intValue() & QueueFragment.EVENTS) != 0) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("arg: ");
                stringBuilder.append(arg);
                Log.d("QueueFragment", stringBuilder.toString());
                QueueFragment.this.loadItems(false);
                if (QueueFragment.this.isUpdatingFeeds != QueueFragment.this.updateRefreshMenuItemChecker.isRefreshing()) {
                    QueueFragment.this.getActivity().supportInvalidateOptionsMenu();
                }
            }
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    public void onStart() {
        super.onStart();
        if (this.queue != null) {
            onFragmentLoaded(true);
        }
    }

    public void onResume() {
        super.onResume();
        loadItems(true);
        EventDistributor.getInstance().register(this.contentUpdate);
        EventBus.getDefault().registerSticky(this);
    }

    public void onPause() {
        super.onPause();
        saveScrollPosition();
        EventDistributor.getInstance().unregister(this.contentUpdate);
        EventBus.getDefault().unregister(this);
        Disposable disposable = this.disposable;
        if (disposable != null) {
            disposable.dispose();
        }
    }

    public void onEventMainThread(QueueEvent event) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("onEventMainThread() called with: event = [");
        stringBuilder.append(event);
        stringBuilder.append("]");
        Log.d("QueueFragment", stringBuilder.toString());
        if (this.queue != null) {
            if (this.recyclerAdapter != null) {
                switch (event.action) {
                    case ADDED:
                        this.queue.add(event.position, event.item);
                        this.recyclerAdapter.notifyItemInserted(event.position);
                        break;
                    case SET_QUEUE:
                        this.queue = event.items;
                        this.recyclerAdapter.notifyDataSetChanged();
                        break;
                    case REMOVED:
                    case IRREVERSIBLE_REMOVED:
                        int position = FeedItemUtil.indexOfItemWithId(this.queue, event.item.getId());
                        this.queue.remove(position);
                        this.recyclerAdapter.notifyItemRemoved(position);
                        break;
                    case CLEARED:
                        this.queue.clear();
                        this.recyclerAdapter.notifyDataSetChanged();
                        break;
                    case SORTED:
                        this.queue = event.items;
                        this.recyclerAdapter.notifyDataSetChanged();
                        break;
                    case MOVED:
                        return;
                    default:
                        break;
                }
                saveScrollPosition();
                onFragmentLoaded(false);
            }
        }
    }

    public void onEventMainThread(FeedItemEvent event) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("onEventMainThread() called with: event = [");
        stringBuilder.append(event);
        stringBuilder.append("]");
        Log.d("QueueFragment", stringBuilder.toString());
        if (this.queue != null) {
            if (this.recyclerAdapter != null) {
                int size = event.items.size();
                for (int i = 0; i < size; i++) {
                    FeedItem item = (FeedItem) event.items.get(i);
                    int pos = FeedItemUtil.indexOfItemWithId(this.queue, item.getId());
                    if (pos >= 0) {
                        this.queue.remove(pos);
                        this.queue.add(pos, item);
                        this.recyclerAdapter.notifyItemChanged(pos);
                    }
                }
            }
        }
    }

    public void onEventMainThread(DownloadEvent event) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("onEventMainThread() called with: event = [");
        stringBuilder.append(event);
        stringBuilder.append("]");
        Log.d("QueueFragment", stringBuilder.toString());
        DownloaderUpdate update = event.update;
        this.downloaderList = update.downloaders;
        if (this.isUpdatingFeeds != (update.feedIds.length > 0)) {
            getActivity().supportInvalidateOptionsMenu();
        }
        if (this.recyclerAdapter != null && update.mediaIds.length > 0) {
            for (long mediaId : update.mediaIds) {
                int pos = FeedItemUtil.indexOfItemWithMediaId(this.queue, mediaId);
                if (pos >= 0) {
                    this.recyclerAdapter.notifyItemChanged(pos);
                }
            }
        }
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
        Editor editor = getActivity().getSharedPreferences("QueueFragment", 0).edit();
        editor.putInt(PREF_SCROLL_POSITION, firstItem);
        editor.putFloat(PREF_SCROLL_OFFSET, topOffset);
        editor.commit();
    }

    private void restoreScrollPosition() {
        SharedPreferences prefs = getActivity().getSharedPreferences("QueueFragment", 0);
        int position = prefs.getInt(PREF_SCROLL_POSITION, 0);
        float offset = prefs.getFloat(PREF_SCROLL_OFFSET, 0.0f);
        if (position <= 0) {
            if (offset <= 0.0f) {
                return;
            }
        }
        this.layoutManager.scrollToPositionWithOffset(position, (int) offset);
    }

    private void resetViewState() {
        this.recyclerAdapter = null;
    }

    public void onDestroyView() {
        super.onDestroyView();
        resetViewState();
    }

    static /* synthetic */ boolean lambda$new$0() {
        return DownloadService.isRunning && DownloadRequester.getInstance().isDownloadingFeeds();
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (isAdded()) {
            super.onCreateOptionsMenu(menu, inflater);
            if (this.queue != null) {
                inflater.inflate(R.menu.queue, menu);
                final SearchView sv = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
                MenuItemUtils.adjustTextColor(getActivity(), sv);
                sv.setQueryHint(getString(R.string.search_hint));
                sv.setOnQueryTextListener(new OnQueryTextListener() {
                    public boolean onQueryTextSubmit(String s) {
                        sv.clearFocus();
                        ((MainActivity) QueueFragment.this.getActivity()).loadChildFragment(SearchFragment.newInstance(s));
                        return true;
                    }

                    public boolean onQueryTextChange(String s) {
                        return false;
                    }
                });
                MenuItemUtils.refreshLockItem(getActivity(), menu);
                this.isUpdatingFeeds = de.danoeh.antennapod.core.menuhandler.MenuItemUtils.updateRefreshMenuItem(menu, R.id.refresh_item, this.updateRefreshMenuItemChecker);
            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (super.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.clear_queue:
                new ConfirmationDialog(getActivity(), R.string.clear_queue_label, R.string.clear_queue_confirmation_msg) {
                    public void onConfirmButtonPressed(DialogInterface dialog) {
                        dialog.dismiss();
                        DBWriter.clearQueue();
                    }
                }.createNewDialog().show();
                return true;
            case R.id.queue_lock:
                boolean newLockState = UserPreferences.isQueueLocked() ^ true;
                UserPreferences.setQueueLocked(newLockState);
                getActivity().supportInvalidateOptionsMenu();
                this.recyclerAdapter.setLocked(newLockState);
                if (newLockState) {
                    Snackbar.make(getActivity().findViewById(R.id.content), (int) R.string.queue_locked, -1).show();
                } else {
                    Snackbar.make(getActivity().findViewById(R.id.content), (int) R.string.queue_unlocked, -1).show();
                }
                return true;
            case R.id.queue_sort_date_asc:
                QueueSorter.sort(getActivity(), Rule.DATE_ASC, true);
                return true;
            case R.id.queue_sort_date_desc:
                QueueSorter.sort(getActivity(), Rule.DATE_DESC, true);
                return true;
            case R.id.queue_sort_duration_asc:
                QueueSorter.sort(getActivity(), Rule.DURATION_ASC, true);
                return true;
            case R.id.queue_sort_duration_desc:
                QueueSorter.sort(getActivity(), Rule.DURATION_DESC, true);
                return true;
            case R.id.queue_sort_episode_title_asc:
                QueueSorter.sort(getActivity(), Rule.EPISODE_TITLE_ASC, true);
                return true;
            case R.id.queue_sort_episode_title_desc:
                QueueSorter.sort(getActivity(), Rule.EPISODE_TITLE_DESC, true);
                return true;
            case R.id.queue_sort_feed_title_asc:
                QueueSorter.sort(getActivity(), Rule.FEED_TITLE_ASC, true);
                return true;
            case R.id.queue_sort_feed_title_desc:
                QueueSorter.sort(getActivity(), Rule.FEED_TITLE_DESC, true);
                return true;
            case R.id.queue_sort_random:
                QueueSorter.sort(getActivity(), Rule.RANDOM, true);
                return true;
            case R.id.queue_sort_smart_shuffle_asc:
                QueueSorter.sort(getActivity(), Rule.SMART_SHUFFLE_ASC, true);
                return true;
            case R.id.queue_sort_smart_shuffle_desc:
                QueueSorter.sort(getActivity(), Rule.SMART_SHUFFLE_DESC, true);
                return true;
            case R.id.refresh_item:
                List<Feed> feeds = ((MainActivity) getActivity()).getFeeds();
                if (feeds != null) {
                    DBTasks.refreshAllFeeds(getActivity(), feeds);
                }
                return true;
            default:
                return false;
        }
    }

    public boolean onContextItemSelected(MenuItem item) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("onContextItemSelected() called with: item = [");
        stringBuilder.append(item);
        stringBuilder.append("]");
        Log.d("QueueFragment", stringBuilder.toString());
        if (!isVisible()) {
            return false;
        }
        FeedItem selectedItem = this.recyclerAdapter.getSelectedItem();
        if (selectedItem == null) {
            Log.i("QueueFragment", "Selected item was null, ignoring selection");
            return super.onContextItemSelected(item);
        }
        switch (item.getItemId()) {
            case R.id.move_to_bottom_item:
                int position = FeedItemUtil.indexOfItemWithId(this.queue, selectedItem.getId());
                List list = this.queue;
                list.add(list.size() - 1, this.queue.remove(position));
                this.recyclerAdapter.notifyItemMoved(position, this.queue.size() - 1);
                DBWriter.moveQueueItemToBottom(selectedItem.getId(), true);
                return true;
            case R.id.move_to_top_item:
                int position2 = FeedItemUtil.indexOfItemWithId(this.queue, selectedItem.getId());
                List list2 = this.queue;
                list2.add(0, list2.remove(position2));
                this.recyclerAdapter.notifyItemMoved(position2, 0);
                DBWriter.moveQueueItemToTop(selectedItem.getId(), true);
                return true;
            default:
                return FeedItemMenuHandler.onMenuItemClicked(getActivity(), item.getItemId(), selectedItem);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle((int) R.string.queue_label);
        final View root = inflater.inflate(R.layout.queue_fragment, container, false);
        this.infoBar = (TextView) root.findViewById(R.id.info_bar);
        this.recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        ItemAnimator animator = this.recyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        this.layoutManager = new LinearLayoutManager(getActivity());
        this.recyclerView.setLayoutManager(this.layoutManager);
        this.recyclerView.addItemDecoration(new Builder(getActivity()).build());
        this.recyclerView.setHasFixedSize(true);
        registerForContextMenu(this.recyclerView);
        this.itemTouchHelper = new ItemTouchHelper(new SimpleCallback(3, 8) {
            int dragFrom = -1;
            int dragTo = -1;

            public boolean onMove(RecyclerView recyclerView, ViewHolder viewHolder, ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                if (this.dragFrom == -1) {
                    this.dragFrom = fromPosition;
                }
                this.dragTo = toPosition;
                int from = viewHolder.getAdapterPosition();
                int to = target.getAdapterPosition();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("move(");
                stringBuilder.append(from);
                stringBuilder.append(", ");
                stringBuilder.append(to);
                stringBuilder.append(") in memory");
                Log.d("QueueFragment", stringBuilder.toString());
                if (from < QueueFragment.this.queue.size()) {
                    if (to < QueueFragment.this.queue.size()) {
                        QueueFragment.this.queue.add(to, QueueFragment.this.queue.remove(from));
                        QueueFragment.this.recyclerAdapter.notifyItemMoved(from, to);
                        return true;
                    }
                }
                return false;
            }

            public void onSwiped(ViewHolder viewHolder, int direction) {
                if (QueueFragment.this.disposable != null) {
                    QueueFragment.this.disposable.dispose();
                }
                int position = viewHolder.getAdapterPosition();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("remove(");
                stringBuilder.append(position);
                stringBuilder.append(")");
                Log.d("QueueFragment", stringBuilder.toString());
                FeedItem item = (FeedItem) QueueFragment.this.queue.get(position);
                boolean isRead = item.isPlayed();
                DBWriter.markItemPlayed(1, false, item.getId());
                DBWriter.removeQueueItem(QueueFragment.this.getActivity(), item, true);
                Snackbar snackbar = Snackbar.make(root, QueueFragment.this.getString(R.string.marked_as_read_label), 0);
                snackbar.setAction(QueueFragment.this.getString(R.string.undo), new -$$Lambda$QueueFragment$3$8oEcf0dZ0vuGCv-7_MEwd2k_HGk(this, item, position, isRead));
                snackbar.show();
            }

            public static /* synthetic */ void lambda$onSwiped$0(C11223 c11223, FeedItem item, int position, boolean isRead, View v) {
                DBWriter.addQueueItemAt(QueueFragment.this.getActivity(), item.getId(), position, false);
                if (!isRead) {
                    DBWriter.markItemPlayed(0, item.getId());
                }
            }

            public boolean isLongPressDragEnabled() {
                return UserPreferences.isQueueLocked() ^ 1;
            }

            public boolean isItemViewSwipeEnabled() {
                return UserPreferences.isQueueLocked() ^ 1;
            }

            public void onSelectedChanged(ViewHolder viewHolder, int actionState) {
                if (actionState != 0) {
                    if (viewHolder instanceof ItemTouchHelperViewHolder) {
                        ((ItemTouchHelperViewHolder) viewHolder).onItemSelected();
                    }
                }
                super.onSelectedChanged(viewHolder, actionState);
            }

            public void clearView(RecyclerView recyclerView, ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                int i = this.dragFrom;
                if (i != -1) {
                    int i2 = this.dragTo;
                    if (!(i2 == -1 || i == i2)) {
                        reallyMoved(i, i2);
                        this.dragTo = -1;
                        this.dragFrom = -1;
                        if (viewHolder instanceof ItemTouchHelperViewHolder) {
                            ((ItemTouchHelperViewHolder) viewHolder).onItemClear();
                        }
                    }
                }
                this.dragTo = -1;
                this.dragFrom = -1;
                if (viewHolder instanceof ItemTouchHelperViewHolder) {
                    ((ItemTouchHelperViewHolder) viewHolder).onItemClear();
                }
            }

            private void reallyMoved(int from, int to) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Write to database move(");
                stringBuilder.append(from);
                stringBuilder.append(", ");
                stringBuilder.append(to);
                stringBuilder.append(")");
                Log.d("QueueFragment", stringBuilder.toString());
                DBWriter.moveQueueItem(from, to, true);
            }
        });
        this.itemTouchHelper.attachToRecyclerView(this.recyclerView);
        this.txtvEmpty = (TextView) root.findViewById(16908292);
        this.txtvEmpty.setVisibility(8);
        this.progLoading = (ProgressBar) root.findViewById(R.id.progLoading);
        this.progLoading.setVisibility(0);
        return root;
    }

    private void onFragmentLoaded(boolean restoreScrollPosition) {
        if (this.recyclerAdapter == null) {
            MainActivity activity = (MainActivity) getActivity();
            this.recyclerAdapter = new QueueRecyclerAdapter(activity, this.itemAccess, new DefaultActionButtonCallback(activity), this.itemTouchHelper);
            this.recyclerAdapter.setHasStableIds(true);
            this.recyclerView.setAdapter(this.recyclerAdapter);
        }
        List list = this.queue;
        if (list != null) {
            if (list.size() != 0) {
                this.txtvEmpty.setVisibility(8);
                this.recyclerView.setVisibility(0);
                if (restoreScrollPosition) {
                    restoreScrollPosition();
                }
                getActivity().supportInvalidateOptionsMenu();
                refreshInfoBar();
            }
        }
        this.recyclerView.setVisibility(8);
        this.txtvEmpty.setVisibility(0);
        if (restoreScrollPosition) {
            restoreScrollPosition();
        }
        getActivity().supportInvalidateOptionsMenu();
        refreshInfoBar();
    }

    private void refreshInfoBar() {
        String info = new StringBuilder();
        info.append(this.queue.size());
        info.append(getString(R.string.episodes_suffix));
        info = info.toString();
        if (this.queue.size() > 0) {
            long timeLeft = 0;
            float playbackSpeed = Float.valueOf(UserPreferences.getPlaybackSpeed()).floatValue();
            for (FeedItem item : this.queue) {
                if (item.getMedia() != null) {
                    timeLeft += (long) (((float) (item.getMedia().getDuration() - item.getMedia().getPosition())) / playbackSpeed);
                }
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(info);
            stringBuilder.append(" • ");
            info = stringBuilder.toString();
            stringBuilder = new StringBuilder();
            stringBuilder.append(info);
            stringBuilder.append(getString(R.string.time_left_label));
            info = stringBuilder.toString();
            stringBuilder = new StringBuilder();
            stringBuilder.append(info);
            stringBuilder.append(Converter.getDurationStringLocalized(getActivity(), timeLeft));
            info = stringBuilder.toString();
        }
        this.infoBar.setText(info);
    }

    private void loadItems(boolean restoreScrollPosition) {
        Log.d("QueueFragment", "loadItems()");
        Disposable disposable = this.disposable;
        if (disposable != null) {
            disposable.dispose();
        }
        if (this.queue == null) {
            this.recyclerView.setVisibility(8);
            this.txtvEmpty.setVisibility(8);
            this.progLoading.setVisibility(0);
        }
        this.disposable = Observable.fromCallable(-$$Lambda$W41vErsRVBKwiIn04LXxvD3pKVM.INSTANCE).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new -$$Lambda$QueueFragment$nXJ1Kyw-T-IbCjFX4cSBTkhyB3M(this, restoreScrollPosition), -$$Lambda$QueueFragment$jETMZCFVoYA1VoPv15AubhhUoJM.INSTANCE);
    }

    public static /* synthetic */ void lambda$loadItems$1(QueueFragment queueFragment, boolean restoreScrollPosition, List items) throws Exception {
        if (items != null) {
            queueFragment.progLoading.setVisibility(8);
            queueFragment.queue = items;
            queueFragment.onFragmentLoaded(restoreScrollPosition);
            QueueRecyclerAdapter queueRecyclerAdapter = queueFragment.recyclerAdapter;
            if (queueRecyclerAdapter != null) {
                queueRecyclerAdapter.notifyDataSetChanged();
            }
        }
    }
}
