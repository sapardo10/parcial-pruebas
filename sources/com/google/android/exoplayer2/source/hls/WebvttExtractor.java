package com.google.android.exoplayer2.source.hls;

import android.text.TextUtils;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.extractor.SeekMap.Unseekable;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.text.webvtt.WebvttParserUtil;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.TimestampAdjuster;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class WebvttExtractor implements Extractor {
    private static final int HEADER_MAX_LENGTH = 9;
    private static final int HEADER_MIN_LENGTH = 6;
    private static final Pattern LOCAL_TIMESTAMP = Pattern.compile("LOCAL:([^,]+)");
    private static final Pattern MEDIA_TIMESTAMP = Pattern.compile("MPEGTS:(\\d+)");
    private final String language;
    private ExtractorOutput output;
    private byte[] sampleData = new byte[1024];
    private final ParsableByteArray sampleDataWrapper = new ParsableByteArray();
    private int sampleSize;
    private final TimestampAdjuster timestampAdjuster;

    public WebvttExtractor(String language, TimestampAdjuster timestampAdjuster) {
        this.language = language;
        this.timestampAdjuster = timestampAdjuster;
    }

    public boolean sniff(ExtractorInput input) throws IOException, InterruptedException {
        input.peekFully(this.sampleData, 0, 6, false);
        this.sampleDataWrapper.reset(this.sampleData, 6);
        if (WebvttParserUtil.isWebvttHeaderLine(this.sampleDataWrapper)) {
            return true;
        }
        input.peekFully(this.sampleData, 6, 3, false);
        this.sampleDataWrapper.reset(this.sampleData, 9);
        return WebvttParserUtil.isWebvttHeaderLine(this.sampleDataWrapper);
    }

    public void init(ExtractorOutput output) {
        this.output = output;
        output.seekMap(new Unseekable(C0555C.TIME_UNSET));
    }

    public void seek(long position, long timeUs) {
        throw new IllegalStateException();
    }

    public void release() {
    }

    public int read(ExtractorInput input, PositionHolder seekPosition) throws IOException, InterruptedException {
        int currentFileSize = (int) input.getLength();
        int i = this.sampleSize;
        byte[] bArr = this.sampleData;
        if (i == bArr.length) {
            this.sampleData = Arrays.copyOf(bArr, ((currentFileSize != -1 ? currentFileSize : bArr.length) * 3) / 2);
        }
        i = this.sampleData;
        int i2 = this.sampleSize;
        i = input.read(i, i2, i.length - i2);
        if (i != -1) {
            this.sampleSize += i;
            if (currentFileSize != -1) {
                if (this.sampleSize != currentFileSize) {
                }
            }
            return 0;
        }
        processSample();
        return -1;
    }

    private void processSample() throws ParserException {
        Matcher localTimestampMatcher;
        ParsableByteArray webvttData = new ParsableByteArray(this.sampleData);
        WebvttParserUtil.validateWebvttHeaderLine(webvttData);
        long vttTimestampUs = 0;
        long tsTimestampUs = 0;
        while (true) {
            CharSequence readLine = webvttData.readLine();
            CharSequence line = readLine;
            if (TextUtils.isEmpty(readLine)) {
                break;
            } else if (line.startsWith("X-TIMESTAMP-MAP")) {
                localTimestampMatcher = LOCAL_TIMESTAMP.matcher(line);
                if (localTimestampMatcher.find()) {
                    Matcher mediaTimestampMatcher = MEDIA_TIMESTAMP.matcher(line);
                    if (mediaTimestampMatcher.find()) {
                        vttTimestampUs = WebvttParserUtil.parseTimestampUs(localTimestampMatcher.group(1));
                        tsTimestampUs = TimestampAdjuster.ptsToUs(Long.parseLong(mediaTimestampMatcher.group(1)));
                    } else {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("X-TIMESTAMP-MAP doesn't contain media timestamp: ");
                        stringBuilder.append(line);
                        throw new ParserException(stringBuilder.toString());
                    }
                }
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("X-TIMESTAMP-MAP doesn't contain local timestamp: ");
                stringBuilder2.append(line);
                throw new ParserException(stringBuilder2.toString());
            }
        }
        localTimestampMatcher = WebvttParserUtil.findNextCueHeader(webvttData);
        if (localTimestampMatcher == null) {
            buildTrackOutput(0);
            return;
        }
        long firstCueTimeUs = WebvttParserUtil.parseTimestampUs(localTimestampMatcher.group(1));
        long sampleTimeUs = r0.timestampAdjuster.adjustTsTimestamp(TimestampAdjuster.usToPts((firstCueTimeUs + tsTimestampUs) - vttTimestampUs));
        long subsampleOffsetUs = sampleTimeUs - firstCueTimeUs;
        TrackOutput trackOutput = buildTrackOutput(subsampleOffsetUs);
        r0.sampleDataWrapper.reset(r0.sampleData, r0.sampleSize);
        trackOutput.sampleData(r0.sampleDataWrapper, r0.sampleSize);
        trackOutput.sampleMetadata(sampleTimeUs, 1, r0.sampleSize, 0, null);
    }

    private TrackOutput buildTrackOutput(long subsampleOffsetUs) {
        TrackOutput trackOutput = this.output.track(0, 3);
        trackOutput.format(Format.createTextSampleFormat(null, MimeTypes.TEXT_VTT, null, -1, 0, this.language, null, subsampleOffsetUs));
        this.output.endTracks();
        return trackOutput;
    }
}
