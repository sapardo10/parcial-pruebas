package de.danoeh.antennapod.core.util;

import de.danoeh.antennapod.core.feed.FeedItem;
import java.util.List;

public class FeedItemUtil {
    private FeedItemUtil() {
    }

    public static int indexOfItemWithDownloadUrl(List<FeedItem> items, String downloadUrl) {
        if (items == null) {
            return -1;
        }
        for (int i = 0; i < items.size(); i++) {
            FeedItem item = (FeedItem) items.get(i);
            if (item.hasMedia() && item.getMedia().getDownload_url().equals(downloadUrl)) {
                return i;
            }
        }
        return -1;
    }

    public static int indexOfItemWithId(List<FeedItem> items, long id) {
        for (int i = 0; i < items.size(); i++) {
            FeedItem item = (FeedItem) items.get(i);
            if (item != null && item.getId() == id) {
                return i;
            }
        }
        return -1;
    }

    public static int indexOfItemWithMediaId(List<FeedItem> items, long mediaId) {
        for (int i = 0; i < items.size(); i++) {
            FeedItem item = (FeedItem) items.get(i);
            if (item != null && item.getMedia() != null && item.getMedia().getId() == mediaId) {
                return i;
            }
        }
        return -1;
    }

    public static long[] getIds(FeedItem... items) {
        if (items != null) {
            if (items.length != 0) {
                long[] result = new long[items.length];
                for (int i = 0; i < items.length; i++) {
                    result[i] = items[i].getId();
                }
                return result;
            }
        }
        return new long[0];
    }

    public static long[] getIds(List<FeedItem> items) {
        if (items != null) {
            if (items.size() != 0) {
                long[] result = new long[items.size()];
                for (int i = 0; i < items.size(); i++) {
                    result[i] = ((FeedItem) items.get(i)).getId();
                }
                return result;
            }
        }
        return new long[0];
    }

    public static boolean containsAnyId(List<FeedItem> items, long[] ids) {
        if (items != null) {
            if (items.size() != 0) {
                for (FeedItem item : items) {
                    for (long id : ids) {
                        if (item.getId() == id) {
                            return true;
                        }
                    }
                }
                return false;
            }
        }
        return false;
    }

    public static String getLinkWithFallback(FeedItem item) {
        if (item == null) {
            return null;
        }
        if (item.getLink() != null) {
            return item.getLink();
        }
        if (item.getFeed() != null) {
            return item.getFeed().getLink();
        }
        return null;
    }
}
