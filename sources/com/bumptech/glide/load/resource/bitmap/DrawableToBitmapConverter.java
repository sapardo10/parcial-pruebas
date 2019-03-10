package com.bumptech.glide.load.resource.bitmap;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.Log;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPoolAdapter;
import java.util.concurrent.locks.Lock;

final class DrawableToBitmapConverter {
    private static final BitmapPool NO_RECYCLE_BITMAP_POOL = new C11141();
    private static final String TAG = "DrawableToBitmap";

    /* renamed from: com.bumptech.glide.load.resource.bitmap.DrawableToBitmapConverter$1 */
    class C11141 extends BitmapPoolAdapter {
        C11141() {
        }

        public void put(Bitmap bitmap) {
        }
    }

    private DrawableToBitmapConverter() {
    }

    @Nullable
    static Resource<Bitmap> convert(BitmapPool bitmapPool, Drawable drawable, int width, int height) {
        drawable = drawable.getCurrent();
        Bitmap result = null;
        boolean isRecycleable = false;
        if (drawable instanceof BitmapDrawable) {
            result = ((BitmapDrawable) drawable).getBitmap();
        } else if (!(drawable instanceof Animatable)) {
            result = drawToBitmap(bitmapPool, drawable, width, height);
            isRecycleable = true;
        }
        return BitmapResource.obtain(result, isRecycleable ? bitmapPool : NO_RECYCLE_BITMAP_POOL);
    }

    @Nullable
    private static Bitmap drawToBitmap(BitmapPool bitmapPool, Drawable drawable, int width, int height) {
        String str;
        StringBuilder stringBuilder;
        if (width == Integer.MIN_VALUE && drawable.getIntrinsicWidth() <= 0) {
            if (Log.isLoggable(TAG, 5)) {
                str = TAG;
                stringBuilder = new StringBuilder();
                stringBuilder.append("Unable to draw ");
                stringBuilder.append(drawable);
                stringBuilder.append(" to Bitmap with Target.SIZE_ORIGINAL because the Drawable has no intrinsic width");
                Log.w(str, stringBuilder.toString());
            }
            return null;
        } else if (height != Integer.MIN_VALUE || drawable.getIntrinsicHeight() > 0) {
            int targetWidth = drawable.getIntrinsicWidth() > 0 ? drawable.getIntrinsicWidth() : width;
            int targetHeight = drawable.getIntrinsicHeight() > 0 ? drawable.getIntrinsicHeight() : height;
            Lock lock = TransformationUtils.getBitmapDrawableLock();
            lock.lock();
            Bitmap result = bitmapPool.get(targetWidth, targetHeight, Config.ARGB_8888);
            try {
                Canvas canvas = new Canvas(result);
                drawable.setBounds(0, 0, targetWidth, targetHeight);
                drawable.draw(canvas);
                canvas.setBitmap(null);
                return result;
            } finally {
                lock.unlock();
            }
        } else {
            if (Log.isLoggable(TAG, 5)) {
                str = TAG;
                stringBuilder = new StringBuilder();
                stringBuilder.append("Unable to draw ");
                stringBuilder.append(drawable);
                stringBuilder.append(" to Bitmap with Target.SIZE_ORIGINAL because the Drawable has no intrinsic height");
                Log.w(str, stringBuilder.toString());
            }
            return null;
        }
    }
}
