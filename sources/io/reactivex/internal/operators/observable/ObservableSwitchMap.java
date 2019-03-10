package io.reactivex.internal.operators.observable;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Function;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.fuseable.QueueDisposable;
import io.reactivex.internal.fuseable.SimpleQueue;
import io.reactivex.internal.queue.SpscLinkedArrayQueue;
import io.reactivex.internal.util.AtomicThrowable;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public final class ObservableSwitchMap<T, R> extends AbstractObservableWithUpstream<T, R> {
    final int bufferSize;
    final boolean delayErrors;
    final Function<? super T, ? extends ObservableSource<? extends R>> mapper;

    static final class SwitchMapInnerObserver<T, R> extends AtomicReference<Disposable> implements Observer<R> {
        private static final long serialVersionUID = 3837284832786408377L;
        final int bufferSize;
        volatile boolean done;
        final long index;
        final SwitchMapObserver<T, R> parent;
        volatile SimpleQueue<R> queue;

        SwitchMapInnerObserver(SwitchMapObserver<T, R> parent, long index, int bufferSize) {
            this.parent = parent;
            this.index = index;
            this.bufferSize = bufferSize;
        }

        public void onSubscribe(Disposable d) {
            if (DisposableHelper.setOnce(this, d)) {
                if (d instanceof QueueDisposable) {
                    QueueDisposable<R> qd = (QueueDisposable) d;
                    int m = qd.requestFusion(7);
                    if (m == 1) {
                        this.queue = qd;
                        this.done = true;
                        this.parent.drain();
                        return;
                    } else if (m == 2) {
                        this.queue = qd;
                        return;
                    }
                }
                this.queue = new SpscLinkedArrayQueue(this.bufferSize);
            }
        }

        public void onNext(R t) {
            if (this.index == this.parent.unique) {
                if (t != null) {
                    this.queue.offer(t);
                }
                this.parent.drain();
            }
        }

        public void onError(Throwable t) {
            this.parent.innerError(this, t);
        }

        public void onComplete() {
            if (this.index == this.parent.unique) {
                this.done = true;
                this.parent.drain();
            }
        }

        public void cancel() {
            DisposableHelper.dispose(this);
        }
    }

    static final class SwitchMapObserver<T, R> extends AtomicInteger implements Observer<T>, Disposable {
        static final SwitchMapInnerObserver<Object, Object> CANCELLED = new SwitchMapInnerObserver(null, -1, 1);
        private static final long serialVersionUID = -3491074160481096299L;
        final AtomicReference<SwitchMapInnerObserver<T, R>> active = new AtomicReference();
        final int bufferSize;
        volatile boolean cancelled;
        final boolean delayErrors;
        volatile boolean done;
        final Observer<? super R> downstream;
        final AtomicThrowable errors;
        final Function<? super T, ? extends ObservableSource<? extends R>> mapper;
        volatile long unique;
        Disposable upstream;

        static {
            CANCELLED.cancel();
        }

        SwitchMapObserver(Observer<? super R> actual, Function<? super T, ? extends ObservableSource<? extends R>> mapper, int bufferSize, boolean delayErrors) {
            this.downstream = actual;
            this.mapper = mapper;
            this.bufferSize = bufferSize;
            this.delayErrors = delayErrors;
            this.errors = new AtomicThrowable();
        }

        public void onSubscribe(Disposable d) {
            if (DisposableHelper.validate(this.upstream, d)) {
                this.upstream = d;
                this.downstream.onSubscribe(this);
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onNext(T r7) {
            /*
            r6 = this;
            r0 = r6.unique;
            r2 = 1;
            r0 = r0 + r2;
            r6.unique = r0;
            r2 = r6.active;
            r2 = r2.get();
            r2 = (io.reactivex.internal.operators.observable.ObservableSwitchMap.SwitchMapInnerObserver) r2;
            if (r2 == 0) goto L_0x0015;
        L_0x0011:
            r2.cancel();
            goto L_0x0016;
        L_0x0016:
            r3 = r6.mapper;	 Catch:{ Throwable -> 0x0048 }
            r3 = r3.apply(r7);	 Catch:{ Throwable -> 0x0048 }
            r4 = "The ObservableSource returned is null";
            r3 = io.reactivex.internal.functions.ObjectHelper.requireNonNull(r3, r4);	 Catch:{ Throwable -> 0x0048 }
            r3 = (io.reactivex.ObservableSource) r3;	 Catch:{ Throwable -> 0x0048 }
            r4 = new io.reactivex.internal.operators.observable.ObservableSwitchMap$SwitchMapInnerObserver;
            r5 = r6.bufferSize;
            r4.<init>(r6, r0, r5);
        L_0x002c:
            r5 = r6.active;
            r5 = r5.get();
            r2 = r5;
            r2 = (io.reactivex.internal.operators.observable.ObservableSwitchMap.SwitchMapInnerObserver) r2;
            r5 = CANCELLED;
            if (r2 != r5) goto L_0x003a;
        L_0x0039:
            goto L_0x0046;
        L_0x003a:
            r5 = r6.active;
            r5 = r5.compareAndSet(r2, r4);
            if (r5 == 0) goto L_0x0047;
        L_0x0042:
            r3.subscribe(r4);
        L_0x0046:
            return;
        L_0x0047:
            goto L_0x002c;
        L_0x0048:
            r3 = move-exception;
            io.reactivex.exceptions.Exceptions.throwIfFatal(r3);
            r4 = r6.upstream;
            r4.dispose();
            r6.onError(r3);
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.observable.ObservableSwitchMap.SwitchMapObserver.onNext(java.lang.Object):void");
        }

        public void onError(Throwable t) {
            if (this.done || !this.errors.addThrowable(t)) {
                RxJavaPlugins.onError(t);
                return;
            }
            if (!this.delayErrors) {
                disposeInner();
            }
            this.done = true;
            drain();
        }

        public void onComplete() {
            if (!this.done) {
                this.done = true;
                drain();
            }
        }

        public void dispose() {
            if (!this.cancelled) {
                this.cancelled = true;
                this.upstream.dispose();
                disposeInner();
            }
        }

        public boolean isDisposed() {
            return this.cancelled;
        }

        void disposeInner() {
            SwitchMapInnerObserver<T, R> a = (SwitchMapInnerObserver) this.active.get();
            SwitchMapInnerObserver<T, R> switchMapInnerObserver = CANCELLED;
            if (a != switchMapInnerObserver) {
                a = (SwitchMapInnerObserver) this.active.getAndSet(switchMapInnerObserver);
                if (a != CANCELLED && a != null) {
                    a.cancel();
                }
            }
        }

        void drain() {
            if (getAndIncrement() == 0) {
                Observer<? super R> a = this.downstream;
                AtomicReference<SwitchMapInnerObserver<T, R>> active = this.active;
                boolean delayErrors = this.delayErrors;
                int missing = 1;
                while (!this.cancelled) {
                    if (this.done) {
                        boolean empty = active.get() == null;
                        if (delayErrors) {
                            if (empty) {
                                Throwable ex = (Throwable) this.errors.get();
                                if (ex != null) {
                                    a.onError(ex);
                                } else {
                                    a.onComplete();
                                }
                                return;
                            }
                        } else if (((Throwable) this.errors.get()) != null) {
                            a.onError(this.errors.terminate());
                            return;
                        } else if (empty) {
                            a.onComplete();
                            return;
                        }
                    }
                    SwitchMapInnerObserver<T, R> inner = (SwitchMapInnerObserver) active.get();
                    if (inner != null) {
                        SimpleQueue<R> q = inner.queue;
                        if (q != null) {
                            boolean empty2;
                            if (inner.done) {
                                empty2 = q.isEmpty();
                                if (delayErrors) {
                                    if (empty2) {
                                        active.compareAndSet(inner, null);
                                    }
                                } else if (((Throwable) this.errors.get()) != null) {
                                    a.onError(this.errors.terminate());
                                    return;
                                } else if (empty2) {
                                    active.compareAndSet(inner, null);
                                }
                            }
                            empty2 = false;
                            while (!this.cancelled) {
                                boolean retry;
                                if (inner != active.get()) {
                                    retry = true;
                                } else {
                                    R v;
                                    if (!delayErrors) {
                                        if (((Throwable) this.errors.get()) != null) {
                                            a.onError(this.errors.terminate());
                                            return;
                                        }
                                    }
                                    boolean d = inner.done;
                                    try {
                                        v = q.poll();
                                    } catch (Throwable ex2) {
                                        Exceptions.throwIfFatal(ex2);
                                        this.errors.addThrowable(ex2);
                                        active.compareAndSet(inner, null);
                                        if (delayErrors) {
                                            inner.cancel();
                                        } else {
                                            disposeInner();
                                            this.upstream.dispose();
                                            this.done = true;
                                        }
                                        empty2 = true;
                                        v = null;
                                    }
                                    boolean empty3 = v == null;
                                    if (d && empty3) {
                                        active.compareAndSet(inner, null);
                                        retry = true;
                                    } else if (empty3) {
                                        retry = empty2;
                                    } else {
                                        a.onNext(v);
                                    }
                                }
                                if (retry) {
                                }
                            }
                            return;
                        }
                    }
                    missing = addAndGet(-missing);
                    if (missing == 0) {
                        return;
                    }
                }
            }
        }

        void innerError(SwitchMapInnerObserver<T, R> inner, Throwable ex) {
            if (inner.index == this.unique && this.errors.addThrowable(ex)) {
                if (!this.delayErrors) {
                    this.upstream.dispose();
                }
                inner.done = true;
                drain();
                return;
            }
            RxJavaPlugins.onError(ex);
        }
    }

    public ObservableSwitchMap(ObservableSource<T> source, Function<? super T, ? extends ObservableSource<? extends R>> mapper, int bufferSize, boolean delayErrors) {
        super(source);
        this.mapper = mapper;
        this.bufferSize = bufferSize;
        this.delayErrors = delayErrors;
    }

    public void subscribeActual(Observer<? super R> t) {
        if (!ObservableScalarXMap.tryScalarXMapSubscribe(this.source, t, this.mapper)) {
            this.source.subscribe(new SwitchMapObserver(t, this.mapper, this.bufferSize, this.delayErrors));
        }
    }
}
