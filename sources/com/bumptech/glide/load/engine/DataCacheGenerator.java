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

class DataCacheGenerator implements DataFetcherGenerator, DataCallback<Object> {
    private File cacheFile;
    private final List<Key> cacheKeys;
    private final FetcherReadyCallback cb;
    private final DecodeHelper<?> helper;
    private volatile LoadData<?> loadData;
    private int modelLoaderIndex;
    private List<ModelLoader<File, ?>> modelLoaders;
    private int sourceIdIndex;
    private Key sourceKey;

    DataCacheGenerator(DecodeHelper<?> helper, FetcherReadyCallback cb) {
        this(helper.getCacheKeys(), helper, cb);
    }

    DataCacheGenerator(List<Key> cacheKeys, DecodeHelper<?> helper, FetcherReadyCallback cb) {
        this.sourceIdIndex = -1;
        this.cacheKeys = cacheKeys;
        this.helper = helper;
        this.cb = cb;
    }

    public boolean startNext() {
        while (true) {
            if (this.modelLoaders != null) {
                if (hasNextModelLoader()) {
                    break;
                }
            }
            this.sourceIdIndex++;
            if (this.sourceIdIndex >= this.cacheKeys.size()) {
                return false;
            }
            Key sourceId = (Key) this.cacheKeys.get(this.sourceIdIndex);
            this.cacheFile = this.helper.getDiskCache().get(new DataCacheKey(sourceId, this.helper.getSignature()));
            File file = this.cacheFile;
            if (file != null) {
                this.sourceKey = sourceId;
                this.modelLoaders = this.helper.getModelLoaders(file);
                this.modelLoaderIndex = 0;
            }
        }
        this.loadData = null;
        boolean started = false;
        while (!started && hasNextModelLoader()) {
            List list = this.modelLoaders;
            int i = this.modelLoaderIndex;
            this.modelLoaderIndex = i + 1;
            this.loadData = ((ModelLoader) list.get(i)).buildLoadData(this.cacheFile, this.helper.getWidth(), this.helper.getHeight(), this.helper.getOptions());
            if (this.loadData != null && this.helper.hasLoadPath(this.loadData.fetcher.getDataClass())) {
                started = true;
                this.loadData.fetcher.loadData(this.helper.getPriority(), this);
            }
        }
        return started;
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
        this.cb.onDataFetcherReady(this.sourceKey, data, this.loadData.fetcher, DataSource.DATA_DISK_CACHE, this.sourceKey);
    }

    public void onLoadFailed(@NonNull Exception e) {
        this.cb.onDataFetcherFailed(this.sourceKey, e, this.loadData.fetcher, DataSource.DATA_DISK_CACHE);
    }
}
