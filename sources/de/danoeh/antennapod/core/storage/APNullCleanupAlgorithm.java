package de.danoeh.antennapod.core.storage;

import android.content.Context;
import android.util.Log;

public class APNullCleanupAlgorithm extends EpisodeCleanupAlgorithm {
    private static final String TAG = "APNullCleanupAlgorithm";

    public int performCleanup(Context context, int parameter) {
        Log.i(TAG, "performCleanup: Not removing anything");
        return 0;
    }

    public int getDefaultCleanupParameter() {
        return 0;
    }

    public int getReclaimableItems() {
        return 0;
    }
}
