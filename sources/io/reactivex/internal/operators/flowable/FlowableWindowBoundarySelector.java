package io.reactivex.internal.operators.flowable;

import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.MissingBackpressureException;
import io.reactivex.functions.Function;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.fuseable.SimplePlainQueue;
import io.reactivex.internal.queue.MpscLinkedQueue;
import io.reactivex.internal.subscribers.QueueDrainSubscriber;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.NotificationLite;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.processors.UnicastProcessor;
import io.reactivex.subscribers.DisposableSubscriber;
import io.reactivex.subscribers.SerializedSubscriber;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class FlowableWindowBoundarySelector<T, B, V> extends AbstractFlowableWithUpstream<T, Flowable<T>> {
    final int bufferSize;
    final Function<? super B, ? extends Publisher<V>> close;
    final Publisher<B> open;

    static final class WindowOperation<T, B> {
        final B open;
        /* renamed from: w */
        final UnicastProcessor<T> f26w;

        WindowOperation(UnicastProcessor<T> w, B open) {
            this.f26w = w;
            this.open = open;
        }
    }

    static final class OperatorWindowBoundaryCloseSubscriber<T, V> extends DisposableSubscriber<V> {
        boolean done;
        final WindowBoundaryMainSubscriber<T, ?, V> parent;
        /* renamed from: w */
        final UnicastProcessor<T> f63w;

        OperatorWindowBoundaryCloseSubscriber(WindowBoundaryMainSubscriber<T, ?, V> parent, UnicastProcessor<T> w) {
            this.parent = parent;
            this.f63w = w;
        }

        public void onNext(V v) {
            cancel();
            onComplete();
        }

        public void onError(Throwable t) {
            if (this.done) {
                RxJavaPlugins.onError(t);
                return;
            }
            this.done = true;
            this.parent.error(t);
        }

        public void onComplete() {
            if (!this.done) {
                this.done = true;
                this.parent.close(this);
            }
        }
    }

    static final class OperatorWindowBoundaryOpenSubscriber<T, B> extends DisposableSubscriber<B> {
        final WindowBoundaryMainSubscriber<T, B, ?> parent;

        OperatorWindowBoundaryOpenSubscriber(WindowBoundaryMainSubscriber<T, B, ?> parent) {
            this.parent = parent;
        }

        public void onNext(B t) {
            this.parent.open(t);
        }

        public void onError(Throwable t) {
            this.parent.error(t);
        }

        public void onComplete() {
            this.parent.onComplete();
        }
    }

    static final class WindowBoundaryMainSubscriber<T, B, V> extends QueueDrainSubscriber<T, Object, Flowable<T>> implements Subscription {
        final AtomicReference<Disposable> boundary = new AtomicReference();
        final int bufferSize;
        final Function<? super B, ? extends Publisher<V>> close;
        final Publisher<B> open;
        final CompositeDisposable resources;
        Subscription upstream;
        final AtomicLong windows = new AtomicLong();
        final List<UnicastProcessor<T>> ws;

        WindowBoundaryMainSubscriber(Subscriber<? super Flowable<T>> actual, Publisher<B> open, Function<? super B, ? extends Publisher<V>> close, int bufferSize) {
            super(actual, new MpscLinkedQueue());
            this.open = open;
            this.close = close;
            this.bufferSize = bufferSize;
            this.resources = new CompositeDisposable();
            this.ws = new ArrayList();
            this.windows.lazySet(1);
        }

        public void onSubscribe(Subscription s) {
            if (SubscriptionHelper.validate(this.upstream, s)) {
                this.upstream = s;
                this.downstream.onSubscribe(this);
                if (!this.cancelled) {
                    OperatorWindowBoundaryOpenSubscriber<T, B> os = new OperatorWindowBoundaryOpenSubscriber(this);
                    if (this.boundary.compareAndSet(null, os)) {
                        this.windows.getAndIncrement();
                        s.request(Long.MAX_VALUE);
                        this.open.subscribe(os);
                    }
                }
            }
        }

        public void onNext(T t) {
            if (!this.done) {
                if (fastEnter()) {
                    for (UnicastProcessor<T> w : this.ws) {
                        w.onNext(t);
                    }
                    if (leave(-1) == 0) {
                        return;
                    }
                } else {
                    this.queue.offer(NotificationLite.next(t));
                    if (!enter()) {
                        return;
                    }
                }
                drainLoop();
            }
        }

        public void onError(Throwable t) {
            if (this.done) {
                RxJavaPlugins.onError(t);
                return;
            }
            this.error = t;
            this.done = true;
            if (enter()) {
                drainLoop();
            }
            if (this.windows.decrementAndGet() == 0) {
                this.resources.dispose();
            }
            this.downstream.onError(t);
        }

        public void onComplete() {
            if (!this.done) {
                this.done = true;
                if (enter()) {
                    drainLoop();
                }
                if (this.windows.decrementAndGet() == 0) {
                    this.resources.dispose();
                }
                this.downstream.onComplete();
            }
        }

        void error(Throwable t) {
            this.upstream.cancel();
            this.resources.dispose();
            DisposableHelper.dispose(this.boundary);
            this.downstream.onError(t);
        }

        public void request(long n) {
            requested(n);
        }

        public void cancel() {
            this.cancelled = true;
        }

        void dispose() {
            this.resources.dispose();
            DisposableHelper.dispose(this.boundary);
        }

        void drainLoop() {
            UnicastProcessor<T> w;
            SimplePlainQueue<Object> q = this.queue;
            Subscriber<? super Flowable<T>> a = this.downstream;
            List<UnicastProcessor<T>> ws = this.ws;
            int missed = 1;
            while (true) {
                boolean d = this.done;
                WindowOperation<T, B> o = q.poll();
                boolean empty = o == null;
                if (d && empty) {
                    break;
                } else if (empty) {
                    missed = leave(-missed);
                    if (missed == 0) {
                        return;
                    }
                } else if (o instanceof WindowOperation) {
                    WindowOperation<T, B> wo = o;
                    if (wo.f26w != null) {
                        if (ws.remove(wo.f26w)) {
                            wo.f26w.onComplete();
                            if (this.windows.decrementAndGet() == 0) {
                                dispose();
                                return;
                            }
                        }
                    } else if (!this.cancelled) {
                        w = UnicastProcessor.create(this.bufferSize);
                        long r = requested();
                        if (r != 0) {
                            ws.add(w);
                            a.onNext(w);
                            if (r != Long.MAX_VALUE) {
                                produced(1);
                            }
                            try {
                                Publisher<V> p = (Publisher) ObjectHelper.requireNonNull(this.close.apply(wo.open), "The publisher supplied is null");
                                OperatorWindowBoundaryCloseSubscriber<T, V> cl = new OperatorWindowBoundaryCloseSubscriber(this, w);
                                if (this.resources.add(cl)) {
                                    this.windows.getAndIncrement();
                                    p.subscribe(cl);
                                }
                            } catch (Throwable e) {
                                this.cancelled = true;
                                a.onError(e);
                            }
                        } else {
                            this.cancelled = true;
                            a.onError(new MissingBackpressureException("Could not deliver new window due to lack of requests"));
                        }
                    }
                } else {
                    for (UnicastProcessor<T> w2 : ws) {
                        w2.onNext(NotificationLite.getValue(o));
                    }
                }
            }
            dispose();
            Throwable e2 = this.error;
            if (e2 != null) {
                for (UnicastProcessor<T> w3 : ws) {
                    w3.onError(e2);
                }
            } else {
                for (UnicastProcessor<T> w32 : ws) {
                    w32.onComplete();
                }
            }
            ws.clear();
        }

        public boolean accept(Subscriber<? super Flowable<T>> subscriber, Object v) {
            return false;
        }

        void open(B b) {
            this.queue.offer(new WindowOperation(null, b));
            if (enter()) {
                drainLoop();
            }
        }

        void close(OperatorWindowBoundaryCloseSubscriber<T, V> w) {
            this.resources.delete(w);
            this.queue.offer(new WindowOperation(w.f63w, null));
            if (enter()) {
                drainLoop();
            }
        }
    }

    public FlowableWindowBoundarySelector(Flowable<T> source, Publisher<B> open, Function<? super B, ? extends Publisher<V>> close, int bufferSize) {
        super(source);
        this.open = open;
        this.close = close;
        this.bufferSize = bufferSize;
    }

    protected void subscribeActual(Subscriber<? super Flowable<T>> s) {
        this.source.subscribe(new WindowBoundaryMainSubscriber(new SerializedSubscriber(s), this.open, this.close, this.bufferSize));
    }
}
