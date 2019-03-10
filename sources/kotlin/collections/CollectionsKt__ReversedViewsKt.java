package kotlin.collections;

import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.JvmName;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.IntRange;
import org.jetbrains.annotations.NotNull;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000\u0018\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0010!\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0005\u001a\u001c\u0010\u0000\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u0001\u001a#\u0010\u0000\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0003\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u0003H\u0007¢\u0006\u0002\b\u0004\u001a\u001d\u0010\u0005\u001a\u00020\u0006*\u0006\u0012\u0002\b\u00030\u00012\u0006\u0010\u0007\u001a\u00020\u0006H\u0002¢\u0006\u0002\b\b\u001a\u001d\u0010\t\u001a\u00020\u0006*\u0006\u0012\u0002\b\u00030\u00012\u0006\u0010\u0007\u001a\u00020\u0006H\u0002¢\u0006\u0002\b\n¨\u0006\u000b"}, d2 = {"asReversed", "", "T", "", "asReversedMutable", "reverseElementIndex", "", "index", "reverseElementIndex$CollectionsKt__ReversedViewsKt", "reversePositionIndex", "reversePositionIndex$CollectionsKt__ReversedViewsKt", "kotlin-stdlib"}, k = 5, mv = {1, 1, 10}, xi = 1, xs = "kotlin/collections/CollectionsKt")
/* compiled from: ReversedViews.kt */
class CollectionsKt__ReversedViewsKt extends CollectionsKt__MutableCollectionsKt {
    private static final int reverseElementIndex$CollectionsKt__ReversedViewsKt(@NotNull List<?> $receiver, int index) {
        int lastIndex = CollectionsKt.getLastIndex($receiver);
        if (index >= 0 && lastIndex >= index) {
            return CollectionsKt.getLastIndex($receiver) - index;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Element index ");
        stringBuilder.append(index);
        stringBuilder.append(" must be in range [");
        stringBuilder.append(new IntRange(0, CollectionsKt.getLastIndex($receiver)));
        stringBuilder.append("].");
        throw new IndexOutOfBoundsException(stringBuilder.toString());
    }

    private static final int reversePositionIndex$CollectionsKt__ReversedViewsKt(@NotNull List<?> $receiver, int index) {
        int size = $receiver.size();
        if (index >= 0 && size >= index) {
            return $receiver.size() - index;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Position index ");
        stringBuilder.append(index);
        stringBuilder.append(" must be in range [");
        stringBuilder.append(new IntRange(0, $receiver.size()));
        stringBuilder.append("].");
        throw new IndexOutOfBoundsException(stringBuilder.toString());
    }

    @NotNull
    public static final <T> List<T> asReversed(@NotNull List<? extends T> $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return new ReversedListReadOnly($receiver);
    }

    @NotNull
    @JvmName(name = "asReversedMutable")
    public static final <T> List<T> asReversedMutable(@NotNull List<T> $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return new ReversedList($receiver);
    }
}
