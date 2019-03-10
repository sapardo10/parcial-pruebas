package de.danoeh.antennapod.adapter;

import android.view.ContextMenu;
import de.danoeh.antennapod.adapter.AllEpisodesRecycleAdapter.Holder;
import de.danoeh.antennapod.menuhandler.FeedItemMenuHandler.MenuInterface;

/* compiled from: lambda */
/* renamed from: de.danoeh.antennapod.adapter.-$$Lambda$AllEpisodesRecycleAdapter$Holder$XWS3ERXhFLh6dNPt0POqRrv5sQE */
public final /* synthetic */ class C1021x80b0a1ef implements MenuInterface {
    private final /* synthetic */ ContextMenu f$0;

    public /* synthetic */ C1021x80b0a1ef(ContextMenu contextMenu) {
        this.f$0 = contextMenu;
    }

    public final void setItemVisibility(int i, boolean z) {
        Holder.lambda$onCreateContextMenu$0(this.f$0, i, z);
    }
}
