package io.reactivex.internal.operators.observable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Function;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.queue.SpscLinkedArrayQueue;
import io.reactivex.internal.util.AtomicThrowable;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public final class ObservableCombineLatest<T, R> extends Observable<R> {
    final int bufferSize;
    final Function<? super Object[], ? extends R> combiner;
    final boolean delayError;
    final ObservableSource<? extends T>[] sources;
    final Iterable<? extends ObservableSource<? extends T>> sourcesIterable;

    static final class CombinerObserver<T, R> extends AtomicReference<Disposable> implements Observer<T> {
        private static final long serialVersionUID = -4823716997131257941L;
        final int index;
        final LatestCoordinator<T, R> parent;

        CombinerObserver(LatestCoordinator<T, R> parent, int index) {
            this.parent = parent;
            this.index = index;
        }

        public void onSubscribe(Disposable d) {
            DisposableHelper.setOnce(this, d);
        }

        public void onNext(T t) {
            this.parent.innerNext(this.index, t);
        }

        public void onError(Throwable t) {
            this.parent.innerError(this.index, t);
        }

        public void onComplete() {
            this.parent.innerComplete(this.index);
        }

        public void dispose() {
            DisposableHelper.dispose(this);
        }
    }

    static final class LatestCoordinator<T, R> extends AtomicInteger implements Disposable {
        private static final long serialVersionUID = 8567835998786448817L;
        int active;
        volatile boolean cancelled;
        final Function<? super Object[], ? extends R> combiner;
        int complete;
        final boolean delayError;
        volatile boolean done;
        final Observer<? super R> downstream;
        final AtomicThrowable errors = new AtomicThrowable();
        Object[] latest;
        final CombinerObserver<T, R>[] observers;
        final SpscLinkedArrayQueue<Object[]> queue;

        LatestCoordinator(Observer<? super R> actual, Function<? super Object[], ? extends R> combiner, int count, int bufferSize, boolean delayError) {
            this.downstream = actual;
            this.combiner = combiner;
            this.delayError = delayError;
            this.latest = new Object[count];
            CombinerObserver<T, R>[] as = new CombinerObserver[count];
            for (int i = 0; i < count; i++) {
                as[i] = new CombinerObserver(this, i);
            }
            this.observers = as;
            this.queue = new SpscLinkedArrayQueue(bufferSize);
        }

        public void subscribe(ObservableSource<? extends T>[] sources) {
            Observer<T>[] as = this.observers;
            int len = as.length;
            this.downstream.onSubscribe(this);
            int i = 0;
            while (i < len) {
                if (!this.done) {
                    if (!this.cancelled) {
                        sources[i].subscribe(as[i]);
                        i++;
                    }
                }
                return;
            }
        }

        public void dispose() {
            if (!this.cancelled) {
                this.cancelled = true;
                cancelSources();
                if (getAndIncrement() == 0) {
                    clear(this.queue);
                }
            }
        }

        public boolean isDisposed() {
            return this.cancelled;
        }

        void cancelSources() {
            for (CombinerObserver<T, R> observer : this.observers) {
                observer.dispose();
            }
        }

        void clear(SpscLinkedArrayQueue<?> q) {
            synchronized (this) {
                this.latest = null;
            }
            q.clear();
        }

        void drain() {
            if (getAndIncrement() == 0) {
                SpscLinkedArrayQueue<Object[]> q = this.queue;
                Observer<? super R> a = this.downstream;
                boolean delayError = this.delayError;
                int missed = 1;
                while (!this.cancelled) {
                    if (delayError || this.errors.get() == null) {
                        boolean d = this.done;
                        Object[] s = (Object[]) q.poll();
                        boolean empty = s == null;
                        Throwable ex;
                        if (d && empty) {
                            clear(q);
                            ex = this.errors.terminate();
                            if (ex == null) {
                                a.onComplete();
                            } else {
                                a.onError(ex);
                            }
                            return;
                        } else if (empty) {
                            missed = addAndGet(-missed);
                            if (missed == 0) {
                                return;
                            }
                        } else {
                            try {
                                a.onNext(ObjectHelper.requireNonNull(this.combiner.apply(s), "The combiner returned a null value"));
                            } catch (Throwable ex2) {
                                Exceptions.throwIfFatal(ex2);
                                this.errors.addThrowable(ex2);
                                cancelSources();
                                clear(q);
                                a.onError(this.errors.terminate());
                                return;
                            }
                        }
                    }
                    cancelSources();
                    clear(q);
                    a.onError(this.errors.terminate());
                    return;
                }
                clear(q);
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        void innerNext(int r7, T r8) {
            /*
            r6 = this;
            r0 = 0;
            monitor-enter(r6);
            r1 = r6.latest;	 Catch:{ all -> 0x002e }
            if (r1 != 0) goto L_0x0008;
        L_0x0006:
            monitor-exit(r6);	 Catch:{ all -> 0x002e }
            return;
        L_0x0008:
            r2 = r1[r7];	 Catch:{ all -> 0x002e }
            r3 = r6.active;	 Catch:{ all -> 0x002e }
            if (r2 != 0) goto L_0x0013;
        L_0x000e:
            r3 = r3 + 1;
            r6.active = r3;	 Catch:{ all -> 0x002e }
            goto L_0x0014;
        L_0x0014:
            r1[r7] = r8;	 Catch:{ all -> 0x002e }
            r4 = r1.length;	 Catch:{ all -> 0x002e }
            if (r3 != r4) goto L_0x0024;
        L_0x0019:
            r4 = r6.queue;	 Catch:{ all -> 0x002e }
            r5 = r1.clone();	 Catch:{ all -> 0x002e }
            r4.offer(r5);	 Catch:{ all -> 0x002e }
            r0 = 1;
            goto L_0x0025;
        L_0x0025:
            monitor-exit(r6);	 Catch:{ all -> 0x002e }
            if (r0 == 0) goto L_0x002c;
        L_0x0028:
            r6.drain();
            goto L_0x002d;
        L_0x002d:
            return;
        L_0x002e:
            r1 = move-exception;
            monitor-exit(r6);	 Catch:{ all -> 0x002e }
            throw r1;
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.observable.ObservableCombineLatest.LatestCoordinator.innerNext(int, java.lang.Object):void");
        }

        void innerError(int index, Throwable ex) {
            if (this.errors.addThrowable(ex)) {
                boolean cancelOthers = true;
                if (this.delayError) {
                    synchronized (this) {
                        Object[] latest = this.latest;
                        if (latest == null) {
                            return;
                        }
                        cancelOthers = latest[index] == null;
                        if (!cancelOthers) {
                            int i = this.complete + 1;
                            this.complete = i;
                            if (i == latest.length) {
                            }
                        }
                        this.done = true;
                    }
                }
                if (cancelOthers) {
                    cancelSources();
                }
                drain();
            } else {
                RxJavaPlugins.onError(ex);
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        void innerComplete(int r6) {
            /*
            r5 = this;
            r0 = 0;
            monitor-enter(r5);
            r1 = r5.latest;	 Catch:{ all -> 0x002b }
            if (r1 != 0) goto L_0x0008;
        L_0x0006:
            monitor-exit(r5);	 Catch:{ all -> 0x002b }
            return;
        L_0x0008:
            r2 = r1[r6];	 Catch:{ all -> 0x002b }
            r3 = 1;
            if (r2 != 0) goto L_0x000f;
        L_0x000d:
            r2 = 1;
            goto L_0x0010;
        L_0x000f:
            r2 = 0;
        L_0x0010:
            r0 = r2;
            if (r0 != 0) goto L_0x001d;
        L_0x0013:
            r2 = r5.complete;	 Catch:{ all -> 0x002b }
            r2 = r2 + r3;
            r5.complete = r2;	 Catch:{ all -> 0x002b }
            r4 = r1.length;	 Catch:{ all -> 0x002b }
            if (r2 != r4) goto L_0x001c;
        L_0x001b:
            goto L_0x001d;
        L_0x001c:
            goto L_0x001f;
        L_0x001d:
            r5.done = r3;	 Catch:{ all -> 0x002b }
        L_0x001f:
            monitor-exit(r5);	 Catch:{ all -> 0x002b }
            if (r0 == 0) goto L_0x0026;
        L_0x0022:
            r5.cancelSources();
            goto L_0x0027;
        L_0x0027:
            r5.drain();
            return;
        L_0x002b:
            r1 = move-exception;
            monitor-exit(r5);	 Catch:{ all -> 0x002b }
            throw r1;
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.observable.ObservableCombineLatest.LatestCoordinator.innerComplete(int):void");
        }
    }

    public ObservableCombineLatest(ObservableSource<? extends T>[] sources, Iterable<? extends ObservableSource<? extends T>> sourcesIterable, Function<? super Object[], ? extends R> combiner, int bufferSize, boolean delayError) {
        this.sources = sources;
        this.sourcesIterable = sourcesIterable;
        this.combiner = combiner;
        this.bufferSize = bufferSize;
        this.delayError = delayError;
    }

    public void subscribeActual(Observer<? super R> observer) {
        ObservableSource<? extends T>[] sources = this.sources;
        int count = 0;
        if (sources == null) {
            sources = new Observable[8];
            for (ObservableSource<? extends T> p : this.sourcesIterable) {
                if (count == sources.length) {
                    ObservableSource<? extends T>[] b = new ObservableSource[((count >> 2) + count)];
                    System.arraycopy(sources, 0, b, 0, count);
                    sources = b;
                }
                int count2 = count + 1;
                sources[count] = p;
                count = count2;
            }
        } else {
            count = sources.length;
        }
        if (count == 0) {
            EmptyDisposable.complete((Observer) observer);
            return;
        }
        new LatestCoordinator(observer, this.combiner, count, this.bufferSize, this.delayError).subscribe(sources);
    }
}
