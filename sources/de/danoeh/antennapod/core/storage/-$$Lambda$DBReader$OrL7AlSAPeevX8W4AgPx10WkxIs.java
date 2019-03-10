package de.danoeh.antennapod.core.storage;

import de.danoeh.antennapod.core.feed.Feed;
import de.danoeh.antennapod.core.util.LongIntMap;
import java.util.Comparator;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DBReader$OrL7AlSAPeevX8W4AgPx10WkxIs implements Comparator {
    private final /* synthetic */ LongIntMap f$0;

    public /* synthetic */ -$$Lambda$DBReader$OrL7AlSAPeevX8W4AgPx10WkxIs(LongIntMap longIntMap) {
        this.f$0 = longIntMap;
    }

    public final int compare(Object obj, Object obj2) {
        return DBReader.lambda$getNavDrawerData$4(this.f$0, (Feed) obj, (Feed) obj2);
    }
}
