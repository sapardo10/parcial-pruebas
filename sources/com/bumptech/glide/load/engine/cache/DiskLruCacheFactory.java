package com.bumptech.glide.load.engine.cache;

import com.bumptech.glide.load.engine.cache.DiskCache.Factory;
import java.io.File;

public class DiskLruCacheFactory implements Factory {
    private final CacheDirectoryGetter cacheDirectoryGetter;
    private final long diskCacheSize;

    public interface CacheDirectoryGetter {
        File getCacheDirectory();
    }

    /* renamed from: com.bumptech.glide.load.engine.cache.DiskLruCacheFactory$1 */
    class C09501 implements CacheDirectoryGetter {
        final /* synthetic */ String val$diskCacheFolder;

        C09501(String str) {
            this.val$diskCacheFolder = str;
        }

        public File getCacheDirectory() {
            return new File(this.val$diskCacheFolder);
        }
    }

    /* renamed from: com.bumptech.glide.load.engine.cache.DiskLruCacheFactory$2 */
    class C09512 implements CacheDirectoryGetter {
        final /* synthetic */ String val$diskCacheFolder;
        final /* synthetic */ String val$diskCacheName;

        C09512(String str, String str2) {
            this.val$diskCacheFolder = str;
            this.val$diskCacheName = str2;
        }

        public File getCacheDirectory() {
            return new File(this.val$diskCacheFolder, this.val$diskCacheName);
        }
    }

    public DiskLruCacheFactory(String diskCacheFolder, long diskCacheSize) {
        this(new C09501(diskCacheFolder), diskCacheSize);
    }

    public DiskLruCacheFactory(String diskCacheFolder, String diskCacheName, long diskCacheSize) {
        this(new C09512(diskCacheFolder, diskCacheName), diskCacheSize);
    }

    public DiskLruCacheFactory(CacheDirectoryGetter cacheDirectoryGetter, long diskCacheSize) {
        this.diskCacheSize = diskCacheSize;
        this.cacheDirectoryGetter = cacheDirectoryGetter;
    }

    public DiskCache build() {
        File cacheDir = this.cacheDirectoryGetter.getCacheDirectory();
        if (cacheDir == null) {
            return null;
        }
        if (cacheDir.mkdirs() || (cacheDir.exists() && cacheDir.isDirectory())) {
            return DiskLruCacheWrapper.create(cacheDir, this.diskCacheSize);
        }
        return null;
    }
}
