package org.jsoup.nodes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.jsoup.SerializationException;
import org.jsoup.helper.StringUtil;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.parser.Parser;
import org.jsoup.select.NodeFilter;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;

public abstract class Node implements Cloneable {
    static final String EmptyString = "";
    Node parentNode;
    int siblingIndex;

    private static class OuterHtmlVisitor implements NodeVisitor {
        private Appendable accum;
        private OutputSettings out;

        OuterHtmlVisitor(Appendable accum, OutputSettings out) {
            this.accum = accum;
            this.out = out;
            out.prepareEncoder();
        }

        public void head(Node node, int depth) {
            try {
                node.outerHtmlHead(this.accum, depth, this.out);
            } catch (Throwable exception) {
                throw new SerializationException(exception);
            }
        }

        public void tail(Node node, int depth) {
            if (!node.nodeName().equals("#text")) {
                try {
                    node.outerHtmlTail(this.accum, depth, this.out);
                } catch (Throwable exception) {
                    throw new SerializationException(exception);
                }
            }
        }
    }

    public abstract Attributes attributes();

    public abstract String baseUri();

    public abstract int childNodeSize();

    protected abstract void doSetBaseUri(String str);

    protected abstract List<Node> ensureChildNodes();

    protected abstract boolean hasAttributes();

    public abstract String nodeName();

    abstract void outerHtmlHead(Appendable appendable, int i, OutputSettings outputSettings) throws IOException;

    abstract void outerHtmlTail(Appendable appendable, int i, OutputSettings outputSettings) throws IOException;

    protected Node() {
    }

    public boolean hasParent() {
        return this.parentNode != null;
    }

    public String attr(String attributeKey) {
        Validate.notNull(attributeKey);
        if (!hasAttributes()) {
            return "";
        }
        String val = attributes().getIgnoreCase(attributeKey);
        if (val.length() > 0) {
            return val;
        }
        if (attributeKey.startsWith("abs:")) {
            return absUrl(attributeKey.substring("abs:".length()));
        }
        return "";
    }

    public Node attr(String attributeKey, String attributeValue) {
        attributes().putIgnoreCase(attributeKey, attributeValue);
        return this;
    }

    public boolean hasAttr(String attributeKey) {
        Validate.notNull(attributeKey);
        if (attributeKey.startsWith("abs:")) {
            String key = attributeKey.substring("abs:".length());
            if (attributes().hasKeyIgnoreCase(key) && !absUrl(key).equals("")) {
                return true;
            }
        }
        return attributes().hasKeyIgnoreCase(attributeKey);
    }

    public Node removeAttr(String attributeKey) {
        Validate.notNull(attributeKey);
        attributes().removeIgnoreCase(attributeKey);
        return this;
    }

    public Node clearAttributes() {
        Iterator<Attribute> it = attributes().iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        return this;
    }

    public void setBaseUri(final String baseUri) {
        Validate.notNull(baseUri);
        traverse(new NodeVisitor() {
            public void head(Node node, int depth) {
                node.doSetBaseUri(baseUri);
            }

            public void tail(Node node, int depth) {
            }
        });
    }

    public String absUrl(String attributeKey) {
        Validate.notEmpty(attributeKey);
        if (hasAttr(attributeKey)) {
            return StringUtil.resolve(baseUri(), attr(attributeKey));
        }
        return "";
    }

    public Node childNode(int index) {
        return (Node) ensureChildNodes().get(index);
    }

    public List<Node> childNodes() {
        return Collections.unmodifiableList(ensureChildNodes());
    }

    public List<Node> childNodesCopy() {
        List<Node> nodes = ensureChildNodes();
        ArrayList<Node> children = new ArrayList(nodes.size());
        for (Node node : nodes) {
            children.add(node.clone());
        }
        return children;
    }

    protected Node[] childNodesAsArray() {
        return (Node[]) ensureChildNodes().toArray(new Node[childNodeSize()]);
    }

    public Node parent() {
        return this.parentNode;
    }

    public final Node parentNode() {
        return this.parentNode;
    }

    public Node root() {
        Node node = this;
        while (node.parentNode != null) {
            node = node.parentNode;
        }
        return node;
    }

    public Document ownerDocument() {
        Node root = root();
        return root instanceof Document ? (Document) root : null;
    }

    public void remove() {
        Validate.notNull(this.parentNode);
        this.parentNode.removeChild(this);
    }

    public Node before(String html) {
        addSiblingHtml(this.siblingIndex, html);
        return this;
    }

    public Node before(Node node) {
        Validate.notNull(node);
        Validate.notNull(this.parentNode);
        this.parentNode.addChildren(this.siblingIndex, node);
        return this;
    }

    public Node after(String html) {
        addSiblingHtml(this.siblingIndex + 1, html);
        return this;
    }

    public Node after(Node node) {
        Validate.notNull(node);
        Validate.notNull(this.parentNode);
        this.parentNode.addChildren(this.siblingIndex + 1, node);
        return this;
    }

    private void addSiblingHtml(int index, String html) {
        Validate.notNull(html);
        Validate.notNull(this.parentNode);
        List<Node> nodes = Parser.parseFragment(html, parent() instanceof Element ? (Element) parent() : null, baseUri());
        this.parentNode.addChildren(index, (Node[]) nodes.toArray(new Node[nodes.size()]));
    }

    public Node wrap(String html) {
        Validate.notEmpty(html);
        List<Node> wrapChildren = Parser.parseFragment(html, parent() instanceof Element ? (Element) parent() : null, baseUri());
        Node wrapNode = (Node) wrapChildren.get(0);
        if (wrapNode != null) {
            if (wrapNode instanceof Element) {
                Element wrap = (Element) wrapNode;
                Element deepest = getDeepChild(wrap);
                this.parentNode.replaceChild(this, wrap);
                deepest.addChildren(this);
                if (wrapChildren.size() > 0) {
                    for (int i = 0; i < wrapChildren.size(); i++) {
                        Node remainder = (Node) wrapChildren.get(i);
                        remainder.parentNode.removeChild(remainder);
                        wrap.appendChild(remainder);
                    }
                }
                return this;
            }
        }
        return null;
    }

    public Node unwrap() {
        Validate.notNull(this.parentNode);
        List<Node> childNodes = ensureChildNodes();
        Node firstChild = childNodes.size() > 0 ? (Node) childNodes.get(0) : null;
        this.parentNode.addChildren(this.siblingIndex, childNodesAsArray());
        remove();
        return firstChild;
    }

    private Element getDeepChild(Element el) {
        List<Element> children = el.children();
        if (children.size() > 0) {
            return getDeepChild((Element) children.get(0));
        }
        return el;
    }

    void nodelistChanged() {
    }

    public void replaceWith(Node in) {
        Validate.notNull(in);
        Validate.notNull(this.parentNode);
        this.parentNode.replaceChild(this, in);
    }

    protected void setParentNode(Node parentNode) {
        Validate.notNull(parentNode);
        Node node = this.parentNode;
        if (node != null) {
            node.removeChild(this);
        }
        this.parentNode = parentNode;
    }

    protected void replaceChild(Node out, Node in) {
        Validate.isTrue(out.parentNode == this);
        Validate.notNull(in);
        Node node = in.parentNode;
        if (node != null) {
            node.removeChild(in);
        }
        int index = out.siblingIndex;
        ensureChildNodes().set(index, in);
        in.parentNode = this;
        in.setSiblingIndex(index);
        out.parentNode = null;
    }

    protected void removeChild(Node out) {
        Validate.isTrue(out.parentNode == this);
        int index = out.siblingIndex;
        ensureChildNodes().remove(index);
        reindexChildren(index);
        out.parentNode = null;
    }

    protected void addChildren(Node... children) {
        List<Node> nodes = ensureChildNodes();
        for (Node child : children) {
            reparentChild(child);
            nodes.add(child);
            child.setSiblingIndex(nodes.size() - 1);
        }
    }

    protected void addChildren(int index, Node... children) {
        Validate.noNullElements(children);
        List<Node> nodes = ensureChildNodes();
        for (Node child : children) {
            reparentChild(child);
        }
        nodes.addAll(index, Arrays.asList(children));
        reindexChildren(index);
    }

    protected void reparentChild(Node child) {
        child.setParentNode(this);
    }

    private void reindexChildren(int start) {
        List<Node> childNodes = ensureChildNodes();
        for (int i = start; i < childNodes.size(); i++) {
            ((Node) childNodes.get(i)).setSiblingIndex(i);
        }
    }

    public List<Node> siblingNodes() {
        List<Node> nodes = this.parentNode;
        if (nodes == null) {
            return Collections.emptyList();
        }
        nodes = nodes.ensureChildNodes();
        List<Node> siblings = new ArrayList(nodes.size() - 1);
        for (Node node : nodes) {
            if (node != this) {
                siblings.add(node);
            }
        }
        return siblings;
    }

    public Node nextSibling() {
        List<Node> siblings = this.parentNode;
        if (siblings == null) {
            return null;
        }
        siblings = siblings.ensureChildNodes();
        int index = this.siblingIndex + 1;
        if (siblings.size() > index) {
            return (Node) siblings.get(index);
        }
        return null;
    }

    public Node previousSibling() {
        Node node = this.parentNode;
        if (node != null && this.siblingIndex > 0) {
            return (Node) node.ensureChildNodes().get(this.siblingIndex - 1);
        }
        return null;
    }

    public int siblingIndex() {
        return this.siblingIndex;
    }

    protected void setSiblingIndex(int siblingIndex) {
        this.siblingIndex = siblingIndex;
    }

    public Node traverse(NodeVisitor nodeVisitor) {
        Validate.notNull(nodeVisitor);
        NodeTraversor.traverse(nodeVisitor, this);
        return this;
    }

    public Node filter(NodeFilter nodeFilter) {
        Validate.notNull(nodeFilter);
        NodeTraversor.filter(nodeFilter, this);
        return this;
    }

    public String outerHtml() {
        StringBuilder accum = new StringBuilder(128);
        outerHtml(accum);
        return accum.toString();
    }

    protected void outerHtml(Appendable accum) {
        NodeTraversor.traverse(new OuterHtmlVisitor(accum, getOutputSettings()), this);
    }

    OutputSettings getOutputSettings() {
        Document owner = ownerDocument();
        return owner != null ? owner.outputSettings() : new Document("").outputSettings();
    }

    public <T extends Appendable> T html(T appendable) {
        outerHtml(appendable);
        return appendable;
    }

    public String toString() {
        return outerHtml();
    }

    protected void indent(Appendable accum, int depth, OutputSettings out) throws IOException {
        accum.append('\n').append(StringUtil.padding(out.indentAmount() * depth));
    }

    public boolean equals(Object o) {
        return this == o;
    }

    public boolean hasSameValue(Object o) {
        if (this == o) {
            return true;
        }
        if (o != null) {
            if (getClass() == o.getClass()) {
                return outerHtml().equals(((Node) o).outerHtml());
            }
        }
        return false;
    }

    public Node clone() {
        Node thisClone = doClone(null);
        LinkedList<Node> nodesToProcess = new LinkedList();
        nodesToProcess.add(thisClone);
        while (!nodesToProcess.isEmpty()) {
            Node currParent = (Node) nodesToProcess.remove();
            int size = currParent.childNodeSize();
            for (int i = 0; i < size; i++) {
                List<Node> childNodes = currParent.ensureChildNodes();
                Node childClone = ((Node) childNodes.get(i)).doClone(currParent);
                childNodes.set(i, childClone);
                nodesToProcess.add(childClone);
            }
        }
        return thisClone;
    }

    public Node shallowClone() {
        return doClone(null);
    }

    protected Node doClone(Node parent) {
        try {
            Node clone = (Node) super.clone();
            clone.parentNode = parent;
            clone.siblingIndex = parent == null ? 0 : this.siblingIndex;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
