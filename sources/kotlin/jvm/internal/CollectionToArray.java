package kotlin.jvm.internal;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import kotlin.Metadata;
import kotlin.jvm.JvmName;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import org.jetbrains.annotations.NotNull;

@Metadata(bv = {1, 0, 2}, d1 = {"\u00002\n\u0000\n\u0002\u0010\u0011\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u001e\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a#\u0010\u0006\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00020\u00012\n\u0010\u0007\u001a\u0006\u0012\u0002\b\u00030\bH\u0007¢\u0006\u0004\b\t\u0010\n\u001a5\u0010\u0006\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00020\u00012\n\u0010\u0007\u001a\u0006\u0012\u0002\b\u00030\b2\u0010\u0010\u000b\u001a\f\u0012\u0006\u0012\u0004\u0018\u00010\u0002\u0018\u00010\u0001H\u0007¢\u0006\u0004\b\t\u0010\f\u001a~\u0010\r\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00020\u00012\n\u0010\u0007\u001a\u0006\u0012\u0002\b\u00030\b2\u0014\u0010\u000e\u001a\u0010\u0012\f\u0012\n\u0012\u0006\u0012\u0004\u0018\u00010\u00020\u00010\u000f2\u001a\u0010\u0010\u001a\u0016\u0012\u0004\u0012\u00020\u0005\u0012\f\u0012\n\u0012\u0006\u0012\u0004\u0018\u00010\u00020\u00010\u00112(\u0010\u0012\u001a$\u0012\f\u0012\n\u0012\u0006\u0012\u0004\u0018\u00010\u00020\u0001\u0012\u0004\u0012\u00020\u0005\u0012\f\u0012\n\u0012\u0006\u0012\u0004\u0018\u00010\u00020\u00010\u0013H\b¢\u0006\u0002\u0010\u0014\"\u0018\u0010\u0000\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00020\u0001X\u0004¢\u0006\u0004\n\u0002\u0010\u0003\"\u000e\u0010\u0004\u001a\u00020\u0005XT¢\u0006\u0002\n\u0000¨\u0006\u0015"}, d2 = {"EMPTY", "", "", "[Ljava/lang/Object;", "MAX_SIZE", "", "collectionToArray", "collection", "", "toArray", "(Ljava/util/Collection;)[Ljava/lang/Object;", "a", "(Ljava/util/Collection;[Ljava/lang/Object;)[Ljava/lang/Object;", "toArrayImpl", "empty", "Lkotlin/Function0;", "alloc", "Lkotlin/Function1;", "trim", "Lkotlin/Function2;", "(Ljava/util/Collection;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function2;)[Ljava/lang/Object;", "kotlin-stdlib"}, k = 2, mv = {1, 1, 10})
@JvmName(name = "CollectionToArray")
/* compiled from: CollectionToArray.kt */
public final class CollectionToArray {
    private static final Object[] EMPTY = new Object[0];
    private static final int MAX_SIZE = 2147483645;

    @org.jetbrains.annotations.NotNull
    @kotlin.jvm.JvmName(name = "toArray")
    public static final java.lang.Object[] toArray(@org.jetbrains.annotations.NotNull java.util.Collection<?> r10, @org.jetbrains.annotations.Nullable java.lang.Object[] r11) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:49:0x00af in {6, 12, 13, 16, 19, 25, 30, 32, 33, 34, 39, 40, 41, 42, 43, 44, 46, 48} preds:[]
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
        r0 = "collection";
        kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r10, r0);
        if (r11 == 0) goto L_0x00a7;
        r0 = 0;
        r1 = r0;
        r2 = r10.size();
        r3 = 0;
        if (r2 != 0) goto L_0x001a;
    L_0x0012:
        r4 = r0;
        r5 = r11.length;
        if (r5 <= 0) goto L_0x0019;
    L_0x0016:
        r11[r0] = r3;
    L_0x0019:
        goto L_0x002b;
    L_0x001a:
        r4 = r10.iterator();
        r5 = r4.hasNext();
        if (r5 != 0) goto L_0x002e;
    L_0x0024:
        r5 = r0;
        r6 = r11.length;
        if (r6 <= 0) goto L_0x0019;
    L_0x0028:
        r11[r0] = r3;
        goto L_0x0019;
    L_0x002b:
        r3 = r11;
        goto L_0x009a;
    L_0x002e:
        r5 = r2;
        r6 = r0;
        r7 = r11.length;
        if (r5 > r7) goto L_0x0036;
    L_0x0034:
        r7 = r11;
        goto L_0x0046;
    L_0x0036:
        r7 = r11.getClass();
        r7 = r7.getComponentType();
        r7 = java.lang.reflect.Array.newInstance(r7, r5);
        if (r7 == 0) goto L_0x009f;
    L_0x0044:
        r7 = (java.lang.Object[]) r7;
    L_0x0046:
        r5 = r7;
        r6 = 0;
        r7 = r6 + 1;
        r8 = r4.next();
        r5[r6] = r8;
        r6 = r5.length;
        if (r7 < r6) goto L_0x0081;
    L_0x0054:
        r6 = r4.hasNext();
        if (r6 != 0) goto L_0x005c;
    L_0x005a:
        r3 = r5;
        goto L_0x009a;
    L_0x005c:
        r6 = r7 * 3;
        r6 = r6 + 1;
        r6 = r6 >>> 1;
        if (r6 > r7) goto L_0x0075;
    L_0x0064:
        r8 = 2147483645; // 0x7ffffffd float:NaN double:1.060997894E-314;
        if (r7 >= r8) goto L_0x006d;
    L_0x0069:
        r6 = 2147483645; // 0x7ffffffd float:NaN double:1.060997894E-314;
        goto L_0x0076;
    L_0x006d:
        r0 = new java.lang.OutOfMemoryError;
        r0.<init>();
        r0 = (java.lang.Throwable) r0;
        throw r0;
    L_0x0076:
        r8 = java.util.Arrays.copyOf(r5, r6);
        r9 = "Arrays.copyOf(result, newSize)";
        kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r8, r9);
        r5 = r8;
        goto L_0x009c;
    L_0x0081:
        r6 = r4.hasNext();
        if (r6 != 0) goto L_0x009b;
    L_0x0087:
        r6 = r5;
        r8 = r7;
        if (r6 != r11) goto L_0x008f;
    L_0x008b:
        r11[r8] = r3;
        r3 = r11;
        goto L_0x0098;
    L_0x008f:
        r3 = java.util.Arrays.copyOf(r6, r8);
        r9 = "Arrays.copyOf(result, size)";
        kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r3, r9);
    L_0x009a:
        return r3;
        r6 = r7;
        goto L_0x0048;
    L_0x009f:
        r0 = new kotlin.TypeCastException;
        r3 = "null cannot be cast to non-null type kotlin.Array<kotlin.Any?>";
        r0.<init>(r3);
        throw r0;
    L_0x00a7:
        r0 = new java.lang.NullPointerException;
        r0.<init>();
        r0 = (java.lang.Throwable) r0;
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.jvm.internal.CollectionToArray.toArray(java.util.Collection, java.lang.Object[]):java.lang.Object[]");
    }

    @NotNull
    @JvmName(name = "toArray")
    public static final Object[] toArray(@NotNull Collection<?> collection) {
        Intrinsics.checkParameterIsNotNull(collection, "collection");
        int $i$f$toArrayImpl = 0;
        int size$iv = collection.size();
        if (size$iv == 0) {
            return EMPTY;
        }
        Iterator iter$iv = collection.iterator();
        if (!iter$iv.hasNext()) {
            return EMPTY;
        }
        int $i$a$2$toArrayImpl = 0;
        Object[] result$iv = new Object[size$iv];
        $i$a$2$toArrayImpl = 0;
        while (true) {
            int i$iv = $i$a$2$toArrayImpl + 1;
            result$iv[$i$a$2$toArrayImpl] = iter$iv.next();
            if (i$iv >= result$iv.length) {
                if (!iter$iv.hasNext()) {
                    return result$iv;
                }
                $i$a$2$toArrayImpl = ((i$iv * 3) + 1) >>> 1;
                if ($i$a$2$toArrayImpl <= i$iv) {
                    if (i$iv < MAX_SIZE) {
                        $i$a$2$toArrayImpl = MAX_SIZE;
                    } else {
                        throw ((Throwable) new OutOfMemoryError());
                    }
                }
                Object copyOf = Arrays.copyOf(result$iv, $i$a$2$toArrayImpl);
                Intrinsics.checkExpressionValueIsNotNull(copyOf, "Arrays.copyOf(result, newSize)");
                result$iv = copyOf;
            } else if (!iter$iv.hasNext()) {
                Object copyOf2 = Arrays.copyOf(result$iv, i$iv);
                Intrinsics.checkExpressionValueIsNotNull(copyOf2, "Arrays.copyOf(result, size)");
                return copyOf2;
            }
            $i$a$2$toArrayImpl = i$iv;
        }
    }

    private static final Object[] toArrayImpl(Collection<?> collection, Function0<Object[]> empty, Function1<? super Integer, Object[]> alloc, Function2<? super Object[], ? super Integer, Object[]> trim) {
        int size = collection.size();
        if (size == 0) {
            return (Object[]) empty.invoke();
        }
        Iterator iter = collection.iterator();
        if (!iter.hasNext()) {
            return (Object[]) empty.invoke();
        }
        Object[] result = (Object[]) alloc.invoke(Integer.valueOf(size));
        int i = 0;
        while (true) {
            int i2 = i + 1;
            result[i] = iter.next();
            if (i2 >= result.length) {
                if (!iter.hasNext()) {
                    return result;
                }
                i = ((i2 * 3) + 1) >>> 1;
                if (i <= i2) {
                    if (i2 < MAX_SIZE) {
                        i = MAX_SIZE;
                    } else {
                        throw new OutOfMemoryError();
                    }
                }
                Object copyOf = Arrays.copyOf(result, i);
                Intrinsics.checkExpressionValueIsNotNull(copyOf, "Arrays.copyOf(result, newSize)");
                result = copyOf;
            } else if (!iter.hasNext()) {
                return (Object[]) trim.invoke(result, Integer.valueOf(i2));
            }
            i = i2;
        }
    }
}
