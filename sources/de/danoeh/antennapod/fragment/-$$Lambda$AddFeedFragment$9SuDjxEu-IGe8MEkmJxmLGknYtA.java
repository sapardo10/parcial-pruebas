package de.danoeh.antennapod.fragment;

import android.view.View;
import android.view.View.OnClickListener;
import de.danoeh.antennapod.activity.MainActivity;
import de.danoeh.antennapod.fragment.gpodnet.GpodnetMainFragment;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AddFeedFragment$9SuDjxEu-IGe8MEkmJxmLGknYtA implements OnClickListener {
    private final /* synthetic */ MainActivity f$0;

    public /* synthetic */ -$$Lambda$AddFeedFragment$9SuDjxEu-IGe8MEkmJxmLGknYtA(MainActivity mainActivity) {
        this.f$0 = mainActivity;
    }

    public final void onClick(View view) {
        this.f$0.loadChildFragment(new GpodnetMainFragment());
    }
}
