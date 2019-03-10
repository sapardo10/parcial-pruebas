package com.google.android.exoplayer2.util;

import java.util.concurrent.ThreadFactory;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$Util$MRC4FgxCpRGDforKj-F0m_7VaCA implements ThreadFactory {
    private final /* synthetic */ String f$0;

    public /* synthetic */ -$$Lambda$Util$MRC4FgxCpRGDforKj-F0m_7VaCA(String str) {
        this.f$0 = str;
    }

    public final Thread newThread(Runnable runnable) {
        return new Thread(runnable, this.f$0);
    }
}
