package de.danoeh.antennapod.activity;

import de.danoeh.antennapod.adapter.NavListAdapter.ItemAccess;
import de.danoeh.antennapod.core.feed.Feed;

class MediaplayerInfoActivity$3 implements ItemAccess {
    final /* synthetic */ MediaplayerInfoActivity this$0;

    MediaplayerInfoActivity$3(MediaplayerInfoActivity this$0) {
        this.this$0 = this$0;
    }

    public int getCount() {
        if (MediaplayerInfoActivity.access$100(this.this$0) != null) {
            return MediaplayerInfoActivity.access$100(this.this$0).feeds.size();
        }
        return 0;
    }

    public Feed getItem(int position) {
        if (MediaplayerInfoActivity.access$100(this.this$0) == null || position < 0 || position >= MediaplayerInfoActivity.access$100(this.this$0).feeds.size()) {
            return null;
        }
        return (Feed) MediaplayerInfoActivity.access$100(this.this$0).feeds.get(position);
    }

    public int getSelectedItemIndex() {
        return -1;
    }

    public int getQueueSize() {
        return MediaplayerInfoActivity.access$100(this.this$0) != null ? MediaplayerInfoActivity.access$100(this.this$0).queueSize : 0;
    }

    public int getNumberOfNewItems() {
        return MediaplayerInfoActivity.access$100(this.this$0) != null ? MediaplayerInfoActivity.access$100(this.this$0).numNewItems : 0;
    }

    public int getNumberOfDownloadedItems() {
        return MediaplayerInfoActivity.access$100(this.this$0) != null ? MediaplayerInfoActivity.access$100(this.this$0).numDownloadedItems : 0;
    }

    public int getReclaimableItems() {
        return MediaplayerInfoActivity.access$100(this.this$0) != null ? MediaplayerInfoActivity.access$100(this.this$0).reclaimableSpace : 0;
    }

    public int getFeedCounter(long feedId) {
        return MediaplayerInfoActivity.access$100(this.this$0) != null ? MediaplayerInfoActivity.access$100(this.this$0).feedCounters.get(feedId) : 0;
    }

    public int getFeedCounterSum() {
        int i = 0;
        if (MediaplayerInfoActivity.access$100(this.this$0) == null) {
            return 0;
        }
        int sum = 0;
        int[] values = MediaplayerInfoActivity.access$100(this.this$0).feedCounters.values();
        while (i < values.length) {
            sum += values[i];
            i++;
        }
        return sum;
    }
}
