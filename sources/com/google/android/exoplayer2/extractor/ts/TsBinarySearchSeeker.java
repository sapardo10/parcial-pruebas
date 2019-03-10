package com.google.android.exoplayer2.extractor.ts;

import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.extractor.BinarySearchSeeker;
import com.google.android.exoplayer2.extractor.BinarySearchSeeker.DefaultSeekTimestampConverter;
import com.google.android.exoplayer2.extractor.BinarySearchSeeker.OutputFrameHolder;
import com.google.android.exoplayer2.extractor.BinarySearchSeeker.TimestampSearchResult;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.TimestampAdjuster;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;

final class TsBinarySearchSeeker extends BinarySearchSeeker {
    private static final int MINIMUM_SEARCH_RANGE_BYTES = 940;
    private static final long SEEK_TOLERANCE_US = 100000;
    private static final int TIMESTAMP_SEARCH_BYTES = 112800;

    private static final class TsPcrSeeker implements TimestampSeeker {
        private final ParsableByteArray packetBuffer = new ParsableByteArray();
        private final int pcrPid;
        private final TimestampAdjuster pcrTimestampAdjuster;

        public TsPcrSeeker(int pcrPid, TimestampAdjuster pcrTimestampAdjuster) {
            this.pcrPid = pcrPid;
            this.pcrTimestampAdjuster = pcrTimestampAdjuster;
        }

        public TimestampSearchResult searchForTimestamp(ExtractorInput input, long targetTimestamp, OutputFrameHolder outputFrameHolder) throws IOException, InterruptedException {
            long inputPosition = input.getPosition();
            int bytesToSearch = (int) Math.min(112800, input.getLength() - inputPosition);
            this.packetBuffer.reset(bytesToSearch);
            input.peekFully(this.packetBuffer.data, 0, bytesToSearch);
            return searchForPcrValueInBuffer(this.packetBuffer, targetTimestamp, inputPosition);
        }

        private TimestampSearchResult searchForPcrValueInBuffer(ParsableByteArray packetBuffer, long targetPcrTimeUs, long bufferStartOffset) {
            long j;
            long endOfLastPacketPosition;
            TsPcrSeeker tsPcrSeeker = this;
            ParsableByteArray parsableByteArray = packetBuffer;
            long j2 = bufferStartOffset;
            int limit = packetBuffer.limit();
            long startOfLastPacketPosition = -1;
            long endOfLastPacketPosition2 = -1;
            long lastPcrTimeUsInRange = C0555C.TIME_UNSET;
            while (packetBuffer.bytesLeft() >= TsExtractor.TS_PACKET_SIZE) {
                int startOfPacket = TsUtil.findSyncBytePosition(parsableByteArray.data, packetBuffer.getPosition(), limit);
                int endOfPacket = startOfPacket + TsExtractor.TS_PACKET_SIZE;
                if (endOfPacket > limit) {
                    int i = limit;
                    j = startOfLastPacketPosition;
                    endOfLastPacketPosition = endOfLastPacketPosition2;
                    break;
                }
                endOfLastPacketPosition2 = TsUtil.readPcrFromPacket(parsableByteArray, startOfPacket, tsPcrSeeker.pcrPid);
                if (endOfLastPacketPosition2 != C0555C.TIME_UNSET) {
                    long pcrTimeUs = tsPcrSeeker.pcrTimestampAdjuster.adjustTsTimestamp(endOfLastPacketPosition2);
                    if (pcrTimeUs > targetPcrTimeUs) {
                        if (lastPcrTimeUsInRange == C0555C.TIME_UNSET) {
                            return TimestampSearchResult.overestimatedResult(pcrTimeUs, j2);
                        }
                        return TimestampSearchResult.targetFoundResult(j2 + startOfLastPacketPosition);
                    } else if (pcrTimeUs + TsBinarySearchSeeker.SEEK_TOLERANCE_US > targetPcrTimeUs) {
                        return TimestampSearchResult.targetFoundResult(((long) startOfPacket) + j2);
                    } else {
                        i = limit;
                        j = startOfLastPacketPosition;
                        startOfLastPacketPosition = (long) startOfPacket;
                        lastPcrTimeUsInRange = pcrTimeUs;
                    }
                } else {
                    i = limit;
                    j = startOfLastPacketPosition;
                }
                parsableByteArray.setPosition(endOfPacket);
                endOfLastPacketPosition2 = (long) endOfPacket;
                limit = i;
            }
            j = startOfLastPacketPosition;
            endOfLastPacketPosition = endOfLastPacketPosition2;
            if (lastPcrTimeUsInRange != C0555C.TIME_UNSET) {
                return TimestampSearchResult.underestimatedResult(lastPcrTimeUsInRange, j2 + endOfLastPacketPosition);
            }
            return TimestampSearchResult.NO_TIMESTAMP_IN_RANGE_RESULT;
        }

        public void onSeekFinished() {
            this.packetBuffer.reset(Util.EMPTY_BYTE_ARRAY);
        }
    }

    public TsBinarySearchSeeker(TimestampAdjuster pcrTimestampAdjuster, long streamDurationUs, long inputLength, int pcrPid) {
        long j = streamDurationUs;
        super(new DefaultSeekTimestampConverter(), new TsPcrSeeker(pcrPid, pcrTimestampAdjuster), j, 0, streamDurationUs + 1, 0, inputLength, 188, MINIMUM_SEARCH_RANGE_BYTES);
    }
}
