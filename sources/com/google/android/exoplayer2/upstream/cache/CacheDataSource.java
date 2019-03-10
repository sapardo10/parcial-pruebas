package com.google.android.exoplayer2.upstream.cache;

import android.net.Uri;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.upstream.DataSink;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSourceException;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.upstream.TeeDataSource;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.upstream.cache.Cache.CacheException;
import com.google.android.exoplayer2.util.Assertions;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class CacheDataSource implements DataSource {
    public static final int CACHE_IGNORED_REASON_ERROR = 0;
    public static final int CACHE_IGNORED_REASON_UNSET_LENGTH = 1;
    private static final int CACHE_NOT_IGNORED = -1;
    public static final long DEFAULT_MAX_CACHE_FILE_SIZE = 2097152;
    public static final int FLAG_BLOCK_ON_CACHE = 1;
    public static final int FLAG_IGNORE_CACHE_FOR_UNSET_LENGTH_REQUESTS = 4;
    public static final int FLAG_IGNORE_CACHE_ON_ERROR = 2;
    private static final long MIN_READ_BEFORE_CHECKING_CACHE = 102400;
    @Nullable
    private Uri actualUri;
    private final boolean blockOnCache;
    private long bytesRemaining;
    private final Cache cache;
    private final CacheKeyFactory cacheKeyFactory;
    private final DataSource cacheReadDataSource;
    @Nullable
    private final DataSource cacheWriteDataSource;
    private long checkCachePosition;
    @Nullable
    private DataSource currentDataSource;
    private boolean currentDataSpecLengthUnset;
    @Nullable
    private CacheSpan currentHoleSpan;
    private boolean currentRequestIgnoresCache;
    @Nullable
    private final EventListener eventListener;
    private int flags;
    private int httpMethod;
    private final boolean ignoreCacheForUnsetLengthRequests;
    private final boolean ignoreCacheOnError;
    @Nullable
    private String key;
    private long readPosition;
    private boolean seenCacheError;
    private long totalCachedBytesRead;
    private final DataSource upstreamDataSource;
    @Nullable
    private Uri uri;

    public interface EventListener {
        void onCacheIgnored(int i);

        void onCachedBytesRead(long j, long j2);
    }

    public CacheDataSource(Cache cache, DataSource upstream) {
        this(cache, upstream, 0, 2097152);
    }

    public CacheDataSource(Cache cache, DataSource upstream, int flags) {
        this(cache, upstream, flags, 2097152);
    }

    public CacheDataSource(Cache cache, DataSource upstream, int flags, long maxCacheFileSize) {
        this(cache, upstream, new FileDataSource(), new CacheDataSink(cache, maxCacheFileSize), flags, null);
    }

    public CacheDataSource(Cache cache, DataSource upstream, DataSource cacheReadDataSource, DataSink cacheWriteDataSink, int flags, @Nullable EventListener eventListener) {
        this(cache, upstream, cacheReadDataSource, cacheWriteDataSink, flags, eventListener, null);
    }

    public CacheDataSource(Cache cache, DataSource upstream, DataSource cacheReadDataSource, DataSink cacheWriteDataSink, int flags, @Nullable EventListener eventListener, @Nullable CacheKeyFactory cacheKeyFactory) {
        this.cache = cache;
        this.cacheReadDataSource = cacheReadDataSource;
        this.cacheKeyFactory = cacheKeyFactory != null ? cacheKeyFactory : CacheUtil.DEFAULT_CACHE_KEY_FACTORY;
        boolean z = false;
        this.blockOnCache = (flags & 1) != 0;
        this.ignoreCacheOnError = (flags & 2) != 0;
        if ((flags & 4) != 0) {
            z = true;
        }
        this.ignoreCacheForUnsetLengthRequests = z;
        this.upstreamDataSource = upstream;
        if (cacheWriteDataSink != null) {
            this.cacheWriteDataSource = new TeeDataSource(upstream, cacheWriteDataSink);
        } else {
            this.cacheWriteDataSource = null;
        }
        this.eventListener = eventListener;
    }

    public void addTransferListener(TransferListener transferListener) {
        this.cacheReadDataSource.addTransferListener(transferListener);
        this.upstreamDataSource.addTransferListener(transferListener);
    }

    public long open(DataSpec dataSpec) throws IOException {
        try {
            this.key = this.cacheKeyFactory.buildCacheKey(dataSpec);
            this.uri = dataSpec.uri;
            this.actualUri = getRedirectedUriOrDefault(this.cache, this.key, this.uri);
            this.httpMethod = dataSpec.httpMethod;
            this.flags = dataSpec.flags;
            this.readPosition = dataSpec.position;
            int reason = shouldIgnoreCacheForRequest(dataSpec);
            this.currentRequestIgnoresCache = reason != -1;
            if (this.currentRequestIgnoresCache) {
                notifyCacheIgnored(reason);
            }
            if (dataSpec.length == -1) {
                if (!this.currentRequestIgnoresCache) {
                    this.bytesRemaining = this.cache.getContentLength(this.key);
                    if (this.bytesRemaining != -1) {
                        this.bytesRemaining -= dataSpec.position;
                        if (this.bytesRemaining <= 0) {
                            throw new DataSourceException(0);
                        }
                    }
                    openNextSource(false);
                    return this.bytesRemaining;
                }
            }
            this.bytesRemaining = dataSpec.length;
            openNextSource(false);
            return this.bytesRemaining;
        } catch (IOException e) {
            handleBeforeThrow(e);
            throw e;
        }
    }

    public int read(byte[] buffer, int offset, int readLength) throws IOException {
        if (readLength == 0) {
            return 0;
        }
        if (this.bytesRemaining == 0) {
            return -1;
        }
        try {
            if (this.readPosition >= this.checkCachePosition) {
                openNextSource(true);
            }
            int bytesRead = this.currentDataSource.read(buffer, offset, readLength);
            if (bytesRead != -1) {
                if (isReadingFromCache()) {
                    this.totalCachedBytesRead += (long) bytesRead;
                }
                this.readPosition += (long) bytesRead;
                if (this.bytesRemaining != -1) {
                    this.bytesRemaining -= (long) bytesRead;
                }
            } else if (this.currentDataSpecLengthUnset) {
                setNoBytesRemainingAndMaybeStoreLength();
            } else {
                if (this.bytesRemaining <= 0) {
                    if (this.bytesRemaining == -1) {
                    }
                }
                closeCurrentSource();
                openNextSource(false);
                return read(buffer, offset, readLength);
            }
            return bytesRead;
        } catch (IOException e) {
            if (this.currentDataSpecLengthUnset && isCausedByPositionOutOfRange(e)) {
                setNoBytesRemainingAndMaybeStoreLength();
                return -1;
            }
            handleBeforeThrow(e);
            throw e;
        }
    }

    @Nullable
    public Uri getUri() {
        return this.actualUri;
    }

    public Map<String, List<String>> getResponseHeaders() {
        if (isReadingFromUpstream()) {
            return this.upstreamDataSource.getResponseHeaders();
        }
        return Collections.emptyMap();
    }

    public void close() throws IOException {
        this.uri = null;
        this.actualUri = null;
        this.httpMethod = 1;
        notifyBytesRead();
        try {
            closeCurrentSource();
        } catch (IOException e) {
            handleBeforeThrow(e);
            throw e;
        }
    }

    private void openNextSource(boolean checkCache) throws IOException {
        CacheSpan nextSpan;
        DataSource nextDataSource;
        DataSpec nextDataSpec;
        CacheSpan nextSpan2;
        long j;
        if (this.currentRequestIgnoresCache) {
            nextSpan = null;
        } else if (r1.blockOnCache) {
            try {
                nextSpan = r1.cache.startReadWrite(r1.key, r1.readPosition);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new InterruptedIOException();
            }
        } else {
            nextSpan = r1.cache.startReadWriteNonBlocking(r1.key, r1.readPosition);
        }
        if (nextSpan == null) {
            nextDataSource = r1.upstreamDataSource;
            Uri uri = r1.uri;
            int i = r1.httpMethod;
            long j2 = r1.readPosition;
            nextDataSpec = new DataSpec(uri, i, null, j2, j2, r1.bytesRemaining, r1.key, r1.flags);
            nextSpan2 = nextSpan;
        } else if (nextSpan.isCached) {
            long length;
            Uri fileUri = Uri.fromFile(nextSpan.file);
            long filePosition = r1.readPosition - nextSpan.position;
            long length2 = nextSpan.length - filePosition;
            j = r1.bytesRemaining;
            if (j != -1) {
                length = Math.min(length2, j);
            } else {
                length = length2;
            }
            DataSpec nextDataSpec2 = new DataSpec(fileUri, r1.readPosition, filePosition, length, r1.key, r1.flags);
            nextDataSource = r1.cacheReadDataSource;
            nextDataSpec = nextDataSpec2;
            nextSpan2 = nextSpan;
        } else {
            long length3;
            if (nextSpan.isOpenEnded()) {
                length3 = r1.bytesRemaining;
            } else {
                length3 = nextSpan.length;
                long j3 = r1.bytesRemaining;
                if (j3 != -1) {
                    length3 = Math.min(length3, j3);
                }
            }
            Uri uri2 = r1.uri;
            int i2 = r1.httpMethod;
            long j4 = r1.readPosition;
            nextDataSpec = new DataSpec(uri2, i2, null, j4, j4, length3, r1.key, r1.flags);
            if (r1.cacheWriteDataSource != null) {
                nextSpan2 = nextSpan;
                nextDataSource = r1.cacheWriteDataSource;
            } else {
                DataSource nextDataSource2 = r1.upstreamDataSource;
                r1.cache.releaseHoleSpan(nextSpan);
                nextSpan2 = null;
                nextDataSource = nextDataSource2;
            }
        }
        j = (r1.currentRequestIgnoresCache || nextDataSource != r1.upstreamDataSource) ? Long.MAX_VALUE : r1.readPosition + MIN_READ_BEFORE_CHECKING_CACHE;
        r1.checkCachePosition = j;
        if (checkCache) {
            Assertions.checkState(isBypassingCache());
            if (nextDataSource != r1.upstreamDataSource) {
                try {
                    closeCurrentSource();
                } catch (Throwable e2) {
                    Throwable e22 = e22;
                    if (nextSpan2.isHoleSpan()) {
                        r1.cache.releaseHoleSpan(nextSpan2);
                    }
                }
            } else {
                return;
            }
        }
        if (nextSpan2 != null && nextSpan2.isHoleSpan()) {
            r1.currentHoleSpan = nextSpan2;
        }
        r1.currentDataSource = nextDataSource;
        r1.currentDataSpecLengthUnset = nextDataSpec.length == -1;
        j = nextDataSource.open(nextDataSpec);
        ContentMetadataMutations mutations = new ContentMetadataMutations();
        if (r1.currentDataSpecLengthUnset && j != -1) {
            r1.bytesRemaining = j;
            ContentMetadataInternal.setContentLength(mutations, r1.readPosition + r1.bytesRemaining);
        }
        if (isReadingFromUpstream()) {
            r1.actualUri = r1.currentDataSource.getUri();
            if (true ^ r1.uri.equals(r1.actualUri)) {
                ContentMetadataInternal.setRedirectedUri(mutations, r1.actualUri);
            } else {
                ContentMetadataInternal.removeRedirectedUri(mutations);
            }
        }
        if (isWritingToCache()) {
            r1.cache.applyContentMetadataMutations(r1.key, mutations);
        }
    }

    private void setNoBytesRemainingAndMaybeStoreLength() throws IOException {
        this.bytesRemaining = 0;
        if (isWritingToCache()) {
            this.cache.setContentLength(this.key, this.readPosition);
        }
    }

    private static Uri getRedirectedUriOrDefault(Cache cache, String key, Uri defaultUri) {
        Uri redirectedUri = ContentMetadataInternal.getRedirectedUri(cache.getContentMetadata(key));
        return redirectedUri == null ? defaultUri : redirectedUri;
    }

    private static boolean isCausedByPositionOutOfRange(IOException e) {
        for (Throwable cause = e; cause != null; cause = cause.getCause()) {
            if (cause instanceof DataSourceException) {
                if (((DataSourceException) cause).reason == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isReadingFromUpstream() {
        return isReadingFromCache() ^ 1;
    }

    private boolean isBypassingCache() {
        return this.currentDataSource == this.upstreamDataSource;
    }

    private boolean isReadingFromCache() {
        return this.currentDataSource == this.cacheReadDataSource;
    }

    private boolean isWritingToCache() {
        return this.currentDataSource == this.cacheWriteDataSource;
    }

    private void closeCurrentSource() throws IOException {
        DataSource dataSource = this.currentDataSource;
        if (dataSource != null) {
            try {
                dataSource.close();
                this.currentDataSource = null;
                this.currentDataSpecLengthUnset = false;
                CacheSpan cacheSpan = this.currentHoleSpan;
                if (cacheSpan != null) {
                    this.cache.releaseHoleSpan(cacheSpan);
                    this.currentHoleSpan = null;
                }
            } catch (Throwable th) {
                this.currentDataSource = null;
                this.currentDataSpecLengthUnset = false;
                CacheSpan cacheSpan2 = this.currentHoleSpan;
                if (cacheSpan2 != null) {
                    this.cache.releaseHoleSpan(cacheSpan2);
                    this.currentHoleSpan = null;
                }
            }
        }
    }

    private void handleBeforeThrow(IOException exception) {
        if (!isReadingFromCache()) {
            if (!(exception instanceof CacheException)) {
                return;
            }
        }
        this.seenCacheError = true;
    }

    private int shouldIgnoreCacheForRequest(DataSpec dataSpec) {
        if (this.ignoreCacheOnError && this.seenCacheError) {
            return 0;
        }
        if (this.ignoreCacheForUnsetLengthRequests && dataSpec.length == -1) {
            return 1;
        }
        return -1;
    }

    private void notifyCacheIgnored(int reason) {
        EventListener eventListener = this.eventListener;
        if (eventListener != null) {
            eventListener.onCacheIgnored(reason);
        }
    }

    private void notifyBytesRead() {
        EventListener eventListener = this.eventListener;
        if (eventListener != null && this.totalCachedBytesRead > 0) {
            eventListener.onCachedBytesRead(this.cache.getCacheSpace(), this.totalCachedBytesRead);
            this.totalCachedBytesRead = 0;
        }
    }
}
