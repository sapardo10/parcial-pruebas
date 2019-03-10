package com.bumptech.glide.load.resource.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.util.Preconditions;
import java.security.MessageDigest;

@Deprecated
public class BitmapDrawableTransformation implements Transformation<BitmapDrawable> {
    private final Transformation<Drawable> wrapped;

    public BitmapDrawableTransformation(Transformation<Bitmap> wrapped) {
        this.wrapped = (Transformation) Preconditions.checkNotNull(new DrawableTransformation(wrapped, false));
    }

    @NonNull
    public Resource<BitmapDrawable> transform(@NonNull Context context, @NonNull Resource<BitmapDrawable> drawableResourceToTransform, int outWidth, int outHeight) {
        return convertToBitmapDrawableResource(this.wrapped.transform(context, convertToDrawableResource(drawableResourceToTransform), outWidth, outHeight));
    }

    private static Resource<BitmapDrawable> convertToBitmapDrawableResource(Resource<Drawable> resource) {
        if (resource.get() instanceof BitmapDrawable) {
            return resource;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Wrapped transformation unexpectedly returned a non BitmapDrawable resource: ");
        stringBuilder.append(resource.get());
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    private static Resource<Drawable> convertToDrawableResource(Resource<BitmapDrawable> toConvert) {
        return toConvert;
    }

    public boolean equals(Object o) {
        if (!(o instanceof BitmapDrawableTransformation)) {
            return false;
        }
        return this.wrapped.equals(((BitmapDrawableTransformation) o).wrapped);
    }

    public int hashCode() {
        return this.wrapped.hashCode();
    }

    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        this.wrapped.updateDiskCacheKey(messageDigest);
    }
}
