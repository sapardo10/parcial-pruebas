package de.danoeh.antennapod.fragment;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import de.danoeh.antennapod.activity.OpmlImportFromPathActivity;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AddFeedFragment$oryctsE8jlsxjUVjjHFnraJywww implements OnClickListener {
    private final /* synthetic */ AddFeedFragment f$0;

    public /* synthetic */ -$$Lambda$AddFeedFragment$oryctsE8jlsxjUVjjHFnraJywww(AddFeedFragment addFeedFragment) {
        this.f$0 = addFeedFragment;
    }

    public final void onClick(View view) {
        this.f$0.startActivity(new Intent(this.f$0.getActivity(), OpmlImportFromPathActivity.class));
    }
}
