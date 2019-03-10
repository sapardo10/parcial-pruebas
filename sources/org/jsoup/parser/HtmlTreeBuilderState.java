package org.jsoup.parser;

import com.google.android.exoplayer2.text.ttml.TtmlNode;
import de.danoeh.antennapod.core.storage.PodDBAdapter;
import java.util.ArrayList;
import java.util.Iterator;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document.QuirksMode;
import org.jsoup.nodes.DocumentType;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

enum HtmlTreeBuilderState {
    Initial {
        boolean process(Token t, HtmlTreeBuilder tb) {
            if (HtmlTreeBuilderState.isWhitespace(t)) {
                return true;
            }
            if (t.isComment()) {
                tb.insert(t.asComment());
            } else if (t.isDoctype()) {
                Doctype d = t.asDoctype();
                DocumentType doctype = new DocumentType(tb.settings.normalizeTag(d.getName()), d.getPublicIdentifier(), d.getSystemIdentifier());
                doctype.setPubSysKey(d.getPubSysKey());
                tb.getDocument().appendChild(doctype);
                if (d.isForceQuirks()) {
                    tb.getDocument().quirksMode(QuirksMode.quirks);
                }
                tb.transition(BeforeHtml);
            } else {
                tb.transition(BeforeHtml);
                return tb.process(t);
            }
            return true;
        }
    },
    BeforeHtml {
        boolean process(Token t, HtmlTreeBuilder tb) {
            if (t.isDoctype()) {
                tb.error(this);
                return false;
            }
            if (t.isComment()) {
                tb.insert(t.asComment());
            } else if (HtmlTreeBuilderState.isWhitespace(t)) {
                return true;
            } else {
                if (t.isStartTag() && t.asStartTag().normalName().equals("html")) {
                    tb.insert(t.asStartTag());
                    tb.transition(BeforeHead);
                } else {
                    if (t.isEndTag()) {
                        if (StringUtil.in(t.asEndTag().normalName(), "head", "body", "html", TtmlNode.TAG_BR)) {
                            return anythingElse(t, tb);
                        }
                    }
                    if (!t.isEndTag()) {
                        return anythingElse(t, tb);
                    }
                    tb.error(this);
                    return false;
                }
            }
            return true;
        }

        private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
            tb.insertStartTag("html");
            tb.transition(BeforeHead);
            return tb.process(t);
        }
    },
    BeforeHead {
        boolean process(Token t, HtmlTreeBuilder tb) {
            if (HtmlTreeBuilderState.isWhitespace(t)) {
                return true;
            }
            if (t.isComment()) {
                tb.insert(t.asComment());
            } else if (t.isDoctype()) {
                tb.error(this);
                return false;
            } else if (t.isStartTag() && t.asStartTag().normalName().equals("html")) {
                return InBody.process(t, tb);
            } else {
                if (t.isStartTag() && t.asStartTag().normalName().equals("head")) {
                    tb.setHeadElement(tb.insert(t.asStartTag()));
                    tb.transition(InHead);
                } else {
                    if (t.isEndTag()) {
                        if (StringUtil.in(t.asEndTag().normalName(), "head", "body", "html", TtmlNode.TAG_BR)) {
                            tb.processStartTag("head");
                            return tb.process(t);
                        }
                    }
                    if (t.isEndTag()) {
                        tb.error(this);
                        return false;
                    }
                    tb.processStartTag("head");
                    return tb.process(t);
                }
            }
            return true;
        }
    },
    InHead {
        boolean process(Token t, HtmlTreeBuilder tb) {
            if (HtmlTreeBuilderState.isWhitespace(t)) {
                tb.insert(t.asCharacter());
                return true;
            }
            String name;
            switch (t.type) {
                case Comment:
                    tb.insert(t.asComment());
                    break;
                case Doctype:
                    tb.error(this);
                    return false;
                case StartTag:
                    StartTag start = t.asStartTag();
                    name = start.normalName();
                    if (name.equals("html")) {
                        return InBody.process(t, tb);
                    }
                    if (StringUtil.in(name, "base", "basefont", "bgsound", "command", PodDBAdapter.KEY_LINK)) {
                        Element el = tb.insertEmpty(start);
                        if (name.equals("base") && el.hasAttr("href")) {
                            tb.maybeSetBaseUri(el);
                        }
                        break;
                    } else if (name.equals("meta")) {
                        tb.insertEmpty(start);
                        break;
                    } else if (name.equals("title")) {
                        HtmlTreeBuilderState.handleRcData(start, tb);
                        break;
                    } else {
                        if (StringUtil.in(name, "noframes", TtmlNode.TAG_STYLE)) {
                            HtmlTreeBuilderState.handleRawtext(start, tb);
                            break;
                        } else if (name.equals("noscript")) {
                            tb.insert(start);
                            tb.transition(InHeadNoscript);
                            break;
                        } else if (name.equals("script")) {
                            tb.tokeniser.transition(TokeniserState.ScriptData);
                            tb.markInsertionMode();
                            tb.transition(Text);
                            tb.insert(start);
                            break;
                        } else if (!name.equals("head")) {
                            return anythingElse(t, tb);
                        } else {
                            tb.error(this);
                            return false;
                        }
                    }
                    break;
                case EndTag:
                    name = t.asEndTag().normalName();
                    if (name.equals("head")) {
                        tb.pop();
                        tb.transition(AfterHead);
                        break;
                    }
                    if (StringUtil.in(name, "body", "html", TtmlNode.TAG_BR)) {
                        return anythingElse(t, tb);
                    }
                    tb.error(this);
                    return false;
                default:
                    return anythingElse(t, tb);
            }
            return true;
        }

        private boolean anythingElse(Token t, TreeBuilder tb) {
            tb.processEndTag("head");
            return tb.process(t);
        }
    },
    InHeadNoscript {
        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        boolean process(org.jsoup.parser.Token r8, org.jsoup.parser.HtmlTreeBuilder r9) {
            /*
            r7 = this;
            r0 = r8.isDoctype();
            r1 = 1;
            if (r0 == 0) goto L_0x000b;
        L_0x0007:
            r9.error(r7);
            goto L_0x0047;
        L_0x000b:
            r0 = r8.isStartTag();
            if (r0 == 0) goto L_0x0028;
        L_0x0011:
            r0 = r8.asStartTag();
            r0 = r0.normalName();
            r2 = "html";
            r0 = r0.equals(r2);
            if (r0 == 0) goto L_0x0028;
        L_0x0021:
            r0 = InBody;
            r0 = r9.process(r8, r0);
            return r0;
            r0 = r8.isEndTag();
            if (r0 == 0) goto L_0x0048;
        L_0x002f:
            r0 = r8.asEndTag();
            r0 = r0.normalName();
            r2 = "noscript";
            r0 = r0.equals(r2);
            if (r0 == 0) goto L_0x0048;
        L_0x003f:
            r9.pop();
            r0 = InHead;
            r9.transition(r0);
        L_0x0047:
            return r1;
            r0 = org.jsoup.parser.HtmlTreeBuilderState.isWhitespace(r8);
            if (r0 != 0) goto L_0x00d3;
        L_0x004f:
            r0 = r8.isComment();
            if (r0 != 0) goto L_0x00d3;
        L_0x0055:
            r0 = r8.isStartTag();
            r2 = 2;
            r3 = 0;
            if (r0 == 0) goto L_0x008a;
        L_0x005d:
            r0 = r8.asStartTag();
            r0 = r0.normalName();
            r4 = 6;
            r4 = new java.lang.String[r4];
            r5 = "basefont";
            r4[r3] = r5;
            r5 = "bgsound";
            r4[r1] = r5;
            r5 = "link";
            r4[r2] = r5;
            r5 = 3;
            r6 = "meta";
            r4[r5] = r6;
            r5 = 4;
            r6 = "noframes";
            r4[r5] = r6;
            r5 = 5;
            r6 = "style";
            r4[r5] = r6;
            r0 = org.jsoup.helper.StringUtil.in(r0, r4);
            if (r0 == 0) goto L_0x008a;
        L_0x0089:
            goto L_0x00d3;
        L_0x008a:
            r0 = r8.isEndTag();
            if (r0 == 0) goto L_0x00a5;
        L_0x0090:
            r0 = r8.asEndTag();
            r0 = r0.normalName();
            r4 = "br";
            r0 = r0.equals(r4);
            if (r0 == 0) goto L_0x00a5;
        L_0x00a0:
            r0 = r7.anythingElse(r8, r9);
            return r0;
            r0 = r8.isStartTag();
            if (r0 == 0) goto L_0x00c4;
        L_0x00ac:
            r0 = r8.asStartTag();
            r0 = r0.normalName();
            r2 = new java.lang.String[r2];
            r4 = "head";
            r2[r3] = r4;
            r4 = "noscript";
            r2[r1] = r4;
            r0 = org.jsoup.helper.StringUtil.in(r0, r2);
            if (r0 != 0) goto L_0x00ca;
        L_0x00c4:
            r0 = r8.isEndTag();
            if (r0 == 0) goto L_0x00ce;
        L_0x00ca:
            r9.error(r7);
            return r3;
        L_0x00ce:
            r0 = r7.anythingElse(r8, r9);
            return r0;
            r0 = InHead;
            r0 = r9.process(r8, r0);
            return r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.jsoup.parser.HtmlTreeBuilderState.5.process(org.jsoup.parser.Token, org.jsoup.parser.HtmlTreeBuilder):boolean");
        }

        private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
            tb.error(this);
            tb.insert(new Character().data(t.toString()));
            return true;
        }
    },
    AfterHead {
        boolean process(Token t, HtmlTreeBuilder tb) {
            if (HtmlTreeBuilderState.isWhitespace(t)) {
                tb.insert(t.asCharacter());
            } else if (t.isComment()) {
                tb.insert(t.asComment());
            } else if (t.isDoctype()) {
                tb.error(this);
            } else if (t.isStartTag()) {
                StartTag startTag = t.asStartTag();
                String name = startTag.normalName();
                if (name.equals("html")) {
                    return tb.process(t, InBody);
                }
                if (name.equals("body")) {
                    tb.insert(startTag);
                    tb.framesetOk(false);
                    tb.transition(InBody);
                } else if (name.equals("frameset")) {
                    tb.insert(startTag);
                    tb.transition(InFrameset);
                } else {
                    if (StringUtil.in(name, "base", "basefont", "bgsound", PodDBAdapter.KEY_LINK, "meta", "noframes", "script", TtmlNode.TAG_STYLE, "title")) {
                        tb.error(this);
                        Element head = tb.getHeadElement();
                        tb.push(head);
                        tb.process(t, InHead);
                        tb.removeFromStack(head);
                    } else if (name.equals("head")) {
                        tb.error(this);
                        return false;
                    } else {
                        anythingElse(t, tb);
                    }
                }
            } else if (t.isEndTag()) {
                if (StringUtil.in(t.asEndTag().normalName(), "body", "html")) {
                    anythingElse(t, tb);
                } else {
                    tb.error(this);
                    return false;
                }
            } else {
                anythingElse(t, tb);
            }
            return true;
        }

        private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
            tb.processStartTag("body");
            tb.framesetOk(true);
            return tb.process(t);
        }
    },
    InBody {
        boolean process(Token t, HtmlTreeBuilder tb) {
            HtmlTreeBuilderState htmlTreeBuilderState = this;
            Token token = t;
            HtmlTreeBuilder htmlTreeBuilder = tb;
            boolean z = true;
            boolean z2 = false;
            Element html;
            switch (token.type) {
                case Comment:
                    htmlTreeBuilder.insert(t.asComment());
                    break;
                case Doctype:
                    htmlTreeBuilder.error(htmlTreeBuilderState);
                    return false;
                case StartTag:
                    StartTag startTag = t.asStartTag();
                    String name = startTag.normalName();
                    if (!name.equals("a")) {
                        if (!StringUtil.inSorted(name, Constants.InBodyStartEmptyFormatters)) {
                            if (!StringUtil.inSorted(name, Constants.InBodyStartPClosers)) {
                                if (!name.equals(TtmlNode.TAG_SPAN)) {
                                    ArrayList<Element> stack;
                                    int i;
                                    Element el;
                                    if (!name.equals("li")) {
                                        if (!name.equals("html")) {
                                            if (!StringUtil.inSorted(name, Constants.InBodyStartToHead)) {
                                                Iterator it;
                                                Attribute attr;
                                                Element second;
                                                if (!name.equals("body")) {
                                                    if (!name.equals("frameset")) {
                                                        if (!StringUtil.inSorted(name, Constants.Headings)) {
                                                            if (!StringUtil.inSorted(name, Constants.InBodyStartPreListing)) {
                                                                if (!name.equals("form")) {
                                                                    if (!StringUtil.inSorted(name, Constants.DdDt)) {
                                                                        if (!name.equals("plaintext")) {
                                                                            if (!name.equals("button")) {
                                                                                if (!StringUtil.inSorted(name, Constants.Formatters)) {
                                                                                    if (!name.equals("nobr")) {
                                                                                        if (!StringUtil.inSorted(name, Constants.InBodyStartApplets)) {
                                                                                            if (!name.equals("table")) {
                                                                                                if (!name.equals("input")) {
                                                                                                    if (!StringUtil.inSorted(name, Constants.InBodyStartMedia)) {
                                                                                                        if (!name.equals("hr")) {
                                                                                                            if (!name.equals("image")) {
                                                                                                                if (!name.equals("isindex")) {
                                                                                                                    if (!name.equals("textarea")) {
                                                                                                                        if (!name.equals("xmp")) {
                                                                                                                            if (!name.equals("iframe")) {
                                                                                                                                if (!name.equals("noembed")) {
                                                                                                                                    if (!name.equals("select")) {
                                                                                                                                        if (!StringUtil.inSorted(name, Constants.InBodyStartOptions)) {
                                                                                                                                            if (!StringUtil.inSorted(name, Constants.InBodyStartRuby)) {
                                                                                                                                                if (!name.equals("math")) {
                                                                                                                                                    if (!name.equals("svg")) {
                                                                                                                                                        if (!StringUtil.inSorted(name, Constants.InBodyStartDrop)) {
                                                                                                                                                            tb.reconstructFormattingElements();
                                                                                                                                                            htmlTreeBuilder.insert(startTag);
                                                                                                                                                            break;
                                                                                                                                                        }
                                                                                                                                                        htmlTreeBuilder.error(htmlTreeBuilderState);
                                                                                                                                                        return false;
                                                                                                                                                    }
                                                                                                                                                    tb.reconstructFormattingElements();
                                                                                                                                                    htmlTreeBuilder.insert(startTag);
                                                                                                                                                    break;
                                                                                                                                                }
                                                                                                                                                tb.reconstructFormattingElements();
                                                                                                                                                htmlTreeBuilder.insert(startTag);
                                                                                                                                                break;
                                                                                                                                            } else if (!htmlTreeBuilder.inScope("ruby")) {
                                                                                                                                                break;
                                                                                                                                            } else {
                                                                                                                                                tb.generateImpliedEndTags();
                                                                                                                                                if (!tb.currentElement().nodeName().equals("ruby")) {
                                                                                                                                                    htmlTreeBuilder.error(htmlTreeBuilderState);
                                                                                                                                                    htmlTreeBuilder.popStackToBefore("ruby");
                                                                                                                                                }
                                                                                                                                                htmlTreeBuilder.insert(startTag);
                                                                                                                                                break;
                                                                                                                                            }
                                                                                                                                        }
                                                                                                                                        if (tb.currentElement().nodeName().equals("option")) {
                                                                                                                                            htmlTreeBuilder.processEndTag("option");
                                                                                                                                        }
                                                                                                                                        tb.reconstructFormattingElements();
                                                                                                                                        htmlTreeBuilder.insert(startTag);
                                                                                                                                        break;
                                                                                                                                    }
                                                                                                                                    tb.reconstructFormattingElements();
                                                                                                                                    htmlTreeBuilder.insert(startTag);
                                                                                                                                    htmlTreeBuilder.framesetOk(false);
                                                                                                                                    HtmlTreeBuilderState state = tb.state();
                                                                                                                                    if (!(state.equals(InTable) || state.equals(InCaption) || state.equals(InTableBody) || state.equals(InRow))) {
                                                                                                                                        if (!state.equals(InCell)) {
                                                                                                                                            htmlTreeBuilder.transition(InSelect);
                                                                                                                                            break;
                                                                                                                                        }
                                                                                                                                    }
                                                                                                                                    htmlTreeBuilder.transition(InSelectInTable);
                                                                                                                                } else {
                                                                                                                                    HtmlTreeBuilderState.handleRawtext(startTag, htmlTreeBuilder);
                                                                                                                                    break;
                                                                                                                                }
                                                                                                                            }
                                                                                                                            htmlTreeBuilder.framesetOk(false);
                                                                                                                            HtmlTreeBuilderState.handleRawtext(startTag, htmlTreeBuilder);
                                                                                                                            break;
                                                                                                                        }
                                                                                                                        if (htmlTreeBuilder.inButtonScope(TtmlNode.TAG_P)) {
                                                                                                                            htmlTreeBuilder.processEndTag(TtmlNode.TAG_P);
                                                                                                                        }
                                                                                                                        tb.reconstructFormattingElements();
                                                                                                                        htmlTreeBuilder.framesetOk(false);
                                                                                                                        HtmlTreeBuilderState.handleRawtext(startTag, htmlTreeBuilder);
                                                                                                                        break;
                                                                                                                    }
                                                                                                                    htmlTreeBuilder.insert(startTag);
                                                                                                                    htmlTreeBuilder.tokeniser.transition(TokeniserState.Rcdata);
                                                                                                                    tb.markInsertionMode();
                                                                                                                    htmlTreeBuilder.framesetOk(false);
                                                                                                                    htmlTreeBuilder.transition(Text);
                                                                                                                    break;
                                                                                                                }
                                                                                                                htmlTreeBuilder.error(htmlTreeBuilderState);
                                                                                                                if (tb.getFormElement() == null) {
                                                                                                                    String prompt;
                                                                                                                    htmlTreeBuilder.processStartTag("form");
                                                                                                                    if (startTag.attributes.hasKey("action")) {
                                                                                                                        tb.getFormElement().attr("action", startTag.attributes.get("action"));
                                                                                                                    }
                                                                                                                    htmlTreeBuilder.processStartTag("hr");
                                                                                                                    htmlTreeBuilder.processStartTag("label");
                                                                                                                    if (startTag.attributes.hasKey("prompt")) {
                                                                                                                        prompt = startTag.attributes.get("prompt");
                                                                                                                    } else {
                                                                                                                        prompt = "This is a searchable index. Enter search keywords: ";
                                                                                                                    }
                                                                                                                    htmlTreeBuilder.process(new Character().data(prompt));
                                                                                                                    Attributes inputAttribs = new Attributes();
                                                                                                                    it = startTag.attributes.iterator();
                                                                                                                    while (it.hasNext()) {
                                                                                                                        attr = (Attribute) it.next();
                                                                                                                        if (!StringUtil.inSorted(attr.getKey(), Constants.InBodyStartInputAttribs)) {
                                                                                                                            inputAttribs.put(attr);
                                                                                                                        }
                                                                                                                    }
                                                                                                                    inputAttribs.put(PodDBAdapter.KEY_NAME, "isindex");
                                                                                                                    htmlTreeBuilder.processStartTag("input", inputAttribs);
                                                                                                                    htmlTreeBuilder.processEndTag("label");
                                                                                                                    htmlTreeBuilder.processStartTag("hr");
                                                                                                                    htmlTreeBuilder.processEndTag("form");
                                                                                                                    break;
                                                                                                                }
                                                                                                                return false;
                                                                                                            } else if (htmlTreeBuilder.getFromStack("svg") != null) {
                                                                                                                htmlTreeBuilder.insert(startTag);
                                                                                                                break;
                                                                                                            } else {
                                                                                                                return htmlTreeBuilder.process(startTag.name("img"));
                                                                                                            }
                                                                                                        }
                                                                                                        if (htmlTreeBuilder.inButtonScope(TtmlNode.TAG_P)) {
                                                                                                            htmlTreeBuilder.processEndTag(TtmlNode.TAG_P);
                                                                                                        }
                                                                                                        htmlTreeBuilder.insertEmpty(startTag);
                                                                                                        htmlTreeBuilder.framesetOk(false);
                                                                                                        break;
                                                                                                    }
                                                                                                    htmlTreeBuilder.insertEmpty(startTag);
                                                                                                    break;
                                                                                                }
                                                                                                tb.reconstructFormattingElements();
                                                                                                if (!htmlTreeBuilder.insertEmpty(startTag).attr("type").equalsIgnoreCase("hidden")) {
                                                                                                    htmlTreeBuilder.framesetOk(false);
                                                                                                }
                                                                                                break;
                                                                                            }
                                                                                            if (tb.getDocument().quirksMode() != QuirksMode.quirks && htmlTreeBuilder.inButtonScope(TtmlNode.TAG_P)) {
                                                                                                htmlTreeBuilder.processEndTag(TtmlNode.TAG_P);
                                                                                            }
                                                                                            htmlTreeBuilder.insert(startTag);
                                                                                            htmlTreeBuilder.framesetOk(false);
                                                                                            htmlTreeBuilder.transition(InTable);
                                                                                            break;
                                                                                        }
                                                                                        tb.reconstructFormattingElements();
                                                                                        htmlTreeBuilder.insert(startTag);
                                                                                        tb.insertMarkerToFormattingElements();
                                                                                        htmlTreeBuilder.framesetOk(false);
                                                                                        break;
                                                                                    }
                                                                                    tb.reconstructFormattingElements();
                                                                                    if (htmlTreeBuilder.inScope("nobr")) {
                                                                                        htmlTreeBuilder.error(htmlTreeBuilderState);
                                                                                        htmlTreeBuilder.processEndTag("nobr");
                                                                                        tb.reconstructFormattingElements();
                                                                                    }
                                                                                    htmlTreeBuilder.pushActiveFormattingElements(htmlTreeBuilder.insert(startTag));
                                                                                    break;
                                                                                }
                                                                                tb.reconstructFormattingElements();
                                                                                htmlTreeBuilder.pushActiveFormattingElements(htmlTreeBuilder.insert(startTag));
                                                                                break;
                                                                            } else if (!htmlTreeBuilder.inButtonScope("button")) {
                                                                                tb.reconstructFormattingElements();
                                                                                htmlTreeBuilder.insert(startTag);
                                                                                htmlTreeBuilder.framesetOk(false);
                                                                                break;
                                                                            } else {
                                                                                htmlTreeBuilder.error(htmlTreeBuilderState);
                                                                                htmlTreeBuilder.processEndTag("button");
                                                                                htmlTreeBuilder.process(startTag);
                                                                                break;
                                                                            }
                                                                        }
                                                                        if (htmlTreeBuilder.inButtonScope(TtmlNode.TAG_P)) {
                                                                            htmlTreeBuilder.processEndTag(TtmlNode.TAG_P);
                                                                        }
                                                                        htmlTreeBuilder.insert(startTag);
                                                                        htmlTreeBuilder.tokeniser.transition(TokeniserState.PLAINTEXT);
                                                                        break;
                                                                    }
                                                                    htmlTreeBuilder.framesetOk(false);
                                                                    stack = tb.getStack();
                                                                    i = stack.size() - 1;
                                                                    while (i > 0) {
                                                                        el = (Element) stack.get(i);
                                                                        if (StringUtil.inSorted(el.nodeName(), Constants.DdDt)) {
                                                                            htmlTreeBuilder.processEndTag(el.nodeName());
                                                                        } else if (!htmlTreeBuilder.isSpecial(el) || StringUtil.inSorted(el.nodeName(), Constants.InBodyStartLiBreakers)) {
                                                                            i--;
                                                                        }
                                                                        if (htmlTreeBuilder.inButtonScope(TtmlNode.TAG_P)) {
                                                                            htmlTreeBuilder.processEndTag(TtmlNode.TAG_P);
                                                                        }
                                                                        htmlTreeBuilder.insert(startTag);
                                                                        break;
                                                                    }
                                                                    if (htmlTreeBuilder.inButtonScope(TtmlNode.TAG_P)) {
                                                                        htmlTreeBuilder.processEndTag(TtmlNode.TAG_P);
                                                                    }
                                                                    htmlTreeBuilder.insert(startTag);
                                                                } else if (tb.getFormElement() == null) {
                                                                    if (htmlTreeBuilder.inButtonScope(TtmlNode.TAG_P)) {
                                                                        htmlTreeBuilder.processEndTag(TtmlNode.TAG_P);
                                                                    }
                                                                    htmlTreeBuilder.insertForm(startTag, true);
                                                                    break;
                                                                } else {
                                                                    htmlTreeBuilder.error(htmlTreeBuilderState);
                                                                    return false;
                                                                }
                                                            }
                                                            if (htmlTreeBuilder.inButtonScope(TtmlNode.TAG_P)) {
                                                                htmlTreeBuilder.processEndTag(TtmlNode.TAG_P);
                                                            }
                                                            htmlTreeBuilder.insert(startTag);
                                                            htmlTreeBuilder.framesetOk(false);
                                                            break;
                                                        }
                                                        if (htmlTreeBuilder.inButtonScope(TtmlNode.TAG_P)) {
                                                            htmlTreeBuilder.processEndTag(TtmlNode.TAG_P);
                                                        }
                                                        if (StringUtil.inSorted(tb.currentElement().nodeName(), Constants.Headings)) {
                                                            htmlTreeBuilder.error(htmlTreeBuilderState);
                                                            tb.pop();
                                                        }
                                                        htmlTreeBuilder.insert(startTag);
                                                        break;
                                                    }
                                                    htmlTreeBuilder.error(htmlTreeBuilderState);
                                                    stack = tb.getStack();
                                                    if (stack.size() != 1) {
                                                        if (stack.size() <= 2 || ((Element) stack.get(1)).nodeName().equals("body")) {
                                                            if (tb.framesetOk()) {
                                                                second = (Element) stack.get(1);
                                                                if (second.parent() != null) {
                                                                    second.remove();
                                                                }
                                                                while (stack.size() > 1) {
                                                                    stack.remove(stack.size() - 1);
                                                                }
                                                                htmlTreeBuilder.insert(startTag);
                                                                htmlTreeBuilder.transition(InFrameset);
                                                                break;
                                                            }
                                                            return false;
                                                        }
                                                    }
                                                    return false;
                                                }
                                                htmlTreeBuilder.error(htmlTreeBuilderState);
                                                stack = tb.getStack();
                                                if (stack.size() != 1) {
                                                    if (stack.size() <= 2 || ((Element) stack.get(1)).nodeName().equals("body")) {
                                                        htmlTreeBuilder.framesetOk(false);
                                                        second = (Element) stack.get(1);
                                                        it = startTag.getAttributes().iterator();
                                                        while (it.hasNext()) {
                                                            attr = (Attribute) it.next();
                                                            if (!second.hasAttr(attr.getKey())) {
                                                                second.attributes().put(attr);
                                                            }
                                                        }
                                                        break;
                                                    }
                                                }
                                                return false;
                                            }
                                            return htmlTreeBuilder.process(token, InHead);
                                        }
                                        htmlTreeBuilder.error(htmlTreeBuilderState);
                                        html = (Element) tb.getStack().get(0);
                                        Iterator it2 = startTag.getAttributes().iterator();
                                        while (it2.hasNext()) {
                                            Attribute attribute = (Attribute) it2.next();
                                            if (!html.hasAttr(attribute.getKey())) {
                                                html.attributes().put(attribute);
                                            }
                                        }
                                        break;
                                    }
                                    htmlTreeBuilder.framesetOk(false);
                                    stack = tb.getStack();
                                    i = stack.size() - 1;
                                    while (i > 0) {
                                        el = (Element) stack.get(i);
                                        if (el.nodeName().equals("li")) {
                                            htmlTreeBuilder.processEndTag("li");
                                        } else if (!htmlTreeBuilder.isSpecial(el) || StringUtil.inSorted(el.nodeName(), Constants.InBodyStartLiBreakers)) {
                                            i--;
                                        }
                                        if (htmlTreeBuilder.inButtonScope(TtmlNode.TAG_P)) {
                                            htmlTreeBuilder.processEndTag(TtmlNode.TAG_P);
                                        }
                                        htmlTreeBuilder.insert(startTag);
                                        break;
                                    }
                                    if (htmlTreeBuilder.inButtonScope(TtmlNode.TAG_P)) {
                                        htmlTreeBuilder.processEndTag(TtmlNode.TAG_P);
                                    }
                                    htmlTreeBuilder.insert(startTag);
                                } else {
                                    tb.reconstructFormattingElements();
                                    htmlTreeBuilder.insert(startTag);
                                    break;
                                }
                            }
                            if (htmlTreeBuilder.inButtonScope(TtmlNode.TAG_P)) {
                                htmlTreeBuilder.processEndTag(TtmlNode.TAG_P);
                            }
                            htmlTreeBuilder.insert(startTag);
                            break;
                        }
                        tb.reconstructFormattingElements();
                        htmlTreeBuilder.insertEmpty(startTag);
                        htmlTreeBuilder.framesetOk(false);
                        break;
                    }
                    if (htmlTreeBuilder.getActiveFormattingElement("a") != null) {
                        htmlTreeBuilder.error(htmlTreeBuilderState);
                        htmlTreeBuilder.processEndTag("a");
                        html = htmlTreeBuilder.getFromStack("a");
                        if (html != null) {
                            htmlTreeBuilder.removeFromActiveFormattingElements(html);
                            htmlTreeBuilder.removeFromStack(html);
                        }
                    }
                    tb.reconstructFormattingElements();
                    htmlTreeBuilder.pushActiveFormattingElements(htmlTreeBuilder.insert(startTag));
                    break;
                    break;
                case EndTag:
                    EndTag endTag = t.asEndTag();
                    String name2 = endTag.normalName();
                    Element adopter;
                    if (StringUtil.inSorted(name2, Constants.InBodyEndAdoptionFormatters)) {
                        int i2 = 0;
                        while (i2 < 8) {
                            Element formatEl = htmlTreeBuilder.getActiveFormattingElement(name2);
                            if (formatEl == null) {
                                return anyOtherEndTag(t, tb);
                            }
                            if (!htmlTreeBuilder.onStack(formatEl)) {
                                htmlTreeBuilder.error(htmlTreeBuilderState);
                                htmlTreeBuilder.removeFromActiveFormattingElements(formatEl);
                                return z;
                            } else if (htmlTreeBuilder.inScope(formatEl.nodeName())) {
                                if (tb.currentElement() != formatEl) {
                                    htmlTreeBuilder.error(htmlTreeBuilderState);
                                }
                                Element furthestBlock = null;
                                Element commonAncestor = null;
                                boolean seenFormattingElement = false;
                                ArrayList<Element> stack2 = tb.getStack();
                                int stackSize = stack2.size();
                                int si = 0;
                                while (si < stackSize && si < 64) {
                                    Element el2 = (Element) stack2.get(si);
                                    if (el2 == formatEl) {
                                        seenFormattingElement = true;
                                        commonAncestor = (Element) stack2.get(si - 1);
                                    } else if (seenFormattingElement && htmlTreeBuilder.isSpecial(el2)) {
                                        furthestBlock = el2;
                                    }
                                    si++;
                                }
                                if (furthestBlock == null) {
                                    htmlTreeBuilder.popStackToClose(formatEl.nodeName());
                                    htmlTreeBuilder.removeFromActiveFormattingElements(formatEl);
                                    return z;
                                }
                                ArrayList<Element> stack3;
                                Node[] childNodes;
                                int length;
                                Element node;
                                html = furthestBlock;
                                Element lastNode = furthestBlock;
                                int j = 0;
                                while (j < 3) {
                                    boolean seenFormattingElement2;
                                    if (htmlTreeBuilder.onStack(html)) {
                                        html = htmlTreeBuilder.aboveOnStack(html);
                                    }
                                    if (!htmlTreeBuilder.isInActiveFormattingElements(html)) {
                                        htmlTreeBuilder.removeFromStack(html);
                                        seenFormattingElement2 = seenFormattingElement;
                                        stack3 = stack2;
                                    } else if (html == formatEl) {
                                        seenFormattingElement2 = seenFormattingElement;
                                        stack3 = stack2;
                                        if (StringUtil.inSorted(commonAncestor.nodeName(), Constants.InBodyEndTableFosters)) {
                                            if (lastNode.parent() != null) {
                                                lastNode.remove();
                                            }
                                            commonAncestor.appendChild(lastNode);
                                        } else {
                                            if (lastNode.parent() != null) {
                                                lastNode.remove();
                                            }
                                            htmlTreeBuilder.insertInFosterParent(lastNode);
                                        }
                                        adopter = new Element(formatEl.tag(), tb.getBaseUri());
                                        adopter.attributes().addAll(formatEl.attributes());
                                        childNodes = (Node[]) furthestBlock.childNodes().toArray(new Node[furthestBlock.childNodeSize()]);
                                        length = childNodes.length;
                                        j = 0;
                                        while (j < length) {
                                            node = html;
                                            adopter.appendChild(childNodes[j]);
                                            j++;
                                            html = node;
                                        }
                                        furthestBlock.appendChild(adopter);
                                        htmlTreeBuilder.removeFromActiveFormattingElements(formatEl);
                                        htmlTreeBuilder.removeFromStack(formatEl);
                                        htmlTreeBuilder.insertOnStackAfter(furthestBlock, adopter);
                                        i2++;
                                        z = true;
                                        z2 = false;
                                    } else {
                                        seenFormattingElement2 = seenFormattingElement;
                                        stack3 = stack2;
                                        adopter = new Element(Tag.valueOf(html.nodeName(), ParseSettings.preserveCase), tb.getBaseUri());
                                        htmlTreeBuilder.replaceActiveFormattingElement(html, adopter);
                                        htmlTreeBuilder.replaceOnStack(html, adopter);
                                        html = adopter;
                                        if (lastNode.parent() != null) {
                                            lastNode.remove();
                                        }
                                        html.appendChild(lastNode);
                                        lastNode = html;
                                    }
                                    j++;
                                    seenFormattingElement = seenFormattingElement2;
                                    stack2 = stack3;
                                }
                                stack3 = stack2;
                                if (StringUtil.inSorted(commonAncestor.nodeName(), Constants.InBodyEndTableFosters)) {
                                    if (lastNode.parent() != null) {
                                        lastNode.remove();
                                    }
                                    commonAncestor.appendChild(lastNode);
                                } else {
                                    if (lastNode.parent() != null) {
                                        lastNode.remove();
                                    }
                                    htmlTreeBuilder.insertInFosterParent(lastNode);
                                }
                                adopter = new Element(formatEl.tag(), tb.getBaseUri());
                                adopter.attributes().addAll(formatEl.attributes());
                                childNodes = (Node[]) furthestBlock.childNodes().toArray(new Node[furthestBlock.childNodeSize()]);
                                length = childNodes.length;
                                j = 0;
                                while (j < length) {
                                    node = html;
                                    adopter.appendChild(childNodes[j]);
                                    j++;
                                    html = node;
                                }
                                furthestBlock.appendChild(adopter);
                                htmlTreeBuilder.removeFromActiveFormattingElements(formatEl);
                                htmlTreeBuilder.removeFromStack(formatEl);
                                htmlTreeBuilder.insertOnStackAfter(furthestBlock, adopter);
                                i2++;
                                z = true;
                                z2 = false;
                            } else {
                                htmlTreeBuilder.error(htmlTreeBuilderState);
                                return z2;
                            }
                        }
                        break;
                    } else if (StringUtil.inSorted(name2, Constants.InBodyEndClosers)) {
                        if (htmlTreeBuilder.inScope(name2)) {
                            tb.generateImpliedEndTags();
                            if (!tb.currentElement().nodeName().equals(name2)) {
                                htmlTreeBuilder.error(htmlTreeBuilderState);
                            }
                            htmlTreeBuilder.popStackToClose(name2);
                            break;
                        }
                        htmlTreeBuilder.error(htmlTreeBuilderState);
                        return false;
                    } else if (name2.equals(TtmlNode.TAG_SPAN)) {
                        return anyOtherEndTag(t, tb);
                    } else {
                        if (name2.equals("li")) {
                            if (htmlTreeBuilder.inListItemScope(name2)) {
                                htmlTreeBuilder.generateImpliedEndTags(name2);
                                if (!tb.currentElement().nodeName().equals(name2)) {
                                    htmlTreeBuilder.error(htmlTreeBuilderState);
                                }
                                htmlTreeBuilder.popStackToClose(name2);
                                break;
                            }
                            htmlTreeBuilder.error(htmlTreeBuilderState);
                            return false;
                        } else if (name2.equals("body")) {
                            if (htmlTreeBuilder.inScope("body")) {
                                htmlTreeBuilder.transition(AfterBody);
                                break;
                            }
                            htmlTreeBuilder.error(htmlTreeBuilderState);
                            return false;
                        } else if (name2.equals("html")) {
                            if (!htmlTreeBuilder.processEndTag("body")) {
                                break;
                            }
                            return htmlTreeBuilder.process(endTag);
                        } else if (name2.equals("form")) {
                            adopter = tb.getFormElement();
                            htmlTreeBuilder.setFormElement(null);
                            if (adopter != null) {
                                if (htmlTreeBuilder.inScope(name2)) {
                                    tb.generateImpliedEndTags();
                                    if (!tb.currentElement().nodeName().equals(name2)) {
                                        htmlTreeBuilder.error(htmlTreeBuilderState);
                                    }
                                    htmlTreeBuilder.removeFromStack(adopter);
                                    break;
                                }
                            }
                            htmlTreeBuilder.error(htmlTreeBuilderState);
                            return false;
                        } else if (name2.equals(TtmlNode.TAG_P)) {
                            if (htmlTreeBuilder.inButtonScope(name2)) {
                                htmlTreeBuilder.generateImpliedEndTags(name2);
                                if (!tb.currentElement().nodeName().equals(name2)) {
                                    htmlTreeBuilder.error(htmlTreeBuilderState);
                                }
                                htmlTreeBuilder.popStackToClose(name2);
                                break;
                            }
                            htmlTreeBuilder.error(htmlTreeBuilderState);
                            htmlTreeBuilder.processStartTag(name2);
                            return htmlTreeBuilder.process(endTag);
                        } else if (StringUtil.inSorted(name2, Constants.DdDt)) {
                            if (htmlTreeBuilder.inScope(name2)) {
                                htmlTreeBuilder.generateImpliedEndTags(name2);
                                if (!tb.currentElement().nodeName().equals(name2)) {
                                    htmlTreeBuilder.error(htmlTreeBuilderState);
                                }
                                htmlTreeBuilder.popStackToClose(name2);
                                break;
                            }
                            htmlTreeBuilder.error(htmlTreeBuilderState);
                            return false;
                        } else if (StringUtil.inSorted(name2, Constants.Headings)) {
                            if (htmlTreeBuilder.inScope(Constants.Headings)) {
                                htmlTreeBuilder.generateImpliedEndTags(name2);
                                if (!tb.currentElement().nodeName().equals(name2)) {
                                    htmlTreeBuilder.error(htmlTreeBuilderState);
                                }
                                htmlTreeBuilder.popStackToClose(Constants.Headings);
                                break;
                            }
                            htmlTreeBuilder.error(htmlTreeBuilderState);
                            return false;
                        } else if (name2.equals("sarcasm")) {
                            return anyOtherEndTag(t, tb);
                        } else {
                            if (StringUtil.inSorted(name2, Constants.InBodyStartApplets)) {
                                if (!htmlTreeBuilder.inScope(PodDBAdapter.KEY_NAME)) {
                                    if (htmlTreeBuilder.inScope(name2)) {
                                        tb.generateImpliedEndTags();
                                        if (!tb.currentElement().nodeName().equals(name2)) {
                                            htmlTreeBuilder.error(htmlTreeBuilderState);
                                        }
                                        htmlTreeBuilder.popStackToClose(name2);
                                        tb.clearFormattingElementsToLastMarker();
                                        break;
                                    }
                                    htmlTreeBuilder.error(htmlTreeBuilderState);
                                    return false;
                                }
                                break;
                            } else if (!name2.equals(TtmlNode.TAG_BR)) {
                                return anyOtherEndTag(t, tb);
                            } else {
                                htmlTreeBuilder.error(htmlTreeBuilderState);
                                htmlTreeBuilder.processStartTag(TtmlNode.TAG_BR);
                                return false;
                            }
                        }
                    }
                    break;
                case Character:
                    Character c = t.asCharacter();
                    if (!c.getData().equals(HtmlTreeBuilderState.nullString)) {
                        if (!tb.framesetOk() || !HtmlTreeBuilderState.isWhitespace((Token) c)) {
                            tb.reconstructFormattingElements();
                            htmlTreeBuilder.insert(c);
                            htmlTreeBuilder.framesetOk(false);
                            break;
                        }
                        tb.reconstructFormattingElements();
                        htmlTreeBuilder.insert(c);
                        break;
                    }
                    htmlTreeBuilder.error(htmlTreeBuilderState);
                    return false;
                    break;
                default:
                    break;
            }
            return true;
        }

        boolean anyOtherEndTag(Token t, HtmlTreeBuilder tb) {
            String name = tb.settings.normalizeTag(t.asEndTag().name());
            ArrayList<Element> stack = tb.getStack();
            int pos = stack.size() - 1;
            while (pos >= 0) {
                Element node = (Element) stack.get(pos);
                if (node.nodeName().equals(name)) {
                    tb.generateImpliedEndTags(name);
                    if (!name.equals(tb.currentElement().nodeName())) {
                        tb.error(this);
                    }
                    tb.popStackToClose(name);
                    return true;
                } else if (tb.isSpecial(node)) {
                    tb.error(this);
                    return false;
                } else {
                    pos--;
                }
            }
            return true;
        }
    },
    Text {
        boolean process(Token t, HtmlTreeBuilder tb) {
            if (t.isCharacter()) {
                tb.insert(t.asCharacter());
            } else if (t.isEOF()) {
                tb.error(this);
                tb.pop();
                tb.transition(tb.originalState());
                return tb.process(t);
            } else if (t.isEndTag()) {
                tb.pop();
                tb.transition(tb.originalState());
            }
            return true;
        }
    },
    InTable {
        boolean process(Token t, HtmlTreeBuilder tb) {
            if (t.isCharacter()) {
                tb.newPendingTableCharacters();
                tb.markInsertionMode();
                tb.transition(InTableText);
                return tb.process(t);
            } else if (t.isComment()) {
                tb.insert(t.asComment());
                return true;
            } else if (t.isDoctype()) {
                tb.error(this);
                return false;
            } else if (t.isStartTag()) {
                StartTag startTag = t.asStartTag();
                name = startTag.normalName();
                if (name.equals("caption")) {
                    tb.clearStackToTableContext();
                    tb.insertMarkerToFormattingElements();
                    tb.insert(startTag);
                    tb.transition(InCaption);
                } else if (name.equals("colgroup")) {
                    tb.clearStackToTableContext();
                    tb.insert(startTag);
                    tb.transition(InColumnGroup);
                } else if (name.equals("col")) {
                    tb.processStartTag("colgroup");
                    return tb.process(t);
                } else {
                    if (StringUtil.in(name, "tbody", "tfoot", "thead")) {
                        tb.clearStackToTableContext();
                        tb.insert(startTag);
                        tb.transition(InTableBody);
                    } else {
                        if (StringUtil.in(name, "td", "th", "tr")) {
                            tb.processStartTag("tbody");
                            return tb.process(t);
                        } else if (name.equals("table")) {
                            tb.error(this);
                            if (tb.processEndTag("table")) {
                                return tb.process(t);
                            }
                        } else {
                            if (StringUtil.in(name, TtmlNode.TAG_STYLE, "script")) {
                                return tb.process(t, InHead);
                            }
                            if (name.equals("input")) {
                                if (!startTag.attributes.get("type").equalsIgnoreCase("hidden")) {
                                    return anythingElse(t, tb);
                                }
                                tb.insertEmpty(startTag);
                            } else if (!name.equals("form")) {
                                return anythingElse(t, tb);
                            } else {
                                tb.error(this);
                                if (tb.getFormElement() != null) {
                                    return false;
                                }
                                tb.insertForm(startTag, false);
                            }
                        }
                    }
                }
                return true;
            } else if (t.isEndTag()) {
                name = t.asEndTag().normalName();
                if (!name.equals("table")) {
                    if (!StringUtil.in(name, "body", "caption", "col", "colgroup", "html", "tbody", "td", "tfoot", "th", "thead", "tr")) {
                        return anythingElse(t, tb);
                    }
                    tb.error(this);
                    return false;
                } else if (tb.inTableScope(name)) {
                    tb.popStackToClose("table");
                    tb.resetInsertionMode();
                    return true;
                } else {
                    tb.error(this);
                    return false;
                }
            } else if (!t.isEOF()) {
                return anythingElse(t, tb);
            } else {
                if (tb.currentElement().nodeName().equals("html")) {
                    tb.error(this);
                }
                return true;
            }
        }

        boolean anythingElse(Token t, HtmlTreeBuilder tb) {
            tb.error(this);
            if (!StringUtil.in(tb.currentElement().nodeName(), "table", "tbody", "tfoot", "thead", "tr")) {
                return tb.process(t, InBody);
            }
            tb.setFosterInserts(true);
            boolean processed = tb.process(t, InBody);
            tb.setFosterInserts(false);
            return processed;
        }
    },
    InTableText {
        boolean process(Token t, HtmlTreeBuilder tb) {
            if (AnonymousClass24.$SwitchMap$org$jsoup$parser$Token$TokenType[t.type.ordinal()] != 5) {
                if (tb.getPendingTableCharacters().size() > 0) {
                    for (String character : tb.getPendingTableCharacters()) {
                        if (HtmlTreeBuilderState.isWhitespace(character)) {
                            tb.insert(new Character().data(character));
                        } else {
                            tb.error(this);
                            if (StringUtil.in(tb.currentElement().nodeName(), "table", "tbody", "tfoot", "thead", "tr")) {
                                tb.setFosterInserts(true);
                                tb.process(new Character().data(character), InBody);
                                tb.setFosterInserts(false);
                            } else {
                                tb.process(new Character().data(character), InBody);
                            }
                        }
                    }
                    tb.newPendingTableCharacters();
                }
                tb.transition(tb.originalState());
                return tb.process(t);
            }
            Character c = t.asCharacter();
            if (c.getData().equals(HtmlTreeBuilderState.nullString)) {
                tb.error(this);
                return false;
            }
            tb.getPendingTableCharacters().add(c.getData());
            return true;
        }
    },
    InCaption {
        boolean process(Token t, HtmlTreeBuilder tb) {
            if (!t.isEndTag() || !t.asEndTag().normalName().equals("caption")) {
                if (t.isStartTag()) {
                    if (StringUtil.in(t.asStartTag().normalName(), "caption", "col", "colgroup", "tbody", "td", "tfoot", "th", "thead", "tr")) {
                        tb.error(this);
                        if (tb.processEndTag("caption")) {
                            return tb.process(t);
                        }
                    }
                }
                if (!(t.isEndTag() && t.asEndTag().normalName().equals("table"))) {
                    if (t.isEndTag()) {
                        if (StringUtil.in(t.asEndTag().normalName(), "body", "col", "colgroup", "html", "tbody", "td", "tfoot", "th", "thead", "tr")) {
                            tb.error(this);
                            return false;
                        }
                    }
                    return tb.process(t, InBody);
                }
                tb.error(this);
                if (tb.processEndTag("caption")) {
                    return tb.process(t);
                }
            } else if (tb.inTableScope(t.asEndTag().normalName())) {
                tb.generateImpliedEndTags();
                if (!tb.currentElement().nodeName().equals("caption")) {
                    tb.error(this);
                }
                tb.popStackToClose("caption");
                tb.clearFormattingElementsToLastMarker();
                tb.transition(InTable);
            } else {
                tb.error(this);
                return false;
            }
            return true;
        }
    },
    InColumnGroup {
        boolean process(Token t, HtmlTreeBuilder tb) {
            if (HtmlTreeBuilderState.isWhitespace(t)) {
                tb.insert(t.asCharacter());
                return true;
            }
            int i = AnonymousClass24.$SwitchMap$org$jsoup$parser$Token$TokenType[t.type.ordinal()];
            if (i != 6) {
                boolean z = false;
                switch (i) {
                    case 1:
                        tb.insert(t.asComment());
                        break;
                    case 2:
                        tb.error(this);
                        break;
                    case 3:
                        StartTag startTag = t.asStartTag();
                        String normalName = startTag.normalName();
                        int hashCode = normalName.hashCode();
                        if (hashCode != 98688) {
                            if (hashCode == 3213227 && normalName.equals("html")) {
                                switch (z) {
                                    case false:
                                        return tb.process(t, InBody);
                                    case true:
                                        tb.insertEmpty(startTag);
                                        break;
                                    default:
                                        return anythingElse(t, tb);
                                }
                            }
                        } else if (normalName.equals("col")) {
                            z = true;
                            switch (z) {
                                case false:
                                    return tb.process(t, InBody);
                                case true:
                                    tb.insertEmpty(startTag);
                                    break;
                                default:
                                    return anythingElse(t, tb);
                            }
                        }
                        z = true;
                        switch (z) {
                            case false:
                                return tb.process(t, InBody);
                            case true:
                                tb.insertEmpty(startTag);
                                break;
                            default:
                                return anythingElse(t, tb);
                        }
                    case 4:
                        if (t.asEndTag().normalName.equals("colgroup")) {
                            if (!tb.currentElement().nodeName().equals("html")) {
                                tb.pop();
                                tb.transition(InTable);
                                break;
                            }
                            tb.error(this);
                            return false;
                        }
                        return anythingElse(t, tb);
                    default:
                        return anythingElse(t, tb);
                }
                return true;
            } else if (tb.currentElement().nodeName().equals("html")) {
                return true;
            } else {
                return anythingElse(t, tb);
            }
        }

        private boolean anythingElse(Token t, TreeBuilder tb) {
            if (tb.processEndTag("colgroup")) {
                return tb.process(t);
            }
            return true;
        }
    },
    InTableBody {
        boolean process(Token t, HtmlTreeBuilder tb) {
            String name;
            switch (t.type) {
                case StartTag:
                    StartTag startTag = t.asStartTag();
                    name = startTag.normalName();
                    if (name.equals("template")) {
                        tb.insert(startTag);
                        break;
                    } else if (name.equals("tr")) {
                        tb.clearStackToTableBodyContext();
                        tb.insert(startTag);
                        tb.transition(InRow);
                        break;
                    } else {
                        if (StringUtil.in(name, "th", "td")) {
                            tb.error(this);
                            tb.processStartTag("tr");
                            return tb.process(startTag);
                        }
                        if (StringUtil.in(name, "caption", "col", "colgroup", "tbody", "tfoot", "thead")) {
                            return exitTableBody(t, tb);
                        }
                        return anythingElse(t, tb);
                    }
                case EndTag:
                    name = t.asEndTag().normalName();
                    if (StringUtil.in(name, "tbody", "tfoot", "thead")) {
                        if (tb.inTableScope(name)) {
                            tb.clearStackToTableBodyContext();
                            tb.pop();
                            tb.transition(InTable);
                            break;
                        }
                        tb.error(this);
                        return false;
                    } else if (name.equals("table")) {
                        return exitTableBody(t, tb);
                    } else {
                        if (!StringUtil.in(name, "body", "caption", "col", "colgroup", "html", "td", "th", "tr")) {
                            return anythingElse(t, tb);
                        }
                        tb.error(this);
                        return false;
                    }
                default:
                    return anythingElse(t, tb);
            }
            return true;
        }

        private boolean exitTableBody(Token t, HtmlTreeBuilder tb) {
            if (tb.inTableScope("tbody") || tb.inTableScope("thead") || tb.inScope("tfoot")) {
                tb.clearStackToTableBodyContext();
                tb.processEndTag(tb.currentElement().nodeName());
                return tb.process(t);
            }
            tb.error(this);
            return false;
        }

        private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
            return tb.process(t, InTable);
        }
    },
    InRow {
        boolean process(Token t, HtmlTreeBuilder tb) {
            String name;
            if (t.isStartTag()) {
                StartTag startTag = t.asStartTag();
                name = startTag.normalName();
                if (name.equals("template")) {
                    tb.insert(startTag);
                } else {
                    if (StringUtil.in(name, "th", "td")) {
                        tb.clearStackToTableRowContext();
                        tb.insert(startTag);
                        tb.transition(InCell);
                        tb.insertMarkerToFormattingElements();
                    } else {
                        if (StringUtil.in(name, "caption", "col", "colgroup", "tbody", "tfoot", "thead", "tr")) {
                            return handleMissingTr(t, tb);
                        }
                        return anythingElse(t, tb);
                    }
                }
            } else if (!t.isEndTag()) {
                return anythingElse(t, tb);
            } else {
                name = t.asEndTag().normalName();
                if (name.equals("tr")) {
                    if (tb.inTableScope(name)) {
                        tb.clearStackToTableRowContext();
                        tb.pop();
                        tb.transition(InTableBody);
                    } else {
                        tb.error(this);
                        return false;
                    }
                } else if (name.equals("table")) {
                    return handleMissingTr(t, tb);
                } else {
                    if (!StringUtil.in(name, "tbody", "tfoot", "thead")) {
                        if (!StringUtil.in(name, "body", "caption", "col", "colgroup", "html", "td", "th")) {
                            return anythingElse(t, tb);
                        }
                        tb.error(this);
                        return false;
                    } else if (tb.inTableScope(name)) {
                        tb.processEndTag("tr");
                        return tb.process(t);
                    } else {
                        tb.error(this);
                        return false;
                    }
                }
            }
            return true;
        }

        private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
            return tb.process(t, InTable);
        }

        private boolean handleMissingTr(Token t, TreeBuilder tb) {
            if (tb.processEndTag("tr")) {
                return tb.process(t);
            }
            return false;
        }
    },
    InCell {
        boolean process(Token t, HtmlTreeBuilder tb) {
            if (t.isEndTag()) {
                String name = t.asEndTag().normalName();
                if (!StringUtil.in(name, "td", "th")) {
                    if (StringUtil.in(name, "body", "caption", "col", "colgroup", "html")) {
                        tb.error(this);
                        return false;
                    }
                    if (!StringUtil.in(name, "table", "tbody", "tfoot", "thead", "tr")) {
                        return anythingElse(t, tb);
                    }
                    if (tb.inTableScope(name)) {
                        closeCell(tb);
                        return tb.process(t);
                    }
                    tb.error(this);
                    return false;
                } else if (tb.inTableScope(name)) {
                    tb.generateImpliedEndTags();
                    if (!tb.currentElement().nodeName().equals(name)) {
                        tb.error(this);
                    }
                    tb.popStackToClose(name);
                    tb.clearFormattingElementsToLastMarker();
                    tb.transition(InRow);
                    return true;
                } else {
                    tb.error(this);
                    tb.transition(InRow);
                    return false;
                }
            }
            if (t.isStartTag()) {
                if (StringUtil.in(t.asStartTag().normalName(), "caption", "col", "colgroup", "tbody", "td", "tfoot", "th", "thead", "tr")) {
                    if (tb.inTableScope("td") || tb.inTableScope("th")) {
                        closeCell(tb);
                        return tb.process(t);
                    }
                    tb.error(this);
                    return false;
                }
            }
            return anythingElse(t, tb);
        }

        private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
            return tb.process(t, InBody);
        }

        private void closeCell(HtmlTreeBuilder tb) {
            if (tb.inTableScope("td")) {
                tb.processEndTag("td");
            } else {
                tb.processEndTag("th");
            }
        }
    },
    InSelect {
        boolean process(Token t, HtmlTreeBuilder tb) {
            int i = 2;
            String name;
            switch (t.type) {
                case Comment:
                    tb.insert(t.asComment());
                    break;
                case Doctype:
                    tb.error(this);
                    return false;
                case StartTag:
                    StartTag start = t.asStartTag();
                    name = start.normalName();
                    if (name.equals("html")) {
                        return tb.process(start, InBody);
                    }
                    if (name.equals("option")) {
                        if (tb.currentElement().nodeName().equals("option")) {
                            tb.processEndTag("option");
                        }
                        tb.insert(start);
                        break;
                    } else if (name.equals("optgroup")) {
                        if (tb.currentElement().nodeName().equals("option")) {
                            tb.processEndTag("option");
                        } else if (tb.currentElement().nodeName().equals("optgroup")) {
                            tb.processEndTag("optgroup");
                        }
                        tb.insert(start);
                        break;
                    } else if (name.equals("select")) {
                        tb.error(this);
                        return tb.processEndTag("select");
                    } else {
                        if (StringUtil.in(name, "input", "keygen", "textarea")) {
                            tb.error(this);
                            if (!tb.inSelectScope("select")) {
                                return false;
                            }
                            tb.processEndTag("select");
                            return tb.process(start);
                        } else if (name.equals("script")) {
                            return tb.process(t, InHead);
                        } else {
                            return anythingElse(t, tb);
                        }
                    }
                case EndTag:
                    name = t.asEndTag().normalName();
                    int hashCode = name.hashCode();
                    if (hashCode != -1010136971) {
                        if (hashCode != -906021636) {
                            if (hashCode == -80773204 && name.equals("optgroup")) {
                                i = 0;
                                switch (i) {
                                    case 0:
                                        if (!tb.currentElement().nodeName().equals("option") && tb.aboveOnStack(tb.currentElement()) != null && tb.aboveOnStack(tb.currentElement()).nodeName().equals("optgroup")) {
                                            tb.processEndTag("option");
                                        }
                                        if (!tb.currentElement().nodeName().equals("optgroup")) {
                                            tb.pop();
                                            break;
                                        }
                                        tb.error(this);
                                        break;
                                        break;
                                    case 1:
                                        if (!tb.currentElement().nodeName().equals("option")) {
                                            tb.pop();
                                            break;
                                        }
                                        tb.error(this);
                                        break;
                                    case 2:
                                        if (tb.inSelectScope(name)) {
                                            tb.popStackToClose(name);
                                            tb.resetInsertionMode();
                                            break;
                                        }
                                        tb.error(this);
                                        return false;
                                    default:
                                        return anythingElse(t, tb);
                                }
                                break;
                            }
                        } else if (name.equals("select")) {
                            switch (i) {
                                case 0:
                                    if (!tb.currentElement().nodeName().equals("option")) {
                                        break;
                                    }
                                    if (!tb.currentElement().nodeName().equals("optgroup")) {
                                        tb.error(this);
                                        break;
                                    }
                                    tb.pop();
                                    break;
                                case 1:
                                    if (!tb.currentElement().nodeName().equals("option")) {
                                        tb.error(this);
                                        break;
                                    }
                                    tb.pop();
                                    break;
                                case 2:
                                    if (tb.inSelectScope(name)) {
                                        tb.popStackToClose(name);
                                        tb.resetInsertionMode();
                                        break;
                                    }
                                    tb.error(this);
                                    return false;
                                default:
                                    return anythingElse(t, tb);
                            }
                        }
                    } else if (name.equals("option")) {
                        i = 1;
                        switch (i) {
                            case 0:
                                if (!tb.currentElement().nodeName().equals("option")) {
                                    break;
                                }
                                if (!tb.currentElement().nodeName().equals("optgroup")) {
                                    tb.pop();
                                    break;
                                }
                                tb.error(this);
                                break;
                            case 1:
                                if (!tb.currentElement().nodeName().equals("option")) {
                                    tb.pop();
                                    break;
                                }
                                tb.error(this);
                                break;
                            case 2:
                                if (tb.inSelectScope(name)) {
                                    tb.popStackToClose(name);
                                    tb.resetInsertionMode();
                                    break;
                                }
                                tb.error(this);
                                return false;
                            default:
                                return anythingElse(t, tb);
                        }
                    }
                    i = -1;
                    switch (i) {
                        case 0:
                            if (!tb.currentElement().nodeName().equals("option")) {
                                break;
                            }
                            if (!tb.currentElement().nodeName().equals("optgroup")) {
                                tb.error(this);
                                break;
                            }
                            tb.pop();
                            break;
                        case 1:
                            if (!tb.currentElement().nodeName().equals("option")) {
                                tb.error(this);
                                break;
                            }
                            tb.pop();
                            break;
                        case 2:
                            if (tb.inSelectScope(name)) {
                                tb.popStackToClose(name);
                                tb.resetInsertionMode();
                                break;
                            }
                            tb.error(this);
                            return false;
                        default:
                            return anythingElse(t, tb);
                    }
                case Character:
                    Character c = t.asCharacter();
                    if (!c.getData().equals(HtmlTreeBuilderState.nullString)) {
                        tb.insert(c);
                        break;
                    }
                    tb.error(this);
                    return false;
                case EOF:
                    if (!tb.currentElement().nodeName().equals("html")) {
                        tb.error(this);
                        break;
                    }
                    break;
                default:
                    return anythingElse(t, tb);
            }
            return true;
        }

        private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
            tb.error(this);
            return false;
        }
    },
    InSelectInTable {
        boolean process(Token t, HtmlTreeBuilder tb) {
            if (t.isStartTag()) {
                if (StringUtil.in(t.asStartTag().normalName(), "caption", "table", "tbody", "tfoot", "thead", "tr", "td", "th")) {
                    tb.error(this);
                    tb.processEndTag("select");
                    return tb.process(t);
                }
            }
            if (t.isEndTag()) {
                if (StringUtil.in(t.asEndTag().normalName(), "caption", "table", "tbody", "tfoot", "thead", "tr", "td", "th")) {
                    tb.error(this);
                    if (!tb.inTableScope(t.asEndTag().normalName())) {
                        return false;
                    }
                    tb.processEndTag("select");
                    return tb.process(t);
                }
            }
            return tb.process(t, InSelect);
        }
    },
    AfterBody {
        boolean process(Token t, HtmlTreeBuilder tb) {
            if (HtmlTreeBuilderState.isWhitespace(t)) {
                return tb.process(t, InBody);
            }
            if (t.isComment()) {
                tb.insert(t.asComment());
            } else if (t.isDoctype()) {
                tb.error(this);
                return false;
            } else if (t.isStartTag() && t.asStartTag().normalName().equals("html")) {
                return tb.process(t, InBody);
            } else {
                if (t.isEndTag() && t.asEndTag().normalName().equals("html")) {
                    if (tb.isFragmentParsing()) {
                        tb.error(this);
                        return false;
                    }
                    tb.transition(AfterAfterBody);
                } else if (!t.isEOF()) {
                    tb.error(this);
                    tb.transition(InBody);
                    return tb.process(t);
                }
            }
            return true;
        }
    },
    InFrameset {
        boolean process(Token t, HtmlTreeBuilder tb) {
            if (HtmlTreeBuilderState.isWhitespace(t)) {
                tb.insert(t.asCharacter());
            } else if (t.isComment()) {
                tb.insert(t.asComment());
            } else if (t.isDoctype()) {
                tb.error(this);
                return false;
            } else if (t.isStartTag()) {
                Object obj;
                StartTag start = t.asStartTag();
                String normalName = start.normalName();
                int hashCode = normalName.hashCode();
                if (hashCode != -1644953643) {
                    if (hashCode != 3213227) {
                        if (hashCode != 97692013) {
                            if (hashCode == 1192721831 && normalName.equals("noframes")) {
                                obj = 3;
                                switch (obj) {
                                    case null:
                                        return tb.process(start, InBody);
                                    case 1:
                                        tb.insert(start);
                                        break;
                                    case 2:
                                        tb.insertEmpty(start);
                                        break;
                                    case 3:
                                        return tb.process(start, InHead);
                                    default:
                                        tb.error(this);
                                        return false;
                                }
                            }
                        } else if (normalName.equals("frame")) {
                            obj = 2;
                            switch (obj) {
                                case null:
                                    return tb.process(start, InBody);
                                case 1:
                                    tb.insert(start);
                                    break;
                                case 2:
                                    tb.insertEmpty(start);
                                    break;
                                case 3:
                                    return tb.process(start, InHead);
                                default:
                                    tb.error(this);
                                    return false;
                            }
                        }
                    } else if (normalName.equals("html")) {
                        obj = null;
                        switch (obj) {
                            case null:
                                return tb.process(start, InBody);
                            case 1:
                                tb.insert(start);
                                break;
                            case 2:
                                tb.insertEmpty(start);
                                break;
                            case 3:
                                return tb.process(start, InHead);
                            default:
                                tb.error(this);
                                return false;
                        }
                    }
                } else if (normalName.equals("frameset")) {
                    obj = 1;
                    switch (obj) {
                        case null:
                            return tb.process(start, InBody);
                        case 1:
                            tb.insert(start);
                            break;
                        case 2:
                            tb.insertEmpty(start);
                            break;
                        case 3:
                            return tb.process(start, InHead);
                        default:
                            tb.error(this);
                            return false;
                    }
                }
                obj = -1;
                switch (obj) {
                    case null:
                        return tb.process(start, InBody);
                    case 1:
                        tb.insert(start);
                        break;
                    case 2:
                        tb.insertEmpty(start);
                        break;
                    case 3:
                        return tb.process(start, InHead);
                    default:
                        tb.error(this);
                        return false;
                }
            } else if (t.isEndTag() && t.asEndTag().normalName().equals("frameset")) {
                if (tb.currentElement().nodeName().equals("html")) {
                    tb.error(this);
                    return false;
                }
                tb.pop();
                if (!tb.isFragmentParsing() && !tb.currentElement().nodeName().equals("frameset")) {
                    tb.transition(AfterFrameset);
                }
            } else if (!t.isEOF()) {
                tb.error(this);
                return false;
            } else if (!tb.currentElement().nodeName().equals("html")) {
                tb.error(this);
                return true;
            }
            return true;
        }
    },
    AfterFrameset {
        boolean process(Token t, HtmlTreeBuilder tb) {
            if (HtmlTreeBuilderState.isWhitespace(t)) {
                tb.insert(t.asCharacter());
            } else if (t.isComment()) {
                tb.insert(t.asComment());
            } else if (t.isDoctype()) {
                tb.error(this);
                return false;
            } else if (t.isStartTag() && t.asStartTag().normalName().equals("html")) {
                return tb.process(t, InBody);
            } else {
                if (t.isEndTag() && t.asEndTag().normalName().equals("html")) {
                    tb.transition(AfterAfterFrameset);
                } else if (t.isStartTag() && t.asStartTag().normalName().equals("noframes")) {
                    return tb.process(t, InHead);
                } else {
                    if (!t.isEOF()) {
                        tb.error(this);
                        return false;
                    }
                }
            }
            return true;
        }
    },
    AfterAfterBody {
        boolean process(Token t, HtmlTreeBuilder tb) {
            if (t.isComment()) {
                tb.insert(t.asComment());
            } else {
                if (!(t.isDoctype() || HtmlTreeBuilderState.isWhitespace(t))) {
                    if (!t.isStartTag() || !t.asStartTag().normalName().equals("html")) {
                        if (!t.isEOF()) {
                            tb.error(this);
                            tb.transition(InBody);
                            return tb.process(t);
                        }
                    }
                }
                return tb.process(t, InBody);
            }
            return true;
        }
    },
    AfterAfterFrameset {
        boolean process(Token t, HtmlTreeBuilder tb) {
            if (t.isComment()) {
                tb.insert(t.asComment());
            } else {
                if (!(t.isDoctype() || HtmlTreeBuilderState.isWhitespace(t))) {
                    if (!t.isStartTag() || !t.asStartTag().normalName().equals("html")) {
                        if (!t.isEOF()) {
                            if (t.isStartTag() && t.asStartTag().normalName().equals("noframes")) {
                                return tb.process(t, InHead);
                            }
                            tb.error(this);
                            return false;
                        }
                    }
                }
                return tb.process(t, InBody);
            }
            return true;
        }
    },
    ForeignContent {
        boolean process(Token t, HtmlTreeBuilder tb) {
            return true;
        }
    };
    
    private static String nullString;

    static final class Constants {
        static final String[] DdDt = null;
        static final String[] Formatters = null;
        static final String[] Headings = null;
        static final String[] InBodyEndAdoptionFormatters = null;
        static final String[] InBodyEndClosers = null;
        static final String[] InBodyEndTableFosters = null;
        static final String[] InBodyStartApplets = null;
        static final String[] InBodyStartDrop = null;
        static final String[] InBodyStartEmptyFormatters = null;
        static final String[] InBodyStartInputAttribs = null;
        static final String[] InBodyStartLiBreakers = null;
        static final String[] InBodyStartMedia = null;
        static final String[] InBodyStartOptions = null;
        static final String[] InBodyStartPClosers = null;
        static final String[] InBodyStartPreListing = null;
        static final String[] InBodyStartRuby = null;
        static final String[] InBodyStartToHead = null;

        Constants() {
        }

        static {
            InBodyStartToHead = new String[]{"base", "basefont", "bgsound", "command", PodDBAdapter.KEY_LINK, "meta", "noframes", "script", TtmlNode.TAG_STYLE, "title"};
            InBodyStartPClosers = new String[]{"address", "article", "aside", "blockquote", TtmlNode.CENTER, "details", "dir", TtmlNode.TAG_DIV, "dl", "fieldset", "figcaption", "figure", "footer", "header", "hgroup", "menu", "nav", "ol", TtmlNode.TAG_P, "section", "summary", "ul"};
            Headings = new String[]{"h1", "h2", "h3", "h4", "h5", "h6"};
            InBodyStartPreListing = new String[]{"listing", "pre"};
            InBodyStartLiBreakers = new String[]{"address", TtmlNode.TAG_DIV, TtmlNode.TAG_P};
            DdDt = new String[]{"dd", "dt"};
            Formatters = new String[]{"b", "big", "code", "em", "font", "i", "s", "small", "strike", "strong", TtmlNode.TAG_TT, "u"};
            InBodyStartApplets = new String[]{"applet", "marquee", "object"};
            InBodyStartEmptyFormatters = new String[]{"area", TtmlNode.TAG_BR, "embed", "img", "keygen", "wbr"};
            InBodyStartMedia = new String[]{"param", "source", "track"};
            InBodyStartInputAttribs = new String[]{"action", PodDBAdapter.KEY_NAME, "prompt"};
            InBodyStartOptions = new String[]{"optgroup", "option"};
            InBodyStartRuby = new String[]{"rp", "rt"};
            InBodyStartDrop = new String[]{"caption", "col", "colgroup", "frame", "head", "tbody", "td", "tfoot", "th", "thead", "tr"};
            InBodyEndClosers = new String[]{"address", "article", "aside", "blockquote", "button", TtmlNode.CENTER, "details", "dir", TtmlNode.TAG_DIV, "dl", "fieldset", "figcaption", "figure", "footer", "header", "hgroup", "listing", "menu", "nav", "ol", "pre", "section", "summary", "ul"};
            InBodyEndAdoptionFormatters = new String[]{"a", "b", "big", "code", "em", "font", "i", "nobr", "s", "small", "strike", "strong", TtmlNode.TAG_TT, "u"};
            InBodyEndTableFosters = new String[]{"table", "tbody", "tfoot", "thead", "tr"};
        }
    }

    abstract boolean process(Token token, HtmlTreeBuilder htmlTreeBuilder);

    static {
        nullString = String.valueOf('\u0000');
    }

    private static boolean isWhitespace(Token t) {
        if (t.isCharacter()) {
            return isWhitespace(t.asCharacter().getData());
        }
        return false;
    }

    private static boolean isWhitespace(String data) {
        for (int i = 0; i < data.length(); i++) {
            if (!StringUtil.isWhitespace(data.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static void handleRcData(StartTag startTag, HtmlTreeBuilder tb) {
        tb.tokeniser.transition(TokeniserState.Rcdata);
        tb.markInsertionMode();
        tb.transition(Text);
        tb.insert(startTag);
    }

    private static void handleRawtext(StartTag startTag, HtmlTreeBuilder tb) {
        tb.tokeniser.transition(TokeniserState.Rawtext);
        tb.markInsertionMode();
        tb.transition(Text);
        tb.insert(startTag);
    }
}
