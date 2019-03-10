package de.danoeh.antennapod.core.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import de.danoeh.antennapod.core.C0734R;
import de.danoeh.antennapod.core.event.ProgressEvent;
import de.greenrobot.event.EventBus;

class PodDBAdapter$PodDBHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1060596;
    private final Context context;

    public PodDBAdapter$PodDBHelper(Context context, String name, CursorFactory factory) {
        super(context, name, factory, VERSION, new PodDBAdapter$PodDbErrorHandler());
        this.context = context;
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Feeds (id INTEGER PRIMARY KEY AUTOINCREMENT ,title TEXT,custom_title TEXT,file_url TEXT,download_url TEXT,downloaded INTEGER,link TEXT,description TEXT,payment_link TEXT,last_update TEXT,language TEXT,author TEXT,image_url TEXT,type TEXT,feed_identifier TEXT,auto_download INTEGER DEFAULT 1,flattr_status INTEGER,username TEXT,password TEXT,include_filter TEXT DEFAULT '',exclude_filter TEXT DEFAULT '',keep_updated INTEGER DEFAULT 1,is_paged INTEGER DEFAULT 0,next_page_link TEXT,hide TEXT,last_update_failed INTEGER DEFAULT 0,auto_delete_action INTEGER DEFAULT 0)");
        db.execSQL("CREATE TABLE FeedItems (id INTEGER PRIMARY KEY AUTOINCREMENT ,title TEXT,content_encoded TEXT,pubDate INTEGER,read INTEGER,link TEXT,description TEXT,payment_link TEXT,media INTEGER,feed INTEGER,has_simple_chapters INTEGER,item_identifier TEXT,flattr_status INTEGER,image_url TEXT,auto_download INTEGER)");
        db.execSQL("CREATE TABLE FeedMedia (id INTEGER PRIMARY KEY AUTOINCREMENT ,duration INTEGER,file_url TEXT,download_url TEXT,downloaded INTEGER,position INTEGER,filesize INTEGER,mime_type TEXT,playback_completion_date INTEGER,feeditem INTEGER,played_duration INTEGER,has_embedded_picture INTEGER,last_played_time INTEGER)");
        db.execSQL("CREATE TABLE DownloadLog (id INTEGER PRIMARY KEY AUTOINCREMENT ,feedfile INTEGER,feedfile_type INTEGER,reason INTEGER,successful INTEGER,completion_date INTEGER,reason_detailed TEXT,title TEXT)");
        db.execSQL("CREATE TABLE Queue(id INTEGER PRIMARY KEY,feeditem INTEGER,feed INTEGER)");
        db.execSQL("CREATE TABLE SimpleChapters (id INTEGER PRIMARY KEY AUTOINCREMENT ,title TEXT,start INTEGER,feeditem INTEGER,link TEXT,type INTEGER)");
        db.execSQL("CREATE TABLE Favorites(id INTEGER PRIMARY KEY,feeditem INTEGER,feed INTEGER)");
        db.execSQL("CREATE INDEX FeedItems_feed ON FeedItems (feed)");
        db.execSQL("CREATE INDEX IF NOT EXISTS FeedItems_pubDate ON FeedItems (pubDate)");
        db.execSQL("CREATE INDEX IF NOT EXISTS FeedItems_read ON FeedItems (read)");
        db.execSQL("CREATE INDEX FeedMedia_feeditem ON FeedMedia (feeditem)");
        db.execSQL("CREATE INDEX Queue_feeditem ON Queue (feeditem)");
        db.execSQL("CREATE INDEX SimpleChapters_feeditem ON SimpleChapters (feeditem)");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        EventBus.getDefault().post(ProgressEvent.start(this.context.getString(C0734R.string.progress_upgrading_database)));
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Upgrading from version ");
        stringBuilder.append(oldVersion);
        stringBuilder.append(" to ");
        stringBuilder.append(newVersion);
        stringBuilder.append(".");
        Log.w("DBAdapter", stringBuilder.toString());
        DBUpgrader.upgrade(db, oldVersion, newVersion);
        EventBus.getDefault().post(ProgressEvent.end());
    }
}
