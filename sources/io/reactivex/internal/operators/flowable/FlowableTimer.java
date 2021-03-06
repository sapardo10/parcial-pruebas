package io.reactivex.internal.operators.flowable;

import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.MissingBackpressureException;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class FlowableTimer extends Flowable<Long> {
    final long delay;
    final Scheduler scheduler;
    final TimeUnit unit;

    static final class TimerSubscriber extends AtomicReference<Disposable> implements Subscription, Runnable {
        private static final long serialVersionUID = -2809475196591179431L;
        final Subscriber<? super Long> downstream;
        volatile boolean requested;

        TimerSubscriber(Subscriber<? super Long> downstream) {
            this.downstream = downstream;
        }

        public void request(long n) {
            if (SubscriptionHelper.validate(n)) {
                this.requested = true;
            }
        }

        public void cancel() {
            DisposableHelper.dispose(this);
        }

        public void run() {
            if (get() == DisposableHelper.DISPOSED) {
                return;
            }
            if (this.requested) {
                this.downstream.onNext(Long.valueOf(0));
                lazySet(EmptyDisposable.INSTANCE);
                this.downstream.onComplete();
                return;
            }
            lazySet(EmptyDisposable.INSTANCE);
            this.downstream.onError(new MissingBackpressureException("Can't deliver value due to lack of requests"));
        }

        public void setResource(Disposable d) {
            DisposableHelper.trySet(this, d);
        }
    }

    public FlowableTimer(long delay, TimeUnit unit, Scheduler scheduler) {
        this.delay = delay;
        this.unit = unit;
        this.scheduler = scheduler;
    }

    public void subscribeActual(Subscriber<? super Long> s) {
        TimerSubscriber ios = new TimerSubscriber(s);
        s.onSubscribe(ios);
        ios.setResource(this.scheduler.scheduleDirect(ios, this.delay, this.unit));
    }
}
