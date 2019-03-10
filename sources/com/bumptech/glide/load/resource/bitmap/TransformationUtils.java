package com.bumptech.glide.load.resource.bitmap;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.os.Build;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.util.Preconditions;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class TransformationUtils {
    private static final Lock BITMAP_DRAWABLE_LOCK = (MODELS_REQUIRING_BITMAP_LOCK.contains(Build.MODEL) ? new ReentrantLock() : new NoLock());
    private static final Paint CIRCLE_CROP_BITMAP_PAINT = new Paint(7);
    private static final int CIRCLE_CROP_PAINT_FLAGS = 7;
    private static final Paint CIRCLE_CROP_SHAPE_PAINT = new Paint(7);
    private static final Paint DEFAULT_PAINT = new Paint(6);
    private static final Set<String> MODELS_REQUIRING_BITMAP_LOCK = new HashSet(Arrays.asList(new String[]{"XT1085", "XT1092", "XT1093", "XT1094", "XT1095", "XT1096", "XT1097", "XT1098", "XT1031", "XT1028", "XT937C", "XT1032", "XT1008", "XT1033", "XT1035", "XT1034", "XT939G", "XT1039", "XT1040", "XT1042", "XT1045", "XT1063", "XT1064", "XT1068", "XT1069", "XT1072", "XT1077", "XT1078", "XT1079"}));
    public static final int PAINT_FLAGS = 6;
    private static final String TAG = "TransformationUtils";

    private static final class NoLock implements Lock {
        NoLock() {
        }

        public void lock() {
        }

        public void lockInterruptibly() throws InterruptedException {
        }

        public boolean tryLock() {
            return true;
        }

        public boolean tryLock(long time, @NonNull TimeUnit unit) throws InterruptedException {
            return true;
        }

        public void unlock() {
        }

        @NonNull
        public Condition newCondition() {
            throw new UnsupportedOperationException("Should not be called");
        }
    }

    static {
        CIRCLE_CROP_BITMAP_PAINT.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
    }

    private TransformationUtils() {
    }

    public static Lock getBitmapDrawableLock() {
        return BITMAP_DRAWABLE_LOCK;
    }

    public static Bitmap centerCrop(@NonNull BitmapPool pool, @NonNull Bitmap inBitmap, int width, int height) {
        if (inBitmap.getWidth() == width && inBitmap.getHeight() == height) {
            return inBitmap;
        }
        float scale;
        float dx;
        float dy;
        Matrix m = new Matrix();
        if (inBitmap.getWidth() * height > inBitmap.getHeight() * width) {
            scale = ((float) height) / ((float) inBitmap.getHeight());
            dx = (((float) width) - (((float) inBitmap.getWidth()) * scale)) * 0.5f;
            dy = 0.0f;
        } else {
            scale = ((float) width) / ((float) inBitmap.getWidth());
            dx = 0.0f;
            dy = (((float) height) - (((float) inBitmap.getHeight()) * scale)) * 0.5f;
        }
        m.setScale(scale, scale);
        m.postTranslate((float) ((int) (dx + 0.5f)), (float) ((int) (0.5f + dy)));
        Bitmap result = pool.get(width, height, getNonNullConfig(inBitmap));
        setAlpha(inBitmap, result);
        applyMatrix(inBitmap, result, m);
        return result;
    }

    public static Bitmap fitCenter(@NonNull BitmapPool pool, @NonNull Bitmap inBitmap, int width, int height) {
        if (inBitmap.getWidth() == width && inBitmap.getHeight() == height) {
            if (Log.isLoggable(TAG, 2)) {
                Log.v(TAG, "requested target size matches input, returning input");
            }
            return inBitmap;
        }
        float minPercentage = Math.min(((float) width) / ((float) inBitmap.getWidth()), ((float) height) / ((float) inBitmap.getHeight()));
        int targetWidth = Math.round(((float) inBitmap.getWidth()) * minPercentage);
        int targetHeight = Math.round(((float) inBitmap.getHeight()) * minPercentage);
        if (inBitmap.getWidth() == targetWidth && inBitmap.getHeight() == targetHeight) {
            if (Log.isLoggable(TAG, 2)) {
                Log.v(TAG, "adjusted target size matches input, returning input");
            }
            return inBitmap;
        }
        Bitmap toReuse = pool.get((int) (((float) inBitmap.getWidth()) * minPercentage), (int) (((float) inBitmap.getHeight()) * minPercentage), getNonNullConfig(inBitmap));
        setAlpha(inBitmap, toReuse);
        if (Log.isLoggable(TAG, 2)) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("request: ");
            stringBuilder.append(width);
            stringBuilder.append("x");
            stringBuilder.append(height);
            Log.v(str, stringBuilder.toString());
            str = TAG;
            stringBuilder = new StringBuilder();
            stringBuilder.append("toFit:   ");
            stringBuilder.append(inBitmap.getWidth());
            stringBuilder.append("x");
            stringBuilder.append(inBitmap.getHeight());
            Log.v(str, stringBuilder.toString());
            str = TAG;
            stringBuilder = new StringBuilder();
            stringBuilder.append("toReuse: ");
            stringBuilder.append(toReuse.getWidth());
            stringBuilder.append("x");
            stringBuilder.append(toReuse.getHeight());
            Log.v(str, stringBuilder.toString());
            str = TAG;
            stringBuilder = new StringBuilder();
            stringBuilder.append("minPct:   ");
            stringBuilder.append(minPercentage);
            Log.v(str, stringBuilder.toString());
        }
        Matrix matrix = new Matrix();
        matrix.setScale(minPercentage, minPercentage);
        applyMatrix(inBitmap, toReuse, matrix);
        return toReuse;
    }

    public static Bitmap centerInside(@NonNull BitmapPool pool, @NonNull Bitmap inBitmap, int width, int height) {
        if (inBitmap.getWidth() > width || inBitmap.getHeight() > height) {
            if (Log.isLoggable(TAG, 2)) {
                Log.v(TAG, "requested target size too big for input, fit centering instead");
            }
            return fitCenter(pool, inBitmap, width, height);
        }
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "requested target size larger or equal to input, returning input");
        }
        return inBitmap;
    }

    public static void setAlpha(Bitmap inBitmap, Bitmap outBitmap) {
        outBitmap.setHasAlpha(inBitmap.hasAlpha());
    }

    public static Bitmap rotateImage(@NonNull Bitmap imageToOrient, int degreesToRotate) {
        Bitmap result = imageToOrient;
        if (degreesToRotate != 0) {
            try {
                Matrix matrix = new Matrix();
                matrix.setRotate((float) degreesToRotate);
                result = Bitmap.createBitmap(imageToOrient, 0, 0, imageToOrient.getWidth(), imageToOrient.getHeight(), matrix, true);
            } catch (Exception e) {
                if (Log.isLoggable(TAG, 6)) {
                    Log.e(TAG, "Exception when trying to orient image", e);
                }
            }
        }
        return result;
    }

    public static int getExifOrientationDegrees(int exifOrientation) {
        switch (exifOrientation) {
            case 3:
            case 4:
                return 180;
            case 5:
            case 6:
                return 90;
            case 7:
            case 8:
                return 270;
            default:
                return 0;
        }
    }

    public static Bitmap rotateImageExif(@NonNull BitmapPool pool, @NonNull Bitmap inBitmap, int exifOrientation) {
        if (!isExifOrientationRequired(exifOrientation)) {
            return inBitmap;
        }
        Matrix matrix = new Matrix();
        initializeMatrixForRotation(exifOrientation, matrix);
        RectF newRect = new RectF(0.0f, 0.0f, (float) inBitmap.getWidth(), (float) inBitmap.getHeight());
        matrix.mapRect(newRect);
        Bitmap result = pool.get(Math.round(newRect.width()), Math.round(newRect.height()), getNonNullConfig(inBitmap));
        matrix.postTranslate(-newRect.left, -newRect.top);
        applyMatrix(inBitmap, result, matrix);
        return result;
    }

    public static boolean isExifOrientationRequired(int exifOrientation) {
        switch (exifOrientation) {
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                return true;
            default:
                return false;
        }
    }

    public static Bitmap circleCrop(@NonNull BitmapPool pool, @NonNull Bitmap inBitmap, int destWidth, int destHeight) {
        Throwable th;
        Bitmap bitmap;
        float f;
        BitmapPool bitmapPool = pool;
        int destMinEdge = Math.min(destWidth, destHeight);
        float radius = ((float) destMinEdge) / 2.0f;
        int srcWidth = inBitmap.getWidth();
        int srcHeight = inBitmap.getHeight();
        float maxScale = Math.max(((float) destMinEdge) / ((float) srcWidth), ((float) destMinEdge) / ((float) srcHeight));
        float scaledWidth = maxScale * ((float) srcWidth);
        float scaledHeight = maxScale * ((float) srcHeight);
        float left = (((float) destMinEdge) - scaledWidth) / 2.0f;
        float top = (((float) destMinEdge) - scaledHeight) / 2.0f;
        RectF destRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);
        Bitmap toTransform = getAlphaSafeBitmap(pool, inBitmap);
        Bitmap result = bitmapPool.get(destMinEdge, destMinEdge, getAlphaSafeConfig(inBitmap));
        result.setHasAlpha(true);
        BITMAP_DRAWABLE_LOCK.lock();
        try {
            Canvas canvas = new Canvas(result);
            try {
                canvas.drawCircle(radius, radius, radius, CIRCLE_CROP_SHAPE_PAINT);
                try {
                    canvas.drawBitmap(toTransform, 0.0f, destRect, CIRCLE_CROP_BITMAP_PAINT);
                    clear(canvas);
                    BITMAP_DRAWABLE_LOCK.unlock();
                    if (!toTransform.equals(inBitmap)) {
                        bitmapPool.put(toTransform);
                    }
                    return result;
                } catch (Throwable th2) {
                    th = th2;
                    bitmap = inBitmap;
                    BITMAP_DRAWABLE_LOCK.unlock();
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                bitmap = inBitmap;
                f = radius;
                BITMAP_DRAWABLE_LOCK.unlock();
                throw th;
            }
        } catch (Throwable th4) {
            th = th4;
            int i = destMinEdge;
            f = radius;
            destMinEdge = inBitmap;
            BITMAP_DRAWABLE_LOCK.unlock();
            throw th;
        }
    }

    private static Bitmap getAlphaSafeBitmap(@NonNull BitmapPool pool, @NonNull Bitmap maybeAlphaSafe) {
        Config safeConfig = getAlphaSafeConfig(maybeAlphaSafe);
        if (safeConfig.equals(maybeAlphaSafe.getConfig())) {
            return maybeAlphaSafe;
        }
        Bitmap argbBitmap = pool.get(maybeAlphaSafe.getWidth(), maybeAlphaSafe.getHeight(), safeConfig);
        new Canvas(argbBitmap).drawBitmap(maybeAlphaSafe, 0.0f, 0.0f, null);
        return argbBitmap;
    }

    @NonNull
    private static Config getAlphaSafeConfig(@NonNull Bitmap inBitmap) {
        if (VERSION.SDK_INT >= 26) {
            if (Config.RGBA_F16.equals(inBitmap.getConfig())) {
                return Config.RGBA_F16;
            }
        }
        return Config.ARGB_8888;
    }

    @Deprecated
    public static Bitmap roundedCorners(@NonNull BitmapPool pool, @NonNull Bitmap inBitmap, int width, int height, int roundingRadius) {
        return roundedCorners(pool, inBitmap, roundingRadius);
    }

    public static Bitmap roundedCorners(@NonNull BitmapPool pool, @NonNull Bitmap inBitmap, int roundingRadius) {
        Preconditions.checkArgument(roundingRadius > 0, "roundingRadius must be greater than 0.");
        Config safeConfig = getAlphaSafeConfig(inBitmap);
        Bitmap toTransform = getAlphaSafeBitmap(pool, inBitmap);
        Bitmap result = pool.get(toTransform.getWidth(), toTransform.getHeight(), safeConfig);
        result.setHasAlpha(true);
        BitmapShader shader = new BitmapShader(toTransform, TileMode.CLAMP, TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(shader);
        RectF rect = new RectF(0.0f, 0.0f, (float) result.getWidth(), (float) result.getHeight());
        BITMAP_DRAWABLE_LOCK.lock();
        try {
            Canvas canvas = new Canvas(result);
            canvas.drawColor(0, Mode.CLEAR);
            canvas.drawRoundRect(rect, (float) roundingRadius, (float) roundingRadius, paint);
            clear(canvas);
            if (!toTransform.equals(inBitmap)) {
                pool.put(toTransform);
            }
            return result;
        } finally {
            BITMAP_DRAWABLE_LOCK.unlock();
        }
    }

    private static void clear(Canvas canvas) {
        canvas.setBitmap(null);
    }

    @NonNull
    private static Config getNonNullConfig(@NonNull Bitmap bitmap) {
        return bitmap.getConfig() != null ? bitmap.getConfig() : Config.ARGB_8888;
    }

    private static void applyMatrix(@NonNull Bitmap inBitmap, @NonNull Bitmap targetBitmap, Matrix matrix) {
        BITMAP_DRAWABLE_LOCK.lock();
        try {
            Canvas canvas = new Canvas(targetBitmap);
            canvas.drawBitmap(inBitmap, matrix, DEFAULT_PAINT);
            clear(canvas);
        } finally {
            BITMAP_DRAWABLE_LOCK.unlock();
        }
    }

    @VisibleForTesting
    static void initializeMatrixForRotation(int exifOrientation, Matrix matrix) {
        switch (exifOrientation) {
            case 2:
                matrix.setScale(-1.0f, 1.0f);
                return;
            case 3:
                matrix.setRotate(180.0f);
                return;
            case 4:
                matrix.setRotate(180.0f);
                matrix.postScale(-1.0f, 1.0f);
                return;
            case 5:
                matrix.setRotate(90.0f);
                matrix.postScale(-1.0f, 1.0f);
                return;
            case 6:
                matrix.setRotate(90.0f);
                return;
            case 7:
                matrix.setRotate(-90.0f);
                matrix.postScale(-1.0f, 1.0f);
                return;
            case 8:
                matrix.setRotate(-90.0f);
                return;
            default:
                return;
        }
    }
}
