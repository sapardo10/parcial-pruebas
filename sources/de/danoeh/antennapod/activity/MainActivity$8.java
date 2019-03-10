package de.danoeh.antennapod.activity;

import de.danoeh.antennapod.adapter.NavListAdapter.ItemAccess;
import de.danoeh.antennapod.core.feed.Feed;

class MainActivity$8 implements ItemAccess {
    final /* synthetic */ MainActivity this$0;

    MainActivity$8(MainActivity this$0) {
        this.this$0 = this$0;
    }

    public int getCount() {
        if (MainActivity.access$800(this.this$0) != null) {
            return MainActivity.access$800(this.this$0).feeds.size();
        }
        return 0;
    }

    public Feed getItem(int position) {
        if (MainActivity.access$800(this.this$0) == null || position < 0 || position >= MainActivity.access$800(this.this$0).feeds.size()) {
            return null;
        }
        return (Feed) MainActivity.access$800(this.this$0).feeds.get(position);
    }

    public int getSelectedItemIndex() {
        return MainActivity.access$000(this.this$0);
    }

    public int getQueueSize() {
        return MainActivity.access$800(this.this$0) != null ? MainActivity.access$800(this.this$0).queueSize : 0;
    }

    public int getNumberOfNewItems() {
        return MainActivity.access$800(this.this$0) != null ? MainActivity.access$800(this.this$0).numNewItems : 0;
    }

    public int getNumberOfDownloadedItems() {
        return MainActivity.access$800(this.this$0) != null ? MainActivity.access$800(this.this$0).numDownloadedItems : 0;
    }

    public int getReclaimableItems() {
        return MainActivity.access$800(this.this$0) != null ? MainActivity.access$800(this.this$0).reclaimableSpace : 0;
    }

    public int getFeedCounter(long feedId) {
        return MainActivity.access$800(this.this$0) != null ? MainActivity.access$800(this.this$0).feedCounters.get(feedId) : 0;
    }

    public int getFeedCounterSum() {
        int i = 0;
        if (MainActivity.access$800(this.this$0) == null) {
            return 0;
        }
        int sum = 0;
        int[] values = MainActivity.access$800(this.this$0).feedCounters.values();
        while (i < values.length) {
            sum += values[i];
            i++;
        }
        return sum;
    }
}
