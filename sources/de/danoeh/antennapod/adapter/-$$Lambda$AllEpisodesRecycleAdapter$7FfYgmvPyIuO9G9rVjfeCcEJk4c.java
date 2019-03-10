package de.danoeh.antennapod.adapter;

import de.danoeh.antennapod.adapter.AllEpisodesRecycleAdapter.Holder;
import io.reactivex.functions.Consumer;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AllEpisodesRecycleAdapter$7FfYgmvPyIuO9G9rVjfeCcEJk4c implements Consumer {
    private final /* synthetic */ Holder f$0;

    public /* synthetic */ -$$Lambda$AllEpisodesRecycleAdapter$7FfYgmvPyIuO9G9rVjfeCcEJk4c(Holder holder) {
        this.f$0 = holder;
    }

    public final void accept(Object obj) {
        AllEpisodesRecycleAdapter.lambda$onBindViewHolder$1(this.f$0, (Long) obj);
    }
}
