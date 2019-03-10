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

final class PsBinarySearchSeeker extends BinarySearchSeeker {
    private static final int MINIMUM_SEARCH_RANGE_BYTES = 1000;
    private static final long SEEK_TOLERANCE_US = 100000;
    private static final int TIMESTAMP_SEARCH_BYTES = 20000;

    private static final class PsScrSeeker implements TimestampSeeker {
        private final ParsableByteArray packetBuffer;
        private final TimestampAdjuster scrTimestampAdjuster;

        private PsScrSeeker(TimestampAdjuster scrTimestampAdjuster) {
            this.scrTimestampAdjuster = scrTimestampAdjuster;
            this.packetBuffer = new ParsableByteArray();
        }

        public TimestampSearchResult searchForTimestamp(ExtractorInput input, long targetTimestamp, OutputFrameHolder outputFrameHolder) throws IOException, InterruptedException {
            long inputPosition = input.getPosition();
            int bytesToSearch = (int) Math.min(20000, input.getLength() - inputPosition);
            this.packetBuffer.reset(bytesToSearch);
            input.peekFully(this.packetBuffer.data, 0, bytesToSearch);
            return searchForScrValueInBuffer(this.packetBuffer, targetTimestamp, inputPosition);
        }

        public void onSeekFinished() {
            this.packetBuffer.reset(Util.EMPTY_BYTE_ARRAY);
        }

        private TimestampSearchResult searchForScrValueInBuffer(ParsableByteArray packetBuffer, long targetScrTimeUs, long bufferStartOffset) {
            PsScrSeeker psScrSeeker;
            ParsableByteArray parsableByteArray = packetBuffer;
            long j = bufferStartOffset;
            int startOfLastPacketPosition = -1;
            int endOfLastPacketPosition = -1;
            long lastScrTimeUsInRange = C0555C.TIME_UNSET;
            while (packetBuffer.bytesLeft() >= 4) {
                if (PsBinarySearchSeeker.peekIntAtPosition(parsableByteArray.data, packetBuffer.getPosition()) != 442) {
                    parsableByteArray.skipBytes(1);
                } else {
                    parsableByteArray.skipBytes(4);
                    long scrValue = PsDurationReader.readScrValueFromPack(packetBuffer);
                    if (scrValue != C0555C.TIME_UNSET) {
                        long scrTimeUs = this.scrTimestampAdjuster.adjustTsTimestamp(scrValue);
                        if (scrTimeUs > targetScrTimeUs) {
                            if (lastScrTimeUsInRange == C0555C.TIME_UNSET) {
                                return TimestampSearchResult.overestimatedResult(scrTimeUs, j);
                            }
                            return TimestampSearchResult.targetFoundResult(((long) startOfLastPacketPosition) + j);
                        } else if (PsBinarySearchSeeker.SEEK_TOLERANCE_US + scrTimeUs > targetScrTimeUs) {
                            return TimestampSearchResult.targetFoundResult(((long) packetBuffer.getPosition()) + j);
                        } else {
                            lastScrTimeUsInRange = scrTimeUs;
                            startOfLastPacketPosition = packetBuffer.getPosition();
                        }
                    } else {
                        psScrSeeker = this;
                    }
                    skipToEndOfCurrentPack(packetBuffer);
                    endOfLastPacketPosition = packetBuffer.getPosition();
                }
            }
            psScrSeeker = this;
            if (lastScrTimeUsInRange != C0555C.TIME_UNSET) {
                return TimestampSearchResult.underestimatedResult(lastScrTimeUsInRange, ((long) endOfLastPacketPosition) + j);
            }
            return TimestampSearchResult.NO_TIMESTAMP_IN_RANGE_RESULT;
        }

        private static void skipToEndOfCurrentPack(ParsableByteArray packetBuffer) {
            int limit = packetBuffer.limit();
            if (packetBuffer.bytesLeft() < 10) {
                packetBuffer.setPosition(limit);
                return;
            }
            packetBuffer.skipBytes(9);
            int packStuffingLength = packetBuffer.readUnsignedByte() & 7;
            if (packetBuffer.bytesLeft() < packStuffingLength) {
                packetBuffer.setPosition(limit);
                return;
            }
            packetBuffer.skipBytes(packStuffingLength);
            if (packetBuffer.bytesLeft() < 4) {
                packetBuffer.setPosition(limit);
                return;
            }
            if (PsBinarySearchSeeker.peekIntAtPosition(packetBuffer.data, packetBuffer.getPosition()) == 443) {
                packetBuffer.skipBytes(4);
                int systemHeaderLength = packetBuffer.readUnsignedShort();
                if (packetBuffer.bytesLeft() < systemHeaderLength) {
                    packetBuffer.setPosition(limit);
                    return;
                }
                packetBuffer.skipBytes(systemHeaderLength);
            }
            while (packetBuffer.bytesLeft() >= 4) {
                int nextStartCode = PsBinarySearchSeeker.peekIntAtPosition(packetBuffer.data, packetBuffer.getPosition());
                if (nextStartCode == 442) {
                    break;
                } else if (nextStartCode == 441) {
                    break;
                } else if ((nextStartCode >>> 8) != 1) {
                    break;
                } else {
                    packetBuffer.skipBytes(4);
                    if (packetBuffer.bytesLeft() < 2) {
                        packetBuffer.setPosition(limit);
                        return;
                    } else {
                        packetBuffer.setPosition(Math.min(packetBuffer.limit(), packetBuffer.getPosition() + packetBuffer.readUnsignedShort()));
                    }
                }
            }
        }
    }

    public PsBinarySearchSeeker(TimestampAdjuster scrTimestampAdjuster, long streamDurationUs, long inputLength) {
        super(new DefaultSeekTimestampConverter(), new PsScrSeeker(scrTimestampAdjuster), streamDurationUs, 0, streamDurationUs + 1, 0, inputLength, 188, 1000);
    }

    private static int peekIntAtPosition(byte[] data, int position) {
        return ((((data[position] & 255) << 24) | ((data[position + 1] & 255) << 16)) | ((data[position + 2] & 255) << 8)) | (data[position + 3] & 255);
    }
}
