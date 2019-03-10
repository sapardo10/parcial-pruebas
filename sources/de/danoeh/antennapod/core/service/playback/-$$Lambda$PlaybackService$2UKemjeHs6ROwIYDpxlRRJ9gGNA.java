package de.danoeh.antennapod.core.service.playback;

import de.danoeh.antennapod.core.util.playback.Playable;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PlaybackService$2UKemjeHs6ROwIYDpxlRRJ9gGNA implements Runnable {
    private final /* synthetic */ PlaybackService f$0;
    private final /* synthetic */ Playable f$1;

    public /* synthetic */ -$$Lambda$PlaybackService$2UKemjeHs6ROwIYDpxlRRJ9gGNA(PlaybackService playbackService, Playable playable) {
        this.f$0 = playbackService;
        this.f$1 = playable;
    }

    public final void run() {
        PlaybackService.lambda$updateMediaSessionMetadata$0(this.f$0, this.f$1);
    }
}
