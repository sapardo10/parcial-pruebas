package io.reactivex.internal.operators.observable;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.fuseable.HasUpstreamObservableSource;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public final class ObservablePublish<T> extends ConnectableObservable<T> implements HasUpstreamObservableSource<T> {
    final AtomicReference<PublishObserver<T>> current;
    final ObservableSource<T> onSubscribe;
    final ObservableSource<T> source;

    static final class InnerDisposable<T> extends AtomicReference<Object> implements Disposable {
        private static final long serialVersionUID = -1100270633763673112L;
        final Observer<? super T> child;

        InnerDisposable(Observer<? super T> child) {
            this.child = child;
        }

        public boolean isDisposed() {
            return get() == this;
        }

        public void dispose() {
            InnerDisposable o = getAndSet(this);
            if (o != null && o != this) {
                ((PublishObserver) o).remove(this);
            }
        }

        void setParent(PublishObserver<T> p) {
            if (!compareAndSet(null, p)) {
                p.remove(this);
            }
        }
    }

    static final class PublishObserver<T> implements Observer<T>, Disposable {
        static final InnerDisposable[] EMPTY = new InnerDisposable[0];
        static final InnerDisposable[] TERMINATED = new InnerDisposable[0];
        final AtomicReference<PublishObserver<T>> current;
        final AtomicReference<InnerDisposable<T>[]> observers = new AtomicReference(EMPTY);
        final AtomicBoolean shouldConnect;
        final AtomicReference<Disposable> upstream = new AtomicReference();

        PublishObserver(AtomicReference<PublishObserver<T>> current) {
            this.current = current;
            this.shouldConnect = new AtomicBoolean();
        }

        public void dispose() {
            if (((InnerDisposable[]) this.observers.getAndSet(TERMINATED)) != TERMINATED) {
                this.current.compareAndSet(this, null);
                DisposableHelper.dispose(this.upstream);
            }
        }

        public boolean isDisposed() {
            return this.observers.get() == TERMINATED;
        }

        public void onSubscribe(Disposable d) {
            DisposableHelper.setOnce(this.upstream, d);
        }

        public void onNext(T t) {
            for (InnerDisposable<T> inner : (InnerDisposable[]) this.observers.get()) {
                inner.child.onNext(t);
            }
        }

        public void onError(Throwable e) {
            this.current.compareAndSet(this, null);
            InnerDisposable[] a = (InnerDisposable[]) this.observers.getAndSet(TERMINATED);
            if (a.length != 0) {
                for (InnerDisposable<T> inner : a) {
                    inner.child.onError(e);
                }
                return;
            }
            RxJavaPlugins.onError(e);
        }

        public void onComplete() {
            this.current.compareAndSet(this, null);
            for (InnerDisposable<T> inner : (InnerDisposable[]) this.observers.getAndSet(TERMINATED)) {
                inner.child.onComplete();
            }
        }

        boolean add(InnerDisposable<T> producer) {
            while (true) {
                InnerDisposable[] c = (InnerDisposable[]) this.observers.get();
                if (c == TERMINATED) {
                    return false;
                }
                int len = c.length;
                InnerDisposable<T>[] u = new InnerDisposable[(len + 1)];
                System.arraycopy(c, 0, u, 0, len);
                u[len] = producer;
                if (this.observers.compareAndSet(c, u)) {
                    return true;
                }
            }
        }

        void remove(InnerDisposable<T> producer) {
            while (true) {
                InnerDisposable[] c = (InnerDisposable[]) this.observers.get();
                int len = c.length;
                if (len != 0) {
                    int j = -1;
                    for (int i = 0; i < len; i++) {
                        if (c[i].equals(producer)) {
                            j = i;
                            break;
                        }
                    }
                    if (j >= 0) {
                        InnerDisposable<T>[] u;
                        if (len == 1) {
                            u = EMPTY;
                        } else {
                            InnerDisposable<T>[] u2 = new InnerDisposable[(len - 1)];
                            System.arraycopy(c, 0, u2, 0, j);
                            System.arraycopy(c, j + 1, u2, j, (len - j) - 1);
                            u = u2;
                        }
                        if (this.observers.compareAndSet(c, u)) {
                            return;
                        }
                    } else {
                        return;
                    }
                }
                return;
            }
        }
    }

    static final class PublishSource<T> implements ObservableSource<T> {
        private final AtomicReference<PublishObserver<T>> curr;

        PublishSource(AtomicReference<PublishObserver<T>> curr) {
            this.curr = curr;
        }

        public void subscribe(Observer<? super T> child) {
            InnerDisposable<T> inner = new InnerDisposable(child);
            child.onSubscribe(inner);
            while (true) {
                PublishObserver<T> r = (PublishObserver) this.curr.get();
                if (r != null) {
                    if (!r.isDisposed()) {
                        if (r.add(inner)) {
                            inner.setParent(r);
                            return;
                        }
                    }
                }
                PublishObserver<T> u = new PublishObserver(this.curr);
                if (this.curr.compareAndSet(r, u)) {
                    r = u;
                    if (r.add(inner)) {
                        inner.setParent(r);
                        return;
                    }
                }
            }
        }
    }

    public void connect(io.reactivex.functions.Consumer<? super io.reactivex.disposables.Disposable> r5) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:27:0x0050 in {4, 5, 8, 9, 14, 15, 21, 22, 23, 26} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r4 = this;
    L_0x0000:
        r0 = r4.current;
        r0 = r0.get();
        r0 = (io.reactivex.internal.operators.observable.ObservablePublish.PublishObserver) r0;
        if (r0 == 0) goto L_0x0012;
    L_0x000a:
        r1 = r0.isDisposed();
        if (r1 == 0) goto L_0x0011;
    L_0x0010:
        goto L_0x0012;
    L_0x0011:
        goto L_0x0023;
    L_0x0012:
        r1 = new io.reactivex.internal.operators.observable.ObservablePublish$PublishObserver;
        r2 = r4.current;
        r1.<init>(r2);
        r2 = r4.current;
        r2 = r2.compareAndSet(r0, r1);
        if (r2 != 0) goto L_0x0022;
    L_0x0021:
        goto L_0x0000;
    L_0x0022:
        r0 = r1;
    L_0x0023:
        r1 = r0.shouldConnect;
        r1 = r1.get();
        r2 = 1;
        r3 = 0;
        if (r1 != 0) goto L_0x0036;
    L_0x002d:
        r1 = r0.shouldConnect;
        r1 = r1.compareAndSet(r3, r2);
        if (r1 == 0) goto L_0x0036;
    L_0x0035:
        goto L_0x0037;
    L_0x0036:
        r2 = 0;
    L_0x0037:
        r1 = r2;
        r5.accept(r0);	 Catch:{ Throwable -> 0x0047 }
        if (r1 == 0) goto L_0x0045;
    L_0x003f:
        r2 = r4.source;
        r2.subscribe(r0);
        goto L_0x0046;
    L_0x0046:
        return;
    L_0x0047:
        r2 = move-exception;
        io.reactivex.exceptions.Exceptions.throwIfFatal(r2);
        r3 = io.reactivex.internal.util.ExceptionHelper.wrapOrThrow(r2);
        throw r3;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.observable.ObservablePublish.connect(io.reactivex.functions.Consumer):void");
    }

    public static <T> ConnectableObservable<T> create(ObservableSource<T> source) {
        AtomicReference<PublishObserver<T>> curr = new AtomicReference();
        return RxJavaPlugins.onAssembly(new ObservablePublish(new PublishSource(curr), source, curr));
    }

    private ObservablePublish(ObservableSource<T> onSubscribe, ObservableSource<T> source, AtomicReference<PublishObserver<T>> current) {
        this.onSubscribe = onSubscribe;
        this.source = source;
        this.current = current;
    }

    public ObservableSource<T> source() {
        return this.source;
    }

    protected void subscribeActual(Observer<? super T> observer) {
        this.onSubscribe.subscribe(observer);
    }
}
