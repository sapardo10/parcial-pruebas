package io.reactivex.internal.util;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.BooleanSupplier;
import io.reactivex.internal.fuseable.SimplePlainQueue;
import io.reactivex.internal.fuseable.SimpleQueue;
import io.reactivex.internal.queue.SpscArrayQueue;
import io.reactivex.internal.queue.SpscLinkedArrayQueue;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicLong;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class QueueDrainHelper {
    static final long COMPLETED_MASK = Long.MIN_VALUE;
    static final long REQUESTED_MASK = Long.MAX_VALUE;

    private QueueDrainHelper() {
        throw new IllegalStateException("No instances!");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static <T, U> void drainMaxLoop(io.reactivex.internal.fuseable.SimplePlainQueue<T> r10, org.reactivestreams.Subscriber<? super U> r11, boolean r12, io.reactivex.disposables.Disposable r13, io.reactivex.internal.util.QueueDrain<T, U> r14) {
        /*
        r0 = 1;
    L_0x0001:
        r7 = r14.done();
        r8 = r10.poll();
        if (r8 != 0) goto L_0x000d;
    L_0x000b:
        r1 = 1;
        goto L_0x000e;
    L_0x000d:
        r1 = 0;
    L_0x000e:
        r9 = r1;
        r1 = r7;
        r2 = r9;
        r3 = r11;
        r4 = r12;
        r5 = r10;
        r6 = r14;
        r1 = checkTerminated(r1, r2, r3, r4, r5, r6);
        if (r1 == 0) goto L_0x0023;
    L_0x001b:
        if (r13 == 0) goto L_0x0021;
    L_0x001d:
        r13.dispose();
        goto L_0x0022;
    L_0x0022:
        return;
    L_0x0023:
        if (r9 == 0) goto L_0x0030;
        r1 = -r0;
        r0 = r14.leave(r1);
        if (r0 != 0) goto L_0x002f;
        return;
    L_0x002f:
        goto L_0x0001;
    L_0x0030:
        r1 = r14.requested();
        r3 = 0;
        r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1));
        if (r5 == 0) goto L_0x0052;
    L_0x003a:
        r3 = r14.accept(r11, r8);
        if (r3 == 0) goto L_0x0050;
    L_0x0040:
        r3 = 9223372036854775807; // 0x7fffffffffffffff float:NaN double:NaN;
        r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1));
        if (r5 == 0) goto L_0x004f;
    L_0x0049:
        r3 = 1;
        r14.produced(r3);
        goto L_0x0051;
    L_0x004f:
        goto L_0x0051;
    L_0x0051:
        goto L_0x0001;
    L_0x0052:
        r10.clear();
        if (r13 == 0) goto L_0x005b;
    L_0x0057:
        r13.dispose();
        goto L_0x005c;
    L_0x005c:
        r3 = new io.reactivex.exceptions.MissingBackpressureException;
        r4 = "Could not emit value due to lack of requests.";
        r3.<init>(r4);
        r11.onError(r3);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.util.QueueDrainHelper.drainMaxLoop(io.reactivex.internal.fuseable.SimplePlainQueue, org.reactivestreams.Subscriber, boolean, io.reactivex.disposables.Disposable, io.reactivex.internal.util.QueueDrain):void");
    }

    public static <T, U> boolean checkTerminated(boolean d, boolean empty, Subscriber<?> s, boolean delayError, SimpleQueue<?> q, QueueDrain<T, U> qd) {
        if (qd.cancelled()) {
            q.clear();
            return true;
        }
        if (d) {
            Throwable err;
            if (!delayError) {
                err = qd.error();
                if (err != null) {
                    q.clear();
                    s.onError(err);
                    return true;
                } else if (empty) {
                    s.onComplete();
                    return true;
                }
            } else if (empty) {
                err = qd.error();
                if (err != null) {
                    s.onError(err);
                } else {
                    s.onComplete();
                }
                return true;
            }
        }
        return false;
    }

    public static <T, U> void drainLoop(SimplePlainQueue<T> q, Observer<? super U> a, boolean delayError, Disposable dispose, ObservableQueueDrain<T, U> qd) {
        int missed = 1;
        while (!checkTerminated(qd.done(), q.isEmpty(), a, delayError, q, dispose, qd)) {
            while (true) {
                boolean d = qd.done();
                T v = q.poll();
                boolean empty = v == null;
                if (!checkTerminated(d, empty, a, delayError, q, dispose, qd)) {
                    if (empty) {
                        break;
                    }
                    qd.accept(a, v);
                } else {
                    return;
                }
            }
            missed = qd.leave(-missed);
            if (missed == 0) {
                return;
            }
        }
    }

    public static <T, U> boolean checkTerminated(boolean d, boolean empty, Observer<?> observer, boolean delayError, SimpleQueue<?> q, Disposable disposable, ObservableQueueDrain<T, U> qd) {
        if (qd.cancelled()) {
            q.clear();
            disposable.dispose();
            return true;
        }
        if (d) {
            Throwable err;
            if (!delayError) {
                err = qd.error();
                if (err != null) {
                    q.clear();
                    if (disposable != null) {
                        disposable.dispose();
                    }
                    observer.onError(err);
                    return true;
                } else if (empty) {
                    if (disposable != null) {
                        disposable.dispose();
                    }
                    observer.onComplete();
                    return true;
                }
            } else if (empty) {
                if (disposable != null) {
                    disposable.dispose();
                }
                err = qd.error();
                if (err != null) {
                    observer.onError(err);
                } else {
                    observer.onComplete();
                }
                return true;
            }
        }
        return false;
    }

    public static <T> SimpleQueue<T> createQueue(int capacityHint) {
        if (capacityHint < 0) {
            return new SpscLinkedArrayQueue(-capacityHint);
        }
        return new SpscArrayQueue(capacityHint);
    }

    public static void request(Subscription s, int prefetch) {
        s.request(prefetch < 0 ? Long.MAX_VALUE : (long) prefetch);
    }

    public static <T> boolean postCompleteRequest(long n, Subscriber<? super T> actual, Queue<T> queue, AtomicLong state, BooleanSupplier isCancelled) {
        long j = n;
        while (true) {
            long r = state.get();
            if (state.compareAndSet(r, (r & Long.MIN_VALUE) | BackpressureHelper.addCap(Long.MAX_VALUE & r, j))) {
                break;
            }
        }
        if (r != Long.MIN_VALUE) {
            return false;
        }
        postCompleteDrain(j | Long.MIN_VALUE, actual, queue, state, isCancelled);
        return true;
    }

    static boolean isCancelled(BooleanSupplier cancelled) {
        try {
            return cancelled.getAsBoolean();
        } catch (Throwable ex) {
            Exceptions.throwIfFatal(ex);
            return true;
        }
    }

    static <T> boolean postCompleteDrain(long n, Subscriber<? super T> actual, Queue<T> queue, AtomicLong state, BooleanSupplier isCancelled) {
        long e = n & Long.MIN_VALUE;
        while (true) {
            if (e != n) {
                if (isCancelled(isCancelled)) {
                    return true;
                }
                T t = queue.poll();
                if (t == null) {
                    actual.onComplete();
                    return true;
                }
                actual.onNext(t);
                e++;
            } else if (isCancelled(isCancelled)) {
                return true;
            } else {
                if (queue.isEmpty()) {
                    actual.onComplete();
                    return true;
                }
                n = state.get();
                if (n == e) {
                    n = state.addAndGet(-(e & Long.MAX_VALUE));
                    if ((Long.MAX_VALUE & n) == 0) {
                        return false;
                    }
                    e = n & Long.MIN_VALUE;
                }
            }
        }
    }

    public static <T> void postComplete(Subscriber<? super T> actual, Queue<T> queue, AtomicLong state, BooleanSupplier isCancelled) {
        if (queue.isEmpty()) {
            actual.onComplete();
        } else if (!postCompleteDrain(state.get(), actual, queue, state, isCancelled)) {
            long r;
            long u;
            while (true) {
                r = state.get();
                if ((r & Long.MIN_VALUE) == 0) {
                    u = Long.MIN_VALUE | r;
                    if (state.compareAndSet(r, u)) {
                        break;
                    }
                } else {
                    return;
                }
            }
            if (r != 0) {
                postCompleteDrain(u, actual, queue, state, isCancelled);
            }
        }
    }
}
