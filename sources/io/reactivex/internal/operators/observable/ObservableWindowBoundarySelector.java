package io.reactivex.internal.operators.observable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Function;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.observers.QueueDrainObserver;
import io.reactivex.internal.queue.MpscLinkedQueue;
import io.reactivex.internal.util.NotificationLite;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.SerializedObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.subjects.UnicastSubject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public final class ObservableWindowBoundarySelector<T, B, V> extends AbstractObservableWithUpstream<T, Observable<T>> {
    final int bufferSize;
    final Function<? super B, ? extends ObservableSource<V>> close;
    final ObservableSource<B> open;

    static final class WindowOperation<T, B> {
        final B open;
        /* renamed from: w */
        final UnicastSubject<T> f30w;

        WindowOperation(UnicastSubject<T> w, B open) {
            this.f30w = w;
            this.open = open;
        }
    }

    static final class OperatorWindowBoundaryCloseObserver<T, V> extends DisposableObserver<V> {
        boolean done;
        final WindowBoundaryMainObserver<T, ?, V> parent;
        /* renamed from: w */
        final UnicastSubject<T> f58w;

        OperatorWindowBoundaryCloseObserver(WindowBoundaryMainObserver<T, ?, V> parent, UnicastSubject<T> w) {
            this.parent = parent;
            this.f58w = w;
        }

        public void onNext(V v) {
            dispose();
            onComplete();
        }

        public void onError(Throwable t) {
            if (this.done) {
                RxJavaPlugins.onError(t);
                return;
            }
            this.done = true;
            this.parent.error(t);
        }

        public void onComplete() {
            if (!this.done) {
                this.done = true;
                this.parent.close(this);
            }
        }
    }

    static final class OperatorWindowBoundaryOpenObserver<T, B> extends DisposableObserver<B> {
        final WindowBoundaryMainObserver<T, B, ?> parent;

        OperatorWindowBoundaryOpenObserver(WindowBoundaryMainObserver<T, B, ?> parent) {
            this.parent = parent;
        }

        public void onNext(B t) {
            this.parent.open(t);
        }

        public void onError(Throwable t) {
            this.parent.error(t);
        }

        public void onComplete() {
            this.parent.onComplete();
        }
    }

    static final class WindowBoundaryMainObserver<T, B, V> extends QueueDrainObserver<T, Object, Observable<T>> implements Disposable {
        final AtomicReference<Disposable> boundary = new AtomicReference();
        final int bufferSize;
        final Function<? super B, ? extends ObservableSource<V>> close;
        final ObservableSource<B> open;
        final CompositeDisposable resources;
        Disposable upstream;
        final AtomicLong windows = new AtomicLong();
        final List<UnicastSubject<T>> ws;

        WindowBoundaryMainObserver(Observer<? super Observable<T>> actual, ObservableSource<B> open, Function<? super B, ? extends ObservableSource<V>> close, int bufferSize) {
            super(actual, new MpscLinkedQueue());
            this.open = open;
            this.close = close;
            this.bufferSize = bufferSize;
            this.resources = new CompositeDisposable();
            this.ws = new ArrayList();
            this.windows.lazySet(1);
        }

        public void onSubscribe(Disposable d) {
            if (DisposableHelper.validate(this.upstream, d)) {
                this.upstream = d;
                this.downstream.onSubscribe(this);
                if (!this.cancelled) {
                    OperatorWindowBoundaryOpenObserver<T, B> os = new OperatorWindowBoundaryOpenObserver(this);
                    if (this.boundary.compareAndSet(null, os)) {
                        this.windows.getAndIncrement();
                        this.open.subscribe(os);
                    }
                }
            }
        }

        public void onNext(T t) {
            if (fastEnter()) {
                for (UnicastSubject<T> w : this.ws) {
                    w.onNext(t);
                }
                if (leave(-1) == 0) {
                    return;
                }
            } else {
                this.queue.offer(NotificationLite.next(t));
                if (!enter()) {
                    return;
                }
            }
            drainLoop();
        }

        public void onError(Throwable t) {
            if (this.done) {
                RxJavaPlugins.onError(t);
                return;
            }
            this.error = t;
            this.done = true;
            if (enter()) {
                drainLoop();
            }
            if (this.windows.decrementAndGet() == 0) {
                this.resources.dispose();
            }
            this.downstream.onError(t);
        }

        public void onComplete() {
            if (!this.done) {
                this.done = true;
                if (enter()) {
                    drainLoop();
                }
                if (this.windows.decrementAndGet() == 0) {
                    this.resources.dispose();
                }
                this.downstream.onComplete();
            }
        }

        void error(Throwable t) {
            this.upstream.dispose();
            this.resources.dispose();
            onError(t);
        }

        public void dispose() {
            this.cancelled = true;
        }

        public boolean isDisposed() {
            return this.cancelled;
        }

        void disposeBoundary() {
            this.resources.dispose();
            DisposableHelper.dispose(this.boundary);
        }

        void drainLoop() {
            MpscLinkedQueue<Object> q = this.queue;
            Observer<? super Observable<T>> a = this.downstream;
            List<UnicastSubject<T>> ws = this.ws;
            int missed = 1;
            while (true) {
                UnicastSubject<T> w;
                boolean d = this.done;
                WindowOperation<T, B> o = q.poll();
                boolean empty = o == null;
                if (d && empty) {
                    break;
                } else if (empty) {
                    missed = leave(-missed);
                    if (missed == 0) {
                        return;
                    }
                } else if (o instanceof WindowOperation) {
                    WindowOperation<T, B> wo = o;
                    if (wo.f30w != null) {
                        if (ws.remove(wo.f30w)) {
                            wo.f30w.onComplete();
                            if (this.windows.decrementAndGet() == 0) {
                                disposeBoundary();
                                return;
                            }
                        }
                    } else if (!this.cancelled) {
                        w = UnicastSubject.create(this.bufferSize);
                        ws.add(w);
                        a.onNext(w);
                        try {
                            ObservableSource<V> p = (ObservableSource) ObjectHelper.requireNonNull(this.close.apply(wo.open), "The ObservableSource supplied is null");
                            OperatorWindowBoundaryCloseObserver<T, V> cl = new OperatorWindowBoundaryCloseObserver(this, w);
                            if (this.resources.add(cl)) {
                                this.windows.getAndIncrement();
                                p.subscribe(cl);
                            }
                        } catch (Throwable e) {
                            Exceptions.throwIfFatal(e);
                            this.cancelled = true;
                            a.onError(e);
                        }
                    }
                } else {
                    for (UnicastSubject<T> w2 : ws) {
                        w2.onNext(NotificationLite.getValue(o));
                    }
                }
            }
            disposeBoundary();
            Throwable e2 = this.error;
            if (e2 != null) {
                for (UnicastSubject<T> w3 : ws) {
                    w3.onError(e2);
                }
            } else {
                for (UnicastSubject<T> w32 : ws) {
                    w32.onComplete();
                }
            }
            ws.clear();
        }

        public void accept(Observer<? super Observable<T>> observer, Object v) {
        }

        void open(B b) {
            this.queue.offer(new WindowOperation(null, b));
            if (enter()) {
                drainLoop();
            }
        }

        void close(OperatorWindowBoundaryCloseObserver<T, V> w) {
            this.resources.delete(w);
            this.queue.offer(new WindowOperation(w.f58w, null));
            if (enter()) {
                drainLoop();
            }
        }
    }

    public ObservableWindowBoundarySelector(ObservableSource<T> source, ObservableSource<B> open, Function<? super B, ? extends ObservableSource<V>> close, int bufferSize) {
        super(source);
        this.open = open;
        this.close = close;
        this.bufferSize = bufferSize;
    }

    public void subscribeActual(Observer<? super Observable<T>> t) {
        this.source.subscribe(new WindowBoundaryMainObserver(new SerializedObserver(t), this.open, this.close, this.bufferSize));
    }
}
