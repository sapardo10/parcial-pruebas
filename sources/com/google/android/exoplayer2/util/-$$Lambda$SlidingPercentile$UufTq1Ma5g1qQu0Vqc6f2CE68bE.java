package com.google.android.exoplayer2.util;

import java.util.Comparator;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SlidingPercentile$UufTq1Ma5g1qQu0Vqc6f2CE68bE implements Comparator {
    public static final /* synthetic */ -$$Lambda$SlidingPercentile$UufTq1Ma5g1qQu0Vqc6f2CE68bE INSTANCE = new -$$Lambda$SlidingPercentile$UufTq1Ma5g1qQu0Vqc6f2CE68bE();

    private /* synthetic */ -$$Lambda$SlidingPercentile$UufTq1Ma5g1qQu0Vqc6f2CE68bE() {
    }

    public final int compare(Object obj, Object obj2) {
        return Float.compare(((Sample) obj).value, ((Sample) obj2).value);
    }
}
