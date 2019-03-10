package io.reactivex.processors;

import io.reactivex.annotations.CheckReturnValue;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.exceptions.MissingBackpressureException;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.AppendOnlyLinkedArrayList;
import io.reactivex.internal.util.AppendOnlyLinkedArrayList.NonThrowingPredicate;
import io.reactivex.internal.util.BackpressureHelper;
import io.reactivex.internal.util.ExceptionHelper;
import io.reactivex.internal.util.NotificationLite;
import io.reactivex.plugins.RxJavaPlugins;
import java.lang.reflect.Array;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class BehaviorProcessor<T> extends FlowableProcessor<T> {
    static final BehaviorSubscription[] EMPTY = new BehaviorSubscription[0];
    static final Object[] EMPTY_ARRAY = new Object[0];
    static final BehaviorSubscription[] TERMINATED = new BehaviorSubscription[0];
    long index;
    final ReadWriteLock lock;
    final Lock readLock;
    final AtomicReference<BehaviorSubscription<T>[]> subscribers;
    final AtomicReference<Throwable> terminalEvent;
    final AtomicReference<Object> value;
    final Lock writeLock;

    static final class BehaviorSubscription<T> extends AtomicLong implements Subscription, NonThrowingPredicate<Object> {
        private static final long serialVersionUID = 3293175281126227086L;
        volatile boolean cancelled;
        final Subscriber<? super T> downstream;
        boolean emitting;
        boolean fastPath;
        long index;
        boolean next;
        AppendOnlyLinkedArrayList<Object> queue;
        final BehaviorProcessor<T> state;

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
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.processors.BehaviorProcessor.BehaviorSubscription.emitLoop():void");
        }

        BehaviorSubscription(Subscriber<? super T> actual, BehaviorProcessor<T> state) {
            this.downstream = actual;
            this.state = state;
        }

        public void request(long n) {
            if (SubscriptionHelper.validate(n)) {
                BackpressureHelper.add(this, n);
            }
        }

        public void cancel() {
            if (!this.cancelled) {
                this.cancelled = true;
                this.state.remove(this);
            }
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
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.processors.BehaviorProcessor.BehaviorSubscription.emitFirst():void");
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
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.processors.BehaviorProcessor.BehaviorSubscription.emitNext(java.lang.Object, long):void");
        }

        public boolean test(Object o) {
            if (this.cancelled) {
                return true;
            }
            if (NotificationLite.isComplete(o)) {
                this.downstream.onComplete();
                return true;
            } else if (NotificationLite.isError(o)) {
                this.downstream.onError(NotificationLite.getError(o));
                return true;
            } else {
                long r = get();
                if (r != 0) {
                    this.downstream.onNext(NotificationLite.getValue(o));
                    if (r != Long.MAX_VALUE) {
                        decrementAndGet();
                    }
                    return false;
                }
                cancel();
                this.downstream.onError(new MissingBackpressureException("Could not deliver value due to lack of requests"));
                return true;
            }
        }

        public boolean isFull() {
            return get() == 0;
        }
    }

    @CheckReturnValue
    @NonNull
    public static <T> BehaviorProcessor<T> create() {
        return new BehaviorProcessor();
    }

    @CheckReturnValue
    @NonNull
    public static <T> BehaviorProcessor<T> createDefault(T defaultValue) {
        ObjectHelper.requireNonNull((Object) defaultValue, "defaultValue is null");
        return new BehaviorProcessor(defaultValue);
    }

    BehaviorProcessor() {
        this.value = new AtomicReference();
        this.lock = new ReentrantReadWriteLock();
        this.readLock = this.lock.readLock();
        this.writeLock = this.lock.writeLock();
        this.subscribers = new AtomicReference(EMPTY);
        this.terminalEvent = new AtomicReference();
    }

    BehaviorProcessor(T defaultValue) {
        this();
        this.value.lazySet(ObjectHelper.requireNonNull((Object) defaultValue, "defaultValue is null"));
    }

    protected void subscribeActual(Subscriber<? super T> s) {
        BehaviorSubscription<T> bs = new BehaviorSubscription(s, this);
        s.onSubscribe(bs);
        if (!add(bs)) {
            Throwable ex = (Throwable) this.terminalEvent.get();
            if (ex == ExceptionHelper.TERMINATED) {
                s.onComplete();
            } else {
                s.onError(ex);
            }
        } else if (bs.cancelled) {
            remove(bs);
        } else {
            bs.emitFirst();
        }
    }

    public void onSubscribe(Subscription s) {
        if (this.terminalEvent.get() != null) {
            s.cancel();
        } else {
            s.request(Long.MAX_VALUE);
        }
    }

    public void onNext(T t) {
        ObjectHelper.requireNonNull((Object) t, "onNext called with null. Null values are generally not allowed in 2.x operators and sources.");
        if (this.terminalEvent.get() == null) {
            Object o = NotificationLite.next(t);
            setCurrent(o);
            for (BehaviorSubscription<T> bs : (BehaviorSubscription[]) this.subscribers.get()) {
                bs.emitNext(o, this.index);
            }
        }
    }

    public void onError(Throwable t) {
        ObjectHelper.requireNonNull((Object) t, "onError called with null. Null values are generally not allowed in 2.x operators and sources.");
        if (this.terminalEvent.compareAndSet(null, t)) {
            Object o = NotificationLite.error(t);
            for (BehaviorSubscription<T> bs : terminate(o)) {
                bs.emitNext(o, this.index);
            }
            return;
        }
        RxJavaPlugins.onError(t);
    }

    public void onComplete() {
        if (this.terminalEvent.compareAndSet(null, ExceptionHelper.TERMINATED)) {
            Object o = NotificationLite.complete();
            for (BehaviorSubscription<T> bs : terminate(o)) {
                bs.emitNext(o, this.index);
            }
        }
    }

    public boolean offer(T t) {
        if (t == null) {
            onError(new NullPointerException("onNext called with null. Null values are generally not allowed in 2.x operators and sources."));
            return true;
        }
        BehaviorSubscription[] array = (BehaviorSubscription[]) this.subscribers.get();
        int i = 0;
        for (BehaviorSubscription<T> s : array) {
            if (s.isFull()) {
                return false;
            }
        }
        Object o = NotificationLite.next(t);
        setCurrent(o);
        int length = array.length;
        while (i < length) {
            array[i].emitNext(o, this.index);
            i++;
        }
        return true;
    }

    public boolean hasSubscribers() {
        return ((BehaviorSubscription[]) this.subscribers.get()).length != 0;
    }

    int subscriberCount() {
        return ((BehaviorSubscription[]) this.subscribers.get()).length;
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

    boolean add(BehaviorSubscription<T> rs) {
        while (true) {
            BehaviorSubscription[] a = (BehaviorSubscription[]) this.subscribers.get();
            if (a == TERMINATED) {
                return false;
            }
            int len = a.length;
            BehaviorSubscription<T>[] b = new BehaviorSubscription[(len + 1)];
            System.arraycopy(a, 0, b, 0, len);
            b[len] = rs;
            if (this.subscribers.compareAndSet(a, b)) {
                return true;
            }
        }
    }

    void remove(BehaviorSubscription<T> rs) {
        while (true) {
            BehaviorSubscription[] a = (BehaviorSubscription[]) this.subscribers.get();
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
                    BehaviorSubscription<T>[] b;
                    if (len == 1) {
                        b = EMPTY;
                    } else {
                        BehaviorSubscription<T>[] b2 = new BehaviorSubscription[(len - 1)];
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

    BehaviorSubscription<T>[] terminate(Object terminalValue) {
        BehaviorSubscription<T>[] a = (BehaviorSubscription[]) this.subscribers.get();
        Object obj = TERMINATED;
        if (a != obj) {
            a = (BehaviorSubscription[]) this.subscribers.getAndSet(obj);
            if (a != TERMINATED) {
                setCurrent(terminalValue);
            }
        }
        return a;
    }

    void setCurrent(Object o) {
        Lock wl = this.writeLock;
        wl.lock();
        this.index++;
        this.value.lazySet(o);
        wl.unlock();
    }
}
