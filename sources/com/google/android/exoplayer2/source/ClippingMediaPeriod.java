package com.google.android.exoplayer2.source;

import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.FormatHolder;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.source.MediaPeriod.Callback;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;

public final class ClippingMediaPeriod implements MediaPeriod, Callback {
    private Callback callback;
    long endUs;
    public final MediaPeriod mediaPeriod;
    private long pendingInitialDiscontinuityPositionUs;
    private ClippingSampleStream[] sampleStreams = new ClippingSampleStream[0];
    long startUs;

    private final class ClippingSampleStream implements SampleStream {
        public final SampleStream childStream;
        private boolean sentEos;

        public ClippingSampleStream(SampleStream childStream) {
            this.childStream = childStream;
        }

        public void clearSentEos() {
            this.sentEos = false;
        }

        public boolean isReady() {
            return !ClippingMediaPeriod.this.isPendingInitialDiscontinuity() && this.childStream.isReady();
        }

        public void maybeThrowError() throws IOException {
            this.childStream.maybeThrowError();
        }

        public int readData(FormatHolder formatHolder, DecoderInputBuffer buffer, boolean requireFormat) {
            if (ClippingMediaPeriod.this.isPendingInitialDiscontinuity()) {
                return -3;
            }
            if (this.sentEos) {
                buffer.setFlags(4);
                return -4;
            }
            int result = this.childStream.readData(formatHolder, buffer, requireFormat);
            if (result == -5) {
                Format format = formatHolder.format;
                if (format.encoderDelay == 0) {
                    if (format.encoderPadding == 0) {
                        return -5;
                    }
                }
                int i = 0;
                int encoderDelay = ClippingMediaPeriod.this.startUs != 0 ? 0 : format.encoderDelay;
                if (ClippingMediaPeriod.this.endUs == Long.MIN_VALUE) {
                    i = format.encoderPadding;
                }
                formatHolder.format = format.copyWithGaplessInfo(encoderDelay, i);
                return -5;
            }
            if (ClippingMediaPeriod.this.endUs != Long.MIN_VALUE) {
                if (result == -4) {
                    if (buffer.timeUs >= ClippingMediaPeriod.this.endUs) {
                        buffer.clear();
                        buffer.setFlags(4);
                        this.sentEos = true;
                        return -4;
                    }
                }
                if (result == -3) {
                    if (ClippingMediaPeriod.this.getBufferedPositionUs() != Long.MIN_VALUE) {
                        return result;
                    }
                    buffer.clear();
                    buffer.setFlags(4);
                    this.sentEos = true;
                    return -4;
                }
            }
            return result;
        }

        public int skipData(long positionUs) {
            if (ClippingMediaPeriod.this.isPendingInitialDiscontinuity()) {
                return -3;
            }
            return this.childStream.skipData(positionUs);
        }
    }

    public ClippingMediaPeriod(MediaPeriod mediaPeriod, boolean enableInitialDiscontinuity, long startUs, long endUs) {
        this.mediaPeriod = mediaPeriod;
        this.pendingInitialDiscontinuityPositionUs = enableInitialDiscontinuity ? startUs : C0555C.TIME_UNSET;
        this.startUs = startUs;
        this.endUs = endUs;
    }

    public void updateClipping(long startUs, long endUs) {
        this.startUs = startUs;
        this.endUs = endUs;
    }

    public void prepare(Callback callback, long positionUs) {
        this.callback = callback;
        this.mediaPeriod.prepare(this, positionUs);
    }

    public void maybeThrowPrepareError() throws IOException {
        this.mediaPeriod.maybeThrowPrepareError();
    }

    public TrackGroupArray getTrackGroups() {
        return this.mediaPeriod.getTrackGroups();
    }

    public long selectTracks(TrackSelection[] selections, boolean[] mayRetainStreamFlags, SampleStream[] streams, boolean[] streamResetFlags, long positionUs) {
        long j;
        boolean z;
        int i;
        this.sampleStreams = new ClippingSampleStream[streams.length];
        SampleStream[] childStreams = new SampleStream[streams.length];
        int i2 = 0;
        while (true) {
            SampleStream sampleStream = null;
            if (i2 >= streams.length) {
                break;
            }
            ClippingSampleStream[] clippingSampleStreamArr = this.sampleStreams;
            clippingSampleStreamArr[i2] = (ClippingSampleStream) streams[i2];
            if (clippingSampleStreamArr[i2] != null) {
                sampleStream = clippingSampleStreamArr[i2].childStream;
            }
            childStreams[i2] = sampleStream;
            i2++;
        }
        long enablePositionUs = this.mediaPeriod.selectTracks(selections, mayRetainStreamFlags, childStreams, streamResetFlags, positionUs);
        if (isPendingInitialDiscontinuity()) {
            j = this.startUs;
            if (positionUs == j) {
                if (shouldKeepInitialDiscontinuity(j, selections)) {
                    j = enablePositionUs;
                    this.pendingInitialDiscontinuityPositionUs = j;
                    if (enablePositionUs != positionUs) {
                        if (enablePositionUs >= this.startUs) {
                            j = this.endUs;
                            if (j != Long.MIN_VALUE) {
                                if (enablePositionUs <= j) {
                                }
                            }
                        }
                        z = false;
                        Assertions.checkState(z);
                        for (i = 0; i < streams.length; i++) {
                            if (childStreams[i] != null) {
                                this.sampleStreams[i] = null;
                            } else {
                                if (streams[i] != null) {
                                    if (this.sampleStreams[i].childStream == childStreams[i]) {
                                    }
                                }
                                this.sampleStreams[i] = new ClippingSampleStream(childStreams[i]);
                            }
                            streams[i] = this.sampleStreams[i];
                        }
                        return enablePositionUs;
                    }
                    z = true;
                    Assertions.checkState(z);
                    for (i = 0; i < streams.length; i++) {
                        if (childStreams[i] != null) {
                            if (streams[i] != null) {
                                if (this.sampleStreams[i].childStream == childStreams[i]) {
                                }
                            }
                            this.sampleStreams[i] = new ClippingSampleStream(childStreams[i]);
                        } else {
                            this.sampleStreams[i] = null;
                        }
                        streams[i] = this.sampleStreams[i];
                    }
                    return enablePositionUs;
                }
                j = C0555C.TIME_UNSET;
                this.pendingInitialDiscontinuityPositionUs = j;
                if (enablePositionUs != positionUs) {
                    if (enablePositionUs >= this.startUs) {
                        j = this.endUs;
                        if (j != Long.MIN_VALUE) {
                            if (enablePositionUs <= j) {
                            }
                        }
                    }
                    z = false;
                    Assertions.checkState(z);
                    for (i = 0; i < streams.length; i++) {
                        if (childStreams[i] != null) {
                            this.sampleStreams[i] = null;
                        } else {
                            if (streams[i] != null) {
                                if (this.sampleStreams[i].childStream == childStreams[i]) {
                                }
                            }
                            this.sampleStreams[i] = new ClippingSampleStream(childStreams[i]);
                        }
                        streams[i] = this.sampleStreams[i];
                    }
                    return enablePositionUs;
                }
                z = true;
                Assertions.checkState(z);
                for (i = 0; i < streams.length; i++) {
                    if (childStreams[i] != null) {
                        if (streams[i] != null) {
                            if (this.sampleStreams[i].childStream == childStreams[i]) {
                            }
                        }
                        this.sampleStreams[i] = new ClippingSampleStream(childStreams[i]);
                    } else {
                        this.sampleStreams[i] = null;
                    }
                    streams[i] = this.sampleStreams[i];
                }
                return enablePositionUs;
            }
        }
        j = C0555C.TIME_UNSET;
        this.pendingInitialDiscontinuityPositionUs = j;
        if (enablePositionUs != positionUs) {
            if (enablePositionUs >= this.startUs) {
                j = this.endUs;
                if (j != Long.MIN_VALUE) {
                    if (enablePositionUs <= j) {
                    }
                }
            }
            z = false;
            Assertions.checkState(z);
            for (i = 0; i < streams.length; i++) {
                if (childStreams[i] != null) {
                    this.sampleStreams[i] = null;
                } else {
                    if (streams[i] != null) {
                        if (this.sampleStreams[i].childStream == childStreams[i]) {
                        }
                    }
                    this.sampleStreams[i] = new ClippingSampleStream(childStreams[i]);
                }
                streams[i] = this.sampleStreams[i];
            }
            return enablePositionUs;
        }
        z = true;
        Assertions.checkState(z);
        for (i = 0; i < streams.length; i++) {
            if (childStreams[i] != null) {
                if (streams[i] != null) {
                    if (this.sampleStreams[i].childStream == childStreams[i]) {
                    }
                }
                this.sampleStreams[i] = new ClippingSampleStream(childStreams[i]);
            } else {
                this.sampleStreams[i] = null;
            }
            streams[i] = this.sampleStreams[i];
        }
        return enablePositionUs;
    }

    public void discardBuffer(long positionUs, boolean toKeyframe) {
        this.mediaPeriod.discardBuffer(positionUs, toKeyframe);
    }

    public void reevaluateBuffer(long positionUs) {
        this.mediaPeriod.reevaluateBuffer(positionUs);
    }

    public long readDiscontinuity() {
        if (isPendingInitialDiscontinuity()) {
            long initialDiscontinuityUs = this.pendingInitialDiscontinuityPositionUs;
            this.pendingInitialDiscontinuityPositionUs = C0555C.TIME_UNSET;
            long childDiscontinuityUs = readDiscontinuity();
            return childDiscontinuityUs != C0555C.TIME_UNSET ? childDiscontinuityUs : initialDiscontinuityUs;
        }
        initialDiscontinuityUs = this.mediaPeriod.readDiscontinuity();
        if (initialDiscontinuityUs == C0555C.TIME_UNSET) {
            return C0555C.TIME_UNSET;
        }
        boolean z = true;
        Assertions.checkState(initialDiscontinuityUs >= this.startUs);
        long j = this.endUs;
        if (j != Long.MIN_VALUE) {
            if (initialDiscontinuityUs > j) {
                z = false;
            }
        }
        Assertions.checkState(z);
        return initialDiscontinuityUs;
    }

    public long getBufferedPositionUs() {
        long bufferedPositionUs = this.mediaPeriod.getBufferedPositionUs();
        if (bufferedPositionUs != Long.MIN_VALUE) {
            long j = this.endUs;
            if (j == Long.MIN_VALUE || bufferedPositionUs < j) {
                return bufferedPositionUs;
            }
        }
        return Long.MIN_VALUE;
    }

    public long seekToUs(long positionUs) {
        this.pendingInitialDiscontinuityPositionUs = C0555C.TIME_UNSET;
        boolean z = false;
        for (ClippingSampleStream sampleStream : this.sampleStreams) {
            if (sampleStream != null) {
                sampleStream.clearSentEos();
            }
        }
        long seekUs = this.mediaPeriod.seekToUs(positionUs);
        if (seekUs != positionUs) {
            if (seekUs >= this.startUs) {
                long j = this.endUs;
                if (j != Long.MIN_VALUE) {
                    if (seekUs <= j) {
                    }
                }
            }
            Assertions.checkState(z);
            return seekUs;
        }
        z = true;
        Assertions.checkState(z);
        return seekUs;
    }

    public long getAdjustedSeekPositionUs(long positionUs, SeekParameters seekParameters) {
        long j = this.startUs;
        if (positionUs == j) {
            return j;
        }
        return this.mediaPeriod.getAdjustedSeekPositionUs(positionUs, clipSeekParameters(positionUs, seekParameters));
    }

    public long getNextLoadPositionUs() {
        long nextLoadPositionUs = this.mediaPeriod.getNextLoadPositionUs();
        if (nextLoadPositionUs != Long.MIN_VALUE) {
            long j = this.endUs;
            if (j == Long.MIN_VALUE || nextLoadPositionUs < j) {
                return nextLoadPositionUs;
            }
        }
        return Long.MIN_VALUE;
    }

    public boolean continueLoading(long positionUs) {
        return this.mediaPeriod.continueLoading(positionUs);
    }

    public void onPrepared(MediaPeriod mediaPeriod) {
        this.callback.onPrepared(this);
    }

    public void onContinueLoadingRequested(MediaPeriod source) {
        this.callback.onContinueLoadingRequested(this);
    }

    boolean isPendingInitialDiscontinuity() {
        return this.pendingInitialDiscontinuityPositionUs != C0555C.TIME_UNSET;
    }

    private SeekParameters clipSeekParameters(long positionUs, SeekParameters seekParameters) {
        long toleranceBeforeUs = Util.constrainValue(seekParameters.toleranceBeforeUs, 0, positionUs - this.startUs);
        long toleranceAfterUs = seekParameters.toleranceAfterUs;
        long j = this.endUs;
        toleranceAfterUs = Util.constrainValue(toleranceAfterUs, 0, j == Long.MIN_VALUE ? Long.MAX_VALUE : j - positionUs);
        if (toleranceBeforeUs == seekParameters.toleranceBeforeUs && toleranceAfterUs == seekParameters.toleranceAfterUs) {
            return seekParameters;
        }
        return new SeekParameters(toleranceBeforeUs, toleranceAfterUs);
    }

    private static boolean shouldKeepInitialDiscontinuity(long startUs, TrackSelection[] selections) {
        if (startUs != 0) {
            for (TrackSelection trackSelection : selections) {
                if (trackSelection != null) {
                    if (!MimeTypes.isAudio(trackSelection.getSelectedFormat().sampleMimeType)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
