package io.reactivex.processors;

import io.reactivex.Scheduler;
import io.reactivex.annotations.CheckReturnValue;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.BackpressureHelper;
import io.reactivex.plugins.RxJavaPlugins;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class ReplayProcessor<T> extends FlowableProcessor<T> {
    static final ReplaySubscription[] EMPTY = new ReplaySubscription[0];
    private static final Object[] EMPTY_ARRAY = new Object[0];
    static final ReplaySubscription[] TERMINATED = new ReplaySubscription[0];
    final ReplayBuffer<T> buffer;
    boolean done;
    final AtomicReference<ReplaySubscription<T>[]> subscribers = new AtomicReference(EMPTY);

    static final class Node<T> extends AtomicReference<Node<T>> {
        private static final long serialVersionUID = 6404226426336033100L;
        final T value;

        Node(T value) {
            this.value = value;
        }
    }

    interface ReplayBuffer<T> {
        void complete();

        void error(Throwable th);

        Throwable getError();

        @Nullable
        T getValue();

        T[] getValues(T[] tArr);

        boolean isDone();

        void next(T t);

        void replay(ReplaySubscription<T> replaySubscription);

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

    static final class ReplaySubscription<T> extends AtomicInteger implements Subscription {
        private static final long serialVersionUID = 466549804534799122L;
        volatile boolean cancelled;
        final Subscriber<? super T> downstream;
        long emitted;
        Object index;
        final AtomicLong requested = new AtomicLong();
        final ReplayProcessor<T> state;

        ReplaySubscription(Subscriber<? super T> actual, ReplayProcessor<T> state) {
            this.downstream = actual;
            this.state = state;
        }

        public void request(long n) {
            if (SubscriptionHelper.validate(n)) {
                BackpressureHelper.add(this.requested, n);
                this.state.buffer.replay(this);
            }
        }

        public void cancel() {
            if (!this.cancelled) {
                this.cancelled = true;
                this.state.remove(this);
            }
        }
    }

    static final class SizeAndTimeBoundReplayBuffer<T> implements ReplayBuffer<T> {
        volatile boolean done;
        Throwable error;
        volatile TimedNode<T> head;
        final long maxAge;
        final int maxSize;
        final Scheduler scheduler;
        int size;
        TimedNode<T> tail;
        final TimeUnit unit;

        SizeAndTimeBoundReplayBuffer(int maxSize, long maxAge, TimeUnit unit, Scheduler scheduler) {
            this.maxSize = ObjectHelper.verifyPositive(maxSize, "maxSize");
            this.maxAge = ObjectHelper.verifyPositive(maxAge, "maxAge");
            this.unit = (TimeUnit) ObjectHelper.requireNonNull((Object) unit, "unit is null");
            this.scheduler = (Scheduler) ObjectHelper.requireNonNull((Object) scheduler, "scheduler is null");
            TimedNode<T> h = new TimedNode(null, 0);
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
            TimedNode<T> h = this.head;
            while (true) {
                TimedNode<T> next = (TimedNode) h.get();
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
            r3 = (io.reactivex.processors.ReplayProcessor.TimedNode) r3;
            r4 = 0;
            r6 = 0;
            if (r3 != 0) goto L_0x0027;
        L_0x0018:
            r7 = r2.value;
            if (r7 == 0) goto L_0x0024;
        L_0x001c:
            r7 = new io.reactivex.processors.ReplayProcessor$TimedNode;
            r7.<init>(r6, r4);
            r10.head = r7;
            goto L_0x0044;
        L_0x0024:
            r10.head = r2;
            goto L_0x0044;
        L_0x0027:
            r7 = r3.time;
            r9 = (r7 > r0 ? 1 : (r7 == r0 ? 0 : -1));
            if (r9 <= 0) goto L_0x0045;
        L_0x002d:
            r7 = r2.value;
            if (r7 == 0) goto L_0x0041;
        L_0x0031:
            r7 = new io.reactivex.processors.ReplayProcessor$TimedNode;
            r7.<init>(r6, r4);
            r4 = r7;
            r5 = r2.get();
            r4.lazySet(r5);
            r10.head = r4;
            goto L_0x0044;
        L_0x0041:
            r10.head = r2;
        L_0x0044:
            return;
        L_0x0045:
            r2 = r3;
            goto L_0x000d;
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.processors.ReplayProcessor.SizeAndTimeBoundReplayBuffer.trimFinal():void");
        }

        public void trimHead() {
            if (this.head.value != null) {
                TimedNode<T> n = new TimedNode(null, 0);
                n.lazySet(this.head.get());
                this.head = n;
            }
        }

        public void next(T value) {
            TimedNode<T> n = new TimedNode(value, this.scheduler.now(this.unit));
            TimedNode<T> t = this.tail;
            this.tail = n;
            this.size++;
            t.set(n);
            trim();
        }

        public void error(Throwable ex) {
            trimFinal();
            this.error = ex;
            this.done = true;
        }

        public void complete() {
            trimFinal();
            this.done = true;
        }

        @Nullable
        public T getValue() {
            TimedNode<T> h = this.head;
            while (true) {
                TimedNode<T> next = (TimedNode) h.get();
                if (next == null) {
                    break;
                }
                h = next;
            }
            if (h.time < this.scheduler.now(this.unit) - this.maxAge) {
                return null;
            }
            return h.value;
        }

        public T[] getValues(T[] array) {
            TimedNode<T> h = getHead();
            int s = size(h);
            if (s != 0) {
                if (array.length < s) {
                    array = (Object[]) Array.newInstance(array.getClass().getComponentType(), s);
                }
                int i = 0;
                while (i != s) {
                    TimedNode<T> next = (TimedNode) h.get();
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

        TimedNode<T> getHead() {
            TimedNode<T> index = this.head;
            long limit = this.scheduler.now(this.unit) - this.maxAge;
            TimedNode<T> next = (TimedNode) index.get();
            while (next != null) {
                if (next.time > limit) {
                    break;
                }
                index = next;
                next = (TimedNode) index.get();
            }
            return index;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void replay(io.reactivex.processors.ReplayProcessor.ReplaySubscription<T> r13) {
            /*
            r12 = this;
            r0 = r13.getAndIncrement();
            if (r0 == 0) goto L_0x0007;
        L_0x0006:
            return;
        L_0x0007:
            r0 = 1;
            r1 = r13.downstream;
            r2 = r13.index;
            r2 = (io.reactivex.processors.ReplayProcessor.TimedNode) r2;
            if (r2 != 0) goto L_0x0015;
        L_0x0010:
            r2 = r12.getHead();
            goto L_0x0016;
        L_0x0016:
            r3 = r13.emitted;
        L_0x0018:
            r5 = r13.requested;
            r5 = r5.get();
        L_0x001e:
            r7 = 1;
            r8 = 0;
            r9 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1));
            if (r9 == 0) goto L_0x005a;
        L_0x0024:
            r9 = r13.cancelled;
            if (r9 == 0) goto L_0x002b;
        L_0x0028:
            r13.index = r8;
            return;
        L_0x002b:
            r9 = r12.done;
            r10 = r2.get();
            r10 = (io.reactivex.processors.ReplayProcessor.TimedNode) r10;
            if (r10 != 0) goto L_0x0037;
        L_0x0035:
            r11 = 1;
            goto L_0x0038;
        L_0x0037:
            r11 = 0;
        L_0x0038:
            if (r9 == 0) goto L_0x004c;
        L_0x003a:
            if (r11 == 0) goto L_0x004c;
        L_0x003c:
            r13.index = r8;
            r13.cancelled = r7;
            r7 = r12.error;
            if (r7 != 0) goto L_0x0048;
        L_0x0044:
            r1.onComplete();
            goto L_0x004b;
        L_0x0048:
            r1.onError(r7);
        L_0x004b:
            return;
            if (r11 == 0) goto L_0x0050;
        L_0x004f:
            goto L_0x005b;
        L_0x0050:
            r7 = r10.value;
            r1.onNext(r7);
            r7 = 1;
            r3 = r3 + r7;
            r2 = r10;
            goto L_0x001e;
        L_0x005b:
            r9 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1));
            if (r9 != 0) goto L_0x0081;
        L_0x005f:
            r9 = r13.cancelled;
            if (r9 == 0) goto L_0x0066;
        L_0x0063:
            r13.index = r8;
            return;
        L_0x0066:
            r9 = r12.done;
            if (r9 == 0) goto L_0x0080;
        L_0x006a:
            r10 = r2.get();
            if (r10 != 0) goto L_0x0080;
        L_0x0070:
            r13.index = r8;
            r13.cancelled = r7;
            r7 = r12.error;
            if (r7 != 0) goto L_0x007c;
        L_0x0078:
            r1.onComplete();
            goto L_0x007f;
        L_0x007c:
            r1.onError(r7);
        L_0x007f:
            return;
        L_0x0080:
            goto L_0x0082;
        L_0x0082:
            r13.index = r2;
            r13.emitted = r3;
            r7 = -r0;
            r0 = r13.addAndGet(r7);
            if (r0 != 0) goto L_0x008f;
            return;
            goto L_0x0018;
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.processors.ReplayProcessor.SizeAndTimeBoundReplayBuffer.replay(io.reactivex.processors.ReplayProcessor$ReplaySubscription):void");
        }

        public int size() {
            return size(getHead());
        }

        int size(TimedNode<T> h) {
            int s = 0;
            while (s != Integer.MAX_VALUE) {
                TimedNode<T> next = (TimedNode) h.get();
                if (next == null) {
                    break;
                }
                s++;
                h = next;
            }
            return s;
        }

        public Throwable getError() {
            return this.error;
        }

        public boolean isDone() {
            return this.done;
        }
    }

    static final class SizeBoundReplayBuffer<T> implements ReplayBuffer<T> {
        volatile boolean done;
        Throwable error;
        volatile Node<T> head;
        final int maxSize;
        int size;
        Node<T> tail;

        SizeBoundReplayBuffer(int maxSize) {
            this.maxSize = ObjectHelper.verifyPositive(maxSize, "maxSize");
            Node<T> h = new Node(null);
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

        public void next(T value) {
            Node<T> n = new Node(value);
            Node<T> t = this.tail;
            this.tail = n;
            this.size++;
            t.set(n);
            trim();
        }

        public void error(Throwable ex) {
            this.error = ex;
            trimHead();
            this.done = true;
        }

        public void complete() {
            trimHead();
            this.done = true;
        }

        public void trimHead() {
            if (this.head.value != null) {
                Node<T> n = new Node(null);
                n.lazySet(this.head.get());
                this.head = n;
            }
        }

        public boolean isDone() {
            return this.done;
        }

        public Throwable getError() {
            return this.error;
        }

        public T getValue() {
            Node<T> h = this.head;
            while (true) {
                Node<T> n = (Node) h.get();
                if (n == null) {
                    return h.value;
                }
                h = n;
            }
        }

        public T[] getValues(T[] array) {
            int s = 0;
            Node<T> h = this.head;
            Node<T> h0 = h;
            while (true) {
                Node<T> next = (Node) h0.get();
                if (next == null) {
                    break;
                }
                s++;
                h0 = next;
            }
            if (array.length < s) {
                array = (Object[]) Array.newInstance(array.getClass().getComponentType(), s);
            }
            for (int j = 0; j < s; j++) {
                h = (Node) h.get();
                array[j] = h.value;
            }
            if (array.length > s) {
                array[s] = null;
            }
            return array;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void replay(io.reactivex.processors.ReplayProcessor.ReplaySubscription<T> r13) {
            /*
            r12 = this;
            r0 = r13.getAndIncrement();
            if (r0 == 0) goto L_0x0007;
        L_0x0006:
            return;
        L_0x0007:
            r0 = 1;
            r1 = r13.downstream;
            r2 = r13.index;
            r2 = (io.reactivex.processors.ReplayProcessor.Node) r2;
            if (r2 != 0) goto L_0x0013;
        L_0x0010:
            r2 = r12.head;
            goto L_0x0014;
        L_0x0014:
            r3 = r13.emitted;
        L_0x0016:
            r5 = r13.requested;
            r5 = r5.get();
        L_0x001c:
            r7 = 1;
            r8 = 0;
            r9 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1));
            if (r9 == 0) goto L_0x0058;
        L_0x0022:
            r9 = r13.cancelled;
            if (r9 == 0) goto L_0x0029;
        L_0x0026:
            r13.index = r8;
            return;
        L_0x0029:
            r9 = r12.done;
            r10 = r2.get();
            r10 = (io.reactivex.processors.ReplayProcessor.Node) r10;
            if (r10 != 0) goto L_0x0035;
        L_0x0033:
            r11 = 1;
            goto L_0x0036;
        L_0x0035:
            r11 = 0;
        L_0x0036:
            if (r9 == 0) goto L_0x004a;
        L_0x0038:
            if (r11 == 0) goto L_0x004a;
        L_0x003a:
            r13.index = r8;
            r13.cancelled = r7;
            r7 = r12.error;
            if (r7 != 0) goto L_0x0046;
        L_0x0042:
            r1.onComplete();
            goto L_0x0049;
        L_0x0046:
            r1.onError(r7);
        L_0x0049:
            return;
            if (r11 == 0) goto L_0x004e;
        L_0x004d:
            goto L_0x0059;
        L_0x004e:
            r7 = r10.value;
            r1.onNext(r7);
            r7 = 1;
            r3 = r3 + r7;
            r2 = r10;
            goto L_0x001c;
        L_0x0059:
            r9 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1));
            if (r9 != 0) goto L_0x007f;
        L_0x005d:
            r9 = r13.cancelled;
            if (r9 == 0) goto L_0x0064;
        L_0x0061:
            r13.index = r8;
            return;
        L_0x0064:
            r9 = r12.done;
            if (r9 == 0) goto L_0x007e;
        L_0x0068:
            r10 = r2.get();
            if (r10 != 0) goto L_0x007e;
        L_0x006e:
            r13.index = r8;
            r13.cancelled = r7;
            r7 = r12.error;
            if (r7 != 0) goto L_0x007a;
        L_0x0076:
            r1.onComplete();
            goto L_0x007d;
        L_0x007a:
            r1.onError(r7);
        L_0x007d:
            return;
        L_0x007e:
            goto L_0x0080;
        L_0x0080:
            r13.index = r2;
            r13.emitted = r3;
            r7 = -r0;
            r0 = r13.addAndGet(r7);
            if (r0 != 0) goto L_0x008d;
            return;
            goto L_0x0016;
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.processors.ReplayProcessor.SizeBoundReplayBuffer.replay(io.reactivex.processors.ReplayProcessor$ReplaySubscription):void");
        }

        public int size() {
            int s = 0;
            Node<T> h = this.head;
            while (s != Integer.MAX_VALUE) {
                Node<T> next = (Node) h.get();
                if (next == null) {
                    break;
                }
                s++;
                h = next;
            }
            return s;
        }
    }

    static final class UnboundedReplayBuffer<T> implements ReplayBuffer<T> {
        final List<T> buffer;
        volatile boolean done;
        Throwable error;
        volatile int size;

        UnboundedReplayBuffer(int capacityHint) {
            this.buffer = new ArrayList(ObjectHelper.verifyPositive(capacityHint, "capacityHint"));
        }

        public void next(T value) {
            this.buffer.add(value);
            this.size++;
        }

        public void error(Throwable ex) {
            this.error = ex;
            this.done = true;
        }

        public void complete() {
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
            return this.buffer.get(s - 1);
        }

        public T[] getValues(T[] array) {
            int s = this.size;
            if (s == 0) {
                if (array.length != 0) {
                    array[0] = null;
                }
                return array;
            }
            List<T> b = this.buffer;
            if (array.length < s) {
                array = (Object[]) Array.newInstance(array.getClass().getComponentType(), s);
            }
            for (int i = 0; i < s; i++) {
                array[i] = b.get(i);
            }
            if (array.length > s) {
                array[s] = null;
            }
            return array;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void replay(io.reactivex.processors.ReplayProcessor.ReplaySubscription<T> r14) {
            /*
            r13 = this;
            r0 = r14.getAndIncrement();
            if (r0 == 0) goto L_0x0007;
        L_0x0006:
            return;
        L_0x0007:
            r0 = 1;
            r1 = r13.buffer;
            r2 = r14.downstream;
            r3 = r14.index;
            r3 = (java.lang.Integer) r3;
            if (r3 == 0) goto L_0x0017;
        L_0x0012:
            r4 = r3.intValue();
            goto L_0x001f;
        L_0x0017:
            r4 = 0;
            r5 = 0;
            r5 = java.lang.Integer.valueOf(r5);
            r14.index = r5;
        L_0x001f:
            r5 = r14.emitted;
        L_0x0021:
            r7 = r14.requested;
            r7 = r7.get();
        L_0x0027:
            r9 = 1;
            r10 = 0;
            r11 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1));
            if (r11 == 0) goto L_0x005d;
        L_0x002d:
            r11 = r14.cancelled;
            if (r11 == 0) goto L_0x0034;
        L_0x0031:
            r14.index = r10;
            return;
        L_0x0034:
            r11 = r13.done;
            r12 = r13.size;
            if (r11 == 0) goto L_0x004c;
        L_0x003a:
            if (r4 != r12) goto L_0x004c;
        L_0x003c:
            r14.index = r10;
            r14.cancelled = r9;
            r9 = r13.error;
            if (r9 != 0) goto L_0x0048;
        L_0x0044:
            r2.onComplete();
            goto L_0x004b;
        L_0x0048:
            r2.onError(r9);
        L_0x004b:
            return;
            if (r4 != r12) goto L_0x0050;
        L_0x004f:
            goto L_0x005e;
        L_0x0050:
            r9 = r1.get(r4);
            r2.onNext(r9);
            r4 = r4 + 1;
            r9 = 1;
            r5 = r5 + r9;
            goto L_0x0027;
        L_0x005e:
            r11 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1));
            if (r11 != 0) goto L_0x0082;
        L_0x0062:
            r11 = r14.cancelled;
            if (r11 == 0) goto L_0x0069;
        L_0x0066:
            r14.index = r10;
            return;
        L_0x0069:
            r11 = r13.done;
            r12 = r13.size;
            if (r11 == 0) goto L_0x0081;
        L_0x006f:
            if (r4 != r12) goto L_0x0081;
        L_0x0071:
            r14.index = r10;
            r14.cancelled = r9;
            r9 = r13.error;
            if (r9 != 0) goto L_0x007d;
        L_0x0079:
            r2.onComplete();
            goto L_0x0080;
        L_0x007d:
            r2.onError(r9);
        L_0x0080:
            return;
        L_0x0081:
            goto L_0x0083;
        L_0x0083:
            r9 = java.lang.Integer.valueOf(r4);
            r14.index = r9;
            r14.emitted = r5;
            r9 = -r0;
            r0 = r14.addAndGet(r9);
            if (r0 != 0) goto L_0x0094;
            return;
            goto L_0x0021;
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.processors.ReplayProcessor.UnboundedReplayBuffer.replay(io.reactivex.processors.ReplayProcessor$ReplaySubscription):void");
        }

        public int size() {
            return this.size;
        }

        public boolean isDone() {
            return this.done;
        }

        public Throwable getError() {
            return this.error;
        }
    }

    @CheckReturnValue
    @NonNull
    public static <T> ReplayProcessor<T> create() {
        return new ReplayProcessor(new UnboundedReplayBuffer(16));
    }

    @CheckReturnValue
    @NonNull
    public static <T> ReplayProcessor<T> create(int capacityHint) {
        return new ReplayProcessor(new UnboundedReplayBuffer(capacityHint));
    }

    @CheckReturnValue
    @NonNull
    public static <T> ReplayProcessor<T> createWithSize(int maxSize) {
        return new ReplayProcessor(new SizeBoundReplayBuffer(maxSize));
    }

    static <T> ReplayProcessor<T> createUnbounded() {
        return new ReplayProcessor(new SizeBoundReplayBuffer(Integer.MAX_VALUE));
    }

    @CheckReturnValue
    @NonNull
    public static <T> ReplayProcessor<T> createWithTime(long maxAge, TimeUnit unit, Scheduler scheduler) {
        return new ReplayProcessor(new SizeAndTimeBoundReplayBuffer(Integer.MAX_VALUE, maxAge, unit, scheduler));
    }

    @CheckReturnValue
    @NonNull
    public static <T> ReplayProcessor<T> createWithTimeAndSize(long maxAge, TimeUnit unit, Scheduler scheduler, int maxSize) {
        return new ReplayProcessor(new SizeAndTimeBoundReplayBuffer(maxSize, maxAge, unit, scheduler));
    }

    ReplayProcessor(ReplayBuffer<T> buffer) {
        this.buffer = buffer;
    }

    protected void subscribeActual(Subscriber<? super T> s) {
        ReplaySubscription<T> rs = new ReplaySubscription(s, this);
        s.onSubscribe(rs);
        if (add(rs)) {
            if (rs.cancelled) {
                remove(rs);
                return;
            }
        }
        this.buffer.replay(rs);
    }

    public void onSubscribe(Subscription s) {
        if (this.done) {
            s.cancel();
        } else {
            s.request(Long.MAX_VALUE);
        }
    }

    public void onNext(T t) {
        ObjectHelper.requireNonNull((Object) t, "onNext called with null. Null values are generally not allowed in 2.x operators and sources.");
        if (!this.done) {
            ReplayBuffer<T> b = this.buffer;
            b.next(t);
            for (ReplaySubscription<T> rs : (ReplaySubscription[]) this.subscribers.get()) {
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
        ReplayBuffer<T> b = this.buffer;
        b.error(t);
        for (ReplaySubscription<T> rs : (ReplaySubscription[]) this.subscribers.getAndSet(TERMINATED)) {
            b.replay(rs);
        }
    }

    public void onComplete() {
        if (!this.done) {
            this.done = true;
            ReplayBuffer<T> b = this.buffer;
            b.complete();
            for (ReplaySubscription<T> rs : (ReplaySubscription[]) this.subscribers.getAndSet(TERMINATED)) {
                b.replay(rs);
            }
        }
    }

    public boolean hasSubscribers() {
        return ((ReplaySubscription[]) this.subscribers.get()).length != 0;
    }

    int subscriberCount() {
        return ((ReplaySubscription[]) this.subscribers.get()).length;
    }

    @Nullable
    public Throwable getThrowable() {
        ReplayBuffer<T> b = this.buffer;
        if (b.isDone()) {
            return b.getError();
        }
        return null;
    }

    public void cleanupBuffer() {
        this.buffer.trimHead();
    }

    public T getValue() {
        return this.buffer.getValue();
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
        ReplayBuffer<T> b = this.buffer;
        return b.isDone() && b.getError() == null;
    }

    public boolean hasThrowable() {
        ReplayBuffer<T> b = this.buffer;
        return b.isDone() && b.getError() != null;
    }

    public boolean hasValue() {
        return this.buffer.size() != 0;
    }

    int size() {
        return this.buffer.size();
    }

    boolean add(ReplaySubscription<T> rs) {
        while (true) {
            ReplaySubscription[] a = (ReplaySubscription[]) this.subscribers.get();
            if (a == TERMINATED) {
                return false;
            }
            int len = a.length;
            ReplaySubscription<T>[] b = new ReplaySubscription[(len + 1)];
            System.arraycopy(a, 0, b, 0, len);
            b[len] = rs;
            if (this.subscribers.compareAndSet(a, b)) {
                return true;
            }
        }
    }

    void remove(ReplaySubscription<T> rs) {
        while (true) {
            ReplaySubscription[] a = (ReplaySubscription[]) this.subscribers.get();
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
                    ReplaySubscription<T>[] b;
                    if (len == 1) {
                        b = EMPTY;
                    } else {
                        ReplaySubscription<T>[] b2 = new ReplaySubscription[(len - 1)];
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
        }
    }
}
