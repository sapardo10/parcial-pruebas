package de.danoeh.antennapod.core.storage;

import java.util.concurrent.ThreadFactory;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DBWriter$bwbmS3vkom7xpjU6j_enwe0fZDg implements ThreadFactory {
    public static final /* synthetic */ -$$Lambda$DBWriter$bwbmS3vkom7xpjU6j_enwe0fZDg INSTANCE = new -$$Lambda$DBWriter$bwbmS3vkom7xpjU6j_enwe0fZDg();

    private /* synthetic */ -$$Lambda$DBWriter$bwbmS3vkom7xpjU6j_enwe0fZDg() {
    }

    public final Thread newThread(Runnable runnable) {
        return DBWriter.lambda$static$0(runnable);
    }
}
