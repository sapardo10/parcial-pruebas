package org.jsoup.helper;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class W3CDom {
    protected DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    protected static class W3CBuilder implements NodeVisitor {
        private static final String xmlnsKey = "xmlns";
        private static final String xmlnsPrefix = "xmlns:";
        private Element dest;
        private final Document doc;
        private final Stack<HashMap<String, String>> namespacesStack = new Stack();

        public W3CBuilder(Document doc) {
            this.doc = doc;
            this.namespacesStack.push(new HashMap());
        }

        public void head(Node source, int depth) {
            Stack stack = this.namespacesStack;
            stack.push(new HashMap((Map) stack.peek()));
            if (source instanceof org.jsoup.nodes.Element) {
                org.jsoup.nodes.Element sourceEl = (org.jsoup.nodes.Element) source;
                Element el = this.doc.createElementNS((String) ((HashMap) this.namespacesStack.peek()).get(updateNamespaces(sourceEl)), sourceEl.tagName());
                copyAttributes(sourceEl, el);
                Element element = this.dest;
                if (element == null) {
                    this.doc.appendChild(el);
                } else {
                    element.appendChild(el);
                }
                this.dest = el;
            } else if (source instanceof TextNode) {
                this.dest.appendChild(this.doc.createTextNode(((TextNode) source).getWholeText()));
            } else {
                if (source instanceof Comment) {
                    this.dest.appendChild(this.doc.createComment(((Comment) source).getData()));
                } else if (source instanceof DataNode) {
                    this.dest.appendChild(this.doc.createTextNode(((DataNode) source).getWholeData()));
                }
            }
        }

        public void tail(Node source, int depth) {
            if ((source instanceof org.jsoup.nodes.Element) && (this.dest.getParentNode() instanceof Element)) {
                this.dest = (Element) this.dest.getParentNode();
            }
            this.namespacesStack.pop();
        }

        private void copyAttributes(Node source, Element el) {
            Iterator it = source.attributes().iterator();
            while (it.hasNext()) {
                Attribute attribute = (Attribute) it.next();
                String key = attribute.getKey().replaceAll("[^-a-zA-Z0-9_:.]", "");
                if (key.matches("[a-zA-Z_:][-a-zA-Z0-9_:.]*")) {
                    el.setAttribute(key, attribute.getValue());
                }
            }
        }

        private String updateNamespaces(org.jsoup.nodes.Element el) {
            Iterator it = el.attributes().iterator();
            while (it.hasNext()) {
                String prefix;
                Attribute attr = (Attribute) it.next();
                String key = attr.getKey();
                if (key.equals(xmlnsKey)) {
                    prefix = "";
                } else if (key.startsWith(xmlnsPrefix)) {
                    prefix = key.substring(xmlnsPrefix.length());
                }
                ((HashMap) this.namespacesStack.peek()).put(prefix, attr.getValue());
            }
            int pos = el.tagName().indexOf(":");
            return pos > 0 ? el.tagName().substring(0, pos) : "";
        }
    }

    public Document fromJsoup(org.jsoup.nodes.Document in) {
        Validate.notNull(in);
        try {
            this.factory.setNamespaceAware(true);
            Document out = this.factory.newDocumentBuilder().newDocument();
            convert(in, out);
            return out;
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException(e);
        }
    }

    public void convert(org.jsoup.nodes.Document in, Document out) {
        if (!StringUtil.isBlank(in.location())) {
            out.setDocumentURI(in.location());
        }
        NodeTraversor.traverse(new W3CBuilder(out), in.child(null));
    }

    public String asString(Document doc) {
        try {
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            TransformerFactory.newInstance().newTransformer().transform(domSource, new StreamResult(writer));
            return writer.toString();
        } catch (TransformerException e) {
            throw new IllegalStateException(e);
        }
    }
}
