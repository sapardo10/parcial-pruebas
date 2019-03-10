package io.reactivex.internal.subscriptions;

import io.reactivex.disposables.Disposable;
import java.util.concurrent.atomic.AtomicReferenceArray;
import org.reactivestreams.Subscription;

public final class ArrayCompositeSubscription extends AtomicReferenceArray<Subscription> implements Disposable {
    private static final long serialVersionUID = 2746389416410565408L;

    public ArrayCompositeSubscription(int capacity) {
        super(capacity);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean setResource(int r3, org.reactivestreams.Subscription r4) {
        /*
        r2 = this;
    L_0x0000:
        r0 = r2.get(r3);
        r0 = (org.reactivestreams.Subscription) r0;
        r1 = io.reactivex.internal.subscriptions.SubscriptionHelper.CANCELLED;
        if (r0 != r1) goto L_0x0013;
    L_0x000a:
        if (r4 == 0) goto L_0x0010;
    L_0x000c:
        r4.cancel();
        goto L_0x0011;
    L_0x0011:
        r1 = 0;
        return r1;
    L_0x0013:
        r1 = r2.compareAndSet(r3, r0, r4);
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
        throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.subscriptions.ArrayCompositeSubscription.setResource(int, org.reactivestreams.Subscription):boolean");
    }

    public Subscription replaceResource(int index, Subscription resource) {
        while (true) {
            Subscription o = (Subscription) get(index);
            if (o == SubscriptionHelper.CANCELLED) {
                break;
            } else if (compareAndSet(index, o, resource)) {
                return o;
            }
        }
        if (resource != null) {
            resource.cancel();
        }
        return null;
    }

    public void dispose() {
        if (get(0) != SubscriptionHelper.CANCELLED) {
            int s = length();
            for (int i = 0; i < s; i++) {
                if (((Subscription) get(i)) != SubscriptionHelper.CANCELLED) {
                    Subscription o = (Subscription) getAndSet(i, SubscriptionHelper.CANCELLED);
                    if (o != SubscriptionHelper.CANCELLED && o != null) {
                        o.cancel();
                    }
                }
            }
        }
    }

    public boolean isDisposed() {
        return get(0) == SubscriptionHelper.CANCELLED;
    }
}
