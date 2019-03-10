package de.danoeh.antennapod.core.storage;

import android.app.backup.BackupManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;
import de.danoeh.antennapod.core.C0734R;
import de.danoeh.antennapod.core.ClientConfig;
import de.danoeh.antennapod.core.asynctask.FlattrClickWorker;
import de.danoeh.antennapod.core.event.FavoritesEvent;
import de.danoeh.antennapod.core.event.FeedItemEvent;
import de.danoeh.antennapod.core.event.MessageEvent;
import de.danoeh.antennapod.core.event.QueueEvent;
import de.danoeh.antennapod.core.feed.EventDistributor;
import de.danoeh.antennapod.core.feed.Feed;
import de.danoeh.antennapod.core.feed.FeedEvent;
import de.danoeh.antennapod.core.feed.FeedItem;
import de.danoeh.antennapod.core.feed.FeedItem.State;
import de.danoeh.antennapod.core.feed.FeedMedia;
import de.danoeh.antennapod.core.feed.FeedPreferences;
import de.danoeh.antennapod.core.gpoddernet.model.GpodnetEpisodeAction.Action;
import de.danoeh.antennapod.core.gpoddernet.model.GpodnetEpisodeAction.Builder;
import de.danoeh.antennapod.core.preferences.GpodnetPreferences;
import de.danoeh.antennapod.core.preferences.PlaybackPreferences;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.service.download.DownloadStatus;
import de.danoeh.antennapod.core.service.playback.PlaybackService;
import de.danoeh.antennapod.core.util.IntentUtils;
import de.danoeh.antennapod.core.util.LongList;
import de.danoeh.antennapod.core.util.Permutor;
import de.danoeh.antennapod.core.util.flattr.FlattrStatus;
import de.danoeh.antennapod.core.util.flattr.FlattrThing;
import de.danoeh.antennapod.core.util.flattr.SimpleFlattrThing;
import de.greenrobot.event.EventBus;
import io.reactivex.annotations.NonNull;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.shredzone.flattr4j.model.Flattr;

public class DBWriter {
    private static final String TAG = "DBWriter";
    private static final ExecutorService dbExec = Executors.newSingleThreadExecutor(-$$Lambda$DBWriter$bwbmS3vkom7xpjU6j_enwe0fZDg.INSTANCE);

    static /* synthetic */ Thread lambda$static$0(Runnable r) {
        Thread t = new Thread(r);
        t.setPriority(1);
        return t;
    }

    private DBWriter() {
    }

    public static Future<?> deleteFeedMediaOfItem(@NonNull Context context, long mediaId) {
        return dbExec.submit(new -$$Lambda$DBWriter$ZILEMLr4Y0iLF0CD9Zd9jO5Ei54(mediaId, context));
    }

    static /* synthetic */ void lambda$deleteFeedMediaOfItem$1(long mediaId, @NonNull Context context) {
        FeedMedia media = DBReader.getFeedMedia(mediaId);
        if (media == null) {
            return;
        }
        if (deleteFeedMediaSynchronous(context, media) && UserPreferences.shouldDeleteRemoveFromQueue()) {
            removeQueueItemSynchronous(context, media.getItem(), false);
        }
    }

    private static boolean deleteFeedMediaSynchronous(@NonNull Context context, @NonNull FeedMedia media) {
        Log.i(TAG, String.format("Requested to delete FeedMedia [id=%d, title=%s, downloaded=%s", new Object[]{Long.valueOf(media.getId()), media.getEpisodeTitle(), String.valueOf(media.isDownloaded())}));
        if (media.isDownloaded()) {
            File mediaFile = new File(media.getFile_url());
            if (!mediaFile.exists() || mediaFile.delete()) {
                media.setDownloaded(false);
                media.setFile_url(null);
                media.setHasEmbeddedPicture(Boolean.valueOf(false));
                PodDBAdapter adapter = PodDBAdapter.getInstance();
                adapter.open();
                adapter.setMedia(media);
                adapter.close();
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                if (PlaybackPreferences.getCurrentlyPlayingMedia() == 1) {
                    if (media.getId() == PlaybackPreferences.getCurrentlyPlayingFeedMediaId()) {
                        Editor editor = prefs.edit();
                        editor.putBoolean(PlaybackPreferences.PREF_CURRENT_EPISODE_IS_STREAM, true);
                        editor.commit();
                    }
                    if (PlaybackPreferences.getCurrentlyPlayingFeedMediaId() == media.getId()) {
                        IntentUtils.sendLocalBroadcast(context, PlaybackService.ACTION_SHUTDOWN_PLAYBACK_SERVICE);
                    }
                }
                if (GpodnetPreferences.loggedIn()) {
                    GpodnetPreferences.enqueueEpisodeAction(new Builder(media.getItem(), Action.DELETE).currentDeviceId().currentTimestamp().build());
                }
            } else {
                EventBus.getDefault().post(new MessageEvent(context.getString(C0734R.string.delete_failed)));
                return false;
            }
        }
        EventBus.getDefault().post(FeedItemEvent.deletedMedia(Collections.singletonList(media.getItem())));
        EventDistributor.getInstance().sendUnreadItemsUpdateBroadcast();
        return true;
    }

    public static Future<?> deleteFeed(Context context, long feedId) {
        return dbExec.submit(new -$$Lambda$DBWriter$LBFnodfBoXOtHGtyJQeM2lOHGK0(context, feedId));
    }

    static /* synthetic */ void lambda$deleteFeed$2(Context context, long feedId) {
        DownloadRequester requester = DownloadRequester.getInstance();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        Feed feed = DBReader.getFeed(feedId);
        if (feed != null) {
            if (PlaybackPreferences.getCurrentlyPlayingMedia() == 1) {
                if (PlaybackPreferences.getLastPlayedFeedId() == feed.getId()) {
                    IntentUtils.sendLocalBroadcast(context, PlaybackService.ACTION_SHUTDOWN_PLAYBACK_SERVICE);
                    Editor editor = prefs.edit();
                    editor.putLong(PlaybackPreferences.PREF_CURRENTLY_PLAYING_FEED_ID, -1);
                    editor.commit();
                }
            }
            List<FeedItem> queue = DBReader.getQueue();
            List<FeedItem> removed = new ArrayList();
            if (feed.getItems() == null) {
                DBReader.getFeedItemList(feed);
            }
            for (FeedItem item : feed.getItems()) {
                if (queue.remove(item)) {
                    removed.add(item);
                }
                if (item.getState() == State.PLAYING && PlaybackService.isRunning) {
                    context.stopService(new Intent(context, PlaybackService.class));
                }
                if (item.getMedia() != null) {
                    if (item.getMedia().isDownloaded()) {
                        new File(item.getMedia().getFile_url()).delete();
                    }
                }
                if (item.getMedia() != null) {
                    if (requester.isDownloadingFile(item.getMedia())) {
                        requester.cancelDownload(context, item.getMedia());
                    }
                }
            }
            PodDBAdapter adapter = PodDBAdapter.getInstance();
            adapter.open();
            if (removed.size() > 0) {
                adapter.setQueue(queue);
                for (FeedItem item2 : removed) {
                    EventBus.getDefault().post(QueueEvent.irreversibleRemoved(item2));
                }
            }
            adapter.removeFeed(feed);
            adapter.close();
            if (ClientConfig.gpodnetCallbacks.gpodnetEnabled()) {
                GpodnetPreferences.addRemovedFeed(feed.getDownload_url());
            }
            EventDistributor.getInstance().sendFeedUpdateBroadcast();
            EventDistributor.getInstance().sendDownloadLogUpdateBroadcast();
            new BackupManager(context).dataChanged();
        }
    }

    public static Future<?> clearPlaybackHistory() {
        return dbExec.submit(-$$Lambda$DBWriter$ti94c51BAPfLlZFN2dCJrapeTlc.INSTANCE);
    }

    static /* synthetic */ void lambda$clearPlaybackHistory$3() {
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        adapter.clearPlaybackHistory();
        adapter.close();
        EventDistributor.getInstance().sendPlaybackHistoryUpdateBroadcast();
    }

    public static Future<?> clearDownloadLog() {
        return dbExec.submit(-$$Lambda$DBWriter$QgC8_PiOfewFTHm7KTZ7QTyQRfY.INSTANCE);
    }

    static /* synthetic */ void lambda$clearDownloadLog$4() {
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        adapter.clearDownloadLog();
        adapter.close();
        EventDistributor.getInstance().sendDownloadLogUpdateBroadcast();
    }

    public static Future<?> addItemToPlaybackHistory(FeedMedia media) {
        return dbExec.submit(new -$$Lambda$DBWriter$p2J-J0P4qHVbGpiW27CB5E6bB6w(media));
    }

    static /* synthetic */ void lambda$addItemToPlaybackHistory$5(FeedMedia media) {
        Log.d(TAG, "Adding new item to playback history");
        media.setPlaybackCompletionDate(new Date());
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        adapter.setFeedMediaPlaybackCompletionDate(media);
        adapter.close();
        EventDistributor.getInstance().sendPlaybackHistoryUpdateBroadcast();
    }

    public static Future<?> addDownloadStatus(DownloadStatus status) {
        return dbExec.submit(new -$$Lambda$DBWriter$ySZMqYxWfPDgDDGW83kKqpzMNOc(status));
    }

    static /* synthetic */ void lambda$addDownloadStatus$6(DownloadStatus status) {
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        adapter.setDownloadStatus(status);
        adapter.close();
        EventDistributor.getInstance().sendDownloadLogUpdateBroadcast();
    }

    public static Future<?> addQueueItemAt(Context context, long itemId, int index, boolean performAutoDownload) {
        return dbExec.submit(new -$$Lambda$DBWriter$oB1x-aSqUm2lZy1rdYsmLhT3G40(itemId, index, performAutoDownload, context));
    }

    static /* synthetic */ void lambda$addQueueItemAt$7(long itemId, int index, boolean performAutoDownload, Context context) {
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        List<FeedItem> queue = DBReader.getQueue(adapter);
        if (queue != null) {
            if (!itemListContains(queue, itemId)) {
                FeedItem item = DBReader.getFeedItem(itemId);
                if (item != null) {
                    queue.add(index, item);
                    adapter.setQueue(queue);
                    item.addTag(FeedItem.TAG_QUEUE);
                    EventBus.getDefault().post(QueueEvent.added(item, index));
                    EventBus.getDefault().post(FeedItemEvent.updated(item));
                    if (item.isNew()) {
                        markItemPlayed(0, item.getId());
                    }
                }
            }
        }
        adapter.close();
        if (performAutoDownload) {
            DBTasks.autodownloadUndownloadedItems(context);
        }
    }

    public static Future<?> addQueueItem(Context context, FeedItem... items) {
        LongList itemIds = new LongList(items.length);
        for (FeedItem item : items) {
            itemIds.add(item.getId());
            item.addTag(FeedItem.TAG_QUEUE);
        }
        return addQueueItem(context, false, itemIds.toArray());
    }

    public static Future<?> addQueueItem(Context context, boolean performAutoDownload, long... itemIds) {
        return dbExec.submit(new -$$Lambda$DBWriter$sktGi0w4HVfv5xxvUlAGyq3boVQ(itemIds, performAutoDownload, context));
    }

    static /* synthetic */ void lambda$addQueueItem$8(long[] itemIds, boolean performAutoDownload, Context context) {
        if (itemIds.length > 0) {
            PodDBAdapter adapter = PodDBAdapter.getInstance();
            adapter.open();
            List<FeedItem> queue = DBReader.getQueue(adapter);
            if (queue != null) {
                boolean queueModified = false;
                LongList markAsUnplayedIds = new LongList();
                List<QueueEvent> events = new ArrayList();
                List updatedItems = new ArrayList();
                for (int i = 0; i < itemIds.length; i++) {
                    if (!itemListContains(queue, itemIds[i])) {
                        FeedItem item = DBReader.getFeedItem(itemIds[i]);
                        if (item != null) {
                            if (UserPreferences.enqueueAtFront()) {
                                queue.add(i, item);
                                events.add(QueueEvent.added(item, i));
                            } else {
                                queue.add(item);
                                events.add(QueueEvent.added(item, queue.size() - 1));
                            }
                            item.addTag(FeedItem.TAG_QUEUE);
                            updatedItems.add(item);
                            queueModified = true;
                            if (item.isNew()) {
                                markAsUnplayedIds.add(item.getId());
                            }
                        }
                    }
                }
                if (queueModified) {
                    adapter.setQueue(queue);
                    for (QueueEvent event : events) {
                        EventBus.getDefault().post(event);
                    }
                    EventBus.getDefault().post(FeedItemEvent.updated(updatedItems));
                    if (markAsUnplayedIds.size() > 0) {
                        markItemPlayed(0, markAsUnplayedIds.toArray());
                    }
                }
            }
            adapter.close();
            if (performAutoDownload) {
                DBTasks.autodownloadUndownloadedItems(context);
            }
        }
    }

    public static Future<?> clearQueue() {
        return dbExec.submit(-$$Lambda$DBWriter$XnJFakrUMMbhPYRB82tsWWhj0VE.INSTANCE);
    }

    static /* synthetic */ void lambda$clearQueue$9() {
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        adapter.clearQueue();
        adapter.close();
        EventBus.getDefault().post(QueueEvent.cleared());
    }

    public static Future<?> removeQueueItem(Context context, FeedItem item, boolean performAutoDownload) {
        return dbExec.submit(new -$$Lambda$DBWriter$rqGdwFQgNnLpkem94Vy1PQAa0dI(context, item, performAutoDownload));
    }

    private static void removeQueueItemSynchronous(Context context, FeedItem item, boolean performAutoDownload) {
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        List<FeedItem> queue = DBReader.getQueue(adapter);
        if (queue != null) {
            int position = queue.indexOf(item);
            if (position >= 0) {
                queue.remove(position);
                adapter.setQueue(queue);
                item.removeTag(FeedItem.TAG_QUEUE);
                EventBus.getDefault().post(QueueEvent.removed(item));
                EventBus.getDefault().post(FeedItemEvent.updated(item));
            } else {
                Log.w(TAG, "Queue was not modified by call to removeQueueItem");
            }
        } else {
            Log.e(TAG, "removeQueueItem: Could not load queue");
        }
        adapter.close();
        if (performAutoDownload) {
            DBTasks.autodownloadUndownloadedItems(context);
        }
    }

    public static Future<?> addFavoriteItem(FeedItem item) {
        return dbExec.submit(new -$$Lambda$DBWriter$OkPvoEuxvF1RBIjmvy0_TvbuLKM(item));
    }

    static /* synthetic */ void lambda$addFavoriteItem$11(FeedItem item) {
        PodDBAdapter adapter = PodDBAdapter.getInstance().open();
        adapter.addFavoriteItem(item);
        adapter.close();
        item.addTag(FeedItem.TAG_FAVORITE);
        EventBus.getDefault().post(FavoritesEvent.added(item));
        EventBus.getDefault().post(FeedItemEvent.updated(item));
    }

    public static Future<?> removeFavoriteItem(FeedItem item) {
        return dbExec.submit(new -$$Lambda$DBWriter$gGXX3gQFVx-GgfklXgNC3Ed-_PM(item));
    }

    static /* synthetic */ void lambda$removeFavoriteItem$12(FeedItem item) {
        PodDBAdapter adapter = PodDBAdapter.getInstance().open();
        adapter.removeFavoriteItem(item);
        adapter.close();
        item.removeTag(FeedItem.TAG_FAVORITE);
        EventBus.getDefault().post(FavoritesEvent.removed(item));
        EventBus.getDefault().post(FeedItemEvent.updated(item));
    }

    public static Future<?> moveQueueItemToTop(long itemId, boolean broadcastUpdate) {
        return dbExec.submit(new -$$Lambda$DBWriter$LDJfA8KBd4C10b0oVofAN8I11Sw(itemId, broadcastUpdate));
    }

    static /* synthetic */ void lambda$moveQueueItemToTop$13(long itemId, boolean broadcastUpdate) {
        int index = DBReader.getQueueIDList().indexOf(itemId);
        if (index >= 0) {
            moveQueueItemHelper(index, 0, broadcastUpdate);
        } else {
            Log.e(TAG, "moveQueueItemToTop: item not found");
        }
    }

    public static Future<?> moveQueueItemToBottom(long itemId, boolean broadcastUpdate) {
        return dbExec.submit(new -$$Lambda$DBWriter$xzYTSnk211oD80eUcTXYHkVl2sU(itemId, broadcastUpdate));
    }

    static /* synthetic */ void lambda$moveQueueItemToBottom$14(long itemId, boolean broadcastUpdate) {
        LongList queueIdList = DBReader.getQueueIDList();
        int index = queueIdList.indexOf(itemId);
        if (index >= 0) {
            moveQueueItemHelper(index, queueIdList.size() - 1, broadcastUpdate);
        } else {
            Log.e(TAG, "moveQueueItemToBottom: item not found");
        }
    }

    public static Future<?> moveQueueItem(int from, int to, boolean broadcastUpdate) {
        return dbExec.submit(new -$$Lambda$DBWriter$gE7GwuBOR8tGanMW0pRRpGpurQI(from, to, broadcastUpdate));
    }

    private static void moveQueueItemHelper(int from, int to, boolean broadcastUpdate) {
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        List<FeedItem> queue = DBReader.getQueue(adapter);
        if (queue == null) {
            Log.e(TAG, "moveQueueItemHelper: Could not load queue");
        } else if (from >= 0 && from < queue.size() && to >= 0 && to < queue.size()) {
            FeedItem item = (FeedItem) queue.remove(from);
            queue.add(to, item);
            adapter.setQueue(queue);
            if (broadcastUpdate) {
                EventBus.getDefault().post(QueueEvent.moved(item, to));
            }
        }
        adapter.close();
    }

    public static Future<?> markItemPlayed(int played, long... itemIds) {
        return markItemPlayed(played, true, itemIds);
    }

    public static Future<?> markItemPlayed(int played, boolean broadcastUpdate, long... itemIds) {
        return dbExec.submit(new -$$Lambda$DBWriter$1miaRFjOtpoFsnrjoNISdu8Cx04(played, itemIds, broadcastUpdate));
    }

    static /* synthetic */ void lambda$markItemPlayed$16(int played, long[] itemIds, boolean broadcastUpdate) {
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        adapter.setFeedItemRead(played, itemIds);
        adapter.close();
        if (broadcastUpdate) {
            EventDistributor.getInstance().sendUnreadItemsUpdateBroadcast();
        }
    }

    public static Future<?> markItemPlayed(FeedItem item, int played, boolean resetMediaPosition) {
        return markItemPlayed(item.getId(), played, item.hasMedia() ? item.getMedia().getId() : 0, resetMediaPosition);
    }

    private static Future<?> markItemPlayed(long itemId, int played, long mediaId, boolean resetMediaPosition) {
        return dbExec.submit(new -$$Lambda$DBWriter$iqaCat6GXZp1XpuE1evaBAb1Pqc(played, itemId, mediaId, resetMediaPosition));
    }

    static /* synthetic */ void lambda$markItemPlayed$17(int played, long itemId, long mediaId, boolean resetMediaPosition) {
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        adapter.setFeedItemRead(played, itemId, mediaId, resetMediaPosition);
        adapter.close();
        EventDistributor.getInstance().sendUnreadItemsUpdateBroadcast();
    }

    public static Future<?> markFeedSeen(long feedId) {
        return dbExec.submit(new -$$Lambda$DBWriter$Rh3jEGe9LdqMgn98faKgtU_u4Ec(feedId));
    }

    static /* synthetic */ void lambda$markFeedSeen$18(long feedId) {
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        adapter.setFeedItems(-1, 0, feedId);
        adapter.close();
        EventDistributor.getInstance().sendUnreadItemsUpdateBroadcast();
    }

    public static Future<?> markFeedRead(long feedId) {
        return dbExec.submit(new -$$Lambda$DBWriter$0mNd1js5hpfcy55wDkkLudvWlSE(feedId));
    }

    static /* synthetic */ void lambda$markFeedRead$19(long feedId) {
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        adapter.setFeedItems(1, feedId);
        adapter.close();
        EventDistributor.getInstance().sendUnreadItemsUpdateBroadcast();
    }

    public static Future<?> markAllItemsRead() {
        return dbExec.submit(-$$Lambda$DBWriter$jdLPZcZprY8Bv1l9k9Q3_ej1tHg.INSTANCE);
    }

    static /* synthetic */ void lambda$markAllItemsRead$20() {
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        adapter.setFeedItems(1);
        adapter.close();
        EventDistributor.getInstance().sendUnreadItemsUpdateBroadcast();
    }

    public static Future<?> markNewItemsSeen() {
        return dbExec.submit(-$$Lambda$DBWriter$MBvgDkpYJEkloHYbq8KVwaN3dVg.INSTANCE);
    }

    static /* synthetic */ void lambda$markNewItemsSeen$21() {
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        adapter.setFeedItems(-1, 0);
        adapter.close();
        EventDistributor.getInstance().sendUnreadItemsUpdateBroadcast();
    }

    static Future<?> addNewFeed(Context context, Feed... feeds) {
        return dbExec.submit(new -$$Lambda$DBWriter$iEuRzTUK5pMZYfOq9wRO7ZLcYAE(feeds, context));
    }

    static /* synthetic */ void lambda$addNewFeed$22(Feed[] feeds, Context context) {
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        adapter.setCompleteFeed(feeds);
        adapter.close();
        if (ClientConfig.gpodnetCallbacks.gpodnetEnabled()) {
            for (Feed feed : feeds) {
                GpodnetPreferences.addAddedFeed(feed.getDownload_url());
            }
        }
        new BackupManager(context).dataChanged();
    }

    static Future<?> setCompleteFeed(Feed... feeds) {
        return dbExec.submit(new -$$Lambda$DBWriter$5QekNLgAxlef4fGkx52_WJvjhhw(feeds));
    }

    static /* synthetic */ void lambda$setCompleteFeed$23(Feed[] feeds) {
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        adapter.setCompleteFeed(feeds);
        adapter.close();
    }

    public static Future<?> setFeedMedia(FeedMedia media) {
        return dbExec.submit(new -$$Lambda$DBWriter$qVx4KjDjY08LqokVYpP--8WlvE4(media));
    }

    static /* synthetic */ void lambda$setFeedMedia$24(FeedMedia media) {
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        adapter.setMedia(media);
        adapter.close();
    }

    public static Future<?> setFeedMediaPlaybackInformation(FeedMedia media) {
        return dbExec.submit(new -$$Lambda$DBWriter$uC6f8_T9J04gSPGxx-QN-ezcgIo(media));
    }

    static /* synthetic */ void lambda$setFeedMediaPlaybackInformation$25(FeedMedia media) {
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        adapter.setFeedMediaPlaybackInformation(media);
        adapter.close();
    }

    public static Future<?> setFeedItem(FeedItem item) {
        return dbExec.submit(new -$$Lambda$DBWriter$FV27LTUqT6eN59kvxA42HOx-zQ8(item));
    }

    static /* synthetic */ void lambda$setFeedItem$26(FeedItem item) {
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        adapter.setSingleFeedItem(item);
        adapter.close();
        EventBus.getDefault().post(FeedItemEvent.updated(item));
    }

    public static Future<?> updateFeedDownloadURL(String original, String updated) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("updateFeedDownloadURL(original: ");
        stringBuilder.append(original);
        stringBuilder.append(", updated: ");
        stringBuilder.append(updated);
        stringBuilder.append(")");
        Log.d(str, stringBuilder.toString());
        return dbExec.submit(new -$$Lambda$DBWriter$O7uBNwaeyiIYJ-jPbxfuYYRu0ks(original, updated));
    }

    static /* synthetic */ void lambda$updateFeedDownloadURL$27(String original, String updated) {
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        adapter.setFeedDownloadUrl(original, updated);
        adapter.close();
    }

    public static Future<?> setFeedPreferences(FeedPreferences preferences) {
        return dbExec.submit(new -$$Lambda$DBWriter$ISwu78Yorq2vs6N56FscktE0BlA(preferences));
    }

    static /* synthetic */ void lambda$setFeedPreferences$28(FeedPreferences preferences) {
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        adapter.setFeedPreferences(preferences);
        adapter.close();
        EventDistributor.getInstance().sendFeedUpdateBroadcast();
    }

    private static boolean itemListContains(List<FeedItem> items, long itemId) {
        for (FeedItem item : items) {
            if (item.getId() == itemId) {
                return true;
            }
        }
        return false;
    }

    private static Future<?> setFeedItemFlattrStatus(Context context, FeedItem item, boolean startFlattrClickWorker) {
        return dbExec.submit(new -$$Lambda$DBWriter$8aJcOrvSqmqeDSnlxS1NTe8t3iU(item, startFlattrClickWorker, context));
    }

    static /* synthetic */ void lambda$setFeedItemFlattrStatus$29(FeedItem item, boolean startFlattrClickWorker, Context context) {
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        adapter.setFeedItemFlattrStatus(item);
        adapter.close();
        if (startFlattrClickWorker) {
            new FlattrClickWorker(context).executeAsync();
        }
    }

    private static Future<?> setFeedFlattrStatus(Context context, Feed feed, boolean startFlattrClickWorker) {
        return dbExec.submit(new -$$Lambda$DBWriter$mgudAMsvBGdD_Lx_vqZXfD91_Uo(feed, startFlattrClickWorker, context));
    }

    static /* synthetic */ void lambda$setFeedFlattrStatus$30(Feed feed, boolean startFlattrClickWorker, Context context) {
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        adapter.setFeedFlattrStatus(feed);
        adapter.close();
        if (startFlattrClickWorker) {
            new FlattrClickWorker(context).executeAsync();
        }
    }

    public static Future<?> setFeedLastUpdateFailed(long feedId, boolean lastUpdateFailed) {
        return dbExec.submit(new -$$Lambda$DBWriter$qNBvYbqmaxefKge-Lcsx56EjyM0(feedId, lastUpdateFailed));
    }

    static /* synthetic */ void lambda$setFeedLastUpdateFailed$31(long feedId, boolean lastUpdateFailed) {
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        adapter.setFeedLastUpdateFailed(feedId, lastUpdateFailed);
        adapter.close();
    }

    public static Future<?> setFeedCustomTitle(Feed feed) {
        return dbExec.submit(new -$$Lambda$DBWriter$VIxkeDkv4nuCHzPOkGY8KHiNppI(feed));
    }

    static /* synthetic */ void lambda$setFeedCustomTitle$32(Feed feed) {
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        adapter.setFeedCustomTitle(feed.getId(), feed.getCustomTitle());
        adapter.close();
        EventDistributor.getInstance().sendFeedUpdateBroadcast();
    }

    private static String formatURIForQuery(String uri) {
        try {
            return URLEncoder.encode(uri.endsWith("/") ? uri.substring(0, uri.length() - 1) : uri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, e.getMessage());
            return "";
        }
    }

    public static Future<?> setFlattredStatus(Context context, FlattrThing thing, boolean startFlattrClickWorker) {
        if (thing instanceof FeedItem) {
            return setFeedItemFlattrStatus(context, (FeedItem) thing, startFlattrClickWorker);
        }
        if (thing instanceof Feed) {
            return setFeedFlattrStatus(context, (Feed) thing, startFlattrClickWorker);
        }
        if (!(thing instanceof SimpleFlattrThing)) {
            Log.e(TAG, "flattrQueue processing - thing is neither FeedItem nor Feed nor SimpleFlattrThing");
        }
        return null;
    }

    public static Future<?> clearAllFlattrStatus() {
        Log.d(TAG, "clearAllFlattrStatus()");
        return dbExec.submit(-$$Lambda$DBWriter$Bp2eu4aP6dixbWk6uUb1cL4vZmg.INSTANCE);
    }

    static /* synthetic */ void lambda$clearAllFlattrStatus$33() {
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        adapter.clearAllFlattrStatus();
        adapter.close();
    }

    public static Future<?> setFlattredStatus(List<Flattr> flattrList) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("setFlattredStatus to status retrieved from flattr api running with ");
        stringBuilder.append(flattrList.size());
        stringBuilder.append(" items");
        Log.d(str, stringBuilder.toString());
        clearAllFlattrStatus();
        return dbExec.submit(new -$$Lambda$DBWriter$Yd3tRZ1piNqmJfj35sKbjsh0Jvk(flattrList));
    }

    static /* synthetic */ void lambda$setFlattredStatus$34(List flattrList) {
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        for (Flattr flattr : flattrList) {
            adapter.setItemFlattrStatus(formatURIForQuery(flattr.getThing().getUrl()), new FlattrStatus(flattr.getCreated().getTime()));
        }
        adapter.close();
    }

    public static Future<?> sortQueue(Comparator<FeedItem> comparator, boolean broadcastUpdate) {
        return dbExec.submit(new -$$Lambda$DBWriter$Lp-CH6KjyWC6BPIvHOslY-DcmR4(comparator, broadcastUpdate));
    }

    static /* synthetic */ void lambda$sortQueue$35(Comparator comparator, boolean broadcastUpdate) {
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        List<FeedItem> queue = DBReader.getQueue(adapter);
        if (queue != null) {
            Collections.sort(queue, comparator);
            adapter.setQueue(queue);
            if (broadcastUpdate) {
                EventBus.getDefault().post(QueueEvent.sorted(queue));
            }
        } else {
            Log.e(TAG, "sortQueue: Could not load queue");
        }
        adapter.close();
    }

    public static Future<?> reorderQueue(Permutor<FeedItem> permutor, boolean broadcastUpdate) {
        return dbExec.submit(new -$$Lambda$DBWriter$rTE-EtLe8qrHL4iUHxHMWMHHfdk(permutor, broadcastUpdate));
    }

    static /* synthetic */ void lambda$reorderQueue$36(Permutor permutor, boolean broadcastUpdate) {
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        List<FeedItem> queue = DBReader.getQueue(adapter);
        if (queue != null) {
            permutor.reorder(queue);
            adapter.setQueue(queue);
            if (broadcastUpdate) {
                EventBus.getDefault().post(QueueEvent.sorted(queue));
            }
        } else {
            Log.e(TAG, "reorderQueue: Could not load queue");
        }
        adapter.close();
    }

    public static Future<?> setFeedItemAutoDownload(FeedItem feedItem, boolean autoDownload) {
        return dbExec.submit(new -$$Lambda$DBWriter$cXT6mwZJi4kqLshsK5YbX5yx1Bw(feedItem, autoDownload));
    }

    static /* synthetic */ void lambda$setFeedItemAutoDownload$37(FeedItem feedItem, boolean autoDownload) {
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        adapter.setFeedItemAutoDownload(feedItem, autoDownload ? 1 : 0);
        adapter.close();
        EventDistributor.getInstance().sendUnreadItemsUpdateBroadcast();
    }

    public static Future<?> saveFeedItemAutoDownloadFailed(FeedItem feedItem) {
        return dbExec.submit(new -$$Lambda$DBWriter$g8Cgj56SY4s1nj5x_yYhmZSazMQ(feedItem));
    }

    static /* synthetic */ void lambda$saveFeedItemAutoDownloadFailed$38(FeedItem feedItem) {
        long autoDownload;
        PodDBAdapter adapter;
        int failedAttempts = feedItem.getFailedAutoDownloadAttempts() + 1;
        if (feedItem.getAutoDownload()) {
            if (failedAttempts < 10) {
                autoDownload = ((System.currentTimeMillis() / 10) * 10) + ((long) failedAttempts);
                adapter = PodDBAdapter.getInstance();
                adapter.open();
                adapter.setFeedItemAutoDownload(feedItem, autoDownload);
                adapter.close();
                EventDistributor.getInstance().sendUnreadItemsUpdateBroadcast();
            }
        }
        autoDownload = 0;
        feedItem.setAutoDownload(false);
        adapter = PodDBAdapter.getInstance();
        adapter.open();
        adapter.setFeedItemAutoDownload(feedItem, autoDownload);
        adapter.close();
        EventDistributor.getInstance().sendUnreadItemsUpdateBroadcast();
    }

    public static Future<?> setFeedsItemsAutoDownload(Feed feed, boolean autoDownload) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(autoDownload ? "Enabling" : "Disabling");
        stringBuilder.append(" auto download for items of feed ");
        stringBuilder.append(feed.getId());
        Log.d(str, stringBuilder.toString());
        return dbExec.submit(new -$$Lambda$DBWriter$Sg2cNLArkctDxT9L6nE3i3Nlykk(feed, autoDownload));
    }

    static /* synthetic */ void lambda$setFeedsItemsAutoDownload$39(Feed feed, boolean autoDownload) {
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        adapter.setFeedsItemsAutoDownload(feed, autoDownload);
        adapter.close();
        EventDistributor.getInstance().sendUnreadItemsUpdateBroadcast();
    }

    public static Future<?> setFeedItemsFilter(long feedId, Set<String> filterValues) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("setFeedItemsFilter() called with: feedId = [");
        stringBuilder.append(feedId);
        stringBuilder.append("], filterValues = [");
        stringBuilder.append(filterValues);
        stringBuilder.append("]");
        Log.d(str, stringBuilder.toString());
        return dbExec.submit(new -$$Lambda$DBWriter$Tq0j0uxsXEXbf_47ccX1B4pRUvI(feedId, filterValues));
    }

    static /* synthetic */ void lambda$setFeedItemsFilter$40(long feedId, Set filterValues) {
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        adapter.setFeedItemFilter(feedId, filterValues);
        adapter.close();
        EventBus.getDefault().post(new FeedEvent(FeedEvent.Action.FILTER_CHANGED, feedId));
    }
}
