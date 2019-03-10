package org.jsoup.parser;

import org.jsoup.internal.Normalizer;
import org.jsoup.nodes.Attributes;

public class ParseSettings {
    public static final ParseSettings htmlDefault = new ParseSettings(false, false);
    public static final ParseSettings preserveCase = new ParseSettings(true, true);
    private final boolean preserveAttributeCase;
    private final boolean preserveTagCase;

    public ParseSettings(boolean tag, boolean attribute) {
        this.preserveTagCase = tag;
        this.preserveAttributeCase = attribute;
    }

    String normalizeTag(String name) {
        name = name.trim();
        if (this.preserveTagCase) {
            return name;
        }
        return Normalizer.lowerCase(name);
    }

    String normalizeAttribute(String name) {
        name = name.trim();
        if (this.preserveAttributeCase) {
            return name;
        }
        return Normalizer.lowerCase(name);
    }

    Attributes normalizeAttributes(Attributes attributes) {
        if (!this.preserveAttributeCase) {
            attributes.normalize();
        }
        return attributes;
    }
}
