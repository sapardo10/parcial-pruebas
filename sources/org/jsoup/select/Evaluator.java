package org.jsoup.select;

import java.util.Iterator;
import java.util.regex.Pattern;
import org.jsoup.helper.Validate;
import org.jsoup.internal.Normalizer;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.DocumentType;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.PseudoTextElement;
import org.jsoup.nodes.TextNode;
import org.jsoup.nodes.XmlDeclaration;

public abstract class Evaluator {

    public static final class AllElements extends Evaluator {
        public boolean matches(Element root, Element element) {
            return true;
        }

        public String toString() {
            return "*";
        }
    }

    public static final class Attribute extends Evaluator {
        private String key;

        public Attribute(String key) {
            this.key = key;
        }

        public boolean matches(Element root, Element element) {
            return element.hasAttr(this.key);
        }

        public String toString() {
            return String.format("[%s]", new Object[]{this.key});
        }
    }

    public static abstract class AttributeKeyPair extends Evaluator {
        String key;
        String value;

        public AttributeKeyPair(String key, String value) {
            Validate.notEmpty(key);
            Validate.notEmpty(value);
            this.key = Normalizer.normalize(key);
            if (value.startsWith("\"")) {
                if (value.endsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                    this.value = Normalizer.normalize(value);
                }
            }
            if (!(value.startsWith("'") && value.endsWith("'"))) {
                this.value = Normalizer.normalize(value);
            }
            value = value.substring(1, value.length() - 1);
            this.value = Normalizer.normalize(value);
        }
    }

    public static final class AttributeStarting extends Evaluator {
        private String keyPrefix;

        public AttributeStarting(String keyPrefix) {
            Validate.notEmpty(keyPrefix);
            this.keyPrefix = Normalizer.lowerCase(keyPrefix);
        }

        public boolean matches(Element root, Element element) {
            for (org.jsoup.nodes.Attribute attribute : element.attributes().asList()) {
                if (Normalizer.lowerCase(attribute.getKey()).startsWith(this.keyPrefix)) {
                    return true;
                }
            }
            return false;
        }

        public String toString() {
            return String.format("[^%s]", new Object[]{this.keyPrefix});
        }
    }

    public static final class AttributeWithValueMatching extends Evaluator {
        String key;
        Pattern pattern;

        public AttributeWithValueMatching(String key, Pattern pattern) {
            this.key = Normalizer.normalize(key);
            this.pattern = pattern;
        }

        public boolean matches(Element root, Element element) {
            return element.hasAttr(this.key) && this.pattern.matcher(element.attr(this.key)).find();
        }

        public String toString() {
            return String.format("[%s~=%s]", new Object[]{this.key, this.pattern.toString()});
        }
    }

    public static final class Class extends Evaluator {
        private String className;

        public Class(String className) {
            this.className = className;
        }

        public boolean matches(Element root, Element element) {
            return element.hasClass(this.className);
        }

        public String toString() {
            return String.format(".%s", new Object[]{this.className});
        }
    }

    public static final class ContainsData extends Evaluator {
        private String searchText;

        public ContainsData(String searchText) {
            this.searchText = Normalizer.lowerCase(searchText);
        }

        public boolean matches(Element root, Element element) {
            return Normalizer.lowerCase(element.data()).contains(this.searchText);
        }

        public String toString() {
            return String.format(":containsData(%s)", new Object[]{this.searchText});
        }
    }

    public static final class ContainsOwnText extends Evaluator {
        private String searchText;

        public ContainsOwnText(String searchText) {
            this.searchText = Normalizer.lowerCase(searchText);
        }

        public boolean matches(Element root, Element element) {
            return Normalizer.lowerCase(element.ownText()).contains(this.searchText);
        }

        public String toString() {
            return String.format(":containsOwn(%s)", new Object[]{this.searchText});
        }
    }

    public static final class ContainsText extends Evaluator {
        private String searchText;

        public ContainsText(String searchText) {
            this.searchText = Normalizer.lowerCase(searchText);
        }

        public boolean matches(Element root, Element element) {
            return Normalizer.lowerCase(element.text()).contains(this.searchText);
        }

        public String toString() {
            return String.format(":contains(%s)", new Object[]{this.searchText});
        }
    }

    public static abstract class CssNthEvaluator extends Evaluator {
        /* renamed from: a */
        protected final int f81a;
        /* renamed from: b */
        protected final int f82b;

        protected abstract int calculatePosition(Element element, Element element2);

        protected abstract String getPseudoClass();

        public CssNthEvaluator(int a, int b) {
            this.f81a = a;
            this.f82b = b;
        }

        public CssNthEvaluator(int b) {
            this(0, b);
        }

        public boolean matches(Element root, Element element) {
            Element p = element.parent();
            boolean z = false;
            if (p != null) {
                if (!(p instanceof Document)) {
                    int pos = calculatePosition(root, element);
                    int i = this.f81a;
                    if (i == 0) {
                        if (pos == this.f82b) {
                            z = true;
                        }
                        return z;
                    }
                    int i2 = this.f82b;
                    if ((pos - i2) * i >= 0 && (pos - i2) % i == 0) {
                        z = true;
                    }
                    return z;
                }
            }
            return false;
        }

        public String toString() {
            if (this.f81a == 0) {
                return String.format(":%s(%d)", new Object[]{getPseudoClass(), Integer.valueOf(this.f82b)});
            } else if (this.f82b == 0) {
                return String.format(":%s(%dn)", new Object[]{getPseudoClass(), Integer.valueOf(this.f81a)});
            } else {
                return String.format(":%s(%dn%+d)", new Object[]{getPseudoClass(), Integer.valueOf(this.f81a), Integer.valueOf(this.f82b)});
            }
        }
    }

    public static final class Id extends Evaluator {
        private String id;

        public Id(String id) {
            this.id = id;
        }

        public boolean matches(Element root, Element element) {
            return this.id.equals(element.id());
        }

        public String toString() {
            return String.format("#%s", new Object[]{this.id});
        }
    }

    public static abstract class IndexEvaluator extends Evaluator {
        int index;

        public IndexEvaluator(int index) {
            this.index = index;
        }
    }

    public static final class IsEmpty extends Evaluator {
        public boolean matches(Element root, Element element) {
            for (Node n : element.childNodes()) {
                if (!(n instanceof Comment) && !(n instanceof XmlDeclaration) && !(n instanceof DocumentType)) {
                    return false;
                }
            }
            return true;
        }

        public String toString() {
            return ":empty";
        }
    }

    public static final class IsFirstChild extends Evaluator {
        public boolean matches(Element root, Element element) {
            Element p = element.parent();
            return (p == null || (p instanceof Document) || element.elementSiblingIndex() != 0) ? false : true;
        }

        public String toString() {
            return ":first-child";
        }
    }

    public static final class IsLastChild extends Evaluator {
        public boolean matches(Element root, Element element) {
            Element p = element.parent();
            return (p == null || (p instanceof Document) || element.elementSiblingIndex() != p.children().size() - 1) ? false : true;
        }

        public String toString() {
            return ":last-child";
        }
    }

    public static final class IsOnlyChild extends Evaluator {
        public boolean matches(Element root, Element element) {
            Element p = element.parent();
            return (p == null || (p instanceof Document) || element.siblingElements().size() != 0) ? false : true;
        }

        public String toString() {
            return ":only-child";
        }
    }

    public static final class IsOnlyOfType extends Evaluator {
        public boolean matches(Element root, Element element) {
            Element p = element.parent();
            boolean z = false;
            if (p != null) {
                if (!(p instanceof Document)) {
                    int pos = 0;
                    Iterator it = p.children().iterator();
                    while (it.hasNext()) {
                        if (((Element) it.next()).tag().equals(element.tag())) {
                            pos++;
                        }
                    }
                    if (pos == 1) {
                        z = true;
                    }
                    return z;
                }
            }
            return false;
        }

        public String toString() {
            return ":only-of-type";
        }
    }

    public static final class IsRoot extends Evaluator {
        public boolean matches(Element root, Element element) {
            if (element == (root instanceof Document ? root.child(0) : root)) {
                return true;
            }
            return false;
        }

        public String toString() {
            return ":root";
        }
    }

    public static final class MatchText extends Evaluator {
        public boolean matches(Element root, Element element) {
            if (element instanceof PseudoTextElement) {
                return true;
            }
            for (TextNode textNode : element.textNodes()) {
                PseudoTextElement pel = new PseudoTextElement(org.jsoup.parser.Tag.valueOf(element.tagName()), element.baseUri(), element.attributes());
                textNode.replaceWith(pel);
                pel.appendChild(textNode);
            }
            return false;
        }

        public String toString() {
            return ":matchText";
        }
    }

    public static final class Matches extends Evaluator {
        private Pattern pattern;

        public Matches(Pattern pattern) {
            this.pattern = pattern;
        }

        public boolean matches(Element root, Element element) {
            return this.pattern.matcher(element.text()).find();
        }

        public String toString() {
            return String.format(":matches(%s)", new Object[]{this.pattern});
        }
    }

    public static final class MatchesOwn extends Evaluator {
        private Pattern pattern;

        public MatchesOwn(Pattern pattern) {
            this.pattern = pattern;
        }

        public boolean matches(Element root, Element element) {
            return this.pattern.matcher(element.ownText()).find();
        }

        public String toString() {
            return String.format(":matchesOwn(%s)", new Object[]{this.pattern});
        }
    }

    public static final class Tag extends Evaluator {
        private String tagName;

        public Tag(String tagName) {
            this.tagName = tagName;
        }

        public boolean matches(Element root, Element element) {
            return element.tagName().equalsIgnoreCase(this.tagName);
        }

        public String toString() {
            return String.format("%s", new Object[]{this.tagName});
        }
    }

    public static final class TagEndsWith extends Evaluator {
        private String tagName;

        public TagEndsWith(String tagName) {
            this.tagName = tagName;
        }

        public boolean matches(Element root, Element element) {
            return element.tagName().endsWith(this.tagName);
        }

        public String toString() {
            return String.format("%s", new Object[]{this.tagName});
        }
    }

    public static final class AttributeWithValue extends AttributeKeyPair {
        public AttributeWithValue(String key, String value) {
            super(key, value);
        }

        public boolean matches(Element root, Element element) {
            return element.hasAttr(this.key) && this.value.equalsIgnoreCase(element.attr(this.key).trim());
        }

        public String toString() {
            return String.format("[%s=%s]", new Object[]{this.key, this.value});
        }
    }

    public static final class AttributeWithValueContaining extends AttributeKeyPair {
        public AttributeWithValueContaining(String key, String value) {
            super(key, value);
        }

        public boolean matches(Element root, Element element) {
            return element.hasAttr(this.key) && Normalizer.lowerCase(element.attr(this.key)).contains(this.value);
        }

        public String toString() {
            return String.format("[%s*=%s]", new Object[]{this.key, this.value});
        }
    }

    public static final class AttributeWithValueEnding extends AttributeKeyPair {
        public AttributeWithValueEnding(String key, String value) {
            super(key, value);
        }

        public boolean matches(Element root, Element element) {
            return element.hasAttr(this.key) && Normalizer.lowerCase(element.attr(this.key)).endsWith(this.value);
        }

        public String toString() {
            return String.format("[%s$=%s]", new Object[]{this.key, this.value});
        }
    }

    public static final class AttributeWithValueNot extends AttributeKeyPair {
        public AttributeWithValueNot(String key, String value) {
            super(key, value);
        }

        public boolean matches(Element root, Element element) {
            return this.value.equalsIgnoreCase(element.attr(this.key)) ^ 1;
        }

        public String toString() {
            return String.format("[%s!=%s]", new Object[]{this.key, this.value});
        }
    }

    public static final class AttributeWithValueStarting extends AttributeKeyPair {
        public AttributeWithValueStarting(String key, String value) {
            super(key, value);
        }

        public boolean matches(Element root, Element element) {
            return element.hasAttr(this.key) && Normalizer.lowerCase(element.attr(this.key)).startsWith(this.value);
        }

        public String toString() {
            return String.format("[%s^=%s]", new Object[]{this.key, this.value});
        }
    }

    public static final class IndexEquals extends IndexEvaluator {
        public IndexEquals(int index) {
            super(index);
        }

        public boolean matches(Element root, Element element) {
            return element.elementSiblingIndex() == this.index;
        }

        public String toString() {
            return String.format(":eq(%d)", new Object[]{Integer.valueOf(this.index)});
        }
    }

    public static final class IndexGreaterThan extends IndexEvaluator {
        public IndexGreaterThan(int index) {
            super(index);
        }

        public boolean matches(Element root, Element element) {
            return element.elementSiblingIndex() > this.index;
        }

        public String toString() {
            return String.format(":gt(%d)", new Object[]{Integer.valueOf(this.index)});
        }
    }

    public static final class IndexLessThan extends IndexEvaluator {
        public IndexLessThan(int index) {
            super(index);
        }

        public boolean matches(Element root, Element element) {
            return root != element && element.elementSiblingIndex() < this.index;
        }

        public String toString() {
            return String.format(":lt(%d)", new Object[]{Integer.valueOf(this.index)});
        }
    }

    public static final class IsNthChild extends CssNthEvaluator {
        public IsNthChild(int a, int b) {
            super(a, b);
        }

        protected int calculatePosition(Element root, Element element) {
            return element.elementSiblingIndex() + 1;
        }

        protected String getPseudoClass() {
            return "nth-child";
        }
    }

    public static final class IsNthLastChild extends CssNthEvaluator {
        public IsNthLastChild(int a, int b) {
            super(a, b);
        }

        protected int calculatePosition(Element root, Element element) {
            return element.parent().children().size() - element.elementSiblingIndex();
        }

        protected String getPseudoClass() {
            return "nth-last-child";
        }
    }

    public static class IsNthLastOfType extends CssNthEvaluator {
        public IsNthLastOfType(int a, int b) {
            super(a, b);
        }

        protected int calculatePosition(Element root, Element element) {
            int pos = 0;
            Elements family = element.parent().children();
            for (int i = element.elementSiblingIndex(); i < family.size(); i++) {
                if (((Element) family.get(i)).tag().equals(element.tag())) {
                    pos++;
                }
            }
            return pos;
        }

        protected String getPseudoClass() {
            return "nth-last-of-type";
        }
    }

    public static class IsNthOfType extends CssNthEvaluator {
        public IsNthOfType(int a, int b) {
            super(a, b);
        }

        protected int calculatePosition(Element root, Element element) {
            int pos = 0;
            Iterator it = element.parent().children().iterator();
            while (it.hasNext()) {
                Element el = (Element) it.next();
                if (el.tag().equals(element.tag())) {
                    pos++;
                }
                if (el == element) {
                    break;
                }
            }
            return pos;
        }

        protected String getPseudoClass() {
            return "nth-of-type";
        }
    }

    public static final class IsFirstOfType extends IsNthOfType {
        public IsFirstOfType() {
            super(0, 1);
        }

        public String toString() {
            return ":first-of-type";
        }
    }

    public static final class IsLastOfType extends IsNthLastOfType {
        public IsLastOfType() {
            super(0, 1);
        }

        public String toString() {
            return ":last-of-type";
        }
    }

    public abstract boolean matches(Element element, Element element2);

    protected Evaluator() {
    }
}
