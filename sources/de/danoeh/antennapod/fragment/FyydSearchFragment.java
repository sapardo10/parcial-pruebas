package de.danoeh.antennapod.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.MenuItemCompat.OnActionExpandListener;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import de.danoeh.antennapod.activity.OnlineFeedViewActivity;
import de.danoeh.antennapod.adapter.itunes.ItunesAdapter;
import de.danoeh.antennapod.adapter.itunes.ItunesAdapter.Podcast;
import de.danoeh.antennapod.core.service.download.AntennapodHttpClient;
import de.danoeh.antennapod.debug.R;
import de.danoeh.antennapod.menuhandler.MenuItemUtils;
import de.mfietz.fyydlin.FyydClient;
import de.mfietz.fyydlin.FyydResponse;
import de.mfietz.fyydlin.SearchHit;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FyydSearchFragment extends Fragment {
    private static final String TAG = "FyydSearchFragment";
    private ItunesAdapter adapter;
    private Button butRetry;
    private final FyydClient client = new FyydClient(AntennapodHttpClient.getHttpClient());
    private Disposable disposable;
    private GridView gridView;
    private ProgressBar progressBar;
    private List<Podcast> searchResults;
    private TextView txtvEmpty;
    private TextView txtvError;

    /* renamed from: de.danoeh.antennapod.fragment.FyydSearchFragment$2 */
    class C10482 implements OnActionExpandListener {
        C10482() {
        }

        public boolean onMenuItemActionExpand(MenuItem item) {
            return true;
        }

        public boolean onMenuItemActionCollapse(MenuItem item) {
            FyydSearchFragment.this.getActivity().getSupportFragmentManager().popBackStack();
            return true;
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_itunes_search, container, false);
        this.gridView = (GridView) root.findViewById(R.id.gridView);
        this.adapter = new ItunesAdapter(getActivity(), new ArrayList());
        this.gridView.setAdapter(this.adapter);
        this.gridView.setOnItemClickListener(new -$$Lambda$FyydSearchFragment$SBDI6nJUR6Bx0QwEfynf9O6mx7o());
        this.progressBar = (ProgressBar) root.findViewById(R.id.progressBar);
        this.txtvError = (TextView) root.findViewById(R.id.txtvError);
        this.butRetry = (Button) root.findViewById(R.id.butRetry);
        this.txtvEmpty = (TextView) root.findViewById(16908292);
        return root;
    }

    public static /* synthetic */ void lambda$onCreateView$0(FyydSearchFragment fyydSearchFragment, AdapterView parent, View view1, int position, long id) {
        Podcast podcast = (Podcast) fyydSearchFragment.searchResults.get(position);
        Intent intent = new Intent(fyydSearchFragment.getActivity(), OnlineFeedViewActivity.class);
        intent.putExtra(OnlineFeedViewActivity.ARG_FEEDURL, podcast.feedUrl);
        intent.putExtra("title", podcast.title);
        fyydSearchFragment.startActivity(intent);
    }

    public void onDestroy() {
        super.onDestroy();
        Disposable disposable = this.disposable;
        if (disposable != null) {
            disposable.dispose();
        }
        this.adapter = null;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.itunes_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView sv = (SearchView) MenuItemCompat.getActionView(searchItem);
        MenuItemUtils.adjustTextColor(getActivity(), sv);
        sv.setQueryHint(getString(R.string.search_fyyd_label));
        sv.setOnQueryTextListener(new OnQueryTextListener() {
            public boolean onQueryTextSubmit(String s) {
                sv.clearFocus();
                FyydSearchFragment.this.search(s);
                return true;
            }

            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        MenuItemCompat.setOnActionExpandListener(searchItem, new C10482());
        MenuItemCompat.expandActionView(searchItem);
    }

    private void search(String query) {
        Disposable disposable = this.disposable;
        if (disposable != null) {
            disposable.dispose();
        }
        showOnlyProgressBar();
        this.disposable = this.client.searchPodcasts(query, Integer.valueOf(10)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new -$$Lambda$FyydSearchFragment$r5ro48_nToqoWOwBvv_9k0lxD_w(), new -$$Lambda$FyydSearchFragment$mdaMbB2w-AkGhW7W1-moTsNjK2s(this, query));
    }

    public static /* synthetic */ void lambda$search$1(FyydSearchFragment fyydSearchFragment, FyydResponse result) throws Exception {
        fyydSearchFragment.progressBar.setVisibility(8);
        fyydSearchFragment.processSearchResult(result);
    }

    public static /* synthetic */ void lambda$search$3(FyydSearchFragment fyydSearchFragment, String query, Throwable error) throws Exception {
        Log.e(TAG, Log.getStackTraceString(error));
        fyydSearchFragment.progressBar.setVisibility(8);
        fyydSearchFragment.txtvError.setText(error.toString());
        fyydSearchFragment.txtvError.setVisibility(0);
        fyydSearchFragment.butRetry.setOnClickListener(new -$$Lambda$FyydSearchFragment$_Hdy2WmdSm1XhO2q2rdKVxpAbMg(fyydSearchFragment, query));
        fyydSearchFragment.butRetry.setVisibility(0);
    }

    private void showOnlyProgressBar() {
        this.gridView.setVisibility(8);
        this.txtvError.setVisibility(8);
        this.butRetry.setVisibility(8);
        this.txtvEmpty.setVisibility(8);
        this.progressBar.setVisibility(0);
    }

    private void processSearchResult(FyydResponse response) {
        this.adapter.clear();
        if (response.getData().isEmpty()) {
            this.searchResults = Collections.emptyList();
        } else {
            this.adapter.clear();
            this.searchResults = new ArrayList();
            for (SearchHit searchHit : response.getData()) {
                this.searchResults.add(Podcast.fromSearch(searchHit));
            }
        }
        for (Podcast podcast : this.searchResults) {
            this.adapter.add(podcast);
        }
        this.adapter.notifyDataSetInvalidated();
        int i = 0;
        this.gridView.setVisibility(!this.searchResults.isEmpty() ? 0 : 8);
        TextView textView = this.txtvEmpty;
        if (!this.searchResults.isEmpty()) {
            i = 8;
        }
        textView.setVisibility(i);
    }
}
