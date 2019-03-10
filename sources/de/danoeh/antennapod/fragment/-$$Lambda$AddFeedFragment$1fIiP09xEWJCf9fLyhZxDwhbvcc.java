package de.danoeh.antennapod.fragment;

import android.view.View;
import android.view.View.OnClickListener;
import de.danoeh.antennapod.activity.MainActivity;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AddFeedFragment$1fIiP09xEWJCf9fLyhZxDwhbvcc implements OnClickListener {
    private final /* synthetic */ MainActivity f$0;

    public /* synthetic */ -$$Lambda$AddFeedFragment$1fIiP09xEWJCf9fLyhZxDwhbvcc(MainActivity mainActivity) {
        this.f$0 = mainActivity;
    }

    public final void onClick(View view) {
        this.f$0.loadChildFragment(new ItunesSearchFragment());
    }
}
