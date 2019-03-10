package com.bumptech.glide.load.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pools.Pool;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.data.DataFetcher.DataCallback;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.model.ModelLoader.LoadData;
import com.bumptech.glide.util.Preconditions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

class MultiModelLoader<Model, Data> implements ModelLoader<Model, Data> {
    private final Pool<List<Throwable>> exceptionListPool;
    private final List<ModelLoader<Model, Data>> modelLoaders;

    static class MultiFetcher<Data> implements DataFetcher<Data>, DataCallback<Data> {
        private DataCallback<? super Data> callback;
        private int currentIndex = 0;
        @Nullable
        private List<Throwable> exceptions;
        private final List<DataFetcher<Data>> fetchers;
        private Priority priority;
        private final Pool<List<Throwable>> throwableListPool;

        MultiFetcher(@NonNull List<DataFetcher<Data>> fetchers, @NonNull Pool<List<Throwable>> throwableListPool) {
            this.throwableListPool = throwableListPool;
            Preconditions.checkNotEmpty((Collection) fetchers);
            this.fetchers = fetchers;
        }

        public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super Data> callback) {
            this.priority = priority;
            this.callback = callback;
            this.exceptions = (List) this.throwableListPool.acquire();
            ((DataFetcher) this.fetchers.get(this.currentIndex)).loadData(priority, this);
        }

        public void cleanup() {
            List list = this.exceptions;
            if (list != null) {
                this.throwableListPool.release(list);
            }
            this.exceptions = null;
            for (DataFetcher<Data> fetcher : this.fetchers) {
                fetcher.cleanup();
            }
        }

        public void cancel() {
            for (DataFetcher<Data> fetcher : this.fetchers) {
                fetcher.cancel();
            }
        }

        @NonNull
        public Class<Data> getDataClass() {
            return ((DataFetcher) this.fetchers.get(0)).getDataClass();
        }

        @NonNull
        public DataSource getDataSource() {
            return ((DataFetcher) this.fetchers.get(0)).getDataSource();
        }

        public void onDataReady(@Nullable Data data) {
            if (data != null) {
                this.callback.onDataReady(data);
            } else {
                startNextOrFail();
            }
        }

        public void onLoadFailed(@NonNull Exception e) {
            ((List) Preconditions.checkNotNull(this.exceptions)).add(e);
            startNextOrFail();
        }

        private void startNextOrFail() {
            if (this.currentIndex < this.fetchers.size() - 1) {
                this.currentIndex++;
                loadData(this.priority, this.callback);
                return;
            }
            Preconditions.checkNotNull(this.exceptions);
            this.callback.onLoadFailed(new GlideException("Fetch failed", new ArrayList(this.exceptions)));
        }
    }

    MultiModelLoader(@NonNull List<ModelLoader<Model, Data>> modelLoaders, @NonNull Pool<List<Throwable>> exceptionListPool) {
        this.modelLoaders = modelLoaders;
        this.exceptionListPool = exceptionListPool;
    }

    public LoadData<Data> buildLoadData(@NonNull Model model, int width, int height, @NonNull Options options) {
        Key sourceKey = null;
        int size = this.modelLoaders.size();
        List<DataFetcher<Data>> fetchers = new ArrayList(size);
        for (int i = 0; i < size; i++) {
            ModelLoader<Model, Data> modelLoader = (ModelLoader) this.modelLoaders.get(i);
            if (modelLoader.handles(model)) {
                LoadData<Data> loadData = modelLoader.buildLoadData(model, width, height, options);
                if (loadData != null) {
                    sourceKey = loadData.sourceKey;
                    fetchers.add(loadData.fetcher);
                }
            }
        }
        return (fetchers.isEmpty() || sourceKey == null) ? null : new LoadData(sourceKey, new MultiFetcher(fetchers, this.exceptionListPool));
    }

    public boolean handles(@NonNull Model model) {
        for (ModelLoader<Model, Data> modelLoader : this.modelLoaders) {
            if (modelLoader.handles(model)) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("MultiModelLoader{modelLoaders=");
        stringBuilder.append(Arrays.toString(this.modelLoaders.toArray()));
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
