package io.reactivex.internal.operators.flowable;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.BackpressureHelper;
import io.reactivex.internal.util.LinkedArrayList;
import io.reactivex.internal.util.NotificationLite;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class FlowableCache<T> extends AbstractFlowableWithUpstream<T, T> {
    final AtomicBoolean once = new AtomicBoolean();
    final CacheState<T> state;

    static final class ReplaySubscription<T> extends AtomicInteger implements Subscription {
        private static final long CANCELLED = Long.MIN_VALUE;
        private static final long serialVersionUID = -2557562030197141021L;
        final Subscriber<? super T> child;
        Object[] currentBuffer;
        int currentIndexInBuffer;
        long emitted;
        int index;
        final AtomicLong requested = new AtomicLong();
        final CacheState<T> state;

        ReplaySubscription(Subscriber<? super T> child, CacheState<T> state) {
            this.child = child;
            this.state = state;
        }

        public void request(long n) {
            if (SubscriptionHelper.validate(n)) {
                BackpressureHelper.addCancel(this.requested, n);
                replay();
            }
        }

        public void cancel() {
            if (this.requested.getAndSet(Long.MIN_VALUE) != Long.MIN_VALUE) {
                this.state.removeChild(this);
            }
        }

        public void replay() {
            ReplaySubscription replaySubscription = this;
            if (getAndIncrement() == 0) {
                int missed = 1;
                Subscriber child = replaySubscription.child;
                AtomicLong rq = replaySubscription.requested;
                long e = replaySubscription.emitted;
                while (true) {
                    long r = rq.get();
                    if (r != Long.MIN_VALUE) {
                        int s = replaySubscription.state.size();
                        if (s != 0) {
                            Object[] b = replaySubscription.currentBuffer;
                            if (b == null) {
                                b = replaySubscription.state.head();
                                replaySubscription.currentBuffer = b;
                            }
                            int n = b.length - 1;
                            int j = replaySubscription.index;
                            int k = replaySubscription.currentIndexInBuffer;
                            while (j < s && e != r) {
                                if (rq.get() != Long.MIN_VALUE) {
                                    if (k == n) {
                                        b = (Object[]) b[n];
                                        k = 0;
                                    }
                                    if (!NotificationLite.accept(b[k], child)) {
                                        k++;
                                        j++;
                                        e++;
                                    } else {
                                        return;
                                    }
                                }
                                return;
                            }
                            if (rq.get() != Long.MIN_VALUE) {
                                if (r == e) {
                                    Object o = b[k];
                                    if (NotificationLite.isComplete(o)) {
                                        child.onComplete();
                                        return;
                                    } else if (NotificationLite.isError(o)) {
                                        child.onError(NotificationLite.getError(o));
                                        return;
                                    }
                                }
                                replaySubscription.index = j;
                                replaySubscription.currentIndexInBuffer = k;
                                replaySubscription.currentBuffer = b;
                            } else {
                                return;
                            }
                        }
                        replaySubscription.emitted = e;
                        missed = addAndGet(-missed);
                        if (missed == 0) {
                            return;
                        }
                    } else {
                        return;
                    }
                }
            }
        }
    }

    static final class CacheState<T> extends LinkedArrayList implements FlowableSubscriber<T> {
        static final ReplaySubscription[] EMPTY = new ReplaySubscription[0];
        static final ReplaySubscription[] TERMINATED = new ReplaySubscription[0];
        final AtomicReference<Subscription> connection = new AtomicReference();
        volatile boolean isConnected;
        final Flowable<T> source;
        boolean sourceDone;
        final AtomicReference<ReplaySubscription<T>[]> subscribers;

        CacheState(Flowable<T> source, int capacityHint) {
            super(capacityHint);
            this.source = source;
            this.subscribers = new AtomicReference(EMPTY);
        }

        public boolean addChild(ReplaySubscription<T> p) {
            while (true) {
                ReplaySubscription[] a = (ReplaySubscription[]) this.subscribers.get();
                if (a == TERMINATED) {
                    return false;
                }
                int n = a.length;
                ReplaySubscription<T>[] b = new ReplaySubscription[(n + 1)];
                System.arraycopy(a, 0, b, 0, n);
                b[n] = p;
                if (this.subscribers.compareAndSet(a, b)) {
                    return true;
                }
            }
        }

        public void removeChild(ReplaySubscription<T> p) {
            while (true) {
                ReplaySubscription[] a = (ReplaySubscription[]) this.subscribers.get();
                int n = a.length;
                if (n != 0) {
                    int j = -1;
                    for (int i = 0; i < n; i++) {
                        if (a[i].equals(p)) {
                            j = i;
                            break;
                        }
                    }
                    if (j >= 0) {
                        ReplaySubscription<T>[] b;
                        if (n == 1) {
                            b = EMPTY;
                        } else {
                            ReplaySubscription<T>[] b2 = new ReplaySubscription[(n - 1)];
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

        public void onSubscribe(Subscription s) {
            SubscriptionHelper.setOnce(this.connection, s, Long.MAX_VALUE);
        }

        public void connect() {
            this.source.subscribe(this);
            this.isConnected = true;
        }

        public void onNext(T t) {
            if (!this.sourceDone) {
                add(NotificationLite.next(t));
                for (ReplaySubscription<?> rp : (ReplaySubscription[]) this.subscribers.get()) {
                    rp.replay();
                }
            }
        }

        public void onError(Throwable e) {
            if (this.sourceDone) {
                RxJavaPlugins.onError(e);
                return;
            }
            this.sourceDone = true;
            add(NotificationLite.error(e));
            SubscriptionHelper.cancel(this.connection);
            for (ReplaySubscription<?> rp : (ReplaySubscription[]) this.subscribers.getAndSet(TERMINATED)) {
                rp.replay();
            }
        }

        public void onComplete() {
            if (!this.sourceDone) {
                this.sourceDone = true;
                add(NotificationLite.complete());
                SubscriptionHelper.cancel(this.connection);
                for (ReplaySubscription<?> rp : (ReplaySubscription[]) this.subscribers.getAndSet(TERMINATED)) {
                    rp.replay();
                }
            }
        }
    }

    public FlowableCache(Flowable<T> source, int capacityHint) {
        super(source);
        this.state = new CacheState(source, capacityHint);
    }

    protected void subscribeActual(Subscriber<? super T> t) {
        ReplaySubscription<T> rp = new ReplaySubscription(t, this.state);
        t.onSubscribe(rp);
        boolean doReplay = true;
        if (this.state.addChild(rp)) {
            if (rp.requested.get() == Long.MIN_VALUE) {
                this.state.removeChild(rp);
                doReplay = false;
            }
        }
        if (!this.once.get() && this.once.compareAndSet(false, true)) {
            this.state.connect();
        }
        if (doReplay) {
            rp.replay();
        }
    }

    boolean isConnected() {
        return this.state.isConnected;
    }

    boolean hasSubscribers() {
        return ((ReplaySubscription[]) this.state.subscribers.get()).length != 0;
    }

    int cachedEventCount() {
        return this.state.size();
    }
}
