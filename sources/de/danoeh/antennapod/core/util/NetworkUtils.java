package de.danoeh.antennapod.core.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.net.ConnectivityManagerCompat;
import android.text.TextUtils;
import android.util.Log;
import de.danoeh.antennapod.core.feed.FeedMedia;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.service.download.AntennapodHttpClient;
import de.danoeh.antennapod.core.storage.DBWriter;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import okhttp3.Request.Builder;
import okhttp3.Response;

public class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();
    private static Context context;

    private NetworkUtils() {
    }

    public static void init(Context context) {
        context = context;
    }

    public static boolean autodownloadNetworkAvailable() {
        NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        if (networkInfo != null) {
            if (networkInfo.getType() == 1) {
                Log.d(TAG, "Device is connected to Wi-Fi");
                if (networkInfo.isConnected()) {
                    if (UserPreferences.isEnableAutodownloadWifiFilter()) {
                        if (Arrays.asList(UserPreferences.getAutodownloadSelectedNetworks()).contains(Integer.toString(((WifiManager) context.getApplicationContext().getSystemService("wifi")).getConnectionInfo().getNetworkId()))) {
                            Log.d(TAG, "Current network is on the selected networks list");
                            return true;
                        }
                    } else {
                        Log.d(TAG, "Auto-dl filter is disabled");
                        return true;
                    }
                }
            } else if (!UserPreferences.isEnableAutodownloadOnMobile()) {
                Log.d(TAG, "Auto Download not enabled on Mobile");
                return false;
            } else if (!networkInfo.isRoaming()) {
                return true;
            } else {
                Log.d(TAG, "Roaming on foreign network");
                return false;
            }
        }
        Log.d(TAG, "Network for auto-dl is not available");
        return false;
    }

    public static boolean networkAvailable() {
        NetworkInfo info = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    public static boolean isDownloadAllowed() {
        if (!UserPreferences.isAllowMobileUpdate()) {
            if (isNetworkMetered()) {
                return false;
            }
        }
        return true;
    }

    private static boolean isNetworkMetered() {
        return ConnectivityManagerCompat.isActiveNetworkMetered((ConnectivityManager) context.getSystemService("connectivity"));
    }

    public static String getWifiSsid() {
        WifiInfo wifiInfo = ((WifiManager) context.getApplicationContext().getSystemService("wifi")).getConnectionInfo();
        if (wifiInfo != null) {
            return wifiInfo.getSSID();
        }
        return null;
    }

    public static Single<Long> getFeedMediaSizeObservable(FeedMedia media) {
        return Single.create(new -$$Lambda$NetworkUtils$xhAeK9_hs_fYEDitvEvFpRPJJek(media)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    static /* synthetic */ void lambda$getFeedMediaSizeObservable$0(FeedMedia media, SingleEmitter emitter) throws Exception {
        if (isDownloadAllowed()) {
            String url;
            StringBuilder stringBuilder;
            long size = -2147483648L;
            if (media.isDownloaded()) {
                File mediaFile = new File(media.getLocalMediaUrl());
                if (mediaFile.exists()) {
                    size = mediaFile.length();
                }
            } else if (!media.checkedOnSizeButUnknown()) {
                url = media.getDownload_url();
                if (TextUtils.isEmpty(url)) {
                    emitter.onSuccess(Long.valueOf(0));
                    return;
                }
                try {
                    Response response = AntennapodHttpClient.getHttpClient().newCall(new Builder().url(url).header("Accept-Encoding", "identity").head().build()).execute();
                    if (response.isSuccessful()) {
                        try {
                            size = (long) Integer.parseInt(response.header("Content-Length"));
                        } catch (NumberFormatException e) {
                            Log.e(TAG, Log.getStackTraceString(e));
                        }
                    }
                    url = TAG;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("new size: ");
                    stringBuilder.append(size);
                    Log.d(url, stringBuilder.toString());
                    if (size > 0) {
                        media.setCheckedOnSizeButUnknown();
                    } else {
                        media.setSize(size);
                    }
                    emitter.onSuccess(Long.valueOf(size));
                    DBWriter.setFeedMedia(media);
                    return;
                } catch (IOException e2) {
                    emitter.onSuccess(Long.valueOf(0));
                    Log.e(TAG, Log.getStackTraceString(e2));
                    return;
                }
            }
            url = TAG;
            stringBuilder = new StringBuilder();
            stringBuilder.append("new size: ");
            stringBuilder.append(size);
            Log.d(url, stringBuilder.toString());
            if (size > 0) {
                media.setSize(size);
            } else {
                media.setCheckedOnSizeButUnknown();
            }
            emitter.onSuccess(Long.valueOf(size));
            DBWriter.setFeedMedia(media);
            return;
        }
        emitter.onSuccess(Long.valueOf(0));
    }
}
