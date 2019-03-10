package com.google.android.exoplayer2.mediacodec;

import android.annotation.TargetApi;
import android.graphics.Point;
import android.media.MediaCodecInfo.AudioCapabilities;
import android.media.MediaCodecInfo.CodecCapabilities;
import android.media.MediaCodecInfo.CodecProfileLevel;
import android.media.MediaCodecInfo.VideoCapabilities;
import android.support.annotation.Nullable;
import android.util.Pair;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil.DecoderQueryException;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;

@TargetApi(16)
public final class MediaCodecInfo {
    public static final int MAX_SUPPORTED_INSTANCES_UNKNOWN = -1;
    public static final String TAG = "MediaCodecInfo";
    public final boolean adaptive;
    @Nullable
    public final CodecCapabilities capabilities;
    private final boolean isVideo;
    @Nullable
    public final String mimeType;
    public final String name;
    public final boolean passthrough;
    public final boolean secure;
    public final boolean tunneling;

    public static MediaCodecInfo newPassthroughInstance(String name) {
        return new MediaCodecInfo(name, null, null, true, false, false);
    }

    public static MediaCodecInfo newInstance(String name, String mimeType, CodecCapabilities capabilities) {
        return new MediaCodecInfo(name, mimeType, capabilities, false, false, false);
    }

    public static MediaCodecInfo newInstance(String name, String mimeType, CodecCapabilities capabilities, boolean forceDisableAdaptive, boolean forceSecure) {
        return new MediaCodecInfo(name, mimeType, capabilities, false, forceDisableAdaptive, forceSecure);
    }

    private MediaCodecInfo(String name, @Nullable String mimeType, @Nullable CodecCapabilities capabilities, boolean passthrough, boolean forceDisableAdaptive, boolean forceSecure) {
        this.name = (String) Assertions.checkNotNull(name);
        this.mimeType = mimeType;
        this.capabilities = capabilities;
        this.passthrough = passthrough;
        boolean z = true;
        boolean z2 = (forceDisableAdaptive || capabilities == null || !isAdaptive(capabilities)) ? false : true;
        this.adaptive = z2;
        z2 = capabilities != null && isTunneling(capabilities);
        this.tunneling = z2;
        if (!forceSecure) {
            if (capabilities == null || !isSecure(capabilities)) {
                z = false;
            }
        }
        this.secure = z;
        this.isVideo = MimeTypes.isVideo(mimeType);
    }

    public String toString() {
        return this.name;
    }

    public CodecProfileLevel[] getProfileLevels() {
        CodecCapabilities codecCapabilities = this.capabilities;
        if (codecCapabilities != null) {
            if (codecCapabilities.profileLevels != null) {
                return this.capabilities.profileLevels;
            }
        }
        return new CodecProfileLevel[0];
    }

    public int getMaxSupportedInstances() {
        int maxSupportedInstancesV23;
        if (Util.SDK_INT >= 23) {
            CodecCapabilities codecCapabilities = this.capabilities;
            if (codecCapabilities != null) {
                maxSupportedInstancesV23 = getMaxSupportedInstancesV23(codecCapabilities);
                return maxSupportedInstancesV23;
            }
        }
        maxSupportedInstancesV23 = -1;
        return maxSupportedInstancesV23;
    }

    public boolean isFormatSupported(Format format) throws DecoderQueryException {
        boolean z = false;
        if (!isCodecSupported(format.codecs)) {
            return false;
        }
        if (this.isVideo) {
            if (format.width > 0) {
                if (format.height > 0) {
                    if (Util.SDK_INT >= 21) {
                        return isVideoSizeAndRateSupportedV21(format.width, format.height, (double) format.frameRate);
                    }
                    if (format.width * format.height <= MediaCodecUtil.maxH264DecodableFrameSize()) {
                        z = true;
                    }
                    boolean isFormatSupported = z;
                    if (!isFormatSupported) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("legacyFrameSize, ");
                        stringBuilder.append(format.width);
                        stringBuilder.append("x");
                        stringBuilder.append(format.height);
                        logNoSupport(stringBuilder.toString());
                    }
                    return isFormatSupported;
                }
            }
            return true;
        }
        if (Util.SDK_INT >= 21) {
            if (format.sampleRate != -1) {
                if (!isAudioSampleRateSupportedV21(format.sampleRate)) {
                    return z;
                }
            }
            if (format.channelCount != -1) {
                if (isAudioChannelCountSupportedV21(format.channelCount)) {
                }
                return z;
            }
        }
        z = true;
        return z;
    }

    public boolean isCodecSupported(String codec) {
        if (codec != null) {
            if (this.mimeType != null) {
                String codecMimeType = MimeTypes.getMediaMimeType(codec);
                if (codecMimeType == null) {
                    return true;
                }
                StringBuilder stringBuilder;
                if (this.mimeType.equals(codecMimeType)) {
                    Pair<Integer, Integer> codecProfileAndLevel = MediaCodecUtil.getCodecProfileAndLevel(codec);
                    if (codecProfileAndLevel == null) {
                        return true;
                    }
                    int profile = ((Integer) codecProfileAndLevel.first).intValue();
                    int level = ((Integer) codecProfileAndLevel.second).intValue();
                    if (!this.isVideo && profile != 42) {
                        return true;
                    }
                    for (CodecProfileLevel capabilities : getProfileLevels()) {
                        if (capabilities.profile == profile && capabilities.level >= level) {
                            return true;
                        }
                    }
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("codec.profileLevel, ");
                    stringBuilder.append(codec);
                    stringBuilder.append(", ");
                    stringBuilder.append(codecMimeType);
                    logNoSupport(stringBuilder.toString());
                    return false;
                }
                stringBuilder = new StringBuilder();
                stringBuilder.append("codec.mime ");
                stringBuilder.append(codec);
                stringBuilder.append(", ");
                stringBuilder.append(codecMimeType);
                logNoSupport(stringBuilder.toString());
                return false;
            }
        }
        return true;
    }

    public boolean isSeamlessAdaptationSupported(Format format) {
        if (this.isVideo) {
            return this.adaptive;
        }
        Pair<Integer, Integer> codecProfileLevel = MediaCodecUtil.getCodecProfileAndLevel(format.codecs);
        boolean z = codecProfileLevel != null && ((Integer) codecProfileLevel.first).intValue() == 42;
        return z;
    }

    public boolean isSeamlessAdaptationSupported(Format oldFormat, Format newFormat, boolean isNewFormatComplete) {
        boolean z = true;
        if (this.isVideo) {
            if (oldFormat.sampleMimeType.equals(newFormat.sampleMimeType) && oldFormat.rotationDegrees == newFormat.rotationDegrees && (this.adaptive || (oldFormat.width == newFormat.width && oldFormat.height == newFormat.height))) {
                if (!isNewFormatComplete) {
                    if (newFormat.colorInfo == null) {
                        return z;
                    }
                }
                if (!Util.areEqual(oldFormat.colorInfo, newFormat.colorInfo)) {
                }
                return z;
            }
            z = false;
            return z;
        }
        if (MimeTypes.AUDIO_AAC.equals(this.mimeType)) {
            if (oldFormat.sampleMimeType.equals(newFormat.sampleMimeType) && oldFormat.channelCount == newFormat.channelCount) {
                if (oldFormat.sampleRate == newFormat.sampleRate) {
                    Pair<Integer, Integer> oldCodecProfileLevel = MediaCodecUtil.getCodecProfileAndLevel(oldFormat.codecs);
                    Pair<Integer, Integer> newCodecProfileLevel = MediaCodecUtil.getCodecProfileAndLevel(newFormat.codecs);
                    if (oldCodecProfileLevel != null) {
                        if (newCodecProfileLevel != null) {
                            int oldProfile = ((Integer) oldCodecProfileLevel.first).intValue();
                            int newProfile = ((Integer) newCodecProfileLevel.first).intValue();
                            if (oldProfile != 42 || newProfile != 42) {
                                z = false;
                            }
                            return z;
                        }
                    }
                    return false;
                }
            }
        }
        return false;
    }

    @TargetApi(21)
    public boolean isVideoSizeAndRateSupportedV21(int width, int height, double frameRate) {
        VideoCapabilities videoCapabilities = this.capabilities;
        if (videoCapabilities == null) {
            logNoSupport("sizeAndRate.caps");
            return false;
        }
        videoCapabilities = videoCapabilities.getVideoCapabilities();
        if (videoCapabilities == null) {
            logNoSupport("sizeAndRate.vCaps");
            return false;
        }
        if (!areSizeAndRateSupportedV21(videoCapabilities, width, height, frameRate)) {
            if (width < height) {
                if (areSizeAndRateSupportedV21(videoCapabilities, height, width, frameRate)) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("sizeAndRate.rotated, ");
                    stringBuilder.append(width);
                    stringBuilder.append("x");
                    stringBuilder.append(height);
                    stringBuilder.append("x");
                    stringBuilder.append(frameRate);
                    logAssumedSupport(stringBuilder.toString());
                }
            }
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("sizeAndRate.support, ");
            stringBuilder2.append(width);
            stringBuilder2.append("x");
            stringBuilder2.append(height);
            stringBuilder2.append("x");
            stringBuilder2.append(frameRate);
            logNoSupport(stringBuilder2.toString());
            return false;
        }
        return true;
    }

    @TargetApi(21)
    public Point alignVideoSizeV21(int width, int height) {
        VideoCapabilities videoCapabilities = this.capabilities;
        if (videoCapabilities == null) {
            logNoSupport("align.caps");
            return null;
        }
        videoCapabilities = videoCapabilities.getVideoCapabilities();
        if (videoCapabilities == null) {
            logNoSupport("align.vCaps");
            return null;
        }
        int widthAlignment = videoCapabilities.getWidthAlignment();
        int heightAlignment = videoCapabilities.getHeightAlignment();
        return new Point(Util.ceilDivide(width, widthAlignment) * widthAlignment, Util.ceilDivide(height, heightAlignment) * heightAlignment);
    }

    @TargetApi(21)
    public boolean isAudioSampleRateSupportedV21(int sampleRate) {
        AudioCapabilities audioCapabilities = this.capabilities;
        if (audioCapabilities == null) {
            logNoSupport("sampleRate.caps");
            return false;
        }
        audioCapabilities = audioCapabilities.getAudioCapabilities();
        if (audioCapabilities == null) {
            logNoSupport("sampleRate.aCaps");
            return false;
        } else if (audioCapabilities.isSampleRateSupported(sampleRate)) {
            return true;
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("sampleRate.support, ");
            stringBuilder.append(sampleRate);
            logNoSupport(stringBuilder.toString());
            return false;
        }
    }

    @TargetApi(21)
    public boolean isAudioChannelCountSupportedV21(int channelCount) {
        AudioCapabilities audioCapabilities = this.capabilities;
        if (audioCapabilities == null) {
            logNoSupport("channelCount.caps");
            return false;
        }
        audioCapabilities = audioCapabilities.getAudioCapabilities();
        if (audioCapabilities == null) {
            logNoSupport("channelCount.aCaps");
            return false;
        } else if (adjustMaxInputChannelCount(this.name, this.mimeType, audioCapabilities.getMaxInputChannelCount()) >= channelCount) {
            return true;
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("channelCount.support, ");
            stringBuilder.append(channelCount);
            logNoSupport(stringBuilder.toString());
            return false;
        }
    }

    private void logNoSupport(String message) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("NoSupport [");
        stringBuilder.append(message);
        stringBuilder.append("] [");
        stringBuilder.append(this.name);
        stringBuilder.append(", ");
        stringBuilder.append(this.mimeType);
        stringBuilder.append("] [");
        stringBuilder.append(Util.DEVICE_DEBUG_INFO);
        stringBuilder.append("]");
        Log.m4d(str, stringBuilder.toString());
    }

    private void logAssumedSupport(String message) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("AssumedSupport [");
        stringBuilder.append(message);
        stringBuilder.append("] [");
        stringBuilder.append(this.name);
        stringBuilder.append(", ");
        stringBuilder.append(this.mimeType);
        stringBuilder.append("] [");
        stringBuilder.append(Util.DEVICE_DEBUG_INFO);
        stringBuilder.append("]");
        Log.m4d(str, stringBuilder.toString());
    }

    private static int adjustMaxInputChannelCount(String name, String mimeType, int maxChannelCount) {
        if (maxChannelCount <= 1) {
            if (Util.SDK_INT < 26 || maxChannelCount <= 0) {
                if (!MimeTypes.AUDIO_MPEG.equals(mimeType)) {
                    if (!MimeTypes.AUDIO_AMR_NB.equals(mimeType)) {
                        if (!MimeTypes.AUDIO_AMR_WB.equals(mimeType)) {
                            if (!MimeTypes.AUDIO_AAC.equals(mimeType)) {
                                if (!MimeTypes.AUDIO_VORBIS.equals(mimeType)) {
                                    if (!MimeTypes.AUDIO_OPUS.equals(mimeType)) {
                                        if (!MimeTypes.AUDIO_RAW.equals(mimeType)) {
                                            if (!MimeTypes.AUDIO_FLAC.equals(mimeType)) {
                                                if (!MimeTypes.AUDIO_ALAW.equals(mimeType)) {
                                                    if (!MimeTypes.AUDIO_MLAW.equals(mimeType)) {
                                                        if (!MimeTypes.AUDIO_MSGSM.equals(mimeType)) {
                                                            int assumedMaxChannelCount;
                                                            if (MimeTypes.AUDIO_AC3.equals(mimeType)) {
                                                                assumedMaxChannelCount = 6;
                                                            } else if (MimeTypes.AUDIO_E_AC3.equals(mimeType)) {
                                                                assumedMaxChannelCount = 16;
                                                            } else {
                                                                assumedMaxChannelCount = 30;
                                                            }
                                                            String str = TAG;
                                                            StringBuilder stringBuilder = new StringBuilder();
                                                            stringBuilder.append("AssumedMaxChannelAdjustment: ");
                                                            stringBuilder.append(name);
                                                            stringBuilder.append(", [");
                                                            stringBuilder.append(maxChannelCount);
                                                            stringBuilder.append(" to ");
                                                            stringBuilder.append(assumedMaxChannelCount);
                                                            stringBuilder.append("]");
                                                            Log.m10w(str, stringBuilder.toString());
                                                            return assumedMaxChannelCount;
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
                return maxChannelCount;
            }
        }
        return maxChannelCount;
    }

    private static boolean isAdaptive(CodecCapabilities capabilities) {
        return Util.SDK_INT >= 19 && isAdaptiveV19(capabilities);
    }

    @TargetApi(19)
    private static boolean isAdaptiveV19(CodecCapabilities capabilities) {
        return capabilities.isFeatureSupported("adaptive-playback");
    }

    private static boolean isTunneling(CodecCapabilities capabilities) {
        return Util.SDK_INT >= 21 && isTunnelingV21(capabilities);
    }

    @TargetApi(21)
    private static boolean isTunnelingV21(CodecCapabilities capabilities) {
        return capabilities.isFeatureSupported("tunneled-playback");
    }

    private static boolean isSecure(CodecCapabilities capabilities) {
        return Util.SDK_INT >= 21 && isSecureV21(capabilities);
    }

    @TargetApi(21)
    private static boolean isSecureV21(CodecCapabilities capabilities) {
        return capabilities.isFeatureSupported("secure-playback");
    }

    @TargetApi(21)
    private static boolean areSizeAndRateSupportedV21(VideoCapabilities capabilities, int width, int height, double frameRate) {
        if (frameRate != -1.0d) {
            if (frameRate > 0.0d) {
                return capabilities.areSizeAndRateSupported(width, height, frameRate);
            }
        }
        return capabilities.isSizeSupported(width, height);
    }

    @TargetApi(23)
    private static int getMaxSupportedInstancesV23(CodecCapabilities capabilities) {
        return capabilities.getMaxSupportedInstances();
    }
}
