package io.reactivex;

import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Function;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.internal.disposables.SequentialDisposable;
import io.reactivex.internal.schedulers.NewThreadWorker;
import io.reactivex.internal.schedulers.SchedulerWhen;
import io.reactivex.internal.util.ExceptionHelper;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.SchedulerRunnableIntrospection;
import java.util.concurrent.TimeUnit;

public abstract class Scheduler {
    static final long CLOCK_DRIFT_TOLERANCE_NANOSECONDS = TimeUnit.MINUTES.toNanos(Long.getLong("rx2.scheduler.drift-tolerance", 15).longValue());

    static final class DisposeTask implements Disposable, Runnable, SchedulerRunnableIntrospection {
        @NonNull
        final Runnable decoratedRun;
        @Nullable
        Thread runner;
        @NonNull
        /* renamed from: w */
        final Worker f35w;

        DisposeTask(@NonNull Runnable decoratedRun, @NonNull Worker w) {
            this.decoratedRun = decoratedRun;
            this.f35w = w;
        }

        public void run() {
            this.runner = Thread.currentThread();
            try {
                this.decoratedRun.run();
            } finally {
                dispose();
                this.runner = null;
            }
        }

        public void dispose() {
            if (this.runner == Thread.currentThread()) {
                Worker worker = this.f35w;
                if (worker instanceof NewThreadWorker) {
                    ((NewThreadWorker) worker).shutdown();
                    return;
                }
            }
            this.f35w.dispose();
        }

        public boolean isDisposed() {
            return this.f35w.isDisposed();
        }

        public Runnable getWrappedRunnable() {
            return this.decoratedRun;
        }
    }

    static final class PeriodicDirectTask implements Disposable, Runnable, SchedulerRunnableIntrospection {
        volatile boolean disposed;
        @NonNull
        final Runnable run;
        @NonNull
        final Worker worker;

        PeriodicDirectTask(@NonNull Runnable run, @NonNull Worker worker) {
            this.run = run;
            this.worker = worker;
        }

        public void run() {
            if (!this.disposed) {
                try {
                    this.run.run();
                } catch (Throwable ex) {
                    Exceptions.throwIfFatal(ex);
                    this.worker.dispose();
                    RuntimeException wrapOrThrow = ExceptionHelper.wrapOrThrow(ex);
                }
            }
        }

        public void dispose() {
            this.disposed = true;
            this.worker.dispose();
        }

        public boolean isDisposed() {
            return this.disposed;
        }

        public Runnable getWrappedRunnable() {
            return this.run;
        }
    }

    public static abstract class Worker implements Disposable {

        final class PeriodicTask implements Runnable, SchedulerRunnableIntrospection {
            long count;
            @NonNull
            final Runnable decoratedRun;
            long lastNowNanoseconds;
            final long periodInNanoseconds;
            @NonNull
            final SequentialDisposable sd;
            long startInNanoseconds;

            PeriodicTask(long firstStartInNanoseconds, @NonNull Runnable decoratedRun, long firstNowNanoseconds, @NonNull SequentialDisposable sd, long periodInNanoseconds) {
                this.decoratedRun = decoratedRun;
                this.sd = sd;
                this.periodInNanoseconds = periodInNanoseconds;
                this.lastNowNanoseconds = firstNowNanoseconds;
                this.startInNanoseconds = firstStartInNanoseconds;
            }

            public void run() {
                this.decoratedRun.run();
                if (!this.sd.isDisposed()) {
                    long nowNanoseconds = Worker.this.now(TimeUnit.NANOSECONDS);
                    long j = Scheduler.CLOCK_DRIFT_TOLERANCE_NANOSECONDS + nowNanoseconds;
                    long j2 = this.lastNowNanoseconds;
                    if (j >= j2) {
                        if (nowNanoseconds < (j2 + this.periodInNanoseconds) + Scheduler.CLOCK_DRIFT_TOLERANCE_NANOSECONDS) {
                            j = this.startInNanoseconds;
                            j2 = this.count + 1;
                            this.count = j2;
                            j += j2 * this.periodInNanoseconds;
                            this.lastNowNanoseconds = nowNanoseconds;
                            this.sd.replace(Worker.this.schedule(this, j - nowNanoseconds, TimeUnit.NANOSECONDS));
                        }
                    }
                    j = this.periodInNanoseconds;
                    j2 = nowNanoseconds + j;
                    long j3 = this.count + 1;
                    this.count = j3;
                    this.startInNanoseconds = j2 - (j * j3);
                    j = j2;
                    this.lastNowNanoseconds = nowNanoseconds;
                    this.sd.replace(Worker.this.schedule(this, j - nowNanoseconds, TimeUnit.NANOSECONDS));
                }
            }

            public Runnable getWrappedRunnable() {
                return this.decoratedRun;
            }
        }

        @NonNull
        public abstract Disposable schedule(@NonNull Runnable runnable, long j, @NonNull TimeUnit timeUnit);

        @NonNull
        public Disposable schedule(@NonNull Runnable run) {
            return schedule(run, 0, TimeUnit.NANOSECONDS);
        }

        @NonNull
        public Disposable schedulePeriodically(@NonNull Runnable run, long initialDelay, long period, @NonNull TimeUnit unit) {
            long j = initialDelay;
            TimeUnit timeUnit = unit;
            SequentialDisposable first = new SequentialDisposable();
            SequentialDisposable sd = new SequentialDisposable(first);
            Runnable decoratedRun = RxJavaPlugins.onSchedule(run);
            long periodInNanoseconds = timeUnit.toNanos(period);
            long firstNowNanoseconds = now(TimeUnit.NANOSECONDS);
            SequentialDisposable first2 = first;
            PeriodicTask periodicTask = r0;
            PeriodicTask periodicTask2 = new PeriodicTask(firstNowNanoseconds + timeUnit.toNanos(j), decoratedRun, firstNowNanoseconds, sd, periodInNanoseconds);
            Disposable d = schedule(periodicTask, j, timeUnit);
            if (d == EmptyDisposable.INSTANCE) {
                return d;
            }
            first2.replace(d);
            return sd;
        }

        public long now(@NonNull TimeUnit unit) {
            return unit.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }
    }

    @NonNull
    public abstract Worker createWorker();

    public static long clockDriftTolerance() {
        return CLOCK_DRIFT_TOLERANCE_NANOSECONDS;
    }

    public long now(@NonNull TimeUnit unit) {
        return unit.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    public void start() {
    }

    public void shutdown() {
    }

    @NonNull
    public Disposable scheduleDirect(@NonNull Runnable run) {
        return scheduleDirect(run, 0, TimeUnit.NANOSECONDS);
    }

    @NonNull
    public Disposable scheduleDirect(@NonNull Runnable run, long delay, @NonNull TimeUnit unit) {
        Worker w = createWorker();
        DisposeTask task = new DisposeTask(RxJavaPlugins.onSchedule(run), w);
        w.schedule(task, delay, unit);
        return task;
    }

    @NonNull
    public Disposable schedulePeriodicallyDirect(@NonNull Runnable run, long initialDelay, long period, @NonNull TimeUnit unit) {
        Worker w = createWorker();
        PeriodicDirectTask periodicTask = new PeriodicDirectTask(RxJavaPlugins.onSchedule(run), w);
        Disposable d = w.schedulePeriodically(periodicTask, initialDelay, period, unit);
        if (d == EmptyDisposable.INSTANCE) {
            return d;
        }
        return periodicTask;
    }

    @NonNull
    public <S extends Scheduler & Disposable> S when(@NonNull Function<Flowable<Flowable<Completable>>, Completable> combine) {
        return new SchedulerWhen(combine, this);
    }
}
