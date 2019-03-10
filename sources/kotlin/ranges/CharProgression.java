package kotlin.ranges;

import kotlin.Metadata;
import kotlin.collections.CharIterator;
import kotlin.internal.ProgressionUtilKt;
import kotlin.jvm.internal.markers.KMappedMarker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(bv = {1, 0, 2}, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u001c\n\u0002\u0010\f\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\t\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0016\u0018\u0000 \u00192\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0001\u0019B\u001f\b\u0000\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0002\u0010\u0007J\u0013\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0012H\u0002J\b\u0010\u0013\u001a\u00020\u0006H\u0016J\b\u0010\u0014\u001a\u00020\u0010H\u0016J\t\u0010\u0015\u001a\u00020\u0016H\u0002J\b\u0010\u0017\u001a\u00020\u0018H\u0016R\u0011\u0010\b\u001a\u00020\u0002¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u000b\u001a\u00020\u0002¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\nR\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000e¨\u0006\u001a"}, d2 = {"Lkotlin/ranges/CharProgression;", "", "", "start", "endInclusive", "step", "", "(CCI)V", "first", "getFirst", "()C", "last", "getLast", "getStep", "()I", "equals", "", "other", "", "hashCode", "isEmpty", "iterator", "Lkotlin/collections/CharIterator;", "toString", "", "Companion", "kotlin-stdlib"}, k = 1, mv = {1, 1, 10})
/* compiled from: Progressions.kt */
public class CharProgression implements Iterable<Character>, KMappedMarker {
    public static final Companion Companion = new Companion();
    private final char first;
    private final char last;
    private final int step;

    @Metadata(bv = {1, 0, 2}, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\f\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u001e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\b\u001a\u00020\t¨\u0006\n"}, d2 = {"Lkotlin/ranges/CharProgression$Companion;", "", "()V", "fromClosedRange", "Lkotlin/ranges/CharProgression;", "rangeStart", "", "rangeEnd", "step", "", "kotlin-stdlib"}, k = 1, mv = {1, 1, 10})
    /* compiled from: Progressions.kt */
    public static final class Companion {
        private Companion() {
        }

        @NotNull
        public final CharProgression fromClosedRange(char rangeStart, char rangeEnd, int step) {
            return new CharProgression(rangeStart, rangeEnd, step);
        }
    }

    public CharProgression(char start, char endInclusive, int step) {
        if (step != 0) {
            this.first = start;
            this.last = (char) ProgressionUtilKt.getProgressionLastElement((int) start, (int) endInclusive, step);
            this.step = step;
            return;
        }
        throw new IllegalArgumentException("Step must be non-zero");
    }

    public final char getFirst() {
        return this.first;
    }

    public final char getLast() {
        return this.last;
    }

    public final int getStep() {
        return this.step;
    }

    @NotNull
    public CharIterator iterator() {
        return new CharProgressionIterator(this.first, this.last, this.step);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isEmpty() {
        /*
        r4 = this;
        r0 = r4.step;
        r1 = 1;
        r2 = 0;
        if (r0 <= 0) goto L_0x000d;
    L_0x0006:
        r0 = r4.first;
        r3 = r4.last;
        if (r0 <= r3) goto L_0x0014;
    L_0x000c:
        goto L_0x0013;
    L_0x000d:
        r0 = r4.first;
        r3 = r4.last;
        if (r0 >= r3) goto L_0x0014;
    L_0x0013:
        goto L_0x0015;
    L_0x0014:
        r1 = 0;
    L_0x0015:
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.ranges.CharProgression.isEmpty():boolean");
    }

    public boolean equals(@Nullable Object other) {
        if (other instanceof CharProgression) {
            if (isEmpty()) {
                if (!((CharProgression) other).isEmpty()) {
                }
                return true;
            }
            if (this.first == ((CharProgression) other).first && this.last == ((CharProgression) other).last && this.step == ((CharProgression) other).step) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        return isEmpty() ? -1 : (((this.first * 31) + this.last) * 31) + this.step;
    }

    @NotNull
    public String toString() {
        StringBuilder stringBuilder;
        int i;
        if (this.step > 0) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(this.first);
            stringBuilder.append("..");
            stringBuilder.append(this.last);
            stringBuilder.append(" step ");
            i = this.step;
        } else {
            stringBuilder = new StringBuilder();
            stringBuilder.append(this.first);
            stringBuilder.append(" downTo ");
            stringBuilder.append(this.last);
            stringBuilder.append(" step ");
            i = -this.step;
        }
        stringBuilder.append(i);
        return stringBuilder.toString();
    }
}
