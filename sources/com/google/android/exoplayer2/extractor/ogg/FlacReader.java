package com.google.android.exoplayer2.extractor.ogg;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.SeekMap.SeekPoints;
import com.google.android.exoplayer2.extractor.SeekPoint;
import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import com.google.android.exoplayer2.util.FlacStreamInfo;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import kotlin.jvm.internal.ByteCompanionObject;

final class FlacReader extends StreamReader {
    private static final byte AUDIO_PACKET_TYPE = (byte) -1;
    private static final int FRAME_HEADER_SAMPLE_NUMBER_OFFSET = 4;
    private static final byte SEEKTABLE_PACKET_TYPE = (byte) 3;
    private FlacOggSeeker flacOggSeeker;
    private FlacStreamInfo streamInfo;

    private class FlacOggSeeker implements OggSeeker, SeekMap {
        private static final int METADATA_LENGTH_OFFSET = 1;
        private static final int SEEK_POINT_SIZE = 18;
        private long firstFrameOffset = -1;
        private long pendingSeekGranule = -1;
        private long[] seekPointGranules;
        private long[] seekPointOffsets;

        public void setFirstFrameOffset(long firstFrameOffset) {
            this.firstFrameOffset = firstFrameOffset;
        }

        public void parseSeekTable(ParsableByteArray data) {
            data.skipBytes(1);
            int numberOfSeekPoints = data.readUnsignedInt24() / 18;
            this.seekPointGranules = new long[numberOfSeekPoints];
            this.seekPointOffsets = new long[numberOfSeekPoints];
            for (int i = 0; i < numberOfSeekPoints; i++) {
                this.seekPointGranules[i] = data.readLong();
                this.seekPointOffsets[i] = data.readLong();
                data.skipBytes(2);
            }
        }

        public long read(ExtractorInput input) throws IOException, InterruptedException {
            long j = this.pendingSeekGranule;
            if (j < 0) {
                return -1;
            }
            j = -(j + 2);
            this.pendingSeekGranule = -1;
            return j;
        }

        public long startSeek(long timeUs) {
            long granule = FlacReader.this.convertTimeToGranule(timeUs);
            this.pendingSeekGranule = this.seekPointGranules[Util.binarySearchFloor(this.seekPointGranules, granule, true, true)];
            return granule;
        }

        public SeekMap createSeekMap() {
            return this;
        }

        public boolean isSeekable() {
            return true;
        }

        public SeekPoints getSeekPoints(long timeUs) {
            long j = timeUs;
            int index = Util.binarySearchFloor(this.seekPointGranules, FlacReader.this.convertTimeToGranule(j), true, true);
            long seekTimeUs = FlacReader.this.convertGranuleToTime(this.seekPointGranules[index]);
            SeekPoint seekPoint = new SeekPoint(seekTimeUs, this.firstFrameOffset + this.seekPointOffsets[index]);
            if (seekTimeUs < j) {
                long[] jArr = r0.seekPointGranules;
                if (index != jArr.length - 1) {
                    return new SeekPoints(seekPoint, new SeekPoint(FlacReader.this.convertGranuleToTime(jArr[index + 1]), r0.firstFrameOffset + r0.seekPointOffsets[index + 1]));
                }
            }
            return new SeekPoints(seekPoint);
        }

        public long getDurationUs() {
            return FlacReader.this.streamInfo.durationUs();
        }
    }

    FlacReader() {
    }

    public static boolean verifyBitstreamType(ParsableByteArray data) {
        if (data.bytesLeft() >= 5 && data.readUnsignedByte() == 127) {
            if (data.readUnsignedInt() == 1179402563) {
                return true;
            }
        }
        return false;
    }

    protected void reset(boolean headerData) {
        super.reset(headerData);
        if (headerData) {
            this.streamInfo = null;
            this.flacOggSeeker = null;
        }
    }

    private static boolean isAudioPacket(byte[] data) {
        return data[0] == AUDIO_PACKET_TYPE;
    }

    protected long preparePayload(ParsableByteArray packet) {
        if (isAudioPacket(packet.data)) {
            return (long) getFlacFrameBlockSize(packet);
        }
        return -1;
    }

    protected boolean readHeaders(ParsableByteArray packet, long position, SetupData setupData) throws IOException, InterruptedException {
        ParsableByteArray parsableByteArray = packet;
        SetupData setupData2 = setupData;
        byte[] data = parsableByteArray.data;
        long j;
        if (this.streamInfo == null) {
            r0.streamInfo = new FlacStreamInfo(data, 17);
            byte[] metadata = Arrays.copyOfRange(data, 9, packet.limit());
            metadata[4] = ByteCompanionObject.MIN_VALUE;
            setupData2.format = Format.createAudioSampleFormat(null, MimeTypes.AUDIO_FLAC, null, -1, r0.streamInfo.bitRate(), r0.streamInfo.channels, r0.streamInfo.sampleRate, Collections.singletonList(metadata), null, 0, null);
            j = position;
        } else if ((data[0] & 127) == 3) {
            r0.flacOggSeeker = new FlacOggSeeker();
            r0.flacOggSeeker.parseSeekTable(parsableByteArray);
            j = position;
        } else if (isAudioPacket(data)) {
            FlacOggSeeker flacOggSeeker = r0.flacOggSeeker;
            if (flacOggSeeker != null) {
                flacOggSeeker.setFirstFrameOffset(position);
                setupData2.oggSeeker = r0.flacOggSeeker;
            } else {
                j = position;
            }
            return false;
        } else {
            j = position;
        }
        return true;
    }

    private int getFlacFrameBlockSize(ParsableByteArray packet) {
        int blockSizeCode = (packet.data[2] & 255) >> 4;
        switch (blockSizeCode) {
            case 1:
                return PsExtractor.AUDIO_STREAM;
            case 2:
            case 3:
            case 4:
            case 5:
                return 576 << (blockSizeCode - 2);
            case 6:
            case 7:
                packet.skipBytes(4);
                packet.readUtf8EncodedLong();
                int value = blockSizeCode == 6 ? packet.readUnsignedByte() : packet.readUnsignedShort();
                packet.setPosition(0);
                return value + 1;
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
                return 256 << (blockSizeCode - 8);
            default:
                return -1;
        }
    }
}
