package okhttp3.internal.tls;

import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import javax.security.auth.x500.X500Principal;
import org.apache.commons.io.IOUtils;

final class DistinguishedNameParser {
    private int beg;
    private char[] chars;
    private int cur;
    private final String dn;
    private int end;
    private final int length = this.dn.length();
    private int pos;

    private java.lang.String hexAV() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:43:0x00c7 in {11, 19, 20, 25, 26, 27, 28, 36, 38, 40, 42} preds:[]
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
        r5 = this;
        r0 = r5.pos;
        r1 = r0 + 4;
        r2 = r5.length;
        if (r1 >= r2) goto L_0x00ae;
    L_0x0008:
        r5.beg = r0;
        r0 = r0 + 1;
        r5.pos = r0;
    L_0x000e:
        r0 = r5.pos;
        r1 = r5.length;
        if (r0 == r1) goto L_0x0062;
    L_0x0014:
        r1 = r5.chars;
        r2 = r1[r0];
        r3 = 43;
        if (r2 == r3) goto L_0x0062;
    L_0x001c:
        r2 = r1[r0];
        r3 = 44;
        if (r2 == r3) goto L_0x0062;
    L_0x0022:
        r2 = r1[r0];
        r3 = 59;
        if (r2 != r3) goto L_0x0029;
    L_0x0028:
        goto L_0x0062;
    L_0x0029:
        r2 = r1[r0];
        r3 = 32;
        if (r2 != r3) goto L_0x0047;
    L_0x002f:
        r5.end = r0;
        r0 = r0 + 1;
        r5.pos = r0;
    L_0x0035:
        r0 = r5.pos;
        r1 = r5.length;
        if (r0 >= r1) goto L_0x0046;
    L_0x003b:
        r1 = r5.chars;
        r1 = r1[r0];
        if (r1 != r3) goto L_0x0046;
    L_0x0041:
        r0 = r0 + 1;
        r5.pos = r0;
        goto L_0x0035;
    L_0x0046:
        goto L_0x0068;
    L_0x0047:
        r2 = r1[r0];
        r4 = 65;
        if (r2 < r4) goto L_0x005a;
    L_0x004d:
        r2 = r1[r0];
        r4 = 70;
        if (r2 > r4) goto L_0x005a;
    L_0x0053:
        r2 = r1[r0];
        r2 = r2 + r3;
        r2 = (char) r2;
        r1[r0] = r2;
        goto L_0x005b;
    L_0x005b:
        r0 = r5.pos;
        r0 = r0 + 1;
        r5.pos = r0;
        goto L_0x000e;
        r0 = r5.pos;
        r5.end = r0;
    L_0x0068:
        r0 = r5.end;
        r1 = r5.beg;
        r0 = r0 - r1;
        r2 = 5;
        if (r0 < r2) goto L_0x0094;
    L_0x0070:
        r2 = r0 & 1;
        if (r2 == 0) goto L_0x0094;
    L_0x0074:
        r2 = r0 / 2;
        r2 = new byte[r2];
        r3 = 0;
        r1 = r1 + 1;
    L_0x007b:
        r4 = r2.length;
        if (r3 >= r4) goto L_0x008a;
    L_0x007e:
        r4 = r5.getByte(r1);
        r4 = (byte) r4;
        r2[r3] = r4;
        r1 = r1 + 2;
        r3 = r3 + 1;
        goto L_0x007b;
    L_0x008a:
        r1 = new java.lang.String;
        r3 = r5.chars;
        r4 = r5.beg;
        r1.<init>(r3, r4, r0);
        return r1;
        r1 = new java.lang.IllegalStateException;
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "Unexpected end of DN: ";
        r2.append(r3);
        r3 = r5.dn;
        r2.append(r3);
        r2 = r2.toString();
        r1.<init>(r2);
        throw r1;
    L_0x00ae:
        r0 = new java.lang.IllegalStateException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Unexpected end of DN: ";
        r1.append(r2);
        r2 = r5.dn;
        r1.append(r2);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.tls.DistinguishedNameParser.hexAV():java.lang.String");
    }

    private java.lang.String nextAT() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:64:0x010c in {4, 8, 16, 27, 32, 34, 35, 41, 49, 53, 57, 58, 59, 61, 63} preds:[]
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
        r5 = this;
    L_0x0000:
        r0 = r5.pos;
        r1 = r5.length;
        r2 = 32;
        if (r0 >= r1) goto L_0x0013;
    L_0x0008:
        r1 = r5.chars;
        r1 = r1[r0];
        if (r1 != r2) goto L_0x0013;
    L_0x000e:
        r0 = r0 + 1;
        r5.pos = r0;
        goto L_0x0000;
    L_0x0013:
        r0 = r5.pos;
        r1 = r5.length;
        if (r0 != r1) goto L_0x001b;
    L_0x0019:
        r0 = 0;
        return r0;
    L_0x001b:
        r5.beg = r0;
        r0 = r0 + 1;
        r5.pos = r0;
    L_0x0021:
        r0 = r5.pos;
        r1 = r5.length;
        r3 = 61;
        if (r0 >= r1) goto L_0x0038;
    L_0x0029:
        r1 = r5.chars;
        r4 = r1[r0];
        if (r4 == r3) goto L_0x0038;
    L_0x002f:
        r1 = r1[r0];
        if (r1 == r2) goto L_0x0038;
    L_0x0033:
        r0 = r0 + 1;
        r5.pos = r0;
        goto L_0x0021;
    L_0x0038:
        r0 = r5.pos;
        r1 = r5.length;
        if (r0 >= r1) goto L_0x00f3;
    L_0x003e:
        r5.end = r0;
        r1 = r5.chars;
        r0 = r1[r0];
        if (r0 != r2) goto L_0x0081;
    L_0x0046:
        r0 = r5.pos;
        r1 = r5.length;
        if (r0 >= r1) goto L_0x005b;
    L_0x004c:
        r1 = r5.chars;
        r4 = r1[r0];
        if (r4 == r3) goto L_0x005b;
    L_0x0052:
        r1 = r1[r0];
        if (r1 != r2) goto L_0x005b;
    L_0x0056:
        r0 = r0 + 1;
        r5.pos = r0;
        goto L_0x0046;
    L_0x005b:
        r0 = r5.chars;
        r1 = r5.pos;
        r0 = r0[r1];
        if (r0 != r3) goto L_0x0068;
    L_0x0063:
        r0 = r5.length;
        if (r1 == r0) goto L_0x0068;
    L_0x0067:
        goto L_0x0082;
    L_0x0068:
        r0 = new java.lang.IllegalStateException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Unexpected end of DN: ";
        r1.append(r2);
        r2 = r5.dn;
        r1.append(r2);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
    L_0x0082:
        r0 = r5.pos;
        r0 = r0 + 1;
        r5.pos = r0;
    L_0x0088:
        r0 = r5.pos;
        r1 = r5.length;
        if (r0 >= r1) goto L_0x0099;
    L_0x008e:
        r1 = r5.chars;
        r1 = r1[r0];
        if (r1 != r2) goto L_0x0099;
    L_0x0094:
        r0 = r0 + 1;
        r5.pos = r0;
        goto L_0x0088;
    L_0x0099:
        r0 = r5.end;
        r1 = r5.beg;
        r0 = r0 - r1;
        r2 = 4;
        if (r0 <= r2) goto L_0x00e5;
    L_0x00a1:
        r0 = r5.chars;
        r3 = r1 + 3;
        r3 = r0[r3];
        r4 = 46;
        if (r3 != r4) goto L_0x00e5;
    L_0x00ab:
        r3 = r0[r1];
        r4 = 79;
        if (r3 == r4) goto L_0x00b7;
    L_0x00b1:
        r0 = r0[r1];
        r1 = 111; // 0x6f float:1.56E-43 double:5.5E-322;
        if (r0 != r1) goto L_0x00e5;
    L_0x00b7:
        r0 = r5.chars;
        r1 = r5.beg;
        r3 = r1 + 1;
        r3 = r0[r3];
        r4 = 73;
        if (r3 == r4) goto L_0x00cb;
    L_0x00c3:
        r1 = r1 + 1;
        r0 = r0[r1];
        r1 = 105; // 0x69 float:1.47E-43 double:5.2E-322;
        if (r0 != r1) goto L_0x00e5;
    L_0x00cb:
        r0 = r5.chars;
        r1 = r5.beg;
        r3 = r1 + 2;
        r3 = r0[r3];
        r4 = 68;
        if (r3 == r4) goto L_0x00df;
    L_0x00d7:
        r1 = r1 + 2;
        r0 = r0[r1];
        r1 = 100;
        if (r0 != r1) goto L_0x00e5;
    L_0x00df:
        r0 = r5.beg;
        r0 = r0 + r2;
        r5.beg = r0;
        goto L_0x00e6;
    L_0x00e6:
        r0 = new java.lang.String;
        r1 = r5.chars;
        r2 = r5.beg;
        r3 = r5.end;
        r3 = r3 - r2;
        r0.<init>(r1, r2, r3);
        return r0;
    L_0x00f3:
        r0 = new java.lang.IllegalStateException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Unexpected end of DN: ";
        r1.append(r2);
        r2 = r5.dn;
        r1.append(r2);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.tls.DistinguishedNameParser.nextAT():java.lang.String");
    }

    private java.lang.String quotedAV() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:20:0x007c in {10, 12, 15, 16, 17, 19} preds:[]
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
        r4 = this;
        r0 = r4.pos;
        r0 = r0 + 1;
        r4.pos = r0;
        r0 = r4.pos;
        r4.beg = r0;
        r0 = r4.beg;
        r4.end = r0;
    L_0x000e:
        r0 = r4.pos;
        r1 = r4.length;
        if (r0 == r1) goto L_0x0063;
    L_0x0014:
        r1 = r4.chars;
        r2 = r1[r0];
        r3 = 34;
        if (r2 != r3) goto L_0x0041;
    L_0x001c:
        r0 = r0 + 1;
        r4.pos = r0;
    L_0x0021:
        r0 = r4.pos;
        r1 = r4.length;
        if (r0 >= r1) goto L_0x0034;
    L_0x0027:
        r1 = r4.chars;
        r1 = r1[r0];
        r2 = 32;
        if (r1 != r2) goto L_0x0034;
    L_0x002f:
        r0 = r0 + 1;
        r4.pos = r0;
        goto L_0x0021;
    L_0x0034:
        r0 = new java.lang.String;
        r1 = r4.chars;
        r2 = r4.beg;
        r3 = r4.end;
        r3 = r3 - r2;
        r0.<init>(r1, r2, r3);
        return r0;
    L_0x0041:
        r2 = r1[r0];
        r3 = 92;
        if (r2 != r3) goto L_0x0050;
    L_0x0047:
        r0 = r4.end;
        r2 = r4.getEscaped();
        r1[r0] = r2;
        goto L_0x0056;
    L_0x0050:
        r2 = r4.end;
        r0 = r1[r0];
        r1[r2] = r0;
    L_0x0056:
        r0 = r4.pos;
        r0 = r0 + 1;
        r4.pos = r0;
        r0 = r4.end;
        r0 = r0 + 1;
        r4.end = r0;
        goto L_0x000e;
    L_0x0063:
        r0 = new java.lang.IllegalStateException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Unexpected end of DN: ";
        r1.append(r2);
        r2 = r4.dn;
        r1.append(r2);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.tls.DistinguishedNameParser.quotedAV():java.lang.String");
    }

    public java.lang.String findMostSpecific(java.lang.String r8) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:35:0x009e in {2, 6, 9, 10, 11, 12, 15, 18, 23, 26, 28, 29, 32, 34} preds:[]
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
        r0 = 0;
        r7.pos = r0;
        r7.beg = r0;
        r7.end = r0;
        r7.cur = r0;
        r0 = r7.dn;
        r0 = r0.toCharArray();
        r7.chars = r0;
        r0 = r7.nextAT();
        r1 = 0;
        if (r0 != 0) goto L_0x0019;
    L_0x0018:
        return r1;
    L_0x001a:
        r2 = "";
        r3 = r7.pos;
        r4 = r7.length;
        if (r3 != r4) goto L_0x0023;
    L_0x0022:
        return r1;
    L_0x0023:
        r4 = r7.chars;
        r3 = r4[r3];
        switch(r3) {
            case 34: goto L_0x0035;
            case 35: goto L_0x0030;
            case 43: goto L_0x002f;
            case 44: goto L_0x002f;
            case 59: goto L_0x002f;
            default: goto L_0x002a;
        };
    L_0x002a:
        r2 = r7.escapedAV();
        goto L_0x003a;
    L_0x002f:
        goto L_0x003a;
    L_0x0030:
        r2 = r7.hexAV();
        goto L_0x003a;
    L_0x0035:
        r2 = r7.quotedAV();
    L_0x003a:
        r3 = r8.equalsIgnoreCase(r0);
        if (r3 == 0) goto L_0x0041;
    L_0x0040:
        return r2;
    L_0x0041:
        r3 = r7.pos;
        r4 = r7.length;
        if (r3 < r4) goto L_0x0048;
    L_0x0047:
        return r1;
    L_0x0048:
        r4 = r7.chars;
        r5 = r4[r3];
        r6 = 44;
        if (r5 == r6) goto L_0x0077;
    L_0x0050:
        r5 = r4[r3];
        r6 = 59;
        if (r5 != r6) goto L_0x0057;
    L_0x0056:
        goto L_0x0077;
    L_0x0057:
        r3 = r4[r3];
        r4 = 43;
        if (r3 != r4) goto L_0x005e;
    L_0x005d:
        goto L_0x0078;
    L_0x005e:
        r1 = new java.lang.IllegalStateException;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "Malformed DN: ";
        r3.append(r4);
        r4 = r7.dn;
        r3.append(r4);
        r3 = r3.toString();
        r1.<init>(r3);
        throw r1;
    L_0x0078:
        r3 = r7.pos;
        r3 = r3 + 1;
        r7.pos = r3;
        r0 = r7.nextAT();
        if (r0 == 0) goto L_0x0085;
    L_0x0084:
        goto L_0x001a;
    L_0x0085:
        r1 = new java.lang.IllegalStateException;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "Malformed DN: ";
        r3.append(r4);
        r4 = r7.dn;
        r3.append(r4);
        r3 = r3.toString();
        r1.<init>(r3);
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.tls.DistinguishedNameParser.findMostSpecific(java.lang.String):java.lang.String");
    }

    DistinguishedNameParser(X500Principal principal) {
        this.dn = principal.getName("RFC2253");
    }

    private String escapedAV() {
        int i = this.pos;
        this.beg = i;
        this.end = i;
        while (true) {
            i = this.pos;
            if (i >= this.length) {
                char[] cArr = this.chars;
                int i2 = this.beg;
                return new String(cArr, i2, this.end - i2);
            }
            cArr = this.chars;
            char c = cArr[i];
            if (c != ' ') {
                if (c != ';') {
                    if (c != IOUtils.DIR_SEPARATOR_WINDOWS) {
                        switch (c) {
                            case '+':
                            case ',':
                                break;
                            default:
                                i2 = this.end;
                                this.end = i2 + 1;
                                cArr[i2] = cArr[i];
                                this.pos = i + 1;
                                continue;
                        }
                    } else {
                        i = this.end;
                        this.end = i + 1;
                        cArr[i] = getEscaped();
                        this.pos++;
                    }
                }
                cArr = this.chars;
                i2 = this.beg;
                return new String(cArr, i2, this.end - i2);
            }
            i2 = this.end;
            this.cur = i2;
            this.pos = i + 1;
            this.end = i2 + 1;
            cArr[i2] = ' ';
            while (true) {
                i = this.pos;
                if (i < this.length) {
                    cArr = this.chars;
                    if (cArr[i] == ' ') {
                        i2 = this.end;
                        this.end = i2 + 1;
                        cArr[i2] = ' ';
                        this.pos = i + 1;
                    }
                }
                i = this.pos;
                if (i != this.length) {
                    cArr = this.chars;
                    if (!(cArr[i] == ',' || cArr[i] == '+')) {
                        if (cArr[i] == ';') {
                        }
                    }
                }
                cArr = this.chars;
                i2 = this.beg;
                return new String(cArr, i2, this.cur - i2);
            }
        }
    }

    private char getEscaped() {
        this.pos++;
        int i = this.pos;
        if (i != this.length) {
            char c = this.chars[i];
            if (!(c == ' ' || c == '%' || c == IOUtils.DIR_SEPARATOR_WINDOWS || c == '_')) {
                switch (c) {
                    case '\"':
                    case '#':
                        break;
                    default:
                        switch (c) {
                            case '*':
                            case '+':
                            case ',':
                                break;
                            default:
                                switch (c) {
                                    case ';':
                                    case '<':
                                    case '=':
                                    case '>':
                                        break;
                                    default:
                                        return getUTF8();
                                }
                        }
                }
            }
            return this.chars[this.pos];
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Unexpected end of DN: ");
        stringBuilder.append(this.dn);
        throw new IllegalStateException(stringBuilder.toString());
    }

    private char getUTF8() {
        int res = getByte(this.pos);
        this.pos++;
        if (res < 128) {
            return (char) res;
        }
        if (res < PsExtractor.AUDIO_STREAM || res > 247) {
            return '?';
        }
        int count;
        if (res <= 223) {
            count = 1;
            res &= 31;
        } else if (res <= 239) {
            count = 2;
            res &= 15;
        } else {
            count = 3;
            res &= 7;
        }
        int i = 0;
        while (i < count) {
            this.pos++;
            int i2 = this.pos;
            if (i2 != this.length) {
                if (this.chars[i2] == IOUtils.DIR_SEPARATOR_WINDOWS) {
                    this.pos = i2 + 1;
                    i2 = getByte(this.pos);
                    this.pos++;
                    if ((i2 & PsExtractor.AUDIO_STREAM) != 128) {
                        return '?';
                    }
                    res = (res << 6) + (i2 & 63);
                    i++;
                }
            }
            return '?';
        }
        return (char) res;
    }

    private int getByte(int position) {
        if (position + 1 < this.length) {
            StringBuilder stringBuilder;
            int b1 = this.chars[position];
            if (b1 >= 48 && b1 <= 57) {
                b1 -= 48;
            } else if (b1 >= 97 && b1 <= 102) {
                b1 -= 87;
            } else if (b1 < 65 || b1 > 70) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("Malformed DN: ");
                stringBuilder.append(this.dn);
                throw new IllegalStateException(stringBuilder.toString());
            } else {
                b1 -= 55;
            }
            int b2 = this.chars[position + 1];
            if (b2 >= 48 && b2 <= 57) {
                b2 -= 48;
            } else if (b2 >= 97 && b2 <= 102) {
                b2 -= 87;
            } else if (b2 < 65 || b2 > 70) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("Malformed DN: ");
                stringBuilder.append(this.dn);
                throw new IllegalStateException(stringBuilder.toString());
            } else {
                b2 -= 55;
            }
            return (b1 << 4) + b2;
        }
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("Malformed DN: ");
        stringBuilder2.append(this.dn);
        throw new IllegalStateException(stringBuilder2.toString());
    }
}
