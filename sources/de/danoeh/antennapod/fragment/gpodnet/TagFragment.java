package de.danoeh.antennapod.fragment.gpodnet;

import android.os.Bundle;
import android.support.annotation.Nullable;
import de.danoeh.antennapod.activity.MainActivity;
import de.danoeh.antennapod.core.gpoddernet.GpodnetService;
import de.danoeh.antennapod.core.gpoddernet.GpodnetServiceException;
import de.danoeh.antennapod.core.gpoddernet.model.GpodnetPodcast;
import de.danoeh.antennapod.core.gpoddernet.model.GpodnetTag;
import java.util.List;
import org.apache.commons.lang3.Validate;

public class TagFragment extends PodcastListFragment {
    private static final int PODCAST_COUNT = 50;
    private static final String TAG = "TagFragment";
    private GpodnetTag tag;

    public static TagFragment newInstance(GpodnetTag tag) {
        Validate.notNull(tag);
        TagFragment fragment = new TagFragment();
        Bundle args = new Bundle();
        args.putParcelable("tag", tag);
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        boolean z = (args == null || args.getParcelable("tag") == null) ? false : true;
        Validate.isTrue(z, "args invalid", new Object[0]);
        this.tag = (GpodnetTag) args.getParcelable("tag");
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(this.tag.getTitle());
    }

    protected List<GpodnetPodcast> loadPodcastData(GpodnetService service) throws GpodnetServiceException {
        return service.getPodcastsForTag(this.tag, 50);
    }
}
