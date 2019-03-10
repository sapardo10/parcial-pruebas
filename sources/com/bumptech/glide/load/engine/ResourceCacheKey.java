package com.bumptech.glide.load.engine;

import android.support.annotation.NonNull;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.util.LruCache;
import com.bumptech.glide.util.Util;
import java.nio.ByteBuffer;
import java.security.MessageDigest;

final class ResourceCacheKey implements Key {
    private static final LruCache<Class<?>, byte[]> RESOURCE_CLASS_BYTES = new LruCache(50);
    private final ArrayPool arrayPool;
    private final Class<?> decodedResourceClass;
    private final int height;
    private final Options options;
    private final Key signature;
    private final Key sourceKey;
    private final Transformation<?> transformation;
    private final int width;

    ResourceCacheKey(ArrayPool arrayPool, Key sourceKey, Key signature, int width, int height, Transformation<?> appliedTransformation, Class<?> decodedResourceClass, Options options) {
        this.arrayPool = arrayPool;
        this.sourceKey = sourceKey;
        this.signature = signature;
        this.width = width;
        this.height = height;
        this.transformation = appliedTransformation;
        this.decodedResourceClass = decodedResourceClass;
        this.options = options;
    }

    public boolean equals(Object o) {
        boolean z = false;
        if (!(o instanceof ResourceCacheKey)) {
            return false;
        }
        ResourceCacheKey other = (ResourceCacheKey) o;
        if (this.height == other.height && this.width == other.width) {
            if (Util.bothNullOrEqual(this.transformation, other.transformation)) {
                if (this.decodedResourceClass.equals(other.decodedResourceClass)) {
                    if (this.sourceKey.equals(other.sourceKey)) {
                        if (this.signature.equals(other.signature)) {
                            if (this.options.equals(other.options)) {
                                z = true;
                                return z;
                            }
                        }
                    }
                }
            }
        }
        return z;
    }

    public int hashCode() {
        int result = (((((this.sourceKey.hashCode() * 31) + this.signature.hashCode()) * 31) + this.width) * 31) + this.height;
        Transformation transformation = this.transformation;
        if (transformation != null) {
            result = (result * 31) + transformation.hashCode();
        }
        return (((result * 31) + this.decodedResourceClass.hashCode()) * 31) + this.options.hashCode();
    }

    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        byte[] dimensions = (byte[]) this.arrayPool.getExact(8, byte[].class);
        ByteBuffer.wrap(dimensions).putInt(this.width).putInt(this.height).array();
        this.signature.updateDiskCacheKey(messageDigest);
        this.sourceKey.updateDiskCacheKey(messageDigest);
        messageDigest.update(dimensions);
        Transformation transformation = this.transformation;
        if (transformation != null) {
            transformation.updateDiskCacheKey(messageDigest);
        }
        this.options.updateDiskCacheKey(messageDigest);
        messageDigest.update(getResourceClassBytes());
        this.arrayPool.put(dimensions);
    }

    private byte[] getResourceClassBytes() {
        byte[] result = (byte[]) RESOURCE_CLASS_BYTES.get(this.decodedResourceClass);
        if (result != null) {
            return result;
        }
        result = this.decodedResourceClass.getName().getBytes(CHARSET);
        RESOURCE_CLASS_BYTES.put(this.decodedResourceClass, result);
        return result;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ResourceCacheKey{sourceKey=");
        stringBuilder.append(this.sourceKey);
        stringBuilder.append(", signature=");
        stringBuilder.append(this.signature);
        stringBuilder.append(", width=");
        stringBuilder.append(this.width);
        stringBuilder.append(", height=");
        stringBuilder.append(this.height);
        stringBuilder.append(", decodedResourceClass=");
        stringBuilder.append(this.decodedResourceClass);
        stringBuilder.append(", transformation='");
        stringBuilder.append(this.transformation);
        stringBuilder.append('\'');
        stringBuilder.append(", options=");
        stringBuilder.append(this.options);
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
