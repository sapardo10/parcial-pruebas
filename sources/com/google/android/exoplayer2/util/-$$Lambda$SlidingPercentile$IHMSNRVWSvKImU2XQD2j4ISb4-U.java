package com.google.android.exoplayer2.util;

import java.util.Comparator;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SlidingPercentile$IHMSNRVWSvKImU2XQD2j4ISb4-U implements Comparator {
    public static final /* synthetic */ -$$Lambda$SlidingPercentile$IHMSNRVWSvKImU2XQD2j4ISb4-U INSTANCE = new -$$Lambda$SlidingPercentile$IHMSNRVWSvKImU2XQD2j4ISb4-U();

    private /* synthetic */ -$$Lambda$SlidingPercentile$IHMSNRVWSvKImU2XQD2j4ISb4-U() {
    }

    public final int compare(Object obj, Object obj2) {
        return (((Sample) obj).index - ((Sample) obj2).index);
    }
}
