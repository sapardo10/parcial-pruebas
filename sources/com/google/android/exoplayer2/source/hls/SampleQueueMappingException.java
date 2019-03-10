package com.google.android.exoplayer2.source.hls;

import android.support.annotation.Nullable;
import java.io.IOException;

public final class SampleQueueMappingException extends IOException {
    public SampleQueueMappingException(@Nullable String mimeType) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Unable to bind a sample queue to TrackGroup with mime type ");
        stringBuilder.append(mimeType);
        stringBuilder.append(".");
        super(stringBuilder.toString());
    }
}
