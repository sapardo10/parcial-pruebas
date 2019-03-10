package io.reactivex.internal.operators.flowable;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.functions.Function;
import io.reactivex.internal.fuseable.QueueSubscription;
import io.reactivex.internal.fuseable.SimpleQueue;
import io.reactivex.internal.queue.SpscArrayQueue;
import io.reactivex.internal.subscriptions.EmptySubscription;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.AtomicThrowable;
import io.reactivex.internal.util.BackpressureHelper;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class FlowableZip<T, R> extends Flowable<R> {
    final int bufferSize;
    final boolean delayError;
    final Publisher<? extends T>[] sources;
    final Iterable<? extends Publisher<? extends T>> sourcesIterable;
    final Function<? super Object[], ? extends R> zipper;

    static final class ZipCoordinator<T, R> extends AtomicInteger implements Subscription {
        private static final long serialVersionUID = -2434867452883857743L;
        volatile boolean cancelled;
        final Object[] current;
        final boolean delayErrors;
        final Subscriber<? super R> downstream;
        final AtomicThrowable errors;
        final AtomicLong requested;
        final ZipSubscriber<T, R>[] subscribers;
        final Function<? super Object[], ? extends R> zipper;

        ZipCoordinator(Subscriber<? super R> actual, Function<? super Object[], ? extends R> zipper, int n, int prefetch, boolean delayErrors) {
            this.downstream = actual;
            this.zipper = zipper;
            this.delayErrors = delayErrors;
            ZipSubscriber<T, R>[] a = new ZipSubscriber[n];
            for (int i = 0; i < n; i++) {
                a[i] = new ZipSubscriber(this, prefetch);
            }
            this.current = new Object[n];
            this.subscribers = a;
            this.requested = new AtomicLong();
            this.errors = new AtomicThrowable();
        }

        void subscribe(Publisher<? extends T>[] sources, int n) {
            ZipSubscriber<T, R>[] a = this.subscribers;
            int i = 0;
            while (i < n) {
                if (!this.cancelled) {
                    if (this.delayErrors || this.errors.get() == null) {
                        sources[i].subscribe(a[i]);
                        i++;
                    }
                }
                return;
            }
        }

        public void request(long n) {
            if (SubscriptionHelper.validate(n)) {
                BackpressureHelper.add(this.requested, n);
                drain();
            }
        }

        public void cancel() {
            if (!this.cancelled) {
                this.cancelled = true;
                cancelAll();
            }
        }

        void error(ZipSubscriber<T, R> inner, Throwable e) {
            if (this.errors.addThrowable(e)) {
                inner.done = true;
                drain();
                return;
            }
            RxJavaPlugins.onError(e);
        }

        void cancelAll() {
            for (ZipSubscriber<T, R> s : this.subscribers) {
                s.cancel();
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        void drain() {
            /*
            r19 = this;
            r1 = r19;
            r0 = r19.getAndIncrement();
            if (r0 == 0) goto L_0x0009;
        L_0x0008:
            return;
        L_0x0009:
            r2 = r1.downstream;
            r3 = r1.subscribers;
            r4 = r3.length;
            r5 = r1.current;
            r0 = 1;
            r6 = r0;
        L_0x0012:
            r0 = r1.requested;
            r7 = r0.get();
            r9 = 0;
        L_0x001a:
            r0 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1));
            if (r0 == 0) goto L_0x00e0;
        L_0x001e:
            r0 = r1.cancelled;
            if (r0 == 0) goto L_0x0023;
        L_0x0022:
            return;
        L_0x0023:
            r0 = r1.delayErrors;
            if (r0 != 0) goto L_0x003c;
        L_0x0027:
            r0 = r1.errors;
            r0 = r0.get();
            if (r0 == 0) goto L_0x003c;
        L_0x002f:
            r19.cancelAll();
            r0 = r1.errors;
            r0 = r0.terminate();
            r2.onError(r0);
            return;
            r0 = 0;
            r14 = 0;
            r15 = r0;
        L_0x0040:
            if (r14 >= r4) goto L_0x00a9;
        L_0x0042:
            r12 = r3[r14];
            r0 = r5[r14];
            if (r0 != 0) goto L_0x00a5;
        L_0x0048:
            r0 = r12.done;	 Catch:{ Throwable -> 0x0088 }
            r13 = r12.queue;	 Catch:{ Throwable -> 0x0088 }
            if (r13 == 0) goto L_0x0053;
        L_0x004e:
            r16 = r13.poll();	 Catch:{ Throwable -> 0x0088 }
            goto L_0x0055;
        L_0x0053:
            r16 = 0;
        L_0x0055:
            if (r16 != 0) goto L_0x005a;
        L_0x0057:
            r17 = 1;
            goto L_0x005c;
        L_0x005a:
            r17 = 0;
        L_0x005c:
            if (r0 == 0) goto L_0x007f;
        L_0x005e:
            if (r17 == 0) goto L_0x007f;
        L_0x0060:
            r19.cancelAll();	 Catch:{ Throwable -> 0x0088 }
            r11 = r1.errors;	 Catch:{ Throwable -> 0x0088 }
            r11 = r11.get();	 Catch:{ Throwable -> 0x0088 }
            r11 = (java.lang.Throwable) r11;	 Catch:{ Throwable -> 0x0088 }
            if (r11 == 0) goto L_0x0079;
        L_0x006d:
            r18 = r0;
            r0 = r1.errors;	 Catch:{ Throwable -> 0x0088 }
            r0 = r0.terminate();	 Catch:{ Throwable -> 0x0088 }
            r2.onError(r0);	 Catch:{ Throwable -> 0x0088 }
            goto L_0x007e;
        L_0x0079:
            r18 = r0;
            r2.onComplete();	 Catch:{ Throwable -> 0x0088 }
        L_0x007e:
            return;
        L_0x007f:
            r18 = r0;
            if (r17 != 0) goto L_0x0086;
        L_0x0083:
            r5[r14] = r16;	 Catch:{ Throwable -> 0x0088 }
            goto L_0x0087;
        L_0x0086:
            r15 = 1;
        L_0x0087:
            goto L_0x00a6;
        L_0x0088:
            r0 = move-exception;
            io.reactivex.exceptions.Exceptions.throwIfFatal(r0);
            r11 = r1.errors;
            r11.addThrowable(r0);
            r11 = r1.delayErrors;
            if (r11 != 0) goto L_0x00a2;
        L_0x0095:
            r19.cancelAll();
            r11 = r1.errors;
            r11 = r11.terminate();
            r2.onError(r11);
            return;
        L_0x00a2:
            r11 = 1;
            r15 = r11;
            goto L_0x00a6;
        L_0x00a6:
            r14 = r14 + 1;
            goto L_0x0040;
        L_0x00a9:
            if (r15 == 0) goto L_0x00ad;
        L_0x00ab:
            r11 = 0;
            goto L_0x00e1;
        L_0x00ad:
            r0 = r1.zipper;	 Catch:{ Throwable -> 0x00ca }
            r11 = r5.clone();	 Catch:{ Throwable -> 0x00ca }
            r0 = r0.apply(r11);	 Catch:{ Throwable -> 0x00ca }
            r11 = "The zipper returned a null value";
            r0 = io.reactivex.internal.functions.ObjectHelper.requireNonNull(r0, r11);	 Catch:{ Throwable -> 0x00ca }
            r2.onNext(r0);
            r11 = 1;
            r9 = r9 + r11;
            r11 = 0;
            java.util.Arrays.fill(r5, r11);
            goto L_0x001a;
        L_0x00ca:
            r0 = move-exception;
            io.reactivex.exceptions.Exceptions.throwIfFatal(r0);
            r19.cancelAll();
            r11 = r1.errors;
            r11.addThrowable(r0);
            r11 = r1.errors;
            r11 = r11.terminate();
            r2.onError(r11);
            return;
        L_0x00e0:
            r11 = 0;
        L_0x00e1:
            r0 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1));
            if (r0 != 0) goto L_0x016e;
        L_0x00e5:
            r0 = r1.cancelled;
            if (r0 == 0) goto L_0x00ea;
        L_0x00e9:
            return;
        L_0x00ea:
            r0 = r1.delayErrors;
            if (r0 != 0) goto L_0x0103;
        L_0x00ee:
            r0 = r1.errors;
            r0 = r0.get();
            if (r0 == 0) goto L_0x0103;
        L_0x00f6:
            r19.cancelAll();
            r0 = r1.errors;
            r0 = r0.terminate();
            r2.onError(r0);
            return;
            r0 = 0;
            r12 = r0;
        L_0x0106:
            if (r12 >= r4) goto L_0x016d;
        L_0x0108:
            r13 = r3[r12];
            r0 = r5[r12];
            if (r0 != 0) goto L_0x0168;
        L_0x010e:
            r0 = r13.done;	 Catch:{ Throwable -> 0x014d }
            r14 = r13.queue;	 Catch:{ Throwable -> 0x014d }
            if (r14 == 0) goto L_0x0119;
        L_0x0114:
            r15 = r14.poll();	 Catch:{ Throwable -> 0x014d }
            goto L_0x011a;
        L_0x0119:
            r15 = r11;
        L_0x011a:
            if (r15 != 0) goto L_0x011f;
        L_0x011c:
            r16 = 1;
            goto L_0x0121;
        L_0x011f:
            r16 = 0;
        L_0x0121:
            if (r0 == 0) goto L_0x0144;
        L_0x0123:
            if (r16 == 0) goto L_0x0144;
        L_0x0125:
            r19.cancelAll();	 Catch:{ Throwable -> 0x014d }
            r11 = r1.errors;	 Catch:{ Throwable -> 0x014d }
            r11 = r11.get();	 Catch:{ Throwable -> 0x014d }
            r11 = (java.lang.Throwable) r11;	 Catch:{ Throwable -> 0x014d }
            if (r11 == 0) goto L_0x013e;
        L_0x0132:
            r17 = r0;
            r0 = r1.errors;	 Catch:{ Throwable -> 0x014d }
            r0 = r0.terminate();	 Catch:{ Throwable -> 0x014d }
            r2.onError(r0);	 Catch:{ Throwable -> 0x014d }
            goto L_0x0143;
        L_0x013e:
            r17 = r0;
            r2.onComplete();	 Catch:{ Throwable -> 0x014d }
        L_0x0143:
            return;
        L_0x0144:
            r17 = r0;
            if (r16 != 0) goto L_0x014b;
        L_0x0148:
            r5[r12] = r15;	 Catch:{ Throwable -> 0x014d }
            goto L_0x014c;
        L_0x014c:
            goto L_0x0169;
        L_0x014d:
            r0 = move-exception;
            io.reactivex.exceptions.Exceptions.throwIfFatal(r0);
            r11 = r1.errors;
            r11.addThrowable(r0);
            r11 = r1.delayErrors;
            if (r11 != 0) goto L_0x0167;
        L_0x015a:
            r19.cancelAll();
            r11 = r1.errors;
            r11 = r11.terminate();
            r2.onError(r11);
            return;
        L_0x0167:
            goto L_0x0169;
        L_0x0169:
            r12 = r12 + 1;
            r11 = 0;
            goto L_0x0106;
        L_0x016d:
            goto L_0x016f;
        L_0x016f:
            r11 = 0;
            r0 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1));
            if (r0 == 0) goto L_0x0192;
        L_0x0175:
            r0 = r3.length;
            r11 = 0;
        L_0x0177:
            if (r11 >= r0) goto L_0x0181;
        L_0x0179:
            r12 = r3[r11];
            r12.request(r9);
            r11 = r11 + 1;
            goto L_0x0177;
        L_0x0181:
            r11 = 9223372036854775807; // 0x7fffffffffffffff float:NaN double:NaN;
            r0 = (r7 > r11 ? 1 : (r7 == r11 ? 0 : -1));
            if (r0 == 0) goto L_0x0191;
        L_0x018a:
            r0 = r1.requested;
            r11 = -r9;
            r0.addAndGet(r11);
            goto L_0x0193;
        L_0x0191:
            goto L_0x0193;
        L_0x0193:
            r0 = -r6;
            r6 = r1.addAndGet(r0);
            if (r6 != 0) goto L_0x019c;
            return;
            goto L_0x0012;
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.flowable.FlowableZip.ZipCoordinator.drain():void");
        }
    }

    static final class ZipSubscriber<T, R> extends AtomicReference<Subscription> implements FlowableSubscriber<T>, Subscription {
        private static final long serialVersionUID = -4627193790118206028L;
        volatile boolean done;
        final int limit;
        final ZipCoordinator<T, R> parent;
        final int prefetch;
        long produced;
        SimpleQueue<T> queue;
        int sourceMode;

        ZipSubscriber(ZipCoordinator<T, R> parent, int prefetch) {
            this.parent = parent;
            this.prefetch = prefetch;
            this.limit = prefetch - (prefetch >> 2);
        }

        public void onSubscribe(Subscription s) {
            if (SubscriptionHelper.setOnce(this, s)) {
                if (s instanceof QueueSubscription) {
                    QueueSubscription<T> f = (QueueSubscription) s;
                    int m = f.requestFusion(7);
                    if (m == 1) {
                        this.sourceMode = m;
                        this.queue = f;
                        this.done = true;
                        this.parent.drain();
                        return;
                    } else if (m == 2) {
                        this.sourceMode = m;
                        this.queue = f;
                        s.request((long) this.prefetch);
                        return;
                    }
                }
                this.queue = new SpscArrayQueue(this.prefetch);
                s.request((long) this.prefetch);
            }
        }

        public void onNext(T t) {
            if (this.sourceMode != 2) {
                this.queue.offer(t);
            }
            this.parent.drain();
        }

        public void onError(Throwable t) {
            this.parent.error(this, t);
        }

        public void onComplete() {
            this.done = true;
            this.parent.drain();
        }

        public void cancel() {
            SubscriptionHelper.cancel(this);
        }

        public void request(long n) {
            if (this.sourceMode != 1) {
                long p = this.produced + n;
                if (p >= ((long) this.limit)) {
                    this.produced = 0;
                    ((Subscription) get()).request(p);
                    return;
                }
                this.produced = p;
            }
        }
    }

    public FlowableZip(Publisher<? extends T>[] sources, Iterable<? extends Publisher<? extends T>> sourcesIterable, Function<? super Object[], ? extends R> zipper, int bufferSize, boolean delayError) {
        this.sources = sources;
        this.sourcesIterable = sourcesIterable;
        this.zipper = zipper;
        this.bufferSize = bufferSize;
        this.delayError = delayError;
    }

    public void subscribeActual(Subscriber<? super R> s) {
        Publisher<? extends T>[] sources = this.sources;
        int count = 0;
        if (sources == null) {
            sources = new Publisher[8];
            for (Publisher<? extends T> p : this.sourcesIterable) {
                if (count == sources.length) {
                    Publisher<? extends T>[] b = new Publisher[((count >> 2) + count)];
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
            EmptySubscription.complete(s);
            return;
        }
        ZipCoordinator<T, R> coordinator = new ZipCoordinator(s, this.zipper, count, this.bufferSize, this.delayError);
        s.onSubscribe(coordinator);
        coordinator.subscribe(sources, count);
    }
}
