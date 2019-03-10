package com.google.android.exoplayer2;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.ColorInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class Format implements Parcelable {
    public static final Creator<Format> CREATOR = new C05581();
    public static final int NO_VALUE = -1;
    public static final long OFFSET_SAMPLE_RELATIVE = Long.MAX_VALUE;
    public final int accessibilityChannel;
    public final int bitrate;
    public final int channelCount;
    @Nullable
    public final String codecs;
    @Nullable
    public final ColorInfo colorInfo;
    @Nullable
    public final String containerMimeType;
    @Nullable
    public final DrmInitData drmInitData;
    public final int encoderDelay;
    public final int encoderPadding;
    public final float frameRate;
    private int hashCode;
    public final int height;
    @Nullable
    public final String id;
    public final List<byte[]> initializationData;
    @Nullable
    public final String label;
    @Nullable
    public final String language;
    public final int maxInputSize;
    @Nullable
    public final Metadata metadata;
    public final int pcmEncoding;
    public final float pixelWidthHeightRatio;
    @Nullable
    public final byte[] projectionData;
    public final int rotationDegrees;
    @Nullable
    public final String sampleMimeType;
    public final int sampleRate;
    public final int selectionFlags;
    public final int stereoMode;
    public final long subsampleOffsetUs;
    public final int width;

    /* renamed from: com.google.android.exoplayer2.Format$1 */
    static class C05581 implements Creator<Format> {
        C05581() {
        }

        public Format createFromParcel(Parcel in) {
            return new Format(in);
        }

        public Format[] newArray(int size) {
            return new Format[size];
        }
    }

    @Deprecated
    public static Format createVideoContainerFormat(@Nullable String id, @Nullable String containerMimeType, String sampleMimeType, String codecs, int bitrate, int width, int height, float frameRate, @Nullable List<byte[]> initializationData, int selectionFlags) {
        return createVideoContainerFormat(id, null, containerMimeType, sampleMimeType, codecs, bitrate, width, height, frameRate, initializationData, selectionFlags);
    }

    public static Format createVideoContainerFormat(@Nullable String id, @Nullable String label, @Nullable String containerMimeType, String sampleMimeType, String codecs, int bitrate, int width, int height, float frameRate, @Nullable List<byte[]> initializationData, int selectionFlags) {
        return new Format(id, label, containerMimeType, sampleMimeType, codecs, bitrate, -1, width, height, frameRate, -1, -1.0f, null, -1, null, -1, -1, -1, -1, -1, selectionFlags, null, -1, Long.MAX_VALUE, initializationData, null, null);
    }

    public static Format createVideoSampleFormat(@Nullable String id, @Nullable String sampleMimeType, @Nullable String codecs, int bitrate, int maxInputSize, int width, int height, float frameRate, @Nullable List<byte[]> initializationData, @Nullable DrmInitData drmInitData) {
        return createVideoSampleFormat(id, sampleMimeType, codecs, bitrate, maxInputSize, width, height, frameRate, initializationData, -1, -1.0f, drmInitData);
    }

    public static Format createVideoSampleFormat(@Nullable String id, @Nullable String sampleMimeType, @Nullable String codecs, int bitrate, int maxInputSize, int width, int height, float frameRate, @Nullable List<byte[]> initializationData, int rotationDegrees, float pixelWidthHeightRatio, @Nullable DrmInitData drmInitData) {
        return createVideoSampleFormat(id, sampleMimeType, codecs, bitrate, maxInputSize, width, height, frameRate, initializationData, rotationDegrees, pixelWidthHeightRatio, null, -1, null, drmInitData);
    }

    public static Format createVideoSampleFormat(@Nullable String id, @Nullable String sampleMimeType, @Nullable String codecs, int bitrate, int maxInputSize, int width, int height, float frameRate, @Nullable List<byte[]> initializationData, int rotationDegrees, float pixelWidthHeightRatio, byte[] projectionData, int stereoMode, @Nullable ColorInfo colorInfo, @Nullable DrmInitData drmInitData) {
        return new Format(id, null, null, sampleMimeType, codecs, bitrate, maxInputSize, width, height, frameRate, rotationDegrees, pixelWidthHeightRatio, projectionData, stereoMode, colorInfo, -1, -1, -1, -1, -1, 0, null, -1, Long.MAX_VALUE, initializationData, drmInitData, null);
    }

    @Deprecated
    public static Format createAudioContainerFormat(@Nullable String id, @Nullable String containerMimeType, @Nullable String sampleMimeType, @Nullable String codecs, int bitrate, int channelCount, int sampleRate, @Nullable List<byte[]> initializationData, int selectionFlags, @Nullable String language) {
        return createAudioContainerFormat(id, null, containerMimeType, sampleMimeType, codecs, bitrate, channelCount, sampleRate, initializationData, selectionFlags, language);
    }

    public static Format createAudioContainerFormat(@Nullable String id, @Nullable String label, @Nullable String containerMimeType, @Nullable String sampleMimeType, @Nullable String codecs, int bitrate, int channelCount, int sampleRate, @Nullable List<byte[]> initializationData, int selectionFlags, @Nullable String language) {
        return new Format(id, label, containerMimeType, sampleMimeType, codecs, bitrate, -1, -1, -1, -1.0f, -1, -1.0f, null, -1, null, channelCount, sampleRate, -1, -1, -1, selectionFlags, language, -1, Long.MAX_VALUE, initializationData, null, null);
    }

    public static Format createAudioSampleFormat(@Nullable String id, @Nullable String sampleMimeType, @Nullable String codecs, int bitrate, int maxInputSize, int channelCount, int sampleRate, @Nullable List<byte[]> initializationData, @Nullable DrmInitData drmInitData, int selectionFlags, @Nullable String language) {
        return createAudioSampleFormat(id, sampleMimeType, codecs, bitrate, maxInputSize, channelCount, sampleRate, -1, initializationData, drmInitData, selectionFlags, language);
    }

    public static Format createAudioSampleFormat(@Nullable String id, @Nullable String sampleMimeType, @Nullable String codecs, int bitrate, int maxInputSize, int channelCount, int sampleRate, int pcmEncoding, @Nullable List<byte[]> initializationData, @Nullable DrmInitData drmInitData, int selectionFlags, @Nullable String language) {
        return createAudioSampleFormat(id, sampleMimeType, codecs, bitrate, maxInputSize, channelCount, sampleRate, pcmEncoding, -1, -1, initializationData, drmInitData, selectionFlags, language, null);
    }

    public static Format createAudioSampleFormat(@Nullable String id, @Nullable String sampleMimeType, @Nullable String codecs, int bitrate, int maxInputSize, int channelCount, int sampleRate, int pcmEncoding, int encoderDelay, int encoderPadding, @Nullable List<byte[]> initializationData, @Nullable DrmInitData drmInitData, int selectionFlags, @Nullable String language, @Nullable Metadata metadata) {
        return new Format(id, null, null, sampleMimeType, codecs, bitrate, maxInputSize, -1, -1, -1.0f, -1, -1.0f, null, -1, null, channelCount, sampleRate, pcmEncoding, encoderDelay, encoderPadding, selectionFlags, language, -1, Long.MAX_VALUE, initializationData, drmInitData, metadata);
    }

    @Deprecated
    public static Format createTextContainerFormat(@Nullable String id, @Nullable String containerMimeType, @Nullable String sampleMimeType, @Nullable String codecs, int bitrate, int selectionFlags, @Nullable String language) {
        return createTextContainerFormat(id, null, containerMimeType, sampleMimeType, codecs, bitrate, selectionFlags, language);
    }

    public static Format createTextContainerFormat(@Nullable String id, @Nullable String label, @Nullable String containerMimeType, @Nullable String sampleMimeType, @Nullable String codecs, int bitrate, int selectionFlags, @Nullable String language) {
        return createTextContainerFormat(id, label, containerMimeType, sampleMimeType, codecs, bitrate, selectionFlags, language, -1);
    }

    public static Format createTextContainerFormat(@Nullable String id, @Nullable String label, @Nullable String containerMimeType, @Nullable String sampleMimeType, @Nullable String codecs, int bitrate, int selectionFlags, @Nullable String language, int accessibilityChannel) {
        return new Format(id, label, containerMimeType, sampleMimeType, codecs, bitrate, -1, -1, -1, -1.0f, -1, -1.0f, null, -1, null, -1, -1, -1, -1, -1, selectionFlags, language, accessibilityChannel, Long.MAX_VALUE, null, null, null);
    }

    public static Format createTextSampleFormat(@Nullable String id, String sampleMimeType, int selectionFlags, @Nullable String language) {
        return createTextSampleFormat(id, sampleMimeType, selectionFlags, language, null);
    }

    public static Format createTextSampleFormat(@Nullable String id, String sampleMimeType, int selectionFlags, @Nullable String language, @Nullable DrmInitData drmInitData) {
        return createTextSampleFormat(id, sampleMimeType, null, -1, selectionFlags, language, -1, drmInitData, Long.MAX_VALUE, Collections.emptyList());
    }

    public static Format createTextSampleFormat(@Nullable String id, @Nullable String sampleMimeType, @Nullable String codecs, int bitrate, int selectionFlags, @Nullable String language, int accessibilityChannel, @Nullable DrmInitData drmInitData) {
        return createTextSampleFormat(id, sampleMimeType, codecs, bitrate, selectionFlags, language, accessibilityChannel, drmInitData, Long.MAX_VALUE, Collections.emptyList());
    }

    public static Format createTextSampleFormat(@Nullable String id, @Nullable String sampleMimeType, @Nullable String codecs, int bitrate, int selectionFlags, @Nullable String language, @Nullable DrmInitData drmInitData, long subsampleOffsetUs) {
        return createTextSampleFormat(id, sampleMimeType, codecs, bitrate, selectionFlags, language, -1, drmInitData, subsampleOffsetUs, Collections.emptyList());
    }

    public static Format createTextSampleFormat(@Nullable String id, @Nullable String sampleMimeType, @Nullable String codecs, int bitrate, int selectionFlags, @Nullable String language, int accessibilityChannel, @Nullable DrmInitData drmInitData, long subsampleOffsetUs, List<byte[]> initializationData) {
        return new Format(id, null, null, sampleMimeType, codecs, bitrate, -1, -1, -1, -1.0f, -1, -1.0f, null, -1, null, -1, -1, -1, -1, -1, selectionFlags, language, accessibilityChannel, subsampleOffsetUs, initializationData, drmInitData, null);
    }

    public static Format createImageSampleFormat(@Nullable String id, @Nullable String sampleMimeType, @Nullable String codecs, int bitrate, int selectionFlags, @Nullable List<byte[]> initializationData, @Nullable String language, @Nullable DrmInitData drmInitData) {
        return new Format(id, null, null, sampleMimeType, codecs, bitrate, -1, -1, -1, -1.0f, -1, -1.0f, null, -1, null, -1, -1, -1, -1, -1, selectionFlags, language, -1, Long.MAX_VALUE, initializationData, drmInitData, null);
    }

    @Deprecated
    public static Format createContainerFormat(@Nullable String id, @Nullable String containerMimeType, @Nullable String sampleMimeType, @Nullable String codecs, int bitrate, int selectionFlags, @Nullable String language) {
        return createContainerFormat(id, null, containerMimeType, sampleMimeType, codecs, bitrate, selectionFlags, language);
    }

    public static Format createContainerFormat(@Nullable String id, @Nullable String label, @Nullable String containerMimeType, @Nullable String sampleMimeType, @Nullable String codecs, int bitrate, int selectionFlags, @Nullable String language) {
        return new Format(id, label, containerMimeType, sampleMimeType, codecs, bitrate, -1, -1, -1, -1.0f, -1, -1.0f, null, -1, null, -1, -1, -1, -1, -1, selectionFlags, language, -1, Long.MAX_VALUE, null, null, null);
    }

    public static Format createSampleFormat(@Nullable String id, @Nullable String sampleMimeType, long subsampleOffsetUs) {
        return new Format(id, null, null, sampleMimeType, null, -1, -1, -1, -1, -1.0f, -1, -1.0f, null, -1, null, -1, -1, -1, -1, -1, 0, null, -1, subsampleOffsetUs, null, null, null);
    }

    public static Format createSampleFormat(@Nullable String id, @Nullable String sampleMimeType, @Nullable String codecs, int bitrate, @Nullable DrmInitData drmInitData) {
        return new Format(id, null, null, sampleMimeType, codecs, bitrate, -1, -1, -1, -1.0f, -1, -1.0f, null, -1, null, -1, -1, -1, -1, -1, 0, null, -1, Long.MAX_VALUE, null, drmInitData, null);
    }

    Format(@Nullable String id, @Nullable String label, @Nullable String containerMimeType, @Nullable String sampleMimeType, @Nullable String codecs, int bitrate, int maxInputSize, int width, int height, float frameRate, int rotationDegrees, float pixelWidthHeightRatio, @Nullable byte[] projectionData, int stereoMode, @Nullable ColorInfo colorInfo, int channelCount, int sampleRate, int pcmEncoding, int encoderDelay, int encoderPadding, int selectionFlags, @Nullable String language, int accessibilityChannel, long subsampleOffsetUs, @Nullable List<byte[]> initializationData, @Nullable DrmInitData drmInitData, @Nullable Metadata metadata) {
        this.id = id;
        this.label = label;
        this.containerMimeType = containerMimeType;
        this.sampleMimeType = sampleMimeType;
        this.codecs = codecs;
        this.bitrate = bitrate;
        this.maxInputSize = maxInputSize;
        this.width = width;
        this.height = height;
        this.frameRate = frameRate;
        int i = rotationDegrees;
        r0.rotationDegrees = i == -1 ? 0 : i;
        r0.pixelWidthHeightRatio = pixelWidthHeightRatio == -1.0f ? 1.0f : pixelWidthHeightRatio;
        r0.projectionData = projectionData;
        r0.stereoMode = stereoMode;
        r0.colorInfo = colorInfo;
        r0.channelCount = channelCount;
        r0.sampleRate = sampleRate;
        r0.pcmEncoding = pcmEncoding;
        int i2 = encoderDelay;
        r0.encoderDelay = i2 == -1 ? 0 : i2;
        int i3 = encoderPadding;
        r0.encoderPadding = i3 == -1 ? 0 : i3;
        r0.selectionFlags = selectionFlags;
        r0.language = language;
        r0.accessibilityChannel = accessibilityChannel;
        r0.subsampleOffsetUs = subsampleOffsetUs;
        r0.initializationData = initializationData == null ? Collections.emptyList() : initializationData;
        r0.drmInitData = drmInitData;
        r0.metadata = metadata;
    }

    Format(Parcel in) {
        this.id = in.readString();
        this.label = in.readString();
        this.containerMimeType = in.readString();
        this.sampleMimeType = in.readString();
        this.codecs = in.readString();
        this.bitrate = in.readInt();
        this.maxInputSize = in.readInt();
        this.width = in.readInt();
        this.height = in.readInt();
        this.frameRate = in.readFloat();
        this.rotationDegrees = in.readInt();
        this.pixelWidthHeightRatio = in.readFloat();
        this.projectionData = Util.readBoolean(in) ? in.createByteArray() : null;
        this.stereoMode = in.readInt();
        this.colorInfo = (ColorInfo) in.readParcelable(ColorInfo.class.getClassLoader());
        this.channelCount = in.readInt();
        this.sampleRate = in.readInt();
        this.pcmEncoding = in.readInt();
        this.encoderDelay = in.readInt();
        this.encoderPadding = in.readInt();
        this.selectionFlags = in.readInt();
        this.language = in.readString();
        this.accessibilityChannel = in.readInt();
        this.subsampleOffsetUs = in.readLong();
        int initializationDataSize = in.readInt();
        this.initializationData = new ArrayList(initializationDataSize);
        for (int i = 0; i < initializationDataSize; i++) {
            this.initializationData.add(in.createByteArray());
        }
        this.drmInitData = (DrmInitData) in.readParcelable(DrmInitData.class.getClassLoader());
        this.metadata = (Metadata) in.readParcelable(Metadata.class.getClassLoader());
    }

    public Format copyWithMaxInputSize(int maxInputSize) {
        int i = maxInputSize;
        return new Format(this.id, this.label, this.containerMimeType, this.sampleMimeType, this.codecs, this.bitrate, i, this.width, this.height, this.frameRate, this.rotationDegrees, this.pixelWidthHeightRatio, this.projectionData, this.stereoMode, this.colorInfo, this.channelCount, this.sampleRate, this.pcmEncoding, this.encoderDelay, this.encoderPadding, this.selectionFlags, this.language, this.accessibilityChannel, this.subsampleOffsetUs, this.initializationData, this.drmInitData, this.metadata);
    }

    public Format copyWithSubsampleOffsetUs(long subsampleOffsetUs) {
        return new Format(this.id, this.label, this.containerMimeType, this.sampleMimeType, this.codecs, this.bitrate, this.maxInputSize, this.width, this.height, this.frameRate, this.rotationDegrees, this.pixelWidthHeightRatio, this.projectionData, this.stereoMode, this.colorInfo, this.channelCount, this.sampleRate, this.pcmEncoding, this.encoderDelay, this.encoderPadding, this.selectionFlags, this.language, this.accessibilityChannel, subsampleOffsetUs, this.initializationData, this.drmInitData, this.metadata);
    }

    public Format copyWithContainerInfo(@Nullable String id, @Nullable String label, @Nullable String sampleMimeType, @Nullable String codecs, int bitrate, int width, int height, int selectionFlags, @Nullable String language) {
        return new Format(id, label, this.containerMimeType, sampleMimeType, codecs, bitrate, this.maxInputSize, width, height, this.frameRate, this.rotationDegrees, this.pixelWidthHeightRatio, this.projectionData, this.stereoMode, this.colorInfo, this.channelCount, this.sampleRate, this.pcmEncoding, this.encoderDelay, this.encoderPadding, selectionFlags, language, this.accessibilityChannel, this.subsampleOffsetUs, this.initializationData, this.drmInitData, this.metadata);
    }

    public Format copyWithManifestFormatInfo(Format manifestFormat) {
        Format format = manifestFormat;
        if (this == format) {
            return r0;
        }
        String language;
        String codecs;
        float frameRate;
        float frameRate2;
        int i;
        DrmInitData createSessionCreationData;
        int trackType = MimeTypes.getTrackType(r0.sampleMimeType);
        String id = format.id;
        String str = format.label;
        if (str == null) {
            str = r0.label;
        }
        String label = str;
        str = r0.language;
        if ((trackType == 3 || trackType == 1) && format.language != null) {
            language = format.language;
        } else {
            language = str;
        }
        int i2 = r0.bitrate;
        if (i2 == -1) {
            i2 = format.bitrate;
        }
        int bitrate = i2;
        str = r0.codecs;
        if (str == null) {
            String codecsOfType = Util.getCodecsOfType(format.codecs, trackType);
            if (Util.splitCodecs(codecsOfType).length == 1) {
                codecs = codecsOfType;
                frameRate = r0.frameRate;
                if (frameRate == -1.0f || trackType != 2) {
                    frameRate2 = frameRate;
                } else {
                    frameRate2 = format.frameRate;
                }
                i = r0.selectionFlags | format.selectionFlags;
                createSessionCreationData = DrmInitData.createSessionCreationData(format.drmInitData, r0.drmInitData);
                return new Format(id, label, r0.containerMimeType, r0.sampleMimeType, codecs, bitrate, r0.maxInputSize, r0.width, r0.height, frameRate2, r0.rotationDegrees, r0.pixelWidthHeightRatio, r0.projectionData, r0.stereoMode, r0.colorInfo, r0.channelCount, r0.sampleRate, r0.pcmEncoding, r0.encoderDelay, r0.encoderPadding, i, language, r0.accessibilityChannel, r0.subsampleOffsetUs, r0.initializationData, createSessionCreationData, r0.metadata);
            }
        }
        codecs = str;
        frameRate = r0.frameRate;
        if (frameRate == -1.0f) {
        }
        frameRate2 = frameRate;
        i = r0.selectionFlags | format.selectionFlags;
        createSessionCreationData = DrmInitData.createSessionCreationData(format.drmInitData, r0.drmInitData);
        return new Format(id, label, r0.containerMimeType, r0.sampleMimeType, codecs, bitrate, r0.maxInputSize, r0.width, r0.height, frameRate2, r0.rotationDegrees, r0.pixelWidthHeightRatio, r0.projectionData, r0.stereoMode, r0.colorInfo, r0.channelCount, r0.sampleRate, r0.pcmEncoding, r0.encoderDelay, r0.encoderPadding, i, language, r0.accessibilityChannel, r0.subsampleOffsetUs, r0.initializationData, createSessionCreationData, r0.metadata);
    }

    public Format copyWithGaplessInfo(int encoderDelay, int encoderPadding) {
        int i = encoderDelay;
        int i2 = encoderPadding;
        return new Format(this.id, this.label, this.containerMimeType, this.sampleMimeType, this.codecs, this.bitrate, this.maxInputSize, this.width, this.height, this.frameRate, this.rotationDegrees, this.pixelWidthHeightRatio, this.projectionData, this.stereoMode, this.colorInfo, this.channelCount, this.sampleRate, this.pcmEncoding, i, i2, this.selectionFlags, this.language, this.accessibilityChannel, this.subsampleOffsetUs, this.initializationData, this.drmInitData, this.metadata);
    }

    public Format copyWithDrmInitData(@Nullable DrmInitData drmInitData) {
        DrmInitData drmInitData2 = drmInitData;
        return new Format(this.id, this.label, this.containerMimeType, this.sampleMimeType, this.codecs, this.bitrate, this.maxInputSize, this.width, this.height, this.frameRate, this.rotationDegrees, this.pixelWidthHeightRatio, this.projectionData, this.stereoMode, this.colorInfo, this.channelCount, this.sampleRate, this.pcmEncoding, this.encoderDelay, this.encoderPadding, this.selectionFlags, this.language, this.accessibilityChannel, this.subsampleOffsetUs, this.initializationData, drmInitData2, this.metadata);
    }

    public Format copyWithMetadata(@Nullable Metadata metadata) {
        Metadata metadata2 = metadata;
        return new Format(this.id, this.label, this.containerMimeType, this.sampleMimeType, this.codecs, this.bitrate, this.maxInputSize, this.width, this.height, this.frameRate, this.rotationDegrees, this.pixelWidthHeightRatio, this.projectionData, this.stereoMode, this.colorInfo, this.channelCount, this.sampleRate, this.pcmEncoding, this.encoderDelay, this.encoderPadding, this.selectionFlags, this.language, this.accessibilityChannel, this.subsampleOffsetUs, this.initializationData, this.drmInitData, metadata2);
    }

    public Format copyWithRotationDegrees(int rotationDegrees) {
        int i = rotationDegrees;
        return new Format(this.id, this.label, this.containerMimeType, this.sampleMimeType, this.codecs, this.bitrate, this.maxInputSize, this.width, this.height, this.frameRate, i, this.pixelWidthHeightRatio, this.projectionData, this.stereoMode, this.colorInfo, this.channelCount, this.sampleRate, this.pcmEncoding, this.encoderDelay, this.encoderPadding, this.selectionFlags, this.language, this.accessibilityChannel, this.subsampleOffsetUs, this.initializationData, this.drmInitData, this.metadata);
    }

    public int getPixelCount() {
        int i = this.width;
        if (i == -1) {
            return -1;
        }
        int i2 = this.height;
        return i2 == -1 ? -1 : i * i2;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Format(");
        stringBuilder.append(this.id);
        stringBuilder.append(", ");
        stringBuilder.append(this.label);
        stringBuilder.append(", ");
        stringBuilder.append(this.containerMimeType);
        stringBuilder.append(", ");
        stringBuilder.append(this.sampleMimeType);
        stringBuilder.append(", ");
        stringBuilder.append(this.codecs);
        stringBuilder.append(", ");
        stringBuilder.append(this.bitrate);
        stringBuilder.append(", ");
        stringBuilder.append(this.language);
        stringBuilder.append(", [");
        stringBuilder.append(this.width);
        stringBuilder.append(", ");
        stringBuilder.append(this.height);
        stringBuilder.append(", ");
        stringBuilder.append(this.frameRate);
        stringBuilder.append("], [");
        stringBuilder.append(this.channelCount);
        stringBuilder.append(", ");
        stringBuilder.append(this.sampleRate);
        stringBuilder.append("])");
        return stringBuilder.toString();
    }

    public int hashCode() {
        if (this.hashCode == 0) {
            int result = 17 * 31;
            String str = this.id;
            int i = 0;
            int hashCode = (result + (str == null ? 0 : str.hashCode())) * 31;
            str = this.containerMimeType;
            result = (hashCode + (str == null ? 0 : str.hashCode())) * 31;
            str = this.sampleMimeType;
            hashCode = (result + (str == null ? 0 : str.hashCode())) * 31;
            str = this.codecs;
            hashCode = (((((((((((hashCode + (str == null ? 0 : str.hashCode())) * 31) + this.bitrate) * 31) + this.width) * 31) + this.height) * 31) + this.channelCount) * 31) + this.sampleRate) * 31;
            str = this.language;
            hashCode = (((hashCode + (str == null ? 0 : str.hashCode())) * 31) + this.accessibilityChannel) * 31;
            DrmInitData drmInitData = this.drmInitData;
            result = (hashCode + (drmInitData == null ? 0 : drmInitData.hashCode())) * 31;
            Metadata metadata = this.metadata;
            hashCode = (result + (metadata == null ? 0 : metadata.hashCode())) * 31;
            str = this.label;
            if (str != null) {
                i = str.hashCode();
            }
            this.hashCode = ((((((((((((((((((((hashCode + i) * 31) + this.maxInputSize) * 31) + ((int) this.subsampleOffsetUs)) * 31) + Float.floatToIntBits(this.frameRate)) * 31) + Float.floatToIntBits(this.pixelWidthHeightRatio)) * 31) + this.rotationDegrees) * 31) + this.stereoMode) * 31) + this.pcmEncoding) * 31) + this.encoderDelay) * 31) + this.encoderPadding) * 31) + this.selectionFlags;
        }
        return this.hashCode;
    }

    public boolean equals(@Nullable Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj != null) {
            if (getClass() == obj.getClass()) {
                Format other = (Format) obj;
                int i = this.hashCode;
                if (i != 0) {
                    int i2 = other.hashCode;
                    if (!(i2 == 0 || i == i2)) {
                        return false;
                    }
                }
                if (this.bitrate == other.bitrate && this.maxInputSize == other.maxInputSize && this.width == other.width && this.height == other.height) {
                    if (Float.compare(this.frameRate, other.frameRate) == 0 && this.rotationDegrees == other.rotationDegrees) {
                        if (Float.compare(this.pixelWidthHeightRatio, other.pixelWidthHeightRatio) == 0 && this.stereoMode == other.stereoMode && this.channelCount == other.channelCount && this.sampleRate == other.sampleRate && this.pcmEncoding == other.pcmEncoding && this.encoderDelay == other.encoderDelay && this.encoderPadding == other.encoderPadding && this.subsampleOffsetUs == other.subsampleOffsetUs && this.selectionFlags == other.selectionFlags) {
                            if (Util.areEqual(this.id, other.id)) {
                                if (Util.areEqual(this.label, other.label)) {
                                    if (Util.areEqual(this.language, other.language) && this.accessibilityChannel == other.accessibilityChannel) {
                                        if (Util.areEqual(this.containerMimeType, other.containerMimeType)) {
                                            if (Util.areEqual(this.sampleMimeType, other.sampleMimeType)) {
                                                if (Util.areEqual(this.codecs, other.codecs)) {
                                                    if (Util.areEqual(this.drmInitData, other.drmInitData)) {
                                                        if (Util.areEqual(this.metadata, other.metadata)) {
                                                            if (Util.areEqual(this.colorInfo, other.colorInfo)) {
                                                                if (Arrays.equals(this.projectionData, other.projectionData)) {
                                                                    if (initializationDataEquals(other)) {
                                                                        return z;
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
                    }
                }
                z = false;
                return z;
            }
        }
        return false;
    }

    public boolean initializationDataEquals(Format other) {
        if (this.initializationData.size() != other.initializationData.size()) {
            return false;
        }
        for (int i = 0; i < this.initializationData.size(); i++) {
            if (!Arrays.equals((byte[]) this.initializationData.get(i), (byte[]) other.initializationData.get(i))) {
                return false;
            }
        }
        return true;
    }

    public static String toLogString(@Nullable Format format) {
        if (format == null) {
            return "null";
        }
        StringBuilder builder = new StringBuilder();
        builder.append("id=");
        builder.append(format.id);
        builder.append(", mimeType=");
        builder.append(format.sampleMimeType);
        if (format.bitrate != -1) {
            builder.append(", bitrate=");
            builder.append(format.bitrate);
        }
        if (format.codecs != null) {
            builder.append(", codecs=");
            builder.append(format.codecs);
        }
        if (format.width != -1 && format.height != -1) {
            builder.append(", res=");
            builder.append(format.width);
            builder.append("x");
            builder.append(format.height);
        }
        if (format.frameRate != -1.0f) {
            builder.append(", fps=");
            builder.append(format.frameRate);
        }
        if (format.channelCount != -1) {
            builder.append(", channels=");
            builder.append(format.channelCount);
        }
        if (format.sampleRate != -1) {
            builder.append(", sample_rate=");
            builder.append(format.sampleRate);
        }
        if (format.language != null) {
            builder.append(", language=");
            builder.append(format.language);
        }
        if (format.label != null) {
            builder.append(", label=");
            builder.append(format.label);
        }
        return builder.toString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.label);
        dest.writeString(this.containerMimeType);
        dest.writeString(this.sampleMimeType);
        dest.writeString(this.codecs);
        dest.writeInt(this.bitrate);
        dest.writeInt(this.maxInputSize);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeFloat(this.frameRate);
        dest.writeInt(this.rotationDegrees);
        dest.writeFloat(this.pixelWidthHeightRatio);
        Util.writeBoolean(dest, this.projectionData != null);
        byte[] bArr = this.projectionData;
        if (bArr != null) {
            dest.writeByteArray(bArr);
        }
        dest.writeInt(this.stereoMode);
        dest.writeParcelable(this.colorInfo, flags);
        dest.writeInt(this.channelCount);
        dest.writeInt(this.sampleRate);
        dest.writeInt(this.pcmEncoding);
        dest.writeInt(this.encoderDelay);
        dest.writeInt(this.encoderPadding);
        dest.writeInt(this.selectionFlags);
        dest.writeString(this.language);
        dest.writeInt(this.accessibilityChannel);
        dest.writeLong(this.subsampleOffsetUs);
        int initializationDataSize = this.initializationData.size();
        dest.writeInt(initializationDataSize);
        for (int i = 0; i < initializationDataSize; i++) {
            dest.writeByteArray((byte[]) this.initializationData.get(i));
        }
        dest.writeParcelable(this.drmInitData, 0);
        dest.writeParcelable(this.metadata, 0);
    }
}
