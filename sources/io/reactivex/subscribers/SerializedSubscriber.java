package io.reactivex.subscribers;

import io.reactivex.FlowableSubscriber;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.AppendOnlyLinkedArrayList;
import io.reactivex.internal.util.NotificationLite;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class SerializedSubscriber<T> implements FlowableSubscriber<T>, Subscription {
    static final int QUEUE_LINK_SIZE = 4;
    final boolean delayError;
    volatile boolean done;
    final Subscriber<? super T> downstream;
    boolean emitting;
    AppendOnlyLinkedArrayList<Object> queue;
    Subscription upstream;

    void emitLoop() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:17:0x001c in {6, 11, 12, 16} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r2 = this;
    L_0x0000:
        monitor-enter(r2);
        r0 = r2.queue;	 Catch:{ all -> 0x0019 }
        if (r0 != 0) goto L_0x000a;	 Catch:{ all -> 0x0019 }
    L_0x0005:
        r1 = 0;	 Catch:{ all -> 0x0019 }
        r2.emitting = r1;	 Catch:{ all -> 0x0019 }
        monitor-exit(r2);	 Catch:{ all -> 0x0019 }
        return;	 Catch:{ all -> 0x0019 }
    L_0x000a:
        r1 = 0;	 Catch:{ all -> 0x0019 }
        r2.queue = r1;	 Catch:{ all -> 0x0019 }
        monitor-exit(r2);	 Catch:{ all -> 0x0019 }
        r1 = r2.downstream;
        r1 = r0.accept(r1);
        if (r1 == 0) goto L_0x0017;
    L_0x0016:
        return;
        goto L_0x0000;
    L_0x0019:
        r0 = move-exception;
        monitor-exit(r2);	 Catch:{ all -> 0x0019 }
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: io.reactivex.subscribers.SerializedSubscriber.emitLoop():void");
    }

    public SerializedSubscriber(Subscriber<? super T> downstream) {
        this(downstream, false);
    }

    public SerializedSubscriber(Subscriber<? super T> actual, boolean delayError) {
        this.downstream = actual;
        this.delayError = delayError;
    }

    public void onSubscribe(Subscription s) {
        if (SubscriptionHelper.validate(this.upstream, s)) {
            this.upstream = s;
            this.downstream.onSubscribe(this);
        }
    }

    public void onNext(T t) {
        if (!this.done) {
            if (t == null) {
                this.upstream.cancel();
                onError(new NullPointerException("onNext called with null. Null values are generally not allowed in 2.x operators and sources."));
                return;
            }
            synchronized (this) {
                if (this.done) {
                } else if (this.emitting) {
                    AppendOnlyLinkedArrayList<Object> q = this.queue;
                    if (q == null) {
                        q = new AppendOnlyLinkedArrayList(4);
                        this.queue = q;
                    }
                    q.add(NotificationLite.next(t));
                } else {
                    this.emitting = true;
                    this.downstream.onNext(t);
                    emitLoop();
                }
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onError(java.lang.Throwable r4) {
        /*
        r3 = this;
        r0 = r3.done;
        if (r0 == 0) goto L_0x0008;
    L_0x0004:
        io.reactivex.plugins.RxJavaPlugins.onError(r4);
        return;
    L_0x0008:
        monitor-enter(r3);
        r0 = r3.done;	 Catch:{ all -> 0x0048 }
        if (r0 == 0) goto L_0x000f;
    L_0x000d:
        r0 = 1;
        goto L_0x003b;
    L_0x000f:
        r0 = r3.emitting;	 Catch:{ all -> 0x0048 }
        r1 = 1;
        if (r0 == 0) goto L_0x0036;
    L_0x0014:
        r3.done = r1;	 Catch:{ all -> 0x0048 }
        r0 = r3.queue;	 Catch:{ all -> 0x0048 }
        if (r0 != 0) goto L_0x0024;
    L_0x001a:
        r1 = new io.reactivex.internal.util.AppendOnlyLinkedArrayList;	 Catch:{ all -> 0x0048 }
        r2 = 4;
        r1.<init>(r2);	 Catch:{ all -> 0x0048 }
        r0 = r1;
        r3.queue = r0;	 Catch:{ all -> 0x0048 }
        goto L_0x0025;
    L_0x0025:
        r1 = io.reactivex.internal.util.NotificationLite.error(r4);	 Catch:{ all -> 0x0048 }
        r2 = r3.delayError;	 Catch:{ all -> 0x0048 }
        if (r2 == 0) goto L_0x0031;
    L_0x002d:
        r0.add(r1);	 Catch:{ all -> 0x0048 }
        goto L_0x0034;
    L_0x0031:
        r0.setFirst(r1);	 Catch:{ all -> 0x0048 }
    L_0x0034:
        monitor-exit(r3);	 Catch:{ all -> 0x0048 }
        return;
    L_0x0036:
        r3.done = r1;	 Catch:{ all -> 0x0048 }
        r3.emitting = r1;	 Catch:{ all -> 0x0048 }
        r0 = 0;
    L_0x003b:
        monitor-exit(r3);	 Catch:{ all -> 0x0048 }
        if (r0 == 0) goto L_0x0042;
    L_0x003e:
        io.reactivex.plugins.RxJavaPlugins.onError(r4);
        return;
    L_0x0042:
        r1 = r3.downstream;
        r1.onError(r4);
        return;
    L_0x0048:
        r0 = move-exception;
        monitor-exit(r3);	 Catch:{ all -> 0x0048 }
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: io.reactivex.subscribers.SerializedSubscriber.onError(java.lang.Throwable):void");
    }

    public void onComplete() {
        if (!this.done) {
            synchronized (this) {
                if (this.done) {
                } else if (this.emitting) {
                    AppendOnlyLinkedArrayList<Object> q = this.queue;
                    if (q == null) {
                        q = new AppendOnlyLinkedArrayList(4);
                        this.queue = q;
                    }
                    q.add(NotificationLite.complete());
                } else {
                    this.done = true;
                    this.emitting = true;
                    this.downstream.onComplete();
                }
            }
        }
    }

    public void request(long n) {
        this.upstream.request(n);
    }

    public void cancel() {
        this.upstream.cancel();
    }
}
