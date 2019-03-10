package de.danoeh.antennapod.fragment;

import android.view.View;
import android.view.View.OnClickListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ItunesSearchFragment$9o5JrccVgCH8fsy4dZ24gApGXf0 implements OnClickListener {
    private final /* synthetic */ ItunesSearchFragment f$0;
    private final /* synthetic */ String f$1;

    public /* synthetic */ -$$Lambda$ItunesSearchFragment$9o5JrccVgCH8fsy4dZ24gApGXf0(ItunesSearchFragment itunesSearchFragment, String str) {
        this.f$0 = itunesSearchFragment;
        this.f$1 = str;
    }

    public final void onClick(View view) {
        this.f$0.search(this.f$1);
    }
}
