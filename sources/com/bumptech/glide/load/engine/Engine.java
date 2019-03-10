package com.bumptech.glide.load.engine;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.util.Pools.Pool;
import android.util.Log;
import com.bumptech.glide.GlideContext;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.DiskCacheAdapter;
import com.bumptech.glide.load.engine.cache.MemoryCache;
import com.bumptech.glide.load.engine.cache.MemoryCache.ResourceRemovedListener;
import com.bumptech.glide.load.engine.executor.GlideExecutor;
import com.bumptech.glide.request.ResourceCallback;
import com.bumptech.glide.util.LogTime;
import com.bumptech.glide.util.Preconditions;
import com.bumptech.glide.util.Util;
import com.bumptech.glide.util.pool.FactoryPools;
import com.bumptech.glide.util.pool.FactoryPools.Factory;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class Engine implements EngineJobListener, ResourceRemovedListener, ResourceListener {
    private static final int JOB_POOL_SIZE = 150;
    private static final String TAG = "Engine";
    private static final boolean VERBOSE_IS_LOGGABLE = Log.isLoggable(TAG, 2);
    private final ActiveResources activeResources;
    private final MemoryCache cache;
    private final DecodeJobFactory decodeJobFactory;
    private final LazyDiskCacheProvider diskCacheProvider;
    private final EngineJobFactory engineJobFactory;
    private final Jobs jobs;
    private final EngineKeyFactory keyFactory;
    private final ResourceRecycler resourceRecycler;

    @VisibleForTesting
    static class DecodeJobFactory {
        private int creationOrder;
        final DiskCacheProvider diskCacheProvider;
        final Pool<DecodeJob<?>> pool = FactoryPools.simple(Engine.JOB_POOL_SIZE, new C09471());

        /* renamed from: com.bumptech.glide.load.engine.Engine$DecodeJobFactory$1 */
        class C09471 implements Factory<DecodeJob<?>> {
            C09471() {
            }

            public DecodeJob<?> create() {
                return new DecodeJob(DecodeJobFactory.this.diskCacheProvider, DecodeJobFactory.this.pool);
            }
        }

        DecodeJobFactory(DiskCacheProvider diskCacheProvider) {
            this.diskCacheProvider = diskCacheProvider;
        }

        <R> DecodeJob<R> build(GlideContext glideContext, Object model, EngineKey loadKey, Key signature, int width, int height, Class<?> resourceClass, Class<R> transcodeClass, Priority priority, DiskCacheStrategy diskCacheStrategy, Map<Class<?>, Transformation<?>> transformations, boolean isTransformationRequired, boolean isScaleOnlyOrNoTransform, boolean onlyRetrieveFromCache, Options options, Callback<R> callback) {
            GlideContext glideContext2 = glideContext;
            Object obj = model;
            EngineKey engineKey = loadKey;
            Key key = signature;
            int i = width;
            int i2 = height;
            Class<?> cls = resourceClass;
            Class<R> cls2 = transcodeClass;
            Priority priority2 = priority;
            DiskCacheStrategy diskCacheStrategy2 = diskCacheStrategy;
            Map<Class<?>, Transformation<?>> map = transformations;
            boolean z = isTransformationRequired;
            boolean z2 = isScaleOnlyOrNoTransform;
            boolean z3 = onlyRetrieveFromCache;
            Options options2 = options;
            Callback<R> callback2 = callback;
            DecodeJob<R> decodeJob = (DecodeJob) Preconditions.checkNotNull((DecodeJob) this.pool.acquire());
            int i3 = this.creationOrder;
            int i4 = i3;
            this.creationOrder = i3 + 1;
            return decodeJob.init(glideContext2, obj, engineKey, key, i, i2, cls, cls2, priority2, diskCacheStrategy2, map, z, z2, z3, options2, callback2, i4);
        }
    }

    @VisibleForTesting
    static class EngineJobFactory {
        final GlideExecutor animationExecutor;
        final GlideExecutor diskCacheExecutor;
        final EngineJobListener listener;
        final Pool<EngineJob<?>> pool = FactoryPools.simple(Engine.JOB_POOL_SIZE, new C09481());
        final GlideExecutor sourceExecutor;
        final GlideExecutor sourceUnlimitedExecutor;

        /* renamed from: com.bumptech.glide.load.engine.Engine$EngineJobFactory$1 */
        class C09481 implements Factory<EngineJob<?>> {
            C09481() {
            }

            public EngineJob<?> create() {
                return new EngineJob(EngineJobFactory.this.diskCacheExecutor, EngineJobFactory.this.sourceExecutor, EngineJobFactory.this.sourceUnlimitedExecutor, EngineJobFactory.this.animationExecutor, EngineJobFactory.this.listener, EngineJobFactory.this.pool);
            }
        }

        EngineJobFactory(GlideExecutor diskCacheExecutor, GlideExecutor sourceExecutor, GlideExecutor sourceUnlimitedExecutor, GlideExecutor animationExecutor, EngineJobListener listener) {
            this.diskCacheExecutor = diskCacheExecutor;
            this.sourceExecutor = sourceExecutor;
            this.sourceUnlimitedExecutor = sourceUnlimitedExecutor;
            this.animationExecutor = animationExecutor;
            this.listener = listener;
        }

        @VisibleForTesting
        void shutdown() {
            shutdownAndAwaitTermination(this.diskCacheExecutor);
            shutdownAndAwaitTermination(this.sourceExecutor);
            shutdownAndAwaitTermination(this.sourceUnlimitedExecutor);
            shutdownAndAwaitTermination(this.animationExecutor);
        }

        <R> EngineJob<R> build(Key key, boolean isMemoryCacheable, boolean useUnlimitedSourceGeneratorPool, boolean useAnimationPool, boolean onlyRetrieveFromCache) {
            return ((EngineJob) Preconditions.checkNotNull((EngineJob) this.pool.acquire())).init(key, isMemoryCacheable, useUnlimitedSourceGeneratorPool, useAnimationPool, onlyRetrieveFromCache);
        }

        private static void shutdownAndAwaitTermination(ExecutorService pool) {
            pool.shutdown();
            try {
                if (!pool.awaitTermination(5, TimeUnit.SECONDS)) {
                    pool.shutdownNow();
                    if (!pool.awaitTermination(5, TimeUnit.SECONDS)) {
                        throw new RuntimeException("Failed to shutdown");
                    }
                }
            } catch (InterruptedException ie) {
                throw new RuntimeException(ie);
            }
        }
    }

    public static class LoadStatus {
        private final ResourceCallback cb;
        private final EngineJob<?> engineJob;

        LoadStatus(ResourceCallback cb, EngineJob<?> engineJob) {
            this.cb = cb;
            this.engineJob = engineJob;
        }

        public void cancel() {
            this.engineJob.removeCallback(this.cb);
        }
    }

    private static class LazyDiskCacheProvider implements DiskCacheProvider {
        private volatile DiskCache diskCache;
        private final DiskCache.Factory factory;

        LazyDiskCacheProvider(DiskCache.Factory factory) {
            this.factory = factory;
        }

        @VisibleForTesting
        synchronized void clearDiskCacheIfCreated() {
            if (this.diskCache != null) {
                this.diskCache.clear();
            }
        }

        public DiskCache getDiskCache() {
            if (this.diskCache == null) {
                synchronized (this) {
                    if (this.diskCache == null) {
                        this.diskCache = this.factory.build();
                    }
                    if (this.diskCache == null) {
                        this.diskCache = new DiskCacheAdapter();
                    }
                }
            }
            return this.diskCache;
        }
    }

    public Engine(MemoryCache memoryCache, DiskCache.Factory diskCacheFactory, GlideExecutor diskCacheExecutor, GlideExecutor sourceExecutor, GlideExecutor sourceUnlimitedExecutor, GlideExecutor animationExecutor, boolean isActiveResourceRetentionAllowed) {
        this(memoryCache, diskCacheFactory, diskCacheExecutor, sourceExecutor, sourceUnlimitedExecutor, animationExecutor, null, null, null, null, null, null, isActiveResourceRetentionAllowed);
    }

    @VisibleForTesting
    Engine(MemoryCache cache, DiskCache.Factory diskCacheFactory, GlideExecutor diskCacheExecutor, GlideExecutor sourceExecutor, GlideExecutor sourceUnlimitedExecutor, GlideExecutor animationExecutor, Jobs jobs, EngineKeyFactory keyFactory, ActiveResources activeResources, EngineJobFactory engineJobFactory, DecodeJobFactory decodeJobFactory, ResourceRecycler resourceRecycler, boolean isActiveResourceRetentionAllowed) {
        ActiveResources activeResources2;
        EngineKeyFactory keyFactory2;
        Jobs jobs2;
        EngineJobFactory engineJobFactory2;
        DecodeJobFactory decodeJobFactory2;
        ResourceRecycler resourceRecycler2;
        this.cache = cache;
        this.diskCacheProvider = new LazyDiskCacheProvider(diskCacheFactory);
        if (activeResources == null) {
            activeResources2 = new ActiveResources(isActiveResourceRetentionAllowed);
        } else {
            boolean z = isActiveResourceRetentionAllowed;
            activeResources2 = activeResources;
        }
        r6.activeResources = activeResources2;
        activeResources2.setListener(this);
        if (keyFactory == null) {
            keyFactory2 = new EngineKeyFactory();
        } else {
            keyFactory2 = keyFactory;
        }
        r6.keyFactory = keyFactory2;
        if (jobs == null) {
            jobs2 = new Jobs();
        } else {
            jobs2 = jobs;
        }
        r6.jobs = jobs2;
        if (engineJobFactory == null) {
            engineJobFactory2 = new EngineJobFactory(diskCacheExecutor, sourceExecutor, sourceUnlimitedExecutor, animationExecutor, this);
        } else {
            engineJobFactory2 = engineJobFactory;
        }
        r6.engineJobFactory = engineJobFactory2;
        if (decodeJobFactory == null) {
            decodeJobFactory2 = new DecodeJobFactory(r6.diskCacheProvider);
        } else {
            decodeJobFactory2 = decodeJobFactory;
        }
        r6.decodeJobFactory = decodeJobFactory2;
        if (resourceRecycler == null) {
            resourceRecycler2 = new ResourceRecycler();
        } else {
            resourceRecycler2 = resourceRecycler;
        }
        r6.resourceRecycler = resourceRecycler2;
        cache.setResourceRemovedListener(this);
    }

    public <R> LoadStatus load(GlideContext glideContext, Object model, Key signature, int width, int height, Class<?> resourceClass, Class<R> transcodeClass, Priority priority, DiskCacheStrategy diskCacheStrategy, Map<Class<?>, Transformation<?>> transformations, boolean isTransformationRequired, boolean isScaleOnlyOrNoTransform, Options options, boolean isMemoryCacheable, boolean useUnlimitedSourceExecutorPool, boolean useAnimationPool, boolean onlyRetrieveFromCache, ResourceCallback cb) {
        Engine engine = this;
        boolean z = isMemoryCacheable;
        ResourceCallback resourceCallback = cb;
        Util.assertMainThread();
        long startTime = VERBOSE_IS_LOGGABLE ? LogTime.getLogTime() : 0;
        EngineKey key = engine.keyFactory.buildKey(model, signature, width, height, transformations, resourceClass, transcodeClass, options);
        EngineResource<?> active = loadFromActiveResources(key, z);
        if (active != null) {
            resourceCallback.onResourceReady(active, DataSource.MEMORY_CACHE);
            if (VERBOSE_IS_LOGGABLE) {
                logWithTimeAndKey("Loaded resource from active resources", startTime, key);
            }
            return null;
        }
        EngineResource<?> cached = loadFromCache(key, z);
        if (cached != null) {
            resourceCallback.onResourceReady(cached, DataSource.MEMORY_CACHE);
            if (VERBOSE_IS_LOGGABLE) {
                logWithTimeAndKey("Loaded resource from cache", startTime, key);
            }
            return null;
        }
        EngineJob<?> current = engine.jobs.get(key, onlyRetrieveFromCache);
        if (current != null) {
            current.addCallback(resourceCallback);
            if (VERBOSE_IS_LOGGABLE) {
                logWithTimeAndKey("Added to existing load", startTime, key);
            }
            return new LoadStatus(resourceCallback, current);
        }
        EngineJob<R> engineJob = engine.engineJobFactory.build(key, isMemoryCacheable, useUnlimitedSourceExecutorPool, useAnimationPool, onlyRetrieveFromCache);
        DecodeJob<R> decodeJob = engine.decodeJobFactory.build(glideContext, model, key, signature, width, height, resourceClass, transcodeClass, priority, diskCacheStrategy, transformations, isTransformationRequired, isScaleOnlyOrNoTransform, onlyRetrieveFromCache, options, engineJob);
        engine.jobs.put(key, engineJob);
        engineJob.addCallback(resourceCallback);
        engineJob.start(decodeJob);
        if (VERBOSE_IS_LOGGABLE) {
            logWithTimeAndKey("Started new load", startTime, key);
        }
        return new LoadStatus(resourceCallback, engineJob);
    }

    private static void logWithTimeAndKey(String log, long startTime, Key key) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(log);
        stringBuilder.append(" in ");
        stringBuilder.append(LogTime.getElapsedMillis(startTime));
        stringBuilder.append("ms, key: ");
        stringBuilder.append(key);
        Log.v(str, stringBuilder.toString());
    }

    @Nullable
    private EngineResource<?> loadFromActiveResources(Key key, boolean isMemoryCacheable) {
        if (!isMemoryCacheable) {
            return null;
        }
        EngineResource<?> active = this.activeResources.get(key);
        if (active != null) {
            active.acquire();
        }
        return active;
    }

    private EngineResource<?> loadFromCache(Key key, boolean isMemoryCacheable) {
        if (!isMemoryCacheable) {
            return null;
        }
        EngineResource<?> cached = getEngineResourceFromCache(key);
        if (cached != null) {
            cached.acquire();
            this.activeResources.activate(key, cached);
        }
        return cached;
    }

    private EngineResource<?> getEngineResourceFromCache(Key key) {
        Resource<?> cached = this.cache.remove(key);
        if (cached == null) {
            return null;
        }
        if (cached instanceof EngineResource) {
            return (EngineResource) cached;
        }
        return new EngineResource(cached, true, true);
    }

    public void release(Resource<?> resource) {
        Util.assertMainThread();
        if (resource instanceof EngineResource) {
            ((EngineResource) resource).release();
            return;
        }
        throw new IllegalArgumentException("Cannot release anything but an EngineResource");
    }

    public void onEngineJobComplete(EngineJob<?> engineJob, Key key, EngineResource<?> resource) {
        Util.assertMainThread();
        if (resource != null) {
            resource.setResourceListener(key, this);
            if (resource.isCacheable()) {
                this.activeResources.activate(key, resource);
            }
        }
        this.jobs.removeIfCurrent(key, engineJob);
    }

    public void onEngineJobCancelled(EngineJob<?> engineJob, Key key) {
        Util.assertMainThread();
        this.jobs.removeIfCurrent(key, engineJob);
    }

    public void onResourceRemoved(@NonNull Resource<?> resource) {
        Util.assertMainThread();
        this.resourceRecycler.recycle(resource);
    }

    public void onResourceReleased(Key cacheKey, EngineResource<?> resource) {
        Util.assertMainThread();
        this.activeResources.deactivate(cacheKey);
        if (resource.isCacheable()) {
            this.cache.put(cacheKey, resource);
        } else {
            this.resourceRecycler.recycle(resource);
        }
    }

    public void clearDiskCache() {
        this.diskCacheProvider.getDiskCache().clear();
    }

    @VisibleForTesting
    public void shutdown() {
        this.engineJobFactory.shutdown();
        this.diskCacheProvider.clearDiskCacheIfCreated();
        this.activeResources.shutdown();
    }
}
