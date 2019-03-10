package org.jsoup.parser;

import com.google.android.exoplayer2.text.ttml.TtmlNode;
import de.danoeh.antennapod.core.storage.PodDBAdapter;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jsoup.helper.StringUtil;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

public class HtmlTreeBuilder extends TreeBuilder {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    public static final int MaxScopeSearchDepth = 100;
    static final String[] TagSearchButton = new String[]{"button"};
    static final String[] TagSearchEndTags = new String[]{"dd", "dt", "li", "optgroup", "option", TtmlNode.TAG_P, "rp", "rt"};
    static final String[] TagSearchList = new String[]{"ol", "ul"};
    static final String[] TagSearchSelectScope = new String[]{"optgroup", "option"};
    static final String[] TagSearchSpecial = new String[]{"address", "applet", "area", "article", "aside", "base", "basefont", "bgsound", "blockquote", "body", TtmlNode.TAG_BR, "button", "caption", TtmlNode.CENTER, "col", "colgroup", "command", "dd", "details", "dir", TtmlNode.TAG_DIV, "dl", "dt", "embed", "fieldset", "figcaption", "figure", "footer", "form", "frame", "frameset", "h1", "h2", "h3", "h4", "h5", "h6", "head", "header", "hgroup", "hr", "html", "iframe", "img", "input", "isindex", "li", PodDBAdapter.KEY_LINK, "listing", "marquee", "menu", "meta", "nav", "noembed", "noframes", "noscript", "object", "ol", TtmlNode.TAG_P, "param", "plaintext", "pre", "script", "section", "select", TtmlNode.TAG_STYLE, "summary", "table", "tbody", "td", "textarea", "tfoot", "th", "thead", "title", "tr", "ul", "wbr", "xmp"};
    static final String[] TagSearchTableScope = new String[]{"html", "table"};
    static final String[] TagsSearchInScope = new String[]{"applet", "caption", "html", "marquee", "object", "table", "td", "th"};
    private boolean baseUriSetFromDoc;
    private Element contextElement;
    private EndTag emptyEnd;
    private FormElement formElement;
    private ArrayList<Element> formattingElements;
    private boolean fosterInserts;
    private boolean fragmentParsing;
    private boolean framesetOk;
    private Element headElement;
    private HtmlTreeBuilderState originalState;
    private List<String> pendingTableCharacters;
    private String[] specificScopeTarget = new String[]{null};
    private HtmlTreeBuilderState state;

    public /* bridge */ /* synthetic */ boolean processStartTag(String str, Attributes attributes) {
        return super.processStartTag(str, attributes);
    }

    HtmlTreeBuilder() {
    }

    ParseSettings defaultSettings() {
        return ParseSettings.htmlDefault;
    }

    protected void initialiseParse(Reader input, String baseUri, ParseErrorList errors, ParseSettings settings) {
        super.initialiseParse(input, baseUri, errors, settings);
        this.state = HtmlTreeBuilderState.Initial;
        this.originalState = null;
        this.baseUriSetFromDoc = false;
        this.headElement = null;
        this.formElement = null;
        this.contextElement = null;
        this.formattingElements = new ArrayList();
        this.pendingTableCharacters = new ArrayList();
        this.emptyEnd = new EndTag();
        this.framesetOk = true;
        this.fosterInserts = false;
        this.fragmentParsing = false;
    }

    List<Node> parseFragment(String inputFragment, Element context, String baseUri, ParseErrorList errors, ParseSettings settings) {
        this.state = HtmlTreeBuilderState.Initial;
        initialiseParse(new StringReader(inputFragment), baseUri, errors, settings);
        this.contextElement = context;
        this.fragmentParsing = true;
        Element root = null;
        if (context != null) {
            if (context.ownerDocument() != null) {
                this.doc.quirksMode(context.ownerDocument().quirksMode());
            }
            String contextTag = context.tagName();
            if (StringUtil.in(contextTag, "title", "textarea")) {
                this.tokeniser.transition(TokeniserState.Rcdata);
            } else {
                if (StringUtil.in(contextTag, "iframe", "noembed", "noframes", TtmlNode.TAG_STYLE, "xmp")) {
                    this.tokeniser.transition(TokeniserState.Rawtext);
                } else if (contextTag.equals("script")) {
                    this.tokeniser.transition(TokeniserState.ScriptData);
                } else if (contextTag.equals("noscript")) {
                    this.tokeniser.transition(TokeniserState.Data);
                } else if (contextTag.equals("plaintext")) {
                    this.tokeniser.transition(TokeniserState.Data);
                } else {
                    this.tokeniser.transition(TokeniserState.Data);
                }
            }
            root = new Element(Tag.valueOf("html", settings), baseUri);
            this.doc.appendChild(root);
            this.stack.add(root);
            resetInsertionMode();
            Elements contextChain = context.parents();
            contextChain.add(0, context);
            Iterator it = contextChain.iterator();
            while (it.hasNext()) {
                Element parent = (Element) it.next();
                if (parent instanceof FormElement) {
                    this.formElement = (FormElement) parent;
                    break;
                }
            }
        }
        runParser();
        if (context != null) {
            return root.childNodes();
        }
        return this.doc.childNodes();
    }

    protected boolean process(Token token) {
        this.currentToken = token;
        return this.state.process(token, this);
    }

    boolean process(Token token, HtmlTreeBuilderState state) {
        this.currentToken = token;
        return state.process(token, this);
    }

    void transition(HtmlTreeBuilderState state) {
        this.state = state;
    }

    HtmlTreeBuilderState state() {
        return this.state;
    }

    void markInsertionMode() {
        this.originalState = this.state;
    }

    HtmlTreeBuilderState originalState() {
        return this.originalState;
    }

    void framesetOk(boolean framesetOk) {
        this.framesetOk = framesetOk;
    }

    boolean framesetOk() {
        return this.framesetOk;
    }

    Document getDocument() {
        return this.doc;
    }

    String getBaseUri() {
        return this.baseUri;
    }

    void maybeSetBaseUri(Element base) {
        if (!this.baseUriSetFromDoc) {
            String href = base.absUrl("href");
            if (href.length() != 0) {
                this.baseUri = href;
                this.baseUriSetFromDoc = true;
                this.doc.setBaseUri(href);
            }
        }
    }

    boolean isFragmentParsing() {
        return this.fragmentParsing;
    }

    void error(HtmlTreeBuilderState state) {
        if (this.errors.canAddError()) {
            this.errors.add(new ParseError(this.reader.pos(), "Unexpected token [%s] when in state [%s]", this.currentToken.tokenType(), state));
        }
    }

    Element insert(StartTag startTag) {
        if (startTag.isSelfClosing()) {
            Element el = insertEmpty(startTag);
            this.stack.add(el);
            this.tokeniser.transition(TokeniserState.Data);
            this.tokeniser.emit(this.emptyEnd.reset().name(el.tagName()));
            return el;
        }
        el = new Element(Tag.valueOf(startTag.name(), this.settings), this.baseUri, this.settings.normalizeAttributes(startTag.attributes));
        insert(el);
        return el;
    }

    Element insertStartTag(String startTagName) {
        Element el = new Element(Tag.valueOf(startTagName, this.settings), this.baseUri);
        insert(el);
        return el;
    }

    void insert(Element el) {
        insertNode(el);
        this.stack.add(el);
    }

    Element insertEmpty(StartTag startTag) {
        Tag tag = Tag.valueOf(startTag.name(), this.settings);
        Element el = new Element(tag, this.baseUri, startTag.attributes);
        insertNode(el);
        if (startTag.isSelfClosing()) {
            if (!tag.isKnownTag()) {
                tag.setSelfClosing();
            } else if (!tag.isEmpty()) {
                this.tokeniser.error("Tag cannot be self closing; not a void tag");
            }
        }
        return el;
    }

    FormElement insertForm(StartTag startTag, boolean onStack) {
        FormElement el = new FormElement(Tag.valueOf(startTag.name(), this.settings), this.baseUri, startTag.attributes);
        setFormElement(el);
        insertNode(el);
        if (onStack) {
            this.stack.add(el);
        }
        return el;
    }

    void insert(Comment commentToken) {
        insertNode(new Comment(commentToken.getData()));
    }

    void insert(Character characterToken) {
        Node node;
        String tagName = currentElement().tagName();
        if (!tagName.equals("script")) {
            if (!tagName.equals(TtmlNode.TAG_STYLE)) {
                node = new TextNode(characterToken.getData());
                currentElement().appendChild(node);
            }
        }
        node = new DataNode(characterToken.getData());
        currentElement().appendChild(node);
    }

    private void insertNode(Node node) {
        if (this.stack.size() == 0) {
            this.doc.appendChild(node);
        } else if (isFosterInserts()) {
            insertInFosterParent(node);
        } else {
            currentElement().appendChild(node);
        }
        if ((node instanceof Element) && ((Element) node).tag().isFormListed()) {
            FormElement formElement = this.formElement;
            if (formElement != null) {
                formElement.addElement((Element) node);
            }
        }
    }

    Element pop() {
        return (Element) this.stack.remove(this.stack.size() - 1);
    }

    void push(Element element) {
        this.stack.add(element);
    }

    ArrayList<Element> getStack() {
        return this.stack;
    }

    boolean onStack(Element el) {
        return isElementInQueue(this.stack, el);
    }

    private boolean isElementInQueue(ArrayList<Element> queue, Element element) {
        for (int pos = queue.size() - 1; pos >= 0; pos--) {
            if (((Element) queue.get(pos)) == element) {
                return true;
            }
        }
        return false;
    }

    Element getFromStack(String elName) {
        for (int pos = this.stack.size() - 1; pos >= 0; pos--) {
            Element next = (Element) this.stack.get(pos);
            if (next.nodeName().equals(elName)) {
                return next;
            }
        }
        return null;
    }

    boolean removeFromStack(Element el) {
        for (int pos = this.stack.size() - 1; pos >= 0; pos--) {
            if (((Element) this.stack.get(pos)) == el) {
                this.stack.remove(pos);
                return true;
            }
        }
        return false;
    }

    void popStackToClose(String elName) {
        int pos = this.stack.size() - 1;
        while (pos >= 0) {
            Element next = (Element) this.stack.get(pos);
            this.stack.remove(pos);
            if (!next.nodeName().equals(elName)) {
                pos--;
            } else {
                return;
            }
        }
    }

    void popStackToClose(String... elNames) {
        int pos = this.stack.size() - 1;
        while (pos >= 0) {
            Element next = (Element) this.stack.get(pos);
            this.stack.remove(pos);
            if (!StringUtil.inSorted(next.nodeName(), elNames)) {
                pos--;
            } else {
                return;
            }
        }
    }

    void popStackToBefore(String elName) {
        int pos = this.stack.size() - 1;
        while (pos >= 0) {
            if (!((Element) this.stack.get(pos)).nodeName().equals(elName)) {
                this.stack.remove(pos);
                pos--;
            } else {
                return;
            }
        }
    }

    void clearStackToTableContext() {
        clearStackToContext("table");
    }

    void clearStackToTableBodyContext() {
        clearStackToContext("tbody", "tfoot", "thead", "template");
    }

    void clearStackToTableRowContext() {
        clearStackToContext("tr", "template");
    }

    private void clearStackToContext(String... nodeNames) {
        int pos = this.stack.size() - 1;
        while (pos >= 0) {
            Element next = (Element) this.stack.get(pos);
            if (!StringUtil.in(next.nodeName(), nodeNames)) {
                if (!next.nodeName().equals("html")) {
                    this.stack.remove(pos);
                    pos--;
                } else {
                    return;
                }
            }
            return;
        }
    }

    Element aboveOnStack(Element el) {
        for (int pos = this.stack.size() - 1; pos >= 0; pos--) {
            if (((Element) this.stack.get(pos)) == el) {
                return (Element) this.stack.get(pos - 1);
            }
        }
        return null;
    }

    void insertOnStackAfter(Element after, Element in) {
        int i = this.stack.lastIndexOf(after);
        Validate.isTrue(i != -1);
        this.stack.add(i + 1, in);
    }

    void replaceOnStack(Element out, Element in) {
        replaceInQueue(this.stack, out, in);
    }

    private void replaceInQueue(ArrayList<Element> queue, Element out, Element in) {
        int i = queue.lastIndexOf(out);
        Validate.isTrue(i != -1);
        queue.set(i, in);
    }

    void resetInsertionMode() {
        boolean last = false;
        int pos = this.stack.size() - 1;
        while (pos >= 0) {
            Element node = (Element) this.stack.get(pos);
            if (pos == 0) {
                last = true;
                node = this.contextElement;
            }
            String name = node.nodeName();
            if ("select".equals(name)) {
                transition(HtmlTreeBuilderState.InSelect);
                return;
            }
            if (!"td".equals(name)) {
                if (!"th".equals(name) || last) {
                    if ("tr".equals(name)) {
                        transition(HtmlTreeBuilderState.InRow);
                        return;
                    }
                    if (!("tbody".equals(name) || "thead".equals(name))) {
                        if (!"tfoot".equals(name)) {
                            if ("caption".equals(name)) {
                                transition(HtmlTreeBuilderState.InCaption);
                                return;
                            } else if ("colgroup".equals(name)) {
                                transition(HtmlTreeBuilderState.InColumnGroup);
                                return;
                            } else if ("table".equals(name)) {
                                transition(HtmlTreeBuilderState.InTable);
                                return;
                            } else if ("head".equals(name)) {
                                transition(HtmlTreeBuilderState.InBody);
                                return;
                            } else if ("body".equals(name)) {
                                transition(HtmlTreeBuilderState.InBody);
                                return;
                            } else if ("frameset".equals(name)) {
                                transition(HtmlTreeBuilderState.InFrameset);
                                return;
                            } else if ("html".equals(name)) {
                                transition(HtmlTreeBuilderState.BeforeHead);
                                return;
                            } else if (last) {
                                transition(HtmlTreeBuilderState.InBody);
                                return;
                            } else {
                                pos--;
                            }
                        }
                    }
                    transition(HtmlTreeBuilderState.InTableBody);
                    return;
                }
            }
            transition(HtmlTreeBuilderState.InCell);
            return;
        }
    }

    private boolean inSpecificScope(String targetName, String[] baseTypes, String[] extraTypes) {
        String[] strArr = this.specificScopeTarget;
        strArr[0] = targetName;
        return inSpecificScope(strArr, baseTypes, extraTypes);
    }

    private boolean inSpecificScope(String[] targetNames, String[] baseTypes, String[] extraTypes) {
        int bottom = this.stack.size() - 1;
        int top = bottom > 100 ? bottom - 100 : 0;
        for (int pos = bottom; pos >= top; pos--) {
            String elName = ((Element) this.stack.get(pos)).nodeName();
            if (StringUtil.inSorted(elName, targetNames)) {
                return true;
            }
            if (StringUtil.inSorted(elName, baseTypes)) {
                return false;
            }
            if (extraTypes != null && StringUtil.inSorted(elName, extraTypes)) {
                return false;
            }
        }
        return false;
    }

    boolean inScope(String[] targetNames) {
        return inSpecificScope(targetNames, TagsSearchInScope, null);
    }

    boolean inScope(String targetName) {
        return inScope(targetName, null);
    }

    boolean inScope(String targetName, String[] extras) {
        return inSpecificScope(targetName, TagsSearchInScope, extras);
    }

    boolean inListItemScope(String targetName) {
        return inScope(targetName, TagSearchList);
    }

    boolean inButtonScope(String targetName) {
        return inScope(targetName, TagSearchButton);
    }

    boolean inTableScope(String targetName) {
        return inSpecificScope(targetName, TagSearchTableScope, null);
    }

    boolean inSelectScope(String targetName) {
        for (int pos = this.stack.size() - 1; pos >= 0; pos--) {
            String elName = ((Element) this.stack.get(pos)).nodeName();
            if (elName.equals(targetName)) {
                return true;
            }
            if (!StringUtil.inSorted(elName, TagSearchSelectScope)) {
                return false;
            }
        }
        Validate.fail("Should not be reachable");
        return false;
    }

    void setHeadElement(Element headElement) {
        this.headElement = headElement;
    }

    Element getHeadElement() {
        return this.headElement;
    }

    boolean isFosterInserts() {
        return this.fosterInserts;
    }

    void setFosterInserts(boolean fosterInserts) {
        this.fosterInserts = fosterInserts;
    }

    FormElement getFormElement() {
        return this.formElement;
    }

    void setFormElement(FormElement formElement) {
        this.formElement = formElement;
    }

    void newPendingTableCharacters() {
        this.pendingTableCharacters = new ArrayList();
    }

    List<String> getPendingTableCharacters() {
        return this.pendingTableCharacters;
    }

    void setPendingTableCharacters(List<String> pendingTableCharacters) {
        this.pendingTableCharacters = pendingTableCharacters;
    }

    void generateImpliedEndTags(String excludeTag) {
        while (excludeTag != null && !currentElement().nodeName().equals(excludeTag)) {
            if (StringUtil.inSorted(currentElement().nodeName(), TagSearchEndTags)) {
                pop();
            } else {
                return;
            }
        }
    }

    void generateImpliedEndTags() {
        generateImpliedEndTags(null);
    }

    boolean isSpecial(Element el) {
        return StringUtil.inSorted(el.nodeName(), TagSearchSpecial);
    }

    Element lastFormattingElement() {
        if (this.formattingElements.size() <= 0) {
            return null;
        }
        ArrayList arrayList = this.formattingElements;
        return (Element) arrayList.get(arrayList.size() - 1);
    }

    Element removeLastFormattingElement() {
        int size = this.formattingElements.size();
        if (size > 0) {
            return (Element) this.formattingElements.remove(size - 1);
        }
        return null;
    }

    void pushActiveFormattingElements(Element in) {
        int numSeen = 0;
        for (int pos = this.formattingElements.size() - 1; pos >= 0; pos--) {
            Element el = (Element) this.formattingElements.get(pos);
            if (el == null) {
                break;
            }
            if (isSameFormattingElement(in, el)) {
                numSeen++;
            }
            if (numSeen == 3) {
                this.formattingElements.remove(pos);
                break;
            }
        }
        this.formattingElements.add(in);
    }

    private boolean isSameFormattingElement(Element a, Element b) {
        if (a.nodeName().equals(b.nodeName())) {
            if (a.attributes().equals(b.attributes())) {
                return true;
            }
        }
        return false;
    }

    void reconstructFormattingElements() {
        Element last = lastFormattingElement();
        if (last != null) {
            if (!onStack(last)) {
                Element entry = last;
                int size = this.formattingElements.size();
                int pos = size - 1;
                boolean skip = false;
                while (pos != 0) {
                    pos--;
                    entry = (Element) this.formattingElements.get(pos);
                    if (entry == null) {
                        break;
                    } else if (onStack(entry)) {
                        break;
                    }
                }
                skip = true;
                while (true) {
                    if (!skip) {
                        pos++;
                        entry = (Element) this.formattingElements.get(pos);
                    }
                    Validate.notNull(entry);
                    skip = false;
                    Element newEl = insertStartTag(entry.nodeName());
                    newEl.attributes().addAll(entry.attributes());
                    this.formattingElements.set(pos, newEl);
                    if (pos == size - 1) {
                        return;
                    }
                }
            }
        }
    }

    void clearFormattingElementsToLastMarker() {
        while (!this.formattingElements.isEmpty()) {
            if (removeLastFormattingElement() == null) {
                return;
            }
        }
    }

    void removeFromActiveFormattingElements(Element el) {
        for (int pos = this.formattingElements.size() - 1; pos >= 0; pos--) {
            if (((Element) this.formattingElements.get(pos)) == el) {
                this.formattingElements.remove(pos);
                return;
            }
        }
    }

    boolean isInActiveFormattingElements(Element el) {
        return isElementInQueue(this.formattingElements, el);
    }

    Element getActiveFormattingElement(String nodeName) {
        int pos = this.formattingElements.size() - 1;
        while (pos >= 0) {
            Element next = (Element) this.formattingElements.get(pos);
            if (next == null) {
                break;
            } else if (next.nodeName().equals(nodeName)) {
                return next;
            } else {
                pos--;
            }
        }
        return null;
    }

    void replaceActiveFormattingElement(Element out, Element in) {
        replaceInQueue(this.formattingElements, out, in);
    }

    void insertMarkerToFormattingElements() {
        this.formattingElements.add(null);
    }

    void insertInFosterParent(Node in) {
        Element fosterParent;
        Element lastTable = getFromStack("table");
        boolean isLastTableParent = false;
        if (lastTable == null) {
            fosterParent = (Element) this.stack.get(0);
        } else if (lastTable.parent() != null) {
            fosterParent = lastTable.parent();
            isLastTableParent = true;
        } else {
            fosterParent = aboveOnStack(lastTable);
        }
        if (isLastTableParent) {
            Validate.notNull(lastTable);
            lastTable.before(in);
            return;
        }
        fosterParent.appendChild(in);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("TreeBuilder{currentToken=");
        stringBuilder.append(this.currentToken);
        stringBuilder.append(", state=");
        stringBuilder.append(this.state);
        stringBuilder.append(", currentElement=");
        stringBuilder.append(currentElement());
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
