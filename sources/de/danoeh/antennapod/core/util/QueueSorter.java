package de.danoeh.antennapod.core.util;

import android.content.Context;
import de.danoeh.antennapod.core.feed.FeedItem;
import de.danoeh.antennapod.core.feed.FeedMedia;
import de.danoeh.antennapod.core.storage.DBWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueueSorter {

    public enum Rule {
        EPISODE_TITLE_ASC,
        EPISODE_TITLE_DESC,
        DATE_ASC,
        DATE_DESC,
        DURATION_ASC,
        DURATION_DESC,
        FEED_TITLE_ASC,
        FEED_TITLE_DESC,
        RANDOM,
        SMART_SHUFFLE_ASC,
        SMART_SHUFFLE_DESC
    }

    public static void sort(Context context, Rule rule, boolean broadcastUpdate) {
        Comparator<FeedItem> comparator = null;
        Permutor<FeedItem> permutor = null;
        switch (rule) {
            case EPISODE_TITLE_ASC:
                comparator = -$$Lambda$QueueSorter$XsOjSA3gI5DJVDwNVzt9K8XD7Cs.INSTANCE;
                break;
            case EPISODE_TITLE_DESC:
                comparator = -$$Lambda$QueueSorter$wRIqyAxqMOwp4Uq_gxjWu5fWmPU.INSTANCE;
                break;
            case DATE_ASC:
                comparator = -$$Lambda$QueueSorter$vrQ-zZlU3AabIhLAKR9ClDzKCRo.INSTANCE;
                break;
            case DATE_DESC:
                comparator = -$$Lambda$QueueSorter$IfYaa_j33bWDlRig5qth54HIxO0.INSTANCE;
                break;
            case DURATION_ASC:
                comparator = -$$Lambda$QueueSorter$K4LO2XTr8dDgKsLayCzgeYm8pSA.INSTANCE;
                break;
            case DURATION_DESC:
                comparator = -$$Lambda$QueueSorter$oluF0cxjBGsiKKXoDpypw3w6U_4.INSTANCE;
                break;
            case FEED_TITLE_ASC:
                comparator = -$$Lambda$QueueSorter$cNdKvbjRUOLgx0AYMiw3ibCVNc4.INSTANCE;
                break;
            case FEED_TITLE_DESC:
                comparator = -$$Lambda$QueueSorter$DNX58fiuVxEu9pfx2FlKy2oCeq0.INSTANCE;
                break;
            case RANDOM:
                permutor = -$$Lambda$66lmK-1uYZO5UTNwxzk72RB6hBw.INSTANCE;
                break;
            case SMART_SHUFFLE_ASC:
                permutor = -$$Lambda$QueueSorter$UWTij89d3iZgkDIju4JA9YzaH3w.INSTANCE;
                break;
            case SMART_SHUFFLE_DESC:
                permutor = -$$Lambda$QueueSorter$7aMMJCreyl5FVcgionW7s0S-Xyk.INSTANCE;
                break;
            default:
                break;
        }
        if (comparator != null) {
            DBWriter.sortQueue(comparator, broadcastUpdate);
        } else if (permutor != null) {
            DBWriter.reorderQueue(permutor, broadcastUpdate);
        }
    }

    static /* synthetic */ int lambda$sort$4(FeedItem f1, FeedItem f2) {
        FeedMedia f1Media = f1.getMedia();
        FeedMedia f2Media = f2.getMedia();
        int duration1 = f1Media != null ? f1Media.getDuration() : -1;
        int duration2 = f2Media != null ? f2Media.getDuration() : -1;
        if (duration1 != -1) {
            if (duration2 != -1) {
                return duration1 - duration2;
            }
        }
        return duration2 - duration1;
    }

    static /* synthetic */ int lambda$sort$5(FeedItem f1, FeedItem f2) {
        FeedMedia f1Media = f1.getMedia();
        FeedMedia f2Media = f2.getMedia();
        return ((f1Media != null ? f1Media.getDuration() : -1) - (f2Media != null ? f2Media.getDuration() : -1)) * -1;
    }

    private static void smartShuffle(List<FeedItem> queue, boolean ascending) {
        Map<Long, List<FeedItem>> map = new HashMap();
        while (!queue.isEmpty()) {
            FeedItem item = (FeedItem) queue.remove(0);
            Long id = Long.valueOf(item.getFeedId());
            if (!map.containsKey(id)) {
                map.put(id, new ArrayList());
            }
            ((List) map.get(id)).add(item);
        }
        Comparator<FeedItem> itemComparator = ascending ? -$$Lambda$QueueSorter$tTupnb1yIyh0u8cLyjxslMvwznY.INSTANCE : -$$Lambda$QueueSorter$mzXKVfyKouyVMqSt_uBG3NeNUuY.INSTANCE;
        for (Long id2 : map.keySet()) {
            Collections.sort((List) map.get(id2), itemComparator);
        }
        List<List<FeedItem>> feeds = new ArrayList(map.values());
        Collections.sort(feeds, -$$Lambda$QueueSorter$HvYcuvycf_ZegYcb4duB7ZS97Pk.INSTANCE);
        while (!feeds.isEmpty()) {
            for (int i = feeds.size() - 1; i >= 0; i--) {
                List<FeedItem> items = (List) feeds.get(i);
                queue.add(items.remove(0));
                if (items.isEmpty()) {
                    feeds.remove(i);
                }
            }
        }
    }
}
