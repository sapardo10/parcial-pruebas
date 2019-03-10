package com.google.android.exoplayer2.text.tx3g;

import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.text.style.UnderlineSpan;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.SimpleSubtitleDecoder;
import com.google.android.exoplayer2.text.Subtitle;
import com.google.android.exoplayer2.text.SubtitleDecoderException;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.nio.charset.Charset;
import java.util.List;

public final class Tx3gDecoder extends SimpleSubtitleDecoder {
    private static final char BOM_UTF16_BE = '﻿';
    private static final char BOM_UTF16_LE = '￾';
    private static final int DEFAULT_COLOR = -1;
    private static final int DEFAULT_FONT_FACE = 0;
    private static final String DEFAULT_FONT_FAMILY = "sans-serif";
    private static final float DEFAULT_VERTICAL_PLACEMENT = 0.85f;
    private static final int FONT_FACE_BOLD = 1;
    private static final int FONT_FACE_ITALIC = 2;
    private static final int FONT_FACE_UNDERLINE = 4;
    private static final int SIZE_ATOM_HEADER = 8;
    private static final int SIZE_BOM_UTF16 = 2;
    private static final int SIZE_SHORT = 2;
    private static final int SIZE_STYLE_RECORD = 12;
    private static final int SPAN_PRIORITY_HIGH = 0;
    private static final int SPAN_PRIORITY_LOW = 16711680;
    private static final String TX3G_SERIF = "Serif";
    private static final int TYPE_STYL = Util.getIntegerCodeForString("styl");
    private static final int TYPE_TBOX = Util.getIntegerCodeForString("tbox");
    private int calculatedVideoTrackHeight;
    private boolean customVerticalPlacement;
    private int defaultColorRgba;
    private int defaultFontFace;
    private String defaultFontFamily;
    private float defaultVerticalPlacement;
    private final ParsableByteArray parsableByteArray = new ParsableByteArray();

    public Tx3gDecoder(List<byte[]> initializationData) {
        super("Tx3gDecoder");
        decodeInitializationData(initializationData);
    }

    private void decodeInitializationData(List<byte[]> initializationData) {
        boolean z = false;
        if (initializationData != null && initializationData.size() == 1) {
            if (((byte[]) initializationData.get(0)).length != 48) {
                if (((byte[]) initializationData.get(0)).length == 53) {
                }
            }
            byte[] initializationBytes = (byte[]) initializationData.get(0);
            this.defaultFontFace = initializationBytes[24];
            this.defaultColorRgba = ((((initializationBytes[26] & 255) << 24) | ((initializationBytes[27] & 255) << 16)) | ((initializationBytes[28] & 255) << 8)) | (initializationBytes[29] & 255);
            this.defaultFontFamily = TX3G_SERIF.equals(Util.fromUtf8Bytes(initializationBytes, 43, initializationBytes.length - 43)) ? C0555C.SERIF_NAME : "sans-serif";
            this.calculatedVideoTrackHeight = initializationBytes[25] * 20;
            if ((initializationBytes[0] & 32) != 0) {
                z = true;
            }
            this.customVerticalPlacement = z;
            if (this.customVerticalPlacement) {
                this.defaultVerticalPlacement = ((float) (((initializationBytes[10] & 255) << 8) | (initializationBytes[11] & 255))) / ((float) this.calculatedVideoTrackHeight);
                this.defaultVerticalPlacement = Util.constrainValue(this.defaultVerticalPlacement, 0.0f, 0.95f);
            } else {
                this.defaultVerticalPlacement = DEFAULT_VERTICAL_PLACEMENT;
            }
            return;
        }
        this.defaultFontFace = 0;
        this.defaultColorRgba = -1;
        this.defaultFontFamily = "sans-serif";
        this.customVerticalPlacement = false;
        this.defaultVerticalPlacement = DEFAULT_VERTICAL_PLACEMENT;
    }

    protected Subtitle decode(byte[] bytes, int length, boolean reset) throws SubtitleDecoderException {
        this.parsableByteArray.reset(bytes, length);
        String cueTextString = readSubtitleText(this.parsableByteArray);
        if (cueTextString.isEmpty()) {
            return Tx3gSubtitle.EMPTY;
        }
        SpannableStringBuilder cueText = new SpannableStringBuilder(cueTextString);
        SpannableStringBuilder spannableStringBuilder = cueText;
        attachFontFace(spannableStringBuilder, r0.defaultFontFace, 0, 0, cueText.length(), SPAN_PRIORITY_LOW);
        attachColor(spannableStringBuilder, r0.defaultColorRgba, -1, 0, cueText.length(), SPAN_PRIORITY_LOW);
        attachFontFamily(spannableStringBuilder, r0.defaultFontFamily, "sans-serif", 0, cueText.length(), SPAN_PRIORITY_LOW);
        float verticalPlacement = r0.defaultVerticalPlacement;
        while (r0.parsableByteArray.bytesLeft() >= 8) {
            int position = r0.parsableByteArray.getPosition();
            int atomSize = r0.parsableByteArray.readInt();
            int atomType = r0.parsableByteArray.readInt();
            boolean z = false;
            if (atomType == TYPE_STYL) {
                if (r0.parsableByteArray.bytesLeft() >= 2) {
                    z = true;
                }
                assertTrue(z);
                int styleRecordCount = r0.parsableByteArray.readUnsignedShort();
                for (int i = 0; i < styleRecordCount; i++) {
                    applyStyleRecord(r0.parsableByteArray, cueText);
                }
            } else if (atomType == TYPE_TBOX && r0.customVerticalPlacement) {
                if (r0.parsableByteArray.bytesLeft() >= 2) {
                    z = true;
                }
                assertTrue(z);
                verticalPlacement = Util.constrainValue(((float) r0.parsableByteArray.readUnsignedShort()) / ((float) r0.calculatedVideoTrackHeight), 0.0f, 0.95f);
                r0.parsableByteArray.setPosition(position + atomSize);
            }
            r0.parsableByteArray.setPosition(position + atomSize);
        }
        Cue cue = r5;
        Cue cue2 = new Cue(cueText, null, verticalPlacement, 0, 0, Float.MIN_VALUE, Integer.MIN_VALUE, Float.MIN_VALUE);
        return new Tx3gSubtitle(cue);
    }

    private static String readSubtitleText(ParsableByteArray parsableByteArray) throws SubtitleDecoderException {
        assertTrue(parsableByteArray.bytesLeft() >= 2);
        int textLength = parsableByteArray.readUnsignedShort();
        if (textLength == 0) {
            return "";
        }
        if (parsableByteArray.bytesLeft() >= 2) {
            char firstChar = parsableByteArray.peekChar();
            if (firstChar != '﻿') {
                if (firstChar == BOM_UTF16_LE) {
                }
            }
            return parsableByteArray.readString(textLength, Charset.forName("UTF-16"));
        }
        return parsableByteArray.readString(textLength, Charset.forName("UTF-8"));
    }

    private void applyStyleRecord(ParsableByteArray parsableByteArray, SpannableStringBuilder cueText) throws SubtitleDecoderException {
        assertTrue(parsableByteArray.bytesLeft() >= 12);
        int start = parsableByteArray.readUnsignedShort();
        int end = parsableByteArray.readUnsignedShort();
        parsableByteArray.skipBytes(2);
        int fontFace = parsableByteArray.readUnsignedByte();
        parsableByteArray.skipBytes(1);
        int colorRgba = parsableByteArray.readInt();
        SpannableStringBuilder spannableStringBuilder = cueText;
        int i = start;
        int i2 = end;
        attachFontFace(spannableStringBuilder, fontFace, this.defaultFontFace, i, i2, 0);
        attachColor(spannableStringBuilder, colorRgba, this.defaultColorRgba, i, i2, 0);
    }

    private static void attachFontFace(SpannableStringBuilder cueText, int fontFace, int defaultFontFace, int start, int end, int spanPriority) {
        if (fontFace != defaultFontFace) {
            int flags = spanPriority | 33;
            boolean isUnderlined = true;
            boolean isBold = (fontFace & 1) != 0;
            boolean isItalic = (fontFace & 2) != 0;
            if (isBold) {
                if (isItalic) {
                    cueText.setSpan(new StyleSpan(3), start, end, flags);
                } else {
                    cueText.setSpan(new StyleSpan(1), start, end, flags);
                }
            } else if (isItalic) {
                cueText.setSpan(new StyleSpan(2), start, end, flags);
            }
            if ((fontFace & 4) == 0) {
                isUnderlined = false;
            }
            if (isUnderlined) {
                cueText.setSpan(new UnderlineSpan(), start, end, flags);
            }
            if (!isUnderlined && !isBold && !isItalic) {
                cueText.setSpan(new StyleSpan(0), start, end, flags);
            }
        }
    }

    private static void attachColor(SpannableStringBuilder cueText, int colorRgba, int defaultColorRgba, int start, int end, int spanPriority) {
        if (colorRgba != defaultColorRgba) {
            cueText.setSpan(new ForegroundColorSpan(((colorRgba & 255) << 24) | (colorRgba >>> 8)), start, end, spanPriority | 33);
        }
    }

    private static void attachFontFamily(SpannableStringBuilder cueText, String fontFamily, String defaultFontFamily, int start, int end, int spanPriority) {
        if (fontFamily != defaultFontFamily) {
            cueText.setSpan(new TypefaceSpan(fontFamily), start, end, spanPriority | 33);
        }
    }

    private static void assertTrue(boolean checkValue) throws SubtitleDecoderException {
        if (!checkValue) {
            throw new SubtitleDecoderException("Unexpected subtitle format.");
        }
    }
}
