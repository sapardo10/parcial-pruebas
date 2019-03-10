package org.jsoup.parser;

import android.support.v4.internal.view.SupportMenu;
import kotlin.text.Typography;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.CharUtils;
import org.jsoup.nodes.DocumentType;

enum TokeniserState {
    Data {
        void read(Tokeniser t, CharacterReader r) {
            char current = r.current();
            if (current == '\u0000') {
                t.error((TokeniserState) this);
                t.emit(r.consume());
            } else if (current == Typography.amp) {
                t.advanceTransition(CharacterReferenceInData);
            } else if (current == Typography.less) {
                t.advanceTransition(TagOpen);
            } else if (current != TokeniserState.eof) {
                t.emit(r.consumeData());
            } else {
                t.emit(new EOF());
            }
        }
    },
    CharacterReferenceInData {
        void read(Tokeniser t, CharacterReader r) {
            TokeniserState.readCharRef(t, Data);
        }
    },
    Rcdata {
        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        void read(org.jsoup.parser.Tokeniser r3, org.jsoup.parser.CharacterReader r4) {
            /*
            r2 = this;
            r0 = r4.current();
            if (r0 == 0) goto L_0x0036;
        L_0x0006:
            r1 = 38;
            if (r0 == r1) goto L_0x0030;
        L_0x000a:
            r1 = 60;
            if (r0 == r1) goto L_0x002a;
        L_0x000e:
            r1 = 65535; // 0xffff float:9.1834E-41 double:3.23786E-319;
            if (r0 == r1) goto L_0x0021;
        L_0x0013:
            r0 = 3;
            r0 = new char[r0];
            r0 = {38, 60, 0};
            r0 = r4.consumeToAny(r0);
            r3.emit(r0);
            goto L_0x0043;
        L_0x0021:
            r0 = new org.jsoup.parser.Token$EOF;
            r0.<init>();
            r3.emit(r0);
            goto L_0x0043;
        L_0x002a:
            r0 = RcdataLessthanSign;
            r3.advanceTransition(r0);
            goto L_0x0043;
        L_0x0030:
            r0 = CharacterReferenceInRcdata;
            r3.advanceTransition(r0);
            goto L_0x0043;
        L_0x0036:
            r3.error(r2);
            r4.advance();
            r0 = 65533; // 0xfffd float:9.1831E-41 double:3.23776E-319;
            r3.emit(r0);
        L_0x0043:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.jsoup.parser.TokeniserState.3.read(org.jsoup.parser.Tokeniser, org.jsoup.parser.CharacterReader):void");
        }
    },
    CharacterReferenceInRcdata {
        void read(Tokeniser t, CharacterReader r) {
            TokeniserState.readCharRef(t, Rcdata);
        }
    },
    Rawtext {
        void read(Tokeniser t, CharacterReader r) {
            TokeniserState.readData(t, r, this, RawtextLessthanSign);
        }
    },
    ScriptData {
        void read(Tokeniser t, CharacterReader r) {
            TokeniserState.readData(t, r, this, ScriptDataLessthanSign);
        }
    },
    PLAINTEXT {
        void read(Tokeniser t, CharacterReader r) {
            char current = r.current();
            if (current == '\u0000') {
                t.error((TokeniserState) this);
                r.advance();
                t.emit((char) TokeniserState.replacementChar);
            } else if (current != TokeniserState.eof) {
                t.emit(r.consumeTo((char) null));
            } else {
                t.emit(new EOF());
            }
        }
    },
    TagOpen {
        void read(Tokeniser t, CharacterReader r) {
            char current = r.current();
            if (current == '!') {
                t.advanceTransition(MarkupDeclarationOpen);
            } else if (current == IOUtils.DIR_SEPARATOR_UNIX) {
                t.advanceTransition(EndTagOpen);
            } else if (current == '?') {
                t.advanceTransition(BogusComment);
            } else if (r.matchesLetter()) {
                t.createTagPending(true);
                t.transition(TagName);
            } else {
                t.error((TokeniserState) this);
                t.emit((char) Typography.less);
                t.transition(Data);
            }
        }
    },
    EndTagOpen {
        void read(Tokeniser t, CharacterReader r) {
            if (r.isEmpty()) {
                t.eofError(this);
                t.emit("</");
                t.transition(Data);
            } else if (r.matchesLetter()) {
                t.createTagPending(false);
                t.transition(TagName);
            } else if (r.matches((char) Typography.greater)) {
                t.error((TokeniserState) this);
                t.advanceTransition(Data);
            } else {
                t.error((TokeniserState) this);
                t.advanceTransition(BogusComment);
            }
        }
    },
    TagName {
        void read(Tokeniser t, CharacterReader r) {
            t.tagPending.appendTagName(r.consumeTagName());
            char c = r.consume();
            switch (c) {
                case '\u0000':
                    t.tagPending.appendTagName(TokeniserState.replacementStr);
                    return;
                case '\t':
                case '\n':
                case '\f':
                case '\r':
                case ' ':
                    t.transition(BeforeAttributeName);
                    return;
                case '/':
                    t.transition(SelfClosingStartTag);
                    return;
                case '>':
                    t.emitTagPending();
                    t.transition(Data);
                    return;
                case SupportMenu.USER_MASK /*65535*/:
                    t.eofError(this);
                    t.transition(Data);
                    return;
                default:
                    t.tagPending.appendTagName(c);
                    return;
            }
        }
    },
    RcdataLessthanSign {
        void read(Tokeniser t, CharacterReader r) {
            if (r.matches((char) IOUtils.DIR_SEPARATOR_UNIX)) {
                t.createTempBuffer();
                t.advanceTransition(RCDATAEndTagOpen);
                return;
            }
            if (r.matchesLetter() && t.appropriateEndTagName() != null) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("</");
                stringBuilder.append(t.appropriateEndTagName());
                if (!r.containsIgnoreCase(stringBuilder.toString())) {
                    t.tagPending = t.createTagPending(false).name(t.appropriateEndTagName());
                    t.emitTagPending();
                    r.unconsume();
                    t.transition(Data);
                    return;
                }
            }
            t.emit("<");
            t.transition(Rcdata);
        }
    },
    RCDATAEndTagOpen {
        void read(Tokeniser t, CharacterReader r) {
            if (r.matchesLetter()) {
                t.createTagPending(false);
                t.tagPending.appendTagName(r.current());
                t.dataBuffer.append(r.current());
                t.advanceTransition(RCDATAEndTagName);
                return;
            }
            t.emit("</");
            t.transition(Rcdata);
        }
    },
    RCDATAEndTagName {
        void read(Tokeniser t, CharacterReader r) {
            if (r.matchesLetter()) {
                String name = r.consumeLetterSequence();
                t.tagPending.appendTagName(name);
                t.dataBuffer.append(name);
                return;
            }
            switch (r.consume()) {
                case '\t':
                case '\n':
                case '\f':
                case '\r':
                case ' ':
                    if (!t.isAppropriateEndTagToken()) {
                        anythingElse(t, r);
                        break;
                    } else {
                        t.transition(BeforeAttributeName);
                        break;
                    }
                case '/':
                    if (!t.isAppropriateEndTagToken()) {
                        anythingElse(t, r);
                        break;
                    } else {
                        t.transition(SelfClosingStartTag);
                        break;
                    }
                case '>':
                    if (!t.isAppropriateEndTagToken()) {
                        anythingElse(t, r);
                        break;
                    }
                    t.emitTagPending();
                    t.transition(Data);
                    break;
                default:
                    anythingElse(t, r);
                    break;
            }
        }

        private void anythingElse(Tokeniser t, CharacterReader r) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("</");
            stringBuilder.append(t.dataBuffer.toString());
            t.emit(stringBuilder.toString());
            r.unconsume();
            t.transition(Rcdata);
        }
    },
    RawtextLessthanSign {
        void read(Tokeniser t, CharacterReader r) {
            if (r.matches((char) IOUtils.DIR_SEPARATOR_UNIX)) {
                t.createTempBuffer();
                t.advanceTransition(RawtextEndTagOpen);
                return;
            }
            t.emit((char) Typography.less);
            t.transition(Rawtext);
        }
    },
    RawtextEndTagOpen {
        void read(Tokeniser t, CharacterReader r) {
            TokeniserState.readEndTag(t, r, RawtextEndTagName, Rawtext);
        }
    },
    RawtextEndTagName {
        void read(Tokeniser t, CharacterReader r) {
            TokeniserState.handleDataEndTag(t, r, Rawtext);
        }
    },
    ScriptDataLessthanSign {
        void read(Tokeniser t, CharacterReader r) {
            char consume = r.consume();
            if (consume == '!') {
                t.emit("<!");
                t.transition(ScriptDataEscapeStart);
            } else if (consume != IOUtils.DIR_SEPARATOR_UNIX) {
                t.emit("<");
                r.unconsume();
                t.transition(ScriptData);
            } else {
                t.createTempBuffer();
                t.transition(ScriptDataEndTagOpen);
            }
        }
    },
    ScriptDataEndTagOpen {
        void read(Tokeniser t, CharacterReader r) {
            TokeniserState.readEndTag(t, r, ScriptDataEndTagName, ScriptData);
        }
    },
    ScriptDataEndTagName {
        void read(Tokeniser t, CharacterReader r) {
            TokeniserState.handleDataEndTag(t, r, ScriptData);
        }
    },
    ScriptDataEscapeStart {
        void read(Tokeniser t, CharacterReader r) {
            if (r.matches('-')) {
                t.emit('-');
                t.advanceTransition(ScriptDataEscapeStartDash);
                return;
            }
            t.transition(ScriptData);
        }
    },
    ScriptDataEscapeStartDash {
        void read(Tokeniser t, CharacterReader r) {
            if (r.matches('-')) {
                t.emit('-');
                t.advanceTransition(ScriptDataEscapedDashDash);
                return;
            }
            t.transition(ScriptData);
        }
    },
    ScriptDataEscaped {
        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        void read(org.jsoup.parser.Tokeniser r3, org.jsoup.parser.CharacterReader r4) {
            /*
            r2 = this;
            r0 = r4.isEmpty();
            if (r0 == 0) goto L_0x000f;
        L_0x0006:
            r3.eofError(r2);
            r0 = Data;
            r3.transition(r0);
            return;
        L_0x000f:
            r0 = r4.current();
            if (r0 == 0) goto L_0x003a;
        L_0x0015:
            r1 = 45;
            if (r0 == r1) goto L_0x0031;
        L_0x0019:
            r1 = 60;
            if (r0 == r1) goto L_0x002b;
        L_0x001d:
            r0 = 3;
            r0 = new char[r0];
            r0 = {45, 60, 0};
            r0 = r4.consumeToAny(r0);
            r3.emit(r0);
            goto L_0x0047;
        L_0x002b:
            r0 = ScriptDataEscapedLessthanSign;
            r3.advanceTransition(r0);
            goto L_0x0047;
        L_0x0031:
            r3.emit(r1);
            r0 = ScriptDataEscapedDash;
            r3.advanceTransition(r0);
            goto L_0x0047;
        L_0x003a:
            r3.error(r2);
            r4.advance();
            r0 = 65533; // 0xfffd float:9.1831E-41 double:3.23776E-319;
            r3.emit(r0);
        L_0x0047:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.jsoup.parser.TokeniserState.22.read(org.jsoup.parser.Tokeniser, org.jsoup.parser.CharacterReader):void");
        }
    },
    ScriptDataEscapedDash {
        void read(Tokeniser t, CharacterReader r) {
            if (r.isEmpty()) {
                t.eofError(this);
                t.transition(Data);
                return;
            }
            char c = r.consume();
            if (c == '\u0000') {
                t.error((TokeniserState) this);
                t.emit((char) TokeniserState.replacementChar);
                t.transition(ScriptDataEscaped);
            } else if (c == '-') {
                t.emit(c);
                t.transition(ScriptDataEscapedDashDash);
            } else if (c != Typography.less) {
                t.emit(c);
                t.transition(ScriptDataEscaped);
            } else {
                t.transition(ScriptDataEscapedLessthanSign);
            }
        }
    },
    ScriptDataEscapedDashDash {
        void read(Tokeniser t, CharacterReader r) {
            if (r.isEmpty()) {
                t.eofError(this);
                t.transition(Data);
                return;
            }
            char c = r.consume();
            if (c == '\u0000') {
                t.error((TokeniserState) this);
                t.emit((char) TokeniserState.replacementChar);
                t.transition(ScriptDataEscaped);
            } else if (c == '-') {
                t.emit(c);
            } else if (c == Typography.less) {
                t.transition(ScriptDataEscapedLessthanSign);
            } else if (c != Typography.greater) {
                t.emit(c);
                t.transition(ScriptDataEscaped);
            } else {
                t.emit(c);
                t.transition(ScriptData);
            }
        }
    },
    ScriptDataEscapedLessthanSign {
        void read(Tokeniser t, CharacterReader r) {
            if (r.matchesLetter()) {
                t.createTempBuffer();
                t.dataBuffer.append(r.current());
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("<");
                stringBuilder.append(r.current());
                t.emit(stringBuilder.toString());
                t.advanceTransition(ScriptDataDoubleEscapeStart);
            } else if (r.matches((char) IOUtils.DIR_SEPARATOR_UNIX)) {
                t.createTempBuffer();
                t.advanceTransition(ScriptDataEscapedEndTagOpen);
            } else {
                t.emit((char) Typography.less);
                t.transition(ScriptDataEscaped);
            }
        }
    },
    ScriptDataEscapedEndTagOpen {
        void read(Tokeniser t, CharacterReader r) {
            if (r.matchesLetter()) {
                t.createTagPending(false);
                t.tagPending.appendTagName(r.current());
                t.dataBuffer.append(r.current());
                t.advanceTransition(ScriptDataEscapedEndTagName);
                return;
            }
            t.emit("</");
            t.transition(ScriptDataEscaped);
        }
    },
    ScriptDataEscapedEndTagName {
        void read(Tokeniser t, CharacterReader r) {
            TokeniserState.handleDataEndTag(t, r, ScriptDataEscaped);
        }
    },
    ScriptDataDoubleEscapeStart {
        void read(Tokeniser t, CharacterReader r) {
            TokeniserState.handleDataDoubleEscapeTag(t, r, ScriptDataDoubleEscaped, ScriptDataEscaped);
        }
    },
    ScriptDataDoubleEscaped {
        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        void read(org.jsoup.parser.Tokeniser r3, org.jsoup.parser.CharacterReader r4) {
            /*
            r2 = this;
            r0 = r4.current();
            if (r0 == 0) goto L_0x003c;
        L_0x0006:
            r1 = 45;
            if (r0 == r1) goto L_0x0033;
        L_0x000a:
            r1 = 60;
            if (r0 == r1) goto L_0x002a;
        L_0x000e:
            r1 = 65535; // 0xffff float:9.1834E-41 double:3.23786E-319;
            if (r0 == r1) goto L_0x0021;
        L_0x0013:
            r1 = 3;
            r1 = new char[r1];
            r1 = {45, 60, 0};
            r1 = r4.consumeToAny(r1);
            r3.emit(r1);
            goto L_0x0049;
        L_0x0021:
            r3.eofError(r2);
            r1 = Data;
            r3.transition(r1);
            goto L_0x0049;
        L_0x002a:
            r3.emit(r0);
            r1 = ScriptDataDoubleEscapedLessthanSign;
            r3.advanceTransition(r1);
            goto L_0x0049;
        L_0x0033:
            r3.emit(r0);
            r1 = ScriptDataDoubleEscapedDash;
            r3.advanceTransition(r1);
            goto L_0x0049;
        L_0x003c:
            r3.error(r2);
            r4.advance();
            r1 = 65533; // 0xfffd float:9.1831E-41 double:3.23776E-319;
            r3.emit(r1);
        L_0x0049:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.jsoup.parser.TokeniserState.29.read(org.jsoup.parser.Tokeniser, org.jsoup.parser.CharacterReader):void");
        }
    },
    ScriptDataDoubleEscapedDash {
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            if (c == '\u0000') {
                t.error((TokeniserState) this);
                t.emit((char) TokeniserState.replacementChar);
                t.transition(ScriptDataDoubleEscaped);
            } else if (c == '-') {
                t.emit(c);
                t.transition(ScriptDataDoubleEscapedDashDash);
            } else if (c == Typography.less) {
                t.emit(c);
                t.transition(ScriptDataDoubleEscapedLessthanSign);
            } else if (c != TokeniserState.eof) {
                t.emit(c);
                t.transition(ScriptDataDoubleEscaped);
            } else {
                t.eofError(this);
                t.transition(Data);
            }
        }
    },
    ScriptDataDoubleEscapedDashDash {
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            if (c == '\u0000') {
                t.error((TokeniserState) this);
                t.emit((char) TokeniserState.replacementChar);
                t.transition(ScriptDataDoubleEscaped);
            } else if (c == '-') {
                t.emit(c);
            } else if (c == Typography.less) {
                t.emit(c);
                t.transition(ScriptDataDoubleEscapedLessthanSign);
            } else if (c == Typography.greater) {
                t.emit(c);
                t.transition(ScriptData);
            } else if (c != TokeniserState.eof) {
                t.emit(c);
                t.transition(ScriptDataDoubleEscaped);
            } else {
                t.eofError(this);
                t.transition(Data);
            }
        }
    },
    ScriptDataDoubleEscapedLessthanSign {
        void read(Tokeniser t, CharacterReader r) {
            if (r.matches((char) IOUtils.DIR_SEPARATOR_UNIX)) {
                t.emit((char) IOUtils.DIR_SEPARATOR_UNIX);
                t.createTempBuffer();
                t.advanceTransition(ScriptDataDoubleEscapeEnd);
                return;
            }
            t.transition(ScriptDataDoubleEscaped);
        }
    },
    ScriptDataDoubleEscapeEnd {
        void read(Tokeniser t, CharacterReader r) {
            TokeniserState.handleDataDoubleEscapeTag(t, r, ScriptDataEscaped, ScriptDataDoubleEscaped);
        }
    },
    BeforeAttributeName {
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case '\u0000':
                    t.error((TokeniserState) this);
                    t.tagPending.newAttribute();
                    r.unconsume();
                    t.transition(AttributeName);
                    return;
                case '\t':
                case '\n':
                case '\f':
                case '\r':
                case ' ':
                    return;
                case '\"':
                case '\'':
                case '<':
                case '=':
                    t.error((TokeniserState) this);
                    t.tagPending.newAttribute();
                    t.tagPending.appendAttributeName(c);
                    t.transition(AttributeName);
                    return;
                case '/':
                    t.transition(SelfClosingStartTag);
                    return;
                case '>':
                    t.emitTagPending();
                    t.transition(Data);
                    return;
                case SupportMenu.USER_MASK /*65535*/:
                    t.eofError(this);
                    t.transition(Data);
                    return;
                default:
                    t.tagPending.newAttribute();
                    r.unconsume();
                    t.transition(AttributeName);
                    return;
            }
        }
    },
    AttributeName {
        void read(Tokeniser t, CharacterReader r) {
            t.tagPending.appendAttributeName(r.consumeToAnySorted(attributeNameCharsSorted));
            char c = r.consume();
            switch (c) {
                case '\u0000':
                    t.error((TokeniserState) this);
                    t.tagPending.appendAttributeName((char) TokeniserState.replacementChar);
                    return;
                case '\t':
                case '\n':
                case '\f':
                case '\r':
                case ' ':
                    t.transition(AfterAttributeName);
                    return;
                case '\"':
                case '\'':
                case '<':
                    t.error((TokeniserState) this);
                    t.tagPending.appendAttributeName(c);
                    return;
                case '/':
                    t.transition(SelfClosingStartTag);
                    return;
                case '=':
                    t.transition(BeforeAttributeValue);
                    return;
                case '>':
                    t.emitTagPending();
                    t.transition(Data);
                    return;
                case SupportMenu.USER_MASK /*65535*/:
                    t.eofError(this);
                    t.transition(Data);
                    return;
                default:
                    t.tagPending.appendAttributeName(c);
                    return;
            }
        }
    },
    AfterAttributeName {
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case '\u0000':
                    t.error((TokeniserState) this);
                    t.tagPending.appendAttributeName((char) TokeniserState.replacementChar);
                    t.transition(AttributeName);
                    return;
                case '\t':
                case '\n':
                case '\f':
                case '\r':
                case ' ':
                    return;
                case '\"':
                case '\'':
                case '<':
                    t.error((TokeniserState) this);
                    t.tagPending.newAttribute();
                    t.tagPending.appendAttributeName(c);
                    t.transition(AttributeName);
                    return;
                case '/':
                    t.transition(SelfClosingStartTag);
                    return;
                case '=':
                    t.transition(BeforeAttributeValue);
                    return;
                case '>':
                    t.emitTagPending();
                    t.transition(Data);
                    return;
                case SupportMenu.USER_MASK /*65535*/:
                    t.eofError(this);
                    t.transition(Data);
                    return;
                default:
                    t.tagPending.newAttribute();
                    r.unconsume();
                    t.transition(AttributeName);
                    return;
            }
        }
    },
    BeforeAttributeValue {
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case '\u0000':
                    t.error((TokeniserState) this);
                    t.tagPending.appendAttributeValue((char) TokeniserState.replacementChar);
                    t.transition(AttributeValue_unquoted);
                    return;
                case '\t':
                case '\n':
                case '\f':
                case '\r':
                case ' ':
                    return;
                case '\"':
                    t.transition(AttributeValue_doubleQuoted);
                    return;
                case '&':
                    r.unconsume();
                    t.transition(AttributeValue_unquoted);
                    return;
                case '\'':
                    t.transition(AttributeValue_singleQuoted);
                    return;
                case '<':
                case '=':
                case '`':
                    t.error((TokeniserState) this);
                    t.tagPending.appendAttributeValue(c);
                    t.transition(AttributeValue_unquoted);
                    return;
                case '>':
                    t.error((TokeniserState) this);
                    t.emitTagPending();
                    t.transition(Data);
                    return;
                case SupportMenu.USER_MASK /*65535*/:
                    t.eofError(this);
                    t.emitTagPending();
                    t.transition(Data);
                    return;
                default:
                    r.unconsume();
                    t.transition(AttributeValue_unquoted);
                    return;
            }
        }
    },
    AttributeValue_doubleQuoted {
        void read(Tokeniser t, CharacterReader r) {
            String value = r.consumeToAny(attributeDoubleValueCharsSorted);
            if (value.length() > 0) {
                t.tagPending.appendAttributeValue(value);
            } else {
                t.tagPending.setEmptyAttributeValue();
            }
            char c = r.consume();
            if (c == '\u0000') {
                t.error((TokeniserState) this);
                t.tagPending.appendAttributeValue((char) TokeniserState.replacementChar);
            } else if (c == Typography.quote) {
                t.transition(AfterAttributeValue_quoted);
            } else if (c == Typography.amp) {
                int[] ref = t.consumeCharacterReference(Character.valueOf(Typography.quote), true);
                if (ref != null) {
                    t.tagPending.appendAttributeValue(ref);
                } else {
                    t.tagPending.appendAttributeValue((char) Typography.amp);
                }
            } else if (c != TokeniserState.eof) {
                t.tagPending.appendAttributeValue(c);
            } else {
                t.eofError(this);
                t.transition(Data);
            }
        }
    },
    AttributeValue_singleQuoted {
        void read(Tokeniser t, CharacterReader r) {
            String value = r.consumeToAny(attributeSingleValueCharsSorted);
            if (value.length() > 0) {
                t.tagPending.appendAttributeValue(value);
            } else {
                t.tagPending.setEmptyAttributeValue();
            }
            char c = r.consume();
            if (c == '\u0000') {
                t.error((TokeniserState) this);
                t.tagPending.appendAttributeValue((char) TokeniserState.replacementChar);
            } else if (c != TokeniserState.eof) {
                switch (c) {
                    case '&':
                        int[] ref = t.consumeCharacterReference(Character.valueOf('\''), true);
                        if (ref != null) {
                            t.tagPending.appendAttributeValue(ref);
                            return;
                        } else {
                            t.tagPending.appendAttributeValue((char) Typography.amp);
                            return;
                        }
                    case '\'':
                        t.transition(AfterAttributeValue_quoted);
                        return;
                    default:
                        t.tagPending.appendAttributeValue(c);
                        return;
                }
            } else {
                t.eofError(this);
                t.transition(Data);
            }
        }
    },
    AttributeValue_unquoted {
        void read(Tokeniser t, CharacterReader r) {
            String value = r.consumeToAnySorted(attributeValueUnquoted);
            if (value.length() > 0) {
                t.tagPending.appendAttributeValue(value);
            }
            char c = r.consume();
            switch (c) {
                case '\u0000':
                    t.error((TokeniserState) this);
                    t.tagPending.appendAttributeValue((char) TokeniserState.replacementChar);
                    return;
                case '\t':
                case '\n':
                case '\f':
                case '\r':
                case ' ':
                    t.transition(BeforeAttributeName);
                    return;
                case '\"':
                case '\'':
                case '<':
                case '=':
                case '`':
                    t.error((TokeniserState) this);
                    t.tagPending.appendAttributeValue(c);
                    return;
                case '&':
                    int[] ref = t.consumeCharacterReference(Character.valueOf(Typography.greater), true);
                    if (ref != null) {
                        t.tagPending.appendAttributeValue(ref);
                        return;
                    } else {
                        t.tagPending.appendAttributeValue((char) Typography.amp);
                        return;
                    }
                case '>':
                    t.emitTagPending();
                    t.transition(Data);
                    return;
                case SupportMenu.USER_MASK /*65535*/:
                    t.eofError(this);
                    t.transition(Data);
                    return;
                default:
                    t.tagPending.appendAttributeValue(c);
                    return;
            }
        }
    },
    AfterAttributeValue_quoted {
        void read(Tokeniser t, CharacterReader r) {
            switch (r.consume()) {
                case '\t':
                case '\n':
                case '\f':
                case '\r':
                case ' ':
                    t.transition(BeforeAttributeName);
                    return;
                case '/':
                    t.transition(SelfClosingStartTag);
                    return;
                case '>':
                    t.emitTagPending();
                    t.transition(Data);
                    return;
                case SupportMenu.USER_MASK /*65535*/:
                    t.eofError(this);
                    t.transition(Data);
                    return;
                default:
                    t.error((TokeniserState) this);
                    r.unconsume();
                    t.transition(BeforeAttributeName);
                    return;
            }
        }
    },
    SelfClosingStartTag {
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            if (c == Typography.greater) {
                t.tagPending.selfClosing = true;
                t.emitTagPending();
                t.transition(Data);
            } else if (c != TokeniserState.eof) {
                t.error((TokeniserState) this);
                r.unconsume();
                t.transition(BeforeAttributeName);
            } else {
                t.eofError(this);
                t.transition(Data);
            }
        }
    },
    BogusComment {
        void read(Tokeniser t, CharacterReader r) {
            r.unconsume();
            Token comment = new Comment();
            comment.bogus = true;
            comment.data.append(r.consumeTo((char) Typography.greater));
            t.emit(comment);
            t.advanceTransition(Data);
        }
    },
    MarkupDeclarationOpen {
        void read(Tokeniser t, CharacterReader r) {
            if (r.matchConsume("--")) {
                t.createCommentPending();
                t.transition(CommentStart);
            } else if (r.matchConsumeIgnoreCase("DOCTYPE")) {
                t.transition(Doctype);
            } else if (r.matchConsume("[CDATA[")) {
                t.transition(CdataSection);
            } else {
                t.error((TokeniserState) this);
                t.advanceTransition(BogusComment);
            }
        }
    },
    CommentStart {
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            if (c == '\u0000') {
                t.error((TokeniserState) this);
                t.commentPending.data.append(TokeniserState.replacementChar);
                t.transition(Comment);
            } else if (c == '-') {
                t.transition(CommentStartDash);
            } else if (c == Typography.greater) {
                t.error((TokeniserState) this);
                t.emitCommentPending();
                t.transition(Data);
            } else if (c != TokeniserState.eof) {
                t.commentPending.data.append(c);
                t.transition(Comment);
            } else {
                t.eofError(this);
                t.emitCommentPending();
                t.transition(Data);
            }
        }
    },
    CommentStartDash {
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            if (c == '\u0000') {
                t.error((TokeniserState) this);
                t.commentPending.data.append(TokeniserState.replacementChar);
                t.transition(Comment);
            } else if (c == '-') {
                t.transition(CommentStartDash);
            } else if (c == Typography.greater) {
                t.error((TokeniserState) this);
                t.emitCommentPending();
                t.transition(Data);
            } else if (c != TokeniserState.eof) {
                t.commentPending.data.append(c);
                t.transition(Comment);
            } else {
                t.eofError(this);
                t.emitCommentPending();
                t.transition(Data);
            }
        }
    },
    Comment {
        void read(Tokeniser t, CharacterReader r) {
            char c = r.current();
            if (c == '\u0000') {
                t.error((TokeniserState) this);
                r.advance();
                t.commentPending.data.append(TokeniserState.replacementChar);
            } else if (c == '-') {
                t.advanceTransition(CommentEndDash);
            } else if (c != TokeniserState.eof) {
                t.commentPending.data.append(r.consumeToAny('-', '\u0000'));
            } else {
                t.eofError(this);
                t.emitCommentPending();
                t.transition(Data);
            }
        }
    },
    CommentEndDash {
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            StringBuilder stringBuilder;
            if (c == '\u0000') {
                t.error((TokeniserState) this);
                stringBuilder = t.commentPending.data;
                stringBuilder.append('-');
                stringBuilder.append(TokeniserState.replacementChar);
                t.transition(Comment);
            } else if (c == '-') {
                t.transition(CommentEnd);
            } else if (c != TokeniserState.eof) {
                stringBuilder = t.commentPending.data;
                stringBuilder.append('-');
                stringBuilder.append(c);
                t.transition(Comment);
            } else {
                t.eofError(this);
                t.emitCommentPending();
                t.transition(Data);
            }
        }
    },
    CommentEnd {
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            StringBuilder stringBuilder;
            if (c == '\u0000') {
                t.error((TokeniserState) this);
                stringBuilder = t.commentPending.data;
                stringBuilder.append("--");
                stringBuilder.append(TokeniserState.replacementChar);
                t.transition(Comment);
            } else if (c == '!') {
                t.error((TokeniserState) this);
                t.transition(CommentEndBang);
            } else if (c == '-') {
                t.error((TokeniserState) this);
                t.commentPending.data.append('-');
            } else if (c == Typography.greater) {
                t.emitCommentPending();
                t.transition(Data);
            } else if (c != TokeniserState.eof) {
                t.error((TokeniserState) this);
                stringBuilder = t.commentPending.data;
                stringBuilder.append("--");
                stringBuilder.append(c);
                t.transition(Comment);
            } else {
                t.eofError(this);
                t.emitCommentPending();
                t.transition(Data);
            }
        }
    },
    CommentEndBang {
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            StringBuilder stringBuilder;
            if (c == '\u0000') {
                t.error((TokeniserState) this);
                stringBuilder = t.commentPending.data;
                stringBuilder.append("--!");
                stringBuilder.append(TokeniserState.replacementChar);
                t.transition(Comment);
            } else if (c == '-') {
                t.commentPending.data.append("--!");
                t.transition(CommentEndDash);
            } else if (c == Typography.greater) {
                t.emitCommentPending();
                t.transition(Data);
            } else if (c != TokeniserState.eof) {
                stringBuilder = t.commentPending.data;
                stringBuilder.append("--!");
                stringBuilder.append(c);
                t.transition(Comment);
            } else {
                t.eofError(this);
                t.emitCommentPending();
                t.transition(Data);
            }
        }
    },
    Doctype {
        void read(Tokeniser t, CharacterReader r) {
            switch (r.consume()) {
                case '\t':
                case '\n':
                case '\f':
                case '\r':
                case ' ':
                    t.transition(BeforeDoctypeName);
                    return;
                case '>':
                    break;
                case SupportMenu.USER_MASK /*65535*/:
                    t.eofError(this);
                    break;
                default:
                    t.error((TokeniserState) this);
                    t.transition(BeforeDoctypeName);
                    return;
            }
            t.error((TokeniserState) this);
            t.createDoctypePending();
            t.doctypePending.forceQuirks = true;
            t.emitDoctypePending();
            t.transition(Data);
        }
    },
    BeforeDoctypeName {
        void read(Tokeniser t, CharacterReader r) {
            if (r.matchesLetter()) {
                t.createDoctypePending();
                t.transition(DoctypeName);
                return;
            }
            char c = r.consume();
            switch (c) {
                case '\u0000':
                    t.error((TokeniserState) this);
                    t.createDoctypePending();
                    t.doctypePending.name.append(TokeniserState.replacementChar);
                    t.transition(DoctypeName);
                    break;
                case '\t':
                case '\n':
                case '\f':
                case '\r':
                case ' ':
                    break;
                case SupportMenu.USER_MASK /*65535*/:
                    t.eofError(this);
                    t.createDoctypePending();
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                default:
                    t.createDoctypePending();
                    t.doctypePending.name.append(c);
                    t.transition(DoctypeName);
                    break;
            }
        }
    },
    DoctypeName {
        void read(Tokeniser t, CharacterReader r) {
            if (r.matchesLetter()) {
                t.doctypePending.name.append(r.consumeLetterSequence());
                return;
            }
            char c = r.consume();
            switch (c) {
                case '\u0000':
                    t.error((TokeniserState) this);
                    t.doctypePending.name.append(TokeniserState.replacementChar);
                    break;
                case '\t':
                case '\n':
                case '\f':
                case '\r':
                case ' ':
                    t.transition(AfterDoctypeName);
                    break;
                case '>':
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                case SupportMenu.USER_MASK /*65535*/:
                    t.eofError(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                default:
                    t.doctypePending.name.append(c);
                    break;
            }
        }
    },
    AfterDoctypeName {
        void read(Tokeniser t, CharacterReader r) {
            if (r.isEmpty()) {
                t.eofError(this);
                t.doctypePending.forceQuirks = true;
                t.emitDoctypePending();
                t.transition(Data);
                return;
            }
            if (r.matchesAny('\t', '\n', CharUtils.CR, '\f', ' ')) {
                r.advance();
            } else if (r.matches((char) Typography.greater)) {
                t.emitDoctypePending();
                t.advanceTransition(Data);
            } else if (r.matchConsumeIgnoreCase(DocumentType.PUBLIC_KEY)) {
                t.doctypePending.pubSysKey = DocumentType.PUBLIC_KEY;
                t.transition(AfterDoctypePublicKeyword);
            } else if (r.matchConsumeIgnoreCase(DocumentType.SYSTEM_KEY)) {
                t.doctypePending.pubSysKey = DocumentType.SYSTEM_KEY;
                t.transition(AfterDoctypeSystemKeyword);
            } else {
                t.error((TokeniserState) this);
                t.doctypePending.forceQuirks = true;
                t.advanceTransition(BogusDoctype);
            }
        }
    },
    AfterDoctypePublicKeyword {
        void read(Tokeniser t, CharacterReader r) {
            switch (r.consume()) {
                case '\t':
                case '\n':
                case '\f':
                case '\r':
                case ' ':
                    t.transition(BeforeDoctypePublicIdentifier);
                    return;
                case '\"':
                    t.error((TokeniserState) this);
                    t.transition(DoctypePublicIdentifier_doubleQuoted);
                    return;
                case '\'':
                    t.error((TokeniserState) this);
                    t.transition(DoctypePublicIdentifier_singleQuoted);
                    return;
                case '>':
                    t.error((TokeniserState) this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    return;
                case SupportMenu.USER_MASK /*65535*/:
                    t.eofError(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    return;
                default:
                    t.error((TokeniserState) this);
                    t.doctypePending.forceQuirks = true;
                    t.transition(BogusDoctype);
                    return;
            }
        }
    },
    BeforeDoctypePublicIdentifier {
        void read(Tokeniser t, CharacterReader r) {
            switch (r.consume()) {
                case '\t':
                case '\n':
                case '\f':
                case '\r':
                case ' ':
                    return;
                case '\"':
                    t.transition(DoctypePublicIdentifier_doubleQuoted);
                    return;
                case '\'':
                    t.transition(DoctypePublicIdentifier_singleQuoted);
                    return;
                case '>':
                    t.error((TokeniserState) this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    return;
                case SupportMenu.USER_MASK /*65535*/:
                    t.eofError(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    return;
                default:
                    t.error((TokeniserState) this);
                    t.doctypePending.forceQuirks = true;
                    t.transition(BogusDoctype);
                    return;
            }
        }
    },
    DoctypePublicIdentifier_doubleQuoted {
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            if (c == '\u0000') {
                t.error((TokeniserState) this);
                t.doctypePending.publicIdentifier.append(TokeniserState.replacementChar);
            } else if (c == Typography.quote) {
                t.transition(AfterDoctypePublicIdentifier);
            } else if (c == Typography.greater) {
                t.error((TokeniserState) this);
                t.doctypePending.forceQuirks = true;
                t.emitDoctypePending();
                t.transition(Data);
            } else if (c != TokeniserState.eof) {
                t.doctypePending.publicIdentifier.append(c);
            } else {
                t.eofError(this);
                t.doctypePending.forceQuirks = true;
                t.emitDoctypePending();
                t.transition(Data);
            }
        }
    },
    DoctypePublicIdentifier_singleQuoted {
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            if (c == '\u0000') {
                t.error((TokeniserState) this);
                t.doctypePending.publicIdentifier.append(TokeniserState.replacementChar);
            } else if (c == '\'') {
                t.transition(AfterDoctypePublicIdentifier);
            } else if (c == Typography.greater) {
                t.error((TokeniserState) this);
                t.doctypePending.forceQuirks = true;
                t.emitDoctypePending();
                t.transition(Data);
            } else if (c != TokeniserState.eof) {
                t.doctypePending.publicIdentifier.append(c);
            } else {
                t.eofError(this);
                t.doctypePending.forceQuirks = true;
                t.emitDoctypePending();
                t.transition(Data);
            }
        }
    },
    AfterDoctypePublicIdentifier {
        void read(Tokeniser t, CharacterReader r) {
            switch (r.consume()) {
                case '\t':
                case '\n':
                case '\f':
                case '\r':
                case ' ':
                    t.transition(BetweenDoctypePublicAndSystemIdentifiers);
                    return;
                case '\"':
                    t.error((TokeniserState) this);
                    t.transition(DoctypeSystemIdentifier_doubleQuoted);
                    return;
                case '\'':
                    t.error((TokeniserState) this);
                    t.transition(DoctypeSystemIdentifier_singleQuoted);
                    return;
                case '>':
                    t.emitDoctypePending();
                    t.transition(Data);
                    return;
                case SupportMenu.USER_MASK /*65535*/:
                    t.eofError(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    return;
                default:
                    t.error((TokeniserState) this);
                    t.doctypePending.forceQuirks = true;
                    t.transition(BogusDoctype);
                    return;
            }
        }
    },
    BetweenDoctypePublicAndSystemIdentifiers {
        void read(Tokeniser t, CharacterReader r) {
            switch (r.consume()) {
                case '\t':
                case '\n':
                case '\f':
                case '\r':
                case ' ':
                    return;
                case '\"':
                    t.error((TokeniserState) this);
                    t.transition(DoctypeSystemIdentifier_doubleQuoted);
                    return;
                case '\'':
                    t.error((TokeniserState) this);
                    t.transition(DoctypeSystemIdentifier_singleQuoted);
                    return;
                case '>':
                    t.emitDoctypePending();
                    t.transition(Data);
                    return;
                case SupportMenu.USER_MASK /*65535*/:
                    t.eofError(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    return;
                default:
                    t.error((TokeniserState) this);
                    t.doctypePending.forceQuirks = true;
                    t.transition(BogusDoctype);
                    return;
            }
        }
    },
    AfterDoctypeSystemKeyword {
        void read(Tokeniser t, CharacterReader r) {
            switch (r.consume()) {
                case '\t':
                case '\n':
                case '\f':
                case '\r':
                case ' ':
                    t.transition(BeforeDoctypeSystemIdentifier);
                    return;
                case '\"':
                    t.error((TokeniserState) this);
                    t.transition(DoctypeSystemIdentifier_doubleQuoted);
                    return;
                case '\'':
                    t.error((TokeniserState) this);
                    t.transition(DoctypeSystemIdentifier_singleQuoted);
                    return;
                case '>':
                    t.error((TokeniserState) this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    return;
                case SupportMenu.USER_MASK /*65535*/:
                    t.eofError(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    return;
                default:
                    t.error((TokeniserState) this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    return;
            }
        }
    },
    BeforeDoctypeSystemIdentifier {
        void read(Tokeniser t, CharacterReader r) {
            switch (r.consume()) {
                case '\t':
                case '\n':
                case '\f':
                case '\r':
                case ' ':
                    return;
                case '\"':
                    t.transition(DoctypeSystemIdentifier_doubleQuoted);
                    return;
                case '\'':
                    t.transition(DoctypeSystemIdentifier_singleQuoted);
                    return;
                case '>':
                    t.error((TokeniserState) this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    return;
                case SupportMenu.USER_MASK /*65535*/:
                    t.eofError(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    return;
                default:
                    t.error((TokeniserState) this);
                    t.doctypePending.forceQuirks = true;
                    t.transition(BogusDoctype);
                    return;
            }
        }
    },
    DoctypeSystemIdentifier_doubleQuoted {
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            if (c == '\u0000') {
                t.error((TokeniserState) this);
                t.doctypePending.systemIdentifier.append(TokeniserState.replacementChar);
            } else if (c == Typography.quote) {
                t.transition(AfterDoctypeSystemIdentifier);
            } else if (c == Typography.greater) {
                t.error((TokeniserState) this);
                t.doctypePending.forceQuirks = true;
                t.emitDoctypePending();
                t.transition(Data);
            } else if (c != TokeniserState.eof) {
                t.doctypePending.systemIdentifier.append(c);
            } else {
                t.eofError(this);
                t.doctypePending.forceQuirks = true;
                t.emitDoctypePending();
                t.transition(Data);
            }
        }
    },
    DoctypeSystemIdentifier_singleQuoted {
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            if (c == '\u0000') {
                t.error((TokeniserState) this);
                t.doctypePending.systemIdentifier.append(TokeniserState.replacementChar);
            } else if (c == '\'') {
                t.transition(AfterDoctypeSystemIdentifier);
            } else if (c == Typography.greater) {
                t.error((TokeniserState) this);
                t.doctypePending.forceQuirks = true;
                t.emitDoctypePending();
                t.transition(Data);
            } else if (c != TokeniserState.eof) {
                t.doctypePending.systemIdentifier.append(c);
            } else {
                t.eofError(this);
                t.doctypePending.forceQuirks = true;
                t.emitDoctypePending();
                t.transition(Data);
            }
        }
    },
    AfterDoctypeSystemIdentifier {
        void read(Tokeniser t, CharacterReader r) {
            switch (r.consume()) {
                case '\t':
                case '\n':
                case '\f':
                case '\r':
                case ' ':
                    return;
                case '>':
                    t.emitDoctypePending();
                    t.transition(Data);
                    return;
                case SupportMenu.USER_MASK /*65535*/:
                    t.eofError(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    return;
                default:
                    t.error((TokeniserState) this);
                    t.transition(BogusDoctype);
                    return;
            }
        }
    },
    BogusDoctype {
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            if (c == Typography.greater) {
                t.emitDoctypePending();
                t.transition(Data);
            } else if (c == TokeniserState.eof) {
                t.emitDoctypePending();
                t.transition(Data);
            }
        }
    },
    CdataSection {
        void read(Tokeniser t, CharacterReader r) {
            t.emit(r.consumeTo("]]>"));
            if (!r.matchConsume("]]>")) {
                if (!r.isEmpty()) {
                    return;
                }
            }
            t.transition(Data);
        }
    };
    
    static final char[] attributeDoubleValueCharsSorted = null;
    static final char[] attributeNameCharsSorted = null;
    static final char[] attributeSingleValueCharsSorted = null;
    static final char[] attributeValueUnquoted = null;
    private static final char eof = '￿';
    static final char nullChar = '\u0000';
    private static final char replacementChar = '�';
    private static final String replacementStr = null;

    abstract void read(Tokeniser tokeniser, CharacterReader characterReader);

    static {
        attributeSingleValueCharsSorted = new char[]{'\u0000', Typography.amp, '\''};
        attributeDoubleValueCharsSorted = new char[]{'\u0000', Typography.quote, Typography.amp};
        attributeNameCharsSorted = new char[]{'\u0000', '\t', '\n', '\f', CharUtils.CR, ' ', Typography.quote, '\'', IOUtils.DIR_SEPARATOR_UNIX, Typography.less, '=', Typography.greater};
        attributeValueUnquoted = new char[]{'\u0000', '\t', '\n', '\f', CharUtils.CR, ' ', Typography.quote, Typography.amp, '\'', Typography.less, '=', Typography.greater, '`'};
        replacementStr = String.valueOf(replacementChar);
    }

    private static void handleDataEndTag(Tokeniser t, CharacterReader r, TokeniserState elseTransition) {
        if (r.matchesLetter()) {
            String name = r.consumeLetterSequence();
            t.tagPending.appendTagName(name);
            t.dataBuffer.append(name);
            return;
        }
        boolean needsExitTransition = false;
        if (t.isAppropriateEndTagToken() && !r.isEmpty()) {
            char c = r.consume();
            switch (c) {
                case '\t':
                case '\n':
                case '\f':
                case '\r':
                case ' ':
                    t.transition(BeforeAttributeName);
                    break;
                case '/':
                    t.transition(SelfClosingStartTag);
                    break;
                case '>':
                    t.emitTagPending();
                    t.transition(Data);
                    break;
                default:
                    t.dataBuffer.append(c);
                    needsExitTransition = true;
                    break;
            }
        }
        needsExitTransition = true;
        if (needsExitTransition) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("</");
            stringBuilder.append(t.dataBuffer.toString());
            t.emit(stringBuilder.toString());
            t.transition(elseTransition);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void readData(org.jsoup.parser.Tokeniser r2, org.jsoup.parser.CharacterReader r3, org.jsoup.parser.TokeniserState r4, org.jsoup.parser.TokeniserState r5) {
        /*
        r0 = r3.current();
        if (r0 == 0) goto L_0x002a;
    L_0x0006:
        r1 = 60;
        if (r0 == r1) goto L_0x0026;
    L_0x000a:
        r1 = 65535; // 0xffff float:9.1834E-41 double:3.23786E-319;
        if (r0 == r1) goto L_0x001d;
    L_0x000f:
        r0 = 2;
        r0 = new char[r0];
        r0 = {60, 0};
        r0 = r3.consumeToAny(r0);
        r2.emit(r0);
        goto L_0x0037;
    L_0x001d:
        r0 = new org.jsoup.parser.Token$EOF;
        r0.<init>();
        r2.emit(r0);
        goto L_0x0037;
    L_0x0026:
        r2.advanceTransition(r5);
        goto L_0x0037;
    L_0x002a:
        r2.error(r4);
        r3.advance();
        r0 = 65533; // 0xfffd float:9.1831E-41 double:3.23776E-319;
        r2.emit(r0);
    L_0x0037:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jsoup.parser.TokeniserState.readData(org.jsoup.parser.Tokeniser, org.jsoup.parser.CharacterReader, org.jsoup.parser.TokeniserState, org.jsoup.parser.TokeniserState):void");
    }

    private static void readCharRef(Tokeniser t, TokeniserState advance) {
        int[] c = t.consumeCharacterReference(null, false);
        if (c == null) {
            t.emit((char) Typography.amp);
        } else {
            t.emit(c);
        }
        t.transition(advance);
    }

    private static void readEndTag(Tokeniser t, CharacterReader r, TokeniserState a, TokeniserState b) {
        if (r.matchesLetter()) {
            t.createTagPending(false);
            t.transition(a);
            return;
        }
        t.emit("</");
        t.transition(b);
    }

    private static void handleDataDoubleEscapeTag(Tokeniser t, CharacterReader r, TokeniserState primary, TokeniserState fallback) {
        if (r.matchesLetter()) {
            String name = r.consumeLetterSequence();
            t.dataBuffer.append(name);
            t.emit(name);
            return;
        }
        char c = r.consume();
        switch (c) {
            case '\t':
            case '\n':
            case '\f':
            case '\r':
            case ' ':
            case '/':
            case '>':
                if (t.dataBuffer.toString().equals("script")) {
                    t.transition(primary);
                } else {
                    t.transition(fallback);
                }
                t.emit(c);
                break;
            default:
                r.unconsume();
                t.transition(fallback);
                break;
        }
    }
}
