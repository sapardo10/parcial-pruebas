package com.google.android.exoplayer2.offline;

import android.net.Uri;
import android.support.annotation.NonNull;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheUtil;
import com.google.android.exoplayer2.upstream.cache.CacheUtil.CachingCounters;
import com.google.android.exoplayer2.util.PriorityTaskManager;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class SegmentDownloader<M extends FilterableManifest<M>> implements Downloader {
    private static final int BUFFER_SIZE_BYTES = 131072;
    private final Cache cache;
    private final CacheDataSource dataSource;
    private volatile long downloadedBytes;
    private volatile int downloadedSegments;
    private final AtomicBoolean isCanceled = new AtomicBoolean();
    private final Uri manifestUri;
    private final CacheDataSource offlineDataSource;
    private final PriorityTaskManager priorityTaskManager;
    private final ArrayList<StreamKey> streamKeys;
    private volatile int totalSegments = -1;

    protected static class Segment implements Comparable<Segment> {
        public final DataSpec dataSpec;
        public final long startTimeUs;

        public Segment(long startTimeUs, DataSpec dataSpec) {
            this.startTimeUs = startTimeUs;
            this.dataSpec = dataSpec;
        }

        public int compareTo(@NonNull Segment other) {
            return Util.compareLong(this.startTimeUs, other.startTimeUs);
        }
    }

    public final void download() throws java.io.IOException, java.lang.InterruptedException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:18:0x0060 in {9, 12, 14, 17} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r13 = this;
        r0 = r13.priorityTaskManager;
        r1 = -1000; // 0xfffffffffffffc18 float:NaN double:NaN;
        r0.add(r1);
        r0 = r13.initDownload();	 Catch:{ all -> 0x0059 }
        java.util.Collections.sort(r0);	 Catch:{ all -> 0x0059 }
        r2 = 131072; // 0x20000 float:1.83671E-40 double:6.47582E-319;	 Catch:{ all -> 0x0059 }
        r6 = new byte[r2];	 Catch:{ all -> 0x0059 }
        r2 = new com.google.android.exoplayer2.upstream.cache.CacheUtil$CachingCounters;	 Catch:{ all -> 0x0059 }
        r2.<init>();	 Catch:{ all -> 0x0059 }
        r3 = 0;	 Catch:{ all -> 0x0059 }
        r12 = r3;	 Catch:{ all -> 0x0059 }
    L_0x0019:
        r3 = r0.size();	 Catch:{ all -> 0x0059 }
        if (r12 >= r3) goto L_0x0051;
        r3 = r0.get(r12);	 Catch:{ all -> 0x0048 }
        r3 = (com.google.android.exoplayer2.offline.SegmentDownloader.Segment) r3;	 Catch:{ all -> 0x0048 }
        r3 = r3.dataSpec;	 Catch:{ all -> 0x0048 }
        r4 = r13.cache;	 Catch:{ all -> 0x0048 }
        r5 = r13.dataSource;	 Catch:{ all -> 0x0048 }
        r7 = r13.priorityTaskManager;	 Catch:{ all -> 0x0048 }
        r8 = -1000; // 0xfffffffffffffc18 float:NaN double:NaN;	 Catch:{ all -> 0x0048 }
        r10 = r13.isCanceled;	 Catch:{ all -> 0x0048 }
        r11 = 1;	 Catch:{ all -> 0x0048 }
        r9 = r2;	 Catch:{ all -> 0x0048 }
        com.google.android.exoplayer2.upstream.cache.CacheUtil.cache(r3, r4, r5, r6, r7, r8, r9, r10, r11);	 Catch:{ all -> 0x0048 }
        r3 = r13.downloadedSegments;	 Catch:{ all -> 0x0048 }
        r3 = r3 + 1;	 Catch:{ all -> 0x0048 }
        r13.downloadedSegments = r3;	 Catch:{ all -> 0x0048 }
        r3 = r13.downloadedBytes;	 Catch:{ all -> 0x0059 }
        r7 = r2.newlyCachedBytes;	 Catch:{ all -> 0x0059 }
        r3 = r3 + r7;	 Catch:{ all -> 0x0059 }
        r13.downloadedBytes = r3;	 Catch:{ all -> 0x0059 }
        r12 = r12 + 1;	 Catch:{ all -> 0x0059 }
        goto L_0x0019;	 Catch:{ all -> 0x0059 }
    L_0x0048:
        r3 = move-exception;	 Catch:{ all -> 0x0059 }
        r4 = r13.downloadedBytes;	 Catch:{ all -> 0x0059 }
        r7 = r2.newlyCachedBytes;	 Catch:{ all -> 0x0059 }
        r4 = r4 + r7;	 Catch:{ all -> 0x0059 }
        r13.downloadedBytes = r4;	 Catch:{ all -> 0x0059 }
        throw r3;	 Catch:{ all -> 0x0059 }
        r0 = r13.priorityTaskManager;
        r0.remove(r1);
        return;
    L_0x0059:
        r0 = move-exception;
        r2 = r13.priorityTaskManager;
        r2.remove(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.offline.SegmentDownloader.download():void");
    }

    protected abstract M getManifest(DataSource dataSource, Uri uri) throws IOException;

    protected abstract List<Segment> getSegments(DataSource dataSource, M m, boolean z) throws InterruptedException, IOException;

    public SegmentDownloader(Uri manifestUri, List<StreamKey> streamKeys, DownloaderConstructorHelper constructorHelper) {
        this.manifestUri = manifestUri;
        this.streamKeys = new ArrayList(streamKeys);
        this.cache = constructorHelper.getCache();
        this.dataSource = constructorHelper.buildCacheDataSource(false);
        this.offlineDataSource = constructorHelper.buildCacheDataSource(true);
        this.priorityTaskManager = constructorHelper.getPriorityTaskManager();
    }

    public void cancel() {
        this.isCanceled.set(true);
    }

    public final long getDownloadedBytes() {
        return this.downloadedBytes;
    }

    public final float getDownloadPercentage() {
        int totalSegments = this.totalSegments;
        int downloadedSegments = this.downloadedSegments;
        if (totalSegments != -1) {
            if (downloadedSegments != -1) {
                float f = 100.0f;
                if (totalSegments != 0) {
                    f = (((float) downloadedSegments) * 100.0f) / ((float) totalSegments);
                }
                return f;
            }
        }
        return -1.0f;
    }

    public final void remove() throws InterruptedException {
        try {
            List<Segment> segments = getSegments(this.offlineDataSource, getManifest(this.offlineDataSource, this.manifestUri), true);
            for (int i = 0; i < segments.size(); i++) {
                removeUri(((Segment) segments.get(i)).dataSpec.uri);
            }
        } catch (IOException e) {
        } catch (Throwable th) {
            removeUri(this.manifestUri);
        }
        removeUri(this.manifestUri);
    }

    private List<Segment> initDownload() throws IOException, InterruptedException {
        M manifest = getManifest(this.dataSource, this.manifestUri);
        if (!this.streamKeys.isEmpty()) {
            manifest = (FilterableManifest) manifest.copy(this.streamKeys);
        }
        List<Segment> segments = getSegments(this.dataSource, manifest, false);
        CachingCounters cachingCounters = new CachingCounters();
        this.totalSegments = segments.size();
        this.downloadedSegments = 0;
        this.downloadedBytes = 0;
        for (int i = segments.size() - 1; i >= 0; i--) {
            CacheUtil.getCached(((Segment) segments.get(i)).dataSpec, this.cache, cachingCounters);
            this.downloadedBytes += cachingCounters.alreadyCachedBytes;
            if (cachingCounters.alreadyCachedBytes == cachingCounters.contentLength) {
                this.downloadedSegments++;
                segments.remove(i);
            }
        }
        return segments;
    }

    private void removeUri(Uri uri) {
        CacheUtil.remove(this.cache, CacheUtil.generateKey(uri));
    }
}
