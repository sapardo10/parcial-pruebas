package kotlin.sequences;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.experimental.Continuation;
import kotlin.coroutines.experimental.SequenceBuilder;
import kotlin.coroutines.experimental.jvm.internal.CoroutineImpl;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000\u0012\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010\u0003*\b\u0012\u0004\u0012\u0002H\u00030\u0004H\n¢\u0006\u0004\b\u0005\u0010\u0006"}, d2 = {"<anonymous>", "", "T", "R", "Lkotlin/coroutines/experimental/SequenceBuilder;", "invoke", "(Lkotlin/coroutines/experimental/SequenceBuilder;Lkotlin/coroutines/experimental/Continuation;)Ljava/lang/Object;"}, k = 3, mv = {1, 1, 10})
/* compiled from: _Sequences.kt */
final class SequencesKt___SequencesKt$zipWithNext$2 extends CoroutineImpl implements Function2<SequenceBuilder<? super R>, Continuation<? super Unit>, Object> {
    final /* synthetic */ Function2 $transform;
    Object L$0;
    Object L$1;
    Object L$2;
    Object L$3;
    private SequenceBuilder p$;
    final /* synthetic */ Sequence receiver$0;

    SequencesKt___SequencesKt$zipWithNext$2(Sequence sequence, Function2 function2, Continuation continuation) {
        this.receiver$0 = sequence;
        this.$transform = function2;
        super(2, continuation);
    }

    @NotNull
    public final Continuation<Unit> create(@NotNull SequenceBuilder<? super R> sequenceBuilder, @NotNull Continuation<? super Unit> continuation) {
        Intrinsics.checkParameterIsNotNull(sequenceBuilder, "$receiver");
        Intrinsics.checkParameterIsNotNull(continuation, "continuation");
        Continuation sequencesKt___SequencesKt$zipWithNext$2 = new SequencesKt___SequencesKt$zipWithNext$2(this.receiver$0, this.$transform, continuation);
        sequencesKt___SequencesKt$zipWithNext$2.p$ = sequenceBuilder;
        return sequencesKt___SequencesKt$zipWithNext$2;
    }

    @org.jetbrains.annotations.Nullable
    public final java.lang.Object doResume(@org.jetbrains.annotations.Nullable java.lang.Object r10, @org.jetbrains.annotations.Nullable java.lang.Throwable r11) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:24:0x006c in {3, 6, 7, 12, 13, 18, 19, 20, 22, 23} preds:[]
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
        r9 = this;
        r0 = kotlin.coroutines.experimental.intrinsics.IntrinsicsKt__IntrinsicsJvmKt.getCOROUTINE_SUSPENDED();
        r1 = r9.label;
        switch(r1) {
            case 0: goto L_0x0026;
            case 1: goto L_0x0011;
            default: goto L_0x0009;
        };
    L_0x0009:
        r10 = new java.lang.IllegalStateException;
        r11 = "call to 'resume' before 'invoke' with coroutine";
        r10.<init>(r11);
        throw r10;
    L_0x0011:
        r1 = 0;
        r2 = r1;
        r3 = r1;
        r4 = r9;
        r2 = r4.L$3;
        r3 = r4.L$2;
        r5 = r4.L$1;
        r1 = r5;
        r1 = (java.util.Iterator) r1;
        r5 = r4.L$0;
        r5 = (kotlin.coroutines.experimental.SequenceBuilder) r5;
        if (r11 != 0) goto L_0x0025;
    L_0x0024:
        goto L_0x0066;
    L_0x0025:
        throw r11;
    L_0x0026:
        if (r11 != 0) goto L_0x006b;
    L_0x0028:
        r1 = r9;
        r2 = r1.p$;
        r3 = r1.receiver$0;
        r3 = r3.iterator();
        r4 = r3.hasNext();
        if (r4 != 0) goto L_0x003a;
    L_0x0037:
        r0 = kotlin.Unit.INSTANCE;
        return r0;
    L_0x003a:
        r4 = r3.next();
        r5 = r2;
        r8 = r4;
        r4 = r1;
        r1 = r3;
        r3 = r8;
    L_0x0043:
        r2 = r1.hasNext();
        if (r2 == 0) goto L_0x0068;
    L_0x0049:
        r2 = r1.next();
        r6 = r4.$transform;
        r6 = r6.invoke(r3, r2);
        r4.L$0 = r5;
        r4.L$1 = r1;
        r4.L$2 = r3;
        r4.L$3 = r2;
        r7 = 1;
        r4.label = r7;
        r6 = r5.yield(r6, r4);
        if (r6 != r0) goto L_0x0065;
    L_0x0064:
        return r0;
    L_0x0066:
        r3 = r2;
        goto L_0x0043;
    L_0x0068:
        r0 = kotlin.Unit.INSTANCE;
        return r0;
    L_0x006b:
        throw r11;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.sequences.SequencesKt___SequencesKt$zipWithNext$2.doResume(java.lang.Object, java.lang.Throwable):java.lang.Object");
    }

    @Nullable
    public final Object invoke(@NotNull SequenceBuilder<? super R> sequenceBuilder, @NotNull Continuation<? super Unit> continuation) {
        Intrinsics.checkParameterIsNotNull(sequenceBuilder, "$receiver");
        Intrinsics.checkParameterIsNotNull(continuation, "continuation");
        return ((SequencesKt___SequencesKt$zipWithNext$2) create((SequenceBuilder) sequenceBuilder, (Continuation) continuation)).doResume(Unit.INSTANCE, null);
    }
}
