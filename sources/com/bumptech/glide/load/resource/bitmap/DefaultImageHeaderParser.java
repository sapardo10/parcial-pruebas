package com.bumptech.glide.load.resource.bitmap;

import android.support.annotation.NonNull;
import android.support.v4.internal.view.SupportMenu;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import com.bumptech.glide.load.ImageHeaderParser;
import com.bumptech.glide.load.ImageHeaderParser.ImageType;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.util.Preconditions;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

public final class DefaultImageHeaderParser implements ImageHeaderParser {
    private static final int[] BYTES_PER_FORMAT = new int[]{0, 1, 1, 2, 4, 8, 1, 1, 2, 4, 8, 4, 8};
    static final int EXIF_MAGIC_NUMBER = 65496;
    static final int EXIF_SEGMENT_TYPE = 225;
    private static final int GIF_HEADER = 4671814;
    private static final int INTEL_TIFF_MAGIC_NUMBER = 18761;
    private static final String JPEG_EXIF_SEGMENT_PREAMBLE = "Exif\u0000\u0000";
    static final byte[] JPEG_EXIF_SEGMENT_PREAMBLE_BYTES = JPEG_EXIF_SEGMENT_PREAMBLE.getBytes(Charset.forName("UTF-8"));
    private static final int MARKER_EOI = 217;
    private static final int MOTOROLA_TIFF_MAGIC_NUMBER = 19789;
    private static final int ORIENTATION_TAG_TYPE = 274;
    private static final int PNG_HEADER = -1991225785;
    private static final int RIFF_HEADER = 1380533830;
    private static final int SEGMENT_SOS = 218;
    static final int SEGMENT_START_ID = 255;
    private static final String TAG = "DfltImageHeaderParser";
    private static final int VP8_HEADER = 1448097792;
    private static final int VP8_HEADER_MASK = -256;
    private static final int VP8_HEADER_TYPE_EXTENDED = 88;
    private static final int VP8_HEADER_TYPE_LOSSLESS = 76;
    private static final int VP8_HEADER_TYPE_MASK = 255;
    private static final int WEBP_EXTENDED_ALPHA_FLAG = 16;
    private static final int WEBP_HEADER = 1464156752;
    private static final int WEBP_LOSSLESS_ALPHA_FLAG = 8;

    private static final class RandomAccessReader {
        private final ByteBuffer data;

        RandomAccessReader(byte[] data, int length) {
            this.data = (ByteBuffer) ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN).limit(length);
        }

        void order(ByteOrder byteOrder) {
            this.data.order(byteOrder);
        }

        int length() {
            return this.data.remaining();
        }

        int getInt32(int offset) {
            return isAvailable(offset, 4) ? this.data.getInt(offset) : -1;
        }

        short getInt16(int offset) {
            return isAvailable(offset, 2) ? this.data.getShort(offset) : (short) -1;
        }

        private boolean isAvailable(int offset, int byteSize) {
            return this.data.remaining() - offset >= byteSize;
        }
    }

    private interface Reader {
        int getByte() throws IOException;

        int getUInt16() throws IOException;

        short getUInt8() throws IOException;

        int read(byte[] bArr, int i) throws IOException;

        long skip(long j) throws IOException;
    }

    private static final class ByteBufferReader implements Reader {
        private final ByteBuffer byteBuffer;

        ByteBufferReader(ByteBuffer byteBuffer) {
            this.byteBuffer = byteBuffer;
            byteBuffer.order(ByteOrder.BIG_ENDIAN);
        }

        public int getUInt16() {
            return ((getByte() << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK) | (getByte() & 255);
        }

        public short getUInt8() {
            return (short) (getByte() & 255);
        }

        public long skip(long total) {
            int toSkip = (int) Math.min((long) this.byteBuffer.remaining(), total);
            ByteBuffer byteBuffer = this.byteBuffer;
            byteBuffer.position(byteBuffer.position() + toSkip);
            return (long) toSkip;
        }

        public int read(byte[] buffer, int byteCount) {
            int toRead = Math.min(byteCount, this.byteBuffer.remaining());
            if (toRead == 0) {
                return -1;
            }
            this.byteBuffer.get(buffer, 0, toRead);
            return toRead;
        }

        public int getByte() {
            if (this.byteBuffer.remaining() < 1) {
                return -1;
            }
            return this.byteBuffer.get();
        }
    }

    private static final class StreamReader implements Reader {
        private final InputStream is;

        StreamReader(InputStream is) {
            this.is = is;
        }

        public int getUInt16() throws IOException {
            return ((this.is.read() << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK) | (this.is.read() & 255);
        }

        public short getUInt8() throws IOException {
            return (short) (this.is.read() & 255);
        }

        public long skip(long total) throws IOException {
            if (total < 0) {
                return 0;
            }
            long toSkip = total;
            while (toSkip > 0) {
                long skipped = this.is.skip(toSkip);
                if (skipped > 0) {
                    toSkip -= skipped;
                } else if (this.is.read() == -1) {
                    break;
                } else {
                    toSkip--;
                }
            }
            return total - toSkip;
        }

        public int read(byte[] buffer, int byteCount) throws IOException {
            int toRead = byteCount;
            while (toRead > 0) {
                int read = this.is.read(buffer, byteCount - toRead, toRead);
                int read2 = read;
                if (read == -1) {
                    break;
                }
                toRead -= read2;
            }
            return byteCount - toRead;
        }

        public int getByte() throws IOException {
            return this.is.read();
        }
    }

    @NonNull
    public ImageType getType(@NonNull InputStream is) throws IOException {
        return getType(new StreamReader((InputStream) Preconditions.checkNotNull(is)));
    }

    @NonNull
    public ImageType getType(@NonNull ByteBuffer byteBuffer) throws IOException {
        return getType(new ByteBufferReader((ByteBuffer) Preconditions.checkNotNull(byteBuffer)));
    }

    public int getOrientation(@NonNull InputStream is, @NonNull ArrayPool byteArrayPool) throws IOException {
        return getOrientation(new StreamReader((InputStream) Preconditions.checkNotNull(is)), (ArrayPool) Preconditions.checkNotNull(byteArrayPool));
    }

    public int getOrientation(@NonNull ByteBuffer byteBuffer, @NonNull ArrayPool byteArrayPool) throws IOException {
        return getOrientation(new ByteBufferReader((ByteBuffer) Preconditions.checkNotNull(byteBuffer)), (ArrayPool) Preconditions.checkNotNull(byteArrayPool));
    }

    @NonNull
    private ImageType getType(Reader reader) throws IOException {
        int firstTwoBytes = reader.getUInt16();
        if (firstTwoBytes == EXIF_MAGIC_NUMBER) {
            return ImageType.JPEG;
        }
        int firstFourBytes = ((firstTwoBytes << 16) & SupportMenu.CATEGORY_MASK) | (reader.getUInt16() & SupportMenu.USER_MASK);
        if (firstFourBytes == PNG_HEADER) {
            reader.skip(21);
            return reader.getByte() >= 3 ? ImageType.PNG_A : ImageType.PNG;
        } else if ((firstFourBytes >> 8) == GIF_HEADER) {
            return ImageType.GIF;
        } else {
            if (firstFourBytes != RIFF_HEADER) {
                return ImageType.UNKNOWN;
            }
            reader.skip(4);
            if ((((reader.getUInt16() << 16) & SupportMenu.CATEGORY_MASK) | (reader.getUInt16() & SupportMenu.USER_MASK)) != WEBP_HEADER) {
                return ImageType.UNKNOWN;
            }
            int fourthFourBytes = (SupportMenu.CATEGORY_MASK & (reader.getUInt16() << 16)) | (SupportMenu.USER_MASK & reader.getUInt16());
            if ((fourthFourBytes & -256) != VP8_HEADER) {
                return ImageType.UNKNOWN;
            }
            if ((fourthFourBytes & 255) == 88) {
                reader.skip(4);
                return (reader.getByte() & 16) != 0 ? ImageType.WEBP_A : ImageType.WEBP;
            } else if ((fourthFourBytes & 255) != 76) {
                return ImageType.WEBP;
            } else {
                reader.skip(4);
                return (reader.getByte() & 8) != 0 ? ImageType.WEBP_A : ImageType.WEBP;
            }
        }
    }

    private int getOrientation(Reader reader, ArrayPool byteArrayPool) throws IOException {
        int magicNumber = reader.getUInt16();
        if (handles(magicNumber)) {
            int exifSegmentLength = moveToExifSegmentAndGetLength(reader);
            if (exifSegmentLength == -1) {
                if (Log.isLoggable(TAG, 3)) {
                    Log.d(TAG, "Failed to parse exif segment length, or exif segment not found");
                }
                return -1;
            }
            byte[] exifData = (byte[]) byteArrayPool.get(exifSegmentLength, byte[].class);
            try {
                int parseExifSegment = parseExifSegment(reader, exifData, exifSegmentLength);
                return parseExifSegment;
            } finally {
                byteArrayPool.put(exifData);
            }
        } else {
            if (Log.isLoggable(TAG, 3)) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Parser doesn't handle magic number: ");
                stringBuilder.append(magicNumber);
                Log.d(str, stringBuilder.toString());
            }
            return -1;
        }
    }

    private int parseExifSegment(Reader reader, byte[] tempArray, int exifSegmentLength) throws IOException {
        int read = reader.read(tempArray, exifSegmentLength);
        if (read != exifSegmentLength) {
            if (Log.isLoggable(TAG, 3)) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Unable to read exif segment data, length: ");
                stringBuilder.append(exifSegmentLength);
                stringBuilder.append(", actually read: ");
                stringBuilder.append(read);
                Log.d(str, stringBuilder.toString());
            }
            return -1;
        } else if (hasJpegExifPreamble(tempArray, exifSegmentLength)) {
            return parseExifSegment(new RandomAccessReader(tempArray, exifSegmentLength));
        } else {
            if (Log.isLoggable(TAG, 3)) {
                Log.d(TAG, "Missing jpeg exif preamble");
            }
            return -1;
        }
    }

    private boolean hasJpegExifPreamble(byte[] exifData, int exifSegmentLength) {
        boolean result = exifData != null && exifSegmentLength > JPEG_EXIF_SEGMENT_PREAMBLE_BYTES.length;
        if (!result) {
            return result;
        }
        int i = 0;
        while (true) {
            byte[] bArr = JPEG_EXIF_SEGMENT_PREAMBLE_BYTES;
            if (i >= bArr.length) {
                return result;
            }
            if (exifData[i] != bArr[i]) {
                return false;
            }
            i++;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int moveToExifSegmentAndGetLength(com.bumptech.glide.load.resource.bitmap.DefaultImageHeaderParser.Reader r11) throws java.io.IOException {
        /*
        r10 = this;
    L_0x0000:
        r0 = r11.getUInt8();
        r1 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        r2 = 3;
        r3 = -1;
        if (r0 == r1) goto L_0x002b;
    L_0x000a:
        r1 = "DfltImageHeaderParser";
        r1 = android.util.Log.isLoggable(r1, r2);
        if (r1 == 0) goto L_0x0029;
    L_0x0012:
        r1 = "DfltImageHeaderParser";
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r4 = "Unknown segmentId=";
        r2.append(r4);
        r2.append(r0);
        r2 = r2.toString();
        android.util.Log.d(r1, r2);
        goto L_0x002a;
    L_0x002a:
        return r3;
    L_0x002b:
        r1 = r11.getUInt8();
        r4 = 218; // 0xda float:3.05E-43 double:1.077E-321;
        if (r1 != r4) goto L_0x0034;
    L_0x0033:
        return r3;
    L_0x0034:
        r4 = 217; // 0xd9 float:3.04E-43 double:1.07E-321;
        if (r1 != r4) goto L_0x004a;
    L_0x0038:
        r4 = "DfltImageHeaderParser";
        r2 = android.util.Log.isLoggable(r4, r2);
        if (r2 == 0) goto L_0x0048;
    L_0x0040:
        r2 = "DfltImageHeaderParser";
        r4 = "Found MARKER_EOI in exif segment";
        android.util.Log.d(r2, r4);
        goto L_0x0049;
    L_0x0049:
        return r3;
    L_0x004a:
        r4 = r11.getUInt16();
        r4 = r4 + -2;
        r5 = 225; // 0xe1 float:3.15E-43 double:1.11E-321;
        if (r1 == r5) goto L_0x0093;
    L_0x0054:
        r5 = (long) r4;
        r5 = r11.skip(r5);
        r7 = (long) r4;
        r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1));
        if (r9 == 0) goto L_0x008f;
    L_0x005e:
        r7 = "DfltImageHeaderParser";
        r2 = android.util.Log.isLoggable(r7, r2);
        if (r2 == 0) goto L_0x008d;
    L_0x0066:
        r2 = "DfltImageHeaderParser";
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r8 = "Unable to skip enough data, type: ";
        r7.append(r8);
        r7.append(r1);
        r8 = ", wanted to skip: ";
        r7.append(r8);
        r7.append(r4);
        r8 = ", but actually skipped: ";
        r7.append(r8);
        r7.append(r5);
        r7 = r7.toString();
        android.util.Log.d(r2, r7);
        goto L_0x008e;
    L_0x008e:
        return r3;
        goto L_0x0000;
    L_0x0093:
        return r4;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.load.resource.bitmap.DefaultImageHeaderParser.moveToExifSegmentAndGetLength(com.bumptech.glide.load.resource.bitmap.DefaultImageHeaderParser$Reader):int");
    }

    private static int parseExifSegment(RandomAccessReader segmentData) {
        ByteOrder byteOrder;
        RandomAccessReader randomAccessReader = segmentData;
        int headerOffsetSize = JPEG_EXIF_SEGMENT_PREAMBLE.length();
        short byteOrderIdentifier = randomAccessReader.getInt16(headerOffsetSize);
        int i = 3;
        if (byteOrderIdentifier == (short) 18761) {
            byteOrder = ByteOrder.LITTLE_ENDIAN;
        } else if (byteOrderIdentifier != (short) 19789) {
            if (Log.isLoggable(TAG, 3)) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Unknown endianness = ");
                stringBuilder.append(byteOrderIdentifier);
                Log.d(str, stringBuilder.toString());
            }
            byteOrder = ByteOrder.BIG_ENDIAN;
        } else {
            byteOrder = ByteOrder.BIG_ENDIAN;
        }
        randomAccessReader.order(byteOrder);
        int firstIfdOffset = randomAccessReader.getInt32(headerOffsetSize + 4) + headerOffsetSize;
        int tagCount = randomAccessReader.getInt16(firstIfdOffset);
        int i2 = 0;
        while (i2 < tagCount) {
            int tagOffset = calcTagOffset(firstIfdOffset, i2);
            int tagType = randomAccessReader.getInt16(tagOffset);
            if (tagType == ORIENTATION_TAG_TYPE) {
                String str2;
                int formatCode = randomAccessReader.getInt16(tagOffset + 2);
                if (formatCode >= 1) {
                    if (formatCode <= 12) {
                        int componentCount = randomAccessReader.getInt32(tagOffset + 4);
                        if (componentCount >= 0) {
                            if (Log.isLoggable(TAG, i)) {
                                String str3 = TAG;
                                StringBuilder stringBuilder2 = new StringBuilder();
                                stringBuilder2.append("Got tagIndex=");
                                stringBuilder2.append(i2);
                                stringBuilder2.append(" tagType=");
                                stringBuilder2.append(tagType);
                                stringBuilder2.append(" formatCode=");
                                stringBuilder2.append(formatCode);
                                stringBuilder2.append(" componentCount=");
                                stringBuilder2.append(componentCount);
                                Log.d(str3, stringBuilder2.toString());
                            }
                            int byteCount = BYTES_PER_FORMAT[formatCode] + componentCount;
                            StringBuilder stringBuilder3;
                            if (byteCount <= 4) {
                                int tagValueOffset = tagOffset + 8;
                                if (tagValueOffset >= 0) {
                                    if (tagValueOffset <= segmentData.length()) {
                                        if (byteCount >= 0) {
                                            if (tagValueOffset + byteCount <= segmentData.length()) {
                                                return randomAccessReader.getInt16(tagValueOffset);
                                            }
                                        }
                                        if (Log.isLoggable(TAG, i)) {
                                            String str4 = TAG;
                                            StringBuilder stringBuilder4 = new StringBuilder();
                                            stringBuilder4.append("Illegal number of bytes for TI tag data tagType=");
                                            stringBuilder4.append(tagType);
                                            Log.d(str4, stringBuilder4.toString());
                                        }
                                    }
                                }
                                if (Log.isLoggable(TAG, 3)) {
                                    str2 = TAG;
                                    stringBuilder3 = new StringBuilder();
                                    stringBuilder3.append("Illegal tagValueOffset=");
                                    stringBuilder3.append(tagValueOffset);
                                    stringBuilder3.append(" tagType=");
                                    stringBuilder3.append(tagType);
                                    Log.d(str2, stringBuilder3.toString());
                                }
                            } else if (Log.isLoggable(TAG, i)) {
                                String str5 = TAG;
                                stringBuilder3 = new StringBuilder();
                                stringBuilder3.append("Got byte count > 4, not orientation, continuing, formatCode=");
                                stringBuilder3.append(formatCode);
                                Log.d(str5, stringBuilder3.toString());
                            }
                        } else if (Log.isLoggable(TAG, i)) {
                            Log.d(TAG, "Negative tiff component count");
                        }
                    }
                }
                if (Log.isLoggable(TAG, 3)) {
                    str2 = TAG;
                    StringBuilder stringBuilder5 = new StringBuilder();
                    stringBuilder5.append("Got invalid format code = ");
                    stringBuilder5.append(formatCode);
                    Log.d(str2, stringBuilder5.toString());
                }
            }
            i2++;
            i = 3;
        }
        return -1;
    }

    private static int calcTagOffset(int ifdOffset, int tagIndex) {
        return (ifdOffset + 2) + (tagIndex * 12);
    }

    private static boolean handles(int imageMagicNumber) {
        if (!((imageMagicNumber & EXIF_MAGIC_NUMBER) == EXIF_MAGIC_NUMBER || imageMagicNumber == MOTOROLA_TIFF_MAGIC_NUMBER)) {
            if (imageMagicNumber != INTEL_TIFF_MAGIC_NUMBER) {
                return false;
            }
        }
        return true;
    }
}
