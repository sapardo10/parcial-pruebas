package io.reactivex.internal.operators.flowable;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Function;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.fuseable.ConditionalSubscriber;
import io.reactivex.internal.subscriptions.EmptySubscription;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.AtomicThrowable;
import io.reactivex.internal.util.HalfSerializer;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class FlowableWithLatestFromMany<T, R> extends AbstractFlowableWithUpstream<T, R> {
    final Function<? super Object[], R> combiner;
    @Nullable
    final Publisher<?>[] otherArray;
    @Nullable
    final Iterable<? extends Publisher<?>> otherIterable;

    final class SingletonArrayFunc implements Function<T, R> {
        SingletonArrayFunc() {
        }

        public R apply(T t) throws Exception {
            return ObjectHelper.requireNonNull(FlowableWithLatestFromMany.this.combiner.apply(new Object[]{t}), "The combiner returned a null value");
        }
    }

    static final class WithLatestInnerSubscriber extends AtomicReference<Subscription> implements FlowableSubscriber<Object> {
        private static final long serialVersionUID = 3256684027868224024L;
        boolean hasValue;
        final int index;
        final WithLatestFromSubscriber<?, ?> parent;

        WithLatestInnerSubscriber(WithLatestFromSubscriber<?, ?> parent, int index) {
            this.parent = parent;
            this.index = index;
        }

        public void onSubscribe(Subscription s) {
            SubscriptionHelper.setOnce(this, s, Long.MAX_VALUE);
        }

        public void onNext(Object t) {
            if (!this.hasValue) {
                this.hasValue = true;
            }
            this.parent.innerNext(this.index, t);
        }

        public void onError(Throwable t) {
            this.parent.innerError(this.index, t);
        }

        public void onComplete() {
            this.parent.innerComplete(this.index, this.hasValue);
        }

        void dispose() {
            SubscriptionHelper.cancel(this);
        }
    }

    static final class WithLatestFromSubscriber<T, R> extends AtomicInteger implements ConditionalSubscriber<T>, Subscription {
        private static final long serialVersionUID = 1577321883966341961L;
        final Function<? super Object[], R> combiner;
        volatile boolean done;
        final Subscriber<? super R> downstream;
        final AtomicThrowable error;
        final AtomicLong requested;
        final WithLatestInnerSubscriber[] subscribers;
        final AtomicReference<Subscription> upstream;
        final AtomicReferenceArray<Object> values;

        WithLatestFromSubscriber(Subscriber<? super R> actual, Function<? super Object[], R> combiner, int n) {
            this.downstream = actual;
            this.combiner = combiner;
            WithLatestInnerSubscriber[] s = new WithLatestInnerSubscriber[n];
            for (int i = 0; i < n; i++) {
                s[i] = new WithLatestInnerSubscriber(this, i);
            }
            this.subscribers = s;
            this.values = new AtomicReferenceArray(n);
            this.upstream = new AtomicReference();
            this.requested = new AtomicLong();
            this.error = new AtomicThrowable();
        }

        void subscribe(Publisher<?>[] others, int n) {
            WithLatestInnerSubscriber[] subscribers = this.subscribers;
            AtomicReference<Subscription> upstream = this.upstream;
            for (int i = 0; i < n && !SubscriptionHelper.isCancelled((Subscription) upstream.get()); i++) {
                others[i].subscribe(subscribers[i]);
            }
        }

        public void onSubscribe(Subscription s) {
            SubscriptionHelper.deferredSetOnce(this.upstream, this.requested, s);
        }

        public void onNext(T t) {
            if (!tryOnNext(t) && !this.done) {
                ((Subscription) this.upstream.get()).request(1);
            }
        }

        public boolean tryOnNext(T t) {
            boolean z = false;
            if (this.done) {
                return false;
            }
            AtomicReferenceArray<Object> ara = this.values;
            int n = ara.length();
            Object[] objects = new Object[(n + 1)];
            objects[0] = t;
            for (int i = 0; i < n; i++) {
                Object o = ara.get(i);
                if (o == null) {
                    return false;
                }
                objects[i + 1] = o;
            }
            try {
                z = ObjectHelper.requireNonNull(this.combiner.apply(objects), "The combiner returned a null value");
                HalfSerializer.onNext(this.downstream, (Object) z, (AtomicInteger) this, this.error);
                return true;
            } catch (Throwable ex) {
                Exceptions.throwIfFatal(ex);
                cancel();
                onError(ex);
                return z;
            }
        }

        public void onError(Throwable t) {
            if (this.done) {
                RxJavaPlugins.onError(t);
                return;
            }
            this.done = true;
            cancelAllBut(-1);
            HalfSerializer.onError(this.downstream, t, (AtomicInteger) this, this.error);
        }

        public void onComplete() {
            if (!this.done) {
                this.done = true;
                cancelAllBut(-1);
                HalfSerializer.onComplete(this.downstream, (AtomicInteger) this, this.error);
            }
        }

        public void request(long n) {
            SubscriptionHelper.deferredRequest(this.upstream, this.requested, n);
        }

        public void cancel() {
            SubscriptionHelper.cancel(this.upstream);
            for (WithLatestInnerSubscriber s : this.subscribers) {
                s.dispose();
            }
        }

        void innerNext(int index, Object o) {
            this.values.set(index, o);
        }

        void innerError(int index, Throwable t) {
            this.done = true;
            SubscriptionHelper.cancel(this.upstream);
            cancelAllBut(index);
            HalfSerializer.onError(this.downstream, t, (AtomicInteger) this, this.error);
        }

        void innerComplete(int index, boolean nonEmpty) {
            if (!nonEmpty) {
                this.done = true;
                SubscriptionHelper.cancel(this.upstream);
                cancelAllBut(index);
                HalfSerializer.onComplete(this.downstream, (AtomicInteger) this, this.error);
            }
        }

        void cancelAllBut(int index) {
            WithLatestInnerSubscriber[] subscribers = this.subscribers;
            for (int i = 0; i < subscribers.length; i++) {
                if (i != index) {
                    subscribers[i].dispose();
                }
            }
        }
    }

    public FlowableWithLatestFromMany(@NonNull Flowable<T> source, @NonNull Publisher<?>[] otherArray, Function<? super Object[], R> combiner) {
        super(source);
        this.otherArray = otherArray;
        this.otherIterable = null;
        this.combiner = combiner;
    }

    public FlowableWithLatestFromMany(@NonNull Flowable<T> source, @NonNull Iterable<? extends Publisher<?>> otherIterable, @NonNull Function<? super Object[], R> combiner) {
        super(source);
        this.otherArray = null;
        this.otherIterable = otherIterable;
        this.combiner = combiner;
    }

    protected void subscribeActual(Subscriber<? super R> s) {
        Throwable ex;
        Publisher<?>[] others = this.otherArray;
        int n = 0;
        if (others == null) {
            others = new Publisher[8];
            try {
                for (Publisher<?> p : this.otherIterable) {
                    if (n == others.length) {
                        others = (Publisher[]) Arrays.copyOf(others, (n >> 1) + n);
                    }
                    int n2 = n + 1;
                    try {
                        others[n] = p;
                        n = n2;
                    } catch (Throwable th) {
                        ex = th;
                        n = n2;
                    }
                }
            } catch (Throwable th2) {
                ex = th2;
                Exceptions.throwIfFatal(ex);
                EmptySubscription.error(ex, s);
                return;
            }
        }
        n = others.length;
        if (n == 0) {
            new FlowableMap(this.source, new SingletonArrayFunc()).subscribeActual(s);
            return;
        }
        WithLatestFromSubscriber<T, R> parent = new WithLatestFromSubscriber(s, this.combiner, n);
        s.onSubscribe(parent);
        parent.subscribe(others, n);
        this.source.subscribe(parent);
    }
}
