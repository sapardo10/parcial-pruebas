package com.google.android.exoplayer2.extractor.mp3;

import android.support.annotation.Nullable;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.extractor.GaplessInfoHolder;
import com.google.android.exoplayer2.extractor.Id3Peeker;
import com.google.android.exoplayer2.extractor.MpegAudioHeader;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.Metadata.Entry;
import com.google.android.exoplayer2.metadata.id3.Id3Decoder.FramePredicate;
import com.google.android.exoplayer2.metadata.id3.MlltFrame;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.io.EOFException;
import java.io.IOException;

public final class Mp3Extractor implements Extractor {
    public static final ExtractorsFactory FACTORY = -$$Lambda$Mp3Extractor$6eyGfoogMVGFHZKg1gVp93FAKZA.INSTANCE;
    public static final int FLAG_DISABLE_ID3_METADATA = 2;
    public static final int FLAG_ENABLE_CONSTANT_BITRATE_SEEKING = 1;
    private static final int MAX_SNIFF_BYTES = 16384;
    private static final int MAX_SYNC_BYTES = 131072;
    private static final int MPEG_AUDIO_HEADER_MASK = -128000;
    private static final FramePredicate REQUIRED_ID3_FRAME_PREDICATE = -$$Lambda$Mp3Extractor$bb754AZIAMUosKBF4SefP1vYq88.INSTANCE;
    private static final int SCRATCH_LENGTH = 10;
    private static final int SEEK_HEADER_INFO = Util.getIntegerCodeForString("Info");
    private static final int SEEK_HEADER_UNSET = 0;
    private static final int SEEK_HEADER_VBRI = Util.getIntegerCodeForString("VBRI");
    private static final int SEEK_HEADER_XING = Util.getIntegerCodeForString("Xing");
    private long basisTimeUs;
    private ExtractorOutput extractorOutput;
    private final int flags;
    private final long forcedFirstSampleTimestampUs;
    private final GaplessInfoHolder gaplessInfoHolder;
    private final Id3Peeker id3Peeker;
    private Metadata metadata;
    private int sampleBytesRemaining;
    private long samplesRead;
    private final ParsableByteArray scratch;
    private Seeker seeker;
    private final MpegAudioHeader synchronizedHeader;
    private int synchronizedHeaderData;
    private TrackOutput trackOutput;

    interface Seeker extends SeekMap {
        long getDataEndPosition();

        long getTimeUs(long j);
    }

    static /* synthetic */ boolean lambda$static$1(int majorVersion, int id0, int id1, int id2, int id3) {
        if (id0 == 67 && id1 == 79 && id2 == 77) {
            if (!(id3 == 77 || majorVersion == 2)) {
            }
            return true;
        }
        if (id0 == 77 && id1 == 76 && id2 == 76) {
            if (id3 != 84) {
                if (majorVersion == 2) {
                }
            }
            return true;
        }
        return false;
    }

    public Mp3Extractor() {
        this(0);
    }

    public Mp3Extractor(int flags) {
        this(flags, C0555C.TIME_UNSET);
    }

    public Mp3Extractor(int flags, long forcedFirstSampleTimestampUs) {
        this.flags = flags;
        this.forcedFirstSampleTimestampUs = forcedFirstSampleTimestampUs;
        this.scratch = new ParsableByteArray(10);
        this.synchronizedHeader = new MpegAudioHeader();
        this.gaplessInfoHolder = new GaplessInfoHolder();
        this.basisTimeUs = C0555C.TIME_UNSET;
        this.id3Peeker = new Id3Peeker();
    }

    public boolean sniff(ExtractorInput input) throws IOException, InterruptedException {
        return synchronize(input, true);
    }

    public void init(ExtractorOutput output) {
        this.extractorOutput = output;
        this.trackOutput = this.extractorOutput.track(0, 1);
        this.extractorOutput.endTracks();
    }

    public void seek(long position, long timeUs) {
        this.synchronizedHeaderData = 0;
        this.basisTimeUs = C0555C.TIME_UNSET;
        this.samplesRead = 0;
        this.sampleBytesRemaining = 0;
    }

    public void release() {
    }

    public int read(ExtractorInput input, PositionHolder seekPosition) throws IOException, InterruptedException {
        if (this.synchronizedHeaderData == 0) {
            try {
                synchronize(input, false);
            } catch (EOFException e) {
                return -1;
            }
        }
        ExtractorInput extractorInput = input;
        if (r1.seeker == null) {
            Seeker seekFrameSeeker = maybeReadSeekFrame(input);
            Seeker metadataSeeker = maybeHandleSeekMetadata(r1.metadata, input.getPosition());
            if (metadataSeeker != null) {
                r1.seeker = metadataSeeker;
            } else if (seekFrameSeeker != null) {
                r1.seeker = seekFrameSeeker;
            }
            Seeker seeker = r1.seeker;
            if (seeker != null) {
                if (seeker.isSeekable() || (r1.flags & 1) == 0) {
                    r1.extractorOutput.seekMap(r1.seeker);
                    r1.trackOutput.format(Format.createAudioSampleFormat(null, r1.synchronizedHeader.mimeType, null, -1, 4096, r1.synchronizedHeader.channels, r1.synchronizedHeader.sampleRate, -1, r1.gaplessInfoHolder.encoderDelay, r1.gaplessInfoHolder.encoderPadding, null, null, 0, null, (r1.flags & 2) == 0 ? null : r1.metadata));
                }
            }
            r1.seeker = getConstantBitrateSeeker(input);
            r1.extractorOutput.seekMap(r1.seeker);
            if ((r1.flags & 2) == 0) {
            }
            r1.trackOutput.format(Format.createAudioSampleFormat(null, r1.synchronizedHeader.mimeType, null, -1, 4096, r1.synchronizedHeader.channels, r1.synchronizedHeader.sampleRate, -1, r1.gaplessInfoHolder.encoderDelay, r1.gaplessInfoHolder.encoderPadding, null, null, 0, null, (r1.flags & 2) == 0 ? null : r1.metadata));
        }
        return readSample(input);
    }

    private int readSample(ExtractorInput extractorInput) throws IOException, InterruptedException {
        int sampleHeaderData;
        if (this.sampleBytesRemaining == 0) {
            extractorInput.resetPeekPosition();
            if (peekEndOfStreamOrHeader(extractorInput)) {
                return -1;
            }
            this.scratch.setPosition(0);
            sampleHeaderData = this.scratch.readInt();
            if (headersMatch(sampleHeaderData, (long) this.synchronizedHeaderData)) {
                if (MpegAudioHeader.getFrameSize(sampleHeaderData) != -1) {
                    MpegAudioHeader.populateHeader(sampleHeaderData, this.synchronizedHeader);
                    if (this.basisTimeUs == C0555C.TIME_UNSET) {
                        this.basisTimeUs = this.seeker.getTimeUs(extractorInput.getPosition());
                        if (this.forcedFirstSampleTimestampUs != C0555C.TIME_UNSET) {
                            this.basisTimeUs += this.forcedFirstSampleTimestampUs - this.seeker.getTimeUs(0);
                        }
                    }
                    this.sampleBytesRemaining = this.synchronizedHeader.frameSize;
                }
            }
            extractorInput.skipFully(1);
            this.synchronizedHeaderData = 0;
            return 0;
        }
        sampleHeaderData = this.trackOutput.sampleData(extractorInput, this.sampleBytesRemaining, true);
        if (sampleHeaderData == -1) {
            return -1;
        }
        this.sampleBytesRemaining -= sampleHeaderData;
        if (this.sampleBytesRemaining > 0) {
            return 0;
        }
        this.trackOutput.sampleMetadata(this.basisTimeUs + ((this.samplesRead * 1000000) / ((long) this.synchronizedHeader.sampleRate)), 1, this.synchronizedHeader.frameSize, 0, null);
        this.samplesRead += (long) this.synchronizedHeader.samplesPerFrame;
        this.sampleBytesRemaining = 0;
        return 0;
    }

    private boolean synchronize(ExtractorInput input, boolean sniffing) throws IOException, InterruptedException {
        int validFrameCount = 0;
        int candidateSynchronizedHeaderData = 0;
        int peekedId3Bytes = 0;
        int searchedBytes = 0;
        int searchLimitBytes = sniffing ? 16384 : 131072;
        input.resetPeekPosition();
        if (input.getPosition() == 0) {
            this.metadata = this.id3Peeker.peekId3Data(input, (this.flags & 2) == 0 ? null : REQUIRED_ID3_FRAME_PREDICATE);
            Metadata metadata = this.metadata;
            if (metadata != null) {
                this.gaplessInfoHolder.setFromMetadata(metadata);
            }
            peekedId3Bytes = (int) input.getPeekPosition();
            if (!sniffing) {
                input.skipFully(peekedId3Bytes);
            }
        }
        while (!peekEndOfStreamOrHeader(input)) {
            int searchedBytes2;
            this.scratch.setPosition(0);
            int headerData = this.scratch.readInt();
            if (candidateSynchronizedHeaderData != 0) {
                if (!headersMatch(headerData, (long) candidateSynchronizedHeaderData)) {
                    searchedBytes2 = searchedBytes + 1;
                    if (searchedBytes == searchLimitBytes) {
                        validFrameCount = 0;
                        candidateSynchronizedHeaderData = 0;
                        if (sniffing) {
                            input.skipFully(1);
                        } else {
                            input.resetPeekPosition();
                            input.advancePeekPosition(peekedId3Bytes + searchedBytes2);
                        }
                        searchedBytes = searchedBytes2;
                    } else if (sniffing) {
                        return false;
                    } else {
                        throw new ParserException("Searched too many bytes.");
                    }
                }
            }
            searchedBytes2 = MpegAudioHeader.getFrameSize(headerData);
            int frameSize = searchedBytes2;
            if (searchedBytes2 != -1) {
                validFrameCount++;
                if (validFrameCount == 1) {
                    MpegAudioHeader.populateHeader(headerData, this.synchronizedHeader);
                    candidateSynchronizedHeaderData = headerData;
                } else if (validFrameCount == 4) {
                    break;
                }
                input.advancePeekPosition(frameSize - 4);
            }
            searchedBytes2 = searchedBytes + 1;
            if (searchedBytes == searchLimitBytes) {
                validFrameCount = 0;
                candidateSynchronizedHeaderData = 0;
                if (sniffing) {
                    input.skipFully(1);
                } else {
                    input.resetPeekPosition();
                    input.advancePeekPosition(peekedId3Bytes + searchedBytes2);
                }
                searchedBytes = searchedBytes2;
            } else if (sniffing) {
                return false;
            } else {
                throw new ParserException("Searched too many bytes.");
            }
        }
        if (validFrameCount > 0) {
            if (sniffing) {
                input.skipFully(peekedId3Bytes + searchedBytes);
            } else {
                input.resetPeekPosition();
            }
            this.synchronizedHeaderData = candidateSynchronizedHeaderData;
            return true;
        }
        throw new EOFException();
    }

    private boolean peekEndOfStreamOrHeader(ExtractorInput extractorInput) throws IOException, InterruptedException {
        if (this.seeker != null) {
            if (extractorInput.getPeekPosition() == this.seeker.getDataEndPosition()) {
                return true;
            }
        }
        if (extractorInput.peekFully(this.scratch.data, 0, 4, true)) {
            return false;
        }
        return true;
    }

    private Seeker maybeReadSeekFrame(ExtractorInput input) throws IOException, InterruptedException {
        int xingBase;
        int seekHeader;
        Seeker seeker;
        ParsableByteArray frame = new ParsableByteArray(this.synchronizedHeader.frameSize);
        input.peekFully(frame.data, 0, this.synchronizedHeader.frameSize);
        int i = 21;
        if ((this.synchronizedHeader.version & 1) != 0) {
            if (this.synchronizedHeader.channels != 1) {
                i = 36;
                xingBase = i;
                seekHeader = getSeekFrameHeader(frame, xingBase);
                if (seekHeader != SEEK_HEADER_XING) {
                    if (seekHeader == SEEK_HEADER_INFO) {
                        if (seekHeader == SEEK_HEADER_VBRI) {
                            seeker = VbriSeeker.create(input.getLength(), input.getPosition(), this.synchronizedHeader, frame);
                            input.skipFully(this.synchronizedHeader.frameSize);
                        } else {
                            seeker = null;
                            input.resetPeekPosition();
                        }
                        return seeker;
                    }
                }
                seeker = XingSeeker.create(input.getLength(), input.getPosition(), this.synchronizedHeader, frame);
                if (seeker == null && !this.gaplessInfoHolder.hasGaplessInfo()) {
                    input.resetPeekPosition();
                    input.advancePeekPosition(xingBase + 141);
                    input.peekFully(this.scratch.data, 0, 3);
                    this.scratch.setPosition(0);
                    this.gaplessInfoHolder.setFromXingHeaderValue(this.scratch.readUnsignedInt24());
                }
                input.skipFully(this.synchronizedHeader.frameSize);
                if (seeker == null && !seeker.isSeekable() && seekHeader == SEEK_HEADER_INFO) {
                    return getConstantBitrateSeeker(input);
                }
                return seeker;
            }
        } else if (this.synchronizedHeader.channels == 1) {
            i = 13;
            xingBase = i;
            seekHeader = getSeekFrameHeader(frame, xingBase);
            if (seekHeader != SEEK_HEADER_XING) {
                if (seekHeader == SEEK_HEADER_INFO) {
                    if (seekHeader == SEEK_HEADER_VBRI) {
                        seeker = null;
                        input.resetPeekPosition();
                    } else {
                        seeker = VbriSeeker.create(input.getLength(), input.getPosition(), this.synchronizedHeader, frame);
                        input.skipFully(this.synchronizedHeader.frameSize);
                    }
                    return seeker;
                }
            }
            seeker = XingSeeker.create(input.getLength(), input.getPosition(), this.synchronizedHeader, frame);
            if (seeker == null) {
            }
            input.skipFully(this.synchronizedHeader.frameSize);
            if (seeker == null) {
            }
            return seeker;
        }
        xingBase = i;
        seekHeader = getSeekFrameHeader(frame, xingBase);
        if (seekHeader != SEEK_HEADER_XING) {
            if (seekHeader == SEEK_HEADER_INFO) {
                if (seekHeader == SEEK_HEADER_VBRI) {
                    seeker = VbriSeeker.create(input.getLength(), input.getPosition(), this.synchronizedHeader, frame);
                    input.skipFully(this.synchronizedHeader.frameSize);
                } else {
                    seeker = null;
                    input.resetPeekPosition();
                }
                return seeker;
            }
        }
        seeker = XingSeeker.create(input.getLength(), input.getPosition(), this.synchronizedHeader, frame);
        if (seeker == null) {
        }
        input.skipFully(this.synchronizedHeader.frameSize);
        if (seeker == null) {
        }
        return seeker;
    }

    private Seeker getConstantBitrateSeeker(ExtractorInput input) throws IOException, InterruptedException {
        input.peekFully(this.scratch.data, 0, 4);
        this.scratch.setPosition(0);
        MpegAudioHeader.populateHeader(this.scratch.readInt(), this.synchronizedHeader);
        return new ConstantBitrateSeeker(input.getLength(), input.getPosition(), this.synchronizedHeader);
    }

    private static boolean headersMatch(int headerA, long headerB) {
        return ((long) (MPEG_AUDIO_HEADER_MASK & headerA)) == (-128000 & headerB);
    }

    private static int getSeekFrameHeader(ParsableByteArray frame, int xingBase) {
        int headerData;
        if (frame.limit() >= xingBase + 4) {
            frame.setPosition(xingBase);
            headerData = frame.readInt();
            if (headerData != SEEK_HEADER_XING) {
                if (headerData == SEEK_HEADER_INFO) {
                }
            }
            return headerData;
        }
        if (frame.limit() >= 40) {
            frame.setPosition(36);
            headerData = frame.readInt();
            int i = SEEK_HEADER_VBRI;
            if (headerData == i) {
                return i;
            }
        }
        return 0;
    }

    @Nullable
    private static MlltSeeker maybeHandleSeekMetadata(Metadata metadata, long firstFramePosition) {
        if (metadata != null) {
            int length = metadata.length();
            for (int i = 0; i < length; i++) {
                Entry entry = metadata.get(i);
                if (entry instanceof MlltFrame) {
                    return MlltSeeker.create(firstFramePosition, (MlltFrame) entry);
                }
            }
        }
        return null;
    }
}
