package io.reactivex.internal.util;

import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicLong;

public final class BackpressureHelper {
    private BackpressureHelper() {
        throw new IllegalStateException("No instances!");
    }

    public static long addCap(long a, long b) {
        long u = a + b;
        if (u < 0) {
            return Long.MAX_VALUE;
        }
        return u;
    }

    public static long multiplyCap(long a, long b) {
        long u = a * b;
        if (((a | b) >>> 31) != 0) {
            if (u / a != b) {
                return Long.MAX_VALUE;
            }
        }
        return u;
    }

    public static long add(AtomicLong requested, long n) {
        while (true) {
            long r = requested.get();
            if (r == Long.MAX_VALUE) {
                return Long.MAX_VALUE;
            }
            if (requested.compareAndSet(r, addCap(r, n))) {
                return r;
            }
        }
    }

    public static long addCancel(AtomicLong requested, long n) {
        while (true) {
            long r = requested.get();
            if (r == Long.MIN_VALUE) {
                return Long.MIN_VALUE;
            }
            if (r == Long.MAX_VALUE) {
                return Long.MAX_VALUE;
            }
            if (requested.compareAndSet(r, addCap(r, n))) {
                return r;
            }
        }
    }

    public static long produced(AtomicLong requested, long n) {
        while (true) {
            long current = requested.get();
            if (current == Long.MAX_VALUE) {
                return Long.MAX_VALUE;
            }
            long update = current - n;
            if (update < 0) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("More produced than requested: ");
                stringBuilder.append(update);
                RxJavaPlugins.onError(new IllegalStateException(stringBuilder.toString()));
                update = 0;
            }
            if (requested.compareAndSet(current, update)) {
                return update;
            }
        }
    }

    public static long producedCancel(AtomicLong requested, long n) {
        while (true) {
            long current = requested.get();
            if (current == Long.MIN_VALUE) {
                return Long.MIN_VALUE;
            }
            if (current == Long.MAX_VALUE) {
                return Long.MAX_VALUE;
            }
            long update = current - n;
            if (update < 0) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("More produced than requested: ");
                stringBuilder.append(update);
                RxJavaPlugins.onError(new IllegalStateException(stringBuilder.toString()));
                update = 0;
            }
            if (requested.compareAndSet(current, update)) {
                return update;
            }
        }
    }
}
