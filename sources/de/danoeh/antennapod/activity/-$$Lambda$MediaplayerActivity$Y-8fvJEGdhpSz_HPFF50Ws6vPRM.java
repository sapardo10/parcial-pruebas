package de.danoeh.antennapod.activity;

import android.view.View;
import android.view.View.OnLongClickListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaplayerActivity$Y-8fvJEGdhpSz_HPFF50Ws6vPRM implements OnLongClickListener {
    private final /* synthetic */ MediaplayerActivity f$0;

    public /* synthetic */ -$$Lambda$MediaplayerActivity$Y-8fvJEGdhpSz_HPFF50Ws6vPRM(MediaplayerActivity mediaplayerActivity) {
        this.f$0 = mediaplayerActivity;
    }

    public final boolean onLongClick(View view) {
        return MediaplayerActivity.showSkipPreference(this.f$0, MediaplayerActivity$SkipDirection.SKIP_REWIND);
    }
}
