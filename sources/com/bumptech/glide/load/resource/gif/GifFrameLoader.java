package com.bumptech.glide.load.resource.gif;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.ObjectKey;
import com.bumptech.glide.util.Preconditions;
import com.bumptech.glide.util.Util;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

class GifFrameLoader {
    private final BitmapPool bitmapPool;
    private final List<FrameCallback> callbacks;
    private DelayTarget current;
    private Bitmap firstFrame;
    private final GifDecoder gifDecoder;
    private final Handler handler;
    private boolean isCleared;
    private boolean isLoadPending;
    private boolean isRunning;
    private DelayTarget next;
    @Nullable
    private OnEveryFrameListener onEveryFrameListener;
    private DelayTarget pendingTarget;
    private RequestBuilder<Bitmap> requestBuilder;
    final RequestManager requestManager;
    private boolean startFromFirstFrame;
    private Transformation<Bitmap> transformation;

    public interface FrameCallback {
        void onFrameReady();
    }

    private class FrameLoaderCallback implements Callback {
        static final int MSG_CLEAR = 2;
        static final int MSG_DELAY = 1;

        FrameLoaderCallback() {
        }

        public boolean handleMessage(Message msg) {
            if (msg.what == 1) {
                GifFrameLoader.this.onFrameReady(msg.obj);
                return true;
            }
            if (msg.what == 2) {
                GifFrameLoader.this.requestManager.clear((DelayTarget) msg.obj);
            }
            return false;
        }
    }

    @VisibleForTesting
    interface OnEveryFrameListener {
        void onFrameReady();
    }

    @VisibleForTesting
    static class DelayTarget extends SimpleTarget<Bitmap> {
        private final Handler handler;
        final int index;
        private Bitmap resource;
        private final long targetTime;

        DelayTarget(Handler handler, int index, long targetTime) {
            this.handler = handler;
            this.index = index;
            this.targetTime = targetTime;
        }

        Bitmap getResource() {
            return this.resource;
        }

        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
            this.resource = resource;
            this.handler.sendMessageAtTime(this.handler.obtainMessage(1, this), this.targetTime);
        }
    }

    GifFrameLoader(Glide glide, GifDecoder gifDecoder, int width, int height, Transformation<Bitmap> transformation, Bitmap firstFrame) {
        this(glide.getBitmapPool(), Glide.with(glide.getContext()), gifDecoder, null, getRequestBuilder(Glide.with(glide.getContext()), width, height), transformation, firstFrame);
    }

    GifFrameLoader(BitmapPool bitmapPool, RequestManager requestManager, GifDecoder gifDecoder, Handler handler, RequestBuilder<Bitmap> requestBuilder, Transformation<Bitmap> transformation, Bitmap firstFrame) {
        this.callbacks = new ArrayList();
        this.requestManager = requestManager;
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper(), new FrameLoaderCallback());
        }
        this.bitmapPool = bitmapPool;
        this.handler = handler;
        this.requestBuilder = requestBuilder;
        this.gifDecoder = gifDecoder;
        setFrameTransformation(transformation, firstFrame);
    }

    void setFrameTransformation(Transformation<Bitmap> transformation, Bitmap firstFrame) {
        this.transformation = (Transformation) Preconditions.checkNotNull(transformation);
        this.firstFrame = (Bitmap) Preconditions.checkNotNull(firstFrame);
        this.requestBuilder = this.requestBuilder.apply(new RequestOptions().transform(transformation));
    }

    Transformation<Bitmap> getFrameTransformation() {
        return this.transformation;
    }

    Bitmap getFirstFrame() {
        return this.firstFrame;
    }

    void subscribe(FrameCallback frameCallback) {
        if (this.isCleared) {
            throw new IllegalStateException("Cannot subscribe to a cleared frame loader");
        } else if (this.callbacks.contains(frameCallback)) {
            throw new IllegalStateException("Cannot subscribe twice in a row");
        } else {
            boolean start = this.callbacks.isEmpty();
            this.callbacks.add(frameCallback);
            if (start) {
                start();
            }
        }
    }

    void unsubscribe(FrameCallback frameCallback) {
        this.callbacks.remove(frameCallback);
        if (this.callbacks.isEmpty()) {
            stop();
        }
    }

    int getWidth() {
        return getCurrentFrame().getWidth();
    }

    int getHeight() {
        return getCurrentFrame().getHeight();
    }

    int getSize() {
        return this.gifDecoder.getByteSize() + getFrameSize();
    }

    int getCurrentIndex() {
        DelayTarget delayTarget = this.current;
        return delayTarget != null ? delayTarget.index : -1;
    }

    private int getFrameSize() {
        return Util.getBitmapByteSize(getCurrentFrame().getWidth(), getCurrentFrame().getHeight(), getCurrentFrame().getConfig());
    }

    ByteBuffer getBuffer() {
        return this.gifDecoder.getData().asReadOnlyBuffer();
    }

    int getFrameCount() {
        return this.gifDecoder.getFrameCount();
    }

    int getLoopCount() {
        return this.gifDecoder.getTotalIterationCount();
    }

    private void start() {
        if (!this.isRunning) {
            this.isRunning = true;
            this.isCleared = false;
            loadNextFrame();
        }
    }

    private void stop() {
        this.isRunning = false;
    }

    void clear() {
        this.callbacks.clear();
        recycleFirstFrame();
        stop();
        Target target = this.current;
        if (target != null) {
            this.requestManager.clear(target);
            this.current = null;
        }
        target = this.next;
        if (target != null) {
            this.requestManager.clear(target);
            this.next = null;
        }
        target = this.pendingTarget;
        if (target != null) {
            this.requestManager.clear(target);
            this.pendingTarget = null;
        }
        this.gifDecoder.clear();
        this.isCleared = true;
    }

    Bitmap getCurrentFrame() {
        DelayTarget delayTarget = this.current;
        return delayTarget != null ? delayTarget.getResource() : this.firstFrame;
    }

    private void loadNextFrame() {
        if (this.isRunning) {
            if (!this.isLoadPending) {
                if (this.startFromFirstFrame) {
                    Preconditions.checkArgument(this.pendingTarget == null, "Pending target must be null when starting from the first frame");
                    this.gifDecoder.resetFrameIndex();
                    this.startFromFirstFrame = false;
                }
                if (this.pendingTarget != null) {
                    DelayTarget temp = this.pendingTarget;
                    this.pendingTarget = null;
                    onFrameReady(temp);
                    return;
                }
                this.isLoadPending = true;
                long targetTime = SystemClock.uptimeMillis() + ((long) this.gifDecoder.getNextDelay());
                this.gifDecoder.advance();
                this.next = new DelayTarget(this.handler, this.gifDecoder.getCurrentFrameIndex(), targetTime);
                this.requestBuilder.apply(RequestOptions.signatureOf(getFrameSignature())).load(this.gifDecoder).into(this.next);
            }
        }
    }

    private void recycleFirstFrame() {
        Bitmap bitmap = this.firstFrame;
        if (bitmap != null) {
            this.bitmapPool.put(bitmap);
            this.firstFrame = null;
        }
    }

    void setNextStartFromFirstFrame() {
        Preconditions.checkArgument(this.isRunning ^ true, "Can't restart a running animation");
        this.startFromFirstFrame = true;
        Target target = this.pendingTarget;
        if (target != null) {
            this.requestManager.clear(target);
            this.pendingTarget = null;
        }
    }

    @VisibleForTesting
    void setOnEveryFrameReadyListener(@Nullable OnEveryFrameListener onEveryFrameListener) {
        this.onEveryFrameListener = onEveryFrameListener;
    }

    @VisibleForTesting
    void onFrameReady(DelayTarget delayTarget) {
        OnEveryFrameListener onEveryFrameListener = this.onEveryFrameListener;
        if (onEveryFrameListener != null) {
            onEveryFrameListener.onFrameReady();
        }
        this.isLoadPending = false;
        if (this.isCleared) {
            this.handler.obtainMessage(2, delayTarget).sendToTarget();
        } else if (this.isRunning) {
            if (delayTarget.getResource() != null) {
                recycleFirstFrame();
                DelayTarget previous = this.current;
                this.current = delayTarget;
                for (int i = this.callbacks.size() - 1; i >= 0; i--) {
                    ((FrameCallback) this.callbacks.get(i)).onFrameReady();
                }
                if (previous != null) {
                    this.handler.obtainMessage(2, previous).sendToTarget();
                }
            }
            loadNextFrame();
        } else {
            this.pendingTarget = delayTarget;
        }
    }

    private static RequestBuilder<Bitmap> getRequestBuilder(RequestManager requestManager, int width, int height) {
        return requestManager.asBitmap().apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE).useAnimationPool(true).skipMemoryCache(true).override(width, height));
    }

    private static Key getFrameSignature() {
        return new ObjectKey(Double.valueOf(Math.random()));
    }
}
