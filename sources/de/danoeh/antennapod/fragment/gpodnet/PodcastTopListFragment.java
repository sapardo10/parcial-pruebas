package de.danoeh.antennapod.fragment.gpodnet;

import de.danoeh.antennapod.core.gpoddernet.GpodnetService;
import de.danoeh.antennapod.core.gpoddernet.GpodnetServiceException;
import de.danoeh.antennapod.core.gpoddernet.model.GpodnetPodcast;
import java.util.List;

public class PodcastTopListFragment extends PodcastListFragment {
    private static final int PODCAST_COUNT = 50;
    private static final String TAG = "PodcastTopListFragment";

    protected List<GpodnetPodcast> loadPodcastData(GpodnetService service) throws GpodnetServiceException {
        return service.getPodcastToplist(50);
    }
}
