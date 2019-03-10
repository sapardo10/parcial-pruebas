package com.bumptech.glide.request;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pools.Pool;
import android.util.Log;
import com.bumptech.glide.GlideContext;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.Engine;
import com.bumptech.glide.load.engine.Engine.LoadStatus;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.drawable.DrawableDecoderCompat;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.TransitionFactory;
import com.bumptech.glide.util.LogTime;
import com.bumptech.glide.util.Util;
import com.bumptech.glide.util.pool.FactoryPools;
import com.bumptech.glide.util.pool.FactoryPools.Factory;
import com.bumptech.glide.util.pool.FactoryPools.Poolable;
import com.bumptech.glide.util.pool.StateVerifier;
import java.util.List;

public final class SingleRequest<R> implements Request, SizeReadyCallback, ResourceCallback, Poolable {
    private static final String GLIDE_TAG = "Glide";
    private static final boolean IS_VERBOSE_LOGGABLE = Log.isLoggable(TAG, 2);
    private static final Pool<SingleRequest<?>> POOL = FactoryPools.simple(150, new C09701());
    private static final String TAG = "Request";
    private TransitionFactory<? super R> animationFactory;
    private Context context;
    private Engine engine;
    private Drawable errorDrawable;
    private Drawable fallbackDrawable;
    private GlideContext glideContext;
    private int height;
    private boolean isCallingCallbacks;
    private LoadStatus loadStatus;
    @Nullable
    private Object model;
    private int overrideHeight;
    private int overrideWidth;
    private Drawable placeholderDrawable;
    private Priority priority;
    private RequestCoordinator requestCoordinator;
    @Nullable
    private List<RequestListener<R>> requestListeners;
    private RequestOptions requestOptions;
    private Resource<R> resource;
    private long startTime;
    private final StateVerifier stateVerifier;
    private Status status;
    @Nullable
    private final String tag;
    private Target<R> target;
    @Nullable
    private RequestListener<R> targetListener;
    private Class<R> transcodeClass;
    private int width;

    private enum Status {
        PENDING,
        RUNNING,
        WAITING_FOR_SIZE,
        COMPLETE,
        FAILED,
        CLEARED
    }

    /* renamed from: com.bumptech.glide.request.SingleRequest$1 */
    class C09701 implements Factory<SingleRequest<?>> {
        C09701() {
        }

        public SingleRequest<?> create() {
            return new SingleRequest();
        }
    }

    private void onLoadFailed(com.bumptech.glide.load.engine.GlideException r10, int r11) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:35:0x00a7 in {4, 5, 6, 14, 15, 16, 21, 22, 23, 24, 28, 29, 31, 34} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r9 = this;
        r0 = r9.stateVerifier;
        r0.throwIfRecycled();
        r0 = r9.glideContext;
        r0 = r0.getLogLevel();
        if (r0 > r11) goto L_0x0049;
    L_0x000d:
        r1 = "Glide";
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "Load failed for ";
        r2.append(r3);
        r3 = r9.model;
        r2.append(r3);
        r3 = " with size [";
        r2.append(r3);
        r3 = r9.width;
        r2.append(r3);
        r3 = "x";
        r2.append(r3);
        r3 = r9.height;
        r2.append(r3);
        r3 = "]";
        r2.append(r3);
        r2 = r2.toString();
        android.util.Log.w(r1, r2, r10);
        r1 = 4;
        if (r0 > r1) goto L_0x0048;
    L_0x0042:
        r1 = "Glide";
        r10.logRootCauses(r1);
        goto L_0x004a;
    L_0x0048:
        goto L_0x004a;
    L_0x004a:
        r1 = 0;
        r9.loadStatus = r1;
        r1 = com.bumptech.glide.request.SingleRequest.Status.FAILED;
        r9.status = r1;
        r1 = 1;
        r9.isCallingCallbacks = r1;
        r2 = 0;
        r3 = 0;
        r4 = r9.requestListeners;	 Catch:{ all -> 0x00a3 }
        if (r4 == 0) goto L_0x007b;	 Catch:{ all -> 0x00a3 }
    L_0x005a:
        r4 = r9.requestListeners;	 Catch:{ all -> 0x00a3 }
        r4 = r4.iterator();	 Catch:{ all -> 0x00a3 }
    L_0x0060:
        r5 = r4.hasNext();	 Catch:{ all -> 0x00a3 }
        if (r5 == 0) goto L_0x007a;	 Catch:{ all -> 0x00a3 }
    L_0x0066:
        r5 = r4.next();	 Catch:{ all -> 0x00a3 }
        r5 = (com.bumptech.glide.request.RequestListener) r5;	 Catch:{ all -> 0x00a3 }
        r6 = r9.model;	 Catch:{ all -> 0x00a3 }
        r7 = r9.target;	 Catch:{ all -> 0x00a3 }
        r8 = r9.isFirstReadyResource();	 Catch:{ all -> 0x00a3 }
        r6 = r5.onLoadFailed(r10, r6, r7, r8);	 Catch:{ all -> 0x00a3 }
        r2 = r2 | r6;	 Catch:{ all -> 0x00a3 }
        goto L_0x0060;	 Catch:{ all -> 0x00a3 }
    L_0x007a:
        goto L_0x007c;	 Catch:{ all -> 0x00a3 }
    L_0x007c:
        r4 = r9.targetListener;	 Catch:{ all -> 0x00a3 }
        if (r4 == 0) goto L_0x0092;	 Catch:{ all -> 0x00a3 }
    L_0x0080:
        r4 = r9.targetListener;	 Catch:{ all -> 0x00a3 }
        r5 = r9.model;	 Catch:{ all -> 0x00a3 }
        r6 = r9.target;	 Catch:{ all -> 0x00a3 }
        r7 = r9.isFirstReadyResource();	 Catch:{ all -> 0x00a3 }
        r4 = r4.onLoadFailed(r10, r5, r6, r7);	 Catch:{ all -> 0x00a3 }
        if (r4 == 0) goto L_0x0091;	 Catch:{ all -> 0x00a3 }
    L_0x0090:
        goto L_0x0094;	 Catch:{ all -> 0x00a3 }
    L_0x0091:
        goto L_0x0093;	 Catch:{ all -> 0x00a3 }
    L_0x0093:
        r1 = 0;	 Catch:{ all -> 0x00a3 }
    L_0x0094:
        r1 = r1 | r2;	 Catch:{ all -> 0x00a3 }
        if (r1 != 0) goto L_0x009b;	 Catch:{ all -> 0x00a3 }
    L_0x0097:
        r9.setErrorPlaceholder();	 Catch:{ all -> 0x00a3 }
        goto L_0x009c;
    L_0x009c:
        r9.isCallingCallbacks = r3;
        r9.notifyLoadFailed();
        return;
    L_0x00a3:
        r1 = move-exception;
        r9.isCallingCallbacks = r3;
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.request.SingleRequest.onLoadFailed(com.bumptech.glide.load.engine.GlideException, int):void");
    }

    private void onResourceReady(com.bumptech.glide.load.engine.Resource<R> r12, R r13, com.bumptech.glide.load.DataSource r14) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:32:0x00cb in {2, 3, 11, 12, 13, 18, 19, 20, 21, 25, 26, 28, 31} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r11 = this;
        r6 = r11.isFirstReadyResource();
        r0 = com.bumptech.glide.request.SingleRequest.Status.COMPLETE;
        r11.status = r0;
        r11.resource = r12;
        r0 = r11.glideContext;
        r0 = r0.getLogLevel();
        r1 = 3;
        if (r0 > r1) goto L_0x006c;
    L_0x0013:
        r0 = "Glide";
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Finished loading ";
        r1.append(r2);
        r2 = r13.getClass();
        r2 = r2.getSimpleName();
        r1.append(r2);
        r2 = " from ";
        r1.append(r2);
        r1.append(r14);
        r2 = " for ";
        r1.append(r2);
        r2 = r11.model;
        r1.append(r2);
        r2 = " with size [";
        r1.append(r2);
        r2 = r11.width;
        r1.append(r2);
        r2 = "x";
        r1.append(r2);
        r2 = r11.height;
        r1.append(r2);
        r2 = "] in ";
        r1.append(r2);
        r2 = r11.startTime;
        r2 = com.bumptech.glide.util.LogTime.getElapsedMillis(r2);
        r1.append(r2);
        r2 = " ms";
        r1.append(r2);
        r1 = r1.toString();
        android.util.Log.d(r0, r1);
        goto L_0x006d;
    L_0x006d:
        r7 = 1;
        r11.isCallingCallbacks = r7;
        r0 = 0;
        r8 = 0;
        r1 = r11.requestListeners;	 Catch:{ all -> 0x00c7 }
        if (r1 == 0) goto L_0x0097;	 Catch:{ all -> 0x00c7 }
    L_0x0076:
        r1 = r11.requestListeners;	 Catch:{ all -> 0x00c7 }
        r9 = r1.iterator();	 Catch:{ all -> 0x00c7 }
        r10 = r0;	 Catch:{ all -> 0x00c7 }
    L_0x007d:
        r0 = r9.hasNext();	 Catch:{ all -> 0x00c7 }
        if (r0 == 0) goto L_0x0096;	 Catch:{ all -> 0x00c7 }
    L_0x0083:
        r0 = r9.next();	 Catch:{ all -> 0x00c7 }
        r0 = (com.bumptech.glide.request.RequestListener) r0;	 Catch:{ all -> 0x00c7 }
        r2 = r11.model;	 Catch:{ all -> 0x00c7 }
        r3 = r11.target;	 Catch:{ all -> 0x00c7 }
        r1 = r13;	 Catch:{ all -> 0x00c7 }
        r4 = r14;	 Catch:{ all -> 0x00c7 }
        r5 = r6;	 Catch:{ all -> 0x00c7 }
        r1 = r0.onResourceReady(r1, r2, r3, r4, r5);	 Catch:{ all -> 0x00c7 }
        r10 = r10 | r1;	 Catch:{ all -> 0x00c7 }
        goto L_0x007d;	 Catch:{ all -> 0x00c7 }
    L_0x0096:
        goto L_0x0098;	 Catch:{ all -> 0x00c7 }
    L_0x0097:
        r10 = r0;	 Catch:{ all -> 0x00c7 }
    L_0x0098:
        r0 = r11.targetListener;	 Catch:{ all -> 0x00c7 }
        if (r0 == 0) goto L_0x00ad;	 Catch:{ all -> 0x00c7 }
    L_0x009c:
        r0 = r11.targetListener;	 Catch:{ all -> 0x00c7 }
        r2 = r11.model;	 Catch:{ all -> 0x00c7 }
        r3 = r11.target;	 Catch:{ all -> 0x00c7 }
        r1 = r13;	 Catch:{ all -> 0x00c7 }
        r4 = r14;	 Catch:{ all -> 0x00c7 }
        r5 = r6;	 Catch:{ all -> 0x00c7 }
        r0 = r0.onResourceReady(r1, r2, r3, r4, r5);	 Catch:{ all -> 0x00c7 }
        if (r0 == 0) goto L_0x00ac;	 Catch:{ all -> 0x00c7 }
    L_0x00ab:
        goto L_0x00af;	 Catch:{ all -> 0x00c7 }
    L_0x00ac:
        goto L_0x00ae;	 Catch:{ all -> 0x00c7 }
    L_0x00ae:
        r7 = 0;	 Catch:{ all -> 0x00c7 }
    L_0x00af:
        r0 = r10 | r7;	 Catch:{ all -> 0x00c7 }
        if (r0 != 0) goto L_0x00bf;	 Catch:{ all -> 0x00c7 }
    L_0x00b3:
        r1 = r11.animationFactory;	 Catch:{ all -> 0x00c7 }
        r1 = r1.build(r14, r6);	 Catch:{ all -> 0x00c7 }
        r2 = r11.target;	 Catch:{ all -> 0x00c7 }
        r2.onResourceReady(r13, r1);	 Catch:{ all -> 0x00c7 }
        goto L_0x00c0;
    L_0x00c0:
        r11.isCallingCallbacks = r8;
        r11.notifyLoadSuccess();
        return;
    L_0x00c7:
        r0 = move-exception;
        r11.isCallingCallbacks = r8;
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.request.SingleRequest.onResourceReady(com.bumptech.glide.load.engine.Resource, java.lang.Object, com.bumptech.glide.load.DataSource):void");
    }

    public static <R> SingleRequest<R> obtain(Context context, GlideContext glideContext, Object model, Class<R> transcodeClass, RequestOptions requestOptions, int overrideWidth, int overrideHeight, Priority priority, Target<R> target, RequestListener<R> targetListener, @Nullable List<RequestListener<R>> requestListeners, RequestCoordinator requestCoordinator, Engine engine, TransitionFactory<? super R> animationFactory) {
        SingleRequest<R> request = (SingleRequest) POOL.acquire();
        if (request == null) {
            request = new SingleRequest();
        }
        request.init(context, glideContext, model, transcodeClass, requestOptions, overrideWidth, overrideHeight, priority, target, targetListener, requestListeners, requestCoordinator, engine, animationFactory);
        return request;
    }

    SingleRequest() {
        this.tag = IS_VERBOSE_LOGGABLE ? String.valueOf(super.hashCode()) : null;
        this.stateVerifier = StateVerifier.newInstance();
    }

    private void init(Context context, GlideContext glideContext, Object model, Class<R> transcodeClass, RequestOptions requestOptions, int overrideWidth, int overrideHeight, Priority priority, Target<R> target, RequestListener<R> targetListener, @Nullable List<RequestListener<R>> requestListeners, RequestCoordinator requestCoordinator, Engine engine, TransitionFactory<? super R> animationFactory) {
        this.context = context;
        this.glideContext = glideContext;
        this.model = model;
        this.transcodeClass = transcodeClass;
        this.requestOptions = requestOptions;
        this.overrideWidth = overrideWidth;
        this.overrideHeight = overrideHeight;
        this.priority = priority;
        this.target = target;
        this.targetListener = targetListener;
        this.requestListeners = requestListeners;
        this.requestCoordinator = requestCoordinator;
        this.engine = engine;
        this.animationFactory = animationFactory;
        this.status = Status.PENDING;
    }

    @NonNull
    public StateVerifier getVerifier() {
        return this.stateVerifier;
    }

    public void recycle() {
        assertNotCallingCallbacks();
        this.context = null;
        this.glideContext = null;
        this.model = null;
        this.transcodeClass = null;
        this.requestOptions = null;
        this.overrideWidth = -1;
        this.overrideHeight = -1;
        this.target = null;
        this.requestListeners = null;
        this.targetListener = null;
        this.requestCoordinator = null;
        this.animationFactory = null;
        this.loadStatus = null;
        this.errorDrawable = null;
        this.placeholderDrawable = null;
        this.fallbackDrawable = null;
        this.width = -1;
        this.height = -1;
        POOL.release(this);
    }

    public void begin() {
        assertNotCallingCallbacks();
        this.stateVerifier.throwIfRecycled();
        this.startTime = LogTime.getLogTime();
        if (this.model == null) {
            if (Util.isValidDimensions(this.overrideWidth, this.overrideHeight)) {
                this.width = this.overrideWidth;
                this.height = this.overrideHeight;
            }
            onLoadFailed(new GlideException("Received null model"), getFallbackDrawable() == null ? 5 : 3);
        } else if (this.status == Status.RUNNING) {
            throw new IllegalArgumentException("Cannot restart a running request");
        } else if (this.status == Status.COMPLETE) {
            onResourceReady(this.resource, DataSource.MEMORY_CACHE);
        } else {
            StringBuilder stringBuilder;
            this.status = Status.WAITING_FOR_SIZE;
            if (Util.isValidDimensions(this.overrideWidth, this.overrideHeight)) {
                onSizeReady(this.overrideWidth, this.overrideHeight);
            } else {
                this.target.getSize(this);
            }
            if (this.status != Status.RUNNING) {
                if (this.status != Status.WAITING_FOR_SIZE) {
                    if (IS_VERBOSE_LOGGABLE) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("finished run method in ");
                        stringBuilder.append(LogTime.getElapsedMillis(this.startTime));
                        logV(stringBuilder.toString());
                    }
                }
            }
            if (canNotifyStatusChanged()) {
                this.target.onLoadStarted(getPlaceholderDrawable());
            }
            if (IS_VERBOSE_LOGGABLE) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("finished run method in ");
                stringBuilder.append(LogTime.getElapsedMillis(this.startTime));
                logV(stringBuilder.toString());
            }
        }
    }

    private void cancel() {
        assertNotCallingCallbacks();
        this.stateVerifier.throwIfRecycled();
        this.target.removeCallback(this);
        LoadStatus loadStatus = this.loadStatus;
        if (loadStatus != null) {
            loadStatus.cancel();
            this.loadStatus = null;
        }
    }

    private void assertNotCallingCallbacks() {
        if (this.isCallingCallbacks) {
            throw new IllegalStateException("You can't start or clear loads in RequestListener or Target callbacks. If you're trying to start a fallback request when a load fails, use RequestBuilder#error(RequestBuilder). Otherwise consider posting your into() or clear() calls to the main thread using a Handler instead.");
        }
    }

    public void clear() {
        Util.assertMainThread();
        assertNotCallingCallbacks();
        this.stateVerifier.throwIfRecycled();
        if (this.status != Status.CLEARED) {
            cancel();
            Resource resource = this.resource;
            if (resource != null) {
                releaseResource(resource);
            }
            if (canNotifyCleared()) {
                this.target.onLoadCleared(getPlaceholderDrawable());
            }
            this.status = Status.CLEARED;
        }
    }

    private void releaseResource(Resource<?> resource) {
        this.engine.release(resource);
        this.resource = null;
    }

    public boolean isRunning() {
        if (this.status != Status.RUNNING) {
            if (this.status != Status.WAITING_FOR_SIZE) {
                return false;
            }
        }
        return true;
    }

    public boolean isComplete() {
        return this.status == Status.COMPLETE;
    }

    public boolean isResourceSet() {
        return isComplete();
    }

    public boolean isCleared() {
        return this.status == Status.CLEARED;
    }

    public boolean isFailed() {
        return this.status == Status.FAILED;
    }

    private Drawable getErrorDrawable() {
        if (this.errorDrawable == null) {
            this.errorDrawable = this.requestOptions.getErrorPlaceholder();
            if (this.errorDrawable == null && this.requestOptions.getErrorId() > 0) {
                this.errorDrawable = loadDrawable(this.requestOptions.getErrorId());
            }
        }
        return this.errorDrawable;
    }

    private Drawable getPlaceholderDrawable() {
        if (this.placeholderDrawable == null) {
            this.placeholderDrawable = this.requestOptions.getPlaceholderDrawable();
            if (this.placeholderDrawable == null && this.requestOptions.getPlaceholderId() > 0) {
                this.placeholderDrawable = loadDrawable(this.requestOptions.getPlaceholderId());
            }
        }
        return this.placeholderDrawable;
    }

    private Drawable getFallbackDrawable() {
        if (this.fallbackDrawable == null) {
            this.fallbackDrawable = this.requestOptions.getFallbackDrawable();
            if (this.fallbackDrawable == null && this.requestOptions.getFallbackId() > 0) {
                this.fallbackDrawable = loadDrawable(this.requestOptions.getFallbackId());
            }
        }
        return this.fallbackDrawable;
    }

    private Drawable loadDrawable(@DrawableRes int resourceId) {
        return DrawableDecoderCompat.getDrawable(this.glideContext, resourceId, this.requestOptions.getTheme() != null ? this.requestOptions.getTheme() : this.context.getTheme());
    }

    private void setErrorPlaceholder() {
        if (canNotifyStatusChanged()) {
            Drawable error = null;
            if (this.model == null) {
                error = getFallbackDrawable();
            }
            if (error == null) {
                error = getErrorDrawable();
            }
            if (error == null) {
                error = getPlaceholderDrawable();
            }
            this.target.onLoadFailed(error);
        }
    }

    public void onSizeReady(int width, int height) {
        this.stateVerifier.throwIfRecycled();
        if (IS_VERBOSE_LOGGABLE) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Got onSizeReady in ");
            stringBuilder.append(LogTime.getElapsedMillis(r15.startTime));
            logV(stringBuilder.toString());
        }
        if (r15.status == Status.WAITING_FOR_SIZE) {
            r15.status = Status.RUNNING;
            float sizeMultiplier = r15.requestOptions.getSizeMultiplier();
            r15.width = maybeApplySizeMultiplier(width, sizeMultiplier);
            r15.height = maybeApplySizeMultiplier(height, sizeMultiplier);
            if (IS_VERBOSE_LOGGABLE) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("finished setup for calling load in ");
                stringBuilder.append(LogTime.getElapsedMillis(r15.startTime));
                logV(stringBuilder.toString());
            }
            Engine engine = r15.engine;
            this.loadStatus = engine.load(r15.glideContext, r15.model, r15.requestOptions.getSignature(), r15.width, r15.height, r15.requestOptions.getResourceClass(), r15.transcodeClass, r15.priority, r15.requestOptions.getDiskCacheStrategy(), r15.requestOptions.getTransformations(), r15.requestOptions.isTransformationRequired(), r15.requestOptions.isScaleOnlyOrNoTransform(), r15.requestOptions.getOptions(), r15.requestOptions.isMemoryCacheable(), r15.requestOptions.getUseUnlimitedSourceGeneratorsPool(), r15.requestOptions.getUseAnimationPool(), r15.requestOptions.getOnlyRetrieveFromCache(), this);
            if (this.status != Status.RUNNING) {
                r1.loadStatus = null;
            }
            if (IS_VERBOSE_LOGGABLE) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("finished onSizeReady in ");
                stringBuilder.append(LogTime.getElapsedMillis(r1.startTime));
                logV(stringBuilder.toString());
            }
        }
    }

    private static int maybeApplySizeMultiplier(int size, float sizeMultiplier) {
        return size == Integer.MIN_VALUE ? size : Math.round(((float) size) * sizeMultiplier);
    }

    private boolean canSetResource() {
        RequestCoordinator requestCoordinator = this.requestCoordinator;
        if (requestCoordinator != null) {
            if (!requestCoordinator.canSetImage(this)) {
                return false;
            }
        }
        return true;
    }

    private boolean canNotifyCleared() {
        RequestCoordinator requestCoordinator = this.requestCoordinator;
        if (requestCoordinator != null) {
            if (!requestCoordinator.canNotifyCleared(this)) {
                return false;
            }
        }
        return true;
    }

    private boolean canNotifyStatusChanged() {
        RequestCoordinator requestCoordinator = this.requestCoordinator;
        if (requestCoordinator != null) {
            if (!requestCoordinator.canNotifyStatusChanged(this)) {
                return false;
            }
        }
        return true;
    }

    private boolean isFirstReadyResource() {
        RequestCoordinator requestCoordinator = this.requestCoordinator;
        if (requestCoordinator != null) {
            if (requestCoordinator.isAnyResourceSet()) {
                return false;
            }
        }
        return true;
    }

    private void notifyLoadSuccess() {
        RequestCoordinator requestCoordinator = this.requestCoordinator;
        if (requestCoordinator != null) {
            requestCoordinator.onRequestSuccess(this);
        }
    }

    private void notifyLoadFailed() {
        RequestCoordinator requestCoordinator = this.requestCoordinator;
        if (requestCoordinator != null) {
            requestCoordinator.onRequestFailed(this);
        }
    }

    public void onResourceReady(Resource<?> resource, DataSource dataSource) {
        this.stateVerifier.throwIfRecycled();
        this.loadStatus = null;
        if (resource == null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Expected to receive a Resource<R> with an object of ");
            stringBuilder.append(this.transcodeClass);
            stringBuilder.append(" inside, but instead got null.");
            onLoadFailed(new GlideException(stringBuilder.toString()));
            return;
        }
        Object received = resource.get();
        if (received != null) {
            if (this.transcodeClass.isAssignableFrom(received.getClass())) {
                if (canSetResource()) {
                    onResourceReady(resource, received, dataSource);
                    return;
                }
                releaseResource(resource);
                this.status = Status.COMPLETE;
                return;
            }
        }
        releaseResource(resource);
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("Expected to receive an object of ");
        stringBuilder2.append(this.transcodeClass);
        stringBuilder2.append(" but instead got ");
        stringBuilder2.append(received != null ? received.getClass() : "");
        stringBuilder2.append("{");
        stringBuilder2.append(received);
        stringBuilder2.append("} inside Resource{");
        stringBuilder2.append(resource);
        stringBuilder2.append("}.");
        stringBuilder2.append(received != null ? "" : " To indicate failure return a null Resource object, rather than a Resource object containing null data.");
        onLoadFailed(new GlideException(stringBuilder2.toString()));
    }

    public void onLoadFailed(GlideException e) {
        onLoadFailed(e, 5);
    }

    public boolean isEquivalentTo(Request o) {
        boolean z = false;
        if (!(o instanceof SingleRequest)) {
            return false;
        }
        SingleRequest<?> that = (SingleRequest) o;
        if (this.overrideWidth == that.overrideWidth && this.overrideHeight == that.overrideHeight) {
            if (Util.bothModelsNullEquivalentOrEquals(this.model, that.model)) {
                if (this.transcodeClass.equals(that.transcodeClass)) {
                    if (this.requestOptions.equals(that.requestOptions) && this.priority == that.priority) {
                        if (listenerCountEquals(this, that)) {
                            z = true;
                            return z;
                        }
                    }
                }
            }
        }
        return z;
    }

    private static boolean listenerCountEquals(SingleRequest<?> first, SingleRequest<?> second) {
        int firstListenerCount = first.requestListeners;
        firstListenerCount = firstListenerCount == 0 ? 0 : firstListenerCount.size();
        List list = second.requestListeners;
        if (firstListenerCount == (list == null ? 0 : list.size())) {
            return true;
        }
        return false;
    }

    private void logV(String message) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(message);
        stringBuilder.append(" this: ");
        stringBuilder.append(this.tag);
        Log.v(str, stringBuilder.toString());
    }
}
