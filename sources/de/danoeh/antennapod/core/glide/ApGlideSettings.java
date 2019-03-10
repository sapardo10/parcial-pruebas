package de.danoeh.antennapod.core.glide;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class ApGlideSettings {
    public static final DiskCacheStrategy AP_DISK_CACHE_STRATEGY = DiskCacheStrategy.ALL;

    private ApGlideSettings() {
    }
}
