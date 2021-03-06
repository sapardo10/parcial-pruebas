package com.google.android.exoplayer2.extractor.mp4;

import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.io.IOException;

final class TrackFragment {
    public long atomPosition;
    public long auxiliaryDataPosition;
    public long dataPosition;
    public boolean definesEncryptionData;
    public DefaultSampleValues header;
    public long nextFragmentDecodeTime;
    public int[] sampleCompositionTimeOffsetTable;
    public int sampleCount;
    public long[] sampleDecodingTimeTable;
    public ParsableByteArray sampleEncryptionData;
    public int sampleEncryptionDataLength;
    public boolean sampleEncryptionDataNeedsFill;
    public boolean[] sampleHasSubsampleEncryptionTable;
    public boolean[] sampleIsSyncFrameTable;
    public int[] sampleSizeTable;
    public TrackEncryptionBox trackEncryptionBox;
    public int trunCount;
    public long[] trunDataPosition;
    public int[] trunLength;

    TrackFragment() {
    }

    public void reset() {
        this.trunCount = 0;
        this.nextFragmentDecodeTime = 0;
        this.definesEncryptionData = false;
        this.sampleEncryptionDataNeedsFill = false;
        this.trackEncryptionBox = null;
    }

    public void initTables(int trunCount, int sampleCount) {
        int tableSize;
        this.trunCount = trunCount;
        this.sampleCount = sampleCount;
        int[] iArr = this.trunLength;
        if (iArr != null) {
            if (iArr.length >= trunCount) {
                iArr = this.sampleSizeTable;
                if (iArr != null) {
                    if (iArr.length < sampleCount) {
                        return;
                    }
                }
                tableSize = (sampleCount * 125) / 100;
                this.sampleSizeTable = new int[tableSize];
                this.sampleCompositionTimeOffsetTable = new int[tableSize];
                this.sampleDecodingTimeTable = new long[tableSize];
                this.sampleIsSyncFrameTable = new boolean[tableSize];
                this.sampleHasSubsampleEncryptionTable = new boolean[tableSize];
            }
        }
        this.trunDataPosition = new long[trunCount];
        this.trunLength = new int[trunCount];
        iArr = this.sampleSizeTable;
        if (iArr != null) {
            if (iArr.length < sampleCount) {
                return;
            }
        }
        tableSize = (sampleCount * 125) / 100;
        this.sampleSizeTable = new int[tableSize];
        this.sampleCompositionTimeOffsetTable = new int[tableSize];
        this.sampleDecodingTimeTable = new long[tableSize];
        this.sampleIsSyncFrameTable = new boolean[tableSize];
        this.sampleHasSubsampleEncryptionTable = new boolean[tableSize];
    }

    public void initEncryptionData(int length) {
        ParsableByteArray parsableByteArray = this.sampleEncryptionData;
        if (parsableByteArray != null) {
            if (parsableByteArray.limit() >= length) {
                this.sampleEncryptionDataLength = length;
                this.definesEncryptionData = true;
                this.sampleEncryptionDataNeedsFill = true;
            }
        }
        this.sampleEncryptionData = new ParsableByteArray(length);
        this.sampleEncryptionDataLength = length;
        this.definesEncryptionData = true;
        this.sampleEncryptionDataNeedsFill = true;
    }

    public void fillEncryptionData(ExtractorInput input) throws IOException, InterruptedException {
        input.readFully(this.sampleEncryptionData.data, 0, this.sampleEncryptionDataLength);
        this.sampleEncryptionData.setPosition(0);
        this.sampleEncryptionDataNeedsFill = false;
    }

    public void fillEncryptionData(ParsableByteArray source) {
        source.readBytes(this.sampleEncryptionData.data, 0, this.sampleEncryptionDataLength);
        this.sampleEncryptionData.setPosition(0);
        this.sampleEncryptionDataNeedsFill = false;
    }

    public long getSamplePresentationTime(int index) {
        return this.sampleDecodingTimeTable[index] + ((long) this.sampleCompositionTimeOffsetTable[index]);
    }

    public boolean sampleHasSubsampleEncryptionTable(int index) {
        return this.definesEncryptionData && this.sampleHasSubsampleEncryptionTable[index];
    }
}
