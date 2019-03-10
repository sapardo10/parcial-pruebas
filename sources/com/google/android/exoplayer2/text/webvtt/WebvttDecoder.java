package com.google.android.exoplayer2.text.webvtt;

import android.text.TextUtils;
import com.google.android.exoplayer2.text.SimpleSubtitleDecoder;
import com.google.android.exoplayer2.text.webvtt.WebvttCue.Builder;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.util.ArrayList;
import java.util.List;

public final class WebvttDecoder extends SimpleSubtitleDecoder {
    private static final String COMMENT_START = "NOTE";
    private static final int EVENT_COMMENT = 1;
    private static final int EVENT_CUE = 3;
    private static final int EVENT_END_OF_FILE = 0;
    private static final int EVENT_NONE = -1;
    private static final int EVENT_STYLE_BLOCK = 2;
    private static final String STYLE_START = "STYLE";
    private final CssParser cssParser = new CssParser();
    private final WebvttCueParser cueParser = new WebvttCueParser();
    private final List<WebvttCssStyle> definedStyles = new ArrayList();
    private final ParsableByteArray parsableWebvttData = new ParsableByteArray();
    private final Builder webvttCueBuilder = new Builder();

    protected com.google.android.exoplayer2.text.webvtt.WebvttSubtitle decode(byte[] r7, int r8, boolean r9) throws com.google.android.exoplayer2.text.SubtitleDecoderException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:35:0x008f in {6, 12, 19, 20, 22, 27, 28, 29, 31, 34} preds:[]
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
        r0 = r6.parsableWebvttData;
        r0.reset(r7, r8);
        r0 = r6.webvttCueBuilder;
        r0.reset();
        r0 = r6.definedStyles;
        r0.clear();
        r0 = r6.parsableWebvttData;	 Catch:{ ParserException -> 0x0088 }
        com.google.android.exoplayer2.text.webvtt.WebvttParserUtil.validateWebvttHeaderLine(r0);	 Catch:{ ParserException -> 0x0088 }
    L_0x0015:
        r0 = r6.parsableWebvttData;
        r0 = r0.readLine();
        r0 = android.text.TextUtils.isEmpty(r0);
        if (r0 != 0) goto L_0x0022;
    L_0x0021:
        goto L_0x0015;
    L_0x0022:
        r0 = new java.util.ArrayList;
        r0.<init>();
    L_0x0027:
        r1 = r6.parsableWebvttData;
        r1 = getNextEvent(r1);
        r2 = r1;
        if (r1 == 0) goto L_0x0082;
    L_0x0030:
        r1 = 1;
        if (r2 != r1) goto L_0x0039;
    L_0x0033:
        r1 = r6.parsableWebvttData;
        skipComment(r1);
        goto L_0x0027;
    L_0x0039:
        r1 = 2;
        if (r2 != r1) goto L_0x0060;
    L_0x003c:
        r1 = r0.isEmpty();
        if (r1 == 0) goto L_0x0058;
    L_0x0042:
        r1 = r6.parsableWebvttData;
        r1.readLine();
        r1 = r6.cssParser;
        r3 = r6.parsableWebvttData;
        r1 = r1.parseBlock(r3);
        if (r1 == 0) goto L_0x0057;
    L_0x0051:
        r3 = r6.definedStyles;
        r3.add(r1);
        goto L_0x0081;
    L_0x0057:
        goto L_0x0081;
    L_0x0058:
        r1 = new com.google.android.exoplayer2.text.SubtitleDecoderException;
        r3 = "A style block was found after the first cue.";
        r1.<init>(r3);
        throw r1;
    L_0x0060:
        r1 = 3;
        if (r2 != r1) goto L_0x0081;
    L_0x0063:
        r1 = r6.cueParser;
        r3 = r6.parsableWebvttData;
        r4 = r6.webvttCueBuilder;
        r5 = r6.definedStyles;
        r1 = r1.parseCue(r3, r4, r5);
        if (r1 == 0) goto L_0x0080;
    L_0x0071:
        r1 = r6.webvttCueBuilder;
        r1 = r1.build();
        r0.add(r1);
        r1 = r6.webvttCueBuilder;
        r1.reset();
        goto L_0x0027;
    L_0x0080:
        goto L_0x0027;
    L_0x0081:
        goto L_0x0027;
    L_0x0082:
        r1 = new com.google.android.exoplayer2.text.webvtt.WebvttSubtitle;
        r1.<init>(r0);
        return r1;
    L_0x0088:
        r0 = move-exception;
        r1 = new com.google.android.exoplayer2.text.SubtitleDecoderException;
        r1.<init>(r0);
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.text.webvtt.WebvttDecoder.decode(byte[], int, boolean):com.google.android.exoplayer2.text.webvtt.WebvttSubtitle");
    }

    public WebvttDecoder() {
        super("WebvttDecoder");
    }

    private static int getNextEvent(ParsableByteArray parsableWebvttData) {
        int foundEvent = -1;
        int currentInputPosition = 0;
        while (foundEvent == -1) {
            currentInputPosition = parsableWebvttData.getPosition();
            String line = parsableWebvttData.readLine();
            if (line == null) {
                foundEvent = 0;
            } else if (STYLE_START.equals(line)) {
                foundEvent = 2;
            } else if (line.startsWith(COMMENT_START)) {
                foundEvent = 1;
            } else {
                foundEvent = 3;
            }
        }
        parsableWebvttData.setPosition(currentInputPosition);
        return foundEvent;
    }

    private static void skipComment(ParsableByteArray parsableWebvttData) {
        while (!TextUtils.isEmpty(parsableWebvttData.readLine())) {
        }
    }
}
