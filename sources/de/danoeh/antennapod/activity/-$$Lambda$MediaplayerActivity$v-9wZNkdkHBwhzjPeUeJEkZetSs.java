package de.danoeh.antennapod.activity;

import android.view.View;
import android.view.View.OnClickListener;
import de.danoeh.antennapod.core.service.playback.PlaybackService;
import de.danoeh.antennapod.core.util.IntentUtils;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaplayerActivity$v-9wZNkdkHBwhzjPeUeJEkZetSs implements OnClickListener {
    private final /* synthetic */ MediaplayerActivity f$0;

    public /* synthetic */ -$$Lambda$MediaplayerActivity$v-9wZNkdkHBwhzjPeUeJEkZetSs(MediaplayerActivity mediaplayerActivity) {
        this.f$0 = mediaplayerActivity;
    }

    public final void onClick(View view) {
        IntentUtils.sendLocalBroadcast(this.f$0, PlaybackService.ACTION_SKIP_CURRENT_EPISODE);
    }
}
