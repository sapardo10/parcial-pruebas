package com.google.android.exoplayer2.mediacodec;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.media.MediaCodecInfo;
import android.media.MediaCodecInfo.CodecCapabilities;
import android.media.MediaCodecInfo.CodecProfileLevel;
import android.media.MediaCodecList;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Pair;
import android.util.SparseIntArray;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressLint({"InlinedApi"})
@TargetApi(16)
public final class MediaCodecUtil {
    private static final SparseIntArray AVC_LEVEL_NUMBER_TO_CONST = new SparseIntArray();
    private static final SparseIntArray AVC_PROFILE_NUMBER_TO_CONST = new SparseIntArray();
    private static final String CODEC_ID_AVC1 = "avc1";
    private static final String CODEC_ID_AVC2 = "avc2";
    private static final String CODEC_ID_HEV1 = "hev1";
    private static final String CODEC_ID_HVC1 = "hvc1";
    private static final String CODEC_ID_MP4A = "mp4a";
    private static final Map<String, Integer> HEVC_CODEC_STRING_TO_PROFILE_LEVEL = new HashMap();
    private static final SparseIntArray MP4A_AUDIO_OBJECT_TYPE_TO_PROFILE = new SparseIntArray();
    private static final Pattern PROFILE_PATTERN = Pattern.compile("^\\D?(\\d+)$");
    private static final RawAudioCodecComparator RAW_AUDIO_CODEC_COMPARATOR = new RawAudioCodecComparator();
    private static final String TAG = "MediaCodecUtil";
    private static final HashMap<CodecKey, List<MediaCodecInfo>> decoderInfosCache = new HashMap();
    private static int maxH264DecodableFrameSize = -1;

    private static final class CodecKey {
        public final String mimeType;
        public final boolean secure;

        public CodecKey(String mimeType, boolean secure) {
            this.mimeType = mimeType;
            this.secure = secure;
        }

        public int hashCode() {
            int result = 1 * 31;
            String str = this.mimeType;
            return ((result + (str == null ? 0 : str.hashCode())) * 31) + (this.secure ? 1231 : 1237);
        }

        public boolean equals(@Nullable Object obj) {
            boolean z = true;
            if (this == obj) {
                return true;
            }
            if (obj != null) {
                if (obj.getClass() == CodecKey.class) {
                    CodecKey other = (CodecKey) obj;
                    if (!TextUtils.equals(this.mimeType, other.mimeType) || this.secure != other.secure) {
                        z = false;
                    }
                    return z;
                }
            }
            return false;
        }
    }

    public static class DecoderQueryException extends Exception {
        private DecoderQueryException(Throwable cause) {
            super("Failed to query underlying media codecs", cause);
        }
    }

    private interface MediaCodecListCompat {
        int getCodecCount();

        MediaCodecInfo getCodecInfoAt(int i);

        boolean isSecurePlaybackSupported(String str, CodecCapabilities codecCapabilities);

        boolean secureDecodersExplicit();
    }

    private static final class RawAudioCodecComparator implements Comparator<MediaCodecInfo> {
        private RawAudioCodecComparator() {
        }

        public int compare(MediaCodecInfo a, MediaCodecInfo b) {
            return scoreMediaCodecInfo(a) - scoreMediaCodecInfo(b);
        }

        private static int scoreMediaCodecInfo(MediaCodecInfo mediaCodecInfo) {
            String name = mediaCodecInfo.name;
            if (!name.startsWith("OMX.google")) {
                if (!name.startsWith("c2.android")) {
                    if (Util.SDK_INT >= 26 || !name.equals("OMX.MTK.AUDIO.DECODER.RAW")) {
                        return 0;
                    }
                    return 1;
                }
            }
            return -1;
        }
    }

    private static final class MediaCodecListCompatV16 implements MediaCodecListCompat {
        private MediaCodecListCompatV16() {
        }

        public int getCodecCount() {
            return MediaCodecList.getCodecCount();
        }

        public MediaCodecInfo getCodecInfoAt(int index) {
            return MediaCodecList.getCodecInfoAt(index);
        }

        public boolean secureDecodersExplicit() {
            return false;
        }

        public boolean isSecurePlaybackSupported(String mimeType, CodecCapabilities capabilities) {
            return MimeTypes.VIDEO_H264.equals(mimeType);
        }
    }

    @TargetApi(21)
    private static final class MediaCodecListCompatV21 implements MediaCodecListCompat {
        private final int codecKind;
        private MediaCodecInfo[] mediaCodecInfos;

        public MediaCodecListCompatV21(boolean includeSecure) {
            this.codecKind = includeSecure;
        }

        public int getCodecCount() {
            ensureMediaCodecInfosInitialized();
            return this.mediaCodecInfos.length;
        }

        public MediaCodecInfo getCodecInfoAt(int index) {
            ensureMediaCodecInfosInitialized();
            return this.mediaCodecInfos[index];
        }

        public boolean secureDecodersExplicit() {
            return true;
        }

        public boolean isSecurePlaybackSupported(String mimeType, CodecCapabilities capabilities) {
            return capabilities.isFeatureSupported("secure-playback");
        }

        private void ensureMediaCodecInfosInitialized() {
            if (this.mediaCodecInfos == null) {
                this.mediaCodecInfos = new MediaCodecList(this.codecKind).getCodecInfos();
            }
        }
    }

    private static java.util.ArrayList<com.google.android.exoplayer2.mediacodec.MediaCodecInfo> getDecoderInfosInternal(com.google.android.exoplayer2.mediacodec.MediaCodecUtil.CodecKey r19, com.google.android.exoplayer2.mediacodec.MediaCodecUtil.MediaCodecListCompat r20, java.lang.String r21) throws com.google.android.exoplayer2.mediacodec.MediaCodecUtil.DecoderQueryException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:68:0x011b in {21, 24, 25, 30, 33, 35, 37, 42, 43, 44, 46, 52, 54, 55, 56, 57, 58, 59, 61, 63, 65, 67} preds:[]
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
        r0 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0111 }
        r0.<init>();	 Catch:{ Exception -> 0x0111 }
        r3 = r0;	 Catch:{ Exception -> 0x0111 }
        r0 = r1.mimeType;	 Catch:{ Exception -> 0x0111 }
        r4 = r0;	 Catch:{ Exception -> 0x0111 }
        r0 = r20.getCodecCount();	 Catch:{ Exception -> 0x0111 }
        r5 = r0;	 Catch:{ Exception -> 0x0111 }
        r0 = r20.secureDecodersExplicit();	 Catch:{ Exception -> 0x0111 }
        r6 = r0;	 Catch:{ Exception -> 0x0111 }
        r0 = 0;	 Catch:{ Exception -> 0x0111 }
        r7 = r0;	 Catch:{ Exception -> 0x0111 }
    L_0x0019:
        if (r7 >= r5) goto L_0x010c;	 Catch:{ Exception -> 0x0111 }
    L_0x001b:
        r0 = r2.getCodecInfoAt(r7);	 Catch:{ Exception -> 0x0111 }
        r8 = r0;	 Catch:{ Exception -> 0x0111 }
        r0 = r8.getName();	 Catch:{ Exception -> 0x0111 }
        r9 = r0;
        r10 = r21;
        r0 = isCodecUsableDecoder(r8, r9, r6, r10);	 Catch:{ Exception -> 0x010a }
        if (r0 == 0) goto L_0x00fe;	 Catch:{ Exception -> 0x010a }
    L_0x002d:
        r11 = r8.getSupportedTypes();	 Catch:{ Exception -> 0x010a }
        r12 = r11.length;	 Catch:{ Exception -> 0x010a }
        r14 = 0;	 Catch:{ Exception -> 0x010a }
    L_0x0033:
        if (r14 >= r12) goto L_0x00fb;	 Catch:{ Exception -> 0x010a }
    L_0x0035:
        r0 = r11[r14];	 Catch:{ Exception -> 0x010a }
        r15 = r0;	 Catch:{ Exception -> 0x010a }
        r0 = r15.equalsIgnoreCase(r4);	 Catch:{ Exception -> 0x010a }
        if (r0 == 0) goto L_0x00ef;
    L_0x003e:
        r0 = r8.getCapabilitiesForType(r15);	 Catch:{ Exception -> 0x009e }
        r16 = r2.isSecurePlaybackSupported(r4, r0);	 Catch:{ Exception -> 0x009e }
        r17 = r16;	 Catch:{ Exception -> 0x009e }
        r16 = codecNeedsDisableAdaptationWorkaround(r9);	 Catch:{ Exception -> 0x009e }
        r18 = r16;
        if (r6 == 0) goto L_0x005c;
    L_0x0050:
        r13 = r1.secure;	 Catch:{ Exception -> 0x0058 }
        r2 = r17;
        if (r13 == r2) goto L_0x0057;
    L_0x0056:
        goto L_0x005e;
    L_0x0057:
        goto L_0x0065;
    L_0x0058:
        r0 = move-exception;
        r16 = r5;
        goto L_0x00a1;
    L_0x005c:
        r2 = r17;
    L_0x005e:
        if (r6 != 0) goto L_0x0079;
    L_0x0060:
        r13 = r1.secure;	 Catch:{ Exception -> 0x0074 }
        if (r13 != 0) goto L_0x0079;
    L_0x0064:
        goto L_0x0057;
    L_0x0065:
        r16 = r5;
        r13 = r18;
        r1 = 0;
        r5 = com.google.android.exoplayer2.mediacodec.MediaCodecInfo.newInstance(r9, r4, r0, r13, r1);	 Catch:{ Exception -> 0x0072 }
        r3.add(r5);	 Catch:{ Exception -> 0x0072 }
        goto L_0x009d;	 Catch:{ Exception -> 0x0072 }
    L_0x0072:
        r0 = move-exception;	 Catch:{ Exception -> 0x0072 }
        goto L_0x00a1;	 Catch:{ Exception -> 0x0072 }
    L_0x0074:
        r0 = move-exception;	 Catch:{ Exception -> 0x0072 }
        r16 = r5;	 Catch:{ Exception -> 0x0072 }
        r1 = 0;	 Catch:{ Exception -> 0x0072 }
        goto L_0x00a1;	 Catch:{ Exception -> 0x0072 }
    L_0x0079:
        r16 = r5;	 Catch:{ Exception -> 0x0072 }
        r13 = r18;	 Catch:{ Exception -> 0x0072 }
        r1 = 0;	 Catch:{ Exception -> 0x0072 }
        if (r6 != 0) goto L_0x009c;	 Catch:{ Exception -> 0x0072 }
    L_0x0080:
        if (r2 == 0) goto L_0x009c;	 Catch:{ Exception -> 0x0072 }
    L_0x0082:
        r5 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0072 }
        r5.<init>();	 Catch:{ Exception -> 0x0072 }
        r5.append(r9);	 Catch:{ Exception -> 0x0072 }
        r1 = ".secure";	 Catch:{ Exception -> 0x0072 }
        r5.append(r1);	 Catch:{ Exception -> 0x0072 }
        r1 = r5.toString();	 Catch:{ Exception -> 0x0072 }
        r5 = 1;	 Catch:{ Exception -> 0x0072 }
        r1 = com.google.android.exoplayer2.mediacodec.MediaCodecInfo.newInstance(r1, r4, r0, r13, r5);	 Catch:{ Exception -> 0x0072 }
        r3.add(r1);	 Catch:{ Exception -> 0x0072 }
        return r3;
    L_0x009d:
        goto L_0x00f1;
    L_0x009e:
        r0 = move-exception;
        r16 = r5;
    L_0x00a1:
        r1 = com.google.android.exoplayer2.util.Util.SDK_INT;	 Catch:{ Exception -> 0x010a }
        r2 = 23;	 Catch:{ Exception -> 0x010a }
        if (r1 > r2) goto L_0x00c9;	 Catch:{ Exception -> 0x010a }
    L_0x00a7:
        r1 = r3.isEmpty();	 Catch:{ Exception -> 0x010a }
        if (r1 != 0) goto L_0x00c9;	 Catch:{ Exception -> 0x010a }
    L_0x00ad:
        r1 = "MediaCodecUtil";	 Catch:{ Exception -> 0x010a }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x010a }
        r2.<init>();	 Catch:{ Exception -> 0x010a }
        r5 = "Skipping codec ";	 Catch:{ Exception -> 0x010a }
        r2.append(r5);	 Catch:{ Exception -> 0x010a }
        r2.append(r9);	 Catch:{ Exception -> 0x010a }
        r5 = " (failed to query capabilities)";	 Catch:{ Exception -> 0x010a }
        r2.append(r5);	 Catch:{ Exception -> 0x010a }
        r2 = r2.toString();	 Catch:{ Exception -> 0x010a }
        com.google.android.exoplayer2.util.Log.m6e(r1, r2);	 Catch:{ Exception -> 0x010a }
        goto L_0x00f1;	 Catch:{ Exception -> 0x010a }
        r1 = "MediaCodecUtil";	 Catch:{ Exception -> 0x010a }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x010a }
        r2.<init>();	 Catch:{ Exception -> 0x010a }
        r5 = "Failed to query codec ";	 Catch:{ Exception -> 0x010a }
        r2.append(r5);	 Catch:{ Exception -> 0x010a }
        r2.append(r9);	 Catch:{ Exception -> 0x010a }
        r5 = " (";	 Catch:{ Exception -> 0x010a }
        r2.append(r5);	 Catch:{ Exception -> 0x010a }
        r2.append(r15);	 Catch:{ Exception -> 0x010a }
        r5 = ")";	 Catch:{ Exception -> 0x010a }
        r2.append(r5);	 Catch:{ Exception -> 0x010a }
        r2 = r2.toString();	 Catch:{ Exception -> 0x010a }
        com.google.android.exoplayer2.util.Log.m6e(r1, r2);	 Catch:{ Exception -> 0x010a }
        throw r0;	 Catch:{ Exception -> 0x010a }
    L_0x00ef:
        r16 = r5;
    L_0x00f1:
        r14 = r14 + 1;
        r5 = r16;
        r1 = r19;
        r2 = r20;
        goto L_0x0033;
    L_0x00fb:
        r16 = r5;
        goto L_0x0100;
    L_0x00fe:
        r16 = r5;
    L_0x0100:
        r7 = r7 + 1;
        r5 = r16;
        r1 = r19;
        r2 = r20;
        goto L_0x0019;
    L_0x010a:
        r0 = move-exception;
        goto L_0x0114;
    L_0x010c:
        r10 = r21;
        r16 = r5;
        return r3;
    L_0x0111:
        r0 = move-exception;
        r10 = r21;
    L_0x0114:
        r1 = new com.google.android.exoplayer2.mediacodec.MediaCodecUtil$DecoderQueryException;
        r2 = 0;
        r1.<init>(r0);
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.mediacodec.MediaCodecUtil.getDecoderInfosInternal(com.google.android.exoplayer2.mediacodec.MediaCodecUtil$CodecKey, com.google.android.exoplayer2.mediacodec.MediaCodecUtil$MediaCodecListCompat, java.lang.String):java.util.ArrayList<com.google.android.exoplayer2.mediacodec.MediaCodecInfo>");
    }

    static {
        AVC_PROFILE_NUMBER_TO_CONST.put(66, 1);
        AVC_PROFILE_NUMBER_TO_CONST.put(77, 2);
        AVC_PROFILE_NUMBER_TO_CONST.put(88, 4);
        AVC_PROFILE_NUMBER_TO_CONST.put(100, 8);
        AVC_PROFILE_NUMBER_TO_CONST.put(110, 16);
        AVC_PROFILE_NUMBER_TO_CONST.put(122, 32);
        AVC_PROFILE_NUMBER_TO_CONST.put(244, 64);
        AVC_LEVEL_NUMBER_TO_CONST.put(10, 1);
        AVC_LEVEL_NUMBER_TO_CONST.put(11, 4);
        AVC_LEVEL_NUMBER_TO_CONST.put(12, 8);
        AVC_LEVEL_NUMBER_TO_CONST.put(13, 16);
        AVC_LEVEL_NUMBER_TO_CONST.put(20, 32);
        AVC_LEVEL_NUMBER_TO_CONST.put(21, 64);
        AVC_LEVEL_NUMBER_TO_CONST.put(22, 128);
        AVC_LEVEL_NUMBER_TO_CONST.put(30, 256);
        AVC_LEVEL_NUMBER_TO_CONST.put(31, 512);
        AVC_LEVEL_NUMBER_TO_CONST.put(32, 1024);
        AVC_LEVEL_NUMBER_TO_CONST.put(40, 2048);
        AVC_LEVEL_NUMBER_TO_CONST.put(41, 4096);
        AVC_LEVEL_NUMBER_TO_CONST.put(42, 8192);
        AVC_LEVEL_NUMBER_TO_CONST.put(50, 16384);
        AVC_LEVEL_NUMBER_TO_CONST.put(51, 32768);
        AVC_LEVEL_NUMBER_TO_CONST.put(52, 65536);
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L30", Integer.valueOf(1));
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L60", Integer.valueOf(4));
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L63", Integer.valueOf(16));
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L90", Integer.valueOf(64));
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L93", Integer.valueOf(256));
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L120", Integer.valueOf(1024));
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L123", Integer.valueOf(4096));
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L150", Integer.valueOf(16384));
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L153", Integer.valueOf(65536));
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L156", Integer.valueOf(262144));
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L180", Integer.valueOf(1048576));
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L183", Integer.valueOf(4194304));
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L186", Integer.valueOf(16777216));
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H30", Integer.valueOf(2));
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H60", Integer.valueOf(8));
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H63", Integer.valueOf(32));
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H90", Integer.valueOf(128));
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H93", Integer.valueOf(512));
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H120", Integer.valueOf(2048));
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H123", Integer.valueOf(8192));
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H150", Integer.valueOf(32768));
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H153", Integer.valueOf(131072));
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H156", Integer.valueOf(524288));
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H180", Integer.valueOf(2097152));
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H183", Integer.valueOf(8388608));
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H186", Integer.valueOf(33554432));
        MP4A_AUDIO_OBJECT_TYPE_TO_PROFILE.put(1, 1);
        MP4A_AUDIO_OBJECT_TYPE_TO_PROFILE.put(2, 2);
        MP4A_AUDIO_OBJECT_TYPE_TO_PROFILE.put(3, 3);
        MP4A_AUDIO_OBJECT_TYPE_TO_PROFILE.put(4, 4);
        MP4A_AUDIO_OBJECT_TYPE_TO_PROFILE.put(5, 5);
        MP4A_AUDIO_OBJECT_TYPE_TO_PROFILE.put(6, 6);
        MP4A_AUDIO_OBJECT_TYPE_TO_PROFILE.put(17, 17);
        MP4A_AUDIO_OBJECT_TYPE_TO_PROFILE.put(20, 20);
        MP4A_AUDIO_OBJECT_TYPE_TO_PROFILE.put(23, 23);
        MP4A_AUDIO_OBJECT_TYPE_TO_PROFILE.put(29, 29);
        MP4A_AUDIO_OBJECT_TYPE_TO_PROFILE.put(39, 39);
        MP4A_AUDIO_OBJECT_TYPE_TO_PROFILE.put(42, 42);
    }

    private MediaCodecUtil() {
    }

    public static void warmDecoderInfoCache(String mimeType, boolean secure) {
        try {
            getDecoderInfos(mimeType, secure);
        } catch (DecoderQueryException e) {
            Log.m7e(TAG, "Codec warming failed", e);
        }
    }

    @Nullable
    public static MediaCodecInfo getPassthroughDecoderInfo() throws DecoderQueryException {
        MediaCodecInfo decoderInfo = getDecoderInfo(MimeTypes.AUDIO_RAW, false);
        return decoderInfo == null ? null : MediaCodecInfo.newPassthroughInstance(decoderInfo.name);
    }

    @Nullable
    public static MediaCodecInfo getDecoderInfo(String mimeType, boolean secure) throws DecoderQueryException {
        List<MediaCodecInfo> decoderInfos = getDecoderInfos(mimeType, secure);
        return decoderInfos.isEmpty() ? null : (MediaCodecInfo) decoderInfos.get(0);
    }

    public static synchronized List<MediaCodecInfo> getDecoderInfos(String mimeType, boolean secure) throws DecoderQueryException {
        synchronized (MediaCodecUtil.class) {
            CodecKey key = new CodecKey(mimeType, secure);
            List<MediaCodecInfo> cachedDecoderInfos = (List) decoderInfosCache.get(key);
            if (cachedDecoderInfos != null) {
                return cachedDecoderInfos;
            }
            MediaCodecListCompat mediaCodecList = Util.SDK_INT >= 21 ? new MediaCodecListCompatV21(secure) : new MediaCodecListCompatV16();
            ArrayList<MediaCodecInfo> decoderInfos = getDecoderInfosInternal(key, mediaCodecList, mimeType);
            if (secure && decoderInfos.isEmpty() && 21 <= Util.SDK_INT && Util.SDK_INT <= 23) {
                mediaCodecList = new MediaCodecListCompatV16();
                decoderInfos = getDecoderInfosInternal(key, mediaCodecList, mimeType);
                if (!decoderInfos.isEmpty()) {
                    String str = TAG;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("MediaCodecList API didn't list secure decoder for: ");
                    stringBuilder.append(mimeType);
                    stringBuilder.append(". Assuming: ");
                    stringBuilder.append(((MediaCodecInfo) decoderInfos.get(0)).name);
                    Log.m10w(str, stringBuilder.toString());
                }
            }
            if (MimeTypes.AUDIO_E_AC3_JOC.equals(mimeType)) {
                decoderInfos.addAll(getDecoderInfosInternal(new CodecKey(MimeTypes.AUDIO_E_AC3, key.secure), mediaCodecList, mimeType));
            }
            applyWorkarounds(mimeType, decoderInfos);
            List<MediaCodecInfo> unmodifiableDecoderInfos = Collections.unmodifiableList(decoderInfos);
            decoderInfosCache.put(key, unmodifiableDecoderInfos);
            return unmodifiableDecoderInfos;
        }
    }

    public static int maxH264DecodableFrameSize() throws DecoderQueryException {
        if (maxH264DecodableFrameSize == -1) {
            int result = 0;
            int i = 0;
            MediaCodecInfo decoderInfo = getDecoderInfo(MimeTypes.VIDEO_H264, false);
            if (decoderInfo != null) {
                CodecProfileLevel[] profileLevels = decoderInfo.getProfileLevels();
                int length = profileLevels.length;
                while (i < length) {
                    result = Math.max(avcLevelToMaxFrameSize(profileLevels[i].level), result);
                    i++;
                }
                result = Math.max(result, Util.SDK_INT >= 21 ? 345600 : 172800);
            }
            maxH264DecodableFrameSize = result;
        }
        return maxH264DecodableFrameSize;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @android.support.annotation.Nullable
    public static android.util.Pair<java.lang.Integer, java.lang.Integer> getCodecProfileAndLevel(java.lang.String r6) {
        /*
        r0 = 0;
        if (r6 != 0) goto L_0x0004;
    L_0x0003:
        return r0;
    L_0x0004:
        r1 = "\\.";
        r1 = r6.split(r1);
        r2 = 0;
        r3 = r1[r2];
        r4 = -1;
        r5 = r3.hashCode();
        switch(r5) {
            case 3006243: goto L_0x003d;
            case 3006244: goto L_0x0033;
            case 3199032: goto L_0x002a;
            case 3214780: goto L_0x0020;
            case 3356560: goto L_0x0016;
            default: goto L_0x0015;
        };
    L_0x0015:
        goto L_0x0047;
    L_0x0016:
        r2 = "mp4a";
        r2 = r3.equals(r2);
        if (r2 == 0) goto L_0x0015;
    L_0x001e:
        r2 = 4;
        goto L_0x0048;
    L_0x0020:
        r2 = "hvc1";
        r2 = r3.equals(r2);
        if (r2 == 0) goto L_0x0015;
    L_0x0028:
        r2 = 1;
        goto L_0x0048;
    L_0x002a:
        r5 = "hev1";
        r3 = r3.equals(r5);
        if (r3 == 0) goto L_0x0015;
    L_0x0032:
        goto L_0x0048;
    L_0x0033:
        r2 = "avc2";
        r2 = r3.equals(r2);
        if (r2 == 0) goto L_0x0015;
    L_0x003b:
        r2 = 3;
        goto L_0x0048;
    L_0x003d:
        r2 = "avc1";
        r2 = r3.equals(r2);
        if (r2 == 0) goto L_0x0015;
    L_0x0045:
        r2 = 2;
        goto L_0x0048;
    L_0x0047:
        r2 = -1;
    L_0x0048:
        switch(r2) {
            case 0: goto L_0x0056;
            case 1: goto L_0x0056;
            case 2: goto L_0x0051;
            case 3: goto L_0x0051;
            case 4: goto L_0x004c;
            default: goto L_0x004b;
        };
    L_0x004b:
        return r0;
    L_0x004c:
        r0 = getAacCodecProfileAndLevel(r6, r1);
        return r0;
    L_0x0051:
        r0 = getAvcProfileAndLevel(r6, r1);
        return r0;
    L_0x0056:
        r0 = getHevcProfileAndLevel(r6, r1);
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.mediacodec.MediaCodecUtil.getCodecProfileAndLevel(java.lang.String):android.util.Pair<java.lang.Integer, java.lang.Integer>");
    }

    private static boolean isCodecUsableDecoder(MediaCodecInfo info, String name, boolean secureDecodersExplicit, String requestedMimeType) {
        if (!info.isEncoder()) {
            if (secureDecodersExplicit || !name.endsWith(".secure")) {
                if (Util.SDK_INT < 21) {
                    if (!"CIPAACDecoder".equals(name)) {
                        if (!"CIPMP3Decoder".equals(name)) {
                            if (!"CIPVorbisDecoder".equals(name)) {
                                if (!"CIPAMRNBDecoder".equals(name)) {
                                    if (!"AACDecoder".equals(name)) {
                                        if ("MP3Decoder".equals(name)) {
                                        }
                                    }
                                }
                            }
                        }
                    }
                    return false;
                }
                if (Util.SDK_INT < 18 && "OMX.SEC.MP3.Decoder".equals(name)) {
                    return false;
                }
                if ("OMX.SEC.mp3.dec".equals(name)) {
                    if (!Util.MODEL.startsWith("GT-I9152")) {
                        if (!Util.MODEL.startsWith("GT-I9515")) {
                            if (!Util.MODEL.startsWith("GT-P5220")) {
                                if (!Util.MODEL.startsWith("GT-S7580")) {
                                    if (!Util.MODEL.startsWith("SM-G350")) {
                                        if (!Util.MODEL.startsWith("SM-G386")) {
                                            if (!Util.MODEL.startsWith("SM-T231")) {
                                                if (Util.MODEL.startsWith("SM-T530")) {
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    return false;
                }
                if ("OMX.brcm.audio.mp3.decoder".equals(name)) {
                    if (!Util.MODEL.startsWith("GT-I9152")) {
                        if (!Util.MODEL.startsWith("GT-S7580")) {
                            if (Util.MODEL.startsWith("SM-G350")) {
                            }
                        }
                    }
                    return false;
                }
                if (Util.SDK_INT < 18 && "OMX.MTK.AUDIO.DECODER.AAC".equals(name)) {
                    if (!"a70".equals(Util.DEVICE)) {
                        if ("Xiaomi".equals(Util.MANUFACTURER) && Util.DEVICE.startsWith("HM")) {
                        }
                    }
                    return false;
                }
                if (Util.SDK_INT == 16) {
                    if ("OMX.qcom.audio.decoder.mp3".equals(name)) {
                        if (!"dlxu".equals(Util.DEVICE)) {
                            if (!"protou".equals(Util.DEVICE)) {
                                if (!"ville".equals(Util.DEVICE)) {
                                    if (!"villeplus".equals(Util.DEVICE)) {
                                        if (!"villec2".equals(Util.DEVICE)) {
                                            if (!Util.DEVICE.startsWith("gee")) {
                                                if (!"C6602".equals(Util.DEVICE)) {
                                                    if (!"C6603".equals(Util.DEVICE)) {
                                                        if (!"C6606".equals(Util.DEVICE)) {
                                                            if (!"C6616".equals(Util.DEVICE)) {
                                                                if (!"L36h".equals(Util.DEVICE)) {
                                                                    if ("SO-02E".equals(Util.DEVICE)) {
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
                        return false;
                    }
                }
                if (Util.SDK_INT == 16) {
                    if ("OMX.qcom.audio.decoder.aac".equals(name)) {
                        if (!"C1504".equals(Util.DEVICE)) {
                            if (!"C1505".equals(Util.DEVICE)) {
                                if (!"C1604".equals(Util.DEVICE)) {
                                    if ("C1605".equals(Util.DEVICE)) {
                                    }
                                }
                            }
                        }
                        return false;
                    }
                }
                if (Util.SDK_INT < 24) {
                    if (!"OMX.SEC.aac.dec".equals(name)) {
                        if ("OMX.Exynos.AAC.Decoder".equals(name)) {
                        }
                    }
                    if ("samsung".equals(Util.MANUFACTURER)) {
                        if (!Util.DEVICE.startsWith("zeroflte")) {
                            if (!Util.DEVICE.startsWith("zerolte")) {
                                if (!Util.DEVICE.startsWith("zenlte")) {
                                    if (!"SC-05G".equals(Util.DEVICE)) {
                                        if (!"marinelteatt".equals(Util.DEVICE)) {
                                            if (!"404SC".equals(Util.DEVICE)) {
                                                if (!"SC-04G".equals(Util.DEVICE)) {
                                                    if ("SCV31".equals(Util.DEVICE)) {
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        return false;
                    }
                }
                if (Util.SDK_INT <= 19) {
                    if ("OMX.SEC.vp8.dec".equals(name) && "samsung".equals(Util.MANUFACTURER)) {
                        if (!Util.DEVICE.startsWith("d2") && !Util.DEVICE.startsWith("serrano")) {
                            if (!Util.DEVICE.startsWith("jflte") && !Util.DEVICE.startsWith("santos")) {
                                if (Util.DEVICE.startsWith("t0")) {
                                }
                            }
                        }
                        return false;
                    }
                }
                if (Util.SDK_INT <= 19 && Util.DEVICE.startsWith("jflte")) {
                    if ("OMX.qcom.video.decoder.vp8".equals(name)) {
                        return false;
                    }
                }
                if (MimeTypes.AUDIO_E_AC3_JOC.equals(requestedMimeType)) {
                    if ("OMX.MTK.AUDIO.DECODER.DSPAC3".equals(name)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    private static void applyWorkarounds(String mimeType, List<MediaCodecInfo> decoderInfos) {
        if (MimeTypes.AUDIO_RAW.equals(mimeType)) {
            Collections.sort(decoderInfos, RAW_AUDIO_CODEC_COMPARATOR);
        }
    }

    private static boolean codecNeedsDisableAdaptationWorkaround(String name) {
        if (Util.SDK_INT <= 22) {
            if (!"ODROID-XU3".equals(Util.MODEL)) {
                if ("Nexus 10".equals(Util.MODEL)) {
                }
            }
            if (!"OMX.Exynos.AVC.Decoder".equals(name)) {
                if ("OMX.Exynos.AVC.Decoder.secure".equals(name)) {
                }
            }
            return true;
        }
        return false;
    }

    private static Pair<Integer, Integer> getHevcProfileAndLevel(String codec, String[] parts) {
        if (parts.length < 4) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Ignoring malformed HEVC codec string: ");
            stringBuilder.append(codec);
            Log.m10w(str, stringBuilder.toString());
            return null;
        }
        Matcher matcher = PROFILE_PATTERN.matcher(parts[1]);
        if (matcher.matches()) {
            int profile;
            String profileString = matcher.group(1);
            if ("1".equals(profileString)) {
                profile = 1;
            } else if ("2".equals(profileString)) {
                profile = 2;
            } else {
                String str2 = TAG;
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Unknown HEVC profile string: ");
                stringBuilder2.append(profileString);
                Log.m10w(str2, stringBuilder2.toString());
                return null;
            }
            Integer level = (Integer) HEVC_CODEC_STRING_TO_PROFILE_LEVEL.get(parts[3]);
            if (level != null) {
                return new Pair(Integer.valueOf(profile), level);
            }
            String str3 = TAG;
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append("Unknown HEVC level string: ");
            stringBuilder3.append(matcher.group(1));
            Log.m10w(str3, stringBuilder3.toString());
            return null;
        }
        str2 = TAG;
        StringBuilder stringBuilder4 = new StringBuilder();
        stringBuilder4.append("Ignoring malformed HEVC codec string: ");
        stringBuilder4.append(codec);
        Log.m10w(str2, stringBuilder4.toString());
        return null;
    }

    private static Pair<Integer, Integer> getAvcProfileAndLevel(String codec, String[] parts) {
        if (parts.length < 2) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Ignoring malformed AVC codec string: ");
            stringBuilder.append(codec);
            Log.m10w(str, stringBuilder.toString());
            return null;
        }
        try {
            Integer levelInteger;
            Integer profileInteger;
            if (parts[1].length() == 6) {
                Integer valueOf = Integer.valueOf(Integer.parseInt(parts[1].substring(0, 2), 16));
                levelInteger = Integer.valueOf(Integer.parseInt(parts[1].substring(4), 16));
                profileInteger = valueOf;
            } else if (parts.length >= 3) {
                profileInteger = Integer.valueOf(Integer.parseInt(parts[1]));
                levelInteger = Integer.valueOf(Integer.parseInt(parts[2]));
            } else {
                str = TAG;
                stringBuilder = new StringBuilder();
                stringBuilder.append("Ignoring malformed AVC codec string: ");
                stringBuilder.append(codec);
                Log.m10w(str, stringBuilder.toString());
                return null;
            }
            int profile = AVC_PROFILE_NUMBER_TO_CONST.get(profileInteger.intValue(), -1);
            if (profile == -1) {
                String str2 = TAG;
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Unknown AVC profile: ");
                stringBuilder2.append(profileInteger);
                Log.m10w(str2, stringBuilder2.toString());
                return null;
            }
            int level = AVC_LEVEL_NUMBER_TO_CONST.get(levelInteger.intValue(), -1);
            if (level != -1) {
                return new Pair(Integer.valueOf(profile), Integer.valueOf(level));
            }
            String str3 = TAG;
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append("Unknown AVC level: ");
            stringBuilder3.append(levelInteger);
            Log.m10w(str3, stringBuilder3.toString());
            return null;
        } catch (NumberFormatException e) {
            String str4 = TAG;
            StringBuilder stringBuilder4 = new StringBuilder();
            stringBuilder4.append("Ignoring malformed AVC codec string: ");
            stringBuilder4.append(codec);
            Log.m10w(str4, stringBuilder4.toString());
            return null;
        }
    }

    private static int avcLevelToMaxFrameSize(int avcLevel) {
        switch (avcLevel) {
            case 1:
            case 2:
                return 25344;
            case 8:
            case 16:
            case 32:
                return 101376;
            case 64:
                return 202752;
            case 128:
            case 256:
                return 414720;
            case 512:
                return 921600;
            case 1024:
                return 1310720;
            case 2048:
            case 4096:
                return 2097152;
            case 8192:
                return 2228224;
            case 16384:
                return 5652480;
            case 32768:
            case 65536:
                return 9437184;
            default:
                return -1;
        }
    }

    @Nullable
    private static Pair<Integer, Integer> getAacCodecProfileAndLevel(String codec, String[] parts) {
        if (parts.length != 3) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Ignoring malformed MP4A codec string: ");
            stringBuilder.append(codec);
            Log.m10w(str, stringBuilder.toString());
            return null;
        }
        try {
            if (MimeTypes.AUDIO_AAC.equals(MimeTypes.getMimeTypeFromMp4ObjectType(Integer.parseInt(parts[1], 16)))) {
                int profile = MP4A_AUDIO_OBJECT_TYPE_TO_PROFILE.get(Integer.parseInt(parts[2]), -1);
                if (profile != -1) {
                    return new Pair(Integer.valueOf(profile), Integer.valueOf(0));
                }
            }
        } catch (NumberFormatException e) {
            String str2 = TAG;
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Ignoring malformed MP4A codec string: ");
            stringBuilder2.append(codec);
            Log.m10w(str2, stringBuilder2.toString());
        }
        return null;
    }
}
