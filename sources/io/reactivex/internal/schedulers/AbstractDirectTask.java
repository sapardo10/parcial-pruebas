package io.reactivex.internal.schedulers;

import io.reactivex.disposables.Disposable;
import io.reactivex.internal.functions.Functions;
import io.reactivex.schedulers.SchedulerRunnableIntrospection;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicReference;

abstract class AbstractDirectTask extends AtomicReference<Future<?>> implements Disposable, SchedulerRunnableIntrospection {
    protected static final FutureTask<Void> DISPOSED = new FutureTask(Functions.EMPTY_RUNNABLE, null);
    protected static final FutureTask<Void> FINISHED = new FutureTask(Functions.EMPTY_RUNNABLE, null);
    private static final long serialVersionUID = 1811839108042568751L;
    protected final Runnable runnable;
    protected Thread runner;

    AbstractDirectTask(Runnable runnable) {
        this.runnable = runnable;
    }

    public final void dispose() {
        Future<?> f = (Future) get();
        if (f != FINISHED) {
            Future<?> future = DISPOSED;
            if (f != future) {
                if (!compareAndSet(f, future)) {
                    return;
                }
                if (f != null) {
                    f.cancel(this.runner != Thread.currentThread());
                }
            }
        }
    }

    public final boolean isDisposed() {
        Future<?> f = (Future) get();
        if (f != FINISHED) {
            if (f != DISPOSED) {
                return false;
            }
        }
        return true;
    }

    public final void setFuture(Future<?> future) {
        while (true) {
            Future<?> f = (Future) get();
            if (f != FINISHED) {
                if (f == DISPOSED) {
                    break;
                } else if (compareAndSet(f, future)) {
                    return;
                }
            } else {
                return;
            }
        }
        future.cancel(this.runner != Thread.currentThread());
    }

    public Runnable getWrappedRunnable() {
        return this.runnable;
    }
}
