package org.jsoup.nodes;

import java.io.IOException;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.parser.Tag;

public class PseudoTextElement extends Element {
    public PseudoTextElement(Tag tag, String baseUri, Attributes attributes) {
        super(tag, baseUri, attributes);
    }

    void outerHtmlHead(Appendable accum, int depth, OutputSettings out) throws IOException {
    }

    void outerHtmlTail(Appendable accum, int depth, OutputSettings out) throws IOException {
    }
}
