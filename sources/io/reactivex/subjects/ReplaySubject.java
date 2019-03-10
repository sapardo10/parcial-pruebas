package io.reactivex.subjects;

import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.annotations.CheckReturnValue;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.util.NotificationLite;
import io.reactivex.plugins.RxJavaPlugins;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public final class ReplaySubject<T> extends Subject<T> {
    static final ReplayDisposable[] EMPTY = new ReplayDisposable[0];
    private static final Object[] EMPTY_ARRAY = new Object[0];
    static final ReplayDisposable[] TERMINATED = new ReplayDisposable[0];
    final ReplayBuffer<T> buffer;
    boolean done;
    final AtomicReference<ReplayDisposable<T>[]> observers = new AtomicReference(EMPTY);

    static final class Node<T> extends AtomicReference<Node<T>> {
        private static final long serialVersionUID = 6404226426336033100L;
        final T value;

        Node(T value) {
            this.value = value;
        }
    }

    interface ReplayBuffer<T> {
        void add(T t);

        void addFinal(Object obj);

        boolean compareAndSet(Object obj, Object obj2);

        Object get();

        @Nullable
        T getValue();

        T[] getValues(T[] tArr);

        void replay(ReplayDisposable<T> replayDisposable);

        int size();

        void trimHead();
    }

    static final class TimedNode<T> extends AtomicReference<TimedNode<T>> {
        private static final long serialVersionUID = 6404226426336033100L;
        final long time;
        final T value;

        TimedNode(T value, long time) {
            this.value = value;
            this.time = time;
        }
    }

    static final class ReplayDisposable<T> extends AtomicInteger implements Disposable {
        private static final long serialVersionUID = 466549804534799122L;
        volatile boolean cancelled;
        final Observer<? super T> downstream;
        Object index;
        final ReplaySubject<T> state;

        ReplayDisposable(Observer<? super T> actual, ReplaySubject<T> state) {
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
    }

    static final class SizeAndTimeBoundReplayBuffer<T> extends AtomicReference<Object> implements ReplayBuffer<T> {
        private static final long serialVersionUID = -8056260896137901749L;
        volatile boolean done;
        volatile TimedNode<Object> head;
        final long maxAge;
        final int maxSize;
        final Scheduler scheduler;
        int size;
        TimedNode<Object> tail;
        final TimeUnit unit;

        SizeAndTimeBoundReplayBuffer(int maxSize, long maxAge, TimeUnit unit, Scheduler scheduler) {
            this.maxSize = ObjectHelper.verifyPositive(maxSize, "maxSize");
            this.maxAge = ObjectHelper.verifyPositive(maxAge, "maxAge");
            this.unit = (TimeUnit) ObjectHelper.requireNonNull((Object) unit, "unit is null");
            this.scheduler = (Scheduler) ObjectHelper.requireNonNull((Object) scheduler, "scheduler is null");
            TimedNode<Object> h = new TimedNode(null, 0);
            this.tail = h;
            this.head = h;
        }

        void trim() {
            int i = this.size;
            if (i > this.maxSize) {
                this.size = i - 1;
                this.head = (TimedNode) this.head.get();
            }
            long limit = this.scheduler.now(this.unit) - this.maxAge;
            TimedNode<Object> h = this.head;
            while (true) {
                TimedNode<Object> next = (TimedNode) h.get();
                if (next == null) {
                    this.head = h;
                    return;
                } else if (next.time > limit) {
                    this.head = h;
                    return;
                } else {
                    h = next;
                }
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        void trimFinal() {
            /*
            r10 = this;
            r0 = r10.scheduler;
            r1 = r10.unit;
            r0 = r0.now(r1);
            r2 = r10.maxAge;
            r0 = r0 - r2;
            r2 = r10.head;
        L_0x000d:
            r3 = r2.get();
            r3 = (io.reactivex.subjects.ReplaySubject.TimedNode) r3;
            r4 = r3.get();
            r5 = 0;
            r7 = 0;
            if (r4 != 0) goto L_0x0032;
        L_0x001c:
            r4 = r2.value;
            if (r4 == 0) goto L_0x002f;
        L_0x0020:
            r4 = new io.reactivex.subjects.ReplaySubject$TimedNode;
            r4.<init>(r7, r5);
            r5 = r2.get();
            r4.lazySet(r5);
            r10.head = r4;
            goto L_0x004e;
        L_0x002f:
            r10.head = r2;
            goto L_0x004e;
        L_0x0032:
            r8 = r3.time;
            r4 = (r8 > r0 ? 1 : (r8 == r0 ? 0 : -1));
            if (r4 <= 0) goto L_0x004f;
        L_0x0038:
            r4 = r2.value;
            if (r4 == 0) goto L_0x004b;
        L_0x003c:
            r4 = new io.reactivex.subjects.ReplaySubject$TimedNode;
            r4.<init>(r7, r5);
            r5 = r2.get();
            r4.lazySet(r5);
            r10.head = r4;
            goto L_0x004e;
        L_0x004b:
            r10.head = r2;
        L_0x004e:
            return;
        L_0x004f:
            r2 = r3;
            goto L_0x000d;
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.subjects.ReplaySubject.SizeAndTimeBoundReplayBuffer.trimFinal():void");
        }

        public void add(T value) {
            TimedNode<Object> n = new TimedNode(value, this.scheduler.now(this.unit));
            TimedNode<Object> t = this.tail;
            this.tail = n;
            this.size++;
            t.set(n);
            trim();
        }

        public void addFinal(Object notificationLite) {
            TimedNode<Object> n = new TimedNode(notificationLite, Long.MAX_VALUE);
            TimedNode<Object> t = this.tail;
            this.tail = n;
            this.size++;
            t.lazySet(n);
            trimFinal();
            this.done = true;
        }

        public void trimHead() {
            TimedNode<Object> h = this.head;
            if (h.value != null) {
                TimedNode<Object> n = new TimedNode(null, 0);
                n.lazySet(h.get());
                this.head = n;
            }
        }

        @Nullable
        public T getValue() {
            TimedNode<Object> prev = null;
            TimedNode<Object> h = this.head;
            while (true) {
                TimedNode<Object> next = (TimedNode) h.get();
                if (next == null) {
                    break;
                }
                prev = h;
                h = next;
            }
            if (h.time < this.scheduler.now(this.unit) - this.maxAge) {
                return null;
            }
            Object v = h.value;
            if (v == null) {
                return null;
            }
            if (!NotificationLite.isComplete(v)) {
                if (!NotificationLite.isError(v)) {
                    return v;
                }
            }
            return prev.value;
        }

        TimedNode<Object> getHead() {
            TimedNode<Object> index = this.head;
            long limit = this.scheduler.now(this.unit) - this.maxAge;
            TimedNode<Object> next = (TimedNode) index.get();
            while (next != null) {
                if (next.time > limit) {
                    break;
                }
                index = next;
                next = (TimedNode) index.get();
            }
            return index;
        }

        public T[] getValues(T[] array) {
            TimedNode<Object> h = getHead();
            int s = size(h);
            if (s != 0) {
                if (array.length < s) {
                    array = (Object[]) Array.newInstance(array.getClass().getComponentType(), s);
                }
                int i = 0;
                while (i != s) {
                    TimedNode<Object> next = (TimedNode) h.get();
                    array[i] = next.value;
                    i++;
                    h = next;
                }
                if (array.length > s) {
                    array[s] = null;
                }
            } else if (array.length != 0) {
                array[0] = null;
            }
            return array;
        }

        public void replay(ReplayDisposable<T> rs) {
            if (rs.getAndIncrement() == 0) {
                int missed = 1;
                Observer<? super T> a = rs.downstream;
                TimedNode<Object> index = rs.index;
                if (index == null) {
                    index = getHead();
                }
                while (!rs.cancelled) {
                    while (!rs.cancelled) {
                        TimedNode<Object> n = (TimedNode) index.get();
                        if (n != null) {
                            Object o = n.value;
                            if (this.done) {
                                if (n.get() == null) {
                                    if (NotificationLite.isComplete(o)) {
                                        a.onComplete();
                                    } else {
                                        a.onError(NotificationLite.getError(o));
                                    }
                                    rs.index = null;
                                    rs.cancelled = true;
                                    return;
                                }
                            }
                            a.onNext(o);
                            index = n;
                        } else if (index.get() == null) {
                            rs.index = index;
                            missed = rs.addAndGet(-missed);
                            if (missed == 0) {
                                return;
                            }
                        }
                    }
                    rs.index = null;
                    return;
                }
                rs.index = null;
            }
        }

        public int size() {
            return size(getHead());
        }

        int size(TimedNode<Object> h) {
            int s = 0;
            while (s != Integer.MAX_VALUE) {
                TimedNode<Object> next = (TimedNode) h.get();
                if (next == null) {
                    Object o = h.value;
                    if (!NotificationLite.isComplete(o)) {
                        if (!NotificationLite.isError(o)) {
                            return s;
                        }
                    }
                    return s - 1;
                }
                s++;
                h = next;
            }
            return s;
        }
    }

    static final class SizeBoundReplayBuffer<T> extends AtomicReference<Object> implements ReplayBuffer<T> {
        private static final long serialVersionUID = 1107649250281456395L;
        volatile boolean done;
        volatile Node<Object> head;
        final int maxSize;
        int size;
        Node<Object> tail;

        SizeBoundReplayBuffer(int maxSize) {
            this.maxSize = ObjectHelper.verifyPositive(maxSize, "maxSize");
            Node<Object> h = new Node(null);
            this.tail = h;
            this.head = h;
        }

        void trim() {
            int i = this.size;
            if (i > this.maxSize) {
                this.size = i - 1;
                this.head = (Node) this.head.get();
            }
        }

        public void add(T value) {
            Node<Object> n = new Node(value);
            Node<Object> t = this.tail;
            this.tail = n;
            this.size++;
            t.set(n);
            trim();
        }

        public void addFinal(Object notificationLite) {
            Node<Object> n = new Node(notificationLite);
            Node<Object> t = this.tail;
            this.tail = n;
            this.size++;
            t.lazySet(n);
            trimHead();
            this.done = true;
        }

        public void trimHead() {
            Node<Object> h = this.head;
            if (h.value != null) {
                Node<Object> n = new Node(null);
                n.lazySet(h.get());
                this.head = n;
            }
        }

        @Nullable
        public T getValue() {
            Node<Object> next;
            Node<Object> prev = null;
            Node<Object> h = this.head;
            while (true) {
                next = (Node) h.get();
                if (next == null) {
                    break;
                }
                prev = h;
                h = next;
            }
            next = h.value;
            if (next == null) {
                return null;
            }
            if (!NotificationLite.isComplete(next)) {
                if (!NotificationLite.isError(next)) {
                    return next;
                }
            }
            return prev.value;
        }

        public T[] getValues(T[] array) {
            Node<Object> h = this.head;
            int s = size();
            if (s != 0) {
                if (array.length < s) {
                    array = (Object[]) Array.newInstance(array.getClass().getComponentType(), s);
                }
                int i = 0;
                while (i != s) {
                    Node<Object> next = (Node) h.get();
                    array[i] = next.value;
                    i++;
                    h = next;
                }
                if (array.length > s) {
                    array[s] = null;
                }
            } else if (array.length != 0) {
                array[0] = null;
            }
            return array;
        }

        public void replay(ReplayDisposable<T> rs) {
            if (rs.getAndIncrement() == 0) {
                int missed = 1;
                Observer<? super T> a = rs.downstream;
                Node<Object> index = rs.index;
                if (index == null) {
                    index = this.head;
                }
                while (!rs.cancelled) {
                    Node<Object> n = (Node) index.get();
                    if (n != null) {
                        Object o = n.value;
                        if (this.done) {
                            if (n.get() == null) {
                                if (NotificationLite.isComplete(o)) {
                                    a.onComplete();
                                } else {
                                    a.onError(NotificationLite.getError(o));
                                }
                                rs.index = null;
                                rs.cancelled = true;
                                return;
                            }
                        }
                        a.onNext(o);
                        index = n;
                    } else if (index.get() == null) {
                        rs.index = index;
                        missed = rs.addAndGet(-missed);
                        if (missed == 0) {
                            return;
                        }
                    }
                }
                rs.index = null;
            }
        }

        public int size() {
            int s = 0;
            Node<Object> h = this.head;
            while (s != Integer.MAX_VALUE) {
                Node<Object> next = (Node) h.get();
                if (next == null) {
                    Object o = h.value;
                    if (!NotificationLite.isComplete(o)) {
                        if (!NotificationLite.isError(o)) {
                            return s;
                        }
                    }
                    return s - 1;
                }
                s++;
                h = next;
            }
            return s;
        }
    }

    static final class UnboundedReplayBuffer<T> extends AtomicReference<Object> implements ReplayBuffer<T> {
        private static final long serialVersionUID = -733876083048047795L;
        final List<Object> buffer;
        volatile boolean done;
        volatile int size;

        UnboundedReplayBuffer(int capacityHint) {
            this.buffer = new ArrayList(ObjectHelper.verifyPositive(capacityHint, "capacityHint"));
        }

        public void add(T value) {
            this.buffer.add(value);
            this.size++;
        }

        public void addFinal(Object notificationLite) {
            this.buffer.add(notificationLite);
            trimHead();
            this.size++;
            this.done = true;
        }

        public void trimHead() {
        }

        @Nullable
        public T getValue() {
            int s = this.size;
            if (s == 0) {
                return null;
            }
            List<Object> b = this.buffer;
            Object o = b.get(s - 1);
            if (!NotificationLite.isComplete(o)) {
                if (!NotificationLite.isError(o)) {
                    return o;
                }
            }
            if (s == 1) {
                return null;
            }
            return b.get(s - 2);
        }

        public T[] getValues(T[] array) {
            int s = this.size;
            if (s == 0) {
                if (array.length != 0) {
                    array[0] = null;
                }
                return array;
            }
            int i;
            List<Object> b = this.buffer;
            Object o = b.get(s - 1);
            if (!NotificationLite.isComplete(o)) {
                if (!NotificationLite.isError(o)) {
                    if (array.length < s) {
                        array = (Object[]) Array.newInstance(array.getClass().getComponentType(), s);
                    }
                    for (i = 0; i < s; i++) {
                        array[i] = b.get(i);
                    }
                    if (array.length > s) {
                        array[s] = null;
                    }
                    return array;
                }
            }
            s--;
            if (s == 0) {
                if (array.length != 0) {
                    array[0] = null;
                }
                return array;
            }
            if (array.length < s) {
                array = (Object[]) Array.newInstance(array.getClass().getComponentType(), s);
            }
            for (i = 0; i < s; i++) {
                array[i] = b.get(i);
            }
            if (array.length > s) {
                array[s] = null;
            }
            return array;
        }

        public void replay(ReplayDisposable<T> rs) {
            if (rs.getAndIncrement() == 0) {
                int index;
                int missed = 1;
                List<Object> b = this.buffer;
                Observer<? super T> a = rs.downstream;
                Integer indexObject = rs.index;
                if (indexObject != null) {
                    index = indexObject.intValue();
                } else {
                    index = 0;
                    rs.index = Integer.valueOf(0);
                }
                while (!rs.cancelled) {
                    int s = this.size;
                    while (s != index) {
                        if (rs.cancelled) {
                            rs.index = null;
                            return;
                        }
                        Object o = b.get(index);
                        if (this.done) {
                            if (index + 1 == s) {
                                s = this.size;
                                if (index + 1 == s) {
                                    if (NotificationLite.isComplete(o)) {
                                        a.onComplete();
                                    } else {
                                        a.onError(NotificationLite.getError(o));
                                    }
                                    rs.index = null;
                                    rs.cancelled = true;
                                    return;
                                }
                            }
                        }
                        a.onNext(o);
                        index++;
                    }
                    if (index == this.size) {
                        rs.index = Integer.valueOf(index);
                        missed = rs.addAndGet(-missed);
                        if (missed == 0) {
                            return;
                        }
                    }
                }
                rs.index = null;
            }
        }

        public int size() {
            int s = this.size;
            if (s == 0) {
                return 0;
            }
            Object o = this.buffer.get(s - 1);
            if (!NotificationLite.isComplete(o)) {
                if (!NotificationLite.isError(o)) {
                    return s;
                }
            }
            return s - 1;
        }
    }

    @CheckReturnValue
    @NonNull
    public static <T> ReplaySubject<T> create() {
        return new ReplaySubject(new UnboundedReplayBuffer(16));
    }

    @CheckReturnValue
    @NonNull
    public static <T> ReplaySubject<T> create(int capacityHint) {
        return new ReplaySubject(new UnboundedReplayBuffer(capacityHint));
    }

    @CheckReturnValue
    @NonNull
    public static <T> ReplaySubject<T> createWithSize(int maxSize) {
        return new ReplaySubject(new SizeBoundReplayBuffer(maxSize));
    }

    static <T> ReplaySubject<T> createUnbounded() {
        return new ReplaySubject(new SizeBoundReplayBuffer(Integer.MAX_VALUE));
    }

    @CheckReturnValue
    @NonNull
    public static <T> ReplaySubject<T> createWithTime(long maxAge, TimeUnit unit, Scheduler scheduler) {
        return new ReplaySubject(new SizeAndTimeBoundReplayBuffer(Integer.MAX_VALUE, maxAge, unit, scheduler));
    }

    @CheckReturnValue
    @NonNull
    public static <T> ReplaySubject<T> createWithTimeAndSize(long maxAge, TimeUnit unit, Scheduler scheduler, int maxSize) {
        return new ReplaySubject(new SizeAndTimeBoundReplayBuffer(maxSize, maxAge, unit, scheduler));
    }

    ReplaySubject(ReplayBuffer<T> buffer) {
        this.buffer = buffer;
    }

    protected void subscribeActual(Observer<? super T> observer) {
        ReplayDisposable<T> rs = new ReplayDisposable(observer, this);
        observer.onSubscribe(rs);
        if (!rs.cancelled) {
            if (add(rs)) {
                if (rs.cancelled) {
                    remove(rs);
                    return;
                }
            }
            this.buffer.replay(rs);
        }
    }

    public void onSubscribe(Disposable d) {
        if (this.done) {
            d.dispose();
        }
    }

    public void onNext(T t) {
        ObjectHelper.requireNonNull((Object) t, "onNext called with null. Null values are generally not allowed in 2.x operators and sources.");
        if (!this.done) {
            ReplayBuffer<T> b = this.buffer;
            b.add(t);
            for (ReplayDisposable<T> rs : (ReplayDisposable[]) this.observers.get()) {
                b.replay(rs);
            }
        }
    }

    public void onError(Throwable t) {
        ObjectHelper.requireNonNull((Object) t, "onError called with null. Null values are generally not allowed in 2.x operators and sources.");
        if (this.done) {
            RxJavaPlugins.onError(t);
            return;
        }
        this.done = true;
        Object o = NotificationLite.error(t);
        ReplayBuffer<T> b = this.buffer;
        b.addFinal(o);
        for (ReplayDisposable<T> rs : terminate(o)) {
            b.replay(rs);
        }
    }

    public void onComplete() {
        if (!this.done) {
            this.done = true;
            Object o = NotificationLite.complete();
            ReplayBuffer<T> b = this.buffer;
            b.addFinal(o);
            for (ReplayDisposable<T> rs : terminate(o)) {
                b.replay(rs);
            }
        }
    }

    public boolean hasObservers() {
        return ((ReplayDisposable[]) this.observers.get()).length != 0;
    }

    int observerCount() {
        return ((ReplayDisposable[]) this.observers.get()).length;
    }

    @Nullable
    public Throwable getThrowable() {
        Object o = this.buffer.get();
        if (NotificationLite.isError(o)) {
            return NotificationLite.getError(o);
        }
        return null;
    }

    @Nullable
    public T getValue() {
        return this.buffer.getValue();
    }

    public void cleanupBuffer() {
        this.buffer.trimHead();
    }

    public Object[] getValues() {
        T[] b = getValues((Object[]) EMPTY_ARRAY);
        if (b == EMPTY_ARRAY) {
            return new Object[0];
        }
        return b;
    }

    public T[] getValues(T[] array) {
        return this.buffer.getValues(array);
    }

    public boolean hasComplete() {
        return NotificationLite.isComplete(this.buffer.get());
    }

    public boolean hasThrowable() {
        return NotificationLite.isError(this.buffer.get());
    }

    public boolean hasValue() {
        return this.buffer.size() != 0;
    }

    int size() {
        return this.buffer.size();
    }

    boolean add(ReplayDisposable<T> rs) {
        while (true) {
            ReplayDisposable[] a = (ReplayDisposable[]) this.observers.get();
            if (a == TERMINATED) {
                return false;
            }
            int len = a.length;
            ReplayDisposable<T>[] b = new ReplayDisposable[(len + 1)];
            System.arraycopy(a, 0, b, 0, len);
            b[len] = rs;
            if (this.observers.compareAndSet(a, b)) {
                return true;
            }
        }
    }

    void remove(ReplayDisposable<T> rs) {
        while (true) {
            ReplayDisposable[] a = (ReplayDisposable[]) this.observers.get();
            if (a == TERMINATED) {
                break;
            } else if (a == EMPTY) {
                break;
            } else {
                int len = a.length;
                int j = -1;
                for (int i = 0; i < len; i++) {
                    if (a[i] == rs) {
                        j = i;
                        break;
                    }
                }
                if (j >= 0) {
                    ReplayDisposable<T>[] b;
                    if (len == 1) {
                        b = EMPTY;
                    } else {
                        ReplayDisposable<T>[] b2 = new ReplayDisposable[(len - 1)];
                        System.arraycopy(a, 0, b2, 0, j);
                        System.arraycopy(a, j + 1, b2, j, (len - j) - 1);
                        b = b2;
                    }
                    if (this.observers.compareAndSet(a, b)) {
                        return;
                    }
                } else {
                    return;
                }
            }
        }
    }

    ReplayDisposable<T>[] terminate(Object terminalValue) {
        if (this.buffer.compareAndSet(null, terminalValue)) {
            return (ReplayDisposable[]) this.observers.getAndSet(TERMINATED);
        }
        return TERMINATED;
    }
}
