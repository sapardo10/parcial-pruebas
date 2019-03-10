package kotlin.collections;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import kotlin.Metadata;
import kotlin.SinceKotlin;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref.IntRef;
import kotlin.jvm.internal.TypeIntrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000&\n\u0000\n\u0002\u0010$\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010%\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010&\n\u0000\u001a0\u0010\u0000\u001a\u000e\u0012\u0004\u0012\u0002H\u0002\u0012\u0004\u0012\u00020\u00030\u0001\"\u0004\b\u0000\u0010\u0004\"\u0004\b\u0001\u0010\u0002*\u000e\u0012\u0004\u0012\u0002H\u0004\u0012\u0004\u0012\u0002H\u00020\u0005H\u0007\u001aW\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u0002H\u0002\u0012\u0004\u0012\u0002H\b0\u0007\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010\t\"\u0004\b\u0002\u0010\b*\u000e\u0012\u0004\u0012\u0002H\u0002\u0012\u0004\u0012\u0002H\t0\u00072\u001e\u0010\n\u001a\u001a\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u0002H\u0002\u0012\u0004\u0012\u0002H\t0\f\u0012\u0004\u0012\u0002H\b0\u000bH\b¨\u0006\r"}, d2 = {"eachCount", "", "K", "", "T", "Lkotlin/collections/Grouping;", "mapValuesInPlace", "", "R", "V", "f", "Lkotlin/Function1;", "", "kotlin-stdlib"}, k = 5, mv = {1, 1, 10}, xi = 1, xs = "kotlin/collections/GroupingKt")
/* compiled from: GroupingJVM.kt */
class GroupingKt__GroupingJVMKt {
    @kotlin.PublishedApi
    @kotlin.internal.InlineOnly
    private static final <K, V, R> java.util.Map<K, R> mapValuesInPlace(@org.jetbrains.annotations.NotNull java.util.Map<K, V> r9, kotlin.jvm.functions.Function1<? super java.util.Map.Entry<? extends K, ? extends V>, ? extends R> r10) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:14:0x0040 in {5, 7, 11, 13} preds:[]
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
        r0 = 0;
        r1 = r9.entrySet();
        r1 = (java.lang.Iterable) r1;
        r2 = 0;
        r3 = r2;
        r4 = r1.iterator();
    L_0x000d:
        r5 = r4.hasNext();
        if (r5 == 0) goto L_0x0030;
    L_0x0013:
        r5 = r4.next();
        r6 = r5;
        r6 = (java.util.Map.Entry) r6;
        if (r6 == 0) goto L_0x0028;
    L_0x001c:
        r7 = kotlin.jvm.internal.TypeIntrinsics.asMutableMapEntry(r6);
        r8 = r10.invoke(r6);
        r7.setValue(r8);
        goto L_0x000d;
    L_0x0028:
        r4 = new kotlin.TypeCastException;
        r7 = "null cannot be cast to non-null type kotlin.collections.MutableMap.MutableEntry<K, R>";
        r4.<init>(r7);
        throw r4;
        if (r9 == 0) goto L_0x0038;
    L_0x0033:
        r1 = kotlin.jvm.internal.TypeIntrinsics.asMutableMap(r9);
        return r1;
    L_0x0038:
        r1 = new kotlin.TypeCastException;
        r2 = "null cannot be cast to non-null type kotlin.collections.MutableMap<K, R>";
        r1.<init>(r2);
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.collections.GroupingKt__GroupingJVMKt.mapValuesInPlace(java.util.Map, kotlin.jvm.functions.Function1):java.util.Map<K, R>");
    }

    @NotNull
    @SinceKotlin(version = "1.1")
    public static final <T, K> Map<K, Integer> eachCount(@NotNull Grouping<T, ? extends K> $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Map linkedHashMap = new LinkedHashMap();
        Grouping $receiver$iv = $receiver;
        int $i$f$foldTo = 0;
        Grouping<T, ? extends K> $receiver$iv$iv = $receiver$iv;
        int $i$f$aggregateTo = 0;
        Iterator sourceIterator = $receiver$iv$iv.sourceIterator();
        while (sourceIterator.hasNext()) {
            Object $noName_1;
            IntRef intRef;
            Object e$iv$iv = sourceIterator.next();
            Object key$iv$iv = $receiver$iv$iv.keyOf(e$iv$iv);
            IntRef accumulator$iv$iv = linkedHashMap.get(key$iv$iv);
            boolean first$iv = accumulator$iv$iv == null && !linkedHashMap.containsKey(key$iv$iv);
            Object key$iv = key$iv$iv;
            IntRef acc$iv = accumulator$iv$iv;
            Object e$iv = e$iv$iv;
            if (first$iv) {
                Object $noName_0 = key$iv;
                $noName_1 = e$iv;
                intRef = new IntRef();
            } else {
                intRef = acc$iv;
            }
            IntRef acc = intRef;
            $noName_1 = key$iv;
            Object $noName_2 = e$iv;
            IntRef $receiver2 = acc;
            Grouping $receiver$iv2 = $receiver$iv;
            $receiver2.element++;
            linkedHashMap.put(key$iv$iv, acc);
            $receiver$iv = $receiver$iv2;
            Grouping<T, ? extends K> grouping = $receiver;
        }
        for (Entry it : linkedHashMap.entrySet()) {
            if (it != null) {
                TypeIntrinsics.asMutableMapEntry(it).setValue(Integer.valueOf(((IntRef) it.getValue()).element));
            } else {
                throw new TypeCastException("null cannot be cast to non-null type kotlin.collections.MutableMap.MutableEntry<K, R>");
            }
        }
        return TypeIntrinsics.asMutableMap(linkedHashMap);
    }
}
