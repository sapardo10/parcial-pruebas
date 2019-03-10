package org.jsoup.nodes;

import java.io.IOException;
import org.jsoup.helper.StringUtil;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document.OutputSettings;

public class TextNode extends LeafNode {
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

    public TextNode(String text) {
        this.value = text;
    }

    public TextNode(String text, String baseUri) {
        this(text);
    }

    public String nodeName() {
        return "#text";
    }

    public String text() {
        return normaliseWhitespace(getWholeText());
    }

    public TextNode text(String text) {
        coreValue(text);
        return this;
    }

    public String getWholeText() {
        return coreValue();
    }

    public boolean isBlank() {
        return StringUtil.isBlank(coreValue());
    }

    public TextNode splitText(int offset) {
        String text = coreValue();
        Validate.isTrue(offset >= 0, "Split offset must be not be negative");
        Validate.isTrue(offset < text.length(), "Split offset must not be greater than current text length");
        String head = text.substring(0, offset);
        String tail = text.substring(offset);
        text(head);
        TextNode tailNode = new TextNode(tail);
        if (parent() != null) {
            parent().addChildren(siblingIndex() + 1, tailNode);
        }
        return tailNode;
    }

    void outerHtmlHead(Appendable accum, int depth, OutputSettings out) throws IOException {
        boolean normaliseWhite;
        if (out.prettyPrint() && ((siblingIndex() == 0 && (this.parentNode instanceof Element) && ((Element) this.parentNode).tag().formatAsBlock() && !isBlank()) || (out.outline() && siblingNodes().size() > 0 && !isBlank()))) {
            indent(accum, depth, out);
        }
        if (out.prettyPrint() && (parent() instanceof Element)) {
            if (!Element.preserveWhitespace(parent())) {
                normaliseWhite = true;
                Entities.escape(accum, coreValue(), out, false, normaliseWhite, false);
            }
        }
        normaliseWhite = false;
        Entities.escape(accum, coreValue(), out, false, normaliseWhite, false);
    }

    void outerHtmlTail(Appendable accum, int depth, OutputSettings out) {
    }

    public String toString() {
        return outerHtml();
    }

    public static TextNode createFromEncoded(String encodedText, String baseUri) {
        return new TextNode(Entities.unescape(encodedText));
    }

    public static TextNode createFromEncoded(String encodedText) {
        return new TextNode(Entities.unescape(encodedText));
    }

    static String normaliseWhitespace(String text) {
        return StringUtil.normaliseWhitespace(text);
    }

    static String stripLeadingWhitespace(String text) {
        return text.replaceFirst("^\\s+", "");
    }

    static boolean lastCharIsWhitespace(StringBuilder sb) {
        return sb.length() != 0 && sb.charAt(sb.length() - 1) == ' ';
    }
}
