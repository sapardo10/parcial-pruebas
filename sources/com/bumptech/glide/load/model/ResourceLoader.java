package com.bumptech.glide.load.model;

import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.ModelLoader.LoadData;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;

public class ResourceLoader<Data> implements ModelLoader<Integer, Data> {
    private static final String TAG = "ResourceLoader";
    private final Resources resources;
    private final ModelLoader<Uri, Data> uriLoader;

    public static final class AssetFileDescriptorFactory implements ModelLoaderFactory<Integer, AssetFileDescriptor> {
        private final Resources resources;

        public AssetFileDescriptorFactory(Resources resources) {
            this.resources = resources;
        }

        public ModelLoader<Integer, AssetFileDescriptor> build(MultiModelLoaderFactory multiFactory) {
            return new ResourceLoader(this.resources, multiFactory.build(Uri.class, AssetFileDescriptor.class));
        }

        public void teardown() {
        }
    }

    public static class FileDescriptorFactory implements ModelLoaderFactory<Integer, ParcelFileDescriptor> {
        private final Resources resources;

        public FileDescriptorFactory(Resources resources) {
            this.resources = resources;
        }

        @NonNull
        public ModelLoader<Integer, ParcelFileDescriptor> build(MultiModelLoaderFactory multiFactory) {
            return new ResourceLoader(this.resources, multiFactory.build(Uri.class, ParcelFileDescriptor.class));
        }

        public void teardown() {
        }
    }

    public static class StreamFactory implements ModelLoaderFactory<Integer, InputStream> {
        private final Resources resources;

        public StreamFactory(Resources resources) {
            this.resources = resources;
        }

        @NonNull
        public ModelLoader<Integer, InputStream> build(MultiModelLoaderFactory multiFactory) {
            return new ResourceLoader(this.resources, multiFactory.build(Uri.class, InputStream.class));
        }

        public void teardown() {
        }
    }

    public static class UriFactory implements ModelLoaderFactory<Integer, Uri> {
        private final Resources resources;

        public UriFactory(Resources resources) {
            this.resources = resources;
        }

        @NonNull
        public ModelLoader<Integer, Uri> build(MultiModelLoaderFactory multiFactory) {
            return new ResourceLoader(this.resources, UnitModelLoader.getInstance());
        }

        public void teardown() {
        }
    }

    public ResourceLoader(Resources resources, ModelLoader<Uri, Data> uriLoader) {
        this.resources = resources;
        this.uriLoader = uriLoader;
    }

    public LoadData<Data> buildLoadData(@NonNull Integer model, int width, int height, @NonNull Options options) {
        Uri uri = getResourceUri(model);
        return uri == null ? null : this.uriLoader.buildLoadData(uri, width, height, options);
    }

    @Nullable
    private Uri getResourceUri(Integer model) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("android.resource://");
            stringBuilder.append(this.resources.getResourcePackageName(model.intValue()));
            stringBuilder.append(IOUtils.DIR_SEPARATOR_UNIX);
            stringBuilder.append(this.resources.getResourceTypeName(model.intValue()));
            stringBuilder.append(IOUtils.DIR_SEPARATOR_UNIX);
            stringBuilder.append(this.resources.getResourceEntryName(model.intValue()));
            return Uri.parse(stringBuilder.toString());
        } catch (NotFoundException e) {
            if (Log.isLoggable(TAG, 5)) {
                String str = TAG;
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Received invalid resource id: ");
                stringBuilder2.append(model);
                Log.w(str, stringBuilder2.toString(), e);
            }
            return null;
        }
    }

    public boolean handles(@NonNull Integer model) {
        return true;
    }
}
