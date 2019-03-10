package com.google.android.exoplayer2.text.cea;

import java.util.Collections;
import java.util.List;

public final class Cea708InitializationData {
    public final boolean isWideAspectRatio;

    private Cea708InitializationData(List<byte[]> initializationData) {
        boolean z = false;
        if (((byte[]) initializationData.get(0))[0] != (byte) 0) {
            z = true;
        }
        this.isWideAspectRatio = z;
    }

    public static Cea708InitializationData fromData(List<byte[]> initializationData) {
        return new Cea708InitializationData(initializationData);
    }

    public static List<byte[]> buildData(boolean isWideAspectRatio) {
        return Collections.singletonList(new byte[]{(byte) isWideAspectRatio});
    }
}
