package de.danoeh.antennapod.core.gpoddernet.model;

import android.support.annotation.NonNull;
import java.util.List;

public class GpodnetSubscriptionChange {
    private final List<String> added;
    private final List<String> removed;
    private final long timestamp;

    public GpodnetSubscriptionChange(@NonNull List<String> added, @NonNull List<String> removed, long timestamp) {
        this.added = added;
        this.removed = removed;
        this.timestamp = timestamp;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("GpodnetSubscriptionChange [added=");
        stringBuilder.append(this.added.toString());
        stringBuilder.append(", removed=");
        stringBuilder.append(this.removed.toString());
        stringBuilder.append(", timestamp=");
        stringBuilder.append(this.timestamp);
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    public List<String> getAdded() {
        return this.added;
    }

    public List<String> getRemoved() {
        return this.removed;
    }

    public long getTimestamp() {
        return this.timestamp;
    }
}
