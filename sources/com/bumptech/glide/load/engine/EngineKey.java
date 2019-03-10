package com.bumptech.glide.load.engine;

import android.support.annotation.NonNull;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.util.Preconditions;
import java.security.MessageDigest;
import java.util.Map;

class EngineKey implements Key {
    private int hashCode;
    private final int height;
    private final Object model;
    private final Options options;
    private final Class<?> resourceClass;
    private final Key signature;
    private final Class<?> transcodeClass;
    private final Map<Class<?>, Transformation<?>> transformations;
    private final int width;

    EngineKey(Object model, Key signature, int width, int height, Map<Class<?>, Transformation<?>> transformations, Class<?> resourceClass, Class<?> transcodeClass, Options options) {
        this.model = Preconditions.checkNotNull(model);
        this.signature = (Key) Preconditions.checkNotNull(signature, "Signature must not be null");
        this.width = width;
        this.height = height;
        this.transformations = (Map) Preconditions.checkNotNull(transformations);
        this.resourceClass = (Class) Preconditions.checkNotNull(resourceClass, "Resource class must not be null");
        this.transcodeClass = (Class) Preconditions.checkNotNull(transcodeClass, "Transcode class must not be null");
        this.options = (Options) Preconditions.checkNotNull(options);
    }

    public boolean equals(Object o) {
        boolean z = false;
        if (!(o instanceof EngineKey)) {
            return false;
        }
        EngineKey other = (EngineKey) o;
        if (this.model.equals(other.model)) {
            if (this.signature.equals(other.signature) && this.height == other.height && this.width == other.width) {
                if (this.transformations.equals(other.transformations)) {
                    if (this.resourceClass.equals(other.resourceClass)) {
                        if (this.transcodeClass.equals(other.transcodeClass)) {
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
        if (this.hashCode == 0) {
            this.hashCode = this.model.hashCode();
            this.hashCode = (this.hashCode * 31) + this.signature.hashCode();
            this.hashCode = (this.hashCode * 31) + this.width;
            this.hashCode = (this.hashCode * 31) + this.height;
            this.hashCode = (this.hashCode * 31) + this.transformations.hashCode();
            this.hashCode = (this.hashCode * 31) + this.resourceClass.hashCode();
            this.hashCode = (this.hashCode * 31) + this.transcodeClass.hashCode();
            this.hashCode = (this.hashCode * 31) + this.options.hashCode();
        }
        return this.hashCode;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("EngineKey{model=");
        stringBuilder.append(this.model);
        stringBuilder.append(", width=");
        stringBuilder.append(this.width);
        stringBuilder.append(", height=");
        stringBuilder.append(this.height);
        stringBuilder.append(", resourceClass=");
        stringBuilder.append(this.resourceClass);
        stringBuilder.append(", transcodeClass=");
        stringBuilder.append(this.transcodeClass);
        stringBuilder.append(", signature=");
        stringBuilder.append(this.signature);
        stringBuilder.append(", hashCode=");
        stringBuilder.append(this.hashCode);
        stringBuilder.append(", transformations=");
        stringBuilder.append(this.transformations);
        stringBuilder.append(", options=");
        stringBuilder.append(this.options);
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        throw new UnsupportedOperationException();
    }
}
