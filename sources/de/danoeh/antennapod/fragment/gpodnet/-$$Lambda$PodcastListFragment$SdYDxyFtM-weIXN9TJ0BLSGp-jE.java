package de.danoeh.antennapod.fragment.gpodnet;

import android.view.View;
import android.view.View.OnClickListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PodcastListFragment$SdYDxyFtM-weIXN9TJ0BLSGp-jE implements OnClickListener {
    private final /* synthetic */ PodcastListFragment f$0;

    public /* synthetic */ -$$Lambda$PodcastListFragment$SdYDxyFtM-weIXN9TJ0BLSGp-jE(PodcastListFragment podcastListFragment) {
        this.f$0 = podcastListFragment;
    }

    public final void onClick(View view) {
        this.f$0.loadData();
    }
}
