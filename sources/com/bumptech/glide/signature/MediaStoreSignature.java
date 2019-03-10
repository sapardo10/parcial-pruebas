package com.bumptech.glide.signature;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.bumptech.glide.load.Key;
import java.nio.ByteBuffer;
import java.security.MessageDigest;

public class MediaStoreSignature implements Key {
    private final long dateModified;
    @NonNull
    private final String mimeType;
    private final int orientation;

    public MediaStoreSignature(@Nullable String mimeType, long dateModified, int orientation) {
        this.mimeType = mimeType == null ? "" : mimeType;
        this.dateModified = dateModified;
        this.orientation = orientation;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o != null) {
            if (getClass() == o.getClass()) {
                MediaStoreSignature that = (MediaStoreSignature) o;
                if (this.dateModified == that.dateModified && this.orientation == that.orientation && this.mimeType.equals(that.mimeType)) {
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    public int hashCode() {
        int result = this.mimeType.hashCode() * 31;
        long j = this.dateModified;
        return ((result + ((int) (j ^ (j >>> 32)))) * 31) + this.orientation;
    }

    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update(ByteBuffer.allocate(12).putLong(this.dateModified).putInt(this.orientation).array());
        messageDigest.update(this.mimeType.getBytes(CHARSET));
    }
}
