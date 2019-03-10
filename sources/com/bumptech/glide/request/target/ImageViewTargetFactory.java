package com.bumptech.glide.request.target;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.widget.ImageView;

public class ImageViewTargetFactory {
    @NonNull
    public <Z> ViewTarget<ImageView, Z> buildTarget(@NonNull ImageView view, @NonNull Class<Z> clazz) {
        if (Bitmap.class.equals(clazz)) {
            return new BitmapImageViewTarget(view);
        }
        if (Drawable.class.isAssignableFrom(clazz)) {
            return new DrawableImageViewTarget(view);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Unhandled class: ");
        stringBuilder.append(clazz);
        stringBuilder.append(", try .as*(Class).transcode(ResourceTranscoder)");
        throw new IllegalArgumentException(stringBuilder.toString());
    }
}
