package com.bumptech.glide.request.target;

import android.support.annotation.NonNull;
import com.bumptech.glide.util.Util;

@Deprecated
public abstract class SimpleTarget<Z> extends BaseTarget<Z> {
    private final int height;
    private final int width;

    public SimpleTarget() {
        this(Integer.MIN_VALUE, Integer.MIN_VALUE);
    }

    public SimpleTarget(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public final void getSize(@NonNull SizeReadyCallback cb) {
        if (Util.isValidDimensions(this.width, this.height)) {
            cb.onSizeReady(this.width, this.height);
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Width and height must both be > 0 or Target#SIZE_ORIGINAL, but given width: ");
        stringBuilder.append(this.width);
        stringBuilder.append(" and height: ");
        stringBuilder.append(this.height);
        stringBuilder.append(", either provide dimensions in the constructor or call override()");
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    public void removeCallback(@NonNull SizeReadyCallback cb) {
    }
}
