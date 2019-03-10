package com.google.android.exoplayer2.extractor.ts;

import android.util.SparseArray;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.extractor.ts.TsPayloadReader.EsInfo;
import com.google.android.exoplayer2.extractor.ts.TsPayloadReader.Factory;
import com.google.android.exoplayer2.text.cea.Cea708InitializationData;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class DefaultTsPayloadReaderFactory implements Factory {
    private static final int DESCRIPTOR_TAG_CAPTION_SERVICE = 134;
    public static final int FLAG_ALLOW_NON_IDR_KEYFRAMES = 1;
    public static final int FLAG_DETECT_ACCESS_UNITS = 8;
    public static final int FLAG_IGNORE_AAC_STREAM = 2;
    public static final int FLAG_IGNORE_H264_STREAM = 4;
    public static final int FLAG_IGNORE_SPLICE_INFO_STREAM = 16;
    public static final int FLAG_OVERRIDE_CAPTION_DESCRIPTORS = 32;
    private final List<Format> closedCaptionFormats;
    private final int flags;

    public DefaultTsPayloadReaderFactory() {
        this(0);
    }

    public DefaultTsPayloadReaderFactory(int flags) {
        this(flags, Collections.singletonList(Format.createTextSampleFormat(null, MimeTypes.APPLICATION_CEA608, 0, null)));
    }

    public DefaultTsPayloadReaderFactory(int flags, List<Format> closedCaptionFormats) {
        this.flags = flags;
        this.closedCaptionFormats = closedCaptionFormats;
    }

    public SparseArray<TsPayloadReader> createInitialPayloadReaders() {
        return new SparseArray();
    }

    public TsPayloadReader createPayloadReader(int streamType, EsInfo esInfo) {
        TsPayloadReader tsPayloadReader = null;
        switch (streamType) {
            case 2:
                return new PesReader(new H262Reader(buildUserDataReader(esInfo)));
            case 3:
            case 4:
                return new PesReader(new MpegAudioReader(esInfo.language));
            case 15:
                if (!isSet(2)) {
                    tsPayloadReader = new PesReader(new AdtsReader(false, esInfo.language));
                }
                return tsPayloadReader;
            case 17:
                if (!isSet(2)) {
                    tsPayloadReader = new PesReader(new LatmReader(esInfo.language));
                }
                return tsPayloadReader;
            case 21:
                return new PesReader(new Id3Reader());
            case 27:
                if (!isSet(4)) {
                    tsPayloadReader = new PesReader(new H264Reader(buildSeiReader(esInfo), isSet(1), isSet(8)));
                }
                return tsPayloadReader;
            case 36:
                return new PesReader(new H265Reader(buildSeiReader(esInfo)));
            case 89:
                return new PesReader(new DvbSubtitleReader(esInfo.dvbSubtitleInfos));
            case TsExtractor.TS_STREAM_TYPE_AC3 /*129*/:
            case TsExtractor.TS_STREAM_TYPE_E_AC3 /*135*/:
                return new PesReader(new Ac3Reader(esInfo.language));
            case TsExtractor.TS_STREAM_TYPE_HDMV_DTS /*130*/:
            case TsExtractor.TS_STREAM_TYPE_DTS /*138*/:
                return new PesReader(new DtsReader(esInfo.language));
            case 134:
                if (!isSet(16)) {
                    tsPayloadReader = new SectionReader(new SpliceInfoSectionReader());
                }
                return tsPayloadReader;
            default:
                return null;
        }
    }

    private SeiReader buildSeiReader(EsInfo esInfo) {
        return new SeiReader(getClosedCaptionFormats(esInfo));
    }

    private UserDataReader buildUserDataReader(EsInfo esInfo) {
        return new UserDataReader(getClosedCaptionFormats(esInfo));
    }

    private List<Format> getClosedCaptionFormats(EsInfo esInfo) {
        if (isSet(32)) {
            return r0.closedCaptionFormats;
        }
        ParsableByteArray scratchDescriptorData = new ParsableByteArray(esInfo.descriptorBytes);
        List<Format> closedCaptionFormats = r0.closedCaptionFormats;
        while (scratchDescriptorData.bytesLeft() > 0) {
            int nextDescriptorPosition = scratchDescriptorData.getPosition() + scratchDescriptorData.readUnsignedByte();
            if (scratchDescriptorData.readUnsignedByte() == 134) {
                closedCaptionFormats = new ArrayList();
                int numberOfServices = scratchDescriptorData.readUnsignedByte() & 31;
                for (int i = 0; i < numberOfServices; i++) {
                    String mimeType;
                    int accessibilityChannel;
                    List<byte[]> initializationData;
                    String language = scratchDescriptorData.readString(3);
                    int captionTypeByte = scratchDescriptorData.readUnsignedByte();
                    boolean isWideAspectRatio = false;
                    boolean isDigital = (captionTypeByte & 128) != 0;
                    if (isDigital) {
                        mimeType = MimeTypes.APPLICATION_CEA708;
                        accessibilityChannel = captionTypeByte & 63;
                    } else {
                        mimeType = MimeTypes.APPLICATION_CEA608;
                        accessibilityChannel = 1;
                    }
                    byte flags = (byte) scratchDescriptorData.readUnsignedByte();
                    scratchDescriptorData.skipBytes(1);
                    if (isDigital) {
                        if ((flags & 64) != 0) {
                            isWideAspectRatio = true;
                        }
                        initializationData = Cea708InitializationData.buildData(isWideAspectRatio);
                    } else {
                        initializationData = null;
                    }
                    closedCaptionFormats.add(Format.createTextSampleFormat(null, mimeType, null, -1, 0, language, accessibilityChannel, null, Long.MAX_VALUE, initializationData));
                }
            }
            scratchDescriptorData.setPosition(nextDescriptorPosition);
        }
        return closedCaptionFormats;
    }

    private boolean isSet(int flag) {
        return (this.flags & flag) != 0;
    }
}
