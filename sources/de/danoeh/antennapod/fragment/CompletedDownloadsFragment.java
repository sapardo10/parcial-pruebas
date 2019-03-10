package de.danoeh.antennapod.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import de.danoeh.antennapod.activity.MainActivity;
import de.danoeh.antennapod.adapter.DownloadedEpisodesListAdapter;
import de.danoeh.antennapod.adapter.DownloadedEpisodesListAdapter.ItemAccess;
import de.danoeh.antennapod.core.feed.EventDistributor;
import de.danoeh.antennapod.core.feed.EventDistributor$EventListener;
import de.danoeh.antennapod.core.feed.FeedItem;
import de.danoeh.antennapod.core.storage.DBWriter;
import de.danoeh.antennapod.core.util.FeedItemUtil;
import de.danoeh.antennapod.debug.R;
import de.danoeh.antennapod.dialog.EpisodesApplyActionFragment;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.List;

public class CompletedDownloadsFragment extends ListFragment {
    private static final int EVENTS = 74;
    private static final String TAG = CompletedDownloadsFragment.class.getSimpleName();
    private final EventDistributor$EventListener contentUpdate = new C10432();
    private Disposable disposable;
    private final ItemAccess itemAccess = new C10421();
    private List<FeedItem> items;
    private DownloadedEpisodesListAdapter listAdapter;
    private boolean viewCreated = false;

    /* renamed from: de.danoeh.antennapod.fragment.CompletedDownloadsFragment$1 */
    class C10421 implements ItemAccess {
        C10421() {
        }

        public int getCount() {
            return CompletedDownloadsFragment.this.items != null ? CompletedDownloadsFragment.this.items.size() : 0;
        }

        public FeedItem getItem(int position) {
            if (CompletedDownloadsFragment.this.items == null || position < 0 || position >= CompletedDownloadsFragment.this.items.size()) {
                return null;
            }
            return (FeedItem) CompletedDownloadsFragment.this.items.get(position);
        }

        public void onFeedItemSecondaryAction(FeedItem item) {
            DBWriter.deleteFeedMediaOfItem(CompletedDownloadsFragment.this.getActivity(), item.getMedia().getId());
        }
    }

    /* renamed from: de.danoeh.antennapod.fragment.CompletedDownloadsFragment$2 */
    class C10432 extends EventDistributor$EventListener {
        C10432() {
        }

        public void update(EventDistributor eventDistributor, Integer arg) {
            if ((arg.intValue() & 74) != 0) {
                CompletedDownloadsFragment.this.loadItems();
            }
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        loadItems();
    }

    public void onStart() {
        super.onStart();
        EventDistributor.getInstance().register(this.contentUpdate);
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
        this.listAdapter = null;
        this.viewCreated = false;
        Disposable disposable = this.disposable;
        if (disposable != null) {
            disposable.dispose();
        }
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        if (this.viewCreated && this.items != null) {
            onFragmentLoaded();
        }
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView lv = getListView();
        lv.setClipToPadding(false);
        int vertPadding = getResources().getDimensionPixelSize(R.dimen.list_vertical_padding);
        lv.setPadding(0, vertPadding, 0, vertPadding);
        this.viewCreated = true;
        if (this.items != null && getActivity() != null) {
            onFragmentLoaded();
        }
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        position -= l.getHeaderViewsCount();
        ((MainActivity) getActivity()).loadChildFragment(ItemFragment.newInstance(FeedItemUtil.getIds(this.items), position));
    }

    private void onFragmentLoaded() {
        if (this.listAdapter == null) {
            this.listAdapter = new DownloadedEpisodesListAdapter(getActivity(), this.itemAccess);
            setListAdapter(this.listAdapter);
        }
        setListShown(true);
        this.listAdapter.notifyDataSetChanged();
        getActivity().supportInvalidateOptionsMenu();
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (isAdded()) {
            super.onCreateOptionsMenu(menu, inflater);
            if (this.items != null) {
                inflater.inflate(R.menu.downloads_completed, menu);
                menu.findItem(R.id.episode_actions).setVisible(this.items.size() > 0);
            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != R.id.episode_actions) {
            return false;
        }
        ((MainActivity) getActivity()).loadChildFragment(EpisodesApplyActionFragment.newInstance(this.items, 17));
        return true;
    }

    private void loadItems() {
        Disposable disposable = this.disposable;
        if (disposable != null) {
            disposable.dispose();
        }
        if (this.items == null && this.viewCreated) {
            setListShown(false);
        }
        this.disposable = Observable.fromCallable(-$$Lambda$pkf5HCK45lQGMOIEp0PowCOBrqo.INSTANCE).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new -$$Lambda$CompletedDownloadsFragment$1xluYts3fDfuEESecyLu-blk_g0(), -$$Lambda$CompletedDownloadsFragment$0Z_mXfchRRk4UWCy2xqhzwgSfak.INSTANCE);
    }

    public static /* synthetic */ void lambda$loadItems$0(CompletedDownloadsFragment completedDownloadsFragment, List result) throws Exception {
        completedDownloadsFragment.items = result;
        if (completedDownloadsFragment.viewCreated && completedDownloadsFragment.getActivity() != null) {
            completedDownloadsFragment.onFragmentLoaded();
        }
    }
}
