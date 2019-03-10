package org.shredzone.flattr4j.model;

import java.util.Date;
import org.shredzone.flattr4j.connector.FlattrObject;

public class Subscription extends Resource implements ThingId {
    private static final long serialVersionUID = -6970294508136441692L;
    private transient Thing thing = null;

    public Subscription(FlattrObject data) {
        super(data);
    }

    public Thing getThing() {
        if (this.thing == null) {
            this.thing = new Thing(this.data.getFlattrObject("thing"));
        }
        return this.thing;
    }

    public String getThingId() {
        return getThing().getThingId();
    }

    public Date getCreated() {
        return this.data.getDate("created_at");
    }

    public Date getStarted() {
        return this.data.getDate("started_at");
    }

    public boolean isActive() {
        return this.data.getBoolean("active");
    }
}
