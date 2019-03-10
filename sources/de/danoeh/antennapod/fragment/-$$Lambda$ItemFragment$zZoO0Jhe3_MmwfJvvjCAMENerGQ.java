package de.danoeh.antennapod.fragment;

import java.util.concurrent.Callable;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ItemFragment$zZoO0Jhe3_MmwfJvvjCAMENerGQ implements Callable {
    private final /* synthetic */ ItemFragment f$0;

    public /* synthetic */ -$$Lambda$ItemFragment$zZoO0Jhe3_MmwfJvvjCAMENerGQ(ItemFragment itemFragment) {
        this.f$0 = itemFragment;
    }

    public final Object call() {
        return this.f$0.loadInBackground();
    }
}
