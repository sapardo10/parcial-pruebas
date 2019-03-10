package com.google.android.exoplayer2.extractor;

import com.google.android.exoplayer2.util.MimeTypes;

public final class MpegAudioHeader {
    private static final int[] BITRATE_V1_L1 = new int[]{32000, 64000, 96000, 128000, 160000, 192000, 224000, 256000, 288000, 320000, 352000, 384000, 416000, 448000};
    private static final int[] BITRATE_V1_L2 = new int[]{32000, 48000, 56000, 64000, 80000, 96000, 112000, 128000, 160000, 192000, 224000, 256000, 320000, 384000};
    private static final int[] BITRATE_V1_L3 = new int[]{32000, 40000, 48000, 56000, 64000, 80000, 96000, 112000, 128000, 160000, 192000, 224000, 256000, 320000};
    private static final int[] BITRATE_V2 = new int[]{8000, 16000, 24000, 32000, 40000, 48000, 56000, 64000, 80000, 96000, 112000, 128000, 144000, 160000};
    private static final int[] BITRATE_V2_L1 = new int[]{32000, 48000, 56000, 64000, 80000, 96000, 112000, 128000, 144000, 160000, 176000, 192000, 224000, 256000};
    public static final int MAX_FRAME_SIZE_BYTES = 4096;
    private static final String[] MIME_TYPE_BY_LAYER = new String[]{MimeTypes.AUDIO_MPEG_L1, MimeTypes.AUDIO_MPEG_L2, MimeTypes.AUDIO_MPEG};
    private static final int[] SAMPLING_RATE_V1 = new int[]{44100, 48000, 32000};
    public int bitrate;
    public int channels;
    public int frameSize;
    public String mimeType;
    public int sampleRate;
    public int samplesPerFrame;
    public int version;

    public static int getFrameSize(int header) {
        if ((header & -2097152) != -2097152) {
            return -1;
        }
        int version = (header >>> 19) & 3;
        if (version == 1) {
            return -1;
        }
        int layer = (header >>> 17) & 3;
        if (layer == 0) {
            return -1;
        }
        int bitrateIndex = (header >>> 12) & 15;
        if (bitrateIndex != 0) {
            if (bitrateIndex != 15) {
                int samplingRateIndex = (header >>> 10) & 3;
                if (samplingRateIndex == 3) {
                    return -1;
                }
                int samplingRate = SAMPLING_RATE_V1[samplingRateIndex];
                if (version == 2) {
                    samplingRate /= 2;
                } else if (version == 0) {
                    samplingRate /= 4;
                }
                int padding = (header >>> 9) & 1;
                if (layer == 3) {
                    return ((((version == 3 ? BITRATE_V1_L1[bitrateIndex - 1] : BITRATE_V2_L1[bitrateIndex - 1]) * 12) / samplingRate) + padding) * 4;
                }
                int bitrate;
                if (version == 3) {
                    bitrate = layer == 2 ? BITRATE_V1_L2[bitrateIndex - 1] : BITRATE_V1_L3[bitrateIndex - 1];
                } else {
                    bitrate = BITRATE_V2[bitrateIndex - 1];
                }
                if (version == 3) {
                    return ((bitrate * 144) / samplingRate) + padding;
                }
                return (((layer == 1 ? 72 : 144) * bitrate) / samplingRate) + padding;
            }
        }
        return -1;
    }

    public static boolean populateHeader(int headerData, MpegAudioHeader header) {
        if ((headerData & -2097152) != -2097152) {
            return false;
        }
        int version = (headerData >>> 19) & 3;
        if (version == 1) {
            return false;
        }
        int layer = (headerData >>> 17) & 3;
        if (layer == 0) {
            return false;
        }
        int bitrateIndex = (headerData >>> 12) & 15;
        if (bitrateIndex != 0) {
            if (bitrateIndex != 15) {
                int samplingRateIndex = (headerData >>> 10) & 3;
                if (samplingRateIndex == 3) {
                    return false;
                }
                int frameSize;
                int samplesPerFrame;
                int sampleRate = SAMPLING_RATE_V1[samplingRateIndex];
                if (version == 2) {
                    sampleRate /= 2;
                } else if (version == 0) {
                    sampleRate /= 4;
                }
                int padding = (headerData >>> 9) & 1;
                if (layer == 3) {
                    frameSize = ((((version == 3 ? BITRATE_V1_L1[bitrateIndex - 1] : BITRATE_V2_L1[bitrateIndex - 1]) * 12) / sampleRate) + padding) * 4;
                    samplesPerFrame = 384;
                } else if (version == 3) {
                    samplesPerFrame = 1152;
                    frameSize = (((layer == 2 ? BITRATE_V1_L2[bitrateIndex - 1] : BITRATE_V1_L3[bitrateIndex - 1]) * 144) / sampleRate) + padding;
                } else {
                    samplesPerFrame = layer == 1 ? 576 : 1152;
                    frameSize = (((layer == 1 ? 72 : 144) * BITRATE_V2[bitrateIndex - 1]) / sampleRate) + padding;
                }
                header.setValues(version, MIME_TYPE_BY_LAYER[3 - layer], frameSize, sampleRate, ((headerData >> 6) & 3) == 3 ? 1 : 2, ((frameSize * 8) * sampleRate) / samplesPerFrame, samplesPerFrame);
                return true;
            }
        }
        return false;
    }

    private void setValues(int version, String mimeType, int frameSize, int sampleRate, int channels, int bitrate, int samplesPerFrame) {
        this.version = version;
        this.mimeType = mimeType;
        this.frameSize = frameSize;
        this.sampleRate = sampleRate;
        this.channels = channels;
        this.bitrate = bitrate;
        this.samplesPerFrame = samplesPerFrame;
    }
}
