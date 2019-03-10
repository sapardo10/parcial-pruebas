package de.danoeh.antennapod.core.storage;

import android.content.Context;
import android.util.Log;
import de.danoeh.antennapod.core.feed.FeedFilter;
import de.danoeh.antennapod.core.feed.FeedItem;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.util.NetworkUtils;
import de.danoeh.antennapod.core.util.PowerUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class APDownloadAlgorithm implements AutomaticDownloadAlgorithm {
    private static final String TAG = "APDownloadAlgorithm";

    public Runnable autoDownloadUndownloadedItems(Context context) {
        return new -$$Lambda$APDownloadAlgorithm$FJzTnHPyYjeSn1Vgj3WndUMZO80(context);
    }

    static /* synthetic */ void lambda$autoDownloadUndownloadedItems$0(Context context) {
        boolean z;
        boolean networkShouldAutoDl;
        boolean powerShouldAutoDl;
        Context context2 = context;
        boolean cacheIsUnlimited = true;
        if (NetworkUtils.autodownloadNetworkAvailable()) {
            if (UserPreferences.isEnableAutodownload()) {
                z = true;
                networkShouldAutoDl = z;
                if (!PowerUtils.deviceCharging(context)) {
                    if (UserPreferences.isEnableAutodownloadOnBattery()) {
                        z = false;
                        powerShouldAutoDl = z;
                        if (!networkShouldAutoDl && powerShouldAutoDl) {
                            int episodeSpaceLeft;
                            FeedItem[] itemsToDownload;
                            String str;
                            StringBuilder stringBuilder;
                            Log.d(TAG, "Performing auto-dl of undownloaded episodes");
                            List<FeedItem> queue = DBReader.getQueue();
                            List<FeedItem> newItems = DBReader.getNewItemsList();
                            ArrayList candidates = new ArrayList(queue.size() + newItems.size());
                            candidates.addAll(queue);
                            for (FeedItem newItem : newItems) {
                                FeedFilter feedFilter = newItem.getFeed().getPreferences().getFilter();
                                if (!candidates.contains(newItem) && feedFilter.shouldAutoDownload(newItem)) {
                                    candidates.add(newItem);
                                }
                            }
                            Iterator<FeedItem> it = candidates.iterator();
                            while (it.hasNext()) {
                                if (!((FeedItem) it.next()).isAutoDownloadable()) {
                                    it.remove();
                                }
                            }
                            int autoDownloadableEpisodes = candidates.size();
                            int downloadedEpisodes = DBReader.getNumberOfDownloadedEpisodes();
                            int deletedEpisodes = UserPreferences.getEpisodeCleanupAlgorithm().makeRoomForEpisodes(context2, autoDownloadableEpisodes);
                            if (UserPreferences.getEpisodeCacheSize() != UserPreferences.getEpisodeCacheSizeUnlimited()) {
                                cacheIsUnlimited = false;
                            }
                            int episodeCacheSize = UserPreferences.getEpisodeCacheSize();
                            if (!cacheIsUnlimited) {
                                if (episodeCacheSize < downloadedEpisodes + autoDownloadableEpisodes) {
                                    episodeSpaceLeft = episodeCacheSize - (downloadedEpisodes - deletedEpisodes);
                                    itemsToDownload = (FeedItem[]) candidates.subList(0, episodeSpaceLeft).toArray(new FeedItem[episodeSpaceLeft]);
                                    str = TAG;
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append("Enqueueing ");
                                    stringBuilder.append(itemsToDownload.length);
                                    stringBuilder.append(" items for download");
                                    Log.d(str, stringBuilder.toString());
                                    DBTasks.downloadFeedItems(false, context2, itemsToDownload);
                                    return;
                                }
                            }
                            episodeSpaceLeft = autoDownloadableEpisodes;
                            itemsToDownload = (FeedItem[]) candidates.subList(0, episodeSpaceLeft).toArray(new FeedItem[episodeSpaceLeft]);
                            str = TAG;
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("Enqueueing ");
                            stringBuilder.append(itemsToDownload.length);
                            stringBuilder.append(" items for download");
                            Log.d(str, stringBuilder.toString());
                            try {
                                DBTasks.downloadFeedItems(false, context2, itemsToDownload);
                                return;
                            } catch (DownloadRequestException e) {
                                e.printStackTrace();
                                return;
                            }
                        }
                    }
                }
                z = true;
                powerShouldAutoDl = z;
                if (!networkShouldAutoDl) {
                }
            }
        }
        z = false;
        networkShouldAutoDl = z;
        if (!PowerUtils.deviceCharging(context)) {
            if (UserPreferences.isEnableAutodownloadOnBattery()) {
                z = false;
                powerShouldAutoDl = z;
                if (!networkShouldAutoDl) {
                }
            }
        }
        z = true;
        powerShouldAutoDl = z;
        if (!networkShouldAutoDl) {
        }
    }
}
