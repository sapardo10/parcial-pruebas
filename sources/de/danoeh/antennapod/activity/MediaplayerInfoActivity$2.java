package de.danoeh.antennapod.activity;

import android.util.Log;
import de.danoeh.antennapod.core.feed.EventDistributor;
import de.danoeh.antennapod.core.feed.EventDistributor$EventListener;

class MediaplayerInfoActivity$2 extends EventDistributor$EventListener {
    final /* synthetic */ MediaplayerInfoActivity this$0;

    MediaplayerInfoActivity$2(MediaplayerInfoActivity this$0) {
        this.this$0 = this$0;
    }

    public void update(EventDistributor eventDistributor, Integer arg) {
        if ((arg.intValue() & 1) != 0) {
            Log.d("MediaplayerInfoActivity", "Received contentUpdate Intent.");
            MediaplayerInfoActivity.access$000(this.this$0);
        }
    }
}
