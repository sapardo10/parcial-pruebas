package de.danoeh.antennapod.core.storage;

import de.danoeh.antennapod.core.storage.DBReader.StatisticsItem;
import java.util.Comparator;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DBReader$Vw_mNSL9bpyHgXZLORgzDg-B5CE implements Comparator {
    public static final /* synthetic */ -$$Lambda$DBReader$Vw_mNSL9bpyHgXZLORgzDg-B5CE INSTANCE = new -$$Lambda$DBReader$Vw_mNSL9bpyHgXZLORgzDg-B5CE();

    private /* synthetic */ -$$Lambda$DBReader$Vw_mNSL9bpyHgXZLORgzDg-B5CE() {
    }

    public final int compare(Object obj, Object obj2) {
        return DBReader.compareLong(((StatisticsItem) obj).timePlayedCountAll, ((StatisticsItem) obj2).timePlayedCountAll);
    }
}
