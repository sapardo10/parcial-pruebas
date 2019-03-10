package de.danoeh.antennapod.core.syndication.namespace.atom;

import de.danoeh.antennapod.core.syndication.namespace.Namespace;
import de.danoeh.antennapod.core.syndication.namespace.SyndElement;
import org.apache.commons.text.StringEscapeUtils;

public class AtomText extends SyndElement {
    private static final String TYPE_HTML = "html";
    public static final String TYPE_TEXT = "text";
    private static final String TYPE_XHTML = "xhtml";
    private String content;
    private final String type;

    public AtomText(String name, Namespace namespace, String type) {
        super(name, namespace);
        this.type = type;
    }

    public String getProcessedContent() {
        String str = this.type;
        if (str == null) {
            return this.content;
        }
        if (str.equals(TYPE_HTML)) {
            return StringEscapeUtils.unescapeHtml4(this.content);
        }
        if (this.type.equals(TYPE_XHTML)) {
            return this.content;
        }
        return this.content;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return this.type;
    }
}
