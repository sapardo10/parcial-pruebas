package com.google.android.exoplayer2.offline;

import android.support.annotation.Nullable;
import com.google.android.exoplayer2.upstream.DataSink;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSource.Factory;
import com.google.android.exoplayer2.upstream.DummyDataSource;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.upstream.PriorityDataSource;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSink;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.PriorityTaskManager;

public final class DownloaderConstructorHelper {
    private final Cache cache;
    private final Factory cacheReadDataSourceFactory;
    private final DataSink.Factory cacheWriteDataSinkFactory;
    private final PriorityTaskManager priorityTaskManager;
    private final Factory upstreamDataSourceFactory;

    public DownloaderConstructorHelper(Cache cache, Factory upstreamDataSourceFactory) {
        this(cache, upstreamDataSourceFactory, null, null, null);
    }

    public DownloaderConstructorHelper(Cache cache, Factory upstreamDataSourceFactory, @Nullable Factory cacheReadDataSourceFactory, @Nullable DataSink.Factory cacheWriteDataSinkFactory, @Nullable PriorityTaskManager priorityTaskManager) {
        Assertions.checkNotNull(upstreamDataSourceFactory);
        this.cache = cache;
        this.upstreamDataSourceFactory = upstreamDataSourceFactory;
        this.cacheReadDataSourceFactory = cacheReadDataSourceFactory;
        this.cacheWriteDataSinkFactory = cacheWriteDataSinkFactory;
        this.priorityTaskManager = priorityTaskManager;
    }

    public Cache getCache() {
        return this.cache;
    }

    public PriorityTaskManager getPriorityTaskManager() {
        PriorityTaskManager priorityTaskManager = this.priorityTaskManager;
        return priorityTaskManager != null ? priorityTaskManager : new PriorityTaskManager();
    }

    public CacheDataSource buildCacheDataSource(boolean offline) {
        Factory factory = this.cacheReadDataSourceFactory;
        DataSource cacheReadDataSource = factory != null ? factory.createDataSource() : new FileDataSource();
        if (offline) {
            return new CacheDataSource(this.cache, DummyDataSource.INSTANCE, cacheReadDataSource, null, 1, null);
        }
        DataSink.Factory factory2 = this.cacheWriteDataSinkFactory;
        DataSink cacheWriteDataSink = factory2 != null ? factory2.createDataSink() : new CacheDataSink(this.cache, 2097152);
        DataSource upstream = this.upstreamDataSourceFactory.createDataSource();
        PriorityTaskManager priorityTaskManager = this.priorityTaskManager;
        return new CacheDataSource(this.cache, priorityTaskManager == null ? upstream : new PriorityDataSource(upstream, priorityTaskManager, -1000), cacheReadDataSource, cacheWriteDataSink, 1, null);
    }
}
