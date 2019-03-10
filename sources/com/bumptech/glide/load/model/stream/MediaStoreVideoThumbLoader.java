package com.bumptech.glide.load.model.stream;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.mediastore.MediaStoreUtil;
import com.bumptech.glide.load.data.mediastore.ThumbFetcher;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoader.LoadData;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.bumptech.glide.load.resource.bitmap.VideoDecoder;
import com.bumptech.glide.signature.ObjectKey;
import java.io.InputStream;

public class MediaStoreVideoThumbLoader implements ModelLoader<Uri, InputStream> {
    private final Context context;

    public static class Factory implements ModelLoaderFactory<Uri, InputStream> {
        private final Context context;

        public Factory(Context context) {
            this.context = context;
        }

        @NonNull
        public ModelLoader<Uri, InputStream> build(MultiModelLoaderFactory multiFactory) {
            return new MediaStoreVideoThumbLoader(this.context);
        }

        public void teardown() {
        }
    }

    public MediaStoreVideoThumbLoader(Context context) {
        this.context = context.getApplicationContext();
    }

    @Nullable
    public LoadData<InputStream> buildLoadData(@NonNull Uri model, int width, int height, @NonNull Options options) {
        if (MediaStoreUtil.isThumbnailSize(width, height) && isRequestingDefaultFrame(options)) {
            return new LoadData(new ObjectKey(model), ThumbFetcher.buildVideoFetcher(this.context, model));
        }
        return null;
    }

    private boolean isRequestingDefaultFrame(Options options) {
        Long specifiedFrame = (Long) options.get(VideoDecoder.TARGET_FRAME);
        return specifiedFrame != null && specifiedFrame.longValue() == -1;
    }

    public boolean handles(@NonNull Uri model) {
        return MediaStoreUtil.isMediaStoreVideoUri(model);
    }
}
