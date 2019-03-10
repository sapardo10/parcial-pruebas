package org.apache.commons.lang3.text.translate;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Locale;

@Deprecated
public abstract class CharSequenceTranslator {
    static final char[] HEX_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public abstract int translate(CharSequence charSequence, int i, Writer writer) throws IOException;

    public final void translate(java.lang.CharSequence r7, java.io.Writer r8) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:23:0x004f in {2, 12, 13, 14, 15, 18, 19, 20, 22} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r6 = this;
        if (r8 == 0) goto L_0x0047;
    L_0x0002:
        if (r7 != 0) goto L_0x0005;
    L_0x0004:
        return;
    L_0x0005:
        r0 = 0;
        r1 = r7.length();
    L_0x000a:
        if (r0 >= r1) goto L_0x0046;
    L_0x000c:
        r2 = r6.translate(r7, r0, r8);
        if (r2 != 0) goto L_0x0036;
    L_0x0012:
        r3 = r7.charAt(r0);
        r8.write(r3);
        r0 = r0 + 1;
        r4 = java.lang.Character.isHighSurrogate(r3);
        if (r4 == 0) goto L_0x0035;
    L_0x0021:
        if (r0 >= r1) goto L_0x0035;
    L_0x0023:
        r4 = r7.charAt(r0);
        r5 = java.lang.Character.isLowSurrogate(r4);
        if (r5 == 0) goto L_0x0033;
    L_0x002d:
        r8.write(r4);
        r0 = r0 + 1;
        goto L_0x0034;
    L_0x0034:
        goto L_0x000a;
    L_0x0035:
        goto L_0x000a;
    L_0x0036:
        r3 = 0;
    L_0x0037:
        if (r3 >= r2) goto L_0x0045;
    L_0x0039:
        r4 = java.lang.Character.codePointAt(r7, r0);
        r4 = java.lang.Character.charCount(r4);
        r0 = r0 + r4;
        r3 = r3 + 1;
        goto L_0x0037;
    L_0x0045:
        goto L_0x000a;
    L_0x0046:
        return;
    L_0x0047:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "The Writer must not be null";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.text.translate.CharSequenceTranslator.translate(java.lang.CharSequence, java.io.Writer):void");
    }

    public final String translate(CharSequence input) {
        if (input == null) {
            return null;
        }
        try {
            StringWriter writer = new StringWriter(input.length() * 2);
            translate(input, writer);
            return writer.toString();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    public final CharSequenceTranslator with(CharSequenceTranslator... translators) {
        CharSequenceTranslator[] newArray = new CharSequenceTranslator[(translators.length + 1)];
        newArray[0] = this;
        System.arraycopy(translators, 0, newArray, 1, translators.length);
        return new AggregateTranslator(newArray);
    }

    public static String hex(int codepoint) {
        return Integer.toHexString(codepoint).toUpperCase(Locale.ENGLISH);
    }
}
