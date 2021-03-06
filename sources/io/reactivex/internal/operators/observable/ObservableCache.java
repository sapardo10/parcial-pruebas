package io.reactivex.internal.operators.observable;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.SequentialDisposable;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.util.LinkedArrayList;
import io.reactivex.internal.util.NotificationLite;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public final class ObservableCache<T> extends AbstractObservableWithUpstream<T, T> {
    final AtomicBoolean once = new AtomicBoolean();
    final CacheState<T> state;

    static final class CacheState<T> extends LinkedArrayList implements Observer<T> {
        static final ReplayDisposable[] EMPTY = new ReplayDisposable[0];
        static final ReplayDisposable[] TERMINATED = new ReplayDisposable[0];
        final SequentialDisposable connection = new SequentialDisposable();
        volatile boolean isConnected;
        final AtomicReference<ReplayDisposable<T>[]> observers = new AtomicReference(EMPTY);
        final Observable<? extends T> source;
        boolean sourceDone;

        CacheState(Observable<? extends T> source, int capacityHint) {
            super(capacityHint);
            this.source = source;
        }

        public boolean addChild(ReplayDisposable<T> p) {
            while (true) {
                ReplayDisposable[] a = (ReplayDisposable[]) this.observers.get();
                if (a == TERMINATED) {
                    return false;
                }
                int n = a.length;
                ReplayDisposable<T>[] b = new ReplayDisposable[(n + 1)];
                System.arraycopy(a, 0, b, 0, n);
                b[n] = p;
                if (this.observers.compareAndSet(a, b)) {
                    return true;
                }
            }
        }

        public void removeChild(ReplayDisposable<T> p) {
            while (true) {
                ReplayDisposable[] a = (ReplayDisposable[]) this.observers.get();
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
                        ReplayDisposable<T>[] b;
                        if (n == 1) {
                            b = EMPTY;
                        } else {
                            ReplayDisposable<T>[] b2 = new ReplayDisposable[(n - 1)];
                            System.arraycopy(a, 0, b2, 0, j);
                            System.arraycopy(a, j + 1, b2, j, (n - j) - 1);
                            b = b2;
                        }
                        if (this.observers.compareAndSet(a, b)) {
                            return;
                        }
                    } else {
                        return;
                    }
                }
                return;
            }
        }

        public void onSubscribe(Disposable d) {
            this.connection.update(d);
        }

        public void connect() {
            this.source.subscribe(this);
            this.isConnected = true;
        }

        public void onNext(T t) {
            if (!this.sourceDone) {
                add(NotificationLite.next(t));
                for (ReplayDisposable<?> rp : (ReplayDisposable[]) this.observers.get()) {
                    rp.replay();
                }
            }
        }

        public void onError(Throwable e) {
            if (!this.sourceDone) {
                this.sourceDone = true;
                add(NotificationLite.error(e));
                this.connection.dispose();
                for (ReplayDisposable<?> rp : (ReplayDisposable[]) this.observers.getAndSet(TERMINATED)) {
                    rp.replay();
                }
            }
        }

        public void onComplete() {
            if (!this.sourceDone) {
                this.sourceDone = true;
                add(NotificationLite.complete());
                this.connection.dispose();
                for (ReplayDisposable<?> rp : (ReplayDisposable[]) this.observers.getAndSet(TERMINATED)) {
                    rp.replay();
                }
            }
        }
    }

    static final class ReplayDisposable<T> extends AtomicInteger implements Disposable {
        private static final long serialVersionUID = 7058506693698832024L;
        volatile boolean cancelled;
        final Observer<? super T> child;
        Object[] currentBuffer;
        int currentIndexInBuffer;
        int index;
        final CacheState<T> state;

        ReplayDisposable(Observer<? super T> child, CacheState<T> state) {
            this.child = child;
            this.state = state;
        }

        public boolean isDisposed() {
            return this.cancelled;
        }

        public void dispose() {
            if (!this.cancelled) {
                this.cancelled = true;
                this.state.removeChild(this);
            }
        }

        public void replay() {
            if (getAndIncrement() == 0) {
                Observer child = this.child;
                int missed = 1;
                while (!this.cancelled) {
                    int s = this.state.size();
                    if (s != 0) {
                        Object[] b = this.currentBuffer;
                        if (b == null) {
                            b = this.state.head();
                            this.currentBuffer = b;
                        }
                        int n = b.length - 1;
                        int j = this.index;
                        int k = this.currentIndexInBuffer;
                        while (j < s) {
                            if (!this.cancelled) {
                                if (k == n) {
                                    b = (Object[]) b[n];
                                    k = 0;
                                }
                                if (!NotificationLite.accept(b[k], child)) {
                                    k++;
                                    j++;
                                } else {
                                    return;
                                }
                            }
                            return;
                        }
                        if (!this.cancelled) {
                            this.index = j;
                            this.currentIndexInBuffer = k;
                            this.currentBuffer = b;
                        } else {
                            return;
                        }
                    }
                    missed = addAndGet(-missed);
                    if (missed == 0) {
                        return;
                    }
                }
            }
        }
    }

    public static <T> Observable<T> from(Observable<T> source) {
        return from(source, 16);
    }

    public static <T> Observable<T> from(Observable<T> source, int capacityHint) {
        ObjectHelper.verifyPositive(capacityHint, "capacityHint");
        return RxJavaPlugins.onAssembly(new ObservableCache(source, new CacheState(source, capacityHint)));
    }

    private ObservableCache(Observable<T> source, CacheState<T> state) {
        super(source);
        this.state = state;
    }

    protected void subscribeActual(Observer<? super T> t) {
        ReplayDisposable<T> rp = new ReplayDisposable(t, this.state);
        t.onSubscribe(rp);
        this.state.addChild(rp);
        if (!this.once.get() && this.once.compareAndSet(false, true)) {
            this.state.connect();
        }
        rp.replay();
    }

    boolean isConnected() {
        return this.state.isConnected;
    }

    boolean hasObservers() {
        return ((ReplayDisposable[]) this.state.observers.get()).length != 0;
    }

    int cachedEventCount() {
        return this.state.size();
    }
}
