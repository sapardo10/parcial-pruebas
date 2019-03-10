package com.google.android.exoplayer2.ui;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DefaultTimeBar$Qcgn0kqjCzq5x_ej2phsDpb1YTU implements Runnable {
    private final /* synthetic */ DefaultTimeBar f$0;

    public /* synthetic */ -$$Lambda$DefaultTimeBar$Qcgn0kqjCzq5x_ej2phsDpb1YTU(DefaultTimeBar defaultTimeBar) {
        this.f$0 = defaultTimeBar;
    }

    public final void run() {
        this.f$0.stopScrubbing(false);
    }
}
