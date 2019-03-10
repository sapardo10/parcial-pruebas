package de.danoeh.antennapod.core.util;

import android.app.Activity;
import android.os.Build.VERSION;
import android.os.StatFs;
import android.util.Log;
import de.danoeh.antennapod.core.ClientConfig;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import java.io.File;

public class StorageUtils {
    private static final String TAG = "StorageUtils";

    private StorageUtils() {
    }

    public static boolean storageAvailable() {
        File dir = UserPreferences.getDataFolder(null);
        boolean z = false;
        if (dir != null) {
            if (dir.exists() && dir.canRead() && dir.canWrite()) {
                z = true;
            }
            return z;
        }
        Log.d(TAG, "Storage not available: data folder is null");
        return false;
    }

    public static boolean checkStorageAvailability(Activity activity) {
        boolean storageAvailable = storageAvailable();
        if (!storageAvailable) {
            activity.finish();
            activity.startActivity(ClientConfig.applicationCallbacks.getStorageErrorActivity(activity));
        }
        return storageAvailable;
    }

    public static long getFreeSpaceAvailable() {
        File dataFolder = UserPreferences.getDataFolder(null);
        if (dataFolder != null) {
            return getFreeSpaceAvailable(dataFolder.getAbsolutePath());
        }
        return 0;
    }

    public static long getFreeSpaceAvailable(String path) {
        long availableBlocks;
        long blockSize;
        StatFs stat = new StatFs(path);
        if (VERSION.SDK_INT >= 18) {
            availableBlocks = stat.getAvailableBlocksLong();
            blockSize = stat.getBlockSizeLong();
        } else {
            availableBlocks = (long) stat.getAvailableBlocks();
            blockSize = (long) stat.getBlockSize();
        }
        return availableBlocks * blockSize;
    }
}
