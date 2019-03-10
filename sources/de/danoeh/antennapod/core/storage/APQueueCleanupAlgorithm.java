package de.danoeh.antennapod.core.storage;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import de.danoeh.antennapod.core.feed.FeedItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class APQueueCleanupAlgorithm extends EpisodeCleanupAlgorithm {
    private static final String TAG = "APQueueCleanupAlgorithm";

    public int getReclaimableItems() {
        return getCandidates().size();
    }

    public int performCleanup(Context context, int numberOfEpisodesToDelete) {
        List<FeedItem> delete;
        List<FeedItem> candidates = getCandidates();
        Collections.sort(candidates, -$$Lambda$APQueueCleanupAlgorithm$X7DrYnXV_c0r_XbmcsXeA0SNAdc.INSTANCE);
        if (candidates.size() > numberOfEpisodesToDelete) {
            delete = candidates.subList(0, numberOfEpisodesToDelete);
        } else {
            delete = candidates;
        }
        for (FeedItem item : delete) {
            try {
                DBWriter.deleteFeedMediaOfItem(context, item.getMedia().getId()).get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        int counter = delete.size();
        Log.i(TAG, String.format("Auto-delete deleted %d episodes (%d requested)", new Object[]{Integer.valueOf(counter), Integer.valueOf(numberOfEpisodesToDelete)}));
        return counter;
    }

    static /* synthetic */ int lambda$performCleanup$0(FeedItem lhs, FeedItem rhs) {
        Date l = lhs.getPubDate();
        Date r = rhs.getPubDate();
        if (l == null) {
            l = new Date();
        }
        if (r == null) {
            r = new Date();
        }
        return l.compareTo(r);
    }

    @NonNull
    private List<FeedItem> getCandidates() {
        List<FeedItem> candidates = new ArrayList();
        for (FeedItem item : DBReader.getDownloadedItems()) {
            if (item.hasMedia()) {
                if (item.getMedia().isDownloaded()) {
                    if (!item.isTagged(FeedItem.TAG_QUEUE)) {
                        if (!item.isTagged(FeedItem.TAG_FAVORITE)) {
                            candidates.add(item);
                        }
                    }
                }
            }
        }
        return candidates;
    }

    public int getDefaultCleanupParameter() {
        return getNumEpisodesToCleanup(0);
    }
}
