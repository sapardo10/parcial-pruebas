package com.bumptech.glide.load.model;

import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.ModelLoader.LoadData;
import java.io.File;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;

public class StringLoader<Data> implements ModelLoader<String, Data> {
    private final ModelLoader<Uri, Data> uriLoader;

    public static final class AssetFileDescriptorFactory implements ModelLoaderFactory<String, AssetFileDescriptor> {
        public ModelLoader<String, AssetFileDescriptor> build(@NonNull MultiModelLoaderFactory multiFactory) {
            return new StringLoader(multiFactory.build(Uri.class, AssetFileDescriptor.class));
        }

        public void teardown() {
        }
    }

    public static class FileDescriptorFactory implements ModelLoaderFactory<String, ParcelFileDescriptor> {
        @NonNull
        public ModelLoader<String, ParcelFileDescriptor> build(@NonNull MultiModelLoaderFactory multiFactory) {
            return new StringLoader(multiFactory.build(Uri.class, ParcelFileDescriptor.class));
        }

        public void teardown() {
        }
    }

    public static class StreamFactory implements ModelLoaderFactory<String, InputStream> {
        @NonNull
        public ModelLoader<String, InputStream> build(@NonNull MultiModelLoaderFactory multiFactory) {
            return new StringLoader(multiFactory.build(Uri.class, InputStream.class));
        }

        public void teardown() {
        }
    }

    public StringLoader(ModelLoader<Uri, Data> uriLoader) {
        this.uriLoader = uriLoader;
    }

    public LoadData<Data> buildLoadData(@NonNull String model, int width, int height, @NonNull Options options) {
        Uri uri = parseUri(model);
        if (uri != null) {
            if (this.uriLoader.handles(uri)) {
                return this.uriLoader.buildLoadData(uri, width, height, options);
            }
        }
        return null;
    }

    public boolean handles(@NonNull String model) {
        return true;
    }

    @Nullable
    private static Uri parseUri(String model) {
        if (TextUtils.isEmpty(model)) {
            return null;
        }
        Uri uri;
        if (model.charAt(0) == IOUtils.DIR_SEPARATOR_UNIX) {
            uri = toFileUri(model);
        } else {
            uri = Uri.parse(model);
            if (uri.getScheme() == null) {
                uri = toFileUri(model);
            }
        }
        return uri;
    }

    private static Uri toFileUri(String path) {
        return Uri.fromFile(new File(path));
    }
}
