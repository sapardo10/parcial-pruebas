package io.reactivex.internal.operators.flowable;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.exceptions.MissingBackpressureException;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.BackpressureHelper;
import io.reactivex.subscribers.SerializedSubscriber;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class FlowableSamplePublisher<T> extends Flowable<T> {
    final boolean emitLast;
    final Publisher<?> other;
    final Publisher<T> source;

    static abstract class SamplePublisherSubscriber<T> extends AtomicReference<T> implements FlowableSubscriber<T>, Subscription {
        private static final long serialVersionUID = -3517602651313910099L;
        final Subscriber<? super T> downstream;
        final AtomicReference<Subscription> other = new AtomicReference();
        final AtomicLong requested = new AtomicLong();
        final Publisher<?> sampler;
        Subscription upstream;

        abstract void completeMain();

        abstract void completeOther();

        abstract void run();

        SamplePublisherSubscriber(Subscriber<? super T> actual, Publisher<?> other) {
            this.downstream = actual;
            this.sampler = other;
        }

        public void onSubscribe(Subscription s) {
            if (SubscriptionHelper.validate(this.upstream, s)) {
                this.upstream = s;
                this.downstream.onSubscribe(this);
                if (this.other.get() == null) {
                    this.sampler.subscribe(new SamplerSubscriber(this));
                    s.request(Long.MAX_VALUE);
                }
            }
        }

        public void onNext(T t) {
            lazySet(t);
        }

        public void onError(Throwable t) {
            SubscriptionHelper.cancel(this.other);
            this.downstream.onError(t);
        }

        public void onComplete() {
            SubscriptionHelper.cancel(this.other);
            completeMain();
        }

        void setOther(Subscription o) {
            SubscriptionHelper.setOnce(this.other, o, Long.MAX_VALUE);
        }

        public void request(long n) {
            if (SubscriptionHelper.validate(n)) {
                BackpressureHelper.add(this.requested, n);
            }
        }

        public void cancel() {
            SubscriptionHelper.cancel(this.other);
            this.upstream.cancel();
        }

        public void error(Throwable e) {
            this.upstream.cancel();
            this.downstream.onError(e);
        }

        public void complete() {
            this.upstream.cancel();
            completeOther();
        }

        void emit() {
            T value = getAndSet(null);
            if (value == null) {
                return;
            }
            if (this.requested.get() != 0) {
                this.downstream.onNext(value);
                BackpressureHelper.produced(this.requested, 1);
                return;
            }
            cancel();
            this.downstream.onError(new MissingBackpressureException("Couldn't emit value due to lack of requests!"));
        }
    }

    static final class SamplerSubscriber<T> implements FlowableSubscriber<Object> {
        final SamplePublisherSubscriber<T> parent;

        SamplerSubscriber(SamplePublisherSubscriber<T> parent) {
            this.parent = parent;
        }

        public void onSubscribe(Subscription s) {
            this.parent.setOther(s);
        }

        public void onNext(Object t) {
            this.parent.run();
        }

        public void onError(Throwable t) {
            this.parent.error(t);
        }

        public void onComplete() {
            this.parent.complete();
        }
    }

    static final class SampleMainEmitLast<T> extends SamplePublisherSubscriber<T> {
        private static final long serialVersionUID = -3029755663834015785L;
        volatile boolean done;
        final AtomicInteger wip = new AtomicInteger();

        SampleMainEmitLast(Subscriber<? super T> actual, Publisher<?> other) {
            super(actual, other);
        }

        void completeMain() {
            this.done = true;
            if (this.wip.getAndIncrement() == 0) {
                emit();
                this.downstream.onComplete();
            }
        }

        void completeOther() {
            this.done = true;
            if (this.wip.getAndIncrement() == 0) {
                emit();
                this.downstream.onComplete();
            }
        }

        void run() {
            if (this.wip.getAndIncrement() == 0) {
                while (true) {
                    boolean d = this.done;
                    emit();
                    if (d) {
                        this.downstream.onComplete();
                        return;
                    } else if (this.wip.decrementAndGet() == 0) {
                        break;
                    }
                }
            }
        }
    }

    static final class SampleMainNoLast<T> extends SamplePublisherSubscriber<T> {
        private static final long serialVersionUID = -3029755663834015785L;

        SampleMainNoLast(Subscriber<? super T> actual, Publisher<?> other) {
            super(actual, other);
        }

        void completeMain() {
            this.downstream.onComplete();
        }

        void completeOther() {
            this.downstream.onComplete();
        }

        void run() {
            emit();
        }
    }

    public FlowableSamplePublisher(Publisher<T> source, Publisher<?> other, boolean emitLast) {
        this.source = source;
        this.other = other;
        this.emitLast = emitLast;
    }

    protected void subscribeActual(Subscriber<? super T> s) {
        SerializedSubscriber<T> serial = new SerializedSubscriber(s);
        if (this.emitLast) {
            this.source.subscribe(new SampleMainEmitLast(serial, this.other));
        } else {
            this.source.subscribe(new SampleMainNoLast(serial, this.other));
        }
    }
}
