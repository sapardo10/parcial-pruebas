package kotlin.collections;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import kotlin.Metadata;
import kotlin.SinceKotlin;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.functions.Function4;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000@\n\u0000\n\u0002\u0010$\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010%\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\n\u001a\u0001\u0010\u0000\u001a\u000e\u0012\u0004\u0012\u0002H\u0002\u0012\u0004\u0012\u0002H\u00030\u0001\"\u0004\b\u0000\u0010\u0004\"\u0004\b\u0001\u0010\u0002\"\u0004\b\u0002\u0010\u0003*\u000e\u0012\u0004\u0012\u0002H\u0004\u0012\u0004\u0012\u0002H\u00020\u00052b\u0010\u0006\u001a^\u0012\u0013\u0012\u0011H\u0002¢\u0006\f\b\b\u0012\b\b\t\u0012\u0004\b\b(\n\u0012\u0015\u0012\u0013\u0018\u0001H\u0003¢\u0006\f\b\b\u0012\b\b\t\u0012\u0004\b\b(\u000b\u0012\u0013\u0012\u0011H\u0004¢\u0006\f\b\b\u0012\b\b\t\u0012\u0004\b\b(\f\u0012\u0013\u0012\u00110\r¢\u0006\f\b\b\u0012\b\b\t\u0012\u0004\b\b(\u000e\u0012\u0004\u0012\u0002H\u00030\u0007H\b\u001a´\u0001\u0010\u000f\u001a\u0002H\u0010\"\u0004\b\u0000\u0010\u0004\"\u0004\b\u0001\u0010\u0002\"\u0004\b\u0002\u0010\u0003\"\u0016\b\u0003\u0010\u0010*\u0010\u0012\u0006\b\u0000\u0012\u0002H\u0002\u0012\u0004\u0012\u0002H\u00030\u0011*\u000e\u0012\u0004\u0012\u0002H\u0004\u0012\u0004\u0012\u0002H\u00020\u00052\u0006\u0010\u0012\u001a\u0002H\u00102b\u0010\u0006\u001a^\u0012\u0013\u0012\u0011H\u0002¢\u0006\f\b\b\u0012\b\b\t\u0012\u0004\b\b(\n\u0012\u0015\u0012\u0013\u0018\u0001H\u0003¢\u0006\f\b\b\u0012\b\b\t\u0012\u0004\b\b(\u000b\u0012\u0013\u0012\u0011H\u0004¢\u0006\f\b\b\u0012\b\b\t\u0012\u0004\b\b(\f\u0012\u0013\u0012\u00110\r¢\u0006\f\b\b\u0012\b\b\t\u0012\u0004\b\b(\u000e\u0012\u0004\u0012\u0002H\u00030\u0007H\b¢\u0006\u0002\u0010\u0013\u001aI\u0010\u0014\u001a\u0002H\u0010\"\u0004\b\u0000\u0010\u0004\"\u0004\b\u0001\u0010\u0002\"\u0016\b\u0002\u0010\u0010*\u0010\u0012\u0006\b\u0000\u0012\u0002H\u0002\u0012\u0004\u0012\u00020\u00150\u0011*\u000e\u0012\u0004\u0012\u0002H\u0004\u0012\u0004\u0012\u0002H\u00020\u00052\u0006\u0010\u0012\u001a\u0002H\u0010H\u0007¢\u0006\u0002\u0010\u0016\u001a¼\u0001\u0010\u0017\u001a\u000e\u0012\u0004\u0012\u0002H\u0002\u0012\u0004\u0012\u0002H\u00030\u0001\"\u0004\b\u0000\u0010\u0004\"\u0004\b\u0001\u0010\u0002\"\u0004\b\u0002\u0010\u0003*\u000e\u0012\u0004\u0012\u0002H\u0004\u0012\u0004\u0012\u0002H\u00020\u000526\u0010\u0018\u001a2\u0012\u0013\u0012\u0011H\u0002¢\u0006\f\b\b\u0012\b\b\t\u0012\u0004\b\b(\n\u0012\u0013\u0012\u0011H\u0004¢\u0006\f\b\b\u0012\b\b\t\u0012\u0004\b\b(\f\u0012\u0004\u0012\u0002H\u00030\u00192K\u0010\u0006\u001aG\u0012\u0013\u0012\u0011H\u0002¢\u0006\f\b\b\u0012\b\b\t\u0012\u0004\b\b(\n\u0012\u0013\u0012\u0011H\u0003¢\u0006\f\b\b\u0012\b\b\t\u0012\u0004\b\b(\u000b\u0012\u0013\u0012\u0011H\u0004¢\u0006\f\b\b\u0012\b\b\t\u0012\u0004\b\b(\f\u0012\u0004\u0012\u0002H\u00030\u001aH\b\u001a|\u0010\u0017\u001a\u000e\u0012\u0004\u0012\u0002H\u0002\u0012\u0004\u0012\u0002H\u00030\u0001\"\u0004\b\u0000\u0010\u0004\"\u0004\b\u0001\u0010\u0002\"\u0004\b\u0002\u0010\u0003*\u000e\u0012\u0004\u0012\u0002H\u0004\u0012\u0004\u0012\u0002H\u00020\u00052\u0006\u0010\u001b\u001a\u0002H\u000326\u0010\u0006\u001a2\u0012\u0013\u0012\u0011H\u0003¢\u0006\f\b\b\u0012\b\b\t\u0012\u0004\b\b(\u000b\u0012\u0013\u0012\u0011H\u0004¢\u0006\f\b\b\u0012\b\b\t\u0012\u0004\b\b(\f\u0012\u0004\u0012\u0002H\u00030\u0019H\b¢\u0006\u0002\u0010\u001c\u001aÕ\u0001\u0010\u001d\u001a\u0002H\u0010\"\u0004\b\u0000\u0010\u0004\"\u0004\b\u0001\u0010\u0002\"\u0004\b\u0002\u0010\u0003\"\u0016\b\u0003\u0010\u0010*\u0010\u0012\u0006\b\u0000\u0012\u0002H\u0002\u0012\u0004\u0012\u0002H\u00030\u0011*\u000e\u0012\u0004\u0012\u0002H\u0004\u0012\u0004\u0012\u0002H\u00020\u00052\u0006\u0010\u0012\u001a\u0002H\u001026\u0010\u0018\u001a2\u0012\u0013\u0012\u0011H\u0002¢\u0006\f\b\b\u0012\b\b\t\u0012\u0004\b\b(\n\u0012\u0013\u0012\u0011H\u0004¢\u0006\f\b\b\u0012\b\b\t\u0012\u0004\b\b(\f\u0012\u0004\u0012\u0002H\u00030\u00192K\u0010\u0006\u001aG\u0012\u0013\u0012\u0011H\u0002¢\u0006\f\b\b\u0012\b\b\t\u0012\u0004\b\b(\n\u0012\u0013\u0012\u0011H\u0003¢\u0006\f\b\b\u0012\b\b\t\u0012\u0004\b\b(\u000b\u0012\u0013\u0012\u0011H\u0004¢\u0006\f\b\b\u0012\b\b\t\u0012\u0004\b\b(\f\u0012\u0004\u0012\u0002H\u00030\u001aH\b¢\u0006\u0002\u0010\u001e\u001a\u0001\u0010\u001d\u001a\u0002H\u0010\"\u0004\b\u0000\u0010\u0004\"\u0004\b\u0001\u0010\u0002\"\u0004\b\u0002\u0010\u0003\"\u0016\b\u0003\u0010\u0010*\u0010\u0012\u0006\b\u0000\u0012\u0002H\u0002\u0012\u0004\u0012\u0002H\u00030\u0011*\u000e\u0012\u0004\u0012\u0002H\u0004\u0012\u0004\u0012\u0002H\u00020\u00052\u0006\u0010\u0012\u001a\u0002H\u00102\u0006\u0010\u001b\u001a\u0002H\u000326\u0010\u0006\u001a2\u0012\u0013\u0012\u0011H\u0003¢\u0006\f\b\b\u0012\b\b\t\u0012\u0004\b\b(\u000b\u0012\u0013\u0012\u0011H\u0004¢\u0006\f\b\b\u0012\b\b\t\u0012\u0004\b\b(\f\u0012\u0004\u0012\u0002H\u00030\u0019H\b¢\u0006\u0002\u0010\u001f\u001a\u0001\u0010 \u001a\u000e\u0012\u0004\u0012\u0002H\u0002\u0012\u0004\u0012\u0002H!0\u0001\"\u0004\b\u0000\u0010!\"\b\b\u0001\u0010\u0004*\u0002H!\"\u0004\b\u0002\u0010\u0002*\u000e\u0012\u0004\u0012\u0002H\u0004\u0012\u0004\u0012\u0002H\u00020\u00052K\u0010\u0006\u001aG\u0012\u0013\u0012\u0011H\u0002¢\u0006\f\b\b\u0012\b\b\t\u0012\u0004\b\b(\n\u0012\u0013\u0012\u0011H!¢\u0006\f\b\b\u0012\b\b\t\u0012\u0004\b\b(\u000b\u0012\u0013\u0012\u0011H\u0004¢\u0006\f\b\b\u0012\b\b\t\u0012\u0004\b\b(\f\u0012\u0004\u0012\u0002H!0\u001aH\b\u001a¡\u0001\u0010\"\u001a\u0002H\u0010\"\u0004\b\u0000\u0010!\"\b\b\u0001\u0010\u0004*\u0002H!\"\u0004\b\u0002\u0010\u0002\"\u0016\b\u0003\u0010\u0010*\u0010\u0012\u0006\b\u0000\u0012\u0002H\u0002\u0012\u0004\u0012\u0002H!0\u0011*\u000e\u0012\u0004\u0012\u0002H\u0004\u0012\u0004\u0012\u0002H\u00020\u00052\u0006\u0010\u0012\u001a\u0002H\u00102K\u0010\u0006\u001aG\u0012\u0013\u0012\u0011H\u0002¢\u0006\f\b\b\u0012\b\b\t\u0012\u0004\b\b(\n\u0012\u0013\u0012\u0011H!¢\u0006\f\b\b\u0012\b\b\t\u0012\u0004\b\b(\u000b\u0012\u0013\u0012\u0011H\u0004¢\u0006\f\b\b\u0012\b\b\t\u0012\u0004\b\b(\f\u0012\u0004\u0012\u0002H!0\u001aH\b¢\u0006\u0002\u0010#¨\u0006$"}, d2 = {"aggregate", "", "K", "R", "T", "Lkotlin/collections/Grouping;", "operation", "Lkotlin/Function4;", "Lkotlin/ParameterName;", "name", "key", "accumulator", "element", "", "first", "aggregateTo", "M", "", "destination", "(Lkotlin/collections/Grouping;Ljava/util/Map;Lkotlin/jvm/functions/Function4;)Ljava/util/Map;", "eachCountTo", "", "(Lkotlin/collections/Grouping;Ljava/util/Map;)Ljava/util/Map;", "fold", "initialValueSelector", "Lkotlin/Function2;", "Lkotlin/Function3;", "initialValue", "(Lkotlin/collections/Grouping;Ljava/lang/Object;Lkotlin/jvm/functions/Function2;)Ljava/util/Map;", "foldTo", "(Lkotlin/collections/Grouping;Ljava/util/Map;Lkotlin/jvm/functions/Function2;Lkotlin/jvm/functions/Function3;)Ljava/util/Map;", "(Lkotlin/collections/Grouping;Ljava/util/Map;Ljava/lang/Object;Lkotlin/jvm/functions/Function2;)Ljava/util/Map;", "reduce", "S", "reduceTo", "(Lkotlin/collections/Grouping;Ljava/util/Map;Lkotlin/jvm/functions/Function3;)Ljava/util/Map;", "kotlin-stdlib"}, k = 5, mv = {1, 1, 10}, xi = 1, xs = "kotlin/collections/GroupingKt")
/* compiled from: Grouping.kt */
class GroupingKt__GroupingKt extends GroupingKt__GroupingJVMKt {
    @NotNull
    @SinceKotlin(version = "1.1")
    public static final <T, K, R> Map<K, R> aggregate(@NotNull Grouping<T, ? extends K> $receiver, @NotNull Function4<? super K, ? super R, ? super T, ? super Boolean, ? extends R> operation) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(operation, "operation");
        Map destination$iv = new LinkedHashMap();
        Grouping<T, ? extends K> $receiver$iv = $receiver;
        int $i$f$aggregateTo = 0;
        Iterator sourceIterator = $receiver$iv.sourceIterator();
        while (sourceIterator.hasNext()) {
            Object e$iv = sourceIterator.next();
            Object key$iv = $receiver$iv.keyOf(e$iv);
            Object accumulator$iv = destination$iv.get(key$iv);
            boolean z = accumulator$iv == null && !destination$iv.containsKey(key$iv);
            destination$iv.put(key$iv, operation.invoke(key$iv, accumulator$iv, e$iv, Boolean.valueOf(z)));
        }
        return destination$iv;
    }

    @NotNull
    @SinceKotlin(version = "1.1")
    public static final <T, K, R, M extends Map<? super K, R>> M aggregateTo(@NotNull Grouping<T, ? extends K> $receiver, @NotNull M destination, @NotNull Function4<? super K, ? super R, ? super T, ? super Boolean, ? extends R> operation) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(destination, "destination");
        Intrinsics.checkParameterIsNotNull(operation, "operation");
        Iterator sourceIterator = $receiver.sourceIterator();
        while (sourceIterator.hasNext()) {
            Object e = sourceIterator.next();
            Object key = $receiver.keyOf(e);
            Object accumulator = destination.get(key);
            boolean z = accumulator == null && !destination.containsKey(key);
            destination.put(key, operation.invoke(key, accumulator, e, Boolean.valueOf(z)));
        }
        return destination;
    }

    @NotNull
    @SinceKotlin(version = "1.1")
    public static final <T, K, R> Map<K, R> fold(@NotNull Grouping<T, ? extends K> $receiver, @NotNull Function2<? super K, ? super T, ? extends R> initialValueSelector, @NotNull Function3<? super K, ? super R, ? super T, ? extends R> operation) {
        Function2 function2 = initialValueSelector;
        Function3<? super K, ? super R, ? super T, ? extends R> function3 = operation;
        int $i$f$fold = 0;
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(function2, "initialValueSelector");
        Intrinsics.checkParameterIsNotNull(function3, "operation");
        int $i$f$aggregate = 0;
        Map destination$iv$iv = new LinkedHashMap();
        Grouping<T, ? extends K> $receiver$iv$iv = $receiver;
        int $i$f$aggregateTo = 0;
        Iterator sourceIterator = $receiver$iv$iv.sourceIterator();
        while (sourceIterator.hasNext()) {
            Object key;
            int $i$f$fold2;
            Object invoke;
            Object e$iv$iv = sourceIterator.next();
            Object key$iv$iv = $receiver$iv$iv.keyOf(e$iv$iv);
            Object accumulator$iv$iv = destination$iv$iv.get(key$iv$iv);
            boolean first = accumulator$iv$iv == null && !destination$iv$iv.containsKey(key$iv$iv);
            Object key2 = key$iv$iv;
            Object acc = accumulator$iv$iv;
            Object e = e$iv$iv;
            if (first) {
                key = key2;
                $i$f$fold2 = $i$f$fold;
                $i$f$fold = e;
                invoke = function2.invoke(key, $i$f$fold);
            } else {
                key = key2;
                $i$f$fold2 = $i$f$fold;
                $i$f$fold = e;
                invoke = acc;
            }
            destination$iv$iv.put(key$iv$iv, function3.invoke(key, invoke, $i$f$fold));
            $i$f$fold = $i$f$fold2;
            Function2<? super K, ? super T, ? extends R> function22 = initialValueSelector;
        }
        return destination$iv$iv;
    }

    @NotNull
    @SinceKotlin(version = "1.1")
    public static final <T, K, R, M extends Map<? super K, R>> M foldTo(@NotNull Grouping<T, ? extends K> $receiver, @NotNull M destination, @NotNull Function2<? super K, ? super T, ? extends R> initialValueSelector, @NotNull Function3<? super K, ? super R, ? super T, ? extends R> operation) {
        M m = destination;
        Function2 function2 = initialValueSelector;
        Function3<? super K, ? super R, ? super T, ? extends R> function3 = operation;
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(m, "destination");
        Intrinsics.checkParameterIsNotNull(function2, "initialValueSelector");
        Intrinsics.checkParameterIsNotNull(function3, "operation");
        Grouping<T, ? extends K> $receiver$iv = $receiver;
        int $i$f$aggregateTo = 0;
        Iterator sourceIterator = $receiver$iv.sourceIterator();
        while (sourceIterator.hasNext()) {
            Object e;
            Object invoke;
            Object e$iv = sourceIterator.next();
            Object key$iv = $receiver$iv.keyOf(e$iv);
            Object accumulator$iv = m.get(key$iv);
            boolean first = accumulator$iv == null && !m.containsKey(key$iv);
            Object key = key$iv;
            Object acc = accumulator$iv;
            Object e2 = e$iv;
            if (first) {
                e = e2;
                invoke = function2.invoke(key, e);
            } else {
                e = e2;
                invoke = acc;
            }
            m.put(key$iv, function3.invoke(key, invoke, e));
            Function2<? super K, ? super T, ? extends R> function22 = initialValueSelector;
        }
        return m;
    }

    @NotNull
    @SinceKotlin(version = "1.1")
    public static final <T, K, R> Map<K, R> fold(@NotNull Grouping<T, ? extends K> $receiver, R initialValue, @NotNull Function2<? super R, ? super T, ? extends R> operation) {
        Function2<? super R, ? super T, ? extends R> function2 = operation;
        int $i$f$fold = 0;
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(function2, "operation");
        int $i$f$aggregate = 0;
        Map destination$iv$iv = new LinkedHashMap();
        Grouping<T, ? extends K> $receiver$iv$iv = $receiver;
        int $i$f$aggregateTo = 0;
        Iterator sourceIterator = $receiver$iv$iv.sourceIterator();
        while (sourceIterator.hasNext()) {
            Object e$iv$iv = sourceIterator.next();
            Object key$iv$iv = $receiver$iv$iv.keyOf(e$iv$iv);
            Object accumulator$iv$iv = destination$iv$iv.get(key$iv$iv);
            boolean first = accumulator$iv$iv == null && !destination$iv$iv.containsKey(key$iv$iv);
            Object $noName_0 = key$iv$iv;
            int $i$f$fold2 = $i$f$fold;
            destination$iv$iv.put(key$iv$iv, function2.invoke(first ? initialValue : accumulator$iv$iv, e$iv$iv));
            $i$f$fold = $i$f$fold2;
        }
        return destination$iv$iv;
    }

    @NotNull
    @SinceKotlin(version = "1.1")
    public static final <T, K, R, M extends Map<? super K, R>> M foldTo(@NotNull Grouping<T, ? extends K> $receiver, @NotNull M destination, R initialValue, @NotNull Function2<? super R, ? super T, ? extends R> operation) {
        M m = destination;
        Function2<? super R, ? super T, ? extends R> function2 = operation;
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(m, "destination");
        Intrinsics.checkParameterIsNotNull(function2, "operation");
        Grouping<T, ? extends K> $receiver$iv = $receiver;
        int $i$f$aggregateTo = 0;
        Iterator sourceIterator = $receiver$iv.sourceIterator();
        while (sourceIterator.hasNext()) {
            Object e$iv = sourceIterator.next();
            Object key$iv = $receiver$iv.keyOf(e$iv);
            Object accumulator$iv = m.get(key$iv);
            boolean first = accumulator$iv == null && !m.containsKey(key$iv);
            Object $noName_0 = key$iv;
            m.put(key$iv, function2.invoke(first ? initialValue : accumulator$iv, e$iv));
        }
        return m;
    }

    @NotNull
    @SinceKotlin(version = "1.1")
    public static final <S, T extends S, K> Map<K, S> reduce(@NotNull Grouping<T, ? extends K> $receiver, @NotNull Function3<? super K, ? super S, ? super T, ? extends S> operation) {
        Function3<? super K, ? super S, ? super T, ? extends S> function3 = operation;
        int $i$f$reduce = 0;
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(function3, "operation");
        int $i$f$aggregate = 0;
        Map destination$iv$iv = new LinkedHashMap();
        Grouping<T, ? extends K> $receiver$iv$iv = $receiver;
        int $i$f$aggregateTo = 0;
        Iterator sourceIterator = $receiver$iv$iv.sourceIterator();
        while (sourceIterator.hasNext()) {
            int $i$f$reduce2;
            Object e$iv$iv = sourceIterator.next();
            Object key$iv$iv = $receiver$iv$iv.keyOf(e$iv$iv);
            Object accumulator$iv$iv = destination$iv$iv.get(key$iv$iv);
            boolean first = accumulator$iv$iv == null && !destination$iv$iv.containsKey(key$iv$iv);
            Object key = key$iv$iv;
            Object acc = accumulator$iv$iv;
            Object e = e$iv$iv;
            if (first) {
                $i$f$reduce2 = $i$f$reduce;
                $i$f$reduce = e;
            } else {
                Object acc2 = acc;
                $i$f$reduce2 = $i$f$reduce;
                $i$f$reduce = function3.invoke(key, acc2, e);
            }
            destination$iv$iv.put(key$iv$iv, $i$f$reduce);
            $i$f$reduce = $i$f$reduce2;
        }
        return destination$iv$iv;
    }

    @NotNull
    @SinceKotlin(version = "1.1")
    public static final <S, T extends S, K, M extends Map<? super K, S>> M reduceTo(@NotNull Grouping<T, ? extends K> $receiver, @NotNull M destination, @NotNull Function3<? super K, ? super S, ? super T, ? extends S> operation) {
        M m = destination;
        Function3<? super K, ? super S, ? super T, ? extends S> function3 = operation;
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(m, "destination");
        Intrinsics.checkParameterIsNotNull(function3, "operation");
        Grouping<T, ? extends K> $receiver$iv = $receiver;
        int $i$f$aggregateTo = 0;
        Iterator sourceIterator = $receiver$iv.sourceIterator();
        while (sourceIterator.hasNext()) {
            Object e$iv = sourceIterator.next();
            Object key$iv = $receiver$iv.keyOf(e$iv);
            Object accumulator$iv = m.get(key$iv);
            boolean first = accumulator$iv == null && !m.containsKey(key$iv);
            Object key = key$iv;
            Object acc = accumulator$iv;
            Object e = e$iv;
            if (!first) {
                e = function3.invoke(key, acc, e);
            }
            m.put(key$iv, e);
        }
        return m;
    }

    @NotNull
    @SinceKotlin(version = "1.1")
    public static final <T, K, M extends Map<? super K, Integer>> M eachCountTo(@NotNull Grouping<T, ? extends K> $receiver, @NotNull M destination) {
        M m = destination;
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(m, "destination");
        Object initialValue$iv = Integer.valueOf(0);
        int $i$f$foldTo = 0;
        Grouping<T, ? extends K> $receiver$iv$iv = $receiver;
        int $i$f$aggregateTo = 0;
        Iterator sourceIterator = $receiver$iv$iv.sourceIterator();
        while (sourceIterator.hasNext()) {
            Object e$iv$iv = sourceIterator.next();
            Object key$iv$iv = $receiver$iv$iv.keyOf(e$iv$iv);
            Object accumulator$iv$iv = m.get(key$iv$iv);
            boolean first$iv = accumulator$iv$iv == null && !m.containsKey(key$iv$iv);
            Object $noName_0$iv = key$iv$iv;
            Object $noName_1 = e$iv$iv;
            m.put(key$iv$iv, Integer.valueOf(((Number) (first$iv ? initialValue$iv : accumulator$iv$iv)).intValue() + 1));
        }
        return m;
    }
}
