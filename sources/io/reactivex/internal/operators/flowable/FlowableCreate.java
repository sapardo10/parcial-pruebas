package io.reactivex.internal.operators.flowable;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.exceptions.MissingBackpressureException;
import io.reactivex.functions.Cancellable;
import io.reactivex.internal.disposables.CancellableDisposable;
import io.reactivex.internal.disposables.SequentialDisposable;
import io.reactivex.internal.fuseable.SimplePlainQueue;
import io.reactivex.internal.queue.SpscLinkedArrayQueue;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.AtomicThrowable;
import io.reactivex.internal.util.BackpressureHelper;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class FlowableCreate<T> extends Flowable<T> {
    final BackpressureStrategy backpressure;
    final FlowableOnSubscribe<T> source;

    static abstract class BaseEmitter<T> extends AtomicLong implements FlowableEmitter<T>, Subscription {
        private static final long serialVersionUID = 7326289992464377023L;
        final Subscriber<? super T> downstream;
        final SequentialDisposable serial = new SequentialDisposable();

        BaseEmitter(Subscriber<? super T> downstream) {
            this.downstream = downstream;
        }

        public void onComplete() {
            complete();
        }

        protected void complete() {
            if (!isCancelled()) {
                try {
                    this.downstream.onComplete();
                } finally {
                    this.serial.dispose();
                }
            }
        }

        public final void onError(Throwable e) {
            if (!tryOnError(e)) {
                RxJavaPlugins.onError(e);
            }
        }

        public boolean tryOnError(Throwable e) {
            return error(e);
        }

        protected boolean error(Throwable e) {
            if (e == null) {
                e = new NullPointerException("onError called with null. Null values are generally not allowed in 2.x operators and sources.");
            }
            if (isCancelled()) {
                return false;
            }
            try {
                this.downstream.onError(e);
                return true;
            } finally {
                this.serial.dispose();
            }
        }

        public final void cancel() {
            this.serial.dispose();
            onUnsubscribed();
        }

        void onUnsubscribed() {
        }

        public final boolean isCancelled() {
            return this.serial.isDisposed();
        }

        public final void request(long n) {
            if (SubscriptionHelper.validate(n)) {
                BackpressureHelper.add(this, n);
                onRequested();
            }
        }

        void onRequested() {
        }

        public final void setDisposable(Disposable d) {
            this.serial.update(d);
        }

        public final void setCancellable(Cancellable c) {
            setDisposable(new CancellableDisposable(c));
        }

        public final long requested() {
            return get();
        }

        public final FlowableEmitter<T> serialize() {
            return new SerializedEmitter(this);
        }

        public String toString() {
            return String.format("%s{%s}", new Object[]{getClass().getSimpleName(), super.toString()});
        }
    }

    static final class SerializedEmitter<T> extends AtomicInteger implements FlowableEmitter<T> {
        private static final long serialVersionUID = 4883307006032401862L;
        volatile boolean done;
        final BaseEmitter<T> emitter;
        final AtomicThrowable error = new AtomicThrowable();
        final SimplePlainQueue<T> queue = new SpscLinkedArrayQueue(16);

        SerializedEmitter(BaseEmitter<T> emitter) {
            this.emitter = emitter;
        }

        public void onNext(T t) {
            if (!this.emitter.isCancelled()) {
                if (!this.done) {
                    if (t == null) {
                        onError(new NullPointerException("onNext called with null. Null values are generally not allowed in 2.x operators and sources."));
                        return;
                    }
                    if (get() == 0 && compareAndSet(0, 1)) {
                        this.emitter.onNext(t);
                        if (decrementAndGet() == 0) {
                            return;
                        }
                    } else {
                        SimplePlainQueue<T> q = this.queue;
                        synchronized (q) {
                            q.offer(t);
                        }
                        if (getAndIncrement() != 0) {
                            return;
                        }
                    }
                    drainLoop();
                }
            }
        }

        public void onError(Throwable t) {
            if (!tryOnError(t)) {
                RxJavaPlugins.onError(t);
            }
        }

        public boolean tryOnError(Throwable t) {
            if (!this.emitter.isCancelled()) {
                if (!this.done) {
                    if (t == null) {
                        t = new NullPointerException("onError called with null. Null values are generally not allowed in 2.x operators and sources.");
                    }
                    if (!this.error.addThrowable(t)) {
                        return false;
                    }
                    this.done = true;
                    drain();
                    return true;
                }
            }
            return false;
        }

        public void onComplete() {
            if (!this.emitter.isCancelled()) {
                if (!this.done) {
                    this.done = true;
                    drain();
                }
            }
        }

        void drain() {
            if (getAndIncrement() == 0) {
                drainLoop();
            }
        }

        void drainLoop() {
            BaseEmitter<T> e = this.emitter;
            SimplePlainQueue<T> q = this.queue;
            AtomicThrowable error = this.error;
            int missed = 1;
            while (!e.isCancelled()) {
                if (error.get() != null) {
                    q.clear();
                    e.onError(error.terminate());
                    return;
                }
                boolean d = this.done;
                T v = q.poll();
                boolean empty = v == null;
                if (d && empty) {
                    e.onComplete();
                    return;
                } else if (empty) {
                    missed = addAndGet(-missed);
                    if (missed == 0) {
                        return;
                    }
                } else {
                    e.onNext(v);
                }
            }
            q.clear();
        }

        public void setDisposable(Disposable d) {
            this.emitter.setDisposable(d);
        }

        public void setCancellable(Cancellable c) {
            this.emitter.setCancellable(c);
        }

        public long requested() {
            return this.emitter.requested();
        }

        public boolean isCancelled() {
            return this.emitter.isCancelled();
        }

        public FlowableEmitter<T> serialize() {
            return this;
        }

        public String toString() {
            return this.emitter.toString();
        }
    }

    static final class BufferAsyncEmitter<T> extends BaseEmitter<T> {
        private static final long serialVersionUID = 2427151001689639875L;
        volatile boolean done;
        Throwable error;
        final SpscLinkedArrayQueue<T> queue;
        final AtomicInteger wip = new AtomicInteger();

        BufferAsyncEmitter(Subscriber<? super T> actual, int capacityHint) {
            super(actual);
            this.queue = new SpscLinkedArrayQueue(capacityHint);
        }

        public void onNext(T t) {
            if (!this.done) {
                if (!isCancelled()) {
                    if (t == null) {
                        onError(new NullPointerException("onNext called with null. Null values are generally not allowed in 2.x operators and sources."));
                        return;
                    }
                    this.queue.offer(t);
                    drain();
                }
            }
        }

        public boolean tryOnError(Throwable e) {
            if (!this.done) {
                if (!isCancelled()) {
                    if (e == null) {
                        e = new NullPointerException("onError called with null. Null values are generally not allowed in 2.x operators and sources.");
                    }
                    this.error = e;
                    this.done = true;
                    drain();
                    return true;
                }
            }
            return false;
        }

        public void onComplete() {
            this.done = true;
            drain();
        }

        void onRequested() {
            drain();
        }

        void onUnsubscribed() {
            if (this.wip.getAndIncrement() == 0) {
                this.queue.clear();
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        void drain() {
            /*
            r12 = this;
            r0 = r12.wip;
            r0 = r0.getAndIncrement();
            if (r0 == 0) goto L_0x0009;
        L_0x0008:
            return;
        L_0x0009:
            r0 = 1;
            r1 = r12.downstream;
            r2 = r12.queue;
        L_0x000e:
            r3 = r12.get();
            r5 = 0;
        L_0x0014:
            r7 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1));
            if (r7 == 0) goto L_0x0048;
        L_0x0018:
            r7 = r12.isCancelled();
            if (r7 == 0) goto L_0x0022;
        L_0x001e:
            r2.clear();
            return;
        L_0x0022:
            r7 = r12.done;
            r8 = r2.poll();
            if (r8 != 0) goto L_0x002c;
        L_0x002a:
            r9 = 1;
            goto L_0x002d;
        L_0x002c:
            r9 = 0;
        L_0x002d:
            if (r7 == 0) goto L_0x003d;
        L_0x002f:
            if (r9 == 0) goto L_0x003d;
        L_0x0031:
            r10 = r12.error;
            if (r10 == 0) goto L_0x0039;
        L_0x0035:
            r12.error(r10);
            goto L_0x003c;
        L_0x0039:
            r12.complete();
        L_0x003c:
            return;
            if (r9 == 0) goto L_0x0041;
        L_0x0040:
            goto L_0x0049;
        L_0x0041:
            r1.onNext(r8);
            r10 = 1;
            r5 = r5 + r10;
            goto L_0x0014;
        L_0x0049:
            r7 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1));
            if (r7 != 0) goto L_0x006e;
        L_0x004d:
            r7 = r12.isCancelled();
            if (r7 == 0) goto L_0x0057;
        L_0x0053:
            r2.clear();
            return;
        L_0x0057:
            r7 = r12.done;
            r8 = r2.isEmpty();
            if (r7 == 0) goto L_0x006d;
        L_0x005f:
            if (r8 == 0) goto L_0x006d;
        L_0x0061:
            r9 = r12.error;
            if (r9 == 0) goto L_0x0069;
        L_0x0065:
            r12.error(r9);
            goto L_0x006c;
        L_0x0069:
            r12.complete();
        L_0x006c:
            return;
        L_0x006d:
            goto L_0x006f;
        L_0x006f:
            r7 = 0;
            r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1));
            if (r9 == 0) goto L_0x0079;
        L_0x0075:
            io.reactivex.internal.util.BackpressureHelper.produced(r12, r5);
            goto L_0x007a;
        L_0x007a:
            r7 = r12.wip;
            r8 = -r0;
            r0 = r7.addAndGet(r8);
            if (r0 != 0) goto L_0x0085;
            return;
            goto L_0x000e;
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.flowable.FlowableCreate.BufferAsyncEmitter.drain():void");
        }
    }

    static final class LatestAsyncEmitter<T> extends BaseEmitter<T> {
        private static final long serialVersionUID = 4023437720691792495L;
        volatile boolean done;
        Throwable error;
        final AtomicReference<T> queue = new AtomicReference();
        final AtomicInteger wip = new AtomicInteger();

        LatestAsyncEmitter(Subscriber<? super T> downstream) {
            super(downstream);
        }

        public void onNext(T t) {
            if (!this.done) {
                if (!isCancelled()) {
                    if (t == null) {
                        onError(new NullPointerException("onNext called with null. Null values are generally not allowed in 2.x operators and sources."));
                        return;
                    }
                    this.queue.set(t);
                    drain();
                }
            }
        }

        public boolean tryOnError(Throwable e) {
            if (!this.done) {
                if (!isCancelled()) {
                    if (e == null) {
                        onError(new NullPointerException("onError called with null. Null values are generally not allowed in 2.x operators and sources."));
                    }
                    this.error = e;
                    this.done = true;
                    drain();
                    return true;
                }
            }
            return false;
        }

        public void onComplete() {
            this.done = true;
            drain();
        }

        void onRequested() {
            drain();
        }

        void onUnsubscribed() {
            if (this.wip.getAndIncrement() == 0) {
                this.queue.lazySet(null);
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        void drain() {
            /*
            r13 = this;
            r0 = r13.wip;
            r0 = r0.getAndIncrement();
            if (r0 == 0) goto L_0x0009;
        L_0x0008:
            return;
        L_0x0009:
            r0 = 1;
            r1 = r13.downstream;
            r2 = r13.queue;
        L_0x000e:
            r3 = r13.get();
            r5 = 0;
        L_0x0014:
            r7 = 1;
            r8 = 0;
            r9 = 0;
            r10 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1));
            if (r10 == 0) goto L_0x004b;
        L_0x001b:
            r10 = r13.isCancelled();
            if (r10 == 0) goto L_0x0025;
        L_0x0021:
            r2.lazySet(r9);
            return;
        L_0x0025:
            r10 = r13.done;
            r11 = r2.getAndSet(r9);
            if (r11 != 0) goto L_0x002f;
        L_0x002d:
            r12 = 1;
            goto L_0x0030;
        L_0x002f:
            r12 = 0;
        L_0x0030:
            if (r10 == 0) goto L_0x0040;
        L_0x0032:
            if (r12 == 0) goto L_0x0040;
        L_0x0034:
            r7 = r13.error;
            if (r7 == 0) goto L_0x003c;
        L_0x0038:
            r13.error(r7);
            goto L_0x003f;
        L_0x003c:
            r13.complete();
        L_0x003f:
            return;
            if (r12 == 0) goto L_0x0044;
        L_0x0043:
            goto L_0x004c;
        L_0x0044:
            r1.onNext(r11);
            r7 = 1;
            r5 = r5 + r7;
            goto L_0x0014;
        L_0x004c:
            r10 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1));
            if (r10 != 0) goto L_0x0075;
        L_0x0050:
            r10 = r13.isCancelled();
            if (r10 == 0) goto L_0x005a;
        L_0x0056:
            r2.lazySet(r9);
            return;
        L_0x005a:
            r9 = r13.done;
            r10 = r2.get();
            if (r10 != 0) goto L_0x0063;
        L_0x0062:
            goto L_0x0064;
        L_0x0063:
            r7 = 0;
        L_0x0064:
            if (r9 == 0) goto L_0x0074;
        L_0x0066:
            if (r7 == 0) goto L_0x0074;
        L_0x0068:
            r8 = r13.error;
            if (r8 == 0) goto L_0x0070;
        L_0x006c:
            r13.error(r8);
            goto L_0x0073;
        L_0x0070:
            r13.complete();
        L_0x0073:
            return;
        L_0x0074:
            goto L_0x0076;
        L_0x0076:
            r7 = 0;
            r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1));
            if (r9 == 0) goto L_0x0080;
        L_0x007c:
            io.reactivex.internal.util.BackpressureHelper.produced(r13, r5);
            goto L_0x0081;
        L_0x0081:
            r7 = r13.wip;
            r8 = -r0;
            r0 = r7.addAndGet(r8);
            if (r0 != 0) goto L_0x008c;
            return;
            goto L_0x000e;
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.flowable.FlowableCreate.LatestAsyncEmitter.drain():void");
        }
    }

    static final class MissingEmitter<T> extends BaseEmitter<T> {
        private static final long serialVersionUID = 3776720187248809713L;

        MissingEmitter(Subscriber<? super T> downstream) {
            super(downstream);
        }

        public void onNext(T t) {
            if (!isCancelled()) {
                if (t != null) {
                    this.downstream.onNext(t);
                    while (true) {
                        long r = get();
                        if (r == 0) {
                            break;
                        } else if (compareAndSet(r, r - 1)) {
                            break;
                        }
                    }
                    return;
                }
                onError(new NullPointerException("onNext called with null. Null values are generally not allowed in 2.x operators and sources."));
            }
        }
    }

    static abstract class NoOverflowBaseAsyncEmitter<T> extends BaseEmitter<T> {
        private static final long serialVersionUID = 4127754106204442833L;

        abstract void onOverflow();

        NoOverflowBaseAsyncEmitter(Subscriber<? super T> downstream) {
            super(downstream);
        }

        public final void onNext(T t) {
            if (!isCancelled()) {
                if (t == null) {
                    onError(new NullPointerException("onNext called with null. Null values are generally not allowed in 2.x operators and sources."));
                    return;
                }
                if (get() != 0) {
                    this.downstream.onNext(t);
                    BackpressureHelper.produced(this, 1);
                } else {
                    onOverflow();
                }
            }
        }
    }

    static final class DropAsyncEmitter<T> extends NoOverflowBaseAsyncEmitter<T> {
        private static final long serialVersionUID = 8360058422307496563L;

        DropAsyncEmitter(Subscriber<? super T> downstream) {
            super(downstream);
        }

        void onOverflow() {
        }
    }

    static final class ErrorAsyncEmitter<T> extends NoOverflowBaseAsyncEmitter<T> {
        private static final long serialVersionUID = 338953216916120960L;

        ErrorAsyncEmitter(Subscriber<? super T> downstream) {
            super(downstream);
        }

        void onOverflow() {
            onError(new MissingBackpressureException("create: could not emit value due to lack of requests"));
        }
    }

    public FlowableCreate(FlowableOnSubscribe<T> source, BackpressureStrategy backpressure) {
        this.source = source;
        this.backpressure = backpressure;
    }

    public void subscribeActual(Subscriber<? super T> t) {
        BaseEmitter<T> emitter;
        switch (this.backpressure) {
            case MISSING:
                emitter = new MissingEmitter(t);
                break;
            case ERROR:
                emitter = new ErrorAsyncEmitter(t);
                break;
            case DROP:
                emitter = new DropAsyncEmitter(t);
                break;
            case LATEST:
                emitter = new LatestAsyncEmitter(t);
                break;
            default:
                emitter = new BufferAsyncEmitter(t, bufferSize());
                break;
        }
        t.onSubscribe(emitter);
        try {
            this.source.subscribe(emitter);
        } catch (Throwable ex) {
            Exceptions.throwIfFatal(ex);
            emitter.onError(ex);
        }
    }
}
