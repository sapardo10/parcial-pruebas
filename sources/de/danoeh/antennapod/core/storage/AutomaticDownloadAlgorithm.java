package de.danoeh.antennapod.core.storage;

import android.content.Context;

public interface AutomaticDownloadAlgorithm {
    Runnable autoDownloadUndownloadedItems(Context context);
}
