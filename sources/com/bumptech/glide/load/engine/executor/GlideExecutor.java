package com.bumptech.glide.load.engine.executor;

import android.os.Process;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy.Builder;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class GlideExecutor implements ExecutorService {
    private static final String ANIMATION_EXECUTOR_NAME = "animation";
    private static final String DEFAULT_DISK_CACHE_EXECUTOR_NAME = "disk-cache";
    private static final int DEFAULT_DISK_CACHE_EXECUTOR_THREADS = 1;
    private static final String DEFAULT_SOURCE_EXECUTOR_NAME = "source";
    private static final long KEEP_ALIVE_TIME_MS = TimeUnit.SECONDS.toMillis(10);
    private static final int MAXIMUM_AUTOMATIC_THREAD_COUNT = 4;
    private static final String SOURCE_UNLIMITED_EXECUTOR_NAME = "source-unlimited";
    private static final String TAG = "GlideExecutor";
    private static volatile int bestThreadCount;
    private final ExecutorService delegate;

    private static final class DefaultThreadFactory implements ThreadFactory {
        private static final int DEFAULT_PRIORITY = 9;
        private final String name;
        final boolean preventNetworkOperations;
        private int threadNum;
        final UncaughtThrowableStrategy uncaughtThrowableStrategy;

        DefaultThreadFactory(String name, UncaughtThrowableStrategy uncaughtThrowableStrategy, boolean preventNetworkOperations) {
            this.name = name;
            this.uncaughtThrowableStrategy = uncaughtThrowableStrategy;
            this.preventNetworkOperations = preventNetworkOperations;
        }

        public synchronized Thread newThread(@NonNull Runnable runnable) {
            Thread result;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("glide-");
            stringBuilder.append(this.name);
            stringBuilder.append("-thread-");
            stringBuilder.append(this.threadNum);
            result = new Thread(runnable, stringBuilder.toString()) {
                public void run() {
                    Process.setThreadPriority(9);
                    if (DefaultThreadFactory.this.preventNetworkOperations) {
                        StrictMode.setThreadPolicy(new Builder().detectNetwork().penaltyDeath().build());
                    }
                    try {
                        super.run();
                    } catch (Throwable t) {
                        DefaultThreadFactory.this.uncaughtThrowableStrategy.handle(t);
                    }
                }
            };
            this.threadNum++;
            return result;
        }
    }

    public interface UncaughtThrowableStrategy {
        public static final UncaughtThrowableStrategy DEFAULT = LOG;
        public static final UncaughtThrowableStrategy IGNORE = new C09561();
        public static final UncaughtThrowableStrategy LOG = new C09572();
        public static final UncaughtThrowableStrategy THROW = new C09583();

        /* renamed from: com.bumptech.glide.load.engine.executor.GlideExecutor$UncaughtThrowableStrategy$1 */
        class C09561 implements UncaughtThrowableStrategy {
            C09561() {
            }

            public void handle(Throwable t) {
            }
        }

        /* renamed from: com.bumptech.glide.load.engine.executor.GlideExecutor$UncaughtThrowableStrategy$2 */
        class C09572 implements UncaughtThrowableStrategy {
            C09572() {
            }

            public void handle(Throwable t) {
                if (t != null && Log.isLoggable(GlideExecutor.TAG, 6)) {
                    Log.e(GlideExecutor.TAG, "Request threw uncaught throwable", t);
                }
            }
        }

        /* renamed from: com.bumptech.glide.load.engine.executor.GlideExecutor$UncaughtThrowableStrategy$3 */
        class C09583 implements UncaughtThrowableStrategy {
            C09583() {
            }

            public void handle(Throwable t) {
                if (t != null) {
                    throw new RuntimeException("Request threw uncaught throwable", t);
                }
            }
        }

        void handle(Throwable th);
    }

    public static GlideExecutor newDiskCacheExecutor() {
        return newDiskCacheExecutor(1, DEFAULT_DISK_CACHE_EXECUTOR_NAME, UncaughtThrowableStrategy.DEFAULT);
    }

    public static GlideExecutor newDiskCacheExecutor(UncaughtThrowableStrategy uncaughtThrowableStrategy) {
        return newDiskCacheExecutor(1, DEFAULT_DISK_CACHE_EXECUTOR_NAME, uncaughtThrowableStrategy);
    }

    public static GlideExecutor newDiskCacheExecutor(int threadCount, String name, UncaughtThrowableStrategy uncaughtThrowableStrategy) {
        return new GlideExecutor(new ThreadPoolExecutor(threadCount, threadCount, 0, TimeUnit.MILLISECONDS, new PriorityBlockingQueue(), new DefaultThreadFactory(name, uncaughtThrowableStrategy, true)));
    }

    public static GlideExecutor newSourceExecutor() {
        return newSourceExecutor(calculateBestThreadCount(), DEFAULT_SOURCE_EXECUTOR_NAME, UncaughtThrowableStrategy.DEFAULT);
    }

    public static GlideExecutor newSourceExecutor(UncaughtThrowableStrategy uncaughtThrowableStrategy) {
        return newSourceExecutor(calculateBestThreadCount(), DEFAULT_SOURCE_EXECUTOR_NAME, uncaughtThrowableStrategy);
    }

    public static GlideExecutor newSourceExecutor(int threadCount, String name, UncaughtThrowableStrategy uncaughtThrowableStrategy) {
        return new GlideExecutor(new ThreadPoolExecutor(threadCount, threadCount, 0, TimeUnit.MILLISECONDS, new PriorityBlockingQueue(), new DefaultThreadFactory(name, uncaughtThrowableStrategy, false)));
    }

    public static GlideExecutor newUnlimitedSourceExecutor() {
        return new GlideExecutor(new ThreadPoolExecutor(0, Integer.MAX_VALUE, KEEP_ALIVE_TIME_MS, TimeUnit.MILLISECONDS, new SynchronousQueue(), new DefaultThreadFactory(SOURCE_UNLIMITED_EXECUTOR_NAME, UncaughtThrowableStrategy.DEFAULT, false)));
    }

    public static GlideExecutor newAnimationExecutor() {
        return newAnimationExecutor(calculateBestThreadCount() >= 4 ? 2 : 1, UncaughtThrowableStrategy.DEFAULT);
    }

    public static GlideExecutor newAnimationExecutor(int threadCount, UncaughtThrowableStrategy uncaughtThrowableStrategy) {
        return new GlideExecutor(new ThreadPoolExecutor(0, threadCount, KEEP_ALIVE_TIME_MS, TimeUnit.MILLISECONDS, new PriorityBlockingQueue(), new DefaultThreadFactory(ANIMATION_EXECUTOR_NAME, uncaughtThrowableStrategy, true)));
    }

    @VisibleForTesting
    GlideExecutor(ExecutorService delegate) {
        this.delegate = delegate;
    }

    public void execute(@NonNull Runnable command) {
        this.delegate.execute(command);
    }

    @NonNull
    public Future<?> submit(@NonNull Runnable task) {
        return this.delegate.submit(task);
    }

    @NonNull
    public <T> List<Future<T>> invokeAll(@NonNull Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return this.delegate.invokeAll(tasks);
    }

    @NonNull
    public <T> List<Future<T>> invokeAll(@NonNull Collection<? extends Callable<T>> tasks, long timeout, @NonNull TimeUnit unit) throws InterruptedException {
        return this.delegate.invokeAll(tasks, timeout, unit);
    }

    @NonNull
    public <T> T invokeAny(@NonNull Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return this.delegate.invokeAny(tasks);
    }

    public <T> T invokeAny(@NonNull Collection<? extends Callable<T>> tasks, long timeout, @NonNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.delegate.invokeAny(tasks, timeout, unit);
    }

    @NonNull
    public <T> Future<T> submit(@NonNull Runnable task, T result) {
        return this.delegate.submit(task, result);
    }

    public <T> Future<T> submit(@NonNull Callable<T> task) {
        return this.delegate.submit(task);
    }

    public void shutdown() {
        this.delegate.shutdown();
    }

    @NonNull
    public List<Runnable> shutdownNow() {
        return this.delegate.shutdownNow();
    }

    public boolean isShutdown() {
        return this.delegate.isShutdown();
    }

    public boolean isTerminated() {
        return this.delegate.isTerminated();
    }

    public boolean awaitTermination(long timeout, @NonNull TimeUnit unit) throws InterruptedException {
        return this.delegate.awaitTermination(timeout, unit);
    }

    public String toString() {
        return this.delegate.toString();
    }

    public static int calculateBestThreadCount() {
        if (bestThreadCount == 0) {
            bestThreadCount = Math.min(4, RuntimeCompat.availableProcessors());
        }
        return bestThreadCount;
    }
}
