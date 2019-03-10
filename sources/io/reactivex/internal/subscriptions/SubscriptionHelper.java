package io.reactivex.internal.subscriptions;

import io.reactivex.exceptions.ProtocolViolationException;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.util.BackpressureHelper;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Subscription;

public enum SubscriptionHelper implements Subscription {
    CANCELLED;

    public void request(long n) {
    }

    public void cancel() {
    }

    public static boolean validate(Subscription current, Subscription next) {
        if (next == null) {
            RxJavaPlugins.onError(new NullPointerException("next is null"));
            return false;
        } else if (current == null) {
            return true;
        } else {
            next.cancel();
            reportSubscriptionSet();
            return false;
        }
    }

    public static void reportSubscriptionSet() {
        RxJavaPlugins.onError(new ProtocolViolationException("Subscription already set!"));
    }

    public static boolean validate(long n) {
        if (n > 0) {
            return true;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("n > 0 required but it was ");
        stringBuilder.append(n);
        RxJavaPlugins.onError(new IllegalArgumentException(stringBuilder.toString()));
        return false;
    }

    public static void reportMoreProduced(long n) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("More produced than requested: ");
        stringBuilder.append(n);
        RxJavaPlugins.onError(new ProtocolViolationException(stringBuilder.toString()));
    }

    public static boolean isCancelled(Subscription s) {
        return s == CANCELLED;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean set(java.util.concurrent.atomic.AtomicReference<org.reactivestreams.Subscription> r2, org.reactivestreams.Subscription r3) {
        /*
    L_0x0000:
        r0 = r2.get();
        r0 = (org.reactivestreams.Subscription) r0;
        r1 = CANCELLED;
        if (r0 != r1) goto L_0x0013;
    L_0x000a:
        if (r3 == 0) goto L_0x0010;
    L_0x000c:
        r3.cancel();
        goto L_0x0011;
    L_0x0011:
        r1 = 0;
        return r1;
    L_0x0013:
        r1 = r2.compareAndSet(r0, r3);
        if (r1 == 0) goto L_0x0022;
    L_0x0019:
        if (r0 == 0) goto L_0x001f;
    L_0x001b:
        r0.cancel();
        goto L_0x0020;
    L_0x0020:
        r1 = 1;
        return r1;
        goto L_0x0000;
        */
        throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.subscriptions.SubscriptionHelper.set(java.util.concurrent.atomic.AtomicReference, org.reactivestreams.Subscription):boolean");
    }

    public static boolean setOnce(AtomicReference<Subscription> field, Subscription s) {
        ObjectHelper.requireNonNull((Object) s, "s is null");
        if (field.compareAndSet(null, s)) {
            return true;
        }
        s.cancel();
        if (field.get() != CANCELLED) {
            reportSubscriptionSet();
        }
        return false;
    }

    public static boolean replace(AtomicReference<Subscription> field, Subscription s) {
        while (true) {
            Subscription current = (Subscription) field.get();
            if (current == CANCELLED) {
                break;
            } else if (field.compareAndSet(current, s)) {
                return true;
            }
        }
        if (s != null) {
            s.cancel();
        }
        return false;
    }

    public static boolean cancel(AtomicReference<Subscription> field) {
        Subscription current = (Subscription) field.get();
        Subscription subscription = CANCELLED;
        if (current != subscription) {
            current = (Subscription) field.getAndSet(subscription);
            if (current != CANCELLED) {
                if (current != null) {
                    current.cancel();
                }
                return true;
            }
        }
        return false;
    }

    public static boolean deferredSetOnce(AtomicReference<Subscription> field, AtomicLong requested, Subscription s) {
        if (!setOnce(field, s)) {
            return false;
        }
        long r = requested.getAndSet(0);
        if (r != 0) {
            s.request(r);
        }
        return true;
    }

    public static void deferredRequest(AtomicReference<Subscription> field, AtomicLong requested, long n) {
        Subscription s = (Subscription) field.get();
        if (s != null) {
            s.request(n);
        } else if (validate(n)) {
            BackpressureHelper.add(requested, n);
            s = (Subscription) field.get();
            if (s != null) {
                long r = requested.getAndSet(0);
                if (r != 0) {
                    s.request(r);
                }
            }
        }
    }

    public static boolean setOnce(AtomicReference<Subscription> field, Subscription s, long request) {
        if (!setOnce(field, s)) {
            return false;
        }
        s.request(request);
        return true;
    }
}
