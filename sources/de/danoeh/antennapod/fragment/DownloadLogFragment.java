package de.danoeh.antennapod.fragment;

import android.app.AlertDialog.Builder;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.os.EnvironmentCompat;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import de.danoeh.antennapod.adapter.DownloadLogAdapter;
import de.danoeh.antennapod.adapter.DownloadLogAdapter.ItemAccess;
import de.danoeh.antennapod.core.feed.EventDistributor;
import de.danoeh.antennapod.core.feed.EventDistributor$EventListener;
import de.danoeh.antennapod.core.feed.Feed;
import de.danoeh.antennapod.core.feed.FeedMedia;
import de.danoeh.antennapod.core.service.download.DownloadStatus;
import de.danoeh.antennapod.core.storage.DBReader;
import de.danoeh.antennapod.core.storage.DBWriter;
import de.danoeh.antennapod.debug.R;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.List;

public class DownloadLogFragment extends ListFragment {
    private static final String TAG = "DownloadLogFragment";
    private DownloadLogAdapter adapter;
    private final EventDistributor$EventListener contentUpdate = new C10452();
    private Disposable disposable;
    private List<DownloadStatus> downloadLog;
    private final ItemAccess itemAccess = new C10441();
    private boolean itemsLoaded = false;
    private boolean viewsCreated = false;

    /* renamed from: de.danoeh.antennapod.fragment.DownloadLogFragment$1 */
    class C10441 implements ItemAccess {
        C10441() {
        }

        public int getCount() {
            return DownloadLogFragment.this.downloadLog != null ? DownloadLogFragment.this.downloadLog.size() : 0;
        }

        public DownloadStatus getItem(int position) {
            if (DownloadLogFragment.this.downloadLog == null || position < 0 || position >= DownloadLogFragment.this.downloadLog.size()) {
                return null;
            }
            return (DownloadStatus) DownloadLogFragment.this.downloadLog.get(position);
        }
    }

    /* renamed from: de.danoeh.antennapod.fragment.DownloadLogFragment$2 */
    class C10452 extends EventDistributor$EventListener {
        C10452() {
        }

        public void update(EventDistributor eventDistributor, Integer arg) {
            if ((arg.intValue() & 8) != 0) {
                DownloadLogFragment.this.loadItems();
            }
        }
    }

    public void onStart() {
        super.onStart();
        setHasOptionsMenu(true);
        EventDistributor.getInstance().register(this.contentUpdate);
        loadItems();
    }

    public void onStop() {
        super.onStop();
        EventDistributor.getInstance().unregister(this.contentUpdate);
        Disposable disposable = this.disposable;
        if (disposable != null) {
            disposable.dispose();
        }
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

    private void onFragmentLoaded() {
        if (this.adapter == null) {
            this.adapter = new DownloadLogAdapter(getActivity(), this.itemAccess);
            setListAdapter(this.adapter);
        }
        setListShown(true);
        this.adapter.notifyDataSetChanged();
        getActivity().supportInvalidateOptionsMenu();
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        Builder builder;
        super.onListItemClick(l, v, position, id);
        DownloadStatus status = this.adapter.getItem(position);
        String url = EnvironmentCompat.MEDIA_UNKNOWN;
        String message = getString(R.string.download_successful);
        if (status.getFeedfileType() == 2) {
            FeedMedia media = DBReader.getFeedMedia(status.getFeedfileId());
            if (media != null) {
                url = media.getDownload_url();
            }
        } else if (status.getFeedfileType() == 0) {
            Feed feed = DBReader.getFeed(status.getFeedfileId());
            if (feed != null) {
                url = feed.getDownload_url();
            }
            if (!status.isSuccessful()) {
                message = status.getReasonDetailed();
            }
            builder = new Builder(getContext());
            builder.setTitle(R.string.download_error_details);
            builder.setMessage(getString(R.string.download_error_details_message, message, url));
            builder.setPositiveButton(17039370, null);
            ((TextView) builder.show().findViewById(16908299)).setTextIsSelectable(true);
        }
        if (!status.isSuccessful()) {
            message = status.getReasonDetailed();
        }
        builder = new Builder(getContext());
        builder.setTitle(R.string.download_error_details);
        builder.setMessage(getString(R.string.download_error_details_message, message, url));
        builder.setPositiveButton(17039370, null);
        ((TextView) builder.show().findViewById(16908299)).setTextIsSelectable(true);
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
                List list = this.downloadLog;
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
        DBWriter.clearDownloadLog();
        return true;
    }

    private void loadItems() {
        Disposable disposable = this.disposable;
        if (disposable != null) {
            disposable.dispose();
        }
        this.disposable = Observable.fromCallable(-$$Lambda$AnkW3qtptXE0ZLYDo_IbKzp4d7o.INSTANCE).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new -$$Lambda$DownloadLogFragment$s61MXAWbV3jwMeQ9goTBBTVlBN0(), -$$Lambda$DownloadLogFragment$Ug6I8M85V5BKLhKUtLTIJhPjPzY.INSTANCE);
    }

    public static /* synthetic */ void lambda$loadItems$0(DownloadLogFragment downloadLogFragment, List result) throws Exception {
        if (result != null) {
            downloadLogFragment.downloadLog = result;
            downloadLogFragment.itemsLoaded = true;
            if (downloadLogFragment.viewsCreated) {
                downloadLogFragment.onFragmentLoaded();
            }
        }
    }
}
