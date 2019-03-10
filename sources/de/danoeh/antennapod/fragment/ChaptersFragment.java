package de.danoeh.antennapod.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import de.danoeh.antennapod.activity.MediaplayerInfoActivity$MediaplayerInfoContentFragment;
import de.danoeh.antennapod.adapter.ChaptersListAdapter;
import de.danoeh.antennapod.core.feed.Chapter;
import de.danoeh.antennapod.core.util.playback.Playable;
import de.danoeh.antennapod.core.util.playback.PlaybackController;
import de.danoeh.antennapod.debug.R;

public class ChaptersFragment extends ListFragment implements MediaplayerInfoActivity$MediaplayerInfoContentFragment {
    private static final String TAG = "ChaptersFragment";
    private ChaptersListAdapter adapter;
    private PlaybackController controller;
    private Playable media;

    public static ChaptersFragment newInstance(Playable media) {
        ChaptersFragment f = new ChaptersFragment();
        f.media = media;
        return f;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView lv = getListView();
        lv.setClipToPadding(false);
        int vertPadding = getResources().getDimensionPixelSize(R.dimen.list_vertical_padding);
        lv.setPadding(0, vertPadding, 0, vertPadding);
        this.adapter = new ChaptersListAdapter(getActivity(), 0, new -$$Lambda$ChaptersFragment$aVJiRtAna1ms1O5LvLEKuX-eSiA());
        setListAdapter(this.adapter);
    }

    public static /* synthetic */ void lambda$onViewCreated$0(ChaptersFragment chaptersFragment, int pos) {
        if (chaptersFragment.controller == null) {
            Log.d(TAG, "controller is null");
            return;
        }
        chaptersFragment.controller.seekToChapter((Chapter) chaptersFragment.getListAdapter().getItem(pos));
    }

    public void onResume() {
        super.onResume();
        this.adapter.setMedia(this.media);
        this.adapter.notifyDataSetChanged();
        Playable playable = this.media;
        if (playable != null) {
            if (playable.getChapters() != null) {
                setEmptyText(null);
                return;
            }
        }
        setEmptyText(getString(R.string.no_chapters_label));
    }

    public void onDestroy() {
        super.onDestroy();
        this.adapter = null;
        this.controller = null;
    }

    public void onMediaChanged(Playable media) {
        if (this.media != media) {
            this.media = media;
            ChaptersListAdapter chaptersListAdapter = this.adapter;
            if (chaptersListAdapter != null) {
                chaptersListAdapter.setMedia(media);
                this.adapter.notifyDataSetChanged();
                if (!(media == null || media.getChapters() == null)) {
                    if (media.getChapters().size() != 0) {
                        setEmptyText(null);
                    }
                }
                setEmptyText(getString(R.string.no_items_label));
            }
        }
    }

    public void setController(PlaybackController controller) {
        this.controller = controller;
    }
}
