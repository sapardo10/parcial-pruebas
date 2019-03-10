package io.reactivex.internal.operators.observable;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Function;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.fuseable.QueueDisposable;
import io.reactivex.internal.fuseable.SimplePlainQueue;
import io.reactivex.internal.fuseable.SimpleQueue;
import io.reactivex.internal.queue.SpscArrayQueue;
import io.reactivex.internal.queue.SpscLinkedArrayQueue;
import io.reactivex.internal.util.AtomicThrowable;
import io.reactivex.internal.util.ExceptionHelper;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public final class ObservableFlatMap<T, U> extends AbstractObservableWithUpstream<T, U> {
    final int bufferSize;
    final boolean delayErrors;
    final Function<? super T, ? extends ObservableSource<? extends U>> mapper;
    final int maxConcurrency;

    static final class InnerObserver<T, U> extends AtomicReference<Disposable> implements Observer<U> {
        private static final long serialVersionUID = -4606175640614850599L;
        volatile boolean done;
        int fusionMode;
        final long id;
        final MergeObserver<T, U> parent;
        volatile SimpleQueue<U> queue;

        InnerObserver(MergeObserver<T, U> parent, long id) {
            this.id = id;
            this.parent = parent;
        }

        public void onSubscribe(Disposable d) {
            if (DisposableHelper.setOnce(this, d)) {
                if (d instanceof QueueDisposable) {
                    QueueDisposable<U> qd = (QueueDisposable) d;
                    int m = qd.requestFusion(7);
                    if (m == 1) {
                        this.fusionMode = m;
                        this.queue = qd;
                        this.done = true;
                        this.parent.drain();
                    } else if (m == 2) {
                        this.fusionMode = m;
                        this.queue = qd;
                    }
                }
            }
        }

        public void onNext(U t) {
            if (this.fusionMode == 0) {
                this.parent.tryEmit(t, this);
            } else {
                this.parent.drain();
            }
        }

        public void onError(Throwable t) {
            if (this.parent.errors.addThrowable(t)) {
                if (!this.parent.delayErrors) {
                    this.parent.disposeAll();
                }
                this.done = true;
                this.parent.drain();
                return;
            }
            RxJavaPlugins.onError(t);
        }

        public void onComplete() {
            this.done = true;
            this.parent.drain();
        }

        public void dispose() {
            DisposableHelper.dispose(this);
        }
    }

    static final class MergeObserver<T, U> extends AtomicInteger implements Disposable, Observer<T> {
        static final InnerObserver<?, ?>[] CANCELLED = new InnerObserver[0];
        static final InnerObserver<?, ?>[] EMPTY = new InnerObserver[0];
        private static final long serialVersionUID = -2117620485640801370L;
        final int bufferSize;
        volatile boolean cancelled;
        final boolean delayErrors;
        volatile boolean done;
        final Observer<? super U> downstream;
        final AtomicThrowable errors = new AtomicThrowable();
        long lastId;
        int lastIndex;
        final Function<? super T, ? extends ObservableSource<? extends U>> mapper;
        final int maxConcurrency;
        final AtomicReference<InnerObserver<?, ?>[]> observers;
        volatile SimplePlainQueue<U> queue;
        Queue<ObservableSource<? extends U>> sources;
        long uniqueId;
        Disposable upstream;
        int wip;

        MergeObserver(Observer<? super U> actual, Function<? super T, ? extends ObservableSource<? extends U>> mapper, boolean delayErrors, int maxConcurrency, int bufferSize) {
            this.downstream = actual;
            this.mapper = mapper;
            this.delayErrors = delayErrors;
            this.maxConcurrency = maxConcurrency;
            this.bufferSize = bufferSize;
            if (maxConcurrency != Integer.MAX_VALUE) {
                this.sources = new ArrayDeque(maxConcurrency);
            }
            this.observers = new AtomicReference(EMPTY);
        }

        public void onSubscribe(Disposable d) {
            if (DisposableHelper.validate(this.upstream, d)) {
                this.upstream = d;
                this.downstream.onSubscribe(this);
            }
        }

        public void onNext(T t) {
            if (!this.done) {
                try {
                    ObservableSource<? extends U> p = (ObservableSource) ObjectHelper.requireNonNull(this.mapper.apply(t), "The mapper returned a null ObservableSource");
                    if (this.maxConcurrency != Integer.MAX_VALUE) {
                        synchronized (this) {
                            if (this.wip == this.maxConcurrency) {
                                this.sources.offer(p);
                                return;
                            }
                            this.wip++;
                        }
                    }
                    subscribeInner(p);
                } catch (Throwable e) {
                    Exceptions.throwIfFatal(e);
                    this.upstream.dispose();
                    onError(e);
                }
            }
        }

        void subscribeInner(ObservableSource<? extends U> p) {
            while (p instanceof Callable) {
                if (tryEmitScalar((Callable) p) && this.maxConcurrency != Integer.MAX_VALUE) {
                    boolean empty = false;
                    synchronized (this) {
                        p = (ObservableSource) this.sources.poll();
                        if (p == null) {
                            this.wip--;
                            empty = true;
                        }
                    }
                    if (empty) {
                        drain();
                        return;
                    }
                } else {
                    return;
                }
            }
            long j = this.uniqueId;
            this.uniqueId = 1 + j;
            InnerObserver<T, U> inner = new InnerObserver(this, j);
            if (addInner(inner)) {
                p.subscribe(inner);
            }
        }

        boolean addInner(InnerObserver<T, U> inner) {
            while (true) {
                InnerObserver[] a = (InnerObserver[]) this.observers.get();
                if (a == CANCELLED) {
                    inner.dispose();
                    return false;
                }
                int n = a.length;
                InnerObserver<?, ?>[] b = new InnerObserver[(n + 1)];
                System.arraycopy(a, 0, b, 0, n);
                b[n] = inner;
                if (this.observers.compareAndSet(a, b)) {
                    return true;
                }
            }
        }

        void removeInner(InnerObserver<T, U> inner) {
            while (true) {
                InnerObserver[] a = (InnerObserver[]) this.observers.get();
                int n = a.length;
                if (n != 0) {
                    int j = -1;
                    for (int i = 0; i < n; i++) {
                        if (a[i] == inner) {
                            j = i;
                            break;
                        }
                    }
                    if (j >= 0) {
                        InnerObserver<?, ?>[] b;
                        if (n == 1) {
                            b = EMPTY;
                        } else {
                            InnerObserver<?, ?>[] b2 = new InnerObserver[(n - 1)];
                            System.arraycopy(a, 0, b2, 0, j);
                            System.arraycopy(a, j + 1, b2, j, (n - j) - 1);
                            b = b2;
                        }
                        if (this.observers.compareAndSet(a, b)) {
                            return;
                        }
                    } else {
                        return;
                    }
                }
                return;
            }
        }

        boolean tryEmitScalar(Callable<? extends U> value) {
            try {
                U u = value.call();
                if (u == null) {
                    return true;
                }
                if (get() == 0 && compareAndSet(0, 1)) {
                    this.downstream.onNext(u);
                    if (decrementAndGet() == 0) {
                        return true;
                    }
                } else {
                    SimplePlainQueue<U> q = this.queue;
                    if (q == null) {
                        int i = this.maxConcurrency;
                        if (i == Integer.MAX_VALUE) {
                            q = new SpscLinkedArrayQueue(this.bufferSize);
                        } else {
                            q = new SpscArrayQueue(i);
                        }
                        this.queue = q;
                    }
                    if (!q.offer(u)) {
                        onError(new IllegalStateException("Scalar queue full?!"));
                        return true;
                    } else if (getAndIncrement() != 0) {
                        return false;
                    }
                }
                drainLoop();
                return true;
            } catch (Throwable ex) {
                Exceptions.throwIfFatal(ex);
                this.errors.addThrowable(ex);
                drain();
                return true;
            }
        }

        void tryEmit(U value, InnerObserver<T, U> inner) {
            if (get() == 0 && compareAndSet(0, 1)) {
                this.downstream.onNext(value);
                if (decrementAndGet() == 0) {
                    return;
                }
            } else {
                SimpleQueue<U> q = inner.queue;
                if (q == null) {
                    q = new SpscLinkedArrayQueue(this.bufferSize);
                    inner.queue = q;
                }
                q.offer(value);
                if (getAndIncrement() != 0) {
                    return;
                }
            }
            drainLoop();
        }

        public void onError(Throwable t) {
            if (this.done) {
                RxJavaPlugins.onError(t);
                return;
            }
            if (this.errors.addThrowable(t)) {
                this.done = true;
                drain();
            } else {
                RxJavaPlugins.onError(t);
            }
        }

        public void onComplete() {
            if (!this.done) {
                this.done = true;
                drain();
            }
        }

        public void dispose() {
            if (!this.cancelled) {
                this.cancelled = true;
                if (disposeAll()) {
                    Throwable ex = this.errors.terminate();
                    if (ex != null && ex != ExceptionHelper.TERMINATED) {
                        RxJavaPlugins.onError(ex);
                    }
                }
            }
        }

        public boolean isDisposed() {
            return this.cancelled;
        }

        void drain() {
            if (getAndIncrement() == 0) {
                drainLoop();
            }
        }

        void drainLoop() {
            Throwable ex;
            InnerObserver<T, U> is;
            Observer<? super U> child = this.downstream;
            int missed = 1;
            while (!checkTerminate()) {
                SimplePlainQueue<U> svq = r1.queue;
                if (svq != null) {
                    while (!checkTerminate()) {
                        U o = svq.poll();
                        if (o != null) {
                            child.onNext(o);
                        } else if (o == null) {
                        }
                    }
                    return;
                }
                boolean d = r1.done;
                SimplePlainQueue<U> svq2 = r1.queue;
                InnerObserver[] inner = (InnerObserver[]) r1.observers.get();
                boolean n = inner.length;
                int nSources = 0;
                if (r1.maxConcurrency != Integer.MAX_VALUE) {
                    synchronized (this) {
                        nSources = r1.sources.size();
                    }
                }
                if (d && ((svq2 == null || svq2.isEmpty()) && !n && nSources == 0)) {
                    ex = r1.errors.terminate();
                    if (ex != ExceptionHelper.TERMINATED) {
                        if (ex == null) {
                            child.onComplete();
                        } else {
                            child.onError(ex);
                        }
                    }
                    return;
                }
                boolean innerCompleted;
                SimplePlainQueue<U> simplePlainQueue;
                if (n) {
                    boolean z;
                    SimpleQueue<U> q;
                    U o2;
                    long startId = r1.lastId;
                    boolean index = r1.lastIndex;
                    if (n > index) {
                        if (inner[index].id == startId) {
                            z = d;
                            simplePlainQueue = svq2;
                            d = index;
                            svq2 = null;
                            innerCompleted = false;
                            while (svq2 < n) {
                                if (checkTerminate()) {
                                    is = inner[d];
                                    while (!checkTerminate()) {
                                        q = is.queue;
                                        if (q == null) {
                                            while (true) {
                                                try {
                                                    o2 = q.poll();
                                                    if (o2 == null) {
                                                        break;
                                                    }
                                                    child.onNext(o2);
                                                    if (checkTerminate()) {
                                                        return;
                                                    }
                                                } catch (Throwable ex2) {
                                                    ex2 = ex2;
                                                    Exceptions.throwIfFatal(ex2);
                                                    is.dispose();
                                                    r1.errors.addThrowable(ex2);
                                                    if (!checkTerminate()) {
                                                        removeInner(is);
                                                        svq2++;
                                                        innerCompleted = true;
                                                    } else {
                                                        return;
                                                    }
                                                }
                                            }
                                            if (o2 != null) {
                                            }
                                        }
                                        o2 = is.done;
                                        q = is.queue;
                                        if (o2 == null && (q == null || q.isEmpty())) {
                                            removeInner(is);
                                            if (!checkTerminate()) {
                                                innerCompleted = true;
                                            } else {
                                                return;
                                            }
                                        }
                                        d++;
                                        if (d == n) {
                                            d = false;
                                        }
                                        svq2++;
                                    }
                                    return;
                                }
                                return;
                            }
                            r1.lastIndex = d;
                            r1.lastId = inner[d].id;
                        }
                    }
                    if (n <= index) {
                        index = false;
                    }
                    innerCompleted = index;
                    boolean i = false;
                    while (i < n) {
                        z = d;
                        simplePlainQueue = svq2;
                        if (inner[innerCompleted].id == startId) {
                            break;
                        }
                        innerCompleted++;
                        if (innerCompleted == n) {
                            innerCompleted = false;
                        }
                        i++;
                        d = z;
                        svq2 = simplePlainQueue;
                    }
                    simplePlainQueue = svq2;
                    index = innerCompleted;
                    r1.lastIndex = innerCompleted;
                    r1.lastId = inner[innerCompleted].id;
                    d = index;
                    svq2 = null;
                    innerCompleted = false;
                    while (svq2 < n) {
                        if (checkTerminate()) {
                            is = inner[d];
                            while (!checkTerminate()) {
                                q = is.queue;
                                if (q == null) {
                                    while (true) {
                                        o2 = q.poll();
                                        if (o2 == null) {
                                            break;
                                        }
                                        child.onNext(o2);
                                        if (checkTerminate()) {
                                            return;
                                        }
                                    }
                                    if (o2 != null) {
                                    }
                                }
                                o2 = is.done;
                                q = is.queue;
                                if (o2 == null) {
                                }
                                d++;
                                if (d == n) {
                                    d = false;
                                }
                                svq2++;
                            }
                            return;
                        }
                        return;
                    }
                    r1.lastIndex = d;
                    r1.lastId = inner[d].id;
                } else {
                    simplePlainQueue = svq2;
                    innerCompleted = false;
                }
                if (!innerCompleted) {
                    missed = addAndGet(-missed);
                    if (missed == 0) {
                        return;
                    }
                } else if (r1.maxConcurrency != Integer.MAX_VALUE) {
                    synchronized (this) {
                        ObservableSource<? extends U> p = (ObservableSource) r1.sources.poll();
                        if (p == null) {
                            r1.wip--;
                        } else {
                            subscribeInner(p);
                        }
                    }
                }
            }
        }

        boolean checkTerminate() {
            if (this.cancelled) {
                return true;
            }
            Throwable e = (Throwable) this.errors.get();
            if (this.delayErrors || e == null) {
                return false;
            }
            disposeAll();
            e = this.errors.terminate();
            if (e != ExceptionHelper.TERMINATED) {
                this.downstream.onError(e);
            }
            return true;
        }

        boolean disposeAll() {
            this.upstream.dispose();
            Object a = (InnerObserver[]) this.observers.get();
            Object obj = CANCELLED;
            int i = 0;
            if (a != obj) {
                InnerObserver[] a2 = (InnerObserver[]) this.observers.getAndSet(obj);
                if (a2 != CANCELLED) {
                    int length = a2.length;
                    while (i < length) {
                        a2[i].dispose();
                        i++;
                    }
                    return true;
                }
            }
            return false;
        }
    }

    public ObservableFlatMap(ObservableSource<T> source, Function<? super T, ? extends ObservableSource<? extends U>> mapper, boolean delayErrors, int maxConcurrency, int bufferSize) {
        super(source);
        this.mapper = mapper;
        this.delayErrors = delayErrors;
        this.maxConcurrency = maxConcurrency;
        this.bufferSize = bufferSize;
    }

    public void subscribeActual(Observer<? super U> t) {
        if (!ObservableScalarXMap.tryScalarXMapSubscribe(this.source, t, this.mapper)) {
            this.source.subscribe(new MergeObserver(t, this.mapper, this.delayErrors, this.maxConcurrency, this.bufferSize));
        }
    }
}
