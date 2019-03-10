package de.danoeh.antennapod.core.util.syndication;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;

public class HtmlToPlainText {

    private static class FormattingVisitor implements NodeVisitor {
        private final StringBuilder accum;

        private FormattingVisitor() {
            this.accum = new StringBuilder();
        }

        public void head(Node node, int depth) {
            String name = node.nodeName();
            if (node instanceof TextNode) {
                append(((TextNode) node).text());
            } else if (name.equals("li")) {
                append("\n * ");
            } else if (name.equals("dt")) {
                append("  ");
            } else {
                if (StringUtil.in(name, new String[]{TtmlNode.TAG_P, "h1", "h2", "h3", "h4", "h5", "tr"})) {
                    append("\n");
                }
            }
        }

        public void tail(Node node, int depth) {
            String name = node.nodeName();
            if (StringUtil.in(name, new String[]{TtmlNode.TAG_BR, "dd", "dt", TtmlNode.TAG_P, "h1", "h2", "h3", "h4", "h5"})) {
                append("\n");
            } else if (name.equals("a")) {
                append(String.format(" <%s>", new Object[]{node.absUrl("href")}));
            }
        }

        private void append(String text) {
            if (text.equals(StringUtils.SPACE)) {
                if (this.accum.length() != 0) {
                    StringBuilder stringBuilder = this.accum;
                    if (StringUtil.in(stringBuilder.substring(stringBuilder.length() - 1), new String[]{StringUtils.SPACE, "\n"})) {
                    }
                }
                return;
            }
            this.accum.append(text);
        }

        public String toString() {
            return this.accum.toString();
        }
    }

    public String getPlainText(Element element) {
        FormattingVisitor formatter = new FormattingVisitor();
        new NodeTraversor(formatter).traverse(element);
        return formatter.toString();
    }
}
