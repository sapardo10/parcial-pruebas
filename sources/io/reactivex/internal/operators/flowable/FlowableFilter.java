package io.reactivex.internal.operators.flowable;

import io.reactivex.Flowable;
import io.reactivex.annotations.Nullable;
import io.reactivex.functions.Predicate;
import io.reactivex.internal.fuseable.ConditionalSubscriber;
import io.reactivex.internal.fuseable.QueueSubscription;
import io.reactivex.internal.subscribers.BasicFuseableConditionalSubscriber;
import io.reactivex.internal.subscribers.BasicFuseableSubscriber;
import org.reactivestreams.Subscriber;

public final class FlowableFilter<T> extends AbstractFlowableWithUpstream<T, T> {
    final Predicate<? super T> predicate;

    static final class FilterConditionalSubscriber<T> extends BasicFuseableConditionalSubscriber<T, T> {
        final Predicate<? super T> filter;

        FilterConditionalSubscriber(ConditionalSubscriber<? super T> actual, Predicate<? super T> filter) {
            super(actual);
            this.filter = filter;
        }

        public void onNext(T t) {
            if (!tryOnNext(t)) {
                this.upstream.request(1);
            }
        }

        public boolean tryOnNext(T t) {
            if (this.done) {
                return false;
            }
            if (this.sourceMode != 0) {
                return this.downstream.tryOnNext(null);
            }
            boolean z = true;
            try {
                if (!this.filter.test(t) || !this.downstream.tryOnNext(t)) {
                    z = false;
                }
                return z;
            } catch (Throwable e) {
                fail(e);
                return true;
            }
        }

        public int requestFusion(int mode) {
            return transitiveBoundaryFusion(mode);
        }

        @Nullable
        public T poll() throws Exception {
            QueueSubscription<T> qs = this.qs;
            Predicate<? super T> f = this.filter;
            while (true) {
                T t = qs.poll();
                if (t == null) {
                    return null;
                }
                if (f.test(t)) {
                    return t;
                }
                if (this.sourceMode == 2) {
                    qs.request(1);
                }
            }
        }
    }

    static final class FilterSubscriber<T> extends BasicFuseableSubscriber<T, T> implements ConditionalSubscriber<T> {
        final Predicate<? super T> filter;

        FilterSubscriber(Subscriber<? super T> actual, Predicate<? super T> filter) {
            super(actual);
            this.filter = filter;
        }

        public void onNext(T t) {
            if (!tryOnNext(t)) {
                this.upstream.request(1);
            }
        }

        public boolean tryOnNext(T t) {
            if (this.done) {
                return false;
            }
            if (this.sourceMode != 0) {
                this.downstream.onNext(null);
                return true;
            }
            try {
                boolean b = this.filter.test(t);
                if (b) {
                    this.downstream.onNext(t);
                }
                return b;
            } catch (Throwable e) {
                fail(e);
                return true;
            }
        }

        public int requestFusion(int mode) {
            return transitiveBoundaryFusion(mode);
        }

        @Nullable
        public T poll() throws Exception {
            QueueSubscription<T> qs = this.qs;
            Predicate<? super T> f = this.filter;
            while (true) {
                T t = qs.poll();
                if (t == null) {
                    return null;
                }
                if (f.test(t)) {
                    return t;
                }
                if (this.sourceMode == 2) {
                    qs.request(1);
                }
            }
        }
    }

    public FlowableFilter(Flowable<T> source, Predicate<? super T> predicate) {
        super(source);
        this.predicate = predicate;
    }

    protected void subscribeActual(Subscriber<? super T> s) {
        if (s instanceof ConditionalSubscriber) {
            this.source.subscribe(new FilterConditionalSubscriber((ConditionalSubscriber) s, this.predicate));
        } else {
            this.source.subscribe(new FilterSubscriber(s, this.predicate));
        }
    }
}
