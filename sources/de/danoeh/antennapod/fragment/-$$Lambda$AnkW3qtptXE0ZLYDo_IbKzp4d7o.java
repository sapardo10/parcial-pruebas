package de.danoeh.antennapod.fragment;

import de.danoeh.antennapod.core.storage.DBReader;
import java.util.concurrent.Callable;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AnkW3qtptXE0ZLYDo_IbKzp4d7o implements Callable {
    public static final /* synthetic */ -$$Lambda$AnkW3qtptXE0ZLYDo_IbKzp4d7o INSTANCE = new -$$Lambda$AnkW3qtptXE0ZLYDo_IbKzp4d7o();

    private /* synthetic */ -$$Lambda$AnkW3qtptXE0ZLYDo_IbKzp4d7o() {
    }

    public final Object call() {
        return DBReader.getDownloadLog();
    }
}
