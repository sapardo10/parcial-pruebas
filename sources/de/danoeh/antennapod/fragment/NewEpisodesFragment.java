package de.danoeh.antennapod.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.support.v7.widget.helper.ItemTouchHelper.SimpleCallback;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import de.danoeh.antennapod.adapter.AllEpisodesRecycleAdapter.Holder;
import de.danoeh.antennapod.adapter.AllEpisodesRecycleAdapter.ItemTouchHelperViewHolder;
import de.danoeh.antennapod.core.feed.FeedItem;
import de.danoeh.antennapod.core.storage.DBReader;
import de.danoeh.antennapod.debug.R;
import java.util.List;

public class NewEpisodesFragment extends AllEpisodesFragment {
    private static final String PREF_NAME = "PrefNewEpisodesFragment";
    public static final String TAG = "NewEpisodesFragment";

    protected boolean showOnlyNewEpisodes() {
        return true;
    }

    protected String getPrefName() {
        return PREF_NAME;
    }

    protected void resetViewState() {
        super.resetViewState();
    }

    protected boolean shouldUpdatedItemRemainInList(FeedItem item) {
        return item.isNew();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateViewHelper(inflater, container, savedInstanceState, R.layout.all_episodes_fragment);
        new ItemTouchHelper(new SimpleCallback(0, 12) {
            public boolean onMove(RecyclerView recyclerView, ViewHolder viewHolder, ViewHolder target) {
                return false;
            }

            public void onSwiped(ViewHolder viewHolder, int swipeDir) {
                NewEpisodesFragment.this.markItemAsSeenWithUndo(((Holder) viewHolder).getFeedItem());
            }

            public void onSelectedChanged(ViewHolder viewHolder, int actionState) {
                if (actionState != 0) {
                    if (viewHolder instanceof ItemTouchHelperViewHolder) {
                        ((ItemTouchHelperViewHolder) viewHolder).onItemSelected();
                    }
                }
                super.onSelectedChanged(viewHolder, actionState);
            }

            public void clearView(RecyclerView recyclerView, ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                if (viewHolder instanceof ItemTouchHelperViewHolder) {
                    ((ItemTouchHelperViewHolder) viewHolder).onItemClear();
                }
            }
        }).attachToRecyclerView(this.recyclerView);
        return root;
    }

    protected List<FeedItem> loadData() {
        return DBReader.getNewItemsList();
    }
}
