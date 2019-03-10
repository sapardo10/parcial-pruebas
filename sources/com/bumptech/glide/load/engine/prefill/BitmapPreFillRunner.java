package com.bumptech.glide.load.engine.prefill;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.engine.cache.MemoryCache;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;
import com.bumptech.glide.util.Util;
import java.security.MessageDigest;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

final class BitmapPreFillRunner implements Runnable {
    static final int BACKOFF_RATIO = 4;
    private static final Clock DEFAULT_CLOCK = new Clock();
    static final long INITIAL_BACKOFF_MS = 40;
    static final long MAX_BACKOFF_MS = TimeUnit.SECONDS.toMillis(1);
    static final long MAX_DURATION_MS = 32;
    @VisibleForTesting
    static final String TAG = "PreFillRunner";
    private final BitmapPool bitmapPool;
    private final Clock clock;
    private long currentDelay;
    private final Handler handler;
    private boolean isCancelled;
    private final MemoryCache memoryCache;
    private final Set<PreFillType> seenTypes;
    private final PreFillQueue toPrefill;

    @VisibleForTesting
    static class Clock {
        Clock() {
        }

        long now() {
            return SystemClock.currentThreadTimeMillis();
        }
    }

    private static final class UniqueKey implements Key {
        UniqueKey() {
        }

        public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
            throw new UnsupportedOperationException();
        }
    }

    public BitmapPreFillRunner(BitmapPool bitmapPool, MemoryCache memoryCache, PreFillQueue allocationOrder) {
        this(bitmapPool, memoryCache, allocationOrder, DEFAULT_CLOCK, new Handler(Looper.getMainLooper()));
    }

    @VisibleForTesting
    BitmapPreFillRunner(BitmapPool bitmapPool, MemoryCache memoryCache, PreFillQueue allocationOrder, Clock clock, Handler handler) {
        this.seenTypes = new HashSet();
        this.currentDelay = INITIAL_BACKOFF_MS;
        this.bitmapPool = bitmapPool;
        this.memoryCache = memoryCache;
        this.toPrefill = allocationOrder;
        this.clock = clock;
        this.handler = handler;
    }

    public void cancel() {
        this.isCancelled = true;
    }

    @VisibleForTesting
    boolean allocate() {
        long start = this.clock.now();
        while (!this.toPrefill.isEmpty() && !isGcDetected(start)) {
            Bitmap bitmap;
            PreFillType toAllocate = this.toPrefill.remove();
            if (this.seenTypes.contains(toAllocate)) {
                bitmap = Bitmap.createBitmap(toAllocate.getWidth(), toAllocate.getHeight(), toAllocate.getConfig());
            } else {
                this.seenTypes.add(toAllocate);
                bitmap = this.bitmapPool.getDirty(toAllocate.getWidth(), toAllocate.getHeight(), toAllocate.getConfig());
            }
            int bitmapSize = Util.getBitmapByteSize(bitmap);
            if (getFreeMemoryCacheBytes() >= ((long) bitmapSize)) {
                this.memoryCache.put(new UniqueKey(), BitmapResource.obtain(bitmap, this.bitmapPool));
            } else {
                this.bitmapPool.put(bitmap);
            }
            if (Log.isLoggable(TAG, 3)) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("allocated [");
                stringBuilder.append(toAllocate.getWidth());
                stringBuilder.append("x");
                stringBuilder.append(toAllocate.getHeight());
                stringBuilder.append("] ");
                stringBuilder.append(toAllocate.getConfig());
                stringBuilder.append(" size: ");
                stringBuilder.append(bitmapSize);
                Log.d(str, stringBuilder.toString());
            }
        }
        return (this.isCancelled || this.toPrefill.isEmpty()) ? false : true;
    }

    private boolean isGcDetected(long startTimeMs) {
        return this.clock.now() - startTimeMs >= 32;
    }

    private long getFreeMemoryCacheBytes() {
        return this.memoryCache.getMaxSize() - this.memoryCache.getCurrentSize();
    }

    public void run() {
        if (allocate()) {
            this.handler.postDelayed(this, getNextDelay());
        }
    }

    private long getNextDelay() {
        long result = this.currentDelay;
        this.currentDelay = Math.min(this.currentDelay * 4, MAX_BACKOFF_MS);
        return result;
    }
}
