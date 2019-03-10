package com.bumptech.glide.integration.okhttp3;

import android.support.annotation.NonNull;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoader.LoadData;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import java.io.InputStream;
import okhttp3.Call$Factory;
import okhttp3.OkHttpClient;

public class OkHttpUrlLoader implements ModelLoader<GlideUrl, InputStream> {
    private final Call$Factory client;

    public static class Factory implements ModelLoaderFactory<GlideUrl, InputStream> {
        private static volatile Call$Factory internalClient;
        private final Call$Factory client;

        private static Call$Factory getInternalClient() {
            if (internalClient == null) {
                synchronized (Factory.class) {
                    if (internalClient == null) {
                        internalClient = new OkHttpClient();
                    }
                }
            }
            return internalClient;
        }

        public Factory() {
            this(getInternalClient());
        }

        public Factory(@NonNull Call$Factory client) {
            this.client = client;
        }

        @NonNull
        public ModelLoader<GlideUrl, InputStream> build(MultiModelLoaderFactory multiFactory) {
            return new OkHttpUrlLoader(this.client);
        }

        public void teardown() {
        }
    }

    public OkHttpUrlLoader(@NonNull Call$Factory client) {
        this.client = client;
    }

    public boolean handles(@NonNull GlideUrl url) {
        return true;
    }

    public LoadData<InputStream> buildLoadData(@NonNull GlideUrl model, int width, int height, @NonNull Options options) {
        return new LoadData(model, new OkHttpStreamFetcher(this.client, model));
    }
}
