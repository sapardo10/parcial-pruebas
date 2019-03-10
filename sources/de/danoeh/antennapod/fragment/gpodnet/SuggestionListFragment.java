package de.danoeh.antennapod.fragment.gpodnet;

import de.danoeh.antennapod.core.gpoddernet.GpodnetService;
import de.danoeh.antennapod.core.gpoddernet.GpodnetServiceException;
import de.danoeh.antennapod.core.gpoddernet.model.GpodnetPodcast;
import de.danoeh.antennapod.core.preferences.GpodnetPreferences;
import java.util.Collections;
import java.util.List;

public class SuggestionListFragment extends PodcastListFragment {
    private static final int SUGGESTIONS_COUNT = 50;

    protected List<GpodnetPodcast> loadPodcastData(GpodnetService service) throws GpodnetServiceException {
        if (!GpodnetPreferences.loggedIn()) {
            return Collections.emptyList();
        }
        service.authenticate(GpodnetPreferences.getUsername(), GpodnetPreferences.getPassword());
        return service.getSuggestions(50);
    }
}
