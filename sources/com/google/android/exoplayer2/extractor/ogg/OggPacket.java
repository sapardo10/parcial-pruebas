package com.google.android.exoplayer2.extractor.ogg;

import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.io.IOException;
import java.util.Arrays;

final class OggPacket {
    private int currentSegmentIndex = -1;
    private final ParsableByteArray packetArray = new ParsableByteArray(new byte[OggPageHeader.MAX_PAGE_PAYLOAD], 0);
    private final OggPageHeader pageHeader = new OggPageHeader();
    private boolean populated;
    private int segmentCount;

    OggPacket() {
    }

    public void reset() {
        this.pageHeader.reset();
        this.packetArray.reset();
        this.currentSegmentIndex = -1;
        this.populated = false;
    }

    public boolean populate(ExtractorInput input) throws IOException, InterruptedException {
        Assertions.checkState(input != null);
        if (this.populated) {
            this.populated = false;
            this.packetArray.reset();
        }
        while (!this.populated) {
            int segmentIndex;
            int bytesToSkip;
            if (this.currentSegmentIndex < 0) {
                if (!this.pageHeader.populate(input, true)) {
                    return false;
                }
                segmentIndex = 0;
                bytesToSkip = this.pageHeader.headerSize;
                if ((this.pageHeader.type & 1) == 1 && this.packetArray.limit() == 0) {
                    bytesToSkip += calculatePacketSize(0);
                    segmentIndex = 0 + this.segmentCount;
                }
                input.skipFully(bytesToSkip);
                this.currentSegmentIndex = segmentIndex;
            }
            segmentIndex = calculatePacketSize(this.currentSegmentIndex);
            bytesToSkip = this.currentSegmentIndex + this.segmentCount;
            if (segmentIndex > 0) {
                ParsableByteArray parsableByteArray;
                if (this.packetArray.capacity() < this.packetArray.limit() + segmentIndex) {
                    parsableByteArray = this.packetArray;
                    parsableByteArray.data = Arrays.copyOf(parsableByteArray.data, this.packetArray.limit() + segmentIndex);
                }
                input.readFully(this.packetArray.data, this.packetArray.limit(), segmentIndex);
                parsableByteArray = this.packetArray;
                parsableByteArray.setLimit(parsableByteArray.limit() + segmentIndex);
                this.populated = this.pageHeader.laces[bytesToSkip + -1] != 255;
            }
            this.currentSegmentIndex = bytesToSkip == this.pageHeader.pageSegmentCount ? -1 : bytesToSkip;
        }
        return true;
    }

    public OggPageHeader getPageHeader() {
        return this.pageHeader;
    }

    public ParsableByteArray getPayload() {
        return this.packetArray;
    }

    public void trimPayload() {
        if (this.packetArray.data.length != OggPageHeader.MAX_PAGE_PAYLOAD) {
            ParsableByteArray parsableByteArray = this.packetArray;
            parsableByteArray.data = Arrays.copyOf(parsableByteArray.data, Math.max(OggPageHeader.MAX_PAGE_PAYLOAD, this.packetArray.limit()));
        }
    }

    private int calculatePacketSize(int startSegmentIndex) {
        this.segmentCount = 0;
        int size = 0;
        while (this.segmentCount + startSegmentIndex < this.pageHeader.pageSegmentCount) {
            int segmentLength = this.pageHeader.laces;
            int i = this.segmentCount;
            this.segmentCount = i + 1;
            segmentLength = segmentLength[i + startSegmentIndex];
            size += segmentLength;
            if (segmentLength != 255) {
                break;
            }
        }
        return size;
    }
}
