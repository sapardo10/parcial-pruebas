package de.danoeh.antennapod.fragment;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.support.v7.widget.helper.ItemTouchHelper.SimpleCallback;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import de.danoeh.antennapod.adapter.AllEpisodesRecycleAdapter.Holder;
import de.danoeh.antennapod.core.event.FavoritesEvent;
import de.danoeh.antennapod.core.feed.FeedItem;
import de.danoeh.antennapod.core.storage.DBReader;
import de.danoeh.antennapod.core.storage.DBWriter;
import de.danoeh.antennapod.debug.R;
import java.util.List;

public class FavoriteEpisodesFragment extends AllEpisodesFragment {
    private static final String PREF_NAME = "PrefFavoriteEpisodesFragment";
    private static final String TAG = "FavoriteEpisodesFrag";

    protected boolean showOnlyNewEpisodes() {
        return true;
    }

    protected String getPrefName() {
        return PREF_NAME;
    }

    public void onEvent(FavoritesEvent event) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("onEvent() called with: event = [");
        stringBuilder.append(event);
        stringBuilder.append("]");
        Log.d(str, stringBuilder.toString());
        loadItems();
    }

    protected void resetViewState() {
        super.resetViewState();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = super.onCreateViewHelper(inflater, container, savedInstanceState, R.layout.all_episodes_fragment);
        new ItemTouchHelper(new SimpleCallback(0, 12) {
            public boolean onMove(RecyclerView recyclerView, ViewHolder viewHolder, ViewHolder target) {
                return false;
            }

            public void onSwiped(ViewHolder viewHolder, int swipeDir) {
                Holder holder = (Holder) viewHolder;
                String str = FavoriteEpisodesFragment.TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("remove(");
                stringBuilder.append(holder.getItemId());
                stringBuilder.append(")");
                Log.d(str, stringBuilder.toString());
                if (FavoriteEpisodesFragment.this.disposable != null) {
                    FavoriteEpisodesFragment.this.disposable.dispose();
                }
                FeedItem item = holder.getFeedItem();
                if (item != null) {
                    DBWriter.removeFavoriteItem(item);
                    Snackbar snackbar = Snackbar.make(root, FavoriteEpisodesFragment.this.getString(R.string.removed_item), 0);
                    snackbar.setAction(FavoriteEpisodesFragment.this.getString(R.string.undo), new -$$Lambda$FavoriteEpisodesFragment$1$zg0fyUR22gjxZrZnyndAAR-uVcE(item));
                    snackbar.show();
                }
            }
        }).attachToRecyclerView(this.recyclerView);
        return root;
    }

    protected List<FeedItem> loadData() {
        return DBReader.getFavoriteItemsList();
    }
}
