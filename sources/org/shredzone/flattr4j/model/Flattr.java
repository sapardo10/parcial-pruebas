package org.shredzone.flattr4j.model;

import de.danoeh.antennapod.core.storage.PodDBAdapter;
import java.util.Date;
import org.shredzone.flattr4j.connector.FlattrObject;

public class Flattr extends Resource implements ThingId, UserId {
    private static final long serialVersionUID = 8013428651001009374L;
    private transient Thing thing = null;
    private transient User user = null;

    public Flattr(FlattrObject data) {
        super(data);
    }

    public Thing getThing() {
        if (this.thing == null) {
            this.thing = new Thing(this.data.getFlattrObject("thing"));
        }
        return this.thing;
    }

    public Date getCreated() {
        return this.data.getDate("created_at");
    }

    public String getThingId() {
        return getThing().getThingId();
    }

    public String getUserId() {
        return this.data.getSubString("owner", PodDBAdapter.KEY_USERNAME);
    }

    public User getUser() {
        if (this.user == null) {
            this.user = new User(this.data.getFlattrObject("owner"));
        }
        return this.user;
    }
}
