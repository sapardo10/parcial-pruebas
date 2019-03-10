package de.danoeh.antennapod.activity;

import android.view.View;
import android.view.View.OnLongClickListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaplayerActivity$Y9ICVStmskEbYducvpUpeCXP87k implements OnLongClickListener {
    private final /* synthetic */ MediaplayerActivity f$0;

    public /* synthetic */ -$$Lambda$MediaplayerActivity$Y9ICVStmskEbYducvpUpeCXP87k(MediaplayerActivity mediaplayerActivity) {
        this.f$0 = mediaplayerActivity;
    }

    public final boolean onLongClick(View view) {
        return MediaplayerActivity.showSkipPreference(this.f$0, MediaplayerActivity$SkipDirection.SKIP_FORWARD);
    }
}
