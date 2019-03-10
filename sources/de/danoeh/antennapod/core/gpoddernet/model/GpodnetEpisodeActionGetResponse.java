package de.danoeh.antennapod.core.gpoddernet.model;

import android.support.annotation.NonNull;
import java.util.List;

public class GpodnetEpisodeActionGetResponse {
    private final List<GpodnetEpisodeAction> episodeActions;
    private final long timestamp;

    public GpodnetEpisodeActionGetResponse(@NonNull List<GpodnetEpisodeAction> episodeActions, long timestamp) {
        this.episodeActions = episodeActions;
        this.timestamp = timestamp;
    }

    public List<GpodnetEpisodeAction> getEpisodeActions() {
        return this.episodeActions;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("GpodnetEpisodeActionGetResponse{episodeActions=");
        stringBuilder.append(this.episodeActions);
        stringBuilder.append(", timestamp=");
        stringBuilder.append(this.timestamp);
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
