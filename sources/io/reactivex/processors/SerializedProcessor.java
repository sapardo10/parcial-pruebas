package io.reactivex.processors;

import io.reactivex.annotations.Nullable;
import io.reactivex.internal.util.AppendOnlyLinkedArrayList;
import io.reactivex.internal.util.NotificationLite;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

final class SerializedProcessor<T> extends FlowableProcessor<T> {
    final FlowableProcessor<T> actual;
    volatile boolean done;
    boolean emitting;
    AppendOnlyLinkedArrayList<Object> queue;

    void emitLoop() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:14:0x0017 in {6, 9, 13} preds:[]
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
        r2 = this;
    L_0x0000:
        monitor-enter(r2);
        r0 = r2.queue;	 Catch:{ all -> 0x0014 }
        if (r0 != 0) goto L_0x000a;	 Catch:{ all -> 0x0014 }
    L_0x0005:
        r1 = 0;	 Catch:{ all -> 0x0014 }
        r2.emitting = r1;	 Catch:{ all -> 0x0014 }
        monitor-exit(r2);	 Catch:{ all -> 0x0014 }
        return;	 Catch:{ all -> 0x0014 }
    L_0x000a:
        r1 = 0;	 Catch:{ all -> 0x0014 }
        r2.queue = r1;	 Catch:{ all -> 0x0014 }
        monitor-exit(r2);	 Catch:{ all -> 0x0014 }
        r1 = r2.actual;
        r0.accept(r1);
        goto L_0x0000;
    L_0x0014:
        r0 = move-exception;
        monitor-exit(r2);	 Catch:{ all -> 0x0014 }
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: io.reactivex.processors.SerializedProcessor.emitLoop():void");
    }

    SerializedProcessor(FlowableProcessor<T> actual) {
        this.actual = actual;
    }

    protected void subscribeActual(Subscriber<? super T> s) {
        this.actual.subscribe(s);
    }

    public void onSubscribe(Subscription s) {
        boolean cancel;
        if (this.done) {
            cancel = true;
        } else {
            synchronized (this) {
                if (this.done) {
                    cancel = true;
                } else if (this.emitting) {
                    AppendOnlyLinkedArrayList<Object> q = this.queue;
                    if (q == null) {
                        q = new AppendOnlyLinkedArrayList(4);
                        this.queue = q;
                    }
                    q.add(NotificationLite.subscription(s));
                    return;
                } else {
                    this.emitting = true;
                    cancel = false;
                }
            }
        }
        if (cancel) {
            s.cancel();
        } else {
            this.actual.onSubscribe(s);
            emitLoop();
        }
    }

    public void onNext(T t) {
        if (!this.done) {
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
                    this.actual.onNext(t);
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
        r0 = r3.done;	 Catch:{ all -> 0x003f }
        if (r0 == 0) goto L_0x000f;
    L_0x000d:
        r0 = 1;
        goto L_0x0032;
    L_0x000f:
        r0 = 1;
        r3.done = r0;	 Catch:{ all -> 0x003f }
        r1 = r3.emitting;	 Catch:{ all -> 0x003f }
        if (r1 == 0) goto L_0x002e;
    L_0x0016:
        r0 = r3.queue;	 Catch:{ all -> 0x003f }
        if (r0 != 0) goto L_0x0024;
    L_0x001a:
        r1 = new io.reactivex.internal.util.AppendOnlyLinkedArrayList;	 Catch:{ all -> 0x003f }
        r2 = 4;
        r1.<init>(r2);	 Catch:{ all -> 0x003f }
        r0 = r1;
        r3.queue = r0;	 Catch:{ all -> 0x003f }
        goto L_0x0025;
    L_0x0025:
        r1 = io.reactivex.internal.util.NotificationLite.error(r4);	 Catch:{ all -> 0x003f }
        r0.setFirst(r1);	 Catch:{ all -> 0x003f }
        monitor-exit(r3);	 Catch:{ all -> 0x003f }
        return;
    L_0x002e:
        r1 = 0;
        r3.emitting = r0;	 Catch:{ all -> 0x003f }
        r0 = r1;
    L_0x0032:
        monitor-exit(r3);	 Catch:{ all -> 0x003f }
        if (r0 == 0) goto L_0x0039;
    L_0x0035:
        io.reactivex.plugins.RxJavaPlugins.onError(r4);
        return;
    L_0x0039:
        r1 = r3.actual;
        r1.onError(r4);
        return;
    L_0x003f:
        r0 = move-exception;
        monitor-exit(r3);	 Catch:{ all -> 0x003f }
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: io.reactivex.processors.SerializedProcessor.onError(java.lang.Throwable):void");
    }

    public void onComplete() {
        if (!this.done) {
            synchronized (this) {
                if (this.done) {
                    return;
                }
                this.done = true;
                if (this.emitting) {
                    AppendOnlyLinkedArrayList<Object> q = this.queue;
                    if (q == null) {
                        q = new AppendOnlyLinkedArrayList(4);
                        this.queue = q;
                    }
                    q.add(NotificationLite.complete());
                    return;
                }
                this.emitting = true;
                this.actual.onComplete();
            }
        }
    }

    public boolean hasSubscribers() {
        return this.actual.hasSubscribers();
    }

    public boolean hasThrowable() {
        return this.actual.hasThrowable();
    }

    @Nullable
    public Throwable getThrowable() {
        return this.actual.getThrowable();
    }

    public boolean hasComplete() {
        return this.actual.hasComplete();
    }
}
