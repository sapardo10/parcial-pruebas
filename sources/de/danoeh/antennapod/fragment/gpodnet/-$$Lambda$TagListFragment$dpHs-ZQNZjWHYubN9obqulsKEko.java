package de.danoeh.antennapod.fragment.gpodnet;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import de.danoeh.antennapod.activity.MainActivity;
import de.danoeh.antennapod.core.gpoddernet.model.GpodnetTag;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TagListFragment$dpHs-ZQNZjWHYubN9obqulsKEko implements OnItemClickListener {
    private final /* synthetic */ TagListFragment f$0;

    public /* synthetic */ -$$Lambda$TagListFragment$dpHs-ZQNZjWHYubN9obqulsKEko(TagListFragment tagListFragment) {
        this.f$0 = tagListFragment;
    }

    public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
        ((MainActivity) this.f$0.getActivity()).loadChildFragment(TagFragment.newInstance((GpodnetTag) this.f$0.getListAdapter().getItem(i)));
    }
}
