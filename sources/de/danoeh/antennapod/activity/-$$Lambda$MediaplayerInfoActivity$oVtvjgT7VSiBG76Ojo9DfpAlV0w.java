package de.danoeh.antennapod.activity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaplayerInfoActivity$oVtvjgT7VSiBG76Ojo9DfpAlV0w implements OnItemLongClickListener {
    private final /* synthetic */ MediaplayerInfoActivity f$0;

    public /* synthetic */ -$$Lambda$MediaplayerInfoActivity$oVtvjgT7VSiBG76Ojo9DfpAlV0w(MediaplayerInfoActivity mediaplayerInfoActivity) {
        this.f$0 = mediaplayerInfoActivity;
    }

    public final boolean onItemLongClick(AdapterView adapterView, View view, int i, long j) {
        return MediaplayerInfoActivity.lambda$setupGUI$1(this.f$0, adapterView, view, i, j);
    }
}
