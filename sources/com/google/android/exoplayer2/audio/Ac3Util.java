package com.google.android.exoplayer2.audio;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.ParsableBitArray;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.nio.ByteBuffer;

public final class Ac3Util {
    private static final int AC3_SYNCFRAME_AUDIO_SAMPLE_COUNT = 1536;
    private static final int AUDIO_SAMPLES_PER_AUDIO_BLOCK = 256;
    private static final int[] BITRATE_BY_HALF_FRMSIZECOD = new int[]{32, 40, 48, 56, 64, 80, 96, 112, 128, 160, PsExtractor.AUDIO_STREAM, 224, 256, 320, 384, 448, 512, 576, 640};
    private static final int[] BLOCKS_PER_SYNCFRAME_BY_NUMBLKSCOD = new int[]{1, 2, 3, 6};
    private static final int[] CHANNEL_COUNT_BY_ACMOD = new int[]{2, 1, 2, 3, 3, 4, 4, 5};
    private static final int[] SAMPLE_RATE_BY_FSCOD = new int[]{48000, 44100, 32000};
    private static final int[] SAMPLE_RATE_BY_FSCOD2 = new int[]{24000, 22050, 16000};
    private static final int[] SYNCFRAME_SIZE_WORDS_BY_HALF_FRMSIZECOD_44_1 = new int[]{69, 87, 104, 121, 139, 174, 208, 243, 278, 348, 417, 487, 557, 696, 835, 975, 1114, 1253, 1393};
    public static final int TRUEHD_RECHUNK_SAMPLE_COUNT = 16;
    public static final int TRUEHD_SYNCFRAME_PREFIX_LENGTH = 10;

    public static final class SyncFrameInfo {
        public static final int STREAM_TYPE_TYPE0 = 0;
        public static final int STREAM_TYPE_TYPE1 = 1;
        public static final int STREAM_TYPE_TYPE2 = 2;
        public static final int STREAM_TYPE_UNDEFINED = -1;
        public final int channelCount;
        public final int frameSize;
        public final String mimeType;
        public final int sampleCount;
        public final int sampleRate;
        public final int streamType;

        private SyncFrameInfo(String mimeType, int streamType, int channelCount, int sampleRate, int frameSize, int sampleCount) {
            this.mimeType = mimeType;
            this.streamType = streamType;
            this.channelCount = channelCount;
            this.sampleRate = sampleRate;
            this.frameSize = frameSize;
            this.sampleCount = sampleCount;
        }
    }

    public static Format parseAc3AnnexFFormat(ParsableByteArray data, String trackId, String language, DrmInitData drmInitData) {
        int channelCount;
        int sampleRate = SAMPLE_RATE_BY_FSCOD[(data.readUnsignedByte() & PsExtractor.AUDIO_STREAM) >> 6];
        int nextByte = data.readUnsignedByte();
        int channelCount2 = CHANNEL_COUNT_BY_ACMOD[(nextByte & 56) >> 3];
        if ((nextByte & 4) != 0) {
            channelCount = channelCount2 + 1;
        } else {
            channelCount = channelCount2;
        }
        return Format.createAudioSampleFormat(trackId, MimeTypes.AUDIO_AC3, null, -1, -1, channelCount, sampleRate, null, drmInitData, 0, language);
    }

    public static Format parseEAc3AnnexFFormat(ParsableByteArray data, String trackId, String language, DrmInitData drmInitData) {
        int channelCount;
        String mimeType;
        int i;
        String mimeType2;
        data.skipBytes(2);
        int sampleRate = SAMPLE_RATE_BY_FSCOD[(data.readUnsignedByte() & PsExtractor.AUDIO_STREAM) >> 6];
        int nextByte = data.readUnsignedByte();
        int channelCount2 = CHANNEL_COUNT_BY_ACMOD[(nextByte & 14) >> 1];
        if ((nextByte & 1) != 0) {
            channelCount2++;
        }
        nextByte = data.readUnsignedByte();
        if (((nextByte & 30) >> 1) > 0) {
            if ((data.readUnsignedByte() & 2) != 0) {
                channelCount = channelCount2 + 2;
                mimeType = MimeTypes.AUDIO_E_AC3;
                if (data.bytesLeft() <= 0) {
                    nextByte = data.readUnsignedByte();
                    if ((nextByte & 1) == 0) {
                        i = nextByte;
                        mimeType2 = MimeTypes.AUDIO_E_AC3_JOC;
                    } else {
                        i = nextByte;
                        mimeType2 = mimeType;
                    }
                } else {
                    mimeType2 = mimeType;
                }
                return Format.createAudioSampleFormat(trackId, mimeType2, null, -1, -1, channelCount, sampleRate, null, drmInitData, 0, language);
            }
        }
        channelCount = channelCount2;
        mimeType = MimeTypes.AUDIO_E_AC3;
        if (data.bytesLeft() <= 0) {
            mimeType2 = mimeType;
        } else {
            nextByte = data.readUnsignedByte();
            if ((nextByte & 1) == 0) {
                i = nextByte;
                mimeType2 = mimeType;
            } else {
                i = nextByte;
                mimeType2 = MimeTypes.AUDIO_E_AC3_JOC;
            }
        }
        return Format.createAudioSampleFormat(trackId, mimeType2, null, -1, -1, channelCount, sampleRate, null, drmInitData, 0, language);
    }

    public static SyncFrameInfo parseAc3SyncframeInfo(ParsableBitArray data) {
        int frameSize;
        int i;
        int sampleCount;
        int channelCount;
        String mimeType;
        ParsableBitArray parsableBitArray = data;
        int initialPosition = data.getPosition();
        parsableBitArray.skipBits(40);
        boolean isEac3 = parsableBitArray.readBits(5) == 16;
        parsableBitArray.setPosition(initialPosition);
        int streamType = -1;
        int acmod;
        if (isEac3) {
            int numblkscod;
            int audioBlocks;
            int i2;
            parsableBitArray.skipBits(16);
            switch (parsableBitArray.readBits(2)) {
                case 0:
                    streamType = 0;
                    break;
                case 1:
                    streamType = 1;
                    break;
                case 2:
                    streamType = 2;
                    break;
                default:
                    streamType = -1;
                    break;
            }
            parsableBitArray.skipBits(3);
            frameSize = (parsableBitArray.readBits(11) + 1) * 2;
            int fscod = parsableBitArray.readBits(2);
            if (fscod == 3) {
                numblkscod = 3;
                i = SAMPLE_RATE_BY_FSCOD2[parsableBitArray.readBits(2)];
                audioBlocks = 6;
            } else {
                numblkscod = parsableBitArray.readBits(2);
                audioBlocks = BLOCKS_PER_SYNCFRAME_BY_NUMBLKSCOD[numblkscod];
                i = SAMPLE_RATE_BY_FSCOD[fscod];
            }
            sampleCount = audioBlocks * 256;
            acmod = parsableBitArray.readBits(3);
            boolean lfeon = data.readBit();
            channelCount = CHANNEL_COUNT_BY_ACMOD[acmod] + lfeon;
            parsableBitArray.skipBits(10);
            if (data.readBit()) {
                parsableBitArray.skipBits(8);
            }
            if (acmod == 0) {
                parsableBitArray.skipBits(5);
                if (data.readBit()) {
                    parsableBitArray.skipBits(8);
                }
            }
            if (streamType == 1 && data.readBit()) {
                parsableBitArray.skipBits(16);
            }
            if (data.readBit()) {
                int i3;
                if (acmod > 2) {
                    parsableBitArray.skipBits(2);
                }
                if ((acmod & 1) == 0 || acmod <= 2) {
                    i3 = 6;
                } else {
                    i3 = 6;
                    parsableBitArray.skipBits(6);
                }
                if ((acmod & 4) != 0) {
                    parsableBitArray.skipBits(i3);
                }
                if (lfeon && data.readBit()) {
                    parsableBitArray.skipBits(5);
                }
                if (streamType == 0) {
                    if (data.readBit()) {
                        i3 = 6;
                        parsableBitArray.skipBits(6);
                    } else {
                        i3 = 6;
                    }
                    if (acmod == 0 && data.readBit()) {
                        parsableBitArray.skipBits(i3);
                    }
                    if (data.readBit()) {
                        parsableBitArray.skipBits(i3);
                    }
                    int mixdef = parsableBitArray.readBits(2);
                    if (mixdef == 1) {
                        parsableBitArray.skipBits(5);
                    } else if (mixdef == 2) {
                        parsableBitArray.skipBits(12);
                    } else if (mixdef == 3) {
                        i3 = parsableBitArray.readBits(5);
                        if (data.readBit()) {
                            parsableBitArray.skipBits(5);
                            if (data.readBit()) {
                                parsableBitArray.skipBits(4);
                            }
                            if (data.readBit()) {
                                parsableBitArray.skipBits(4);
                            }
                            if (data.readBit()) {
                                parsableBitArray.skipBits(4);
                            }
                            if (data.readBit()) {
                                parsableBitArray.skipBits(4);
                            }
                            if (data.readBit()) {
                                parsableBitArray.skipBits(4);
                            }
                            if (data.readBit()) {
                                parsableBitArray.skipBits(4);
                            }
                            if (data.readBit()) {
                                parsableBitArray.skipBits(4);
                            }
                            if (data.readBit()) {
                                if (data.readBit()) {
                                    parsableBitArray.skipBits(4);
                                }
                                if (data.readBit()) {
                                    parsableBitArray.skipBits(4);
                                }
                            }
                        }
                        if (data.readBit()) {
                            parsableBitArray.skipBits(5);
                            if (data.readBit()) {
                                parsableBitArray.skipBits(7);
                                if (data.readBit()) {
                                    parsableBitArray.skipBits(8);
                                }
                            }
                        }
                        parsableBitArray.skipBits((i3 + 2) * 8);
                        data.byteAlign();
                    }
                    if (acmod < 2) {
                        if (data.readBit()) {
                            parsableBitArray.skipBits(14);
                        }
                        if (acmod == 0) {
                            if (data.readBit()) {
                                parsableBitArray.skipBits(14);
                            }
                        }
                    }
                    if (data.readBit()) {
                        if (numblkscod == 0) {
                            parsableBitArray.skipBits(5);
                        } else {
                            for (i3 = 0; i3 < audioBlocks; i3++) {
                                if (data.readBit()) {
                                    parsableBitArray.skipBits(5);
                                }
                            }
                        }
                    }
                }
            }
            if (data.readBit()) {
                parsableBitArray.skipBits(5);
                if (acmod == 2) {
                    parsableBitArray.skipBits(4);
                }
                if (acmod >= 6) {
                    parsableBitArray.skipBits(2);
                }
                if (data.readBit()) {
                    i2 = 8;
                    parsableBitArray.skipBits(8);
                } else {
                    i2 = 8;
                }
                if (acmod == 0 && data.readBit()) {
                    parsableBitArray.skipBits(i2);
                }
                i2 = 3;
                if (fscod < 3) {
                    data.skipBit();
                }
            } else {
                i2 = 3;
            }
            if (streamType == 0 && numblkscod != i2) {
                data.skipBit();
            }
            if (streamType == 2) {
                if (numblkscod != i2) {
                    if (!data.readBit()) {
                        i2 = 6;
                    }
                }
                i2 = 6;
                parsableBitArray.skipBits(6);
            } else {
                i2 = 6;
            }
            mimeType = MimeTypes.AUDIO_E_AC3;
            if (data.readBit()) {
                if (parsableBitArray.readBits(i2) == 1 && parsableBitArray.readBits(8) == 1) {
                    mimeType = MimeTypes.AUDIO_E_AC3_JOC;
                }
            }
        } else {
            int i4;
            mimeType = MimeTypes.AUDIO_AC3;
            parsableBitArray.skipBits(32);
            int fscod2 = parsableBitArray.readBits(2);
            frameSize = getAc3SyncframeSize(fscod2, parsableBitArray.readBits(6));
            parsableBitArray.skipBits(8);
            acmod = parsableBitArray.readBits(3);
            if ((acmod & 1) == 0 || acmod == 1) {
                i4 = 2;
            } else {
                i4 = 2;
                parsableBitArray.skipBits(2);
            }
            if ((acmod & 4) != 0) {
                parsableBitArray.skipBits(i4);
            }
            if (acmod == i4) {
                parsableBitArray.skipBits(i4);
            }
            i = SAMPLE_RATE_BY_FSCOD[fscod2];
            sampleCount = AC3_SYNCFRAME_AUDIO_SAMPLE_COUNT;
            channelCount = CHANNEL_COUNT_BY_ACMOD[acmod] + data.readBit();
        }
        return new SyncFrameInfo(mimeType, streamType, channelCount, i, frameSize, sampleCount);
    }

    public static int parseAc3SyncframeSize(byte[] data) {
        if (data.length < 6) {
            return -1;
        }
        if (!(((data[5] & 255) >> 3) == 16)) {
            return getAc3SyncframeSize((data[4] & PsExtractor.AUDIO_STREAM) >> 6, data[4] & 63);
        }
        return (((data[3] & 255) | ((data[2] & 7) << 8)) + 1) * 2;
    }

    public static int getAc3SyncframeAudioSampleCount() {
        return AC3_SYNCFRAME_AUDIO_SAMPLE_COUNT;
    }

    public static int parseEAc3SyncframeAudioSampleCount(ByteBuffer buffer) {
        int i = 6;
        if (((buffer.get(buffer.position() + 4) & PsExtractor.AUDIO_STREAM) >> 6) != 3) {
            i = BLOCKS_PER_SYNCFRAME_BY_NUMBLKSCOD[(buffer.get(buffer.position() + 4) & 48) >> 4];
        }
        return i * 256;
    }

    public static int findTrueHdSyncframeOffset(ByteBuffer buffer) {
        int startIndex = buffer.position();
        int endIndex = buffer.limit() - 10;
        for (int i = startIndex; i <= endIndex; i++) {
            if ((buffer.getInt(i + 4) & -16777217) == -1167101192) {
                return i - startIndex;
            }
        }
        return -1;
    }

    public static int parseTrueHdSyncframeAudioSampleCount(byte[] syncframe) {
        boolean isMlp = false;
        if (syncframe[4] == (byte) -8 && syncframe[5] == (byte) 114 && syncframe[6] == (byte) 111) {
            if ((syncframe[7] & 254) == 186) {
                if ((syncframe[7] & 255) == 187) {
                    isMlp = true;
                }
                return 40 << ((syncframe[isMlp ? 9 : 8] >> 4) & 7);
            }
        }
        return 0;
    }

    public static int parseTrueHdSyncframeAudioSampleCount(ByteBuffer buffer, int offset) {
        return 40 << ((buffer.get((buffer.position() + offset) + ((buffer.get((buffer.position() + offset) + 7) & 255) == 187 ? 9 : 8)) >> 4) & 7);
    }

    private static int getAc3SyncframeSize(int fscod, int frmsizecod) {
        int halfFrmsizecod = frmsizecod / 2;
        if (fscod >= 0) {
            int sampleRate = SAMPLE_RATE_BY_FSCOD;
            if (fscod < sampleRate.length && frmsizecod >= 0) {
                int[] iArr = SYNCFRAME_SIZE_WORDS_BY_HALF_FRMSIZECOD_44_1;
                if (halfFrmsizecod < iArr.length) {
                    sampleRate = sampleRate[fscod];
                    if (sampleRate == 44100) {
                        return (iArr[halfFrmsizecod] + (frmsizecod % 2)) * 2;
                    }
                    int bitrate = BITRATE_BY_HALF_FRMSIZECOD[halfFrmsizecod];
                    if (sampleRate == 32000) {
                        return bitrate * 6;
                    }
                    return bitrate * 4;
                }
            }
        }
        return -1;
    }

    private Ac3Util() {
    }
}
