package io.reactivex.internal.operators.flowable;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.exceptions.MissingBackpressureException;
import io.reactivex.functions.Function;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.fuseable.SimpleQueue;
import io.reactivex.internal.queue.SpscLinkedArrayQueue;
import io.reactivex.internal.subscribers.InnerQueuedSubscriber;
import io.reactivex.internal.subscribers.InnerQueuedSubscriberSupport;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.AtomicThrowable;
import io.reactivex.internal.util.BackpressureHelper;
import io.reactivex.internal.util.ErrorMode;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class FlowableConcatMapEager<T, R> extends AbstractFlowableWithUpstream<T, R> {
    final ErrorMode errorMode;
    final Function<? super T, ? extends Publisher<? extends R>> mapper;
    final int maxConcurrency;
    final int prefetch;

    static final class ConcatMapEagerDelayErrorSubscriber<T, R> extends AtomicInteger implements FlowableSubscriber<T>, Subscription, InnerQueuedSubscriberSupport<R> {
        private static final long serialVersionUID = -4255299542215038287L;
        volatile boolean cancelled;
        volatile InnerQueuedSubscriber<R> current;
        volatile boolean done;
        final Subscriber<? super R> downstream;
        final ErrorMode errorMode;
        final AtomicThrowable errors = new AtomicThrowable();
        final Function<? super T, ? extends Publisher<? extends R>> mapper;
        final int maxConcurrency;
        final int prefetch;
        final AtomicLong requested = new AtomicLong();
        final SpscLinkedArrayQueue<InnerQueuedSubscriber<R>> subscribers;
        Subscription upstream;

        ConcatMapEagerDelayErrorSubscriber(Subscriber<? super R> actual, Function<? super T, ? extends Publisher<? extends R>> mapper, int maxConcurrency, int prefetch, ErrorMode errorMode) {
            this.downstream = actual;
            this.mapper = mapper;
            this.maxConcurrency = maxConcurrency;
            this.prefetch = prefetch;
            this.errorMode = errorMode;
            this.subscribers = new SpscLinkedArrayQueue(Math.min(prefetch, maxConcurrency));
        }

        public void onSubscribe(Subscription s) {
            if (SubscriptionHelper.validate(this.upstream, s)) {
                this.upstream = s;
                this.downstream.onSubscribe(this);
                int i = this.maxConcurrency;
                s.request(i == Integer.MAX_VALUE ? Long.MAX_VALUE : (long) i);
            }
        }

        public void onNext(T t) {
            try {
                Publisher<? extends R> p = (Publisher) ObjectHelper.requireNonNull(this.mapper.apply(t), "The mapper returned a null Publisher");
                InnerQueuedSubscriber<R> inner = new InnerQueuedSubscriber(this, this.prefetch);
                if (!this.cancelled) {
                    this.subscribers.offer(inner);
                    p.subscribe(inner);
                    if (this.cancelled) {
                        inner.cancel();
                        drainAndCancel();
                    }
                }
            } catch (Throwable ex) {
                Exceptions.throwIfFatal(ex);
                this.upstream.cancel();
                onError(ex);
            }
        }

        public void onError(Throwable t) {
            if (this.errors.addThrowable(t)) {
                this.done = true;
                drain();
                return;
            }
            RxJavaPlugins.onError(t);
        }

        public void onComplete() {
            this.done = true;
            drain();
        }

        public void cancel() {
            if (!this.cancelled) {
                this.cancelled = true;
                this.upstream.cancel();
                drainAndCancel();
            }
        }

        void drainAndCancel() {
            if (getAndIncrement() == 0) {
                while (true) {
                    cancelAll();
                    if (decrementAndGet() == 0) {
                        return;
                    }
                }
            }
        }

        void cancelAll() {
            while (true) {
                InnerQueuedSubscriber<R> innerQueuedSubscriber = (InnerQueuedSubscriber) this.subscribers.poll();
                InnerQueuedSubscriber<R> inner = innerQueuedSubscriber;
                if (innerQueuedSubscriber != null) {
                    inner.cancel();
                } else {
                    return;
                }
            }
        }

        public void request(long n) {
            if (SubscriptionHelper.validate(n)) {
                BackpressureHelper.add(this.requested, n);
                drain();
            }
        }

        public void innerNext(InnerQueuedSubscriber<R> inner, R value) {
            if (inner.queue().offer(value)) {
                drain();
                return;
            }
            inner.cancel();
            innerError(inner, new MissingBackpressureException());
        }

        public void innerError(InnerQueuedSubscriber<R> inner, Throwable e) {
            if (this.errors.addThrowable(e)) {
                inner.setDone();
                if (this.errorMode != ErrorMode.END) {
                    this.upstream.cancel();
                }
                drain();
                return;
            }
            RxJavaPlugins.onError(e);
        }

        public void innerComplete(InnerQueuedSubscriber<R> inner) {
            inner.setDone();
            drain();
        }

        public void drain() {
            ConcatMapEagerDelayErrorSubscriber concatMapEagerDelayErrorSubscriber = this;
            if (getAndIncrement() == 0) {
                InnerQueuedSubscriber<R> inner = concatMapEagerDelayErrorSubscriber.current;
                Subscriber<? super R> a = concatMapEagerDelayErrorSubscriber.downstream;
                ErrorMode em = concatMapEagerDelayErrorSubscriber.errorMode;
                int missed = 1;
                while (true) {
                    long r = concatMapEagerDelayErrorSubscriber.requested.get();
                    long e = 0;
                    if (inner == null) {
                        if (em != ErrorMode.END) {
                            if (((Throwable) concatMapEagerDelayErrorSubscriber.errors.get()) != null) {
                                cancelAll();
                                a.onError(concatMapEagerDelayErrorSubscriber.errors.terminate());
                                return;
                            }
                        }
                        inner = (InnerQueuedSubscriber) concatMapEagerDelayErrorSubscriber.subscribers.poll();
                        if (concatMapEagerDelayErrorSubscriber.done && inner == null) {
                            break;
                        } else if (inner != null) {
                            concatMapEagerDelayErrorSubscriber.current = inner;
                        }
                    }
                    boolean continueNextSource = false;
                    if (inner != null) {
                        SimpleQueue<R> q = inner.queue();
                        if (q != null) {
                            while (e != r) {
                                if (concatMapEagerDelayErrorSubscriber.cancelled) {
                                    cancelAll();
                                    return;
                                }
                                if (em == ErrorMode.IMMEDIATE) {
                                    if (((Throwable) concatMapEagerDelayErrorSubscriber.errors.get()) != null) {
                                        concatMapEagerDelayErrorSubscriber.current = null;
                                        inner.cancel();
                                        cancelAll();
                                        a.onError(concatMapEagerDelayErrorSubscriber.errors.terminate());
                                        return;
                                    }
                                }
                                boolean d = inner.isDone();
                                try {
                                    R v = q.poll();
                                    boolean empty = v == null;
                                    if (d && empty) {
                                        inner = null;
                                        concatMapEagerDelayErrorSubscriber.current = null;
                                        concatMapEagerDelayErrorSubscriber.upstream.request(1);
                                        continueNextSource = true;
                                        break;
                                    } else if (empty) {
                                        break;
                                    } else {
                                        a.onNext(v);
                                        e++;
                                        inner.requestOne();
                                    }
                                } catch (Throwable ex) {
                                    Throwable ex2 = ex2;
                                    Exceptions.throwIfFatal(ex2);
                                    concatMapEagerDelayErrorSubscriber.current = null;
                                    inner.cancel();
                                    cancelAll();
                                    a.onError(ex2);
                                    return;
                                }
                            }
                            if (e == r) {
                                if (concatMapEagerDelayErrorSubscriber.cancelled) {
                                    cancelAll();
                                    return;
                                }
                                if (em == ErrorMode.IMMEDIATE) {
                                    if (((Throwable) concatMapEagerDelayErrorSubscriber.errors.get()) != null) {
                                        concatMapEagerDelayErrorSubscriber.current = null;
                                        inner.cancel();
                                        cancelAll();
                                        a.onError(concatMapEagerDelayErrorSubscriber.errors.terminate());
                                        return;
                                    }
                                }
                                boolean d2 = inner.isDone();
                                boolean empty2 = q.isEmpty();
                                if (d2 && empty2) {
                                    inner = null;
                                    concatMapEagerDelayErrorSubscriber.current = null;
                                    concatMapEagerDelayErrorSubscriber.upstream.request(1);
                                    continueNextSource = true;
                                }
                            }
                        }
                    }
                    if (e != 0 && r != Long.MAX_VALUE) {
                        concatMapEagerDelayErrorSubscriber.requested.addAndGet(-e);
                    }
                    if (!continueNextSource) {
                        missed = addAndGet(-missed);
                        if (missed == 0) {
                            return;
                        }
                    }
                }
                Throwable ex3 = concatMapEagerDelayErrorSubscriber.errors.terminate();
                if (ex3 != null) {
                    a.onError(ex3);
                } else {
                    a.onComplete();
                }
            }
        }
    }

    public FlowableConcatMapEager(Flowable<T> source, Function<? super T, ? extends Publisher<? extends R>> mapper, int maxConcurrency, int prefetch, ErrorMode errorMode) {
        super(source);
        this.mapper = mapper;
        this.maxConcurrency = maxConcurrency;
        this.prefetch = prefetch;
        this.errorMode = errorMode;
    }

    protected void subscribeActual(Subscriber<? super R> s) {
        this.source.subscribe(new ConcatMapEagerDelayErrorSubscriber(s, this.mapper, this.maxConcurrency, this.prefetch, this.errorMode));
    }
}
