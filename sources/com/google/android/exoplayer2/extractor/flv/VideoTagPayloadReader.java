package com.google.android.exoplayer2.extractor.flv;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.extractor.flv.TagPayloadReader.UnsupportedFormatException;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.NalUnitUtil;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.video.AvcConfig;

final class VideoTagPayloadReader extends TagPayloadReader {
    private static final int AVC_PACKET_TYPE_AVC_NALU = 1;
    private static final int AVC_PACKET_TYPE_SEQUENCE_HEADER = 0;
    private static final int VIDEO_CODEC_AVC = 7;
    private static final int VIDEO_FRAME_KEYFRAME = 1;
    private static final int VIDEO_FRAME_VIDEO_INFO = 5;
    private int frameType;
    private boolean hasOutputFormat;
    private final ParsableByteArray nalLength = new ParsableByteArray(4);
    private final ParsableByteArray nalStartCode = new ParsableByteArray(NalUnitUtil.NAL_START_CODE);
    private int nalUnitLengthFieldLength;

    public VideoTagPayloadReader(TrackOutput output) {
        super(output);
    }

    public void seek() {
    }

    protected boolean parseHeader(ParsableByteArray data) throws UnsupportedFormatException {
        int header = data.readUnsignedByte();
        int frameType = (header >> 4) & 15;
        int videoCodec = header & 15;
        if (videoCodec == 7) {
            this.frameType = frameType;
            return frameType != 5;
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Video format not supported: ");
            stringBuilder.append(videoCodec);
            throw new UnsupportedFormatException(stringBuilder.toString());
        }
    }

    protected void parsePayload(ParsableByteArray data, long timeUs) throws ParserException {
        VideoTagPayloadReader videoTagPayloadReader = this;
        ParsableByteArray parsableByteArray = data;
        int packetType = data.readUnsignedByte();
        long timeUs2 = timeUs + (((long) data.readInt24()) * 1000);
        int i;
        int i2;
        if (packetType == 0 && !videoTagPayloadReader.hasOutputFormat) {
            ParsableByteArray videoSequence = new ParsableByteArray(new byte[data.bytesLeft()]);
            parsableByteArray.readBytes(videoSequence.data, 0, data.bytesLeft());
            AvcConfig avcConfig = AvcConfig.parse(videoSequence);
            videoTagPayloadReader.nalUnitLengthFieldLength = avcConfig.nalUnitLengthFieldLength;
            String str = MimeTypes.VIDEO_H264;
            i = avcConfig.width;
            i2 = avcConfig.height;
            videoTagPayloadReader.output.format(Format.createVideoSampleFormat(null, str, null, -1, -1, i, i2, -1.0f, avcConfig.initializationData, -1, avcConfig.pixelWidthAspectRatio, null));
            videoTagPayloadReader.hasOutputFormat = true;
        } else if (packetType == 1 && videoTagPayloadReader.hasOutputFormat) {
            byte[] nalLengthData = videoTagPayloadReader.nalLength.data;
            nalLengthData[0] = (byte) 0;
            nalLengthData[1] = (byte) 0;
            nalLengthData[2] = (byte) 0;
            int nalUnitLengthFieldLengthDiff = 4 - videoTagPayloadReader.nalUnitLengthFieldLength;
            i = 0;
            while (data.bytesLeft() > 0) {
                parsableByteArray.readBytes(videoTagPayloadReader.nalLength.data, nalUnitLengthFieldLengthDiff, videoTagPayloadReader.nalUnitLengthFieldLength);
                videoTagPayloadReader.nalLength.setPosition(0);
                int bytesToWrite = videoTagPayloadReader.nalLength.readUnsignedIntToInt();
                videoTagPayloadReader.nalStartCode.setPosition(0);
                videoTagPayloadReader.output.sampleData(videoTagPayloadReader.nalStartCode, 4);
                i += 4;
                videoTagPayloadReader.output.sampleData(parsableByteArray, bytesToWrite);
                i += bytesToWrite;
            }
            TrackOutput trackOutput = videoTagPayloadReader.output;
            i2 = 1;
            if (videoTagPayloadReader.frameType != 1) {
                i2 = 0;
            }
            trackOutput.sampleMetadata(timeUs2, i2, i, 0, null);
        }
    }
}
