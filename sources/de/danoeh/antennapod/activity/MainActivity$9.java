package de.danoeh.antennapod.activity;

import android.util.Log;
import de.danoeh.antennapod.core.feed.EventDistributor;
import de.danoeh.antennapod.core.feed.EventDistributor$EventListener;

class MainActivity$9 extends EventDistributor$EventListener {
    final /* synthetic */ MainActivity this$0;

    MainActivity$9(MainActivity this$0) {
        this.this$0 = this$0;
    }

    public void update(EventDistributor eventDistributor, Integer arg) {
        if ((arg.intValue() & 3) != 0) {
            Log.d("MainActivity", "Received contentUpdate Intent.");
            MainActivity.access$900(this.this$0);
        }
    }
}
