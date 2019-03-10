package com.google.android.exoplayer2.upstream;

import android.os.Handler;
import android.support.annotation.Nullable;

public interface BandwidthMeter {
    void addEventListener(Handler handler, BandwidthMeter$EventListener bandwidthMeter$EventListener);

    long getBitrateEstimate();

    @Nullable
    TransferListener getTransferListener();

    void removeEventListener(BandwidthMeter$EventListener bandwidthMeter$EventListener);
}
