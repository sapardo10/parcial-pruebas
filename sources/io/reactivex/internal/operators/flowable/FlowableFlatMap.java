package io.reactivex.internal.operators.flowable;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.exceptions.MissingBackpressureException;
import io.reactivex.functions.Function;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.fuseable.QueueSubscription;
import io.reactivex.internal.fuseable.SimplePlainQueue;
import io.reactivex.internal.fuseable.SimpleQueue;
import io.reactivex.internal.queue.SpscArrayQueue;
import io.reactivex.internal.queue.SpscLinkedArrayQueue;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.AtomicThrowable;
import io.reactivex.internal.util.BackpressureHelper;
import io.reactivex.internal.util.ExceptionHelper;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class FlowableFlatMap<T, U> extends AbstractFlowableWithUpstream<T, U> {
    final int bufferSize;
    final boolean delayErrors;
    final Function<? super T, ? extends Publisher<? extends U>> mapper;
    final int maxConcurrency;

    static final class InnerSubscriber<T, U> extends AtomicReference<Subscription> implements FlowableSubscriber<U>, Disposable {
        private static final long serialVersionUID = -4606175640614850599L;
        final int bufferSize;
        volatile boolean done;
        int fusionMode;
        final long id;
        final int limit = (this.bufferSize >> 2);
        final MergeSubscriber<T, U> parent;
        long produced;
        volatile SimpleQueue<U> queue;

        InnerSubscriber(MergeSubscriber<T, U> parent, long id) {
            this.id = id;
            this.parent = parent;
            this.bufferSize = parent.bufferSize;
        }

        public void onSubscribe(Subscription s) {
            if (SubscriptionHelper.setOnce(this, s)) {
                if (s instanceof QueueSubscription) {
                    QueueSubscription<U> qs = (QueueSubscription) s;
                    int m = qs.requestFusion(7);
                    if (m == 1) {
                        this.fusionMode = m;
                        this.queue = qs;
                        this.done = true;
                        this.parent.drain();
                        return;
                    } else if (m == 2) {
                        this.fusionMode = m;
                        this.queue = qs;
                    }
                }
                s.request((long) this.bufferSize);
            }
        }

        public void onNext(U t) {
            if (this.fusionMode != 2) {
                this.parent.tryEmit(t, this);
            } else {
                this.parent.drain();
            }
        }

        public void onError(Throwable t) {
            lazySet(SubscriptionHelper.CANCELLED);
            this.parent.innerError(this, t);
        }

        public void onComplete() {
            this.done = true;
            this.parent.drain();
        }

        void requestMore(long n) {
            if (this.fusionMode != 1) {
                long p = this.produced + n;
                if (p >= ((long) this.limit)) {
                    this.produced = 0;
                    ((Subscription) get()).request(p);
                    return;
                }
                this.produced = p;
            }
        }

        public void dispose() {
            SubscriptionHelper.cancel(this);
        }

        public boolean isDisposed() {
            return get() == SubscriptionHelper.CANCELLED;
        }
    }

    static final class MergeSubscriber<T, U> extends AtomicInteger implements FlowableSubscriber<T>, Subscription {
        static final InnerSubscriber<?, ?>[] CANCELLED = new InnerSubscriber[0];
        static final InnerSubscriber<?, ?>[] EMPTY = new InnerSubscriber[0];
        private static final long serialVersionUID = -2117620485640801370L;
        final int bufferSize;
        volatile boolean cancelled;
        final boolean delayErrors;
        volatile boolean done;
        final Subscriber<? super U> downstream;
        final AtomicThrowable errs = new AtomicThrowable();
        long lastId;
        int lastIndex;
        final Function<? super T, ? extends Publisher<? extends U>> mapper;
        final int maxConcurrency;
        volatile SimplePlainQueue<U> queue;
        final AtomicLong requested = new AtomicLong();
        int scalarEmitted;
        final int scalarLimit;
        final AtomicReference<InnerSubscriber<?, ?>[]> subscribers = new AtomicReference();
        long uniqueId;
        Subscription upstream;

        MergeSubscriber(Subscriber<? super U> actual, Function<? super T, ? extends Publisher<? extends U>> mapper, boolean delayErrors, int maxConcurrency, int bufferSize) {
            this.downstream = actual;
            this.mapper = mapper;
            this.delayErrors = delayErrors;
            this.maxConcurrency = maxConcurrency;
            this.bufferSize = bufferSize;
            this.scalarLimit = Math.max(1, maxConcurrency >> 1);
            this.subscribers.lazySet(EMPTY);
        }

        public void onSubscribe(Subscription s) {
            if (SubscriptionHelper.validate(this.upstream, s)) {
                this.upstream = s;
                this.downstream.onSubscribe(this);
                if (!this.cancelled) {
                    int i = this.maxConcurrency;
                    if (i == Integer.MAX_VALUE) {
                        s.request(Long.MAX_VALUE);
                    } else {
                        s.request((long) i);
                    }
                }
            }
        }

        public void onNext(T t) {
            if (!this.done) {
                try {
                    Publisher<? extends U> p = (Publisher) ObjectHelper.requireNonNull(this.mapper.apply(t), "The mapper returned a null Publisher");
                    if (p instanceof Callable) {
                        try {
                            U u = ((Callable) p).call();
                            if (u != null) {
                                tryEmitScalar(u);
                            } else if (!(this.maxConcurrency == Integer.MAX_VALUE || this.cancelled)) {
                                int i = this.scalarEmitted + 1;
                                this.scalarEmitted = i;
                                int i2 = this.scalarLimit;
                                if (i == i2) {
                                    this.scalarEmitted = 0;
                                    this.upstream.request((long) i2);
                                }
                            }
                        } catch (Throwable ex) {
                            Exceptions.throwIfFatal(ex);
                            this.errs.addThrowable(ex);
                            drain();
                            return;
                        }
                    }
                    long j = this.uniqueId;
                    this.uniqueId = 1 + j;
                    InnerSubscriber<T, U> inner = new InnerSubscriber(this, j);
                    if (addInner(inner)) {
                        p.subscribe(inner);
                    }
                } catch (Throwable e) {
                    Exceptions.throwIfFatal(e);
                    this.upstream.cancel();
                    onError(e);
                }
            }
        }

        boolean addInner(InnerSubscriber<T, U> inner) {
            while (true) {
                InnerSubscriber[] a = (InnerSubscriber[]) this.subscribers.get();
                if (a == CANCELLED) {
                    inner.dispose();
                    return false;
                }
                int n = a.length;
                InnerSubscriber<?, ?>[] b = new InnerSubscriber[(n + 1)];
                System.arraycopy(a, 0, b, 0, n);
                b[n] = inner;
                if (this.subscribers.compareAndSet(a, b)) {
                    return true;
                }
            }
        }

        void removeInner(InnerSubscriber<T, U> inner) {
            while (true) {
                InnerSubscriber[] a = (InnerSubscriber[]) this.subscribers.get();
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
                        InnerSubscriber<?, ?>[] b;
                        if (n == 1) {
                            b = EMPTY;
                        } else {
                            InnerSubscriber<?, ?>[] b2 = new InnerSubscriber[(n - 1)];
                            System.arraycopy(a, 0, b2, 0, j);
                            System.arraycopy(a, j + 1, b2, j, (n - j) - 1);
                            b = b2;
                        }
                        if (this.subscribers.compareAndSet(a, b)) {
                            return;
                        }
                    } else {
                        return;
                    }
                }
                return;
            }
        }

        SimpleQueue<U> getMainQueue() {
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
            return q;
        }

        void tryEmitScalar(U value) {
            if (get() == 0 && compareAndSet(0, 1)) {
                long r = this.requested.get();
                SimpleQueue<U> q = this.queue;
                if (r == 0 || !(q == null || q.isEmpty())) {
                    if (q == null) {
                        q = getMainQueue();
                    }
                    if (!q.offer(value)) {
                        onError(new IllegalStateException("Scalar queue full?!"));
                        return;
                    }
                } else {
                    this.downstream.onNext(value);
                    if (r != Long.MAX_VALUE) {
                        this.requested.decrementAndGet();
                    }
                    if (!(this.maxConcurrency == Integer.MAX_VALUE || this.cancelled)) {
                        int i = this.scalarEmitted + 1;
                        this.scalarEmitted = i;
                        int i2 = this.scalarLimit;
                        if (i == i2) {
                            this.scalarEmitted = 0;
                            this.upstream.request((long) i2);
                        }
                    }
                }
                if (decrementAndGet() == 0) {
                    return;
                }
            } else if (!getMainQueue().offer(value)) {
                onError(new IllegalStateException("Scalar queue full?!"));
                return;
            } else if (getAndIncrement() != 0) {
                return;
            }
            drainLoop();
        }

        SimpleQueue<U> getInnerQueue(InnerSubscriber<T, U> inner) {
            SimpleQueue<U> q = inner.queue;
            if (q != null) {
                return q;
            }
            SpscArrayQueue q2 = new SpscArrayQueue(this.bufferSize);
            inner.queue = q2;
            return q2;
        }

        void tryEmit(U value, InnerSubscriber<T, U> inner) {
            if (get() == 0 && compareAndSet(0, 1)) {
                long r = this.requested.get();
                SimpleQueue<U> q = inner.queue;
                if (r == 0 || !(q == null || q.isEmpty())) {
                    if (q == null) {
                        q = getInnerQueue(inner);
                    }
                    if (!q.offer(value)) {
                        onError(new MissingBackpressureException("Inner queue full?!"));
                        return;
                    }
                } else {
                    this.downstream.onNext(value);
                    if (r != Long.MAX_VALUE) {
                        this.requested.decrementAndGet();
                    }
                    inner.requestMore(1);
                }
                if (decrementAndGet() == 0) {
                    return;
                }
            } else {
                SimpleQueue<U> q2 = inner.queue;
                if (q2 == null) {
                    q2 = new SpscArrayQueue(this.bufferSize);
                    inner.queue = q2;
                }
                if (!q2.offer(value)) {
                    onError(new MissingBackpressureException("Inner queue full?!"));
                    return;
                } else if (getAndIncrement() != 0) {
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
            if (this.errs.addThrowable(t)) {
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

        public void request(long n) {
            if (SubscriptionHelper.validate(n)) {
                BackpressureHelper.add(this.requested, n);
                drain();
            }
        }

        public void cancel() {
            if (!this.cancelled) {
                this.cancelled = true;
                this.upstream.cancel();
                disposeAll();
                if (getAndIncrement() == 0) {
                    SimpleQueue<U> q = this.queue;
                    if (q != null) {
                        q.clear();
                    }
                }
            }
        }

        void drain() {
            if (getAndIncrement() == 0) {
                drainLoop();
            }
        }

        void drainLoop() {
            Throwable ex;
            Subscriber<? super U> child = this.downstream;
            int missed = 1;
            while (!checkTerminate()) {
                long replenishMain;
                SimplePlainQueue<U> svq = r1.queue;
                long r = r1.requested.get();
                boolean unbounded = r == Long.MAX_VALUE;
                long replenishMain2 = 0;
                long j = 1;
                if (svq != null) {
                    while (true) {
                        long scalarEmission;
                        U o = null;
                        long scalarEmission2 = 0;
                        while (r != 0) {
                            o = svq.poll();
                            if (!checkTerminate()) {
                                if (o == null) {
                                    replenishMain = replenishMain2;
                                    scalarEmission = scalarEmission2;
                                    break;
                                }
                                child.onNext(o);
                                scalarEmission2 += j;
                                r -= j;
                                replenishMain2 += j;
                            } else {
                                return;
                            }
                        }
                        replenishMain = replenishMain2;
                        scalarEmission = scalarEmission2;
                        if (scalarEmission != 0) {
                            if (unbounded) {
                                r = Long.MAX_VALUE;
                            } else {
                                r = r1.requested.addAndGet(-scalarEmission);
                            }
                        }
                        if (r == 0) {
                            break;
                        } else if (o == null) {
                            break;
                        } else {
                            replenishMain2 = replenishMain;
                            j = 1;
                        }
                    }
                } else {
                    replenishMain = 0;
                }
                boolean d = r1.done;
                SimplePlainQueue<U> svq2 = r1.queue;
                InnerSubscriber[] inner = (InnerSubscriber[]) r1.subscribers.get();
                int n = inner.length;
                if (d && ((svq2 == null || svq2.isEmpty()) && n == 0)) {
                    ex = r1.errs.terminate();
                    if (ex != ExceptionHelper.TERMINATED) {
                        if (ex == null) {
                            child.onComplete();
                        } else {
                            child.onError(ex);
                        }
                    }
                    return;
                }
                Subscriber<? super U> child2;
                boolean innerCompleted = false;
                boolean z;
                long j2;
                boolean d2;
                SimplePlainQueue<U> svq3;
                if (n != 0) {
                    int i;
                    int j3;
                    InnerSubscriber<T, U> is;
                    U o2;
                    SimpleQueue<U> q;
                    long startId;
                    U o3;
                    long produced;
                    SimpleQueue<U> innerQueue;
                    j = r1.lastId;
                    int index = r1.lastIndex;
                    if (n > index) {
                        if (inner[index].id == j) {
                            z = false;
                            j2 = r;
                            i = 0;
                            j3 = index;
                            while (i < n) {
                                if (checkTerminate()) {
                                    is = inner[j3];
                                    o2 = null;
                                    while (!checkTerminate()) {
                                        q = is.queue;
                                        if (q != null) {
                                            child2 = child;
                                            d2 = d;
                                            svq3 = svq2;
                                            startId = j;
                                        } else {
                                            svq3 = svq2;
                                            o3 = o2;
                                            d2 = d;
                                            produced = 0;
                                            while (j2 != 0) {
                                                try {
                                                    o2 = q.poll();
                                                    if (o2 == null) {
                                                        child2 = child;
                                                        break;
                                                    }
                                                    child.onNext(o2);
                                                    if (checkTerminate()) {
                                                        j2--;
                                                        produced++;
                                                        o3 = o2;
                                                    } else {
                                                        return;
                                                    }
                                                } catch (Throwable ex2) {
                                                    ex2 = ex2;
                                                    Exceptions.throwIfFatal(ex2);
                                                    is.dispose();
                                                    child2 = child;
                                                    r1.errs.addThrowable(ex2);
                                                    if (r1.delayErrors == null) {
                                                        r1.upstream.cancel();
                                                    }
                                                    if (checkTerminate() == null) {
                                                        removeInner(is);
                                                        i++;
                                                        z = true;
                                                        startId = j;
                                                    } else {
                                                        return;
                                                    }
                                                }
                                            }
                                            child2 = child;
                                            o2 = o3;
                                            if (produced == 0) {
                                                if (unbounded) {
                                                    startId = j;
                                                    j = r1.requested.addAndGet(-produced);
                                                } else {
                                                    startId = j;
                                                    j = Long.MAX_VALUE;
                                                }
                                                is.requestMore(produced);
                                                j2 = j;
                                            } else {
                                                startId = j;
                                            }
                                            if (j2 != 0) {
                                                if (o2 == null) {
                                                    d = d2;
                                                    svq2 = svq3;
                                                    child = child2;
                                                    j = startId;
                                                }
                                            }
                                        }
                                        child = is.done;
                                        innerQueue = is.queue;
                                        if (child != null) {
                                            if (innerQueue != null) {
                                                if (innerQueue.isEmpty()) {
                                                }
                                            }
                                            removeInner(is);
                                            if (checkTerminate()) {
                                                replenishMain++;
                                                z = true;
                                            } else {
                                                return;
                                            }
                                        }
                                        if (j2 == 0) {
                                            innerCompleted = z;
                                            break;
                                        }
                                        j3++;
                                        if (j3 == n) {
                                            j3 = 0;
                                        }
                                        i++;
                                        d = d2;
                                        svq2 = svq3;
                                        child = child2;
                                        j = startId;
                                    }
                                    return;
                                }
                                return;
                            }
                            child2 = child;
                            d2 = d;
                            svq3 = svq2;
                            startId = j;
                            innerCompleted = z;
                            r1.lastIndex = j3;
                            r1.lastId = inner[j3].id;
                            r = replenishMain;
                        }
                    }
                    if (n <= index) {
                        index = 0;
                    }
                    int j4 = index;
                    int i2 = 0;
                    while (i2 < n) {
                        z = innerCompleted;
                        j2 = r;
                        if (inner[j4].id == j) {
                            break;
                        }
                        j4++;
                        if (j4 == n) {
                            j4 = 0;
                        }
                        i2++;
                        innerCompleted = z;
                        r = j2;
                    }
                    z = innerCompleted;
                    j2 = r;
                    index = j4;
                    r1.lastIndex = j4;
                    r1.lastId = inner[j4].id;
                    i = 0;
                    j3 = index;
                    while (i < n) {
                        if (checkTerminate()) {
                            is = inner[j3];
                            o2 = null;
                            while (!checkTerminate()) {
                                q = is.queue;
                                if (q != null) {
                                    svq3 = svq2;
                                    o3 = o2;
                                    d2 = d;
                                    produced = 0;
                                    while (j2 != 0) {
                                        o2 = q.poll();
                                        if (o2 == null) {
                                            child2 = child;
                                            break;
                                        }
                                        child.onNext(o2);
                                        if (checkTerminate()) {
                                            j2--;
                                            produced++;
                                            o3 = o2;
                                        } else {
                                            return;
                                        }
                                    }
                                    child2 = child;
                                    o2 = o3;
                                    if (produced == 0) {
                                        startId = j;
                                    } else {
                                        if (unbounded) {
                                            startId = j;
                                            j = Long.MAX_VALUE;
                                        } else {
                                            startId = j;
                                            j = r1.requested.addAndGet(-produced);
                                        }
                                        is.requestMore(produced);
                                        j2 = j;
                                    }
                                    if (j2 != 0) {
                                        if (o2 == null) {
                                            d = d2;
                                            svq2 = svq3;
                                            child = child2;
                                            j = startId;
                                        }
                                    }
                                } else {
                                    child2 = child;
                                    d2 = d;
                                    svq3 = svq2;
                                    startId = j;
                                }
                                child = is.done;
                                innerQueue = is.queue;
                                if (child != null) {
                                    if (innerQueue != null) {
                                        if (innerQueue.isEmpty()) {
                                        }
                                    }
                                    removeInner(is);
                                    if (checkTerminate()) {
                                        replenishMain++;
                                        z = true;
                                    } else {
                                        return;
                                    }
                                }
                                if (j2 == 0) {
                                    innerCompleted = z;
                                    break;
                                }
                                j3++;
                                if (j3 == n) {
                                    j3 = 0;
                                }
                                i++;
                                d = d2;
                                svq2 = svq3;
                                child = child2;
                                j = startId;
                            }
                            return;
                        }
                        return;
                    }
                    child2 = child;
                    d2 = d;
                    svq3 = svq2;
                    startId = j;
                    innerCompleted = z;
                    r1.lastIndex = j3;
                    r1.lastId = inner[j3].id;
                    r = replenishMain;
                } else {
                    z = false;
                    child2 = child;
                    j2 = r;
                    d2 = d;
                    svq3 = svq2;
                    r = replenishMain;
                }
                if (r != 0 && !r1.cancelled) {
                    r1.upstream.request(r);
                }
                if (innerCompleted) {
                    child = child2;
                } else {
                    missed = addAndGet(-missed);
                    if (missed != 0) {
                        child = child2;
                    } else {
                        return;
                    }
                }
            }
        }

        boolean checkTerminate() {
            if (this.cancelled) {
                clearScalarQueue();
                return true;
            } else if (this.delayErrors || this.errs.get() == null) {
                return false;
            } else {
                clearScalarQueue();
                Throwable ex = this.errs.terminate();
                if (ex != ExceptionHelper.TERMINATED) {
                    this.downstream.onError(ex);
                }
                return true;
            }
        }

        void clearScalarQueue() {
            SimpleQueue<U> q = this.queue;
            if (q != null) {
                q.clear();
            }
        }

        void disposeAll() {
            Object a = (InnerSubscriber[]) this.subscribers.get();
            Object obj = CANCELLED;
            if (a != obj) {
                InnerSubscriber[] a2 = (InnerSubscriber[]) this.subscribers.getAndSet(obj);
                if (a2 != CANCELLED) {
                    for (InnerSubscriber<?, ?> inner : a2) {
                        inner.dispose();
                    }
                    Throwable ex = this.errs.terminate();
                    if (ex != null && ex != ExceptionHelper.TERMINATED) {
                        RxJavaPlugins.onError(ex);
                    }
                }
            }
        }

        void innerError(InnerSubscriber<T, U> inner, Throwable t) {
            if (this.errs.addThrowable(t)) {
                inner.done = true;
                if (!this.delayErrors) {
                    this.upstream.cancel();
                    for (InnerSubscriber<?, ?> a : (InnerSubscriber[]) this.subscribers.getAndSet(CANCELLED)) {
                        a.dispose();
                    }
                }
                drain();
                return;
            }
            RxJavaPlugins.onError(t);
        }
    }

    public FlowableFlatMap(Flowable<T> source, Function<? super T, ? extends Publisher<? extends U>> mapper, boolean delayErrors, int maxConcurrency, int bufferSize) {
        super(source);
        this.mapper = mapper;
        this.delayErrors = delayErrors;
        this.maxConcurrency = maxConcurrency;
        this.bufferSize = bufferSize;
    }

    protected void subscribeActual(Subscriber<? super U> s) {
        if (!FlowableScalarXMap.tryScalarXMapSubscribe(this.source, s, this.mapper)) {
            this.source.subscribe(subscribe(s, this.mapper, this.delayErrors, this.maxConcurrency, this.bufferSize));
        }
    }

    public static <T, U> FlowableSubscriber<T> subscribe(Subscriber<? super U> s, Function<? super T, ? extends Publisher<? extends U>> mapper, boolean delayErrors, int maxConcurrency, int bufferSize) {
        return new MergeSubscriber(s, mapper, delayErrors, maxConcurrency, bufferSize);
    }
}
