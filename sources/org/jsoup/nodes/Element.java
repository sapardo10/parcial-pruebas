package org.jsoup.nodes;

import com.google.android.exoplayer2.text.ttml.TtmlNode;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import kotlin.text.Typography;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.helper.ChangeNotifyingArrayList;
import org.jsoup.helper.StringUtil;
import org.jsoup.helper.Validate;
import org.jsoup.internal.Normalizer;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.nodes.Document.OutputSettings.Syntax;
import org.jsoup.parser.ParseSettings;
import org.jsoup.parser.Parser;
import org.jsoup.parser.Tag;
import org.jsoup.select.Collector;
import org.jsoup.select.Elements;
import org.jsoup.select.Evaluator;
import org.jsoup.select.Evaluator.AllElements;
import org.jsoup.select.Evaluator.Attribute;
import org.jsoup.select.Evaluator.AttributeStarting;
import org.jsoup.select.Evaluator.AttributeWithValue;
import org.jsoup.select.Evaluator.AttributeWithValueContaining;
import org.jsoup.select.Evaluator.AttributeWithValueEnding;
import org.jsoup.select.Evaluator.AttributeWithValueMatching;
import org.jsoup.select.Evaluator.AttributeWithValueNot;
import org.jsoup.select.Evaluator.AttributeWithValueStarting;
import org.jsoup.select.Evaluator.Class;
import org.jsoup.select.Evaluator.ContainsOwnText;
import org.jsoup.select.Evaluator.ContainsText;
import org.jsoup.select.Evaluator.Id;
import org.jsoup.select.Evaluator.IndexEquals;
import org.jsoup.select.Evaluator.IndexGreaterThan;
import org.jsoup.select.Evaluator.IndexLessThan;
import org.jsoup.select.Evaluator.Matches;
import org.jsoup.select.Evaluator.MatchesOwn;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;
import org.jsoup.select.QueryParser;
import org.jsoup.select.Selector;

public class Element extends Node {
    private static final List<Node> EMPTY_NODES = Collections.emptyList();
    private static final Pattern classSplit = Pattern.compile("\\s+");
    private Attributes attributes;
    private String baseUri;
    List<Node> childNodes;
    private WeakReference<List<Element>> shadowChildrenRef;
    private Tag tag;

    private static final class NodeList extends ChangeNotifyingArrayList<Node> {
        private final Element owner;

        NodeList(Element owner, int initialCapacity) {
            super(initialCapacity);
            this.owner = owner;
        }

        public void onContentsChanged() {
            this.owner.nodelistChanged();
        }
    }

    public Element(String tag) {
        this(Tag.valueOf(tag), "", new Attributes());
    }

    public Element(Tag tag, String baseUri, Attributes attributes) {
        Validate.notNull(tag);
        Validate.notNull(baseUri);
        this.childNodes = EMPTY_NODES;
        this.baseUri = baseUri;
        this.attributes = attributes;
        this.tag = tag;
    }

    public Element(Tag tag, String baseUri) {
        this(tag, baseUri, null);
    }

    protected List<Node> ensureChildNodes() {
        if (this.childNodes == EMPTY_NODES) {
            this.childNodes = new NodeList(this, 4);
        }
        return this.childNodes;
    }

    protected boolean hasAttributes() {
        return this.attributes != null;
    }

    public Attributes attributes() {
        if (!hasAttributes()) {
            this.attributes = new Attributes();
        }
        return this.attributes;
    }

    public String baseUri() {
        return this.baseUri;
    }

    protected void doSetBaseUri(String baseUri) {
        this.baseUri = baseUri;
    }

    public int childNodeSize() {
        return this.childNodes.size();
    }

    public String nodeName() {
        return this.tag.getName();
    }

    public String tagName() {
        return this.tag.getName();
    }

    public Element tagName(String tagName) {
        Validate.notEmpty(tagName, "Tag name must not be empty.");
        this.tag = Tag.valueOf(tagName, ParseSettings.preserveCase);
        return this;
    }

    public Tag tag() {
        return this.tag;
    }

    public boolean isBlock() {
        return this.tag.isBlock();
    }

    public String id() {
        return attributes().getIgnoreCase("id");
    }

    public Element attr(String attributeKey, String attributeValue) {
        super.attr(attributeKey, attributeValue);
        return this;
    }

    public Element attr(String attributeKey, boolean attributeValue) {
        attributes().put(attributeKey, attributeValue);
        return this;
    }

    public Map<String, String> dataset() {
        return attributes().dataset();
    }

    public final Element parent() {
        return (Element) this.parentNode;
    }

    public Elements parents() {
        Elements parents = new Elements();
        accumulateParents(this, parents);
        return parents;
    }

    private static void accumulateParents(Element el, Elements parents) {
        Element parent = el.parent();
        if (parent != null && !parent.tagName().equals("#root")) {
            parents.add(parent);
            accumulateParents(parent, parents);
        }
    }

    public Element child(int index) {
        return (Element) childElementsList().get(index);
    }

    public Elements children() {
        return new Elements(childElementsList());
    }

    private List<Element> childElementsList() {
        List<Element> children;
        WeakReference weakReference = this.shadowChildrenRef;
        if (weakReference != null) {
            List<Element> list = (List) weakReference.get();
            children = list;
            if (list != null) {
                return children;
            }
        }
        int size = this.childNodes.size();
        children = new ArrayList(size);
        for (int i = 0; i < size; i++) {
            Node node = (Node) this.childNodes.get(i);
            if (node instanceof Element) {
                children.add((Element) node);
            }
        }
        this.shadowChildrenRef = new WeakReference(children);
        return children;
    }

    void nodelistChanged() {
        super.nodelistChanged();
        this.shadowChildrenRef = null;
    }

    public List<TextNode> textNodes() {
        List<TextNode> textNodes = new ArrayList();
        for (Node node : this.childNodes) {
            if (node instanceof TextNode) {
                textNodes.add((TextNode) node);
            }
        }
        return Collections.unmodifiableList(textNodes);
    }

    public List<DataNode> dataNodes() {
        List<DataNode> dataNodes = new ArrayList();
        for (Node node : this.childNodes) {
            if (node instanceof DataNode) {
                dataNodes.add((DataNode) node);
            }
        }
        return Collections.unmodifiableList(dataNodes);
    }

    public Elements select(String cssQuery) {
        return Selector.select(cssQuery, this);
    }

    public Element selectFirst(String cssQuery) {
        return Selector.selectFirst(cssQuery, this);
    }

    public boolean is(String cssQuery) {
        return is(QueryParser.parse(cssQuery));
    }

    public boolean is(Evaluator evaluator) {
        return evaluator.matches((Element) root(), this);
    }

    public Element appendChild(Node child) {
        Validate.notNull(child);
        reparentChild(child);
        ensureChildNodes();
        this.childNodes.add(child);
        child.setSiblingIndex(this.childNodes.size() - 1);
        return this;
    }

    public Element appendTo(Element parent) {
        Validate.notNull(parent);
        parent.appendChild(this);
        return this;
    }

    public Element prependChild(Node child) {
        Validate.notNull(child);
        addChildren(0, child);
        return this;
    }

    public Element insertChildren(int index, Collection<? extends Node> children) {
        Validate.notNull(children, "Children collection to be inserted must not be null.");
        int currentSize = childNodeSize();
        if (index < 0) {
            index += currentSize + 1;
        }
        boolean z = index >= 0 && index <= currentSize;
        Validate.isTrue(z, "Insert position out of bounds.");
        ArrayList<Node> nodes = new ArrayList(children);
        addChildren(index, (Node[]) nodes.toArray(new Node[nodes.size()]));
        return this;
    }

    public Element insertChildren(int index, Node... children) {
        Validate.notNull(children, "Children collection to be inserted must not be null.");
        int currentSize = childNodeSize();
        if (index < 0) {
            index += currentSize + 1;
        }
        boolean z = index >= 0 && index <= currentSize;
        Validate.isTrue(z, "Insert position out of bounds.");
        addChildren(index, children);
        return this;
    }

    public Element appendElement(String tagName) {
        Element child = new Element(Tag.valueOf(tagName), baseUri());
        appendChild(child);
        return child;
    }

    public Element prependElement(String tagName) {
        Element child = new Element(Tag.valueOf(tagName), baseUri());
        prependChild(child);
        return child;
    }

    public Element appendText(String text) {
        Validate.notNull(text);
        appendChild(new TextNode(text));
        return this;
    }

    public Element prependText(String text) {
        Validate.notNull(text);
        prependChild(new TextNode(text));
        return this;
    }

    public Element append(String html) {
        Validate.notNull(html);
        List<Node> nodes = Parser.parseFragment(html, this, baseUri());
        addChildren((Node[]) nodes.toArray(new Node[nodes.size()]));
        return this;
    }

    public Element prepend(String html) {
        Validate.notNull(html);
        List<Node> nodes = Parser.parseFragment(html, this, baseUri());
        addChildren(0, (Node[]) nodes.toArray(new Node[nodes.size()]));
        return this;
    }

    public Element before(String html) {
        return (Element) super.before(html);
    }

    public Element before(Node node) {
        return (Element) super.before(node);
    }

    public Element after(String html) {
        return (Element) super.after(html);
    }

    public Element after(Node node) {
        return (Element) super.after(node);
    }

    public Element empty() {
        this.childNodes.clear();
        return this;
    }

    public Element wrap(String html) {
        return (Element) super.wrap(html);
    }

    public String cssSelector() {
        if (id().length() > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("#");
            stringBuilder.append(id());
            return stringBuilder.toString();
        }
        StringBuilder selector = new StringBuilder(tagName().replace(':', '|'));
        String classes = StringUtil.join(classNames(), ".");
        if (classes.length() > 0) {
            selector.append('.');
            selector.append(classes);
        }
        if (parent() != null) {
            if (!(parent() instanceof Document)) {
                selector.insert(0, " > ");
                if (parent().select(selector.toString()).size() > 1) {
                    selector.append(String.format(":nth-child(%d)", new Object[]{Integer.valueOf(elementSiblingIndex() + 1)}));
                }
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append(parent().cssSelector());
                stringBuilder2.append(selector.toString());
                return stringBuilder2.toString();
            }
        }
        return selector.toString();
    }

    public Elements siblingElements() {
        if (this.parentNode == null) {
            return new Elements(0);
        }
        List<Element> elements = parent().childElementsList();
        Elements siblings = new Elements(elements.size() - 1);
        for (Element el : elements) {
            if (el != this) {
                siblings.add(el);
            }
        }
        return siblings;
    }

    public Element nextElementSibling() {
        if (this.parentNode == null) {
            return null;
        }
        List<Element> siblings = parent().childElementsList();
        Integer index = Integer.valueOf(indexInList(this, siblings));
        Validate.notNull(index);
        if (siblings.size() > index.intValue() + 1) {
            return (Element) siblings.get(index.intValue() + 1);
        }
        return null;
    }

    public Element previousElementSibling() {
        if (this.parentNode == null) {
            return null;
        }
        List<Element> siblings = parent().childElementsList();
        Integer index = Integer.valueOf(indexInList(this, siblings));
        Validate.notNull(index);
        if (index.intValue() > 0) {
            return (Element) siblings.get(index.intValue() - 1);
        }
        return null;
    }

    public Element firstElementSibling() {
        List<Element> siblings = parent().childElementsList();
        return siblings.size() > 1 ? (Element) siblings.get(0) : null;
    }

    public int elementSiblingIndex() {
        if (parent() == null) {
            return 0;
        }
        return indexInList(this, parent().childElementsList());
    }

    public Element lastElementSibling() {
        List<Element> siblings = parent().childElementsList();
        return siblings.size() > 1 ? (Element) siblings.get(siblings.size() - 1) : null;
    }

    private static <E extends Element> int indexInList(Element search, List<E> elements) {
        for (int i = 0; i < elements.size(); i++) {
            if (elements.get(i) == search) {
                return i;
            }
        }
        return 0;
    }

    public Elements getElementsByTag(String tagName) {
        Validate.notEmpty(tagName);
        return Collector.collect(new Evaluator.Tag(Normalizer.normalize(tagName)), this);
    }

    public Element getElementById(String id) {
        Validate.notEmpty(id);
        Elements elements = Collector.collect(new Id(id), this);
        if (elements.size() > 0) {
            return (Element) elements.get(0);
        }
        return null;
    }

    public Elements getElementsByClass(String className) {
        Validate.notEmpty(className);
        return Collector.collect(new Class(className), this);
    }

    public Elements getElementsByAttribute(String key) {
        Validate.notEmpty(key);
        return Collector.collect(new Attribute(key.trim()), this);
    }

    public Elements getElementsByAttributeStarting(String keyPrefix) {
        Validate.notEmpty(keyPrefix);
        return Collector.collect(new AttributeStarting(keyPrefix.trim()), this);
    }

    public Elements getElementsByAttributeValue(String key, String value) {
        return Collector.collect(new AttributeWithValue(key, value), this);
    }

    public Elements getElementsByAttributeValueNot(String key, String value) {
        return Collector.collect(new AttributeWithValueNot(key, value), this);
    }

    public Elements getElementsByAttributeValueStarting(String key, String valuePrefix) {
        return Collector.collect(new AttributeWithValueStarting(key, valuePrefix), this);
    }

    public Elements getElementsByAttributeValueEnding(String key, String valueSuffix) {
        return Collector.collect(new AttributeWithValueEnding(key, valueSuffix), this);
    }

    public Elements getElementsByAttributeValueContaining(String key, String match) {
        return Collector.collect(new AttributeWithValueContaining(key, match), this);
    }

    public Elements getElementsByAttributeValueMatching(String key, Pattern pattern) {
        return Collector.collect(new AttributeWithValueMatching(key, pattern), this);
    }

    public Elements getElementsByAttributeValueMatching(String key, String regex) {
        try {
            return getElementsByAttributeValueMatching(key, Pattern.compile(regex));
        } catch (PatternSyntaxException e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Pattern syntax error: ");
            stringBuilder.append(regex);
            throw new IllegalArgumentException(stringBuilder.toString(), e);
        }
    }

    public Elements getElementsByIndexLessThan(int index) {
        return Collector.collect(new IndexLessThan(index), this);
    }

    public Elements getElementsByIndexGreaterThan(int index) {
        return Collector.collect(new IndexGreaterThan(index), this);
    }

    public Elements getElementsByIndexEquals(int index) {
        return Collector.collect(new IndexEquals(index), this);
    }

    public Elements getElementsContainingText(String searchText) {
        return Collector.collect(new ContainsText(searchText), this);
    }

    public Elements getElementsContainingOwnText(String searchText) {
        return Collector.collect(new ContainsOwnText(searchText), this);
    }

    public Elements getElementsMatchingText(Pattern pattern) {
        return Collector.collect(new Matches(pattern), this);
    }

    public Elements getElementsMatchingText(String regex) {
        try {
            return getElementsMatchingText(Pattern.compile(regex));
        } catch (PatternSyntaxException e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Pattern syntax error: ");
            stringBuilder.append(regex);
            throw new IllegalArgumentException(stringBuilder.toString(), e);
        }
    }

    public Elements getElementsMatchingOwnText(Pattern pattern) {
        return Collector.collect(new MatchesOwn(pattern), this);
    }

    public Elements getElementsMatchingOwnText(String regex) {
        try {
            return getElementsMatchingOwnText(Pattern.compile(regex));
        } catch (PatternSyntaxException e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Pattern syntax error: ");
            stringBuilder.append(regex);
            throw new IllegalArgumentException(stringBuilder.toString(), e);
        }
    }

    public Elements getAllElements() {
        return Collector.collect(new AllElements(), this);
    }

    public String text() {
        final StringBuilder accum = new StringBuilder();
        NodeTraversor.traverse(new NodeVisitor() {
            public void head(Node node, int depth) {
                if (node instanceof TextNode) {
                    Element.appendNormalisedText(accum, (TextNode) node);
                } else if (node instanceof Element) {
                    Element element = (Element) node;
                    if (accum.length() > 0) {
                        if (!element.isBlock()) {
                            if (!element.tag.getName().equals(TtmlNode.TAG_BR)) {
                                return;
                            }
                        }
                        if (!TextNode.lastCharIsWhitespace(accum)) {
                            accum.append(' ');
                        }
                    }
                }
            }

            public void tail(Node node, int depth) {
            }
        }, (Node) this);
        return accum.toString().trim();
    }

    public String wholeText() {
        final StringBuilder accum = new StringBuilder();
        NodeTraversor.traverse(new NodeVisitor() {
            public void head(Node node, int depth) {
                if (node instanceof TextNode) {
                    accum.append(((TextNode) node).getWholeText());
                }
            }

            public void tail(Node node, int depth) {
            }
        }, (Node) this);
        return accum.toString();
    }

    public String ownText() {
        StringBuilder sb = new StringBuilder();
        ownText(sb);
        return sb.toString().trim();
    }

    private void ownText(StringBuilder accum) {
        for (Node child : this.childNodes) {
            if (child instanceof TextNode) {
                appendNormalisedText(accum, (TextNode) child);
            } else if (child instanceof Element) {
                appendWhitespaceIfBr((Element) child, accum);
            }
        }
    }

    private static void appendNormalisedText(StringBuilder accum, TextNode textNode) {
        String text = textNode.getWholeText();
        if (preserveWhitespace(textNode.parentNode)) {
            accum.append(text);
        } else {
            StringUtil.appendNormalisedWhitespace(accum, text, TextNode.lastCharIsWhitespace(accum));
        }
    }

    private static void appendWhitespaceIfBr(Element element, StringBuilder accum) {
        if (element.tag.getName().equals(TtmlNode.TAG_BR) && !TextNode.lastCharIsWhitespace(accum)) {
            accum.append(StringUtils.SPACE);
        }
    }

    static boolean preserveWhitespace(Node node) {
        if (node != null && (node instanceof Element)) {
            Element el = (Element) node;
            int i = 0;
            while (!el.tag.preserveWhitespace()) {
                el = el.parent();
                i++;
                if (i < 6) {
                    if (el == null) {
                    }
                }
            }
            return true;
        }
        return false;
    }

    public Element text(String text) {
        Validate.notNull(text);
        empty();
        appendChild(new TextNode(text));
        return this;
    }

    public boolean hasText() {
        for (Node child : this.childNodes) {
            if (child instanceof TextNode) {
                if (!((TextNode) child).isBlank()) {
                    return true;
                }
            } else if (child instanceof Element) {
                if (((Element) child).hasText()) {
                    return true;
                }
            }
        }
        return false;
    }

    public String data() {
        StringBuilder sb = new StringBuilder();
        for (Node childNode : this.childNodes) {
            if (childNode instanceof DataNode) {
                sb.append(((DataNode) childNode).getWholeData());
            } else if (childNode instanceof Comment) {
                sb.append(((Comment) childNode).getData());
            } else if (childNode instanceof Element) {
                sb.append(((Element) childNode).data());
            }
        }
        return sb.toString();
    }

    public String className() {
        return attr("class").trim();
    }

    public Set<String> classNames() {
        Set<String> classNames = new LinkedHashSet(Arrays.asList(classSplit.split(className())));
        classNames.remove("");
        return classNames;
    }

    public Element classNames(Set<String> classNames) {
        Validate.notNull(classNames);
        if (classNames.isEmpty()) {
            attributes().remove("class");
        } else {
            attributes().put("class", StringUtil.join((Collection) classNames, StringUtils.SPACE));
        }
        return this;
    }

    public boolean hasClass(String className) {
        String classAttr = attributes().getIgnoreCase("class");
        int len = classAttr.length();
        int wantLen = className.length();
        if (len != 0) {
            if (len >= wantLen) {
                if (len == wantLen) {
                    return className.equalsIgnoreCase(classAttr);
                }
                boolean inClass = false;
                int start = 0;
                for (int i = 0; i < len; i++) {
                    if (Character.isWhitespace(classAttr.charAt(i))) {
                        if (inClass) {
                            if (i - start == wantLen && classAttr.regionMatches(true, start, className, 0, wantLen)) {
                                return true;
                            }
                            inClass = false;
                        }
                    } else if (!inClass) {
                        start = i;
                        inClass = true;
                    }
                }
                if (inClass && len - start == wantLen) {
                    return classAttr.regionMatches(true, start, className, 0, wantLen);
                }
                return false;
            }
        }
        return false;
    }

    public Element addClass(String className) {
        Validate.notNull(className);
        Set<String> classes = classNames();
        classes.add(className);
        classNames(classes);
        return this;
    }

    public Element removeClass(String className) {
        Validate.notNull(className);
        Set<String> classes = classNames();
        classes.remove(className);
        classNames(classes);
        return this;
    }

    public Element toggleClass(String className) {
        Validate.notNull(className);
        Set<String> classes = classNames();
        if (classes.contains(className)) {
            classes.remove(className);
        } else {
            classes.add(className);
        }
        classNames(classes);
        return this;
    }

    public String val() {
        if (tagName().equals("textarea")) {
            return text();
        }
        return attr("value");
    }

    public Element val(String value) {
        if (tagName().equals("textarea")) {
            text(value);
        } else {
            attr("value", value);
        }
        return this;
    }

    void outerHtmlHead(Appendable accum, int depth, OutputSettings out) throws IOException {
        if (out.prettyPrint() && (this.tag.formatAsBlock() || ((parent() != null && parent().tag().formatAsBlock()) || out.outline()))) {
            if (!(accum instanceof StringBuilder)) {
                indent(accum, depth, out);
            } else if (((StringBuilder) accum).length() > 0) {
                indent(accum, depth, out);
            }
        }
        accum.append(Typography.less).append(tagName());
        Attributes attributes = this.attributes;
        if (attributes != null) {
            attributes.html(accum, out);
        }
        if (!this.childNodes.isEmpty() || !this.tag.isSelfClosing()) {
            accum.append(Typography.greater);
        } else if (out.syntax() == Syntax.html && this.tag.isEmpty()) {
            accum.append(Typography.greater);
        } else {
            accum.append(" />");
        }
    }

    void outerHtmlTail(Appendable accum, int depth, OutputSettings out) throws IOException {
        if (this.childNodes.isEmpty()) {
            if (this.tag.isSelfClosing()) {
                return;
            }
        }
        if (out.prettyPrint() && !this.childNodes.isEmpty()) {
            if (!this.tag.formatAsBlock()) {
                if (out.outline()) {
                    if (this.childNodes.size() <= 1) {
                        if (this.childNodes.size() == 1 && !(this.childNodes.get(0) instanceof TextNode)) {
                        }
                    }
                }
            }
            indent(accum, depth, out);
        }
        accum.append("</").append(tagName()).append(Typography.greater);
    }

    public String html() {
        StringBuilder accum = StringUtil.stringBuilder();
        html(accum);
        return getOutputSettings().prettyPrint() ? accum.toString().trim() : accum.toString();
    }

    private void html(StringBuilder accum) {
        for (Node node : this.childNodes) {
            node.outerHtml(accum);
        }
    }

    public <T extends Appendable> T html(T appendable) {
        for (Node node : this.childNodes) {
            node.outerHtml(appendable);
        }
        return appendable;
    }

    public Element html(String html) {
        empty();
        append(html);
        return this;
    }

    public String toString() {
        return outerHtml();
    }

    public Element clone() {
        return (Element) super.clone();
    }

    public Element shallowClone() {
        return new Element(this.tag, this.baseUri, this.attributes);
    }

    protected Element doClone(Node parent) {
        Element clone = (Element) super.doClone(parent);
        Attributes attributes = this.attributes;
        clone.attributes = attributes != null ? attributes.clone() : null;
        clone.baseUri = this.baseUri;
        clone.childNodes = new NodeList(clone, this.childNodes.size());
        clone.childNodes.addAll(this.childNodes);
        return clone;
    }
}
