package com.bumptech.glide.load.engine;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.EncodeStrategy;

public abstract class DiskCacheStrategy {
    public static final DiskCacheStrategy ALL = new C09421();
    public static final DiskCacheStrategy AUTOMATIC = new C09465();
    public static final DiskCacheStrategy DATA = new C09443();
    public static final DiskCacheStrategy NONE = new C09432();
    public static final DiskCacheStrategy RESOURCE = new C09454();

    /* renamed from: com.bumptech.glide.load.engine.DiskCacheStrategy$1 */
    class C09421 extends DiskCacheStrategy {
        C09421() {
        }

        public boolean isDataCacheable(DataSource dataSource) {
            return dataSource == DataSource.REMOTE;
        }

        public boolean isResourceCacheable(boolean isFromAlternateCacheKey, DataSource dataSource, EncodeStrategy encodeStrategy) {
            return (dataSource == DataSource.RESOURCE_DISK_CACHE || dataSource == DataSource.MEMORY_CACHE) ? false : true;
        }

        public boolean decodeCachedResource() {
            return true;
        }

        public boolean decodeCachedData() {
            return true;
        }
    }

    /* renamed from: com.bumptech.glide.load.engine.DiskCacheStrategy$2 */
    class C09432 extends DiskCacheStrategy {
        C09432() {
        }

        public boolean isDataCacheable(DataSource dataSource) {
            return false;
        }

        public boolean isResourceCacheable(boolean isFromAlternateCacheKey, DataSource dataSource, EncodeStrategy encodeStrategy) {
            return false;
        }

        public boolean decodeCachedResource() {
            return false;
        }

        public boolean decodeCachedData() {
            return false;
        }
    }

    /* renamed from: com.bumptech.glide.load.engine.DiskCacheStrategy$3 */
    class C09443 extends DiskCacheStrategy {
        C09443() {
        }

        public boolean isDataCacheable(DataSource dataSource) {
            return (dataSource == DataSource.DATA_DISK_CACHE || dataSource == DataSource.MEMORY_CACHE) ? false : true;
        }

        public boolean isResourceCacheable(boolean isFromAlternateCacheKey, DataSource dataSource, EncodeStrategy encodeStrategy) {
            return false;
        }

        public boolean decodeCachedResource() {
            return false;
        }

        public boolean decodeCachedData() {
            return true;
        }
    }

    /* renamed from: com.bumptech.glide.load.engine.DiskCacheStrategy$4 */
    class C09454 extends DiskCacheStrategy {
        C09454() {
        }

        public boolean isDataCacheable(DataSource dataSource) {
            return false;
        }

        public boolean isResourceCacheable(boolean isFromAlternateCacheKey, DataSource dataSource, EncodeStrategy encodeStrategy) {
            return (dataSource == DataSource.RESOURCE_DISK_CACHE || dataSource == DataSource.MEMORY_CACHE) ? false : true;
        }

        public boolean decodeCachedResource() {
            return true;
        }

        public boolean decodeCachedData() {
            return false;
        }
    }

    /* renamed from: com.bumptech.glide.load.engine.DiskCacheStrategy$5 */
    class C09465 extends DiskCacheStrategy {
        C09465() {
        }

        public boolean isDataCacheable(DataSource dataSource) {
            return dataSource == DataSource.REMOTE;
        }

        public boolean isResourceCacheable(boolean isFromAlternateCacheKey, DataSource dataSource, EncodeStrategy encodeStrategy) {
            return ((isFromAlternateCacheKey && dataSource == DataSource.DATA_DISK_CACHE) || dataSource == DataSource.LOCAL) && encodeStrategy == EncodeStrategy.TRANSFORMED;
        }

        public boolean decodeCachedResource() {
            return true;
        }

        public boolean decodeCachedData() {
            return true;
        }
    }

    public abstract boolean decodeCachedData();

    public abstract boolean decodeCachedResource();

    public abstract boolean isDataCacheable(DataSource dataSource);

    public abstract boolean isResourceCacheable(boolean z, DataSource dataSource, EncodeStrategy encodeStrategy);
}
