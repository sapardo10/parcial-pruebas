package io.reactivex.internal.schedulers;

import io.reactivex.Scheduler;
import io.reactivex.Scheduler.Worker;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.internal.disposables.ListCompositeDisposable;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.schedulers.SchedulerMultiWorkerSupport.WorkerCallback;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public final class ComputationScheduler extends Scheduler implements SchedulerMultiWorkerSupport {
    private static final String KEY_COMPUTATION_PRIORITY = "rx2.computation-priority";
    static final String KEY_MAX_THREADS = "rx2.computation-threads";
    static final int MAX_THREADS = cap(Runtime.getRuntime().availableProcessors(), Integer.getInteger(KEY_MAX_THREADS, 0).intValue());
    static final FixedSchedulerPool NONE = new FixedSchedulerPool(0, THREAD_FACTORY);
    static final PoolWorker SHUTDOWN_WORKER = new PoolWorker(new RxThreadFactory("RxComputationShutdown"));
    static final RxThreadFactory THREAD_FACTORY = new RxThreadFactory(THREAD_NAME_PREFIX, Math.max(1, Math.min(10, Integer.getInteger(KEY_COMPUTATION_PRIORITY, 5).intValue())), true);
    private static final String THREAD_NAME_PREFIX = "RxComputationThreadPool";
    final AtomicReference<FixedSchedulerPool> pool;
    final ThreadFactory threadFactory;

    static final class FixedSchedulerPool implements SchedulerMultiWorkerSupport {
        final int cores;
        final PoolWorker[] eventLoops;
        /* renamed from: n */
        long f52n;

        FixedSchedulerPool(int maxThreads, ThreadFactory threadFactory) {
            this.cores = maxThreads;
            this.eventLoops = new PoolWorker[maxThreads];
            for (int i = 0; i < maxThreads; i++) {
                this.eventLoops[i] = new PoolWorker(threadFactory);
            }
        }

        public PoolWorker getEventLoop() {
            int c = this.cores;
            if (c == 0) {
                return ComputationScheduler.SHUTDOWN_WORKER;
            }
            PoolWorker[] poolWorkerArr = this.eventLoops;
            long j = this.f52n;
            this.f52n = 1 + j;
            return poolWorkerArr[(int) (j % ((long) c))];
        }

        public void shutdown() {
            for (PoolWorker w : this.eventLoops) {
                w.dispose();
            }
        }

        public void createWorkers(int number, WorkerCallback callback) {
            int c = this.cores;
            int i;
            if (c == 0) {
                for (i = 0; i < number; i++) {
                    callback.onWorker(i, ComputationScheduler.SHUTDOWN_WORKER);
                }
                return;
            }
            i = ((int) this.f52n) % c;
            for (int i2 = 0; i2 < number; i2++) {
                callback.onWorker(i2, new EventLoopWorker(this.eventLoops[i]));
                i++;
                if (i == c) {
                    i = 0;
                }
            }
            this.f52n = (long) i;
        }
    }

    static final class EventLoopWorker extends Worker {
        private final ListCompositeDisposable both = new ListCompositeDisposable();
        volatile boolean disposed;
        private final PoolWorker poolWorker;
        private final ListCompositeDisposable serial = new ListCompositeDisposable();
        private final CompositeDisposable timed = new CompositeDisposable();

        EventLoopWorker(PoolWorker poolWorker) {
            this.poolWorker = poolWorker;
            this.both.add(this.serial);
            this.both.add(this.timed);
        }

        public void dispose() {
            if (!this.disposed) {
                this.disposed = true;
                this.both.dispose();
            }
        }

        public boolean isDisposed() {
            return this.disposed;
        }

        @NonNull
        public Disposable schedule(@NonNull Runnable action) {
            if (this.disposed) {
                return EmptyDisposable.INSTANCE;
            }
            return this.poolWorker.scheduleActual(action, 0, TimeUnit.MILLISECONDS, this.serial);
        }

        @NonNull
        public Disposable schedule(@NonNull Runnable action, long delayTime, @NonNull TimeUnit unit) {
            if (this.disposed) {
                return EmptyDisposable.INSTANCE;
            }
            return this.poolWorker.scheduleActual(action, delayTime, unit, this.timed);
        }
    }

    static final class PoolWorker extends NewThreadWorker {
        PoolWorker(ThreadFactory threadFactory) {
            super(threadFactory);
        }
    }

    static {
        SHUTDOWN_WORKER.dispose();
        NONE.shutdown();
    }

    static int cap(int cpuCount, int paramThreads) {
        if (paramThreads > 0) {
            if (paramThreads <= cpuCount) {
                return paramThreads;
            }
        }
        return cpuCount;
    }

    public ComputationScheduler() {
        this(THREAD_FACTORY);
    }

    public ComputationScheduler(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
        this.pool = new AtomicReference(NONE);
        start();
    }

    @NonNull
    public Worker createWorker() {
        return new EventLoopWorker(((FixedSchedulerPool) this.pool.get()).getEventLoop());
    }

    public void createWorkers(int number, WorkerCallback callback) {
        ObjectHelper.verifyPositive(number, "number > 0 required");
        ((FixedSchedulerPool) this.pool.get()).createWorkers(number, callback);
    }

    @NonNull
    public Disposable scheduleDirect(@NonNull Runnable run, long delay, TimeUnit unit) {
        return ((FixedSchedulerPool) this.pool.get()).getEventLoop().scheduleDirect(run, delay, unit);
    }

    @NonNull
    public Disposable schedulePeriodicallyDirect(@NonNull Runnable run, long initialDelay, long period, TimeUnit unit) {
        return ((FixedSchedulerPool) this.pool.get()).getEventLoop().schedulePeriodicallyDirect(run, initialDelay, period, unit);
    }

    public void start() {
        FixedSchedulerPool update = new FixedSchedulerPool(MAX_THREADS, this.threadFactory);
        if (!this.pool.compareAndSet(NONE, update)) {
            update.shutdown();
        }
    }

    public void shutdown() {
        while (true) {
            FixedSchedulerPool curr = (FixedSchedulerPool) this.pool.get();
            FixedSchedulerPool fixedSchedulerPool = NONE;
            if (curr != fixedSchedulerPool) {
                if (this.pool.compareAndSet(curr, fixedSchedulerPool)) {
                    curr.shutdown();
                    return;
                }
            } else {
                return;
            }
        }
    }
}
