package de.danoeh.antennapod.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import de.danoeh.antennapod.core.asynctask.FeedRemover;
import de.danoeh.antennapod.core.dialog.ConfirmationDialog;
import de.danoeh.antennapod.core.feed.Feed;
import de.danoeh.antennapod.core.preferences.PlaybackPreferences;
import de.danoeh.antennapod.core.service.playback.PlaybackService;
import de.danoeh.antennapod.core.util.FeedItemUtil;
import de.danoeh.antennapod.core.util.IntentUtils;

class MainActivity$7 extends ConfirmationDialog {
    final /* synthetic */ MainActivity this$0;
    final /* synthetic */ Feed val$feed;
    final /* synthetic */ FeedRemover val$remover;

    MainActivity$7(MainActivity this$0, Context x0, int x1, String x2, Feed feed, FeedRemover feedRemover) {
        this.this$0 = this$0;
        this.val$feed = feed;
        this.val$remover = feedRemover;
        super(x0, x1, x2);
    }

    public void onConfirmButtonPressed(DialogInterface dialog) {
        dialog.dismiss();
        long mediaId = PlaybackPreferences.getCurrentlyPlayingFeedMediaId();
        if (mediaId > 0) {
            if (FeedItemUtil.indexOfItemWithMediaId(this.val$feed.getItems(), mediaId) >= 0) {
                Log.d("MainActivity", "Currently playing episode is about to be deleted, skipping");
                this.val$remover.skipOnCompletion = true;
                if (PlaybackPreferences.getCurrentPlayerStatus() == 1) {
                    IntentUtils.sendLocalBroadcast(this.this$0, PlaybackService.ACTION_PAUSE_PLAY_CURRENT_EPISODE);
                }
            }
        }
        this.val$remover.executeAsync();
    }
}
