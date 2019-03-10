package com.google.android.exoplayer2.util;

import android.support.annotation.Nullable;
import android.util.Pair;
import com.google.android.exoplayer2.ParserException;
import java.util.ArrayList;
import java.util.List;

public final class CodecSpecificDataUtil {
    private static final int AUDIO_OBJECT_TYPE_AAC_LC = 2;
    private static final int AUDIO_OBJECT_TYPE_ER_BSAC = 22;
    private static final int AUDIO_OBJECT_TYPE_ESCAPE = 31;
    private static final int AUDIO_OBJECT_TYPE_PS = 29;
    private static final int AUDIO_OBJECT_TYPE_SBR = 5;
    private static final int AUDIO_SPECIFIC_CONFIG_CHANNEL_CONFIGURATION_INVALID = -1;
    private static final int[] AUDIO_SPECIFIC_CONFIG_CHANNEL_COUNT_TABLE = new int[]{0, 1, 2, 3, 4, 5, 6, 8, -1, -1, -1, 7, 8, -1, 8, -1};
    private static final int AUDIO_SPECIFIC_CONFIG_FREQUENCY_INDEX_ARBITRARY = 15;
    private static final int[] AUDIO_SPECIFIC_CONFIG_SAMPLING_RATE_TABLE = new int[]{96000, 88200, 64000, 48000, 44100, 32000, 24000, 22050, 16000, 12000, 11025, 8000, 7350};
    private static final byte[] NAL_START_CODE = new byte[]{(byte) 0, (byte) 0, (byte) 0, (byte) 1};

    public static byte[] buildAacLcAudioSpecificConfig(int r5, int r6) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:23:0x004d in {5, 6, 7, 13, 14, 15, 20, 22} preds:[]
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
        r0 = -1;
        r1 = 0;
    L_0x0002:
        r2 = AUDIO_SPECIFIC_CONFIG_SAMPLING_RATE_TABLE;
        r3 = r2.length;
        if (r1 >= r3) goto L_0x0011;
    L_0x0007:
        r2 = r2[r1];
        if (r5 != r2) goto L_0x000d;
    L_0x000b:
        r0 = r1;
        goto L_0x000e;
    L_0x000e:
        r1 = r1 + 1;
        goto L_0x0002;
    L_0x0011:
        r1 = -1;
        r2 = 0;
    L_0x0013:
        r3 = AUDIO_SPECIFIC_CONFIG_CHANNEL_COUNT_TABLE;
        r4 = r3.length;
        if (r2 >= r4) goto L_0x0022;
    L_0x0018:
        r3 = r3[r2];
        if (r6 != r3) goto L_0x001e;
    L_0x001c:
        r1 = r2;
        goto L_0x001f;
    L_0x001f:
        r2 = r2 + 1;
        goto L_0x0013;
    L_0x0022:
        r2 = -1;
        if (r5 == r2) goto L_0x002d;
    L_0x0025:
        if (r1 == r2) goto L_0x002d;
    L_0x0027:
        r2 = 2;
        r2 = buildAacAudioSpecificConfig(r2, r0, r1);
        return r2;
        r2 = new java.lang.IllegalArgumentException;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "Invalid sample rate or number of channels: ";
        r3.append(r4);
        r3.append(r5);
        r4 = ", ";
        r3.append(r4);
        r3.append(r6);
        r3 = r3.toString();
        r2.<init>(r3);
        throw r2;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.util.CodecSpecificDataUtil.buildAacLcAudioSpecificConfig(int, int):byte[]");
    }

    private CodecSpecificDataUtil() {
    }

    public static Pair<Integer, Integer> parseAacAudioSpecificConfig(byte[] audioSpecificConfig) throws ParserException {
        return parseAacAudioSpecificConfig(new ParsableBitArray(audioSpecificConfig), false);
    }

    public static Pair<Integer, Integer> parseAacAudioSpecificConfig(ParsableBitArray bitArray, boolean forceReadToEnd) throws ParserException {
        int epConfig;
        int channelCount;
        int audioObjectType = getAacAudioObjectType(bitArray);
        int sampleRate = getAacSamplingFrequency(bitArray);
        int channelConfiguration = bitArray.readBits(4);
        if (audioObjectType != 5) {
            if (audioObjectType != 29) {
                if (forceReadToEnd) {
                    if (audioObjectType != 17) {
                        switch (audioObjectType) {
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                                break;
                            default:
                                switch (audioObjectType) {
                                    case 6:
                                    case 7:
                                        break;
                                    default:
                                        switch (audioObjectType) {
                                            case 19:
                                            case 20:
                                            case 21:
                                            case 22:
                                            case 23:
                                                break;
                                            default:
                                                StringBuilder stringBuilder = new StringBuilder();
                                                stringBuilder.append("Unsupported audio object type: ");
                                                stringBuilder.append(audioObjectType);
                                                throw new ParserException(stringBuilder.toString());
                                        }
                                }
                        }
                    }
                    parseGaSpecificConfig(bitArray, audioObjectType, channelConfiguration);
                    switch (audioObjectType) {
                        case 17:
                        case 19:
                        case 20:
                        case 21:
                        case 22:
                        case 23:
                            epConfig = bitArray.readBits(2);
                            if (epConfig == 2 && epConfig != 3) {
                                break;
                            }
                            StringBuilder stringBuilder2 = new StringBuilder();
                            stringBuilder2.append("Unsupported epConfig: ");
                            stringBuilder2.append(epConfig);
                            throw new ParserException(stringBuilder2.toString());
                        default:
                            break;
                    }
                }
                channelCount = AUDIO_SPECIFIC_CONFIG_CHANNEL_COUNT_TABLE[channelConfiguration];
                Assertions.checkArgument(channelCount == -1);
                return Pair.create(Integer.valueOf(sampleRate), Integer.valueOf(channelCount));
            }
        }
        sampleRate = getAacSamplingFrequency(bitArray);
        audioObjectType = getAacAudioObjectType(bitArray);
        if (audioObjectType == 22) {
            channelConfiguration = bitArray.readBits(4);
        }
        if (forceReadToEnd) {
            if (audioObjectType != 17) {
                switch (audioObjectType) {
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                        break;
                    default:
                        switch (audioObjectType) {
                            case 6:
                            case 7:
                                break;
                            default:
                                switch (audioObjectType) {
                                    case 19:
                                    case 20:
                                    case 21:
                                    case 22:
                                    case 23:
                                        break;
                                    default:
                                        StringBuilder stringBuilder3 = new StringBuilder();
                                        stringBuilder3.append("Unsupported audio object type: ");
                                        stringBuilder3.append(audioObjectType);
                                        throw new ParserException(stringBuilder3.toString());
                                }
                        }
                }
            }
            parseGaSpecificConfig(bitArray, audioObjectType, channelConfiguration);
            switch (audioObjectType) {
                case 17:
                case 19:
                case 20:
                case 21:
                case 22:
                case 23:
                    epConfig = bitArray.readBits(2);
                    if (epConfig == 2) {
                        break;
                    }
                    StringBuilder stringBuilder22 = new StringBuilder();
                    stringBuilder22.append("Unsupported epConfig: ");
                    stringBuilder22.append(epConfig);
                    throw new ParserException(stringBuilder22.toString());
                default:
                    break;
            }
        }
        channelCount = AUDIO_SPECIFIC_CONFIG_CHANNEL_COUNT_TABLE[channelConfiguration];
        if (channelCount == -1) {
        }
        Assertions.checkArgument(channelCount == -1);
        return Pair.create(Integer.valueOf(sampleRate), Integer.valueOf(channelCount));
    }

    public static byte[] buildAacAudioSpecificConfig(int audioObjectType, int sampleRateIndex, int channelConfig) {
        return new byte[]{(byte) (((audioObjectType << 3) & 248) | ((sampleRateIndex >> 1) & 7)), (byte) (((sampleRateIndex << 7) & 128) | ((channelConfig << 3) & 120))};
    }

    public static String buildAvcCodecString(int profileIdc, int constraintsFlagsAndReservedZero2Bits, int levelIdc) {
        return String.format("avc1.%02X%02X%02X", new Object[]{Integer.valueOf(profileIdc), Integer.valueOf(constraintsFlagsAndReservedZero2Bits), Integer.valueOf(levelIdc)});
    }

    public static byte[] buildNalUnit(byte[] data, int offset, int length) {
        Object obj = NAL_START_CODE;
        byte[] nalUnit = new byte[(obj.length + length)];
        System.arraycopy(obj, 0, nalUnit, 0, obj.length);
        System.arraycopy(data, offset, nalUnit, NAL_START_CODE.length, length);
        return nalUnit;
    }

    @Nullable
    public static byte[][] splitNalUnits(byte[] data) {
        if (!isNalStartCode(data, 0)) {
            return (byte[][]) null;
        }
        List<Integer> starts = new ArrayList();
        int nalUnitIndex = 0;
        while (true) {
            starts.add(Integer.valueOf(nalUnitIndex));
            nalUnitIndex = findNalStartCode(data, NAL_START_CODE.length + nalUnitIndex);
            if (nalUnitIndex == -1) {
                break;
            }
        }
        byte[][] split = new byte[starts.size()][];
        int i = 0;
        while (i < starts.size()) {
            int startIndex = ((Integer) starts.get(i)).intValue();
            byte[] nal = new byte[((i < starts.size() + -1 ? ((Integer) starts.get(i + 1)).intValue() : data.length) - startIndex)];
            System.arraycopy(data, startIndex, nal, 0, nal.length);
            split[i] = nal;
            i++;
        }
        return split;
    }

    private static int findNalStartCode(byte[] data, int index) {
        int endIndex = data.length - NAL_START_CODE.length;
        for (int i = index; i <= endIndex; i++) {
            if (isNalStartCode(data, i)) {
                return i;
            }
        }
        return -1;
    }

    private static boolean isNalStartCode(byte[] data, int index) {
        if (data.length - index <= NAL_START_CODE.length) {
            return false;
        }
        int j = 0;
        while (true) {
            byte[] bArr = NAL_START_CODE;
            if (j >= bArr.length) {
                return true;
            }
            if (data[index + j] != bArr[j]) {
                return false;
            }
            j++;
        }
    }

    private static int getAacAudioObjectType(ParsableBitArray bitArray) {
        int audioObjectType = bitArray.readBits(5);
        if (audioObjectType == 31) {
            return bitArray.readBits(6) + 32;
        }
        return audioObjectType;
    }

    private static int getAacSamplingFrequency(ParsableBitArray bitArray) {
        int frequencyIndex = bitArray.readBits(4);
        if (frequencyIndex == 15) {
            return bitArray.readBits(24);
        }
        Assertions.checkArgument(frequencyIndex < 13);
        return AUDIO_SPECIFIC_CONFIG_SAMPLING_RATE_TABLE[frequencyIndex];
    }

    private static void parseGaSpecificConfig(ParsableBitArray bitArray, int audioObjectType, int channelConfiguration) {
        bitArray.skipBits(1);
        if (bitArray.readBit()) {
            bitArray.skipBits(14);
        }
        boolean extensionFlag = bitArray.readBit();
        if (channelConfiguration != 0) {
            if (audioObjectType != 6) {
                if (audioObjectType != 20) {
                    if (extensionFlag) {
                        if (audioObjectType == 22) {
                            bitArray.skipBits(16);
                        }
                        if (!(audioObjectType == 17 || audioObjectType == 19 || audioObjectType == 20)) {
                            if (audioObjectType == 23) {
                                bitArray.skipBits(1);
                                return;
                            }
                        }
                        bitArray.skipBits(3);
                        bitArray.skipBits(1);
                        return;
                    }
                    return;
                }
            }
            bitArray.skipBits(3);
            if (extensionFlag) {
                if (audioObjectType == 22) {
                    bitArray.skipBits(16);
                }
                if (audioObjectType == 23) {
                    bitArray.skipBits(1);
                    return;
                }
                bitArray.skipBits(3);
                bitArray.skipBits(1);
                return;
            }
            return;
        }
        throw new UnsupportedOperationException();
    }
}
