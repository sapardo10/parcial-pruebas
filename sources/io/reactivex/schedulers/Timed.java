package io.reactivex.schedulers;

import io.reactivex.annotations.NonNull;
import io.reactivex.internal.functions.ObjectHelper;
import java.util.concurrent.TimeUnit;

public final class Timed<T> {
    final long time;
    final TimeUnit unit;
    final T value;

    public Timed(@NonNull T value, long time, @NonNull TimeUnit unit) {
        this.value = value;
        this.time = time;
        this.unit = (TimeUnit) ObjectHelper.requireNonNull((Object) unit, "unit is null");
    }

    @NonNull
    public T value() {
        return this.value;
    }

    @NonNull
    public TimeUnit unit() {
        return this.unit;
    }

    public long time() {
        return this.time;
    }

    public long time(@NonNull TimeUnit unit) {
        return unit.convert(this.time, this.unit);
    }

    public boolean equals(Object other) {
        boolean z = false;
        if (!(other instanceof Timed)) {
            return false;
        }
        Timed<?> o = (Timed) other;
        if (ObjectHelper.equals(this.value, o.value) && this.time == o.time) {
            if (ObjectHelper.equals(this.unit, o.unit)) {
                z = true;
                return z;
            }
        }
        return z;
    }

    public int hashCode() {
        Object obj = this.value;
        int h = (obj != null ? obj.hashCode() : 0) * 31;
        long j = this.time;
        return ((h + ((int) (j ^ (j >>> 31)))) * 31) + this.unit.hashCode();
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Timed[time=");
        stringBuilder.append(this.time);
        stringBuilder.append(", unit=");
        stringBuilder.append(this.unit);
        stringBuilder.append(", value=");
        stringBuilder.append(this.value);
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}
