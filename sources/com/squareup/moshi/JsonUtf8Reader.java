package com.squareup.moshi;

import com.squareup.moshi.JsonReader.Options;
import java.io.IOException;
import java.math.BigDecimal;
import javax.annotation.Nullable;
import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;

final class JsonUtf8Reader extends JsonReader {
    private static final ByteString CLOSING_BLOCK_COMMENT = ByteString.encodeUtf8("*/");
    private static final ByteString DOUBLE_QUOTE_OR_SLASH = ByteString.encodeUtf8("\"\\");
    private static final ByteString LINEFEED_OR_CARRIAGE_RETURN = ByteString.encodeUtf8("\n\r");
    private static final long MIN_INCOMPLETE_INTEGER = -922337203685477580L;
    private static final int NUMBER_CHAR_DECIMAL = 3;
    private static final int NUMBER_CHAR_DIGIT = 2;
    private static final int NUMBER_CHAR_EXP_DIGIT = 7;
    private static final int NUMBER_CHAR_EXP_E = 5;
    private static final int NUMBER_CHAR_EXP_SIGN = 6;
    private static final int NUMBER_CHAR_FRACTION_DIGIT = 4;
    private static final int NUMBER_CHAR_NONE = 0;
    private static final int NUMBER_CHAR_SIGN = 1;
    private static final int PEEKED_BEGIN_ARRAY = 3;
    private static final int PEEKED_BEGIN_OBJECT = 1;
    private static final int PEEKED_BUFFERED = 11;
    private static final int PEEKED_BUFFERED_NAME = 15;
    private static final int PEEKED_DOUBLE_QUOTED = 9;
    private static final int PEEKED_DOUBLE_QUOTED_NAME = 13;
    private static final int PEEKED_END_ARRAY = 4;
    private static final int PEEKED_END_OBJECT = 2;
    private static final int PEEKED_EOF = 18;
    private static final int PEEKED_FALSE = 6;
    private static final int PEEKED_LONG = 16;
    private static final int PEEKED_NONE = 0;
    private static final int PEEKED_NULL = 7;
    private static final int PEEKED_NUMBER = 17;
    private static final int PEEKED_SINGLE_QUOTED = 8;
    private static final int PEEKED_SINGLE_QUOTED_NAME = 12;
    private static final int PEEKED_TRUE = 5;
    private static final int PEEKED_UNQUOTED = 10;
    private static final int PEEKED_UNQUOTED_NAME = 14;
    private static final ByteString SINGLE_QUOTE_OR_SLASH = ByteString.encodeUtf8("'\\");
    private static final ByteString UNQUOTED_STRING_TERMINALS = ByteString.encodeUtf8("{}[]:, \n\t\r\f/\\;#=");
    private final Buffer buffer;
    private int peeked = 0;
    private long peekedLong;
    private int peekedNumberLength;
    @Nullable
    private String peekedString;
    private final BufferedSource source;

    private int nextNonWhitespace(boolean r7) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:38:0x0098 in {11, 16, 20, 21, 24, 26, 29, 30, 31, 32, 35, 37} preds:[]
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
        r0 = 0;
    L_0x0001:
        r1 = r6.source;
        r2 = r0 + 1;
        r2 = (long) r2;
        r1 = r1.request(r2);
        if (r1 == 0) goto L_0x008c;
    L_0x000c:
        r1 = r6.buffer;
        r2 = r0 + 1;
        r3 = (long) r0;
        r0 = r1.getByte(r3);
        r1 = 10;
        if (r0 == r1) goto L_0x0088;
    L_0x0019:
        r1 = 32;
        if (r0 == r1) goto L_0x0088;
    L_0x001d:
        r1 = 13;
        if (r0 == r1) goto L_0x0088;
    L_0x0021:
        r1 = 9;
        if (r0 != r1) goto L_0x0026;
    L_0x0025:
        goto L_0x0089;
    L_0x0026:
        r1 = r6.buffer;
        r3 = r2 + -1;
        r3 = (long) r3;
        r1.skip(r3);
        r1 = 47;
        if (r0 != r1) goto L_0x0079;
    L_0x0032:
        r3 = r6.source;
        r4 = 2;
        r3 = r3.request(r4);
        if (r3 != 0) goto L_0x003d;
    L_0x003c:
        return r0;
    L_0x003d:
        r6.checkLenient();
        r3 = r6.buffer;
        r4 = 1;
        r3 = r3.getByte(r4);
        r4 = 42;
        if (r3 == r4) goto L_0x005f;
    L_0x004c:
        if (r3 == r1) goto L_0x004f;
    L_0x004e:
        return r0;
    L_0x004f:
        r1 = r6.buffer;
        r1.readByte();
        r1 = r6.buffer;
        r1.readByte();
        r6.skipToEndOfLine();
        r1 = 0;
        r0 = r1;
        goto L_0x0001;
    L_0x005f:
        r1 = r6.buffer;
        r1.readByte();
        r1 = r6.buffer;
        r1.readByte();
        r1 = r6.skipToEndOfBlockComment();
        if (r1 == 0) goto L_0x0072;
    L_0x006f:
        r1 = 0;
        r0 = r1;
        goto L_0x0001;
    L_0x0072:
        r1 = "Unterminated comment";
        r1 = r6.syntaxError(r1);
        throw r1;
    L_0x0079:
        r1 = 35;
        if (r0 != r1) goto L_0x0087;
    L_0x007d:
        r6.checkLenient();
        r6.skipToEndOfLine();
        r1 = 0;
        r0 = r1;
        goto L_0x0001;
    L_0x0087:
        return r0;
    L_0x0089:
        r0 = r2;
        goto L_0x0001;
    L_0x008c:
        if (r7 != 0) goto L_0x0090;
    L_0x008e:
        r1 = -1;
        return r1;
    L_0x0090:
        r1 = new java.io.EOFException;
        r2 = "End of input";
        r1.<init>(r2);
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.squareup.moshi.JsonUtf8Reader.nextNonWhitespace(boolean):int");
    }

    private java.lang.String nextQuotedValue(okio.ByteString r7) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:15:0x005e in {6, 7, 10, 12, 14} preds:[]
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
        r0 = 0;
    L_0x0001:
        r1 = r6.source;
        r1 = r1.indexOfElement(r7);
        r3 = -1;
        r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1));
        if (r5 == 0) goto L_0x0057;
    L_0x000d:
        r3 = r6.buffer;
        r3 = r3.getByte(r1);
        r4 = 92;
        if (r3 != r4) goto L_0x0036;
    L_0x0017:
        if (r0 != 0) goto L_0x0020;
    L_0x0019:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r0 = r3;
    L_0x0020:
        r3 = r6.buffer;
        r3 = r3.readUtf8(r1);
        r0.append(r3);
        r3 = r6.buffer;
        r3.readByte();
        r3 = r6.readEscapeCharacter();
        r0.append(r3);
        goto L_0x0001;
    L_0x0036:
        if (r0 != 0) goto L_0x0044;
    L_0x0038:
        r3 = r6.buffer;
        r3 = r3.readUtf8(r1);
        r4 = r6.buffer;
        r4.readByte();
        return r3;
    L_0x0044:
        r3 = r6.buffer;
        r3 = r3.readUtf8(r1);
        r0.append(r3);
        r3 = r6.buffer;
        r3.readByte();
        r3 = r0.toString();
        return r3;
    L_0x0057:
        r3 = "Unterminated string";
        r3 = r6.syntaxError(r3);
        throw r3;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.squareup.moshi.JsonUtf8Reader.nextQuotedValue(okio.ByteString):java.lang.String");
    }

    private char readEscapeCharacter() throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:65:0x00ee in {24, 26, 35, 39, 44, 45, 47, 49, 51, 53, 55, 56, 58, 60, 62, 64} preds:[]
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
        r11 = this;
        r0 = r11.source;
        r1 = 1;
        r0 = r0.request(r1);
        if (r0 == 0) goto L_0x00e7;
    L_0x000a:
        r0 = r11.buffer;
        r0 = r0.readByte();
        r1 = 10;
        if (r0 == r1) goto L_0x00e5;
    L_0x0014:
        r2 = 34;
        if (r0 == r2) goto L_0x00e5;
    L_0x0018:
        r2 = 39;
        if (r0 == r2) goto L_0x00e5;
    L_0x001c:
        r2 = 47;
        if (r0 == r2) goto L_0x00e5;
    L_0x0020:
        r2 = 92;
        if (r0 == r2) goto L_0x00e5;
    L_0x0024:
        r2 = 98;
        if (r0 == r2) goto L_0x00e2;
    L_0x0028:
        r2 = 102; // 0x66 float:1.43E-43 double:5.04E-322;
        if (r0 == r2) goto L_0x00df;
    L_0x002c:
        r3 = 110; // 0x6e float:1.54E-43 double:5.43E-322;
        if (r0 == r3) goto L_0x00de;
    L_0x0030:
        r3 = 114; // 0x72 float:1.6E-43 double:5.63E-322;
        if (r0 == r3) goto L_0x00db;
    L_0x0034:
        switch(r0) {
            case 116: goto L_0x00d8;
            case 117: goto L_0x0054;
            default: goto L_0x0037;
        };
    L_0x0037:
        r1 = r11.lenient;
        if (r1 == 0) goto L_0x003d;
    L_0x003b:
        r1 = (char) r0;
        return r1;
    L_0x003d:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Invalid escape sequence: \\";
        r1.append(r2);
        r2 = (char) r0;
        r1.append(r2);
        r1 = r1.toString();
        r1 = r11.syntaxError(r1);
        throw r1;
    L_0x0054:
        r3 = r11.source;
        r4 = 4;
        r3 = r3.request(r4);
        if (r3 == 0) goto L_0x00bd;
    L_0x005e:
        r3 = 0;
        r6 = 0;
        r7 = r6 + 4;
    L_0x0062:
        if (r6 >= r7) goto L_0x00b6;
    L_0x0064:
        r8 = r11.buffer;
        r9 = (long) r6;
        r8 = r8.getByte(r9);
        r9 = r3 << 4;
        r3 = (char) r9;
        r9 = 48;
        if (r8 < r9) goto L_0x007b;
    L_0x0072:
        r9 = 57;
        if (r8 > r9) goto L_0x007b;
    L_0x0076:
        r9 = r8 + -48;
        r9 = r9 + r3;
        r3 = (char) r9;
        goto L_0x0096;
        r9 = 97;
        if (r8 < r9) goto L_0x0088;
    L_0x0080:
        if (r8 > r2) goto L_0x0088;
    L_0x0082:
        r9 = r8 + -97;
        r9 = r9 + r1;
        r9 = r9 + r3;
        r3 = (char) r9;
        goto L_0x0096;
        r9 = 65;
        if (r8 < r9) goto L_0x0099;
    L_0x008d:
        r9 = 70;
        if (r8 > r9) goto L_0x0099;
    L_0x0091:
        r9 = r8 + -65;
        r9 = r9 + r1;
        r9 = r9 + r3;
        r3 = (char) r9;
    L_0x0096:
        r6 = r6 + 1;
        goto L_0x0062;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "\\u";
        r1.append(r2);
        r2 = r11.buffer;
        r2 = r2.readUtf8(r4);
        r1.append(r2);
        r1 = r1.toString();
        r1 = r11.syntaxError(r1);
        throw r1;
        r1 = r11.buffer;
        r1.skip(r4);
        return r3;
    L_0x00bd:
        r1 = new java.io.EOFException;
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "Unterminated escape sequence at path ";
        r2.append(r3);
        r3 = r11.getPath();
        r2.append(r3);
        r2 = r2.toString();
        r1.<init>(r2);
        throw r1;
    L_0x00d8:
        r1 = 9;
        return r1;
    L_0x00db:
        r1 = 13;
        return r1;
    L_0x00de:
        return r1;
    L_0x00df:
        r1 = 12;
        return r1;
    L_0x00e2:
        r1 = 8;
        return r1;
    L_0x00e5:
        r1 = (char) r0;
        return r1;
    L_0x00e7:
        r0 = "Unterminated escape sequence";
        r0 = r11.syntaxError(r0);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.squareup.moshi.JsonUtf8Reader.readEscapeCharacter():char");
    }

    private void skipQuotedValue(okio.ByteString r7) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:9:0x0030 in {4, 6, 8} preds:[]
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
    L_0x0000:
        r0 = r6.source;
        r0 = r0.indexOfElement(r7);
        r2 = -1;
        r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r4 == 0) goto L_0x0029;
    L_0x000c:
        r2 = r6.buffer;
        r2 = r2.getByte(r0);
        r3 = 92;
        r4 = 1;
        if (r2 != r3) goto L_0x0022;
    L_0x0018:
        r2 = r6.buffer;
        r4 = r4 + r0;
        r2.skip(r4);
        r6.readEscapeCharacter();
        goto L_0x0000;
    L_0x0022:
        r2 = r6.buffer;
        r4 = r4 + r0;
        r2.skip(r4);
        return;
    L_0x0029:
        r2 = "Unterminated string";
        r2 = r6.syntaxError(r2);
        throw r2;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.squareup.moshi.JsonUtf8Reader.skipQuotedValue(okio.ByteString):void");
    }

    public void skipValue() throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:47:0x00b4 in {5, 6, 9, 11, 14, 17, 22, 27, 32, 35, 36, 37, 38, 39, 43, 44, 46} preds:[]
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
        r0 = r6.failOnUnknown;
        if (r0 != 0) goto L_0x008d;
    L_0x0004:
        r0 = 0;
    L_0x0005:
        r1 = r6.peeked;
        if (r1 != 0) goto L_0x000e;
    L_0x0009:
        r1 = r6.doPeek();
        goto L_0x000f;
    L_0x000f:
        r2 = 3;
        r3 = 1;
        if (r1 != r2) goto L_0x0019;
    L_0x0013:
        r6.pushScope(r3);
        r0 = r0 + 1;
        goto L_0x0072;
    L_0x0019:
        if (r1 != r3) goto L_0x0021;
    L_0x001b:
        r6.pushScope(r2);
        r0 = r0 + 1;
        goto L_0x0072;
    L_0x0021:
        r2 = 4;
        if (r1 != r2) goto L_0x002c;
    L_0x0024:
        r2 = r6.stackSize;
        r2 = r2 - r3;
        r6.stackSize = r2;
        r0 = r0 + -1;
        goto L_0x0072;
    L_0x002c:
        r2 = 2;
        if (r1 != r2) goto L_0x0037;
    L_0x002f:
        r2 = r6.stackSize;
        r2 = r2 - r3;
        r6.stackSize = r2;
        r0 = r0 + -1;
        goto L_0x0072;
    L_0x0037:
        r2 = 14;
        if (r1 == r2) goto L_0x006e;
    L_0x003b:
        r2 = 10;
        if (r1 != r2) goto L_0x0040;
    L_0x003f:
        goto L_0x006e;
    L_0x0040:
        r2 = 9;
        if (r1 == r2) goto L_0x0067;
    L_0x0044:
        r2 = 13;
        if (r1 != r2) goto L_0x0049;
    L_0x0048:
        goto L_0x0067;
    L_0x0049:
        r2 = 8;
        if (r1 == r2) goto L_0x0060;
    L_0x004d:
        r2 = 12;
        if (r1 != r2) goto L_0x0052;
    L_0x0051:
        goto L_0x0060;
    L_0x0052:
        r2 = 17;
        if (r1 != r2) goto L_0x005f;
    L_0x0056:
        r2 = r6.buffer;
        r4 = r6.peekedNumberLength;
        r4 = (long) r4;
        r2.skip(r4);
        goto L_0x0072;
    L_0x005f:
        goto L_0x0072;
        r2 = SINGLE_QUOTE_OR_SLASH;
        r6.skipQuotedValue(r2);
        goto L_0x0072;
        r2 = DOUBLE_QUOTE_OR_SLASH;
        r6.skipQuotedValue(r2);
        goto L_0x0072;
        r6.skipUnquotedValue();
    L_0x0072:
        r2 = 0;
        r6.peeked = r2;
        if (r0 != 0) goto L_0x008b;
    L_0x0077:
        r1 = r6.pathIndices;
        r2 = r6.stackSize;
        r2 = r2 - r3;
        r4 = r1[r2];
        r4 = r4 + r3;
        r1[r2] = r4;
        r1 = r6.pathNames;
        r2 = r6.stackSize;
        r2 = r2 - r3;
        r3 = "null";
        r1[r2] = r3;
        return;
    L_0x008b:
        goto L_0x0005;
    L_0x008d:
        r0 = new com.squareup.moshi.JsonDataException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Cannot skip unexpected ";
        r1.append(r2);
        r2 = r6.peek();
        r1.append(r2);
        r2 = " at ";
        r1.append(r2);
        r2 = r6.getPath();
        r1.append(r2);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.squareup.moshi.JsonUtf8Reader.skipValue():void");
    }

    JsonUtf8Reader(BufferedSource source) {
        if (source != null) {
            this.source = source;
            this.buffer = source.buffer();
            pushScope(6);
            return;
        }
        throw new NullPointerException("source == null");
    }

    public void beginArray() throws IOException {
        int p = this.peeked;
        if (p == 0) {
            p = doPeek();
        }
        if (p == 3) {
            pushScope(1);
            this.pathIndices[this.stackSize - 1] = 0;
            this.peeked = 0;
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Expected BEGIN_ARRAY but was ");
        stringBuilder.append(peek());
        stringBuilder.append(" at path ");
        stringBuilder.append(getPath());
        throw new JsonDataException(stringBuilder.toString());
    }

    public void endArray() throws IOException {
        int p = this.peeked;
        if (p == 0) {
            p = doPeek();
        }
        if (p == 4) {
            this.stackSize--;
            int[] iArr = this.pathIndices;
            int i = this.stackSize - 1;
            iArr[i] = iArr[i] + 1;
            this.peeked = 0;
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Expected END_ARRAY but was ");
        stringBuilder.append(peek());
        stringBuilder.append(" at path ");
        stringBuilder.append(getPath());
        throw new JsonDataException(stringBuilder.toString());
    }

    public void beginObject() throws IOException {
        int p = this.peeked;
        if (p == 0) {
            p = doPeek();
        }
        if (p == 1) {
            pushScope(3);
            this.peeked = 0;
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Expected BEGIN_OBJECT but was ");
        stringBuilder.append(peek());
        stringBuilder.append(" at path ");
        stringBuilder.append(getPath());
        throw new JsonDataException(stringBuilder.toString());
    }

    public void endObject() throws IOException {
        int p = this.peeked;
        if (p == 0) {
            p = doPeek();
        }
        if (p == 2) {
            this.stackSize--;
            this.pathNames[this.stackSize] = null;
            int[] iArr = this.pathIndices;
            int i = this.stackSize - 1;
            iArr[i] = iArr[i] + 1;
            this.peeked = 0;
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Expected END_OBJECT but was ");
        stringBuilder.append(peek());
        stringBuilder.append(" at path ");
        stringBuilder.append(getPath());
        throw new JsonDataException(stringBuilder.toString());
    }

    public boolean hasNext() throws IOException {
        int p = this.peeked;
        if (p == 0) {
            p = doPeek();
        }
        return (p == 2 || p == 4 || p == 18) ? false : true;
    }

    public JsonReader$Token peek() throws IOException {
        int p = this.peeked;
        if (p == 0) {
            p = doPeek();
        }
        switch (p) {
            case 1:
                return JsonReader$Token.BEGIN_OBJECT;
            case 2:
                return JsonReader$Token.END_OBJECT;
            case 3:
                return JsonReader$Token.BEGIN_ARRAY;
            case 4:
                return JsonReader$Token.END_ARRAY;
            case 5:
            case 6:
                return JsonReader$Token.BOOLEAN;
            case 7:
                return JsonReader$Token.NULL;
            case 8:
            case 9:
            case 10:
            case 11:
                return JsonReader$Token.STRING;
            case 12:
            case 13:
            case 14:
            case 15:
                return JsonReader$Token.NAME;
            case 16:
            case 17:
                return JsonReader$Token.NUMBER;
            case 18:
                return JsonReader$Token.END_DOCUMENT;
            default:
                throw new AssertionError();
        }
    }

    private int doPeek() throws IOException {
        int c;
        int peekStack = this.scopes[this.stackSize - 1];
        if (peekStack == 1) {
            r0.scopes[r0.stackSize - 1] = 2;
        } else if (peekStack == 2) {
            c = nextNonWhitespace(true);
            r0.buffer.readByte();
            if (c != 44) {
                if (c == 59) {
                    checkLenient();
                } else if (c == 93) {
                    r0.peeked = 4;
                    return 4;
                } else {
                    throw syntaxError("Unterminated array");
                }
            }
        } else {
            if (peekStack != 3) {
                if (peekStack != 5) {
                    if (peekStack == 4) {
                        r0.scopes[r0.stackSize - 1] = 5;
                        c = nextNonWhitespace(true);
                        r0.buffer.readByte();
                        if (c != 58) {
                            if (c == 61) {
                                checkLenient();
                                if (r0.source.request(1) && r0.buffer.getByte(0) == (byte) 62) {
                                    r0.buffer.readByte();
                                }
                            } else {
                                throw syntaxError("Expected ':'");
                            }
                        }
                    } else if (peekStack == 6) {
                        r0.scopes[r0.stackSize - 1] = 7;
                    } else if (peekStack == 7) {
                        if (nextNonWhitespace(false) == -1) {
                            r0.peeked = 18;
                            return 18;
                        }
                        checkLenient();
                    } else if (peekStack == 8) {
                        throw new IllegalStateException("JsonReader is closed");
                    }
                }
            }
            r0.scopes[r0.stackSize - 1] = 4;
            if (peekStack == 5) {
                int c2 = nextNonWhitespace(true);
                r0.buffer.readByte();
                if (c2 != 44) {
                    if (c2 == 59) {
                        checkLenient();
                    } else if (c2 == 125) {
                        r0.peeked = 2;
                        return 2;
                    } else {
                        throw syntaxError("Unterminated object");
                    }
                }
            }
            int c3 = nextNonWhitespace(true);
            if (c3 == 34) {
                r0.buffer.readByte();
                r0.peeked = 13;
                return 13;
            } else if (c3 == 39) {
                r0.buffer.readByte();
                checkLenient();
                r0.peeked = 12;
                return 12;
            } else if (c3 != 125) {
                checkLenient();
                if (isLiteral((char) c3)) {
                    r0.peeked = 14;
                    return 14;
                }
                throw syntaxError("Expected name");
            } else if (peekStack != 5) {
                r0.buffer.readByte();
                r0.peeked = 2;
                return 2;
            } else {
                throw syntaxError("Expected name");
            }
        }
        c = nextNonWhitespace(true);
        if (c == 34) {
            r0.buffer.readByte();
            r0.peeked = 9;
            return 9;
        } else if (c != 39) {
            if (c != 44 && c != 59) {
                if (c == 91) {
                    r0.buffer.readByte();
                    r0.peeked = 3;
                    return 3;
                } else if (c != 93) {
                    if (c != 123) {
                        int result = peekKeyword();
                        if (result != 0) {
                            return result;
                        }
                        result = peekNumber();
                        if (result != 0) {
                            return result;
                        }
                        if (isLiteral(r0.buffer.getByte(0))) {
                            checkLenient();
                            r0.peeked = 10;
                            return 10;
                        }
                        throw syntaxError("Expected value");
                    }
                    r0.buffer.readByte();
                    r0.peeked = 1;
                    return 1;
                } else if (peekStack == 1) {
                    r0.buffer.readByte();
                    r0.peeked = 4;
                    return 4;
                }
            }
            if (peekStack != 1) {
                if (peekStack != 2) {
                    throw syntaxError("Unexpected value");
                }
            }
            checkLenient();
            r0.peeked = 7;
            return 7;
        } else {
            checkLenient();
            r0.buffer.readByte();
            r0.peeked = 8;
            return 8;
        }
    }

    private int peekKeyword() throws IOException {
        String keyword;
        String keywordUpper;
        int peeking;
        int length;
        int i;
        char c;
        byte c2 = this.buffer.getByte(0);
        if (c2 != (byte) 116) {
            if (c2 != (byte) 84) {
                if (c2 != (byte) 102) {
                    if (c2 != (byte) 70) {
                        if (c2 != (byte) 110) {
                            if (c2 != (byte) 78) {
                                return 0;
                            }
                        }
                        keyword = "null";
                        keywordUpper = "NULL";
                        peeking = 7;
                        length = keyword.length();
                        i = 1;
                        while (i < length) {
                            if (this.source.request((long) (i + 1))) {
                                return 0;
                            }
                            c = this.buffer.getByte((long) i);
                            if (c != keyword.charAt(i) && c != keywordUpper.charAt(i)) {
                                return 0;
                            }
                            i++;
                        }
                        if (!this.source.request((long) (length + 1)) && isLiteral(this.buffer.getByte((long) length))) {
                            return 0;
                        }
                        this.buffer.skip((long) length);
                        this.peeked = peeking;
                        return peeking;
                    }
                }
                keyword = "false";
                keywordUpper = "FALSE";
                peeking = 6;
                length = keyword.length();
                i = 1;
                while (i < length) {
                    if (this.source.request((long) (i + 1))) {
                        return 0;
                    }
                    c = this.buffer.getByte((long) i);
                    if (c != keyword.charAt(i)) {
                    }
                    i++;
                }
                if (!this.source.request((long) (length + 1))) {
                }
                this.buffer.skip((long) length);
                this.peeked = peeking;
                return peeking;
            }
        }
        keyword = "true";
        keywordUpper = "TRUE";
        peeking = 5;
        length = keyword.length();
        i = 1;
        while (i < length) {
            if (this.source.request((long) (i + 1))) {
                return 0;
            }
            c = this.buffer.getByte((long) i);
            if (c != keyword.charAt(i)) {
            }
            i++;
        }
        if (!this.source.request((long) (length + 1))) {
        }
        this.buffer.skip((long) length);
        this.peeked = peeking;
        return peeking;
    }

    private int peekNumber() throws IOException {
        long value = 0;
        boolean negative = false;
        boolean fitsInLong = true;
        int last = 0;
        for (int i = 0; this.source.request((long) (i + 1)); i++) {
            byte c = this.buffer.getByte((long) i);
            if (c != (byte) 43) {
                if (c == (byte) 69 || c == (byte) 101) {
                    if (last != 2) {
                        if (last != 4) {
                            return 0;
                        }
                    }
                    last = 5;
                } else {
                    switch (c) {
                        case (byte) 45:
                            if (last == 0) {
                                negative = true;
                                last = 1;
                                continue;
                            } else if (last == 5) {
                                last = 6;
                                break;
                            } else {
                                return 0;
                            }
                        case (byte) 46:
                            if (last != 2) {
                                return 0;
                            }
                            last = 3;
                            continue;
                        default:
                            if (c >= (byte) 48) {
                                if (c <= (byte) 57) {
                                    int i2 = 1;
                                    if (last != 1) {
                                        if (last != 0) {
                                            if (last != 2) {
                                                if (last != 3) {
                                                    if (last != 5) {
                                                        if (last != 6) {
                                                            break;
                                                        }
                                                    }
                                                    last = 7;
                                                    break;
                                                }
                                                last = 4;
                                                break;
                                            } else if (value == 0) {
                                                return 0;
                                            } else {
                                                long newValue = (10 * value) - ((long) (c - 48));
                                                if (value <= MIN_INCOMPLETE_INTEGER) {
                                                    if (value != MIN_INCOMPLETE_INTEGER || newValue >= value) {
                                                        i2 = 0;
                                                    }
                                                }
                                                fitsInLong &= i2;
                                                value = newValue;
                                                continue;
                                            }
                                        }
                                    }
                                    value = (long) (-(c - 48));
                                    last = 2;
                                    break;
                                }
                            }
                            if (!isLiteral(c)) {
                                break;
                            }
                            return 0;
                    }
                    if (last == 2 || !fitsInLong || ((value == Long.MIN_VALUE && !negative) || (value == 0 && negative))) {
                        if (!(last == 2 || last == 4)) {
                            if (last == 7) {
                                return 0;
                            }
                        }
                        this.peekedNumberLength = i;
                        this.peeked = 17;
                        return 17;
                    }
                    this.peekedLong = negative ? value : -value;
                    this.buffer.skip((long) i);
                    this.peeked = 16;
                    return 16;
                }
            } else if (last != 5) {
                return 0;
            } else {
                last = 6;
            }
        }
        if (last == 2) {
        }
        if (last == 7) {
            return 0;
        }
        this.peekedNumberLength = i;
        this.peeked = 17;
        return 17;
    }

    private boolean isLiteral(int c) throws IOException {
        switch (c) {
            case 9:
            case 10:
            case 12:
            case 13:
            case 32:
            case 44:
            case 58:
            case 91:
            case 93:
            case 123:
            case 125:
                break;
            case 35:
            case 47:
            case 59:
            case 61:
            case 92:
                checkLenient();
                break;
            default:
                return true;
        }
        return false;
    }

    public String nextName() throws IOException {
        String result;
        int p = this.peeked;
        if (p == 0) {
            p = doPeek();
        }
        if (p == 14) {
            result = nextUnquotedValue();
        } else if (p == 13) {
            result = nextQuotedValue(DOUBLE_QUOTE_OR_SLASH);
        } else if (p == 12) {
            result = nextQuotedValue(SINGLE_QUOTE_OR_SLASH);
        } else if (p == 15) {
            result = this.peekedString;
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Expected a name but was ");
            stringBuilder.append(peek());
            stringBuilder.append(" at path ");
            stringBuilder.append(getPath());
            throw new JsonDataException(stringBuilder.toString());
        }
        this.peeked = 0;
        this.pathNames[this.stackSize - 1] = result;
        return result;
    }

    public int selectName(Options options) throws IOException {
        int p = this.peeked;
        if (p == 0) {
            p = doPeek();
        }
        if (p >= 12) {
            if (p <= 15) {
                if (p == 15) {
                    return findName(this.peekedString, options);
                }
                int result = this.source.select(options.doubleQuoteSuffix);
                if (result != -1) {
                    this.peeked = 0;
                    this.pathNames[this.stackSize - 1] = options.strings[result];
                    return result;
                }
                String lastPathName = this.pathNames[this.stackSize - 1];
                String nextName = nextName();
                result = findName(nextName, options);
                if (result == -1) {
                    this.peeked = 15;
                    this.peekedString = nextName;
                    this.pathNames[this.stackSize - 1] = lastPathName;
                }
                return result;
            }
        }
        return -1;
    }

    public void skipName() throws IOException {
        if (this.failOnUnknown) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Cannot skip unexpected ");
            stringBuilder.append(peek());
            stringBuilder.append(" at ");
            stringBuilder.append(getPath());
            throw new JsonDataException(stringBuilder.toString());
        }
        int p = this.peeked;
        if (p == 0) {
            p = doPeek();
        }
        if (p == 14) {
            skipUnquotedValue();
        } else if (p == 13) {
            skipQuotedValue(DOUBLE_QUOTE_OR_SLASH);
        } else if (p == 12) {
            skipQuotedValue(SINGLE_QUOTE_OR_SLASH);
        } else if (p != 15) {
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Expected a name but was ");
            stringBuilder2.append(peek());
            stringBuilder2.append(" at path ");
            stringBuilder2.append(getPath());
            throw new JsonDataException(stringBuilder2.toString());
        }
        this.peeked = 0;
        this.pathNames[this.stackSize - 1] = "null";
    }

    private int findName(String name, Options options) {
        int size = options.strings.length;
        for (int i = 0; i < size; i++) {
            if (name.equals(options.strings[i])) {
                this.peeked = 0;
                this.pathNames[this.stackSize - 1] = name;
                return i;
            }
        }
        return -1;
    }

    public String nextString() throws IOException {
        String result;
        int p = this.peeked;
        if (p == 0) {
            p = doPeek();
        }
        if (p == 10) {
            result = nextUnquotedValue();
        } else if (p == 9) {
            result = nextQuotedValue(DOUBLE_QUOTE_OR_SLASH);
        } else if (p == 8) {
            result = nextQuotedValue(SINGLE_QUOTE_OR_SLASH);
        } else if (p == 11) {
            result = this.peekedString;
            this.peekedString = null;
        } else if (p == 16) {
            result = Long.toString(this.peekedLong);
        } else if (p == 17) {
            result = this.buffer.readUtf8((long) this.peekedNumberLength);
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Expected a string but was ");
            stringBuilder.append(peek());
            stringBuilder.append(" at path ");
            stringBuilder.append(getPath());
            throw new JsonDataException(stringBuilder.toString());
        }
        this.peeked = 0;
        int[] iArr = this.pathIndices;
        int i = this.stackSize - 1;
        iArr[i] = iArr[i] + 1;
        return result;
    }

    public int selectString(Options options) throws IOException {
        int p = this.peeked;
        if (p == 0) {
            p = doPeek();
        }
        if (p >= 8) {
            if (p <= 11) {
                if (p == 11) {
                    return findString(this.peekedString, options);
                }
                int result = this.source.select(options.doubleQuoteSuffix);
                int[] iArr;
                int i;
                if (result != -1) {
                    this.peeked = 0;
                    iArr = this.pathIndices;
                    i = this.stackSize - 1;
                    iArr[i] = iArr[i] + 1;
                    return result;
                }
                String nextString = nextString();
                result = findString(nextString, options);
                if (result == -1) {
                    this.peeked = 11;
                    this.peekedString = nextString;
                    iArr = this.pathIndices;
                    i = this.stackSize - 1;
                    iArr[i] = iArr[i] - 1;
                }
                return result;
            }
        }
        return -1;
    }

    private int findString(String string, Options options) {
        int size = options.strings.length;
        for (int i = 0; i < size; i++) {
            if (string.equals(options.strings[i])) {
                this.peeked = 0;
                int[] iArr = this.pathIndices;
                int i2 = this.stackSize - 1;
                iArr[i2] = iArr[i2] + 1;
                return i;
            }
        }
        return -1;
    }

    public boolean nextBoolean() throws IOException {
        int p = this.peeked;
        if (p == 0) {
            p = doPeek();
        }
        int[] iArr;
        if (p == 5) {
            this.peeked = 0;
            iArr = this.pathIndices;
            int i = this.stackSize - 1;
            iArr[i] = iArr[i] + 1;
            return true;
        } else if (p == 6) {
            this.peeked = 0;
            iArr = this.pathIndices;
            int i2 = this.stackSize - 1;
            iArr[i2] = iArr[i2] + 1;
            return false;
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Expected a boolean but was ");
            stringBuilder.append(peek());
            stringBuilder.append(" at path ");
            stringBuilder.append(getPath());
            throw new JsonDataException(stringBuilder.toString());
        }
    }

    @Nullable
    public <T> T nextNull() throws IOException {
        int p = this.peeked;
        if (p == 0) {
            p = doPeek();
        }
        if (p == 7) {
            this.peeked = 0;
            int[] iArr = this.pathIndices;
            int i = this.stackSize - 1;
            iArr[i] = iArr[i] + 1;
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Expected null but was ");
        stringBuilder.append(peek());
        stringBuilder.append(" at path ");
        stringBuilder.append(getPath());
        throw new JsonDataException(stringBuilder.toString());
    }

    public double nextDouble() throws IOException {
        int p = this.peeked;
        if (p == 0) {
            p = doPeek();
        }
        if (p == 16) {
            this.peeked = 0;
            int[] iArr = this.pathIndices;
            int i = this.stackSize - 1;
            iArr[i] = iArr[i] + 1;
            return (double) this.peekedLong;
        }
        if (p == 17) {
            this.peekedString = this.buffer.readUtf8((long) this.peekedNumberLength);
        } else if (p == 9) {
            this.peekedString = nextQuotedValue(DOUBLE_QUOTE_OR_SLASH);
        } else if (p == 8) {
            this.peekedString = nextQuotedValue(SINGLE_QUOTE_OR_SLASH);
        } else if (p == 10) {
            this.peekedString = nextUnquotedValue();
        } else if (p != 11) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Expected a double but was ");
            stringBuilder.append(peek());
            stringBuilder.append(" at path ");
            stringBuilder.append(getPath());
            throw new JsonDataException(stringBuilder.toString());
        }
        this.peeked = 11;
        try {
            double result = Double.parseDouble(this.peekedString);
            if (!this.lenient) {
                if (Double.isNaN(result) || Double.isInfinite(result)) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("JSON forbids NaN and infinities: ");
                    stringBuilder.append(result);
                    stringBuilder.append(" at path ");
                    stringBuilder.append(getPath());
                    throw new JsonEncodingException(stringBuilder.toString());
                }
            }
            this.peekedString = null;
            this.peeked = 0;
            iArr = this.pathIndices;
            i = this.stackSize - 1;
            iArr[i] = iArr[i] + 1;
            return result;
        } catch (NumberFormatException e) {
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Expected a double but was ");
            stringBuilder2.append(this.peekedString);
            stringBuilder2.append(" at path ");
            stringBuilder2.append(getPath());
            throw new JsonDataException(stringBuilder2.toString());
        }
    }

    public long nextLong() throws IOException {
        int p = this.peeked;
        if (p == 0) {
            p = doPeek();
        }
        if (p == 16) {
            this.peeked = 0;
            int[] iArr = this.pathIndices;
            int i = this.stackSize - 1;
            iArr[i] = iArr[i] + 1;
            return this.peekedLong;
        }
        if (p == 17) {
            this.peekedString = this.buffer.readUtf8((long) this.peekedNumberLength);
        } else {
            String nextQuotedValue;
            if (p != 9) {
                if (p != 8) {
                    if (p != 11) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Expected a long but was ");
                        stringBuilder.append(peek());
                        stringBuilder.append(" at path ");
                        stringBuilder.append(getPath());
                        throw new JsonDataException(stringBuilder.toString());
                    }
                }
            }
            if (p == 9) {
                nextQuotedValue = nextQuotedValue(DOUBLE_QUOTE_OR_SLASH);
            } else {
                nextQuotedValue = nextQuotedValue(SINGLE_QUOTE_OR_SLASH);
            }
            this.peekedString = nextQuotedValue;
            try {
                long result = Long.parseLong(this.peekedString);
                this.peeked = 0;
                iArr = this.pathIndices;
                int i2 = this.stackSize - 1;
                iArr[i2] = iArr[i2] + 1;
                return result;
            } catch (NumberFormatException e) {
            }
        }
        this.peeked = 11;
        try {
            long result2 = new BigDecimal(this.peekedString).longValueExact();
            this.peekedString = null;
            this.peeked = 0;
            iArr = this.pathIndices;
            i = this.stackSize - 1;
            iArr[i] = iArr[i] + 1;
            return result2;
        } catch (NumberFormatException e2) {
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Expected a long but was ");
            stringBuilder2.append(this.peekedString);
            stringBuilder2.append(" at path ");
            stringBuilder2.append(getPath());
            throw new JsonDataException(stringBuilder2.toString());
        }
    }

    private String nextUnquotedValue() throws IOException {
        long i = this.source.indexOfElement(UNQUOTED_STRING_TERMINALS);
        return i != -1 ? this.buffer.readUtf8(i) : this.buffer.readUtf8();
    }

    private void skipUnquotedValue() throws IOException {
        long i = this.source.indexOfElement(UNQUOTED_STRING_TERMINALS);
        Buffer buffer = this.buffer;
        buffer.skip(i != -1 ? i : buffer.size());
    }

    public int nextInt() throws IOException {
        int p = this.peeked;
        if (p == 0) {
            p = doPeek();
        }
        int result;
        if (p == 16) {
            long j = this.peekedLong;
            result = (int) j;
            if (j == ((long) result)) {
                this.peeked = 0;
                int[] iArr = this.pathIndices;
                int i = this.stackSize - 1;
                iArr[i] = iArr[i] + 1;
                return result;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Expected an int but was ");
            stringBuilder.append(this.peekedLong);
            stringBuilder.append(" at path ");
            stringBuilder.append(getPath());
            throw new JsonDataException(stringBuilder.toString());
        }
        if (p == 17) {
            this.peekedString = this.buffer.readUtf8((long) this.peekedNumberLength);
        } else {
            String nextQuotedValue;
            if (p != 9) {
                if (p != 8) {
                    if (p != 11) {
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("Expected an int but was ");
                        stringBuilder2.append(peek());
                        stringBuilder2.append(" at path ");
                        stringBuilder2.append(getPath());
                        throw new JsonDataException(stringBuilder2.toString());
                    }
                }
            }
            if (p == 9) {
                nextQuotedValue = nextQuotedValue(DOUBLE_QUOTE_OR_SLASH);
            } else {
                nextQuotedValue = nextQuotedValue(SINGLE_QUOTE_OR_SLASH);
            }
            this.peekedString = nextQuotedValue;
            try {
                result = Integer.parseInt(this.peekedString);
                this.peeked = 0;
                int[] iArr2 = this.pathIndices;
                int i2 = this.stackSize - 1;
                iArr2[i2] = iArr2[i2] + 1;
                return result;
            } catch (NumberFormatException e) {
            }
        }
        this.peeked = 11;
        try {
            double asDouble = Double.parseDouble(this.peekedString);
            result = (int) asDouble;
            if (((double) result) == asDouble) {
                this.peekedString = null;
                this.peeked = 0;
                iArr = this.pathIndices;
                i2 = this.stackSize - 1;
                iArr[i2] = iArr[i2] + 1;
                return result;
            }
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append("Expected an int but was ");
            stringBuilder3.append(this.peekedString);
            stringBuilder3.append(" at path ");
            stringBuilder3.append(getPath());
            throw new JsonDataException(stringBuilder3.toString());
        } catch (NumberFormatException e2) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("Expected an int but was ");
            stringBuilder.append(this.peekedString);
            stringBuilder.append(" at path ");
            stringBuilder.append(getPath());
            throw new JsonDataException(stringBuilder.toString());
        }
    }

    public void close() throws IOException {
        this.peeked = 0;
        this.scopes[0] = 8;
        this.stackSize = 1;
        this.buffer.clear();
        this.source.close();
    }

    private void checkLenient() throws IOException {
        if (!this.lenient) {
            throw syntaxError("Use JsonReader.setLenient(true) to accept malformed JSON");
        }
    }

    private void skipToEndOfLine() throws IOException {
        long index = this.source.indexOfElement(LINEFEED_OR_CARRIAGE_RETURN);
        Buffer buffer = this.buffer;
        buffer.skip(index != -1 ? 1 + index : buffer.size());
    }

    private boolean skipToEndOfBlockComment() throws IOException {
        long index = this.source.indexOf(CLOSING_BLOCK_COMMENT);
        boolean found = index != -1;
        Buffer buffer = this.buffer;
        buffer.skip(found ? ((long) CLOSING_BLOCK_COMMENT.size()) + index : buffer.size());
        return found;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("JsonReader(");
        stringBuilder.append(this.source);
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    void promoteNameToValue() throws IOException {
        if (hasNext()) {
            this.peekedString = nextName();
            this.peeked = 11;
        }
    }
}
