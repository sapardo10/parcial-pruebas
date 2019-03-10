package de.danoeh.antennapod.fragment;

import android.view.View;
import android.view.View.OnClickListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ItunesSearchFragment$LnkVLde5MUclLzniKHOZjj99d_o implements OnClickListener {
    private final /* synthetic */ ItunesSearchFragment f$0;

    public /* synthetic */ -$$Lambda$ItunesSearchFragment$LnkVLde5MUclLzniKHOZjj99d_o(ItunesSearchFragment itunesSearchFragment) {
        this.f$0 = itunesSearchFragment;
    }

    public final void onClick(View view) {
        this.f$0.loadToplist();
    }
}
