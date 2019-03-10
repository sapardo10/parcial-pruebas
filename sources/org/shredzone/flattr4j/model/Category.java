package org.shredzone.flattr4j.model;

import org.shredzone.flattr4j.connector.FlattrObject;

public class Category extends Resource implements CategoryId {
    private static final long serialVersionUID = 6749493295567461888L;

    public static CategoryId withId(final String id) {
        return new CategoryId() {
            public String getCategoryId() {
                return id;
            }
        };
    }

    public Category(FlattrObject data) {
        super(data);
    }

    public String getCategoryId() {
        return this.data.get("id");
    }

    public String getName() {
        return this.data.get("text");
    }

    public boolean equals(Object obj) {
        String pk = getCategoryId();
        if (!(pk == null || obj == null)) {
            if (obj instanceof Category) {
                return pk.equals(((Category) obj).getCategoryId());
            }
        }
        return false;
    }

    public int hashCode() {
        String pk = getCategoryId();
        return pk != null ? pk.hashCode() : 0;
    }
}
