package de.danoeh.antennapod.activity;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import de.danoeh.antennapod.core.util.playback.Playable;
import de.danoeh.antennapod.core.util.playback.PlaybackController;
import de.danoeh.antennapod.fragment.ChaptersFragment;
import de.danoeh.antennapod.fragment.CoverFragment;
import de.danoeh.antennapod.fragment.ItemDescriptionFragment;

class MediaplayerInfoActivity$MediaplayerInfoPagerAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = "MPInfoPagerAdapter";
    private ChaptersFragment chaptersFragment;
    private PlaybackController controller;
    private CoverFragment coverFragment;
    private ItemDescriptionFragment itemDescriptionFragment;
    private Playable media;

    public MediaplayerInfoActivity$MediaplayerInfoPagerAdapter(FragmentManager fm, Playable media) {
        super(fm);
        this.media = media;
    }

    public void onMediaChanged(Playable media) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("media changing to ");
        stringBuilder.append(media != null ? media.getEpisodeTitle() : "null");
        Log.d(str, stringBuilder.toString());
        this.media = media;
        CoverFragment coverFragment = this.coverFragment;
        if (coverFragment != null) {
            coverFragment.onMediaChanged(media);
        }
        ItemDescriptionFragment itemDescriptionFragment = this.itemDescriptionFragment;
        if (itemDescriptionFragment != null) {
            itemDescriptionFragment.onMediaChanged(media);
        }
        ChaptersFragment chaptersFragment = this.chaptersFragment;
        if (chaptersFragment != null) {
            chaptersFragment.onMediaChanged(media);
        }
    }

    public void setController(PlaybackController controller) {
        this.controller = controller;
        ChaptersFragment chaptersFragment = this.chaptersFragment;
        if (chaptersFragment != null) {
            chaptersFragment.setController(controller);
        }
    }

    @Nullable
    public ChaptersFragment getChaptersFragment() {
        return this.chaptersFragment;
    }

    public Fragment getItem(int position) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("getItem(");
        stringBuilder.append(position);
        stringBuilder.append(")");
        Log.d(str, stringBuilder.toString());
        switch (position) {
            case 0:
                if (this.coverFragment == null) {
                    this.coverFragment = CoverFragment.newInstance(this.media);
                }
                return this.coverFragment;
            case 1:
                if (this.itemDescriptionFragment == null) {
                    this.itemDescriptionFragment = ItemDescriptionFragment.newInstance(this.media, true, true);
                }
                return this.itemDescriptionFragment;
            case 2:
                if (this.chaptersFragment == null) {
                    this.chaptersFragment = ChaptersFragment.newInstance(this.media);
                    this.chaptersFragment.setController(this.controller);
                }
                return this.chaptersFragment;
            default:
                return null;
        }
    }

    public int getCount() {
        return 3;
    }
}
