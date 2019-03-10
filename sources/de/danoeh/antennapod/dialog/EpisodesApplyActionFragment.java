package de.danoeh.antennapod.dialog;

import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import de.danoeh.antennapod.core.dialog.DownloadRequestErrorDialogCreator;
import de.danoeh.antennapod.core.feed.FeedItem;
import de.danoeh.antennapod.core.storage.DBTasks;
import de.danoeh.antennapod.core.storage.DBWriter;
import de.danoeh.antennapod.core.storage.DownloadRequestException;
import de.danoeh.antennapod.core.util.LongList;
import de.danoeh.antennapod.debug.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class EpisodesApplyActionFragment extends Fragment {
    private static final int ACTION_ALL = 31;
    private static final int ACTION_DOWNLOAD = 8;
    private static final int ACTION_MARK_PLAYED = 2;
    private static final int ACTION_MARK_UNPLAYED = 4;
    public static final int ACTION_QUEUE = 1;
    public static final int ACTION_REMOVE = 16;
    public static final String TAG = "EpisodeActionFragment";
    private int actions;
    private Button btnAddToQueue;
    private Button btnDelete;
    private Button btnDownload;
    private Button btnMarkAsPlayed;
    private Button btnMarkAsUnplayed;
    private final LongList checkedIds = new LongList();
    private final List<FeedItem> episodes = new ArrayList();
    private final Map<Long, FeedItem> idMap = new ArrayMap();
    private ArrayAdapter<String> mAdapter;
    private ListView mListView;
    private MenuItem mSelectToggle;
    private final List<String> titles = new ArrayList();

    public static EpisodesApplyActionFragment newInstance(List<FeedItem> items) {
        return newInstance(items, 31);
    }

    public static EpisodesApplyActionFragment newInstance(List<FeedItem> items, int actions) {
        EpisodesApplyActionFragment f = new EpisodesApplyActionFragment();
        f.episodes.addAll(items);
        for (FeedItem episode : items) {
            f.idMap.put(Long.valueOf(episode.getId()), episode);
        }
        f.actions = actions;
        return f;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.episodes_apply_action_fragment, container, false);
        this.mListView = (ListView) view.findViewById(16908298);
        this.mListView.setChoiceMode(2);
        this.mListView.setOnItemClickListener(new C0779x4ccbf07f());
        this.mListView.setOnItemLongClickListener(new C0778xe7a35401());
        for (FeedItem episode : this.episodes) {
            this.titles.add(episode.getTitle());
        }
        this.mAdapter = new ArrayAdapter(getActivity(), 17367056, this.titles);
        this.mListView.setAdapter(this.mAdapter);
        checkAll();
        int lastVisibleDiv = 0;
        this.btnAddToQueue = (Button) view.findViewById(R.id.btnAddToQueue);
        if ((this.actions & 1) != 0) {
            this.btnAddToQueue.setOnClickListener(new C0773x894f80a6());
            lastVisibleDiv = R.id.divider1;
        } else {
            this.btnAddToQueue.setVisibility(8);
            view.findViewById(R.id.divider1).setVisibility(8);
        }
        this.btnMarkAsPlayed = (Button) view.findViewById(R.id.btnMarkAsPlayed);
        if ((2 & this.actions) != 0) {
            this.btnMarkAsPlayed.setOnClickListener(new C0777x64c95da8());
            lastVisibleDiv = R.id.divider2;
        } else {
            this.btnMarkAsPlayed.setVisibility(8);
            view.findViewById(R.id.divider2).setVisibility(8);
        }
        this.btnMarkAsUnplayed = (Button) view.findViewById(R.id.btnMarkAsUnplayed);
        if ((this.actions & 4) != 0) {
            this.btnMarkAsUnplayed.setOnClickListener(new C0775xb860edac());
            lastVisibleDiv = R.id.divider3;
        } else {
            this.btnMarkAsUnplayed.setVisibility(8);
            view.findViewById(R.id.divider3).setVisibility(8);
        }
        this.btnDownload = (Button) view.findViewById(R.id.btnDownload);
        if ((this.actions & 8) != 0) {
            this.btnDownload.setOnClickListener(new C0769xe7797271());
            lastVisibleDiv = R.id.divider4;
        } else {
            this.btnDownload.setVisibility(8);
            view.findViewById(R.id.divider4).setVisibility(8);
        }
        this.btnDelete = (Button) view.findViewById(R.id.btnDelete);
        if ((this.actions & 16) != 0) {
            this.btnDelete.setOnClickListener(new C0776xf38fc06b());
        } else {
            this.btnDelete.setVisibility(8);
            if (lastVisibleDiv > 0) {
                view.findViewById(lastVisibleDiv).setVisibility(8);
            }
        }
        return view;
    }

    public static /* synthetic */ void lambda$onCreateView$0(EpisodesApplyActionFragment episodesApplyActionFragment, AdapterView ListView, View view1, int position, long rowId) {
        long id = ((FeedItem) episodesApplyActionFragment.episodes.get(position)).getId();
        if (episodesApplyActionFragment.checkedIds.contains(id)) {
            episodesApplyActionFragment.checkedIds.remove(id);
        } else {
            episodesApplyActionFragment.checkedIds.add(id);
        }
        episodesApplyActionFragment.refreshCheckboxes();
    }

    public static /* synthetic */ void lambda$null$1(EpisodesApplyActionFragment episodesApplyActionFragment, int position, DialogInterface dialogInterface, int item) {
        int direction;
        if (item == 0) {
            direction = -1;
        } else {
            direction = 1;
        }
        int currentPosition = position + direction;
        while (currentPosition >= 0 && currentPosition < episodesApplyActionFragment.episodes.size()) {
            long id1 = ((FeedItem) episodesApplyActionFragment.episodes.get(currentPosition)).getId();
            if (!episodesApplyActionFragment.checkedIds.contains(id1)) {
                episodesApplyActionFragment.checkedIds.add(id1);
            }
            currentPosition += direction;
        }
        episodesApplyActionFragment.refreshCheckboxes();
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.episodes_apply_action_options, menu);
        this.mSelectToggle = menu.findItem(R.id.select_toggle);
        this.mSelectToggle.setOnMenuItemClickListener(new C0772x4d743ba5());
    }

    public static /* synthetic */ boolean lambda$onCreateOptionsMenu$8(EpisodesApplyActionFragment episodesApplyActionFragment, MenuItem item) {
        if (episodesApplyActionFragment.checkedIds.size() == episodesApplyActionFragment.episodes.size()) {
            episodesApplyActionFragment.checkNone();
        } else {
            episodesApplyActionFragment.checkAll();
        }
        return true;
    }

    public void onPrepareOptionsMenu(Menu menu) {
        int[] icon = new int[1];
        if (this.checkedIds.size() == this.episodes.size()) {
            icon[0] = R.attr.ic_check_box;
        } else if (this.checkedIds.size() == 0) {
            icon[0] = R.attr.ic_check_box_outline;
        } else {
            icon[0] = R.attr.ic_indeterminate_check_box;
        }
        TypedArray a = getActivity().obtainStyledAttributes(icon);
        Drawable iconDrawable = a.getDrawable(0);
        a.recycle();
        this.mSelectToggle.setIcon(iconDrawable);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int resId = 0;
        int itemId = item.getItemId();
        if (itemId == R.id.select_options) {
            return true;
        }
        switch (itemId) {
            case R.id.check_all:
                checkAll();
                resId = R.string.selected_all_label;
                break;
            case R.id.check_downloaded:
                checkDownloaded(true);
                resId = R.string.selected_downloaded_label;
                break;
            case R.id.check_has_media:
                checkWithMedia();
                resId = R.string.selected_has_media_label;
                break;
            case R.id.check_none:
                checkNone();
                resId = R.string.deselected_all_label;
                break;
            case R.id.check_not_downloaded:
                checkDownloaded(false);
                resId = R.string.selected_not_downloaded_label;
                break;
            case R.id.check_not_queued:
                checkQueued(false);
                resId = R.string.selected_not_queued_label;
                break;
            case R.id.check_played:
                checkPlayed(true);
                resId = R.string.selected_played_label;
                break;
            case R.id.check_queued:
                checkQueued(true);
                resId = R.string.selected_queued_label;
                break;
            case R.id.check_unplayed:
                checkPlayed(false);
                resId = R.string.selected_unplayed_label;
                break;
            default:
                switch (itemId) {
                    case R.id.sort_date_new_old:
                        sortByDate(true);
                        return true;
                    case R.id.sort_date_old_new:
                        sortByDate(false);
                        return true;
                    case R.id.sort_duration_long_short:
                        sortByDuration(true);
                        return true;
                    case R.id.sort_duration_short_long:
                        sortByDuration(false);
                        return true;
                    case R.id.sort_title_a_z:
                        sortByTitle(false);
                        return true;
                    case R.id.sort_title_z_a:
                        sortByTitle(true);
                        return true;
                    default:
                        break;
                }
        }
        if (resId == 0) {
            return false;
        }
        Toast.makeText(getActivity(), resId, 0).show();
        return true;
    }

    private void sortByTitle(boolean reverse) {
        Collections.sort(this.episodes, new C0768x383d62b8(reverse));
        refreshTitles();
        refreshCheckboxes();
    }

    static /* synthetic */ int lambda$sortByTitle$9(boolean reverse, FeedItem lhs, FeedItem rhs) {
        if (reverse) {
            return lhs.getTitle().compareTo(rhs.getTitle()) * -1;
        }
        return lhs.getTitle().compareTo(rhs.getTitle());
    }

    private void sortByDate(boolean reverse) {
        Collections.sort(this.episodes, new C0774x20798267(reverse));
        refreshTitles();
        refreshCheckboxes();
    }

    static /* synthetic */ int lambda$sortByDate$10(boolean reverse, FeedItem lhs, FeedItem rhs) {
        if (lhs.getPubDate() == null) {
            return -1;
        }
        if (rhs.getPubDate() == null) {
            return 1;
        }
        int code = lhs.getPubDate().compareTo(rhs.getPubDate());
        if (reverse) {
            return code * -1;
        }
        return code;
    }

    private void sortByDuration(boolean reverse) {
        Collections.sort(this.episodes, new C0770x6a0f1df7(reverse));
        refreshTitles();
        refreshCheckboxes();
    }

    static /* synthetic */ int lambda$sortByDuration$11(boolean reverse, FeedItem lhs, FeedItem rhs) {
        int ordering;
        if (!lhs.hasMedia()) {
            ordering = 1;
        } else if (rhs.hasMedia()) {
            ordering = lhs.getMedia().getDuration() - rhs.getMedia().getDuration();
        } else {
            ordering = -1;
        }
        if (reverse) {
            return ordering * -1;
        }
        return ordering;
    }

    private void checkAll() {
        for (FeedItem episode : this.episodes) {
            if (!this.checkedIds.contains(episode.getId())) {
                this.checkedIds.add(episode.getId());
            }
        }
        refreshCheckboxes();
    }

    private void checkNone() {
        this.checkedIds.clear();
        refreshCheckboxes();
    }

    private void checkPlayed(boolean isPlayed) {
        for (FeedItem episode : this.episodes) {
            if (episode.isPlayed() == isPlayed) {
                if (!this.checkedIds.contains(episode.getId())) {
                    this.checkedIds.add(episode.getId());
                }
            } else if (this.checkedIds.contains(episode.getId())) {
                this.checkedIds.remove(episode.getId());
            }
        }
        refreshCheckboxes();
    }

    private void checkDownloaded(boolean isDownloaded) {
        for (FeedItem episode : this.episodes) {
            if (episode.hasMedia() && episode.getMedia().isDownloaded() == isDownloaded) {
                if (!this.checkedIds.contains(episode.getId())) {
                    this.checkedIds.add(episode.getId());
                }
            } else if (this.checkedIds.contains(episode.getId())) {
                this.checkedIds.remove(episode.getId());
            }
        }
        refreshCheckboxes();
    }

    private void checkQueued(boolean isQueued) {
        for (FeedItem episode : this.episodes) {
            if (episode.isTagged(FeedItem.TAG_QUEUE) == isQueued) {
                this.checkedIds.add(episode.getId());
            } else {
                this.checkedIds.remove(episode.getId());
            }
        }
        refreshCheckboxes();
    }

    private void checkWithMedia() {
        for (FeedItem episode : this.episodes) {
            if (episode.hasMedia()) {
                this.checkedIds.add(episode.getId());
            } else {
                this.checkedIds.remove(episode.getId());
            }
        }
        refreshCheckboxes();
    }

    private void refreshTitles() {
        this.titles.clear();
        for (FeedItem episode : this.episodes) {
            this.titles.add(episode.getTitle());
        }
        this.mAdapter.notifyDataSetChanged();
    }

    private void refreshCheckboxes() {
        for (int i = 0; i < this.episodes.size(); i++) {
            this.mListView.setItemChecked(i, this.checkedIds.contains(((FeedItem) this.episodes.get(i)).getId()));
        }
        ActivityCompat.invalidateOptionsMenu(getActivity());
    }

    private void queueChecked() {
        DBWriter.addQueueItem(getActivity(), true, this.checkedIds.toArray());
        close();
    }

    private void markedCheckedPlayed() {
        DBWriter.markItemPlayed(1, this.checkedIds.toArray());
        close();
    }

    private void markedCheckedUnplayed() {
        DBWriter.markItemPlayed(0, this.checkedIds.toArray());
        close();
    }

    private void downloadChecked() {
        List<FeedItem> toDownload = new ArrayList(this.checkedIds.size());
        for (FeedItem episode : this.episodes) {
            if (this.checkedIds.contains(episode.getId())) {
                toDownload.add(episode);
            }
        }
        try {
            DBTasks.downloadFeedItems(getActivity(), (FeedItem[]) toDownload.toArray(new FeedItem[toDownload.size()]));
        } catch (DownloadRequestException e) {
            e.printStackTrace();
            DownloadRequestErrorDialogCreator.newRequestErrorDialog(getActivity(), e.getMessage());
        }
        close();
    }

    private void deleteChecked() {
        for (long id : this.checkedIds.toArray()) {
            FeedItem episode = (FeedItem) this.idMap.get(Long.valueOf(id));
            if (episode.hasMedia()) {
                DBWriter.deleteFeedMediaOfItem(getActivity(), episode.getMedia().getId());
            }
        }
        close();
    }

    private void close() {
        getActivity().getSupportFragmentManager().popBackStack();
    }
}
