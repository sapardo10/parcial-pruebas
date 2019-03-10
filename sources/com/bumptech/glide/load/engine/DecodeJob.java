package com.bumptech.glide.load.engine;

import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.v4.util.Pools.Pool;
import android.util.Log;
import com.bumptech.glide.GlideContext;
import com.bumptech.glide.Priority;
import com.bumptech.glide.Registry.NoResultEncoderAvailableException;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.EncodeStrategy;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceEncoder;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.data.DataRewinder;
import com.bumptech.glide.load.engine.DataFetcherGenerator.FetcherReadyCallback;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.resource.bitmap.Downsampler;
import com.bumptech.glide.util.LogTime;
import com.bumptech.glide.util.pool.FactoryPools.Poolable;
import com.bumptech.glide.util.pool.GlideTrace;
import com.bumptech.glide.util.pool.StateVerifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class DecodeJob<R> implements FetcherReadyCallback, Runnable, Comparable<DecodeJob<?>>, Poolable {
    private static final String TAG = "DecodeJob";
    private Callback<R> callback;
    private Key currentAttemptingKey;
    private Object currentData;
    private DataSource currentDataSource;
    private DataFetcher<?> currentFetcher;
    private volatile DataFetcherGenerator currentGenerator;
    private Key currentSourceKey;
    private Thread currentThread;
    private final DecodeHelper<R> decodeHelper = new DecodeHelper();
    private final DeferredEncodeManager<?> deferredEncodeManager = new DeferredEncodeManager();
    private final DiskCacheProvider diskCacheProvider;
    private DiskCacheStrategy diskCacheStrategy;
    private GlideContext glideContext;
    private int height;
    private volatile boolean isCallbackNotified;
    private volatile boolean isCancelled;
    private EngineKey loadKey;
    private Object model;
    private boolean onlyRetrieveFromCache;
    private Options options;
    private int order;
    private final Pool<DecodeJob<?>> pool;
    private Priority priority;
    private final ReleaseManager releaseManager = new ReleaseManager();
    private RunReason runReason;
    private Key signature;
    private Stage stage;
    private long startFetchTime;
    private final StateVerifier stateVerifier = StateVerifier.newInstance();
    private final List<Throwable> throwables = new ArrayList();
    private int width;

    interface Callback<R> {
        void onLoadFailed(GlideException glideException);

        void onResourceReady(Resource<R> resource, DataSource dataSource);

        void reschedule(DecodeJob<?> decodeJob);
    }

    private static class DeferredEncodeManager<Z> {
        private ResourceEncoder<Z> encoder;
        private Key key;
        private LockedResource<Z> toEncode;

        DeferredEncodeManager() {
        }

        <X> void init(Key key, ResourceEncoder<X> encoder, LockedResource<X> toEncode) {
            this.key = key;
            this.encoder = encoder;
            this.toEncode = toEncode;
        }

        void encode(DiskCacheProvider diskCacheProvider, Options options) {
            GlideTrace.beginSection("DecodeJob.encode");
            try {
                diskCacheProvider.getDiskCache().put(this.key, new DataCacheWriter(this.encoder, this.toEncode, options));
            } finally {
                this.toEncode.unlock();
                GlideTrace.endSection();
            }
        }

        boolean hasResourceToEncode() {
            return this.toEncode != null;
        }

        void clear() {
            this.key = null;
            this.encoder = null;
            this.toEncode = null;
        }
    }

    interface DiskCacheProvider {
        DiskCache getDiskCache();
    }

    private static class ReleaseManager {
        private boolean isEncodeComplete;
        private boolean isFailed;
        private boolean isReleased;

        ReleaseManager() {
        }

        synchronized boolean release(boolean isRemovedFromQueue) {
            this.isReleased = true;
            return isComplete(isRemovedFromQueue);
        }

        synchronized boolean onEncodeComplete() {
            this.isEncodeComplete = true;
            return isComplete(false);
        }

        synchronized boolean onFailed() {
            this.isFailed = true;
            return isComplete(false);
        }

        synchronized void reset() {
            this.isEncodeComplete = false;
            this.isReleased = false;
            this.isFailed = false;
        }

        private boolean isComplete(boolean isRemovedFromQueue) {
            return (this.isFailed || isRemovedFromQueue || this.isEncodeComplete) && this.isReleased;
        }
    }

    private enum RunReason {
        INITIALIZE,
        SWITCH_TO_SOURCE_SERVICE,
        DECODE_DATA
    }

    private enum Stage {
        INITIALIZE,
        RESOURCE_CACHE,
        DATA_CACHE,
        SOURCE,
        ENCODE,
        FINISHED
    }

    private final class DecodeCallback<Z> implements DecodeCallback<Z> {
        private final DataSource dataSource;

        DecodeCallback(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @NonNull
        public Resource<Z> onResourceDecoded(@NonNull Resource<Z> decoded) {
            return DecodeJob.this.onResourceDecoded(this.dataSource, decoded);
        }
    }

    public void run() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:40:0x007e in {6, 7, 9, 12, 13, 14, 22, 23, 26, 27, 31, 32, 34, 36, 37, 39} preds:[]
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
        r5 = this;
        r0 = "DecodeJob#run(model=%s)";
        r1 = r5.model;
        com.bumptech.glide.util.pool.GlideTrace.beginSectionFormat(r0, r1);
        r0 = r5.currentFetcher;
        r1 = r5.isCancelled;	 Catch:{ Throwable -> 0x002b }
        if (r1 == 0) goto L_0x001b;	 Catch:{ Throwable -> 0x002b }
    L_0x000d:
        r5.notifyFailed();	 Catch:{ Throwable -> 0x002b }
        if (r0 == 0) goto L_0x0016;
    L_0x0012:
        r0.cleanup();
        goto L_0x0017;
    L_0x0017:
        com.bumptech.glide.util.pool.GlideTrace.endSection();
        return;
    L_0x001b:
        r5.runWrapped();	 Catch:{ Throwable -> 0x002b }
        if (r0 == 0) goto L_0x0024;
    L_0x0020:
        r0.cleanup();
        goto L_0x0025;
    L_0x0025:
        com.bumptech.glide.util.pool.GlideTrace.endSection();
        goto L_0x0070;
    L_0x0029:
        r1 = move-exception;
        goto L_0x0073;
    L_0x002b:
        r1 = move-exception;
        r2 = "DecodeJob";	 Catch:{ all -> 0x0029 }
        r3 = 3;	 Catch:{ all -> 0x0029 }
        r2 = android.util.Log.isLoggable(r2, r3);	 Catch:{ all -> 0x0029 }
        if (r2 == 0) goto L_0x0058;	 Catch:{ all -> 0x0029 }
    L_0x0035:
        r2 = "DecodeJob";	 Catch:{ all -> 0x0029 }
        r3 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0029 }
        r3.<init>();	 Catch:{ all -> 0x0029 }
        r4 = "DecodeJob threw unexpectedly, isCancelled: ";	 Catch:{ all -> 0x0029 }
        r3.append(r4);	 Catch:{ all -> 0x0029 }
        r4 = r5.isCancelled;	 Catch:{ all -> 0x0029 }
        r3.append(r4);	 Catch:{ all -> 0x0029 }
        r4 = ", stage: ";	 Catch:{ all -> 0x0029 }
        r3.append(r4);	 Catch:{ all -> 0x0029 }
        r4 = r5.stage;	 Catch:{ all -> 0x0029 }
        r3.append(r4);	 Catch:{ all -> 0x0029 }
        r3 = r3.toString();	 Catch:{ all -> 0x0029 }
        android.util.Log.d(r2, r3, r1);	 Catch:{ all -> 0x0029 }
        goto L_0x0059;	 Catch:{ all -> 0x0029 }
    L_0x0059:
        r2 = r5.stage;	 Catch:{ all -> 0x0029 }
        r3 = com.bumptech.glide.load.engine.DecodeJob.Stage.ENCODE;	 Catch:{ all -> 0x0029 }
        if (r2 == r3) goto L_0x0068;	 Catch:{ all -> 0x0029 }
    L_0x005f:
        r2 = r5.throwables;	 Catch:{ all -> 0x0029 }
        r2.add(r1);	 Catch:{ all -> 0x0029 }
        r5.notifyFailed();	 Catch:{ all -> 0x0029 }
        goto L_0x0069;	 Catch:{ all -> 0x0029 }
    L_0x0069:
        r2 = r5.isCancelled;	 Catch:{ all -> 0x0029 }
        if (r2 == 0) goto L_0x0071;	 Catch:{ all -> 0x0029 }
    L_0x006d:
        if (r0 == 0) goto L_0x0024;	 Catch:{ all -> 0x0029 }
    L_0x006f:
        goto L_0x0020;	 Catch:{ all -> 0x0029 }
    L_0x0070:
        return;	 Catch:{ all -> 0x0029 }
        throw r1;	 Catch:{ all -> 0x0029 }
    L_0x0073:
        if (r0 == 0) goto L_0x0079;
    L_0x0075:
        r0.cleanup();
        goto L_0x007a;
    L_0x007a:
        com.bumptech.glide.util.pool.GlideTrace.endSection();
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.load.engine.DecodeJob.run():void");
    }

    DecodeJob(DiskCacheProvider diskCacheProvider, Pool<DecodeJob<?>> pool) {
        this.diskCacheProvider = diskCacheProvider;
        this.pool = pool;
    }

    DecodeJob<R> init(GlideContext glideContext, Object model, EngineKey loadKey, Key signature, int width, int height, Class<?> resourceClass, Class<R> transcodeClass, Priority priority, DiskCacheStrategy diskCacheStrategy, Map<Class<?>, Transformation<?>> transformations, boolean isTransformationRequired, boolean isScaleOnlyOrNoTransform, boolean onlyRetrieveFromCache, Options options, Callback<R> callback, int order) {
        int i = width;
        int i2 = height;
        DiskCacheStrategy diskCacheStrategy2 = diskCacheStrategy;
        this.decodeHelper.init(glideContext, model, signature, i, i2, diskCacheStrategy2, resourceClass, transcodeClass, priority, options, transformations, isTransformationRequired, isScaleOnlyOrNoTransform, this.diskCacheProvider);
        this.glideContext = glideContext;
        this.signature = signature;
        this.priority = priority;
        this.loadKey = loadKey;
        this.width = i;
        this.height = i2;
        this.diskCacheStrategy = diskCacheStrategy2;
        this.onlyRetrieveFromCache = onlyRetrieveFromCache;
        this.options = options;
        this.callback = callback;
        this.order = order;
        this.runReason = RunReason.INITIALIZE;
        this.model = model;
        return r0;
    }

    boolean willDecodeFromCache() {
        Stage firstStage = getNextStage(Stage.INITIALIZE);
        if (firstStage != Stage.RESOURCE_CACHE) {
            if (firstStage != Stage.DATA_CACHE) {
                return false;
            }
        }
        return true;
    }

    void release(boolean isRemovedFromQueue) {
        if (this.releaseManager.release(isRemovedFromQueue)) {
            releaseInternal();
        }
    }

    private void onEncodeComplete() {
        if (this.releaseManager.onEncodeComplete()) {
            releaseInternal();
        }
    }

    private void onLoadFailed() {
        if (this.releaseManager.onFailed()) {
            releaseInternal();
        }
    }

    private void releaseInternal() {
        this.releaseManager.reset();
        this.deferredEncodeManager.clear();
        this.decodeHelper.clear();
        this.isCallbackNotified = false;
        this.glideContext = null;
        this.signature = null;
        this.options = null;
        this.priority = null;
        this.loadKey = null;
        this.callback = null;
        this.stage = null;
        this.currentGenerator = null;
        this.currentThread = null;
        this.currentSourceKey = null;
        this.currentData = null;
        this.currentDataSource = null;
        this.currentFetcher = null;
        this.startFetchTime = 0;
        this.isCancelled = false;
        this.model = null;
        this.throwables.clear();
        this.pool.release(this);
    }

    public int compareTo(@NonNull DecodeJob<?> other) {
        int result = getPriority() - other.getPriority();
        if (result == 0) {
            return this.order - other.order;
        }
        return result;
    }

    private int getPriority() {
        return this.priority.ordinal();
    }

    public void cancel() {
        this.isCancelled = true;
        DataFetcherGenerator local = this.currentGenerator;
        if (local != null) {
            local.cancel();
        }
    }

    private void runWrapped() {
        switch (this.runReason) {
            case INITIALIZE:
                this.stage = getNextStage(Stage.INITIALIZE);
                this.currentGenerator = getNextGenerator();
                runGenerators();
                return;
            case SWITCH_TO_SOURCE_SERVICE:
                runGenerators();
                return;
            case DECODE_DATA:
                decodeFromRetrievedData();
                return;
            default:
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Unrecognized run reason: ");
                stringBuilder.append(this.runReason);
                throw new IllegalStateException(stringBuilder.toString());
        }
    }

    private DataFetcherGenerator getNextGenerator() {
        switch (this.stage) {
            case RESOURCE_CACHE:
                return new ResourceCacheGenerator(this.decodeHelper, this);
            case DATA_CACHE:
                return new DataCacheGenerator(this.decodeHelper, this);
            case SOURCE:
                return new SourceGenerator(this.decodeHelper, this);
            case FINISHED:
                return null;
            default:
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Unrecognized stage: ");
                stringBuilder.append(this.stage);
                throw new IllegalStateException(stringBuilder.toString());
        }
    }

    private void runGenerators() {
        this.currentThread = Thread.currentThread();
        this.startFetchTime = LogTime.getLogTime();
        boolean isStarted = false;
        while (!this.isCancelled && this.currentGenerator != null) {
            boolean startNext = this.currentGenerator.startNext();
            isStarted = startNext;
            if (startNext) {
                break;
            }
            this.stage = getNextStage(this.stage);
            this.currentGenerator = getNextGenerator();
            if (this.stage == Stage.SOURCE) {
                reschedule();
                return;
            }
        }
        if ((this.stage == Stage.FINISHED || this.isCancelled) && !isStarted) {
            notifyFailed();
        }
    }

    private void notifyFailed() {
        setNotifiedOrThrow();
        this.callback.onLoadFailed(new GlideException("Failed to load resource", new ArrayList(this.throwables)));
        onLoadFailed();
    }

    private void notifyComplete(Resource<R> resource, DataSource dataSource) {
        setNotifiedOrThrow();
        this.callback.onResourceReady(resource, dataSource);
    }

    private void setNotifiedOrThrow() {
        this.stateVerifier.throwIfRecycled();
        if (this.isCallbackNotified) {
            throw new IllegalStateException("Already notified");
        }
        this.isCallbackNotified = true;
    }

    private Stage getNextStage(Stage current) {
        switch (current) {
            case RESOURCE_CACHE:
                return this.diskCacheStrategy.decodeCachedData() ? Stage.DATA_CACHE : getNextStage(Stage.DATA_CACHE);
            case DATA_CACHE:
                return this.onlyRetrieveFromCache ? Stage.FINISHED : Stage.SOURCE;
            case SOURCE:
            case FINISHED:
                return Stage.FINISHED;
            case INITIALIZE:
                return this.diskCacheStrategy.decodeCachedResource() ? Stage.RESOURCE_CACHE : getNextStage(Stage.RESOURCE_CACHE);
            default:
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Unrecognized stage: ");
                stringBuilder.append(current);
                throw new IllegalArgumentException(stringBuilder.toString());
        }
    }

    public void reschedule() {
        this.runReason = RunReason.SWITCH_TO_SOURCE_SERVICE;
        this.callback.reschedule(this);
    }

    public void onDataFetcherReady(Key sourceKey, Object data, DataFetcher<?> fetcher, DataSource dataSource, Key attemptedKey) {
        this.currentSourceKey = sourceKey;
        this.currentData = data;
        this.currentFetcher = fetcher;
        this.currentDataSource = dataSource;
        this.currentAttemptingKey = attemptedKey;
        if (Thread.currentThread() != this.currentThread) {
            this.runReason = RunReason.DECODE_DATA;
            this.callback.reschedule(this);
            return;
        }
        GlideTrace.beginSection("DecodeJob.decodeFromRetrievedData");
        try {
            decodeFromRetrievedData();
        } finally {
            GlideTrace.endSection();
        }
    }

    public void onDataFetcherFailed(Key attemptedKey, Exception e, DataFetcher<?> fetcher, DataSource dataSource) {
        fetcher.cleanup();
        GlideException exception = new GlideException("Fetching data failed", (Throwable) e);
        exception.setLoggingDetails(attemptedKey, dataSource, fetcher.getDataClass());
        this.throwables.add(exception);
        if (Thread.currentThread() != this.currentThread) {
            this.runReason = RunReason.SWITCH_TO_SOURCE_SERVICE;
            this.callback.reschedule(this);
            return;
        }
        runGenerators();
    }

    private void decodeFromRetrievedData() {
        if (Log.isLoggable(TAG, 2)) {
            long j = this.startFetchTime;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("data: ");
            stringBuilder.append(this.currentData);
            stringBuilder.append(", cache key: ");
            stringBuilder.append(this.currentSourceKey);
            stringBuilder.append(", fetcher: ");
            stringBuilder.append(this.currentFetcher);
            logWithTimeAndKey("Retrieved data", j, stringBuilder.toString());
        }
        Resource<R> resource = null;
        try {
            resource = decodeFromData(this.currentFetcher, this.currentData, this.currentDataSource);
        } catch (GlideException e) {
            e.setLoggingDetails(this.currentAttemptingKey, this.currentDataSource);
            this.throwables.add(e);
        }
        if (resource != null) {
            notifyEncodeAndRelease(resource, this.currentDataSource);
        } else {
            runGenerators();
        }
    }

    private void notifyEncodeAndRelease(Resource<R> resource, DataSource dataSource) {
        if (resource instanceof Initializable) {
            ((Initializable) resource).initialize();
        }
        Resource<R> result = resource;
        LockedResource<R> lockedResource = null;
        if (this.deferredEncodeManager.hasResourceToEncode()) {
            lockedResource = LockedResource.obtain(resource);
            result = lockedResource;
        }
        notifyComplete(result, dataSource);
        this.stage = Stage.ENCODE;
        try {
            if (this.deferredEncodeManager.hasResourceToEncode()) {
                this.deferredEncodeManager.encode(this.diskCacheProvider, this.options);
            }
            if (lockedResource != null) {
                lockedResource.unlock();
            }
            onEncodeComplete();
        } catch (Throwable th) {
            if (lockedResource != null) {
                lockedResource.unlock();
            }
        }
    }

    private <Data> Resource<R> decodeFromData(DataFetcher<?> fetcher, Data data, DataSource dataSource) throws GlideException {
        if (data == null) {
            fetcher.cleanup();
            return null;
        }
        try {
            long startTime = LogTime.getLogTime();
            Resource<R> result = decodeFromFetcher(data, dataSource);
            if (Log.isLoggable(TAG, 2)) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Decoded result ");
                stringBuilder.append(result);
                logWithTimeAndKey(stringBuilder.toString(), startTime);
            }
            fetcher.cleanup();
            return result;
        } catch (Throwable th) {
            fetcher.cleanup();
        }
    }

    private <Data> Resource<R> decodeFromFetcher(Data data, DataSource dataSource) throws GlideException {
        return runLoadPath(data, dataSource, this.decodeHelper.getLoadPath(data.getClass()));
    }

    @NonNull
    private Options getOptionsWithHardwareConfig(DataSource dataSource) {
        Options options = this.options;
        if (VERSION.SDK_INT < 26) {
            return options;
        }
        boolean isHardwareConfigSafe;
        Boolean isHardwareConfigAllowed;
        if (dataSource != DataSource.RESOURCE_DISK_CACHE) {
            if (!this.decodeHelper.isScaleOnlyOrNoTransform()) {
                isHardwareConfigSafe = false;
                isHardwareConfigAllowed = (Boolean) options.get(Downsampler.ALLOW_HARDWARE_CONFIG);
                if (isHardwareConfigAllowed == null && (!isHardwareConfigAllowed.booleanValue() || isHardwareConfigSafe)) {
                    return options;
                }
                options = new Options();
                options.putAll(this.options);
                options.set(Downsampler.ALLOW_HARDWARE_CONFIG, Boolean.valueOf(isHardwareConfigSafe));
                return options;
            }
        }
        isHardwareConfigSafe = true;
        isHardwareConfigAllowed = (Boolean) options.get(Downsampler.ALLOW_HARDWARE_CONFIG);
        if (isHardwareConfigAllowed == null) {
        }
        options = new Options();
        options.putAll(this.options);
        options.set(Downsampler.ALLOW_HARDWARE_CONFIG, Boolean.valueOf(isHardwareConfigSafe));
        return options;
    }

    private <Data, ResourceType> Resource<R> runLoadPath(Data data, DataSource dataSource, LoadPath<Data, ResourceType, R> path) throws GlideException {
        Options options = getOptionsWithHardwareConfig(dataSource);
        DataRewinder<Data> rewinder = this.glideContext.getRegistry().getRewinder(data);
        try {
            Resource<R> load = path.load(rewinder, options, this.width, this.height, new DecodeCallback(dataSource));
            return load;
        } finally {
            rewinder.cleanup();
        }
    }

    private void logWithTimeAndKey(String message, long startTime) {
        logWithTimeAndKey(message, startTime, null);
    }

    private void logWithTimeAndKey(String message, long startTime, String extraArgs) {
        String stringBuilder;
        String str = TAG;
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(message);
        stringBuilder2.append(" in ");
        stringBuilder2.append(LogTime.getElapsedMillis(startTime));
        stringBuilder2.append(", load key: ");
        stringBuilder2.append(this.loadKey);
        if (extraArgs != null) {
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append(", ");
            stringBuilder3.append(extraArgs);
            stringBuilder = stringBuilder3.toString();
        } else {
            stringBuilder = "";
        }
        stringBuilder2.append(stringBuilder);
        stringBuilder2.append(", thread: ");
        stringBuilder2.append(Thread.currentThread().getName());
        Log.v(str, stringBuilder2.toString());
    }

    @NonNull
    public StateVerifier getVerifier() {
        return this.stateVerifier;
    }

    @NonNull
    <Z> Resource<Z> onResourceDecoded(DataSource dataSource, @NonNull Resource<Z> decoded) {
        Transformation<Z> appliedTransformation;
        Resource<Z> transformed;
        ResourceEncoder<Z> encoder;
        EncodeStrategy encodeStrategy;
        DecodeJob decodeJob = this;
        DataSource dataSource2 = dataSource;
        Resource<Z> resource = decoded;
        Class<Z> resourceSubClass = decoded.get().getClass();
        Resource<Z> transformed2 = decoded;
        if (dataSource2 != DataSource.RESOURCE_DISK_CACHE) {
            Transformation<Z> appliedTransformation2 = decodeJob.decodeHelper.getTransformation(resourceSubClass);
            appliedTransformation = appliedTransformation2;
            transformed = appliedTransformation2.transform(decodeJob.glideContext, resource, decodeJob.width, decodeJob.height);
        } else {
            appliedTransformation = null;
            transformed = transformed2;
        }
        if (!resource.equals(transformed)) {
            decoded.recycle();
        }
        if (decodeJob.decodeHelper.isResourceEncoderAvailable(transformed)) {
            ResourceEncoder<Z> encoder2 = decodeJob.decodeHelper.getResultEncoder(transformed);
            encoder = encoder2;
            encodeStrategy = encoder2.getEncodeStrategy(decodeJob.options);
        } else {
            encoder = null;
            encodeStrategy = EncodeStrategy.NONE;
        }
        Resource<Z> result = transformed;
        boolean isFromAlternateCacheKey = decodeJob.decodeHelper.isSourceKey(decodeJob.currentSourceKey) ^ 1;
        ResourceEncoder<Z> resourceEncoder;
        if (!decodeJob.diskCacheStrategy.isResourceCacheable(isFromAlternateCacheKey, dataSource2, encodeStrategy)) {
            resourceEncoder = encoder;
            return result;
        } else if (encoder != null) {
            Key key;
            switch (encodeStrategy) {
                case SOURCE:
                    resourceEncoder = encoder;
                    key = new DataCacheKey(decodeJob.currentSourceKey, decodeJob.signature);
                    break;
                case TRANSFORMED:
                    boolean z = isFromAlternateCacheKey;
                    resourceEncoder = encoder;
                    key = new ResourceCacheKey(decodeJob.decodeHelper.getArrayPool(), decodeJob.currentSourceKey, decodeJob.signature, decodeJob.width, decodeJob.height, appliedTransformation, resourceSubClass, decodeJob.options);
                    break;
                default:
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Unknown strategy: ");
                    stringBuilder.append(encodeStrategy);
                    throw new IllegalArgumentException(stringBuilder.toString());
            }
            LockedResource<Z> lockedResult = LockedResource.obtain(transformed);
            decodeJob.deferredEncodeManager.init(key, resourceEncoder, lockedResult);
            return lockedResult;
        } else {
            resourceEncoder = encoder;
            throw new NoResultEncoderAvailableException(transformed.get().getClass());
        }
    }
}
