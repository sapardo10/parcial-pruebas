package com.bumptech.glide.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import com.bumptech.glide.ListPreloader.PreloadSizeProvider;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.ViewTarget;
import com.bumptech.glide.request.transition.Transition;
import java.util.Arrays;

public class ViewPreloadSizeProvider<T> implements PreloadSizeProvider<T>, SizeReadyCallback {
    private int[] size;
    private SizeViewTarget viewTarget;

    private static final class SizeViewTarget extends ViewTarget<View, Object> {
        SizeViewTarget(@NonNull View view, @NonNull SizeReadyCallback callback) {
            super(view);
            getSize(callback);
        }

        public void onResourceReady(@NonNull Object resource, @Nullable Transition<? super Object> transition) {
        }
    }

    public ViewPreloadSizeProvider(@NonNull View view) {
        this.viewTarget = new SizeViewTarget(view, this);
    }

    @Nullable
    public int[] getPreloadSize(@NonNull T t, int adapterPosition, int itemPosition) {
        int[] iArr = this.size;
        if (iArr == null) {
            return null;
        }
        return Arrays.copyOf(iArr, iArr.length);
    }

    public void onSizeReady(int width, int height) {
        this.size = new int[]{width, height};
        this.viewTarget = null;
    }

    public void setView(@NonNull View view) {
        if (this.size == null) {
            if (this.viewTarget == null) {
                this.viewTarget = new SizeViewTarget(view, this);
            }
        }
    }
}
