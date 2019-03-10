package io.reactivex.internal.operators.maybe;

import io.reactivex.Flowable;
import io.reactivex.MaybeObserver;
import io.reactivex.MaybeSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.SequentialDisposable;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.AtomicThrowable;
import io.reactivex.internal.util.BackpressureHelper;
import io.reactivex.internal.util.NotificationLite;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class MaybeConcatArrayDelayError<T> extends Flowable<T> {
    final MaybeSource<? extends T>[] sources;

    static final class ConcatMaybeObserver<T> extends AtomicInteger implements MaybeObserver<T>, Subscription {
        private static final long serialVersionUID = 3520831347801429610L;
        final AtomicReference<Object> current = new AtomicReference(NotificationLite.COMPLETE);
        final SequentialDisposable disposables = new SequentialDisposable();
        final Subscriber<? super T> downstream;
        final AtomicThrowable errors = new AtomicThrowable();
        int index;
        long produced;
        final AtomicLong requested = new AtomicLong();
        final MaybeSource<? extends T>[] sources;

        ConcatMaybeObserver(Subscriber<? super T> actual, MaybeSource<? extends T>[] sources) {
            this.downstream = actual;
            this.sources = sources;
        }

        public void request(long n) {
            if (SubscriptionHelper.validate(n)) {
                BackpressureHelper.add(this.requested, n);
                drain();
            }
        }

        public void cancel() {
            this.disposables.dispose();
        }

        public void onSubscribe(Disposable d) {
            this.disposables.replace(d);
        }

        public void onSuccess(T value) {
            this.current.lazySet(value);
            drain();
        }

        public void onError(Throwable e) {
            this.current.lazySet(NotificationLite.COMPLETE);
            if (this.errors.addThrowable(e)) {
                drain();
            } else {
                RxJavaPlugins.onError(e);
            }
        }

        public void onComplete() {
            this.current.lazySet(NotificationLite.COMPLETE);
            drain();
        }

        void drain() {
            if (getAndIncrement() == 0) {
                AtomicReference<Object> c = this.current;
                Subscriber<? super T> a = this.downstream;
                Disposable cancelled = this.disposables;
                while (!cancelled.isDisposed()) {
                    NotificationLite o = c.get();
                    if (o != null) {
                        boolean z;
                        if (o != NotificationLite.COMPLETE) {
                            long p = this.produced;
                            if (p != this.requested.get()) {
                                this.produced = 1 + p;
                                c.lazySet(null);
                                z = true;
                                a.onNext(o);
                            } else {
                                z = false;
                            }
                        } else {
                            c.lazySet(null);
                            z = true;
                        }
                        if (z && !cancelled.isDisposed()) {
                            int i = this.index;
                            MaybeSource[] maybeSourceArr = this.sources;
                            if (i == maybeSourceArr.length) {
                                if (((Throwable) this.errors.get()) != null) {
                                    a.onError(this.errors.terminate());
                                } else {
                                    a.onComplete();
                                }
                                return;
                            }
                            this.index = i + 1;
                            maybeSourceArr[i].subscribe(this);
                        }
                    }
                    if (decrementAndGet() == 0) {
                        return;
                    }
                }
                c.lazySet(null);
            }
        }
    }

    public MaybeConcatArrayDelayError(MaybeSource<? extends T>[] sources) {
        this.sources = sources;
    }

    protected void subscribeActual(Subscriber<? super T> s) {
        ConcatMaybeObserver<T> parent = new ConcatMaybeObserver(s, this.sources);
        s.onSubscribe(parent);
        parent.drain();
    }
}
