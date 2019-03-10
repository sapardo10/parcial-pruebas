package de.danoeh.antennapod.adapter;

import android.view.ContextMenu;
import de.danoeh.antennapod.adapter.QueueRecyclerAdapter.ViewHolder;
import de.danoeh.antennapod.menuhandler.FeedItemMenuHandler.MenuInterface;

/* compiled from: lambda */
/* renamed from: de.danoeh.antennapod.adapter.-$$Lambda$QueueRecyclerAdapter$ViewHolder$t9So763GJNR_gxSoSEKnaRdbpsg */
public final /* synthetic */ class C1026x158e7f10 implements MenuInterface {
    private final /* synthetic */ ContextMenu f$0;

    public /* synthetic */ C1026x158e7f10(ContextMenu contextMenu) {
        this.f$0 = contextMenu;
    }

    public final void setItemVisibility(int i, boolean z) {
        ViewHolder.lambda$onCreateContextMenu$1(this.f$0, i, z);
    }
}
