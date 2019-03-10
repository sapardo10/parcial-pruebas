package io.reactivex.internal.schedulers;

import io.reactivex.disposables.Disposable;
import io.reactivex.internal.functions.Functions;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicReference;

final class InstantPeriodicTask implements Callable<Void>, Disposable {
    static final FutureTask<Void> CANCELLED = new FutureTask(Functions.EMPTY_RUNNABLE, null);
    final ExecutorService executor;
    final AtomicReference<Future<?>> first = new AtomicReference();
    final AtomicReference<Future<?>> rest = new AtomicReference();
    Thread runner;
    final Runnable task;

    InstantPeriodicTask(Runnable task, ExecutorService executor) {
        this.task = task;
        this.executor = executor;
    }

    public Void call() throws Exception {
        this.runner = Thread.currentThread();
        try {
            this.task.run();
            setRest(this.executor.submit(this));
            this.runner = null;
        } catch (Throwable ex) {
            this.runner = null;
            RxJavaPlugins.onError(ex);
        }
        return null;
    }

    public void dispose() {
        Future<?> current = (Future) this.first.getAndSet(CANCELLED);
        boolean z = true;
        if (current != null && current != CANCELLED) {
            current.cancel(this.runner != Thread.currentThread());
        }
        current = (Future) this.rest.getAndSet(CANCELLED);
        if (current != null && current != CANCELLED) {
            if (this.runner == Thread.currentThread()) {
                z = false;
            }
            current.cancel(z);
        }
    }

    public boolean isDisposed() {
        return this.first.get() == CANCELLED;
    }

    void setFirst(Future<?> f) {
        while (true) {
            Future<?> current = (Future) this.first.get();
            if (current == CANCELLED) {
                break;
            } else if (this.first.compareAndSet(current, f)) {
                return;
            }
        }
        f.cancel(this.runner != Thread.currentThread());
    }

    void setRest(Future<?> f) {
        while (true) {
            Future<?> current = (Future) this.rest.get();
            if (current == CANCELLED) {
                break;
            } else if (this.rest.compareAndSet(current, f)) {
                return;
            }
        }
        f.cancel(this.runner != Thread.currentThread());
    }
}
