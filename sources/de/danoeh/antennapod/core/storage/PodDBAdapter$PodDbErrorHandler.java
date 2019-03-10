package de.danoeh.antennapod.core.storage;

import android.database.DatabaseErrorHandler;
import android.database.DefaultDatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

public class PodDBAdapter$PodDbErrorHandler implements DatabaseErrorHandler {
    public void onCorruption(SQLiteDatabase db) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Database corrupted: ");
        stringBuilder.append(db.getPath());
        Log.e("PodDBAdapter", stringBuilder.toString());
        File dbPath = new File(db.getPath());
        File backupFile = new File(PodDBAdapter.access$000().getExternalFilesDir(null), "CorruptedDatabaseBackup.db");
        try {
            FileUtils.copyFile(dbPath, backupFile);
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Dumped database to ");
            stringBuilder2.append(backupFile.getPath());
            Log.d("PodDBAdapter", stringBuilder2.toString());
        } catch (IOException e) {
            Log.d("PodDBAdapter", Log.getStackTraceString(e));
        }
        new DefaultDatabaseErrorHandler().onCorruption(db);
    }
}
