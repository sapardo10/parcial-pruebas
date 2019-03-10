package com.google.android.exoplayer2.util;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import java.util.ArrayList;

public final class MimeTypes {
    public static final String APPLICATION_CAMERA_MOTION = "application/x-camera-motion";
    public static final String APPLICATION_CEA608 = "application/cea-608";
    public static final String APPLICATION_CEA708 = "application/cea-708";
    public static final String APPLICATION_DVBSUBS = "application/dvbsubs";
    public static final String APPLICATION_EMSG = "application/x-emsg";
    public static final String APPLICATION_EXIF = "application/x-exif";
    public static final String APPLICATION_ID3 = "application/id3";
    public static final String APPLICATION_M3U8 = "application/x-mpegURL";
    public static final String APPLICATION_MP4 = "application/mp4";
    public static final String APPLICATION_MP4CEA608 = "application/x-mp4-cea-608";
    public static final String APPLICATION_MP4VTT = "application/x-mp4-vtt";
    public static final String APPLICATION_MPD = "application/dash+xml";
    public static final String APPLICATION_PGS = "application/pgs";
    public static final String APPLICATION_RAWCC = "application/x-rawcc";
    public static final String APPLICATION_SCTE35 = "application/x-scte35";
    public static final String APPLICATION_SS = "application/vnd.ms-sstr+xml";
    public static final String APPLICATION_SUBRIP = "application/x-subrip";
    public static final String APPLICATION_TTML = "application/ttml+xml";
    public static final String APPLICATION_TX3G = "application/x-quicktime-tx3g";
    public static final String APPLICATION_VOBSUB = "application/vobsub";
    public static final String APPLICATION_WEBM = "application/webm";
    public static final String AUDIO_AAC = "audio/mp4a-latm";
    public static final String AUDIO_AC3 = "audio/ac3";
    public static final String AUDIO_ALAC = "audio/alac";
    public static final String AUDIO_ALAW = "audio/g711-alaw";
    public static final String AUDIO_AMR_NB = "audio/3gpp";
    public static final String AUDIO_AMR_WB = "audio/amr-wb";
    public static final String AUDIO_DTS = "audio/vnd.dts";
    public static final String AUDIO_DTS_EXPRESS = "audio/vnd.dts.hd;profile=lbr";
    public static final String AUDIO_DTS_HD = "audio/vnd.dts.hd";
    public static final String AUDIO_E_AC3 = "audio/eac3";
    public static final String AUDIO_E_AC3_JOC = "audio/eac3-joc";
    public static final String AUDIO_FLAC = "audio/flac";
    public static final String AUDIO_MLAW = "audio/g711-mlaw";
    public static final String AUDIO_MP4 = "audio/mp4";
    public static final String AUDIO_MPEG = "audio/mpeg";
    public static final String AUDIO_MPEG_L1 = "audio/mpeg-L1";
    public static final String AUDIO_MPEG_L2 = "audio/mpeg-L2";
    public static final String AUDIO_MSGSM = "audio/gsm";
    public static final String AUDIO_OPUS = "audio/opus";
    public static final String AUDIO_RAW = "audio/raw";
    public static final String AUDIO_TRUEHD = "audio/true-hd";
    public static final String AUDIO_UNKNOWN = "audio/x-unknown";
    public static final String AUDIO_VORBIS = "audio/vorbis";
    public static final String AUDIO_WEBM = "audio/webm";
    public static final String BASE_TYPE_APPLICATION = "application";
    public static final String BASE_TYPE_AUDIO = "audio";
    public static final String BASE_TYPE_TEXT = "text";
    public static final String BASE_TYPE_VIDEO = "video";
    public static final String TEXT_SSA = "text/x-ssa";
    public static final String TEXT_VTT = "text/vtt";
    public static final String VIDEO_H263 = "video/3gpp";
    public static final String VIDEO_H264 = "video/avc";
    public static final String VIDEO_H265 = "video/hevc";
    public static final String VIDEO_MP4 = "video/mp4";
    public static final String VIDEO_MP4V = "video/mp4v-es";
    public static final String VIDEO_MPEG = "video/mpeg";
    public static final String VIDEO_MPEG2 = "video/mpeg2";
    public static final String VIDEO_UNKNOWN = "video/x-unknown";
    public static final String VIDEO_VC1 = "video/wvc1";
    public static final String VIDEO_VP8 = "video/x-vnd.on2.vp8";
    public static final String VIDEO_VP9 = "video/x-vnd.on2.vp9";
    public static final String VIDEO_WEBM = "video/webm";
    private static final ArrayList<CustomMimeType> customMimeTypes = new ArrayList();

    private static final class CustomMimeType {
        public final String codecPrefix;
        public final String mimeType;
        public final int trackType;

        public CustomMimeType(String mimeType, String codecPrefix, int trackType) {
            this.mimeType = mimeType;
            this.codecPrefix = codecPrefix;
            this.trackType = trackType;
        }
    }

    public static void registerCustomMimeType(String mimeType, String codecPrefix, int trackType) {
        CustomMimeType customMimeType = new CustomMimeType(mimeType, codecPrefix, trackType);
        int customMimeTypeCount = customMimeTypes.size();
        for (int i = 0; i < customMimeTypeCount; i++) {
            if (mimeType.equals(((CustomMimeType) customMimeTypes.get(i)).mimeType)) {
                customMimeTypes.remove(i);
                break;
            }
        }
        customMimeTypes.add(customMimeType);
    }

    public static boolean isAudio(@Nullable String mimeType) {
        return BASE_TYPE_AUDIO.equals(getTopLevelType(mimeType));
    }

    public static boolean isVideo(@Nullable String mimeType) {
        return BASE_TYPE_VIDEO.equals(getTopLevelType(mimeType));
    }

    public static boolean isText(@Nullable String mimeType) {
        return "text".equals(getTopLevelType(mimeType));
    }

    public static boolean isApplication(@Nullable String mimeType) {
        return BASE_TYPE_APPLICATION.equals(getTopLevelType(mimeType));
    }

    @Nullable
    public static String getVideoMediaMimeType(@Nullable String codecs) {
        if (codecs == null) {
            return null;
        }
        for (String codec : Util.splitCodecs(codecs)) {
            String mimeType = getMediaMimeType(codec);
            if (mimeType != null && isVideo(mimeType)) {
                return mimeType;
            }
        }
        return null;
    }

    @Nullable
    public static String getAudioMediaMimeType(@Nullable String codecs) {
        if (codecs == null) {
            return null;
        }
        for (String codec : Util.splitCodecs(codecs)) {
            String mimeType = getMediaMimeType(codec);
            if (mimeType != null && isAudio(mimeType)) {
                return mimeType;
            }
        }
        return null;
    }

    @Nullable
    public static String getMediaMimeType(@Nullable String codec) {
        if (codec == null) {
            return null;
        }
        codec = Util.toLowerInvariant(codec.trim());
        if (!codec.startsWith("avc1")) {
            if (!codec.startsWith("avc3")) {
                if (!codec.startsWith("hev1")) {
                    if (!codec.startsWith("hvc1")) {
                        if (!codec.startsWith("vp9")) {
                            if (!codec.startsWith("vp09")) {
                                if (!codec.startsWith("vp8")) {
                                    if (!codec.startsWith("vp08")) {
                                        if (codec.startsWith("mp4a")) {
                                            String mimeType = null;
                                            if (codec.startsWith("mp4a.")) {
                                                String objectTypeString = codec.substring(5);
                                                if (objectTypeString.length() >= 2) {
                                                    try {
                                                        mimeType = getMimeTypeFromMp4ObjectType(Integer.parseInt(Util.toUpperInvariant(objectTypeString.substring(0, 2)), 16));
                                                    } catch (NumberFormatException e) {
                                                    }
                                                }
                                            }
                                            return mimeType == null ? AUDIO_AAC : mimeType;
                                        }
                                        if (!codec.startsWith("ac-3")) {
                                            if (!codec.startsWith("dac3")) {
                                                if (!codec.startsWith("ec-3")) {
                                                    if (!codec.startsWith("dec3")) {
                                                        if (codec.startsWith("ec+3")) {
                                                            return AUDIO_E_AC3_JOC;
                                                        }
                                                        if (!codec.startsWith("dtsc")) {
                                                            if (!codec.startsWith("dtse")) {
                                                                if (!codec.startsWith("dtsh")) {
                                                                    if (!codec.startsWith("dtsl")) {
                                                                        if (codec.startsWith("opus")) {
                                                                            return AUDIO_OPUS;
                                                                        }
                                                                        if (codec.startsWith("vorbis")) {
                                                                            return AUDIO_VORBIS;
                                                                        }
                                                                        if (codec.startsWith("flac")) {
                                                                            return AUDIO_FLAC;
                                                                        }
                                                                        return getCustomMimeTypeForCodec(codec);
                                                                    }
                                                                }
                                                                return AUDIO_DTS_HD;
                                                            }
                                                        }
                                                        return AUDIO_DTS;
                                                    }
                                                }
                                                return AUDIO_E_AC3;
                                            }
                                        }
                                        return AUDIO_AC3;
                                    }
                                }
                                return VIDEO_VP8;
                            }
                        }
                        return VIDEO_VP9;
                    }
                }
                return VIDEO_H265;
            }
        }
        return VIDEO_H264;
    }

    @Nullable
    public static String getMimeTypeFromMp4ObjectType(int objectType) {
        if (objectType == 35) {
            return VIDEO_H265;
        }
        if (objectType != 64) {
            if (objectType == 163) {
                return VIDEO_VC1;
            }
            if (objectType == 177) {
                return VIDEO_VP9;
            }
            switch (objectType) {
                case 32:
                    return VIDEO_MP4V;
                case 33:
                    return VIDEO_H264;
                default:
                    switch (objectType) {
                        case 96:
                        case 97:
                        case 98:
                        case 99:
                        case 100:
                        case 101:
                            return VIDEO_MPEG2;
                        case 102:
                        case 103:
                        case 104:
                            break;
                        case 105:
                        case 107:
                            return AUDIO_MPEG;
                        case 106:
                            return VIDEO_MPEG;
                        default:
                            switch (objectType) {
                                case 165:
                                    return AUDIO_AC3;
                                case 166:
                                    return AUDIO_E_AC3;
                                default:
                                    switch (objectType) {
                                        case 169:
                                        case 172:
                                            return AUDIO_DTS;
                                        case 170:
                                        case 171:
                                            return AUDIO_DTS_HD;
                                        case 173:
                                            return AUDIO_OPUS;
                                        default:
                                            return null;
                                    }
                            }
                    }
            }
        }
        return AUDIO_AAC;
    }

    public static int getTrackType(@Nullable String mimeType) {
        if (TextUtils.isEmpty(mimeType)) {
            return -1;
        }
        if (isAudio(mimeType)) {
            return 1;
        }
        if (isVideo(mimeType)) {
            return 2;
        }
        if (!isText(mimeType) && !APPLICATION_CEA608.equals(mimeType)) {
            if (!APPLICATION_CEA708.equals(mimeType) && !APPLICATION_MP4CEA608.equals(mimeType)) {
                if (!APPLICATION_SUBRIP.equals(mimeType) && !APPLICATION_TTML.equals(mimeType)) {
                    if (!APPLICATION_TX3G.equals(mimeType) && !APPLICATION_MP4VTT.equals(mimeType)) {
                        if (!APPLICATION_RAWCC.equals(mimeType) && !APPLICATION_VOBSUB.equals(mimeType)) {
                            if (!APPLICATION_PGS.equals(mimeType)) {
                                if (!APPLICATION_DVBSUBS.equals(mimeType)) {
                                    if (!APPLICATION_ID3.equals(mimeType)) {
                                        if (!APPLICATION_EMSG.equals(mimeType)) {
                                            if (!APPLICATION_SCTE35.equals(mimeType)) {
                                                if (APPLICATION_CAMERA_MOTION.equals(mimeType)) {
                                                    return 5;
                                                }
                                                return getTrackTypeForCustomMimeType(mimeType);
                                            }
                                        }
                                    }
                                    return 4;
                                }
                            }
                        }
                    }
                }
            }
        }
        return 3;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int getEncoding(java.lang.String r3) {
        /*
        r0 = r3.hashCode();
        r1 = 5;
        r2 = 0;
        switch(r0) {
            case -2123537834: goto L_0x003c;
            case -1095064472: goto L_0x0032;
            case 187078296: goto L_0x0028;
            case 1504578661: goto L_0x001e;
            case 1505942594: goto L_0x0014;
            case 1556697186: goto L_0x000a;
            default: goto L_0x0009;
        };
    L_0x0009:
        goto L_0x0046;
    L_0x000a:
        r0 = "audio/true-hd";
        r0 = r3.equals(r0);
        if (r0 == 0) goto L_0x0009;
    L_0x0012:
        r0 = 5;
        goto L_0x0047;
    L_0x0014:
        r0 = "audio/vnd.dts.hd";
        r0 = r3.equals(r0);
        if (r0 == 0) goto L_0x0009;
    L_0x001c:
        r0 = 4;
        goto L_0x0047;
    L_0x001e:
        r0 = "audio/eac3";
        r0 = r3.equals(r0);
        if (r0 == 0) goto L_0x0009;
    L_0x0026:
        r0 = 1;
        goto L_0x0047;
    L_0x0028:
        r0 = "audio/ac3";
        r0 = r3.equals(r0);
        if (r0 == 0) goto L_0x0009;
    L_0x0030:
        r0 = 0;
        goto L_0x0047;
    L_0x0032:
        r0 = "audio/vnd.dts";
        r0 = r3.equals(r0);
        if (r0 == 0) goto L_0x0009;
    L_0x003a:
        r0 = 3;
        goto L_0x0047;
    L_0x003c:
        r0 = "audio/eac3-joc";
        r0 = r3.equals(r0);
        if (r0 == 0) goto L_0x0009;
    L_0x0044:
        r0 = 2;
        goto L_0x0047;
    L_0x0046:
        r0 = -1;
    L_0x0047:
        switch(r0) {
            case 0: goto L_0x0055;
            case 1: goto L_0x0053;
            case 2: goto L_0x0053;
            case 3: goto L_0x0051;
            case 4: goto L_0x004e;
            case 5: goto L_0x004b;
            default: goto L_0x004a;
        };
    L_0x004a:
        return r2;
    L_0x004b:
        r0 = 14;
        return r0;
    L_0x004e:
        r0 = 8;
        return r0;
    L_0x0051:
        r0 = 7;
        return r0;
    L_0x0053:
        r0 = 6;
        return r0;
    L_0x0055:
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.util.MimeTypes.getEncoding(java.lang.String):int");
    }

    public static int getTrackTypeOfCodec(String codec) {
        return getTrackType(getMediaMimeType(codec));
    }

    @Nullable
    private static String getTopLevelType(@Nullable String mimeType) {
        if (mimeType == null) {
            return null;
        }
        int indexOfSlash = mimeType.indexOf(47);
        if (indexOfSlash != -1) {
            return mimeType.substring(0, indexOfSlash);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Invalid mime type: ");
        stringBuilder.append(mimeType);
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    @Nullable
    private static String getCustomMimeTypeForCodec(String codec) {
        int customMimeTypeCount = customMimeTypes.size();
        for (int i = 0; i < customMimeTypeCount; i++) {
            CustomMimeType customMimeType = (CustomMimeType) customMimeTypes.get(i);
            if (codec.startsWith(customMimeType.codecPrefix)) {
                return customMimeType.mimeType;
            }
        }
        return null;
    }

    private static int getTrackTypeForCustomMimeType(String mimeType) {
        int customMimeTypeCount = customMimeTypes.size();
        for (int i = 0; i < customMimeTypeCount; i++) {
            CustomMimeType customMimeType = (CustomMimeType) customMimeTypes.get(i);
            if (mimeType.equals(customMimeType.mimeType)) {
                return customMimeType.trackType;
            }
        }
        return -1;
    }

    private MimeTypes() {
    }
}
