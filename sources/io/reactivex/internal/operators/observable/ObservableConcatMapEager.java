package io.reactivex.internal.operators.observable;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Function;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.fuseable.QueueDisposable;
import io.reactivex.internal.fuseable.SimpleQueue;
import io.reactivex.internal.observers.InnerQueuedObserver;
import io.reactivex.internal.observers.InnerQueuedObserverSupport;
import io.reactivex.internal.queue.SpscLinkedArrayQueue;
import io.reactivex.internal.util.AtomicThrowable;
import io.reactivex.internal.util.ErrorMode;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.ArrayDeque;
import java.util.concurrent.atomic.AtomicInteger;

public final class ObservableConcatMapEager<T, R> extends AbstractObservableWithUpstream<T, R> {
    final ErrorMode errorMode;
    final Function<? super T, ? extends ObservableSource<? extends R>> mapper;
    final int maxConcurrency;
    final int prefetch;

    static final class ConcatMapEagerMainObserver<T, R> extends AtomicInteger implements Observer<T>, Disposable, InnerQueuedObserverSupport<R> {
        private static final long serialVersionUID = 8080567949447303262L;
        int activeCount;
        volatile boolean cancelled;
        InnerQueuedObserver<R> current;
        volatile boolean done;
        final Observer<? super R> downstream;
        final AtomicThrowable error = new AtomicThrowable();
        final ErrorMode errorMode;
        final Function<? super T, ? extends ObservableSource<? extends R>> mapper;
        final int maxConcurrency;
        final ArrayDeque<InnerQueuedObserver<R>> observers = new ArrayDeque();
        final int prefetch;
        SimpleQueue<T> queue;
        int sourceMode;
        Disposable upstream;

        ConcatMapEagerMainObserver(Observer<? super R> actual, Function<? super T, ? extends ObservableSource<? extends R>> mapper, int maxConcurrency, int prefetch, ErrorMode errorMode) {
            this.downstream = actual;
            this.mapper = mapper;
            this.maxConcurrency = maxConcurrency;
            this.prefetch = prefetch;
            this.errorMode = errorMode;
        }

        public void onSubscribe(Disposable d) {
            if (DisposableHelper.validate(this.upstream, d)) {
                this.upstream = d;
                if (d instanceof QueueDisposable) {
                    QueueDisposable<T> qd = (QueueDisposable) d;
                    int m = qd.requestFusion(3);
                    if (m == 1) {
                        this.sourceMode = m;
                        this.queue = qd;
                        this.done = true;
                        this.downstream.onSubscribe(this);
                        drain();
                        return;
                    } else if (m == 2) {
                        this.sourceMode = m;
                        this.queue = qd;
                        this.downstream.onSubscribe(this);
                        return;
                    }
                }
                this.queue = new SpscLinkedArrayQueue(this.prefetch);
                this.downstream.onSubscribe(this);
            }
        }

        public void onNext(T value) {
            if (this.sourceMode == 0) {
                this.queue.offer(value);
            }
            drain();
        }

        public void onError(Throwable e) {
            if (this.error.addThrowable(e)) {
                this.done = true;
                drain();
                return;
            }
            RxJavaPlugins.onError(e);
        }

        public void onComplete() {
            this.done = true;
            drain();
        }

        public void dispose() {
            this.cancelled = true;
            if (getAndIncrement() == 0) {
                this.queue.clear();
                disposeAll();
            }
        }

        public boolean isDisposed() {
            return this.cancelled;
        }

        void disposeAll() {
            InnerQueuedObserver<R> inner = this.current;
            if (inner != null) {
                inner.dispose();
            }
            while (true) {
                inner = (InnerQueuedObserver) this.observers.poll();
                if (inner != null) {
                    inner.dispose();
                } else {
                    return;
                }
            }
        }

        public void innerNext(InnerQueuedObserver<R> inner, R value) {
            inner.queue().offer(value);
            drain();
        }

        public void innerError(InnerQueuedObserver<R> inner, Throwable e) {
            if (this.error.addThrowable(e)) {
                if (this.errorMode == ErrorMode.IMMEDIATE) {
                    this.upstream.dispose();
                }
                inner.setDone();
                drain();
                return;
            }
            RxJavaPlugins.onError(e);
        }

        public void innerComplete(InnerQueuedObserver<R> inner) {
            inner.setDone();
            drain();
        }

        public void drain() {
            if (getAndIncrement() == 0) {
                int missed = 1;
                SimpleQueue<T> q = this.queue;
                ArrayDeque<InnerQueuedObserver<R>> observers = this.observers;
                Observer<? super R> a = this.downstream;
                ErrorMode errorMode = this.errorMode;
                while (true) {
                    int ac = this.activeCount;
                    while (ac != this.maxConcurrency) {
                        if (this.cancelled) {
                            q.clear();
                            disposeAll();
                            return;
                        }
                        if (errorMode == ErrorMode.IMMEDIATE) {
                            if (((Throwable) this.error.get()) != null) {
                                q.clear();
                                disposeAll();
                                a.onError(this.error.terminate());
                                return;
                            }
                        }
                        try {
                            T v = q.poll();
                            if (v == null) {
                                break;
                            }
                            ObservableSource<? extends R> source = (ObservableSource) ObjectHelper.requireNonNull(this.mapper.apply(v), "The mapper returned a null ObservableSource");
                            InnerQueuedObserver<R> inner = new InnerQueuedObserver(this, this.prefetch);
                            observers.offer(inner);
                            source.subscribe(inner);
                            ac++;
                        } catch (Throwable ex) {
                            Exceptions.throwIfFatal(ex);
                            this.upstream.dispose();
                            q.clear();
                            disposeAll();
                            this.error.addThrowable(ex);
                            a.onError(this.error.terminate());
                            return;
                        }
                    }
                    this.activeCount = ac;
                    if (this.cancelled) {
                        q.clear();
                        disposeAll();
                        return;
                    }
                    boolean empty;
                    if (errorMode == ErrorMode.IMMEDIATE) {
                        if (((Throwable) this.error.get()) != null) {
                            q.clear();
                            disposeAll();
                            a.onError(this.error.terminate());
                            return;
                        }
                    }
                    InnerQueuedObserver<R> active = this.current;
                    if (active == null) {
                        if (errorMode == ErrorMode.BOUNDARY) {
                            if (((Throwable) this.error.get()) != null) {
                                q.clear();
                                disposeAll();
                                a.onError(this.error.terminate());
                                return;
                            }
                        }
                        boolean d = this.done;
                        active = (InnerQueuedObserver) observers.poll();
                        empty = active == null;
                        if (d && empty) {
                            break;
                        } else if (!empty) {
                            this.current = active;
                        }
                    }
                    if (active != null) {
                        SimpleQueue<R> aq = active.queue();
                        while (!this.cancelled) {
                            empty = active.isDone();
                            if (errorMode == ErrorMode.IMMEDIATE) {
                                if (((Throwable) this.error.get()) != null) {
                                    q.clear();
                                    disposeAll();
                                    a.onError(this.error.terminate());
                                    return;
                                }
                            }
                            try {
                                R w = aq.poll();
                                boolean empty2 = w == null;
                                if (empty && empty2) {
                                    this.current = null;
                                    this.activeCount--;
                                } else if (!empty2) {
                                    a.onNext(w);
                                }
                            } catch (Throwable ex2) {
                                Exceptions.throwIfFatal(ex2);
                                this.error.addThrowable(ex2);
                                this.current = null;
                                this.activeCount--;
                            }
                        }
                        q.clear();
                        disposeAll();
                        return;
                    }
                    missed = addAndGet(-missed);
                    if (missed == 0) {
                        return;
                    }
                }
                if (((Throwable) this.error.get()) != null) {
                    q.clear();
                    disposeAll();
                    a.onError(this.error.terminate());
                } else {
                    a.onComplete();
                }
            }
        }
    }

    public ObservableConcatMapEager(ObservableSource<T> source, Function<? super T, ? extends ObservableSource<? extends R>> mapper, ErrorMode errorMode, int maxConcurrency, int prefetch) {
        super(source);
        this.mapper = mapper;
        this.errorMode = errorMode;
        this.maxConcurrency = maxConcurrency;
        this.prefetch = prefetch;
    }

    protected void subscribeActual(Observer<? super R> observer) {
        this.source.subscribe(new ConcatMapEagerMainObserver(observer, this.mapper, this.maxConcurrency, this.prefetch, this.errorMode));
    }
}
