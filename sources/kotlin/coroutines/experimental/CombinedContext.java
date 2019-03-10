package kotlin.coroutines.experimental;

import kotlin.Metadata;
import kotlin.coroutines.experimental.CoroutineContext.DefaultImpls;
import kotlin.coroutines.experimental.CoroutineContext.Element;
import kotlin.coroutines.experimental.CoroutineContext.Key;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\u0000\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0000\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0001\u0012\u0006\u0010\u0003\u001a\u00020\u0004¢\u0006\u0002\u0010\u0005J\u0010\u0010\n\u001a\u00020\u000b2\u0006\u0010\u0003\u001a\u00020\u0004H\u0002J\u0010\u0010\f\u001a\u00020\u000b2\u0006\u0010\r\u001a\u00020\u0000H\u0002J\u0013\u0010\u000e\u001a\u00020\u000b2\b\u0010\u000f\u001a\u0004\u0018\u00010\u0010H\u0002J5\u0010\u0011\u001a\u0002H\u0012\"\u0004\b\u0000\u0010\u00122\u0006\u0010\u0013\u001a\u0002H\u00122\u0018\u0010\u0014\u001a\u0014\u0012\u0004\u0012\u0002H\u0012\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u0002H\u00120\u0015H\u0016¢\u0006\u0002\u0010\u0016J(\u0010\u0017\u001a\u0004\u0018\u0001H\u0018\"\b\b\u0000\u0010\u0018*\u00020\u00042\f\u0010\u0019\u001a\b\u0012\u0004\u0012\u0002H\u00180\u001aH\u0002¢\u0006\u0002\u0010\u001bJ\b\u0010\u001c\u001a\u00020\u001dH\u0016J\u0014\u0010\u001e\u001a\u00020\u00012\n\u0010\u0019\u001a\u0006\u0012\u0002\b\u00030\u001aH\u0016J\b\u0010\u001f\u001a\u00020\u001dH\u0002J\b\u0010 \u001a\u00020!H\u0016R\u0011\u0010\u0003\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0011\u0010\u0002\u001a\u00020\u0001¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\t¨\u0006\""}, d2 = {"Lkotlin/coroutines/experimental/CombinedContext;", "Lkotlin/coroutines/experimental/CoroutineContext;", "left", "element", "Lkotlin/coroutines/experimental/CoroutineContext$Element;", "(Lkotlin/coroutines/experimental/CoroutineContext;Lkotlin/coroutines/experimental/CoroutineContext$Element;)V", "getElement", "()Lkotlin/coroutines/experimental/CoroutineContext$Element;", "getLeft", "()Lkotlin/coroutines/experimental/CoroutineContext;", "contains", "", "containsAll", "context", "equals", "other", "", "fold", "R", "initial", "operation", "Lkotlin/Function2;", "(Ljava/lang/Object;Lkotlin/jvm/functions/Function2;)Ljava/lang/Object;", "get", "E", "key", "Lkotlin/coroutines/experimental/CoroutineContext$Key;", "(Lkotlin/coroutines/experimental/CoroutineContext$Key;)Lkotlin/coroutines/experimental/CoroutineContext$Element;", "hashCode", "", "minusKey", "size", "toString", "", "kotlin-stdlib"}, k = 1, mv = {1, 1, 10})
/* compiled from: CoroutineContextImpl.kt */
public final class CombinedContext implements CoroutineContext {
    @NotNull
    private final Element element;
    @NotNull
    private final CoroutineContext left;

    private final boolean containsAll(kotlin.coroutines.experimental.CombinedContext r5) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:13:0x0029 in {4, 7, 10, 12} preds:[]
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
        r4 = this;
        r0 = r5;
        r1 = r0.element;
        r1 = r4.contains(r1);
        if (r1 != 0) goto L_0x000c;
    L_0x000a:
        r1 = 0;
        return r1;
    L_0x000c:
        r1 = r0.left;
        r2 = r1 instanceof kotlin.coroutines.experimental.CombinedContext;
        if (r2 == 0) goto L_0x0017;
    L_0x0012:
        r0 = r1;
        r0 = (kotlin.coroutines.experimental.CombinedContext) r0;
        goto L_0x0001;
    L_0x0017:
        if (r1 == 0) goto L_0x0021;
    L_0x0019:
        r2 = r1;
        r2 = (kotlin.coroutines.experimental.CoroutineContext.Element) r2;
        r2 = r4.contains(r2);
        return r2;
    L_0x0021:
        r2 = new kotlin.TypeCastException;
        r3 = "null cannot be cast to non-null type kotlin.coroutines.experimental.CoroutineContext.Element";
        r2.<init>(r3);
        throw r2;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.coroutines.experimental.CombinedContext.containsAll(kotlin.coroutines.experimental.CombinedContext):boolean");
    }

    public CombinedContext(@NotNull CoroutineContext left, @NotNull Element element) {
        Intrinsics.checkParameterIsNotNull(left, TtmlNode.LEFT);
        Intrinsics.checkParameterIsNotNull(element, "element");
        this.left = left;
        this.element = element;
    }

    @NotNull
    public final Element getElement() {
        return this.element;
    }

    @NotNull
    public final CoroutineContext getLeft() {
        return this.left;
    }

    @NotNull
    public CoroutineContext plus(@NotNull CoroutineContext context) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        return DefaultImpls.plus(this, context);
    }

    @Nullable
    public <E extends Element> E get(@NotNull Key<E> key) {
        Intrinsics.checkParameterIsNotNull(key, "key");
        CombinedContext cur = this;
        while (true) {
            Element it = cur.element.get(key);
            if (it != null) {
                return it;
            }
            CoroutineContext next = cur.left;
            if (!(next instanceof CombinedContext)) {
                return next.get(key);
            }
            cur = (CombinedContext) next;
        }
    }

    public <R> R fold(R initial, @NotNull Function2<? super R, ? super Element, ? extends R> operation) {
        Intrinsics.checkParameterIsNotNull(operation, "operation");
        return operation.invoke(this.left.fold(initial, operation), this.element);
    }

    @NotNull
    public CoroutineContext minusKey(@NotNull Key<?> key) {
        Intrinsics.checkParameterIsNotNull(key, "key");
        if (this.element.get(key) != null) {
            return this.left;
        }
        CoroutineContext coroutineContext;
        CoroutineContext newLeft = this.left.minusKey(key);
        if (newLeft == this.left) {
            coroutineContext = this;
        } else if (newLeft == EmptyCoroutineContext.INSTANCE) {
            coroutineContext = this.element;
        } else {
            coroutineContext = new CombinedContext(newLeft, this.element);
        }
        return coroutineContext;
    }

    private final int size() {
        CoroutineContext coroutineContext = this.left;
        return coroutineContext instanceof CombinedContext ? ((CombinedContext) coroutineContext).size() + 1 : 2;
    }

    private final boolean contains(Element element) {
        return Intrinsics.areEqual(get(element.getKey()), (Object) element);
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (!(other instanceof CombinedContext) || ((CombinedContext) other).size() != size() || !((CombinedContext) other).containsAll(this)) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        return this.left.hashCode() + this.element.hashCode();
    }

    @NotNull
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        stringBuilder.append((String) fold("", CombinedContext$toString$1.INSTANCE));
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}
