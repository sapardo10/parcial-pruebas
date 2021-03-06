package com.google.android.exoplayer2.upstream.cache;

import com.google.android.exoplayer2.upstream.DataSink;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSource.Factory;
import com.google.android.exoplayer2.upstream.FileDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource.EventListener;

public final class CacheDataSourceFactory implements Factory {
    private final Cache cache;
    private final Factory cacheReadDataSourceFactory;
    private final DataSink.Factory cacheWriteDataSinkFactory;
    private final EventListener eventListener;
    private final int flags;
    private final Factory upstreamFactory;

    public CacheDataSourceFactory(Cache cache, Factory upstreamFactory) {
        this(cache, upstreamFactory, 0);
    }

    public CacheDataSourceFactory(Cache cache, Factory upstreamFactory, int flags) {
        this(cache, upstreamFactory, flags, 2097152);
    }

    public CacheDataSourceFactory(Cache cache, Factory upstreamFactory, int flags, long maxCacheFileSize) {
        this(cache, upstreamFactory, new FileDataSourceFactory(), new CacheDataSinkFactory(cache, maxCacheFileSize), flags, null);
    }

    public CacheDataSourceFactory(Cache cache, Factory upstreamFactory, Factory cacheReadDataSourceFactory, DataSink.Factory cacheWriteDataSinkFactory, int flags, EventListener eventListener) {
        this.cache = cache;
        this.upstreamFactory = upstreamFactory;
        this.cacheReadDataSourceFactory = cacheReadDataSourceFactory;
        this.cacheWriteDataSinkFactory = cacheWriteDataSinkFactory;
        this.flags = flags;
        this.eventListener = eventListener;
    }

    public CacheDataSource createDataSource() {
        Cache cache = this.cache;
        DataSource createDataSource = this.upstreamFactory.createDataSource();
        DataSource createDataSource2 = this.cacheReadDataSourceFactory.createDataSource();
        DataSink.Factory factory = this.cacheWriteDataSinkFactory;
        return new CacheDataSource(cache, createDataSource, createDataSource2, factory != null ? factory.createDataSink() : null, this.flags, this.eventListener);
    }
}
