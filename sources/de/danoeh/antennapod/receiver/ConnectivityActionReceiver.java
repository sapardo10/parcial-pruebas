package de.danoeh.antennapod.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;
import de.danoeh.antennapod.core.ClientConfig;
import de.danoeh.antennapod.core.storage.DBTasks;
import de.danoeh.antennapod.core.storage.DownloadRequester;
import de.danoeh.antennapod.core.util.NetworkUtils;

public class ConnectivityActionReceiver extends BroadcastReceiver {
    private static final String TAG = "ConnectivityActionRecvr";

    public void onReceive(Context context, Intent intent) {
        if (TextUtils.equals(intent.getAction(), "android.net.conn.CONNECTIVITY_CHANGE")) {
            Log.d(TAG, "Received intent");
            ClientConfig.initialize(context);
            if (NetworkUtils.autodownloadNetworkAvailable()) {
                Log.d(TAG, "auto-dl network available, starting auto-download");
                DBTasks.autodownloadUndownloadedItems(context);
                return;
            }
            NetworkInfo ni = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
            if (ni != null) {
                if (ni.getType() == 1) {
                    return;
                }
            }
            Log.i(TAG, "Device is no longer connected to Wi-Fi. Cancelling ongoing downloads");
            DownloadRequester.getInstance().cancelAllDownloads(context);
        }
    }
}
