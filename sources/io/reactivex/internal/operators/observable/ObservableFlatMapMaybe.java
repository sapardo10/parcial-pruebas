package io.reactivex.internal.operators.observable;

import io.reactivex.MaybeObserver;
import io.reactivex.MaybeSource;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Function;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.queue.SpscLinkedArrayQueue;
import io.reactivex.internal.util.AtomicThrowable;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public final class ObservableFlatMapMaybe<T, R> extends AbstractObservableWithUpstream<T, R> {
    final boolean delayErrors;
    final Function<? super T, ? extends MaybeSource<? extends R>> mapper;

    static final class FlatMapMaybeObserver<T, R> extends AtomicInteger implements Observer<T>, Disposable {
        private static final long serialVersionUID = 8600231336733376951L;
        final AtomicInteger active = new AtomicInteger(1);
        volatile boolean cancelled;
        final boolean delayErrors;
        final Observer<? super R> downstream;
        final AtomicThrowable errors = new AtomicThrowable();
        final Function<? super T, ? extends MaybeSource<? extends R>> mapper;
        final AtomicReference<SpscLinkedArrayQueue<R>> queue = new AtomicReference();
        final CompositeDisposable set = new CompositeDisposable();
        Disposable upstream;

        final class InnerObserver extends AtomicReference<Disposable> implements MaybeObserver<R>, Disposable {
            private static final long serialVersionUID = -502562646270949838L;

            InnerObserver() {
            }

            public void onSubscribe(Disposable d) {
                DisposableHelper.setOnce(this, d);
            }

            public void onSuccess(R value) {
                FlatMapMaybeObserver.this.innerSuccess(this, value);
            }

            public void onError(Throwable e) {
                FlatMapMaybeObserver.this.innerError(this, e);
            }

            public void onComplete() {
                FlatMapMaybeObserver.this.innerComplete(this);
            }

            public boolean isDisposed() {
                return DisposableHelper.isDisposed((Disposable) get());
            }

            public void dispose() {
                DisposableHelper.dispose(this);
            }
        }

        FlatMapMaybeObserver(Observer<? super R> actual, Function<? super T, ? extends MaybeSource<? extends R>> mapper, boolean delayErrors) {
            this.downstream = actual;
            this.mapper = mapper;
            this.delayErrors = delayErrors;
        }

        public void onSubscribe(Disposable d) {
            if (DisposableHelper.validate(this.upstream, d)) {
                this.upstream = d;
                this.downstream.onSubscribe(this);
            }
        }

        public void onNext(T t) {
            try {
                MaybeSource<? extends R> ms = (MaybeSource) ObjectHelper.requireNonNull(this.mapper.apply(t), "The mapper returned a null MaybeSource");
                this.active.getAndIncrement();
                InnerObserver inner = new InnerObserver();
                if (!this.cancelled && this.set.add(inner)) {
                    ms.subscribe(inner);
                }
            } catch (Throwable ex) {
                Exceptions.throwIfFatal(ex);
                this.upstream.dispose();
                onError(ex);
            }
        }

        public void onError(Throwable t) {
            this.active.decrementAndGet();
            if (this.errors.addThrowable(t)) {
                if (!this.delayErrors) {
                    this.set.dispose();
                }
                drain();
                return;
            }
            RxJavaPlugins.onError(t);
        }

        public void onComplete() {
            this.active.decrementAndGet();
            drain();
        }

        public void dispose() {
            this.cancelled = true;
            this.upstream.dispose();
            this.set.dispose();
        }

        public boolean isDisposed() {
            return this.cancelled;
        }

        void innerSuccess(InnerObserver inner, R value) {
            this.set.delete(inner);
            if (get() == 0) {
                boolean d = true;
                if (compareAndSet(0, 1)) {
                    this.downstream.onNext(value);
                    if (this.active.decrementAndGet() != 0) {
                        d = false;
                    }
                    SpscLinkedArrayQueue<R> q = (SpscLinkedArrayQueue) this.queue.get();
                    if (d && (q == null || q.isEmpty())) {
                        Throwable ex = this.errors.terminate();
                        if (ex != null) {
                            this.downstream.onError(ex);
                        } else {
                            this.downstream.onComplete();
                        }
                        return;
                    } else if (decrementAndGet() != 0) {
                        drainLoop();
                    } else {
                        return;
                    }
                }
            }
            SpscLinkedArrayQueue<R> q2 = getOrCreateQueue();
            synchronized (q2) {
                q2.offer(value);
            }
            this.active.decrementAndGet();
            if (getAndIncrement() == 0) {
                drainLoop();
            }
        }

        SpscLinkedArrayQueue<R> getOrCreateQueue() {
            while (true) {
                SpscLinkedArrayQueue<R> current = (SpscLinkedArrayQueue) this.queue.get();
                if (current != null) {
                    return current;
                }
                current = new SpscLinkedArrayQueue(Observable.bufferSize());
                if (this.queue.compareAndSet(null, current)) {
                    return current;
                }
            }
        }

        void innerError(InnerObserver inner, Throwable e) {
            this.set.delete(inner);
            if (this.errors.addThrowable(e)) {
                if (!this.delayErrors) {
                    this.upstream.dispose();
                    this.set.dispose();
                }
                this.active.decrementAndGet();
                drain();
                return;
            }
            RxJavaPlugins.onError(e);
        }

        void innerComplete(InnerObserver inner) {
            this.set.delete(inner);
            if (get() == 0) {
                boolean d = true;
                if (compareAndSet(0, 1)) {
                    if (this.active.decrementAndGet() != 0) {
                        d = false;
                    }
                    SpscLinkedArrayQueue<R> q = (SpscLinkedArrayQueue) this.queue.get();
                    if (d && (q == null || q.isEmpty())) {
                        Throwable ex = this.errors.terminate();
                        if (ex != null) {
                            this.downstream.onError(ex);
                        } else {
                            this.downstream.onComplete();
                        }
                        return;
                    } else if (decrementAndGet() != 0) {
                        drainLoop();
                    } else {
                        return;
                    }
                }
            }
            this.active.decrementAndGet();
            drain();
        }

        void drain() {
            if (getAndIncrement() == 0) {
                drainLoop();
            }
        }

        void clear() {
            SpscLinkedArrayQueue<R> q = (SpscLinkedArrayQueue) this.queue.get();
            if (q != null) {
                q.clear();
            }
        }

        void drainLoop() {
            int missed = 1;
            Observer<? super R> a = this.downstream;
            AtomicInteger n = this.active;
            AtomicReference<SpscLinkedArrayQueue<R>> qr = this.queue;
            while (!this.cancelled) {
                if (!this.delayErrors) {
                    if (((Throwable) this.errors.get()) != null) {
                        Throwable ex = this.errors.terminate();
                        clear();
                        a.onError(ex);
                        return;
                    }
                }
                boolean empty = true;
                boolean d = n.get() == 0;
                SpscLinkedArrayQueue<R> q = (SpscLinkedArrayQueue) qr.get();
                R v = q != null ? q.poll() : null;
                if (v != null) {
                    empty = false;
                }
                if (d && empty) {
                    Throwable ex2 = this.errors.terminate();
                    if (ex2 != null) {
                        a.onError(ex2);
                    } else {
                        a.onComplete();
                    }
                    return;
                } else if (empty) {
                    missed = addAndGet(-missed);
                    if (missed == 0) {
                        return;
                    }
                } else {
                    a.onNext(v);
                }
            }
            clear();
        }
    }

    public ObservableFlatMapMaybe(ObservableSource<T> source, Function<? super T, ? extends MaybeSource<? extends R>> mapper, boolean delayError) {
        super(source);
        this.mapper = mapper;
        this.delayErrors = delayError;
    }

    protected void subscribeActual(Observer<? super R> observer) {
        this.source.subscribe(new FlatMapMaybeObserver(observer, this.mapper, this.delayErrors));
    }
}
