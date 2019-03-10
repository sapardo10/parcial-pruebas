package kotlin.collections;

import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.Sequence;
import org.jetbrains.annotations.NotNull;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000*\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010(\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\u001a\u0018\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0003H\u0000\u001aH\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\b0\u00070\u0006\"\u0004\b\u0000\u0010\b2\f\u0010\t\u001a\b\u0012\u0004\u0012\u0002H\b0\u00062\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00032\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000bH\u0000\u001aD\u0010\r\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\b0\u00070\u000e\"\u0004\b\u0000\u0010\b*\b\u0012\u0004\u0012\u0002H\b0\u000e2\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00032\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000bH\u0000¨\u0006\u000f"}, d2 = {"checkWindowSizeStep", "", "size", "", "step", "windowedIterator", "", "", "T", "iterator", "partialWindows", "", "reuseBuffer", "windowedSequence", "Lkotlin/sequences/Sequence;", "kotlin-stdlib"}, k = 2, mv = {1, 1, 10})
/* compiled from: SlidingWindow.kt */
public final class SlidingWindowKt {
    public static final void checkWindowSizeStep(int size, int step) {
        Object obj = (size <= 0 || step <= 0) ? null : 1;
        if (obj == null) {
            StringBuilder stringBuilder;
            if (size != step) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("Both size ");
                stringBuilder.append(size);
                stringBuilder.append(" and step ");
                stringBuilder.append(step);
                stringBuilder.append(" must be greater than zero.");
                obj = stringBuilder.toString();
            } else {
                stringBuilder = new StringBuilder();
                stringBuilder.append("size ");
                stringBuilder.append(size);
                stringBuilder.append(" must be greater than zero.");
                obj = stringBuilder.toString();
            }
            throw new IllegalArgumentException(obj.toString());
        }
    }

    @NotNull
    public static final <T> Sequence<List<T>> windowedSequence(@NotNull Sequence<? extends T> $receiver, int size, int step, boolean partialWindows, boolean reuseBuffer) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        checkWindowSizeStep(size, step);
        return new SlidingWindowKt$windowedSequence$$inlined$Sequence$1($receiver, size, step, partialWindows, reuseBuffer);
    }

    @NotNull
    public static final <T> Iterator<List<T>> windowedIterator(@NotNull Iterator<? extends T> iterator, int size, int step, boolean partialWindows, boolean reuseBuffer) {
        Intrinsics.checkParameterIsNotNull(iterator, "iterator");
        if (iterator.hasNext()) {
            return SequenceBuilderKt__SequenceBuilderKt.buildIterator(new SlidingWindowKt$windowedIterator$1(step, size, iterator, reuseBuffer, partialWindows, null));
        }
        return EmptyIterator.INSTANCE;
    }
}
