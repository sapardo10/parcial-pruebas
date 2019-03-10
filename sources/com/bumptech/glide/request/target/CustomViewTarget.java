package com.bumptech.glide.request.target;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;
import com.bumptech.glide.C0517R;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.util.Preconditions;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class CustomViewTarget<T extends View, Z> implements Target<Z> {
    private static final String TAG = "CustomViewTarget";
    @IdRes
    private static final int VIEW_TAG_ID = C0517R.id.glide_custom_view_target_tag;
    @Nullable
    private OnAttachStateChangeListener attachStateListener;
    private boolean isAttachStateListenerAdded;
    private boolean isClearedByUs;
    @IdRes
    private int overrideTag;
    private final SizeDeterminer sizeDeterminer;
    protected final T view;

    /* renamed from: com.bumptech.glide.request.target.CustomViewTarget$1 */
    class C05341 implements OnAttachStateChangeListener {
        C05341() {
        }

        public void onViewAttachedToWindow(View v) {
            CustomViewTarget.this.resumeMyRequest();
        }

        public void onViewDetachedFromWindow(View v) {
            CustomViewTarget.this.pauseMyRequest();
        }
    }

    @VisibleForTesting
    static final class SizeDeterminer {
        private static final int PENDING_SIZE = 0;
        @Nullable
        @VisibleForTesting
        static Integer maxDisplayLength;
        private final List<SizeReadyCallback> cbs = new ArrayList();
        @Nullable
        private SizeDeterminerLayoutListener layoutListener;
        private final View view;
        boolean waitForLayout;

        private static final class SizeDeterminerLayoutListener implements OnPreDrawListener {
            private final WeakReference<SizeDeterminer> sizeDeterminerRef;

            SizeDeterminerLayoutListener(@NonNull SizeDeterminer sizeDeterminer) {
                this.sizeDeterminerRef = new WeakReference(sizeDeterminer);
            }

            public boolean onPreDraw() {
                if (Log.isLoggable(CustomViewTarget.TAG, 2)) {
                    String str = CustomViewTarget.TAG;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("OnGlobalLayoutListener called attachStateListener=");
                    stringBuilder.append(this);
                    Log.v(str, stringBuilder.toString());
                }
                SizeDeterminer sizeDeterminer = (SizeDeterminer) this.sizeDeterminerRef.get();
                if (sizeDeterminer != null) {
                    sizeDeterminer.checkCurrentDimens();
                }
                return true;
            }
        }

        SizeDeterminer(@NonNull View view) {
            this.view = view;
        }

        private static int getMaxDisplayLength(@NonNull Context context) {
            if (maxDisplayLength == null) {
                Display display = ((WindowManager) Preconditions.checkNotNull((WindowManager) context.getSystemService("window"))).getDefaultDisplay();
                Point displayDimensions = new Point();
                display.getSize(displayDimensions);
                maxDisplayLength = Integer.valueOf(Math.max(displayDimensions.x, displayDimensions.y));
            }
            return maxDisplayLength.intValue();
        }

        private void notifyCbs(int width, int height) {
            Iterator it = new ArrayList(this.cbs).iterator();
            while (it.hasNext()) {
                ((SizeReadyCallback) it.next()).onSizeReady(width, height);
            }
        }

        void checkCurrentDimens() {
            if (!this.cbs.isEmpty()) {
                int currentWidth = getTargetWidth();
                int currentHeight = getTargetHeight();
                if (isViewStateAndSizeValid(currentWidth, currentHeight)) {
                    notifyCbs(currentWidth, currentHeight);
                    clearCallbacksAndListener();
                }
            }
        }

        void getSize(@NonNull SizeReadyCallback cb) {
            int currentWidth = getTargetWidth();
            int currentHeight = getTargetHeight();
            if (isViewStateAndSizeValid(currentWidth, currentHeight)) {
                cb.onSizeReady(currentWidth, currentHeight);
                return;
            }
            if (!this.cbs.contains(cb)) {
                this.cbs.add(cb);
            }
            if (this.layoutListener == null) {
                ViewTreeObserver observer = this.view.getViewTreeObserver();
                this.layoutListener = new SizeDeterminerLayoutListener(this);
                observer.addOnPreDrawListener(this.layoutListener);
            }
        }

        void removeCallback(@NonNull SizeReadyCallback cb) {
            this.cbs.remove(cb);
        }

        void clearCallbacksAndListener() {
            ViewTreeObserver observer = this.view.getViewTreeObserver();
            if (observer.isAlive()) {
                observer.removeOnPreDrawListener(this.layoutListener);
            }
            this.layoutListener = null;
            this.cbs.clear();
        }

        private boolean isViewStateAndSizeValid(int width, int height) {
            return isDimensionValid(width) && isDimensionValid(height);
        }

        private int getTargetHeight() {
            int verticalPadding = this.view.getPaddingTop() + this.view.getPaddingBottom();
            LayoutParams layoutParams = this.view.getLayoutParams();
            return getTargetDimen(this.view.getHeight(), layoutParams != null ? layoutParams.height : 0, verticalPadding);
        }

        private int getTargetWidth() {
            int horizontalPadding = this.view.getPaddingLeft() + this.view.getPaddingRight();
            LayoutParams layoutParams = this.view.getLayoutParams();
            return getTargetDimen(this.view.getWidth(), layoutParams != null ? layoutParams.width : 0, horizontalPadding);
        }

        private int getTargetDimen(int viewSize, int paramSize, int paddingSize) {
            int adjustedParamSize = paramSize - paddingSize;
            if (adjustedParamSize > 0) {
                return adjustedParamSize;
            }
            if (this.waitForLayout && this.view.isLayoutRequested()) {
                return 0;
            }
            int adjustedViewSize = viewSize - paddingSize;
            if (adjustedViewSize > 0) {
                return adjustedViewSize;
            }
            if (this.view.isLayoutRequested() || paramSize != -2) {
                return 0;
            }
            if (Log.isLoggable(CustomViewTarget.TAG, 4)) {
                Log.i(CustomViewTarget.TAG, "Glide treats LayoutParams.WRAP_CONTENT as a request for an image the size of this device's screen dimensions. If you want to load the original image and are ok with the corresponding memory cost and OOMs (depending on the input size), use .override(Target.SIZE_ORIGINAL). Otherwise, use LayoutParams.MATCH_PARENT, set layout_width and layout_height to fixed dimension, or use .override() with fixed dimensions.");
            }
            return getMaxDisplayLength(this.view.getContext());
        }

        private boolean isDimensionValid(int size) {
            if (size <= 0) {
                if (size != Integer.MIN_VALUE) {
                    return false;
                }
            }
            return true;
        }
    }

    protected abstract void onResourceCleared(@Nullable Drawable drawable);

    public CustomViewTarget(@NonNull T view) {
        this.view = (View) Preconditions.checkNotNull(view);
        this.sizeDeterminer = new SizeDeterminer(view);
    }

    protected void onResourceLoading(@Nullable Drawable placeholder) {
    }

    public void onStart() {
    }

    public void onStop() {
    }

    public void onDestroy() {
    }

    @NonNull
    public final CustomViewTarget<T, Z> waitForLayout() {
        this.sizeDeterminer.waitForLayout = true;
        return this;
    }

    @NonNull
    public final CustomViewTarget<T, Z> clearOnDetach() {
        if (this.attachStateListener != null) {
            return this;
        }
        this.attachStateListener = new C05341();
        maybeAddAttachStateListener();
        return this;
    }

    public final CustomViewTarget<T, Z> useTagId(@IdRes int tagId) {
        if (this.overrideTag == 0) {
            this.overrideTag = tagId;
            return this;
        }
        throw new IllegalArgumentException("You cannot change the tag id once it has been set.");
    }

    @NonNull
    public final T getView() {
        return this.view;
    }

    public final void getSize(@NonNull SizeReadyCallback cb) {
        this.sizeDeterminer.getSize(cb);
    }

    public final void removeCallback(@NonNull SizeReadyCallback cb) {
        this.sizeDeterminer.removeCallback(cb);
    }

    public final void onLoadStarted(@Nullable Drawable placeholder) {
        maybeAddAttachStateListener();
        onResourceLoading(placeholder);
    }

    public final void onLoadCleared(@Nullable Drawable placeholder) {
        this.sizeDeterminer.clearCallbacksAndListener();
        onResourceCleared(placeholder);
        if (!this.isClearedByUs) {
            maybeRemoveAttachStateListener();
        }
    }

    public final void setRequest(@Nullable Request request) {
        setTag(request);
    }

    @Nullable
    public final Request getRequest() {
        Object tag = getTag();
        if (tag == null) {
            return null;
        }
        if (tag instanceof Request) {
            return (Request) tag;
        }
        throw new IllegalArgumentException("You must not pass non-R.id ids to setTag(id)");
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Target for: ");
        stringBuilder.append(this.view);
        return stringBuilder.toString();
    }

    final void resumeMyRequest() {
        Request request = getRequest();
        if (request != null && request.isCleared()) {
            request.begin();
        }
    }

    final void pauseMyRequest() {
        Request request = getRequest();
        if (request != null) {
            this.isClearedByUs = true;
            request.clear();
            this.isClearedByUs = false;
        }
    }

    private void setTag(@Nullable Object tag) {
        View view = this.view;
        int i = this.overrideTag;
        if (i == 0) {
            i = VIEW_TAG_ID;
        }
        view.setTag(i, tag);
    }

    @Nullable
    private Object getTag() {
        View view = this.view;
        int i = this.overrideTag;
        if (i == 0) {
            i = VIEW_TAG_ID;
        }
        return view.getTag(i);
    }

    private void maybeAddAttachStateListener() {
        OnAttachStateChangeListener onAttachStateChangeListener = this.attachStateListener;
        if (onAttachStateChangeListener != null) {
            if (!this.isAttachStateListenerAdded) {
                this.view.addOnAttachStateChangeListener(onAttachStateChangeListener);
                this.isAttachStateListenerAdded = true;
            }
        }
    }

    private void maybeRemoveAttachStateListener() {
        OnAttachStateChangeListener onAttachStateChangeListener = this.attachStateListener;
        if (onAttachStateChangeListener != null) {
            if (this.isAttachStateListenerAdded) {
                this.view.removeOnAttachStateChangeListener(onAttachStateChangeListener);
                this.isAttachStateListenerAdded = false;
            }
        }
    }
}
