package kotlin.ranges;

import kotlin.Metadata;
import kotlin.collections.LongIterator;
import kotlin.internal.ProgressionUtilKt;
import kotlin.jvm.internal.markers.KMappedMarker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(bv = {1, 0, 2}, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u001c\n\u0002\u0010\t\n\u0002\b\u000b\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0016\u0018\u0000 \u00182\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0001\u0018B\u001f\b\u0000\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0002¢\u0006\u0002\u0010\u0006J\u0013\u0010\r\u001a\u00020\u000e2\b\u0010\u000f\u001a\u0004\u0018\u00010\u0010H\u0002J\b\u0010\u0011\u001a\u00020\u0012H\u0016J\b\u0010\u0013\u001a\u00020\u000eH\u0016J\t\u0010\u0014\u001a\u00020\u0015H\u0002J\b\u0010\u0016\u001a\u00020\u0017H\u0016R\u0011\u0010\u0007\u001a\u00020\u0002¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\n\u001a\u00020\u0002¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\tR\u0011\u0010\u0005\u001a\u00020\u0002¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\t¨\u0006\u0019"}, d2 = {"Lkotlin/ranges/LongProgression;", "", "", "start", "endInclusive", "step", "(JJJ)V", "first", "getFirst", "()J", "last", "getLast", "getStep", "equals", "", "other", "", "hashCode", "", "isEmpty", "iterator", "Lkotlin/collections/LongIterator;", "toString", "", "Companion", "kotlin-stdlib"}, k = 1, mv = {1, 1, 10})
/* compiled from: Progressions.kt */
public class LongProgression implements Iterable<Long>, KMappedMarker {
    public static final Companion Companion = new Companion();
    private final long first;
    private final long last;
    private final long step;

    @Metadata(bv = {1, 0, 2}, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u001e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\b\u001a\u00020\u0006¨\u0006\t"}, d2 = {"Lkotlin/ranges/LongProgression$Companion;", "", "()V", "fromClosedRange", "Lkotlin/ranges/LongProgression;", "rangeStart", "", "rangeEnd", "step", "kotlin-stdlib"}, k = 1, mv = {1, 1, 10})
    /* compiled from: Progressions.kt */
    public static final class Companion {
        private Companion() {
        }

        @NotNull
        public final LongProgression fromClosedRange(long rangeStart, long rangeEnd, long step) {
            return new LongProgression(rangeStart, rangeEnd, step);
        }
    }

    public LongProgression(long start, long endInclusive, long step) {
        if (step != 0) {
            this.first = start;
            this.last = ProgressionUtilKt.getProgressionLastElement(start, endInclusive, step);
            this.step = step;
            return;
        }
        throw new IllegalArgumentException("Step must be non-zero");
    }

    public final long getFirst() {
        return this.first;
    }

    public final long getLast() {
        return this.last;
    }

    public final long getStep() {
        return this.step;
    }

    @NotNull
    public LongIterator iterator() {
        return new LongProgressionIterator(this.first, this.last, this.step);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isEmpty() {
        /*
        r7 = this;
        r0 = r7.step;
        r2 = 1;
        r3 = 0;
        r4 = 0;
        r6 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
        if (r6 <= 0) goto L_0x0013;
    L_0x000a:
        r0 = r7.first;
        r4 = r7.last;
        r6 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
        if (r6 <= 0) goto L_0x001c;
    L_0x0012:
        goto L_0x001b;
    L_0x0013:
        r0 = r7.first;
        r4 = r7.last;
        r6 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
        if (r6 >= 0) goto L_0x001c;
    L_0x001b:
        goto L_0x001d;
    L_0x001c:
        r2 = 0;
    L_0x001d:
        return r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.ranges.LongProgression.isEmpty():boolean");
    }

    public boolean equals(@Nullable Object other) {
        if (other instanceof LongProgression) {
            if (isEmpty()) {
                if (!((LongProgression) other).isEmpty()) {
                }
                return true;
            }
            if (this.first == ((LongProgression) other).first && this.last == ((LongProgression) other).last && this.step == ((LongProgression) other).step) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        if (isEmpty()) {
            return -1;
        }
        long j = (long) 31;
        long j2 = this.first;
        j2 = (j2 ^ (j2 >>> 32)) * j;
        long j3 = this.last;
        j *= j2 + (j3 ^ (j3 >>> 32));
        j2 = this.step;
        return (int) (j + (j2 ^ (j2 >>> 32)));
    }

    @NotNull
    public String toString() {
        StringBuilder stringBuilder;
        long j;
        if (this.step > 0) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(this.first);
            stringBuilder.append("..");
            stringBuilder.append(this.last);
            stringBuilder.append(" step ");
            j = this.step;
        } else {
            stringBuilder = new StringBuilder();
            stringBuilder.append(this.first);
            stringBuilder.append(" downTo ");
            stringBuilder.append(this.last);
            stringBuilder.append(" step ");
            j = -this.step;
        }
        stringBuilder.append(j);
        return stringBuilder.toString();
    }
}
