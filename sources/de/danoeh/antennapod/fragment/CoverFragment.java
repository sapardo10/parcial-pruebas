package de.danoeh.antennapod.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import de.danoeh.antennapod.activity.MediaplayerInfoActivity$MediaplayerInfoContentFragment;
import de.danoeh.antennapod.core.glide.ApGlideSettings;
import de.danoeh.antennapod.core.util.playback.Playable;
import de.danoeh.antennapod.debug.R;

public class CoverFragment extends Fragment implements MediaplayerInfoActivity$MediaplayerInfoContentFragment {
    private static final String TAG = "CoverFragment";
    private ImageView imgvCover;
    private Playable media;
    private View root;
    private TextView txtvEpisodeTitle;
    private TextView txtvPodcastTitle;

    public static CoverFragment newInstance(Playable item) {
        CoverFragment f = new CoverFragment();
        f.media = item;
        return f;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.media == null) {
            Log.e(TAG, "CoverFragment was called without media");
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        this.root = inflater.inflate(R.layout.cover_fragment, container, false);
        this.txtvPodcastTitle = (TextView) this.root.findViewById(R.id.txtvPodcastTitle);
        this.txtvEpisodeTitle = (TextView) this.root.findViewById(R.id.txtvEpisodeTitle);
        this.imgvCover = (ImageView) this.root.findViewById(R.id.imgvCover);
        return this.root;
    }

    private void loadMediaInfo() {
        Playable playable = this.media;
        if (playable != null) {
            this.txtvPodcastTitle.setText(playable.getFeedTitle());
            this.txtvEpisodeTitle.setText(this.media.getEpisodeTitle());
            Glide.with((Fragment) this).load(this.media.getImageLocation()).apply(new RequestOptions().diskCacheStrategy(ApGlideSettings.AP_DISK_CACHE_STRATEGY).dontAnimate().fitCenter()).into(this.imgvCover);
            return;
        }
        Log.w(TAG, "loadMediaInfo was called while media was null");
    }

    public void onStart() {
        Log.d(TAG, "On Start");
        super.onStart();
        if (this.media != null) {
            Log.d(TAG, "Loading media info");
            loadMediaInfo();
            return;
        }
        Log.w(TAG, "Unable to load media info: media was null");
    }

    public void onDestroy() {
        super.onDestroy();
        this.root = null;
    }

    public void onMediaChanged(Playable media) {
        if (this.media != media) {
            this.media = media;
            if (isAdded()) {
                loadMediaInfo();
            }
        }
    }
}
