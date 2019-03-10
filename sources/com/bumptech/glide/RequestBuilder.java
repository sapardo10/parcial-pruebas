package com.bumptech.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.CheckResult;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.ErrorRequestCoordinator;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestCoordinator;
import com.bumptech.glide.request.RequestFutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.SingleRequest;
import com.bumptech.glide.request.ThumbnailRequestCoordinator;
import com.bumptech.glide.request.target.PreloadTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;
import com.bumptech.glide.signature.ApplicationVersionSignature;
import com.bumptech.glide.util.Preconditions;
import com.bumptech.glide.util.Util;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RequestBuilder<TranscodeType> implements Cloneable, ModelTypes<RequestBuilder<TranscodeType>> {
    protected static final RequestOptions DOWNLOAD_ONLY_OPTIONS = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA).priority(Priority.LOW).skipMemoryCache(true);
    private final Context context;
    private final RequestOptions defaultRequestOptions;
    @Nullable
    private RequestBuilder<TranscodeType> errorBuilder;
    private final Glide glide;
    private final GlideContext glideContext;
    private boolean isDefaultTransitionOptionsSet;
    private boolean isModelSet;
    private boolean isThumbnailBuilt;
    @Nullable
    private Object model;
    @Nullable
    private List<RequestListener<TranscodeType>> requestListeners;
    private final RequestManager requestManager;
    @NonNull
    protected RequestOptions requestOptions;
    @Nullable
    private Float thumbSizeMultiplier;
    @Nullable
    private RequestBuilder<TranscodeType> thumbnailBuilder;
    private final Class<TranscodeType> transcodeClass;
    @NonNull
    private TransitionOptions<?, ? super TranscodeType> transitionOptions;

    /* renamed from: com.bumptech.glide.RequestBuilder$2 */
    static /* synthetic */ class C05192 {
        static final /* synthetic */ int[] $SwitchMap$android$widget$ImageView$ScaleType = new int[ScaleType.values().length];

        static {
            $SwitchMap$com$bumptech$glide$Priority = new int[Priority.values().length];
            try {
                $SwitchMap$com$bumptech$glide$Priority[Priority.LOW.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$bumptech$glide$Priority[Priority.NORMAL.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$bumptech$glide$Priority[Priority.HIGH.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$bumptech$glide$Priority[Priority.IMMEDIATE.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ScaleType.CENTER_CROP.ordinal()] = 1;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ScaleType.CENTER_INSIDE.ordinal()] = 2;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ScaleType.FIT_CENTER.ordinal()] = 3;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ScaleType.FIT_START.ordinal()] = 4;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ScaleType.FIT_END.ordinal()] = 5;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ScaleType.FIT_XY.ordinal()] = 6;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ScaleType.CENTER.ordinal()] = 7;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ScaleType.MATRIX.ordinal()] = 8;
            } catch (NoSuchFieldError e12) {
            }
        }
    }

    protected RequestBuilder(Glide glide, RequestManager requestManager, Class<TranscodeType> transcodeClass, Context context) {
        this.isDefaultTransitionOptionsSet = true;
        this.glide = glide;
        this.requestManager = requestManager;
        this.transcodeClass = transcodeClass;
        this.defaultRequestOptions = requestManager.getDefaultRequestOptions();
        this.context = context;
        this.transitionOptions = requestManager.getDefaultTransitionOptions(transcodeClass);
        this.requestOptions = this.defaultRequestOptions;
        this.glideContext = glide.getGlideContext();
    }

    protected RequestBuilder(Class<TranscodeType> transcodeClass, RequestBuilder<?> other) {
        this(other.glide, other.requestManager, transcodeClass, other.context);
        this.model = other.model;
        this.isModelSet = other.isModelSet;
        this.requestOptions = other.requestOptions;
    }

    @CheckResult
    @NonNull
    public RequestBuilder<TranscodeType> apply(@NonNull RequestOptions requestOptions) {
        Preconditions.checkNotNull(requestOptions);
        this.requestOptions = getMutableOptions().apply(requestOptions);
        return this;
    }

    @NonNull
    protected RequestOptions getMutableOptions() {
        RequestOptions requestOptions = this.defaultRequestOptions;
        RequestOptions requestOptions2 = this.requestOptions;
        if (requestOptions == requestOptions2) {
            return requestOptions2.clone();
        }
        return requestOptions2;
    }

    @CheckResult
    @NonNull
    public RequestBuilder<TranscodeType> transition(@NonNull TransitionOptions<?, ? super TranscodeType> transitionOptions) {
        this.transitionOptions = (TransitionOptions) Preconditions.checkNotNull(transitionOptions);
        this.isDefaultTransitionOptionsSet = false;
        return this;
    }

    @CheckResult
    @NonNull
    public RequestBuilder<TranscodeType> listener(@Nullable RequestListener<TranscodeType> requestListener) {
        this.requestListeners = null;
        return addListener(requestListener);
    }

    @CheckResult
    @NonNull
    public RequestBuilder<TranscodeType> addListener(@Nullable RequestListener<TranscodeType> requestListener) {
        if (requestListener != null) {
            if (this.requestListeners == null) {
                this.requestListeners = new ArrayList();
            }
            this.requestListeners.add(requestListener);
        }
        return this;
    }

    @NonNull
    public RequestBuilder<TranscodeType> error(@Nullable RequestBuilder<TranscodeType> errorBuilder) {
        this.errorBuilder = errorBuilder;
        return this;
    }

    @CheckResult
    @NonNull
    public RequestBuilder<TranscodeType> thumbnail(@Nullable RequestBuilder<TranscodeType> thumbnailRequest) {
        this.thumbnailBuilder = thumbnailRequest;
        return this;
    }

    @CheckResult
    @NonNull
    public RequestBuilder<TranscodeType> thumbnail(@Nullable RequestBuilder<TranscodeType>... thumbnails) {
        if (thumbnails != null) {
            if (thumbnails.length != 0) {
                RequestBuilder previous = null;
                for (int i = thumbnails.length - 1; i >= 0; i--) {
                    RequestBuilder<TranscodeType> current = thumbnails[i];
                    if (current != null) {
                        if (previous == null) {
                            previous = current;
                        } else {
                            previous = current.thumbnail(previous);
                        }
                    }
                }
                return thumbnail(previous);
            }
        }
        return thumbnail((RequestBuilder) null);
    }

    @CheckResult
    @NonNull
    public RequestBuilder<TranscodeType> thumbnail(float sizeMultiplier) {
        if (sizeMultiplier < 0.0f || sizeMultiplier > 1.0f) {
            throw new IllegalArgumentException("sizeMultiplier must be between 0 and 1");
        }
        this.thumbSizeMultiplier = Float.valueOf(sizeMultiplier);
        return this;
    }

    @CheckResult
    @NonNull
    public RequestBuilder<TranscodeType> load(@Nullable Object model) {
        return loadGeneric(model);
    }

    @NonNull
    private RequestBuilder<TranscodeType> loadGeneric(@Nullable Object model) {
        this.model = model;
        this.isModelSet = true;
        return this;
    }

    @CheckResult
    @NonNull
    public RequestBuilder<TranscodeType> load(@Nullable Bitmap bitmap) {
        return loadGeneric(bitmap).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE));
    }

    @CheckResult
    @NonNull
    public RequestBuilder<TranscodeType> load(@Nullable Drawable drawable) {
        return loadGeneric(drawable).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE));
    }

    @CheckResult
    @NonNull
    public RequestBuilder<TranscodeType> load(@Nullable String string) {
        return loadGeneric(string);
    }

    @CheckResult
    @NonNull
    public RequestBuilder<TranscodeType> load(@Nullable Uri uri) {
        return loadGeneric(uri);
    }

    @CheckResult
    @NonNull
    public RequestBuilder<TranscodeType> load(@Nullable File file) {
        return loadGeneric(file);
    }

    @CheckResult
    @NonNull
    public RequestBuilder<TranscodeType> load(@Nullable @RawRes @DrawableRes Integer resourceId) {
        return loadGeneric(resourceId).apply(RequestOptions.signatureOf(ApplicationVersionSignature.obtain(this.context)));
    }

    @Deprecated
    @CheckResult
    public RequestBuilder<TranscodeType> load(@Nullable URL url) {
        return loadGeneric(url);
    }

    @CheckResult
    @NonNull
    public RequestBuilder<TranscodeType> load(@Nullable byte[] model) {
        RequestBuilder<TranscodeType> result = loadGeneric(model);
        if (!result.requestOptions.isDiskCacheStrategySet()) {
            result = result.apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE));
        }
        if (result.requestOptions.isSkipMemoryCacheSet()) {
            return result;
        }
        return result.apply(RequestOptions.skipMemoryCacheOf(true));
    }

    @CheckResult
    public RequestBuilder<TranscodeType> clone() {
        try {
            RequestBuilder<TranscodeType> result = (RequestBuilder) super.clone();
            result.requestOptions = result.requestOptions.clone();
            result.transitionOptions = result.transitionOptions.clone();
            return result;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    public <Y extends Target<TranscodeType>> Y into(@NonNull Y target) {
        return into((Target) target, null);
    }

    @NonNull
    <Y extends Target<TranscodeType>> Y into(@NonNull Y target, @Nullable RequestListener<TranscodeType> targetListener) {
        return into(target, targetListener, getMutableOptions());
    }

    private <Y extends Target<TranscodeType>> Y into(@NonNull Y target, @Nullable RequestListener<TranscodeType> targetListener, @NonNull RequestOptions options) {
        Util.assertMainThread();
        Preconditions.checkNotNull(target);
        if (this.isModelSet) {
            options = options.autoClone();
            Request request = buildRequest(target, targetListener, options);
            Request previous = target.getRequest();
            if (request.isEquivalentTo(previous)) {
                if (!isSkipMemoryCacheWithCompletePreviousRequest(options, previous)) {
                    request.recycle();
                    if (!((Request) Preconditions.checkNotNull(previous)).isRunning()) {
                        previous.begin();
                    }
                    return target;
                }
            }
            this.requestManager.clear((Target) target);
            target.setRequest(request);
            this.requestManager.track(target, request);
            return target;
        }
        throw new IllegalArgumentException("You must call #load() before calling #into()");
    }

    private boolean isSkipMemoryCacheWithCompletePreviousRequest(RequestOptions options, Request previous) {
        return !options.isMemoryCacheable() && previous.isComplete();
    }

    @NonNull
    public ViewTarget<ImageView, TranscodeType> into(@NonNull ImageView view) {
        Util.assertMainThread();
        Preconditions.checkNotNull(view);
        RequestOptions requestOptions = this.requestOptions;
        if (!requestOptions.isTransformationSet()) {
            if (requestOptions.isTransformationAllowed()) {
                if (view.getScaleType() != null) {
                    switch (C05192.$SwitchMap$android$widget$ImageView$ScaleType[view.getScaleType().ordinal()]) {
                        case 1:
                            requestOptions = requestOptions.clone().optionalCenterCrop();
                            break;
                        case 2:
                            requestOptions = requestOptions.clone().optionalCenterInside();
                            break;
                        case 3:
                        case 4:
                        case 5:
                            requestOptions = requestOptions.clone().optionalFitCenter();
                            break;
                        case 6:
                            requestOptions = requestOptions.clone().optionalCenterInside();
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        return (ViewTarget) into(this.glideContext.buildImageViewTarget(view, this.transcodeClass), null, requestOptions);
    }

    @Deprecated
    public FutureTarget<TranscodeType> into(int width, int height) {
        return submit(width, height);
    }

    @NonNull
    public FutureTarget<TranscodeType> submit() {
        return submit(Integer.MIN_VALUE, Integer.MIN_VALUE);
    }

    @NonNull
    public FutureTarget<TranscodeType> submit(int width, int height) {
        final Target target = new RequestFutureTarget(this.glideContext.getMainHandler(), width, height);
        if (Util.isOnBackgroundThread()) {
            this.glideContext.getMainHandler().post(new Runnable() {
                public void run() {
                    if (!target.isCancelled()) {
                        RequestBuilder requestBuilder = RequestBuilder.this;
                        Target target = target;
                        requestBuilder.into(target, (RequestListener) target);
                    }
                }
            });
        } else {
            into(target, (RequestListener) target);
        }
        return target;
    }

    @NonNull
    public Target<TranscodeType> preload(int width, int height) {
        return into(PreloadTarget.obtain(this.requestManager, width, height));
    }

    @NonNull
    public Target<TranscodeType> preload() {
        return preload(Integer.MIN_VALUE, Integer.MIN_VALUE);
    }

    @Deprecated
    @CheckResult
    public <Y extends Target<File>> Y downloadOnly(@NonNull Y target) {
        return getDownloadOnlyRequest().into((Target) target);
    }

    @Deprecated
    @CheckResult
    public FutureTarget<File> downloadOnly(int width, int height) {
        return getDownloadOnlyRequest().submit(width, height);
    }

    @CheckResult
    @NonNull
    protected RequestBuilder<File> getDownloadOnlyRequest() {
        return new RequestBuilder(File.class, this).apply(DOWNLOAD_ONLY_OPTIONS);
    }

    @NonNull
    private Priority getThumbnailPriority(@NonNull Priority current) {
        switch (current) {
            case LOW:
                return Priority.NORMAL;
            case NORMAL:
                return Priority.HIGH;
            case HIGH:
            case IMMEDIATE:
                return Priority.IMMEDIATE;
            default:
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("unknown priority: ");
                stringBuilder.append(this.requestOptions.getPriority());
                throw new IllegalArgumentException(stringBuilder.toString());
        }
    }

    private Request buildRequest(Target<TranscodeType> target, @Nullable RequestListener<TranscodeType> targetListener, RequestOptions requestOptions) {
        return buildRequestRecursive(target, targetListener, null, this.transitionOptions, requestOptions.getPriority(), requestOptions.getOverrideWidth(), requestOptions.getOverrideHeight(), requestOptions);
    }

    private Request buildRequestRecursive(Target<TranscodeType> target, @Nullable RequestListener<TranscodeType> targetListener, @Nullable RequestCoordinator parentCoordinator, TransitionOptions<?, ? super TranscodeType> transitionOptions, Priority priority, int overrideWidth, int overrideHeight, RequestOptions requestOptions) {
        ErrorRequestCoordinator errorRequestCoordinator;
        RequestCoordinator parentCoordinator2;
        if (this.errorBuilder != null) {
            ErrorRequestCoordinator errorRequestCoordinator2 = new ErrorRequestCoordinator(parentCoordinator);
            errorRequestCoordinator = errorRequestCoordinator2;
            parentCoordinator2 = errorRequestCoordinator2;
        } else {
            errorRequestCoordinator = null;
            parentCoordinator2 = parentCoordinator;
        }
        Request mainRequest = buildThumbnailRequestRecursive(target, targetListener, parentCoordinator2, transitionOptions, priority, overrideWidth, overrideHeight, requestOptions);
        if (errorRequestCoordinator == null) {
            return mainRequest;
        }
        int errorOverrideWidth;
        int errorOverrideHeight;
        Request errorRequest;
        int errorOverrideWidth2 = r9.errorBuilder.requestOptions.getOverrideWidth();
        int errorOverrideHeight2 = r9.errorBuilder.requestOptions.getOverrideHeight();
        if (Util.isValidDimensions(overrideWidth, overrideHeight)) {
            if (!r9.errorBuilder.requestOptions.isValidOverride()) {
                errorOverrideWidth = requestOptions.getOverrideWidth();
                errorOverrideHeight = requestOptions.getOverrideHeight();
                errorRequest = r9.errorBuilder;
                errorRequestCoordinator.setRequests(mainRequest, errorRequest.buildRequestRecursive(target, targetListener, errorRequestCoordinator, errorRequest.transitionOptions, errorRequest.requestOptions.getPriority(), errorOverrideWidth, errorOverrideHeight, r9.errorBuilder.requestOptions));
                return errorRequestCoordinator;
            }
        }
        errorOverrideWidth = errorOverrideWidth2;
        errorOverrideHeight = errorOverrideHeight2;
        errorRequest = r9.errorBuilder;
        errorRequestCoordinator.setRequests(mainRequest, errorRequest.buildRequestRecursive(target, targetListener, errorRequestCoordinator, errorRequest.transitionOptions, errorRequest.requestOptions.getPriority(), errorOverrideWidth, errorOverrideHeight, r9.errorBuilder.requestOptions));
        return errorRequestCoordinator;
    }

    private Request buildThumbnailRequestRecursive(Target<TranscodeType> target, RequestListener<TranscodeType> targetListener, @Nullable RequestCoordinator parentCoordinator, TransitionOptions<?, ? super TranscodeType> transitionOptions, Priority priority, int overrideWidth, int overrideHeight, RequestOptions requestOptions) {
        RequestCoordinator requestCoordinator = parentCoordinator;
        Priority priority2 = priority;
        RequestBuilder requestBuilder = this.thumbnailBuilder;
        if (requestBuilder != null) {
            if (r9.isThumbnailBuilt) {
                throw new IllegalStateException("You cannot use a request as both the main request and a thumbnail, consider using clone() on the request(s) passed to thumbnail()");
            }
            TransitionOptions<?, ? super TranscodeType> thumbTransitionOptions;
            int thumbOverrideWidth;
            int thumbOverrideHeight;
            RequestCoordinator thumbnailRequestCoordinator;
            Request fullRequest;
            RequestBuilder requestBuilder2;
            RequestCoordinator coordinator;
            Request thumbRequest;
            TransitionOptions<?, ? super TranscodeType> thumbTransitionOptions2 = requestBuilder.transitionOptions;
            if (requestBuilder.isDefaultTransitionOptionsSet) {
                thumbTransitionOptions = transitionOptions;
            } else {
                thumbTransitionOptions = thumbTransitionOptions2;
            }
            Priority thumbPriority = r9.thumbnailBuilder.requestOptions.isPrioritySet() ? r9.thumbnailBuilder.requestOptions.getPriority() : getThumbnailPriority(priority2);
            int thumbOverrideWidth2 = r9.thumbnailBuilder.requestOptions.getOverrideWidth();
            int thumbOverrideHeight2 = r9.thumbnailBuilder.requestOptions.getOverrideHeight();
            if (Util.isValidDimensions(overrideWidth, overrideHeight)) {
                if (!r9.thumbnailBuilder.requestOptions.isValidOverride()) {
                    thumbOverrideWidth = requestOptions.getOverrideWidth();
                    thumbOverrideHeight = requestOptions.getOverrideHeight();
                    thumbnailRequestCoordinator = new ThumbnailRequestCoordinator(requestCoordinator);
                    fullRequest = obtainRequest(target, targetListener, requestOptions, thumbnailRequestCoordinator, transitionOptions, priority, overrideWidth, overrideHeight);
                    r9.isThumbnailBuilt = true;
                    requestBuilder2 = r9.thumbnailBuilder;
                    coordinator = thumbnailRequestCoordinator;
                    thumbRequest = requestBuilder2.buildRequestRecursive(target, targetListener, thumbnailRequestCoordinator, thumbTransitionOptions, thumbPriority, thumbOverrideWidth, thumbOverrideHeight, requestBuilder2.requestOptions);
                    r9.isThumbnailBuilt = false;
                    coordinator.setRequests(fullRequest, thumbRequest);
                    return coordinator;
                }
            }
            thumbOverrideWidth = thumbOverrideWidth2;
            thumbOverrideHeight = thumbOverrideHeight2;
            thumbnailRequestCoordinator = new ThumbnailRequestCoordinator(requestCoordinator);
            fullRequest = obtainRequest(target, targetListener, requestOptions, thumbnailRequestCoordinator, transitionOptions, priority, overrideWidth, overrideHeight);
            r9.isThumbnailBuilt = true;
            requestBuilder2 = r9.thumbnailBuilder;
            coordinator = thumbnailRequestCoordinator;
            thumbRequest = requestBuilder2.buildRequestRecursive(target, targetListener, thumbnailRequestCoordinator, thumbTransitionOptions, thumbPriority, thumbOverrideWidth, thumbOverrideHeight, requestBuilder2.requestOptions);
            r9.isThumbnailBuilt = false;
            coordinator.setRequests(fullRequest, thumbRequest);
            return coordinator;
        } else if (r9.thumbSizeMultiplier == null) {
            return obtainRequest(target, targetListener, requestOptions, parentCoordinator, transitionOptions, priority, overrideWidth, overrideHeight);
        } else {
            ThumbnailRequestCoordinator coordinator2 = new ThumbnailRequestCoordinator(requestCoordinator);
            RequestListener<TranscodeType> requestListener = targetListener;
            ThumbnailRequestCoordinator thumbnailRequestCoordinator2 = coordinator2;
            TransitionOptions<?, ? super TranscodeType> transitionOptions2 = transitionOptions;
            int i = overrideWidth;
            int i2 = overrideHeight;
            coordinator2.setRequests(obtainRequest(target, requestListener, requestOptions, thumbnailRequestCoordinator2, transitionOptions2, priority, i, i2), obtainRequest(target, requestListener, requestOptions.clone().sizeMultiplier(r9.thumbSizeMultiplier.floatValue()), thumbnailRequestCoordinator2, transitionOptions2, getThumbnailPriority(priority2), i, i2));
            return coordinator2;
        }
    }

    private Request obtainRequest(Target<TranscodeType> target, RequestListener<TranscodeType> targetListener, RequestOptions requestOptions, RequestCoordinator requestCoordinator, TransitionOptions<?, ? super TranscodeType> transitionOptions, Priority priority, int overrideWidth, int overrideHeight) {
        Context context = this.context;
        GlideContext glideContext = this.glideContext;
        return SingleRequest.obtain(context, glideContext, this.model, this.transcodeClass, requestOptions, overrideWidth, overrideHeight, priority, target, targetListener, this.requestListeners, requestCoordinator, glideContext.getEngine(), transitionOptions.getTransitionFactory());
    }
}
