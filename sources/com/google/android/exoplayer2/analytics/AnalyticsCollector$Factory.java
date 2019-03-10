package com.google.android.exoplayer2.analytics;

import android.support.annotation.Nullable;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.util.Clock;

public class AnalyticsCollector$Factory {
    public AnalyticsCollector createAnalyticsCollector(@Nullable Player player, Clock clock) {
        return new AnalyticsCollector(player, clock);
    }
}
