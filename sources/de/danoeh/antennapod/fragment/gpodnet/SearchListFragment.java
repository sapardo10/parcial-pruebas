package de.danoeh.antennapod.fragment.gpodnet;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.Menu;
import android.view.MenuInflater;
import de.danoeh.antennapod.core.gpoddernet.GpodnetService;
import de.danoeh.antennapod.core.gpoddernet.GpodnetServiceException;
import de.danoeh.antennapod.core.gpoddernet.model.GpodnetPodcast;
import de.danoeh.antennapod.debug.R;
import de.danoeh.antennapod.menuhandler.MenuItemUtils;
import java.util.List;
import org.apache.commons.lang3.Validate;

public class SearchListFragment extends PodcastListFragment {
    private static final String ARG_QUERY = "query";
    private String query;

    public static SearchListFragment newInstance(String query) {
        SearchListFragment fragment = new SearchListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_QUERY, query);
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() == null || !getArguments().containsKey(ARG_QUERY)) {
            this.query = "";
        } else {
            this.query = getArguments().getString(ARG_QUERY);
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        final SearchView sv = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        MenuItemUtils.adjustTextColor(getActivity(), sv);
        sv.setQueryHint(getString(R.string.gpodnet_search_hint));
        sv.setQuery(this.query, false);
        sv.setOnQueryTextListener(new OnQueryTextListener() {
            public boolean onQueryTextSubmit(String s) {
                sv.clearFocus();
                SearchListFragment.this.changeQuery(s);
                return true;
            }

            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    protected List<GpodnetPodcast> loadPodcastData(GpodnetService service) throws GpodnetServiceException {
        return service.searchPodcasts(this.query, 0);
    }

    private void changeQuery(String query) {
        Validate.notNull(query);
        this.query = query;
        loadData();
    }
}
