package com.google.android.exoplayer2.text.ttml;

import com.google.android.exoplayer2.text.SimpleSubtitleDecoder;
import com.google.android.exoplayer2.text.SubtitleDecoderException;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.util.XmlPullParserUtil;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public final class TtmlDecoder extends SimpleSubtitleDecoder {
    private static final String ATTR_BEGIN = "begin";
    private static final String ATTR_DURATION = "dur";
    private static final String ATTR_END = "end";
    private static final String ATTR_IMAGE = "backgroundImage";
    private static final String ATTR_REGION = "region";
    private static final String ATTR_STYLE = "style";
    private static final Pattern CELL_RESOLUTION = Pattern.compile("^(\\d+) (\\d+)$");
    private static final Pattern CLOCK_TIME = Pattern.compile("^([0-9][0-9]+):([0-9][0-9]):([0-9][0-9])(?:(\\.[0-9]+)|:([0-9][0-9])(?:\\.([0-9]+))?)?$");
    private static final CellResolution DEFAULT_CELL_RESOLUTION = new CellResolution(32, 15);
    private static final FrameAndTickRate DEFAULT_FRAME_AND_TICK_RATE = new FrameAndTickRate(30.0f, 1, 1);
    private static final int DEFAULT_FRAME_RATE = 30;
    private static final Pattern FONT_SIZE = Pattern.compile("^(([0-9]*.)?[0-9]+)(px|em|%)$");
    private static final Pattern OFFSET_TIME = Pattern.compile("^([0-9]+(?:\\.[0-9]+)?)(h|m|s|ms|f|t)$");
    private static final Pattern PERCENTAGE_COORDINATES = Pattern.compile("^(\\d+\\.?\\d*?)% (\\d+\\.?\\d*?)%$");
    private static final Pattern PIXEL_COORDINATES = Pattern.compile("^(\\d+\\.?\\d*?)px (\\d+\\.?\\d*?)px$");
    private static final String TAG = "TtmlDecoder";
    private static final String TTP = "http://www.w3.org/ns/ttml#parameter";
    private final XmlPullParserFactory xmlParserFactory;

    private static final class CellResolution {
        final int columns;
        final int rows;

        CellResolution(int columns, int rows) {
            this.columns = columns;
            this.rows = rows;
        }
    }

    private static final class FrameAndTickRate {
        final float effectiveFrameRate;
        final int subFrameRate;
        final int tickRate;

        FrameAndTickRate(float effectiveFrameRate, int subFrameRate, int tickRate) {
            this.effectiveFrameRate = effectiveFrameRate;
            this.subFrameRate = subFrameRate;
            this.tickRate = tickRate;
        }
    }

    private static final class TtsExtent {
        final int height;
        final int width;

        TtsExtent(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }

    private static void parseFontSize(java.lang.String r9, com.google.android.exoplayer2.text.ttml.TtmlStyle r10) throws com.google.android.exoplayer2.text.SubtitleDecoderException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:36:0x00d4 in {2, 5, 14, 17, 20, 23, 26, 27, 28, 29, 31, 33, 35} preds:[]
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
        r0 = "\\s+";
        r0 = com.google.android.exoplayer2.util.Util.split(r9, r0);
        r1 = r0.length;
        r2 = 2;
        r3 = 1;
        if (r1 != r3) goto L_0x0012;
    L_0x000b:
        r1 = FONT_SIZE;
        r1 = r1.matcher(r9);
        goto L_0x0024;
    L_0x0012:
        r1 = r0.length;
        if (r1 != r2) goto L_0x00b7;
    L_0x0015:
        r1 = FONT_SIZE;
        r4 = r0[r3];
        r1 = r1.matcher(r4);
        r4 = "TtmlDecoder";
        r5 = "Multiple values in fontSize attribute. Picking the second value for vertical font size and ignoring the first.";
        com.google.android.exoplayer2.util.Log.m10w(r4, r5);
    L_0x0024:
        r4 = r1.matches();
        if (r4 == 0) goto L_0x009b;
    L_0x002a:
        r4 = 3;
        r5 = r1.group(r4);
        r6 = -1;
        r7 = r5.hashCode();
        r8 = 37;
        if (r7 == r8) goto L_0x0056;
    L_0x0038:
        r8 = 3240; // 0xca8 float:4.54E-42 double:1.601E-320;
        if (r7 == r8) goto L_0x004c;
    L_0x003c:
        r8 = 3592; // 0xe08 float:5.033E-42 double:1.7747E-320;
        if (r7 == r8) goto L_0x0041;
    L_0x0040:
        goto L_0x005f;
    L_0x0041:
        r7 = "px";
        r7 = r5.equals(r7);
        if (r7 == 0) goto L_0x0040;
    L_0x004a:
        r6 = 0;
        goto L_0x005f;
    L_0x004c:
        r7 = "em";
        r7 = r5.equals(r7);
        if (r7 == 0) goto L_0x0040;
    L_0x0054:
        r6 = 1;
        goto L_0x005f;
    L_0x0056:
        r7 = "%";
        r7 = r5.equals(r7);
        if (r7 == 0) goto L_0x0040;
    L_0x005e:
        r6 = 2;
    L_0x005f:
        switch(r6) {
            case 0: goto L_0x0086;
            case 1: goto L_0x0082;
            case 2: goto L_0x007e;
            default: goto L_0x0062;
        };
    L_0x0062:
        r2 = new com.google.android.exoplayer2.text.SubtitleDecoderException;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "Invalid unit for fontSize: '";
        r3.append(r4);
        r3.append(r5);
        r4 = "'.";
        r3.append(r4);
        r3 = r3.toString();
        r2.<init>(r3);
        throw r2;
    L_0x007e:
        r10.setFontSizeUnit(r4);
        goto L_0x008a;
    L_0x0082:
        r10.setFontSizeUnit(r2);
        goto L_0x008a;
    L_0x0086:
        r10.setFontSizeUnit(r3);
    L_0x008a:
        r2 = r1.group(r3);
        r2 = java.lang.Float.valueOf(r2);
        r2 = r2.floatValue();
        r10.setFontSize(r2);
        return;
    L_0x009b:
        r2 = new com.google.android.exoplayer2.text.SubtitleDecoderException;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "Invalid expression for fontSize: '";
        r3.append(r4);
        r3.append(r9);
        r4 = "'.";
        r3.append(r4);
        r3 = r3.toString();
        r2.<init>(r3);
        throw r2;
    L_0x00b7:
        r1 = new com.google.android.exoplayer2.text.SubtitleDecoderException;
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "Invalid number of entries for fontSize: ";
        r2.append(r3);
        r3 = r0.length;
        r2.append(r3);
        r3 = ".";
        r2.append(r3);
        r2 = r2.toString();
        r1.<init>(r2);
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.text.ttml.TtmlDecoder.parseFontSize(java.lang.String, com.google.android.exoplayer2.text.ttml.TtmlStyle):void");
    }

    private static long parseTimeExpression(java.lang.String r16, com.google.android.exoplayer2.text.ttml.TtmlDecoder.FrameAndTickRate r17) throws com.google.android.exoplayer2.text.SubtitleDecoderException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:60:0x0166 in {4, 5, 8, 9, 12, 13, 15, 30, 33, 36, 39, 42, 45, 46, 49, 50, 51, 52, 53, 54, 55, 57, 59} preds:[]
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
        r0 = r16;
        r1 = r17;
        r2 = CLOCK_TIME;
        r2 = r2.matcher(r0);
        r3 = r2.matches();
        r6 = 5;
        r7 = 4;
        r8 = 3;
        r9 = 2;
        r10 = 1;
        if (r3 == 0) goto L_0x008e;
    L_0x0015:
        r3 = r2.group(r10);
        r10 = java.lang.Long.parseLong(r3);
        r12 = 3600; // 0xe10 float:5.045E-42 double:1.7786E-320;
        r10 = r10 * r12;
        r10 = (double) r10;
        r9 = r2.group(r9);
        r12 = java.lang.Long.parseLong(r9);
        r14 = 60;
        r12 = r12 * r14;
        r12 = (double) r12;
        java.lang.Double.isNaN(r10);
        java.lang.Double.isNaN(r12);
        r10 = r10 + r12;
        r8 = r2.group(r8);
        r12 = java.lang.Long.parseLong(r8);
        r12 = (double) r12;
        java.lang.Double.isNaN(r12);
        r10 = r10 + r12;
        r7 = r2.group(r7);
        r12 = 0;
        if (r7 == 0) goto L_0x0050;
    L_0x004b:
        r14 = java.lang.Double.parseDouble(r7);
        goto L_0x0051;
    L_0x0050:
        r14 = r12;
    L_0x0051:
        r10 = r10 + r14;
        r6 = r2.group(r6);
        if (r6 == 0) goto L_0x0062;
    L_0x0058:
        r14 = java.lang.Long.parseLong(r6);
        r14 = (float) r14;
        r15 = r1.effectiveFrameRate;
        r14 = r14 / r15;
        r14 = (double) r14;
        goto L_0x0063;
    L_0x0062:
        r14 = r12;
    L_0x0063:
        r10 = r10 + r14;
        r14 = 6;
        r14 = r2.group(r14);
        if (r14 == 0) goto L_0x0082;
    L_0x006b:
        r12 = java.lang.Long.parseLong(r14);
        r12 = (double) r12;
        r15 = r1.subFrameRate;
        r4 = (double) r15;
        java.lang.Double.isNaN(r12);
        java.lang.Double.isNaN(r4);
        r12 = r12 / r4;
        r4 = r1.effectiveFrameRate;
        r4 = (double) r4;
        java.lang.Double.isNaN(r4);
        r12 = r12 / r4;
        goto L_0x0083;
    L_0x0083:
        r10 = r10 + r12;
        r4 = 4696837146684686336; // 0x412e848000000000 float:0.0 double:1000000.0;
        r4 = r4 * r10;
        r4 = (long) r4;
        return r4;
        r3 = OFFSET_TIME;
        r2 = r3.matcher(r0);
        r3 = r2.matches();
        if (r3 == 0) goto L_0x014e;
        r3 = r2.group(r10);
        r4 = java.lang.Double.parseDouble(r3);
        r11 = r2.group(r9);
        r12 = -1;
        r13 = r11.hashCode();
        r14 = 102; // 0x66 float:1.43E-43 double:5.04E-322;
        if (r13 == r14) goto L_0x0103;
    L_0x00b5:
        r7 = 104; // 0x68 float:1.46E-43 double:5.14E-322;
        if (r13 == r7) goto L_0x00f7;
    L_0x00b9:
        r7 = 109; // 0x6d float:1.53E-43 double:5.4E-322;
        if (r13 == r7) goto L_0x00eb;
    L_0x00bd:
        r7 = 3494; // 0xda6 float:4.896E-42 double:1.7263E-320;
        if (r13 == r7) goto L_0x00df;
    L_0x00c1:
        switch(r13) {
            case 115: goto L_0x00d2;
            case 116: goto L_0x00c6;
            default: goto L_0x00c4;
        };
        goto L_0x010f;
        r7 = "t";
        r7 = r11.equals(r7);
        if (r7 == 0) goto L_0x00c4;
        goto L_0x0110;
        r6 = "s";
        r6 = r11.equals(r6);
        if (r6 == 0) goto L_0x00c4;
        r6 = 2;
        goto L_0x0110;
        r6 = "ms";
        r6 = r11.equals(r6);
        if (r6 == 0) goto L_0x00c4;
        r6 = 3;
        goto L_0x0110;
        r6 = "m";
        r6 = r11.equals(r6);
        if (r6 == 0) goto L_0x00c4;
        r6 = 1;
        goto L_0x0110;
        r6 = "h";
        r6 = r11.equals(r6);
        if (r6 == 0) goto L_0x00c4;
        r6 = 0;
        goto L_0x0110;
        r6 = "f";
        r6 = r11.equals(r6);
        if (r6 == 0) goto L_0x00c4;
        r6 = 4;
        goto L_0x0110;
    L_0x010f:
        r6 = -1;
        switch(r6) {
            case 0: goto L_0x013b;
            case 1: goto L_0x0134;
            case 2: goto L_0x0132;
            case 3: goto L_0x0129;
            case 4: goto L_0x011f;
            case 5: goto L_0x0116;
            default: goto L_0x0114;
        };
        goto L_0x0144;
        r6 = r1.tickRate;
        r6 = (double) r6;
        java.lang.Double.isNaN(r6);
        r4 = r4 / r6;
        goto L_0x0144;
        r6 = r1.effectiveFrameRate;
        r6 = (double) r6;
        java.lang.Double.isNaN(r6);
        r4 = r4 / r6;
        goto L_0x0144;
        r6 = 4652007308841189376; // 0x408f400000000000 float:0.0 double:1000.0;
        r4 = r4 / r6;
        goto L_0x0144;
        goto L_0x0144;
        r6 = 4633641066610819072; // 0x404e000000000000 float:0.0 double:60.0;
        r4 = r4 * r6;
        goto L_0x0144;
        r6 = 4660134898793709568; // 0x40ac200000000000 float:0.0 double:3600.0;
        r4 = r4 * r6;
        r6 = 4696837146684686336; // 0x412e848000000000 float:0.0 double:1000000.0;
        r6 = r6 * r4;
        r6 = (long) r6;
        return r6;
        r3 = new com.google.android.exoplayer2.text.SubtitleDecoderException;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "Malformed time expression: ";
        r4.append(r5);
        r4.append(r0);
        r4 = r4.toString();
        r3.<init>(r4);
        throw r3;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.text.ttml.TtmlDecoder.parseTimeExpression(java.lang.String, com.google.android.exoplayer2.text.ttml.TtmlDecoder$FrameAndTickRate):long");
    }

    protected com.google.android.exoplayer2.text.ttml.TtmlSubtitle decode(byte[] r24, int r25, boolean r26) throws com.google.android.exoplayer2.text.SubtitleDecoderException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:60:0x0187 in {11, 12, 15, 19, 25, 26, 27, 28, 31, 34, 38, 39, 40, 41, 42, 43, 46, 48, 49, 51, 53, 56, 59} preds:[]
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
        r23 = this;
        r8 = r23;
        r0 = r8.xmlParserFactory;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r0 = r0.newPullParser();	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r9 = r0;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r0 = new java.util.HashMap;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r0.<init>();	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r10 = r0;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r0 = new java.util.HashMap;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r0.<init>();	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r11 = r0;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r0 = new java.util.HashMap;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r0.<init>();	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r12 = r0;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r0 = "";	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r1 = new com.google.android.exoplayer2.text.ttml.TtmlRegion;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r2 = 0;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r1.<init>(r2);	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r11.put(r0, r1);	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r0 = new java.io.ByteArrayInputStream;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r1 = 0;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r13 = r24;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r14 = r25;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r0.<init>(r13, r1, r14);	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r15 = r0;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r9.setInput(r15, r2);	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r0 = 0;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r1 = new java.util.ArrayDeque;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r1.<init>();	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r7 = r1;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r1 = 0;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r2 = r9.getEventType();	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r3 = DEFAULT_FRAME_AND_TICK_RATE;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r4 = DEFAULT_CELL_RESOLUTION;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r5 = 0;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r16 = r0;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r17 = r1;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r6 = r2;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
    L_0x004a:
        r0 = 1;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        if (r6 == r0) goto L_0x016f;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
    L_0x004d:
        r0 = r7.peek();	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r0 = (com.google.android.exoplayer2.text.ttml.TtmlNode) r0;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r2 = r0;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r0 = 3;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r1 = 2;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        if (r17 != 0) goto L_0x014c;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
    L_0x0058:
        r18 = r9.getName();	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r19 = r18;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        if (r6 != r1) goto L_0x00fb;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
    L_0x0060:
        r0 = "tt";	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r1 = r19;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r0 = r0.equals(r1);	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        if (r0 == 0) goto L_0x0080;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
    L_0x006b:
        r0 = r8.parseFrameAndTickRates(r9);	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r3 = r0;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r0 = DEFAULT_CELL_RESOLUTION;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r0 = r8.parseCellResolution(r9, r0);	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r4 = r8.parseTtsExtent(r9);	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r18 = r0;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r5 = r3;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r19 = r4;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        goto L_0x0085;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
    L_0x0080:
        r18 = r4;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r19 = r5;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r5 = r3;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
    L_0x0085:
        r0 = isSupportedTag(r1);	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        if (r0 != 0) goto L_0x00b0;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
    L_0x008b:
        r0 = "TtmlDecoder";	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r3 = new java.lang.StringBuilder;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r3.<init>();	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r4 = "Ignoring unsupported tag: ";	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r3.append(r4);	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r4 = r9.getName();	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r3.append(r4);	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r3 = r3.toString();	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        com.google.android.exoplayer2.util.Log.m8i(r0, r3);	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r17 = r17 + 1;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r13 = r2;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r14 = r5;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r2 = r10;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r21 = r15;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r15 = r6;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r10 = r7;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        goto L_0x0146;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
    L_0x00b0:
        r0 = "head";	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r0 = r0.equals(r1);	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        if (r0 == 0) goto L_0x00d1;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
    L_0x00b8:
        r20 = r1;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r1 = r23;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r4 = r2;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r2 = r9;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r3 = r10;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r13 = r4;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r4 = r18;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r14 = r5;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r5 = r19;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r21 = r15;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r15 = r6;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r6 = r11;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r22 = r10;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r10 = r7;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r7 = r12;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r1.parseHeader(r2, r3, r4, r5, r6, r7);	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        goto L_0x00ea;
    L_0x00d1:
        r20 = r1;
        r13 = r2;
        r14 = r5;
        r22 = r10;
        r21 = r15;
        r15 = r6;
        r10 = r7;
        r0 = r8.parseNode(r9, r13, r11, r14);	 Catch:{ SubtitleDecoderException -> 0x00ed }
        r10.push(r0);	 Catch:{ SubtitleDecoderException -> 0x00ed }
        if (r13 == 0) goto L_0x00e8;	 Catch:{ SubtitleDecoderException -> 0x00ed }
    L_0x00e4:
        r13.addChild(r0);	 Catch:{ SubtitleDecoderException -> 0x00ed }
        goto L_0x00e9;
    L_0x00ea:
        r2 = r22;
        goto L_0x0146;
    L_0x00ed:
        r0 = move-exception;
        r1 = "TtmlDecoder";	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r2 = "Suppressing parser error";	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        com.google.android.exoplayer2.util.Log.m11w(r1, r2, r0);	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r17 = r17 + 1;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r2 = r22;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        goto L_0x0146;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
    L_0x00fb:
        r13 = r2;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r22 = r10;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r21 = r15;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r20 = r19;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r15 = r6;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r10 = r7;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r1 = 4;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        if (r15 != r1) goto L_0x0115;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
    L_0x0107:
        r0 = r9.getText();	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r0 = com.google.android.exoplayer2.text.ttml.TtmlNode.buildTextNode(r0);	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r13.addChild(r0);	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r2 = r22;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        goto L_0x0141;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
    L_0x0115:
        if (r15 != r0) goto L_0x013f;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
    L_0x0117:
        r0 = r9.getName();	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r1 = "tt";	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r0 = r0.equals(r1);	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        if (r0 == 0) goto L_0x0134;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
    L_0x0124:
        r0 = new com.google.android.exoplayer2.text.ttml.TtmlSubtitle;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r1 = r10.peek();	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r1 = (com.google.android.exoplayer2.text.ttml.TtmlNode) r1;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r2 = r22;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r0.<init>(r1, r2, r11, r12);	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r16 = r0;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        goto L_0x0136;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
    L_0x0134:
        r2 = r22;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
    L_0x0136:
        r10.pop();	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r14 = r3;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r18 = r4;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r19 = r5;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        goto L_0x0146;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
    L_0x013f:
        r2 = r22;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
    L_0x0141:
        r14 = r3;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r18 = r4;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r19 = r5;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
    L_0x0146:
        r3 = r14;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r4 = r18;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r5 = r19;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        goto L_0x015d;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
    L_0x014c:
        r13 = r2;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r2 = r10;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r21 = r15;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r15 = r6;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r10 = r7;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        if (r15 != r1) goto L_0x0157;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
    L_0x0154:
        r17 = r17 + 1;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        goto L_0x015d;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
    L_0x0157:
        if (r15 != r0) goto L_0x015c;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
    L_0x0159:
        r17 = r17 + -1;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        goto L_0x015d;	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
    L_0x015d:
        r9.next();	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r0 = r9.getEventType();	 Catch:{ XmlPullParserException -> 0x017e, IOException -> 0x0175 }
        r6 = r0;
        r7 = r10;
        r15 = r21;
        r13 = r24;
        r14 = r25;
        r10 = r2;
        goto L_0x004a;
    L_0x016f:
        r2 = r10;
        r21 = r15;
        r15 = r6;
        r10 = r7;
        return r16;
    L_0x0175:
        r0 = move-exception;
        r1 = new java.lang.IllegalStateException;
        r2 = "Unexpected error when reading input.";
        r1.<init>(r2, r0);
        throw r1;
    L_0x017e:
        r0 = move-exception;
        r1 = new com.google.android.exoplayer2.text.SubtitleDecoderException;
        r2 = "Unable to decode source";
        r1.<init>(r2, r0);
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.text.ttml.TtmlDecoder.decode(byte[], int, boolean):com.google.android.exoplayer2.text.ttml.TtmlSubtitle");
    }

    public TtmlDecoder() {
        super(TAG);
        try {
            this.xmlParserFactory = XmlPullParserFactory.newInstance();
            this.xmlParserFactory.setNamespaceAware(true);
        } catch (XmlPullParserException e) {
            throw new RuntimeException("Couldn't create XmlPullParserFactory instance", e);
        }
    }

    private FrameAndTickRate parseFrameAndTickRates(XmlPullParser xmlParser) throws SubtitleDecoderException {
        int frameRate = 30;
        String frameRateString = xmlParser.getAttributeValue(TTP, "frameRate");
        if (frameRateString != null) {
            frameRate = Integer.parseInt(frameRateString);
        }
        float frameRateMultiplier = 1.0f;
        String frameRateMultiplierString = xmlParser.getAttributeValue(TTP, "frameRateMultiplier");
        if (frameRateMultiplierString != null) {
            String[] parts = Util.split(frameRateMultiplierString, StringUtils.SPACE);
            if (parts.length == 2) {
                frameRateMultiplier = ((float) Integer.parseInt(parts[0])) / ((float) Integer.parseInt(parts[1]));
            } else {
                throw new SubtitleDecoderException("frameRateMultiplier doesn't have 2 parts");
            }
        }
        int subFrameRate = DEFAULT_FRAME_AND_TICK_RATE.subFrameRate;
        String subFrameRateString = xmlParser.getAttributeValue(TTP, "subFrameRate");
        if (subFrameRateString != null) {
            subFrameRate = Integer.parseInt(subFrameRateString);
        }
        int tickRate = DEFAULT_FRAME_AND_TICK_RATE.tickRate;
        String tickRateString = xmlParser.getAttributeValue(TTP, "tickRate");
        if (tickRateString != null) {
            tickRate = Integer.parseInt(tickRateString);
        }
        return new FrameAndTickRate(((float) frameRate) * frameRateMultiplier, subFrameRate, tickRate);
    }

    private CellResolution parseCellResolution(XmlPullParser xmlParser, CellResolution defaultValue) throws SubtitleDecoderException {
        String cellResolution = xmlParser.getAttributeValue(TTP, "cellResolution");
        if (cellResolution == null) {
            return defaultValue;
        }
        Matcher cellResolutionMatcher = CELL_RESOLUTION.matcher(cellResolution);
        if (cellResolutionMatcher.matches()) {
            try {
                int columns = Integer.parseInt(cellResolutionMatcher.group(1));
                int rows = Integer.parseInt(cellResolutionMatcher.group(2));
                if (columns != 0 && rows != 0) {
                    return new CellResolution(columns, rows);
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Invalid cell resolution ");
                stringBuilder.append(columns);
                stringBuilder.append(StringUtils.SPACE);
                stringBuilder.append(rows);
                throw new SubtitleDecoderException(stringBuilder.toString());
            } catch (NumberFormatException e) {
                String str = TAG;
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Ignoring malformed cell resolution: ");
                stringBuilder2.append(cellResolution);
                Log.m10w(str, stringBuilder2.toString());
                return defaultValue;
            }
        }
        String str2 = TAG;
        StringBuilder stringBuilder3 = new StringBuilder();
        stringBuilder3.append("Ignoring malformed cell resolution: ");
        stringBuilder3.append(cellResolution);
        Log.m10w(str2, stringBuilder3.toString());
        return defaultValue;
    }

    private TtsExtent parseTtsExtent(XmlPullParser xmlParser) {
        String ttsExtent = XmlPullParserUtil.getAttributeValue(xmlParser, TtmlNode.ATTR_TTS_EXTENT);
        if (ttsExtent == null) {
            return null;
        }
        Matcher extentMatcher = PIXEL_COORDINATES.matcher(ttsExtent);
        if (extentMatcher.matches()) {
            try {
                return new TtsExtent(Integer.parseInt(extentMatcher.group(1)), Integer.parseInt(extentMatcher.group(2)));
            } catch (NumberFormatException e) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Ignoring malformed tts extent: ");
                stringBuilder.append(ttsExtent);
                Log.m10w(str, stringBuilder.toString());
                return null;
            }
        }
        String str2 = TAG;
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("Ignoring non-pixel tts extent: ");
        stringBuilder2.append(ttsExtent);
        Log.m10w(str2, stringBuilder2.toString());
        return null;
    }

    private Map<String, TtmlStyle> parseHeader(XmlPullParser xmlParser, Map<String, TtmlStyle> globalStyles, CellResolution cellResolution, TtsExtent ttsExtent, Map<String, TtmlRegion> globalRegions, Map<String, String> imageMap) throws IOException, XmlPullParserException {
        while (true) {
            xmlParser.next();
            if (XmlPullParserUtil.isStartTag(xmlParser, "style")) {
                String parentStyleId = XmlPullParserUtil.getAttributeValue(xmlParser, "style");
                TtmlStyle style = parseStyleAttributes(xmlParser, new TtmlStyle());
                if (parentStyleId != null) {
                    for (String id : parseStyleIds(parentStyleId)) {
                        style.chain((TtmlStyle) globalStyles.get(id));
                    }
                }
                if (style.getId() != null) {
                    globalStyles.put(style.getId(), style);
                }
            } else if (XmlPullParserUtil.isStartTag(xmlParser, "region")) {
                TtmlRegion ttmlRegion = parseRegionAttributes(xmlParser, cellResolution, ttsExtent);
                if (ttmlRegion != null) {
                    globalRegions.put(ttmlRegion.id, ttmlRegion);
                }
            } else if (XmlPullParserUtil.isStartTag(xmlParser, TtmlNode.TAG_METADATA)) {
                parseMetadata(xmlParser, imageMap);
            }
            if (XmlPullParserUtil.isEndTag(xmlParser, "head")) {
                return globalStyles;
            }
        }
    }

    private void parseMetadata(XmlPullParser xmlParser, Map<String, String> imageMap) throws IOException, XmlPullParserException {
        while (true) {
            xmlParser.next();
            if (XmlPullParserUtil.isStartTag(xmlParser, "image")) {
                String id = XmlPullParserUtil.getAttributeValue(xmlParser, "id");
                if (id != null) {
                    imageMap.put(id, xmlParser.nextText());
                }
            }
            if (XmlPullParserUtil.isEndTag(xmlParser, TtmlNode.TAG_METADATA)) {
                return;
            }
        }
    }

    private TtmlRegion parseRegionAttributes(XmlPullParser xmlParser, CellResolution cellResolution, TtsExtent ttsExtent) {
        String str;
        StringBuilder stringBuilder;
        XmlPullParser xmlPullParser = xmlParser;
        TtsExtent ttsExtent2 = ttsExtent;
        String regionId = XmlPullParserUtil.getAttributeValue(xmlPullParser, "id");
        TtmlRegion ttmlRegion = null;
        if (regionId == null) {
            return ttmlRegion;
        }
        String regionOrigin = XmlPullParserUtil.getAttributeValue(xmlPullParser, TtmlNode.ATTR_TTS_ORIGIN);
        if (regionOrigin != null) {
            float line;
            float position;
            String str2;
            StringBuilder stringBuilder2;
            Matcher originPercentageMatcher = PERCENTAGE_COORDINATES.matcher(regionOrigin);
            Matcher originPixelMatcher = PIXEL_COORDINATES.matcher(regionOrigin);
            int i = 1;
            if (originPercentageMatcher.matches()) {
                try {
                    line = Float.parseFloat(originPercentageMatcher.group(2)) / 100.0f;
                    position = Float.parseFloat(originPercentageMatcher.group(1)) / 100.0f;
                } catch (NumberFormatException e) {
                    str = TAG;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Ignoring region with malformed origin: ");
                    stringBuilder.append(regionOrigin);
                    Log.m10w(str, stringBuilder.toString());
                    return ttmlRegion;
                }
            } else if (!originPixelMatcher.matches()) {
                str2 = TAG;
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Ignoring region with unsupported origin: ");
                stringBuilder2.append(regionOrigin);
                Log.m10w(str2, stringBuilder2.toString());
                return ttmlRegion;
            } else if (ttsExtent2 == null) {
                str2 = TAG;
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Ignoring region with missing tts:extent: ");
                stringBuilder2.append(regionOrigin);
                Log.m10w(str2, stringBuilder2.toString());
                return ttmlRegion;
            } else {
                try {
                    line = ((float) Integer.parseInt(originPixelMatcher.group(2))) / ((float) ttsExtent2.height);
                    position = ((float) Integer.parseInt(originPixelMatcher.group(1))) / ((float) ttsExtent2.width);
                } catch (NumberFormatException e2) {
                    str = TAG;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Ignoring region with malformed origin: ");
                    stringBuilder.append(regionOrigin);
                    Log.m10w(str, stringBuilder.toString());
                    return ttmlRegion;
                }
            }
            String regionExtent = XmlPullParserUtil.getAttributeValue(xmlPullParser, TtmlNode.ATTR_TTS_EXTENT);
            if (regionExtent != null) {
                float width;
                float height;
                int lineAnchor;
                float line2;
                originPercentageMatcher = PERCENTAGE_COORDINATES.matcher(regionExtent);
                originPixelMatcher = PIXEL_COORDINATES.matcher(regionExtent);
                if (originPercentageMatcher.matches()) {
                    try {
                        width = Float.parseFloat(originPercentageMatcher.group(1)) / 100.0f;
                        ttmlRegion = Float.parseFloat(originPercentageMatcher.group(2));
                        height = ttmlRegion / 100.0f;
                    } catch (NumberFormatException e3) {
                        str = TAG;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("Ignoring region with malformed extent: ");
                        stringBuilder.append(regionOrigin);
                        Log.m10w(str, stringBuilder.toString());
                        return ttmlRegion;
                    }
                } else if (!originPixelMatcher.matches()) {
                    str2 = TAG;
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("Ignoring region with unsupported extent: ");
                    stringBuilder2.append(regionOrigin);
                    Log.m10w(str2, stringBuilder2.toString());
                    return ttmlRegion;
                } else if (ttsExtent2 == null) {
                    str2 = TAG;
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("Ignoring region with missing tts:extent: ");
                    stringBuilder2.append(regionOrigin);
                    Log.m10w(str2, stringBuilder2.toString());
                    return ttmlRegion;
                } else {
                    try {
                        height = ((float) Integer.parseInt(originPixelMatcher.group(2))) / ((float) ttsExtent2.height);
                        width = ((float) Integer.parseInt(originPixelMatcher.group(1))) / ((float) ttsExtent2.width);
                    } catch (NumberFormatException e4) {
                        str = TAG;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("Ignoring region with malformed extent: ");
                        stringBuilder.append(regionOrigin);
                        Log.m10w(str, stringBuilder.toString());
                        return ttmlRegion;
                    }
                }
                String displayAlign = XmlPullParserUtil.getAttributeValue(xmlPullParser, TtmlNode.ATTR_TTS_DISPLAY_ALIGN);
                if (displayAlign != null) {
                    String toLowerInvariant = Util.toLowerInvariant(displayAlign);
                    int hashCode = toLowerInvariant.hashCode();
                    if (hashCode == -1364013995) {
                        if (toLowerInvariant.equals(TtmlNode.CENTER)) {
                            i = 0;
                            switch (i) {
                                case 0:
                                    lineAnchor = 1;
                                    line2 = line + (height / 2.0f);
                                    break;
                                case 1:
                                    lineAnchor = 2;
                                    line2 = line + height;
                                    break;
                                default:
                                    break;
                            }
                        }
                    } else if (hashCode == 92734940 && toLowerInvariant.equals("after")) {
                        switch (i) {
                            case 0:
                                lineAnchor = 1;
                                line2 = line + (height / 2.0f);
                                break;
                            case 1:
                                lineAnchor = 2;
                                line2 = line + height;
                                break;
                            default:
                                break;
                        }
                    }
                    i = -1;
                    switch (i) {
                        case 0:
                            lineAnchor = 1;
                            line2 = line + (height / 2.0f);
                            break;
                        case 1:
                            lineAnchor = 2;
                            line2 = line + height;
                            break;
                        default:
                            break;
                    }
                }
                lineAnchor = 0;
                line2 = line;
                return new TtmlRegion(regionId, position, line2, 0, lineAnchor, width, 1, 1.0f / ((float) cellResolution.rows));
            }
            Log.m10w(TAG, "Ignoring region without an extent");
            return ttmlRegion;
        }
        Log.m10w(TAG, "Ignoring region without an origin");
        return ttmlRegion;
    }

    private String[] parseStyleIds(String parentStyleIds) {
        parentStyleIds = parentStyleIds.trim();
        return parentStyleIds.isEmpty() ? new String[0] : Util.split(parentStyleIds, "\\s+");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.google.android.exoplayer2.text.ttml.TtmlStyle parseStyleAttributes(org.xmlpull.v1.XmlPullParser r12, com.google.android.exoplayer2.text.ttml.TtmlStyle r13) {
        /*
        r11 = this;
        r0 = r12.getAttributeCount();
        r1 = 0;
    L_0x0005:
        if (r1 >= r0) goto L_0x021b;
    L_0x0007:
        r2 = r12.getAttributeValue(r1);
        r3 = r12.getAttributeName(r1);
        r4 = r3.hashCode();
        r5 = 4;
        r6 = 2;
        r7 = 3;
        r8 = -1;
        r9 = 0;
        r10 = 1;
        switch(r4) {
            case -1550943582: goto L_0x0070;
            case -1224696685: goto L_0x0066;
            case -1065511464: goto L_0x005b;
            case -879295043: goto L_0x004f;
            case -734428249: goto L_0x0045;
            case 3355: goto L_0x003b;
            case 94842723: goto L_0x0031;
            case 365601008: goto L_0x0027;
            case 1287124693: goto L_0x001d;
            default: goto L_0x001c;
        };
    L_0x001c:
        goto L_0x007a;
    L_0x001d:
        r4 = "backgroundColor";
        r3 = r3.equals(r4);
        if (r3 == 0) goto L_0x001c;
    L_0x0025:
        r3 = 1;
        goto L_0x007b;
    L_0x0027:
        r4 = "fontSize";
        r3 = r3.equals(r4);
        if (r3 == 0) goto L_0x001c;
    L_0x002f:
        r3 = 4;
        goto L_0x007b;
    L_0x0031:
        r4 = "color";
        r3 = r3.equals(r4);
        if (r3 == 0) goto L_0x001c;
    L_0x0039:
        r3 = 2;
        goto L_0x007b;
    L_0x003b:
        r4 = "id";
        r3 = r3.equals(r4);
        if (r3 == 0) goto L_0x001c;
    L_0x0043:
        r3 = 0;
        goto L_0x007b;
    L_0x0045:
        r4 = "fontWeight";
        r3 = r3.equals(r4);
        if (r3 == 0) goto L_0x001c;
    L_0x004d:
        r3 = 5;
        goto L_0x007b;
    L_0x004f:
        r4 = "textDecoration";
        r3 = r3.equals(r4);
        if (r3 == 0) goto L_0x001c;
    L_0x0058:
        r3 = 8;
        goto L_0x007b;
    L_0x005b:
        r4 = "textAlign";
        r3 = r3.equals(r4);
        if (r3 == 0) goto L_0x001c;
    L_0x0064:
        r3 = 7;
        goto L_0x007b;
    L_0x0066:
        r4 = "fontFamily";
        r3 = r3.equals(r4);
        if (r3 == 0) goto L_0x001c;
    L_0x006e:
        r3 = 3;
        goto L_0x007b;
    L_0x0070:
        r4 = "fontStyle";
        r3 = r3.equals(r4);
        if (r3 == 0) goto L_0x001c;
    L_0x0078:
        r3 = 6;
        goto L_0x007b;
    L_0x007a:
        r3 = -1;
    L_0x007b:
        switch(r3) {
            case 0: goto L_0x0200;
            case 1: goto L_0x01dc;
            case 2: goto L_0x01b8;
            case 3: goto L_0x01af;
            case 4: goto L_0x018d;
            case 5: goto L_0x017d;
            case 6: goto L_0x016d;
            case 7: goto L_0x00f0;
            case 8: goto L_0x0080;
            default: goto L_0x007e;
        };
    L_0x007e:
        goto L_0x0217;
    L_0x0080:
        r3 = com.google.android.exoplayer2.util.Util.toLowerInvariant(r2);
        r4 = r3.hashCode();
        r5 = -1461280213; // 0xffffffffa8e6a22b float:-2.5605459E-14 double:NaN;
        if (r4 == r5) goto L_0x00bb;
    L_0x008d:
        r5 = -1026963764; // 0xffffffffc2c9c6cc float:-100.888275 double:NaN;
        if (r4 == r5) goto L_0x00b1;
    L_0x0092:
        r5 = 913457136; // 0x36723ff0 float:3.6098027E-6 double:4.5130779E-315;
        if (r4 == r5) goto L_0x00a7;
    L_0x0097:
        r5 = 1679736913; // 0x641ec051 float:1.1713774E22 double:8.29900303E-315;
        if (r4 == r5) goto L_0x009d;
    L_0x009c:
        goto L_0x00c5;
    L_0x009d:
        r4 = "linethrough";
        r3 = r3.equals(r4);
        if (r3 == 0) goto L_0x009c;
    L_0x00a5:
        r6 = 0;
        goto L_0x00c6;
    L_0x00a7:
        r4 = "nolinethrough";
        r3 = r3.equals(r4);
        if (r3 == 0) goto L_0x009c;
    L_0x00af:
        r6 = 1;
        goto L_0x00c6;
    L_0x00b1:
        r4 = "underline";
        r3 = r3.equals(r4);
        if (r3 == 0) goto L_0x009c;
    L_0x00ba:
        goto L_0x00c6;
    L_0x00bb:
        r4 = "nounderline";
        r3 = r3.equals(r4);
        if (r3 == 0) goto L_0x009c;
    L_0x00c3:
        r6 = 3;
        goto L_0x00c6;
    L_0x00c5:
        r6 = -1;
    L_0x00c6:
        switch(r6) {
            case 0: goto L_0x00e5;
            case 1: goto L_0x00dc;
            case 2: goto L_0x00d3;
            case 3: goto L_0x00ca;
            default: goto L_0x00c9;
        };
    L_0x00c9:
        goto L_0x00ee;
    L_0x00ca:
        r3 = r11.createIfNull(r13);
        r13 = r3.setUnderline(r9);
        goto L_0x00ee;
    L_0x00d3:
        r3 = r11.createIfNull(r13);
        r13 = r3.setUnderline(r10);
        goto L_0x00ee;
    L_0x00dc:
        r3 = r11.createIfNull(r13);
        r13 = r3.setLinethrough(r9);
        goto L_0x00ee;
    L_0x00e5:
        r3 = r11.createIfNull(r13);
        r13 = r3.setLinethrough(r10);
    L_0x00ee:
        goto L_0x0217;
    L_0x00f0:
        r3 = com.google.android.exoplayer2.util.Util.toLowerInvariant(r2);
        r4 = r3.hashCode();
        switch(r4) {
            case -1364013995: goto L_0x0126;
            case 100571: goto L_0x011c;
            case 3317767: goto L_0x0112;
            case 108511772: goto L_0x0107;
            case 109757538: goto L_0x00fc;
            default: goto L_0x00fb;
        };
    L_0x00fb:
        goto L_0x012f;
    L_0x00fc:
        r4 = "start";
        r3 = r3.equals(r4);
        if (r3 == 0) goto L_0x00fb;
    L_0x0105:
        r5 = 1;
        goto L_0x0130;
    L_0x0107:
        r4 = "right";
        r3 = r3.equals(r4);
        if (r3 == 0) goto L_0x00fb;
    L_0x0110:
        r5 = 2;
        goto L_0x0130;
    L_0x0112:
        r4 = "left";
        r3 = r3.equals(r4);
        if (r3 == 0) goto L_0x00fb;
    L_0x011a:
        r5 = 0;
        goto L_0x0130;
    L_0x011c:
        r4 = "end";
        r3 = r3.equals(r4);
        if (r3 == 0) goto L_0x00fb;
    L_0x0124:
        r5 = 3;
        goto L_0x0130;
    L_0x0126:
        r4 = "center";
        r3 = r3.equals(r4);
        if (r3 == 0) goto L_0x00fb;
    L_0x012e:
        goto L_0x0130;
    L_0x012f:
        r5 = -1;
    L_0x0130:
        switch(r5) {
            case 0: goto L_0x0160;
            case 1: goto L_0x0155;
            case 2: goto L_0x014a;
            case 3: goto L_0x013f;
            case 4: goto L_0x0134;
            default: goto L_0x0133;
        };
    L_0x0133:
        goto L_0x016b;
    L_0x0134:
        r3 = r11.createIfNull(r13);
        r4 = android.text.Layout.Alignment.ALIGN_CENTER;
        r13 = r3.setTextAlign(r4);
        goto L_0x016b;
    L_0x013f:
        r3 = r11.createIfNull(r13);
        r4 = android.text.Layout.Alignment.ALIGN_OPPOSITE;
        r13 = r3.setTextAlign(r4);
        goto L_0x016b;
    L_0x014a:
        r3 = r11.createIfNull(r13);
        r4 = android.text.Layout.Alignment.ALIGN_OPPOSITE;
        r13 = r3.setTextAlign(r4);
        goto L_0x016b;
    L_0x0155:
        r3 = r11.createIfNull(r13);
        r4 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r13 = r3.setTextAlign(r4);
        goto L_0x016b;
    L_0x0160:
        r3 = r11.createIfNull(r13);
        r4 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r13 = r3.setTextAlign(r4);
    L_0x016b:
        goto L_0x0217;
    L_0x016d:
        r3 = r11.createIfNull(r13);
        r4 = "italic";
        r4 = r4.equalsIgnoreCase(r2);
        r13 = r3.setItalic(r4);
        goto L_0x0217;
    L_0x017d:
        r3 = r11.createIfNull(r13);
        r4 = "bold";
        r4 = r4.equalsIgnoreCase(r2);
        r13 = r3.setBold(r4);
        goto L_0x0217;
    L_0x018d:
        r3 = r11.createIfNull(r13);	 Catch:{ SubtitleDecoderException -> 0x0197 }
        r13 = r3;
        parseFontSize(r2, r13);	 Catch:{ SubtitleDecoderException -> 0x0197 }
        goto L_0x0217;
    L_0x0197:
        r3 = move-exception;
        r4 = "TtmlDecoder";
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "Failed parsing fontSize value: ";
        r5.append(r6);
        r5.append(r2);
        r5 = r5.toString();
        com.google.android.exoplayer2.util.Log.m10w(r4, r5);
        goto L_0x0217;
    L_0x01af:
        r3 = r11.createIfNull(r13);
        r13 = r3.setFontFamily(r2);
        goto L_0x0217;
    L_0x01b8:
        r13 = r11.createIfNull(r13);
        r3 = com.google.android.exoplayer2.util.ColorParser.parseTtmlColor(r2);	 Catch:{ IllegalArgumentException -> 0x01c4 }
        r13.setFontColor(r3);	 Catch:{ IllegalArgumentException -> 0x01c4 }
        goto L_0x0217;
    L_0x01c4:
        r3 = move-exception;
        r4 = "TtmlDecoder";
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "Failed parsing color value: ";
        r5.append(r6);
        r5.append(r2);
        r5 = r5.toString();
        com.google.android.exoplayer2.util.Log.m10w(r4, r5);
        goto L_0x0217;
    L_0x01dc:
        r13 = r11.createIfNull(r13);
        r3 = com.google.android.exoplayer2.util.ColorParser.parseTtmlColor(r2);	 Catch:{ IllegalArgumentException -> 0x01e8 }
        r13.setBackgroundColor(r3);	 Catch:{ IllegalArgumentException -> 0x01e8 }
        goto L_0x0217;
    L_0x01e8:
        r3 = move-exception;
        r4 = "TtmlDecoder";
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "Failed parsing background value: ";
        r5.append(r6);
        r5.append(r2);
        r5 = r5.toString();
        com.google.android.exoplayer2.util.Log.m10w(r4, r5);
        goto L_0x0217;
    L_0x0200:
        r3 = "style";
        r4 = r12.getName();
        r3 = r3.equals(r4);
        if (r3 == 0) goto L_0x0216;
    L_0x020d:
        r3 = r11.createIfNull(r13);
        r13 = r3.setId(r2);
        goto L_0x0217;
    L_0x0217:
        r1 = r1 + 1;
        goto L_0x0005;
    L_0x021b:
        return r13;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.text.ttml.TtmlDecoder.parseStyleAttributes(org.xmlpull.v1.XmlPullParser, com.google.android.exoplayer2.text.ttml.TtmlStyle):com.google.android.exoplayer2.text.ttml.TtmlStyle");
    }

    private TtmlStyle createIfNull(TtmlStyle style) {
        return style == null ? new TtmlStyle() : style;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.google.android.exoplayer2.text.ttml.TtmlNode parseNode(org.xmlpull.v1.XmlPullParser r27, com.google.android.exoplayer2.text.ttml.TtmlNode r28, java.util.Map<java.lang.String, com.google.android.exoplayer2.text.ttml.TtmlRegion> r29, com.google.android.exoplayer2.text.ttml.TtmlDecoder.FrameAndTickRate r30) throws com.google.android.exoplayer2.text.SubtitleDecoderException {
        /*
        r26 = this;
        r0 = r26;
        r1 = r27;
        r2 = r28;
        r3 = r30;
        r4 = -9223372036854775807; // 0x8000000000000001 float:1.4E-45 double:-4.9E-324;
        r6 = -9223372036854775807; // 0x8000000000000001 float:1.4E-45 double:-4.9E-324;
        r8 = -9223372036854775807; // 0x8000000000000001 float:1.4E-45 double:-4.9E-324;
        r10 = "";
        r11 = 0;
        r12 = 0;
        r13 = r27.getAttributeCount();
        r14 = 0;
        r14 = r0.parseStyleAttributes(r1, r14);
        r15 = 0;
    L_0x0025:
        if (r15 >= r13) goto L_0x00d4;
    L_0x0027:
        r24 = r13;
        r13 = r1.getAttributeName(r15);
        r25 = r11;
        r11 = r1.getAttributeValue(r15);
        r16 = -1;
        r17 = r13.hashCode();
        switch(r17) {
            case -934795532: goto L_0x0075;
            case 99841: goto L_0x006a;
            case 100571: goto L_0x005f;
            case 93616297: goto L_0x0054;
            case 109780401: goto L_0x0048;
            case 1292595405: goto L_0x003d;
            default: goto L_0x003c;
        };
    L_0x003c:
        goto L_0x0080;
    L_0x003d:
        r1 = "backgroundImage";
        r1 = r13.equals(r1);
        if (r1 == 0) goto L_0x003c;
    L_0x0045:
        r16 = 5;
        goto L_0x0080;
    L_0x0048:
        r1 = "style";
        r1 = r13.equals(r1);
        if (r1 == 0) goto L_0x003c;
    L_0x0051:
        r16 = 3;
        goto L_0x0080;
    L_0x0054:
        r1 = "begin";
        r1 = r13.equals(r1);
        if (r1 == 0) goto L_0x003c;
    L_0x005c:
        r16 = 0;
        goto L_0x0080;
    L_0x005f:
        r1 = "end";
        r1 = r13.equals(r1);
        if (r1 == 0) goto L_0x003c;
    L_0x0067:
        r16 = 1;
        goto L_0x0080;
    L_0x006a:
        r1 = "dur";
        r1 = r13.equals(r1);
        if (r1 == 0) goto L_0x003c;
    L_0x0072:
        r16 = 2;
        goto L_0x0080;
    L_0x0075:
        r1 = "region";
        r1 = r13.equals(r1);
        if (r1 == 0) goto L_0x003c;
    L_0x007e:
        r16 = 4;
    L_0x0080:
        switch(r16) {
            case 0: goto L_0x00c0;
            case 1: goto L_0x00b8;
            case 2: goto L_0x00b0;
            case 3: goto L_0x00a1;
            case 4: goto L_0x0094;
            case 5: goto L_0x0084;
            default: goto L_0x0083;
        };
    L_0x0083:
        goto L_0x00c8;
    L_0x0084:
        r1 = "#";
        r1 = r11.startsWith(r1);
        if (r1 == 0) goto L_0x0093;
    L_0x008c:
        r1 = 1;
        r1 = r11.substring(r1);
        r11 = r1;
        goto L_0x00ca;
    L_0x0093:
        goto L_0x00c8;
    L_0x0094:
        r1 = r29;
        r16 = r1.containsKey(r11);
        if (r16 == 0) goto L_0x00a0;
    L_0x009c:
        r10 = r11;
        r11 = r25;
        goto L_0x00ca;
    L_0x00a0:
        goto L_0x00c8;
    L_0x00a1:
        r1 = r29;
        r1 = r0.parseStyleIds(r11);
        r0 = r1.length;
        if (r0 <= 0) goto L_0x00af;
    L_0x00aa:
        r0 = r1;
        r12 = r0;
        r11 = r25;
        goto L_0x00ca;
    L_0x00af:
        goto L_0x00c8;
    L_0x00b0:
        r0 = parseTimeExpression(r11, r3);
        r4 = r0;
        r11 = r25;
        goto L_0x00ca;
    L_0x00b8:
        r0 = parseTimeExpression(r11, r3);
        r8 = r0;
        r11 = r25;
        goto L_0x00ca;
    L_0x00c0:
        r0 = parseTimeExpression(r11, r3);
        r6 = r0;
        r11 = r25;
        goto L_0x00ca;
    L_0x00c8:
        r11 = r25;
    L_0x00ca:
        r15 = r15 + 1;
        r13 = r24;
        r0 = r26;
        r1 = r27;
        goto L_0x0025;
    L_0x00d4:
        r25 = r11;
        r24 = r13;
        r0 = -9223372036854775807; // 0x8000000000000001 float:1.4E-45 double:-4.9E-324;
        if (r2 == 0) goto L_0x00f8;
    L_0x00df:
        r13 = r10;
        r10 = r2.startTimeUs;
        r15 = (r10 > r0 ? 1 : (r10 == r0 ? 0 : -1));
        if (r15 == 0) goto L_0x00f9;
    L_0x00e6:
        r10 = (r6 > r0 ? 1 : (r6 == r0 ? 0 : -1));
        if (r10 == 0) goto L_0x00ee;
    L_0x00ea:
        r10 = r2.startTimeUs;
        r6 = r6 + r10;
        goto L_0x00ef;
    L_0x00ef:
        r10 = (r8 > r0 ? 1 : (r8 == r0 ? 0 : -1));
        if (r10 == 0) goto L_0x00f7;
    L_0x00f3:
        r10 = r2.startTimeUs;
        r8 = r8 + r10;
        goto L_0x00f9;
    L_0x00f7:
        goto L_0x00f9;
    L_0x00f8:
        r13 = r10;
    L_0x00f9:
        r10 = (r8 > r0 ? 1 : (r8 == r0 ? 0 : -1));
        if (r10 != 0) goto L_0x0110;
    L_0x00fd:
        r10 = (r4 > r0 ? 1 : (r4 == r0 ? 0 : -1));
        if (r10 == 0) goto L_0x0104;
    L_0x0101:
        r0 = r6 + r4;
        goto L_0x0112;
    L_0x0104:
        if (r2 == 0) goto L_0x010f;
    L_0x0106:
        r10 = r2.endTimeUs;
        r15 = (r10 > r0 ? 1 : (r10 == r0 ? 0 : -1));
        if (r15 == 0) goto L_0x010f;
    L_0x010c:
        r0 = r2.endTimeUs;
        goto L_0x0112;
    L_0x010f:
        goto L_0x0111;
    L_0x0111:
        r0 = r8;
        r15 = r27.getName();
        r16 = r6;
        r18 = r0;
        r20 = r14;
        r21 = r12;
        r22 = r13;
        r23 = r25;
        r8 = com.google.android.exoplayer2.text.ttml.TtmlNode.buildNode(r15, r16, r18, r20, r21, r22, r23);
        return r8;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.text.ttml.TtmlDecoder.parseNode(org.xmlpull.v1.XmlPullParser, com.google.android.exoplayer2.text.ttml.TtmlNode, java.util.Map, com.google.android.exoplayer2.text.ttml.TtmlDecoder$FrameAndTickRate):com.google.android.exoplayer2.text.ttml.TtmlNode");
    }

    private static boolean isSupportedTag(String tag) {
        if (!tag.equals(TtmlNode.TAG_TT)) {
            if (!tag.equals("head")) {
                if (!tag.equals("body")) {
                    if (!tag.equals(TtmlNode.TAG_DIV)) {
                        if (!tag.equals(TtmlNode.TAG_P)) {
                            if (!tag.equals(TtmlNode.TAG_SPAN)) {
                                if (!tag.equals(TtmlNode.TAG_BR)) {
                                    if (!tag.equals("style")) {
                                        if (!tag.equals(TtmlNode.TAG_STYLING)) {
                                            if (!tag.equals(TtmlNode.TAG_LAYOUT)) {
                                                if (!tag.equals("region")) {
                                                    if (!tag.equals(TtmlNode.TAG_METADATA)) {
                                                        if (!tag.equals("image")) {
                                                            if (!tag.equals("data")) {
                                                                if (!tag.equals(TtmlNode.TAG_INFORMATION)) {
                                                                    return false;
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
}
