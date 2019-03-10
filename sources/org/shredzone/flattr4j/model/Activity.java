package org.shredzone.flattr4j.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.shredzone.flattr4j.connector.FlattrObject;
import org.shredzone.flattr4j.connector.impl.Logger;
import org.shredzone.flattr4j.exception.MarshalException;

public class Activity extends Resource {
    private static final Logger LOG = new Logger("flattr4j", Activity.class.getName());
    private static final long serialVersionUID = -7610676384296279814L;

    public enum Type {
        OUTGOING,
        INCOMING
    }

    public Activity(FlattrObject data) {
        super(data);
    }

    public Date getPublished() {
        try {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(this.data.get("published").replaceAll("(\\d\\d):(\\d\\d)$", "$1$2"));
        } catch (ParseException e) {
            return null;
        }
    }

    public String getTitle() {
        return this.data.get("title");
    }

    public String getVerb() {
        return this.data.get("verb");
    }

    public String getActivityId() {
        return this.data.get("id");
    }

    public String getActor(String key) {
        try {
            return this.data.getSubString("actor", key);
        } catch (MarshalException ex) {
            LOG.debug("actor", ex);
            return null;
        }
    }

    public String getObject(String key) {
        try {
            return this.data.getSubString("object", key);
        } catch (MarshalException ex) {
            LOG.debug("object", ex);
            return null;
        }
    }
}
