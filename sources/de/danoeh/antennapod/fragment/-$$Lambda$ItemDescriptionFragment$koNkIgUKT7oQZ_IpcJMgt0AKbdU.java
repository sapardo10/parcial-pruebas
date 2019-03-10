package de.danoeh.antennapod.fragment;

import de.danoeh.antennapod.core.feed.FeedItem;
import io.reactivex.functions.Consumer;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ItemDescriptionFragment$koNkIgUKT7oQZ_IpcJMgt0AKbdU implements Consumer {
    private final /* synthetic */ ItemDescriptionFragment f$0;

    public /* synthetic */ -$$Lambda$ItemDescriptionFragment$koNkIgUKT7oQZ_IpcJMgt0AKbdU(ItemDescriptionFragment itemDescriptionFragment) {
        this.f$0 = itemDescriptionFragment;
    }

    public final void accept(Object obj) {
        ItemDescriptionFragment.lambda$onViewCreated$1(this.f$0, (FeedItem) obj);
    }
}
