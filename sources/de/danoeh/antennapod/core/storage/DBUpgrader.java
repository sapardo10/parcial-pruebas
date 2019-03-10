package de.danoeh.antennapod.core.storage;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaMetadataRetriever;
import android.util.Log;

class DBUpgrader {
    DBUpgrader() {
    }

    static void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        int i;
        Cursor feeditemCursor;
        SQLiteDatabase sQLiteDatabase = db;
        int i2 = oldVersion;
        if (i2 <= 1) {
            sQLiteDatabase.execSQL("ALTER TABLE Feeds ADD COLUMN type TEXT");
        }
        if (i2 <= 2) {
            sQLiteDatabase.execSQL("ALTER TABLE SimpleChapters ADD COLUMN link TEXT");
        }
        if (i2 <= 3) {
            sQLiteDatabase.execSQL("ALTER TABLE FeedItems ADD COLUMN item_identifier TEXT");
        }
        if (i2 <= 4) {
            sQLiteDatabase.execSQL("ALTER TABLE Feeds ADD COLUMN feed_identifier TEXT");
        }
        if (i2 <= 5) {
            sQLiteDatabase.execSQL("ALTER TABLE DownloadLog ADD COLUMN reason_detailed TEXT");
            sQLiteDatabase.execSQL("ALTER TABLE DownloadLog ADD COLUMN title TEXT");
        }
        if (i2 <= 6) {
            sQLiteDatabase.execSQL("ALTER TABLE SimpleChapters ADD COLUMN type INTEGER");
        }
        if (i2 <= 7) {
            sQLiteDatabase.execSQL("ALTER TABLE FeedMedia ADD COLUMN playback_completion_date INTEGER");
        }
        int i3;
        int i4;
        if (i2 <= 8) {
            sQLiteDatabase.execSQL("ALTER TABLE FeedMedia ADD COLUMN feeditem INTEGER");
            SQLiteDatabase feeditemCursor2 = db;
            i = 0;
            i3 = 8;
            i4 = 7;
            feeditemCursor = feeditemCursor2.query("FeedItems", new String[]{"id", "media"}, "? > 0", new String[]{"media"}, null, null, null);
            if (feeditemCursor.moveToFirst()) {
                db.beginTransaction();
                ContentValues contentValues = new ContentValues();
                while (true) {
                    long mediaId = feeditemCursor.getLong(1);
                    contentValues.put(PodDBAdapter.KEY_FEEDITEM, Long.valueOf(feeditemCursor.getLong(0)));
                    sQLiteDatabase.update("FeedMedia", contentValues, "id=?", new String[]{String.valueOf(mediaId)});
                    contentValues.clear();
                    if (!feeditemCursor.moveToNext()) {
                        break;
                    }
                }
                db.setTransactionSuccessful();
                db.endTransaction();
            }
            feeditemCursor.close();
        } else {
            i = 0;
            i3 = 8;
            i4 = 7;
        }
        if (i2 <= 9) {
            sQLiteDatabase.execSQL("ALTER TABLE Feeds ADD COLUMN auto_download INTEGER DEFAULT 1");
        }
        if (i2 <= 10) {
            sQLiteDatabase.execSQL("ALTER TABLE Feeds ADD COLUMN flattr_status INTEGER");
            sQLiteDatabase.execSQL("ALTER TABLE FeedItems ADD COLUMN flattr_status INTEGER");
            sQLiteDatabase.execSQL("ALTER TABLE FeedMedia ADD COLUMN played_duration INTEGER");
        }
        if (i2 <= 11) {
            sQLiteDatabase.execSQL("ALTER TABLE Feeds ADD COLUMN username TEXT");
            sQLiteDatabase.execSQL("ALTER TABLE Feeds ADD COLUMN password TEXT");
            sQLiteDatabase.execSQL("ALTER TABLE FeedItems ADD COLUMN image INTEGER");
        }
        if (i2 <= 12) {
            sQLiteDatabase.execSQL("ALTER TABLE Feeds ADD COLUMN is_paged INTEGER DEFAULT 0");
            sQLiteDatabase.execSQL("ALTER TABLE Feeds ADD COLUMN next_page_link TEXT");
        }
        if (i2 <= 13) {
            sQLiteDatabase.execSQL(String.format("DELETE FROM %s WHERE %s NOT IN (SELECT MIN(%s) as %s FROM %s GROUP BY %s,%s,%s,%s,%s)", new Object[]{"SimpleChapters", "id", "id", "id", "SimpleChapters", "title", "start", PodDBAdapter.KEY_FEEDITEM, PodDBAdapter.KEY_LINK, "type"}));
        }
        if (i2 <= 14) {
            sQLiteDatabase.execSQL("ALTER TABLE FeedItems ADD COLUMN auto_download INTEGER");
            sQLiteDatabase.execSQL("UPDATE FeedItems SET auto_download = (SELECT auto_download FROM Feeds WHERE Feeds.id = FeedItems.feed)");
            sQLiteDatabase.execSQL("ALTER TABLE Feeds ADD COLUMN hide TEXT");
            sQLiteDatabase.execSQL("ALTER TABLE Feeds ADD COLUMN last_update_failed INTEGER DEFAULT 0");
            sQLiteDatabase.execSQL("CREATE INDEX FeedItems_feed ON FeedItems (feed)");
            sQLiteDatabase.execSQL("CREATE INDEX FeedMedia_feeditem ON FeedMedia (feeditem)");
            sQLiteDatabase.execSQL("CREATE INDEX Queue_feeditem ON Queue (feeditem)");
            sQLiteDatabase.execSQL("CREATE INDEX SimpleChapters_feeditem ON SimpleChapters (feeditem)");
        }
        if (i2 <= 15) {
            sQLiteDatabase.execSQL("ALTER TABLE FeedMedia ADD COLUMN has_embedded_picture INTEGER DEFAULT -1");
            sQLiteDatabase.execSQL("UPDATE FeedMedia SET has_embedded_picture=0 WHERE downloaded=0");
            feeditemCursor = sQLiteDatabase.rawQuery("SELECT file_url FROM FeedMedia WHERE downloaded=1  AND has_embedded_picture=-1", null);
            if (feeditemCursor.moveToFirst()) {
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                while (true) {
                    String fileUrl = feeditemCursor.getString(i);
                    try {
                        mmr.setDataSource(fileUrl);
                        StringBuilder stringBuilder;
                        if (mmr.getEmbeddedPicture() != null) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("UPDATE FeedMedia SET has_embedded_picture=1 WHERE file_url='");
                            stringBuilder.append(fileUrl);
                            stringBuilder.append("'");
                            sQLiteDatabase.execSQL(stringBuilder.toString());
                        } else {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("UPDATE FeedMedia SET has_embedded_picture=0 WHERE file_url='");
                            stringBuilder.append(fileUrl);
                            stringBuilder.append("'");
                            sQLiteDatabase.execSQL(stringBuilder.toString());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (!feeditemCursor.moveToNext()) {
                        break;
                    }
                }
            }
            feeditemCursor.close();
        }
        if (i2 <= 16) {
            String sql = new StringBuilder();
            sql.append("UPDATE FeedItems SET read=-1 WHERE id IN (");
            sql.append("SELECT FeedItems.id FROM FeedItems INNER JOIN FeedMedia ON FeedItems.id=FeedMedia.feeditem LEFT OUTER JOIN Queue ON FeedItems.id=Queue.feeditem WHERE FeedItems.read = 0 AND FeedMedia.downloaded = 0 AND FeedMedia.position = 0 AND Queue.id IS NULL");
            sql.append(")");
            sql = sql.toString();
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("SQL: ");
            stringBuilder2.append(sql);
            Log.d("Migration", stringBuilder2.toString());
            sQLiteDatabase.execSQL(sql);
        }
        if (i2 <= 17) {
            sQLiteDatabase.execSQL("ALTER TABLE Feeds ADD COLUMN auto_delete_action INTEGER DEFAULT 0");
        }
        if (i2 < 1030005) {
            sQLiteDatabase.execSQL("UPDATE FeedItems SET auto_download=0 WHERE (read=1 OR id IN (SELECT feeditem FROM FeedMedia WHERE position>0 OR downloaded=1)) AND id NOT IN (SELECT feeditem FROM Queue)");
        }
        if (i2 < 1040001) {
            sQLiteDatabase.execSQL("CREATE TABLE Favorites(id INTEGER PRIMARY KEY,feeditem INTEGER,feed INTEGER)");
        }
        if (i2 < 1040002) {
            sQLiteDatabase.execSQL("ALTER TABLE FeedMedia ADD COLUMN last_played_time INTEGER DEFAULT 0");
        }
        if (i2 < 1040013) {
            sQLiteDatabase.execSQL("CREATE INDEX IF NOT EXISTS FeedItems_pubDate ON FeedItems (pubDate)");
            sQLiteDatabase.execSQL("CREATE INDEX IF NOT EXISTS FeedItems_read ON FeedItems (read)");
        }
        if (i2 < 1050003) {
            db.beginTransaction();
            sQLiteDatabase.execSQL("UPDATE Feeds\nSET hide = replace(hide, 'unplayed', 'noplay')");
            sQLiteDatabase.execSQL("UPDATE Feeds\nSET hide = replace(hide, 'not_queued', 'noqueue')");
            sQLiteDatabase.execSQL("UPDATE Feeds\nSET hide = replace(hide, 'not_downloaded', 'nodl')");
            sQLiteDatabase.execSQL("UPDATE Feeds\nSET hide = replace(hide, 'played', 'unplayed')");
            sQLiteDatabase.execSQL("UPDATE Feeds\nSET hide = replace(hide, 'queued', 'not_queued')");
            sQLiteDatabase.execSQL("UPDATE Feeds\nSET hide = replace(hide, 'downloaded', 'not_downloaded')");
            sQLiteDatabase.execSQL("UPDATE Feeds\nSET hide = replace(hide, 'noplay', 'played')");
            sQLiteDatabase.execSQL("UPDATE Feeds\nSET hide = replace(hide, 'noqueue', 'queued')");
            sQLiteDatabase.execSQL("UPDATE Feeds\nSET hide = replace(hide, 'nodl', 'downloaded')");
            sQLiteDatabase.execSQL("UPDATE Feeds\nSET hide = replace(hide, 'paused', 'unplayed')");
            db.setTransactionSuccessful();
            db.endTransaction();
            sQLiteDatabase.execSQL("ALTER TABLE Feeds ADD COLUMN include_filter TEXT DEFAULT ''");
            sQLiteDatabase.execSQL("ALTER TABLE Feeds ADD COLUMN exclude_filter TEXT DEFAULT ''");
            sQLiteDatabase.execSQL("ALTER TABLE Feeds ADD COLUMN keep_updated INTEGER DEFAULT 1");
        }
        if (i2 < 1050004) {
            sQLiteDatabase.execSQL("UPDATE Feeds SET last_update=NULL");
        }
        if (i2 < 1060200) {
            sQLiteDatabase.execSQL("ALTER TABLE Feeds ADD COLUMN custom_title TEXT");
        }
        if (i2 < 1060596) {
            sQLiteDatabase.execSQL("ALTER TABLE Feeds ADD COLUMN image_url TEXT");
            sQLiteDatabase.execSQL("ALTER TABLE FeedItems ADD COLUMN image_url TEXT");
            sQLiteDatabase.execSQL("UPDATE FeedItems SET image_url  = ( SELECT download_url FROM FeedImages WHERE FeedImages.id = FeedItems.image)");
            sQLiteDatabase.execSQL("UPDATE Feeds SET image_url = ( SELECT download_url FROM FeedImages WHERE FeedImages.id = Feeds.image)");
            sQLiteDatabase.execSQL("DROP TABLE FeedImages");
        }
    }
}
