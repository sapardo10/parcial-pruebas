package de.danoeh.antennapod.fragment;

import de.danoeh.antennapod.core.storage.DBReader;
import java.util.concurrent.Callable;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$pkf5HCK45lQGMOIEp0PowCOBrqo implements Callable {
    public static final /* synthetic */ -$$Lambda$pkf5HCK45lQGMOIEp0PowCOBrqo INSTANCE = new -$$Lambda$pkf5HCK45lQGMOIEp0PowCOBrqo();

    private /* synthetic */ -$$Lambda$pkf5HCK45lQGMOIEp0PowCOBrqo() {
    }

    public final Object call() {
        return DBReader.getDownloadedItems();
    }
}
