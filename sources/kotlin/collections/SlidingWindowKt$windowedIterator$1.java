package kotlin.collections;

import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.experimental.Continuation;
import kotlin.coroutines.experimental.SequenceBuilder;
import kotlin.coroutines.experimental.jvm.internal.CoroutineImpl;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000\u0014\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0002\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002*\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00020\u00040\u0003H\n¢\u0006\u0004\b\u0005\u0010\u0006"}, d2 = {"<anonymous>", "", "T", "Lkotlin/coroutines/experimental/SequenceBuilder;", "", "invoke", "(Lkotlin/coroutines/experimental/SequenceBuilder;Lkotlin/coroutines/experimental/Continuation;)Ljava/lang/Object;"}, k = 3, mv = {1, 1, 10})
/* compiled from: SlidingWindow.kt */
final class SlidingWindowKt$windowedIterator$1 extends CoroutineImpl implements Function2<SequenceBuilder<? super List<? extends T>>, Continuation<? super Unit>, Object> {
    final /* synthetic */ Iterator $iterator;
    final /* synthetic */ boolean $partialWindows;
    final /* synthetic */ boolean $reuseBuffer;
    final /* synthetic */ int $size;
    final /* synthetic */ int $step;
    int I$0;
    int I$1;
    Object L$0;
    Object L$1;
    Object L$2;
    Object L$3;
    private SequenceBuilder p$;

    SlidingWindowKt$windowedIterator$1(int i, int i2, Iterator it, boolean z, boolean z2, Continuation continuation) {
        this.$step = i;
        this.$size = i2;
        this.$iterator = it;
        this.$reuseBuffer = z;
        this.$partialWindows = z2;
        super(2, continuation);
    }

    @NotNull
    public final Continuation<Unit> create(@NotNull SequenceBuilder<? super List<? extends T>> sequenceBuilder, @NotNull Continuation<? super Unit> continuation) {
        Intrinsics.checkParameterIsNotNull(sequenceBuilder, "$receiver");
        Intrinsics.checkParameterIsNotNull(continuation, "continuation");
        Continuation slidingWindowKt$windowedIterator$1 = new SlidingWindowKt$windowedIterator$1(this.$step, this.$size, this.$iterator, this.$reuseBuffer, this.$partialWindows, continuation);
        slidingWindowKt$windowedIterator$1.p$ = sequenceBuilder;
        return slidingWindowKt$windowedIterator$1;
    }

    @org.jetbrains.annotations.Nullable
    public final java.lang.Object doResume(@org.jetbrains.annotations.Nullable java.lang.Object r14, @org.jetbrains.annotations.Nullable java.lang.Throwable r15) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:102:0x01cc in {3, 6, 7, 10, 11, 14, 15, 18, 19, 22, 23, 27, 32, 37, 38, 41, 42, 43, 44, 45, 52, 53, 56, 57, 58, 59, 60, 67, 68, 71, 72, 73, 74, 75, 78, 83, 84, 87, 88, 89, 94, 95, 96, 97, 98, 100, 101} preds:[]
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
        r13 = this;
        r0 = kotlin.coroutines.experimental.intrinsics.IntrinsicsKt__IntrinsicsJvmKt.getCOROUTINE_SUSPENDED();
        r1 = r13.label;
        r2 = 0;
        r3 = 0;
        r4 = 1;
        switch(r1) {
            case 0: goto L_0x008d;
            case 1: goto L_0x006a;
            case 2: goto L_0x0057;
            case 3: goto L_0x003f;
            case 4: goto L_0x0026;
            case 5: goto L_0x0014;
            default: goto L_0x000c;
        };
    L_0x000c:
        r14 = new java.lang.IllegalStateException;
        r15 = "call to 'resume' before 'invoke' with coroutine";
        r14.<init>(r15);
        throw r14;
    L_0x0014:
        r0 = r3;
        r1 = r2;
        r2 = r13;
        r3 = r2.L$0;
        r0 = r3;
        r0 = (kotlin.collections.RingBuffer) r0;
        r1 = r2.I$0;
        if (r15 != 0) goto L_0x0025;
    L_0x0020:
        r5 = r15;
        r3 = r2;
        r15 = r14;
        goto L_0x01c1;
    L_0x0025:
        throw r15;
    L_0x0026:
        r1 = r3;
        r3 = r13;
        r5 = r3.L$1;
        r1 = r5;
        r1 = (kotlin.collections.RingBuffer) r1;
        r2 = r3.I$0;
        r5 = r3.L$0;
        r5 = (kotlin.coroutines.experimental.SequenceBuilder) r5;
        if (r15 != 0) goto L_0x003e;
    L_0x0035:
        r11 = r15;
        r15 = r14;
        r14 = r0;
        r0 = r1;
        r1 = r2;
        r2 = r3;
        r3 = r11;
        goto L_0x01a1;
    L_0x003e:
        throw r15;
    L_0x003f:
        r1 = r3;
        r5 = r13;
        r6 = r5.L$3;
        r6 = (java.util.Iterator) r6;
        r1 = r5.L$2;
        r7 = r5.L$1;
        r3 = r7;
        r3 = (kotlin.collections.RingBuffer) r3;
        r2 = r5.I$0;
        r7 = r5.L$0;
        r7 = (kotlin.coroutines.experimental.SequenceBuilder) r7;
        if (r15 != 0) goto L_0x0056;
    L_0x0054:
        goto L_0x0162;
    L_0x0056:
        throw r15;
    L_0x0057:
        r0 = r2;
        r1 = r3;
        r3 = r13;
        r0 = r3.I$1;
        r4 = r3.L$0;
        r1 = r4;
        r1 = (java.util.ArrayList) r1;
        r2 = r3.I$0;
        if (r15 != 0) goto L_0x0069;
    L_0x0065:
        r5 = r15;
        r15 = r14;
        goto L_0x0118;
    L_0x0069:
        throw r15;
    L_0x006a:
        r1 = r3;
        r5 = r2;
        r6 = r13;
        r7 = r6.L$3;
        r7 = (java.util.Iterator) r7;
        r1 = r6.L$2;
        r5 = r6.I$1;
        r8 = r6.L$1;
        r3 = r8;
        r3 = (java.util.ArrayList) r3;
        r2 = r6.I$0;
        r8 = r6.L$0;
        r8 = (kotlin.coroutines.experimental.SequenceBuilder) r8;
        if (r15 != 0) goto L_0x008c;
    L_0x0082:
        r11 = r15;
        r15 = r14;
        r14 = r0;
        r0 = r5;
        r5 = r11;
        r12 = r6;
        r6 = r1;
        r1 = r3;
        r3 = r12;
        goto L_0x00da;
    L_0x008c:
        throw r15;
    L_0x008d:
        if (r15 != 0) goto L_0x01cb;
    L_0x008f:
        r1 = r13;
        r2 = r1.p$;
        r3 = r1.$step;
        r5 = r1.$size;
        r3 = r3 - r5;
        if (r3 < 0) goto L_0x011c;
    L_0x0099:
        r6 = new java.util.ArrayList;
        r6.<init>(r5);
        r5 = r6;
        r6 = 0;
        r7 = r1.$iterator;
        r8 = r2;
        r2 = r3;
        r3 = r1;
        r1 = r5;
        r5 = r15;
        r15 = r14;
        r14 = r0;
        r0 = r6;
    L_0x00aa:
        r6 = r7.hasNext();
        if (r6 == 0) goto L_0x00ee;
    L_0x00b0:
        r6 = r7.next();
        if (r0 <= 0) goto L_0x00b9;
    L_0x00b6:
        r0 = r0 + -1;
        goto L_0x00ed;
    L_0x00b9:
        r1.add(r6);
        r9 = r1.size();
        r10 = r3.$size;
        if (r9 != r10) goto L_0x00ec;
    L_0x00c4:
        r3.L$0 = r8;
        r3.I$0 = r2;
        r3.L$1 = r1;
        r3.I$1 = r0;
        r3.L$2 = r6;
        r3.L$3 = r7;
        r3.label = r4;
        r9 = r8.yield(r1, r3);
        if (r9 != r14) goto L_0x00d9;
    L_0x00d8:
        return r14;
    L_0x00da:
        r9 = r3.$reuseBuffer;
        if (r9 == 0) goto L_0x00e2;
    L_0x00de:
        r1.clear();
        goto L_0x00ea;
    L_0x00e2:
        r9 = new java.util.ArrayList;
        r10 = r3.$size;
        r9.<init>(r10);
        r1 = r9;
    L_0x00ea:
        r0 = r2;
        goto L_0x00ed;
    L_0x00ed:
        goto L_0x00aa;
    L_0x00ee:
        r6 = r1;
        r6 = (java.util.Collection) r6;
        r6 = r6.isEmpty();
        r4 = r4 ^ r6;
        if (r4 == 0) goto L_0x011a;
    L_0x00f8:
        r4 = r3.$partialWindows;
        if (r4 != 0) goto L_0x0107;
    L_0x00fc:
        r4 = r1.size();
        r6 = r3.$size;
        if (r4 != r6) goto L_0x0105;
    L_0x0104:
        goto L_0x0107;
    L_0x0105:
        goto L_0x01c8;
    L_0x0107:
        r3.I$0 = r2;
        r3.L$0 = r1;
        r3.I$1 = r0;
        r4 = 2;
        r3.label = r4;
        r4 = r8.yield(r1, r3);
        if (r4 != r14) goto L_0x0117;
    L_0x0116:
        return r14;
    L_0x0118:
        goto L_0x01c8;
    L_0x011a:
        goto L_0x01c8;
    L_0x011c:
        r6 = new kotlin.collections.RingBuffer;
        r6.<init>(r5);
        r5 = r6;
        r6 = r1.$iterator;
        r7 = r2;
        r2 = r3;
        r3 = r5;
        r5 = r1;
    L_0x0128:
        r1 = r6.hasNext();
        if (r1 == 0) goto L_0x016a;
    L_0x012e:
        r1 = r6.next();
        r3.add(r1);
        r8 = r3.isFull();
        if (r8 == 0) goto L_0x0168;
    L_0x013b:
        r8 = r5.$reuseBuffer;
        if (r8 == 0) goto L_0x0143;
    L_0x013f:
        r8 = r3;
        r8 = (java.util.List) r8;
        goto L_0x014d;
    L_0x0143:
        r8 = new java.util.ArrayList;
        r9 = r3;
        r9 = (java.util.Collection) r9;
        r8.<init>(r9);
        r8 = (java.util.List) r8;
    L_0x014d:
        r5.L$0 = r7;
        r5.I$0 = r2;
        r5.L$1 = r3;
        r5.L$2 = r1;
        r5.L$3 = r6;
        r9 = 3;
        r5.label = r9;
        r8 = r7.yield(r8, r5);
        if (r8 != r0) goto L_0x0161;
    L_0x0160:
        return r0;
    L_0x0162:
        r8 = r5.$step;
        r3.removeFirst(r8);
        goto L_0x0169;
    L_0x0169:
        goto L_0x0128;
    L_0x016a:
        r1 = r5.$partialWindows;
        if (r1 == 0) goto L_0x01c5;
    L_0x016e:
        r1 = r2;
        r2 = r5;
        r5 = r7;
        r11 = r15;
        r15 = r14;
        r14 = r0;
        r0 = r3;
        r3 = r11;
    L_0x0176:
        r6 = r0.size();
        r7 = r2.$step;
        if (r6 <= r7) goto L_0x01a7;
    L_0x017e:
        r6 = r2.$reuseBuffer;
        if (r6 == 0) goto L_0x0186;
    L_0x0182:
        r6 = r0;
        r6 = (java.util.List) r6;
        goto L_0x0190;
    L_0x0186:
        r6 = new java.util.ArrayList;
        r7 = r0;
        r7 = (java.util.Collection) r7;
        r6.<init>(r7);
        r6 = (java.util.List) r6;
    L_0x0190:
        r2.L$0 = r5;
        r2.I$0 = r1;
        r2.L$1 = r0;
        r7 = 4;
        r2.label = r7;
        r6 = r5.yield(r6, r2);
        if (r6 != r14) goto L_0x01a0;
    L_0x019f:
        return r14;
    L_0x01a1:
        r6 = r2.$step;
        r0.removeFirst(r6);
        goto L_0x0176;
    L_0x01a7:
        r6 = r0;
        r6 = (java.util.Collection) r6;
        r6 = r6.isEmpty();
        r4 = r4 ^ r6;
        if (r4 == 0) goto L_0x01c2;
    L_0x01b1:
        r2.I$0 = r1;
        r2.L$0 = r0;
        r4 = 5;
        r2.label = r4;
        r4 = r5.yield(r0, r2);
        if (r4 != r14) goto L_0x01bf;
    L_0x01be:
        return r14;
    L_0x01bf:
        r5 = r3;
        r3 = r2;
    L_0x01c1:
        goto L_0x01c8;
    L_0x01c2:
        r5 = r3;
        r3 = r2;
        goto L_0x01c8;
    L_0x01c5:
        r3 = r5;
        r5 = r15;
        r15 = r14;
    L_0x01c8:
        r14 = kotlin.Unit.INSTANCE;
        return r14;
    L_0x01cb:
        throw r15;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.collections.SlidingWindowKt$windowedIterator$1.doResume(java.lang.Object, java.lang.Throwable):java.lang.Object");
    }

    @Nullable
    public final Object invoke(@NotNull SequenceBuilder<? super List<? extends T>> sequenceBuilder, @NotNull Continuation<? super Unit> continuation) {
        Intrinsics.checkParameterIsNotNull(sequenceBuilder, "$receiver");
        Intrinsics.checkParameterIsNotNull(continuation, "continuation");
        return ((SlidingWindowKt$windowedIterator$1) create((SequenceBuilder) sequenceBuilder, (Continuation) continuation)).doResume(Unit.INSTANCE, null);
    }
}
