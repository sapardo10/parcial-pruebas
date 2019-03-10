package org.jsoup.nodes;

import java.util.List;
import org.jsoup.helper.Validate;

abstract class LeafNode extends Node {
    Object value;

    LeafNode() {
    }

    protected final boolean hasAttributes() {
        return this.value instanceof Attributes;
    }

    public final Attributes attributes() {
        ensureAttributes();
        return (Attributes) this.value;
    }

    private void ensureAttributes() {
        if (!hasAttributes()) {
            Object coreValue = this.value;
            Attributes attributes = new Attributes();
            this.value = attributes;
            if (coreValue != null) {
                attributes.put(nodeName(), (String) coreValue);
            }
        }
    }

    String coreValue() {
        return attr(nodeName());
    }

    void coreValue(String value) {
        attr(nodeName(), value);
    }

    public String attr(String key) {
        Validate.notNull(key);
        if (hasAttributes()) {
            return super.attr(key);
        }
        return key.equals(nodeName()) ? (String) this.value : "";
    }

    public Node attr(String key, String value) {
        if (hasAttributes() || !key.equals(nodeName())) {
            ensureAttributes();
            super.attr(key, value);
        } else {
            this.value = value;
        }
        return this;
    }

    public boolean hasAttr(String key) {
        ensureAttributes();
        return super.hasAttr(key);
    }

    public Node removeAttr(String key) {
        ensureAttributes();
        return super.removeAttr(key);
    }

    public String absUrl(String key) {
        ensureAttributes();
        return super.absUrl(key);
    }

    public String baseUri() {
        return hasParent() ? parent().baseUri() : "";
    }

    protected void doSetBaseUri(String baseUri) {
    }

    public int childNodeSize() {
        return 0;
    }

    protected List<Node> ensureChildNodes() {
        throw new UnsupportedOperationException("Leaf Nodes do not have child nodes.");
    }
}
