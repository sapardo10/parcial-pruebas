package de.danoeh.antennapod.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import de.danoeh.antennapod.activity.MainActivity;
import de.danoeh.antennapod.adapter.SearchlistAdapter;
import de.danoeh.antennapod.adapter.SearchlistAdapter.ItemAccess;
import de.danoeh.antennapod.core.feed.EventDistributor;
import de.danoeh.antennapod.core.feed.EventDistributor$EventListener;
import de.danoeh.antennapod.core.feed.Feed;
import de.danoeh.antennapod.core.feed.FeedComponent;
import de.danoeh.antennapod.core.feed.FeedItem;
import de.danoeh.antennapod.core.feed.SearchResult;
import de.danoeh.antennapod.core.storage.FeedSearcher;
import de.danoeh.antennapod.debug.R;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.List;

public class SearchFragment extends ListFragment {
    private static final String ARG_FEED = "feed";
    private static final String ARG_QUERY = "query";
    private static final String TAG = "SearchFragment";
    private final EventDistributor$EventListener contentUpdate = new C10692();
    private Disposable disposable;
    private final ItemAccess itemAccess = new C10703();
    private boolean itemsLoaded = false;
    private SearchlistAdapter searchAdapter;
    private List<SearchResult> searchResults;
    private boolean viewCreated = false;

    /* renamed from: de.danoeh.antennapod.fragment.SearchFragment$1 */
    class C10681 implements OnQueryTextListener {
        C10681() {
        }

        public boolean onQueryTextSubmit(String s) {
            SearchFragment.this.getArguments().putString(SearchFragment.ARG_QUERY, s);
            SearchFragment.this.itemsLoaded = false;
            SearchFragment.this.search();
            return true;
        }

        public boolean onQueryTextChange(String s) {
            return false;
        }
    }

    /* renamed from: de.danoeh.antennapod.fragment.SearchFragment$2 */
    class C10692 extends EventDistributor$EventListener {
        C10692() {
        }

        public void update(EventDistributor eventDistributor, Integer arg) {
            if ((arg.intValue() & 66) != 0) {
                SearchFragment.this.search();
            }
        }
    }

    /* renamed from: de.danoeh.antennapod.fragment.SearchFragment$3 */
    class C10703 implements ItemAccess {
        C10703() {
        }

        public int getCount() {
            return SearchFragment.this.searchResults != null ? SearchFragment.this.searchResults.size() : 0;
        }

        public SearchResult getItem(int position) {
            if (SearchFragment.this.searchResults == null || position < 0 || position >= SearchFragment.this.searchResults.size()) {
                return null;
            }
            return (SearchResult) SearchFragment.this.searchResults.get(position);
        }
    }

    public static SearchFragment newInstance(String query) {
        if (query == null) {
            query = "";
        }
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_QUERY, query);
        args.putLong("feed", 0);
        fragment.setArguments(args);
        return fragment;
    }

    public static SearchFragment newInstance(String query, long feed) {
        SearchFragment fragment = newInstance(query);
        fragment.getArguments().putLong("feed", feed);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        search();
    }

    public void onStart() {
        super.onStart();
        EventDistributor.getInstance().register(this.contentUpdate);
    }

    public void onStop() {
        super.onStop();
        Disposable disposable = this.disposable;
        if (disposable != null) {
            disposable.dispose();
        }
        EventDistributor.getInstance().unregister(this.contentUpdate);
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
        this.searchAdapter = null;
        this.viewCreated = false;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView lv = getListView();
        lv.setClipToPadding(false);
        int vertPadding = getResources().getDimensionPixelSize(R.dimen.list_vertical_padding);
        lv.setPadding(0, vertPadding, 0, vertPadding);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle((int) R.string.search_label);
        this.viewCreated = true;
        if (this.itemsLoaded) {
            onFragmentLoaded();
        }
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        FeedComponent comp = ((SearchResult) l.getAdapter().getItem(position)).getComponent();
        if (comp.getClass() == Feed.class) {
            ((MainActivity) getActivity()).loadFeedFragmentById(comp.getId(), null);
        } else if (comp.getClass() == FeedItem.class) {
            ((MainActivity) getActivity()).loadChildFragment(ItemFragment.newInstance(((FeedItem) comp).getId()));
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (this.itemsLoaded) {
            MenuItem item = menu.add(0, R.id.search_item, 0, R.string.search_label);
            MenuItemCompat.setShowAsAction(item, 1);
            View sv = new SearchView(getActivity());
            sv.setQueryHint(getString(R.string.search_hint));
            sv.setQuery(getArguments().getString(ARG_QUERY), false);
            sv.setOnQueryTextListener(new C10681());
            MenuItemCompat.setActionView(item, sv);
        }
    }

    private void onFragmentLoaded() {
        if (this.searchAdapter == null) {
            this.searchAdapter = new SearchlistAdapter(getActivity(), this.itemAccess);
            setListAdapter(this.searchAdapter);
        }
        this.searchAdapter.notifyDataSetChanged();
        setListShown(true);
        setEmptyText(getString(R.string.no_results_for_query, getArguments().getString(ARG_QUERY)));
    }

    private void search() {
        Disposable disposable = this.disposable;
        if (disposable != null) {
            disposable.dispose();
        }
        if (this.viewCreated && !this.itemsLoaded) {
            setListShown(false);
        }
        this.disposable = Observable.fromCallable(new -$$Lambda$SearchFragment$tgMfHz2s9vPLHQBwUl9NHVO6iK4()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new -$$Lambda$SearchFragment$DVHXibMmH1iRCdvvb02OWyZQ0Cw(), -$$Lambda$SearchFragment$_KDSY1UFCG62XvMnvhZ5xXxtq78.INSTANCE);
    }

    public static /* synthetic */ void lambda$search$0(SearchFragment searchFragment, List result) throws Exception {
        if (result != null) {
            searchFragment.itemsLoaded = true;
            searchFragment.searchResults = result;
            if (searchFragment.viewCreated) {
                searchFragment.onFragmentLoaded();
            }
        }
    }

    private List<SearchResult> performSearch() {
        Bundle args = getArguments();
        return FeedSearcher.performSearch(getActivity(), args.getString(ARG_QUERY), args.getLong("feed"));
    }
}
