package com.bumptech.glide;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.widget.ImageView;
import com.bumptech.glide.load.engine.Engine;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ImageViewTargetFactory;
import com.bumptech.glide.request.target.ViewTarget;
import java.util.Map;
import java.util.Map.Entry;

public class GlideContext extends ContextWrapper {
    @VisibleForTesting
    static final TransitionOptions<?, ?> DEFAULT_TRANSITION_OPTIONS = new GenericTransitionOptions();
    private final ArrayPool arrayPool;
    private final RequestOptions defaultRequestOptions;
    private final Map<Class<?>, TransitionOptions<?, ?>> defaultTransitionOptions;
    private final Engine engine;
    private final ImageViewTargetFactory imageViewTargetFactory;
    private final int logLevel;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final Registry registry;

    public GlideContext(@NonNull Context context, @NonNull ArrayPool arrayPool, @NonNull Registry registry, @NonNull ImageViewTargetFactory imageViewTargetFactory, @NonNull RequestOptions defaultRequestOptions, @NonNull Map<Class<?>, TransitionOptions<?, ?>> defaultTransitionOptions, @NonNull Engine engine, int logLevel) {
        super(context.getApplicationContext());
        this.arrayPool = arrayPool;
        this.registry = registry;
        this.imageViewTargetFactory = imageViewTargetFactory;
        this.defaultRequestOptions = defaultRequestOptions;
        this.defaultTransitionOptions = defaultTransitionOptions;
        this.engine = engine;
        this.logLevel = logLevel;
    }

    public RequestOptions getDefaultRequestOptions() {
        return this.defaultRequestOptions;
    }

    @NonNull
    public <T> TransitionOptions<?, T> getDefaultTransitionOptions(@NonNull Class<T> transcodeClass) {
        TransitionOptions<?, ?> result = (TransitionOptions) this.defaultTransitionOptions.get(transcodeClass);
        if (result == null) {
            for (Entry<Class<?>, TransitionOptions<?, ?>> value : this.defaultTransitionOptions.entrySet()) {
                if (((Class) value.getKey()).isAssignableFrom(transcodeClass)) {
                    result = (TransitionOptions) value.getValue();
                }
            }
        }
        if (result == null) {
            return DEFAULT_TRANSITION_OPTIONS;
        }
        return result;
    }

    @NonNull
    public <X> ViewTarget<ImageView, X> buildImageViewTarget(@NonNull ImageView imageView, @NonNull Class<X> transcodeClass) {
        return this.imageViewTargetFactory.buildTarget(imageView, transcodeClass);
    }

    @NonNull
    public Handler getMainHandler() {
        return this.mainHandler;
    }

    @NonNull
    public Engine getEngine() {
        return this.engine;
    }

    @NonNull
    public Registry getRegistry() {
        return this.registry;
    }

    public int getLogLevel() {
        return this.logLevel;
    }

    @NonNull
    public ArrayPool getArrayPool() {
        return this.arrayPool;
    }
}
