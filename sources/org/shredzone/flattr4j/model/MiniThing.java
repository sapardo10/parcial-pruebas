package org.shredzone.flattr4j.model;

import de.danoeh.antennapod.activity.PreferenceActivity;
import de.danoeh.antennapod.core.storage.PodDBAdapter;
import org.shredzone.flattr4j.connector.FlattrObject;

public class MiniThing extends Resource implements ThingId {
    private static final long serialVersionUID = 520173054571474737L;

    public MiniThing(FlattrObject data) {
        super(data);
    }

    public String getThingId() {
        return String.valueOf(this.data.getInt("id"));
    }

    public String getResource() {
        return this.data.get(PreferenceActivity.PARAM_RESOURCE);
    }

    public String getLink() {
        return this.data.get(PodDBAdapter.KEY_LINK);
    }

    public int getClicks() {
        return this.data.getInt("flattrs");
    }

    public String getUrl() {
        return this.data.get("url");
    }

    public String getTitle() {
        return this.data.get("title");
    }

    public String getImage() {
        return this.data.has("image") ? this.data.get("image") : "";
    }
}
