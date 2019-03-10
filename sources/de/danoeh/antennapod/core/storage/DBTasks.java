package de.danoeh.antennapod.core.storage;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.util.Log;
import de.danoeh.antennapod.core.ClientConfig;
import de.danoeh.antennapod.core.asynctask.FlattrClickWorker;
import de.danoeh.antennapod.core.asynctask.FlattrStatusFetcher;
import de.danoeh.antennapod.core.feed.EventDistributor;
import de.danoeh.antennapod.core.feed.Feed;
import de.danoeh.antennapod.core.feed.FeedFile;
import de.danoeh.antennapod.core.feed.FeedItem;
import de.danoeh.antennapod.core.feed.FeedMedia;
import de.danoeh.antennapod.core.service.GpodnetSyncService;
import de.danoeh.antennapod.core.service.download.DownloadStatus;
import de.danoeh.antennapod.core.service.playback.PlaybackService;
import de.danoeh.antennapod.core.util.DownloadError;
import de.danoeh.antennapod.core.util.IntentUtils;
import de.danoeh.antennapod.core.util.exception.MediaFileNotFoundException;
import de.danoeh.antennapod.core.util.flattr.FlattrUtils;
import de.danoeh.antennapod.core.util.playback.PlaybackServiceStarter;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;

public final class DBTasks {
    private static final String PREF_LAST_REFRESH = "last_refresh";
    private static final String PREF_NAME = "dbtasks";
    private static final String TAG = "DBTasks";
    private static final ExecutorService autodownloadExec = Executors.newSingleThreadExecutor(-$$Lambda$DBTasks$La3Ha_nDKOhGcMkR9TMbtaVeJx8.INSTANCE);
    private static final AtomicBoolean isRefreshing = new AtomicBoolean(false);

    static abstract class QueryTask<T> implements Callable<T> {
        private T result;

        public abstract void execute(PodDBAdapter podDBAdapter);

        public QueryTask(Context context) {
        }

        public T call() throws Exception {
            PodDBAdapter adapter = PodDBAdapter.getInstance();
            adapter.open();
            execute(adapter);
            adapter.close();
            return this.result;
        }

        void setResult(T result) {
            this.result = result;
        }
    }

    public static synchronized de.danoeh.antennapod.core.feed.Feed[] updateFeed(android.content.Context r16, de.danoeh.antennapod.core.feed.Feed... r17) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:70:0x0256 in {10, 11, 12, 17, 18, 19, 22, 23, 26, 27, 39, 40, 41, 42, 43, 44, 45, 46, 47, 55, 57, 59, 62, 66, 69} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r1 = r17;
        r2 = de.danoeh.antennapod.core.storage.DBTasks.class;
        monitor-enter(r2);
        r0 = new java.util.ArrayList;	 Catch:{ all -> 0x0253 }
        r0.<init>();	 Catch:{ all -> 0x0253 }
        r3 = r0;	 Catch:{ all -> 0x0253 }
        r0 = new java.util.ArrayList;	 Catch:{ all -> 0x0253 }
        r0.<init>();	 Catch:{ all -> 0x0253 }
        r4 = r0;	 Catch:{ all -> 0x0253 }
        r0 = r1.length;	 Catch:{ all -> 0x0253 }
        r0 = new de.danoeh.antennapod.core.feed.Feed[r0];	 Catch:{ all -> 0x0253 }
        r5 = r0;	 Catch:{ all -> 0x0253 }
        r0 = de.danoeh.antennapod.core.storage.PodDBAdapter.getInstance();	 Catch:{ all -> 0x0253 }
        r6 = r0;	 Catch:{ all -> 0x0253 }
        r6.open();	 Catch:{ all -> 0x0253 }
        r0 = 0;	 Catch:{ all -> 0x0253 }
        r7 = r0;	 Catch:{ all -> 0x0253 }
        r8 = r1.length;	 Catch:{ all -> 0x0253 }
        if (r7 >= r8) goto L_0x0201;	 Catch:{ all -> 0x0253 }
        r8 = r1[r7];	 Catch:{ all -> 0x0253 }
        r9 = searchFeedByIdentifyingValueOrID(r6, r8);	 Catch:{ all -> 0x0253 }
        if (r9 != 0) goto L_0x0077;	 Catch:{ all -> 0x0253 }
        r10 = "DBTasks";	 Catch:{ all -> 0x0253 }
        r11 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0253 }
        r11.<init>();	 Catch:{ all -> 0x0253 }
        r12 = "Found no existing Feed with title ";	 Catch:{ all -> 0x0253 }
        r11.append(r12);	 Catch:{ all -> 0x0253 }
        r12 = r8.getTitle();	 Catch:{ all -> 0x0253 }
        r11.append(r12);	 Catch:{ all -> 0x0253 }
        r12 = ". Adding as new one.";	 Catch:{ all -> 0x0253 }
        r11.append(r12);	 Catch:{ all -> 0x0253 }
        r11 = r11.toString();	 Catch:{ all -> 0x0253 }
        android.util.Log.d(r10, r11);	 Catch:{ all -> 0x0253 }
        r10 = r8.getMostRecentItem();	 Catch:{ all -> 0x0253 }
        if (r10 == 0) goto L_0x006b;	 Catch:{ all -> 0x0253 }
        r10.setNew();	 Catch:{ all -> 0x0253 }
        goto L_0x006c;	 Catch:{ all -> 0x0253 }
        r3.add(r8);	 Catch:{ all -> 0x0253 }
        r5[r7] = r8;	 Catch:{ all -> 0x0253 }
        goto L_0x01fa;	 Catch:{ all -> 0x0253 }
        r10 = "DBTasks";	 Catch:{ all -> 0x0253 }
        r11 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0253 }
        r11.<init>();	 Catch:{ all -> 0x0253 }
        r12 = "Feed with title ";	 Catch:{ all -> 0x0253 }
        r11.append(r12);	 Catch:{ all -> 0x0253 }
        r12 = r8.getTitle();	 Catch:{ all -> 0x0253 }
        r11.append(r12);	 Catch:{ all -> 0x0253 }
        r12 = " already exists. Syncing new with existing one.";	 Catch:{ all -> 0x0253 }
        r11.append(r12);	 Catch:{ all -> 0x0253 }
        r11 = r11.toString();	 Catch:{ all -> 0x0253 }
        android.util.Log.d(r10, r11);	 Catch:{ all -> 0x0253 }
        r10 = r8.getItems();	 Catch:{ all -> 0x0253 }
        r11 = new de.danoeh.antennapod.core.util.comparator.FeedItemPubdateComparator;	 Catch:{ all -> 0x0253 }
        r11.<init>();	 Catch:{ all -> 0x0253 }
        java.util.Collections.sort(r10, r11);	 Catch:{ all -> 0x0253 }
        r10 = r8.getPageNr();	 Catch:{ all -> 0x0253 }
        r11 = r9.getPageNr();	 Catch:{ all -> 0x0253 }
        if (r10 != r11) goto L_0x00d8;	 Catch:{ all -> 0x0253 }
        r10 = r9.compareWithOther(r8);	 Catch:{ all -> 0x0253 }
        if (r10 == 0) goto L_0x00d6;	 Catch:{ all -> 0x0253 }
        r10 = "DBTasks";	 Catch:{ all -> 0x0253 }
        r11 = "Feed has updated attribute values. Updating old feed's attributes";	 Catch:{ all -> 0x0253 }
        android.util.Log.d(r10, r11);	 Catch:{ all -> 0x0253 }
        r9.updateFromOther(r8);	 Catch:{ all -> 0x0253 }
        goto L_0x00ec;	 Catch:{ all -> 0x0253 }
        goto L_0x00ec;	 Catch:{ all -> 0x0253 }
        r10 = "DBTasks";	 Catch:{ all -> 0x0253 }
        r11 = "New feed has a higher page number.";	 Catch:{ all -> 0x0253 }
        android.util.Log.d(r10, r11);	 Catch:{ all -> 0x0253 }
        r10 = r8.getNextPageLink();	 Catch:{ all -> 0x0253 }
        r9.setNextPageLink(r10);	 Catch:{ all -> 0x0253 }
        r10 = r9.getPreferences();	 Catch:{ all -> 0x0253 }
        r11 = r8.getPreferences();	 Catch:{ all -> 0x0253 }
        r10 = r10.compareWithOther(r11);	 Catch:{ all -> 0x0253 }
        if (r10 == 0) goto L_0x0118;	 Catch:{ all -> 0x0253 }
        r10 = "DBTasks";	 Catch:{ all -> 0x0253 }
        r11 = "Feed has updated preferences. Updating old feed's preferences";	 Catch:{ all -> 0x0253 }
        android.util.Log.d(r10, r11);	 Catch:{ all -> 0x0253 }
        r10 = r9.getPreferences();	 Catch:{ all -> 0x0253 }
        r11 = r8.getPreferences();	 Catch:{ all -> 0x0253 }
        r10.updateFromOther(r11);	 Catch:{ all -> 0x0253 }
        goto L_0x0119;	 Catch:{ all -> 0x0253 }
        r10 = r9.getMostRecentItem();	 Catch:{ all -> 0x0253 }
        r11 = 0;	 Catch:{ all -> 0x0253 }
        if (r10 == 0) goto L_0x012c;	 Catch:{ all -> 0x0253 }
        r12 = r10.getPubDate();	 Catch:{ all -> 0x0253 }
        r11 = r12;	 Catch:{ all -> 0x0253 }
        goto L_0x012d;	 Catch:{ all -> 0x0253 }
        r12 = r0;	 Catch:{ all -> 0x0253 }
        r13 = r8.getItems();	 Catch:{ all -> 0x0253 }
        r13 = r13.size();	 Catch:{ all -> 0x0253 }
        if (r12 >= r13) goto L_0x01da;	 Catch:{ all -> 0x0253 }
        r13 = r8.getItems();	 Catch:{ all -> 0x0253 }
        r13 = r13.get(r12);	 Catch:{ all -> 0x0253 }
        r13 = (de.danoeh.antennapod.core.feed.FeedItem) r13;	 Catch:{ all -> 0x0253 }
        r14 = r13.getIdentifyingValue();	 Catch:{ all -> 0x0253 }
        r14 = searchFeedItemByIdentifyingValue(r9, r14);	 Catch:{ all -> 0x0253 }
        if (r14 != 0) goto L_0x01cd;	 Catch:{ all -> 0x0253 }
        r13.setFeed(r9);	 Catch:{ all -> 0x0253 }
        r15 = r9.getPreferences();	 Catch:{ all -> 0x0253 }
        r15 = r15.getAutoDownload();	 Catch:{ all -> 0x0253 }
        r13.setAutoDownload(r15);	 Catch:{ all -> 0x0253 }
        r15 = r9.getItems();	 Catch:{ all -> 0x0253 }
        r15.add(r12, r13);	 Catch:{ all -> 0x0253 }
        if (r11 == 0) goto L_0x0198;	 Catch:{ all -> 0x0253 }
        r15 = r13.getPubDate();	 Catch:{ all -> 0x0253 }
        r15 = r11.before(r15);	 Catch:{ all -> 0x0253 }
        if (r15 != 0) goto L_0x0196;	 Catch:{ all -> 0x0253 }
        r15 = r13.getPubDate();	 Catch:{ all -> 0x0253 }
        r15 = r11.equals(r15);	 Catch:{ all -> 0x0253 }
        if (r15 == 0) goto L_0x0194;	 Catch:{ all -> 0x0253 }
        goto L_0x0199;	 Catch:{ all -> 0x0253 }
        goto L_0x01d2;	 Catch:{ all -> 0x0253 }
        goto L_0x0199;	 Catch:{ all -> 0x0253 }
        r15 = "DBTasks";	 Catch:{ all -> 0x0253 }
        r0 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0253 }
        r0.<init>();	 Catch:{ all -> 0x0253 }
        r1 = "Marking item published on ";	 Catch:{ all -> 0x0253 }
        r0.append(r1);	 Catch:{ all -> 0x0253 }
        r1 = r13.getPubDate();	 Catch:{ all -> 0x0253 }
        r0.append(r1);	 Catch:{ all -> 0x0253 }
        r1 = " new, prior most recent date = ";	 Catch:{ all -> 0x0253 }
        r0.append(r1);	 Catch:{ all -> 0x0253 }
        r0.append(r11);	 Catch:{ all -> 0x0253 }
        r0 = r0.toString();	 Catch:{ all -> 0x0253 }
        android.util.Log.d(r15, r0);	 Catch:{ all -> 0x0253 }
        r13.setNew();	 Catch:{ all -> 0x0253 }
        goto L_0x01d2;	 Catch:{ all -> 0x0253 }
        r14.updateFromOther(r13);	 Catch:{ all -> 0x0253 }
        r12 = r12 + 1;	 Catch:{ all -> 0x0253 }
        r0 = 0;	 Catch:{ all -> 0x0253 }
        r1 = r17;	 Catch:{ all -> 0x0253 }
        goto L_0x0130;	 Catch:{ all -> 0x0253 }
        r0 = r8.getLastUpdate();	 Catch:{ all -> 0x0253 }
        r9.setLastUpdate(r0);	 Catch:{ all -> 0x0253 }
        r0 = r8.getType();	 Catch:{ all -> 0x0253 }
        r9.setType(r0);	 Catch:{ all -> 0x0253 }
        r0 = 0;	 Catch:{ all -> 0x0253 }
        r9.setLastUpdateFailed(r0);	 Catch:{ all -> 0x0253 }
        r4.add(r9);	 Catch:{ all -> 0x0253 }
        r5[r7] = r9;	 Catch:{ all -> 0x0253 }
        r7 = r7 + 1;	 Catch:{ all -> 0x0253 }
        r1 = r17;	 Catch:{ all -> 0x0253 }
        goto L_0x0020;	 Catch:{ all -> 0x0253 }
        r6.close();	 Catch:{ all -> 0x0253 }
        r0 = r3.size();	 Catch:{ InterruptedException -> 0x023e, InterruptedException -> 0x023e }
        r0 = new de.danoeh.antennapod.core.feed.Feed[r0];	 Catch:{ InterruptedException -> 0x023e, InterruptedException -> 0x023e }
        r0 = r3.toArray(r0);	 Catch:{ InterruptedException -> 0x023e, InterruptedException -> 0x023e }
        r0 = (de.danoeh.antennapod.core.feed.Feed[]) r0;	 Catch:{ InterruptedException -> 0x023e, InterruptedException -> 0x023e }
        r1 = r16;
        r0 = de.danoeh.antennapod.core.storage.DBWriter.addNewFeed(r1, r0);	 Catch:{ InterruptedException -> 0x023c, InterruptedException -> 0x023c }
        r0.get();	 Catch:{ InterruptedException -> 0x023c, InterruptedException -> 0x023c }
        r0 = r4.size();	 Catch:{ InterruptedException -> 0x023c, InterruptedException -> 0x023c }
        r0 = new de.danoeh.antennapod.core.feed.Feed[r0];	 Catch:{ InterruptedException -> 0x023c, InterruptedException -> 0x023c }
        r0 = r4.toArray(r0);	 Catch:{ InterruptedException -> 0x023c, InterruptedException -> 0x023c }
        r0 = (de.danoeh.antennapod.core.feed.Feed[]) r0;	 Catch:{ InterruptedException -> 0x023c, InterruptedException -> 0x023c }
        r0 = de.danoeh.antennapod.core.storage.DBWriter.setCompleteFeed(r0);	 Catch:{ InterruptedException -> 0x023c, InterruptedException -> 0x023c }
        r0.get();	 Catch:{ InterruptedException -> 0x023c, InterruptedException -> 0x023c }
        goto L_0x0247;
    L_0x023c:
        r0 = move-exception;
        goto L_0x0241;
    L_0x023e:
        r0 = move-exception;
        r1 = r16;
        r0.printStackTrace();	 Catch:{ all -> 0x0253 }
        r0 = de.danoeh.antennapod.core.feed.EventDistributor.getInstance();	 Catch:{ all -> 0x0253 }
        r0.sendFeedUpdateBroadcast();	 Catch:{ all -> 0x0253 }
        monitor-exit(r2);
        return r5;
    L_0x0253:
        r0 = move-exception;
        monitor-exit(r2);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: de.danoeh.antennapod.core.storage.DBTasks.updateFeed(android.content.Context, de.danoeh.antennapod.core.feed.Feed[]):de.danoeh.antennapod.core.feed.Feed[]");
    }

    static /* synthetic */ Thread lambda$static$0(Runnable r) {
        Thread t = new Thread(r);
        t.setPriority(1);
        return t;
    }

    private DBTasks() {
    }

    public static void removeFeedWithDownloadUrl(Context context, String downloadUrl) {
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        Cursor cursor = adapter.getFeedCursorDownloadUrls();
        long feedID = 0;
        if (cursor.moveToFirst()) {
            while (true) {
                if (cursor.getString(1).equals(downloadUrl)) {
                    feedID = cursor.getLong(0);
                }
                if (!cursor.moveToNext()) {
                    break;
                }
            }
        }
        cursor.close();
        adapter.close();
        if (feedID != 0) {
            try {
                DBWriter.deleteFeed(context, feedID).get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("removeFeedWithDownloadUrl: Could not find feed with url: ");
        stringBuilder.append(downloadUrl);
        Log.w(str, stringBuilder.toString());
    }

    public static void playMedia(Context context, FeedMedia media, boolean showPlayer, boolean startWhenPrepared, boolean shouldStream) {
        if (!shouldStream) {
            try {
                if (!media.fileExists()) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("No episode was found at ");
                    stringBuilder.append(media.getFile_url());
                    throw new MediaFileNotFoundException(stringBuilder.toString(), media);
                }
            } catch (MediaFileNotFoundException e) {
                e.printStackTrace();
                if (media.isPlaying()) {
                    IntentUtils.sendLocalBroadcast(context, PlaybackService.ACTION_SHUTDOWN_PLAYBACK_SERVICE);
                }
                notifyMissingFeedMediaFile(context, media);
                return;
            }
        }
        new PlaybackServiceStarter(context, media).callEvenIfRunning(true).startWhenPrepared(startWhenPrepared).shouldStream(shouldStream).start();
        if (showPlayer) {
            context.startActivity(PlaybackService.getPlayerActivityIntent(context, media));
        }
        DBWriter.addQueueItemAt(context, media.getItem().getId(), 0, false);
    }

    public static void refreshAllFeeds(Context context, List<Feed> feeds) {
        refreshAllFeeds(context, feeds, null);
    }

    public static void refreshAllFeeds(Context context, List<Feed> feeds, @Nullable Runnable callback) {
        if (isRefreshing.compareAndSet(false, true)) {
            new Thread(new -$$Lambda$DBTasks$A87dXm5WJdJAukt51qNkRmFl8vk(feeds, context, callback)).start();
        } else {
            Log.d(TAG, "Ignoring request to refresh all feeds: Refresh lock is locked");
        }
    }

    static /* synthetic */ void lambda$refreshAllFeeds$1(List feeds, Context context, @Nullable Runnable callback) {
        if (feeds != null) {
            refreshFeeds(context, feeds);
        } else {
            refreshFeeds(context, DBReader.getFeedList());
        }
        isRefreshing.set(false);
        context.getSharedPreferences(PREF_NAME, 0).edit().putLong(PREF_LAST_REFRESH, System.currentTimeMillis()).apply();
        if (FlattrUtils.hasToken()) {
            Log.d(TAG, "Flattring all pending things.");
            new FlattrClickWorker(context).executeAsync();
            Log.d(TAG, "Fetching flattr status.");
            new FlattrStatusFetcher(context).start();
        }
        if (ClientConfig.gpodnetCallbacks.gpodnetEnabled()) {
            GpodnetSyncService.sendSyncIntent(context);
        }
        if (callback != null) {
            callback.run();
        }
    }

    public static long getLastRefreshAllFeedsTimeMillis(Context context) {
        return context.getSharedPreferences(PREF_NAME, 0).getLong(PREF_LAST_REFRESH, 0);
    }

    private static void refreshFeeds(Context context, List<Feed> feedList) {
        for (FeedFile feed : feedList) {
            if (feed.getPreferences().getKeepUpdated()) {
                try {
                    refreshFeed(context, feed);
                } catch (DownloadRequestException e) {
                    DownloadRequestException e2 = e;
                    e2.printStackTrace();
                    DBWriter.addDownloadStatus(new DownloadStatus(feed, feed.getHumanReadableIdentifier(), DownloadError.ERROR_REQUEST_ERROR, false, e2.getMessage()));
                }
            }
        }
    }

    public static void forceRefreshCompleteFeed(Context context, Feed feed) {
        try {
            refreshFeed(context, feed, true, true);
        } catch (DownloadRequestException e) {
            e.printStackTrace();
            DBWriter.addDownloadStatus(new DownloadStatus((FeedFile) feed, feed.getHumanReadableIdentifier(), DownloadError.ERROR_REQUEST_ERROR, false, e.getMessage()));
        }
    }

    public static void loadNextPageOfFeed(Context context, Feed feed, boolean loadAllPages) throws DownloadRequestException {
        if (!feed.isPaged() || feed.getNextPageLink() == null) {
            Log.e(TAG, "loadNextPageOfFeed: Feed was either not paged or contained no nextPageLink");
            return;
        }
        int pageNr = feed.getPageNr() + 1;
        String nextPageLink = feed.getNextPageLink();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(feed.getTitle());
        stringBuilder.append("(");
        stringBuilder.append(pageNr);
        stringBuilder.append(")");
        Feed nextFeed = new Feed(nextPageLink, null, stringBuilder.toString());
        nextFeed.setPageNr(pageNr);
        nextFeed.setPaged(true);
        nextFeed.setId(feed.getId());
        DownloadRequester.getInstance().downloadFeed(context, nextFeed, loadAllPages, false);
    }

    private static void refreshFeed(Context context, Feed feed) throws DownloadRequestException {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("refreshFeed(feed.id: ");
        stringBuilder.append(feed.getId());
        stringBuilder.append(")");
        Log.d(str, stringBuilder.toString());
        refreshFeed(context, feed, false, false);
    }

    public static void forceRefreshFeed(Context context, Feed feed) throws DownloadRequestException {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("refreshFeed(feed.id: ");
        stringBuilder.append(feed.getId());
        stringBuilder.append(")");
        Log.d(str, stringBuilder.toString());
        refreshFeed(context, feed, false, true);
    }

    private static void refreshFeed(Context context, Feed feed, boolean loadAllPages, boolean force) throws DownloadRequestException {
        Feed f;
        String lastUpdate = feed.hasLastUpdateFailed() ? null : feed.getLastUpdate();
        if (feed.getPreferences() == null) {
            f = new Feed(feed.getDownload_url(), lastUpdate, feed.getTitle());
        } else {
            f = new Feed(feed.getDownload_url(), lastUpdate, feed.getTitle(), feed.getPreferences().getUsername(), feed.getPreferences().getPassword());
        }
        f.setId(feed.getId());
        DownloadRequester.getInstance().downloadFeed(context, f, loadAllPages, force);
    }

    public static void notifyMissingFeedMediaFile(Context context, FeedMedia media) {
        Log.i(TAG, "The feedmanager was notified about a missing episode. It will update its database now.");
        media.setDownloaded(false);
        media.setFile_url(null);
        DBWriter.setFeedMedia(media);
        EventDistributor.getInstance().sendFeedUpdateBroadcast();
    }

    public static void downloadFeedItems(Context context, FeedItem... items) throws DownloadRequestException {
        downloadFeedItems(true, context, items);
    }

    static void downloadFeedItems(boolean performAutoCleanup, final Context context, final FeedItem... items) throws DownloadRequestException {
        DownloadRequester requester = DownloadRequester.getInstance();
        if (performAutoCleanup) {
            new Thread() {
                public void run() {
                    ClientConfig.dbTasksCallbacks.getEpisodeCacheCleanupAlgorithm().makeRoomForEpisodes(context, items.length);
                }
            }.start();
        }
        for (FeedItem item : items) {
            if (item.getMedia() != null) {
                if (!requester.isDownloadingFile(item.getMedia())) {
                    if (!item.getMedia().isDownloaded()) {
                        if (items.length > 1) {
                            try {
                                requester.downloadMedia(context, item.getMedia());
                            } catch (DownloadRequestException e) {
                                e.printStackTrace();
                                DBWriter.addDownloadStatus(new DownloadStatus(item.getMedia(), item.getMedia().getHumanReadableIdentifier(), DownloadError.ERROR_REQUEST_ERROR, false, e.getMessage()));
                            }
                        } else {
                            requester.downloadMedia(context, item.getMedia());
                        }
                    }
                }
            }
        }
    }

    public static Future<?> autodownloadUndownloadedItems(Context context) {
        Log.d(TAG, "autodownloadUndownloadedItems");
        return autodownloadExec.submit(ClientConfig.dbTasksCallbacks.getAutomaticDownloadAlgorithm().autoDownloadUndownloadedItems(context));
    }

    public static void performAutoCleanup(Context context) {
        ClientConfig.dbTasksCallbacks.getEpisodeCacheCleanupAlgorithm().performCleanup(context);
    }

    public static FeedItem getQueueSuccessorOfItem(long itemId, List<FeedItem> queue) {
        if (queue == null) {
            queue = DBReader.getQueue();
        }
        if (queue == null) {
            return null;
        }
        Iterator<FeedItem> iterator = queue.iterator();
        while (iterator.hasNext()) {
            if (((FeedItem) iterator.next()).getId() == itemId) {
                if (iterator.hasNext()) {
                    return (FeedItem) iterator.next();
                }
                return null;
            }
        }
        return null;
    }

    public static boolean isInQueue(Context context, long feedItemId) {
        return DBReader.getQueueIDList().contains(feedItemId);
    }

    private static Feed searchFeedByIdentifyingValueOrID(PodDBAdapter adapter, Feed feed) {
        if (feed.getId() != 0) {
            return DBReader.getFeed(feed.getId(), adapter);
        }
        for (Feed f : DBReader.getFeedList()) {
            if (f.getIdentifyingValue().equals(feed.getIdentifyingValue())) {
                f.setItems(DBReader.getFeedItemList(f));
                return f;
            }
        }
        return null;
    }

    private static FeedItem searchFeedItemByIdentifyingValue(Feed feed, String identifier) {
        for (FeedItem item : feed.getItems()) {
            if (item.getIdentifyingValue().equals(identifier)) {
                return item;
            }
        }
        return null;
    }

    public static FutureTask<List<FeedItem>> searchFeedItemTitle(Context context, final long feedID, final String query) {
        return new FutureTask(new QueryTask<List<FeedItem>>(context) {
            public void execute(PodDBAdapter adapter) {
                Cursor searchResult = adapter.searchItemTitles(feedID, query);
                List<FeedItem> items = DBReader.extractItemlistFromCursor(searchResult);
                DBReader.loadAdditionalFeedItemListData(items);
                setResult(items);
                searchResult.close();
            }
        });
    }

    public static FutureTask<List<FeedItem>> searchFeedItemAuthor(Context context, final long feedID, final String query) {
        return new FutureTask(new QueryTask<List<FeedItem>>(context) {
            public void execute(PodDBAdapter adapter) {
                Cursor searchResult = adapter.searchItemAuthors(feedID, query);
                List<FeedItem> items = DBReader.extractItemlistFromCursor(searchResult);
                DBReader.loadAdditionalFeedItemListData(items);
                setResult(items);
                searchResult.close();
            }
        });
    }

    public static FutureTask<List<FeedItem>> searchFeedItemFeedIdentifier(Context context, final long feedID, final String query) {
        return new FutureTask(new QueryTask<List<FeedItem>>(context) {
            public void execute(PodDBAdapter adapter) {
                Cursor searchResult = adapter.searchItemFeedIdentifiers(feedID, query);
                List<FeedItem> items = DBReader.extractItemlistFromCursor(searchResult);
                DBReader.loadAdditionalFeedItemListData(items);
                setResult(items);
                searchResult.close();
            }
        });
    }

    public static FutureTask<List<FeedItem>> searchFeedItemDescription(Context context, final long feedID, final String query) {
        return new FutureTask(new QueryTask<List<FeedItem>>(context) {
            public void execute(PodDBAdapter adapter) {
                Cursor searchResult = adapter.searchItemDescriptions(feedID, query);
                List<FeedItem> items = DBReader.extractItemlistFromCursor(searchResult);
                DBReader.loadAdditionalFeedItemListData(items);
                setResult(items);
                searchResult.close();
            }
        });
    }

    public static FutureTask<List<FeedItem>> searchFeedItemContentEncoded(Context context, final long feedID, final String query) {
        return new FutureTask(new QueryTask<List<FeedItem>>(context) {
            public void execute(PodDBAdapter adapter) {
                Cursor searchResult = adapter.searchItemContentEncoded(feedID, query);
                List<FeedItem> items = DBReader.extractItemlistFromCursor(searchResult);
                DBReader.loadAdditionalFeedItemListData(items);
                setResult(items);
                searchResult.close();
            }
        });
    }

    public static FutureTask<List<FeedItem>> searchFeedItemChapters(Context context, final long feedID, final String query) {
        return new FutureTask(new QueryTask<List<FeedItem>>(context) {
            public void execute(PodDBAdapter adapter) {
                Cursor searchResult = adapter.searchItemChapters(feedID, query);
                List<FeedItem> items = DBReader.extractItemlistFromCursor(searchResult);
                DBReader.loadAdditionalFeedItemListData(items);
                setResult(items);
                searchResult.close();
            }
        });
    }

    public static void flattrItemIfLoggedIn(Context context, FeedItem item) {
        if (FlattrUtils.hasToken()) {
            item.getFlattrStatus().setFlattrQueue();
            DBWriter.setFlattredStatus(context, item, true);
            return;
        }
        FlattrUtils.showNoTokenDialogOrRedirect(context, item.getPaymentLink());
    }

    public static void flattrFeedIfLoggedIn(Context context, Feed feed) {
        if (FlattrUtils.hasToken()) {
            feed.getFlattrStatus().setFlattrQueue();
            DBWriter.setFlattredStatus(context, feed, true);
            return;
        }
        FlattrUtils.showNoTokenDialogOrRedirect(context, feed.getPaymentLink());
    }
}
