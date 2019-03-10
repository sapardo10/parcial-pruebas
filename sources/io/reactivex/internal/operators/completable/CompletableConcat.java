package io.reactivex.internal.operators.completable;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.CompletableSource;
import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.exceptions.MissingBackpressureException;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.fuseable.QueueSubscription;
import io.reactivex.internal.fuseable.SimpleQueue;
import io.reactivex.internal.queue.SpscArrayQueue;
import io.reactivex.internal.queue.SpscLinkedArrayQueue;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;

public final class CompletableConcat extends Completable {
    final int prefetch;
    final Publisher<? extends CompletableSource> sources;

    static final class CompletableConcatSubscriber extends AtomicInteger implements FlowableSubscriber<CompletableSource>, Disposable {
        private static final long serialVersionUID = 9032184911934499404L;
        volatile boolean active;
        int consumed;
        volatile boolean done;
        final CompletableObserver downstream;
        final ConcatInnerObserver inner = new ConcatInnerObserver(this);
        final int limit;
        final AtomicBoolean once = new AtomicBoolean();
        final int prefetch;
        SimpleQueue<CompletableSource> queue;
        int sourceFused;
        Subscription upstream;

        static final class ConcatInnerObserver extends AtomicReference<Disposable> implements CompletableObserver {
            private static final long serialVersionUID = -5454794857847146511L;
            final CompletableConcatSubscriber parent;

            ConcatInnerObserver(CompletableConcatSubscriber parent) {
                this.parent = parent;
            }

            public void onSubscribe(Disposable d) {
                DisposableHelper.replace(this, d);
            }

            public void onError(Throwable e) {
                this.parent.innerError(e);
            }

            public void onComplete() {
                this.parent.innerComplete();
            }
        }

        CompletableConcatSubscriber(CompletableObserver actual, int prefetch) {
            this.downstream = actual;
            this.prefetch = prefetch;
            this.limit = prefetch - (prefetch >> 2);
        }

        public void onSubscribe(Subscription s) {
            if (SubscriptionHelper.validate(this.upstream, s)) {
                this.upstream = s;
                int i = this.prefetch;
                long r = i == Integer.MAX_VALUE ? Long.MAX_VALUE : (long) i;
                if (s instanceof QueueSubscription) {
                    QueueSubscription<CompletableSource> qs = (QueueSubscription) s;
                    int m = qs.requestFusion(3);
                    if (m == 1) {
                        this.sourceFused = m;
                        this.queue = qs;
                        this.done = true;
                        this.downstream.onSubscribe(this);
                        drain();
                        return;
                    } else if (m == 2) {
                        this.sourceFused = m;
                        this.queue = qs;
                        this.downstream.onSubscribe(this);
                        s.request(r);
                        return;
                    }
                }
                i = this.prefetch;
                if (i == Integer.MAX_VALUE) {
                    this.queue = new SpscLinkedArrayQueue(Flowable.bufferSize());
                } else {
                    this.queue = new SpscArrayQueue(i);
                }
                this.downstream.onSubscribe(this);
                s.request(r);
            }
        }

        public void onNext(CompletableSource t) {
            if (this.sourceFused == 0) {
                if (!this.queue.offer(t)) {
                    onError(new MissingBackpressureException());
                    return;
                }
            }
            drain();
        }

        public void onError(Throwable t) {
            if (this.once.compareAndSet(false, true)) {
                DisposableHelper.dispose(this.inner);
                this.downstream.onError(t);
                return;
            }
            RxJavaPlugins.onError(t);
        }

        public void onComplete() {
            this.done = true;
            drain();
        }

        public void dispose() {
            this.upstream.cancel();
            DisposableHelper.dispose(this.inner);
        }

        public boolean isDisposed() {
            return DisposableHelper.isDisposed((Disposable) this.inner.get());
        }

        void drain() {
            if (getAndIncrement() == 0) {
                while (!isDisposed()) {
                    if (!this.active) {
                        boolean d = this.done;
                        try {
                            CompletableSource cs = (CompletableSource) this.queue.poll();
                            boolean empty = cs == null;
                            if (d && empty) {
                                if (this.once.compareAndSet(false, true)) {
                                    this.downstream.onComplete();
                                }
                                return;
                            } else if (!empty) {
                                this.active = true;
                                cs.subscribe(this.inner);
                                request();
                            }
                        } catch (Throwable ex) {
                            Exceptions.throwIfFatal(ex);
                            innerError(ex);
                            return;
                        }
                    }
                    if (decrementAndGet() == 0) {
                        return;
                    }
                }
            }
        }

        void request() {
            if (this.sourceFused != 1) {
                int p = this.consumed + 1;
                if (p == this.limit) {
                    this.consumed = 0;
                    this.upstream.request((long) p);
                    return;
                }
                this.consumed = p;
            }
        }

        void innerError(Throwable e) {
            if (this.once.compareAndSet(false, true)) {
                this.upstream.cancel();
                this.downstream.onError(e);
                return;
            }
            RxJavaPlugins.onError(e);
        }

        void innerComplete() {
            this.active = false;
            drain();
        }
    }

    public CompletableConcat(Publisher<? extends CompletableSource> sources, int prefetch) {
        this.sources = sources;
        this.prefetch = prefetch;
    }

    public void subscribeActual(CompletableObserver observer) {
        this.sources.subscribe(new CompletableConcatSubscriber(observer, this.prefetch));
    }
}
