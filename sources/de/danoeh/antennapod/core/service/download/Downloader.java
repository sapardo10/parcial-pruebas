package de.danoeh.antennapod.core.service.download;

import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import de.danoeh.antennapod.core.C0734R;
import de.danoeh.antennapod.core.ClientConfig;
import java.util.concurrent.Callable;

public abstract class Downloader implements Callable<Downloader> {
    private static final String TAG = "Downloader";
    volatile boolean cancelled = false;
    private volatile boolean finished;
    final DownloadRequest request;
    final DownloadStatus result;

    protected abstract void download();

    Downloader(DownloadRequest request) {
        this.request = request;
        this.request.setStatusMsg(C0734R.string.download_pending);
        this.result = new DownloadStatus(request, null, false, false, null);
    }

    public final Downloader call() {
        WifiManager wifiManager = (WifiManager) ClientConfig.applicationCallbacks.getApplicationInstance().getApplicationContext().getSystemService("wifi");
        WifiLock wifiLock = null;
        if (wifiManager != null) {
            wifiLock = wifiManager.createWifiLock(TAG);
            wifiLock.acquire();
        }
        download();
        if (wifiLock != null) {
            wifiLock.release();
        }
        if (this.result != null) {
            this.finished = true;
            return this;
        }
        throw new IllegalStateException("Downloader hasn't created DownloadStatus object");
    }

    public DownloadRequest getDownloadRequest() {
        return this.request;
    }

    public DownloadStatus getResult() {
        return this.result;
    }

    public boolean isFinished() {
        return this.finished;
    }

    public void cancel() {
        this.cancelled = true;
    }
}
