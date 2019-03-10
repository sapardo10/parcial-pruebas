package io.reactivex.internal.operators.parallel;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.BackpressureHelper;
import io.reactivex.parallel.ParallelFlowable;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class ParallelSortedJoin<T> extends Flowable<T> {
    final Comparator<? super T> comparator;
    final ParallelFlowable<List<T>> source;

    static final class SortedJoinSubscription<T> extends AtomicInteger implements Subscription {
        private static final long serialVersionUID = 3481980673745556697L;
        volatile boolean cancelled;
        final Comparator<? super T> comparator;
        final Subscriber<? super T> downstream;
        final AtomicReference<Throwable> error = new AtomicReference();
        final int[] indexes;
        final List<T>[] lists;
        final AtomicInteger remaining = new AtomicInteger();
        final AtomicLong requested = new AtomicLong();
        final SortedJoinInnerSubscriber<T>[] subscribers;

        SortedJoinSubscription(Subscriber<? super T> actual, int n, Comparator<? super T> comparator) {
            this.downstream = actual;
            this.comparator = comparator;
            SortedJoinInnerSubscriber<T>[] s = new SortedJoinInnerSubscriber[n];
            for (int i = 0; i < n; i++) {
                s[i] = new SortedJoinInnerSubscriber(this, i);
            }
            this.subscribers = s;
            this.lists = new List[n];
            this.indexes = new int[n];
            this.remaining.lazySet(n);
        }

        public void request(long n) {
            if (SubscriptionHelper.validate(n)) {
                BackpressureHelper.add(this.requested, n);
                if (this.remaining.get() == 0) {
                    drain();
                }
            }
        }

        public void cancel() {
            if (!this.cancelled) {
                this.cancelled = true;
                cancelAll();
                if (getAndIncrement() == 0) {
                    Arrays.fill(this.lists, null);
                }
            }
        }

        void cancelAll() {
            for (SortedJoinInnerSubscriber<T> s : this.subscribers) {
                s.cancel();
            }
        }

        void innerNext(List<T> value, int index) {
            this.lists[index] = value;
            if (this.remaining.decrementAndGet() == 0) {
                drain();
            }
        }

        void innerError(Throwable e) {
            if (this.error.compareAndSet(null, e)) {
                drain();
            } else if (e != this.error.get()) {
                RxJavaPlugins.onError(e);
            }
        }

        void drain() {
            SortedJoinSubscription sortedJoinSubscription = this;
            if (getAndIncrement() == 0) {
                Subscriber<? super T> a = sortedJoinSubscription.downstream;
                List<T>[] lists = sortedJoinSubscription.lists;
                int[] indexes = sortedJoinSubscription.indexes;
                int n = indexes.length;
                int missed = 1;
                while (true) {
                    int missed2;
                    Throwable exc;
                    long r = sortedJoinSubscription.requested.get();
                    long e = 0;
                    while (e != r) {
                        if (sortedJoinSubscription.cancelled) {
                            Arrays.fill(lists, null);
                            return;
                        }
                        Throwable ex = (Throwable) sortedJoinSubscription.error.get();
                        if (ex != null) {
                            cancelAll();
                            Arrays.fill(lists, null);
                            a.onError(ex);
                            return;
                        }
                        Throwable ex2;
                        int i = 0;
                        int minIndex = -1;
                        T min = null;
                        while (i < n) {
                            List<T> list = lists[i];
                            ex2 = ex;
                            int index = indexes[i];
                            if (list.size() == index) {
                                missed2 = missed;
                                List<T> list2 = list;
                            } else if (min == null) {
                                missed2 = missed;
                                minIndex = i;
                                min = list.get(index);
                            } else {
                                missed2 = missed;
                                T missed3 = list.get(index);
                                try {
                                    if (sortedJoinSubscription.comparator.compare(min, missed3) > 0) {
                                        min = missed3;
                                        minIndex = i;
                                    }
                                } catch (Throwable exc2) {
                                    Exceptions.throwIfFatal(exc2);
                                    cancelAll();
                                    T b = missed3;
                                    Arrays.fill(lists, null);
                                    if (sortedJoinSubscription.error.compareAndSet(null, exc2) == 0) {
                                        RxJavaPlugins.onError(exc2);
                                    }
                                    a.onError((Throwable) sortedJoinSubscription.error.get());
                                    return;
                                }
                            }
                            i++;
                            ex = ex2;
                            missed = missed2;
                        }
                        missed2 = missed;
                        ex2 = ex;
                        if (min == null) {
                            Arrays.fill(lists, null);
                            a.onComplete();
                            return;
                        }
                        a.onNext(min);
                        indexes[minIndex] = indexes[minIndex] + 1;
                        e++;
                        missed = missed2;
                    }
                    missed2 = missed;
                    if (e == r) {
                        if (sortedJoinSubscription.cancelled) {
                            Arrays.fill(lists, null);
                            return;
                        }
                        exc2 = (Throwable) sortedJoinSubscription.error.get();
                        if (exc2 != null) {
                            cancelAll();
                            Arrays.fill(lists, null);
                            a.onError(exc2);
                            return;
                        }
                        boolean empty = true;
                        for (int i2 = 0; i2 < n; i2++) {
                            if (indexes[i2] != lists[i2].size()) {
                                empty = false;
                                break;
                            }
                        }
                        if (empty) {
                            Arrays.fill(lists, null);
                            a.onComplete();
                            return;
                        }
                    }
                    if (e != 0 && r != Long.MAX_VALUE) {
                        sortedJoinSubscription.requested.addAndGet(-e);
                    }
                    int w = get();
                    missed = missed2;
                    if (w == missed) {
                        missed = addAndGet(-missed);
                        if (missed == 0) {
                            return;
                        }
                    } else {
                        missed = w;
                    }
                }
            }
        }
    }

    static final class SortedJoinInnerSubscriber<T> extends AtomicReference<Subscription> implements FlowableSubscriber<List<T>> {
        private static final long serialVersionUID = 6751017204873808094L;
        final int index;
        final SortedJoinSubscription<T> parent;

        SortedJoinInnerSubscriber(SortedJoinSubscription<T> parent, int index) {
            this.parent = parent;
            this.index = index;
        }

        public void onSubscribe(Subscription s) {
            SubscriptionHelper.setOnce(this, s, Long.MAX_VALUE);
        }

        public void onNext(List<T> t) {
            this.parent.innerNext(t, this.index);
        }

        public void onError(Throwable t) {
            this.parent.innerError(t);
        }

        public void onComplete() {
        }

        void cancel() {
            SubscriptionHelper.cancel(this);
        }
    }

    public ParallelSortedJoin(ParallelFlowable<List<T>> source, Comparator<? super T> comparator) {
        this.source = source;
        this.comparator = comparator;
    }

    protected void subscribeActual(Subscriber<? super T> s) {
        SortedJoinSubscription<T> parent = new SortedJoinSubscription(s, this.source.parallelism(), this.comparator);
        s.onSubscribe(parent);
        this.source.subscribe(parent.subscribers);
    }
}
