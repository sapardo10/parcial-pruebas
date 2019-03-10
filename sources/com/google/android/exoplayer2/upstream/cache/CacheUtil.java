package com.google.android.exoplayer2.upstream.cache;

import android.net.Uri;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.cache.Cache.CacheException;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.PriorityTaskManager;
import com.google.android.exoplayer2.util.PriorityTaskManager.PriorityTooLowException;
import com.google.android.exoplayer2.util.Util;
import java.io.EOFException;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public final class CacheUtil {
    public static final int DEFAULT_BUFFER_SIZE_BYTES = 131072;
    public static final CacheKeyFactory DEFAULT_CACHE_KEY_FACTORY = -$$Lambda$u97poD-IIwob7OPYcVJkh9jokx0.INSTANCE;

    public static class CachingCounters {
        public volatile long alreadyCachedBytes;
        public volatile long contentLength = -1;
        public volatile long newlyCachedBytes;

        public long totalCachedBytes() {
            return this.alreadyCachedBytes + this.newlyCachedBytes;
        }
    }

    public static String generateKey(Uri uri) {
        return uri.toString();
    }

    public static String getKey(DataSpec dataSpec) {
        return dataSpec.key != null ? dataSpec.key : generateKey(dataSpec.uri);
    }

    public static void getCached(DataSpec dataSpec, Cache cache, CachingCounters counters) {
        long left;
        DataSpec dataSpec2 = dataSpec;
        CachingCounters cachingCounters = counters;
        String key = getKey(dataSpec);
        long start = dataSpec2.absoluteStreamPosition;
        if (dataSpec2.length != -1) {
            left = dataSpec2.length;
            Cache cache2 = cache;
        } else {
            left = cache.getContentLength(key);
        }
        cachingCounters.contentLength = left;
        cachingCounters.alreadyCachedBytes = 0;
        cachingCounters.newlyCachedBytes = 0;
        long start2 = start;
        long left2 = left;
        while (left2 != 0) {
            start = cache.getCachedLength(key, start2, left2 != -1 ? left2 : Long.MAX_VALUE);
            if (start > 0) {
                cachingCounters.alreadyCachedBytes += start;
            } else {
                start = -start;
                if (start == Long.MAX_VALUE) {
                    return;
                }
            }
            start2 += start;
            left2 -= left2 == -1 ? 0 : start;
        }
    }

    public static void cache(DataSpec dataSpec, Cache cache, DataSource upstream, @Nullable CachingCounters counters, @Nullable AtomicBoolean isCanceled) throws IOException, InterruptedException {
        cache(dataSpec, cache, new CacheDataSource(cache, upstream), new byte[131072], null, 0, counters, isCanceled, false);
    }

    public static void cache(DataSpec dataSpec, Cache cache, CacheDataSource dataSource, byte[] buffer, PriorityTaskManager priorityTaskManager, int priority, @Nullable CachingCounters counters, @Nullable AtomicBoolean isCanceled, boolean enableEOFException) throws IOException, InterruptedException {
        CachingCounters counters2;
        DataSpec dataSpec2 = dataSpec;
        Cache cache2 = cache;
        CachingCounters cachingCounters = counters;
        Assertions.checkNotNull(dataSource);
        Assertions.checkNotNull(buffer);
        if (cachingCounters != null) {
            getCached(dataSpec2, cache2, cachingCounters);
            counters2 = cachingCounters;
        } else {
            counters2 = new CachingCounters();
        }
        String key = getKey(dataSpec);
        long start = dataSpec2.absoluteStreamPosition;
        long left = dataSpec2.length != -1 ? dataSpec2.length : cache2.getContentLength(key);
        while (true) {
            long j = 0;
            if (left != 0) {
                long blockLength;
                throwExceptionIfInterruptedOrCancelled(isCanceled);
                long blockLength2 = cache.getCachedLength(key, start, left != -1 ? left : Long.MAX_VALUE);
                if (blockLength2 > 0) {
                    blockLength = blockLength2;
                } else {
                    long blockLength3 = -blockLength2;
                    blockLength = blockLength3;
                    if (readAndDiscard(dataSpec, start, blockLength3, dataSource, buffer, priorityTaskManager, priority, counters2, isCanceled) < blockLength) {
                        break;
                    }
                }
                start += blockLength;
                if (left != -1) {
                    j = blockLength;
                }
                left -= j;
            } else {
                return;
            }
        }
        if (enableEOFException) {
            if (left != -1) {
                throw new EOFException();
            }
        }
    }

    private static long readAndDiscard(DataSpec dataSpec, long absoluteStreamPosition, long length, DataSource dataSource, byte[] buffer, PriorityTaskManager priorityTaskManager, int priority, CachingCounters counters, AtomicBoolean isCanceled) throws IOException, InterruptedException {
        DataSource dataSource2 = dataSource;
        byte[] bArr = buffer;
        CachingCounters cachingCounters = counters;
        DataSpec dataSpec2 = dataSpec;
        while (true) {
            if (priorityTaskManager != null) {
                priorityTaskManager.proceed(priority);
            }
            try {
                break;
            } catch (PriorityTooLowException e) {
                Util.closeQuietly(dataSource);
            } catch (Throwable th) {
                Util.closeQuietly(dataSource);
                throw th;
            }
        }
        throwExceptionIfInterruptedOrCancelled(isCanceled);
        dataSpec2 = new DataSpec(dataSpec2.uri, dataSpec2.httpMethod, dataSpec2.httpBody, absoluteStreamPosition, (dataSpec2.position + absoluteStreamPosition) - dataSpec2.absoluteStreamPosition, -1, dataSpec2.key, dataSpec2.flags | 2);
        long resolvedLength = dataSource2.open(dataSpec2);
        if (cachingCounters.contentLength == -1 && resolvedLength != -1) {
            cachingCounters.contentLength = dataSpec2.absoluteStreamPosition + resolvedLength;
        }
        long totalRead = 0;
        while (totalRead != length) {
            throwExceptionIfInterruptedOrCancelled(isCanceled);
            int read = dataSource2.read(bArr, 0, length != -1 ? (int) Math.min((long) bArr.length, length - totalRead) : bArr.length);
            if (read == -1) {
                if (cachingCounters.contentLength == -1) {
                    cachingCounters.contentLength = dataSpec2.absoluteStreamPosition + totalRead;
                }
                Util.closeQuietly(dataSource);
                return totalRead;
            }
            totalRead += (long) read;
            cachingCounters.newlyCachedBytes += (long) read;
        }
        Util.closeQuietly(dataSource);
        return totalRead;
    }

    public static void remove(Cache cache, String key) {
        for (CacheSpan cachedSpan : cache.getCachedSpans(key)) {
            try {
                cache.removeSpan(cachedSpan);
            } catch (CacheException e) {
            }
        }
    }

    private static void throwExceptionIfInterruptedOrCancelled(AtomicBoolean isCanceled) throws InterruptedException {
        if (Thread.interrupted() || (isCanceled != null && isCanceled.get())) {
            throw new InterruptedException();
        }
    }

    private CacheUtil() {
    }
}
