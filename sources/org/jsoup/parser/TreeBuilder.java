package org.jsoup.parser;

import java.io.Reader;
import java.util.ArrayList;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

abstract class TreeBuilder {
    protected String baseUri;
    protected Token currentToken;
    protected Document doc;
    private EndTag end = new EndTag();
    protected ParseErrorList errors;
    CharacterReader reader;
    protected ParseSettings settings;
    protected ArrayList<Element> stack;
    private StartTag start = new StartTag();
    Tokeniser tokeniser;

    abstract ParseSettings defaultSettings();

    protected abstract boolean process(Token token);

    TreeBuilder() {
    }

    protected void initialiseParse(Reader input, String baseUri, ParseErrorList errors, ParseSettings settings) {
        Validate.notNull(input, "String input must not be null");
        Validate.notNull(baseUri, "BaseURI must not be null");
        this.doc = new Document(baseUri);
        this.settings = settings;
        this.reader = new CharacterReader(input);
        this.errors = errors;
        this.currentToken = null;
        this.tokeniser = new Tokeniser(this.reader, errors);
        this.stack = new ArrayList(32);
        this.baseUri = baseUri;
    }

    Document parse(Reader input, String baseUri, ParseErrorList errors, ParseSettings settings) {
        initialiseParse(input, baseUri, errors, settings);
        runParser();
        return this.doc;
    }

    protected void runParser() {
        while (true) {
            Token token = this.tokeniser.read();
            process(token);
            token.reset();
            if (token.type == TokenType.EOF) {
                return;
            }
        }
    }

    protected boolean processStartTag(String name) {
        Token token = this.currentToken;
        Token token2 = this.start;
        if (token == token2) {
            return process(new StartTag().name(name));
        }
        return process(token2.reset().name(name));
    }

    public boolean processStartTag(String name, Attributes attrs) {
        Token token = this.currentToken;
        Token token2 = this.start;
        if (token == token2) {
            return process(new StartTag().nameAttr(name, attrs));
        }
        token2.reset();
        this.start.nameAttr(name, attrs);
        return process(this.start);
    }

    protected boolean processEndTag(String name) {
        Token token = this.currentToken;
        Token token2 = this.end;
        if (token == token2) {
            return process(new EndTag().name(name));
        }
        return process(token2.reset().name(name));
    }

    protected Element currentElement() {
        int size = this.stack.size();
        return size > 0 ? (Element) this.stack.get(size - 1) : null;
    }
}
