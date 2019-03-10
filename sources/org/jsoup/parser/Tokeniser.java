package org.jsoup.parser;

import java.util.Arrays;
import kotlin.text.Typography;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.text.RandomStringGenerator.Builder;
import org.jsoup.helper.StringUtil;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Entities;

final class Tokeniser {
    private static final char[] notCharRefCharsSorted = new char[]{'\t', '\n', CharUtils.CR, '\f', ' ', Typography.less, Typography.amp};
    static final char replacementChar = '�';
    Character charPending = new Character();
    private StringBuilder charsBuilder = new StringBuilder(1024);
    private String charsString = null;
    private final int[] codepointHolder = new int[1];
    Comment commentPending = new Comment();
    StringBuilder dataBuffer = new StringBuilder(1024);
    Doctype doctypePending = new Doctype();
    private Token emitPending;
    EndTag endPending = new EndTag();
    private final ParseErrorList errors;
    private boolean isEmitPending = false;
    private String lastStartTag;
    private final int[] multipointHolder = new int[2];
    private final CharacterReader reader;
    StartTag startPending = new StartTag();
    private TokeniserState state = TokeniserState.Data;
    Tag tagPending;

    static {
        Arrays.sort(notCharRefCharsSorted);
    }

    Tokeniser(CharacterReader reader, ParseErrorList errors) {
        this.reader = reader;
        this.errors = errors;
    }

    Token read() {
        while (!this.isEmitPending) {
            this.state.read(this, this.reader);
        }
        if (this.charsBuilder.length() > 0) {
            String str = this.charsBuilder.toString();
            StringBuilder stringBuilder = this.charsBuilder;
            stringBuilder.delete(0, stringBuilder.length());
            this.charsString = null;
            return this.charPending.data(str);
        }
        Token token = this.charsString;
        if (token != null) {
            token = this.charPending.data(token);
            this.charsString = null;
            return token;
        }
        this.isEmitPending = false;
        return this.emitPending;
    }

    void emit(Token token) {
        Validate.isFalse(this.isEmitPending, "There is an unread token pending!");
        this.emitPending = token;
        this.isEmitPending = true;
        if (token.type == TokenType.StartTag) {
            this.lastStartTag = ((StartTag) token).tagName;
        } else if (token.type == TokenType.EndTag) {
            if (((EndTag) token).attributes != null) {
                error("Attributes incorrectly present on end tag");
            }
        }
    }

    void emit(String str) {
        if (this.charsString == null) {
            this.charsString = str;
            return;
        }
        if (this.charsBuilder.length() == 0) {
            this.charsBuilder.append(this.charsString);
        }
        this.charsBuilder.append(str);
    }

    void emit(char[] chars) {
        emit(String.valueOf(chars));
    }

    void emit(int[] codepoints) {
        emit(new String(codepoints, 0, codepoints.length));
    }

    void emit(char c) {
        emit(String.valueOf(c));
    }

    TokeniserState getState() {
        return this.state;
    }

    void transition(TokeniserState state) {
        this.state = state;
    }

    void advanceTransition(TokeniserState state) {
        this.reader.advance();
        this.state = state;
    }

    int[] consumeCharacterReference(Character additionalAllowedCharacter, boolean inAttribute) {
        if (this.reader.isEmpty()) {
            return null;
        }
        if ((additionalAllowedCharacter != null && additionalAllowedCharacter.charValue() == this.reader.current()) || this.reader.matchesAnySorted(notCharRefCharsSorted)) {
            return null;
        }
        int[] codeRef = this.codepointHolder;
        this.reader.mark();
        int charval;
        if (this.reader.matchConsume("#")) {
            boolean isHexMode = this.reader.matchConsumeIgnoreCase("X");
            String numRef = isHexMode ? this.reader.consumeHexSequence() : this.reader.consumeDigitSequence();
            if (numRef.length() == 0) {
                characterReferenceError("numeric reference with no numerals");
                this.reader.rewindToMark();
                return null;
            }
            if (!this.reader.matchConsume(";")) {
                characterReferenceError("missing semicolon");
            }
            charval = -1;
            try {
                charval = Integer.valueOf(numRef, isHexMode ? 16 : 10).intValue();
            } catch (NumberFormatException e) {
            }
            if (charval != -1 && (charval < 55296 || charval > 57343)) {
                if (charval <= Builder.DEFAULT_MAXIMUM_CODE_POINT) {
                    codeRef[0] = charval;
                    return codeRef;
                }
            }
            characterReferenceError("character outside of valid range");
            codeRef[0] = 65533;
            return codeRef;
        }
        boolean found;
        String nameRef = this.reader.consumeLetterThenDigitSequence();
        boolean looksLegit = this.reader.matches(';');
        if (!Entities.isBaseNamedEntity(nameRef)) {
            if (!Entities.isNamedEntity(nameRef) || !looksLegit) {
                found = false;
                if (found) {
                    this.reader.rewindToMark();
                    if (looksLegit) {
                        characterReferenceError(String.format("invalid named referenece '%s'", new Object[]{nameRef}));
                    }
                    return null;
                } else if (inAttribute || !(this.reader.matchesLetter() || this.reader.matchesDigit() || this.reader.matchesAny('=', '-', '_'))) {
                    if (!this.reader.matchConsume(";")) {
                        characterReferenceError("missing semicolon");
                    }
                    charval = Entities.codepointsForName(nameRef, this.multipointHolder);
                    if (charval == 1) {
                        codeRef[0] = this.multipointHolder[0];
                        return codeRef;
                    } else if (charval == 2) {
                        return this.multipointHolder;
                    } else {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Unexpected characters returned for ");
                        stringBuilder.append(nameRef);
                        Validate.fail(stringBuilder.toString());
                        return this.multipointHolder;
                    }
                } else {
                    this.reader.rewindToMark();
                    return null;
                }
            }
        }
        found = true;
        if (found) {
            if (inAttribute) {
            }
            if (!this.reader.matchConsume(";")) {
                characterReferenceError("missing semicolon");
            }
            charval = Entities.codepointsForName(nameRef, this.multipointHolder);
            if (charval == 1) {
                codeRef[0] = this.multipointHolder[0];
                return codeRef;
            } else if (charval == 2) {
                return this.multipointHolder;
            } else {
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Unexpected characters returned for ");
                stringBuilder2.append(nameRef);
                Validate.fail(stringBuilder2.toString());
                return this.multipointHolder;
            }
        }
        this.reader.rewindToMark();
        if (looksLegit) {
            characterReferenceError(String.format("invalid named referenece '%s'", new Object[]{nameRef}));
        }
        return null;
    }

    Tag createTagPending(boolean start) {
        this.tagPending = start ? this.startPending.reset() : this.endPending.reset();
        return this.tagPending;
    }

    void emitTagPending() {
        this.tagPending.finaliseTag();
        emit(this.tagPending);
    }

    void createCommentPending() {
        this.commentPending.reset();
    }

    void emitCommentPending() {
        emit(this.commentPending);
    }

    void createDoctypePending() {
        this.doctypePending.reset();
    }

    void emitDoctypePending() {
        emit(this.doctypePending);
    }

    void createTempBuffer() {
        Token.reset(this.dataBuffer);
    }

    boolean isAppropriateEndTagToken() {
        return this.lastStartTag != null && this.tagPending.name().equalsIgnoreCase(this.lastStartTag);
    }

    String appropriateEndTagName() {
        return this.lastStartTag;
    }

    void error(TokeniserState state) {
        if (this.errors.canAddError()) {
            this.errors.add(new ParseError(this.reader.pos(), "Unexpected character '%s' in input state [%s]", Character.valueOf(this.reader.current()), state));
        }
    }

    void eofError(TokeniserState state) {
        if (this.errors.canAddError()) {
            this.errors.add(new ParseError(this.reader.pos(), "Unexpectedly reached end of file (EOF) in input state [%s]", state));
        }
    }

    private void characterReferenceError(String message) {
        if (this.errors.canAddError()) {
            this.errors.add(new ParseError(this.reader.pos(), "Invalid character reference: %s", message));
        }
    }

    void error(String errorMsg) {
        if (this.errors.canAddError()) {
            this.errors.add(new ParseError(this.reader.pos(), errorMsg));
        }
    }

    boolean currentNodeInHtmlNS() {
        return true;
    }

    String unescapeEntities(boolean inAttribute) {
        StringBuilder builder = StringUtil.stringBuilder();
        while (!this.reader.isEmpty()) {
            builder.append(this.reader.consumeTo((char) Typography.amp));
            if (this.reader.matches((char) Typography.amp)) {
                this.reader.consume();
                int[] c = consumeCharacterReference(null, inAttribute);
                if (c != null) {
                    if (c.length != 0) {
                        builder.appendCodePoint(c[0]);
                        if (c.length == 2) {
                            builder.appendCodePoint(c[1]);
                        }
                    }
                }
                builder.append(Typography.amp);
            }
        }
        return builder.toString();
    }
}
