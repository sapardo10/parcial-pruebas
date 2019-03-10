package de.danoeh.antennapod.dialog;

import android.app.AlertDialog.Builder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import de.danoeh.antennapod.debug.R;

/* compiled from: lambda */
/* renamed from: de.danoeh.antennapod.dialog.-$$Lambda$EpisodesApplyActionFragment$v6pvyZPA2kwK6oFw2MA3SKDypgY */
public final /* synthetic */ class C0778xe7a35401 implements OnItemLongClickListener {
    private final /* synthetic */ EpisodesApplyActionFragment f$0;

    public /* synthetic */ C0778xe7a35401(EpisodesApplyActionFragment episodesApplyActionFragment) {
        this.f$0 = episodesApplyActionFragment;
    }

    public final boolean onItemLongClick(AdapterView adapterView, View view, int i, long j) {
        return new Builder(this.f$0.getActivity()).setItems(R.array.batch_long_press_options, new C0771xadc0103e(this.f$0, i)).show();
    }
}
