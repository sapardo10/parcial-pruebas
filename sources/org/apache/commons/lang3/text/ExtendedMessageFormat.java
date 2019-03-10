package org.apache.commons.lang3.text;

import java.text.Format;
import java.text.MessageFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.Validate;

@Deprecated
public class ExtendedMessageFormat extends MessageFormat {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final String DUMMY_PATTERN = "";
    private static final char END_FE = '}';
    private static final int HASH_SEED = 31;
    private static final char QUOTE = '\'';
    private static final char START_FE = '{';
    private static final char START_FMT = ',';
    private static final long serialVersionUID = -2362048321261811743L;
    private final Map<String, ? extends FormatFactory> registry;
    private String toPattern;

    private java.lang.StringBuilder appendQuotedString(java.lang.String r7, java.text.ParsePosition r8, java.lang.StringBuilder r9) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:17:0x0057 in {2, 3, 9, 12, 13, 14, 16} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r6 = this;
        r0 = 39;
        if (r9 == 0) goto L_0x0009;
    L_0x0005:
        r9.append(r0);
        goto L_0x000a;
    L_0x000a:
        r6.next(r8);
        r1 = r8.getIndex();
        r2 = r7.toCharArray();
        r3 = r1;
        r4 = r8.getIndex();
    L_0x001a:
        r5 = r7.length();
        if (r4 >= r5) goto L_0x003f;
    L_0x0020:
        r5 = r8.getIndex();
        r5 = r2[r5];
        if (r5 == r0) goto L_0x002e;
    L_0x0028:
        r6.next(r8);
        r4 = r4 + 1;
        goto L_0x001a;
    L_0x002e:
        r6.next(r8);
        if (r9 != 0) goto L_0x0035;
    L_0x0033:
        r0 = 0;
        goto L_0x003e;
    L_0x0035:
        r0 = r8.getIndex();
        r0 = r0 - r3;
        r9.append(r2, r3, r0);
        r0 = r9;
    L_0x003e:
        return r0;
        r0 = new java.lang.IllegalArgumentException;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "Unterminated quoted string at position ";
        r4.append(r5);
        r4.append(r1);
        r4 = r4.toString();
        r0.<init>(r4);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.text.ExtendedMessageFormat.appendQuotedString(java.lang.String, java.text.ParsePosition, java.lang.StringBuilder):java.lang.StringBuilder");
    }

    private java.lang.String parseFormatDescription(java.lang.String r7, java.text.ParsePosition r8) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:20:0x005b in {9, 13, 14, 15, 16, 17, 19} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r6 = this;
        r0 = r8.getIndex();
        r6.seekNonWs(r7, r8);
        r1 = r8.getIndex();
        r2 = 1;
    L_0x000c:
        r3 = r8.getIndex();
        r4 = r7.length();
        if (r3 >= r4) goto L_0x0044;
    L_0x0016:
        r3 = r8.getIndex();
        r3 = r7.charAt(r3);
        r4 = 39;
        if (r3 == r4) goto L_0x003c;
    L_0x0022:
        r4 = 123; // 0x7b float:1.72E-43 double:6.1E-322;
        if (r3 == r4) goto L_0x0039;
    L_0x0026:
        r4 = 125; // 0x7d float:1.75E-43 double:6.2E-322;
        if (r3 == r4) goto L_0x002b;
    L_0x002a:
        goto L_0x0040;
    L_0x002b:
        r2 = r2 + -1;
        if (r2 != 0) goto L_0x0038;
    L_0x002f:
        r3 = r8.getIndex();
        r3 = r7.substring(r1, r3);
        return r3;
    L_0x0038:
        goto L_0x0040;
    L_0x0039:
        r2 = r2 + 1;
        goto L_0x0040;
    L_0x003c:
        r6.getQuotedString(r7, r8);
    L_0x0040:
        r6.next(r8);
        goto L_0x000c;
    L_0x0044:
        r3 = new java.lang.IllegalArgumentException;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "Unterminated format element at position ";
        r4.append(r5);
        r4.append(r0);
        r4 = r4.toString();
        r3.<init>(r4);
        throw r3;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.text.ExtendedMessageFormat.parseFormatDescription(java.lang.String, java.text.ParsePosition):java.lang.String");
    }

    private int readArgumentIndex(java.lang.String r8, java.text.ParsePosition r9) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:29:0x00a2 in {9, 10, 11, 13, 18, 20, 21, 22, 23, 26, 28} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r7 = this;
        r0 = r9.getIndex();
        r7.seekNonWs(r8, r9);
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = 0;
    L_0x000d:
        if (r2 != 0) goto L_0x0062;
    L_0x000f:
        r3 = r9.getIndex();
        r4 = r8.length();
        if (r3 >= r4) goto L_0x0062;
    L_0x0019:
        r3 = r9.getIndex();
        r3 = r8.charAt(r3);
        r4 = java.lang.Character.isWhitespace(r3);
        r5 = 125; // 0x7d float:1.75E-43 double:6.2E-322;
        r6 = 44;
        if (r4 == 0) goto L_0x003d;
    L_0x002b:
        r7.seekNonWs(r8, r9);
        r4 = r9.getIndex();
        r3 = r8.charAt(r4);
        if (r3 == r6) goto L_0x003c;
    L_0x0038:
        if (r3 == r5) goto L_0x003c;
    L_0x003a:
        r2 = 1;
        goto L_0x005e;
    L_0x003c:
        goto L_0x003e;
    L_0x003e:
        if (r3 == r6) goto L_0x0042;
    L_0x0040:
        if (r3 != r5) goto L_0x0053;
    L_0x0042:
        r4 = r1.length();
        if (r4 <= 0) goto L_0x0053;
    L_0x0048:
        r4 = r1.toString();	 Catch:{ NumberFormatException -> 0x0051 }
        r4 = java.lang.Integer.parseInt(r4);	 Catch:{ NumberFormatException -> 0x0051 }
        return r4;
    L_0x0051:
        r4 = move-exception;
        goto L_0x0054;
    L_0x0054:
        r4 = java.lang.Character.isDigit(r3);
        r4 = r4 ^ 1;
        r2 = r4;
        r1.append(r3);
    L_0x005e:
        r7.next(r9);
        goto L_0x000d;
    L_0x0062:
        if (r2 == 0) goto L_0x008b;
    L_0x0064:
        r3 = new java.lang.IllegalArgumentException;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "Invalid format argument index at position ";
        r4.append(r5);
        r4.append(r0);
        r5 = ": ";
        r4.append(r5);
        r5 = r9.getIndex();
        r5 = r8.substring(r0, r5);
        r4.append(r5);
        r4 = r4.toString();
        r3.<init>(r4);
        throw r3;
    L_0x008b:
        r3 = new java.lang.IllegalArgumentException;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "Unterminated format element at position ";
        r4.append(r5);
        r4.append(r0);
        r4 = r4.toString();
        r3.<init>(r4);
        throw r3;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.text.ExtendedMessageFormat.readArgumentIndex(java.lang.String, java.text.ParsePosition):int");
    }

    public ExtendedMessageFormat(String pattern) {
        this(pattern, Locale.getDefault());
    }

    public ExtendedMessageFormat(String pattern, Locale locale) {
        this(pattern, locale, null);
    }

    public ExtendedMessageFormat(String pattern, Map<String, ? extends FormatFactory> registry) {
        this(pattern, Locale.getDefault(), registry);
    }

    public ExtendedMessageFormat(String pattern, Locale locale, Map<String, ? extends FormatFactory> registry) {
        super("");
        setLocale(locale);
        this.registry = registry;
        applyPattern(pattern);
    }

    public String toPattern() {
        return this.toPattern;
    }

    public final void applyPattern(String pattern) {
        if (this.registry == null) {
            super.applyPattern(pattern);
            this.toPattern = super.toPattern();
            return;
        }
        int start;
        ArrayList<Format> foundFormats = new ArrayList();
        ArrayList<String> foundDescriptions = new ArrayList();
        StringBuilder stripCustom = new StringBuilder(pattern.length());
        ParsePosition pos = new ParsePosition(0);
        char[] c = pattern.toCharArray();
        int fmtCount = 0;
        while (pos.getIndex() < pattern.length()) {
            char c2 = c[pos.getIndex()];
            if (c2 != QUOTE) {
                if (c2 == START_FE) {
                    fmtCount++;
                    seekNonWs(pattern, pos);
                    start = pos.getIndex();
                    int index = readArgumentIndex(pattern, next(pos));
                    stripCustom.append(START_FE);
                    stripCustom.append(index);
                    seekNonWs(pattern, pos);
                    Format format = null;
                    String formatDescription = null;
                    if (c[pos.getIndex()] == START_FMT) {
                        formatDescription = parseFormatDescription(pattern, next(pos));
                        format = getFormat(formatDescription);
                        if (format == null) {
                            stripCustom.append(START_FMT);
                            stripCustom.append(formatDescription);
                        }
                    }
                    foundFormats.add(format);
                    foundDescriptions.add(format == null ? null : formatDescription);
                    boolean z = true;
                    Validate.isTrue(foundFormats.size() == fmtCount);
                    if (foundDescriptions.size() != fmtCount) {
                        z = false;
                    }
                    Validate.isTrue(z);
                    if (c[pos.getIndex()] != END_FE) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Unreadable format element at position ");
                        stringBuilder.append(start);
                        throw new IllegalArgumentException(stringBuilder.toString());
                    }
                }
                stripCustom.append(c[pos.getIndex()]);
                next(pos);
            } else {
                appendQuotedString(pattern, pos, stripCustom);
            }
        }
        super.applyPattern(stripCustom.toString());
        this.toPattern = insertFormats(super.toPattern(), foundDescriptions);
        if (containsElements(foundFormats)) {
            Format[] origFormats = getFormats();
            start = 0;
            Iterator<Format> it = foundFormats.iterator();
            while (it.hasNext()) {
                Format f = (Format) it.next();
                if (f != null) {
                    origFormats[start] = f;
                }
                start++;
            }
            super.setFormats(origFormats);
        }
    }

    public void setFormat(int formatElementIndex, Format newFormat) {
        throw new UnsupportedOperationException();
    }

    public void setFormatByArgumentIndex(int argumentIndex, Format newFormat) {
        throw new UnsupportedOperationException();
    }

    public void setFormats(Format[] newFormats) {
        throw new UnsupportedOperationException();
    }

    public void setFormatsByArgumentIndex(Format[] newFormats) {
        throw new UnsupportedOperationException();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || !super.equals(obj) || ObjectUtils.notEqual(getClass(), obj.getClass())) {
            return false;
        }
        ExtendedMessageFormat rhs = (ExtendedMessageFormat) obj;
        if (ObjectUtils.notEqual(this.toPattern, rhs.toPattern)) {
            return false;
        }
        return true ^ ObjectUtils.notEqual(this.registry, rhs.registry);
    }

    public int hashCode() {
        return (((super.hashCode() * 31) + Objects.hashCode(this.registry)) * 31) + Objects.hashCode(this.toPattern);
    }

    private Format getFormat(String desc) {
        if (this.registry != null) {
            String name = desc;
            String args = null;
            int i = desc.indexOf(44);
            if (i > 0) {
                name = desc.substring(0, i).trim();
                args = desc.substring(i + 1).trim();
            }
            FormatFactory factory = (FormatFactory) this.registry.get(name);
            if (factory != null) {
                return factory.getFormat(name, args, getLocale());
            }
        }
        return null;
    }

    private String insertFormats(String pattern, ArrayList<String> customPatterns) {
        if (!containsElements(customPatterns)) {
            return pattern;
        }
        StringBuilder sb = new StringBuilder(pattern.length() * 2);
        ParsePosition pos = new ParsePosition(0);
        int fe = -1;
        int depth = 0;
        while (pos.getIndex() < pattern.length()) {
            char c = pattern.charAt(pos.getIndex());
            if (c == QUOTE) {
                appendQuotedString(pattern, pos, sb);
            } else if (c != START_FE) {
                if (c == END_FE) {
                    depth--;
                }
                sb.append(c);
                next(pos);
            } else {
                depth++;
                sb.append(START_FE);
                sb.append(readArgumentIndex(pattern, next(pos)));
                if (depth == 1) {
                    fe++;
                    String customPattern = (String) customPatterns.get(fe);
                    if (customPattern != null) {
                        sb.append(START_FMT);
                        sb.append(customPattern);
                    }
                }
            }
        }
        return sb.toString();
    }

    private void seekNonWs(String pattern, ParsePosition pos) {
        char[] buffer = pattern.toCharArray();
        while (true) {
            int len = StrMatcher.splitMatcher().isMatch(buffer, pos.getIndex());
            pos.setIndex(pos.getIndex() + len);
            if (len <= 0) {
                return;
            }
            if (pos.getIndex() >= pattern.length()) {
                return;
            }
        }
    }

    private ParsePosition next(ParsePosition pos) {
        pos.setIndex(pos.getIndex() + 1);
        return pos;
    }

    private void getQuotedString(String pattern, ParsePosition pos) {
        appendQuotedString(pattern, pos, null);
    }

    private boolean containsElements(Collection<?> coll) {
        if (coll != null) {
            if (!coll.isEmpty()) {
                for (Object name : coll) {
                    if (name != null) {
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }
}
