package com.bumptech.glide.load.engine.cache;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.cache.MemoryCache.ResourceRemovedListener;
import com.bumptech.glide.util.LruCache;

public class LruResourceCache extends LruCache<Key, Resource<?>> implements MemoryCache {
    private ResourceRemovedListener listener;

    @Nullable
    public /* bridge */ /* synthetic */ Resource put(@NonNull Key key, @Nullable Resource resource) {
        return (Resource) super.put(key, resource);
    }

    @Nullable
    public /* bridge */ /* synthetic */ Resource remove(@NonNull Key key) {
        return (Resource) super.remove(key);
    }

    public LruResourceCache(long size) {
        super(size);
    }

    public void setResourceRemovedListener(@NonNull ResourceRemovedListener listener) {
        this.listener = listener;
    }

    protected void onItemEvicted(@NonNull Key key, @Nullable Resource<?> item) {
        ResourceRemovedListener resourceRemovedListener = this.listener;
        if (resourceRemovedListener != null && item != null) {
            resourceRemovedListener.onResourceRemoved(item);
        }
    }

    protected int getSize(@Nullable Resource<?> item) {
        if (item == null) {
            return super.getSize(null);
        }
        return item.getSize();
    }

    @SuppressLint({"InlinedApi"})
    public void trimMemory(int level) {
        if (level >= 40) {
            clearMemory();
            return;
        }
        if (level < 20) {
            if (level != 15) {
                return;
            }
        }
        trimToSize(getMaxSize() / 2);
    }
}
