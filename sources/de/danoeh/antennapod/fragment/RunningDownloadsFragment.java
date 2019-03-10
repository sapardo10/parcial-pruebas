package de.danoeh.antennapod.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import de.danoeh.antennapod.adapter.DownloadlistAdapter;
import de.danoeh.antennapod.adapter.DownloadlistAdapter.ItemAccess;
import de.danoeh.antennapod.core.event.DownloadEvent;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.service.download.DownloadRequest;
import de.danoeh.antennapod.core.service.download.Downloader;
import de.danoeh.antennapod.core.storage.DBReader;
import de.danoeh.antennapod.core.storage.DBWriter;
import de.danoeh.antennapod.core.storage.DownloadRequester;
import de.danoeh.antennapod.debug.R;
import de.greenrobot.event.EventBus;
import java.util.List;

public class RunningDownloadsFragment extends ListFragment {
    private static final String TAG = "RunningDownloadsFrag";
    private DownloadlistAdapter adapter;
    private List<Downloader> downloaderList;
    private final ItemAccess itemAccess = new C10671();

    /* renamed from: de.danoeh.antennapod.fragment.RunningDownloadsFragment$1 */
    class C10671 implements ItemAccess {
        C10671() {
        }

        public int getCount() {
            return RunningDownloadsFragment.this.downloaderList != null ? RunningDownloadsFragment.this.downloaderList.size() : 0;
        }

        public Downloader getItem(int position) {
            if (RunningDownloadsFragment.this.downloaderList == null || position < 0 || position >= RunningDownloadsFragment.this.downloaderList.size()) {
                return null;
            }
            return (Downloader) RunningDownloadsFragment.this.downloaderList.get(position);
        }

        public void onSecondaryActionClick(Downloader downloader) {
            DownloadRequest downloadRequest = downloader.getDownloadRequest();
            DownloadRequester.getInstance().cancelDownload(RunningDownloadsFragment.this.getActivity(), downloadRequest.getSource());
            if (downloadRequest.getFeedfileType() == 2) {
                if (UserPreferences.isEnableAutodownload()) {
                    DBWriter.setFeedItemAutoDownload(DBReader.getFeedMedia(downloadRequest.getFeedfileId()).getItem(), false);
                    Toast.makeText(RunningDownloadsFragment.this.getActivity(), R.string.download_canceled_autodownload_enabled_msg, 0).show();
                    return;
                }
            }
            Toast.makeText(RunningDownloadsFragment.this.getActivity(), R.string.download_canceled_msg, 0).show();
        }
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView lv = getListView();
        lv.setClipToPadding(false);
        int vertPadding = getResources().getDimensionPixelSize(R.dimen.list_vertical_padding);
        lv.setPadding(0, vertPadding, 0, vertPadding);
        this.adapter = new DownloadlistAdapter(getActivity(), this.itemAccess);
        setListAdapter(this.adapter);
    }

    public void onResume() {
        super.onResume();
        EventBus.getDefault().registerSticky(this);
    }

    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    public void onDestroy() {
        super.onDestroy();
        setListAdapter(null);
        this.adapter = null;
    }

    public void onEvent(DownloadEvent event) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("onEvent() called with: event = [");
        stringBuilder.append(event);
        stringBuilder.append("]");
        Log.d(str, stringBuilder.toString());
        this.downloaderList = event.update.downloaders;
        DownloadlistAdapter downloadlistAdapter = this.adapter;
        if (downloadlistAdapter != null) {
            downloadlistAdapter.notifyDataSetChanged();
        }
    }
}
