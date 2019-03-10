package io.reactivex.internal.operators.flowable;

import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.exceptions.MissingBackpressureException;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.fuseable.SimpleQueue;
import io.reactivex.internal.queue.SpscLinkedArrayQueue;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.BackpressureHelper;
import io.reactivex.internal.util.ExceptionHelper;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class FlowableJoin<TLeft, TRight, TLeftEnd, TRightEnd, R> extends AbstractFlowableWithUpstream<TLeft, R> {
    final Function<? super TLeft, ? extends Publisher<TLeftEnd>> leftEnd;
    final Publisher<? extends TRight> other;
    final BiFunction<? super TLeft, ? super TRight, ? extends R> resultSelector;
    final Function<? super TRight, ? extends Publisher<TRightEnd>> rightEnd;

    static final class JoinSubscription<TLeft, TRight, TLeftEnd, TRightEnd, R> extends AtomicInteger implements Subscription, JoinSupport {
        static final Integer LEFT_CLOSE = Integer.valueOf(3);
        static final Integer LEFT_VALUE = Integer.valueOf(1);
        static final Integer RIGHT_CLOSE = Integer.valueOf(4);
        static final Integer RIGHT_VALUE = Integer.valueOf(2);
        private static final long serialVersionUID = -6071216598687999801L;
        final AtomicInteger active;
        volatile boolean cancelled;
        final CompositeDisposable disposables = new CompositeDisposable();
        final Subscriber<? super R> downstream;
        final AtomicReference<Throwable> error = new AtomicReference();
        final Function<? super TLeft, ? extends Publisher<TLeftEnd>> leftEnd;
        int leftIndex;
        final Map<Integer, TLeft> lefts = new LinkedHashMap();
        final SpscLinkedArrayQueue<Object> queue = new SpscLinkedArrayQueue(Flowable.bufferSize());
        final AtomicLong requested = new AtomicLong();
        final BiFunction<? super TLeft, ? super TRight, ? extends R> resultSelector;
        final Function<? super TRight, ? extends Publisher<TRightEnd>> rightEnd;
        int rightIndex;
        final Map<Integer, TRight> rights = new LinkedHashMap();

        JoinSubscription(Subscriber<? super R> actual, Function<? super TLeft, ? extends Publisher<TLeftEnd>> leftEnd, Function<? super TRight, ? extends Publisher<TRightEnd>> rightEnd, BiFunction<? super TLeft, ? super TRight, ? extends R> resultSelector) {
            this.downstream = actual;
            this.leftEnd = leftEnd;
            this.rightEnd = rightEnd;
            this.resultSelector = resultSelector;
            this.active = new AtomicInteger(2);
        }

        public void request(long n) {
            if (SubscriptionHelper.validate(n)) {
                BackpressureHelper.add(this.requested, n);
            }
        }

        public void cancel() {
            if (!this.cancelled) {
                this.cancelled = true;
                cancelAll();
                if (getAndIncrement() == 0) {
                    this.queue.clear();
                }
            }
        }

        void cancelAll() {
            this.disposables.dispose();
        }

        void errorAll(Subscriber<?> a) {
            Throwable ex = ExceptionHelper.terminate(this.error);
            this.lefts.clear();
            this.rights.clear();
            a.onError(ex);
        }

        void fail(Throwable exc, Subscriber<?> a, SimpleQueue<?> q) {
            Exceptions.throwIfFatal(exc);
            ExceptionHelper.addThrowable(this.error, exc);
            q.clear();
            cancelAll();
            errorAll(a);
        }

        void drain() {
            Throwable ex;
            Throwable exc;
            Throwable th;
            TLeft left;
            JoinSubscription joinSubscription = this;
            SpscLinkedArrayQueue<Object> q;
            Subscriber<? super R> a;
            if (getAndIncrement() == 0) {
                q = joinSubscription.queue;
                a = joinSubscription.downstream;
                int missed = 1;
                while (!joinSubscription.cancelled) {
                    if (((Throwable) joinSubscription.error.get()) != null) {
                        q.clear();
                        cancelAll();
                        errorAll(a);
                        return;
                    }
                    boolean d = joinSubscription.active.get() == 0;
                    Integer mode = (Integer) q.poll();
                    boolean empty = mode == null;
                    if (d && empty) {
                        joinSubscription.lefts.clear();
                        joinSubscription.rights.clear();
                        joinSubscription.disposables.dispose();
                        a.onComplete();
                        return;
                    } else if (empty) {
                        missed = addAndGet(-missed);
                        if (missed == 0) {
                            return;
                        }
                    } else {
                        int missed2;
                        TLeft val = q.poll();
                        int i;
                        Iterator it;
                        TRight missed3;
                        TRight right;
                        R w;
                        if (mode == LEFT_VALUE) {
                            TLeft left2 = val;
                            i = joinSubscription.leftIndex;
                            joinSubscription.leftIndex = i + 1;
                            int idx = i;
                            joinSubscription.lefts.put(Integer.valueOf(idx), left2);
                            try {
                                Publisher<TLeftEnd> p = (Publisher) ObjectHelper.requireNonNull(joinSubscription.leftEnd.apply(left2), "The leftEnd returned a null Publisher");
                                LeftRightEndSubscriber end = new LeftRightEndSubscriber(joinSubscription, true, idx);
                                joinSubscription.disposables.add(end);
                                p.subscribe(end);
                                ex = (Throwable) joinSubscription.error.get();
                                if (ex != null) {
                                    q.clear();
                                    cancelAll();
                                    errorAll(a);
                                    return;
                                }
                                long r = joinSubscription.requested.get();
                                it = joinSubscription.rights.values().iterator();
                                long e = 0;
                                while (it.hasNext()) {
                                    missed2 = missed;
                                    missed3 = it.next();
                                    Iterator it2 = it;
                                    try {
                                        right = missed3;
                                        try {
                                            w = ObjectHelper.requireNonNull(joinSubscription.resultSelector.apply(left2, missed3), "The resultSelector returned a null value");
                                            if (e != r) {
                                                a.onNext(w);
                                                e++;
                                                missed = missed2;
                                                it = it2;
                                            } else {
                                                ExceptionHelper.addThrowable(joinSubscription.error, new MissingBackpressureException("Could not emit value due to lack of requests"));
                                                q.clear();
                                                cancelAll();
                                                errorAll(a);
                                                return;
                                            }
                                        } catch (Throwable th2) {
                                            exc = th2;
                                            th = ex;
                                        }
                                    } catch (Throwable th3) {
                                        exc = th3;
                                        right = missed3;
                                        th = ex;
                                    }
                                }
                                missed2 = missed;
                                th = ex;
                                if (e != 0) {
                                    BackpressureHelper.produced(joinSubscription.requested, e);
                                }
                            } catch (Throwable exc2) {
                                missed2 = missed;
                                fail(exc2, a, q);
                                return;
                            }
                        }
                        missed2 = missed;
                        if (mode == RIGHT_VALUE) {
                            missed3 = val;
                            i = joinSubscription.rightIndex;
                            joinSubscription.rightIndex = i + 1;
                            int idx2 = i;
                            joinSubscription.rights.put(Integer.valueOf(idx2), missed3);
                            int idx3;
                            try {
                                Publisher<TRightEnd> p2 = (Publisher) ObjectHelper.requireNonNull(joinSubscription.rightEnd.apply(missed3), "The rightEnd returned a null Publisher");
                                LeftRightEndSubscriber end2 = new LeftRightEndSubscriber(joinSubscription, false, idx2);
                                joinSubscription.disposables.add(end2);
                                p2.subscribe(end2);
                                ex = (Throwable) joinSubscription.error.get();
                                if (ex != null) {
                                    q.clear();
                                    cancelAll();
                                    errorAll(a);
                                    return;
                                }
                                long r2 = joinSubscription.requested.get();
                                it = joinSubscription.lefts.values().iterator();
                                ex = 0;
                                while (it.hasNext()) {
                                    idx3 = idx2;
                                    TLeft idx4 = it.next();
                                    Iterator it3 = it;
                                    try {
                                        right = missed3;
                                        try {
                                            w = ObjectHelper.requireNonNull(joinSubscription.resultSelector.apply(idx4, missed3), "The resultSelector returned a null value");
                                            if (ex != r2) {
                                                a.onNext(w);
                                                ex++;
                                                idx2 = idx3;
                                                it = it3;
                                                missed3 = right;
                                            } else {
                                                left = idx4;
                                                ExceptionHelper.addThrowable(joinSubscription.error, new MissingBackpressureException("Could not emit value due to lack of requests"));
                                                q.clear();
                                                cancelAll();
                                                errorAll(a);
                                                return;
                                            }
                                        } catch (Throwable th4) {
                                            exc2 = th4;
                                            left = idx4;
                                        }
                                    } catch (Throwable th5) {
                                        exc2 = th5;
                                        right = missed3;
                                        left = idx4;
                                    }
                                }
                                idx3 = idx2;
                                if (ex != 0) {
                                    BackpressureHelper.produced(joinSubscription.requested, ex);
                                }
                            } catch (Throwable exc22) {
                                right = missed3;
                                idx3 = idx2;
                                fail(exc22, a, q);
                                return;
                            }
                        } else if (mode == LEFT_CLOSE) {
                            end = (LeftRightEndSubscriber) val;
                            joinSubscription.lefts.remove(Integer.valueOf(end.index));
                            joinSubscription.disposables.remove(end);
                        } else if (mode == RIGHT_CLOSE) {
                            end = (LeftRightEndSubscriber) val;
                            joinSubscription.rights.remove(Integer.valueOf(end.index));
                            joinSubscription.disposables.remove(end);
                        }
                        missed = missed2;
                    }
                }
                q.clear();
                return;
            }
            return;
            fail(exc22, a, q);
            return;
            fail(exc22, a, q);
        }

        public void innerError(Throwable ex) {
            if (ExceptionHelper.addThrowable(this.error, ex)) {
                this.active.decrementAndGet();
                drain();
                return;
            }
            RxJavaPlugins.onError(ex);
        }

        public void innerComplete(LeftRightSubscriber sender) {
            this.disposables.delete(sender);
            this.active.decrementAndGet();
            drain();
        }

        public void innerValue(boolean isLeft, Object o) {
            synchronized (this) {
                this.queue.offer(isLeft ? LEFT_VALUE : RIGHT_VALUE, o);
            }
            drain();
        }

        public void innerClose(boolean isLeft, LeftRightEndSubscriber index) {
            synchronized (this) {
                this.queue.offer(isLeft ? LEFT_CLOSE : RIGHT_CLOSE, index);
            }
            drain();
        }

        public void innerCloseError(Throwable ex) {
            if (ExceptionHelper.addThrowable(this.error, ex)) {
                drain();
            } else {
                RxJavaPlugins.onError(ex);
            }
        }
    }

    public FlowableJoin(Flowable<TLeft> source, Publisher<? extends TRight> other, Function<? super TLeft, ? extends Publisher<TLeftEnd>> leftEnd, Function<? super TRight, ? extends Publisher<TRightEnd>> rightEnd, BiFunction<? super TLeft, ? super TRight, ? extends R> resultSelector) {
        super(source);
        this.other = other;
        this.leftEnd = leftEnd;
        this.rightEnd = rightEnd;
        this.resultSelector = resultSelector;
    }

    protected void subscribeActual(Subscriber<? super R> s) {
        JoinSubscription<TLeft, TRight, TLeftEnd, TRightEnd, R> parent = new JoinSubscription(s, this.leftEnd, this.rightEnd, this.resultSelector);
        s.onSubscribe(parent);
        LeftRightSubscriber left = new LeftRightSubscriber(parent, true);
        parent.disposables.add(left);
        LeftRightSubscriber right = new LeftRightSubscriber(parent, false);
        parent.disposables.add(right);
        this.source.subscribe(left);
        this.other.subscribe(right);
    }
}
