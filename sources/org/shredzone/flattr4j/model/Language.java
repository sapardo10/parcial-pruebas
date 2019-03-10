package org.shredzone.flattr4j.model;

import org.shredzone.flattr4j.connector.FlattrObject;

public class Language extends Resource implements LanguageId {
    private static final long serialVersionUID = -2166187856968632922L;

    public static LanguageId withId(final String id) {
        return new LanguageId() {
            public String getLanguageId() {
                return id;
            }
        };
    }

    public Language(FlattrObject data) {
        super(data);
    }

    public String getLanguageId() {
        return this.data.get("id");
    }

    public String getName() {
        return this.data.get("text");
    }

    public boolean equals(Object obj) {
        String pk = getLanguageId();
        if (!(pk == null || obj == null)) {
            if (obj instanceof Language) {
                return pk.equals(((Language) obj).getLanguageId());
            }
        }
        return false;
    }

    public int hashCode() {
        String pk = getLanguageId();
        return pk != null ? pk.hashCode() : 0;
    }
}
