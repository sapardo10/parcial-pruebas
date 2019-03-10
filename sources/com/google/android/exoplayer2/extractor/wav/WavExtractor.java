package com.google.android.exoplayer2.extractor.wav;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.MimeTypes;
import java.io.IOException;

public final class WavExtractor implements Extractor {
    public static final ExtractorsFactory FACTORY = -$$Lambda$WavExtractor$5r6M_S0QCNNj_Xavzq9WwuFHep0.INSTANCE;
    private static final int MAX_INPUT_SIZE = 32768;
    private int bytesPerFrame;
    private ExtractorOutput extractorOutput;
    private int pendingBytes;
    private TrackOutput trackOutput;
    private WavHeader wavHeader;

    public boolean sniff(ExtractorInput input) throws IOException, InterruptedException {
        return WavHeaderReader.peek(input) != null;
    }

    public void init(ExtractorOutput output) {
        this.extractorOutput = output;
        this.trackOutput = output.track(0, 1);
        this.wavHeader = null;
        output.endTracks();
    }

    public void seek(long position, long timeUs) {
        this.pendingBytes = 0;
    }

    public void release() {
    }

    public int read(ExtractorInput input, PositionHolder seekPosition) throws IOException, InterruptedException {
        ExtractorInput extractorInput = input;
        if (this.wavHeader == null) {
            r0.wavHeader = WavHeaderReader.peek(input);
            WavHeader wavHeader = r0.wavHeader;
            if (wavHeader != null) {
                r0.trackOutput.format(Format.createAudioSampleFormat(null, MimeTypes.AUDIO_RAW, null, wavHeader.getBitrate(), 32768, r0.wavHeader.getNumChannels(), r0.wavHeader.getSampleRateHz(), r0.wavHeader.getEncoding(), null, null, 0, null));
                r0.bytesPerFrame = r0.wavHeader.getBytesPerFrame();
            } else {
                throw new ParserException("Unsupported or unrecognized wav header.");
            }
        }
        if (!r0.wavHeader.hasDataBounds()) {
            WavHeaderReader.skipToData(extractorInput, r0.wavHeader);
            r0.extractorOutput.seekMap(r0.wavHeader);
        }
        long dataLimit = r0.wavHeader.getDataLimit();
        int i = 0;
        Assertions.checkState(dataLimit != -1);
        long bytesLeft = dataLimit - input.getPosition();
        if (bytesLeft <= 0) {
            return -1;
        }
        int bytesAppended = r0.trackOutput.sampleData(extractorInput, (int) Math.min((long) (32768 - r0.pendingBytes), bytesLeft), 1);
        if (bytesAppended != -1) {
            r0.pendingBytes += bytesAppended;
        }
        int pendingFrames = r0.pendingBytes / r0.bytesPerFrame;
        if (pendingFrames > 0) {
            long timeUs = r0.wavHeader.getTimeUs(input.getPosition() - ((long) r0.pendingBytes));
            int size = r0.bytesPerFrame * pendingFrames;
            r0.pendingBytes -= size;
            r0.trackOutput.sampleMetadata(timeUs, 1, size, r0.pendingBytes, null);
        }
        if (bytesAppended == -1) {
            i = -1;
        }
        return i;
    }
}
