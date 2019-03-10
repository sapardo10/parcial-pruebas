package io.reactivex.internal.operators.flowable;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.exceptions.MissingBackpressureException;
import io.reactivex.functions.Function;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.fuseable.QueueSubscription;
import io.reactivex.internal.fuseable.SimpleQueue;
import io.reactivex.internal.subscriptions.EmptySubscription;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.BackpressureHelper;
import io.reactivex.internal.util.QueueDrainHelper;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class FlowablePublishMulticast<T, R> extends AbstractFlowableWithUpstream<T, R> {
    final boolean delayError;
    final int prefetch;
    final Function<? super Flowable<T>, ? extends Publisher<? extends R>> selector;

    static final class MulticastSubscription<T> extends AtomicLong implements Subscription {
        private static final long serialVersionUID = 8664815189257569791L;
        final Subscriber<? super T> downstream;
        long emitted;
        final MulticastProcessor<T> parent;

        MulticastSubscription(Subscriber<? super T> actual, MulticastProcessor<T> parent) {
            this.downstream = actual;
            this.parent = parent;
        }

        public void request(long n) {
            if (SubscriptionHelper.validate(n)) {
                BackpressureHelper.addCancel(this, n);
                this.parent.drain();
            }
        }

        public void cancel() {
            if (getAndSet(Long.MIN_VALUE) != Long.MIN_VALUE) {
                this.parent.remove(this);
                this.parent.drain();
            }
        }

        public boolean isCancelled() {
            return get() == Long.MIN_VALUE;
        }
    }

    static final class MulticastProcessor<T> extends Flowable<T> implements FlowableSubscriber<T>, Disposable {
        static final MulticastSubscription[] EMPTY = new MulticastSubscription[0];
        static final MulticastSubscription[] TERMINATED = new MulticastSubscription[0];
        int consumed;
        final boolean delayError;
        volatile boolean done;
        Throwable error;
        final int limit;
        final int prefetch;
        volatile SimpleQueue<T> queue;
        int sourceMode;
        final AtomicReference<MulticastSubscription<T>[]> subscribers = new AtomicReference(EMPTY);
        final AtomicReference<Subscription> upstream = new AtomicReference();
        final AtomicInteger wip = new AtomicInteger();

        MulticastProcessor(int prefetch, boolean delayError) {
            this.prefetch = prefetch;
            this.limit = prefetch - (prefetch >> 2);
            this.delayError = delayError;
        }

        public void onSubscribe(Subscription s) {
            if (SubscriptionHelper.setOnce(this.upstream, s)) {
                if (s instanceof QueueSubscription) {
                    QueueSubscription<T> qs = (QueueSubscription) s;
                    int m = qs.requestFusion(3);
                    if (m == 1) {
                        this.sourceMode = m;
                        this.queue = qs;
                        this.done = true;
                        drain();
                        return;
                    } else if (m == 2) {
                        this.sourceMode = m;
                        this.queue = qs;
                        QueueDrainHelper.request(s, this.prefetch);
                        return;
                    }
                }
                this.queue = QueueDrainHelper.createQueue(this.prefetch);
                QueueDrainHelper.request(s, this.prefetch);
            }
        }

        public void dispose() {
            SubscriptionHelper.cancel(this.upstream);
            if (this.wip.getAndIncrement() == 0) {
                SimpleQueue<T> q = this.queue;
                if (q != null) {
                    q.clear();
                }
            }
        }

        public boolean isDisposed() {
            return SubscriptionHelper.isCancelled((Subscription) this.upstream.get());
        }

        public void onNext(T t) {
            if (!this.done) {
                if (this.sourceMode != 0 || this.queue.offer(t)) {
                    drain();
                    return;
                }
                ((Subscription) this.upstream.get()).cancel();
                onError(new MissingBackpressureException());
            }
        }

        public void onError(Throwable t) {
            if (this.done) {
                RxJavaPlugins.onError(t);
                return;
            }
            this.error = t;
            this.done = true;
            drain();
        }

        public void onComplete() {
            if (!this.done) {
                this.done = true;
                drain();
            }
        }

        boolean add(MulticastSubscription<T> s) {
            while (true) {
                MulticastSubscription[] current = (MulticastSubscription[]) this.subscribers.get();
                if (current == TERMINATED) {
                    return false;
                }
                int n = current.length;
                MulticastSubscription<T>[] next = new MulticastSubscription[(n + 1)];
                System.arraycopy(current, 0, next, 0, n);
                next[n] = s;
                if (this.subscribers.compareAndSet(current, next)) {
                    return true;
                }
            }
        }

        void remove(MulticastSubscription<T> s) {
            while (true) {
                MulticastSubscription[] current = (MulticastSubscription[]) this.subscribers.get();
                int n = current.length;
                if (n != 0) {
                    int j = -1;
                    for (int i = 0; i < n; i++) {
                        if (current[i] == s) {
                            j = i;
                            break;
                        }
                    }
                    if (j >= 0) {
                        MulticastSubscription<T>[] next;
                        if (n == 1) {
                            next = EMPTY;
                        } else {
                            MulticastSubscription<T>[] next2 = new MulticastSubscription[(n - 1)];
                            System.arraycopy(current, 0, next2, 0, j);
                            System.arraycopy(current, j + 1, next2, j, (n - j) - 1);
                            next = next2;
                        }
                        if (this.subscribers.compareAndSet(current, next)) {
                            return;
                        }
                    } else {
                        return;
                    }
                }
                return;
            }
        }

        protected void subscribeActual(Subscriber<? super T> s) {
            MulticastSubscription<T> ms = new MulticastSubscription(s, this);
            s.onSubscribe(ms);
            if (!add(ms)) {
                Throwable ex = this.error;
                if (ex != null) {
                    s.onError(ex);
                } else {
                    s.onComplete();
                }
            } else if (ms.isCancelled()) {
                remove(ms);
            } else {
                drain();
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        void drain() {
            /*
            r28 = this;
            r1 = r28;
            r0 = r1.wip;
            r0 = r0.getAndIncrement();
            if (r0 == 0) goto L_0x000b;
        L_0x000a:
            return;
        L_0x000b:
            r0 = 1;
            r2 = r1.queue;
            r3 = r1.consumed;
            r4 = r1.limit;
            r5 = r1.sourceMode;
            r7 = 1;
            if (r5 == r7) goto L_0x0019;
        L_0x0017:
            r5 = 1;
            goto L_0x001a;
        L_0x0019:
            r5 = 0;
        L_0x001a:
            r8 = r1.subscribers;
            r9 = r8.get();
            r9 = (io.reactivex.internal.operators.flowable.FlowablePublishMulticast.MulticastSubscription[]) r9;
            r27 = r3;
            r3 = r0;
            r0 = r27;
        L_0x0027:
            r10 = r9.length;
            if (r2 == 0) goto L_0x0164;
        L_0x002a:
            if (r10 == 0) goto L_0x0164;
        L_0x002c:
            r11 = 9223372036854775807; // 0x7fffffffffffffff float:NaN double:NaN;
            r13 = r9.length;
            r14 = r11;
            r11 = r10;
            r10 = 0;
        L_0x0035:
            r16 = -9223372036854775808;
            if (r10 >= r13) goto L_0x0056;
        L_0x0039:
            r12 = r9[r10];
            r18 = r12.get();
            r6 = r12.emitted;
            r18 = r18 - r6;
            r6 = (r18 > r16 ? 1 : (r18 == r16 ? 0 : -1));
            if (r6 == 0) goto L_0x0050;
        L_0x0047:
            r6 = (r14 > r18 ? 1 : (r14 == r18 ? 0 : -1));
            if (r6 <= 0) goto L_0x004f;
        L_0x004b:
            r6 = r18;
            r14 = r6;
            goto L_0x0052;
        L_0x004f:
            goto L_0x0052;
        L_0x0050:
            r11 = r11 + -1;
        L_0x0052:
            r10 = r10 + 1;
            r7 = 1;
            goto L_0x0035;
        L_0x0056:
            if (r11 != 0) goto L_0x005c;
        L_0x0058:
            r14 = 0;
            r6 = r0;
            goto L_0x005d;
        L_0x005c:
            r6 = r0;
        L_0x005d:
            r12 = 0;
            r0 = (r14 > r12 ? 1 : (r14 == r12 ? 0 : -1));
            if (r0 == 0) goto L_0x0128;
        L_0x0063:
            r0 = r28.isDisposed();
            if (r0 == 0) goto L_0x006d;
        L_0x0069:
            r2.clear();
            return;
        L_0x006d:
            r7 = r1.done;
            if (r7 == 0) goto L_0x007e;
        L_0x0071:
            r0 = r1.delayError;
            if (r0 != 0) goto L_0x007e;
        L_0x0075:
            r0 = r1.error;
            if (r0 == 0) goto L_0x007d;
        L_0x0079:
            r1.errorAll(r0);
            return;
        L_0x007d:
            goto L_0x007f;
        L_0x007f:
            r0 = r2.poll();	 Catch:{ Throwable -> 0x0115 }
            if (r0 != 0) goto L_0x0088;
        L_0x0086:
            r10 = 1;
            goto L_0x0089;
        L_0x0088:
            r10 = 0;
        L_0x0089:
            if (r7 == 0) goto L_0x0099;
        L_0x008b:
            if (r10 == 0) goto L_0x0099;
        L_0x008d:
            r12 = r1.error;
            if (r12 == 0) goto L_0x0095;
        L_0x0091:
            r1.errorAll(r12);
            goto L_0x0098;
        L_0x0095:
            r28.completeAll();
        L_0x0098:
            return;
            if (r10 == 0) goto L_0x00a0;
        L_0x009c:
            r24 = r11;
            goto L_0x012a;
        L_0x00a0:
            r12 = 0;
            r13 = r9.length;
            r18 = r12;
            r12 = 0;
        L_0x00a5:
            r20 = 1;
            if (r12 >= r13) goto L_0x00e3;
        L_0x00a9:
            r19 = r7;
            r7 = r9[r12];
            r22 = r7.get();
            r24 = (r22 > r16 ? 1 : (r22 == r16 ? 0 : -1));
            if (r24 == 0) goto L_0x00d3;
        L_0x00b5:
            r24 = 9223372036854775807; // 0x7fffffffffffffff float:NaN double:NaN;
            r26 = (r22 > r24 ? 1 : (r22 == r24 ? 0 : -1));
            if (r26 == 0) goto L_0x00c9;
        L_0x00be:
            r25 = r10;
            r24 = r11;
            r10 = r7.emitted;
            r10 = r10 + r20;
            r7.emitted = r10;
            goto L_0x00cd;
        L_0x00c9:
            r25 = r10;
            r24 = r11;
        L_0x00cd:
            r10 = r7.downstream;
            r10.onNext(r0);
            goto L_0x00da;
        L_0x00d3:
            r25 = r10;
            r24 = r11;
            r10 = 1;
            r18 = r10;
        L_0x00da:
            r12 = r12 + 1;
            r7 = r19;
            r11 = r24;
            r10 = r25;
            goto L_0x00a5;
        L_0x00e3:
            r19 = r7;
            r25 = r10;
            r24 = r11;
            r14 = r14 - r20;
            if (r5 == 0) goto L_0x00ff;
        L_0x00ed:
            r6 = r6 + 1;
            if (r6 != r4) goto L_0x00ff;
        L_0x00f1:
            r6 = 0;
            r7 = r1.upstream;
            r7 = r7.get();
            r7 = (org.reactivestreams.Subscription) r7;
            r10 = (long) r4;
            r7.request(r10);
            goto L_0x0100;
        L_0x0100:
            r7 = r8.get();
            r7 = (io.reactivex.internal.operators.flowable.FlowablePublishMulticast.MulticastSubscription[]) r7;
            if (r18 != 0) goto L_0x010f;
        L_0x0108:
            if (r7 == r9) goto L_0x010b;
        L_0x010a:
            goto L_0x010f;
        L_0x010b:
            r11 = r24;
            goto L_0x005d;
            r9 = r7;
            r0 = r6;
            r7 = 1;
            goto L_0x0027;
        L_0x0115:
            r0 = move-exception;
            r19 = r7;
            r24 = r11;
            r7 = r0;
            r0 = r7;
            io.reactivex.exceptions.Exceptions.throwIfFatal(r0);
            r7 = r1.upstream;
            io.reactivex.internal.subscriptions.SubscriptionHelper.cancel(r7);
            r1.errorAll(r0);
            return;
        L_0x0128:
            r24 = r11;
        L_0x012a:
            r0 = (r14 > r12 ? 1 : (r14 == r12 ? 0 : -1));
            if (r0 != 0) goto L_0x015f;
        L_0x012e:
            r0 = r28.isDisposed();
            if (r0 == 0) goto L_0x0138;
        L_0x0134:
            r2.clear();
            return;
        L_0x0138:
            r0 = r1.done;
            if (r0 == 0) goto L_0x0149;
        L_0x013c:
            r7 = r1.delayError;
            if (r7 != 0) goto L_0x0149;
        L_0x0140:
            r7 = r1.error;
            if (r7 == 0) goto L_0x0148;
        L_0x0144:
            r1.errorAll(r7);
            return;
        L_0x0148:
            goto L_0x014a;
        L_0x014a:
            if (r0 == 0) goto L_0x015e;
        L_0x014c:
            r7 = r2.isEmpty();
            if (r7 == 0) goto L_0x015e;
        L_0x0152:
            r7 = r1.error;
            if (r7 == 0) goto L_0x015a;
        L_0x0156:
            r1.errorAll(r7);
            goto L_0x015d;
        L_0x015a:
            r28.completeAll();
        L_0x015d:
            return;
        L_0x015e:
            goto L_0x0160;
        L_0x0160:
            r0 = r6;
            r10 = r24;
            goto L_0x0165;
        L_0x0165:
            r1.consumed = r0;
            r6 = r1.wip;
            r7 = -r3;
            r3 = r6.addAndGet(r7);
            if (r3 != 0) goto L_0x0172;
            return;
        L_0x0172:
            if (r2 != 0) goto L_0x0177;
        L_0x0174:
            r2 = r1.queue;
            goto L_0x0178;
        L_0x0178:
            r6 = r8.get();
            r9 = r6;
            r9 = (io.reactivex.internal.operators.flowable.FlowablePublishMulticast.MulticastSubscription[]) r9;
            r7 = 1;
            goto L_0x0027;
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.flowable.FlowablePublishMulticast.MulticastProcessor.drain():void");
        }

        void errorAll(Throwable ex) {
            for (MulticastSubscription<T> ms : (MulticastSubscription[]) this.subscribers.getAndSet(TERMINATED)) {
                if (ms.get() != Long.MIN_VALUE) {
                    ms.downstream.onError(ex);
                }
            }
        }

        void completeAll() {
            for (MulticastSubscription<T> ms : (MulticastSubscription[]) this.subscribers.getAndSet(TERMINATED)) {
                if (ms.get() != Long.MIN_VALUE) {
                    ms.downstream.onComplete();
                }
            }
        }
    }

    static final class OutputCanceller<R> implements FlowableSubscriber<R>, Subscription {
        final Subscriber<? super R> downstream;
        final MulticastProcessor<?> processor;
        Subscription upstream;

        OutputCanceller(Subscriber<? super R> actual, MulticastProcessor<?> processor) {
            this.downstream = actual;
            this.processor = processor;
        }

        public void onSubscribe(Subscription s) {
            if (SubscriptionHelper.validate(this.upstream, s)) {
                this.upstream = s;
                this.downstream.onSubscribe(this);
            }
        }

        public void onNext(R t) {
            this.downstream.onNext(t);
        }

        public void onError(Throwable t) {
            this.downstream.onError(t);
            this.processor.dispose();
        }

        public void onComplete() {
            this.downstream.onComplete();
            this.processor.dispose();
        }

        public void request(long n) {
            this.upstream.request(n);
        }

        public void cancel() {
            this.upstream.cancel();
            this.processor.dispose();
        }
    }

    public FlowablePublishMulticast(Flowable<T> source, Function<? super Flowable<T>, ? extends Publisher<? extends R>> selector, int prefetch, boolean delayError) {
        super(source);
        this.selector = selector;
        this.prefetch = prefetch;
        this.delayError = delayError;
    }

    protected void subscribeActual(Subscriber<? super R> s) {
        MulticastProcessor<T> mp = new MulticastProcessor(this.prefetch, this.delayError);
        try {
            ((Publisher) ObjectHelper.requireNonNull(this.selector.apply(mp), "selector returned a null Publisher")).subscribe(new OutputCanceller(s, mp));
            this.source.subscribe(mp);
        } catch (Throwable ex) {
            Exceptions.throwIfFatal(ex);
            EmptySubscription.error(ex, s);
        }
    }
}
