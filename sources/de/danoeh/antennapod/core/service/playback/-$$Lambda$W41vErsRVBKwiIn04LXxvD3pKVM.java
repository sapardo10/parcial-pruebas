package de.danoeh.antennapod.core.service.playback;

import de.danoeh.antennapod.core.storage.DBReader;
import java.util.concurrent.Callable;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$W41vErsRVBKwiIn04LXxvD3pKVM implements Callable {
    public static final /* synthetic */ -$$Lambda$W41vErsRVBKwiIn04LXxvD3pKVM INSTANCE = new -$$Lambda$W41vErsRVBKwiIn04LXxvD3pKVM();

    private /* synthetic */ -$$Lambda$W41vErsRVBKwiIn04LXxvD3pKVM() {
    }

    public final Object call() {
        return DBReader.getQueue();
    }
}
