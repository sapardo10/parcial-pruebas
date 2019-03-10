package de.danoeh.antennapod.core.asynctask;

import android.content.Context;
import android.util.Log;
import de.danoeh.antennapod.core.BuildConfig;
import de.danoeh.antennapod.core.storage.DBWriter;
import de.danoeh.antennapod.core.util.flattr.FlattrUtils;
import org.shredzone.flattr4j.exception.FlattrException;

public class FlattrStatusFetcher extends Thread {
    private static final String TAG = "FlattrStatusFetcher";

    public FlattrStatusFetcher(Context context) {
    }

    public void run() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Starting background work: Retrieving Flattr status");
        }
        Thread.currentThread().setPriority(1);
        try {
            DBWriter.setFlattredStatus(FlattrUtils.retrieveFlattredThings()).get();
        } catch (FlattrException e) {
            e.printStackTrace();
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("flattrQueue exception retrieving list with flattred items ");
            stringBuilder.append(e.getMessage());
            Log.d(str, stringBuilder.toString());
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Finished background work: Retrieved Flattr status");
        }
    }
}
