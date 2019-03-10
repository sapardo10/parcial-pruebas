package com.google.android.exoplayer2.offline;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DownloadManager$Task$BscZ_DsnJwLao_N7rZjz7bnzplk implements Runnable {
    private final /* synthetic */ Task f$0;

    public /* synthetic */ -$$Lambda$DownloadManager$Task$BscZ_DsnJwLao_N7rZjz7bnzplk(Task task) {
        this.f$0 = task;
    }

    public final void run() {
        this.f$0.changeStateAndNotify(5, 3);
    }
}
