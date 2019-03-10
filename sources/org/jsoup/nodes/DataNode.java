package org.jsoup.nodes;

import java.io.IOException;
import org.jsoup.nodes.Document.OutputSettings;

public class DataNode extends LeafNode {
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

    public DataNode(String data) {
        this.value = data;
    }

    public DataNode(String data, String baseUri) {
        this(data);
    }

    public String nodeName() {
        return "#data";
    }

    public String getWholeData() {
        return coreValue();
    }

    public DataNode setWholeData(String data) {
        coreValue(data);
        return this;
    }

    void outerHtmlHead(Appendable accum, int depth, OutputSettings out) throws IOException {
        accum.append(getWholeData());
    }

    void outerHtmlTail(Appendable accum, int depth, OutputSettings out) {
    }

    public String toString() {
        return outerHtml();
    }

    public static DataNode createFromEncoded(String encodedData, String baseUri) {
        return new DataNode(Entities.unescape(encodedData));
    }
}
