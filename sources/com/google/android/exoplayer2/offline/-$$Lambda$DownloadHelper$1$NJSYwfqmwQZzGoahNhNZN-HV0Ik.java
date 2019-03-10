package com.google.android.exoplayer2.offline;

import com.google.android.exoplayer2.offline.DownloadHelper.C06071;
import com.google.android.exoplayer2.offline.DownloadHelper.Callback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DownloadHelper$1$NJSYwfqmwQZzGoahNhNZN-HV0Ik implements Runnable {
    private final /* synthetic */ C06071 f$0;
    private final /* synthetic */ Callback f$1;

    public /* synthetic */ -$$Lambda$DownloadHelper$1$NJSYwfqmwQZzGoahNhNZN-HV0Ik(C06071 c06071, Callback callback) {
        this.f$0 = c06071;
        this.f$1 = callback;
    }

    public final void run() {
        this.f$1.onPrepared(this.f$0.this$0);
    }
}
