package com.bumptech.glide.load.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher.DataCallback;
import java.io.FileNotFoundException;
import java.io.IOException;

public abstract class LocalUriFetcher<T> implements DataFetcher<T> {
    private static final String TAG = "LocalUriFetcher";
    private final ContentResolver contentResolver;
    private T data;
    private final Uri uri;

    protected abstract void close(T t) throws IOException;

    protected abstract T loadResource(Uri uri, ContentResolver contentResolver) throws FileNotFoundException;

    public LocalUriFetcher(ContentResolver contentResolver, Uri uri) {
        this.contentResolver = contentResolver;
        this.uri = uri;
    }

    public final void loadData(@NonNull Priority priority, @NonNull DataCallback<? super T> callback) {
        try {
            this.data = loadResource(this.uri, this.contentResolver);
            callback.onDataReady(this.data);
        } catch (FileNotFoundException e) {
            if (Log.isLoggable(TAG, 3)) {
                Log.d(TAG, "Failed to open Uri", e);
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
