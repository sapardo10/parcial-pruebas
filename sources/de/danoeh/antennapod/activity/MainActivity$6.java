package de.danoeh.antennapod.activity;

import android.content.Context;
import de.danoeh.antennapod.core.asynctask.FeedRemover;
import de.danoeh.antennapod.core.feed.Feed;
import de.danoeh.antennapod.fragment.EpisodesFragment;

class MainActivity$6 extends FeedRemover {
    final /* synthetic */ MainActivity this$0;
    final /* synthetic */ int val$position;

    MainActivity$6(MainActivity this$0, Context x0, Feed x1, int i) {
        this.this$0 = this$0;
        this.val$position = i;
        super(x0, x1);
    }

    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        if (MainActivity.access$100(this.this$0) == this.val$position) {
            this.this$0.loadFragment(EpisodesFragment.TAG, null);
        }
    }
}
