package org.jsoup.nodes;

import java.io.IOException;
import org.jsoup.nodes.Document.OutputSettings;

public class Comment extends LeafNode {
    private static final String COMMENT_KEY = "comment";

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

    public Comment(String data) {
        this.value = data;
    }

    public Comment(String data, String baseUri) {
        this(data);
    }

    public String nodeName() {
        return "#comment";
    }

    public String getData() {
        return coreValue();
    }

    void outerHtmlHead(Appendable accum, int depth, OutputSettings out) throws IOException {
        if (out.prettyPrint()) {
            indent(accum, depth, out);
        }
        accum.append("<!--").append(getData()).append("-->");
    }

    void outerHtmlTail(Appendable accum, int depth, OutputSettings out) {
    }

    public String toString() {
        return outerHtml();
    }
}
