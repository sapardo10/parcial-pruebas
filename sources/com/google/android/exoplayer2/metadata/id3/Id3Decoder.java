package com.google.android.exoplayer2.metadata.id3;

import android.support.annotation.Nullable;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.MetadataDecoder;
import com.google.android.exoplayer2.metadata.MetadataInputBuffer;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.ParsableBitArray;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.apache.commons.lang3.CharEncoding;

public final class Id3Decoder implements MetadataDecoder {
    private static final int FRAME_FLAG_V3_HAS_GROUP_IDENTIFIER = 32;
    private static final int FRAME_FLAG_V3_IS_COMPRESSED = 128;
    private static final int FRAME_FLAG_V3_IS_ENCRYPTED = 64;
    private static final int FRAME_FLAG_V4_HAS_DATA_LENGTH = 1;
    private static final int FRAME_FLAG_V4_HAS_GROUP_IDENTIFIER = 64;
    private static final int FRAME_FLAG_V4_IS_COMPRESSED = 8;
    private static final int FRAME_FLAG_V4_IS_ENCRYPTED = 4;
    private static final int FRAME_FLAG_V4_IS_UNSYNCHRONIZED = 2;
    public static final int ID3_HEADER_LENGTH = 10;
    public static final int ID3_TAG = Util.getIntegerCodeForString("ID3");
    private static final int ID3_TEXT_ENCODING_ISO_8859_1 = 0;
    private static final int ID3_TEXT_ENCODING_UTF_16 = 1;
    private static final int ID3_TEXT_ENCODING_UTF_16BE = 2;
    private static final int ID3_TEXT_ENCODING_UTF_8 = 3;
    public static final FramePredicate NO_FRAMES_PREDICATE = -$$Lambda$Id3Decoder$7M0gB-IGKaTbyTVX-WCb62bIHyc.INSTANCE;
    private static final String TAG = "Id3Decoder";
    @Nullable
    private final FramePredicate framePredicate;

    public interface FramePredicate {
        boolean evaluate(int i, int i2, int i3, int i4, int i5);
    }

    private static final class Id3Header {
        private final int framesSize;
        private final boolean isUnsynchronized;
        private final int majorVersion;

        public Id3Header(int majorVersion, boolean isUnsynchronized, int framesSize) {
            this.majorVersion = majorVersion;
            this.isUnsynchronized = isUnsynchronized;
            this.framesSize = framesSize;
        }
    }

    private static boolean validateFrames(com.google.android.exoplayer2.util.ParsableByteArray r19, int r20, int r21, boolean r22) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:78:0x00d8 in {8, 10, 17, 24, 25, 26, 31, 32, 35, 36, 37, 41, 42, 45, 46, 47, 48, 51, 52, 54, 55, 59, 65, 69, 71, 73, 75, 77} preds:[]
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
        r1 = r19;
        r2 = r20;
        r3 = r19.getPosition();
    L_0x0008:
        r0 = r19.bytesLeft();	 Catch:{ all -> 0x00d1 }
        r4 = 1;
        r5 = r21;
        if (r0 < r5) goto L_0x00cc;
    L_0x0011:
        r0 = 3;
        if (r2 < r0) goto L_0x0021;
    L_0x0014:
        r6 = r19.readInt();	 Catch:{ all -> 0x00ca }
        r7 = r19.readUnsignedInt();	 Catch:{ all -> 0x00ca }
        r9 = r19.readUnsignedShort();	 Catch:{ all -> 0x00ca }
        goto L_0x002b;	 Catch:{ all -> 0x00ca }
    L_0x0021:
        r6 = r19.readUnsignedInt24();	 Catch:{ all -> 0x00ca }
        r7 = r19.readUnsignedInt24();	 Catch:{ all -> 0x00ca }
        r7 = (long) r7;
        r9 = 0;
    L_0x002b:
        r10 = 0;
        if (r6 != 0) goto L_0x003a;
    L_0x002f:
        r12 = (r7 > r10 ? 1 : (r7 == r10 ? 0 : -1));
        if (r12 != 0) goto L_0x003a;
    L_0x0033:
        if (r9 != 0) goto L_0x003a;
        r1.setPosition(r3);
        return r4;
        r12 = 4;
        r13 = 0;
        if (r2 != r12) goto L_0x0077;
    L_0x003f:
        if (r22 != 0) goto L_0x0077;
    L_0x0041:
        r14 = 8421504; // 0x808080 float:1.180104E-38 double:4.160776E-317;
        r14 = r14 & r7;
        r16 = (r14 > r10 ? 1 : (r14 == r10 ? 0 : -1));
        if (r16 == 0) goto L_0x004e;
        r1.setPosition(r3);
        return r13;
    L_0x004e:
        r10 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        r14 = r7 & r10;
        r16 = 8;
        r16 = r7 >> r16;
        r16 = r16 & r10;
        r18 = 7;
        r16 = r16 << r18;
        r14 = r14 | r16;
        r16 = 16;
        r16 = r7 >> r16;
        r16 = r16 & r10;
        r18 = 14;
        r16 = r16 << r18;
        r14 = r14 | r16;
        r16 = 24;
        r16 = r7 >> r16;
        r10 = r16 & r10;
        r16 = 21;
        r10 = r10 << r16;
        r7 = r14 | r10;
        goto L_0x0078;
    L_0x0078:
        r10 = 0;
        r11 = 0;
        if (r2 != r12) goto L_0x008c;
    L_0x007c:
        r0 = r9 & 64;
        if (r0 == 0) goto L_0x0082;
    L_0x0080:
        r0 = 1;
        goto L_0x0083;
    L_0x0082:
        r0 = 0;
    L_0x0083:
        r10 = r0;
        r0 = r9 & 1;
        if (r0 == 0) goto L_0x0089;
    L_0x0088:
        goto L_0x008a;
    L_0x0089:
        r4 = 0;
    L_0x008a:
        r11 = r4;
        goto L_0x009f;
    L_0x008c:
        if (r2 != r0) goto L_0x009e;
    L_0x008e:
        r0 = r9 & 32;
        if (r0 == 0) goto L_0x0094;
    L_0x0092:
        r0 = 1;
        goto L_0x0095;
    L_0x0094:
        r0 = 0;
    L_0x0095:
        r10 = r0;
        r0 = r9 & 128;
        if (r0 == 0) goto L_0x009b;
    L_0x009a:
        goto L_0x009c;
    L_0x009b:
        r4 = 0;
    L_0x009c:
        r11 = r4;
        goto L_0x009f;
    L_0x009f:
        r0 = 0;
        if (r10 == 0) goto L_0x00a5;
    L_0x00a2:
        r0 = r0 + 1;
        goto L_0x00a6;
    L_0x00a6:
        if (r11 == 0) goto L_0x00ab;
    L_0x00a8:
        r0 = r0 + 4;
        goto L_0x00ac;
    L_0x00ac:
        r14 = (long) r0;
        r4 = (r7 > r14 ? 1 : (r7 == r14 ? 0 : -1));
        if (r4 >= 0) goto L_0x00b6;
        r1.setPosition(r3);
        return r13;
    L_0x00b6:
        r4 = r19.bytesLeft();	 Catch:{ all -> 0x00ca }
        r14 = (long) r4;
        r4 = (r14 > r7 ? 1 : (r14 == r7 ? 0 : -1));
        if (r4 >= 0) goto L_0x00c4;
        r1.setPosition(r3);
        return r13;
    L_0x00c4:
        r4 = (int) r7;
        r1.skipBytes(r4);	 Catch:{ all -> 0x00ca }
        goto L_0x0008;
    L_0x00ca:
        r0 = move-exception;
        goto L_0x00d4;
        r1.setPosition(r3);
        return r4;
    L_0x00d1:
        r0 = move-exception;
        r5 = r21;
    L_0x00d4:
        r1.setPosition(r3);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.metadata.id3.Id3Decoder.validateFrames(com.google.android.exoplayer2.util.ParsableByteArray, int, int, boolean):boolean");
    }

    public Id3Decoder() {
        this(null);
    }

    public Id3Decoder(@Nullable FramePredicate framePredicate) {
        this.framePredicate = framePredicate;
    }

    @Nullable
    public Metadata decode(MetadataInputBuffer inputBuffer) {
        ByteBuffer buffer = inputBuffer.data;
        return decode(buffer.array(), buffer.limit());
    }

    @Nullable
    public Metadata decode(byte[] data, int size) {
        List id3Frames = new ArrayList();
        ParsableByteArray id3Data = new ParsableByteArray(data, size);
        Id3Header id3Header = decodeHeader(id3Data);
        if (id3Header == null) {
            return null;
        }
        int startPosition = id3Data.getPosition();
        int frameHeaderSize = id3Header.majorVersion == 2 ? 6 : 10;
        int framesSize = id3Header.framesSize;
        if (id3Header.isUnsynchronized) {
            framesSize = removeUnsynchronization(id3Data, id3Header.framesSize);
        }
        id3Data.setLimit(startPosition + framesSize);
        boolean unsignedIntFrameSizeHack = false;
        if (!validateFrames(id3Data, id3Header.majorVersion, frameHeaderSize, false)) {
            if (id3Header.majorVersion == 4 && validateFrames(id3Data, 4, frameHeaderSize, true)) {
                unsignedIntFrameSizeHack = true;
            } else {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Failed to validate ID3 tag with majorVersion=");
                stringBuilder.append(id3Header.majorVersion);
                Log.m10w(str, stringBuilder.toString());
                return null;
            }
        }
        while (id3Data.bytesLeft() >= frameHeaderSize) {
            Id3Frame frame = decodeFrame(id3Header.majorVersion, id3Data, unsignedIntFrameSizeHack, frameHeaderSize, this.framePredicate);
            if (frame != null) {
                id3Frames.add(frame);
            }
        }
        return new Metadata(id3Frames);
    }

    @Nullable
    private static Id3Header decodeHeader(ParsableByteArray data) {
        if (data.bytesLeft() < 10) {
            Log.m10w(TAG, "Data too short to be an ID3 tag");
            return null;
        }
        int id = data.readUnsignedInt24();
        if (id != ID3_TAG) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Unexpected first three bytes of ID3 tag header: ");
            stringBuilder.append(id);
            Log.m10w(str, stringBuilder.toString());
            return null;
        }
        int majorVersion = data.readUnsignedByte();
        boolean z = true;
        data.skipBytes(1);
        int flags = data.readUnsignedByte();
        int framesSize = data.readSynchSafeInt();
        if (majorVersion == 2) {
            if ((flags & 64) != 0) {
                Log.m10w(TAG, "Skipped ID3 tag with majorVersion=2 and undefined compression scheme");
                return null;
            }
        } else if (majorVersion == 3) {
            if ((flags & 64) != 0) {
                extendedHeaderSize = data.readInt();
                data.skipBytes(extendedHeaderSize);
                framesSize -= extendedHeaderSize + 4;
            }
        } else if (majorVersion == 4) {
            if ((flags & 64) != 0) {
                extendedHeaderSize = data.readSynchSafeInt();
                data.skipBytes(extendedHeaderSize - 4);
                framesSize -= extendedHeaderSize;
            }
            if ((flags & 16) != 0) {
                framesSize -= 10;
            }
        } else {
            String str2 = TAG;
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Skipped ID3 tag with unsupported majorVersion=");
            stringBuilder2.append(majorVersion);
            Log.m10w(str2, stringBuilder2.toString());
            return null;
        }
        if (majorVersion >= 4 || (flags & 128) == 0) {
            z = false;
        }
        return new Id3Header(majorVersion, z, framesSize);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @android.support.annotation.Nullable
    private static com.google.android.exoplayer2.metadata.id3.Id3Frame decodeFrame(int r22, com.google.android.exoplayer2.util.ParsableByteArray r23, boolean r24, int r25, @android.support.annotation.Nullable com.google.android.exoplayer2.metadata.id3.Id3Decoder.FramePredicate r26) {
        /*
        r7 = r22;
        r8 = r23;
        r9 = r23.readUnsignedByte();
        r10 = r23.readUnsignedByte();
        r11 = r23.readUnsignedByte();
        r0 = 3;
        if (r7 < r0) goto L_0x0018;
    L_0x0013:
        r1 = r23.readUnsignedByte();
        goto L_0x0019;
    L_0x0018:
        r1 = 0;
    L_0x0019:
        r13 = r1;
        r14 = 4;
        if (r7 != r14) goto L_0x003f;
    L_0x001d:
        r1 = r23.readUnsignedIntToInt();
        if (r24 != 0) goto L_0x003d;
    L_0x0023:
        r2 = r1 & 255;
        r3 = r1 >> 8;
        r3 = r3 & 255;
        r3 = r3 << 7;
        r2 = r2 | r3;
        r3 = r1 >> 16;
        r3 = r3 & 255;
        r3 = r3 << 14;
        r2 = r2 | r3;
        r3 = r1 >> 24;
        r3 = r3 & 255;
        r3 = r3 << 21;
        r1 = r2 | r3;
        r15 = r1;
        goto L_0x004c;
    L_0x003d:
        r15 = r1;
        goto L_0x004c;
    L_0x003f:
        if (r7 != r0) goto L_0x0047;
    L_0x0041:
        r1 = r23.readUnsignedIntToInt();
        r15 = r1;
        goto L_0x004c;
    L_0x0047:
        r1 = r23.readUnsignedInt24();
        r15 = r1;
    L_0x004c:
        if (r7 < r0) goto L_0x0053;
    L_0x004e:
        r1 = r23.readUnsignedShort();
        goto L_0x0054;
    L_0x0053:
        r1 = 0;
    L_0x0054:
        r6 = r1;
        r16 = 0;
        if (r9 != 0) goto L_0x006b;
    L_0x0059:
        if (r10 != 0) goto L_0x006b;
    L_0x005b:
        if (r11 != 0) goto L_0x006b;
    L_0x005d:
        if (r13 != 0) goto L_0x006b;
    L_0x005f:
        if (r15 != 0) goto L_0x006b;
    L_0x0061:
        if (r6 != 0) goto L_0x006b;
    L_0x0063:
        r0 = r23.limit();
        r8.setPosition(r0);
        return r16;
        r1 = r23.getPosition();
        r5 = r1 + r15;
        r1 = r23.limit();
        if (r5 <= r1) goto L_0x0087;
    L_0x0078:
        r0 = "Id3Decoder";
        r1 = "Frame size exceeds remaining tag data";
        com.google.android.exoplayer2.util.Log.m10w(r0, r1);
        r0 = r23.limit();
        r8.setPosition(r0);
        return r16;
    L_0x0087:
        if (r26 == 0) goto L_0x009e;
    L_0x0089:
        r1 = r26;
        r2 = r22;
        r3 = r9;
        r4 = r10;
        r12 = r5;
        r5 = r11;
        r14 = r6;
        r6 = r13;
        r1 = r1.evaluate(r2, r3, r4, r5, r6);
        if (r1 != 0) goto L_0x009d;
    L_0x0099:
        r8.setPosition(r12);
        return r16;
    L_0x009d:
        goto L_0x00a0;
    L_0x009e:
        r12 = r5;
        r14 = r6;
    L_0x00a0:
        r1 = 0;
        r2 = 0;
        r3 = 0;
        r4 = 0;
        r5 = 0;
        r6 = 1;
        if (r7 != r0) goto L_0x00cf;
    L_0x00a8:
        r0 = r14 & 128;
        if (r0 == 0) goto L_0x00ae;
    L_0x00ac:
        r0 = 1;
        goto L_0x00af;
    L_0x00ae:
        r0 = 0;
    L_0x00af:
        r1 = r0;
        r0 = r14 & 64;
        if (r0 == 0) goto L_0x00b6;
    L_0x00b4:
        r0 = 1;
        goto L_0x00b7;
    L_0x00b6:
        r0 = 0;
    L_0x00b7:
        r2 = r0;
        r0 = r14 & 32;
        if (r0 == 0) goto L_0x00bf;
    L_0x00bc:
        r17 = 1;
        goto L_0x00c1;
    L_0x00bf:
        r17 = 0;
    L_0x00c1:
        r5 = r17;
        r4 = r1;
        r17 = r1;
        r18 = r2;
        r20 = r3;
        r21 = r4;
        r19 = r5;
        goto L_0x0112;
    L_0x00cf:
        r0 = 4;
        if (r7 != r0) goto L_0x0108;
    L_0x00d2:
        r0 = r14 & 64;
        if (r0 == 0) goto L_0x00d8;
    L_0x00d6:
        r0 = 1;
        goto L_0x00d9;
    L_0x00d8:
        r0 = 0;
    L_0x00d9:
        r5 = r0;
        r0 = r14 & 8;
        if (r0 == 0) goto L_0x00e0;
    L_0x00de:
        r0 = 1;
        goto L_0x00e1;
    L_0x00e0:
        r0 = 0;
    L_0x00e1:
        r1 = r0;
        r0 = r14 & 4;
        if (r0 == 0) goto L_0x00e8;
    L_0x00e6:
        r0 = 1;
        goto L_0x00e9;
    L_0x00e8:
        r0 = 0;
    L_0x00e9:
        r2 = r0;
        r0 = r14 & 2;
        if (r0 == 0) goto L_0x00f0;
    L_0x00ee:
        r0 = 1;
        goto L_0x00f1;
    L_0x00f0:
        r0 = 0;
    L_0x00f1:
        r3 = r0;
        r0 = r14 & 1;
        if (r0 == 0) goto L_0x00f9;
    L_0x00f6:
        r17 = 1;
        goto L_0x00fb;
    L_0x00f9:
        r17 = 0;
    L_0x00fb:
        r4 = r17;
        r17 = r1;
        r18 = r2;
        r20 = r3;
        r21 = r4;
        r19 = r5;
        goto L_0x0112;
    L_0x0108:
        r17 = r1;
        r18 = r2;
        r20 = r3;
        r21 = r4;
        r19 = r5;
    L_0x0112:
        if (r17 != 0) goto L_0x026a;
    L_0x0114:
        if (r18 == 0) goto L_0x0118;
    L_0x0116:
        goto L_0x026a;
    L_0x0118:
        if (r19 == 0) goto L_0x0120;
    L_0x011a:
        r15 = r15 + -1;
        r8.skipBytes(r6);
        goto L_0x0121;
    L_0x0121:
        if (r21 == 0) goto L_0x012a;
    L_0x0123:
        r15 = r15 + -4;
        r0 = 4;
        r8.skipBytes(r0);
        goto L_0x012b;
    L_0x012b:
        if (r20 == 0) goto L_0x0133;
    L_0x012d:
        r0 = removeUnsynchronization(r8, r15);
        r15 = r0;
        goto L_0x0134;
    L_0x0134:
        r0 = 84;
        r1 = 2;
        r2 = 88;
        if (r9 != r0) goto L_0x0149;
    L_0x013b:
        if (r10 != r2) goto L_0x0149;
    L_0x013d:
        if (r11 != r2) goto L_0x0149;
    L_0x013f:
        if (r7 == r1) goto L_0x0143;
    L_0x0141:
        if (r13 != r2) goto L_0x0149;
    L_0x0143:
        r0 = decodeTxxxFrame(r8, r15);	 Catch:{ UnsupportedEncodingException -> 0x015a }
        goto L_0x022e;
        if (r9 != r0) goto L_0x015d;
    L_0x014c:
        r0 = getFrameId(r7, r9, r10, r11, r13);	 Catch:{ UnsupportedEncodingException -> 0x015a }
        r1 = decodeTextInformationFrame(r8, r15, r0);	 Catch:{ UnsupportedEncodingException -> 0x015a }
        r0 = r1;
        goto L_0x022e;
    L_0x0157:
        r0 = move-exception;
        goto L_0x0266;
    L_0x015a:
        r0 = move-exception;
        goto L_0x0259;
    L_0x015d:
        r3 = 87;
        if (r9 != r3) goto L_0x016f;
    L_0x0161:
        if (r10 != r2) goto L_0x016f;
    L_0x0163:
        if (r11 != r2) goto L_0x016f;
    L_0x0165:
        if (r7 == r1) goto L_0x0169;
    L_0x0167:
        if (r13 != r2) goto L_0x016f;
    L_0x0169:
        r0 = decodeWxxxFrame(r8, r15);	 Catch:{ UnsupportedEncodingException -> 0x015a }
        goto L_0x022e;
        r2 = 87;
        if (r9 != r2) goto L_0x017f;
    L_0x0174:
        r0 = getFrameId(r7, r9, r10, r11, r13);	 Catch:{ UnsupportedEncodingException -> 0x015a }
        r1 = decodeUrlLinkFrame(r8, r15, r0);	 Catch:{ UnsupportedEncodingException -> 0x015a }
        r0 = r1;
        goto L_0x022e;
    L_0x017f:
        r2 = 73;
        r3 = 80;
        if (r9 != r3) goto L_0x0195;
    L_0x0185:
        r4 = 82;
        if (r10 != r4) goto L_0x0195;
    L_0x0189:
        if (r11 != r2) goto L_0x0195;
    L_0x018b:
        r4 = 86;
        if (r13 != r4) goto L_0x0195;
    L_0x018f:
        r0 = decodePrivFrame(r8, r15);	 Catch:{ UnsupportedEncodingException -> 0x015a }
        goto L_0x022e;
        r4 = 71;
        r5 = 79;
        if (r9 != r4) goto L_0x01ae;
    L_0x019c:
        r4 = 69;
        if (r10 != r4) goto L_0x01ae;
    L_0x01a0:
        if (r11 != r5) goto L_0x01ae;
    L_0x01a2:
        r4 = 66;
        if (r13 == r4) goto L_0x01a8;
    L_0x01a6:
        if (r7 != r1) goto L_0x01ae;
    L_0x01a8:
        r0 = decodeGeobFrame(r8, r15);	 Catch:{ UnsupportedEncodingException -> 0x015a }
        goto L_0x022e;
        r4 = 67;
        if (r7 != r1) goto L_0x01ba;
    L_0x01b3:
        if (r9 != r3) goto L_0x01ca;
    L_0x01b5:
        if (r10 != r2) goto L_0x01ca;
    L_0x01b7:
        if (r11 != r4) goto L_0x01ca;
    L_0x01b9:
        goto L_0x01c4;
    L_0x01ba:
        r6 = 65;
        if (r9 != r6) goto L_0x01ca;
    L_0x01be:
        if (r10 != r3) goto L_0x01ca;
    L_0x01c0:
        if (r11 != r2) goto L_0x01ca;
    L_0x01c2:
        if (r13 != r4) goto L_0x01ca;
    L_0x01c4:
        r0 = decodeApicFrame(r8, r15, r7);	 Catch:{ UnsupportedEncodingException -> 0x015a }
        goto L_0x022e;
        r2 = 77;
        if (r9 != r4) goto L_0x01dc;
    L_0x01cf:
        if (r10 != r5) goto L_0x01dc;
    L_0x01d1:
        if (r11 != r2) goto L_0x01dc;
    L_0x01d3:
        if (r13 == r2) goto L_0x01d7;
    L_0x01d5:
        if (r7 != r1) goto L_0x01dc;
    L_0x01d7:
        r0 = decodeCommentFrame(r8, r15);	 Catch:{ UnsupportedEncodingException -> 0x015a }
        goto L_0x022e;
        if (r9 != r4) goto L_0x01f9;
    L_0x01df:
        r1 = 72;
        if (r10 != r1) goto L_0x01f9;
    L_0x01e3:
        r1 = 65;
        if (r11 != r1) goto L_0x01f9;
    L_0x01e7:
        if (r13 != r3) goto L_0x01f9;
    L_0x01e9:
        r1 = r23;
        r2 = r15;
        r3 = r22;
        r4 = r24;
        r5 = r25;
        r6 = r26;
        r0 = decodeChapterFrame(r1, r2, r3, r4, r5, r6);	 Catch:{ UnsupportedEncodingException -> 0x015a }
        goto L_0x022e;
        if (r9 != r4) goto L_0x0212;
    L_0x01fc:
        if (r10 != r0) goto L_0x0212;
    L_0x01fe:
        if (r11 != r5) goto L_0x0212;
    L_0x0200:
        if (r13 != r4) goto L_0x0212;
    L_0x0202:
        r1 = r23;
        r2 = r15;
        r3 = r22;
        r4 = r24;
        r5 = r25;
        r6 = r26;
        r0 = decodeChapterTOCFrame(r1, r2, r3, r4, r5, r6);	 Catch:{ UnsupportedEncodingException -> 0x015a }
        goto L_0x022e;
        if (r9 != r2) goto L_0x0224;
    L_0x0215:
        r1 = 76;
        if (r10 != r1) goto L_0x0224;
    L_0x0219:
        r1 = 76;
        if (r11 != r1) goto L_0x0224;
    L_0x021d:
        if (r13 != r0) goto L_0x0224;
    L_0x021f:
        r0 = decodeMlltFrame(r8, r15);	 Catch:{ UnsupportedEncodingException -> 0x015a }
        goto L_0x022e;
        r0 = getFrameId(r7, r9, r10, r11, r13);	 Catch:{ UnsupportedEncodingException -> 0x015a }
        r1 = decodeBinaryFrame(r8, r15, r0);	 Catch:{ UnsupportedEncodingException -> 0x015a }
        r0 = r1;
    L_0x022e:
        if (r0 != 0) goto L_0x0253;
    L_0x0230:
        r1 = "Id3Decoder";
        r2 = new java.lang.StringBuilder;	 Catch:{ UnsupportedEncodingException -> 0x015a }
        r2.<init>();	 Catch:{ UnsupportedEncodingException -> 0x015a }
        r3 = "Failed to decode frame: id=";
        r2.append(r3);	 Catch:{ UnsupportedEncodingException -> 0x015a }
        r3 = getFrameId(r7, r9, r10, r11, r13);	 Catch:{ UnsupportedEncodingException -> 0x015a }
        r2.append(r3);	 Catch:{ UnsupportedEncodingException -> 0x015a }
        r3 = ", frameSize=";
        r2.append(r3);	 Catch:{ UnsupportedEncodingException -> 0x015a }
        r2.append(r15);	 Catch:{ UnsupportedEncodingException -> 0x015a }
        r2 = r2.toString();	 Catch:{ UnsupportedEncodingException -> 0x015a }
        com.google.android.exoplayer2.util.Log.m10w(r1, r2);	 Catch:{ UnsupportedEncodingException -> 0x015a }
        goto L_0x0254;
        r8.setPosition(r12);
        return r0;
        r1 = "Id3Decoder";
        r2 = "Unsupported character encoding";
        com.google.android.exoplayer2.util.Log.m10w(r1, r2);	 Catch:{ all -> 0x0157 }
        r8.setPosition(r12);
        return r16;
    L_0x0266:
        r8.setPosition(r12);
        throw r0;
        r0 = "Id3Decoder";
        r1 = "Skipping unsupported compressed or encrypted frame";
        com.google.android.exoplayer2.util.Log.m10w(r0, r1);
        r8.setPosition(r12);
        return r16;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.metadata.id3.Id3Decoder.decodeFrame(int, com.google.android.exoplayer2.util.ParsableByteArray, boolean, int, com.google.android.exoplayer2.metadata.id3.Id3Decoder$FramePredicate):com.google.android.exoplayer2.metadata.id3.Id3Frame");
    }

    @Nullable
    private static TextInformationFrame decodeTxxxFrame(ParsableByteArray id3Data, int frameSize) throws UnsupportedEncodingException {
        if (frameSize < 1) {
            return null;
        }
        int encoding = id3Data.readUnsignedByte();
        String charset = getCharsetName(encoding);
        byte[] data = new byte[(frameSize - 1)];
        id3Data.readBytes(data, 0, frameSize - 1);
        int descriptionEndIndex = indexOfEos(data, 0, encoding);
        String description = new String(data, 0, descriptionEndIndex, charset);
        int valueStartIndex = delimiterLength(encoding) + descriptionEndIndex;
        return new TextInformationFrame("TXXX", description, decodeStringIfValid(data, valueStartIndex, indexOfEos(data, valueStartIndex, encoding), charset));
    }

    @Nullable
    private static TextInformationFrame decodeTextInformationFrame(ParsableByteArray id3Data, int frameSize, String id) throws UnsupportedEncodingException {
        if (frameSize < 1) {
            return null;
        }
        int encoding = id3Data.readUnsignedByte();
        String charset = getCharsetName(encoding);
        byte[] data = new byte[(frameSize - 1)];
        id3Data.readBytes(data, 0, frameSize - 1);
        return new TextInformationFrame(id, null, new String(data, 0, indexOfEos(data, 0, encoding), charset));
    }

    @Nullable
    private static UrlLinkFrame decodeWxxxFrame(ParsableByteArray id3Data, int frameSize) throws UnsupportedEncodingException {
        if (frameSize < 1) {
            return null;
        }
        int encoding = id3Data.readUnsignedByte();
        String charset = getCharsetName(encoding);
        byte[] data = new byte[(frameSize - 1)];
        id3Data.readBytes(data, 0, frameSize - 1);
        int descriptionEndIndex = indexOfEos(data, 0, encoding);
        String description = new String(data, 0, descriptionEndIndex, charset);
        int urlStartIndex = delimiterLength(encoding) + descriptionEndIndex;
        return new UrlLinkFrame("WXXX", description, decodeStringIfValid(data, urlStartIndex, indexOfZeroByte(data, urlStartIndex), CharEncoding.ISO_8859_1));
    }

    private static UrlLinkFrame decodeUrlLinkFrame(ParsableByteArray id3Data, int frameSize, String id) throws UnsupportedEncodingException {
        byte[] data = new byte[frameSize];
        id3Data.readBytes(data, 0, frameSize);
        return new UrlLinkFrame(id, null, new String(data, 0, indexOfZeroByte(data, 0), CharEncoding.ISO_8859_1));
    }

    private static PrivFrame decodePrivFrame(ParsableByteArray id3Data, int frameSize) throws UnsupportedEncodingException {
        byte[] data = new byte[frameSize];
        id3Data.readBytes(data, 0, frameSize);
        int ownerEndIndex = indexOfZeroByte(data, 0);
        return new PrivFrame(new String(data, 0, ownerEndIndex, CharEncoding.ISO_8859_1), copyOfRangeIfValid(data, ownerEndIndex + 1, data.length));
    }

    private static GeobFrame decodeGeobFrame(ParsableByteArray id3Data, int frameSize) throws UnsupportedEncodingException {
        int encoding = id3Data.readUnsignedByte();
        String charset = getCharsetName(encoding);
        byte[] data = new byte[(frameSize - 1)];
        id3Data.readBytes(data, 0, frameSize - 1);
        int mimeTypeEndIndex = indexOfZeroByte(data, 0);
        String mimeType = new String(data, 0, mimeTypeEndIndex, CharEncoding.ISO_8859_1);
        int filenameStartIndex = mimeTypeEndIndex + 1;
        int filenameEndIndex = indexOfEos(data, filenameStartIndex, encoding);
        String filename = decodeStringIfValid(data, filenameStartIndex, filenameEndIndex, charset);
        int descriptionStartIndex = delimiterLength(encoding) + filenameEndIndex;
        int descriptionEndIndex = indexOfEos(data, descriptionStartIndex, encoding);
        return new GeobFrame(mimeType, filename, decodeStringIfValid(data, descriptionStartIndex, descriptionEndIndex, charset), copyOfRangeIfValid(data, delimiterLength(encoding) + descriptionEndIndex, data.length));
    }

    private static ApicFrame decodeApicFrame(ParsableByteArray id3Data, int frameSize, int majorVersion) throws UnsupportedEncodingException {
        int mimeTypeEndIndex;
        String mimeType;
        int encoding = id3Data.readUnsignedByte();
        String charset = getCharsetName(encoding);
        byte[] data = new byte[(frameSize - 1)];
        id3Data.readBytes(data, 0, frameSize - 1);
        StringBuilder stringBuilder;
        if (majorVersion == 2) {
            mimeTypeEndIndex = 2;
            stringBuilder = new StringBuilder();
            stringBuilder.append("image/");
            stringBuilder.append(Util.toLowerInvariant(new String(data, 0, 3, CharEncoding.ISO_8859_1)));
            mimeType = stringBuilder.toString();
            if ("image/jpg".equals(mimeType)) {
                mimeType = "image/jpeg";
            }
        } else {
            mimeTypeEndIndex = indexOfZeroByte(data, 0);
            mimeType = Util.toLowerInvariant(new String(data, 0, mimeTypeEndIndex, CharEncoding.ISO_8859_1));
            if (mimeType.indexOf(47) == -1) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("image/");
                stringBuilder.append(mimeType);
                mimeType = stringBuilder.toString();
            }
        }
        int pictureType = data[mimeTypeEndIndex + 1] & 255;
        int descriptionStartIndex = mimeTypeEndIndex + 2;
        int descriptionEndIndex = indexOfEos(data, descriptionStartIndex, encoding);
        return new ApicFrame(mimeType, new String(data, descriptionStartIndex, descriptionEndIndex - descriptionStartIndex, charset), pictureType, copyOfRangeIfValid(data, delimiterLength(encoding) + descriptionEndIndex, data.length));
    }

    @Nullable
    private static CommentFrame decodeCommentFrame(ParsableByteArray id3Data, int frameSize) throws UnsupportedEncodingException {
        if (frameSize < 4) {
            return null;
        }
        int encoding = id3Data.readUnsignedByte();
        String charset = getCharsetName(encoding);
        byte[] data = new byte[3];
        id3Data.readBytes(data, 0, 3);
        String language = new String(data, 0, 3);
        data = new byte[(frameSize - 4)];
        id3Data.readBytes(data, 0, frameSize - 4);
        int descriptionEndIndex = indexOfEos(data, 0, encoding);
        String description = new String(data, 0, descriptionEndIndex, charset);
        int textStartIndex = delimiterLength(encoding) + descriptionEndIndex;
        return new CommentFrame(language, description, decodeStringIfValid(data, textStartIndex, indexOfEos(data, textStartIndex, encoding), charset));
    }

    private static ChapterFrame decodeChapterFrame(ParsableByteArray id3Data, int frameSize, int majorVersion, boolean unsignedIntFrameSizeHack, int frameHeaderSize, @Nullable FramePredicate framePredicate) throws UnsupportedEncodingException {
        long startOffset;
        long endOffset;
        ParsableByteArray parsableByteArray = id3Data;
        int framePosition = id3Data.getPosition();
        int chapterIdEndIndex = indexOfZeroByte(parsableByteArray.data, framePosition);
        String chapterId = new String(parsableByteArray.data, framePosition, chapterIdEndIndex - framePosition, CharEncoding.ISO_8859_1);
        parsableByteArray.setPosition(chapterIdEndIndex + 1);
        int startTime = id3Data.readInt();
        int endTime = id3Data.readInt();
        long startOffset2 = id3Data.readUnsignedInt();
        if (startOffset2 == 4294967295L) {
            startOffset = -1;
        } else {
            startOffset = startOffset2;
        }
        startOffset2 = id3Data.readUnsignedInt();
        if (startOffset2 == 4294967295L) {
            endOffset = -1;
        } else {
            endOffset = startOffset2;
        }
        ArrayList<Id3Frame> subFrames = new ArrayList();
        int limit = framePosition + frameSize;
        while (id3Data.getPosition() < limit) {
            Id3Frame frame = decodeFrame(majorVersion, parsableByteArray, unsignedIntFrameSizeHack, frameHeaderSize, framePredicate);
            if (frame != null) {
                subFrames.add(frame);
            }
        }
        int i = majorVersion;
        boolean z = unsignedIntFrameSizeHack;
        int i2 = frameHeaderSize;
        FramePredicate framePredicate2 = framePredicate;
        Id3Frame[] subFrameArray = new Id3Frame[subFrames.size()];
        subFrames.toArray(subFrameArray);
        return new ChapterFrame(chapterId, startTime, endTime, startOffset, endOffset, subFrameArray);
    }

    private static ChapterTocFrame decodeChapterTOCFrame(ParsableByteArray id3Data, int frameSize, int majorVersion, boolean unsignedIntFrameSizeHack, int frameHeaderSize, @Nullable FramePredicate framePredicate) throws UnsupportedEncodingException {
        int endIndex;
        ParsableByteArray parsableByteArray = id3Data;
        int framePosition = id3Data.getPosition();
        int elementIdEndIndex = indexOfZeroByte(parsableByteArray.data, framePosition);
        String elementId = new String(parsableByteArray.data, framePosition, elementIdEndIndex - framePosition, CharEncoding.ISO_8859_1);
        parsableByteArray.setPosition(elementIdEndIndex + 1);
        int ctocFlags = id3Data.readUnsignedByte();
        boolean isOrdered = false;
        boolean isRoot = (ctocFlags & 2) != 0;
        if ((ctocFlags & 1) != 0) {
            isOrdered = true;
        }
        int childCount = id3Data.readUnsignedByte();
        String[] children = new String[childCount];
        for (int i = 0; i < childCount; i++) {
            int startIndex = id3Data.getPosition();
            endIndex = indexOfZeroByte(parsableByteArray.data, startIndex);
            children[i] = new String(parsableByteArray.data, startIndex, endIndex - startIndex, CharEncoding.ISO_8859_1);
            parsableByteArray.setPosition(endIndex + 1);
        }
        ArrayList<Id3Frame> subFrames = new ArrayList();
        int limit = framePosition + frameSize;
        while (id3Data.getPosition() < limit) {
            Id3Frame frame = decodeFrame(majorVersion, parsableByteArray, unsignedIntFrameSizeHack, frameHeaderSize, framePredicate);
            if (frame != null) {
                subFrames.add(frame);
            }
        }
        int i2 = majorVersion;
        boolean z = unsignedIntFrameSizeHack;
        endIndex = frameHeaderSize;
        FramePredicate framePredicate2 = framePredicate;
        Id3Frame[] subFrameArray = new Id3Frame[subFrames.size()];
        subFrames.toArray(subFrameArray);
        return new ChapterTocFrame(elementId, isRoot, isOrdered, children, subFrameArray);
    }

    private static MlltFrame decodeMlltFrame(ParsableByteArray id3Data, int frameSize) {
        int mpegFramesBetweenReference = id3Data.readUnsignedShort();
        int bytesBetweenReference = id3Data.readUnsignedInt24();
        int millisecondsBetweenReference = id3Data.readUnsignedInt24();
        int bitsForBytesDeviation = id3Data.readUnsignedByte();
        int bitsForMillisecondsDeviation = id3Data.readUnsignedByte();
        ParsableBitArray references = new ParsableBitArray();
        references.reset(id3Data);
        int referencesCount = ((frameSize - 10) * 8) / (bitsForBytesDeviation + bitsForMillisecondsDeviation);
        int[] bytesDeviations = new int[referencesCount];
        int[] millisecondsDeviations = new int[referencesCount];
        for (int i = 0; i < referencesCount; i++) {
            int bytesDeviation = references.readBits(bitsForBytesDeviation);
            int millisecondsDeviation = references.readBits(bitsForMillisecondsDeviation);
            bytesDeviations[i] = bytesDeviation;
            millisecondsDeviations[i] = millisecondsDeviation;
        }
        return new MlltFrame(mpegFramesBetweenReference, bytesBetweenReference, millisecondsBetweenReference, bytesDeviations, millisecondsDeviations);
    }

    private static BinaryFrame decodeBinaryFrame(ParsableByteArray id3Data, int frameSize, String id) {
        byte[] frame = new byte[frameSize];
        id3Data.readBytes(frame, 0, frameSize);
        return new BinaryFrame(id, frame);
    }

    private static int removeUnsynchronization(ParsableByteArray data, int length) {
        byte[] bytes = data.data;
        int i = data.getPosition();
        while (i + 1 < length) {
            if ((bytes[i] & 255) == 255 && bytes[i + 1] == (byte) 0) {
                System.arraycopy(bytes, i + 2, bytes, i + 1, (length - i) - 2);
                length--;
            }
            i++;
        }
        return length;
    }

    private static String getCharsetName(int encodingByte) {
        switch (encodingByte) {
            case 1:
                return "UTF-16";
            case 2:
                return CharEncoding.UTF_16BE;
            case 3:
                return "UTF-8";
            default:
                return CharEncoding.ISO_8859_1;
        }
    }

    private static String getFrameId(int majorVersion, int frameId0, int frameId1, int frameId2, int frameId3) {
        if (majorVersion == 2) {
            return String.format(Locale.US, "%c%c%c", new Object[]{Integer.valueOf(frameId0), Integer.valueOf(frameId1), Integer.valueOf(frameId2)});
        }
        return String.format(Locale.US, "%c%c%c%c", new Object[]{Integer.valueOf(frameId0), Integer.valueOf(frameId1), Integer.valueOf(frameId2), Integer.valueOf(frameId3)});
    }

    private static int indexOfEos(byte[] data, int fromIndex, int encoding) {
        int terminationPos = indexOfZeroByte(data, fromIndex);
        if (encoding != 0) {
            if (encoding != 3) {
                while (terminationPos < data.length - 1) {
                    if (terminationPos % 2 == 0 && data[terminationPos + 1] == (byte) 0) {
                        return terminationPos;
                    }
                    terminationPos = indexOfZeroByte(data, terminationPos + 1);
                }
                return data.length;
            }
        }
        return terminationPos;
    }

    private static int indexOfZeroByte(byte[] data, int fromIndex) {
        for (int i = fromIndex; i < data.length; i++) {
            if (data[i] == (byte) 0) {
                return i;
            }
        }
        return data.length;
    }

    private static int delimiterLength(int encodingByte) {
        if (encodingByte != 0) {
            if (encodingByte != 3) {
                return 2;
            }
        }
        return 1;
    }

    private static byte[] copyOfRangeIfValid(byte[] data, int from, int to) {
        if (to <= from) {
            return Util.EMPTY_BYTE_ARRAY;
        }
        return Arrays.copyOfRange(data, from, to);
    }

    private static String decodeStringIfValid(byte[] data, int from, int to, String charsetName) throws UnsupportedEncodingException {
        if (to > from) {
            if (to <= data.length) {
                return new String(data, from, to - from, charsetName);
            }
        }
        return "";
    }
}
