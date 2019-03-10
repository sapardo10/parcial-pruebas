package com.bumptech.glide.load.engine.cache;

import android.content.Context;
import com.bumptech.glide.load.engine.cache.DiskCache.Factory;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory.CacheDirectoryGetter;
import java.io.File;

public final class InternalCacheDiskCacheFactory extends DiskLruCacheFactory {

    /* renamed from: com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory$1 */
    class C09541 implements CacheDirectoryGetter {
        final /* synthetic */ Context val$context;
        final /* synthetic */ String val$diskCacheName;

        C09541(Context context, String str) {
            this.val$context = context;
            this.val$diskCacheName = str;
        }

        public File getCacheDirectory() {
            File cacheDirectory = this.val$context.getCacheDir();
            if (cacheDirectory == null) {
                return null;
            }
            String str = this.val$diskCacheName;
            if (str != null) {
                return new File(cacheDirectory, str);
            }
            return cacheDirectory;
        }
    }

    public InternalCacheDiskCacheFactory(Context context) {
        this(context, Factory.DEFAULT_DISK_CACHE_DIR, 262144000);
    }

    public InternalCacheDiskCacheFactory(Context context, long diskCacheSize) {
        this(context, Factory.DEFAULT_DISK_CACHE_DIR, diskCacheSize);
    }

    public InternalCacheDiskCacheFactory(Context context, String diskCacheName, long diskCacheSize) {
        super(new C09541(context, diskCacheName), diskCacheSize);
    }
}
