package de.danoeh.antennapod.core.service.download;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat.BigTextStyle;
import android.support.v4.app.NotificationCompat.Builder;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.webkit.URLUtil;
import de.danoeh.antennapod.core.C0734R;
import de.danoeh.antennapod.core.ClientConfig;
import de.danoeh.antennapod.core.event.DownloadEvent;
import de.danoeh.antennapod.core.event.FeedItemEvent;
import de.danoeh.antennapod.core.feed.Feed;
import de.danoeh.antennapod.core.feed.FeedFile;
import de.danoeh.antennapod.core.feed.FeedItem;
import de.danoeh.antennapod.core.feed.FeedMedia;
import de.danoeh.antennapod.core.feed.FeedPreferences;
import de.danoeh.antennapod.core.feed.FeedPreferences.AutoDeleteAction;
import de.danoeh.antennapod.core.preferences.GpodnetPreferences;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.service.GpodnetSyncService;
import de.danoeh.antennapod.core.storage.DBReader;
import de.danoeh.antennapod.core.storage.DBTasks;
import de.danoeh.antennapod.core.storage.DBWriter;
import de.danoeh.antennapod.core.storage.DownloadRequestException;
import de.danoeh.antennapod.core.storage.DownloadRequester;
import de.danoeh.antennapod.core.storage.PodDBAdapter;
import de.danoeh.antennapod.core.syndication.handler.FeedHandler;
import de.danoeh.antennapod.core.syndication.handler.FeedHandlerResult;
import de.danoeh.antennapod.core.syndication.handler.UnsupportedFeedtypeException;
import de.danoeh.antennapod.core.util.DownloadError;
import de.danoeh.antennapod.core.util.InvalidFeedException;
import de.danoeh.antennapod.core.util.gui.NotificationUtils;
import de.greenrobot.event.EventBus;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.io.FileUtils;

public class DownloadService extends Service {
    public static final String ACTION_CANCEL_ALL_DOWNLOADS = "action.de.danoeh.antennapod.core.service.cancelAllDownloads";
    public static final String ACTION_CANCEL_DOWNLOAD = "action.de.danoeh.antennapod.core.service.cancelDownload";
    public static final String EXTRA_DOWNLOAD_URL = "downloadUrl";
    public static final String EXTRA_REQUEST = "request";
    private static final int NOTIFICATION_ID = 2;
    private static final int REPORT_ID = 3;
    private static final int SCHED_EX_POOL_SIZE = 1;
    private static final String TAG = "DownloadService";
    public static boolean isRunning = false;
    private final BroadcastReceiver cancelDownloadReceiver = new C07482();
    private final Thread downloadCompletionThread = new C07471();
    private CompletionService<Downloader> downloadExecutor;
    private List<Downloader> downloads;
    private FeedSyncThread feedSyncThread;
    private Handler handler;
    private long lastPost = 0;
    private final IBinder mBinder = new LocalBinder();
    private Builder notificationCompatBuilder;
    private NotificationUpdater notificationUpdater;
    private ScheduledFuture<?> notificationUpdaterFuture;
    private AtomicInteger numberOfDownloads;
    private final Runnable postDownloaderTask = new C07493();
    private final Handler postHandler = new Handler();
    private List<DownloadStatus> reportQueue;
    private DownloadRequester requester;
    private ScheduledThreadPoolExecutor schedExecutor;
    private ExecutorService syncExecutor;

    /* renamed from: de.danoeh.antennapod.core.service.download.DownloadService$1 */
    class C07471 extends Thread {
        private static final String TAG = "downloadCompletionThd";

        C07471() {
        }

        public void run() {
            Log.d(TAG, "downloadCompletionThread was started");
            while (!isInterrupted()) {
                try {
                    Downloader downloader = (Downloader) DownloadService.this.downloadExecutor.take().get();
                    Log.d(TAG, "Received 'Download Complete' - message.");
                    DownloadService.this.removeDownload(downloader);
                    DownloadStatus status = downloader.getResult();
                    boolean successful = status.isSuccessful();
                    int type = status.getFeedfileType();
                    if (!successful) {
                        DownloadService.this.numberOfDownloads.decrementAndGet();
                        if (status.isCancelled()) {
                            if (status.getFeedfileType() == 2) {
                                FeedMedia media = DBReader.getFeedMedia(status.getFeedfileId());
                                if (media != null) {
                                    FeedItem item = media.getItem();
                                    FeedItem item2 = item;
                                    if (item != null) {
                                        EventBus.getDefault().post(FeedItemEvent.updated(item2));
                                    }
                                }
                                return;
                            }
                        } else if (status.getReason() == DownloadError.ERROR_UNAUTHORIZED) {
                            DownloadService.this.postAuthenticationNotification(downloader.getDownloadRequest());
                        } else {
                            if (status.getReason() == DownloadError.ERROR_HTTP_DATA_ERROR) {
                                if (Integer.parseInt(status.getReasonDetailed()) == 416) {
                                    Log.d(TAG, "Requested invalid range, restarting download from the beginning");
                                    FileUtils.deleteQuietly(new File(downloader.getDownloadRequest().getDestination()));
                                    DownloadRequester.getInstance().download(DownloadService.this, downloader.getDownloadRequest());
                                }
                            }
                            Log.e(TAG, "Download failed");
                            DownloadService.this.saveDownloadStatus(status);
                            DownloadService.this.handleFailedDownload(status, downloader.getDownloadRequest());
                            if (type == 2) {
                                FeedMedia media2 = DBReader.getFeedMedia(status.getFeedfileId());
                                if (media2 != null) {
                                    FeedItem item3 = media2.getItem();
                                    FeedItem item4 = item3;
                                    if (item3 != null) {
                                        boolean httpNotFound;
                                        boolean forbidden;
                                        boolean notEnoughSpace;
                                        boolean wrongFileType;
                                        if (status.getReason() == DownloadError.ERROR_HTTP_DATA_ERROR) {
                                            if (String.valueOf(404).equals(status.getReasonDetailed())) {
                                                httpNotFound = true;
                                                if (status.getReason() == DownloadError.ERROR_FORBIDDEN) {
                                                    if (String.valueOf(403).equals(status.getReasonDetailed())) {
                                                        forbidden = true;
                                                        notEnoughSpace = status.getReason() != DownloadError.ERROR_NOT_ENOUGH_SPACE;
                                                        wrongFileType = status.getReason() != DownloadError.ERROR_FILE_TYPE;
                                                        if (!(httpNotFound || forbidden || notEnoughSpace)) {
                                                            if (wrongFileType) {
                                                                EventBus.getDefault().post(FeedItemEvent.updated(item4));
                                                            }
                                                        }
                                                        DBWriter.saveFeedItemAutoDownloadFailed(item4).get();
                                                        EventBus.getDefault().post(FeedItemEvent.updated(item4));
                                                    }
                                                }
                                                forbidden = false;
                                                if (status.getReason() != DownloadError.ERROR_NOT_ENOUGH_SPACE) {
                                                }
                                                if (status.getReason() != DownloadError.ERROR_FILE_TYPE) {
                                                }
                                                if (wrongFileType) {
                                                    EventBus.getDefault().post(FeedItemEvent.updated(item4));
                                                } else {
                                                    DBWriter.saveFeedItemAutoDownloadFailed(item4).get();
                                                    EventBus.getDefault().post(FeedItemEvent.updated(item4));
                                                }
                                            }
                                        }
                                        httpNotFound = false;
                                        if (status.getReason() == DownloadError.ERROR_FORBIDDEN) {
                                            if (String.valueOf(403).equals(status.getReasonDetailed())) {
                                                forbidden = true;
                                                if (status.getReason() != DownloadError.ERROR_NOT_ENOUGH_SPACE) {
                                                }
                                                if (status.getReason() != DownloadError.ERROR_FILE_TYPE) {
                                                }
                                                if (wrongFileType) {
                                                    DBWriter.saveFeedItemAutoDownloadFailed(item4).get();
                                                    EventBus.getDefault().post(FeedItemEvent.updated(item4));
                                                } else {
                                                    EventBus.getDefault().post(FeedItemEvent.updated(item4));
                                                }
                                            }
                                        }
                                        forbidden = false;
                                        if (status.getReason() != DownloadError.ERROR_NOT_ENOUGH_SPACE) {
                                        }
                                        if (status.getReason() != DownloadError.ERROR_FILE_TYPE) {
                                        }
                                        if (wrongFileType) {
                                            EventBus.getDefault().post(FeedItemEvent.updated(item4));
                                        } else {
                                            DBWriter.saveFeedItemAutoDownloadFailed(item4).get();
                                            EventBus.getDefault().post(FeedItemEvent.updated(item4));
                                        }
                                    }
                                }
                                return;
                            }
                        }
                        DownloadService.this.queryDownloadsAsync();
                    } else if (type == 0) {
                        DownloadService.this.handleCompletedFeedDownload(downloader.getDownloadRequest());
                    } else if (type == 2) {
                        DownloadService.this.handleCompletedFeedMediaDownload(status, downloader.getDownloadRequest());
                    }
                } catch (InterruptedException e) {
                    Log.e(TAG, "DownloadCompletionThread was interrupted");
                } catch (ExecutionException e2) {
                    String str = TAG;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("ExecutionException in DownloadCompletionThread: ");
                    stringBuilder.append(e2.getMessage());
                    Log.e(str, stringBuilder.toString());
                    DownloadService.this.numberOfDownloads.decrementAndGet();
                }
            }
            Log.d(TAG, "End of downloadCompletionThread");
        }
    }

    /* renamed from: de.danoeh.antennapod.core.service.download.DownloadService$2 */
    class C07482 extends BroadcastReceiver {
        C07482() {
        }

        public void onReceive(Context context, Intent intent) {
            Downloader d;
            if (TextUtils.equals(intent.getAction(), DownloadService.ACTION_CANCEL_DOWNLOAD)) {
                String url = intent.getStringExtra(DownloadService.EXTRA_DOWNLOAD_URL);
                if (url != null) {
                    String str = DownloadService.TAG;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Cancelling download with url ");
                    stringBuilder.append(url);
                    Log.d(str, stringBuilder.toString());
                    d = DownloadService.this.getDownloader(url);
                    if (d != null) {
                        d.cancel();
                    } else {
                        String str2 = DownloadService.TAG;
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("Could not cancel download with url ");
                        stringBuilder2.append(url);
                        Log.e(str2, stringBuilder2.toString());
                    }
                    DownloadService.this.postDownloaders();
                } else {
                    throw new IllegalArgumentException("ACTION_CANCEL_DOWNLOAD intent needs download url extra");
                }
            } else if (TextUtils.equals(intent.getAction(), DownloadService.ACTION_CANCEL_ALL_DOWNLOADS)) {
                for (Downloader d2 : DownloadService.this.downloads) {
                    d2.cancel();
                    Log.d(DownloadService.TAG, "Cancelled all downloads");
                }
                DownloadService.this.postDownloaders();
                DownloadService.this.queryDownloads();
            }
            DownloadService.this.queryDownloads();
        }
    }

    /* renamed from: de.danoeh.antennapod.core.service.download.DownloadService$3 */
    class C07493 implements Runnable {
        C07493() {
        }

        public void run() {
            EventBus.getDefault().postSticky(DownloadEvent.refresh(Collections.unmodifiableList(DownloadService.this.downloads)));
            DownloadService.this.postHandler.postDelayed(DownloadService.this.postDownloaderTask, 1500);
        }
    }

    private static class FailedDownloadHandler implements Runnable {
        private final DownloadRequest request;
        private final DownloadStatus status;

        FailedDownloadHandler(DownloadStatus status, DownloadRequest request) {
            this.request = request;
            this.status = status;
        }

        public void run() {
            if (this.request.getFeedfileType() == 0) {
                DBWriter.setFeedLastUpdateFailed(this.request.getFeedfileId(), true);
            } else if (this.request.isDeleteOnFailure()) {
                Log.d(DownloadService.TAG, "Ignoring failed download, deleteOnFailure=true");
            }
        }
    }

    private class FeedSyncThread extends Thread {
        private static final String TAG = "FeedSyncThread";
        private static final long WAIT_TIMEOUT = 3000;
        private final BlockingQueue<DownloadRequest> completedRequests;
        private final ExecutorService dbService;
        private Future<?> dbUpdateFuture;
        private volatile boolean isActive;
        private volatile boolean isCollectingRequests;
        private final CompletionService<Pair<DownloadRequest, FeedHandlerResult>> parserService;

        private class FeedParserTask implements Callable<Pair<DownloadRequest, FeedHandlerResult>> {
            private final DownloadRequest request;

            private FeedParserTask(DownloadRequest request) {
                this.request = request;
            }

            public Pair<DownloadRequest, FeedHandlerResult> call() throws Exception {
                return FeedSyncThread.this.parseFeed(this.request);
            }
        }

        private FeedSyncThread() {
            this.completedRequests = new LinkedBlockingDeque();
            this.parserService = new ExecutorCompletionService(Executors.newSingleThreadExecutor());
            this.dbService = Executors.newSingleThreadExecutor();
            this.isActive = true;
            this.isCollectingRequests = null;
        }

        private List<Pair<DownloadRequest, FeedHandlerResult>> collectCompletedRequests() {
            List<Pair<DownloadRequest, FeedHandlerResult>> results = new LinkedList();
            DownloadRequester requester = DownloadRequester.getInstance();
            try {
                this.parserService.submit(new FeedParserTask((DownloadRequest) this.completedRequests.take()));
                int tasks = (0 + 1) + pollCompletedDownloads();
                this.isCollectingRequests = true;
                if (requester.isDownloadingFeeds()) {
                    long startTime = System.currentTimeMillis();
                    long currentTime = startTime;
                    while (requester.isDownloadingFeeds() && currentTime - startTime < WAIT_TIMEOUT) {
                        try {
                            String str = TAG;
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("Waiting for ");
                            stringBuilder.append((startTime + WAIT_TIMEOUT) - currentTime);
                            stringBuilder.append(" ms");
                            Log.d(str, stringBuilder.toString());
                            sleep((WAIT_TIMEOUT + startTime) - currentTime);
                        } catch (InterruptedException e) {
                            Log.d(TAG, "interrupted while waiting for more downloads");
                            tasks += pollCompletedDownloads();
                        } catch (Throwable th) {
                            currentTime = System.currentTimeMillis();
                        }
                        currentTime = System.currentTimeMillis();
                    }
                    tasks += pollCompletedDownloads();
                }
                this.isCollectingRequests = false;
                for (int i = 0; i < tasks; i++) {
                    try {
                        Pair<DownloadRequest, FeedHandlerResult> result = (Pair) this.parserService.take().get();
                        if (result != null) {
                            results.add(result);
                        }
                    } catch (InterruptedException e2) {
                        Log.e(TAG, "FeedSyncThread was interrupted");
                    } catch (ExecutionException e3) {
                        String str2 = TAG;
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("ExecutionException in FeedSyncThread: ");
                        stringBuilder2.append(e3.getMessage());
                        Log.e(str2, stringBuilder2.toString());
                    }
                }
                return results;
            } catch (InterruptedException e4) {
                Log.e(TAG, "FeedSyncThread was interrupted");
                return null;
            }
        }

        private int pollCompletedDownloads() {
            int tasks = 0;
            while (!this.completedRequests.isEmpty()) {
                this.parserService.submit(new FeedParserTask((DownloadRequest) this.completedRequests.poll()));
                tasks++;
            }
            return tasks;
        }

        public void run() {
            while (this.isActive) {
                List<Pair<DownloadRequest, FeedHandlerResult>> results = collectCompletedRequests();
                if (results != null) {
                    String str = TAG;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Bundling ");
                    stringBuilder.append(results.size());
                    stringBuilder.append(" feeds");
                    Log.d(str, stringBuilder.toString());
                    Future future = this.dbUpdateFuture;
                    if (future != null) {
                        try {
                            future.get();
                        } catch (InterruptedException e) {
                            Log.e(TAG, "FeedSyncThread was interrupted");
                        } catch (ExecutionException e2) {
                            String str2 = TAG;
                            StringBuilder stringBuilder2 = new StringBuilder();
                            stringBuilder2.append("ExecutionException in FeedSyncThread: ");
                            stringBuilder2.append(e2.getMessage());
                            Log.e(str2, stringBuilder2.toString());
                        }
                    }
                    this.dbUpdateFuture = this.dbService.submit(new C0745x5923bf26(this, results));
                }
            }
            Future future2 = this.dbUpdateFuture;
            if (future2 != null) {
                try {
                    future2.get();
                } catch (InterruptedException e3) {
                    Log.e(TAG, "interrupted while updating the db");
                } catch (ExecutionException e4) {
                    str = TAG;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("ExecutionException while updating the db: ");
                    stringBuilder.append(e4.getMessage());
                    Log.e(str, stringBuilder.toString());
                }
            }
            Log.d(TAG, "Shutting down");
        }

        public static /* synthetic */ void lambda$run$0(FeedSyncThread feedSyncThread, List results) {
            Feed[] savedFeeds = DBTasks.updateFeed(DownloadService.this, feedSyncThread.getFeeds(results));
            for (int i = 0; i < savedFeeds.length; i++) {
                Feed savedFeed = savedFeeds[i];
                boolean loadAllPages = ((DownloadRequest) ((Pair) results.get(i)).first).getArguments().getBoolean(DownloadRequester.REQUEST_ARG_LOAD_ALL_PAGES);
                Feed feed = ((FeedHandlerResult) ((Pair) results.get(i)).second).feed;
                if (loadAllPages && feed.getNextPageLink() != null) {
                    try {
                        feed.setId(savedFeed.getId());
                        DBTasks.loadNextPageOfFeed(DownloadService.this, savedFeed, true);
                    } catch (DownloadRequestException e) {
                        Log.e(TAG, "Error trying to load next page", e);
                    }
                }
                ClientConfig.downloadServiceCallbacks.onFeedParsed(DownloadService.this, savedFeed);
                DownloadService.this.numberOfDownloads.decrementAndGet();
            }
            DownloadService.this.queryDownloadsAsync();
        }

        private Feed[] getFeeds(List<Pair<DownloadRequest, FeedHandlerResult>> results) {
            Feed[] feeds = new Feed[results.size()];
            for (int i = 0; i < results.size(); i++) {
                feeds[i] = ((FeedHandlerResult) ((Pair) results.get(i)).second).feed;
            }
            return feeds;
        }

        private Pair<DownloadRequest, FeedHandlerResult> parseFeed(DownloadRequest request) {
            String str;
            String reasonDetailed;
            boolean successful;
            FeedHandlerResult result;
            List<DownloadStatus> log;
            DownloadRequest downloadRequest;
            boolean successful2;
            String str2;
            String reasonDetailed2;
            FeedSyncThread feedSyncThread = this;
            FeedFile feed = new Feed(request.getSource(), request.getLastModified());
            feed.setFile_url(request.getDestination());
            feed.setId(request.getFeedfileId());
            feed.setDownloaded(true);
            feed.setPreferences(new FeedPreferences(0, true, AutoDeleteAction.GLOBAL, request.getUsername(), request.getPassword()));
            feed.setPageNr(request.getArguments().getInt(DownloadRequester.REQUEST_ARG_PAGE_NR, 0));
            DownloadError reason = null;
            FeedHandlerResult feedHandlerResult = null;
            boolean deleted;
            String str3;
            StringBuilder stringBuilder;
            try {
                feedHandlerResult = new FeedHandler().parseFeed(feed);
                str = TAG;
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append(feed.getTitle());
                stringBuilder2.append(" parsed");
                Log.d(str, stringBuilder2.toString());
                if (checkFeedData(feed)) {
                    File feedFile = new File(request.getDestination());
                    if (feedFile.exists()) {
                        deleted = feedFile.delete();
                        str3 = TAG;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("Deletion of file '");
                        stringBuilder.append(feedFile.getAbsolutePath());
                        stringBuilder.append("' ");
                        stringBuilder.append(deleted ? PodDBAdapter.KEY_SUCCESSFUL : "FAILED");
                        Log.d(str3, stringBuilder.toString());
                    }
                    reasonDetailed = null;
                    successful = true;
                    result = feedHandlerResult;
                    if (successful) {
                        log = DBReader.getFeedDownloadLog(feed);
                        if (log.size() <= 0 && !((DownloadStatus) log.get(0)).isSuccessful()) {
                            DownloadService.this.saveDownloadStatus(new DownloadStatus(feed, feed.getHumanReadableIdentifier(), DownloadError.SUCCESS, successful, reasonDetailed));
                        }
                        return Pair.create(request, result);
                    }
                    downloadRequest = request;
                    DownloadService.this.numberOfDownloads.decrementAndGet();
                    DownloadService.this.saveDownloadStatus(new DownloadStatus(feed, feed.getHumanReadableIdentifier(), reason, successful, reasonDetailed));
                    return null;
                }
                throw new InvalidFeedException();
            } catch (Exception e) {
                successful2 = false;
                e.printStackTrace();
                reason = DownloadError.ERROR_PARSER_EXCEPTION;
                str = e.getMessage();
                File feedFile2 = new File(request.getDestination());
                if (feedFile2.exists()) {
                    deleted = feedFile2.delete();
                    str3 = TAG;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Deletion of file '");
                    stringBuilder.append(feedFile2.getAbsolutePath());
                    stringBuilder.append("' ");
                    str2 = deleted ? PodDBAdapter.KEY_SUCCESSFUL : "FAILED";
                    stringBuilder.append(str2);
                    Log.d(str3, stringBuilder.toString());
                    reasonDetailed = str;
                    successful = successful2;
                    result = feedHandlerResult;
                    if (successful) {
                        downloadRequest = request;
                        DownloadService.this.numberOfDownloads.decrementAndGet();
                        DownloadService.this.saveDownloadStatus(new DownloadStatus(feed, feed.getHumanReadableIdentifier(), reason, successful, reasonDetailed));
                        return null;
                    }
                    log = DBReader.getFeedDownloadLog(feed);
                    if (log.size() <= 0) {
                    }
                    return Pair.create(request, result);
                }
                reasonDetailed = str;
                successful = successful2;
                result = feedHandlerResult;
                if (successful) {
                    log = DBReader.getFeedDownloadLog(feed);
                    if (log.size() <= 0) {
                    }
                    return Pair.create(request, result);
                }
                downloadRequest = request;
                DownloadService.this.numberOfDownloads.decrementAndGet();
                DownloadService.this.saveDownloadStatus(new DownloadStatus(feed, feed.getHumanReadableIdentifier(), reason, successful, reasonDetailed));
                return null;
            } catch (UnsupportedFeedtypeException e2) {
                e2.printStackTrace();
                successful2 = false;
                reason = DownloadError.ERROR_UNSUPPORTED_TYPE;
                str = e2.getMessage();
                reasonDetailed2 = new File(request.getDestination());
                if (reasonDetailed2.exists()) {
                    deleted = reasonDetailed2.delete();
                    str3 = TAG;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Deletion of file '");
                    stringBuilder.append(reasonDetailed2.getAbsolutePath());
                    stringBuilder.append("' ");
                    str2 = deleted ? PodDBAdapter.KEY_SUCCESSFUL : "FAILED";
                    stringBuilder.append(str2);
                    Log.d(str3, stringBuilder.toString());
                    reasonDetailed = str;
                    successful = successful2;
                    result = feedHandlerResult;
                    if (successful) {
                        downloadRequest = request;
                        DownloadService.this.numberOfDownloads.decrementAndGet();
                        DownloadService.this.saveDownloadStatus(new DownloadStatus(feed, feed.getHumanReadableIdentifier(), reason, successful, reasonDetailed));
                        return null;
                    }
                    log = DBReader.getFeedDownloadLog(feed);
                    if (log.size() <= 0) {
                    }
                    return Pair.create(request, result);
                }
                reasonDetailed = str;
                successful = successful2;
                result = feedHandlerResult;
                if (successful) {
                    log = DBReader.getFeedDownloadLog(feed);
                    if (log.size() <= 0) {
                    }
                    return Pair.create(request, result);
                }
                downloadRequest = request;
                DownloadService.this.numberOfDownloads.decrementAndGet();
                DownloadService.this.saveDownloadStatus(new DownloadStatus(feed, feed.getHumanReadableIdentifier(), reason, successful, reasonDetailed));
                return null;
            } catch (InvalidFeedException e3) {
                e3.printStackTrace();
                successful2 = false;
                reason = DownloadError.ERROR_PARSER_EXCEPTION;
                str = e3.getMessage();
                reasonDetailed2 = new File(request.getDestination());
                if (reasonDetailed2.exists()) {
                    deleted = reasonDetailed2.delete();
                    str3 = TAG;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Deletion of file '");
                    stringBuilder.append(reasonDetailed2.getAbsolutePath());
                    stringBuilder.append("' ");
                    str2 = deleted ? PodDBAdapter.KEY_SUCCESSFUL : "FAILED";
                    stringBuilder.append(str2);
                    Log.d(str3, stringBuilder.toString());
                    reasonDetailed = str;
                    successful = successful2;
                    result = feedHandlerResult;
                    if (successful) {
                        downloadRequest = request;
                        DownloadService.this.numberOfDownloads.decrementAndGet();
                        DownloadService.this.saveDownloadStatus(new DownloadStatus(feed, feed.getHumanReadableIdentifier(), reason, successful, reasonDetailed));
                        return null;
                    }
                    log = DBReader.getFeedDownloadLog(feed);
                    if (log.size() <= 0) {
                    }
                    return Pair.create(request, result);
                }
                reasonDetailed = str;
                successful = successful2;
                result = feedHandlerResult;
                if (successful) {
                    log = DBReader.getFeedDownloadLog(feed);
                    if (log.size() <= 0) {
                    }
                    return Pair.create(request, result);
                }
                downloadRequest = request;
                DownloadService.this.numberOfDownloads.decrementAndGet();
                DownloadService.this.saveDownloadStatus(new DownloadStatus(feed, feed.getHumanReadableIdentifier(), reason, successful, reasonDetailed));
                return null;
            } catch (Throwable th) {
                downloadRequest = request;
                File feedFile3 = new File(request.getDestination());
                if (feedFile3.exists()) {
                    deleted = feedFile3.delete();
                    StringBuilder stringBuilder3 = new StringBuilder();
                    stringBuilder3.append("Deletion of file '");
                    stringBuilder3.append(feedFile3.getAbsolutePath());
                    stringBuilder3.append("' ");
                    stringBuilder3.append(deleted ? PodDBAdapter.KEY_SUCCESSFUL : "FAILED");
                    Log.d(TAG, stringBuilder3.toString());
                }
            }
        }

        private boolean checkFeedData(Feed feed) {
            if (feed.getTitle() == null) {
                Log.e(TAG, "Feed has no title.");
                return false;
            } else if (hasValidFeedItems(feed)) {
                return true;
            } else {
                Log.e(TAG, "Feed has invalid items");
                return false;
            }
        }

        private boolean hasValidFeedItems(Feed feed) {
            for (FeedItem item : feed.getItems()) {
                if (item.getTitle() == null) {
                    Log.e(TAG, "Item has no title");
                    return false;
                } else if (item.getPubDate() == null) {
                    Log.e(TAG, "Item has no pubDate. Using current time as pubDate");
                    if (item.getTitle() != null) {
                        String str = TAG;
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Title of invalid item: ");
                        stringBuilder.append(item.getTitle());
                        Log.e(str, stringBuilder.toString());
                    }
                    item.setPubDate(new Date());
                }
            }
            return true;
        }

        public void shutdown() {
            this.isActive = false;
            if (this.isCollectingRequests) {
                interrupt();
            }
        }

        void submitCompletedDownload(DownloadRequest request) {
            this.completedRequests.offer(request);
            if (this.isCollectingRequests) {
                interrupt();
            }
        }
    }

    private class LocalBinder extends Binder {
        private LocalBinder() {
        }

        public DownloadService getService() {
            return DownloadService.this;
        }
    }

    private class MediaHandlerThread implements Runnable {
        private final DownloadRequest request;
        private DownloadStatus status;

        public void run() {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:54:0x014b in {3, 8, 9, 13, 19, 24, 28, 29, 37, 38, 39, 41, 43, 44, 48, 49, 51, 53} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
            /*
            r12 = this;
            r0 = r12.request;
            r0 = r0.getFeedfileId();
            r0 = de.danoeh.antennapod.core.storage.DBReader.getFeedMedia(r0);
            if (r0 != 0) goto L_0x0014;
        L_0x000c:
            r1 = "DownloadService";
            r2 = "Could not find downloaded media object in database";
            android.util.Log.e(r1, r2);
            return;
        L_0x0014:
            r1 = 1;
            r0.setDownloaded(r1);
            r2 = r12.request;
            r2 = r2.getDestination();
            r0.setFile_url(r2);
            r0.checkEmbeddedPicture();
            r2 = r0.getItem();
            if (r2 == 0) goto L_0x0038;
        L_0x002a:
            r2 = r0.getItem();
            r2 = r2.hasChapters();
            if (r2 != 0) goto L_0x0038;
        L_0x0034:
            de.danoeh.antennapod.core.util.ChapterUtils.loadChaptersFromFileUrl(r0);
            goto L_0x0039;
        L_0x0039:
            r2 = new android.media.MediaMetadataRetriever;
            r2.<init>();
            r8 = r2;
            r2 = 0;
            r3 = r0.getFile_url();	 Catch:{ NumberFormatException -> 0x0081, Exception -> 0x0073 }
            r8.setDataSource(r3);	 Catch:{ NumberFormatException -> 0x0081, Exception -> 0x0073 }
            r3 = 9;	 Catch:{ NumberFormatException -> 0x0081, Exception -> 0x0073 }
            r3 = r8.extractMetadata(r3);	 Catch:{ NumberFormatException -> 0x0081, Exception -> 0x0073 }
            r2 = r3;	 Catch:{ NumberFormatException -> 0x0081, Exception -> 0x0073 }
            r3 = java.lang.Integer.parseInt(r2);	 Catch:{ NumberFormatException -> 0x0081, Exception -> 0x0073 }
            r0.setDuration(r3);	 Catch:{ NumberFormatException -> 0x0081, Exception -> 0x0073 }
            r3 = "DownloadService";	 Catch:{ NumberFormatException -> 0x0081, Exception -> 0x0073 }
            r4 = new java.lang.StringBuilder;	 Catch:{ NumberFormatException -> 0x0081, Exception -> 0x0073 }
            r4.<init>();	 Catch:{ NumberFormatException -> 0x0081, Exception -> 0x0073 }
            r5 = "Duration of file is ";	 Catch:{ NumberFormatException -> 0x0081, Exception -> 0x0073 }
            r4.append(r5);	 Catch:{ NumberFormatException -> 0x0081, Exception -> 0x0073 }
            r5 = r0.getDuration();	 Catch:{ NumberFormatException -> 0x0081, Exception -> 0x0073 }
            r4.append(r5);	 Catch:{ NumberFormatException -> 0x0081, Exception -> 0x0073 }
            r4 = r4.toString();	 Catch:{ NumberFormatException -> 0x0081, Exception -> 0x0073 }
            android.util.Log.d(r3, r4);	 Catch:{ NumberFormatException -> 0x0081, Exception -> 0x0073 }
            goto L_0x007c;
        L_0x0070:
            r1 = move-exception;
            goto L_0x0147;
        L_0x0073:
            r3 = move-exception;
            r4 = "DownloadService";	 Catch:{ all -> 0x0070 }
            r5 = "Get duration failed";	 Catch:{ all -> 0x0070 }
            android.util.Log.e(r4, r5, r3);	 Catch:{ all -> 0x0070 }
        L_0x007c:
            r8.release();
            r9 = r2;
            goto L_0x009a;
        L_0x0081:
            r3 = move-exception;
            r4 = "DownloadService";	 Catch:{ all -> 0x0070 }
            r5 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0070 }
            r5.<init>();	 Catch:{ all -> 0x0070 }
            r6 = "Invalid file duration: ";	 Catch:{ all -> 0x0070 }
            r5.append(r6);	 Catch:{ all -> 0x0070 }
            r5.append(r2);	 Catch:{ all -> 0x0070 }
            r5 = r5.toString();	 Catch:{ all -> 0x0070 }
            android.util.Log.d(r4, r5);	 Catch:{ all -> 0x0070 }
            goto L_0x007c;
        L_0x009a:
            r10 = r0.getItem();
            r2 = 0;
            if (r10 == 0) goto L_0x00ac;
        L_0x00a1:
            r10.setAutoDownload(r2);	 Catch:{ InterruptedException -> 0x0108, ExecutionException -> 0x00d8 }
            r3 = de.danoeh.antennapod.core.storage.DBWriter.setFeedItem(r10);	 Catch:{ InterruptedException -> 0x0108, ExecutionException -> 0x00d8 }
            r3.get();	 Catch:{ InterruptedException -> 0x0108, ExecutionException -> 0x00d8 }
            goto L_0x00ad;	 Catch:{ InterruptedException -> 0x0108, ExecutionException -> 0x00d8 }
        L_0x00ad:
            r3 = de.danoeh.antennapod.core.storage.DBWriter.setFeedMedia(r0);	 Catch:{ InterruptedException -> 0x0108, ExecutionException -> 0x00d8 }
            r3.get();	 Catch:{ InterruptedException -> 0x0108, ExecutionException -> 0x00d8 }
            if (r10 == 0) goto L_0x00d7;	 Catch:{ InterruptedException -> 0x0108, ExecutionException -> 0x00d8 }
        L_0x00b6:
            r3 = de.danoeh.antennapod.core.preferences.UserPreferences.enqueueDownloadedEpisodes();	 Catch:{ InterruptedException -> 0x0108, ExecutionException -> 0x00d8 }
            if (r3 == 0) goto L_0x00d7;	 Catch:{ InterruptedException -> 0x0108, ExecutionException -> 0x00d8 }
        L_0x00bc:
            r3 = de.danoeh.antennapod.core.service.download.DownloadService.this;	 Catch:{ InterruptedException -> 0x0108, ExecutionException -> 0x00d8 }
            r4 = r10.getId();	 Catch:{ InterruptedException -> 0x0108, ExecutionException -> 0x00d8 }
            r3 = de.danoeh.antennapod.core.storage.DBTasks.isInQueue(r3, r4);	 Catch:{ InterruptedException -> 0x0108, ExecutionException -> 0x00d8 }
            if (r3 != 0) goto L_0x00d6;	 Catch:{ InterruptedException -> 0x0108, ExecutionException -> 0x00d8 }
        L_0x00c8:
            r3 = de.danoeh.antennapod.core.service.download.DownloadService.this;	 Catch:{ InterruptedException -> 0x0108, ExecutionException -> 0x00d8 }
            r1 = new de.danoeh.antennapod.core.feed.FeedItem[r1];	 Catch:{ InterruptedException -> 0x0108, ExecutionException -> 0x00d8 }
            r1[r2] = r10;	 Catch:{ InterruptedException -> 0x0108, ExecutionException -> 0x00d8 }
            r1 = de.danoeh.antennapod.core.storage.DBWriter.addQueueItem(r3, r1);	 Catch:{ InterruptedException -> 0x0108, ExecutionException -> 0x00d8 }
            r1.get();	 Catch:{ InterruptedException -> 0x0108, ExecutionException -> 0x00d8 }
            goto L_0x0110;
        L_0x00d6:
            goto L_0x0110;
        L_0x00d7:
            goto L_0x0110;
        L_0x00d8:
            r1 = move-exception;
            r2 = "DownloadService";
            r3 = new java.lang.StringBuilder;
            r3.<init>();
            r4 = "ExecutionException in MediaHandlerThread: ";
            r3.append(r4);
            r4 = r1.getMessage();
            r3.append(r4);
            r3 = r3.toString();
            android.util.Log.e(r2, r3);
            r11 = new de.danoeh.antennapod.core.service.download.DownloadStatus;
            r4 = r0.getEpisodeTitle();
            r5 = de.danoeh.antennapod.core.util.DownloadError.ERROR_DB_ACCESS_ERROR;
            r6 = 0;
            r7 = r1.getMessage();
            r2 = r11;
            r3 = r0;
            r2.<init>(r3, r4, r5, r6, r7);
            r12.status = r11;
            goto L_0x0111;
        L_0x0108:
            r1 = move-exception;
            r2 = "DownloadService";
            r3 = "MediaHandlerThread was interrupted";
            android.util.Log.e(r2, r3);
        L_0x0111:
            r1 = de.danoeh.antennapod.core.service.download.DownloadService.this;
            r2 = r12.status;
            r1.saveDownloadStatus(r2);
            r1 = de.danoeh.antennapod.core.preferences.GpodnetPreferences.loggedIn();
            if (r1 == 0) goto L_0x0137;
        L_0x011e:
            if (r10 == 0) goto L_0x0137;
        L_0x0120:
            r1 = new de.danoeh.antennapod.core.gpoddernet.model.GpodnetEpisodeAction$Builder;
            r2 = de.danoeh.antennapod.core.gpoddernet.model.GpodnetEpisodeAction.Action.DOWNLOAD;
            r1.<init>(r10, r2);
            r1 = r1.currentDeviceId();
            r1 = r1.currentTimestamp();
            r1 = r1.build();
            de.danoeh.antennapod.core.preferences.GpodnetPreferences.enqueueEpisodeAction(r1);
            goto L_0x0138;
        L_0x0138:
            r1 = de.danoeh.antennapod.core.service.download.DownloadService.this;
            r1 = r1.numberOfDownloads;
            r1.decrementAndGet();
            r1 = de.danoeh.antennapod.core.service.download.DownloadService.this;
            r1.queryDownloadsAsync();
            return;
        L_0x0147:
            r8.release();
            throw r1;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: de.danoeh.antennapod.core.service.download.DownloadService.MediaHandlerThread.run():void");
        }

        MediaHandlerThread(@NonNull DownloadStatus status, @NonNull DownloadRequest request) {
            this.status = status;
            this.request = request;
        }
    }

    private class NotificationUpdater implements Runnable {
        private NotificationUpdater() {
        }

        public void run() {
            DownloadService.this.handler.post(new C0746xe3e1dea5());
        }

        public static /* synthetic */ void lambda$run$0(NotificationUpdater notificationUpdater) {
            Notification n = DownloadService.this.updateNotifications();
            if (n != null) {
                ((NotificationManager) DownloadService.this.getSystemService("notification")).notify(2, n);
            }
        }
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getParcelableExtra("request") != null) {
            onDownloadQueued(intent);
        } else if (this.numberOfDownloads.get() == 0) {
            stopSelf();
        }
        return 2;
    }

    public void onCreate() {
        Log.d(TAG, "Service started");
        isRunning = true;
        this.handler = new Handler();
        this.reportQueue = Collections.synchronizedList(new ArrayList());
        this.downloads = Collections.synchronizedList(new ArrayList());
        this.numberOfDownloads = new AtomicInteger(0);
        IntentFilter cancelDownloadReceiverFilter = new IntentFilter();
        cancelDownloadReceiverFilter.addAction(ACTION_CANCEL_ALL_DOWNLOADS);
        cancelDownloadReceiverFilter.addAction(ACTION_CANCEL_DOWNLOAD);
        registerReceiver(this.cancelDownloadReceiver, cancelDownloadReceiverFilter);
        this.syncExecutor = Executors.newSingleThreadExecutor(-$$Lambda$DownloadService$tz7yEQIu3iJxCJX3QuhbO0OuPGk.INSTANCE);
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("parallel downloads: ");
        stringBuilder.append(UserPreferences.getParallelDownloads());
        Log.d(str, stringBuilder.toString());
        this.downloadExecutor = new ExecutorCompletionService(Executors.newFixedThreadPool(UserPreferences.getParallelDownloads(), -$$Lambda$DownloadService$pl2GseLBavFoV_kmkDm43MxRZqM.INSTANCE));
        this.schedExecutor = new ScheduledThreadPoolExecutor(1, -$$Lambda$DownloadService$s9x7FQ3YeZtzrwlH5OZ5glsDOKA.INSTANCE, -$$Lambda$DownloadService$3JPOU4jjuMTZ0qznfNOPLvwSItk.INSTANCE);
        this.downloadCompletionThread.start();
        this.feedSyncThread = new FeedSyncThread();
        this.feedSyncThread.start();
        setupNotificationBuilders();
        this.requester = DownloadRequester.getInstance();
        startForeground(2, updateNotifications());
    }

    static /* synthetic */ Thread lambda$onCreate$0(Runnable r) {
        Thread t = new Thread(r);
        t.setPriority(1);
        return t;
    }

    static /* synthetic */ Thread lambda$onCreate$1(Runnable r) {
        Thread t = new Thread(r);
        t.setPriority(1);
        return t;
    }

    static /* synthetic */ Thread lambda$onCreate$2(Runnable r) {
        Thread t = new Thread(r);
        t.setPriority(1);
        return t;
    }

    public IBinder onBind(Intent intent) {
        return this.mBinder;
    }

    public void onDestroy() {
        Log.d(TAG, "Service shutting down");
        isRunning = false;
        if (ClientConfig.downloadServiceCallbacks.shouldCreateReport()) {
            if (UserPreferences.showDownloadReport()) {
                updateReport();
            }
        }
        this.postHandler.removeCallbacks(this.postDownloaderTask);
        EventBus.getDefault().postSticky(DownloadEvent.refresh(Collections.emptyList()));
        stopForeground(true);
        ((NotificationManager) getSystemService("notification")).cancel(2);
        this.downloadCompletionThread.interrupt();
        this.syncExecutor.shutdown();
        this.schedExecutor.shutdown();
        this.feedSyncThread.shutdown();
        cancelNotificationUpdater();
        unregisterReceiver(this.cancelDownloadReceiver);
        if (GpodnetPreferences.loggedIn()) {
            if (GpodnetPreferences.getLastSubscriptionSyncTimestamp() > 0) {
                if (GpodnetPreferences.getLastEpisodeActionsSyncTimestamp() == 0) {
                    GpodnetSyncService.sendSyncActionsIntent(this);
                }
            }
        }
        DBTasks.autodownloadUndownloadedItems(getApplicationContext());
    }

    private void setupNotificationBuilders() {
        this.notificationCompatBuilder = new Builder(this, NotificationUtils.CHANNEL_ID_DOWNLOADING).setOngoing(true).setContentIntent(ClientConfig.downloadServiceCallbacks.getNotificationContentIntent(this)).setSmallIcon(C0734R.drawable.stat_notify_sync);
        if (VERSION.SDK_INT >= 21) {
            this.notificationCompatBuilder.setVisibility(1);
        }
        Log.d(TAG, "Notification set up");
    }

    private Notification updateNotifications() {
        if (this.notificationCompatBuilder == null) {
            return null;
        }
        String downloadsLeft;
        String contentTitle = getString(C0734R.string.download_notification_title);
        int numDownloads = this.requester.getNumberOfDownloads();
        if (numDownloads > 0) {
            downloadsLeft = getResources().getQuantityString(C0734R.plurals.downloads_left, numDownloads, new Object[]{Integer.valueOf(numDownloads)});
        } else {
            downloadsLeft = getString(C0734R.string.downloads_processing);
        }
        String bigText = compileNotificationString(this.downloads);
        this.notificationCompatBuilder.setContentTitle(contentTitle);
        this.notificationCompatBuilder.setContentText(downloadsLeft);
        this.notificationCompatBuilder.setStyle(new BigTextStyle().bigText(bigText));
        return this.notificationCompatBuilder.build();
    }

    private Downloader getDownloader(String downloadUrl) {
        for (Downloader downloader : this.downloads) {
            if (downloader.getDownloadRequest().getSource().equals(downloadUrl)) {
                return downloader;
            }
        }
        return null;
    }

    private void onDownloadQueued(Intent intent) {
        Log.d(TAG, "Received enqueue request");
        DownloadRequest request = (DownloadRequest) intent.getParcelableExtra("request");
        if (request != null) {
            writeFileUrl(request);
            Downloader downloader = getDownloader(request);
            if (downloader != null) {
                this.numberOfDownloads.incrementAndGet();
                if (request.getFeedfileType() == 0) {
                    this.downloads.add(0, downloader);
                } else {
                    this.downloads.add(downloader);
                }
                this.downloadExecutor.submit(downloader);
                postDownloaders();
            }
            queryDownloads();
            return;
        }
        throw new IllegalArgumentException("ACTION_ENQUEUE_DOWNLOAD intent needs request extra");
    }

    private Downloader getDownloader(DownloadRequest request) {
        if (URLUtil.isHttpUrl(request.getSource()) || URLUtil.isHttpsUrl(request.getSource())) {
            return new HttpDownloader(request);
        }
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Could not find appropriate downloader for ");
        stringBuilder.append(request.getSource());
        Log.e(str, stringBuilder.toString());
        return null;
    }

    private void removeDownload(Downloader d) {
        this.handler.post(new -$$Lambda$DownloadService$hYVgizZTfUdy4JbfFXsZ4HoN5M0(this, d));
    }

    public static /* synthetic */ void lambda$removeDownload$4(DownloadService downloadService, Downloader d) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Removing downloader: ");
        stringBuilder.append(d.getDownloadRequest().getSource());
        Log.d(str, stringBuilder.toString());
        boolean rc = downloadService.downloads.remove(d);
        String str2 = TAG;
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("Result of downloads.remove: ");
        stringBuilder2.append(rc);
        Log.d(str2, stringBuilder2.toString());
        DownloadRequester.getInstance().removeDownload(d.getDownloadRequest());
        downloadService.postDownloaders();
    }

    private void saveDownloadStatus(DownloadStatus status) {
        this.reportQueue.add(status);
        DBWriter.addDownloadStatus(status);
    }

    private void updateReport() {
        boolean createReport = false;
        int successfulDownloads = 0;
        int failedDownloads = 0;
        for (DownloadStatus status : this.reportQueue) {
            if (status.isSuccessful()) {
                successfulDownloads++;
            } else if (!status.isCancelled()) {
                createReport = true;
                failedDownloads++;
            }
        }
        if (createReport) {
            Log.d(TAG, "Creating report");
            Builder builder = new Builder(this, "error").setTicker(getString(C0734R.string.download_report_title)).setContentTitle(getString(C0734R.string.download_report_content_title)).setContentText(String.format(getString(C0734R.string.download_report_content), new Object[]{Integer.valueOf(successfulDownloads), Integer.valueOf(failedDownloads)})).setSmallIcon(C0734R.drawable.stat_notify_sync_error).setContentIntent(ClientConfig.downloadServiceCallbacks.getReportNotificationContentIntent(this)).setAutoCancel(true);
            if (VERSION.SDK_INT >= 21) {
                builder.setVisibility(1);
            }
            ((NotificationManager) getSystemService("notification")).notify(3, builder.build());
        } else {
            Log.d(TAG, "No report is created");
        }
        this.reportQueue.clear();
    }

    private void queryDownloadsAsync() {
        this.handler.post(new -$$Lambda$DownloadService$-Ouk4A5B9WbWuMaWU-6JBHD_7nQ());
    }

    private void queryDownloads() {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.numberOfDownloads.get());
        stringBuilder.append(" downloads left");
        Log.d(str, stringBuilder.toString());
        if (this.numberOfDownloads.get() > 0 || !DownloadRequester.getInstance().hasNoDownloads()) {
            setupNotificationUpdater();
            startForeground(2, updateNotifications());
            return;
        }
        str = TAG;
        stringBuilder = new StringBuilder();
        stringBuilder.append("Number of downloads is ");
        stringBuilder.append(this.numberOfDownloads.get());
        stringBuilder.append(", attempting shutdown");
        Log.d(str, stringBuilder.toString());
        stopSelf();
    }

    private void postAuthenticationNotification(DownloadRequest downloadRequest) {
        this.handler.post(new -$$Lambda$DownloadService$vRnSiOcE-0YVy-hsmK2vfPwJcVY(this, downloadRequest));
    }

    public static /* synthetic */ void lambda$postAuthenticationNotification$5(DownloadService downloadService, DownloadRequest downloadRequest) {
        String resourceTitle = downloadRequest.getTitle() != null ? downloadRequest.getTitle() : downloadRequest.getSource();
        Builder builder = new Builder(downloadService, NotificationUtils.CHANNEL_ID_USER_ACTION);
        Builder contentText = builder.setTicker(downloadService.getText(C0734R.string.authentication_notification_title)).setContentTitle(downloadService.getText(C0734R.string.authentication_notification_title)).setContentText(downloadService.getText(C0734R.string.authentication_notification_msg));
        BigTextStyle bigTextStyle = new BigTextStyle();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(downloadService.getText(C0734R.string.authentication_notification_msg));
        stringBuilder.append(": ");
        stringBuilder.append(resourceTitle);
        contentText.setStyle(bigTextStyle.bigText(stringBuilder.toString())).setSmallIcon(C0734R.drawable.ic_stat_authentication).setAutoCancel(true).setContentIntent(ClientConfig.downloadServiceCallbacks.getAuthentificationNotificationContentIntent(downloadService, downloadRequest));
        if (VERSION.SDK_INT >= 21) {
            builder.setVisibility(1);
        }
        ((NotificationManager) downloadService.getSystemService("notification")).notify(downloadRequest.getSource().hashCode(), builder.build());
    }

    private void handleCompletedFeedDownload(DownloadRequest request) {
        Log.d(TAG, "Handling completed Feed Download");
        this.feedSyncThread.submitCompletedDownload(request);
    }

    private void handleCompletedFeedMediaDownload(DownloadStatus status, DownloadRequest request) {
        Log.d(TAG, "Handling completed FeedMedia Download");
        this.syncExecutor.execute(new MediaHandlerThread(status, request));
    }

    private void handleFailedDownload(DownloadStatus status, DownloadRequest request) {
        Log.d(TAG, "Handling failed download");
        this.syncExecutor.execute(new FailedDownloadHandler(status, request));
    }

    private void writeFileUrl(DownloadRequest request) {
        if (request.getFeedfileType() == 2) {
            File dest = new File(request.getDestination());
            if (!dest.exists()) {
                try {
                    dest.createNewFile();
                } catch (IOException e) {
                    Log.e(TAG, "Unable to create file");
                }
            }
            if (dest.exists()) {
                Log.d(TAG, "Writing file url");
                FeedMedia media = DBReader.getFeedMedia(request.getFeedfileId());
                if (media == null) {
                    Log.d(TAG, "No media");
                    return;
                }
                media.setFile_url(request.getDestination());
                try {
                    DBWriter.setFeedMedia(media).get();
                } catch (InterruptedException e2) {
                    Log.e(TAG, "writeFileUrl was interrupted");
                } catch (ExecutionException e3) {
                    String str = TAG;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("ExecutionException in writeFileUrl: ");
                    stringBuilder.append(e3.getMessage());
                    Log.e(str, stringBuilder.toString());
                }
            }
        }
    }

    private void setupNotificationUpdater() {
        Log.d(TAG, "Setting up notification updater");
        if (this.notificationUpdater == null) {
            this.notificationUpdater = new NotificationUpdater();
            this.notificationUpdaterFuture = this.schedExecutor.scheduleAtFixedRate(this.notificationUpdater, 5, 5, TimeUnit.SECONDS);
        }
    }

    private void cancelNotificationUpdater() {
        boolean result = false;
        ScheduledFuture scheduledFuture = this.notificationUpdaterFuture;
        if (scheduledFuture != null) {
            result = scheduledFuture.cancel(true);
        }
        this.notificationUpdater = null;
        this.notificationUpdaterFuture = null;
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("NotificationUpdater cancelled. Result: ");
        stringBuilder.append(result);
        Log.d(str, stringBuilder.toString());
    }

    private void postDownloaders() {
        long now = System.currentTimeMillis();
        if (now - this.lastPost >= 250) {
            this.postHandler.removeCallbacks(this.postDownloaderTask);
            this.postDownloaderTask.run();
            this.lastPost = now;
        }
    }

    private static String compileNotificationString(List<Downloader> downloads) {
        List<String> lines = new ArrayList(downloads.size());
        for (Downloader downloader : downloads) {
            StringBuilder line = new StringBuilder("• ");
            DownloadRequest request = downloader.getDownloadRequest();
            int feedfileType = request.getFeedfileType();
            if (feedfileType != 0) {
                if (feedfileType != 2) {
                    line.append("Unknown: ");
                    line.append(request.getFeedfileType());
                } else if (request.getTitle() != null) {
                    line.append(request.getTitle());
                    line.append(" (");
                    line.append(request.getProgressPercent());
                    line.append("%)");
                }
            } else if (request.getTitle() != null) {
                line.append(request.getTitle());
            }
            lines.add(line.toString());
        }
        return TextUtils.join("\n", lines);
    }
}
