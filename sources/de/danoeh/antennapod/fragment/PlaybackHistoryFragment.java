package de.danoeh.antennapod.fragment;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import de.danoeh.antennapod.activity.MainActivity;
import de.danoeh.antennapod.adapter.DefaultActionButtonCallback;
import de.danoeh.antennapod.adapter.FeedItemlistAdapter;
import de.danoeh.antennapod.adapter.FeedItemlistAdapter.ItemAccess;
import de.danoeh.antennapod.core.event.DownloadEvent;
import de.danoeh.antennapod.core.event.FeedItemEvent;
import de.danoeh.antennapod.core.feed.EventDistributor;
import de.danoeh.antennapod.core.feed.EventDistributor$EventListener;
import de.danoeh.antennapod.core.feed.FeedItem;
import de.danoeh.antennapod.core.service.download.Downloader;
import de.danoeh.antennapod.core.storage.DBReader;
import de.danoeh.antennapod.core.storage.DBWriter;
import de.danoeh.antennapod.core.util.FeedItemUtil;
import de.danoeh.antennapod.core.util.LongList;
import de.danoeh.antennapod.debug.R;
import de.greenrobot.event.EventBus;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.List;

public class PlaybackHistoryFragment extends ListFragment {
    private static final int EVENTS = 144;
    public static final String TAG = "PlaybackHistoryFragment";
    private FeedItemlistAdapter adapter;
    private final EventDistributor$EventListener contentUpdate = new C10611();
    private Disposable disposable;
    private List<Downloader> downloaderList;
    private final ItemAccess itemAccess = new C10622();
    private boolean itemsLoaded = false;
    private List<FeedItem> playbackHistory;
    private boolean viewsCreated = false;

    /* renamed from: de.danoeh.antennapod.fragment.PlaybackHistoryFragment$1 */
    class C10611 extends EventDistributor$EventListener {
        C10611() {
        }

        public void update(EventDistributor eventDistributor, Integer arg) {
            if ((arg.intValue() & PlaybackHistoryFragment.EVENTS) != 0) {
                PlaybackHistoryFragment.this.loadItems();
                PlaybackHistoryFragment.this.getActivity().supportInvalidateOptionsMenu();
            }
        }
    }

    /* renamed from: de.danoeh.antennapod.fragment.PlaybackHistoryFragment$2 */
    class C10622 implements ItemAccess {
        C10622() {
        }

        public int getItemDownloadProgressPercent(FeedItem item) {
            if (PlaybackHistoryFragment.this.downloaderList != null) {
                for (Downloader downloader : PlaybackHistoryFragment.this.downloaderList) {
                    if (downloader.getDownloadRequest().getFeedfileType() == 2) {
                        if (downloader.getDownloadRequest().getFeedfileId() == item.getMedia().getId()) {
                            return downloader.getDownloadRequest().getProgressPercent();
                        }
                    }
                }
            }
            return 0;
        }

        public int getCount() {
            return PlaybackHistoryFragment.this.playbackHistory != null ? PlaybackHistoryFragment.this.playbackHistory.size() : 0;
        }

        public FeedItem getItem(int position) {
            if (PlaybackHistoryFragment.this.playbackHistory == null || position < 0 || position >= PlaybackHistoryFragment.this.playbackHistory.size()) {
                return null;
            }
            return (FeedItem) PlaybackHistoryFragment.this.playbackHistory.get(position);
        }

        public LongList getQueueIds() {
            LongList queueIds = new LongList();
            if (PlaybackHistoryFragment.this.playbackHistory == null) {
                return queueIds;
            }
            for (FeedItem item : PlaybackHistoryFragment.this.playbackHistory) {
                if (item.isTagged(FeedItem.TAG_QUEUE)) {
                    queueIds.add(item.getId());
                }
            }
            return queueIds;
        }
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        if (this.viewsCreated && this.itemsLoaded) {
            onFragmentLoaded();
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView lv = getListView();
        lv.setClipToPadding(false);
        int vertPadding = getResources().getDimensionPixelSize(R.dimen.list_vertical_padding);
        lv.setPadding(0, vertPadding, 0, vertPadding);
        this.viewsCreated = true;
        if (this.itemsLoaded) {
            onFragmentLoaded();
        }
    }

    public void onResume() {
        super.onResume();
        EventBus.getDefault().registerSticky(this);
        loadItems();
    }

    public void onStart() {
        super.onStart();
        EventDistributor.getInstance().register(this.contentUpdate);
    }

    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    public void onStop() {
        super.onStop();
        EventDistributor.getInstance().unregister(this.contentUpdate);
        Disposable disposable = this.disposable;
        if (disposable != null) {
            disposable.dispose();
        }
    }

    public void onDetach() {
        super.onDetach();
        Disposable disposable = this.disposable;
        if (disposable != null) {
            disposable.dispose();
        }
    }

    public void onDestroyView() {
        super.onDestroyView();
        this.adapter = null;
        this.viewsCreated = false;
    }

    public void onEvent(DownloadEvent event) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("onEvent() called with: event = [");
        stringBuilder.append(event);
        stringBuilder.append("]");
        Log.d(str, stringBuilder.toString());
        this.downloaderList = event.update.downloaders;
        FeedItemlistAdapter feedItemlistAdapter = this.adapter;
        if (feedItemlistAdapter != null) {
            feedItemlistAdapter.notifyDataSetChanged();
        }
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        position -= l.getHeaderViewsCount();
        ((MainActivity) getActivity()).loadChildFragment(ItemFragment.newInstance(FeedItemUtil.getIds(this.playbackHistory), position));
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (isAdded()) {
            super.onCreateOptionsMenu(menu, inflater);
            if (this.itemsLoaded) {
                MenuItem clearHistory = menu.add(0, R.id.clear_history_item, 65536, R.string.clear_history_label);
                MenuItemCompat.setShowAsAction(clearHistory, 1);
                TypedArray drawables = getActivity().obtainStyledAttributes(new int[]{R.attr.content_discard});
                clearHistory.setIcon(drawables.getDrawable(0));
                drawables.recycle();
            }
        }
    }

    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (this.itemsLoaded) {
            MenuItem menuItem = menu.findItem(R.id.clear_history_item);
            if (menuItem != null) {
                List list = this.playbackHistory;
                boolean z = (list == null || list.isEmpty()) ? false : true;
                menuItem.setVisible(z);
            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (super.onOptionsItemSelected(item)) {
            return true;
        }
        if (item.getItemId() != R.id.clear_history_item) {
            return false;
        }
        DBWriter.clearPlaybackHistory();
        return true;
    }

    public void onEventMainThread(FeedItemEvent event) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("onEventMainThread() called with: event = [");
        stringBuilder.append(event);
        stringBuilder.append("]");
        Log.d(str, stringBuilder.toString());
        if (this.playbackHistory != null) {
            for (FeedItem item : event.items) {
                if (FeedItemUtil.indexOfItemWithId(this.playbackHistory, item.getId()) >= 0) {
                    loadItems();
                    return;
                }
            }
        }
    }

    private void onFragmentLoaded() {
        if (this.adapter == null) {
            this.adapter = new FeedItemlistAdapter(getActivity(), this.itemAccess, new DefaultActionButtonCallback(getActivity()), true, false);
            setListAdapter(this.adapter);
        }
        setListShown(true);
        this.adapter.notifyDataSetChanged();
        getActivity().supportInvalidateOptionsMenu();
    }

    private void loadItems() {
        Disposable disposable = this.disposable;
        if (disposable != null) {
            disposable.dispose();
        }
        this.disposable = Observable.fromCallable(new -$$Lambda$PlaybackHistoryFragment$bVp-eMzPv8RgA8Ss9UC84uxgIeQ()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new -$$Lambda$PlaybackHistoryFragment$Xe3UkpuKyEzLnC1i6WWcO4AesQw(), -$$Lambda$PlaybackHistoryFragment$6NlpuSaBEmYj0lRFdrt8ManvDGQ.INSTANCE);
    }

    public static /* synthetic */ void lambda$loadItems$0(PlaybackHistoryFragment playbackHistoryFragment, List result) throws Exception {
        if (result != null) {
            playbackHistoryFragment.playbackHistory = result;
            playbackHistoryFragment.itemsLoaded = true;
            if (playbackHistoryFragment.viewsCreated) {
                playbackHistoryFragment.onFragmentLoaded();
            }
        }
    }

    private List<FeedItem> loadData() {
        List<FeedItem> history = DBReader.getPlaybackHistory();
        DBReader.loadAdditionalFeedItemListData(history);
        return history;
    }
}
