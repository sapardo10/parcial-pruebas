package de.danoeh.antennapod.activity;

import de.danoeh.antennapod.core.storage.DBReader.NavDrawerData;
import io.reactivex.functions.Consumer;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MainActivity$3qwr_xgwK_2qcuFFDI6ewwVOWw0 implements Consumer {
    private final /* synthetic */ MainActivity f$0;

    public /* synthetic */ -$$Lambda$MainActivity$3qwr_xgwK_2qcuFFDI6ewwVOWw0(MainActivity mainActivity) {
        this.f$0 = mainActivity;
    }

    public final void accept(Object obj) {
        MainActivity.lambda$loadData$6(this.f$0, (NavDrawerData) obj);
    }
}
