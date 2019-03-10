package io.reactivex.internal.operators.observable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.internal.disposables.ResettableConnectable;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.fuseable.HasUpstreamObservableSource;
import io.reactivex.internal.util.NotificationLite;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Timed;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public final class ObservableReplay<T> extends ConnectableObservable<T> implements HasUpstreamObservableSource<T>, ResettableConnectable {
    static final BufferSupplier DEFAULT_UNBOUNDED_FACTORY = new UnBoundedFactory();
    final BufferSupplier<T> bufferFactory;
    final AtomicReference<ReplayObserver<T>> current;
    final ObservableSource<T> onSubscribe;
    final ObservableSource<T> source;

    interface BufferSupplier<T> {
        ReplayBuffer<T> call();
    }

    static final class Node extends AtomicReference<Node> {
        private static final long serialVersionUID = 245354315435971818L;
        final Object value;

        Node(Object value) {
            this.value = value;
        }
    }

    interface ReplayBuffer<T> {
        void complete();

        void error(Throwable th);

        void next(T t);

        void replay(InnerDisposable<T> innerDisposable);
    }

    static abstract class BoundedReplayBuffer<T> extends AtomicReference<Node> implements ReplayBuffer<T> {
        private static final long serialVersionUID = 2346567790059478686L;
        int size;
        Node tail;

        abstract void truncate();

        BoundedReplayBuffer() {
            Node n = new Node(null);
            this.tail = n;
            set(n);
        }

        final void addLast(Node n) {
            this.tail.set(n);
            this.tail = n;
            this.size++;
        }

        final void removeFirst() {
            Node next = (Node) ((Node) get()).get();
            this.size--;
            setFirst(next);
        }

        final void trimHead() {
            Node head = (Node) get();
            if (head.value != null) {
                Node n = new Node(null);
                n.lazySet(head.get());
                set(n);
            }
        }

        final void removeSome(int n) {
            Node head = (Node) get();
            while (n > 0) {
                head = (Node) head.get();
                n--;
                this.size--;
            }
            setFirst(head);
        }

        final void setFirst(Node n) {
            set(n);
        }

        public final void next(T value) {
            addLast(new Node(enterTransform(NotificationLite.next(value))));
            truncate();
        }

        public final void error(Throwable e) {
            addLast(new Node(enterTransform(NotificationLite.error(e))));
            truncateFinal();
        }

        public final void complete() {
            addLast(new Node(enterTransform(NotificationLite.complete())));
            truncateFinal();
        }

        public final void replay(InnerDisposable<T> output) {
            if (output.getAndIncrement() == 0) {
                int missed = 1;
                while (true) {
                    Node node = (Node) output.index();
                    if (node == null) {
                        node = getHead();
                        output.index = node;
                    }
                    while (!output.isDisposed()) {
                        Node v = (Node) node.get();
                        if (v == null) {
                            output.index = node;
                            missed = output.addAndGet(-missed);
                            if (missed == 0) {
                                return;
                            }
                        } else if (NotificationLite.accept(leaveTransform(v.value), output.child)) {
                            output.index = null;
                            return;
                        } else {
                            node = v;
                        }
                    }
                    return;
                }
            }
        }

        Object enterTransform(Object value) {
            return value;
        }

        Object leaveTransform(Object value) {
            return value;
        }

        void truncateFinal() {
            trimHead();
        }

        final void collect(Collection<? super T> output) {
            Node n = getHead();
            while (true) {
                Node next = (Node) n.get();
                if (next != null) {
                    Object v = leaveTransform(next.value);
                    if (!NotificationLite.isComplete(v)) {
                        if (!NotificationLite.isError(v)) {
                            output.add(NotificationLite.getValue(v));
                            n = next;
                        } else {
                            return;
                        }
                    }
                    return;
                }
                return;
            }
        }

        boolean hasError() {
            return this.tail.value != null && NotificationLite.isError(leaveTransform(this.tail.value));
        }

        boolean hasCompleted() {
            return this.tail.value != null && NotificationLite.isComplete(leaveTransform(this.tail.value));
        }

        Node getHead() {
            return (Node) get();
        }
    }

    static final class DisposeConsumer<R> implements Consumer<Disposable> {
        private final ObserverResourceWrapper<R> srw;

        DisposeConsumer(ObserverResourceWrapper<R> srw) {
            this.srw = srw;
        }

        public void accept(Disposable r) {
            this.srw.setResource(r);
        }
    }

    static final class InnerDisposable<T> extends AtomicInteger implements Disposable {
        private static final long serialVersionUID = 2728361546769921047L;
        volatile boolean cancelled;
        final Observer<? super T> child;
        Object index;
        final ReplayObserver<T> parent;

        InnerDisposable(ReplayObserver<T> parent, Observer<? super T> child) {
            this.parent = parent;
            this.child = child;
        }

        public boolean isDisposed() {
            return this.cancelled;
        }

        public void dispose() {
            if (!this.cancelled) {
                this.cancelled = true;
                this.parent.remove(this);
            }
        }

        <U> U index() {
            return this.index;
        }
    }

    static final class ReplayBufferSupplier<T> implements BufferSupplier<T> {
        private final int bufferSize;

        ReplayBufferSupplier(int bufferSize) {
            this.bufferSize = bufferSize;
        }

        public ReplayBuffer<T> call() {
            return new SizeBoundReplayBuffer(this.bufferSize);
        }
    }

    static final class ReplayObserver<T> extends AtomicReference<Disposable> implements Observer<T>, Disposable {
        static final InnerDisposable[] EMPTY = new InnerDisposable[0];
        static final InnerDisposable[] TERMINATED = new InnerDisposable[0];
        private static final long serialVersionUID = -533785617179540163L;
        final ReplayBuffer<T> buffer;
        boolean done;
        final AtomicReference<InnerDisposable[]> observers = new AtomicReference(EMPTY);
        final AtomicBoolean shouldConnect = new AtomicBoolean();

        ReplayObserver(ReplayBuffer<T> buffer) {
            this.buffer = buffer;
        }

        public boolean isDisposed() {
            return this.observers.get() == TERMINATED;
        }

        public void dispose() {
            this.observers.set(TERMINATED);
            DisposableHelper.dispose(this);
        }

        boolean add(InnerDisposable<T> producer) {
            while (true) {
                InnerDisposable[] c = (InnerDisposable[]) this.observers.get();
                if (c == TERMINATED) {
                    return false;
                }
                int len = c.length;
                InnerDisposable[] u = new InnerDisposable[(len + 1)];
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
                        InnerDisposable[] u;
                        if (len == 1) {
                            u = EMPTY;
                        } else {
                            InnerDisposable[] u2 = new InnerDisposable[(len - 1)];
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

        public void onSubscribe(Disposable p) {
            if (DisposableHelper.setOnce(this, p)) {
                replay();
            }
        }

        public void onNext(T t) {
            if (!this.done) {
                this.buffer.next(t);
                replay();
            }
        }

        public void onError(Throwable e) {
            if (this.done) {
                RxJavaPlugins.onError(e);
                return;
            }
            this.done = true;
            this.buffer.error(e);
            replayFinal();
        }

        public void onComplete() {
            if (!this.done) {
                this.done = true;
                this.buffer.complete();
                replayFinal();
            }
        }

        void replay() {
            for (InnerDisposable<T> rp : (InnerDisposable[]) this.observers.get()) {
                this.buffer.replay(rp);
            }
        }

        void replayFinal() {
            for (InnerDisposable<T> rp : (InnerDisposable[]) this.observers.getAndSet(TERMINATED)) {
                this.buffer.replay(rp);
            }
        }
    }

    static final class ReplaySource<T> implements ObservableSource<T> {
        private final BufferSupplier<T> bufferFactory;
        private final AtomicReference<ReplayObserver<T>> curr;

        ReplaySource(AtomicReference<ReplayObserver<T>> curr, BufferSupplier<T> bufferFactory) {
            this.curr = curr;
            this.bufferFactory = bufferFactory;
        }

        public void subscribe(Observer<? super T> child) {
            ReplayObserver<T> r;
            ReplayObserver<T> u;
            InnerDisposable<T> inner;
            while (true) {
                r = (ReplayObserver) this.curr.get();
                if (r != null) {
                    break;
                }
                u = new ReplayObserver(this.bufferFactory.call());
                if (this.curr.compareAndSet(null, u)) {
                    break;
                }
                inner = new InnerDisposable(r, child);
                child.onSubscribe(inner);
                r.add(inner);
                if (inner.isDisposed()) {
                    r.buffer.replay(inner);
                } else {
                    r.remove(inner);
                }
            }
            r = u;
            inner = new InnerDisposable(r, child);
            child.onSubscribe(inner);
            r.add(inner);
            if (inner.isDisposed()) {
                r.buffer.replay(inner);
            } else {
                r.remove(inner);
            }
        }
    }

    static final class ScheduledReplaySupplier<T> implements BufferSupplier<T> {
        private final int bufferSize;
        private final long maxAge;
        private final Scheduler scheduler;
        private final TimeUnit unit;

        ScheduledReplaySupplier(int bufferSize, long maxAge, TimeUnit unit, Scheduler scheduler) {
            this.bufferSize = bufferSize;
            this.maxAge = maxAge;
            this.unit = unit;
            this.scheduler = scheduler;
        }

        public ReplayBuffer<T> call() {
            return new SizeAndTimeBoundReplayBuffer(this.bufferSize, this.maxAge, this.unit, this.scheduler);
        }
    }

    static final class UnBoundedFactory implements BufferSupplier<Object> {
        UnBoundedFactory() {
        }

        public ReplayBuffer<Object> call() {
            return new UnboundedReplayBuffer(16);
        }
    }

    static final class UnboundedReplayBuffer<T> extends ArrayList<Object> implements ReplayBuffer<T> {
        private static final long serialVersionUID = 7063189396499112664L;
        volatile int size;

        UnboundedReplayBuffer(int capacityHint) {
            super(capacityHint);
        }

        public void next(T value) {
            add(NotificationLite.next(value));
            this.size++;
        }

        public void error(Throwable e) {
            add(NotificationLite.error(e));
            this.size++;
        }

        public void complete() {
            add(NotificationLite.complete());
            this.size++;
        }

        public void replay(InnerDisposable<T> output) {
            if (output.getAndIncrement() == 0) {
                Observer child = output.child;
                int missed = 1;
                while (!output.isDisposed()) {
                    int sourceIndex = this.size;
                    Integer destinationIndexObject = (Integer) output.index();
                    int destinationIndex = destinationIndexObject != null ? destinationIndexObject.intValue() : 0;
                    while (destinationIndex < sourceIndex) {
                        if (!NotificationLite.accept(get(destinationIndex), child) && !output.isDisposed()) {
                            destinationIndex++;
                        } else {
                            return;
                        }
                    }
                    output.index = Integer.valueOf(destinationIndex);
                    missed = output.addAndGet(-missed);
                    if (missed == 0) {
                        return;
                    }
                }
            }
        }
    }

    static final class MulticastReplay<R, U> extends Observable<R> {
        private final Callable<? extends ConnectableObservable<U>> connectableFactory;
        private final Function<? super Observable<U>, ? extends ObservableSource<R>> selector;

        MulticastReplay(Callable<? extends ConnectableObservable<U>> connectableFactory, Function<? super Observable<U>, ? extends ObservableSource<R>> selector) {
            this.connectableFactory = connectableFactory;
            this.selector = selector;
        }

        protected void subscribeActual(Observer<? super R> child) {
            try {
                ConnectableObservable<U> co = (ConnectableObservable) ObjectHelper.requireNonNull(this.connectableFactory.call(), "The connectableFactory returned a null ConnectableObservable");
                ObservableSource<R> observable = (ObservableSource) ObjectHelper.requireNonNull(this.selector.apply(co), "The selector returned a null ObservableSource");
                ObserverResourceWrapper<R> srw = new ObserverResourceWrapper(child);
                observable.subscribe(srw);
                co.connect(new DisposeConsumer(srw));
            } catch (Throwable e) {
                Exceptions.throwIfFatal(e);
                EmptyDisposable.error(e, (Observer) child);
            }
        }
    }

    static final class SizeAndTimeBoundReplayBuffer<T> extends BoundedReplayBuffer<T> {
        private static final long serialVersionUID = 3457957419649567404L;
        final int limit;
        final long maxAge;
        final Scheduler scheduler;
        final TimeUnit unit;

        SizeAndTimeBoundReplayBuffer(int limit, long maxAge, TimeUnit unit, Scheduler scheduler) {
            this.scheduler = scheduler;
            this.limit = limit;
            this.maxAge = maxAge;
            this.unit = unit;
        }

        Object enterTransform(Object value) {
            return new Timed(value, this.scheduler.now(this.unit), this.unit);
        }

        Object leaveTransform(Object value) {
            return ((Timed) value).value();
        }

        void truncate() {
            long timeLimit = this.scheduler.now(this.unit) - this.maxAge;
            Node prev = (Node) get();
            Node next = (Node) prev.get();
            int e = 0;
            while (next != null) {
                if (this.size <= this.limit) {
                    if (next.value.time() > timeLimit) {
                        break;
                    }
                    e++;
                    this.size--;
                    prev = next;
                    next = (Node) next.get();
                } else {
                    e++;
                    this.size--;
                    prev = next;
                    next = (Node) next.get();
                }
            }
            if (e != 0) {
                setFirst(prev);
            }
        }

        void truncateFinal() {
            long timeLimit = this.scheduler.now(this.unit) - this.maxAge;
            Node prev = (Node) get();
            int e = 0;
            for (Node next = (Node) prev.get(); next != null && this.size > 1; next = (Node) next.get()) {
                if (next.value.time() > timeLimit) {
                    break;
                }
                e++;
                this.size--;
                prev = next;
            }
            if (e != 0) {
                setFirst(prev);
            }
        }

        Node getHead() {
            long timeLimit = this.scheduler.now(this.unit) - this.maxAge;
            Node prev = (Node) get();
            Node next = (Node) prev.get();
            while (next != null) {
                Timed<?> v = next.value;
                if (!NotificationLite.isComplete(v.value())) {
                    if (!NotificationLite.isError(v.value())) {
                        if (v.time() > timeLimit) {
                            break;
                        }
                        prev = next;
                        next = (Node) next.get();
                    } else {
                        break;
                    }
                }
                break;
            }
            return prev;
        }
    }

    static final class SizeBoundReplayBuffer<T> extends BoundedReplayBuffer<T> {
        private static final long serialVersionUID = -5898283885385201806L;
        final int limit;

        SizeBoundReplayBuffer(int limit) {
            this.limit = limit;
        }

        void truncate() {
            if (this.size > this.limit) {
                removeFirst();
            }
        }
    }

    static final class Replay<T> extends ConnectableObservable<T> {
        private final ConnectableObservable<T> co;
        private final Observable<T> observable;

        Replay(ConnectableObservable<T> co, Observable<T> observable) {
            this.co = co;
            this.observable = observable;
        }

        public void connect(Consumer<? super Disposable> connection) {
            this.co.connect(connection);
        }

        protected void subscribeActual(Observer<? super T> observer) {
            this.observable.subscribe(observer);
        }
    }

    public void connect(io.reactivex.functions.Consumer<? super io.reactivex.disposables.Disposable> r7) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:30:0x005d in {4, 5, 8, 9, 14, 15, 21, 22, 23, 26, 27, 29} preds:[]
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
        r6 = this;
    L_0x0000:
        r0 = r6.current;
        r0 = r0.get();
        r0 = (io.reactivex.internal.operators.observable.ObservableReplay.ReplayObserver) r0;
        if (r0 == 0) goto L_0x0012;
    L_0x000a:
        r1 = r0.isDisposed();
        if (r1 == 0) goto L_0x0011;
    L_0x0010:
        goto L_0x0012;
    L_0x0011:
        goto L_0x0027;
    L_0x0012:
        r1 = r6.bufferFactory;
        r1 = r1.call();
        r2 = new io.reactivex.internal.operators.observable.ObservableReplay$ReplayObserver;
        r2.<init>(r1);
        r3 = r6.current;
        r3 = r3.compareAndSet(r0, r2);
        if (r3 != 0) goto L_0x0026;
    L_0x0025:
        goto L_0x0000;
    L_0x0026:
        r0 = r2;
    L_0x0027:
        r1 = r0.shouldConnect;
        r1 = r1.get();
        r2 = 1;
        r3 = 0;
        if (r1 != 0) goto L_0x003b;
    L_0x0031:
        r1 = r0.shouldConnect;
        r1 = r1.compareAndSet(r3, r2);
        if (r1 == 0) goto L_0x003b;
    L_0x0039:
        r1 = 1;
        goto L_0x003c;
    L_0x003b:
        r1 = 0;
        r7.accept(r0);	 Catch:{ Throwable -> 0x004b }
        if (r1 == 0) goto L_0x0049;
    L_0x0043:
        r2 = r6.source;
        r2.subscribe(r0);
        goto L_0x004a;
    L_0x004a:
        return;
    L_0x004b:
        r4 = move-exception;
        if (r1 == 0) goto L_0x0054;
    L_0x004e:
        r5 = r0.shouldConnect;
        r5.compareAndSet(r2, r3);
        goto L_0x0055;
    L_0x0055:
        io.reactivex.exceptions.Exceptions.throwIfFatal(r4);
        r2 = io.reactivex.internal.util.ExceptionHelper.wrapOrThrow(r4);
        throw r2;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.observable.ObservableReplay.connect(io.reactivex.functions.Consumer):void");
    }

    public static <U, R> Observable<R> multicastSelector(Callable<? extends ConnectableObservable<U>> connectableFactory, Function<? super Observable<U>, ? extends ObservableSource<R>> selector) {
        return RxJavaPlugins.onAssembly(new MulticastReplay(connectableFactory, selector));
    }

    public static <T> ConnectableObservable<T> observeOn(ConnectableObservable<T> co, Scheduler scheduler) {
        return RxJavaPlugins.onAssembly(new Replay(co, co.observeOn(scheduler)));
    }

    public static <T> ConnectableObservable<T> createFrom(ObservableSource<? extends T> source) {
        return create((ObservableSource) source, DEFAULT_UNBOUNDED_FACTORY);
    }

    public static <T> ConnectableObservable<T> create(ObservableSource<T> source, int bufferSize) {
        if (bufferSize == Integer.MAX_VALUE) {
            return createFrom(source);
        }
        return create((ObservableSource) source, new ReplayBufferSupplier(bufferSize));
    }

    public static <T> ConnectableObservable<T> create(ObservableSource<T> source, long maxAge, TimeUnit unit, Scheduler scheduler) {
        return create(source, maxAge, unit, scheduler, Integer.MAX_VALUE);
    }

    public static <T> ConnectableObservable<T> create(ObservableSource<T> source, long maxAge, TimeUnit unit, Scheduler scheduler, int bufferSize) {
        return create((ObservableSource) source, new ScheduledReplaySupplier(bufferSize, maxAge, unit, scheduler));
    }

    static <T> ConnectableObservable<T> create(ObservableSource<T> source, BufferSupplier<T> bufferFactory) {
        AtomicReference<ReplayObserver<T>> curr = new AtomicReference();
        return RxJavaPlugins.onAssembly(new ObservableReplay(new ReplaySource(curr, bufferFactory), source, curr, bufferFactory));
    }

    private ObservableReplay(ObservableSource<T> onSubscribe, ObservableSource<T> source, AtomicReference<ReplayObserver<T>> current, BufferSupplier<T> bufferFactory) {
        this.onSubscribe = onSubscribe;
        this.source = source;
        this.current = current;
        this.bufferFactory = bufferFactory;
    }

    public ObservableSource<T> source() {
        return this.source;
    }

    public void resetIf(Disposable connectionObject) {
        this.current.compareAndSet((ReplayObserver) connectionObject, null);
    }

    protected void subscribeActual(Observer<? super T> observer) {
        this.onSubscribe.subscribe(observer);
    }
}
