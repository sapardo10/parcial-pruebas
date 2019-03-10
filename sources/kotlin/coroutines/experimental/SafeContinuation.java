package kotlin.coroutines.experimental;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import kotlin.Metadata;
import kotlin.PublishedApi;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@PublishedApi
@Metadata(bv = {1, 0, 2}, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0010\u0003\n\u0002\b\u0003\b\u0001\u0018\u0000 \u0015*\u0006\b\u0000\u0010\u0001 \u00002\b\u0012\u0004\u0012\u0002H\u00010\u0002:\u0002\u0015\u0016B\u0015\b\u0011\u0012\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u00028\u00000\u0002¢\u0006\u0002\u0010\u0004B\u001f\b\u0000\u0012\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u00028\u00000\u0002\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006¢\u0006\u0002\u0010\u0007J\n\u0010\r\u001a\u0004\u0018\u00010\u0006H\u0001J\u0015\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00028\u0000H\u0016¢\u0006\u0002\u0010\u0011J\u0010\u0010\u0012\u001a\u00020\u000f2\u0006\u0010\u0013\u001a\u00020\u0014H\u0016R\u0014\u0010\b\u001a\u00020\t8VX\u0004¢\u0006\u0006\u001a\u0004\b\n\u0010\u000bR\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00028\u00000\u0002X\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\f\u001a\u0004\u0018\u00010\u00068\u0002@\u0002X\u000e¢\u0006\u0002\n\u0000¨\u0006\u0017"}, d2 = {"Lkotlin/coroutines/experimental/SafeContinuation;", "T", "Lkotlin/coroutines/experimental/Continuation;", "delegate", "(Lkotlin/coroutines/experimental/Continuation;)V", "initialResult", "", "(Lkotlin/coroutines/experimental/Continuation;Ljava/lang/Object;)V", "context", "Lkotlin/coroutines/experimental/CoroutineContext;", "getContext", "()Lkotlin/coroutines/experimental/CoroutineContext;", "result", "getResult", "resume", "", "value", "(Ljava/lang/Object;)V", "resumeWithException", "exception", "", "Companion", "Fail", "kotlin-stdlib"}, k = 1, mv = {1, 1, 10})
/* compiled from: SafeContinuationJvm.kt */
public final class SafeContinuation<T> implements Continuation<T> {
    public static final Companion Companion = new Companion();
    private static final AtomicReferenceFieldUpdater<SafeContinuation<?>, Object> RESULT = AtomicReferenceFieldUpdater.newUpdater(SafeContinuation.class, Object.class, "result");
    private static final Object RESUMED = new Object();
    private static final Object UNDECIDED = new Object();
    private final Continuation<T> delegate;
    private volatile Object result;

    @Metadata(bv = {1, 0, 2}, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002RZ\u0010\u0003\u001aF\u0012\u0014\u0012\u0012\u0012\u0002\b\u0003 \u0006*\b\u0012\u0002\b\u0003\u0018\u00010\u00050\u0005\u0012\u0006\u0012\u0004\u0018\u00010\u0001 \u0006*\"\u0012\u0014\u0012\u0012\u0012\u0002\b\u0003 \u0006*\b\u0012\u0002\b\u0003\u0018\u00010\u00050\u0005\u0012\u0006\u0012\u0004\u0018\u00010\u0001\u0018\u00010\u00040\u00048\u0002X\u0004¢\u0006\b\n\u0000\u0012\u0004\b\u0007\u0010\u0002R\u0010\u0010\b\u001a\u0004\u0018\u00010\u0001X\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\t\u001a\u0004\u0018\u00010\u0001X\u0004¢\u0006\u0002\n\u0000¨\u0006\n"}, d2 = {"Lkotlin/coroutines/experimental/SafeContinuation$Companion;", "", "()V", "RESULT", "Ljava/util/concurrent/atomic/AtomicReferenceFieldUpdater;", "Lkotlin/coroutines/experimental/SafeContinuation;", "kotlin.jvm.PlatformType", "RESULT$annotations", "RESUMED", "UNDECIDED", "kotlin-stdlib"}, k = 1, mv = {1, 1, 10})
    /* compiled from: SafeContinuationJvm.kt */
    public static final class Companion {
        @JvmStatic
        private static /* synthetic */ void RESULT$annotations() {
        }

        private Companion() {
        }
    }

    @Metadata(bv = {1, 0, 2}, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0003\n\u0002\b\u0004\b\u0002\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006¨\u0006\u0007"}, d2 = {"Lkotlin/coroutines/experimental/SafeContinuation$Fail;", "", "exception", "", "(Ljava/lang/Throwable;)V", "getException", "()Ljava/lang/Throwable;", "kotlin-stdlib"}, k = 1, mv = {1, 1, 10})
    /* compiled from: SafeContinuationJvm.kt */
    private static final class Fail {
        @NotNull
        private final Throwable exception;

        public Fail(@NotNull Throwable exception) {
            Intrinsics.checkParameterIsNotNull(exception, "exception");
            this.exception = exception;
        }

        @NotNull
        public final Throwable getException() {
            return this.exception;
        }
    }

    public void resume(T r5) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:16:0x0039 in {4, 5, 11, 12, 13, 15} preds:[]
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
        r4 = this;
        r0 = r4.result;
        r1 = UNDECIDED;
        if (r0 != r1) goto L_0x0012;
    L_0x0008:
        r2 = RESULT;
        r1 = r2.compareAndSet(r4, r1, r5);
        if (r1 == 0) goto L_0x0011;
    L_0x0010:
        return;
    L_0x0011:
        goto L_0x002d;
    L_0x0012:
        r1 = kotlin.coroutines.experimental.intrinsics.IntrinsicsKt__IntrinsicsJvmKt.getCOROUTINE_SUSPENDED();
        if (r0 != r1) goto L_0x002f;
    L_0x0018:
        r1 = RESULT;
        r2 = kotlin.coroutines.experimental.intrinsics.IntrinsicsKt__IntrinsicsJvmKt.getCOROUTINE_SUSPENDED();
        r3 = RESUMED;
        r1 = r1.compareAndSet(r4, r2, r3);
        if (r1 == 0) goto L_0x002c;
    L_0x0026:
        r1 = r4.delegate;
        r1.resume(r5);
        return;
        goto L_0x0000;
    L_0x002f:
        r1 = new java.lang.IllegalStateException;
        r2 = "Already resumed";
        r1.<init>(r2);
        r1 = (java.lang.Throwable) r1;
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.coroutines.experimental.SafeContinuation.resume(java.lang.Object):void");
    }

    public void resumeWithException(@org.jetbrains.annotations.NotNull java.lang.Throwable r5) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:17:0x0043 in {5, 6, 12, 13, 14, 16} preds:[]
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
        r4 = this;
        r0 = "exception";
        kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r5, r0);
        r0 = r4.result;
        r1 = UNDECIDED;
        if (r0 != r1) goto L_0x001c;
    L_0x000d:
        r2 = RESULT;
        r3 = new kotlin.coroutines.experimental.SafeContinuation$Fail;
        r3.<init>(r5);
        r1 = r2.compareAndSet(r4, r1, r3);
        if (r1 == 0) goto L_0x001b;
    L_0x001a:
        return;
    L_0x001b:
        goto L_0x0037;
    L_0x001c:
        r1 = kotlin.coroutines.experimental.intrinsics.IntrinsicsKt__IntrinsicsJvmKt.getCOROUTINE_SUSPENDED();
        if (r0 != r1) goto L_0x0039;
    L_0x0022:
        r1 = RESULT;
        r2 = kotlin.coroutines.experimental.intrinsics.IntrinsicsKt__IntrinsicsJvmKt.getCOROUTINE_SUSPENDED();
        r3 = RESUMED;
        r1 = r1.compareAndSet(r4, r2, r3);
        if (r1 == 0) goto L_0x0036;
    L_0x0030:
        r1 = r4.delegate;
        r1.resumeWithException(r5);
        return;
        goto L_0x0005;
    L_0x0039:
        r1 = new java.lang.IllegalStateException;
        r2 = "Already resumed";
        r1.<init>(r2);
        r1 = (java.lang.Throwable) r1;
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.coroutines.experimental.SafeContinuation.resumeWithException(java.lang.Throwable):void");
    }

    public SafeContinuation(@NotNull Continuation<? super T> delegate, @Nullable Object initialResult) {
        Intrinsics.checkParameterIsNotNull(delegate, "delegate");
        this.delegate = delegate;
        this.result = initialResult;
    }

    @PublishedApi
    public SafeContinuation(@NotNull Continuation<? super T> delegate) {
        Intrinsics.checkParameterIsNotNull(delegate, "delegate");
        this(delegate, UNDECIDED);
    }

    @NotNull
    public CoroutineContext getContext() {
        return this.delegate.getContext();
    }

    @Nullable
    @PublishedApi
    public final Object getResult() {
        Object result = this.result;
        Object obj = UNDECIDED;
        if (result == obj) {
            if (RESULT.compareAndSet(this, obj, IntrinsicsKt__IntrinsicsJvmKt.getCOROUTINE_SUSPENDED())) {
                return IntrinsicsKt__IntrinsicsJvmKt.getCOROUTINE_SUSPENDED();
            }
            result = this.result;
        }
        if (result == RESUMED) {
            return IntrinsicsKt__IntrinsicsJvmKt.getCOROUTINE_SUSPENDED();
        }
        if (!(result instanceof Fail)) {
            return result;
        }
        throw ((Fail) result).getException();
    }
}
