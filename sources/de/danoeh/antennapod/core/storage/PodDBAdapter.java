package de.danoeh.antennapod.core.storage;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.MergeCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.util.Log;
import de.danoeh.antennapod.core.feed.Chapter;
import de.danoeh.antennapod.core.feed.Feed;
import de.danoeh.antennapod.core.feed.FeedItem;
import de.danoeh.antennapod.core.feed.FeedMedia;
import de.danoeh.antennapod.core.feed.FeedPreferences;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.service.download.DownloadStatus;
import de.danoeh.antennapod.core.util.LongIntMap;
import de.danoeh.antennapod.core.util.flattr.FlattrStatus;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

public class PodDBAdapter {
    private static final String[] ALL_TABLES = new String[]{TABLE_NAME_FEEDS, TABLE_NAME_FEED_ITEMS, TABLE_NAME_FEED_MEDIA, TABLE_NAME_DOWNLOAD_LOG, "Queue", TABLE_NAME_SIMPLECHAPTERS, TABLE_NAME_FAVORITES};
    static final String CREATE_INDEX_FEEDITEMS_FEED = "CREATE INDEX FeedItems_feed ON FeedItems (feed)";
    static final String CREATE_INDEX_FEEDITEMS_PUBDATE = "CREATE INDEX IF NOT EXISTS FeedItems_pubDate ON FeedItems (pubDate)";
    static final String CREATE_INDEX_FEEDITEMS_READ = "CREATE INDEX IF NOT EXISTS FeedItems_read ON FeedItems (read)";
    static final String CREATE_INDEX_FEEDMEDIA_FEEDITEM = "CREATE INDEX FeedMedia_feeditem ON FeedMedia (feeditem)";
    static final String CREATE_INDEX_QUEUE_FEEDITEM = "CREATE INDEX Queue_feeditem ON Queue (feeditem)";
    static final String CREATE_INDEX_SIMPLECHAPTERS_FEEDITEM = "CREATE INDEX SimpleChapters_feeditem ON SimpleChapters (feeditem)";
    private static final String CREATE_TABLE_DOWNLOAD_LOG = "CREATE TABLE DownloadLog (id INTEGER PRIMARY KEY AUTOINCREMENT ,feedfile INTEGER,feedfile_type INTEGER,reason INTEGER,successful INTEGER,completion_date INTEGER,reason_detailed TEXT,title TEXT)";
    static final String CREATE_TABLE_FAVORITES = "CREATE TABLE Favorites(id INTEGER PRIMARY KEY,feeditem INTEGER,feed INTEGER)";
    private static final String CREATE_TABLE_FEEDS = "CREATE TABLE Feeds (id INTEGER PRIMARY KEY AUTOINCREMENT ,title TEXT,custom_title TEXT,file_url TEXT,download_url TEXT,downloaded INTEGER,link TEXT,description TEXT,payment_link TEXT,last_update TEXT,language TEXT,author TEXT,image_url TEXT,type TEXT,feed_identifier TEXT,auto_download INTEGER DEFAULT 1,flattr_status INTEGER,username TEXT,password TEXT,include_filter TEXT DEFAULT '',exclude_filter TEXT DEFAULT '',keep_updated INTEGER DEFAULT 1,is_paged INTEGER DEFAULT 0,next_page_link TEXT,hide TEXT,last_update_failed INTEGER DEFAULT 0,auto_delete_action INTEGER DEFAULT 0)";
    private static final String CREATE_TABLE_FEED_ITEMS = "CREATE TABLE FeedItems (id INTEGER PRIMARY KEY AUTOINCREMENT ,title TEXT,content_encoded TEXT,pubDate INTEGER,read INTEGER,link TEXT,description TEXT,payment_link TEXT,media INTEGER,feed INTEGER,has_simple_chapters INTEGER,item_identifier TEXT,flattr_status INTEGER,image_url TEXT,auto_download INTEGER)";
    private static final String CREATE_TABLE_FEED_MEDIA = "CREATE TABLE FeedMedia (id INTEGER PRIMARY KEY AUTOINCREMENT ,duration INTEGER,file_url TEXT,download_url TEXT,downloaded INTEGER,position INTEGER,filesize INTEGER,mime_type TEXT,playback_completion_date INTEGER,feeditem INTEGER,played_duration INTEGER,has_embedded_picture INTEGER,last_played_time INTEGER)";
    private static final String CREATE_TABLE_QUEUE = "CREATE TABLE Queue(id INTEGER PRIMARY KEY,feeditem INTEGER,feed INTEGER)";
    private static final String CREATE_TABLE_SIMPLECHAPTERS = "CREATE TABLE SimpleChapters (id INTEGER PRIMARY KEY AUTOINCREMENT ,title TEXT,start INTEGER,feeditem INTEGER,link TEXT,type INTEGER)";
    public static final String DATABASE_NAME = "Antennapod.db";
    private static final String[] FEEDITEM_SEL_FI_SMALL = new String[]{"FeedItems.id", "FeedItems.title", "FeedItems.pubDate", "FeedItems.read", "FeedItems.link", "FeedItems.payment_link", "FeedItems.media", "FeedItems.feed", "FeedItems.has_simple_chapters", "FeedItems.item_identifier", "FeedItems.flattr_status", "FeedItems.image_url", "FeedItems.auto_download"};
    private static final String[] FEED_SEL_STD = new String[]{"Feeds.id", "Feeds.title", "Feeds.custom_title", "Feeds.file_url", "Feeds.download_url", "Feeds.downloaded", "Feeds.link", "Feeds.description", "Feeds.payment_link", "Feeds.last_update", "Feeds.language", "Feeds.author", "Feeds.image_url", "Feeds.type", "Feeds.feed_identifier", "Feeds.auto_download", "Feeds.keep_updated", "Feeds.flattr_status", "Feeds.is_paged", "Feeds.next_page_link", "Feeds.username", "Feeds.password", "Feeds.hide", "Feeds.last_update_failed", "Feeds.auto_delete_action", "Feeds.include_filter", "Feeds.exclude_filter"};
    private static final String FEED_STATISTICS_QUERY = "SELECT Feeds.id, num_items, new_items, latest_episode, in_progress FROM  Feeds LEFT JOIN (SELECT feed,count(*) AS num_items, COUNT(CASE WHEN read=0 THEN 1 END) AS new_items, MAX(pubDate) AS latest_episode, COUNT(CASE WHEN position>0 THEN 1 END) AS in_progress, COUNT(CASE WHEN downloaded=1 THEN 1 END) AS episodes_downloaded  FROM FeedItems LEFT JOIN FeedMedia ON FeedItems.id=FeedMedia.feeditem GROUP BY FeedItems.feed) ON Feeds.id = feed ORDER BY Feeds.title COLLATE NOCASE ASC;";
    public static final int IDX_FEEDSTATISTICS_FEED = 0;
    public static final int IDX_FEEDSTATISTICS_IN_PROGRESS_EPISODES = 4;
    public static final int IDX_FEEDSTATISTICS_LATEST_EPISODE = 3;
    public static final int IDX_FEEDSTATISTICS_NEW_ITEMS = 2;
    public static final int IDX_FEEDSTATISTICS_NUM_ITEMS = 1;
    private static final int IN_OPERATOR_MAXIMUM = 800;
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_AUTO_DELETE_ACTION = "auto_delete_action";
    public static final String KEY_AUTO_DOWNLOAD = "auto_download";
    public static final String KEY_CHAPTER_TYPE = "type";
    public static final String KEY_COMPLETION_DATE = "completion_date";
    public static final String KEY_CONTENT_ENCODED = "content_encoded";
    public static final String KEY_CUSTOM_TITLE = "custom_title";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_DOWNLOADED = "downloaded";
    public static final String KEY_DOWNLOADSTATUS_TITLE = "title";
    public static final String KEY_DOWNLOAD_URL = "download_url";
    public static final String KEY_DURATION = "duration";
    public static final String KEY_EXCLUDE_FILTER = "exclude_filter";
    public static final String KEY_FEED = "feed";
    public static final String KEY_FEEDFILE = "feedfile";
    public static final String KEY_FEEDFILETYPE = "feedfile_type";
    public static final String KEY_FEEDITEM = "feeditem";
    public static final String KEY_FEED_IDENTIFIER = "feed_identifier";
    public static final String KEY_FILE_URL = "file_url";
    public static final String KEY_FLATTR_STATUS = "flattr_status";
    public static final String KEY_HAS_CHAPTERS = "has_simple_chapters";
    public static final String KEY_HAS_EMBEDDED_PICTURE = "has_embedded_picture";
    public static final String KEY_HIDE = "hide";
    public static final String KEY_ID = "id";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_IMAGE_URL = "image_url";
    public static final String KEY_INCLUDE_FILTER = "include_filter";
    public static final String KEY_IS_PAGED = "is_paged";
    public static final String KEY_ITEM_IDENTIFIER = "item_identifier";
    public static final String KEY_KEEP_UPDATED = "keep_updated";
    public static final String KEY_LANGUAGE = "language";
    public static final String KEY_LASTUPDATE = "last_update";
    public static final String KEY_LAST_PLAYED_TIME = "last_played_time";
    public static final String KEY_LAST_UPDATE_FAILED = "last_update_failed";
    public static final String KEY_LINK = "link";
    public static final String KEY_MEDIA = "media";
    public static final String KEY_MIME_TYPE = "mime_type";
    public static final String KEY_NAME = "name";
    public static final String KEY_NEXT_PAGE_LINK = "next_page_link";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_PAYMENT_LINK = "payment_link";
    public static final String KEY_PLAYBACK_COMPLETION_DATE = "playback_completion_date";
    public static final String KEY_PLAYED_DURATION = "played_duration";
    public static final String KEY_POSITION = "position";
    public static final String KEY_PUBDATE = "pubDate";
    public static final String KEY_READ = "read";
    public static final String KEY_REASON = "reason";
    public static final String KEY_REASON_DETAILED = "reason_detailed";
    public static final String KEY_SIZE = "filesize";
    public static final String KEY_START = "start";
    public static final String KEY_SUCCESSFUL = "successful";
    public static final String KEY_TITLE = "title";
    public static final String KEY_TYPE = "type";
    public static final String KEY_USERNAME = "username";
    public static final int SEARCH_LIMIT = 30;
    private static final String[] SEL_FI_EXTRA = new String[]{"id", KEY_DESCRIPTION, KEY_CONTENT_ENCODED, KEY_FEED};
    private static final String SEL_FI_SMALL_STR;
    static final String TABLE_NAME_DOWNLOAD_LOG = "DownloadLog";
    static final String TABLE_NAME_FAVORITES = "Favorites";
    static final String TABLE_NAME_FEEDS = "Feeds";
    static final String TABLE_NAME_FEED_IMAGES = "FeedImages";
    static final String TABLE_NAME_FEED_ITEMS = "FeedItems";
    static final String TABLE_NAME_FEED_MEDIA = "FeedMedia";
    static final String TABLE_NAME_QUEUE = "Queue";
    static final String TABLE_NAME_SIMPLECHAPTERS = "SimpleChapters";
    private static final String TABLE_PRIMARY_KEY = "id INTEGER PRIMARY KEY AUTOINCREMENT ,";
    private static final String TAG = "PodDBAdapter";
    private static Context context;
    private static volatile SQLiteDatabase db;

    public static boolean deleteDatabase() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:11:0x0026 in {5, 7, 10} preds:[]
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
        r0 = getInstance();
        r0.open();
        r1 = ALL_TABLES;	 Catch:{ all -> 0x0021 }
        r2 = r1.length;	 Catch:{ all -> 0x0021 }
        r3 = 0;	 Catch:{ all -> 0x0021 }
    L_0x000b:
        if (r3 >= r2) goto L_0x001b;	 Catch:{ all -> 0x0021 }
    L_0x000d:
        r4 = r1[r3];	 Catch:{ all -> 0x0021 }
        r5 = db;	 Catch:{ all -> 0x0021 }
        r6 = "1";	 Catch:{ all -> 0x0021 }
        r7 = 0;	 Catch:{ all -> 0x0021 }
        r5.delete(r4, r6, r7);	 Catch:{ all -> 0x0021 }
        r3 = r3 + 1;
        goto L_0x000b;
        r0.close();
        r1 = 1;
        return r1;
    L_0x0021:
        r1 = move-exception;
        r0.close();
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: de.danoeh.antennapod.core.storage.PodDBAdapter.deleteDatabase():boolean");
    }

    public void removeFeed(de.danoeh.antennapod.core.feed.Feed r9) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:21:0x0078 in {6, 7, 8, 10, 16, 18, 20} preds:[]
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
        r8 = this;
        r0 = db;	 Catch:{ SQLException -> 0x0060 }
        r0.beginTransactionNonExclusive();	 Catch:{ SQLException -> 0x0060 }
        r0 = r9.getItems();	 Catch:{ SQLException -> 0x0060 }
        if (r0 == 0) goto L_0x0024;	 Catch:{ SQLException -> 0x0060 }
    L_0x000b:
        r0 = r9.getItems();	 Catch:{ SQLException -> 0x0060 }
        r0 = r0.iterator();	 Catch:{ SQLException -> 0x0060 }
    L_0x0013:
        r1 = r0.hasNext();	 Catch:{ SQLException -> 0x0060 }
        if (r1 == 0) goto L_0x0023;	 Catch:{ SQLException -> 0x0060 }
    L_0x0019:
        r1 = r0.next();	 Catch:{ SQLException -> 0x0060 }
        r1 = (de.danoeh.antennapod.core.feed.FeedItem) r1;	 Catch:{ SQLException -> 0x0060 }
        r8.removeFeedItem(r1);	 Catch:{ SQLException -> 0x0060 }
        goto L_0x0013;	 Catch:{ SQLException -> 0x0060 }
    L_0x0023:
        goto L_0x0025;	 Catch:{ SQLException -> 0x0060 }
    L_0x0025:
        r0 = db;	 Catch:{ SQLException -> 0x0060 }
        r1 = "DownloadLog";	 Catch:{ SQLException -> 0x0060 }
        r2 = "feedfile=? AND feedfile_type=?";	 Catch:{ SQLException -> 0x0060 }
        r3 = 2;	 Catch:{ SQLException -> 0x0060 }
        r3 = new java.lang.String[r3];	 Catch:{ SQLException -> 0x0060 }
        r4 = r9.getId();	 Catch:{ SQLException -> 0x0060 }
        r4 = java.lang.String.valueOf(r4);	 Catch:{ SQLException -> 0x0060 }
        r5 = 0;	 Catch:{ SQLException -> 0x0060 }
        r3[r5] = r4;	 Catch:{ SQLException -> 0x0060 }
        r4 = java.lang.String.valueOf(r5);	 Catch:{ SQLException -> 0x0060 }
        r6 = 1;	 Catch:{ SQLException -> 0x0060 }
        r3[r6] = r4;	 Catch:{ SQLException -> 0x0060 }
        r0.delete(r1, r2, r3);	 Catch:{ SQLException -> 0x0060 }
        r0 = db;	 Catch:{ SQLException -> 0x0060 }
        r1 = "Feeds";	 Catch:{ SQLException -> 0x0060 }
        r2 = "id=?";	 Catch:{ SQLException -> 0x0060 }
        r3 = new java.lang.String[r6];	 Catch:{ SQLException -> 0x0060 }
        r6 = r9.getId();	 Catch:{ SQLException -> 0x0060 }
        r4 = java.lang.String.valueOf(r6);	 Catch:{ SQLException -> 0x0060 }
        r3[r5] = r4;	 Catch:{ SQLException -> 0x0060 }
        r0.delete(r1, r2, r3);	 Catch:{ SQLException -> 0x0060 }
        r0 = db;	 Catch:{ SQLException -> 0x0060 }
        r0.setTransactionSuccessful();	 Catch:{ SQLException -> 0x0060 }
        goto L_0x006b;
    L_0x005e:
        r0 = move-exception;
        goto L_0x0072;
    L_0x0060:
        r0 = move-exception;
        r1 = "PodDBAdapter";	 Catch:{ all -> 0x005e }
        r2 = android.util.Log.getStackTraceString(r0);	 Catch:{ all -> 0x005e }
        android.util.Log.e(r1, r2);	 Catch:{ all -> 0x005e }
    L_0x006b:
        r0 = db;
        r0.endTransaction();
        return;
    L_0x0072:
        r1 = db;
        r1.endTransaction();
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: de.danoeh.antennapod.core.storage.PodDBAdapter.removeFeed(de.danoeh.antennapod.core.feed.Feed):void");
    }

    public void setCompleteFeed(de.danoeh.antennapod.core.feed.Feed... r7) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:28:0x0061 in {8, 9, 10, 13, 14, 15, 17, 23, 25, 27} preds:[]
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
        r6 = this;
        r0 = db;	 Catch:{ SQLException -> 0x0049 }
        r0.beginTransactionNonExclusive();	 Catch:{ SQLException -> 0x0049 }
        r0 = r7.length;	 Catch:{ SQLException -> 0x0049 }
        r1 = 0;	 Catch:{ SQLException -> 0x0049 }
        r2 = 0;	 Catch:{ SQLException -> 0x0049 }
    L_0x0008:
        if (r2 >= r0) goto L_0x0041;	 Catch:{ SQLException -> 0x0049 }
    L_0x000a:
        r3 = r7[r2];	 Catch:{ SQLException -> 0x0049 }
        r6.setFeed(r3);	 Catch:{ SQLException -> 0x0049 }
        r4 = r3.getItems();	 Catch:{ SQLException -> 0x0049 }
        if (r4 == 0) goto L_0x002e;	 Catch:{ SQLException -> 0x0049 }
    L_0x0015:
        r4 = r3.getItems();	 Catch:{ SQLException -> 0x0049 }
        r4 = r4.iterator();	 Catch:{ SQLException -> 0x0049 }
    L_0x001d:
        r5 = r4.hasNext();	 Catch:{ SQLException -> 0x0049 }
        if (r5 == 0) goto L_0x002d;	 Catch:{ SQLException -> 0x0049 }
    L_0x0023:
        r5 = r4.next();	 Catch:{ SQLException -> 0x0049 }
        r5 = (de.danoeh.antennapod.core.feed.FeedItem) r5;	 Catch:{ SQLException -> 0x0049 }
        r6.setFeedItem(r5, r1);	 Catch:{ SQLException -> 0x0049 }
        goto L_0x001d;	 Catch:{ SQLException -> 0x0049 }
    L_0x002d:
        goto L_0x002f;	 Catch:{ SQLException -> 0x0049 }
    L_0x002f:
        r4 = r3.getPreferences();	 Catch:{ SQLException -> 0x0049 }
        if (r4 == 0) goto L_0x003d;	 Catch:{ SQLException -> 0x0049 }
    L_0x0035:
        r4 = r3.getPreferences();	 Catch:{ SQLException -> 0x0049 }
        r6.setFeedPreferences(r4);	 Catch:{ SQLException -> 0x0049 }
        goto L_0x003e;	 Catch:{ SQLException -> 0x0049 }
    L_0x003e:
        r2 = r2 + 1;	 Catch:{ SQLException -> 0x0049 }
        goto L_0x0008;	 Catch:{ SQLException -> 0x0049 }
    L_0x0041:
        r0 = db;	 Catch:{ SQLException -> 0x0049 }
        r0.setTransactionSuccessful();	 Catch:{ SQLException -> 0x0049 }
        goto L_0x0054;
    L_0x0047:
        r0 = move-exception;
        goto L_0x005b;
    L_0x0049:
        r0 = move-exception;
        r1 = "PodDBAdapter";	 Catch:{ all -> 0x0047 }
        r2 = android.util.Log.getStackTraceString(r0);	 Catch:{ all -> 0x0047 }
        android.util.Log.e(r1, r2);	 Catch:{ all -> 0x0047 }
    L_0x0054:
        r0 = db;
        r0.endTransaction();
        return;
    L_0x005b:
        r1 = db;
        r1.endTransaction();
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: de.danoeh.antennapod.core.storage.PodDBAdapter.setCompleteFeed(de.danoeh.antennapod.core.feed.Feed[]):void");
    }

    public void setFavorites(java.util.List<de.danoeh.antennapod.core.feed.FeedItem> r8) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:18:0x0072 in {5, 7, 13, 15, 17} preds:[]
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
        r7 = this;
        r0 = new android.content.ContentValues;
        r0.<init>();
        r1 = db;	 Catch:{ SQLException -> 0x005a }
        r1.beginTransactionNonExclusive();	 Catch:{ SQLException -> 0x005a }
        r1 = db;	 Catch:{ SQLException -> 0x005a }
        r2 = "Favorites";	 Catch:{ SQLException -> 0x005a }
        r3 = 0;	 Catch:{ SQLException -> 0x005a }
        r1.delete(r2, r3, r3);	 Catch:{ SQLException -> 0x005a }
        r1 = 0;	 Catch:{ SQLException -> 0x005a }
    L_0x0013:
        r2 = r8.size();	 Catch:{ SQLException -> 0x005a }
        if (r1 >= r2) goto L_0x0052;	 Catch:{ SQLException -> 0x005a }
    L_0x0019:
        r2 = r8.get(r1);	 Catch:{ SQLException -> 0x005a }
        r2 = (de.danoeh.antennapod.core.feed.FeedItem) r2;	 Catch:{ SQLException -> 0x005a }
        r4 = "id";	 Catch:{ SQLException -> 0x005a }
        r5 = java.lang.Integer.valueOf(r1);	 Catch:{ SQLException -> 0x005a }
        r0.put(r4, r5);	 Catch:{ SQLException -> 0x005a }
        r4 = "feeditem";	 Catch:{ SQLException -> 0x005a }
        r5 = r2.getId();	 Catch:{ SQLException -> 0x005a }
        r5 = java.lang.Long.valueOf(r5);	 Catch:{ SQLException -> 0x005a }
        r0.put(r4, r5);	 Catch:{ SQLException -> 0x005a }
        r4 = "feed";	 Catch:{ SQLException -> 0x005a }
        r5 = r2.getFeed();	 Catch:{ SQLException -> 0x005a }
        r5 = r5.getId();	 Catch:{ SQLException -> 0x005a }
        r5 = java.lang.Long.valueOf(r5);	 Catch:{ SQLException -> 0x005a }
        r0.put(r4, r5);	 Catch:{ SQLException -> 0x005a }
        r4 = db;	 Catch:{ SQLException -> 0x005a }
        r5 = "Favorites";	 Catch:{ SQLException -> 0x005a }
        r6 = 5;	 Catch:{ SQLException -> 0x005a }
        r4.insertWithOnConflict(r5, r3, r0, r6);	 Catch:{ SQLException -> 0x005a }
        r1 = r1 + 1;	 Catch:{ SQLException -> 0x005a }
        goto L_0x0013;	 Catch:{ SQLException -> 0x005a }
    L_0x0052:
        r1 = db;	 Catch:{ SQLException -> 0x005a }
        r1.setTransactionSuccessful();	 Catch:{ SQLException -> 0x005a }
        goto L_0x0065;
    L_0x0058:
        r1 = move-exception;
        goto L_0x006c;
    L_0x005a:
        r1 = move-exception;
        r2 = "PodDBAdapter";	 Catch:{ all -> 0x0058 }
        r3 = android.util.Log.getStackTraceString(r1);	 Catch:{ all -> 0x0058 }
        android.util.Log.e(r2, r3);	 Catch:{ all -> 0x0058 }
    L_0x0065:
        r1 = db;
        r1.endTransaction();
        return;
    L_0x006c:
        r2 = db;
        r2.endTransaction();
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: de.danoeh.antennapod.core.storage.PodDBAdapter.setFavorites(java.util.List):void");
    }

    public void setFeedItemRead(int r12, long... r13) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:16:0x0053 in {3, 5, 11, 13, 15} preds:[]
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
        r11 = this;
        r0 = db;	 Catch:{ SQLException -> 0x003b }
        r0.beginTransactionNonExclusive();	 Catch:{ SQLException -> 0x003b }
        r0 = new android.content.ContentValues;	 Catch:{ SQLException -> 0x003b }
        r0.<init>();	 Catch:{ SQLException -> 0x003b }
        r1 = r13.length;	 Catch:{ SQLException -> 0x003b }
        r2 = 0;	 Catch:{ SQLException -> 0x003b }
        r3 = 0;	 Catch:{ SQLException -> 0x003b }
    L_0x000d:
        if (r3 >= r1) goto L_0x0033;	 Catch:{ SQLException -> 0x003b }
    L_0x000f:
        r4 = r13[r3];	 Catch:{ SQLException -> 0x003b }
        r0.clear();	 Catch:{ SQLException -> 0x003b }
        r6 = "read";	 Catch:{ SQLException -> 0x003b }
        r7 = java.lang.Integer.valueOf(r12);	 Catch:{ SQLException -> 0x003b }
        r0.put(r6, r7);	 Catch:{ SQLException -> 0x003b }
        r6 = db;	 Catch:{ SQLException -> 0x003b }
        r7 = "FeedItems";	 Catch:{ SQLException -> 0x003b }
        r8 = "id=?";	 Catch:{ SQLException -> 0x003b }
        r9 = 1;	 Catch:{ SQLException -> 0x003b }
        r9 = new java.lang.String[r9];	 Catch:{ SQLException -> 0x003b }
        r10 = java.lang.String.valueOf(r4);	 Catch:{ SQLException -> 0x003b }
        r9[r2] = r10;	 Catch:{ SQLException -> 0x003b }
        r6.update(r7, r0, r8, r9);	 Catch:{ SQLException -> 0x003b }
        r3 = r3 + 1;	 Catch:{ SQLException -> 0x003b }
        goto L_0x000d;	 Catch:{ SQLException -> 0x003b }
    L_0x0033:
        r1 = db;	 Catch:{ SQLException -> 0x003b }
        r1.setTransactionSuccessful();	 Catch:{ SQLException -> 0x003b }
        goto L_0x0046;
    L_0x0039:
        r0 = move-exception;
        goto L_0x004d;
    L_0x003b:
        r0 = move-exception;
        r1 = "PodDBAdapter";	 Catch:{ all -> 0x0039 }
        r2 = android.util.Log.getStackTraceString(r0);	 Catch:{ all -> 0x0039 }
        android.util.Log.e(r1, r2);	 Catch:{ all -> 0x0039 }
    L_0x0046:
        r0 = db;
        r0.endTransaction();
        return;
    L_0x004d:
        r1 = db;
        r1.endTransaction();
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: de.danoeh.antennapod.core.storage.PodDBAdapter.setFeedItemRead(int, long[]):void");
    }

    public void setFeedItemlist(java.util.List<de.danoeh.antennapod.core.feed.FeedItem> r4) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:17:0x003a in {4, 6, 12, 14, 16} preds:[]
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
        r3 = this;
        r0 = db;	 Catch:{ SQLException -> 0x0022 }
        r0.beginTransactionNonExclusive();	 Catch:{ SQLException -> 0x0022 }
        r0 = r4.iterator();	 Catch:{ SQLException -> 0x0022 }
    L_0x0009:
        r1 = r0.hasNext();	 Catch:{ SQLException -> 0x0022 }
        if (r1 == 0) goto L_0x001a;	 Catch:{ SQLException -> 0x0022 }
    L_0x000f:
        r1 = r0.next();	 Catch:{ SQLException -> 0x0022 }
        r1 = (de.danoeh.antennapod.core.feed.FeedItem) r1;	 Catch:{ SQLException -> 0x0022 }
        r2 = 1;	 Catch:{ SQLException -> 0x0022 }
        r3.setFeedItem(r1, r2);	 Catch:{ SQLException -> 0x0022 }
        goto L_0x0009;	 Catch:{ SQLException -> 0x0022 }
    L_0x001a:
        r0 = db;	 Catch:{ SQLException -> 0x0022 }
        r0.setTransactionSuccessful();	 Catch:{ SQLException -> 0x0022 }
        goto L_0x002d;
    L_0x0020:
        r0 = move-exception;
        goto L_0x0034;
    L_0x0022:
        r0 = move-exception;
        r1 = "PodDBAdapter";	 Catch:{ all -> 0x0020 }
        r2 = android.util.Log.getStackTraceString(r0);	 Catch:{ all -> 0x0020 }
        android.util.Log.e(r1, r2);	 Catch:{ all -> 0x0020 }
    L_0x002d:
        r0 = db;
        r0.endTransaction();
        return;
    L_0x0034:
        r1 = db;
        r1.endTransaction();
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: de.danoeh.antennapod.core.storage.PodDBAdapter.setFeedItemlist(java.util.List):void");
    }

    public void setQueue(java.util.List<de.danoeh.antennapod.core.feed.FeedItem> r8) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:18:0x0072 in {5, 7, 13, 15, 17} preds:[]
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
        r7 = this;
        r0 = new android.content.ContentValues;
        r0.<init>();
        r1 = db;	 Catch:{ SQLException -> 0x005a }
        r1.beginTransactionNonExclusive();	 Catch:{ SQLException -> 0x005a }
        r1 = db;	 Catch:{ SQLException -> 0x005a }
        r2 = "Queue";	 Catch:{ SQLException -> 0x005a }
        r3 = 0;	 Catch:{ SQLException -> 0x005a }
        r1.delete(r2, r3, r3);	 Catch:{ SQLException -> 0x005a }
        r1 = 0;	 Catch:{ SQLException -> 0x005a }
    L_0x0013:
        r2 = r8.size();	 Catch:{ SQLException -> 0x005a }
        if (r1 >= r2) goto L_0x0052;	 Catch:{ SQLException -> 0x005a }
    L_0x0019:
        r2 = r8.get(r1);	 Catch:{ SQLException -> 0x005a }
        r2 = (de.danoeh.antennapod.core.feed.FeedItem) r2;	 Catch:{ SQLException -> 0x005a }
        r4 = "id";	 Catch:{ SQLException -> 0x005a }
        r5 = java.lang.Integer.valueOf(r1);	 Catch:{ SQLException -> 0x005a }
        r0.put(r4, r5);	 Catch:{ SQLException -> 0x005a }
        r4 = "feeditem";	 Catch:{ SQLException -> 0x005a }
        r5 = r2.getId();	 Catch:{ SQLException -> 0x005a }
        r5 = java.lang.Long.valueOf(r5);	 Catch:{ SQLException -> 0x005a }
        r0.put(r4, r5);	 Catch:{ SQLException -> 0x005a }
        r4 = "feed";	 Catch:{ SQLException -> 0x005a }
        r5 = r2.getFeed();	 Catch:{ SQLException -> 0x005a }
        r5 = r5.getId();	 Catch:{ SQLException -> 0x005a }
        r5 = java.lang.Long.valueOf(r5);	 Catch:{ SQLException -> 0x005a }
        r0.put(r4, r5);	 Catch:{ SQLException -> 0x005a }
        r4 = db;	 Catch:{ SQLException -> 0x005a }
        r5 = "Queue";	 Catch:{ SQLException -> 0x005a }
        r6 = 5;	 Catch:{ SQLException -> 0x005a }
        r4.insertWithOnConflict(r5, r3, r0, r6);	 Catch:{ SQLException -> 0x005a }
        r1 = r1 + 1;	 Catch:{ SQLException -> 0x005a }
        goto L_0x0013;	 Catch:{ SQLException -> 0x005a }
    L_0x0052:
        r1 = db;	 Catch:{ SQLException -> 0x005a }
        r1.setTransactionSuccessful();	 Catch:{ SQLException -> 0x005a }
        goto L_0x0065;
    L_0x0058:
        r1 = move-exception;
        goto L_0x006c;
    L_0x005a:
        r1 = move-exception;
        r2 = "PodDBAdapter";	 Catch:{ all -> 0x0058 }
        r3 = android.util.Log.getStackTraceString(r1);	 Catch:{ all -> 0x0058 }
        android.util.Log.e(r2, r3);	 Catch:{ all -> 0x0058 }
    L_0x0065:
        r1 = db;
        r1.endTransaction();
        return;
    L_0x006c:
        r2 = db;
        r2.endTransaction();
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: de.danoeh.antennapod.core.storage.PodDBAdapter.setQueue(java.util.List):void");
    }

    static {
        String selFiSmall = Arrays.toString(FEEDITEM_SEL_FI_SMALL);
        SEL_FI_SMALL_STR = selFiSmall.substring(1, selFiSmall.length() - 1);
    }

    public static void init(Context context) {
        context = context.getApplicationContext();
    }

    public static PodDBAdapter getInstance() {
        return PodDBAdapter$SingletonHolder.access$200();
    }

    private PodDBAdapter() {
    }

    public synchronized PodDBAdapter open() {
        if (db != null && db.isOpen()) {
            if (db.isReadOnly()) {
            }
        }
        db = openDb();
        return this;
    }

    @SuppressLint({"NewApi"})
    private SQLiteDatabase openDb() {
        try {
            SQLiteDatabase newDb = PodDBAdapter$SingletonHolder.access$300().getWritableDatabase();
            if (VERSION.SDK_INT >= 16) {
                newDb.disableWriteAheadLogging();
            }
            return newDb;
        } catch (SQLException ex) {
            Log.e(TAG, Log.getStackTraceString(ex));
            return PodDBAdapter$SingletonHolder.access$300().getReadableDatabase();
        }
    }

    public synchronized void close() {
    }

    private long setFeed(Feed feed) {
        ContentValues values = new ContentValues();
        values.put("title", feed.getFeedTitle());
        values.put(KEY_LINK, feed.getLink());
        values.put(KEY_DESCRIPTION, feed.getDescription());
        values.put(KEY_PAYMENT_LINK, feed.getPaymentLink());
        values.put(KEY_AUTHOR, feed.getAuthor());
        values.put(KEY_LANGUAGE, feed.getLanguage());
        values.put(KEY_IMAGE_URL, feed.getImageUrl());
        values.put(KEY_FILE_URL, feed.getFile_url());
        values.put(KEY_DOWNLOAD_URL, feed.getDownload_url());
        values.put(KEY_DOWNLOADED, Boolean.valueOf(feed.isDownloaded()));
        values.put(KEY_LASTUPDATE, feed.getLastUpdate());
        values.put("type", feed.getType());
        values.put(KEY_FEED_IDENTIFIER, feed.getFeedIdentifier());
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Setting feed with flattr status ");
        stringBuilder.append(feed.getTitle());
        stringBuilder.append(": ");
        stringBuilder.append(feed.getFlattrStatus().toLong());
        Log.d(str, stringBuilder.toString());
        values.put(KEY_FLATTR_STATUS, Long.valueOf(feed.getFlattrStatus().toLong()));
        values.put(KEY_IS_PAGED, Boolean.valueOf(feed.isPaged()));
        values.put(KEY_NEXT_PAGE_LINK, feed.getNextPageLink());
        if (feed.getItemFilter() == null || feed.getItemFilter().getValues().length <= 0) {
            values.put(KEY_HIDE, "");
        } else {
            values.put(KEY_HIDE, TextUtils.join(",", feed.getItemFilter().getValues()));
        }
        values.put(KEY_LAST_UPDATE_FAILED, Boolean.valueOf(feed.hasLastUpdateFailed()));
        if (feed.getId() == 0) {
            Log.d(toString(), "Inserting new Feed into db");
            feed.setId(db.insert(TABLE_NAME_FEEDS, null, values));
        } else {
            Log.d(toString(), "Updating existing Feed in db");
            db.update(TABLE_NAME_FEEDS, values, "id=?", new String[]{String.valueOf(feed.getId())});
        }
        return feed.getId();
    }

    public void setFeedPreferences(FeedPreferences prefs) {
        if (prefs.getFeedID() != 0) {
            ContentValues values = new ContentValues();
            values.put(KEY_AUTO_DOWNLOAD, Boolean.valueOf(prefs.getAutoDownload()));
            values.put(KEY_KEEP_UPDATED, Boolean.valueOf(prefs.getKeepUpdated()));
            values.put(KEY_AUTO_DELETE_ACTION, Integer.valueOf(prefs.getAutoDeleteAction().ordinal()));
            values.put(KEY_USERNAME, prefs.getUsername());
            values.put(KEY_PASSWORD, prefs.getPassword());
            values.put(KEY_INCLUDE_FILTER, prefs.getFilter().getIncludeFilter());
            values.put(KEY_EXCLUDE_FILTER, prefs.getFilter().getExcludeFilter());
            db.update(TABLE_NAME_FEEDS, values, "id=?", new String[]{String.valueOf(prefs.getFeedID())});
            return;
        }
        throw new IllegalArgumentException("Feed ID of preference must not be null");
    }

    public void setFeedItemFilter(long feedId, Set<String> filterValues) {
        String valuesList = TextUtils.join(",", filterValues);
        Log.d(TAG, String.format("setFeedItemFilter() called with: feedId = [%d], filterValues = [%s]", new Object[]{Long.valueOf(feedId), valuesList}));
        ContentValues values = new ContentValues();
        values.put(KEY_HIDE, valuesList);
        db.update(TABLE_NAME_FEEDS, values, "id=?", new String[]{String.valueOf(feedId)});
    }

    public long setMedia(FeedMedia media) {
        ContentValues values = new ContentValues();
        values.put("duration", Integer.valueOf(media.getDuration()));
        values.put(KEY_POSITION, Integer.valueOf(media.getPosition()));
        values.put(KEY_SIZE, Long.valueOf(media.getSize()));
        values.put(KEY_MIME_TYPE, media.getMime_type());
        values.put(KEY_DOWNLOAD_URL, media.getDownload_url());
        values.put(KEY_DOWNLOADED, Boolean.valueOf(media.isDownloaded()));
        values.put(KEY_FILE_URL, media.getFile_url());
        values.put(KEY_HAS_EMBEDDED_PICTURE, Boolean.valueOf(media.hasEmbeddedPicture()));
        values.put(KEY_LAST_PLAYED_TIME, Long.valueOf(media.getLastPlayedTime()));
        if (media.getPlaybackCompletionDate() != null) {
            values.put(KEY_PLAYBACK_COMPLETION_DATE, Long.valueOf(media.getPlaybackCompletionDate().getTime()));
        } else {
            values.put(KEY_PLAYBACK_COMPLETION_DATE, Integer.valueOf(0));
        }
        if (media.getItem() != null) {
            values.put(KEY_FEEDITEM, Long.valueOf(media.getItem().getId()));
        }
        if (media.getId() == 0) {
            media.setId(db.insert(TABLE_NAME_FEED_MEDIA, null, values));
        } else {
            db.update(TABLE_NAME_FEED_MEDIA, values, "id=?", new String[]{String.valueOf(media.getId())});
        }
        return media.getId();
    }

    public void setFeedMediaPlaybackInformation(FeedMedia media) {
        if (media.getId() != 0) {
            ContentValues values = new ContentValues();
            values.put(KEY_POSITION, Integer.valueOf(media.getPosition()));
            values.put("duration", Integer.valueOf(media.getDuration()));
            values.put(KEY_PLAYED_DURATION, Integer.valueOf(media.getPlayedDuration()));
            values.put(KEY_LAST_PLAYED_TIME, Long.valueOf(media.getLastPlayedTime()));
            db.update(TABLE_NAME_FEED_MEDIA, values, "id=?", new String[]{String.valueOf(media.getId())});
            return;
        }
        Log.e(TAG, "setFeedMediaPlaybackInformation: ID of media was 0");
    }

    public void setFeedMediaPlaybackCompletionDate(FeedMedia media) {
        if (media.getId() != 0) {
            ContentValues values = new ContentValues();
            values.put(KEY_PLAYBACK_COMPLETION_DATE, Long.valueOf(media.getPlaybackCompletionDate().getTime()));
            values.put(KEY_PLAYED_DURATION, Integer.valueOf(media.getPlayedDuration()));
            db.update(TABLE_NAME_FEED_MEDIA, values, "id=?", new String[]{String.valueOf(media.getId())});
            return;
        }
        Log.e(TAG, "setFeedMediaPlaybackCompletionDate: ID of media was 0");
    }

    public void setFeedFlattrStatus(Feed feed) {
        ContentValues values = new ContentValues();
        values.put(KEY_FLATTR_STATUS, Long.valueOf(feed.getFlattrStatus().toLong()));
        db.update(TABLE_NAME_FEEDS, values, "id=?", new String[]{String.valueOf(feed.getId())});
    }

    public Cursor getFeedsInFlattrQueueCursor() {
        return db.query(TABLE_NAME_FEEDS, FEED_SEL_STD, "flattr_status=?", new String[]{String.valueOf(1)}, null, null, null);
    }

    public Cursor getFeedItemsInFlattrQueueCursor() {
        return db.query(TABLE_NAME_FEED_ITEMS, FEEDITEM_SEL_FI_SMALL, "flattr_status=?", new String[]{String.valueOf(1)}, null, null, null);
    }

    public void setFeedDownloadUrl(String original, String updated) {
        ContentValues values = new ContentValues();
        values.put(KEY_DOWNLOAD_URL, updated);
        db.update(TABLE_NAME_FEEDS, values, "download_url=?", new String[]{original});
    }

    public long setSingleFeedItem(FeedItem item) {
        long result = 0;
        try {
            db.beginTransactionNonExclusive();
            result = setFeedItem(item, true);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } catch (Throwable th) {
            db.endTransaction();
        }
        db.endTransaction();
        return result;
    }

    public void setFeedItemFlattrStatus(FeedItem feedItem) {
        ContentValues values = new ContentValues();
        values.put(KEY_FLATTR_STATUS, Long.valueOf(feedItem.getFlattrStatus().toLong()));
        db.update(TABLE_NAME_FEED_ITEMS, values, "id=?", new String[]{String.valueOf(feedItem.getId())});
    }

    public void setItemFlattrStatus(String url, FlattrStatus status) {
        ContentValues values = new ContentValues();
        values.put(KEY_FLATTR_STATUS, Long.valueOf(status.toLong()));
        String[] query_urls = new String[4];
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("*");
        stringBuilder.append(url);
        stringBuilder.append("&*");
        query_urls[0] = stringBuilder.toString();
        stringBuilder = new StringBuilder();
        stringBuilder.append("*");
        stringBuilder.append(url);
        stringBuilder.append("%2F&*");
        query_urls[1] = stringBuilder.toString();
        stringBuilder = new StringBuilder();
        stringBuilder.append("*");
        stringBuilder.append(url);
        stringBuilder.append("");
        query_urls[2] = stringBuilder.toString();
        stringBuilder = new StringBuilder();
        stringBuilder.append("*");
        stringBuilder.append(url);
        stringBuilder.append("%2F");
        query_urls[3] = stringBuilder.toString();
        if (db.update(TABLE_NAME_FEEDS, values, "payment_link GLOB ? OR payment_link GLOB ? OR payment_link GLOB ? OR payment_link GLOB ?", query_urls) > 0) {
            String str = TAG;
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("setItemFlattrStatus found match for ");
            stringBuilder2.append(url);
            stringBuilder2.append(" = ");
            stringBuilder2.append(status.toLong());
            stringBuilder2.append(" in Feeds table");
            Log.i(str, stringBuilder2.toString());
            return;
        }
        if (db.update(TABLE_NAME_FEED_ITEMS, values, "payment_link GLOB ? OR payment_link GLOB ? OR payment_link GLOB ? OR payment_link GLOB ?", query_urls) > 0) {
            str = TAG;
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append("setItemFlattrStatus found match for ");
            stringBuilder2.append(url);
            stringBuilder2.append(" = ");
            stringBuilder2.append(status.toLong());
            stringBuilder2.append(" in FeedsItems table");
            Log.i(str, stringBuilder2.toString());
        }
    }

    public void clearAllFlattrStatus() {
        ContentValues values = new ContentValues();
        values.put(KEY_FLATTR_STATUS, Integer.valueOf(0));
        db.update(TABLE_NAME_FEEDS, values, null, null);
        db.update(TABLE_NAME_FEED_ITEMS, values, null, null);
    }

    private long setFeedItem(FeedItem item, boolean saveFeed) {
        boolean z;
        ContentValues values = new ContentValues();
        values.put("title", item.getTitle());
        values.put(KEY_LINK, item.getLink());
        if (item.getDescription() != null) {
            values.put(KEY_DESCRIPTION, item.getDescription());
        }
        if (item.getContentEncoded() != null) {
            values.put(KEY_CONTENT_ENCODED, item.getContentEncoded());
        }
        values.put(KEY_PUBDATE, Long.valueOf(item.getPubDate().getTime()));
        values.put(KEY_PAYMENT_LINK, item.getPaymentLink());
        if (saveFeed && item.getFeed() != null) {
            setFeed(item.getFeed());
        }
        values.put(KEY_FEED, Long.valueOf(item.getFeed().getId()));
        if (item.isNew()) {
            values.put(KEY_READ, Integer.valueOf(-1));
        } else if (item.isPlayed()) {
            values.put(KEY_READ, Integer.valueOf(1));
        } else {
            values.put(KEY_READ, Integer.valueOf(0));
        }
        String str = KEY_HAS_CHAPTERS;
        if (item.getChapters() == null) {
            if (!item.hasChapters()) {
                z = false;
                values.put(str, Boolean.valueOf(z));
                values.put(KEY_ITEM_IDENTIFIER, item.getItemIdentifier());
                values.put(KEY_FLATTR_STATUS, Long.valueOf(item.getFlattrStatus().toLong()));
                values.put(KEY_AUTO_DOWNLOAD, Boolean.valueOf(item.getAutoDownload()));
                values.put(KEY_IMAGE_URL, item.getImageUrl());
                if (item.getId() != 0) {
                    item.setId(db.insert(TABLE_NAME_FEED_ITEMS, null, values));
                } else {
                    db.update(TABLE_NAME_FEED_ITEMS, values, "id=?", new String[]{String.valueOf(item.getId())});
                }
                if (item.getMedia() != null) {
                    setMedia(item.getMedia());
                }
                if (item.getChapters() != null) {
                    setChapters(item);
                }
                return item.getId();
            }
        }
        z = true;
        values.put(str, Boolean.valueOf(z));
        values.put(KEY_ITEM_IDENTIFIER, item.getItemIdentifier());
        values.put(KEY_FLATTR_STATUS, Long.valueOf(item.getFlattrStatus().toLong()));
        values.put(KEY_AUTO_DOWNLOAD, Boolean.valueOf(item.getAutoDownload()));
        values.put(KEY_IMAGE_URL, item.getImageUrl());
        if (item.getId() != 0) {
            db.update(TABLE_NAME_FEED_ITEMS, values, "id=?", new String[]{String.valueOf(item.getId())});
        } else {
            item.setId(db.insert(TABLE_NAME_FEED_ITEMS, null, values));
        }
        if (item.getMedia() != null) {
            setMedia(item.getMedia());
        }
        if (item.getChapters() != null) {
            setChapters(item);
        }
        return item.getId();
    }

    public void setFeedItemRead(int played, long itemId, long mediaId, boolean resetMediaPosition) {
        try {
            db.beginTransactionNonExclusive();
            ContentValues values = new ContentValues();
            values.put(KEY_READ, Integer.valueOf(played));
            db.update(TABLE_NAME_FEED_ITEMS, values, "id=?", new String[]{String.valueOf(itemId)});
            if (resetMediaPosition) {
                values.clear();
                values.put(KEY_POSITION, Integer.valueOf(0));
                db.update(TABLE_NAME_FEED_MEDIA, values, "id=?", new String[]{String.valueOf(mediaId)});
            }
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } catch (Throwable th) {
            db.endTransaction();
        }
        db.endTransaction();
    }

    private void setChapters(FeedItem item) {
        ContentValues values = new ContentValues();
        for (Chapter chapter : item.getChapters()) {
            values.put("title", chapter.getTitle());
            values.put("start", Long.valueOf(chapter.getStart()));
            values.put(KEY_FEEDITEM, Long.valueOf(item.getId()));
            values.put(KEY_LINK, chapter.getLink());
            values.put("type", Integer.valueOf(chapter.getChapterType()));
            if (chapter.getId() == 0) {
                chapter.setId(db.insert(TABLE_NAME_SIMPLECHAPTERS, null, values));
            } else {
                db.update(TABLE_NAME_SIMPLECHAPTERS, values, "id=?", new String[]{String.valueOf(chapter.getId())});
            }
        }
    }

    public void setFeedLastUpdateFailed(long feedId, boolean failed) {
        String sql = new StringBuilder();
        sql.append("UPDATE Feeds SET last_update_failed=");
        sql.append(failed ? "1" : "0");
        sql.append(" WHERE ");
        sql.append("id");
        sql.append("=");
        sql.append(feedId);
        db.execSQL(sql.toString());
    }

    void setFeedCustomTitle(long feedId, String customTitle) {
        ContentValues values = new ContentValues();
        values.put(KEY_CUSTOM_TITLE, customTitle);
        db.update(TABLE_NAME_FEEDS, values, "id=?", new String[]{String.valueOf(feedId)});
    }

    public long setDownloadStatus(DownloadStatus status) {
        ContentValues values = new ContentValues();
        values.put(KEY_FEEDFILE, Long.valueOf(status.getFeedfileId()));
        values.put(KEY_FEEDFILETYPE, Integer.valueOf(status.getFeedfileType()));
        values.put(KEY_REASON, Integer.valueOf(status.getReason().getCode()));
        values.put(KEY_SUCCESSFUL, Boolean.valueOf(status.isSuccessful()));
        values.put(KEY_COMPLETION_DATE, Long.valueOf(status.getCompletionDate().getTime()));
        values.put(KEY_REASON_DETAILED, status.getReasonDetailed());
        values.put("title", status.getTitle());
        if (status.getId() == 0) {
            status.setId(db.insert(TABLE_NAME_DOWNLOAD_LOG, null, values));
        } else {
            db.update(TABLE_NAME_DOWNLOAD_LOG, values, "id=?", new String[]{String.valueOf(status.getId())});
        }
        return status.getId();
    }

    public void setFeedItemAutoDownload(FeedItem feedItem, long autoDownload) {
        ContentValues values = new ContentValues();
        values.put(KEY_AUTO_DOWNLOAD, Long.valueOf(autoDownload));
        db.update(TABLE_NAME_FEED_ITEMS, values, "id=?", new String[]{String.valueOf(feedItem.getId())});
    }

    public void setFeedsItemsAutoDownload(Feed feed, boolean autoDownload) {
        String sql = new StringBuilder();
        sql.append("UPDATE FeedItems SET auto_download=");
        sql.append(autoDownload ? "1" : "0");
        sql.append(" WHERE ");
        sql.append(KEY_FEED);
        sql.append("=");
        sql.append(feed.getId());
        db.execSQL(sql.toString());
    }

    public void addFavoriteItem(FeedItem item) {
        if (isItemInFavorites(item)) {
            Log.d(TAG, "item already in favorites");
            return;
        }
        ContentValues values = new ContentValues();
        values.put(KEY_FEEDITEM, Long.valueOf(item.getId()));
        values.put(KEY_FEED, Long.valueOf(item.getFeedId()));
        db.insert(TABLE_NAME_FAVORITES, null, values);
    }

    public void removeFavoriteItem(FeedItem item) {
        db.execSQL(String.format("DELETE FROM %s WHERE %s=%s AND %s=%s", new Object[]{TABLE_NAME_FAVORITES, KEY_FEEDITEM, Long.valueOf(item.getId()), KEY_FEED, Long.valueOf(item.getFeedId())}));
    }

    private boolean isItemInFavorites(FeedItem item) {
        Cursor c = db.rawQuery(String.format("SELECT %s from %s WHERE %s=%d", new Object[]{"id", TABLE_NAME_FAVORITES, KEY_FEEDITEM, Long.valueOf(item.getId())}), null);
        int count = c.getCount();
        c.close();
        if (count > 0) {
            return true;
        }
        return false;
    }

    public void clearQueue() {
        db.delete("Queue", null, null);
    }

    private void removeFeedMedia(FeedMedia media) {
        db.delete(TABLE_NAME_DOWNLOAD_LOG, "feedfile=? AND feedfile_type=?", new String[]{String.valueOf(media.getId()), String.valueOf(2)});
        db.delete(TABLE_NAME_FEED_MEDIA, "id=?", new String[]{String.valueOf(media.getId())});
    }

    private void removeChaptersOfItem(FeedItem item) {
        db.delete(TABLE_NAME_SIMPLECHAPTERS, "feeditem=?", new String[]{String.valueOf(item.getId())});
    }

    private void removeFeedItem(FeedItem item) {
        if (item.getMedia() != null) {
            removeFeedMedia(item.getMedia());
        }
        if (!item.hasChapters()) {
            if (item.getChapters() == null) {
                db.delete(TABLE_NAME_FEED_ITEMS, "id=?", new String[]{String.valueOf(item.getId())});
            }
        }
        removeChaptersOfItem(item);
        db.delete(TABLE_NAME_FEED_ITEMS, "id=?", new String[]{String.valueOf(item.getId())});
    }

    public void clearPlaybackHistory() {
        ContentValues values = new ContentValues();
        values.put(KEY_PLAYBACK_COMPLETION_DATE, Integer.valueOf(0));
        db.update(TABLE_NAME_FEED_MEDIA, values, null, null);
    }

    public void clearDownloadLog() {
        db.delete(TABLE_NAME_DOWNLOAD_LOG, null, null);
    }

    public final Cursor getAllFeedsCursor() {
        return db.query(TABLE_NAME_FEEDS, FEED_SEL_STD, null, null, null, null, "title COLLATE NOCASE ASC");
    }

    public final Cursor getFeedCursorDownloadUrls() {
        return db.query(TABLE_NAME_FEEDS, new String[]{"id", KEY_DOWNLOAD_URL}, null, null, null, null, null);
    }

    public final Cursor getAllItemsOfFeedCursor(Feed feed) {
        return getAllItemsOfFeedCursor(feed.getId());
    }

    private Cursor getAllItemsOfFeedCursor(long feedId) {
        return db.query(TABLE_NAME_FEED_ITEMS, FEEDITEM_SEL_FI_SMALL, "feed=?", new String[]{String.valueOf(feedId)}, null, null, null);
    }

    public final Cursor getExtraInformationOfItem(FeedItem item) {
        return db.query(TABLE_NAME_FEED_ITEMS, SEL_FI_EXTRA, "id=?", new String[]{String.valueOf(item.getId())}, null, null, null);
    }

    public final Cursor getImageCursor(String... imageIds) {
        int length = imageIds.length;
        if (length > IN_OPERATOR_MAXIMUM) {
            Log.w(TAG, "Length of id array is larger than 800. Creating multiple cursors");
            double d = (double) length;
            Double.isNaN(d);
            int numCursors = ((int) (d / 800.0d)) + 1;
            Cursor[] cursors = new Cursor[numCursors];
            for (int i = 0; i < numCursors; i++) {
                int neededLength;
                String[] parts;
                int elementsLeft = length - (i * IN_OPERATOR_MAXIMUM);
                if (elementsLeft >= IN_OPERATOR_MAXIMUM) {
                    neededLength = IN_OPERATOR_MAXIMUM;
                    parts = (String[]) Arrays.copyOfRange(imageIds, i * IN_OPERATOR_MAXIMUM, (i + 1) * IN_OPERATOR_MAXIMUM);
                } else {
                    neededLength = elementsLeft;
                    parts = (String[]) Arrays.copyOfRange(imageIds, i * IN_OPERATOR_MAXIMUM, (i * IN_OPERATOR_MAXIMUM) + neededLength);
                }
                SQLiteDatabase sQLiteDatabase = db;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("SELECT * FROM FeedImages WHERE id IN ");
                stringBuilder.append(buildInOperator(neededLength));
                cursors[i] = sQLiteDatabase.rawQuery(stringBuilder.toString(), parts);
            }
            Cursor result = new MergeCursor(cursors);
            result.moveToFirst();
            return result;
        }
        SQLiteDatabase sQLiteDatabase2 = db;
        String str = TABLE_NAME_FEED_IMAGES;
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("id IN ");
        stringBuilder2.append(buildInOperator(length));
        return sQLiteDatabase2.query(str, null, stringBuilder2.toString(), imageIds, null, null, null);
    }

    public final Cursor getSimpleChaptersOfFeedItemCursor(FeedItem item) {
        return db.query(TABLE_NAME_SIMPLECHAPTERS, null, "feeditem=?", new String[]{String.valueOf(item.getId())}, null, null, null);
    }

    public final Cursor getDownloadLog(int feedFileType, long feedFileId) {
        String query = new StringBuilder();
        query.append("SELECT * FROM DownloadLog WHERE feedfile=");
        query.append(feedFileId);
        query.append(" AND ");
        query.append(KEY_FEEDFILETYPE);
        query.append("=");
        query.append(feedFileType);
        query.append(" ORDER BY ");
        query.append("id");
        query.append(" DESC");
        return db.rawQuery(query.toString(), null);
    }

    public final Cursor getDownloadLogCursor(int limit) {
        SQLiteDatabase sQLiteDatabase = db;
        String str = TABLE_NAME_DOWNLOAD_LOG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("completion_date DESC LIMIT ");
        stringBuilder.append(limit);
        return sQLiteDatabase.query(str, null, null, null, null, null, stringBuilder.toString());
    }

    public final Cursor getQueueCursor() {
        return db.rawQuery(String.format("SELECT %s FROM %s INNER JOIN %s ON %s=%s ORDER BY %s", new String[]{SEL_FI_SMALL_STR, TABLE_NAME_FEED_ITEMS, "Queue", "FeedItems.id", "Queue.feeditem", "Queue.id"}), null);
    }

    public Cursor getQueueIDCursor() {
        return db.query("Queue", new String[]{KEY_FEEDITEM}, null, null, null, null, "id ASC", null);
    }

    public final Cursor getFavoritesCursor() {
        return db.rawQuery(String.format("SELECT %s FROM %s INNER JOIN %s ON %s=%s ORDER BY %s DESC", new String[]{SEL_FI_SMALL_STR, TABLE_NAME_FEED_ITEMS, TABLE_NAME_FAVORITES, "FeedItems.id", "Favorites.feeditem", "FeedItems.pubDate"}), null);
    }

    public void setFeedItems(int state) {
        setFeedItems(Integer.MIN_VALUE, state, 0);
    }

    public void setFeedItems(int oldState, int newState) {
        setFeedItems(oldState, newState, 0);
    }

    public void setFeedItems(int state, long feedId) {
        setFeedItems(Integer.MIN_VALUE, state, feedId);
    }

    public void setFeedItems(int oldState, int newState, long feedId) {
        String sql = new StringBuilder();
        sql.append("UPDATE FeedItems SET read=");
        sql.append(newState);
        sql = sql.toString();
        if (feedId > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(sql);
            stringBuilder.append(" WHERE feed=");
            stringBuilder.append(feedId);
            sql = stringBuilder.toString();
        }
        if (-1 <= oldState && oldState <= 1) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(sql);
            stringBuilder.append(feedId > 0 ? " AND " : " WHERE ");
            sql = stringBuilder.toString();
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(sql);
            stringBuilder2.append("read=");
            stringBuilder2.append(oldState);
            sql = stringBuilder2.toString();
        }
        db.execSQL(sql);
    }

    public final Cursor getNewItemsCursor() {
        return db.rawQuery(String.format("SELECT %s FROM %s INNER JOIN %s ON %s WHERE %s ORDER BY %s", new String[]{SEL_FI_SMALL_STR, TABLE_NAME_FEED_ITEMS, TABLE_NAME_FEEDS, "FeedItems.feed=Feeds.id", "FeedItems.read=-1 AND Feeds.keep_updated > 0", "pubDate DESC"}), null);
    }

    public final Cursor getRecentlyPublishedItemsCursor(int limit) {
        SQLiteDatabase sQLiteDatabase = db;
        String str = TABLE_NAME_FEED_ITEMS;
        String[] strArr = FEEDITEM_SEL_FI_SMALL;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("pubDate DESC LIMIT ");
        stringBuilder.append(limit);
        return sQLiteDatabase.query(str, strArr, null, null, null, null, stringBuilder.toString());
    }

    public Cursor getDownloadedItemsCursor() {
        String query = new StringBuilder();
        query.append("SELECT ");
        query.append(SEL_FI_SMALL_STR);
        query.append(" FROM ");
        query.append(TABLE_NAME_FEED_ITEMS);
        query.append(" INNER JOIN ");
        query.append(TABLE_NAME_FEED_MEDIA);
        query.append(" ON ");
        query.append(TABLE_NAME_FEED_ITEMS);
        query.append(".");
        query.append("id");
        query.append("=");
        query.append(TABLE_NAME_FEED_MEDIA);
        query.append(".");
        query.append(KEY_FEEDITEM);
        query.append(" WHERE ");
        query.append(TABLE_NAME_FEED_MEDIA);
        query.append(".");
        query.append(KEY_DOWNLOADED);
        query.append(">0");
        return db.rawQuery(query.toString(), null);
    }

    public final Cursor getCompletedMediaCursor(int limit) {
        if (limit >= 0) {
            return db.query(TABLE_NAME_FEED_MEDIA, null, "playback_completion_date > 0", null, null, null, String.format("%s DESC LIMIT %d", new Object[]{KEY_PLAYBACK_COMPLETION_DATE, Integer.valueOf(limit)}));
        }
        throw new IllegalArgumentException("Limit must be >= 0");
    }

    public final Cursor getSingleFeedMediaCursor(long id) {
        return db.query(TABLE_NAME_FEED_MEDIA, null, "id=?", new String[]{String.valueOf(id)}, null, null, null);
    }

    public final Cursor getFeedMediaCursor(String... itemIds) {
        int length = itemIds.length;
        if (length > IN_OPERATOR_MAXIMUM) {
            Log.w(TAG, "Length of id array is larger than 800. Creating multiple cursors");
            double d = (double) length;
            Double.isNaN(d);
            int numCursors = ((int) (d / 800.0d)) + 1;
            Cursor[] cursors = new Cursor[numCursors];
            for (int i = 0; i < numCursors; i++) {
                int neededLength;
                String[] parts;
                int elementsLeft = length - (i * IN_OPERATOR_MAXIMUM);
                if (elementsLeft >= IN_OPERATOR_MAXIMUM) {
                    neededLength = IN_OPERATOR_MAXIMUM;
                    parts = (String[]) Arrays.copyOfRange(itemIds, i * IN_OPERATOR_MAXIMUM, (i + 1) * IN_OPERATOR_MAXIMUM);
                } else {
                    neededLength = elementsLeft;
                    parts = (String[]) Arrays.copyOfRange(itemIds, i * IN_OPERATOR_MAXIMUM, (i * IN_OPERATOR_MAXIMUM) + neededLength);
                }
                SQLiteDatabase sQLiteDatabase = db;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("SELECT * FROM FeedMedia WHERE feeditem IN ");
                stringBuilder.append(buildInOperator(neededLength));
                cursors[i] = sQLiteDatabase.rawQuery(stringBuilder.toString(), parts);
            }
            Cursor result = new MergeCursor(cursors);
            result.moveToFirst();
            return result;
        }
        SQLiteDatabase sQLiteDatabase2 = db;
        String str = TABLE_NAME_FEED_MEDIA;
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("feeditem IN ");
        stringBuilder2.append(buildInOperator(length));
        return sQLiteDatabase2.query(str, null, stringBuilder2.toString(), itemIds, null, null, null);
    }

    private String buildInOperator(int size) {
        if (size == 1) {
            return "(?)";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(");
        stringBuilder.append(TextUtils.join(",", Collections.nCopies(size, "?")));
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    public final Cursor getFeedCursor(long id) {
        SQLiteDatabase sQLiteDatabase = db;
        String str = TABLE_NAME_FEEDS;
        String[] strArr = FEED_SEL_STD;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("id=");
        stringBuilder.append(id);
        return sQLiteDatabase.query(str, strArr, stringBuilder.toString(), null, null, null, null);
    }

    public final Cursor getFeedItemCursor(String id) {
        return getFeedItemCursor(new String[]{id});
    }

    public final Cursor getFeedItemCursor(String[] ids) {
        if (ids.length <= IN_OPERATOR_MAXIMUM) {
            SQLiteDatabase sQLiteDatabase = db;
            String str = TABLE_NAME_FEED_ITEMS;
            String[] strArr = FEEDITEM_SEL_FI_SMALL;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("id IN ");
            stringBuilder.append(buildInOperator(ids.length));
            return sQLiteDatabase.query(str, strArr, stringBuilder.toString(), ids, null, null, null);
        }
        throw new IllegalArgumentException("number of IDs must not be larger than 800");
    }

    public final Cursor getFeedItemCursor(String podcastUrl, String episodeUrl) {
        String escapedPodcastUrl = DatabaseUtils.sqlEscapeString(podcastUrl);
        String escapedEpisodeUrl = DatabaseUtils.sqlEscapeString(episodeUrl);
        String query = new StringBuilder();
        query.append("SELECT ");
        query.append(SEL_FI_SMALL_STR);
        query.append(" FROM ");
        query.append(TABLE_NAME_FEED_ITEMS);
        query.append(" INNER JOIN ");
        query.append(TABLE_NAME_FEEDS);
        query.append(" ON ");
        query.append(TABLE_NAME_FEED_ITEMS);
        query.append(".");
        query.append(KEY_FEED);
        query.append("=");
        query.append(TABLE_NAME_FEEDS);
        query.append(".");
        query.append("id");
        query.append(" INNER JOIN ");
        query.append(TABLE_NAME_FEED_MEDIA);
        query.append(" ON ");
        query.append(TABLE_NAME_FEED_MEDIA);
        query.append(".");
        query.append(KEY_FEEDITEM);
        query.append("=");
        query.append(TABLE_NAME_FEED_ITEMS);
        query.append(".");
        query.append("id");
        query.append(" WHERE ");
        query.append(TABLE_NAME_FEED_MEDIA);
        query.append(".");
        query.append(KEY_DOWNLOAD_URL);
        query.append("=");
        query.append(escapedEpisodeUrl);
        query.append(" AND ");
        query.append(TABLE_NAME_FEEDS);
        query.append(".");
        query.append(KEY_DOWNLOAD_URL);
        query.append("=");
        query.append(escapedPodcastUrl);
        query = query.toString();
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SQL: ");
        stringBuilder.append(query);
        Log.d(str, stringBuilder.toString());
        return db.rawQuery(query, null);
    }

    public Cursor getImageAuthenticationCursor(String imageUrl) {
        String downloadUrl = DatabaseUtils.sqlEscapeString(imageUrl);
        String query = new StringBuilder();
        query.append("SELECT username,password FROM FeedItems INNER JOIN Feeds ON FeedItems.feed = Feeds.id WHERE FeedItems.image_url=");
        query.append(downloadUrl);
        query.append(" UNION SELECT ");
        query.append(KEY_USERNAME);
        query.append(",");
        query.append(KEY_PASSWORD);
        query.append(" FROM ");
        query.append(TABLE_NAME_FEEDS);
        query.append(" WHERE ");
        query.append(TABLE_NAME_FEEDS);
        query.append(".");
        query.append(KEY_IMAGE_URL);
        query.append("=");
        query.append(downloadUrl);
        return db.rawQuery(query.toString(), null);
    }

    public int getQueueSize() {
        Cursor c = db.rawQuery(String.format("SELECT COUNT(%s) FROM %s", new Object[]{"id", "Queue"}), null);
        int result = 0;
        if (c.moveToFirst()) {
            result = c.getInt(0);
        }
        c.close();
        return result;
    }

    public final int getNumberOfNewItems() {
        String query = "SELECT COUNT(id) FROM FeedItems WHERE read=-1";
        Cursor c = db.rawQuery("SELECT COUNT(id) FROM FeedItems WHERE read=-1", null);
        int result = 0;
        if (c.moveToFirst()) {
            result = c.getInt(0);
        }
        c.close();
        return result;
    }

    public final LongIntMap getFeedCounters(long... feedIds) {
        String whereRead;
        int setting = UserPreferences.getFeedCounterSetting();
        if (setting != 4) {
            switch (setting) {
                case 0:
                    whereRead = "(read=-1 OR read=0)";
                    break;
                case 1:
                    whereRead = "read=-1";
                    break;
                case 2:
                    whereRead = "read=0";
                    break;
                default:
                    return new LongIntMap(0);
            }
        }
        whereRead = "downloaded=1";
        return conditionalFeedCounterRead(whereRead, feedIds);
    }

    private LongIntMap conditionalFeedCounterRead(String whereRead, long... feedIds) {
        StringBuilder builder = new StringBuilder();
        for (long id : feedIds) {
            builder.append(id);
            builder.append(',');
        }
        if (feedIds.length > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        String query = new StringBuilder();
        query.append("SELECT feed, COUNT(FeedItems.id) AS count  FROM FeedItems LEFT JOIN FeedMedia ON FeedItems.id=FeedMedia.feeditem WHERE feed IN (");
        query.append(builder.toString());
        query.append(")  AND ");
        query.append(whereRead);
        query.append(" GROUP BY ");
        query.append(KEY_FEED);
        Cursor c = db.rawQuery(query.toString(), null);
        LongIntMap result = new LongIntMap(c.getCount());
        if (c.moveToFirst()) {
            while (true) {
                result.put(c.getLong(0), c.getInt(1));
                if (!c.moveToNext()) {
                    break;
                }
            }
        }
        c.close();
        return result;
    }

    public final LongIntMap getPlayedEpisodesCounters(long... feedIds) {
        return conditionalFeedCounterRead("read=1", feedIds);
    }

    public final int getNumberOfDownloadedEpisodes() {
        String query = "SELECT COUNT(DISTINCT id) AS count FROM FeedMedia WHERE downloaded > 0";
        Cursor c = db.rawQuery("SELECT COUNT(DISTINCT id) AS count FROM FeedMedia WHERE downloaded > 0", null);
        int result = 0;
        if (c.moveToFirst()) {
            result = c.getInt(0);
        }
        c.close();
        return result;
    }

    private String prepareSearchQuery(String query) {
        StringBuilder builder = new StringBuilder();
        DatabaseUtils.appendEscapedSQLString(builder, query);
        builder.deleteCharAt(0);
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    public Cursor searchItemDescriptions(long feedID, String query) {
        if (feedID != 0) {
            SQLiteDatabase sQLiteDatabase = db;
            String str = TABLE_NAME_FEED_ITEMS;
            String[] strArr = FEEDITEM_SEL_FI_SMALL;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("feed=? AND description LIKE '%");
            stringBuilder.append(prepareSearchQuery(query));
            stringBuilder.append("%'");
            return sQLiteDatabase.query(str, strArr, stringBuilder.toString(), new String[]{String.valueOf(feedID)}, null, null, null);
        }
        SQLiteDatabase sQLiteDatabase2 = db;
        String str2 = TABLE_NAME_FEED_ITEMS;
        String[] strArr2 = FEEDITEM_SEL_FI_SMALL;
        stringBuilder = new StringBuilder();
        stringBuilder.append("description LIKE '%");
        stringBuilder.append(prepareSearchQuery(query));
        stringBuilder.append("%'");
        return sQLiteDatabase2.query(str2, strArr2, stringBuilder.toString(), null, null, null, null);
    }

    public Cursor searchItemContentEncoded(long feedID, String query) {
        if (feedID != 0) {
            SQLiteDatabase sQLiteDatabase = db;
            String str = TABLE_NAME_FEED_ITEMS;
            String[] strArr = FEEDITEM_SEL_FI_SMALL;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("feed=? AND content_encoded LIKE '%");
            stringBuilder.append(prepareSearchQuery(query));
            stringBuilder.append("%'");
            return sQLiteDatabase.query(str, strArr, stringBuilder.toString(), new String[]{String.valueOf(feedID)}, null, null, null);
        }
        SQLiteDatabase sQLiteDatabase2 = db;
        String str2 = TABLE_NAME_FEED_ITEMS;
        String[] strArr2 = FEEDITEM_SEL_FI_SMALL;
        stringBuilder = new StringBuilder();
        stringBuilder.append("content_encoded LIKE '%");
        stringBuilder.append(prepareSearchQuery(query));
        stringBuilder.append("%'");
        return sQLiteDatabase2.query(str2, strArr2, stringBuilder.toString(), null, null, null, null);
    }

    public Cursor searchItemTitles(long feedID, String query) {
        if (feedID != 0) {
            SQLiteDatabase sQLiteDatabase = db;
            String str = TABLE_NAME_FEED_ITEMS;
            String[] strArr = FEEDITEM_SEL_FI_SMALL;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("feed=? AND title LIKE '%");
            stringBuilder.append(prepareSearchQuery(query));
            stringBuilder.append("%'");
            return sQLiteDatabase.query(str, strArr, stringBuilder.toString(), new String[]{String.valueOf(feedID)}, null, null, null);
        }
        SQLiteDatabase sQLiteDatabase2 = db;
        String str2 = TABLE_NAME_FEED_ITEMS;
        String[] strArr2 = FEEDITEM_SEL_FI_SMALL;
        stringBuilder = new StringBuilder();
        stringBuilder.append("title LIKE '%");
        stringBuilder.append(prepareSearchQuery(query));
        stringBuilder.append("%'");
        return sQLiteDatabase2.query(str2, strArr2, stringBuilder.toString(), null, null, null, null);
    }

    public Cursor searchItemAuthors(long feedID, String query) {
        if (feedID != 0) {
            SQLiteDatabase sQLiteDatabase = db;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("SELECT ");
            stringBuilder.append(TextUtils.join(", ", FEEDITEM_SEL_FI_SMALL));
            stringBuilder.append(" FROM ");
            stringBuilder.append(TABLE_NAME_FEED_ITEMS);
            stringBuilder.append(" JOIN ");
            stringBuilder.append(TABLE_NAME_FEEDS);
            stringBuilder.append(" ON ");
            stringBuilder.append(TABLE_NAME_FEED_ITEMS);
            stringBuilder.append(".");
            stringBuilder.append(KEY_FEED);
            stringBuilder.append("=");
            stringBuilder.append(TABLE_NAME_FEEDS);
            stringBuilder.append(".");
            stringBuilder.append("id");
            stringBuilder.append(" WHERE ");
            stringBuilder.append(KEY_FEED);
            stringBuilder.append("=? AND ");
            stringBuilder.append(KEY_AUTHOR);
            stringBuilder.append(" LIKE '%");
            stringBuilder.append(prepareSearchQuery(query));
            stringBuilder.append("%' ORDER BY ");
            stringBuilder.append(TABLE_NAME_FEED_ITEMS);
            stringBuilder.append(".");
            stringBuilder.append(KEY_PUBDATE);
            stringBuilder.append(" DESC");
            return sQLiteDatabase.rawQuery(stringBuilder.toString(), new String[]{String.valueOf(feedID)});
        }
        sQLiteDatabase = db;
        stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT ");
        stringBuilder.append(TextUtils.join(", ", FEEDITEM_SEL_FI_SMALL));
        stringBuilder.append(" FROM ");
        stringBuilder.append(TABLE_NAME_FEED_ITEMS);
        stringBuilder.append(" JOIN ");
        stringBuilder.append(TABLE_NAME_FEEDS);
        stringBuilder.append(" ON ");
        stringBuilder.append(TABLE_NAME_FEED_ITEMS);
        stringBuilder.append(".");
        stringBuilder.append(KEY_FEED);
        stringBuilder.append("=");
        stringBuilder.append(TABLE_NAME_FEEDS);
        stringBuilder.append(".");
        stringBuilder.append("id");
        stringBuilder.append(" WHERE ");
        stringBuilder.append(KEY_AUTHOR);
        stringBuilder.append(" LIKE '%");
        stringBuilder.append(prepareSearchQuery(query));
        stringBuilder.append("%' ORDER BY ");
        stringBuilder.append(TABLE_NAME_FEED_ITEMS);
        stringBuilder.append(".");
        stringBuilder.append(KEY_PUBDATE);
        stringBuilder.append(" DESC");
        return sQLiteDatabase.rawQuery(stringBuilder.toString(), null);
    }

    public Cursor searchItemFeedIdentifiers(long feedID, String query) {
        if (feedID != 0) {
            SQLiteDatabase sQLiteDatabase = db;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("SELECT ");
            stringBuilder.append(TextUtils.join(", ", FEEDITEM_SEL_FI_SMALL));
            stringBuilder.append(" FROM ");
            stringBuilder.append(TABLE_NAME_FEED_ITEMS);
            stringBuilder.append(" JOIN ");
            stringBuilder.append(TABLE_NAME_FEEDS);
            stringBuilder.append(" ON ");
            stringBuilder.append(TABLE_NAME_FEED_ITEMS);
            stringBuilder.append(".");
            stringBuilder.append(KEY_FEED);
            stringBuilder.append("=");
            stringBuilder.append(TABLE_NAME_FEEDS);
            stringBuilder.append(".");
            stringBuilder.append("id");
            stringBuilder.append(" WHERE ");
            stringBuilder.append(KEY_FEED);
            stringBuilder.append("=? AND ");
            stringBuilder.append(KEY_FEED_IDENTIFIER);
            stringBuilder.append(" LIKE '%");
            stringBuilder.append(prepareSearchQuery(query));
            stringBuilder.append("%' ORDER BY ");
            stringBuilder.append(TABLE_NAME_FEED_ITEMS);
            stringBuilder.append(".");
            stringBuilder.append(KEY_PUBDATE);
            stringBuilder.append(" DESC");
            return sQLiteDatabase.rawQuery(stringBuilder.toString(), new String[]{String.valueOf(feedID)});
        }
        sQLiteDatabase = db;
        stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT ");
        stringBuilder.append(TextUtils.join(", ", FEEDITEM_SEL_FI_SMALL));
        stringBuilder.append(" FROM ");
        stringBuilder.append(TABLE_NAME_FEED_ITEMS);
        stringBuilder.append(" JOIN ");
        stringBuilder.append(TABLE_NAME_FEEDS);
        stringBuilder.append(" ON ");
        stringBuilder.append(TABLE_NAME_FEED_ITEMS);
        stringBuilder.append(".");
        stringBuilder.append(KEY_FEED);
        stringBuilder.append("=");
        stringBuilder.append(TABLE_NAME_FEEDS);
        stringBuilder.append(".");
        stringBuilder.append("id");
        stringBuilder.append(" WHERE ");
        stringBuilder.append(KEY_FEED_IDENTIFIER);
        stringBuilder.append(" LIKE '%");
        stringBuilder.append(prepareSearchQuery(query));
        stringBuilder.append("%' ORDER BY ");
        stringBuilder.append(TABLE_NAME_FEED_ITEMS);
        stringBuilder.append(".");
        stringBuilder.append(KEY_PUBDATE);
        stringBuilder.append(" DESC");
        return sQLiteDatabase.rawQuery(stringBuilder.toString(), null);
    }

    public Cursor searchItemChapters(long feedID, String searchQuery) {
        String query;
        if (feedID != 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("SELECT ");
            stringBuilder.append(SEL_FI_SMALL_STR);
            stringBuilder.append(" FROM ");
            stringBuilder.append(TABLE_NAME_FEED_ITEMS);
            stringBuilder.append(" INNER JOIN ");
            stringBuilder.append(TABLE_NAME_SIMPLECHAPTERS);
            stringBuilder.append(" ON ");
            stringBuilder.append(TABLE_NAME_SIMPLECHAPTERS);
            stringBuilder.append(".");
            stringBuilder.append(KEY_FEEDITEM);
            stringBuilder.append("=");
            stringBuilder.append(TABLE_NAME_FEED_ITEMS);
            stringBuilder.append(".");
            stringBuilder.append("id");
            stringBuilder.append(" WHERE ");
            stringBuilder.append(TABLE_NAME_FEED_ITEMS);
            stringBuilder.append(".");
            stringBuilder.append(KEY_FEED);
            stringBuilder.append("=");
            stringBuilder.append(feedID);
            stringBuilder.append(" AND ");
            stringBuilder.append(TABLE_NAME_SIMPLECHAPTERS);
            stringBuilder.append(".");
            stringBuilder.append("title");
            stringBuilder.append(" LIKE '%");
            stringBuilder.append(prepareSearchQuery(searchQuery));
            stringBuilder.append("%'");
            query = stringBuilder.toString();
        } else {
            query = new StringBuilder();
            query.append("SELECT ");
            query.append(SEL_FI_SMALL_STR);
            query.append(" FROM ");
            query.append(TABLE_NAME_FEED_ITEMS);
            query.append(" INNER JOIN ");
            query.append(TABLE_NAME_SIMPLECHAPTERS);
            query.append(" ON ");
            query.append(TABLE_NAME_SIMPLECHAPTERS);
            query.append(".");
            query.append(KEY_FEEDITEM);
            query.append("=");
            query.append(TABLE_NAME_FEED_ITEMS);
            query.append(".");
            query.append("id");
            query.append(" WHERE ");
            query.append(TABLE_NAME_SIMPLECHAPTERS);
            query.append(".");
            query.append("title");
            query.append(" LIKE '%");
            query.append(prepareSearchQuery(searchQuery));
            query.append("%'");
            query = query.toString();
        }
        return db.rawQuery(query, null);
    }

    public Cursor getFeedStatisticsCursor() {
        return db.rawQuery(FEED_STATISTICS_QUERY, null);
    }
}
