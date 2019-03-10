package de.danoeh.antennapod.core.storage;

import de.danoeh.antennapod.core.storage.DBReader.StatisticsItem;
import java.util.Comparator;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DBReader$J14FiokVfxZ2H5XUZEtHQOEEq_0 implements Comparator {
    public static final /* synthetic */ -$$Lambda$DBReader$J14FiokVfxZ2H5XUZEtHQOEEq_0 INSTANCE = new -$$Lambda$DBReader$J14FiokVfxZ2H5XUZEtHQOEEq_0();

    private /* synthetic */ -$$Lambda$DBReader$J14FiokVfxZ2H5XUZEtHQOEEq_0() {
    }

    public final int compare(Object obj, Object obj2) {
        return DBReader.compareLong(((StatisticsItem) obj).timePlayed, ((StatisticsItem) obj2).timePlayed);
    }
}
