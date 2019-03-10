package com.bumptech.glide.load.model;

import android.content.res.AssetManager;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.data.FileDescriptorAssetPathFetcher;
import com.bumptech.glide.load.data.StreamAssetPathFetcher;
import com.bumptech.glide.load.model.ModelLoader.LoadData;
import com.bumptech.glide.signature.ObjectKey;
import java.io.InputStream;

public class AssetUriLoader<Data> implements ModelLoader<Uri, Data> {
    private static final String ASSET_PATH_SEGMENT = "android_asset";
    private static final String ASSET_PREFIX = "file:///android_asset/";
    private static final int ASSET_PREFIX_LENGTH = ASSET_PREFIX.length();
    private final AssetManager assetManager;
    private final AssetFetcherFactory<Data> factory;

    public interface AssetFetcherFactory<Data> {
        DataFetcher<Data> buildFetcher(AssetManager assetManager, String str);
    }

    public static class FileDescriptorFactory implements ModelLoaderFactory<Uri, ParcelFileDescriptor>, AssetFetcherFactory<ParcelFileDescriptor> {
        private final AssetManager assetManager;

        public FileDescriptorFactory(AssetManager assetManager) {
            this.assetManager = assetManager;
        }

        @NonNull
        public ModelLoader<Uri, ParcelFileDescriptor> build(MultiModelLoaderFactory multiFactory) {
            return new AssetUriLoader(this.assetManager, this);
        }

        public void teardown() {
        }

        public DataFetcher<ParcelFileDescriptor> buildFetcher(AssetManager assetManager, String assetPath) {
            return new FileDescriptorAssetPathFetcher(assetManager, assetPath);
        }
    }

    public static class StreamFactory implements ModelLoaderFactory<Uri, InputStream>, AssetFetcherFactory<InputStream> {
        private final AssetManager assetManager;

        public StreamFactory(AssetManager assetManager) {
            this.assetManager = assetManager;
        }

        @NonNull
        public ModelLoader<Uri, InputStream> build(MultiModelLoaderFactory multiFactory) {
            return new AssetUriLoader(this.assetManager, this);
        }

        public void teardown() {
        }

        public DataFetcher<InputStream> buildFetcher(AssetManager assetManager, String assetPath) {
            return new StreamAssetPathFetcher(assetManager, assetPath);
        }
    }

    public AssetUriLoader(AssetManager assetManager, AssetFetcherFactory<Data> factory) {
        this.assetManager = assetManager;
        this.factory = factory;
    }

    public LoadData<Data> buildLoadData(@NonNull Uri model, int width, int height, @NonNull Options options) {
        return new LoadData(new ObjectKey(model), this.factory.buildFetcher(this.assetManager, model.toString().substring(ASSET_PREFIX_LENGTH)));
    }

    public boolean handles(@NonNull Uri model) {
        if ("file".equals(model.getScheme())) {
            if (!model.getPathSegments().isEmpty() && ASSET_PATH_SEGMENT.equals(model.getPathSegments().get(0))) {
                return true;
            }
        }
        return false;
    }
}
