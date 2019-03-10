package de.danoeh.antennapod.fragment.gpodnet;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import de.danoeh.antennapod.core.gpoddernet.model.GpodnetPodcast;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PodcastListFragment$MIsAirvxoDgToU-JQHkhpcZ5BTM implements OnItemClickListener {
    private final /* synthetic */ PodcastListFragment f$0;

    public /* synthetic */ -$$Lambda$PodcastListFragment$MIsAirvxoDgToU-JQHkhpcZ5BTM(PodcastListFragment podcastListFragment) {
        this.f$0 = podcastListFragment;
    }

    public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
        this.f$0.onPodcastSelected((GpodnetPodcast) this.f$0.gridView.getAdapter().getItem(i));
    }
}
