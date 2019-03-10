package org.jsoup.nodes;

import java.io.IOException;
import kotlin.text.Typography;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.helper.StringUtil;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.nodes.Document.OutputSettings.Syntax;

public class DocumentType extends LeafNode {
    private static final String NAME = "name";
    private static final String PUBLIC_ID = "publicId";
    public static final String PUBLIC_KEY = "PUBLIC";
    private static final String PUB_SYS_KEY = "pubSysKey";
    private static final String SYSTEM_ID = "systemId";
    public static final String SYSTEM_KEY = "SYSTEM";

    public /* bridge */ /* synthetic */ String absUrl(String str) {
        return super.absUrl(str);
    }

    public /* bridge */ /* synthetic */ String attr(String str) {
        return super.attr(str);
    }

    public /* bridge */ /* synthetic */ Node attr(String str, String str2) {
        return super.attr(str, str2);
    }

    public /* bridge */ /* synthetic */ String baseUri() {
        return super.baseUri();
    }

    public /* bridge */ /* synthetic */ int childNodeSize() {
        return super.childNodeSize();
    }

    public /* bridge */ /* synthetic */ boolean hasAttr(String str) {
        return super.hasAttr(str);
    }

    public /* bridge */ /* synthetic */ Node removeAttr(String str) {
        return super.removeAttr(str);
    }

    public DocumentType(String name, String publicId, String systemId) {
        Validate.notNull(name);
        Validate.notNull(publicId);
        Validate.notNull(systemId);
        attr("name", name);
        attr(PUBLIC_ID, publicId);
        if (has(PUBLIC_ID)) {
            attr(PUB_SYS_KEY, PUBLIC_KEY);
        }
        attr(SYSTEM_ID, systemId);
    }

    public DocumentType(String name, String publicId, String systemId, String baseUri) {
        attr("name", name);
        attr(PUBLIC_ID, publicId);
        if (has(PUBLIC_ID)) {
            attr(PUB_SYS_KEY, PUBLIC_KEY);
        }
        attr(SYSTEM_ID, systemId);
    }

    public DocumentType(String name, String pubSysKey, String publicId, String systemId, String baseUri) {
        attr("name", name);
        if (pubSysKey != null) {
            attr(PUB_SYS_KEY, pubSysKey);
        }
        attr(PUBLIC_ID, publicId);
        attr(SYSTEM_ID, systemId);
    }

    public void setPubSysKey(String value) {
        if (value != null) {
            attr(PUB_SYS_KEY, value);
        }
    }

    public String nodeName() {
        return "#doctype";
    }

    void outerHtmlHead(Appendable accum, int depth, OutputSettings out) throws IOException {
        if (out.syntax() != Syntax.html || has(PUBLIC_ID) || has(SYSTEM_ID)) {
            accum.append("<!DOCTYPE");
        } else {
            accum.append("<!doctype");
        }
        if (has("name")) {
            accum.append(StringUtils.SPACE).append(attr("name"));
        }
        if (has(PUB_SYS_KEY)) {
            accum.append(StringUtils.SPACE).append(attr(PUB_SYS_KEY));
        }
        if (has(PUBLIC_ID)) {
            accum.append(" \"").append(attr(PUBLIC_ID)).append(Typography.quote);
        }
        if (has(SYSTEM_ID)) {
            accum.append(" \"").append(attr(SYSTEM_ID)).append(Typography.quote);
        }
        accum.append(Typography.greater);
    }

    void outerHtmlTail(Appendable accum, int depth, OutputSettings out) {
    }

    private boolean has(String attribute) {
        return StringUtil.isBlank(attr(attribute)) ^ 1;
    }
}
