package de.danoeh.antennapod.core.storage;

import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;
import de.danoeh.antennapod.core.feed.Feed;
import de.danoeh.antennapod.core.feed.FeedItem;
import de.danoeh.antennapod.core.feed.FeedMedia;
import de.danoeh.antennapod.core.feed.FeedPreferences;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.util.LongIntMap;
import de.danoeh.antennapod.core.util.LongList;
import de.danoeh.antennapod.core.util.comparator.FeedItemPubdateComparator;
import de.danoeh.antennapod.core.util.flattr.FlattrThing;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public final class DBReader {
    private static final int DOWNLOAD_LOG_SIZE = 200;
    public static final int PLAYBACK_HISTORY_SIZE = 50;
    private static final String TAG = "DBReader";

    public static class NavDrawerData {
        public final LongIntMap feedCounters;
        public final List<Feed> feeds;
        public final int numDownloadedItems;
        public final int numNewItems;
        public final int queueSize;
        public final int reclaimableSpace;

        public NavDrawerData(List<Feed> feeds, int queueSize, int numNewItems, int numDownloadedItems, LongIntMap feedIndicatorValues, int reclaimableSpace) {
            this.feeds = feeds;
            this.queueSize = queueSize;
            this.numNewItems = numNewItems;
            this.numDownloadedItems = numDownloadedItems;
            this.feedCounters = feedIndicatorValues;
            this.reclaimableSpace = reclaimableSpace;
        }
    }

    public static class StatisticsData {
        public final List<StatisticsItem> feedTime;
        public final long totalTime;
        public final long totalTimeCountAll;

        public StatisticsData(long totalTime, long totalTimeCountAll, List<StatisticsItem> feedTime) {
            this.totalTime = totalTime;
            this.totalTimeCountAll = totalTimeCountAll;
            this.feedTime = feedTime;
        }
    }

    public static class StatisticsItem {
        public final long episodes;
        public final long episodesStarted;
        public final long episodesStartedIncludingMarked;
        public final Feed feed;
        public final long time;
        public final long timePlayed;
        public final long timePlayedCountAll;

        public StatisticsItem(Feed feed, long time, long timePlayed, long timePlayedCountAll, long episodes, long episodesStarted, long episodesStartedIncludingMarked) {
            this.feed = feed;
            this.time = time;
            this.timePlayed = timePlayed;
            this.timePlayedCountAll = timePlayedCountAll;
            this.episodes = episodes;
            this.episodesStarted = episodesStarted;
            this.episodesStartedIncludingMarked = episodesStartedIncludingMarked;
        }
    }

    public static java.util.List<de.danoeh.antennapod.core.service.download.DownloadStatus> getDownloadLog() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:19:0x004d in {5, 9, 10, 12, 15, 16, 18} preds:[]
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
        r0 = "DBReader";
        r1 = "getDownloadLog() called";
        android.util.Log.d(r0, r1);
        r0 = de.danoeh.antennapod.core.storage.PodDBAdapter.getInstance();
        r0.open();
        r1 = 0;
        r2 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        r2 = r0.getDownloadLogCursor(r2);	 Catch:{ all -> 0x0041 }
        r1 = r2;	 Catch:{ all -> 0x0041 }
        r2 = new java.util.ArrayList;	 Catch:{ all -> 0x0041 }
        r3 = r1.getCount();	 Catch:{ all -> 0x0041 }
        r2.<init>(r3);	 Catch:{ all -> 0x0041 }
    L_0x001f:
        r3 = r1.moveToNext();	 Catch:{ all -> 0x0041 }
        if (r3 == 0) goto L_0x002d;	 Catch:{ all -> 0x0041 }
    L_0x0025:
        r3 = de.danoeh.antennapod.core.service.download.DownloadStatus.fromCursor(r1);	 Catch:{ all -> 0x0041 }
        r2.add(r3);	 Catch:{ all -> 0x0041 }
        goto L_0x001f;	 Catch:{ all -> 0x0041 }
    L_0x002d:
        r3 = new de.danoeh.antennapod.core.util.comparator.DownloadStatusComparator;	 Catch:{ all -> 0x0041 }
        r3.<init>();	 Catch:{ all -> 0x0041 }
        java.util.Collections.sort(r2, r3);	 Catch:{ all -> 0x0041 }
        if (r1 == 0) goto L_0x003c;
    L_0x0038:
        r1.close();
        goto L_0x003d;
    L_0x003d:
        r0.close();
        return r2;
    L_0x0041:
        r2 = move-exception;
        if (r1 == 0) goto L_0x0048;
    L_0x0044:
        r1.close();
        goto L_0x0049;
    L_0x0049:
        r0.close();
        throw r2;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: de.danoeh.antennapod.core.storage.DBReader.getDownloadLog():java.util.List<de.danoeh.antennapod.core.service.download.DownloadStatus>");
    }

    private static de.danoeh.antennapod.core.util.LongList getFavoriteIDList() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:19:0x0044 in {6, 9, 10, 12, 15, 16, 18} preds:[]
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
        r0 = "DBReader";
        r1 = "getFavoriteIDList() called";
        android.util.Log.d(r0, r1);
        r0 = de.danoeh.antennapod.core.storage.PodDBAdapter.getInstance();
        r0.open();
        r1 = 0;
        r2 = r0.getFavoritesCursor();	 Catch:{ all -> 0x0038 }
        r1 = r2;	 Catch:{ all -> 0x0038 }
        r2 = new de.danoeh.antennapod.core.util.LongList;	 Catch:{ all -> 0x0038 }
        r3 = r1.getCount();	 Catch:{ all -> 0x0038 }
        r2.<init>(r3);	 Catch:{ all -> 0x0038 }
    L_0x001d:
        r3 = r1.moveToNext();	 Catch:{ all -> 0x0038 }
        if (r3 == 0) goto L_0x002c;	 Catch:{ all -> 0x0038 }
    L_0x0023:
        r3 = 0;	 Catch:{ all -> 0x0038 }
        r3 = r1.getLong(r3);	 Catch:{ all -> 0x0038 }
        r2.add(r3);	 Catch:{ all -> 0x0038 }
        goto L_0x001d;
        if (r1 == 0) goto L_0x0033;
    L_0x002f:
        r1.close();
        goto L_0x0034;
    L_0x0034:
        r0.close();
        return r2;
    L_0x0038:
        r2 = move-exception;
        if (r1 == 0) goto L_0x003f;
    L_0x003b:
        r1.close();
        goto L_0x0040;
    L_0x0040:
        r0.close();
        throw r2;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: de.danoeh.antennapod.core.storage.DBReader.getFavoriteIDList():de.danoeh.antennapod.core.util.LongList");
    }

    public static java.util.List<de.danoeh.antennapod.core.service.download.DownloadStatus> getFeedDownloadLog(de.danoeh.antennapod.core.feed.Feed r5) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:19:0x0064 in {5, 9, 10, 12, 15, 16, 18} preds:[]
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
        r0 = "DBReader";
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "getFeedDownloadLog() called with: feed = [";
        r1.append(r2);
        r1.append(r5);
        r2 = "]";
        r1.append(r2);
        r1 = r1.toString();
        android.util.Log.d(r0, r1);
        r0 = de.danoeh.antennapod.core.storage.PodDBAdapter.getInstance();
        r0.open();
        r1 = 0;
        r2 = 0;
        r3 = r5.getId();	 Catch:{ all -> 0x0058 }
        r2 = r0.getDownloadLog(r2, r3);	 Catch:{ all -> 0x0058 }
        r1 = r2;	 Catch:{ all -> 0x0058 }
        r2 = new java.util.ArrayList;	 Catch:{ all -> 0x0058 }
        r3 = r1.getCount();	 Catch:{ all -> 0x0058 }
        r2.<init>(r3);	 Catch:{ all -> 0x0058 }
    L_0x0036:
        r3 = r1.moveToNext();	 Catch:{ all -> 0x0058 }
        if (r3 == 0) goto L_0x0044;	 Catch:{ all -> 0x0058 }
    L_0x003c:
        r3 = de.danoeh.antennapod.core.service.download.DownloadStatus.fromCursor(r1);	 Catch:{ all -> 0x0058 }
        r2.add(r3);	 Catch:{ all -> 0x0058 }
        goto L_0x0036;	 Catch:{ all -> 0x0058 }
    L_0x0044:
        r3 = new de.danoeh.antennapod.core.util.comparator.DownloadStatusComparator;	 Catch:{ all -> 0x0058 }
        r3.<init>();	 Catch:{ all -> 0x0058 }
        java.util.Collections.sort(r2, r3);	 Catch:{ all -> 0x0058 }
        if (r1 == 0) goto L_0x0053;
    L_0x004f:
        r1.close();
        goto L_0x0054;
    L_0x0054:
        r0.close();
        return r2;
    L_0x0058:
        r2 = move-exception;
        if (r1 == 0) goto L_0x005f;
    L_0x005b:
        r1.close();
        goto L_0x0060;
    L_0x0060:
        r0.close();
        throw r2;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: de.danoeh.antennapod.core.storage.DBReader.getFeedDownloadLog(de.danoeh.antennapod.core.feed.Feed):java.util.List<de.danoeh.antennapod.core.service.download.DownloadStatus>");
    }

    public static java.util.List<de.danoeh.antennapod.core.feed.FeedItem> getFeedItemList(de.danoeh.antennapod.core.feed.Feed r5) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:19:0x0060 in {6, 9, 10, 12, 15, 16, 18} preds:[]
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
        r0 = "DBReader";
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "getFeedItemList() called with: feed = [";
        r1.append(r2);
        r1.append(r5);
        r2 = "]";
        r1.append(r2);
        r1 = r1.toString();
        android.util.Log.d(r0, r1);
        r0 = de.danoeh.antennapod.core.storage.PodDBAdapter.getInstance();
        r0.open();
        r1 = 0;
        r2 = r0.getAllItemsOfFeedCursor(r5);	 Catch:{ all -> 0x0054 }
        r1 = r2;	 Catch:{ all -> 0x0054 }
        r2 = extractItemlistFromCursor(r0, r1);	 Catch:{ all -> 0x0054 }
        r3 = new de.danoeh.antennapod.core.util.comparator.FeedItemPubdateComparator;	 Catch:{ all -> 0x0054 }
        r3.<init>();	 Catch:{ all -> 0x0054 }
        java.util.Collections.sort(r2, r3);	 Catch:{ all -> 0x0054 }
        r3 = r2.iterator();	 Catch:{ all -> 0x0054 }
    L_0x0038:
        r4 = r3.hasNext();	 Catch:{ all -> 0x0054 }
        if (r4 == 0) goto L_0x0048;	 Catch:{ all -> 0x0054 }
    L_0x003e:
        r4 = r3.next();	 Catch:{ all -> 0x0054 }
        r4 = (de.danoeh.antennapod.core.feed.FeedItem) r4;	 Catch:{ all -> 0x0054 }
        r4.setFeed(r5);	 Catch:{ all -> 0x0054 }
        goto L_0x0038;
        if (r1 == 0) goto L_0x004f;
    L_0x004b:
        r1.close();
        goto L_0x0050;
    L_0x0050:
        r0.close();
        return r2;
    L_0x0054:
        r2 = move-exception;
        if (r1 == 0) goto L_0x005b;
    L_0x0057:
        r1.close();
        goto L_0x005c;
    L_0x005c:
        r0.close();
        throw r2;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: de.danoeh.antennapod.core.storage.DBReader.getFeedItemList(de.danoeh.antennapod.core.feed.Feed):java.util.List<de.danoeh.antennapod.core.feed.FeedItem>");
    }

    private static java.util.List<de.danoeh.antennapod.core.feed.Feed> getFeedList(de.danoeh.antennapod.core.storage.PodDBAdapter r3) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:17:0x002f in {6, 9, 10, 11, 14, 15, 16} preds:[]
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
        r0 = 0;
        r1 = r3.getAllFeedsCursor();	 Catch:{ all -> 0x0026 }
        r0 = r1;	 Catch:{ all -> 0x0026 }
        r1 = new java.util.ArrayList;	 Catch:{ all -> 0x0026 }
        r2 = r0.getCount();	 Catch:{ all -> 0x0026 }
        r1.<init>(r2);	 Catch:{ all -> 0x0026 }
    L_0x000f:
        r2 = r0.moveToNext();	 Catch:{ all -> 0x0026 }
        if (r2 == 0) goto L_0x001d;	 Catch:{ all -> 0x0026 }
    L_0x0015:
        r2 = extractFeedFromCursorRow(r0);	 Catch:{ all -> 0x0026 }
        r1.add(r2);	 Catch:{ all -> 0x0026 }
        goto L_0x000f;
        if (r0 == 0) goto L_0x0024;
    L_0x0020:
        r0.close();
        goto L_0x0025;
    L_0x0025:
        return r1;
    L_0x0026:
        r1 = move-exception;
        if (r0 == 0) goto L_0x002d;
    L_0x0029:
        r0.close();
        goto L_0x002e;
    L_0x002e:
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: de.danoeh.antennapod.core.storage.DBReader.getFeedList(de.danoeh.antennapod.core.storage.PodDBAdapter):java.util.List<de.danoeh.antennapod.core.feed.Feed>");
    }

    public static java.util.List<java.lang.String> getFeedListDownloadUrls() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:19:0x003d in {6, 9, 10, 12, 15, 16, 18} preds:[]
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
        r0 = de.danoeh.antennapod.core.storage.PodDBAdapter.getInstance();
        r0.open();
        r1 = 0;
        r2 = r0.getFeedCursorDownloadUrls();	 Catch:{ all -> 0x0031 }
        r1 = r2;	 Catch:{ all -> 0x0031 }
        r2 = new java.util.ArrayList;	 Catch:{ all -> 0x0031 }
        r3 = r1.getCount();	 Catch:{ all -> 0x0031 }
        r2.<init>(r3);	 Catch:{ all -> 0x0031 }
    L_0x0016:
        r3 = r1.moveToNext();	 Catch:{ all -> 0x0031 }
        if (r3 == 0) goto L_0x0025;	 Catch:{ all -> 0x0031 }
    L_0x001c:
        r3 = 1;	 Catch:{ all -> 0x0031 }
        r3 = r1.getString(r3);	 Catch:{ all -> 0x0031 }
        r2.add(r3);	 Catch:{ all -> 0x0031 }
        goto L_0x0016;
        if (r1 == 0) goto L_0x002c;
    L_0x0028:
        r1.close();
        goto L_0x002d;
    L_0x002d:
        r0.close();
        return r2;
    L_0x0031:
        r2 = move-exception;
        if (r1 == 0) goto L_0x0038;
    L_0x0034:
        r1.close();
        goto L_0x0039;
    L_0x0039:
        r0.close();
        throw r2;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: de.danoeh.antennapod.core.storage.DBReader.getFeedListDownloadUrls():java.util.List<java.lang.String>");
    }

    private static java.util.Map<java.lang.Long, de.danoeh.antennapod.core.feed.FeedMedia> getFeedMedia(de.danoeh.antennapod.core.storage.PodDBAdapter r8, de.danoeh.antennapod.core.util.LongList r9) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:17:0x0062 in {2, 9, 10, 11, 13, 16} preds:[]
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
        r0 = new java.util.ArrayList;
        r1 = r9.size();
        r0.<init>(r1);
        r1 = r9.toArray();
        r2 = r1.length;
        r3 = 0;
        r4 = 0;
    L_0x0010:
        if (r4 >= r2) goto L_0x001e;
    L_0x0012:
        r5 = r1[r4];
        r7 = java.lang.String.valueOf(r5);
        r0.add(r7);
        r4 = r4 + 1;
        goto L_0x0010;
    L_0x001e:
        r1 = new android.support.v4.util.ArrayMap;
        r2 = r9.size();
        r1.<init>(r2);
        r2 = new java.lang.String[r3];
        r2 = r0.toArray(r2);
        r2 = (java.lang.String[]) r2;
        r2 = r8.getFeedMediaCursor(r2);
        r3 = r2.moveToFirst();	 Catch:{ all -> 0x005d }
        if (r3 == 0) goto L_0x0057;	 Catch:{ all -> 0x005d }
    L_0x0039:
        r3 = "feeditem";	 Catch:{ all -> 0x005d }
        r3 = r2.getColumnIndex(r3);	 Catch:{ all -> 0x005d }
        r4 = r2.getLong(r3);	 Catch:{ all -> 0x005d }
        r6 = de.danoeh.antennapod.core.feed.FeedMedia.fromCursor(r2);	 Catch:{ all -> 0x005d }
        r7 = java.lang.Long.valueOf(r4);	 Catch:{ all -> 0x005d }
        r1.put(r7, r6);	 Catch:{ all -> 0x005d }
        r3 = r2.moveToNext();	 Catch:{ all -> 0x005d }
        if (r3 != 0) goto L_0x0056;
    L_0x0055:
        goto L_0x0058;
    L_0x0056:
        goto L_0x0039;
    L_0x0058:
        r2.close();
        return r1;
    L_0x005d:
        r3 = move-exception;
        r2.close();
        throw r3;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: de.danoeh.antennapod.core.storage.DBReader.getFeedMedia(de.danoeh.antennapod.core.storage.PodDBAdapter, de.danoeh.antennapod.core.util.LongList):java.util.Map<java.lang.Long, de.danoeh.antennapod.core.feed.FeedMedia>");
    }

    public static java.util.List<de.danoeh.antennapod.core.storage.FeedItemStatistics> getFeedStatisticsList() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:19:0x0043 in {6, 9, 10, 12, 15, 16, 18} preds:[]
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
        r0 = "DBReader";
        r1 = "getFeedStatisticsList() called";
        android.util.Log.d(r0, r1);
        r0 = de.danoeh.antennapod.core.storage.PodDBAdapter.getInstance();
        r0.open();
        r1 = 0;
        r2 = r0.getFeedStatisticsCursor();	 Catch:{ all -> 0x0037 }
        r1 = r2;	 Catch:{ all -> 0x0037 }
        r2 = new java.util.ArrayList;	 Catch:{ all -> 0x0037 }
        r3 = r1.getCount();	 Catch:{ all -> 0x0037 }
        r2.<init>(r3);	 Catch:{ all -> 0x0037 }
    L_0x001d:
        r3 = r1.moveToNext();	 Catch:{ all -> 0x0037 }
        if (r3 == 0) goto L_0x002b;	 Catch:{ all -> 0x0037 }
    L_0x0023:
        r3 = de.danoeh.antennapod.core.storage.FeedItemStatistics.fromCursor(r1);	 Catch:{ all -> 0x0037 }
        r2.add(r3);	 Catch:{ all -> 0x0037 }
        goto L_0x001d;
        if (r1 == 0) goto L_0x0032;
    L_0x002e:
        r1.close();
        goto L_0x0033;
    L_0x0033:
        r0.close();
        return r2;
    L_0x0037:
        r2 = move-exception;
        if (r1 == 0) goto L_0x003e;
    L_0x003a:
        r1.close();
        goto L_0x003f;
    L_0x003f:
        r0.close();
        throw r2;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: de.danoeh.antennapod.core.storage.DBReader.getFeedStatisticsList():java.util.List<de.danoeh.antennapod.core.storage.FeedItemStatistics>");
    }

    public static java.util.List<de.danoeh.antennapod.core.feed.FeedItem> getPlaybackHistory() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:27:0x0074 in {7, 11, 12, 14, 15, 17, 20, 21, 23, 24, 26} preds:[]
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
        r0 = "DBReader";
        r1 = "getPlaybackHistory() called";
        android.util.Log.d(r0, r1);
        r0 = de.danoeh.antennapod.core.storage.PodDBAdapter.getInstance();
        r0.open();
        r1 = 0;
        r2 = 0;
        r3 = 50;
        r3 = r0.getCompletedMediaCursor(r3);	 Catch:{ all -> 0x0061 }
        r1 = r3;	 Catch:{ all -> 0x0061 }
        r3 = r1.getCount();	 Catch:{ all -> 0x0061 }
        r3 = new java.lang.String[r3];	 Catch:{ all -> 0x0061 }
        r4 = 0;	 Catch:{ all -> 0x0061 }
    L_0x001e:
        r5 = r3.length;	 Catch:{ all -> 0x0061 }
        if (r4 >= r5) goto L_0x003a;	 Catch:{ all -> 0x0061 }
    L_0x0021:
        r5 = r1.moveToPosition(r4);	 Catch:{ all -> 0x0061 }
        if (r5 == 0) goto L_0x003a;	 Catch:{ all -> 0x0061 }
    L_0x0027:
        r5 = "feeditem";	 Catch:{ all -> 0x0061 }
        r5 = r1.getColumnIndex(r5);	 Catch:{ all -> 0x0061 }
        r6 = r1.getLong(r5);	 Catch:{ all -> 0x0061 }
        r6 = java.lang.Long.toString(r6);	 Catch:{ all -> 0x0061 }
        r3[r4] = r6;	 Catch:{ all -> 0x0061 }
        r4 = r4 + 1;	 Catch:{ all -> 0x0061 }
        goto L_0x001e;	 Catch:{ all -> 0x0061 }
    L_0x003a:
        r4 = r0.getFeedItemCursor(r3);	 Catch:{ all -> 0x0061 }
        r2 = r4;	 Catch:{ all -> 0x0061 }
        r4 = extractItemlistFromCursor(r0, r2);	 Catch:{ all -> 0x0061 }
        loadAdditionalFeedItemListData(r4);	 Catch:{ all -> 0x0061 }
        r5 = new de.danoeh.antennapod.core.util.comparator.PlaybackCompletionDateComparator;	 Catch:{ all -> 0x0061 }
        r5.<init>();	 Catch:{ all -> 0x0061 }
        java.util.Collections.sort(r4, r5);	 Catch:{ all -> 0x0061 }
        if (r1 == 0) goto L_0x0055;
    L_0x0051:
        r1.close();
        goto L_0x0056;
    L_0x0056:
        if (r2 == 0) goto L_0x005c;
    L_0x0058:
        r2.close();
        goto L_0x005d;
    L_0x005d:
        r0.close();
        return r4;
    L_0x0061:
        r3 = move-exception;
        if (r1 == 0) goto L_0x0068;
    L_0x0064:
        r1.close();
        goto L_0x0069;
    L_0x0069:
        if (r2 == 0) goto L_0x006f;
    L_0x006b:
        r2.close();
        goto L_0x0070;
    L_0x0070:
        r0.close();
        throw r3;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: de.danoeh.antennapod.core.storage.DBReader.getPlaybackHistory():java.util.List<de.danoeh.antennapod.core.feed.FeedItem>");
    }

    private static de.danoeh.antennapod.core.util.LongList getQueueIDList(de.danoeh.antennapod.core.storage.PodDBAdapter r4) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:17:0x0030 in {6, 9, 10, 11, 14, 15, 16} preds:[]
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
        r0 = 0;
        r1 = r4.getQueueIDCursor();	 Catch:{ all -> 0x0027 }
        r0 = r1;	 Catch:{ all -> 0x0027 }
        r1 = new de.danoeh.antennapod.core.util.LongList;	 Catch:{ all -> 0x0027 }
        r2 = r0.getCount();	 Catch:{ all -> 0x0027 }
        r1.<init>(r2);	 Catch:{ all -> 0x0027 }
    L_0x000f:
        r2 = r0.moveToNext();	 Catch:{ all -> 0x0027 }
        if (r2 == 0) goto L_0x001e;	 Catch:{ all -> 0x0027 }
    L_0x0015:
        r2 = 0;	 Catch:{ all -> 0x0027 }
        r2 = r0.getLong(r2);	 Catch:{ all -> 0x0027 }
        r1.add(r2);	 Catch:{ all -> 0x0027 }
        goto L_0x000f;
        if (r0 == 0) goto L_0x0025;
    L_0x0021:
        r0.close();
        goto L_0x0026;
    L_0x0026:
        return r1;
    L_0x0027:
        r1 = move-exception;
        if (r0 == 0) goto L_0x002e;
    L_0x002a:
        r0.close();
        goto L_0x002f;
    L_0x002f:
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: de.danoeh.antennapod.core.storage.DBReader.getQueueIDList(de.danoeh.antennapod.core.storage.PodDBAdapter):de.danoeh.antennapod.core.util.LongList");
    }

    private static void loadChaptersOfFeedItem(de.danoeh.antennapod.core.storage.PodDBAdapter r4, de.danoeh.antennapod.core.feed.FeedItem r5) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:29:0x0048 in {6, 7, 8, 16, 17, 18, 21, 22, 23, 26, 27, 28} preds:[]
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
        r0 = 0;
        r1 = r4.getSimpleChaptersOfFeedItemCursor(r5);	 Catch:{ all -> 0x003f }
        r0 = r1;	 Catch:{ all -> 0x003f }
        r1 = r0.getCount();	 Catch:{ all -> 0x003f }
        if (r1 != 0) goto L_0x0018;	 Catch:{ all -> 0x003f }
    L_0x000c:
        r2 = 0;	 Catch:{ all -> 0x003f }
        r5.setChapters(r2);	 Catch:{ all -> 0x003f }
        if (r0 == 0) goto L_0x0016;
    L_0x0012:
        r0.close();
        goto L_0x0017;
    L_0x0017:
        return;
    L_0x0018:
        r2 = new java.util.ArrayList;	 Catch:{ all -> 0x003f }
        r2.<init>(r1);	 Catch:{ all -> 0x003f }
        r5.setChapters(r2);	 Catch:{ all -> 0x003f }
    L_0x0020:
        r2 = r0.moveToNext();	 Catch:{ all -> 0x003f }
        if (r2 == 0) goto L_0x0036;	 Catch:{ all -> 0x003f }
    L_0x0026:
        r2 = de.danoeh.antennapod.core.feed.Chapter.fromCursor(r0, r5);	 Catch:{ all -> 0x003f }
        if (r2 == 0) goto L_0x0034;	 Catch:{ all -> 0x003f }
    L_0x002c:
        r3 = r5.getChapters();	 Catch:{ all -> 0x003f }
        r3.add(r2);	 Catch:{ all -> 0x003f }
        goto L_0x0035;
    L_0x0035:
        goto L_0x0020;
        if (r0 == 0) goto L_0x003d;
    L_0x0039:
        r0.close();
        goto L_0x003e;
    L_0x003e:
        return;
    L_0x003f:
        r1 = move-exception;
        if (r0 == 0) goto L_0x0046;
    L_0x0042:
        r0.close();
        goto L_0x0047;
    L_0x0047:
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: de.danoeh.antennapod.core.storage.DBReader.loadChaptersOfFeedItem(de.danoeh.antennapod.core.storage.PodDBAdapter, de.danoeh.antennapod.core.feed.FeedItem):void");
    }

    private DBReader() {
    }

    public static List<Feed> getFeedList() {
        Log.d(TAG, "Extracting Feedlist");
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        try {
            List<Feed> feedList = getFeedList(adapter);
            return feedList;
        } finally {
            adapter.close();
        }
    }

    public static void loadAdditionalFeedItemListData(List<FeedItem> items) {
        loadTagsOfFeedItemList(items);
        loadFeedDataOfFeedItemList(items);
    }

    private static void loadTagsOfFeedItemList(List<FeedItem> items) {
        LongList favoriteIds = getFavoriteIDList();
        LongList queueIds = getQueueIDList();
        for (FeedItem item : items) {
            if (favoriteIds.contains(item.getId())) {
                item.addTag(FeedItem.TAG_FAVORITE);
            }
            if (queueIds.contains(item.getId())) {
                item.addTag(FeedItem.TAG_QUEUE);
            }
        }
    }

    private static void loadFeedDataOfFeedItemList(List<FeedItem> items) {
        List<Feed> feeds = getFeedList();
        Map<Long, Feed> feedIndex = new ArrayMap(feeds.size());
        for (Feed feed : feeds) {
            feedIndex.put(Long.valueOf(feed.getId()), feed);
        }
        for (FeedItem item : items) {
            Feed feed2 = (Feed) feedIndex.get(Long.valueOf(item.getFeedId()));
            if (feed2 == null) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("No match found for item with ID ");
                stringBuilder.append(item.getId());
                stringBuilder.append(". Feed ID was ");
                stringBuilder.append(item.getFeedId());
                Log.w(str, stringBuilder.toString());
            }
            item.setFeed(feed2);
        }
    }

    public static List<FeedItem> extractItemlistFromCursor(Cursor itemlistCursor) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("extractItemlistFromCursor() called with: itemlistCursor = [");
        stringBuilder.append(itemlistCursor);
        stringBuilder.append("]");
        Log.d(str, stringBuilder.toString());
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        try {
            List<FeedItem> extractItemlistFromCursor = extractItemlistFromCursor(adapter, itemlistCursor);
            return extractItemlistFromCursor;
        } finally {
            adapter.close();
        }
    }

    private static List<FeedItem> extractItemlistFromCursor(PodDBAdapter adapter, Cursor cursor) {
        List<FeedItem> result = new ArrayList(cursor.getCount());
        LongList itemIds = new LongList(cursor.getCount());
        if (cursor.moveToFirst()) {
            while (true) {
                FeedItem item = FeedItem.fromCursor(cursor);
                result.add(item);
                itemIds.add(item.getId());
                if (!cursor.moveToNext()) {
                    break;
                }
            }
            Map<Long, FeedMedia> medias = getFeedMedia(adapter, itemIds);
            for (FeedItem item2 : result) {
                FeedMedia media = (FeedMedia) medias.get(Long.valueOf(item2.getId()));
                item2.setMedia(media);
                if (media != null) {
                    media.setItem(item2);
                }
            }
        }
        return result;
    }

    private static Feed extractFeedFromCursorRow(Cursor cursor) {
        Feed feed = Feed.fromCursor(cursor);
        feed.setPreferences(FeedPreferences.fromCursor(cursor));
        return feed;
    }

    static List<FeedItem> getQueue(PodDBAdapter adapter) {
        Log.d(TAG, "getQueue()");
        Cursor cursor = null;
        try {
            cursor = adapter.getQueueCursor();
            List<FeedItem> items = extractItemlistFromCursor(adapter, cursor);
            loadAdditionalFeedItemListData(items);
            if (cursor != null) {
                cursor.close();
            }
            return items;
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static LongList getQueueIDList() {
        Log.d(TAG, "getQueueIDList() called");
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        try {
            LongList queueIDList = getQueueIDList(adapter);
            return queueIDList;
        } finally {
            adapter.close();
        }
    }

    public static List<FeedItem> getQueue() {
        Log.d(TAG, "getQueue() called");
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        try {
            List<FeedItem> queue = getQueue(adapter);
            return queue;
        } finally {
            adapter.close();
        }
    }

    public static List<FeedItem> getDownloadedItems() {
        Log.d(TAG, "getDownloadedItems() called");
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        Cursor cursor = null;
        try {
            cursor = adapter.getDownloadedItemsCursor();
            List<FeedItem> items = extractItemlistFromCursor(adapter, cursor);
            loadAdditionalFeedItemListData(items);
            Collections.sort(items, new FeedItemPubdateComparator());
            if (cursor != null) {
                cursor.close();
            }
            adapter.close();
            return items;
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
            adapter.close();
        }
    }

    public static List<FeedItem> getNewItemsList() {
        Log.d(TAG, "getNewItemsList() called");
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        Cursor cursor = null;
        try {
            cursor = adapter.getNewItemsCursor();
            List<FeedItem> items = extractItemlistFromCursor(adapter, cursor);
            loadAdditionalFeedItemListData(items);
            if (cursor != null) {
                cursor.close();
            }
            adapter.close();
            return items;
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
            adapter.close();
        }
    }

    public static List<FeedItem> getFavoriteItemsList() {
        Log.d(TAG, "getFavoriteItemsList() called");
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        Cursor cursor = null;
        try {
            cursor = adapter.getFavoritesCursor();
            List<FeedItem> items = extractItemlistFromCursor(adapter, cursor);
            loadAdditionalFeedItemListData(items);
            if (cursor != null) {
                cursor.close();
            }
            adapter.close();
            return items;
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
            adapter.close();
        }
    }

    public static List<FeedItem> getRecentlyPublishedEpisodes(int limit) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("getRecentlyPublishedEpisodes() called with: limit = [");
        stringBuilder.append(limit);
        stringBuilder.append("]");
        Log.d(str, stringBuilder.toString());
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        Cursor cursor = null;
        try {
            cursor = adapter.getRecentlyPublishedItemsCursor(limit);
            List<FeedItem> items = extractItemlistFromCursor(adapter, cursor);
            loadAdditionalFeedItemListData(items);
            if (cursor != null) {
                cursor.close();
            }
            adapter.close();
            return items;
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
            adapter.close();
        }
    }

    public static Feed getFeed(long feedId) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("getFeed() called with: feedId = [");
        stringBuilder.append(feedId);
        stringBuilder.append("]");
        Log.d(str, stringBuilder.toString());
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        try {
            Feed feed = getFeed(feedId, adapter);
            return feed;
        } finally {
            adapter.close();
        }
    }

    @Nullable
    static Feed getFeed(long feedId, PodDBAdapter adapter) {
        Feed feed = null;
        Cursor cursor = null;
        try {
            cursor = adapter.getFeedCursor(feedId);
            if (cursor.moveToNext()) {
                feed = extractFeedFromCursorRow(cursor);
                feed.setItems(getFeedItemList(feed));
            } else {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("getFeed could not find feed with id ");
                stringBuilder.append(feedId);
                Log.e(str, stringBuilder.toString());
            }
            if (cursor != null) {
                cursor.close();
            }
            return feed;
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private static FeedItem getFeedItem(long itemId, PodDBAdapter adapter) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Loading feeditem with id ");
        stringBuilder.append(itemId);
        Log.d(str, stringBuilder.toString());
        FeedItem item = null;
        Cursor cursor = null;
        try {
            cursor = adapter.getFeedItemCursor(Long.toString(itemId));
            if (cursor.moveToNext()) {
                List<FeedItem> list = extractItemlistFromCursor(adapter, cursor);
                if (!list.isEmpty()) {
                    item = (FeedItem) list.get(0);
                    loadAdditionalFeedItemListData(list);
                    if (item.hasChapters()) {
                        loadChaptersOfFeedItem(adapter, item);
                    }
                }
            }
            if (cursor != null) {
                cursor.close();
            }
            return item;
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static FeedItem getFeedItem(long itemId) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("getFeedItem() called with: itemId = [");
        stringBuilder.append(itemId);
        stringBuilder.append("]");
        Log.d(str, stringBuilder.toString());
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        try {
            FeedItem feedItem = getFeedItem(itemId, adapter);
            return feedItem;
        } finally {
            adapter.close();
        }
    }

    private static FeedItem getFeedItem(String podcastUrl, String episodeUrl, PodDBAdapter adapter) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Loading feeditem with podcast url ");
        stringBuilder.append(podcastUrl);
        stringBuilder.append(" and episode url ");
        stringBuilder.append(episodeUrl);
        Log.d(str, stringBuilder.toString());
        Cursor cursor = null;
        try {
            cursor = adapter.getFeedItemCursor(podcastUrl, episodeUrl);
            if (cursor.moveToNext()) {
                List<FeedItem> list = extractItemlistFromCursor(adapter, cursor);
                FeedItem item = null;
                if (!list.isEmpty()) {
                    item = (FeedItem) list.get(0);
                    loadAdditionalFeedItemListData(list);
                    if (item.hasChapters()) {
                        loadChaptersOfFeedItem(adapter, item);
                    }
                }
                if (cursor != null) {
                    cursor.close();
                }
                return item;
            }
            if (cursor != null) {
                cursor.close();
            }
            return null;
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static String getImageAuthentication(String imageUrl) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("getImageAuthentication() called with: imageUrl = [");
        stringBuilder.append(imageUrl);
        stringBuilder.append("]");
        Log.d(str, stringBuilder.toString());
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        try {
            String imageAuthentication = getImageAuthentication(imageUrl, adapter);
            return imageAuthentication;
        } finally {
            adapter.close();
        }
    }

    private static String getImageAuthentication(String imageUrl, PodDBAdapter adapter) {
        Cursor cursor = null;
        try {
            String credentials;
            cursor = adapter.getImageAuthenticationCursor(imageUrl);
            if (cursor.moveToFirst()) {
                String username = cursor.getString(null);
                String password = cursor.getString(1);
                if (TextUtils.isEmpty(username) || password == null) {
                    credentials = "";
                } else {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(username);
                    stringBuilder.append(":");
                    stringBuilder.append(password);
                    credentials = stringBuilder.toString();
                }
            } else {
                credentials = "";
            }
            if (cursor != null) {
                cursor.close();
            }
            return credentials;
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static FeedItem getFeedItem(String podcastUrl, String episodeUrl) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("getFeedItem() called with: podcastUrl = [");
        stringBuilder.append(podcastUrl);
        stringBuilder.append("], episodeUrl = [");
        stringBuilder.append(episodeUrl);
        stringBuilder.append("]");
        Log.d(str, stringBuilder.toString());
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        try {
            FeedItem feedItem = getFeedItem(podcastUrl, episodeUrl, adapter);
            return feedItem;
        } finally {
            adapter.close();
        }
    }

    public static void loadExtraInformationOfFeedItem(FeedItem item) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("loadExtraInformationOfFeedItem() called with: item = [");
        stringBuilder.append(item);
        stringBuilder.append("]");
        Log.d(str, stringBuilder.toString());
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        Cursor cursor = null;
        try {
            cursor = adapter.getExtraInformationOfItem(item);
            if (cursor.moveToFirst()) {
                String description = cursor.getString(cursor.getColumnIndex(PodDBAdapter.KEY_DESCRIPTION));
                String contentEncoded = cursor.getString(cursor.getColumnIndex(PodDBAdapter.KEY_CONTENT_ENCODED));
                item.setDescription(description);
                item.setContentEncoded(contentEncoded);
            }
            if (cursor != null) {
                cursor.close();
            }
            adapter.close();
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
            adapter.close();
        }
    }

    public static void loadChaptersOfFeedItem(FeedItem item) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("loadChaptersOfFeedItem() called with: item = [");
        stringBuilder.append(item);
        stringBuilder.append("]");
        Log.d(str, stringBuilder.toString());
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        try {
            loadChaptersOfFeedItem(adapter, item);
        } finally {
            adapter.close();
        }
    }

    public static int getNumberOfDownloadedEpisodes() {
        Log.d(TAG, "getNumberOfDownloadedEpisodes() called with: ");
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        try {
            int numberOfDownloadedEpisodes = adapter.getNumberOfDownloadedEpisodes();
            return numberOfDownloadedEpisodes;
        } finally {
            adapter.close();
        }
    }

    public static FeedMedia getFeedMedia(long mediaId) {
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        Cursor mediaCursor = null;
        try {
            mediaCursor = adapter.getSingleFeedMediaCursor(mediaId);
            if (mediaCursor.moveToFirst()) {
                long itemId = mediaCursor.getLong(mediaCursor.getColumnIndex(PodDBAdapter.KEY_FEEDITEM));
                FeedMedia media = FeedMedia.fromCursor(mediaCursor);
                if (media != null) {
                    FeedItem item = getFeedItem(itemId);
                    if (item != null) {
                        media.setItem(item);
                        item.setMedia(media);
                    }
                }
                if (mediaCursor != null) {
                    mediaCursor.close();
                }
                adapter.close();
                return media;
            }
            if (mediaCursor != null) {
                mediaCursor.close();
            }
            adapter.close();
            return null;
        } catch (Throwable th) {
            if (mediaCursor != null) {
                mediaCursor.close();
            }
            adapter.close();
        }
    }

    public static StatisticsData getStatistics(boolean sortByCountAll) {
        PodDBAdapter adapter;
        long totalTimeCountAll;
        PodDBAdapter adapter2 = PodDBAdapter.getInstance();
        adapter2.open();
        long totalTimeCountAll2 = 0;
        long totalTime = 0;
        List<StatisticsItem> feedTime = new ArrayList();
        for (Feed feed : getFeedList()) {
            long feedPlayedTime = 0;
            long feedPlayedTimeCountAll = 0;
            long feedTotalTime = 0;
            long episodes = 0;
            long episodesStarted = 0;
            long episodesStartedIncludingMarked = 0;
            for (FeedItem item : getFeed(feed.getId()).getItems()) {
                FeedMedia media = item.getMedia();
                if (media != null) {
                    if (media.getPlaybackCompletionDate() != null) {
                        adapter = adapter2;
                        totalTimeCountAll = totalTimeCountAll2;
                        feedPlayedTime += (long) (media.getDuration() / 1000);
                    } else {
                        adapter = adapter2;
                        totalTimeCountAll = totalTimeCountAll2;
                    }
                    feedPlayedTime += (long) (media.getPlayedDuration() / 1000);
                    if (item.isPlayed()) {
                        feedPlayedTimeCountAll += (long) (media.getDuration() / 1000);
                    } else {
                        feedPlayedTimeCountAll += (long) (media.getPosition() / 1000);
                    }
                    if (media.getPlaybackCompletionDate() == null) {
                        if (media.getPlayedDuration() <= 0) {
                            if (!item.isPlayed()) {
                                if (media.getPosition() != 0) {
                                    feedTotalTime += (long) (media.getDuration() / 1000);
                                    episodes++;
                                    adapter2 = adapter;
                                    totalTimeCountAll2 = totalTimeCountAll;
                                }
                            }
                            episodesStartedIncludingMarked++;
                            feedTotalTime += (long) (media.getDuration() / 1000);
                            episodes++;
                            adapter2 = adapter;
                            totalTimeCountAll2 = totalTimeCountAll;
                        }
                    }
                    episodesStarted++;
                    if (item.isPlayed()) {
                        if (media.getPosition() != 0) {
                            feedTotalTime += (long) (media.getDuration() / 1000);
                            episodes++;
                            adapter2 = adapter;
                            totalTimeCountAll2 = totalTimeCountAll;
                        }
                    }
                    episodesStartedIncludingMarked++;
                    feedTotalTime += (long) (media.getDuration() / 1000);
                    episodes++;
                    adapter2 = adapter;
                    totalTimeCountAll2 = totalTimeCountAll;
                }
            }
            adapter = adapter2;
            totalTimeCountAll = totalTimeCountAll2;
            feedTime.add(new StatisticsItem(feed, feedTotalTime, feedPlayedTime, feedPlayedTimeCountAll, episodes, episodesStarted, episodesStartedIncludingMarked));
            totalTime += feedPlayedTime;
            totalTimeCountAll2 = totalTimeCountAll + feedPlayedTimeCountAll;
            adapter2 = adapter;
        }
        adapter = adapter2;
        totalTimeCountAll = totalTimeCountAll2;
        if (sortByCountAll) {
            Collections.sort(feedTime, -$$Lambda$DBReader$Vw_mNSL9bpyHgXZLORgzDg-B5CE.INSTANCE);
        } else {
            Collections.sort(feedTime, -$$Lambda$DBReader$J14FiokVfxZ2H5XUZEtHQOEEq_0.INSTANCE);
        }
        adapter.close();
        return new StatisticsData(totalTime, totalTimeCountAll, feedTime);
    }

    private static int compareLong(long long1, long long2) {
        if (long1 > long2) {
            return -1;
        }
        if (long1 < long2) {
            return 1;
        }
        return 0;
    }

    public static List<FlattrThing> getFlattrQueue() {
        Log.d(TAG, "getFlattrQueue() called with: ");
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        List<FlattrThing> result = new ArrayList();
        Cursor feedCursor = adapter.getFeedsInFlattrQueueCursor();
        if (feedCursor.moveToFirst()) {
            while (true) {
                result.add(extractFeedFromCursorRow(feedCursor));
                if (!feedCursor.moveToNext()) {
                    break;
                }
            }
        }
        feedCursor.close();
        Cursor feedItemCursor = adapter.getFeedItemsInFlattrQueueCursor();
        result.addAll(extractItemlistFromCursor(adapter, feedItemCursor));
        feedItemCursor.close();
        adapter.close();
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Returning flattrQueueIterator for queue with ");
        stringBuilder.append(result.size());
        stringBuilder.append(" items.");
        Log.d(str, stringBuilder.toString());
        return result;
    }

    public static NavDrawerData getNavDrawerData() {
        Comparator<Feed> comparator;
        Log.d(TAG, "getNavDrawerData() called with: ");
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        List<Feed> feeds = getFeedList(adapter);
        long[] feedIds = new long[feeds.size()];
        for (int i = 0; i < feeds.size(); i++) {
            feedIds[i] = ((Feed) feeds.get(i)).getId();
        }
        LongIntMap feedCounters = adapter.getFeedCounters(feedIds);
        int feedOrder = UserPreferences.getFeedOrder();
        if (feedOrder == 0) {
            comparator = new -$$Lambda$DBReader$l2IBubhY03Bb5jIh9v_HDz1tLmQ(feedCounters);
        } else if (feedOrder == 1) {
            comparator = -$$Lambda$DBReader$TrZZVHUlGig7ZDDMlhh7M_r8L6U.INSTANCE;
        } else if (feedOrder == 3) {
            comparator = new -$$Lambda$DBReader$OrL7AlSAPeevX8W4AgPx10WkxIs(adapter.getPlayedEpisodesCounters(feedIds));
        } else {
            comparator = -$$Lambda$DBReader$Z_pWv5ijLL2IlTQvad8N2cjEgIA.INSTANCE;
        }
        Collections.sort(feeds, comparator);
        int queueSize = adapter.getQueueSize();
        int numNewItems = adapter.getNumberOfNewItems();
        NavDrawerData result = new NavDrawerData(feeds, queueSize, numNewItems, adapter.getNumberOfDownloadedEpisodes(), feedCounters, UserPreferences.getEpisodeCleanupAlgorithm().getReclaimableItems());
        adapter.close();
        return result;
    }

    static /* synthetic */ int lambda$getNavDrawerData$2(LongIntMap feedCounters, Feed lhs, Feed rhs) {
        long counterLhs = (long) feedCounters.get(lhs.getId());
        long counterRhs = (long) feedCounters.get(rhs.getId());
        if (counterLhs > counterRhs) {
            return -1;
        }
        if (counterLhs == counterRhs) {
            return lhs.getTitle().compareToIgnoreCase(rhs.getTitle());
        }
        return 1;
    }

    static /* synthetic */ int lambda$getNavDrawerData$3(Feed lhs, Feed rhs) {
        String t1 = lhs.getTitle();
        String t2 = rhs.getTitle();
        if (t1 == null) {
            return 1;
        }
        if (t2 == null) {
            return -1;
        }
        return t1.compareToIgnoreCase(t2);
    }

    static /* synthetic */ int lambda$getNavDrawerData$4(LongIntMap playedCounters, Feed lhs, Feed rhs) {
        long counterLhs = (long) playedCounters.get(lhs.getId());
        long counterRhs = (long) playedCounters.get(rhs.getId());
        if (counterLhs > counterRhs) {
            return -1;
        }
        if (counterLhs == counterRhs) {
            return lhs.getTitle().compareToIgnoreCase(rhs.getTitle());
        }
        return 1;
    }

    static /* synthetic */ int lambda$getNavDrawerData$5(Feed lhs, Feed rhs) {
        if (lhs.getItems() != null) {
            if (lhs.getItems().size() != 0) {
                if (rhs.getItems() != null) {
                    if (rhs.getItems().size() == 0) {
                        if (lhs.getMostRecentItem() != null) {
                            return 1;
                        }
                        if (rhs.getMostRecentItem() != null) {
                            return -1;
                        }
                        return rhs.getMostRecentItem().getPubDate().compareTo(lhs.getMostRecentItem().getPubDate());
                    }
                }
                rhs.setItems(getFeedItemList(rhs));
                if (lhs.getMostRecentItem() != null) {
                    return 1;
                }
                if (rhs.getMostRecentItem() != null) {
                    return -1;
                }
                return rhs.getMostRecentItem().getPubDate().compareTo(lhs.getMostRecentItem().getPubDate());
            }
        }
        lhs.setItems(getFeedItemList(lhs));
        if (rhs.getItems() != null) {
            if (rhs.getItems().size() == 0) {
                if (lhs.getMostRecentItem() != null) {
                    return 1;
                }
                if (rhs.getMostRecentItem() != null) {
                    return -1;
                }
                return rhs.getMostRecentItem().getPubDate().compareTo(lhs.getMostRecentItem().getPubDate());
            }
        }
        rhs.setItems(getFeedItemList(rhs));
        if (lhs.getMostRecentItem() != null) {
            return 1;
        }
        if (rhs.getMostRecentItem() != null) {
            return -1;
        }
        return rhs.getMostRecentItem().getPubDate().compareTo(lhs.getMostRecentItem().getPubDate());
    }
}
