package de.danoeh.antennapod.fragment.gpodnet;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import de.danoeh.antennapod.activity.MainActivity;
import de.danoeh.antennapod.activity.OnlineFeedViewActivity;
import de.danoeh.antennapod.adapter.gpodnet.PodcastListAdapter;
import de.danoeh.antennapod.core.gpoddernet.GpodnetService;
import de.danoeh.antennapod.core.gpoddernet.GpodnetServiceException;
import de.danoeh.antennapod.core.gpoddernet.model.GpodnetPodcast;
import de.danoeh.antennapod.debug.R;
import de.danoeh.antennapod.menuhandler.MenuItemUtils;
import java.util.List;

public abstract class PodcastListFragment extends Fragment {
    private static final String TAG = "PodcastListFragment";
    private Button butRetry;
    private GridView gridView;
    private ProgressBar progressBar;
    private TextView txtvError;

    /* renamed from: de.danoeh.antennapod.fragment.gpodnet.PodcastListFragment$2 */
    class C07892 extends AsyncTask<Void, Void, List<GpodnetPodcast>> {
        volatile Exception exception = null;

        C07892() {
        }

        protected List<GpodnetPodcast> doInBackground(Void... params) {
            GpodnetService service = null;
            try {
                service = new GpodnetService();
                List<GpodnetPodcast> loadPodcastData = PodcastListFragment.this.loadPodcastData(service);
                service.shutdown();
                return loadPodcastData;
            } catch (GpodnetServiceException e) {
                this.exception = e;
                e.printStackTrace();
                if (service != null) {
                    service.shutdown();
                }
                return null;
            } catch (Throwable th) {
                if (service != null) {
                    service.shutdown();
                }
            }
        }

        protected void onPostExecute(List<GpodnetPodcast> gpodnetPodcasts) {
            super.onPostExecute(gpodnetPodcasts);
            Context context = PodcastListFragment.this.getActivity();
            if (context != null && gpodnetPodcasts != null && gpodnetPodcasts.size() > 0) {
                PodcastListAdapter listAdapter = new PodcastListAdapter(context, 0, gpodnetPodcasts);
                PodcastListFragment.this.gridView.setAdapter(listAdapter);
                listAdapter.notifyDataSetChanged();
                PodcastListFragment.this.progressBar.setVisibility(8);
                PodcastListFragment.this.gridView.setVisibility(0);
                PodcastListFragment.this.txtvError.setVisibility(8);
                PodcastListFragment.this.butRetry.setVisibility(8);
            } else if (context != null && gpodnetPodcasts != null) {
                PodcastListFragment.this.gridView.setVisibility(8);
                PodcastListFragment.this.progressBar.setVisibility(8);
                PodcastListFragment.this.txtvError.setText(PodcastListFragment.this.getString(R.string.search_status_no_results));
                PodcastListFragment.this.txtvError.setVisibility(0);
                PodcastListFragment.this.butRetry.setVisibility(8);
            } else if (context != null) {
                PodcastListFragment.this.gridView.setVisibility(8);
                PodcastListFragment.this.progressBar.setVisibility(8);
                TextView access$200 = PodcastListFragment.this.txtvError;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(PodcastListFragment.this.getString(R.string.error_msg_prefix));
                stringBuilder.append(this.exception.getMessage());
                access$200.setText(stringBuilder.toString());
                PodcastListFragment.this.txtvError.setVisibility(0);
                PodcastListFragment.this.butRetry.setVisibility(0);
            }
        }

        protected void onPreExecute() {
            super.onPreExecute();
            PodcastListFragment.this.gridView.setVisibility(8);
            PodcastListFragment.this.progressBar.setVisibility(0);
            PodcastListFragment.this.txtvError.setVisibility(8);
            PodcastListFragment.this.butRetry.setVisibility(8);
        }
    }

    protected abstract List<GpodnetPodcast> loadPodcastData(GpodnetService gpodnetService) throws GpodnetServiceException;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.gpodder_podcasts, menu);
        final SearchView sv = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        MenuItemUtils.adjustTextColor(getActivity(), sv);
        sv.setQueryHint(getString(R.string.gpodnet_search_hint));
        sv.setOnQueryTextListener(new OnQueryTextListener() {
            public boolean onQueryTextSubmit(String s) {
                sv.clearFocus();
                MainActivity activity = (MainActivity) PodcastListFragment.this.getActivity();
                if (activity != null) {
                    activity.loadChildFragment(SearchListFragment.newInstance(s));
                }
                return true;
            }

            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.gpodnet_podcast_list, container, false);
        this.gridView = (GridView) root.findViewById(R.id.gridView);
        this.progressBar = (ProgressBar) root.findViewById(R.id.progressBar);
        this.txtvError = (TextView) root.findViewById(R.id.txtvError);
        this.butRetry = (Button) root.findViewById(R.id.butRetry);
        this.gridView.setOnItemClickListener(new -$$Lambda$PodcastListFragment$MIsAirvxoDgToU-JQHkhpcZ5BTM());
        this.butRetry.setOnClickListener(new -$$Lambda$PodcastListFragment$SdYDxyFtM-weIXN9TJ0BLSGp-jE());
        loadData();
        return root;
    }

    private void onPodcastSelected(GpodnetPodcast selection) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Selected podcast: ");
        stringBuilder.append(selection.toString());
        Log.d(str, stringBuilder.toString());
        Intent intent = new Intent(getActivity(), OnlineFeedViewActivity.class);
        intent.putExtra(OnlineFeedViewActivity.ARG_FEEDURL, selection.getUrl());
        intent.putExtra("title", getString(R.string.gpodnet_main_label));
        startActivity(intent);
    }

    final void loadData() {
        new C07892().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }
}
