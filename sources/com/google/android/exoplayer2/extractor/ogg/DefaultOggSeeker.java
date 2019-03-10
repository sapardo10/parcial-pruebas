package com.google.android.exoplayer2.extractor.ogg;

import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.SeekMap.SeekPoints;
import com.google.android.exoplayer2.extractor.SeekPoint;
import com.google.android.exoplayer2.util.Assertions;
import java.io.EOFException;
import java.io.IOException;

final class DefaultOggSeeker implements OggSeeker {
    private static final int DEFAULT_OFFSET = 30000;
    public static final int MATCH_BYTE_RANGE = 100000;
    public static final int MATCH_RANGE = 72000;
    private static final int STATE_IDLE = 3;
    private static final int STATE_READ_LAST_PAGE = 1;
    private static final int STATE_SEEK = 2;
    private static final int STATE_SEEK_TO_END = 0;
    private long end;
    private long endGranule;
    private final long endPosition;
    private final OggPageHeader pageHeader = new OggPageHeader();
    private long positionBeforeSeekToEnd;
    private long start;
    private long startGranule;
    private final long startPosition;
    private int state;
    private final StreamReader streamReader;
    private long targetGranule;
    private long totalGranules;

    private class OggSeekMap implements SeekMap {
        private OggSeekMap() {
        }

        public boolean isSeekable() {
            return true;
        }

        public SeekPoints getSeekPoints(long timeUs) {
            if (timeUs == 0) {
                return new SeekPoints(new SeekPoint(0, DefaultOggSeeker.this.startPosition));
            }
            long granule = DefaultOggSeeker.this.streamReader.convertTimeToGranule(timeUs);
            DefaultOggSeeker defaultOggSeeker = DefaultOggSeeker.this;
            return new SeekPoints(new SeekPoint(timeUs, defaultOggSeeker.getEstimatedPosition(defaultOggSeeker.startPosition, granule, 30000)));
        }

        public long getDurationUs() {
            return DefaultOggSeeker.this.streamReader.convertGranuleToTime(DefaultOggSeeker.this.totalGranules);
        }
    }

    public DefaultOggSeeker(long startPosition, long endPosition, StreamReader streamReader, long firstPayloadPageSize, long firstPayloadPageGranulePosition, boolean firstPayloadPageIsLastPage) {
        boolean z = startPosition >= 0 && endPosition > startPosition;
        Assertions.checkArgument(z);
        this.streamReader = streamReader;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        if (firstPayloadPageSize != endPosition - startPosition) {
            if (!firstPayloadPageIsLastPage) {
                this.state = 0;
                return;
            }
        }
        this.totalGranules = firstPayloadPageGranulePosition;
        this.state = 3;
    }

    public long read(ExtractorInput input) throws IOException, InterruptedException {
        long lastPageSearchPosition;
        switch (this.state) {
            case 0:
                this.positionBeforeSeekToEnd = input.getPosition();
                this.state = 1;
                lastPageSearchPosition = this.endPosition - 65307;
                if (lastPageSearchPosition <= this.positionBeforeSeekToEnd) {
                    break;
                }
                return lastPageSearchPosition;
            case 1:
                break;
            case 2:
                lastPageSearchPosition = this.targetGranule;
                if (lastPageSearchPosition == 0) {
                    lastPageSearchPosition = 0;
                } else {
                    lastPageSearchPosition = getNextSeekPosition(lastPageSearchPosition, input);
                    if (lastPageSearchPosition >= 0) {
                        return lastPageSearchPosition;
                    }
                    ExtractorInput extractorInput = input;
                    lastPageSearchPosition = skipToPageOfGranule(extractorInput, this.targetGranule, -(lastPageSearchPosition + 2));
                }
                this.state = 3;
                return -(2 + lastPageSearchPosition);
            case 3:
                return -1;
            default:
                throw new IllegalStateException();
        }
        this.totalGranules = readGranuleOfLastPage(input);
        this.state = 3;
        return this.positionBeforeSeekToEnd;
    }

    public long startSeek(long timeUs) {
        boolean z;
        long j;
        int i = this.state;
        if (i != 3) {
            if (i != 2) {
                z = false;
                Assertions.checkArgument(z);
                j = 0;
                if (timeUs == 0) {
                    j = this.streamReader.convertTimeToGranule(timeUs);
                }
                this.targetGranule = j;
                this.state = 2;
                resetSeeking();
                return this.targetGranule;
            }
        }
        z = true;
        Assertions.checkArgument(z);
        j = 0;
        if (timeUs == 0) {
            j = this.streamReader.convertTimeToGranule(timeUs);
        }
        this.targetGranule = j;
        this.state = 2;
        resetSeeking();
        return this.targetGranule;
    }

    public OggSeekMap createSeekMap() {
        return this.totalGranules != 0 ? new OggSeekMap() : null;
    }

    public void resetSeeking() {
        this.start = this.startPosition;
        this.end = this.endPosition;
        this.startGranule = 0;
        this.endGranule = this.totalGranules;
    }

    public long getNextSeekPosition(long targetGranule, ExtractorInput input) throws IOException, InterruptedException {
        ExtractorInput extractorInput = input;
        long j = 2;
        if (this.start == this.end) {
            return -(r0.startGranule + 2);
        }
        long initialPosition = input.getPosition();
        long granuleDistance;
        if (skipToNextPage(extractorInput, r0.end)) {
            r0.pageHeader.populate(extractorInput, false);
            input.resetPeekPosition();
            granuleDistance = targetGranule - r0.pageHeader.granulePosition;
            int pageSize = r0.pageHeader.headerSize + r0.pageHeader.bodySize;
            if (granuleDistance >= 0) {
                if (granuleDistance <= 72000) {
                    extractorInput.skipFully(pageSize);
                    return -(r0.pageHeader.granulePosition + 2);
                }
            }
            if (granuleDistance < 0) {
                r0.end = initialPosition;
                r0.endGranule = r0.pageHeader.granulePosition;
            } else {
                r0.start = input.getPosition() + ((long) pageSize);
                r0.startGranule = r0.pageHeader.granulePosition;
                if ((r0.end - r0.start) + ((long) pageSize) < 100000) {
                    extractorInput.skipFully(pageSize);
                    return -(r0.startGranule + 2);
                }
            }
            long j2 = r0.end;
            long j3 = r0.start;
            if (j2 - j3 < 100000) {
                r0.end = j3;
                return j3;
            }
            j2 = (long) pageSize;
            if (granuleDistance > 0) {
                j = 1;
            }
            j = input.getPosition() - (j2 * j);
            j3 = r0.end;
            long j4 = r0.start;
            return Math.min(Math.max(j + (((j3 - j4) * granuleDistance) / (r0.endGranule - r0.startGranule)), j4), r0.end - 1);
        }
        granuleDistance = r0.start;
        if (granuleDistance != initialPosition) {
            return granuleDistance;
        }
        throw new IOException("No ogg page can be found.");
    }

    private long getEstimatedPosition(long position, long granuleDistance, long offset) {
        long j = this.endPosition;
        long j2 = this.startPosition;
        position += (((j - j2) * granuleDistance) / this.totalGranules) - offset;
        if (position < j2) {
            position = this.startPosition;
        }
        j = this.endPosition;
        if (position >= j) {
            return j - 1;
        }
        return position;
    }

    void skipToNextPage(ExtractorInput input) throws IOException, InterruptedException {
        if (!skipToNextPage(input, this.endPosition)) {
            throw new EOFException();
        }
    }

    boolean skipToNextPage(ExtractorInput input, long limit) throws IOException, InterruptedException {
        limit = Math.min(3 + limit, this.endPosition);
        byte[] buffer = new byte[2048];
        int peekLength = buffer.length;
        while (true) {
            if (input.getPosition() + ((long) peekLength) > limit) {
                peekLength = (int) (limit - input.getPosition());
                if (peekLength < 4) {
                    return false;
                }
            }
            input.peekFully(buffer, 0, peekLength, false);
            int i = 0;
            while (i < peekLength - 3) {
                if (buffer[i] == (byte) 79 && buffer[i + 1] == (byte) 103 && buffer[i + 2] == (byte) 103 && buffer[i + 3] == (byte) 83) {
                    input.skipFully(i);
                    return true;
                }
                i++;
            }
            input.skipFully(peekLength - 3);
        }
    }

    long readGranuleOfLastPage(ExtractorInput input) throws IOException, InterruptedException {
        skipToNextPage(input);
        this.pageHeader.reset();
        while ((this.pageHeader.type & 4) != 4 && input.getPosition() < this.endPosition) {
            this.pageHeader.populate(input, false);
            input.skipFully(this.pageHeader.headerSize + this.pageHeader.bodySize);
        }
        return this.pageHeader.granulePosition;
    }

    long skipToPageOfGranule(ExtractorInput input, long targetGranule, long currentGranule) throws IOException, InterruptedException {
        this.pageHeader.populate(input, false);
        while (this.pageHeader.granulePosition < targetGranule) {
            input.skipFully(this.pageHeader.headerSize + this.pageHeader.bodySize);
            currentGranule = this.pageHeader.granulePosition;
            this.pageHeader.populate(input, false);
        }
        input.resetPeekPosition();
        return currentGranule;
    }
}
