package de.danoeh.antennapod.core.service.playback;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PlaybackService$dlRsv4jogcQmYXeP_hk3FKrHUHs implements OnSharedPreferenceChangeListener {
    private final /* synthetic */ PlaybackService f$0;

    public /* synthetic */ -$$Lambda$PlaybackService$dlRsv4jogcQmYXeP_hk3FKrHUHs(PlaybackService playbackService) {
        this.f$0 = playbackService;
    }

    public final void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
        PlaybackService.lambda$new$1(this.f$0, sharedPreferences, str);
    }
}
