package de.danoeh.antennapod.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import de.danoeh.antennapod.core.asynctask.FeedRemover;
import de.danoeh.antennapod.core.dialog.ConfirmationDialog;
import de.danoeh.antennapod.core.feed.Feed;
import de.danoeh.antennapod.core.feed.FeedMedia;
import de.danoeh.antennapod.core.service.playback.PlaybackService;
import de.danoeh.antennapod.core.service.playback.PlayerStatus;
import de.danoeh.antennapod.core.util.IntentUtils;
import de.danoeh.antennapod.core.util.playback.Playable;

class MediaplayerInfoActivity$1 extends ConfirmationDialog {
    final /* synthetic */ MediaplayerInfoActivity this$0;
    final /* synthetic */ Feed val$feed;
    final /* synthetic */ FeedRemover val$remover;

    MediaplayerInfoActivity$1(MediaplayerInfoActivity this$0, Context x0, int x1, String x2, Feed feed, FeedRemover feedRemover) {
        this.this$0 = this$0;
        this.val$feed = feed;
        this.val$remover = feedRemover;
        super(x0, x1, x2);
    }

    public void onConfirmButtonPressed(DialogInterface dialog) {
        dialog.dismiss();
        if (this.this$0.controller != null) {
            Playable playable = this.this$0.controller.getMedia();
            if (playable != null && (playable instanceof FeedMedia)) {
                FeedMedia media = (FeedMedia) playable;
                if (media.getItem() != null && media.getItem().getFeed() != null) {
                    if (media.getItem().getFeed().getId() == this.val$feed.getId()) {
                        Log.d("MediaplayerInfoActivity", "Currently playing episode is about to be deleted, skipping");
                        this.val$remover.skipOnCompletion = true;
                        if (this.this$0.controller.getStatus() == PlayerStatus.PLAYING) {
                            IntentUtils.sendLocalBroadcast(this.this$0, PlaybackService.ACTION_PAUSE_PLAY_CURRENT_EPISODE);
                        }
                    }
                }
            }
        }
        this.val$remover.executeAsync();
    }
}
