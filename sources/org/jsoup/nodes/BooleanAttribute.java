package org.jsoup.nodes;

public class BooleanAttribute extends Attribute {
    public BooleanAttribute(String key) {
        super(key, null);
    }

    protected boolean isBooleanAttribute() {
        return true;
    }
}
