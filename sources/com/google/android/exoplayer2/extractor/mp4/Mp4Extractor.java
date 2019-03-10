package com.google.android.exoplayer2.extractor.mp4;

import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.extractor.GaplessInfoHolder;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.SeekMap.SeekPoints;
import com.google.android.exoplayer2.extractor.SeekPoint;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.NalUnitUtil;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public final class Mp4Extractor implements Extractor, SeekMap {
    private static final int BRAND_QUICKTIME = Util.getIntegerCodeForString("qt  ");
    public static final ExtractorsFactory FACTORY = -$$Lambda$Mp4Extractor$quy71uYOGsneho91FZy1d2UGE1Q.INSTANCE;
    public static final int FLAG_WORKAROUND_IGNORE_EDIT_LISTS = 1;
    private static final long MAXIMUM_READ_AHEAD_BYTES_STREAM = 10485760;
    private static final long RELOAD_MINIMUM_SEEK_DISTANCE = 262144;
    private static final int STATE_READING_ATOM_HEADER = 0;
    private static final int STATE_READING_ATOM_PAYLOAD = 1;
    private static final int STATE_READING_SAMPLE = 2;
    private long[][] accumulatedSampleSizes;
    private ParsableByteArray atomData;
    private final ParsableByteArray atomHeader;
    private int atomHeaderBytesRead;
    private long atomSize;
    private int atomType;
    private final ArrayDeque<ContainerAtom> containerAtoms;
    private long durationUs;
    private ExtractorOutput extractorOutput;
    private int firstVideoTrackIndex;
    private final int flags;
    private boolean isQuickTime;
    private final ParsableByteArray nalLength;
    private final ParsableByteArray nalStartCode;
    private int parserState;
    private int sampleBytesWritten;
    private int sampleCurrentNalBytesRemaining;
    private int sampleTrackIndex;
    private Mp4Track[] tracks;

    private static final class Mp4Track {
        public int sampleIndex;
        public final TrackSampleTable sampleTable;
        public final Track track;
        public final TrackOutput trackOutput;

        public Mp4Track(Track track, TrackSampleTable sampleTable, TrackOutput trackOutput) {
            this.track = track;
            this.sampleTable = sampleTable;
            this.trackOutput = trackOutput;
        }
    }

    public Mp4Extractor() {
        this(0);
    }

    public Mp4Extractor(int flags) {
        this.flags = flags;
        this.atomHeader = new ParsableByteArray(16);
        this.containerAtoms = new ArrayDeque();
        this.nalStartCode = new ParsableByteArray(NalUnitUtil.NAL_START_CODE);
        this.nalLength = new ParsableByteArray(4);
        this.sampleTrackIndex = -1;
    }

    public boolean sniff(ExtractorInput input) throws IOException, InterruptedException {
        return Sniffer.sniffUnfragmented(input);
    }

    public void init(ExtractorOutput output) {
        this.extractorOutput = output;
    }

    public void seek(long position, long timeUs) {
        this.containerAtoms.clear();
        this.atomHeaderBytesRead = 0;
        this.sampleTrackIndex = -1;
        this.sampleBytesWritten = 0;
        this.sampleCurrentNalBytesRemaining = 0;
        if (position == 0) {
            enterReadingAtomHeaderState();
        } else if (this.tracks != null) {
            updateSampleIndices(timeUs);
        }
    }

    public void release() {
    }

    public int read(ExtractorInput input, PositionHolder seekPosition) throws IOException, InterruptedException {
        while (true) {
            switch (this.parserState) {
                case 0:
                    if (readAtomHeader(input)) {
                        break;
                    }
                    return -1;
                case 1:
                    if (!readAtomPayload(input, seekPosition)) {
                        break;
                    }
                    return 1;
                case 2:
                    return readSample(input, seekPosition);
                default:
                    throw new IllegalStateException();
            }
        }
    }

    public boolean isSeekable() {
        return true;
    }

    public long getDurationUs() {
        return this.durationUs;
    }

    public SeekPoints getSeekPoints(long timeUs) {
        long j = timeUs;
        Mp4Track[] mp4TrackArr = this.tracks;
        if (mp4TrackArr.length == 0) {
            return new SeekPoints(SeekPoint.START);
        }
        long firstTimeUs;
        long firstOffset;
        long secondTimeUs = C0555C.TIME_UNSET;
        long secondOffset = -1;
        int i = r0.firstVideoTrackIndex;
        if (i != -1) {
            TrackSampleTable sampleTable = mp4TrackArr[i].sampleTable;
            i = getSynchronizationSampleIndex(sampleTable, j);
            if (i == -1) {
                return new SeekPoints(SeekPoint.START);
            }
            long sampleTimeUs = sampleTable.timestampsUs[i];
            firstTimeUs = sampleTimeUs;
            firstOffset = sampleTable.offsets[i];
            if (sampleTimeUs < j && i < sampleTable.sampleCount - 1) {
                int secondSampleIndex = sampleTable.getIndexOfLaterOrEqualSynchronizationSample(j);
                if (secondSampleIndex != -1 && secondSampleIndex != i) {
                    secondTimeUs = sampleTable.timestampsUs[secondSampleIndex];
                    secondOffset = sampleTable.offsets[secondSampleIndex];
                }
            }
        } else {
            firstTimeUs = timeUs;
            firstOffset = Long.MAX_VALUE;
        }
        int i2 = 0;
        long secondOffset2 = secondOffset;
        secondOffset = firstOffset;
        while (true) {
            Mp4Track[] mp4TrackArr2 = r0.tracks;
            if (i2 >= mp4TrackArr2.length) {
                break;
            }
            if (i2 != r0.firstVideoTrackIndex) {
                TrackSampleTable sampleTable2 = mp4TrackArr2[i2].sampleTable;
                secondOffset = maybeAdjustSeekOffset(sampleTable2, firstTimeUs, secondOffset);
                if (secondTimeUs != C0555C.TIME_UNSET) {
                    secondOffset2 = maybeAdjustSeekOffset(sampleTable2, secondTimeUs, secondOffset2);
                }
            }
            i2++;
        }
        SeekPoint firstSeekPoint = new SeekPoint(firstTimeUs, secondOffset);
        if (secondTimeUs == C0555C.TIME_UNSET) {
            return new SeekPoints(firstSeekPoint);
        }
        return new SeekPoints(firstSeekPoint, new SeekPoint(secondTimeUs, secondOffset2));
    }

    private void enterReadingAtomHeaderState() {
        this.parserState = 0;
        this.atomHeaderBytesRead = 0;
    }

    private boolean readAtomHeader(ExtractorInput input) throws IOException, InterruptedException {
        long endPosition;
        if (this.atomHeaderBytesRead == 0) {
            if (!input.readFully(this.atomHeader.data, 0, 8, true)) {
                return false;
            }
            this.atomHeaderBytesRead = 8;
            this.atomHeader.setPosition(0);
            this.atomSize = this.atomHeader.readUnsignedInt();
            this.atomType = this.atomHeader.readInt();
        }
        long j = this.atomSize;
        if (j == 1) {
            input.readFully(this.atomHeader.data, 8, 8);
            this.atomHeaderBytesRead += 8;
            this.atomSize = this.atomHeader.readUnsignedLongToLong();
        } else if (j == 0) {
            j = input.getLength();
            if (j == -1 && !this.containerAtoms.isEmpty()) {
                j = ((ContainerAtom) this.containerAtoms.peek()).endPosition;
            }
            if (j != -1) {
                this.atomSize = (j - input.getPosition()) + ((long) this.atomHeaderBytesRead);
            }
            if (this.atomSize < ((long) this.atomHeaderBytesRead)) {
                if (shouldParseContainerAtom(this.atomType)) {
                    endPosition = (input.getPosition() + this.atomSize) - ((long) this.atomHeaderBytesRead);
                    this.containerAtoms.push(new ContainerAtom(this.atomType, endPosition));
                    if (this.atomSize != ((long) this.atomHeaderBytesRead)) {
                        processAtomEnded(endPosition);
                    } else {
                        enterReadingAtomHeaderState();
                    }
                } else if (shouldParseLeafAtom(this.atomType)) {
                    this.atomData = null;
                    this.parserState = 1;
                } else {
                    Assertions.checkState(this.atomHeaderBytesRead != 8);
                    Assertions.checkState(this.atomSize > 2147483647L);
                    this.atomData = new ParsableByteArray((int) this.atomSize);
                    System.arraycopy(this.atomHeader.data, 0, this.atomData.data, 0, 8);
                    this.parserState = 1;
                }
                return true;
            }
            throw new ParserException("Atom size less than header length (unsupported).");
        }
        if (this.atomSize < ((long) this.atomHeaderBytesRead)) {
            throw new ParserException("Atom size less than header length (unsupported).");
        }
        if (shouldParseContainerAtom(this.atomType)) {
            endPosition = (input.getPosition() + this.atomSize) - ((long) this.atomHeaderBytesRead);
            this.containerAtoms.push(new ContainerAtom(this.atomType, endPosition));
            if (this.atomSize != ((long) this.atomHeaderBytesRead)) {
                enterReadingAtomHeaderState();
            } else {
                processAtomEnded(endPosition);
            }
        } else if (shouldParseLeafAtom(this.atomType)) {
            this.atomData = null;
            this.parserState = 1;
        } else {
            if (this.atomHeaderBytesRead != 8) {
            }
            Assertions.checkState(this.atomHeaderBytesRead != 8);
            if (this.atomSize > 2147483647L) {
            }
            Assertions.checkState(this.atomSize > 2147483647L);
            this.atomData = new ParsableByteArray((int) this.atomSize);
            System.arraycopy(this.atomHeader.data, 0, this.atomData.data, 0, 8);
            this.parserState = 1;
        }
        return true;
    }

    private boolean readAtomPayload(ExtractorInput input, PositionHolder positionHolder) throws IOException, InterruptedException {
        long atomPayloadSize = this.atomSize - ((long) this.atomHeaderBytesRead);
        long atomEndPosition = input.getPosition() + atomPayloadSize;
        boolean seekRequired = false;
        ParsableByteArray parsableByteArray = this.atomData;
        if (parsableByteArray != null) {
            input.readFully(parsableByteArray.data, this.atomHeaderBytesRead, (int) atomPayloadSize);
            if (this.atomType == Atom.TYPE_ftyp) {
                this.isQuickTime = processFtypAtom(this.atomData);
            } else if (!this.containerAtoms.isEmpty()) {
                ((ContainerAtom) this.containerAtoms.peek()).add(new LeafAtom(this.atomType, this.atomData));
            }
        } else if (atomPayloadSize < 262144) {
            input.skipFully((int) atomPayloadSize);
        } else {
            positionHolder.position = input.getPosition() + atomPayloadSize;
            seekRequired = true;
        }
        processAtomEnded(atomEndPosition);
        return seekRequired && this.parserState != 2;
    }

    private void processAtomEnded(long atomEndPosition) throws ParserException {
        while (!this.containerAtoms.isEmpty() && ((ContainerAtom) this.containerAtoms.peek()).endPosition == atomEndPosition) {
            ContainerAtom containerAtom = (ContainerAtom) this.containerAtoms.pop();
            if (containerAtom.type == Atom.TYPE_moov) {
                processMoovAtom(containerAtom);
                this.containerAtoms.clear();
                this.parserState = 2;
            } else if (!this.containerAtoms.isEmpty()) {
                ((ContainerAtom) this.containerAtoms.peek()).add(containerAtom);
            }
        }
        if (this.parserState != 2) {
            enterReadingAtomHeaderState();
        }
    }

    private void processMoovAtom(ContainerAtom moov) throws ParserException {
        boolean ignoreEditLists;
        Mp4Extractor mp4Extractor = this;
        ContainerAtom containerAtom = moov;
        int firstVideoTrackIndex = -1;
        long durationUs = C0555C.TIME_UNSET;
        List<Mp4Track> tracks = new ArrayList();
        Metadata metadata = null;
        GaplessInfoHolder gaplessInfoHolder = new GaplessInfoHolder();
        LeafAtom udta = containerAtom.getLeafAtomOfType(Atom.TYPE_udta);
        if (udta != null) {
            metadata = AtomParsers.parseUdta(udta, mp4Extractor.isQuickTime);
            if (metadata != null) {
                gaplessInfoHolder.setFromMetadata(metadata);
            }
        }
        boolean ignoreEditLists2 = (mp4Extractor.flags & 1) != 0;
        ArrayList<TrackSampleTable> trackSampleTables = getTrackSampleTables(containerAtom, gaplessInfoHolder, ignoreEditLists2);
        int trackCount = trackSampleTables.size();
        int i = 0;
        while (i < trackCount) {
            TrackSampleTable trackSampleTable = (TrackSampleTable) trackSampleTables.get(i);
            Track track = trackSampleTable.track;
            LeafAtom udta2 = udta;
            Mp4Track mp4Track = new Mp4Track(track, trackSampleTable, mp4Extractor.extractorOutput.track(i, track.type));
            udta = trackSampleTable.maximumSize + 30;
            Format format = track.format.copyWithMaxInputSize(udta);
            int maxInputSize = udta;
            ignoreEditLists = ignoreEditLists2;
            if (track.type == true) {
                if (gaplessInfoHolder.hasGaplessInfo() != null) {
                    format = format.copyWithGaplessInfo(gaplessInfoHolder.encoderDelay, gaplessInfoHolder.encoderPadding);
                }
                if (metadata != null) {
                    format = format.copyWithMetadata(metadata);
                }
            }
            mp4Track.trackOutput.format(format);
            durationUs = Math.max(durationUs, track.durationUs != C0555C.TIME_UNSET ? track.durationUs : trackSampleTable.durationUs);
            if (track.type == 2 && firstVideoTrackIndex == -1) {
                firstVideoTrackIndex = tracks.size();
            }
            tracks.add(mp4Track);
            i++;
            udta = udta2;
            ignoreEditLists2 = ignoreEditLists;
            containerAtom = moov;
        }
        ignoreEditLists = ignoreEditLists2;
        mp4Extractor.firstVideoTrackIndex = firstVideoTrackIndex;
        mp4Extractor.durationUs = durationUs;
        mp4Extractor.tracks = (Mp4Track[]) tracks.toArray(new Mp4Track[tracks.size()]);
        mp4Extractor.accumulatedSampleSizes = calculateAccumulatedSampleSizes(mp4Extractor.tracks);
        mp4Extractor.extractorOutput.endTracks();
        mp4Extractor.extractorOutput.seekMap(mp4Extractor);
    }

    private ArrayList<TrackSampleTable> getTrackSampleTables(ContainerAtom moov, GaplessInfoHolder gaplessInfoHolder, boolean ignoreEditLists) throws ParserException {
        ArrayList<TrackSampleTable> trackSampleTables = new ArrayList();
        for (int i = 0; i < moov.containerChildren.size(); i++) {
            ContainerAtom atom = (ContainerAtom) moov.containerChildren.get(i);
            if (atom.type == Atom.TYPE_trak) {
                Track track = AtomParsers.parseTrak(atom, moov.getLeafAtomOfType(Atom.TYPE_mvhd), C0555C.TIME_UNSET, null, ignoreEditLists, this.isQuickTime);
                if (track != null) {
                    TrackSampleTable trackSampleTable = AtomParsers.parseStbl(track, atom.getContainerAtomOfType(Atom.TYPE_mdia).getContainerAtomOfType(Atom.TYPE_minf).getContainerAtomOfType(Atom.TYPE_stbl), gaplessInfoHolder);
                    if (trackSampleTable.sampleCount != 0) {
                        trackSampleTables.add(trackSampleTable);
                    }
                }
            }
        }
        return trackSampleTables;
    }

    private int readSample(ExtractorInput input, PositionHolder positionHolder) throws IOException, InterruptedException {
        long position;
        ExtractorInput extractorInput = input;
        long inputPosition = input.getPosition();
        if (this.sampleTrackIndex == -1) {
            r0.sampleTrackIndex = getTrackIndexOfNextReadSample(inputPosition);
            if (r0.sampleTrackIndex == -1) {
                return -1;
            }
        }
        Mp4Track track = r0.tracks[r0.sampleTrackIndex];
        TrackOutput trackOutput = track.trackOutput;
        int sampleIndex = track.sampleIndex;
        long position2 = track.sampleTable.offsets[sampleIndex];
        int sampleSize = track.sampleTable.sizes[sampleIndex];
        long skipAmount = (position2 - inputPosition) + ((long) r0.sampleBytesWritten);
        if (skipAmount < 0) {
            position = position2;
        } else if (skipAmount >= 262144) {
            r18 = inputPosition;
            position = position2;
        } else {
            long skipAmount2;
            int i;
            if (track.track.sampleTransformation == 1) {
                sampleSize -= 8;
                skipAmount2 = skipAmount + 8;
            } else {
                skipAmount2 = skipAmount;
            }
            extractorInput.skipFully((int) skipAmount2);
            if (track.track.nalUnitLengthFieldLength != 0) {
                byte[] nalLengthData = r0.nalLength.data;
                nalLengthData[0] = (byte) 0;
                nalLengthData[1] = (byte) 0;
                nalLengthData[2] = (byte) 0;
                int nalUnitLengthFieldLength = track.track.nalUnitLengthFieldLength;
                int nalUnitLengthFieldLengthDiff = 4 - track.track.nalUnitLengthFieldLength;
                while (r0.sampleBytesWritten < sampleSize) {
                    int writtenBytes = r0.sampleCurrentNalBytesRemaining;
                    if (writtenBytes == 0) {
                        extractorInput.readFully(r0.nalLength.data, nalUnitLengthFieldLengthDiff, nalUnitLengthFieldLength);
                        r18 = inputPosition;
                        r0.nalLength.setPosition(0);
                        r0.sampleCurrentNalBytesRemaining = r0.nalLength.readUnsignedIntToInt();
                        r0.nalStartCode.setPosition(0);
                        trackOutput.sampleData(r0.nalStartCode, 4);
                        r0.sampleBytesWritten += 4;
                        sampleSize += nalUnitLengthFieldLengthDiff;
                        inputPosition = r18;
                    } else {
                        r18 = inputPosition;
                        writtenBytes = trackOutput.sampleData(extractorInput, writtenBytes, 0);
                        r0.sampleBytesWritten += writtenBytes;
                        r0.sampleCurrentNalBytesRemaining -= writtenBytes;
                        inputPosition = r18;
                    }
                }
                inputPosition = sampleSize;
                i = 0;
            } else {
                while (true) {
                    inputPosition = r0.sampleBytesWritten;
                    if (inputPosition >= sampleSize) {
                        break;
                    }
                    inputPosition = trackOutput.sampleData(extractorInput, sampleSize - inputPosition, false);
                    r0.sampleBytesWritten += inputPosition;
                    r0.sampleCurrentNalBytesRemaining -= inputPosition;
                }
                i = 0;
                inputPosition = sampleSize;
            }
            trackOutput.sampleMetadata(track.sampleTable.timestampsUs[sampleIndex], track.sampleTable.flags[sampleIndex], inputPosition, 0, null);
            track.sampleIndex++;
            r0.sampleTrackIndex = -1;
            r0.sampleBytesWritten = i;
            r0.sampleCurrentNalBytesRemaining = i;
            return i;
        }
        positionHolder.position = position;
        return 1;
    }

    private int getTrackIndexOfNextReadSample(long inputPosition) {
        Mp4Extractor mp4Extractor = this;
        long preferredSkipAmount = Long.MAX_VALUE;
        boolean preferredRequiresReload = true;
        int preferredTrackIndex = -1;
        long preferredAccumulatedBytes = Long.MAX_VALUE;
        long minAccumulatedBytes = Long.MAX_VALUE;
        boolean minAccumulatedBytesRequiresReload = true;
        int minAccumulatedBytesTrackIndex = -1;
        int trackIndex = 0;
        while (true) {
            Mp4Track track = mp4Extractor.tracks;
            if (trackIndex >= track.length) {
                break;
            }
            track = track[trackIndex];
            int sampleIndex = track.sampleIndex;
            if (sampleIndex != track.sampleTable.sampleCount) {
                boolean requiresReload;
                long sampleOffset = track.sampleTable.offsets[sampleIndex];
                long sampleAccumulatedBytes = mp4Extractor.accumulatedSampleSizes[trackIndex][sampleIndex];
                long skipAmount = sampleOffset - inputPosition;
                if (skipAmount >= 0) {
                    if (skipAmount < 262144) {
                        requiresReload = false;
                        if (!requiresReload) {
                            if (!preferredRequiresReload) {
                            }
                            preferredRequiresReload = requiresReload;
                            preferredSkipAmount = skipAmount;
                            preferredTrackIndex = trackIndex;
                            preferredAccumulatedBytes = sampleAccumulatedBytes;
                            if (sampleAccumulatedBytes < minAccumulatedBytes) {
                                minAccumulatedBytes = sampleAccumulatedBytes;
                                minAccumulatedBytesRequiresReload = requiresReload;
                                minAccumulatedBytesTrackIndex = trackIndex;
                            }
                        }
                        if (requiresReload != preferredRequiresReload && skipAmount < preferredSkipAmount) {
                            preferredRequiresReload = requiresReload;
                            preferredSkipAmount = skipAmount;
                            preferredTrackIndex = trackIndex;
                            preferredAccumulatedBytes = sampleAccumulatedBytes;
                            if (sampleAccumulatedBytes < minAccumulatedBytes) {
                                minAccumulatedBytes = sampleAccumulatedBytes;
                                minAccumulatedBytesRequiresReload = requiresReload;
                                minAccumulatedBytesTrackIndex = trackIndex;
                            }
                        } else if (sampleAccumulatedBytes < minAccumulatedBytes) {
                            minAccumulatedBytes = sampleAccumulatedBytes;
                            minAccumulatedBytesRequiresReload = requiresReload;
                            minAccumulatedBytesTrackIndex = trackIndex;
                        }
                    }
                }
                requiresReload = true;
                if (requiresReload) {
                    if (preferredRequiresReload) {
                    }
                    preferredRequiresReload = requiresReload;
                    preferredSkipAmount = skipAmount;
                    preferredTrackIndex = trackIndex;
                    preferredAccumulatedBytes = sampleAccumulatedBytes;
                    if (sampleAccumulatedBytes < minAccumulatedBytes) {
                        minAccumulatedBytes = sampleAccumulatedBytes;
                        minAccumulatedBytesRequiresReload = requiresReload;
                        minAccumulatedBytesTrackIndex = trackIndex;
                    }
                }
                if (requiresReload != preferredRequiresReload) {
                }
                if (sampleAccumulatedBytes < minAccumulatedBytes) {
                    minAccumulatedBytes = sampleAccumulatedBytes;
                    minAccumulatedBytesRequiresReload = requiresReload;
                    minAccumulatedBytesTrackIndex = trackIndex;
                }
            }
            trackIndex++;
        }
        if (minAccumulatedBytes != Long.MAX_VALUE && minAccumulatedBytesRequiresReload) {
            if (preferredAccumulatedBytes >= MAXIMUM_READ_AHEAD_BYTES_STREAM + minAccumulatedBytes) {
                return minAccumulatedBytesTrackIndex;
            }
        }
        return preferredTrackIndex;
    }

    private void updateSampleIndices(long timeUs) {
        for (Mp4Track track : this.tracks) {
            TrackSampleTable sampleTable = track.sampleTable;
            int sampleIndex = sampleTable.getIndexOfEarlierOrEqualSynchronizationSample(timeUs);
            if (sampleIndex == -1) {
                sampleIndex = sampleTable.getIndexOfLaterOrEqualSynchronizationSample(timeUs);
            }
            track.sampleIndex = sampleIndex;
        }
    }

    private static long[][] calculateAccumulatedSampleSizes(Mp4Track[] tracks) {
        long[][] accumulatedSampleSizes = new long[tracks.length][];
        int[] nextSampleIndex = new int[tracks.length];
        long[] nextSampleTimesUs = new long[tracks.length];
        boolean[] tracksFinished = new boolean[tracks.length];
        for (int i = 0; i < tracks.length; i++) {
            accumulatedSampleSizes[i] = new long[tracks[i].sampleTable.sampleCount];
            nextSampleTimesUs[i] = tracks[i].sampleTable.timestampsUs[0];
        }
        long accumulatedSampleSize = 0;
        int finishedTracks = 0;
        while (finishedTracks < tracks.length) {
            long minTimeUs = Long.MAX_VALUE;
            int minTimeTrackIndex = -1;
            int i2 = 0;
            while (i2 < tracks.length) {
                if (!tracksFinished[i2] && nextSampleTimesUs[i2] <= minTimeUs) {
                    minTimeTrackIndex = i2;
                    minTimeUs = nextSampleTimesUs[i2];
                }
                i2++;
            }
            i2 = nextSampleIndex[minTimeTrackIndex];
            accumulatedSampleSizes[minTimeTrackIndex][i2] = accumulatedSampleSize;
            accumulatedSampleSize += (long) tracks[minTimeTrackIndex].sampleTable.sizes[i2];
            i2++;
            nextSampleIndex[minTimeTrackIndex] = i2;
            if (i2 < accumulatedSampleSizes[minTimeTrackIndex].length) {
                nextSampleTimesUs[minTimeTrackIndex] = tracks[minTimeTrackIndex].sampleTable.timestampsUs[i2];
            } else {
                tracksFinished[minTimeTrackIndex] = true;
                finishedTracks++;
            }
        }
        return accumulatedSampleSizes;
    }

    private static long maybeAdjustSeekOffset(TrackSampleTable sampleTable, long seekTimeUs, long offset) {
        int sampleIndex = getSynchronizationSampleIndex(sampleTable, seekTimeUs);
        if (sampleIndex == -1) {
            return offset;
        }
        return Math.min(sampleTable.offsets[sampleIndex], offset);
    }

    private static int getSynchronizationSampleIndex(TrackSampleTable sampleTable, long timeUs) {
        int sampleIndex = sampleTable.getIndexOfEarlierOrEqualSynchronizationSample(timeUs);
        if (sampleIndex == -1) {
            return sampleTable.getIndexOfLaterOrEqualSynchronizationSample(timeUs);
        }
        return sampleIndex;
    }

    private static boolean processFtypAtom(ParsableByteArray atomData) {
        atomData.setPosition(8);
        if (atomData.readInt() == BRAND_QUICKTIME) {
            return true;
        }
        atomData.skipBytes(4);
        while (atomData.bytesLeft() > 0) {
            if (atomData.readInt() == BRAND_QUICKTIME) {
                return true;
            }
        }
        return false;
    }

    private static boolean shouldParseLeafAtom(int atom) {
        if (!(atom == Atom.TYPE_mdhd || atom == Atom.TYPE_mvhd || atom == Atom.TYPE_hdlr || atom == Atom.TYPE_stsd || atom == Atom.TYPE_stts || atom == Atom.TYPE_stss || atom == Atom.TYPE_ctts || atom == Atom.TYPE_elst || atom == Atom.TYPE_stsc || atom == Atom.TYPE_stsz || atom == Atom.TYPE_stz2 || atom == Atom.TYPE_stco || atom == Atom.TYPE_co64 || atom == Atom.TYPE_tkhd || atom == Atom.TYPE_ftyp)) {
            if (atom != Atom.TYPE_udta) {
                return false;
            }
        }
        return true;
    }

    private static boolean shouldParseContainerAtom(int atom) {
        if (!(atom == Atom.TYPE_moov || atom == Atom.TYPE_trak || atom == Atom.TYPE_mdia || atom == Atom.TYPE_minf || atom == Atom.TYPE_stbl)) {
            if (atom != Atom.TYPE_edts) {
                return false;
            }
        }
        return true;
    }
}
