package com.google.android.exoplayer2.extractor.ts;

import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.TimestampAdjuster;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;

final class PsDurationReader {
    private static final int TIMESTAMP_SEARCH_BYTES = 20000;
    private long durationUs = C0555C.TIME_UNSET;
    private long firstScrValue = C0555C.TIME_UNSET;
    private boolean isDurationRead;
    private boolean isFirstScrValueRead;
    private boolean isLastScrValueRead;
    private long lastScrValue = C0555C.TIME_UNSET;
    private final ParsableByteArray packetBuffer = new ParsableByteArray();
    private final TimestampAdjuster scrTimestampAdjuster = new TimestampAdjuster(0);

    PsDurationReader() {
    }

    public boolean isDurationReadFinished() {
        return this.isDurationRead;
    }

    public TimestampAdjuster getScrTimestampAdjuster() {
        return this.scrTimestampAdjuster;
    }

    public int readDuration(ExtractorInput input, PositionHolder seekPositionHolder) throws IOException, InterruptedException {
        if (!this.isLastScrValueRead) {
            return readLastScrValue(input, seekPositionHolder);
        }
        if (this.lastScrValue == C0555C.TIME_UNSET) {
            return finishReadDuration(input);
        }
        if (!this.isFirstScrValueRead) {
            return readFirstScrValue(input, seekPositionHolder);
        }
        long minScrPositionUs = this.firstScrValue;
        if (minScrPositionUs == C0555C.TIME_UNSET) {
            return finishReadDuration(input);
        }
        this.durationUs = this.scrTimestampAdjuster.adjustTsTimestamp(this.lastScrValue) - this.scrTimestampAdjuster.adjustTsTimestamp(minScrPositionUs);
        return finishReadDuration(input);
    }

    public long getDurationUs() {
        return this.durationUs;
    }

    public static long readScrValueFromPack(ParsableByteArray packetBuffer) {
        int originalPosition = packetBuffer.getPosition();
        if (packetBuffer.bytesLeft() < 9) {
            return C0555C.TIME_UNSET;
        }
        byte[] scrBytes = new byte[9];
        packetBuffer.readBytes(scrBytes, 0, scrBytes.length);
        packetBuffer.setPosition(originalPosition);
        if (checkMarkerBits(scrBytes)) {
            return readScrValueFromPackHeader(scrBytes);
        }
        return C0555C.TIME_UNSET;
    }

    private int finishReadDuration(ExtractorInput input) {
        this.packetBuffer.reset(Util.EMPTY_BYTE_ARRAY);
        this.isDurationRead = true;
        input.resetPeekPosition();
        return 0;
    }

    private int readFirstScrValue(ExtractorInput input, PositionHolder seekPositionHolder) throws IOException, InterruptedException {
        int bytesToSearch = (int) Math.min(20000, input.getLength());
        if (input.getPosition() != ((long) null)) {
            seekPositionHolder.position = (long) null;
            return 1;
        }
        this.packetBuffer.reset(bytesToSearch);
        input.resetPeekPosition();
        input.peekFully(this.packetBuffer.data, 0, bytesToSearch);
        this.firstScrValue = readFirstScrValueFromBuffer(this.packetBuffer);
        this.isFirstScrValueRead = true;
        return 0;
    }

    private long readFirstScrValueFromBuffer(ParsableByteArray packetBuffer) {
        int searchStartPosition = packetBuffer.getPosition();
        int searchEndPosition = packetBuffer.limit();
        for (int searchPosition = searchStartPosition; searchPosition < searchEndPosition - 3; searchPosition++) {
            if (peekIntAtPosition(packetBuffer.data, searchPosition) == 442) {
                packetBuffer.setPosition(searchPosition + 4);
                long scrValue = readScrValueFromPack(packetBuffer);
                if (scrValue != C0555C.TIME_UNSET) {
                    return scrValue;
                }
            }
        }
        return C0555C.TIME_UNSET;
    }

    private int readLastScrValue(ExtractorInput input, PositionHolder seekPositionHolder) throws IOException, InterruptedException {
        long inputLength = input.getLength();
        int bytesToSearch = (int) Math.min(20000, inputLength);
        long searchStartPosition = inputLength - ((long) bytesToSearch);
        if (input.getPosition() != searchStartPosition) {
            seekPositionHolder.position = searchStartPosition;
            return 1;
        }
        this.packetBuffer.reset(bytesToSearch);
        input.resetPeekPosition();
        input.peekFully(this.packetBuffer.data, 0, bytesToSearch);
        this.lastScrValue = readLastScrValueFromBuffer(this.packetBuffer);
        this.isLastScrValueRead = true;
        return 0;
    }

    private long readLastScrValueFromBuffer(ParsableByteArray packetBuffer) {
        int searchStartPosition = packetBuffer.getPosition();
        for (int searchPosition = packetBuffer.limit() - 4; searchPosition >= searchStartPosition; searchPosition--) {
            if (peekIntAtPosition(packetBuffer.data, searchPosition) == 442) {
                packetBuffer.setPosition(searchPosition + 4);
                long scrValue = readScrValueFromPack(packetBuffer);
                if (scrValue != C0555C.TIME_UNSET) {
                    return scrValue;
                }
            }
        }
        return C0555C.TIME_UNSET;
    }

    private int peekIntAtPosition(byte[] data, int position) {
        return ((((data[position] & 255) << 24) | ((data[position + 1] & 255) << 16)) | ((data[position + 2] & 255) << 8)) | (data[position + 3] & 255);
    }

    private static boolean checkMarkerBits(byte[] scrBytes) {
        boolean z = false;
        if ((scrBytes[0] & 196) != 68 || (scrBytes[2] & 4) != 4 || (scrBytes[4] & 4) != 4 || (scrBytes[5] & 1) != 1) {
            return false;
        }
        if ((scrBytes[8] & 3) == 3) {
            z = true;
        }
        return z;
    }

    private static long readScrValueFromPackHeader(byte[] scrBytes) {
        return ((((((((((long) scrBytes[0]) & 56) >> 3) << 30) | ((((long) scrBytes[0]) & 3) << 28)) | ((((long) scrBytes[1]) & 255) << 20)) | (((((long) scrBytes[2]) & 248) >> 3) << 15)) | ((((long) scrBytes[2]) & 3) << 13)) | ((((long) scrBytes[3]) & 255) << 5)) | ((((long) scrBytes[4]) & 248) >> 3);
    }
}
