package com.bumptech.glide.load.engine.cache;

import android.content.Context;
import android.support.annotation.Nullable;
import com.bumptech.glide.load.engine.cache.DiskCache.Factory;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory.CacheDirectoryGetter;
import java.io.File;

public final class ExternalPreferredCacheDiskCacheFactory extends DiskLruCacheFactory {

    /* renamed from: com.bumptech.glide.load.engine.cache.ExternalPreferredCacheDiskCacheFactory$1 */
    class C09531 implements CacheDirectoryGetter {
        final /* synthetic */ Context val$context;
        final /* synthetic */ String val$diskCacheName;

        C09531(Context context, String str) {
            this.val$context = context;
            this.val$diskCacheName = str;
        }

        @Nullable
        private File getInternalCacheDirectory() {
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

        public File getCacheDirectory() {
            File internalCacheDirectory = getInternalCacheDirectory();
            if (internalCacheDirectory != null && internalCacheDirectory.exists()) {
                return internalCacheDirectory;
            }
            File cacheDirectory = this.val$context.getExternalCacheDir();
            if (cacheDirectory != null) {
                if (cacheDirectory.canWrite()) {
                    String str = this.val$diskCacheName;
                    if (str != null) {
                        return new File(cacheDirectory, str);
                    }
                    return cacheDirectory;
                }
            }
            return internalCacheDirectory;
        }
    }

    public ExternalPreferredCacheDiskCacheFactory(Context context) {
        this(context, Factory.DEFAULT_DISK_CACHE_DIR, 262144000);
    }

    public ExternalPreferredCacheDiskCacheFactory(Context context, long diskCacheSize) {
        this(context, Factory.DEFAULT_DISK_CACHE_DIR, diskCacheSize);
    }

    public ExternalPreferredCacheDiskCacheFactory(Context context, String diskCacheName, long diskCacheSize) {
        super(new C09531(context, diskCacheName), diskCacheSize);
    }
}
