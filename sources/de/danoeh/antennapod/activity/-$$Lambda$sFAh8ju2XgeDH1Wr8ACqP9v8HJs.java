package de.danoeh.antennapod.activity;

import de.danoeh.antennapod.core.storage.DBReader;
import java.util.concurrent.Callable;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$sFAh8ju2XgeDH1Wr8ACqP9v8HJs implements Callable {
    public static final /* synthetic */ -$$Lambda$sFAh8ju2XgeDH1Wr8ACqP9v8HJs INSTANCE = new -$$Lambda$sFAh8ju2XgeDH1Wr8ACqP9v8HJs();

    private /* synthetic */ -$$Lambda$sFAh8ju2XgeDH1Wr8ACqP9v8HJs() {
    }

    public final Object call() {
        return DBReader.getFeedList();
    }
}
