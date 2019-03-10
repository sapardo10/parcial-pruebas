package com.bumptech.glide.integration.okhttp3;

import android.support.annotation.NonNull;
import android.util.Log;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.HttpException;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.data.DataFetcher.DataCallback;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.util.ContentLengthInputStream;
import com.bumptech.glide.util.Preconditions;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;
import okhttp3.Call;
import okhttp3.Call$Factory;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class OkHttpStreamFetcher implements DataFetcher<InputStream>, Callback {
    private static final String TAG = "OkHttpFetcher";
    private volatile Call call;
    private DataCallback<? super InputStream> callback;
    private final Call$Factory client;
    private ResponseBody responseBody;
    private InputStream stream;
    private final GlideUrl url;

    public OkHttpStreamFetcher(Call$Factory client, GlideUrl url) {
        this.client = client;
        this.url = url;
    }

    public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super InputStream> callback) {
        Builder requestBuilder = new Builder().url(this.url.toStringUrl());
        for (Entry<String, String> headerEntry : this.url.getHeaders().entrySet()) {
            requestBuilder.addHeader((String) headerEntry.getKey(), (String) headerEntry.getValue());
        }
        Request request = requestBuilder.build();
        this.callback = callback;
        this.call = this.client.newCall(request);
        this.call.enqueue(this);
    }

    public void onFailure(@NonNull Call call, @NonNull IOException e) {
        if (Log.isLoggable(TAG, 3)) {
            Log.d(TAG, "OkHttp failed to obtain result", e);
        }
        this.callback.onLoadFailed(e);
    }

    public void onResponse(@NonNull Call call, @NonNull Response response) {
        this.responseBody = response.body();
        if (response.isSuccessful()) {
            this.stream = ContentLengthInputStream.obtain(this.responseBody.byteStream(), ((ResponseBody) Preconditions.checkNotNull(this.responseBody)).contentLength());
            this.callback.onDataReady(this.stream);
            return;
        }
        this.callback.onLoadFailed(new HttpException(response.message(), response.code()));
    }

    public void cleanup() {
        try {
            if (this.stream != null) {
                this.stream.close();
            }
        } catch (IOException e) {
        }
        ResponseBody responseBody = this.responseBody;
        if (responseBody != null) {
            responseBody.close();
        }
        this.callback = null;
    }

    public void cancel() {
        Call local = this.call;
        if (local != null) {
            local.cancel();
        }
    }

    @NonNull
    public Class<InputStream> getDataClass() {
        return InputStream.class;
    }

    @NonNull
    public DataSource getDataSource() {
        return DataSource.REMOTE;
    }
}
