package com.google.android.exoplayer2.text.pgs;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.SimpleSubtitleDecoder;
import com.google.android.exoplayer2.text.Subtitle;
import com.google.android.exoplayer2.text.SubtitleDecoderException;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.zip.Inflater;

public final class PgsDecoder extends SimpleSubtitleDecoder {
    private static final byte INFLATE_HEADER = (byte) 120;
    private static final int SECTION_TYPE_BITMAP_PICTURE = 21;
    private static final int SECTION_TYPE_END = 128;
    private static final int SECTION_TYPE_IDENTIFIER = 22;
    private static final int SECTION_TYPE_PALETTE = 20;
    private final ParsableByteArray buffer = new ParsableByteArray();
    private final CueBuilder cueBuilder = new CueBuilder();
    private final ParsableByteArray inflatedBuffer = new ParsableByteArray();
    private Inflater inflater;

    private static final class CueBuilder {
        private final ParsableByteArray bitmapData = new ParsableByteArray();
        private int bitmapHeight;
        private int bitmapWidth;
        private int bitmapX;
        private int bitmapY;
        private final int[] colors = new int[256];
        private boolean colorsSet;
        private int planeHeight;
        private int planeWidth;

        private void parsePaletteSection(ParsableByteArray buffer, int sectionLength) {
            CueBuilder cueBuilder = this;
            if (sectionLength % 5 == 2) {
                int i;
                buffer.skipBytes(2);
                Arrays.fill(cueBuilder.colors, 0);
                int entryCount = sectionLength / 5;
                int i2 = 0;
                while (i2 < entryCount) {
                    int index = buffer.readUnsignedByte();
                    int y = buffer.readUnsignedByte();
                    int cr = buffer.readUnsignedByte();
                    int cb = buffer.readUnsignedByte();
                    int a = buffer.readUnsignedByte();
                    double d = (double) y;
                    double d2 = (double) (cr - 128);
                    Double.isNaN(d2);
                    d2 *= 1.402d;
                    Double.isNaN(d);
                    int r = (int) (d + d2);
                    double d3 = (double) y;
                    i = i2;
                    double d4 = (double) (cb - 128);
                    Double.isNaN(d4);
                    d4 *= 0.34414d;
                    Double.isNaN(d3);
                    d3 -= d4;
                    double d5 = (double) (cr - 128);
                    Double.isNaN(d5);
                    int g = (int) (d3 - (d5 * 0.71414d));
                    d3 = (double) y;
                    int entryCount2 = entryCount;
                    double d6 = (double) (cb - 128);
                    Double.isNaN(d6);
                    d6 *= 1.772d;
                    Double.isNaN(d3);
                    cueBuilder.colors[index] = (((a << 24) | (Util.constrainValue(r, 0, 255) << 16)) | (Util.constrainValue(g, 0, 255) << 8)) | Util.constrainValue((int) (d3 + d6), 0, 255);
                    i2 = i + 1;
                    entryCount = entryCount2;
                    ParsableByteArray parsableByteArray = buffer;
                }
                i = i2;
                cueBuilder.colorsSet = true;
            }
        }

        private void parseBitmapSection(ParsableByteArray buffer, int sectionLength) {
            if (sectionLength >= 4) {
                int totalLength;
                buffer.skipBytes(3);
                sectionLength -= 4;
                if ((buffer.readUnsignedByte() & 128) != 0) {
                    if (sectionLength >= 7) {
                        totalLength = buffer.readUnsignedInt24();
                        if (totalLength >= 4) {
                            this.bitmapWidth = buffer.readUnsignedShort();
                            this.bitmapHeight = buffer.readUnsignedShort();
                            this.bitmapData.reset(totalLength - 4);
                            sectionLength -= 7;
                        } else {
                            return;
                        }
                    }
                    return;
                }
                int position = this.bitmapData.getPosition();
                totalLength = this.bitmapData.limit();
                if (position < totalLength && sectionLength > 0) {
                    int bytesToRead = Math.min(sectionLength, totalLength - position);
                    buffer.readBytes(this.bitmapData.data, position, bytesToRead);
                    this.bitmapData.setPosition(position + bytesToRead);
                }
            }
        }

        private void parseIdentifierSection(ParsableByteArray buffer, int sectionLength) {
            if (sectionLength >= 19) {
                this.planeWidth = buffer.readUnsignedShort();
                this.planeHeight = buffer.readUnsignedShort();
                buffer.skipBytes(11);
                this.bitmapX = buffer.readUnsignedShort();
                this.bitmapY = buffer.readUnsignedShort();
            }
        }

        public Cue build() {
            if (this.planeWidth != 0 && this.planeHeight != 0 && this.bitmapWidth != 0 && this.bitmapHeight != 0) {
                if (this.bitmapData.limit() != 0) {
                    if (this.bitmapData.getPosition() == this.bitmapData.limit()) {
                        if (this.colorsSet) {
                            int runLength;
                            this.bitmapData.setPosition(0);
                            int[] argbBitmapData = new int[(this.bitmapWidth * this.bitmapHeight)];
                            int argbBitmapDataIndex = 0;
                            while (argbBitmapDataIndex < argbBitmapData.length) {
                                int colorIndex = this.bitmapData.readUnsignedByte();
                                int argbBitmapDataIndex2;
                                if (colorIndex != 0) {
                                    argbBitmapDataIndex2 = argbBitmapDataIndex + 1;
                                    argbBitmapData[argbBitmapDataIndex] = this.colors[colorIndex];
                                    argbBitmapDataIndex = argbBitmapDataIndex2;
                                } else {
                                    argbBitmapDataIndex2 = this.bitmapData.readUnsignedByte();
                                    if (argbBitmapDataIndex2 != 0) {
                                        if ((argbBitmapDataIndex2 & 64) == 0) {
                                            runLength = argbBitmapDataIndex2 & 63;
                                        } else {
                                            runLength = ((argbBitmapDataIndex2 & 63) << 8) | this.bitmapData.readUnsignedByte();
                                        }
                                        Arrays.fill(argbBitmapData, argbBitmapDataIndex, argbBitmapDataIndex + runLength, (argbBitmapDataIndex2 & 128) == 0 ? 0 : this.colors[this.bitmapData.readUnsignedByte()]);
                                        argbBitmapDataIndex += runLength;
                                    }
                                }
                            }
                            Bitmap bitmap = Bitmap.createBitmap(argbBitmapData, this.bitmapWidth, this.bitmapHeight, Config.ARGB_8888);
                            float f = (float) this.bitmapX;
                            runLength = this.planeWidth;
                            float f2 = f / ((float) runLength);
                            f = (float) this.bitmapY;
                            int i = this.planeHeight;
                            return new Cue(bitmap, f2, 0, f / ((float) i), 0, ((float) this.bitmapWidth) / ((float) runLength), ((float) this.bitmapHeight) / ((float) i));
                        }
                    }
                }
            }
            return null;
        }

        public void reset() {
            this.planeWidth = 0;
            this.planeHeight = 0;
            this.bitmapX = 0;
            this.bitmapY = 0;
            this.bitmapWidth = 0;
            this.bitmapHeight = 0;
            this.bitmapData.reset(0);
            this.colorsSet = false;
        }
    }

    public PgsDecoder() {
        super("PgsDecoder");
    }

    protected Subtitle decode(byte[] data, int size, boolean reset) throws SubtitleDecoderException {
        this.buffer.reset(data, size);
        maybeInflateData(this.buffer);
        this.cueBuilder.reset();
        ArrayList<Cue> cues = new ArrayList();
        while (this.buffer.bytesLeft() >= 3) {
            Cue cue = readNextSection(this.buffer, this.cueBuilder);
            if (cue != null) {
                cues.add(cue);
            }
        }
        return new PgsSubtitle(Collections.unmodifiableList(cues));
    }

    private void maybeInflateData(ParsableByteArray buffer) {
        if (buffer.bytesLeft() > 0 && buffer.peekUnsignedByte() == 120) {
            if (this.inflater == null) {
                this.inflater = new Inflater();
            }
            if (Util.inflate(buffer, this.inflatedBuffer, this.inflater)) {
                buffer.reset(this.inflatedBuffer.data, this.inflatedBuffer.limit());
            }
        }
    }

    private static Cue readNextSection(ParsableByteArray buffer, CueBuilder cueBuilder) {
        int limit = buffer.limit();
        int sectionType = buffer.readUnsignedByte();
        int sectionLength = buffer.readUnsignedShort();
        int nextSectionPosition = buffer.getPosition() + sectionLength;
        if (nextSectionPosition > limit) {
            buffer.setPosition(limit);
            return null;
        }
        Cue cue = null;
        if (sectionType != 128) {
            switch (sectionType) {
                case 20:
                    cueBuilder.parsePaletteSection(buffer, sectionLength);
                    break;
                case 21:
                    cueBuilder.parseBitmapSection(buffer, sectionLength);
                    break;
                case 22:
                    cueBuilder.parseIdentifierSection(buffer, sectionLength);
                    break;
                default:
                    break;
            }
        }
        cue = cueBuilder.build();
        cueBuilder.reset();
        buffer.setPosition(nextSectionPosition);
        return cue;
    }
}
