package de.danoeh.antennapod.fragment;

import de.danoeh.antennapod.core.storage.DBReader;
import java.util.concurrent.Callable;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ERRoheq1FpvAoMtPU72LGBndRfQ implements Callable {
    public static final /* synthetic */ -$$Lambda$ERRoheq1FpvAoMtPU72LGBndRfQ INSTANCE = new -$$Lambda$ERRoheq1FpvAoMtPU72LGBndRfQ();

    private /* synthetic */ -$$Lambda$ERRoheq1FpvAoMtPU72LGBndRfQ() {
    }

    public final Object call() {
        return DBReader.getNavDrawerData();
    }
}
