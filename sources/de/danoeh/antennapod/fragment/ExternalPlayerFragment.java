package de.danoeh.antennapod.fragment;

import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import de.danoeh.antennapod.core.feed.MediaType;
import de.danoeh.antennapod.core.glide.ApGlideSettings;
import de.danoeh.antennapod.core.service.playback.PlaybackService;
import de.danoeh.antennapod.core.util.playback.Playable;
import de.danoeh.antennapod.core.util.playback.PlaybackController;
import de.danoeh.antennapod.debug.R;
import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ExternalPlayerFragment extends Fragment {
    public static final String TAG = "ExternalPlayerFragment";
    private ImageButton butPlay;
    private PlaybackController controller;
    private Disposable disposable;
    private ViewGroup fragmentLayout;
    private ImageView imgvCover;
    private TextView mFeedName;
    private ProgressBar mProgressBar;
    private TextView txtvTitle;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.external_player_fragment, container, false);
        this.fragmentLayout = (ViewGroup) root.findViewById(R.id.fragmentLayout);
        this.imgvCover = (ImageView) root.findViewById(R.id.imgvCover);
        this.txtvTitle = (TextView) root.findViewById(R.id.txtvTitle);
        this.butPlay = (ImageButton) root.findViewById(R.id.butPlay);
        this.mFeedName = (TextView) root.findViewById(R.id.txtvAuthor);
        this.mProgressBar = (ProgressBar) root.findViewById(R.id.episodeProgress);
        this.fragmentLayout.setOnClickListener(new -$$Lambda$ExternalPlayerFragment$HdX78Xv9lOkx9fZIdcrLnLRdIxo());
        return root;
    }

    public static /* synthetic */ void lambda$onCreateView$0(ExternalPlayerFragment externalPlayerFragment, View v) {
        Log.d(TAG, "layoutInfo was clicked");
        PlaybackController playbackController = externalPlayerFragment.controller;
        if (playbackController != null && playbackController.getMedia() != null) {
            Intent intent = PlaybackService.getPlayerActivityIntent(externalPlayerFragment.getActivity(), externalPlayerFragment.controller.getMedia());
            if (VERSION.SDK_INT < 16 || externalPlayerFragment.controller.getMedia().getMediaType() != MediaType.AUDIO) {
                externalPlayerFragment.startActivity(intent);
            } else {
                externalPlayerFragment.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(externalPlayerFragment.getActivity(), externalPlayerFragment.imgvCover, "coverTransition").toBundle());
            }
        }
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.controller = setupPlaybackController();
        this.butPlay.setOnClickListener(new -$$Lambda$ExternalPlayerFragment$pjQuYFAveAQrDDms49VSCPP4rgI());
        loadMediaInfo();
    }

    public static /* synthetic */ void lambda$onActivityCreated$1(ExternalPlayerFragment externalPlayerFragment, View v) {
        PlaybackController playbackController = externalPlayerFragment.controller;
        if (playbackController != null) {
            playbackController.playPause();
        }
    }

    public void connectToPlaybackService() {
        this.controller.init();
    }

    private PlaybackController setupPlaybackController() {
        return new PlaybackController(getActivity(), true) {
            public void onPositionObserverUpdate() {
                ExternalPlayerFragment.this.onPositionObserverUpdate();
            }

            public ImageButton getPlayButton() {
                return ExternalPlayerFragment.this.butPlay;
            }

            public boolean loadMediaInfo() {
                ExternalPlayerFragment fragment = ExternalPlayerFragment.this;
                if (fragment != null) {
                    return fragment.loadMediaInfo();
                }
                return false;
            }

            public void onShutdownNotification() {
                ExternalPlayerFragment.this.playbackDone();
            }

            public void onPlaybackEnd() {
                ExternalPlayerFragment.this.playbackDone();
            }
        };
    }

    public void onResume() {
        super.onResume();
        onPositionObserverUpdate();
        this.controller.init();
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Fragment is about to be destroyed");
        PlaybackController playbackController = this.controller;
        if (playbackController != null) {
            playbackController.release();
        }
        Disposable disposable = this.disposable;
        if (disposable != null) {
            disposable.dispose();
        }
    }

    public void onPause() {
        super.onPause();
        PlaybackController playbackController = this.controller;
        if (playbackController != null) {
            playbackController.pause();
        }
    }

    private void playbackDone() {
        ViewGroup viewGroup = this.fragmentLayout;
        if (viewGroup != null) {
            viewGroup.setVisibility(8);
        }
        PlaybackController playbackController = this.controller;
        if (playbackController != null) {
            playbackController.release();
        }
        this.controller = setupPlaybackController();
        ImageButton imageButton = this.butPlay;
        if (imageButton != null) {
            imageButton.setOnClickListener(new -$$Lambda$ExternalPlayerFragment$GtLxqSh2MKJyMreYYZv5e0cwrk0());
        }
        this.controller.init();
    }

    public static /* synthetic */ void lambda$playbackDone$2(ExternalPlayerFragment externalPlayerFragment, View v) {
        PlaybackController playbackController = externalPlayerFragment.controller;
        if (playbackController != null) {
            playbackController.playPause();
        }
    }

    private boolean loadMediaInfo() {
        Log.d(TAG, "Loading media info");
        if (this.controller == null) {
            Log.w(TAG, "loadMediaInfo was called while PlaybackController was null!");
            return false;
        }
        Disposable disposable = this.disposable;
        if (disposable != null) {
            disposable.dispose();
        }
        this.disposable = Maybe.create(new -$$Lambda$ExternalPlayerFragment$9XuNBeu6IxZhGAa_Uc5vmAzBSrA()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new -$$Lambda$ExternalPlayerFragment$9-rstjPtxYoNsV1vRS5gycgklsY(), -$$Lambda$ExternalPlayerFragment$pirSMj1beYymn2od-DY3yVPgnsw.INSTANCE);
        return true;
    }

    public static /* synthetic */ void lambda$loadMediaInfo$3(ExternalPlayerFragment externalPlayerFragment, MaybeEmitter emitter) throws Exception {
        Playable media = externalPlayerFragment.controller.getMedia();
        if (media != null) {
            emitter.onSuccess(media);
        } else {
            emitter.onComplete();
        }
    }

    private void updateUi(Playable media) {
        if (media != null) {
            this.txtvTitle.setText(media.getEpisodeTitle());
            this.mFeedName.setText(media.getFeedTitle());
            onPositionObserverUpdate();
            Glide.with(getActivity()).load(media.getImageLocation()).apply(new RequestOptions().placeholder((int) R.color.light_gray).error((int) R.color.light_gray).diskCacheStrategy(ApGlideSettings.AP_DISK_CACHE_STRATEGY).fitCenter().dontAnimate()).into(this.imgvCover);
            this.fragmentLayout.setVisibility(0);
            if (this.controller.isPlayingVideoLocally()) {
                this.butPlay.setVisibility(8);
                return;
            } else {
                this.butPlay.setVisibility(0);
                return;
            }
        }
        Log.w(TAG, "loadMediaInfo was called while the media object of playbackService was null!");
    }

    public PlaybackController getPlaybackControllerTestingOnly() {
        return this.controller;
    }

    private void onPositionObserverUpdate() {
        if (this.controller.getPosition() != -1) {
            if (this.controller.getDuration() != -1) {
                ProgressBar progressBar = this.mProgressBar;
                double position = (double) this.controller.getPosition();
                double duration = (double) this.controller.getDuration();
                Double.isNaN(position);
                Double.isNaN(duration);
                progressBar.setProgress((int) ((position / duration) * 100.0d));
            }
        }
    }
}
