package de.danoeh.antennapod.fragment;

import java.util.concurrent.Callable;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$wyqltIxaRvqGRuhOuBjt1rJ0WQ8 implements Callable {
    private final /* synthetic */ AllEpisodesFragment f$0;

    public /* synthetic */ -$$Lambda$wyqltIxaRvqGRuhOuBjt1rJ0WQ8(AllEpisodesFragment allEpisodesFragment) {
        this.f$0 = allEpisodesFragment;
    }

    public final Object call() {
        return this.f$0.loadData();
    }
}
