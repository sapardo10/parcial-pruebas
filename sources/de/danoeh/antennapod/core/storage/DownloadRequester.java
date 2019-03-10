package de.danoeh.antennapod.core.storage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.URLUtil;
import de.danoeh.antennapod.core.BuildConfig;
import de.danoeh.antennapod.core.feed.Feed;
import de.danoeh.antennapod.core.feed.FeedFile;
import de.danoeh.antennapod.core.feed.FeedMedia;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.service.download.DownloadRequest;
import de.danoeh.antennapod.core.service.download.DownloadRequest$Builder;
import de.danoeh.antennapod.core.service.download.DownloadService;
import de.danoeh.antennapod.core.util.FileNameGenerator;
import de.danoeh.antennapod.core.util.IntentUtils;
import de.danoeh.antennapod.core.util.URLChecker;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.io.FilenameUtils;

public class DownloadRequester {
    private static final String FEED_DOWNLOADPATH = "cache/";
    public static final String IMAGE_DOWNLOADPATH = "images/";
    private static final String MEDIA_DOWNLOADPATH = "media/";
    public static final String REQUEST_ARG_LOAD_ALL_PAGES = "loadAllPages";
    public static final String REQUEST_ARG_PAGE_NR = "page";
    private static final String TAG = "DownloadRequester";
    private static DownloadRequester downloader;
    private final Map<String, DownloadRequest> downloads = new ConcurrentHashMap();

    public synchronized boolean isDownloadingFeeds() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:17:0x0028 in {9, 10, 13, 16} preds:[]
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
        monitor-enter(r3);
        r0 = r3.downloads;	 Catch:{ all -> 0x0025 }
        r0 = r0.values();	 Catch:{ all -> 0x0025 }
        r0 = r0.iterator();	 Catch:{ all -> 0x0025 }
    L_0x000b:
        r1 = r0.hasNext();	 Catch:{ all -> 0x0025 }
        if (r1 == 0) goto L_0x0022;	 Catch:{ all -> 0x0025 }
    L_0x0011:
        r1 = r0.next();	 Catch:{ all -> 0x0025 }
        r1 = (de.danoeh.antennapod.core.service.download.DownloadRequest) r1;	 Catch:{ all -> 0x0025 }
        r2 = r1.getFeedfileType();	 Catch:{ all -> 0x0025 }
        if (r2 != 0) goto L_0x0020;
    L_0x001d:
        r0 = 1;
        monitor-exit(r3);
        return r0;
        goto L_0x000b;
    L_0x0022:
        r0 = 0;
        monitor-exit(r3);
        return r0;
    L_0x0025:
        r0 = move-exception;
        monitor-exit(r3);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: de.danoeh.antennapod.core.storage.DownloadRequester.isDownloadingFeeds():boolean");
    }

    private DownloadRequester() {
    }

    public static synchronized DownloadRequester getInstance() {
        DownloadRequester downloadRequester;
        synchronized (DownloadRequester.class) {
            if (downloader == null) {
                downloader = new DownloadRequester();
            }
            downloadRequester = downloader;
        }
        return downloadRequester;
    }

    public synchronized boolean download(@NonNull Context context, @NonNull DownloadRequest request) {
        if (this.downloads.containsKey(request.getSource())) {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "DownloadRequest is already stored.");
            }
            return false;
        }
        this.downloads.put(request.getSource(), request);
        Intent launchIntent = new Intent(context, DownloadService.class);
        launchIntent.putExtra("request", request);
        ContextCompat.startForegroundService(context, launchIntent);
        return true;
    }

    private void download(Context context, FeedFile item, FeedFile container, File dest, boolean overwriteIfExists, String username, String password, String lastModified, boolean deleteOnFailure, Bundle arguments) {
        DownloadRequester downloadRequester = this;
        FeedFile feedFile = item;
        boolean partiallyDownloadedFileExists = item.getFile_url() != null && new File(item.getFile_url()).exists();
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("partiallyDownloadedFileExists: ");
        stringBuilder.append(partiallyDownloadedFileExists);
        Log.d(str, stringBuilder.toString());
        if (isDownloadingFile(item)) {
            str = TAG;
            stringBuilder = new StringBuilder();
            stringBuilder.append("URL ");
            stringBuilder.append(item.getDownload_url());
            stringBuilder.append(" is already being downloaded");
            Log.e(str, stringBuilder.toString());
            return;
        }
        if (isFilenameAvailable(dest.toString())) {
            if (partiallyDownloadedFileExists || !dest.exists()) {
                File dest2 = dest;
                str = TAG;
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Requesting download of url ");
                stringBuilder2.append(item.getDownload_url());
                Log.d(str, stringBuilder2.toString());
                item.setDownload_url(URLChecker.prepareURL(item.getDownload_url(), container == null ? container.getDownload_url() : null));
                Context context2 = context;
                download(context, new DownloadRequest$Builder(dest2.toString(), item).withAuthentication(username, password).lastModified(lastModified).deleteOnFailure(deleteOnFailure).withArguments(arguments).build());
            }
        }
        Log.d(TAG, "Filename already used.");
        if (isFilenameAvailable(dest.toString()) && overwriteIfExists) {
            boolean result = dest.delete();
            String str2 = TAG;
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Deleting file. Result: ");
            stringBuilder2.append(result);
            Log.d(str2, stringBuilder2.toString());
            File dest22 = dest;
            str = TAG;
            StringBuilder stringBuilder22 = new StringBuilder();
            stringBuilder22.append("Requesting download of url ");
            stringBuilder22.append(item.getDownload_url());
            Log.d(str, stringBuilder22.toString());
            if (container == null) {
            }
            item.setDownload_url(URLChecker.prepareURL(item.getDownload_url(), container == null ? container.getDownload_url() : null));
            Context context22 = context;
            download(context, new DownloadRequest$Builder(dest22.toString(), item).withAuthentication(username, password).lastModified(lastModified).deleteOnFailure(deleteOnFailure).withArguments(arguments).build());
        }
        File newDest = null;
        for (int i = 1; i < Integer.MAX_VALUE; i++) {
            String newName = new StringBuilder();
            newName.append(FilenameUtils.getBaseName(dest.getName()));
            newName.append("-");
            newName.append(i);
            newName.append('.');
            newName.append(FilenameUtils.getExtension(dest.getName()));
            newName = newName.toString();
            String str3 = TAG;
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append("Testing filename ");
            stringBuilder3.append(newName);
            Log.d(str3, stringBuilder3.toString());
            newDest = new File(dest.getParent(), newName);
            if (!newDest.exists()) {
                if (isFilenameAvailable(newDest.toString())) {
                    str3 = TAG;
                    stringBuilder3 = new StringBuilder();
                    stringBuilder3.append("File doesn't exist yet. Using ");
                    stringBuilder3.append(newName);
                    Log.d(str3, stringBuilder3.toString());
                    break;
                }
            }
        }
        if (newDest != null) {
            dest22 = newDest;
            str = TAG;
            StringBuilder stringBuilder222 = new StringBuilder();
            stringBuilder222.append("Requesting download of url ");
            stringBuilder222.append(item.getDownload_url());
            Log.d(str, stringBuilder222.toString());
            if (container == null) {
            }
            item.setDownload_url(URLChecker.prepareURL(item.getDownload_url(), container == null ? container.getDownload_url() : null));
            Context context222 = context;
            download(context, new DownloadRequest$Builder(dest22.toString(), item).withAuthentication(username, password).lastModified(lastModified).deleteOnFailure(deleteOnFailure).withArguments(arguments).build());
        }
        File dest222 = dest;
        str = TAG;
        StringBuilder stringBuilder2222 = new StringBuilder();
        stringBuilder2222.append("Requesting download of url ");
        stringBuilder2222.append(item.getDownload_url());
        Log.d(str, stringBuilder2222.toString());
        if (container == null) {
        }
        item.setDownload_url(URLChecker.prepareURL(item.getDownload_url(), container == null ? container.getDownload_url() : null));
        Context context2222 = context;
        download(context, new DownloadRequest$Builder(dest222.toString(), item).withAuthentication(username, password).lastModified(lastModified).deleteOnFailure(deleteOnFailure).withArguments(arguments).build());
    }

    private boolean isFilenameAvailable(String path) {
        for (String key : this.downloads.keySet()) {
            if (TextUtils.equals(((DownloadRequest) this.downloads.get(key)).getDestination(), path)) {
                if (BuildConfig.DEBUG) {
                    String str = TAG;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(path);
                    stringBuilder.append(" is already used by another requested download");
                    Log.d(str, stringBuilder.toString());
                }
                return false;
            }
        }
        if (BuildConfig.DEBUG) {
            str = TAG;
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(path);
            stringBuilder2.append(" is available as a download destination");
            Log.d(str, stringBuilder2.toString());
        }
        return true;
    }

    public synchronized void downloadFeed(Context context, Feed feed, boolean loadAllPages, boolean force) throws DownloadRequestException {
        DownloadRequester downloadRequester = this;
        FeedFile feedFile = feed;
        synchronized (this) {
            if (feedFileValid(feedFile)) {
                String lastModified;
                Bundle args;
                String username = feed.getPreferences() != null ? feed.getPreferences().getUsername() : null;
                String password = feed.getPreferences() != null ? feed.getPreferences().getPassword() : null;
                if (!feed.isPaged()) {
                    if (!force) {
                        lastModified = feed.getLastUpdate();
                        args = new Bundle();
                        args.putInt(REQUEST_ARG_PAGE_NR, feed.getPageNr());
                        args.putBoolean(REQUEST_ARG_LOAD_ALL_PAGES, loadAllPages);
                        download(context, feed, null, new File(getFeedfilePath(), getFeedfileName(feedFile)), true, username, password, lastModified, true, args);
                    }
                }
                lastModified = null;
                args = new Bundle();
                args.putInt(REQUEST_ARG_PAGE_NR, feed.getPageNr());
                args.putBoolean(REQUEST_ARG_LOAD_ALL_PAGES, loadAllPages);
                download(context, feed, null, new File(getFeedfilePath(), getFeedfileName(feedFile)), true, username, password, lastModified, true, args);
            } else {
                boolean z = loadAllPages;
            }
        }
    }

    public synchronized void downloadFeed(Context context, Feed feed) throws DownloadRequestException {
        downloadFeed(context, feed, false, false);
    }

    public synchronized void downloadMedia(Context context, FeedMedia feedmedia) throws DownloadRequestException {
        DownloadRequester downloadRequester = this;
        FeedFile feedFile = feedmedia;
        synchronized (this) {
            if (feedFileValid(feedFile)) {
                String username;
                String password;
                File dest;
                FeedFile feed = feedmedia.getItem().getFeed();
                if (feed == null || feed.getPreferences() == null) {
                    username = null;
                    password = null;
                } else {
                    username = feed.getPreferences().getUsername();
                    password = feed.getPreferences().getPassword();
                }
                if (feedmedia.getFile_url() != null) {
                    dest = new File(feedmedia.getFile_url());
                } else {
                    dest = new File(getMediafilePath(feedFile), getMediafilename(feedFile));
                }
                download(context, feedmedia, feed, dest, false, username, password, null, false, null);
            }
        }
    }

    private boolean feedFileValid(FeedFile f) throws DownloadRequestException {
        if (f == null) {
            throw new DownloadRequestException("Feedfile was null");
        } else if (f.getDownload_url() != null) {
            return true;
        } else {
            throw new DownloadRequestException("File has no download URL");
        }
    }

    public synchronized void cancelDownload(Context context, FeedFile f) {
        cancelDownload(context, f.getDownload_url());
    }

    public synchronized void cancelDownload(Context context, String downloadUrl) {
        if (BuildConfig.DEBUG) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Cancelling download with url ");
            stringBuilder.append(downloadUrl);
            Log.d(str, stringBuilder.toString());
        }
        Intent cancelIntent = new Intent(DownloadService.ACTION_CANCEL_DOWNLOAD);
        cancelIntent.putExtra(DownloadService.EXTRA_DOWNLOAD_URL, downloadUrl);
        cancelIntent.setPackage(context.getPackageName());
        context.sendBroadcast(cancelIntent);
    }

    public synchronized void cancelAllDownloads(Context context) {
        Log.d(TAG, "Cancelling all running downloads");
        IntentUtils.sendLocalBroadcast(context, DownloadService.ACTION_CANCEL_ALL_DOWNLOADS);
    }

    public synchronized boolean isDownloadingFile(FeedFile item) {
        boolean z;
        z = item.getDownload_url() != null && this.downloads.containsKey(item.getDownload_url());
        return z;
    }

    public synchronized DownloadRequest getDownload(String downloadUrl) {
        return (DownloadRequest) this.downloads.get(downloadUrl);
    }

    public synchronized boolean isDownloadingFile(String downloadUrl) {
        return this.downloads.get(downloadUrl) != null;
    }

    public synchronized boolean hasNoDownloads() {
        return this.downloads.isEmpty();
    }

    public synchronized void removeDownload(DownloadRequest r) {
        if (this.downloads.remove(r.getSource()) == null) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Could not remove object with url ");
            stringBuilder.append(r.getSource());
            Log.e(str, stringBuilder.toString());
        }
    }

    public synchronized int getNumberOfDownloads() {
        return this.downloads.size();
    }

    private synchronized String getFeedfilePath() throws DownloadRequestException {
        StringBuilder stringBuilder;
        stringBuilder = new StringBuilder();
        stringBuilder.append(getExternalFilesDirOrThrowException(FEED_DOWNLOADPATH).toString());
        stringBuilder.append("/");
        return stringBuilder.toString();
    }

    private synchronized String getFeedfileName(Feed feed) {
        StringBuilder stringBuilder;
        String filename = feed.getDownload_url();
        if (feed.getTitle() != null && !feed.getTitle().isEmpty()) {
            filename = feed.getTitle();
        }
        stringBuilder = new StringBuilder();
        stringBuilder.append("feed-");
        stringBuilder.append(FileNameGenerator.generateFileName(filename));
        return stringBuilder.toString();
    }

    private synchronized String getMediafilePath(FeedMedia media) throws DownloadRequestException {
        StringBuilder stringBuilder;
        stringBuilder = new StringBuilder();
        stringBuilder.append(MEDIA_DOWNLOADPATH);
        stringBuilder.append(FileNameGenerator.generateFileName(media.getItem().getFeed().getTitle()));
        stringBuilder.append("/");
        return getExternalFilesDirOrThrowException(stringBuilder.toString()).toString();
    }

    private File getExternalFilesDirOrThrowException(String type) throws DownloadRequestException {
        File result = UserPreferences.getDataFolder(type);
        if (result != null) {
            return result;
        }
        throw new DownloadRequestException("Failed to access external storage");
    }

    private String getMediafilename(FeedMedia media) {
        String titleBaseFilename = "";
        if (media.getItem() != null && media.getItem().getTitle() != null) {
            titleBaseFilename = FileNameGenerator.generateFileName(media.getItem().getTitle());
        }
        String URLBaseFilename = URLUtil.guessFileName(media.getDownload_url(), null, media.getMime_type());
        if (titleBaseFilename.equals("")) {
            return URLBaseFilename;
        }
        if (titleBaseFilename.length() > 220) {
            titleBaseFilename = titleBaseFilename.substring(0, 220);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(titleBaseFilename);
        stringBuilder.append('.');
        stringBuilder.append(FilenameUtils.getExtension(URLBaseFilename));
        return stringBuilder.toString();
    }
}
