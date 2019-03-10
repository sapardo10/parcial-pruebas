package org.jsoup.select;

import java.util.Iterator;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.NodeFilter.FilterResult;

public class NodeTraversor {
    private NodeVisitor visitor;

    public NodeTraversor(NodeVisitor visitor) {
        this.visitor = visitor;
    }

    public void traverse(Node root) {
        traverse(this.visitor, root);
    }

    public static void traverse(NodeVisitor visitor, Node root) {
        Node node = root;
        int depth = 0;
        while (node != null) {
            visitor.head(node, depth);
            if (node.childNodeSize() > 0) {
                node = node.childNode(0);
                depth++;
            } else {
                while (node.nextSibling() == null && depth > 0) {
                    visitor.tail(node, depth);
                    node = node.parentNode();
                    depth--;
                }
                visitor.tail(node, depth);
                if (node != root) {
                    node = node.nextSibling();
                } else {
                    return;
                }
            }
        }
    }

    public static void traverse(NodeVisitor visitor, Elements elements) {
        Validate.notNull(visitor);
        Validate.notNull(elements);
        Iterator it = elements.iterator();
        while (it.hasNext()) {
            traverse(visitor, (Element) it.next());
        }
    }

    public static FilterResult filter(NodeFilter filter, Node root) {
        Node node = root;
        int depth = 0;
        while (node != null) {
            FilterResult result = filter.head(node, depth);
            if (result == FilterResult.STOP) {
                return result;
            }
            if (result != FilterResult.CONTINUE || node.childNodeSize() <= 0) {
                Node prev;
                while (node.nextSibling() == null && depth > 0) {
                    if (result != FilterResult.CONTINUE) {
                        if (result != FilterResult.SKIP_CHILDREN) {
                            prev = node;
                            node = node.parentNode();
                            depth--;
                            if (result == FilterResult.REMOVE) {
                                prev.remove();
                            }
                            result = FilterResult.CONTINUE;
                        }
                    }
                    result = filter.tail(node, depth);
                    if (result == FilterResult.STOP) {
                        return result;
                    }
                    prev = node;
                    node = node.parentNode();
                    depth--;
                    if (result == FilterResult.REMOVE) {
                        prev.remove();
                    }
                    result = FilterResult.CONTINUE;
                }
                if (result != FilterResult.CONTINUE) {
                    if (result != FilterResult.SKIP_CHILDREN) {
                        if (node == root) {
                            return result;
                        }
                        prev = node;
                        node = node.nextSibling();
                        if (result == FilterResult.REMOVE) {
                            prev.remove();
                        }
                    }
                }
                result = filter.tail(node, depth);
                if (result == FilterResult.STOP) {
                    return result;
                }
                if (node == root) {
                    return result;
                }
                prev = node;
                node = node.nextSibling();
                if (result == FilterResult.REMOVE) {
                    prev.remove();
                }
            } else {
                node = node.childNode(0);
                depth++;
            }
        }
        return FilterResult.CONTINUE;
    }

    public static void filter(NodeFilter filter, Elements elements) {
        Validate.notNull(filter);
        Validate.notNull(elements);
        Iterator it = elements.iterator();
        while (it.hasNext()) {
            if (filter(filter, (Element) it.next()) == FilterResult.STOP) {
                return;
            }
        }
    }
}
