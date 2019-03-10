package com.bumptech.glide.signature;

import android.support.annotation.NonNull;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.util.Preconditions;
import java.security.MessageDigest;

public final class ObjectKey implements Key {
    private final Object object;

    public ObjectKey(@NonNull Object object) {
        this.object = Preconditions.checkNotNull(object);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ObjectKey{object=");
        stringBuilder.append(this.object);
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

    public boolean equals(Object o) {
        if (!(o instanceof ObjectKey)) {
            return false;
        }
        return this.object.equals(((ObjectKey) o).object);
    }

    public int hashCode() {
        return this.object.hashCode();
    }

    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update(this.object.toString().getBytes(CHARSET));
    }
}
