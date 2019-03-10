package org.shredzone.flattr4j.model;

import java.io.Serializable;
import org.shredzone.flattr4j.connector.FlattrObject;

public class Resource implements Serializable {
    private static final long serialVersionUID = 2052931614858694519L;
    protected FlattrObject data;

    public Resource(FlattrObject data) {
        this.data = data;
    }

    public String toJSON() {
        return this.data.toString();
    }

    public FlattrObject toFlattrObject() {
        return this.data;
    }
}
