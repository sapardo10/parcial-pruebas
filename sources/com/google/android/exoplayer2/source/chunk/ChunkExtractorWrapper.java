package com.google.android.exoplayer2.source.chunk;

import android.support.annotation.Nullable;
import android.util.SparseArray;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.extractor.DummyTrackOutput;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.extractor.TrackOutput.CryptoData;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.io.IOException;

public final class ChunkExtractorWrapper implements ExtractorOutput {
    private final SparseArray<BindingTrackOutput> bindingTrackOutputs = new SparseArray();
    private long endTimeUs;
    public final Extractor extractor;
    private boolean extractorInitialized;
    private final Format primaryTrackManifestFormat;
    private final int primaryTrackType;
    private Format[] sampleFormats;
    private SeekMap seekMap;
    private TrackOutputProvider trackOutputProvider;

    public interface TrackOutputProvider {
        TrackOutput track(int i, int i2);
    }

    private static final class BindingTrackOutput implements TrackOutput {
        private final DummyTrackOutput dummyTrackOutput = new DummyTrackOutput();
        private long endTimeUs;
        private final int id;
        private final Format manifestFormat;
        public Format sampleFormat;
        private TrackOutput trackOutput;
        private final int type;

        public BindingTrackOutput(int id, int type, Format manifestFormat) {
            this.id = id;
            this.type = type;
            this.manifestFormat = manifestFormat;
        }

        public void bind(TrackOutputProvider trackOutputProvider, long endTimeUs) {
            if (trackOutputProvider == null) {
                this.trackOutput = this.dummyTrackOutput;
                return;
            }
            this.endTimeUs = endTimeUs;
            this.trackOutput = trackOutputProvider.track(this.id, this.type);
            Format format = this.sampleFormat;
            if (format != null) {
                this.trackOutput.format(format);
            }
        }

        public void format(Format format) {
            Format format2 = this.manifestFormat;
            this.sampleFormat = format2 != null ? format.copyWithManifestFormatInfo(format2) : format;
            this.trackOutput.format(this.sampleFormat);
        }

        public int sampleData(ExtractorInput input, int length, boolean allowEndOfInput) throws IOException, InterruptedException {
            return this.trackOutput.sampleData(input, length, allowEndOfInput);
        }

        public void sampleData(ParsableByteArray data, int length) {
            this.trackOutput.sampleData(data, length);
        }

        public void sampleMetadata(long timeUs, int flags, int size, int offset, CryptoData cryptoData) {
            long j = this.endTimeUs;
            if (j != C0555C.TIME_UNSET && timeUs >= j) {
                this.trackOutput = this.dummyTrackOutput;
            }
            this.trackOutput.sampleMetadata(timeUs, flags, size, offset, cryptoData);
        }
    }

    public ChunkExtractorWrapper(Extractor extractor, int primaryTrackType, Format primaryTrackManifestFormat) {
        this.extractor = extractor;
        this.primaryTrackType = primaryTrackType;
        this.primaryTrackManifestFormat = primaryTrackManifestFormat;
    }

    public SeekMap getSeekMap() {
        return this.seekMap;
    }

    public Format[] getSampleFormats() {
        return this.sampleFormats;
    }

    public void init(@Nullable TrackOutputProvider trackOutputProvider, long startTimeUs, long endTimeUs) {
        this.trackOutputProvider = trackOutputProvider;
        this.endTimeUs = endTimeUs;
        if (this.extractorInitialized) {
            this.extractor.seek(0, startTimeUs == C0555C.TIME_UNSET ? 0 : startTimeUs);
            for (int i = 0; i < this.bindingTrackOutputs.size(); i++) {
                ((BindingTrackOutput) this.bindingTrackOutputs.valueAt(i)).bind(trackOutputProvider, endTimeUs);
            }
            return;
        }
        this.extractor.init(this);
        if (startTimeUs != C0555C.TIME_UNSET) {
            this.extractor.seek(0, startTimeUs);
        }
        this.extractorInitialized = true;
    }

    public TrackOutput track(int id, int type) {
        BindingTrackOutput bindingTrackOutput = (BindingTrackOutput) this.bindingTrackOutputs.get(id);
        if (bindingTrackOutput != null) {
            return bindingTrackOutput;
        }
        Assertions.checkState(this.sampleFormats == null);
        bindingTrackOutput = new BindingTrackOutput(id, type, type == this.primaryTrackType ? this.primaryTrackManifestFormat : null);
        bindingTrackOutput.bind(this.trackOutputProvider, this.endTimeUs);
        this.bindingTrackOutputs.put(id, bindingTrackOutput);
        return bindingTrackOutput;
    }

    public void endTracks() {
        Format[] sampleFormats = new Format[this.bindingTrackOutputs.size()];
        for (int i = 0; i < this.bindingTrackOutputs.size(); i++) {
            sampleFormats[i] = ((BindingTrackOutput) this.bindingTrackOutputs.valueAt(i)).sampleFormat;
        }
        this.sampleFormats = sampleFormats;
    }

    public void seekMap(SeekMap seekMap) {
        this.seekMap = seekMap;
    }
}
