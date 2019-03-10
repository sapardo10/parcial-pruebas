package com.bumptech.glide.load.data;

import android.content.res.AssetManager;
import android.support.annotation.NonNull;
import android.util.Log;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher.DataCallback;
import java.io.IOException;

public abstract class AssetPathFetcher<T> implements DataFetcher<T> {
    private static final String TAG = "AssetPathFetcher";
    private final AssetManager assetManager;
    private final String assetPath;
    private T data;

    protected abstract void close(T t) throws IOException;

    protected abstract T loadResource(AssetManager assetManager, String str) throws IOException;

    public AssetPathFetcher(AssetManager assetManager, String assetPath) {
        this.assetManager = assetManager;
        this.assetPath = assetPath;
    }

    public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super T> callback) {
        try {
            this.data = loadResource(this.assetManager, this.assetPath);
            callback.onDataReady(this.data);
        } catch (IOException e) {
            if (Log.isLoggable(TAG, 3)) {
                Log.d(TAG, "Failed to load data from asset manager", e);
            }
            callback.onLoadFailed(e);
        }
    }

    public void cleanup() {
        Object obj = this.data;
        if (obj != null) {
            try {
                close(obj);
            } catch (IOException e) {
            }
        }
    }

    public void cancel() {
    }

    @NonNull
    public DataSource getDataSource() {
        return DataSource.LOCAL;
    }
}
