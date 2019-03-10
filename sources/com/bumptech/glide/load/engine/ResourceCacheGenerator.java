package com.bumptech.glide.load.engine;

import android.support.annotation.NonNull;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.data.DataFetcher.DataCallback;
import com.bumptech.glide.load.engine.DataFetcherGenerator.FetcherReadyCallback;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoader.LoadData;
import java.io.File;
import java.util.List;

class ResourceCacheGenerator implements DataFetcherGenerator, DataCallback<Object> {
    private File cacheFile;
    private final FetcherReadyCallback cb;
    private ResourceCacheKey currentKey;
    private final DecodeHelper<?> helper;
    private volatile LoadData<?> loadData;
    private int modelLoaderIndex;
    private List<ModelLoader<File, ?>> modelLoaders;
    private int resourceClassIndex = -1;
    private int sourceIdIndex;
    private Key sourceKey;

    ResourceCacheGenerator(DecodeHelper<?> helper, FetcherReadyCallback cb) {
        this.helper = helper;
        this.cb = cb;
    }

    public boolean startNext() {
        List<Key> sourceIds = this.helper.getCacheKeys();
        boolean z = false;
        if (sourceIds.isEmpty()) {
            return false;
        }
        List<Class<?>> resourceClasses = r0.helper.getRegisteredResourceClasses();
        if (resourceClasses.isEmpty()) {
            if (File.class.equals(r0.helper.getTranscodeClass())) {
                return false;
            }
        }
        while (true) {
            if (r0.modelLoaders != null) {
                if (hasNextModelLoader()) {
                    break;
                }
            }
            r0.resourceClassIndex++;
            if (r0.resourceClassIndex >= resourceClasses.size()) {
                r0.sourceIdIndex++;
                if (r0.sourceIdIndex >= sourceIds.size()) {
                    return z;
                }
                r0.resourceClassIndex = z;
            }
            Key sourceId = (Key) sourceIds.get(r0.sourceIdIndex);
            Class<?> resourceClass = (Class) resourceClasses.get(r0.resourceClassIndex);
            Key key = sourceId;
            ResourceCacheKey resourceCacheKey = r5;
            ResourceCacheKey resourceCacheKey2 = new ResourceCacheKey(r0.helper.getArrayPool(), key, r0.helper.getSignature(), r0.helper.getWidth(), r0.helper.getHeight(), r0.helper.getTransformation(resourceClass), resourceClass, r0.helper.getOptions());
            r0.currentKey = resourceCacheKey;
            r0.cacheFile = r0.helper.getDiskCache().get(r0.currentKey);
            File file = r0.cacheFile;
            if (file != null) {
                r0.sourceKey = sourceId;
                r0.modelLoaders = r0.helper.getModelLoaders(file);
                z = false;
                r0.modelLoaderIndex = 0;
            } else {
                z = false;
            }
        }
        r0.loadData = null;
        z = false;
        while (!z && hasNextModelLoader()) {
            List list = r0.modelLoaders;
            int i = r0.modelLoaderIndex;
            r0.modelLoaderIndex = i + 1;
            r0.loadData = ((ModelLoader) list.get(i)).buildLoadData(r0.cacheFile, r0.helper.getWidth(), r0.helper.getHeight(), r0.helper.getOptions());
            if (r0.loadData != null && r0.helper.hasLoadPath(r0.loadData.fetcher.getDataClass())) {
                z = true;
                r0.loadData.fetcher.loadData(r0.helper.getPriority(), r0);
            }
        }
        return z;
    }

    private boolean hasNextModelLoader() {
        return this.modelLoaderIndex < this.modelLoaders.size();
    }

    public void cancel() {
        LoadData<?> local = this.loadData;
        if (local != null) {
            local.fetcher.cancel();
        }
    }

    public void onDataReady(Object data) {
        this.cb.onDataFetcherReady(this.sourceKey, data, this.loadData.fetcher, DataSource.RESOURCE_DISK_CACHE, this.currentKey);
    }

    public void onLoadFailed(@NonNull Exception e) {
        this.cb.onDataFetcherFailed(this.currentKey, e, this.loadData.fetcher, DataSource.RESOURCE_DISK_CACHE);
    }
}
