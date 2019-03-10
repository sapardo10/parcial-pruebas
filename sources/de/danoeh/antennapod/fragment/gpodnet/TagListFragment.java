package de.danoeh.antennapod.fragment.gpodnet;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;
import de.danoeh.antennapod.activity.MainActivity;
import de.danoeh.antennapod.adapter.gpodnet.TagListAdapter;
import de.danoeh.antennapod.core.gpoddernet.GpodnetService;
import de.danoeh.antennapod.core.gpoddernet.GpodnetServiceException;
import de.danoeh.antennapod.core.gpoddernet.model.GpodnetTag;
import de.danoeh.antennapod.debug.R;
import de.danoeh.antennapod.menuhandler.MenuItemUtils;
import java.util.List;

public class TagListFragment extends ListFragment {
    private static final int COUNT = 50;
    private static final String TAG = "TagListFragment";
    private AsyncTask<Void, Void, List<GpodnetTag>> loadTask;

    /* renamed from: de.danoeh.antennapod.fragment.gpodnet.TagListFragment$2 */
    class C07902 extends AsyncTask<Void, Void, List<GpodnetTag>> {
        private Exception exception;

        C07902() {
        }

        protected List<GpodnetTag> doInBackground(Void... params) {
            GpodnetService service = new GpodnetService();
            try {
                List<GpodnetTag> topTags = service.getTopTags(50);
                service.shutdown();
                return topTags;
            } catch (GpodnetServiceException e) {
                e.printStackTrace();
                this.exception = e;
                service.shutdown();
                return null;
            } catch (Throwable th) {
                service.shutdown();
                throw th;
            }
        }

        protected void onPreExecute() {
            super.onPreExecute();
            TagListFragment.this.setListShown(false);
        }

        protected void onPostExecute(List<GpodnetTag> gpodnetTags) {
            super.onPostExecute(gpodnetTags);
            Context context = TagListFragment.this.getActivity();
            if (context != null) {
                if (gpodnetTags != null) {
                    TagListFragment.this.setListAdapter(new TagListAdapter(context, 17367043, gpodnetTags));
                } else if (this.exception != null) {
                    TextView txtvError = new TextView(TagListFragment.this.getActivity());
                    txtvError.setText(this.exception.getMessage());
                    TagListFragment.this.getListView().setEmptyView(txtvError);
                }
                TagListFragment.this.setListShown(true);
            }
        }
    }

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
                Activity activity = TagListFragment.this.getActivity();
                if (activity != null) {
                    sv.clearFocus();
                    ((MainActivity) activity).loadChildFragment(SearchListFragment.newInstance(s));
                }
                return true;
            }

            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setOnItemClickListener(new -$$Lambda$TagListFragment$dpHs-ZQNZjWHYubN9obqulsKEko());
        startLoadTask();
    }

    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle((int) R.string.add_feed_label);
    }

    public void onDestroyView() {
        super.onDestroyView();
        cancelLoadTask();
    }

    private void cancelLoadTask() {
        AsyncTask asyncTask = this.loadTask;
        if (asyncTask != null && !asyncTask.isCancelled()) {
            this.loadTask.cancel(true);
        }
    }

    private void startLoadTask() {
        cancelLoadTask();
        this.loadTask = new C07902();
        this.loadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }
}
