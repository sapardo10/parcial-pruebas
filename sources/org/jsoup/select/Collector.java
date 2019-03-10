package org.jsoup.select;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.NodeFilter.FilterResult;

public class Collector {

    private static class Accumulator implements NodeVisitor {
        private final Elements elements;
        private final Evaluator eval;
        private final Element root;

        Accumulator(Element root, Elements elements, Evaluator eval) {
            this.root = root;
            this.elements = elements;
            this.eval = eval;
        }

        public void head(Node node, int depth) {
            if (node instanceof Element) {
                Element el = (Element) node;
                if (this.eval.matches(this.root, el)) {
                    this.elements.add(el);
                }
            }
        }

        public void tail(Node node, int depth) {
        }
    }

    private static class FirstFinder implements NodeFilter {
        private final Evaluator eval;
        private Element match = null;
        private final Element root;

        FirstFinder(Element root, Evaluator eval) {
            this.root = root;
            this.eval = eval;
        }

        public FilterResult head(Node node, int depth) {
            if (node instanceof Element) {
                Element el = (Element) node;
                if (this.eval.matches(this.root, el)) {
                    this.match = el;
                    return FilterResult.STOP;
                }
            }
            return FilterResult.CONTINUE;
        }

        public FilterResult tail(Node node, int depth) {
            return FilterResult.CONTINUE;
        }
    }

    private Collector() {
    }

    public static Elements collect(Evaluator eval, Element root) {
        Elements elements = new Elements();
        NodeTraversor.traverse(new Accumulator(root, elements, eval), (Node) root);
        return elements;
    }

    public static Element findFirst(Evaluator eval, Element root) {
        NodeFilter finder = new FirstFinder(root, eval);
        NodeTraversor.filter(finder, (Node) root);
        return finder.match;
    }
}
