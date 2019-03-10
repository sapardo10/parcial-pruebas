package de.danoeh.antennapod.activity;

import de.danoeh.antennapod.core.storage.DBReader;
import java.util.concurrent.Callable;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$StatisticsActivity$uNQDYDR7ocMOvbQUdDpG1hpoC1o implements Callable {
    private final /* synthetic */ StatisticsActivity f$0;

    public /* synthetic */ -$$Lambda$StatisticsActivity$uNQDYDR7ocMOvbQUdDpG1hpoC1o(StatisticsActivity statisticsActivity) {
        this.f$0 = statisticsActivity;
    }

    public final Object call() {
        return DBReader.getStatistics(this.f$0.countAll);
    }
}
