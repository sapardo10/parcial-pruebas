package io.reactivex.internal.operators.maybe;

import io.reactivex.Flowable;
import io.reactivex.MaybeObserver;
import io.reactivex.MaybeSource;
import io.reactivex.annotations.Nullable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.fuseable.SimpleQueue;
import io.reactivex.internal.subscriptions.BasicIntQueueSubscription;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.AtomicThrowable;
import io.reactivex.internal.util.BackpressureHelper;
import io.reactivex.internal.util.NotificationLite;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReferenceArray;
import org.reactivestreams.Subscriber;

public final class MaybeMergeArray<T> extends Flowable<T> {
    final MaybeSource<? extends T>[] sources;

    interface SimpleQueueWithConsumerIndex<T> extends SimpleQueue<T> {
        int consumerIndex();

        void drop();

        T peek();

        @Nullable
        T poll();

        int producerIndex();
    }

    static final class ClqSimpleQueue<T> extends ConcurrentLinkedQueue<T> implements SimpleQueueWithConsumerIndex<T> {
        private static final long serialVersionUID = -4025173261791142821L;
        int consumerIndex;
        final AtomicInteger producerIndex = new AtomicInteger();

        ClqSimpleQueue() {
        }

        public boolean offer(T t, T t2) {
            throw new UnsupportedOperationException();
        }

        public boolean offer(T e) {
            this.producerIndex.getAndIncrement();
            return super.offer(e);
        }

        @Nullable
        public T poll() {
            T v = super.poll();
            if (v != null) {
                this.consumerIndex++;
            }
            return v;
        }

        public int consumerIndex() {
            return this.consumerIndex;
        }

        public int producerIndex() {
            return this.producerIndex.get();
        }

        public void drop() {
            poll();
        }
    }

    static final class MpscFillOnceSimpleQueue<T> extends AtomicReferenceArray<T> implements SimpleQueueWithConsumerIndex<T> {
        private static final long serialVersionUID = -7969063454040569579L;
        int consumerIndex;
        final AtomicInteger producerIndex = new AtomicInteger();

        MpscFillOnceSimpleQueue(int length) {
            super(length);
        }

        public boolean offer(T value) {
            ObjectHelper.requireNonNull((Object) value, "value is null");
            int idx = this.producerIndex.getAndIncrement();
            if (idx >= length()) {
                return false;
            }
            lazySet(idx, value);
            return true;
        }

        public boolean offer(T t, T t2) {
            throw new UnsupportedOperationException();
        }

        @Nullable
        public T poll() {
            int ci = this.consumerIndex;
            if (ci == length()) {
                return null;
            }
            AtomicInteger pi = this.producerIndex;
            while (true) {
                T v = get(ci);
                if (v != null) {
                    this.consumerIndex = ci + 1;
                    lazySet(ci, null);
                    return v;
                } else if (pi.get() == ci) {
                    return null;
                }
            }
        }

        public T peek() {
            int ci = this.consumerIndex;
            if (ci == length()) {
                return null;
            }
            return get(ci);
        }

        public void drop() {
            int ci = this.consumerIndex;
            lazySet(ci, null);
            this.consumerIndex = ci + 1;
        }

        public boolean isEmpty() {
            return this.consumerIndex == producerIndex();
        }

        public void clear() {
            while (poll() != null && !isEmpty()) {
            }
        }

        public int consumerIndex() {
            return this.consumerIndex;
        }

        public int producerIndex() {
            return this.producerIndex.get();
        }
    }

    static final class MergeMaybeObserver<T> extends BasicIntQueueSubscription<T> implements MaybeObserver<T> {
        private static final long serialVersionUID = -660395290758764731L;
        volatile boolean cancelled;
        long consumed;
        final Subscriber<? super T> downstream;
        final AtomicThrowable error = new AtomicThrowable();
        boolean outputFused;
        final SimpleQueueWithConsumerIndex<Object> queue;
        final AtomicLong requested = new AtomicLong();
        final CompositeDisposable set = new CompositeDisposable();
        final int sourceCount;

        MergeMaybeObserver(Subscriber<? super T> actual, int sourceCount, SimpleQueueWithConsumerIndex<Object> queue) {
            this.downstream = actual;
            this.sourceCount = sourceCount;
            this.queue = queue;
        }

        public int requestFusion(int mode) {
            if ((mode & 2) == 0) {
                return 0;
            }
            this.outputFused = true;
            return 2;
        }

        @Nullable
        public T poll() throws Exception {
            while (true) {
                NotificationLite o = this.queue.poll();
                if (o != NotificationLite.COMPLETE) {
                    return o;
                }
            }
        }

        public boolean isEmpty() {
            return this.queue.isEmpty();
        }

        public void clear() {
            this.queue.clear();
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
                this.set.dispose();
                if (getAndIncrement() == 0) {
                    this.queue.clear();
                }
            }
        }

        public void onSubscribe(Disposable d) {
            this.set.add(d);
        }

        public void onSuccess(T value) {
            this.queue.offer(value);
            drain();
        }

        public void onError(Throwable e) {
            if (this.error.addThrowable(e)) {
                this.set.dispose();
                this.queue.offer(NotificationLite.COMPLETE);
                drain();
                return;
            }
            RxJavaPlugins.onError(e);
        }

        public void onComplete() {
            this.queue.offer(NotificationLite.COMPLETE);
            drain();
        }

        boolean isCancelled() {
            return this.cancelled;
        }

        void drainNormal() {
            int missed = 1;
            Subscriber<? super T> a = this.downstream;
            SimpleQueueWithConsumerIndex<Object> q = this.queue;
            long e = this.consumed;
            while (true) {
                long r = this.requested.get();
                while (e != r) {
                    if (this.cancelled) {
                        q.clear();
                        return;
                    } else if (((Throwable) this.error.get()) != null) {
                        q.clear();
                        a.onError(this.error.terminate());
                        return;
                    } else if (q.consumerIndex() == this.sourceCount) {
                        a.onComplete();
                        return;
                    } else {
                        NotificationLite v = q.poll();
                        if (v == null) {
                            break;
                        } else if (v != NotificationLite.COMPLETE) {
                            a.onNext(v);
                            e++;
                        }
                    }
                }
                if (e == r) {
                    if (((Throwable) this.error.get()) != null) {
                        q.clear();
                        a.onError(this.error.terminate());
                        return;
                    }
                    while (q.peek() == NotificationLite.COMPLETE) {
                        q.drop();
                    }
                    if (q.consumerIndex() == this.sourceCount) {
                        a.onComplete();
                        return;
                    }
                }
                this.consumed = e;
                missed = addAndGet(-missed);
                if (missed == 0) {
                    return;
                }
            }
        }

        void drainFused() {
            int missed = 1;
            Subscriber<? super T> a = this.downstream;
            SimpleQueueWithConsumerIndex<Object> q = this.queue;
            while (!this.cancelled) {
                Throwable ex = (Throwable) this.error.get();
                if (ex != null) {
                    q.clear();
                    a.onError(ex);
                    return;
                }
                boolean d = q.producerIndex() == this.sourceCount;
                if (!q.isEmpty()) {
                    a.onNext(null);
                }
                if (d) {
                    a.onComplete();
                    return;
                }
                missed = addAndGet(-missed);
                if (missed == 0) {
                    return;
                }
            }
            q.clear();
        }

        void drain() {
            if (getAndIncrement() == 0) {
                if (this.outputFused) {
                    drainFused();
                } else {
                    drainNormal();
                }
            }
        }
    }

    public MaybeMergeArray(MaybeSource<? extends T>[] sources) {
        this.sources = sources;
    }

    protected void subscribeActual(Subscriber<? super T> s) {
        SimpleQueueWithConsumerIndex<Object> queue;
        MaybeSource<? extends T>[] maybes = this.sources;
        int n = maybes.length;
        if (n <= bufferSize()) {
            queue = new MpscFillOnceSimpleQueue(n);
        } else {
            queue = new ClqSimpleQueue();
        }
        MergeMaybeObserver<T> parent = new MergeMaybeObserver(s, n, queue);
        s.onSubscribe(parent);
        AtomicThrowable e = parent.error;
        int length = maybes.length;
        int i = 0;
        while (i < length) {
            MaybeSource<? extends T> source = maybes[i];
            if (!parent.isCancelled()) {
                if (e.get() == null) {
                    source.subscribe(parent);
                    i++;
                }
            }
            return;
        }
    }
}