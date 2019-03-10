package de.danoeh.antennapod.activity;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$OnlineFeedViewActivity$D9hz9flWjFYkCvkvXjYzcUiMIJM implements OnCancelListener {
    private final /* synthetic */ OnlineFeedViewActivity f$0;

    public /* synthetic */ -$$Lambda$OnlineFeedViewActivity$D9hz9flWjFYkCvkvXjYzcUiMIJM(OnlineFeedViewActivity onlineFeedViewActivity) {
        this.f$0 = onlineFeedViewActivity;
    }

    public final void onCancel(DialogInterface dialogInterface) {
        this.f$0.finish();
    }
}
