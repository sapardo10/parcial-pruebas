package io.reactivex.internal.operators.mixed;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.CompletableSource;
import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.exceptions.MissingBackpressureException;
import io.reactivex.functions.Function;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.fuseable.SimplePlainQueue;
import io.reactivex.internal.queue.SpscArrayQueue;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.AtomicThrowable;
import io.reactivex.internal.util.ErrorMode;
import io.reactivex.internal.util.ExceptionHelper;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Subscription;

public final class FlowableConcatMapCompletable<T> extends Completable {
    final ErrorMode errorMode;
    final Function<? super T, ? extends CompletableSource> mapper;
    final int prefetch;
    final Flowable<T> source;

    static final class ConcatMapCompletableObserver<T> extends AtomicInteger implements FlowableSubscriber<T>, Disposable {
        private static final long serialVersionUID = 3610901111000061034L;
        volatile boolean active;
        int consumed;
        volatile boolean disposed;
        volatile boolean done;
        final CompletableObserver downstream;
        final ErrorMode errorMode;
        final AtomicThrowable errors = new AtomicThrowable();
        final ConcatMapInnerObserver inner = new ConcatMapInnerObserver(this);
        final Function<? super T, ? extends CompletableSource> mapper;
        final int prefetch;
        final SimplePlainQueue<T> queue;
        Subscription upstream;

        static final class ConcatMapInnerObserver extends AtomicReference<Disposable> implements CompletableObserver {
            private static final long serialVersionUID = 5638352172918776687L;
            final ConcatMapCompletableObserver<?> parent;

            ConcatMapInnerObserver(ConcatMapCompletableObserver<?> parent) {
                this.parent = parent;
            }

            public void onSubscribe(Disposable d) {
                DisposableHelper.replace(this, d);
            }

            public void onError(Throwable e) {
                this.parent.innerError(e);
            }

            public void onComplete() {
                this.parent.innerComplete();
            }

            void dispose() {
                DisposableHelper.dispose(this);
            }
        }

        ConcatMapCompletableObserver(CompletableObserver downstream, Function<? super T, ? extends CompletableSource> mapper, ErrorMode errorMode, int prefetch) {
            this.downstream = downstream;
            this.mapper = mapper;
            this.errorMode = errorMode;
            this.prefetch = prefetch;
            this.queue = new SpscArrayQueue(prefetch);
        }

        public void onSubscribe(Subscription s) {
            if (SubscriptionHelper.validate(this.upstream, s)) {
                this.upstream = s;
                this.downstream.onSubscribe(this);
                s.request((long) this.prefetch);
            }
        }

        public void onNext(T t) {
            if (this.queue.offer(t)) {
                drain();
                return;
            }
            this.upstream.cancel();
            onError(new MissingBackpressureException("Queue full?!"));
        }

        public void onError(Throwable t) {
            if (!this.errors.addThrowable(t)) {
                RxJavaPlugins.onError(t);
            } else if (this.errorMode == ErrorMode.IMMEDIATE) {
                this.inner.dispose();
                t = this.errors.terminate();
                if (t != ExceptionHelper.TERMINATED) {
                    this.downstream.onError(t);
                }
                if (getAndIncrement() == 0) {
                    this.queue.clear();
                }
            } else {
                this.done = true;
                drain();
            }
        }

        public void onComplete() {
            this.done = true;
            drain();
        }

        public void dispose() {
            this.disposed = true;
            this.upstream.cancel();
            this.inner.dispose();
            if (getAndIncrement() == 0) {
                this.queue.clear();
            }
        }

        public boolean isDisposed() {
            return this.disposed;
        }

        void innerError(Throwable ex) {
            if (!this.errors.addThrowable(ex)) {
                RxJavaPlugins.onError(ex);
            } else if (this.errorMode == ErrorMode.IMMEDIATE) {
                this.upstream.cancel();
                ex = this.errors.terminate();
                if (ex != ExceptionHelper.TERMINATED) {
                    this.downstream.onError(ex);
                }
                if (getAndIncrement() == 0) {
                    this.queue.clear();
                }
            } else {
                this.active = false;
                drain();
            }
        }

        void innerComplete() {
            this.active = false;
            drain();
        }

        void drain() {
            if (getAndIncrement() == 0) {
                while (!this.disposed) {
                    if (!this.active) {
                        if (this.errorMode == ErrorMode.BOUNDARY) {
                            if (this.errors.get() != null) {
                                this.queue.clear();
                                this.downstream.onError(this.errors.terminate());
                                return;
                            }
                        }
                        boolean d = this.done;
                        T v = this.queue.poll();
                        boolean empty = v == null;
                        Throwable ex;
                        if (d && empty) {
                            ex = this.errors.terminate();
                            if (ex != null) {
                                this.downstream.onError(ex);
                            } else {
                                this.downstream.onComplete();
                            }
                            return;
                        } else if (!empty) {
                            int limit = this.prefetch;
                            limit -= limit >> 1;
                            int c = this.consumed + 1;
                            if (c == limit) {
                                this.consumed = 0;
                                this.upstream.request((long) limit);
                            } else {
                                this.consumed = c;
                            }
                            try {
                                CompletableSource cs = (CompletableSource) ObjectHelper.requireNonNull(this.mapper.apply(v), "The mapper returned a null CompletableSource");
                                this.active = true;
                                cs.subscribe(this.inner);
                            } catch (Throwable ex2) {
                                Exceptions.throwIfFatal(ex2);
                                this.queue.clear();
                                this.upstream.cancel();
                                this.errors.addThrowable(ex2);
                                this.downstream.onError(this.errors.terminate());
                                return;
                            }
                        }
                    }
                    if (decrementAndGet() == 0) {
                        return;
                    }
                }
                this.queue.clear();
            }
        }
    }

    public FlowableConcatMapCompletable(Flowable<T> source, Function<? super T, ? extends CompletableSource> mapper, ErrorMode errorMode, int prefetch) {
        this.source = source;
        this.mapper = mapper;
        this.errorMode = errorMode;
        this.prefetch = prefetch;
    }

    protected void subscribeActual(CompletableObserver observer) {
        this.source.subscribe(new ConcatMapCompletableObserver(observer, this.mapper, this.errorMode, this.prefetch));
    }
}
