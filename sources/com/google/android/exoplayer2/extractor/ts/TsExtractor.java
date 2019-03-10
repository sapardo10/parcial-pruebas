package com.google.android.exoplayer2.extractor.ts;

import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.extractor.SeekMap.Unseekable;
import com.google.android.exoplayer2.extractor.ts.TsPayloadReader.DvbSubtitleInfo;
import com.google.android.exoplayer2.extractor.ts.TsPayloadReader.EsInfo;
import com.google.android.exoplayer2.extractor.ts.TsPayloadReader.Factory;
import com.google.android.exoplayer2.extractor.ts.TsPayloadReader.TrackIdGenerator;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.ParsableBitArray;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.TimestampAdjuster;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class TsExtractor implements Extractor {
    private static final long AC3_FORMAT_IDENTIFIER = ((long) Util.getIntegerCodeForString("AC-3"));
    private static final int BUFFER_SIZE = 9400;
    private static final long E_AC3_FORMAT_IDENTIFIER = ((long) Util.getIntegerCodeForString("EAC3"));
    public static final ExtractorsFactory FACTORY = -$$Lambda$TsExtractor$f-UE6PC86cqq4V-qVoFQnPhfFZ8.INSTANCE;
    private static final long HEVC_FORMAT_IDENTIFIER = ((long) Util.getIntegerCodeForString("HEVC"));
    private static final int MAX_PID_PLUS_ONE = 8192;
    public static final int MODE_HLS = 2;
    public static final int MODE_MULTI_PMT = 0;
    public static final int MODE_SINGLE_PMT = 1;
    private static final int SNIFF_TS_PACKET_COUNT = 5;
    public static final int TS_PACKET_SIZE = 188;
    private static final int TS_PAT_PID = 0;
    public static final int TS_STREAM_TYPE_AAC_ADTS = 15;
    public static final int TS_STREAM_TYPE_AAC_LATM = 17;
    public static final int TS_STREAM_TYPE_AC3 = 129;
    public static final int TS_STREAM_TYPE_DTS = 138;
    public static final int TS_STREAM_TYPE_DVBSUBS = 89;
    public static final int TS_STREAM_TYPE_E_AC3 = 135;
    public static final int TS_STREAM_TYPE_H262 = 2;
    public static final int TS_STREAM_TYPE_H264 = 27;
    public static final int TS_STREAM_TYPE_H265 = 36;
    public static final int TS_STREAM_TYPE_HDMV_DTS = 130;
    public static final int TS_STREAM_TYPE_ID3 = 21;
    public static final int TS_STREAM_TYPE_MPA = 3;
    public static final int TS_STREAM_TYPE_MPA_LSF = 4;
    public static final int TS_STREAM_TYPE_SPLICE_INFO = 134;
    public static final int TS_SYNC_BYTE = 71;
    private int bytesSinceLastSync;
    private final SparseIntArray continuityCounters;
    private final TsDurationReader durationReader;
    private boolean hasOutputSeekMap;
    private TsPayloadReader id3Reader;
    private final int mode;
    private ExtractorOutput output;
    private final Factory payloadReaderFactory;
    private int pcrPid;
    private boolean pendingSeekToStart;
    private int remainingPmts;
    private final List<TimestampAdjuster> timestampAdjusters;
    private final SparseBooleanArray trackIds;
    private final SparseBooleanArray trackPids;
    private boolean tracksEnded;
    private TsBinarySearchSeeker tsBinarySearchSeeker;
    private final ParsableByteArray tsPacketBuffer;
    private final SparseArray<TsPayloadReader> tsPayloadReaders;

    private class PatReader implements SectionPayloadReader {
        private final ParsableBitArray patScratch = new ParsableBitArray(new byte[4]);

        public void init(TimestampAdjuster timestampAdjuster, ExtractorOutput extractorOutput, TrackIdGenerator idGenerator) {
        }

        public void consume(ParsableByteArray sectionData) {
            if (sectionData.readUnsignedByte() == 0) {
                sectionData.skipBytes(7);
                int programCount = sectionData.bytesLeft() / 4;
                for (int i = 0; i < programCount; i++) {
                    sectionData.readBytes(this.patScratch, 4);
                    int programNumber = this.patScratch.readBits(16);
                    this.patScratch.skipBits(3);
                    if (programNumber == 0) {
                        this.patScratch.skipBits(13);
                    } else {
                        int pid = this.patScratch.readBits(13);
                        TsExtractor.this.tsPayloadReaders.put(pid, new SectionReader(new PmtReader(pid)));
                        TsExtractor.this.remainingPmts = TsExtractor.this.remainingPmts + 1;
                    }
                }
                if (TsExtractor.this.mode != 2) {
                    TsExtractor.this.tsPayloadReaders.remove(0);
                }
            }
        }
    }

    private class PmtReader implements SectionPayloadReader {
        private static final int TS_PMT_DESC_AC3 = 106;
        private static final int TS_PMT_DESC_DTS = 123;
        private static final int TS_PMT_DESC_DVBSUBS = 89;
        private static final int TS_PMT_DESC_EAC3 = 122;
        private static final int TS_PMT_DESC_ISO639_LANG = 10;
        private static final int TS_PMT_DESC_REGISTRATION = 5;
        private final int pid;
        private final ParsableBitArray pmtScratch = new ParsableBitArray(new byte[5]);
        private final SparseIntArray trackIdToPidScratch = new SparseIntArray();
        private final SparseArray<TsPayloadReader> trackIdToReaderScratch = new SparseArray();

        public PmtReader(int pid) {
            this.pid = pid;
        }

        public void init(TimestampAdjuster timestampAdjuster, ExtractorOutput extractorOutput, TrackIdGenerator idGenerator) {
        }

        public void consume(ParsableByteArray sectionData) {
            PmtReader pmtReader = this;
            ParsableByteArray parsableByteArray = sectionData;
            if (sectionData.readUnsignedByte() == 2) {
                TimestampAdjuster timestampAdjuster;
                int programNumber;
                int i;
                int i2;
                int i3;
                int i4;
                int i5;
                TsExtractor tsExtractor;
                int remainingEntriesLength;
                int streamType;
                int elementaryPid;
                int esInfoLength;
                EsInfo esInfo;
                TsPayloadReader reader;
                if (!(TsExtractor.this.mode == 1 || TsExtractor.this.mode == 2)) {
                    if (TsExtractor.this.remainingPmts != 1) {
                        timestampAdjuster = new TimestampAdjuster(((TimestampAdjuster) TsExtractor.this.timestampAdjusters.get(0)).getFirstSampleTimestampUs());
                        TsExtractor.this.timestampAdjusters.add(timestampAdjuster);
                        parsableByteArray.skipBytes(2);
                        programNumber = sectionData.readUnsignedShort();
                        i = 3;
                        parsableByteArray.skipBytes(3);
                        parsableByteArray.readBytes(pmtReader.pmtScratch, 2);
                        pmtReader.pmtScratch.skipBits(3);
                        i2 = 13;
                        TsExtractor.this.pcrPid = pmtReader.pmtScratch.readBits(13);
                        parsableByteArray.readBytes(pmtReader.pmtScratch, 2);
                        i3 = 4;
                        pmtReader.pmtScratch.skipBits(4);
                        i4 = 12;
                        parsableByteArray.skipBytes(pmtReader.pmtScratch.readBits(12));
                        i5 = 21;
                        if (TsExtractor.this.mode != 2 && TsExtractor.this.id3Reader == null) {
                            EsInfo dummyEsInfo = new EsInfo(21, null, null, Util.EMPTY_BYTE_ARRAY);
                            tsExtractor = TsExtractor.this;
                            tsExtractor.id3Reader = tsExtractor.payloadReaderFactory.createPayloadReader(21, dummyEsInfo);
                            TsExtractor.this.id3Reader.init(timestampAdjuster, TsExtractor.this.output, new TrackIdGenerator(programNumber, 21, 8192));
                        }
                        pmtReader.trackIdToReaderScratch.clear();
                        pmtReader.trackIdToPidScratch.clear();
                        remainingEntriesLength = sectionData.bytesLeft();
                        while (remainingEntriesLength > 0) {
                            parsableByteArray.readBytes(pmtReader.pmtScratch, 5);
                            streamType = pmtReader.pmtScratch.readBits(8);
                            pmtReader.pmtScratch.skipBits(i);
                            elementaryPid = pmtReader.pmtScratch.readBits(i2);
                            pmtReader.pmtScratch.skipBits(i3);
                            esInfoLength = pmtReader.pmtScratch.readBits(i4);
                            esInfo = readEsInfo(parsableByteArray, esInfoLength);
                            if (streamType == 6) {
                                streamType = esInfo.streamType;
                            }
                            remainingEntriesLength -= esInfoLength + 5;
                            i3 = TsExtractor.this.mode != 2 ? streamType : elementaryPid;
                            if (TsExtractor.this.trackIds.get(i3)) {
                                if (TsExtractor.this.mode == 2 || streamType != r15) {
                                    reader = TsExtractor.this.payloadReaderFactory.createPayloadReader(streamType, esInfo);
                                } else {
                                    reader = TsExtractor.this.id3Reader;
                                }
                                if (TsExtractor.this.mode == 2) {
                                    if (elementaryPid >= pmtReader.trackIdToPidScratch.get(i3, 8192)) {
                                    }
                                }
                                pmtReader.trackIdToPidScratch.put(i3, elementaryPid);
                                pmtReader.trackIdToReaderScratch.put(i3, reader);
                            }
                            i = 3;
                            i3 = 4;
                            i2 = 13;
                            i4 = 12;
                            i5 = 21;
                        }
                        streamType = pmtReader.trackIdToPidScratch.size();
                        for (elementaryPid = 0; elementaryPid < streamType; elementaryPid++) {
                            i = pmtReader.trackIdToPidScratch.keyAt(elementaryPid);
                            i3 = pmtReader.trackIdToPidScratch.valueAt(elementaryPid);
                            TsExtractor.this.trackIds.put(i, true);
                            TsExtractor.this.trackPids.put(i3, true);
                            reader = (TsPayloadReader) pmtReader.trackIdToReaderScratch.valueAt(elementaryPid);
                            if (reader != null) {
                                if (reader != TsExtractor.this.id3Reader) {
                                    reader.init(timestampAdjuster, TsExtractor.this.output, new TrackIdGenerator(programNumber, i, 8192));
                                }
                                TsExtractor.this.tsPayloadReaders.put(i3, reader);
                            }
                        }
                        if (TsExtractor.this.mode == 2) {
                            i = 0;
                            TsExtractor.this.tsPayloadReaders.remove(pmtReader.pid);
                            tsExtractor = TsExtractor.this;
                            if (tsExtractor.mode == 1) {
                                i = TsExtractor.this.remainingPmts - 1;
                            }
                            tsExtractor.remainingPmts = i;
                            if (TsExtractor.this.remainingPmts == 0) {
                                TsExtractor.this.output.endTracks();
                                TsExtractor.this.tracksEnded = true;
                            }
                        } else if (!TsExtractor.this.tracksEnded) {
                            TsExtractor.this.output.endTracks();
                            TsExtractor.this.remainingPmts = 0;
                            TsExtractor.this.tracksEnded = true;
                        }
                    }
                }
                timestampAdjuster = (TimestampAdjuster) TsExtractor.this.timestampAdjusters.get(0);
                parsableByteArray.skipBytes(2);
                programNumber = sectionData.readUnsignedShort();
                i = 3;
                parsableByteArray.skipBytes(3);
                parsableByteArray.readBytes(pmtReader.pmtScratch, 2);
                pmtReader.pmtScratch.skipBits(3);
                i2 = 13;
                TsExtractor.this.pcrPid = pmtReader.pmtScratch.readBits(13);
                parsableByteArray.readBytes(pmtReader.pmtScratch, 2);
                i3 = 4;
                pmtReader.pmtScratch.skipBits(4);
                i4 = 12;
                parsableByteArray.skipBytes(pmtReader.pmtScratch.readBits(12));
                i5 = 21;
                if (TsExtractor.this.mode != 2) {
                }
                pmtReader.trackIdToReaderScratch.clear();
                pmtReader.trackIdToPidScratch.clear();
                remainingEntriesLength = sectionData.bytesLeft();
                while (remainingEntriesLength > 0) {
                    parsableByteArray.readBytes(pmtReader.pmtScratch, 5);
                    streamType = pmtReader.pmtScratch.readBits(8);
                    pmtReader.pmtScratch.skipBits(i);
                    elementaryPid = pmtReader.pmtScratch.readBits(i2);
                    pmtReader.pmtScratch.skipBits(i3);
                    esInfoLength = pmtReader.pmtScratch.readBits(i4);
                    esInfo = readEsInfo(parsableByteArray, esInfoLength);
                    if (streamType == 6) {
                        streamType = esInfo.streamType;
                    }
                    remainingEntriesLength -= esInfoLength + 5;
                    if (TsExtractor.this.mode != 2) {
                    }
                    if (TsExtractor.this.trackIds.get(i3)) {
                        if (TsExtractor.this.mode == 2) {
                        }
                        reader = TsExtractor.this.payloadReaderFactory.createPayloadReader(streamType, esInfo);
                        if (TsExtractor.this.mode == 2) {
                            if (elementaryPid >= pmtReader.trackIdToPidScratch.get(i3, 8192)) {
                            }
                        }
                        pmtReader.trackIdToPidScratch.put(i3, elementaryPid);
                        pmtReader.trackIdToReaderScratch.put(i3, reader);
                    }
                    i = 3;
                    i3 = 4;
                    i2 = 13;
                    i4 = 12;
                    i5 = 21;
                }
                streamType = pmtReader.trackIdToPidScratch.size();
                for (elementaryPid = 0; elementaryPid < streamType; elementaryPid++) {
                    i = pmtReader.trackIdToPidScratch.keyAt(elementaryPid);
                    i3 = pmtReader.trackIdToPidScratch.valueAt(elementaryPid);
                    TsExtractor.this.trackIds.put(i, true);
                    TsExtractor.this.trackPids.put(i3, true);
                    reader = (TsPayloadReader) pmtReader.trackIdToReaderScratch.valueAt(elementaryPid);
                    if (reader != null) {
                        if (reader != TsExtractor.this.id3Reader) {
                            reader.init(timestampAdjuster, TsExtractor.this.output, new TrackIdGenerator(programNumber, i, 8192));
                        }
                        TsExtractor.this.tsPayloadReaders.put(i3, reader);
                    }
                }
                if (TsExtractor.this.mode == 2) {
                    i = 0;
                    TsExtractor.this.tsPayloadReaders.remove(pmtReader.pid);
                    tsExtractor = TsExtractor.this;
                    if (tsExtractor.mode == 1) {
                        i = TsExtractor.this.remainingPmts - 1;
                    }
                    tsExtractor.remainingPmts = i;
                    if (TsExtractor.this.remainingPmts == 0) {
                        TsExtractor.this.output.endTracks();
                        TsExtractor.this.tracksEnded = true;
                    }
                } else if (!TsExtractor.this.tracksEnded) {
                    TsExtractor.this.output.endTracks();
                    TsExtractor.this.remainingPmts = 0;
                    TsExtractor.this.tracksEnded = true;
                }
            }
        }

        private EsInfo readEsInfo(ParsableByteArray data, int length) {
            ParsableByteArray parsableByteArray = data;
            int descriptorsStartPosition = data.getPosition();
            int descriptorsEndPosition = descriptorsStartPosition + length;
            int streamType = -1;
            String language = null;
            List<DvbSubtitleInfo> dvbSubtitleInfos = null;
            while (data.getPosition() < descriptorsEndPosition) {
                int descriptorTag = data.readUnsignedByte();
                int positionOfNextDescriptor = data.getPosition() + data.readUnsignedByte();
                if (descriptorTag == 5) {
                    long formatIdentifier = data.readUnsignedInt();
                    if (formatIdentifier == TsExtractor.AC3_FORMAT_IDENTIFIER) {
                        streamType = TsExtractor.TS_STREAM_TYPE_AC3;
                    } else if (formatIdentifier == TsExtractor.E_AC3_FORMAT_IDENTIFIER) {
                        streamType = TsExtractor.TS_STREAM_TYPE_E_AC3;
                    } else if (formatIdentifier == TsExtractor.HEVC_FORMAT_IDENTIFIER) {
                        streamType = 36;
                    }
                } else if (descriptorTag == 106) {
                    streamType = TsExtractor.TS_STREAM_TYPE_AC3;
                } else if (descriptorTag == TS_PMT_DESC_EAC3) {
                    streamType = TsExtractor.TS_STREAM_TYPE_E_AC3;
                } else if (descriptorTag == TS_PMT_DESC_DTS) {
                    streamType = TsExtractor.TS_STREAM_TYPE_DTS;
                } else if (descriptorTag == 10) {
                    language = parsableByteArray.readString(3).trim();
                } else if (descriptorTag == 89) {
                    streamType = 89;
                    dvbSubtitleInfos = new ArrayList();
                    while (data.getPosition() < positionOfNextDescriptor) {
                        String dvbLanguage = parsableByteArray.readString(3).trim();
                        int dvbSubtitlingType = data.readUnsignedByte();
                        byte[] initializationData = new byte[4];
                        parsableByteArray.readBytes(initializationData, 0, 4);
                        dvbSubtitleInfos.add(new DvbSubtitleInfo(dvbLanguage, dvbSubtitlingType, initializationData));
                    }
                }
                parsableByteArray.skipBytes(positionOfNextDescriptor - data.getPosition());
            }
            parsableByteArray.setPosition(descriptorsEndPosition);
            return new EsInfo(streamType, language, dvbSubtitleInfos, Arrays.copyOfRange(parsableByteArray.data, descriptorsStartPosition, descriptorsEndPosition));
        }
    }

    public TsExtractor() {
        this(0);
    }

    public TsExtractor(int defaultTsPayloadReaderFlags) {
        this(1, defaultTsPayloadReaderFlags);
    }

    public TsExtractor(int mode, int defaultTsPayloadReaderFlags) {
        this(mode, new TimestampAdjuster(0), new DefaultTsPayloadReaderFactory(defaultTsPayloadReaderFlags));
    }

    public TsExtractor(int mode, TimestampAdjuster timestampAdjuster, Factory payloadReaderFactory) {
        this.payloadReaderFactory = (Factory) Assertions.checkNotNull(payloadReaderFactory);
        this.mode = mode;
        if (mode != 1) {
            if (mode != 2) {
                this.timestampAdjusters = new ArrayList();
                this.timestampAdjusters.add(timestampAdjuster);
                this.tsPacketBuffer = new ParsableByteArray(new byte[BUFFER_SIZE], 0);
                this.trackIds = new SparseBooleanArray();
                this.trackPids = new SparseBooleanArray();
                this.tsPayloadReaders = new SparseArray();
                this.continuityCounters = new SparseIntArray();
                this.durationReader = new TsDurationReader();
                this.pcrPid = -1;
                resetPayloadReaders();
            }
        }
        this.timestampAdjusters = Collections.singletonList(timestampAdjuster);
        this.tsPacketBuffer = new ParsableByteArray(new byte[BUFFER_SIZE], 0);
        this.trackIds = new SparseBooleanArray();
        this.trackPids = new SparseBooleanArray();
        this.tsPayloadReaders = new SparseArray();
        this.continuityCounters = new SparseIntArray();
        this.durationReader = new TsDurationReader();
        this.pcrPid = -1;
        resetPayloadReaders();
    }

    public boolean sniff(ExtractorInput input) throws IOException, InterruptedException {
        byte[] buffer = this.tsPacketBuffer.data;
        input.peekFully(buffer, 0, 940);
        for (int startPosCandidate = 0; startPosCandidate < TS_PACKET_SIZE; startPosCandidate++) {
            boolean isSyncBytePatternCorrect = true;
            for (int i = 0; i < 5; i++) {
                if (buffer[(i * TS_PACKET_SIZE) + startPosCandidate] != (byte) 71) {
                    isSyncBytePatternCorrect = false;
                    break;
                }
            }
            if (isSyncBytePatternCorrect) {
                input.skipFully(startPosCandidate);
                return true;
            }
        }
        return false;
    }

    public void init(ExtractorOutput output) {
        this.output = output;
    }

    public void seek(long position, long timeUs) {
        int i;
        long j = timeUs;
        Assertions.checkState(this.mode != 2);
        int timestampAdjustersCount = r0.timestampAdjusters.size();
        for (int i2 = 0; i2 < timestampAdjustersCount; i2++) {
            TimestampAdjuster timestampAdjuster = (TimestampAdjuster) r0.timestampAdjusters.get(i2);
            if (!(timestampAdjuster.getTimestampOffsetUs() == C0555C.TIME_UNSET)) {
                if (timestampAdjuster.getTimestampOffsetUs() != 0) {
                    if (timestampAdjuster.getFirstSampleTimestampUs() != j) {
                    }
                }
            }
            timestampAdjuster.reset();
            timestampAdjuster.setFirstSampleTimestampUs(j);
        }
        if (j != 0) {
            TsBinarySearchSeeker tsBinarySearchSeeker = r0.tsBinarySearchSeeker;
            if (tsBinarySearchSeeker != null) {
                tsBinarySearchSeeker.setSeekTargetUs(j);
                r0.tsPacketBuffer.reset();
                r0.continuityCounters.clear();
                for (i = 0; i < r0.tsPayloadReaders.size(); i++) {
                    ((TsPayloadReader) r0.tsPayloadReaders.valueAt(i)).seek();
                }
                r0.bytesSinceLastSync = 0;
            }
        }
        r0.tsPacketBuffer.reset();
        r0.continuityCounters.clear();
        for (i = 0; i < r0.tsPayloadReaders.size(); i++) {
            ((TsPayloadReader) r0.tsPayloadReaders.valueAt(i)).seek();
        }
        r0.bytesSinceLastSync = 0;
    }

    public void release() {
    }

    public int read(ExtractorInput input, PositionHolder seekPosition) throws IOException, InterruptedException {
        ExtractorInput extractorInput = input;
        PositionHolder positionHolder = seekPosition;
        long inputLength = input.getLength();
        TsPayloadReader payloadReader = null;
        if (this.tracksEnded) {
            boolean canReadDuration = (inputLength == -1 || r0.mode == 2) ? false : true;
            if (canReadDuration && !r0.durationReader.isDurationReadFinished()) {
                return r0.durationReader.readDuration(extractorInput, positionHolder, r0.pcrPid);
            }
            maybeOutputSeekMap(inputLength);
            if (r0.pendingSeekToStart) {
                r0.pendingSeekToStart = false;
                seek(0, 0);
                if (input.getPosition() != 0) {
                    positionHolder.position = 0;
                    return 1;
                }
            }
            TsBinarySearchSeeker tsBinarySearchSeeker = r0.tsBinarySearchSeeker;
            if (tsBinarySearchSeeker != null && tsBinarySearchSeeker.isSeeking()) {
                return r0.tsBinarySearchSeeker.handlePendingSeek(extractorInput, positionHolder, null);
            }
        }
        if (!fillBufferWithAtLeastOnePacket(input)) {
            return -1;
        }
        int endOfPacket = findEndOfFirstTsPacketInBuffer();
        int limit = r0.tsPacketBuffer.limit();
        if (endOfPacket > limit) {
            return 0;
        }
        int tsPacketHeader = r0.tsPacketBuffer.readInt();
        if ((8388608 & tsPacketHeader) != 0) {
            r0.tsPacketBuffer.setPosition(endOfPacket);
            return 0;
        }
        int packetHeaderFlags = 0 | ((4194304 & tsPacketHeader) != 0 ? 1 : 0);
        int pid = (2096896 & tsPacketHeader) >> 8;
        boolean adaptationFieldExists = (tsPacketHeader & 32) != 0;
        if ((tsPacketHeader & 16) != 0) {
            payloadReader = (TsPayloadReader) r0.tsPayloadReaders.get(pid);
        }
        if (payloadReader == null) {
            r0.tsPacketBuffer.setPosition(endOfPacket);
            return 0;
        }
        if (r0.mode != 2) {
            int continuityCounter = tsPacketHeader & 15;
            int previousCounter = r0.continuityCounters.get(pid, continuityCounter - 1);
            r0.continuityCounters.put(pid, continuityCounter);
            if (previousCounter == continuityCounter) {
                r0.tsPacketBuffer.setPosition(endOfPacket);
                return 0;
            } else if (continuityCounter != ((previousCounter + 1) & 15)) {
                payloadReader.seek();
            }
        }
        if (adaptationFieldExists) {
            packetHeaderFlags |= (r0.tsPacketBuffer.readUnsignedByte() & 64) != 0 ? 2 : 0;
            r0.tsPacketBuffer.skipBytes(r0.tsPacketBuffer.readUnsignedByte() - 1);
        }
        boolean wereTracksEnded = r0.tracksEnded;
        if (shouldConsumePacketPayload(pid)) {
            r0.tsPacketBuffer.setLimit(endOfPacket);
            payloadReader.consume(r0.tsPacketBuffer, packetHeaderFlags);
            r0.tsPacketBuffer.setLimit(limit);
        }
        if (r0.mode != 2 && !wereTracksEnded && r0.tracksEnded && inputLength != -1) {
            r0.pendingSeekToStart = true;
        }
        r0.tsPacketBuffer.setPosition(endOfPacket);
        return 0;
    }

    private void maybeOutputSeekMap(long inputLength) {
        if (!this.hasOutputSeekMap) {
            this.hasOutputSeekMap = true;
            if (this.durationReader.getDurationUs() != C0555C.TIME_UNSET) {
                this.tsBinarySearchSeeker = new TsBinarySearchSeeker(this.durationReader.getPcrTimestampAdjuster(), this.durationReader.getDurationUs(), inputLength, this.pcrPid);
                this.output.seekMap(this.tsBinarySearchSeeker.getSeekMap());
                return;
            }
            this.output.seekMap(new Unseekable(this.durationReader.getDurationUs()));
        }
    }

    private boolean fillBufferWithAtLeastOnePacket(ExtractorInput input) throws IOException, InterruptedException {
        int bytesLeft;
        byte[] data = this.tsPacketBuffer.data;
        if (9400 - this.tsPacketBuffer.getPosition() < TS_PACKET_SIZE) {
            bytesLeft = this.tsPacketBuffer.bytesLeft();
            if (bytesLeft > 0) {
                System.arraycopy(data, this.tsPacketBuffer.getPosition(), data, 0, bytesLeft);
            }
            this.tsPacketBuffer.reset(data, bytesLeft);
        }
        while (this.tsPacketBuffer.bytesLeft() < TS_PACKET_SIZE) {
            bytesLeft = this.tsPacketBuffer.limit();
            int read = input.read(data, bytesLeft, 9400 - bytesLeft);
            if (read == -1) {
                return false;
            }
            this.tsPacketBuffer.setLimit(bytesLeft + read);
        }
        return true;
    }

    private int findEndOfFirstTsPacketInBuffer() throws ParserException {
        int searchStart = this.tsPacketBuffer.getPosition();
        int limit = this.tsPacketBuffer.limit();
        int syncBytePosition = TsUtil.findSyncBytePosition(this.tsPacketBuffer.data, searchStart, limit);
        this.tsPacketBuffer.setPosition(syncBytePosition);
        int endOfPacket = syncBytePosition + TS_PACKET_SIZE;
        if (endOfPacket > limit) {
            this.bytesSinceLastSync += syncBytePosition - searchStart;
            if (this.mode == 2) {
                if (this.bytesSinceLastSync > 376) {
                    throw new ParserException("Cannot find sync byte. Most likely not a Transport Stream.");
                }
            }
        } else {
            this.bytesSinceLastSync = 0;
        }
        return endOfPacket;
    }

    private boolean shouldConsumePacketPayload(int packetPid) {
        if (this.mode != 2 && !this.tracksEnded) {
            if (this.trackPids.get(packetPid, false)) {
                return false;
            }
        }
        return true;
    }

    private void resetPayloadReaders() {
        this.trackIds.clear();
        this.tsPayloadReaders.clear();
        SparseArray<TsPayloadReader> initialPayloadReaders = this.payloadReaderFactory.createInitialPayloadReaders();
        int initialPayloadReadersSize = initialPayloadReaders.size();
        for (int i = 0; i < initialPayloadReadersSize; i++) {
            this.tsPayloadReaders.put(initialPayloadReaders.keyAt(i), initialPayloadReaders.valueAt(i));
        }
        this.tsPayloadReaders.put(0, new SectionReader(new PatReader()));
        this.id3Reader = null;
    }
}
