package de.danoeh.antennapod.core;

import de.danoeh.antennapod.core.storage.AutomaticDownloadAlgorithm;
import de.danoeh.antennapod.core.storage.EpisodeCleanupAlgorithm;

public interface DBTasksCallbacks {
    AutomaticDownloadAlgorithm getAutomaticDownloadAlgorithm();

    EpisodeCleanupAlgorithm getEpisodeCacheCleanupAlgorithm();
}
