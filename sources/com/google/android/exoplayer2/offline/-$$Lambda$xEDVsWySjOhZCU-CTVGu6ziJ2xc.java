package com.google.android.exoplayer2.offline;

import android.os.ConditionVariable;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$xEDVsWySjOhZCU-CTVGu6ziJ2xc implements Runnable {
    private final /* synthetic */ ConditionVariable f$0;

    public /* synthetic */ -$$Lambda$xEDVsWySjOhZCU-CTVGu6ziJ2xc(ConditionVariable conditionVariable) {
        this.f$0 = conditionVariable;
    }

    public final void run() {
        this.f$0.open();
    }
}
