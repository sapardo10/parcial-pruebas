package com.bumptech.glide.load.engine;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v4.util.Pools.Pool;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.executor.GlideExecutor;
import com.bumptech.glide.request.ResourceCallback;
import com.bumptech.glide.util.Util;
import com.bumptech.glide.util.pool.FactoryPools.Poolable;
import com.bumptech.glide.util.pool.StateVerifier;
import java.util.ArrayList;
import java.util.List;

class EngineJob<R> implements Callback<R>, Poolable {
    private static final EngineResourceFactory DEFAULT_FACTORY = new EngineResourceFactory();
    private static final Handler MAIN_THREAD_HANDLER = new Handler(Looper.getMainLooper(), new MainThreadCallback());
    private static final int MSG_CANCELLED = 3;
    private static final int MSG_COMPLETE = 1;
    private static final int MSG_EXCEPTION = 2;
    private final GlideExecutor animationExecutor;
    private final List<ResourceCallback> cbs;
    private DataSource dataSource;
    private DecodeJob<R> decodeJob;
    private final GlideExecutor diskCacheExecutor;
    private EngineResource<?> engineResource;
    private final EngineResourceFactory engineResourceFactory;
    private GlideException exception;
    private boolean hasLoadFailed;
    private boolean hasResource;
    private List<ResourceCallback> ignoredCallbacks;
    private boolean isCacheable;
    private volatile boolean isCancelled;
    private Key key;
    private final EngineJobListener listener;
    private boolean onlyRetrieveFromCache;
    private final Pool<EngineJob<?>> pool;
    private Resource<?> resource;
    private final GlideExecutor sourceExecutor;
    private final GlideExecutor sourceUnlimitedExecutor;
    private final StateVerifier stateVerifier;
    private boolean useAnimationPool;
    private boolean useUnlimitedSourceGeneratorPool;

    @VisibleForTesting
    static class EngineResourceFactory {
        EngineResourceFactory() {
        }

        public <R> EngineResource<R> build(Resource<R> resource, boolean isMemoryCacheable) {
            return new EngineResource(resource, isMemoryCacheable, true);
        }
    }

    private static class MainThreadCallback implements Callback {
        MainThreadCallback() {
        }

        public boolean handleMessage(Message message) {
            EngineJob<?> job = message.obj;
            switch (message.what) {
                case 1:
                    job.handleResultOnMainThread();
                    break;
                case 2:
                    job.handleExceptionOnMainThread();
                    break;
                case 3:
                    job.handleCancelledOnMainThread();
                    break;
                default:
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Unrecognized message: ");
                    stringBuilder.append(message.what);
                    throw new IllegalStateException(stringBuilder.toString());
            }
            return true;
        }
    }

    void handleExceptionOnMainThread() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:22:0x0059 in {3, 13, 14, 15, 17, 19, 21} preds:[]
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
        r4 = this;
        r0 = r4.stateVerifier;
        r0.throwIfRecycled();
        r0 = r4.isCancelled;
        r1 = 0;
        if (r0 == 0) goto L_0x000e;
    L_0x000a:
        r4.release(r1);
        return;
    L_0x000e:
        r0 = r4.cbs;
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x0051;
    L_0x0016:
        r0 = r4.hasLoadFailed;
        if (r0 != 0) goto L_0x0049;
    L_0x001a:
        r0 = 1;
        r4.hasLoadFailed = r0;
        r0 = r4.listener;
        r2 = r4.key;
        r3 = 0;
        r0.onEngineJobComplete(r4, r2, r3);
        r0 = r4.cbs;
        r0 = r0.iterator();
    L_0x002b:
        r2 = r0.hasNext();
        if (r2 == 0) goto L_0x0045;
    L_0x0031:
        r2 = r0.next();
        r2 = (com.bumptech.glide.request.ResourceCallback) r2;
        r3 = r4.isInIgnoredCallbacks(r2);
        if (r3 != 0) goto L_0x0043;
    L_0x003d:
        r3 = r4.exception;
        r2.onLoadFailed(r3);
        goto L_0x0044;
    L_0x0044:
        goto L_0x002b;
    L_0x0045:
        r4.release(r1);
        return;
    L_0x0049:
        r0 = new java.lang.IllegalStateException;
        r1 = "Already failed once";
        r0.<init>(r1);
        throw r0;
    L_0x0051:
        r0 = new java.lang.IllegalStateException;
        r1 = "Received an exception without any callbacks to notify";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.load.engine.EngineJob.handleExceptionOnMainThread():void");
    }

    void handleResultOnMainThread() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:21:0x007d in {3, 12, 13, 14, 16, 18, 20} preds:[]
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
        r6 = this;
        r0 = r6.stateVerifier;
        r0.throwIfRecycled();
        r0 = r6.isCancelled;
        r1 = 0;
        if (r0 == 0) goto L_0x0013;
    L_0x000a:
        r0 = r6.resource;
        r0.recycle();
        r6.release(r1);
        return;
    L_0x0013:
        r0 = r6.cbs;
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x0075;
    L_0x001b:
        r0 = r6.hasResource;
        if (r0 != 0) goto L_0x006d;
    L_0x001f:
        r0 = r6.engineResourceFactory;
        r2 = r6.resource;
        r3 = r6.isCacheable;
        r0 = r0.build(r2, r3);
        r6.engineResource = r0;
        r0 = 1;
        r6.hasResource = r0;
        r0 = r6.engineResource;
        r0.acquire();
        r0 = r6.listener;
        r2 = r6.key;
        r3 = r6.engineResource;
        r0.onEngineJobComplete(r6, r2, r3);
        r0 = 0;
        r2 = r6.cbs;
        r2 = r2.size();
    L_0x0043:
        if (r0 >= r2) goto L_0x0064;
    L_0x0045:
        r3 = r6.cbs;
        r3 = r3.get(r0);
        r3 = (com.bumptech.glide.request.ResourceCallback) r3;
        r4 = r6.isInIgnoredCallbacks(r3);
        if (r4 != 0) goto L_0x0060;
    L_0x0053:
        r4 = r6.engineResource;
        r4.acquire();
        r4 = r6.engineResource;
        r5 = r6.dataSource;
        r3.onResourceReady(r4, r5);
        goto L_0x0061;
    L_0x0061:
        r0 = r0 + 1;
        goto L_0x0043;
    L_0x0064:
        r0 = r6.engineResource;
        r0.release();
        r6.release(r1);
        return;
    L_0x006d:
        r0 = new java.lang.IllegalStateException;
        r1 = "Already have resource";
        r0.<init>(r1);
        throw r0;
    L_0x0075:
        r0 = new java.lang.IllegalStateException;
        r1 = "Received a resource without any callbacks to notify";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.load.engine.EngineJob.handleResultOnMainThread():void");
    }

    EngineJob(GlideExecutor diskCacheExecutor, GlideExecutor sourceExecutor, GlideExecutor sourceUnlimitedExecutor, GlideExecutor animationExecutor, EngineJobListener listener, Pool<EngineJob<?>> pool) {
        this(diskCacheExecutor, sourceExecutor, sourceUnlimitedExecutor, animationExecutor, listener, pool, DEFAULT_FACTORY);
    }

    @VisibleForTesting
    EngineJob(GlideExecutor diskCacheExecutor, GlideExecutor sourceExecutor, GlideExecutor sourceUnlimitedExecutor, GlideExecutor animationExecutor, EngineJobListener listener, Pool<EngineJob<?>> pool, EngineResourceFactory engineResourceFactory) {
        this.cbs = new ArrayList(2);
        this.stateVerifier = StateVerifier.newInstance();
        this.diskCacheExecutor = diskCacheExecutor;
        this.sourceExecutor = sourceExecutor;
        this.sourceUnlimitedExecutor = sourceUnlimitedExecutor;
        this.animationExecutor = animationExecutor;
        this.listener = listener;
        this.pool = pool;
        this.engineResourceFactory = engineResourceFactory;
    }

    @VisibleForTesting
    EngineJob<R> init(Key key, boolean isCacheable, boolean useUnlimitedSourceGeneratorPool, boolean useAnimationPool, boolean onlyRetrieveFromCache) {
        this.key = key;
        this.isCacheable = isCacheable;
        this.useUnlimitedSourceGeneratorPool = useUnlimitedSourceGeneratorPool;
        this.useAnimationPool = useAnimationPool;
        this.onlyRetrieveFromCache = onlyRetrieveFromCache;
        return this;
    }

    public void start(DecodeJob<R> decodeJob) {
        GlideExecutor executor;
        this.decodeJob = decodeJob;
        if (decodeJob.willDecodeFromCache()) {
            executor = this.diskCacheExecutor;
        } else {
            executor = getActiveSourceExecutor();
        }
        executor.execute(decodeJob);
    }

    void addCallback(ResourceCallback cb) {
        Util.assertMainThread();
        this.stateVerifier.throwIfRecycled();
        if (this.hasResource) {
            cb.onResourceReady(this.engineResource, this.dataSource);
        } else if (this.hasLoadFailed) {
            cb.onLoadFailed(this.exception);
        } else {
            this.cbs.add(cb);
        }
    }

    void removeCallback(ResourceCallback cb) {
        Util.assertMainThread();
        this.stateVerifier.throwIfRecycled();
        if (!this.hasResource) {
            if (!this.hasLoadFailed) {
                this.cbs.remove(cb);
                if (this.cbs.isEmpty()) {
                    cancel();
                    return;
                }
                return;
            }
        }
        addIgnoredCallback(cb);
    }

    boolean onlyRetrieveFromCache() {
        return this.onlyRetrieveFromCache;
    }

    private GlideExecutor getActiveSourceExecutor() {
        if (this.useUnlimitedSourceGeneratorPool) {
            return this.sourceUnlimitedExecutor;
        }
        return this.useAnimationPool ? this.animationExecutor : this.sourceExecutor;
    }

    private void addIgnoredCallback(ResourceCallback cb) {
        if (this.ignoredCallbacks == null) {
            this.ignoredCallbacks = new ArrayList(2);
        }
        if (!this.ignoredCallbacks.contains(cb)) {
            this.ignoredCallbacks.add(cb);
        }
    }

    private boolean isInIgnoredCallbacks(ResourceCallback cb) {
        List list = this.ignoredCallbacks;
        return list != null && list.contains(cb);
    }

    void cancel() {
        if (!(this.hasLoadFailed || this.hasResource)) {
            if (!this.isCancelled) {
                this.isCancelled = true;
                this.decodeJob.cancel();
                this.listener.onEngineJobCancelled(this, this.key);
            }
        }
    }

    boolean isCancelled() {
        return this.isCancelled;
    }

    void handleCancelledOnMainThread() {
        this.stateVerifier.throwIfRecycled();
        if (this.isCancelled) {
            this.listener.onEngineJobCancelled(this, this.key);
            release(false);
            return;
        }
        throw new IllegalStateException("Not cancelled");
    }

    private void release(boolean isRemovedFromQueue) {
        Util.assertMainThread();
        this.cbs.clear();
        this.key = null;
        this.engineResource = null;
        this.resource = null;
        List list = this.ignoredCallbacks;
        if (list != null) {
            list.clear();
        }
        this.hasLoadFailed = false;
        this.isCancelled = false;
        this.hasResource = false;
        this.decodeJob.release(isRemovedFromQueue);
        this.decodeJob = null;
        this.exception = null;
        this.dataSource = null;
        this.pool.release(this);
    }

    public void onResourceReady(Resource<R> resource, DataSource dataSource) {
        this.resource = resource;
        this.dataSource = dataSource;
        MAIN_THREAD_HANDLER.obtainMessage(1, this).sendToTarget();
    }

    public void onLoadFailed(GlideException e) {
        this.exception = e;
        MAIN_THREAD_HANDLER.obtainMessage(2, this).sendToTarget();
    }

    public void reschedule(DecodeJob<?> job) {
        getActiveSourceExecutor().execute(job);
    }

    @NonNull
    public StateVerifier getVerifier() {
        return this.stateVerifier;
    }
}
