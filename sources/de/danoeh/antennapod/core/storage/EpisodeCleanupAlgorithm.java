package de.danoeh.antennapod.core.storage;

import android.content.Context;
import de.danoeh.antennapod.core.preferences.UserPreferences;

public abstract class EpisodeCleanupAlgorithm {
    protected abstract int getDefaultCleanupParameter();

    public abstract int getReclaimableItems();

    protected abstract int performCleanup(Context context, int i);

    public int performCleanup(Context context) {
        return performCleanup(context, getDefaultCleanupParameter());
    }

    public int makeRoomForEpisodes(Context context, int amountOfRoomNeeded) {
        return performCleanup(context, getNumEpisodesToCleanup(amountOfRoomNeeded));
    }

    int getNumEpisodesToCleanup(int amountOfRoomNeeded) {
        if (amountOfRoomNeeded >= 0) {
            if (UserPreferences.getEpisodeCacheSize() != UserPreferences.getEpisodeCacheSizeUnlimited()) {
                int downloadedEpisodes = DBReader.getNumberOfDownloadedEpisodes();
                if (downloadedEpisodes + amountOfRoomNeeded >= UserPreferences.getEpisodeCacheSize()) {
                    return (downloadedEpisodes + amountOfRoomNeeded) - UserPreferences.getEpisodeCacheSize();
                }
            }
        }
        return 0;
    }
}
