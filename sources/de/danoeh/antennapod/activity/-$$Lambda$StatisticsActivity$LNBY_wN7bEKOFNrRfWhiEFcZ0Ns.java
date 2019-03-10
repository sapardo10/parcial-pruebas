package de.danoeh.antennapod.activity;

import de.danoeh.antennapod.core.storage.DBReader.StatisticsData;
import io.reactivex.functions.Consumer;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$StatisticsActivity$LNBY_wN7bEKOFNrRfWhiEFcZ0Ns implements Consumer {
    private final /* synthetic */ StatisticsActivity f$0;

    public /* synthetic */ -$$Lambda$StatisticsActivity$LNBY_wN7bEKOFNrRfWhiEFcZ0Ns(StatisticsActivity statisticsActivity) {
        this.f$0 = statisticsActivity;
    }

    public final void accept(Object obj) {
        StatisticsActivity.lambda$loadStatistics$2(this.f$0, (StatisticsData) obj);
    }
}
