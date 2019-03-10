package com.bumptech.glide.load.resource.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.util.Util;

public abstract class BitmapTransformation implements Transformation<Bitmap> {
    protected abstract Bitmap transform(@NonNull BitmapPool bitmapPool, @NonNull Bitmap bitmap, int i, int i2);

    @NonNull
    public final Resource<Bitmap> transform(@NonNull Context context, @NonNull Resource<Bitmap> resource, int outWidth, int outHeight) {
        if (Util.isValidDimensions(outWidth, outHeight)) {
            BitmapPool bitmapPool = Glide.get(context).getBitmapPool();
            Bitmap toTransform = (Bitmap) resource.get();
            Bitmap transformed = transform(bitmapPool, toTransform, outWidth == Integer.MIN_VALUE ? toTransform.getWidth() : outWidth, outHeight == Integer.MIN_VALUE ? toTransform.getHeight() : outHeight);
            if (toTransform.equals(transformed)) {
                return resource;
            }
            return BitmapResource.obtain(transformed, bitmapPool);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Cannot apply transformation on width: ");
        stringBuilder.append(outWidth);
        stringBuilder.append(" or height: ");
        stringBuilder.append(outHeight);
        stringBuilder.append(" less than or equal to zero and not Target.SIZE_ORIGINAL");
        throw new IllegalArgumentException(stringBuilder.toString());
    }
}
