package com.bumptech.glide.load.resource.gif;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.bumptech.glide.gifdecoder.GifDecoder.BitmapProvider;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;

public final class GifBitmapProvider implements BitmapProvider {
    @Nullable
    private final ArrayPool arrayPool;
    private final BitmapPool bitmapPool;

    public GifBitmapProvider(BitmapPool bitmapPool) {
        this(bitmapPool, null);
    }

    public GifBitmapProvider(BitmapPool bitmapPool, @Nullable ArrayPool arrayPool) {
        this.bitmapPool = bitmapPool;
        this.arrayPool = arrayPool;
    }

    @NonNull
    public Bitmap obtain(int width, int height, @NonNull Config config) {
        return this.bitmapPool.getDirty(width, height, config);
    }

    public void release(@NonNull Bitmap bitmap) {
        this.bitmapPool.put(bitmap);
    }

    @NonNull
    public byte[] obtainByteArray(int size) {
        ArrayPool arrayPool = this.arrayPool;
        if (arrayPool == null) {
            return new byte[size];
        }
        return (byte[]) arrayPool.get(size, byte[].class);
    }

    public void release(@NonNull byte[] bytes) {
        ArrayPool arrayPool = this.arrayPool;
        if (arrayPool != null) {
            arrayPool.put(bytes);
        }
    }

    @NonNull
    public int[] obtainIntArray(int size) {
        ArrayPool arrayPool = this.arrayPool;
        if (arrayPool == null) {
            return new int[size];
        }
        return (int[]) arrayPool.get(size, int[].class);
    }

    public void release(@NonNull int[] array) {
        ArrayPool arrayPool = this.arrayPool;
        if (arrayPool != null) {
            arrayPool.put(array);
        }
    }
}
