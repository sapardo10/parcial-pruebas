package io.reactivex.subjects;

import io.reactivex.Observer;
import io.reactivex.annotations.CheckReturnValue;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.fuseable.SimpleQueue;
import io.reactivex.internal.observers.BasicIntQueueDisposable;
import io.reactivex.internal.queue.SpscLinkedArrayQueue;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public final class UnicastSubject<T> extends Subject<T> {
    final boolean delayError;
    volatile boolean disposed;
    volatile boolean done;
    final AtomicReference<Observer<? super T>> downstream;
    boolean enableOperatorFusion;
    Throwable error;
    final AtomicReference<Runnable> onTerminate;
    final AtomicBoolean once;
    final SpscLinkedArrayQueue<T> queue;
    final BasicIntQueueDisposable<T> wip;

    final class UnicastQueueDisposable extends BasicIntQueueDisposable<T> {
        private static final long serialVersionUID = 7926949470189395511L;

        UnicastQueueDisposable() {
        }

        public int requestFusion(int mode) {
            if ((mode & 2) == 0) {
                return 0;
            }
            UnicastSubject.this.enableOperatorFusion = true;
            return 2;
        }

        @Nullable
        public T poll() throws Exception {
            return UnicastSubject.this.queue.poll();
        }

        public boolean isEmpty() {
            return UnicastSubject.this.queue.isEmpty();
        }

        public void clear() {
            UnicastSubject.this.queue.clear();
        }

        public void dispose() {
            if (!UnicastSubject.this.disposed) {
                UnicastSubject unicastSubject = UnicastSubject.this;
                unicastSubject.disposed = true;
                unicastSubject.doTerminate();
                UnicastSubject.this.downstream.lazySet(null);
                if (UnicastSubject.this.wip.getAndIncrement() == 0) {
                    UnicastSubject.this.downstream.lazySet(null);
                    UnicastSubject.this.queue.clear();
                }
            }
        }

        public boolean isDisposed() {
            return UnicastSubject.this.disposed;
        }
    }

    @CheckReturnValue
    @NonNull
    public static <T> UnicastSubject<T> create() {
        return new UnicastSubject(bufferSize(), true);
    }

    @CheckReturnValue
    @NonNull
    public static <T> UnicastSubject<T> create(int capacityHint) {
        return new UnicastSubject(capacityHint, true);
    }

    @CheckReturnValue
    @NonNull
    public static <T> UnicastSubject<T> create(int capacityHint, Runnable onTerminate) {
        return new UnicastSubject(capacityHint, onTerminate, true);
    }

    @CheckReturnValue
    @NonNull
    public static <T> UnicastSubject<T> create(int capacityHint, Runnable onTerminate, boolean delayError) {
        return new UnicastSubject(capacityHint, onTerminate, delayError);
    }

    @CheckReturnValue
    @NonNull
    public static <T> UnicastSubject<T> create(boolean delayError) {
        return new UnicastSubject(bufferSize(), delayError);
    }

    UnicastSubject(int capacityHint, boolean delayError) {
        this.queue = new SpscLinkedArrayQueue(ObjectHelper.verifyPositive(capacityHint, "capacityHint"));
        this.onTerminate = new AtomicReference();
        this.delayError = delayError;
        this.downstream = new AtomicReference();
        this.once = new AtomicBoolean();
        this.wip = new UnicastQueueDisposable();
    }

    UnicastSubject(int capacityHint, Runnable onTerminate) {
        this(capacityHint, onTerminate, true);
    }

    UnicastSubject(int capacityHint, Runnable onTerminate, boolean delayError) {
        this.queue = new SpscLinkedArrayQueue(ObjectHelper.verifyPositive(capacityHint, "capacityHint"));
        this.onTerminate = new AtomicReference(ObjectHelper.requireNonNull((Object) onTerminate, "onTerminate"));
        this.delayError = delayError;
        this.downstream = new AtomicReference();
        this.once = new AtomicBoolean();
        this.wip = new UnicastQueueDisposable();
    }

    protected void subscribeActual(Observer<? super T> observer) {
        if (this.once.get() || !this.once.compareAndSet(false, true)) {
            EmptyDisposable.error(new IllegalStateException("Only a single observer allowed."), (Observer) observer);
        } else {
            observer.onSubscribe(this.wip);
            this.downstream.lazySet(observer);
            if (this.disposed) {
                this.downstream.lazySet(null);
                return;
            }
            drain();
        }
    }

    void doTerminate() {
        Runnable r = (Runnable) this.onTerminate.get();
        if (r != null && this.onTerminate.compareAndSet(r, null)) {
            r.run();
        }
    }

    public void onSubscribe(Disposable d) {
        if (!this.done) {
            if (!this.disposed) {
                return;
            }
        }
        d.dispose();
    }

    public void onNext(T t) {
        ObjectHelper.requireNonNull((Object) t, "onNext called with null. Null values are generally not allowed in 2.x operators and sources.");
        if (!this.done) {
            if (!this.disposed) {
                this.queue.offer(t);
                drain();
            }
        }
    }

    public void onError(Throwable t) {
        ObjectHelper.requireNonNull((Object) t, "onError called with null. Null values are generally not allowed in 2.x operators and sources.");
        if (!this.done) {
            if (!this.disposed) {
                this.error = t;
                this.done = true;
                doTerminate();
                drain();
                return;
            }
        }
        RxJavaPlugins.onError(t);
    }

    public void onComplete() {
        if (!this.done) {
            if (!this.disposed) {
                this.done = true;
                doTerminate();
                drain();
            }
        }
    }

    void drainNormal(Observer<? super T> a) {
        int missed = 1;
        SimpleQueue<T> q = this.queue;
        boolean failFast = this.delayError ^ true;
        boolean canBeError = true;
        while (!this.disposed) {
            boolean d = this.done;
            T v = this.queue.poll();
            boolean empty = v == null;
            if (d) {
                if (failFast && canBeError) {
                    if (!failedFast(q, a)) {
                        canBeError = false;
                    } else {
                        return;
                    }
                }
                if (empty) {
                    errorOrComplete(a);
                    return;
                }
            }
            if (empty) {
                missed = this.wip.addAndGet(-missed);
                if (missed == 0) {
                    return;
                }
            } else {
                a.onNext(v);
            }
        }
        this.downstream.lazySet(null);
        q.clear();
    }

    void drainFused(Observer<? super T> a) {
        int missed = 1;
        SpscLinkedArrayQueue<T> q = this.queue;
        boolean failFast = this.delayError ^ 1;
        while (!this.disposed) {
            boolean d = this.done;
            if (failFast && d) {
                if (failedFast(q, a)) {
                    return;
                }
            }
            a.onNext(null);
            if (d) {
                errorOrComplete(a);
                return;
            }
            missed = this.wip.addAndGet(-missed);
            if (missed == 0) {
                return;
            }
        }
        this.downstream.lazySet(null);
        q.clear();
    }

    void errorOrComplete(Observer<? super T> a) {
        this.downstream.lazySet(null);
        Throwable ex = this.error;
        if (ex != null) {
            a.onError(ex);
        } else {
            a.onComplete();
        }
    }

    boolean failedFast(SimpleQueue<T> q, Observer<? super T> a) {
        Throwable ex = this.error;
        if (ex == null) {
            return false;
        }
        this.downstream.lazySet(null);
        q.clear();
        a.onError(ex);
        return true;
    }

    void drain() {
        if (this.wip.getAndIncrement() == 0) {
            Observer<? super T> a = (Observer) this.downstream.get();
            int missed = 1;
            while (a == null) {
                missed = this.wip.addAndGet(-missed);
                if (missed != 0) {
                    a = (Observer) this.downstream.get();
                } else {
                    return;
                }
            }
            if (this.enableOperatorFusion) {
                drainFused(a);
            } else {
                drainNormal(a);
            }
        }
    }

    public boolean hasObservers() {
        return this.downstream.get() != null;
    }

    @Nullable
    public Throwable getThrowable() {
        if (this.done) {
            return this.error;
        }
        return null;
    }

    public boolean hasThrowable() {
        return this.done && this.error != null;
    }

    public boolean hasComplete() {
        return this.done && this.error == null;
    }
}
