package io.reactivex.subjects;

import io.reactivex.Observer;
import io.reactivex.annotations.CheckReturnValue;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.util.AppendOnlyLinkedArrayList;
import io.reactivex.internal.util.AppendOnlyLinkedArrayList.NonThrowingPredicate;
import io.reactivex.internal.util.ExceptionHelper;
import io.reactivex.internal.util.NotificationLite;
import io.reactivex.plugins.RxJavaPlugins;
import java.lang.reflect.Array;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class BehaviorSubject<T> extends Subject<T> {
    static final BehaviorDisposable[] EMPTY = new BehaviorDisposable[0];
    private static final Object[] EMPTY_ARRAY = new Object[0];
    static final BehaviorDisposable[] TERMINATED = new BehaviorDisposable[0];
    long index;
    final ReadWriteLock lock;
    final Lock readLock;
    final AtomicReference<BehaviorDisposable<T>[]> subscribers;
    final AtomicReference<Throwable> terminalEvent;
    final AtomicReference<Object> value;
    final Lock writeLock;

    static final class BehaviorDisposable<T> implements Disposable, NonThrowingPredicate<Object> {
        volatile boolean cancelled;
        final Observer<? super T> downstream;
        boolean emitting;
        boolean fastPath;
        long index;
        boolean next;
        AppendOnlyLinkedArrayList<Object> queue;
        final BehaviorSubject<T> state;

        void emitLoop() {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:17:0x001a in {2, 9, 12, 16} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
            /*
            r2 = this;
        L_0x0000:
            r0 = r2.cancelled;
            if (r0 == 0) goto L_0x0005;
        L_0x0004:
            return;
        L_0x0005:
            monitor-enter(r2);
            r0 = r2.queue;	 Catch:{ all -> 0x0017 }
            if (r0 != 0) goto L_0x000f;	 Catch:{ all -> 0x0017 }
        L_0x000a:
            r1 = 0;	 Catch:{ all -> 0x0017 }
            r2.emitting = r1;	 Catch:{ all -> 0x0017 }
            monitor-exit(r2);	 Catch:{ all -> 0x0017 }
            return;	 Catch:{ all -> 0x0017 }
        L_0x000f:
            r1 = 0;	 Catch:{ all -> 0x0017 }
            r2.queue = r1;	 Catch:{ all -> 0x0017 }
            monitor-exit(r2);	 Catch:{ all -> 0x0017 }
            r0.forEachWhile(r2);
            goto L_0x0000;
        L_0x0017:
            r0 = move-exception;
            monitor-exit(r2);	 Catch:{ all -> 0x0017 }
            throw r0;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.subjects.BehaviorSubject.BehaviorDisposable.emitLoop():void");
        }

        BehaviorDisposable(Observer<? super T> actual, BehaviorSubject<T> state) {
            this.downstream = actual;
            this.state = state;
        }

        public void dispose() {
            if (!this.cancelled) {
                this.cancelled = true;
                this.state.remove(this);
            }
        }

        public boolean isDisposed() {
            return this.cancelled;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        void emitFirst() {
            /*
            r5 = this;
            r0 = r5.cancelled;
            if (r0 == 0) goto L_0x0005;
        L_0x0004:
            return;
        L_0x0005:
            monitor-enter(r5);
            r0 = r5.cancelled;	 Catch:{ all -> 0x0040 }
            if (r0 == 0) goto L_0x000c;
        L_0x000a:
            monitor-exit(r5);	 Catch:{ all -> 0x0040 }
            return;
        L_0x000c:
            r0 = r5.next;	 Catch:{ all -> 0x0040 }
            if (r0 == 0) goto L_0x0012;
        L_0x0010:
            monitor-exit(r5);	 Catch:{ all -> 0x0040 }
            return;
        L_0x0012:
            r0 = r5.state;	 Catch:{ all -> 0x0040 }
            r1 = r0.readLock;	 Catch:{ all -> 0x0040 }
            r1.lock();	 Catch:{ all -> 0x0040 }
            r2 = r0.index;	 Catch:{ all -> 0x0040 }
            r5.index = r2;	 Catch:{ all -> 0x0040 }
            r2 = r0.value;	 Catch:{ all -> 0x0040 }
            r2 = r2.get();	 Catch:{ all -> 0x0040 }
            r1.unlock();	 Catch:{ all -> 0x0040 }
            r3 = 1;
            if (r2 == 0) goto L_0x002b;
        L_0x0029:
            r4 = 1;
            goto L_0x002c;
        L_0x002b:
            r4 = 0;
        L_0x002c:
            r5.emitting = r4;	 Catch:{ all -> 0x0040 }
            r5.next = r3;	 Catch:{ all -> 0x0040 }
            monitor-exit(r5);	 Catch:{ all -> 0x0040 }
            if (r2 == 0) goto L_0x003e;
        L_0x0033:
            r0 = r5.test(r2);
            if (r0 == 0) goto L_0x003a;
        L_0x0039:
            return;
        L_0x003a:
            r5.emitLoop();
            goto L_0x003f;
        L_0x003f:
            return;
        L_0x0040:
            r0 = move-exception;
            monitor-exit(r5);	 Catch:{ all -> 0x0040 }
            throw r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.subjects.BehaviorSubject.BehaviorDisposable.emitFirst():void");
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        void emitNext(java.lang.Object r4, long r5) {
            /*
            r3 = this;
            r0 = r3.cancelled;
            if (r0 == 0) goto L_0x0005;
        L_0x0004:
            return;
        L_0x0005:
            r0 = r3.fastPath;
            if (r0 != 0) goto L_0x003a;
        L_0x0009:
            monitor-enter(r3);
            r0 = r3.cancelled;	 Catch:{ all -> 0x0037 }
            if (r0 == 0) goto L_0x0010;
        L_0x000e:
            monitor-exit(r3);	 Catch:{ all -> 0x0037 }
            return;
        L_0x0010:
            r0 = r3.index;	 Catch:{ all -> 0x0037 }
            r2 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1));
            if (r2 != 0) goto L_0x0018;
        L_0x0016:
            monitor-exit(r3);	 Catch:{ all -> 0x0037 }
            return;
        L_0x0018:
            r0 = r3.emitting;	 Catch:{ all -> 0x0037 }
            if (r0 == 0) goto L_0x0030;
        L_0x001c:
            r0 = r3.queue;	 Catch:{ all -> 0x0037 }
            if (r0 != 0) goto L_0x002a;
        L_0x0020:
            r1 = new io.reactivex.internal.util.AppendOnlyLinkedArrayList;	 Catch:{ all -> 0x0037 }
            r2 = 4;
            r1.<init>(r2);	 Catch:{ all -> 0x0037 }
            r0 = r1;
            r3.queue = r0;	 Catch:{ all -> 0x0037 }
            goto L_0x002b;
        L_0x002b:
            r0.add(r4);	 Catch:{ all -> 0x0037 }
            monitor-exit(r3);	 Catch:{ all -> 0x0037 }
            return;
        L_0x0030:
            r0 = 1;
            r3.next = r0;	 Catch:{ all -> 0x0037 }
            monitor-exit(r3);	 Catch:{ all -> 0x0037 }
            r3.fastPath = r0;
            goto L_0x003b;
        L_0x0037:
            r0 = move-exception;
            monitor-exit(r3);	 Catch:{ all -> 0x0037 }
            throw r0;
        L_0x003b:
            r3.test(r4);
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.subjects.BehaviorSubject.BehaviorDisposable.emitNext(java.lang.Object, long):void");
        }

        public boolean test(Object o) {
            if (!this.cancelled) {
                if (!NotificationLite.accept(o, this.downstream)) {
                    return false;
                }
            }
            return true;
        }
    }

    @CheckReturnValue
    @NonNull
    public static <T> BehaviorSubject<T> create() {
        return new BehaviorSubject();
    }

    @CheckReturnValue
    @NonNull
    public static <T> BehaviorSubject<T> createDefault(T defaultValue) {
        return new BehaviorSubject(defaultValue);
    }

    BehaviorSubject() {
        this.lock = new ReentrantReadWriteLock();
        this.readLock = this.lock.readLock();
        this.writeLock = this.lock.writeLock();
        this.subscribers = new AtomicReference(EMPTY);
        this.value = new AtomicReference();
        this.terminalEvent = new AtomicReference();
    }

    BehaviorSubject(T defaultValue) {
        this();
        this.value.lazySet(ObjectHelper.requireNonNull((Object) defaultValue, "defaultValue is null"));
    }

    protected void subscribeActual(Observer<? super T> observer) {
        BehaviorDisposable<T> bs = new BehaviorDisposable(observer, this);
        observer.onSubscribe(bs);
        if (!add(bs)) {
            Throwable ex = (Throwable) this.terminalEvent.get();
            if (ex == ExceptionHelper.TERMINATED) {
                observer.onComplete();
            } else {
                observer.onError(ex);
            }
        } else if (bs.cancelled) {
            remove(bs);
        } else {
            bs.emitFirst();
        }
    }

    public void onSubscribe(Disposable d) {
        if (this.terminalEvent.get() != null) {
            d.dispose();
        }
    }

    public void onNext(T t) {
        ObjectHelper.requireNonNull((Object) t, "onNext called with null. Null values are generally not allowed in 2.x operators and sources.");
        if (this.terminalEvent.get() == null) {
            Object o = NotificationLite.next(t);
            setCurrent(o);
            for (BehaviorDisposable<T> bs : (BehaviorDisposable[]) this.subscribers.get()) {
                bs.emitNext(o, this.index);
            }
        }
    }

    public void onError(Throwable t) {
        ObjectHelper.requireNonNull((Object) t, "onError called with null. Null values are generally not allowed in 2.x operators and sources.");
        if (this.terminalEvent.compareAndSet(null, t)) {
            Object o = NotificationLite.error(t);
            for (BehaviorDisposable<T> bs : terminate(o)) {
                bs.emitNext(o, this.index);
            }
            return;
        }
        RxJavaPlugins.onError(t);
    }

    public void onComplete() {
        if (this.terminalEvent.compareAndSet(null, ExceptionHelper.TERMINATED)) {
            Object o = NotificationLite.complete();
            for (BehaviorDisposable<T> bs : terminate(o)) {
                bs.emitNext(o, this.index);
            }
        }
    }

    public boolean hasObservers() {
        return ((BehaviorDisposable[]) this.subscribers.get()).length != 0;
    }

    int subscriberCount() {
        return ((BehaviorDisposable[]) this.subscribers.get()).length;
    }

    @Nullable
    public Throwable getThrowable() {
        Object o = this.value.get();
        if (NotificationLite.isError(o)) {
            return NotificationLite.getError(o);
        }
        return null;
    }

    @Nullable
    public T getValue() {
        Object o = this.value.get();
        if (!NotificationLite.isComplete(o)) {
            if (!NotificationLite.isError(o)) {
                return NotificationLite.getValue(o);
            }
        }
        return null;
    }

    @Deprecated
    public Object[] getValues() {
        T[] b = getValues((Object[]) EMPTY_ARRAY);
        if (b == EMPTY_ARRAY) {
            return new Object[0];
        }
        return b;
    }

    @Deprecated
    public T[] getValues(T[] array) {
        Object o = this.value.get();
        if (!(o == null || NotificationLite.isComplete(o))) {
            if (!NotificationLite.isError(o)) {
                T v = NotificationLite.getValue(o);
                if (array.length != 0) {
                    array[0] = v;
                    if (array.length != 1) {
                        array[1] = null;
                    }
                } else {
                    array = (Object[]) Array.newInstance(array.getClass().getComponentType(), 1);
                    array[0] = v;
                }
                return array;
            }
        }
        if (array.length != 0) {
            array[0] = null;
        }
        return array;
    }

    public boolean hasComplete() {
        return NotificationLite.isComplete(this.value.get());
    }

    public boolean hasThrowable() {
        return NotificationLite.isError(this.value.get());
    }

    public boolean hasValue() {
        Object o = this.value.get();
        return (o == null || NotificationLite.isComplete(o) || NotificationLite.isError(o)) ? false : true;
    }

    boolean add(BehaviorDisposable<T> rs) {
        while (true) {
            BehaviorDisposable[] a = (BehaviorDisposable[]) this.subscribers.get();
            if (a == TERMINATED) {
                return false;
            }
            int len = a.length;
            BehaviorDisposable<T>[] b = new BehaviorDisposable[(len + 1)];
            System.arraycopy(a, 0, b, 0, len);
            b[len] = rs;
            if (this.subscribers.compareAndSet(a, b)) {
                return true;
            }
        }
    }

    void remove(BehaviorDisposable<T> rs) {
        while (true) {
            BehaviorDisposable[] a = (BehaviorDisposable[]) this.subscribers.get();
            int len = a.length;
            if (len != 0) {
                int j = -1;
                for (int i = 0; i < len; i++) {
                    if (a[i] == rs) {
                        j = i;
                        break;
                    }
                }
                if (j >= 0) {
                    BehaviorDisposable<T>[] b;
                    if (len == 1) {
                        b = EMPTY;
                    } else {
                        BehaviorDisposable<T>[] b2 = new BehaviorDisposable[(len - 1)];
                        System.arraycopy(a, 0, b2, 0, j);
                        System.arraycopy(a, j + 1, b2, j, (len - j) - 1);
                        b = b2;
                    }
                    if (this.subscribers.compareAndSet(a, b)) {
                        return;
                    }
                } else {
                    return;
                }
            }
            return;
        }
    }

    BehaviorDisposable<T>[] terminate(Object terminalValue) {
        BehaviorDisposable[] a = (BehaviorDisposable[]) this.subscribers.getAndSet(TERMINATED);
        if (a != TERMINATED) {
            setCurrent(terminalValue);
        }
        return a;
    }

    void setCurrent(Object o) {
        this.writeLock.lock();
        this.index++;
        this.value.lazySet(o);
        this.writeLock.unlock();
    }
}
