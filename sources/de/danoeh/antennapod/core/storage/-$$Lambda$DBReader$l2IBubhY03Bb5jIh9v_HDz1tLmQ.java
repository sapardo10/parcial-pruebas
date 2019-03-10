package de.danoeh.antennapod.core.storage;

import de.danoeh.antennapod.core.feed.Feed;
import de.danoeh.antennapod.core.util.LongIntMap;
import java.util.Comparator;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DBReader$l2IBubhY03Bb5jIh9v_HDz1tLmQ implements Comparator {
    private final /* synthetic */ LongIntMap f$0;

    public /* synthetic */ -$$Lambda$DBReader$l2IBubhY03Bb5jIh9v_HDz1tLmQ(LongIntMap longIntMap) {
        this.f$0 = longIntMap;
    }

    public final int compare(Object obj, Object obj2) {
        return DBReader.lambda$getNavDrawerData$2(this.f$0, (Feed) obj, (Feed) obj2);
    }
}
