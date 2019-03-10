package org.jsoup.nodes;

import java.io.IOException;
import java.util.Iterator;
import org.jsoup.SerializationException;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document.OutputSettings;

public class XmlDeclaration extends LeafNode {
    private final boolean isProcessingInstruction;

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

    public XmlDeclaration(String name, boolean isProcessingInstruction) {
        Validate.notNull(name);
        this.value = name;
        this.isProcessingInstruction = isProcessingInstruction;
    }

    public XmlDeclaration(String name, String baseUri, boolean isProcessingInstruction) {
        this(name, isProcessingInstruction);
    }

    public String nodeName() {
        return "#declaration";
    }

    public String name() {
        return coreValue();
    }

    public String getWholeDeclaration() {
        StringBuilder sb = new StringBuilder();
        try {
            getWholeDeclaration(sb, new OutputSettings());
            return sb.toString().trim();
        } catch (Throwable e) {
            throw new SerializationException(e);
        }
    }

    private void getWholeDeclaration(Appendable accum, OutputSettings out) throws IOException {
        Iterator it = attributes().iterator();
        while (it.hasNext()) {
            Attribute attribute = (Attribute) it.next();
            if (!attribute.getKey().equals(nodeName())) {
                accum.append(' ');
                attribute.html(accum, out);
            }
        }
    }

    void outerHtmlHead(Appendable accum, int depth, OutputSettings out) throws IOException {
        accum.append("<").append(this.isProcessingInstruction ? "!" : "?").append(coreValue());
        getWholeDeclaration(accum, out);
        accum.append(this.isProcessingInstruction ? "!" : "?").append(">");
    }

    void outerHtmlTail(Appendable accum, int depth, OutputSettings out) {
    }

    public String toString() {
        return outerHtml();
    }
}
