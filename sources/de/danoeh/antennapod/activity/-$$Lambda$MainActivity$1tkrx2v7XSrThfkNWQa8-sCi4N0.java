package de.danoeh.antennapod.activity;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MainActivity$1tkrx2v7XSrThfkNWQa8-sCi4N0 implements OnBackStackChangedListener {
    private final /* synthetic */ MainActivity f$0;
    private final /* synthetic */ FragmentManager f$1;

    public /* synthetic */ -$$Lambda$MainActivity$1tkrx2v7XSrThfkNWQa8-sCi4N0(MainActivity mainActivity, FragmentManager fragmentManager) {
        this.f$0 = mainActivity;
        this.f$1 = fragmentManager;
    }

    public final void onBackStackChanged() {
        MainActivity.lambda$onCreate$0(this.f$0, this.f$1);
    }
}
