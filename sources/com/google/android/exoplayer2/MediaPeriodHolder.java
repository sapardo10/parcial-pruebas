package com.google.android.exoplayer2;

import com.google.android.exoplayer2.source.ClippingMediaPeriod;
import com.google.android.exoplayer2.source.EmptySampleStream;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.SampleStream;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectorResult;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;

final class MediaPeriodHolder {
    private static final String TAG = "MediaPeriodHolder";
    public boolean hasEnabledTracks;
    public MediaPeriodInfo info;
    public final boolean[] mayRetainStreamFlags;
    public final MediaPeriod mediaPeriod;
    private final MediaSource mediaSource;
    public MediaPeriodHolder next;
    private TrackSelectorResult periodTrackSelectorResult;
    public boolean prepared;
    private final RendererCapabilities[] rendererCapabilities;
    private long rendererPositionOffsetUs;
    public final SampleStream[] sampleStreams;
    public TrackGroupArray trackGroups;
    private final TrackSelector trackSelector;
    public TrackSelectorResult trackSelectorResult;
    public final Object uid;

    public MediaPeriodHolder(RendererCapabilities[] rendererCapabilities, long rendererPositionOffsetUs, TrackSelector trackSelector, Allocator allocator, MediaSource mediaSource, MediaPeriodInfo info) {
        RendererCapabilities[] rendererCapabilitiesArr = rendererCapabilities;
        MediaSource mediaSource2 = mediaSource;
        MediaPeriodInfo mediaPeriodInfo = info;
        this.rendererCapabilities = rendererCapabilitiesArr;
        this.rendererPositionOffsetUs = rendererPositionOffsetUs - mediaPeriodInfo.startPositionUs;
        this.trackSelector = trackSelector;
        this.mediaSource = mediaSource2;
        this.uid = Assertions.checkNotNull(mediaPeriodInfo.id.periodUid);
        this.info = mediaPeriodInfo;
        this.sampleStreams = new SampleStream[rendererCapabilitiesArr.length];
        this.mayRetainStreamFlags = new boolean[rendererCapabilitiesArr.length];
        MediaPeriod mediaPeriod = mediaSource2.createPeriod(mediaPeriodInfo.id, allocator);
        if (mediaPeriodInfo.id.endPositionUs != Long.MIN_VALUE) {
            mediaPeriod = new ClippingMediaPeriod(mediaPeriod, true, 0, mediaPeriodInfo.id.endPositionUs);
        }
        r0.mediaPeriod = mediaPeriod;
    }

    public long toRendererTime(long periodTimeUs) {
        return getRendererOffset() + periodTimeUs;
    }

    public long toPeriodTime(long rendererTimeUs) {
        return rendererTimeUs - getRendererOffset();
    }

    public long getRendererOffset() {
        return this.rendererPositionOffsetUs;
    }

    public long getStartPositionRendererTime() {
        return this.info.startPositionUs + this.rendererPositionOffsetUs;
    }

    public boolean isFullyBuffered() {
        if (this.prepared) {
            if (this.hasEnabledTracks) {
                if (this.mediaPeriod.getBufferedPositionUs() == Long.MIN_VALUE) {
                }
            }
            return true;
        }
        return false;
    }

    public long getDurationUs() {
        return this.info.durationUs;
    }

    public long getBufferedPositionUs() {
        if (!this.prepared) {
            return this.info.startPositionUs;
        }
        long bufferedPositionUs = this.hasEnabledTracks ? this.mediaPeriod.getBufferedPositionUs() : Long.MIN_VALUE;
        return bufferedPositionUs == Long.MIN_VALUE ? this.info.durationUs : bufferedPositionUs;
    }

    public long getNextLoadPositionUs() {
        return !this.prepared ? 0 : this.mediaPeriod.getNextLoadPositionUs();
    }

    public void handlePrepared(float playbackSpeed) throws ExoPlaybackException {
        this.prepared = true;
        this.trackGroups = this.mediaPeriod.getTrackGroups();
        selectTracks(playbackSpeed);
        long newStartPositionUs = applyTrackSelection(this.info.startPositionUs, false);
        this.rendererPositionOffsetUs += this.info.startPositionUs - newStartPositionUs;
        this.info = this.info.copyWithStartPositionUs(newStartPositionUs);
    }

    public void reevaluateBuffer(long rendererPositionUs) {
        if (this.prepared) {
            this.mediaPeriod.reevaluateBuffer(toPeriodTime(rendererPositionUs));
        }
    }

    public void continueLoading(long rendererPositionUs) {
        this.mediaPeriod.continueLoading(toPeriodTime(rendererPositionUs));
    }

    public boolean selectTracks(float playbackSpeed) throws ExoPlaybackException {
        TrackSelectorResult selectorResult = this.trackSelector.selectTracks(this.rendererCapabilities, this.trackGroups);
        int i = 0;
        if (selectorResult.isEquivalent(this.periodTrackSelectorResult)) {
            return false;
        }
        this.trackSelectorResult = selectorResult;
        TrackSelection[] all = this.trackSelectorResult.selections.getAll();
        int length = all.length;
        while (i < length) {
            TrackSelection trackSelection = all[i];
            if (trackSelection != null) {
                trackSelection.onPlaybackSpeed(playbackSpeed);
            }
            i++;
        }
        return true;
    }

    public long applyTrackSelection(long positionUs, boolean forceRecreateStreams) {
        return applyTrackSelection(positionUs, forceRecreateStreams, new boolean[this.rendererCapabilities.length]);
    }

    public long applyTrackSelection(long positionUs, boolean forceRecreateStreams, boolean[] streamResetFlags) {
        int i = 0;
        while (true) {
            boolean z = false;
            if (i >= this.trackSelectorResult.length) {
                break;
            }
            boolean[] zArr = this.mayRetainStreamFlags;
            if (!forceRecreateStreams) {
                if (this.trackSelectorResult.isEquivalent(this.periodTrackSelectorResult, i)) {
                    z = true;
                    zArr[i] = z;
                    i++;
                }
            }
            zArr[i] = z;
            i++;
        }
        disassociateNoSampleRenderersWithEmptySampleStream(this.sampleStreams);
        updatePeriodTrackSelectorResult(this.trackSelectorResult);
        TrackSelectionArray trackSelections = this.trackSelectorResult.selections;
        positionUs = this.mediaPeriod.selectTracks(trackSelections.getAll(), this.mayRetainStreamFlags, this.sampleStreams, streamResetFlags, positionUs);
        associateNoSampleRenderersWithEmptySampleStream(this.sampleStreams);
        this.hasEnabledTracks = false;
        int i2 = 0;
        while (true) {
            SampleStream[] sampleStreamArr = this.sampleStreams;
            if (i2 >= sampleStreamArr.length) {
                return positionUs;
            }
            if (sampleStreamArr[i2] != null) {
                Assertions.checkState(this.trackSelectorResult.isRendererEnabled(i2));
                if (this.rendererCapabilities[i2].getTrackType() != 6) {
                    this.hasEnabledTracks = true;
                }
            } else {
                Assertions.checkState(trackSelections.get(i2) == null);
            }
            i2++;
        }
    }

    public void release() {
        updatePeriodTrackSelectorResult(null);
        try {
            if (this.info.id.endPositionUs != Long.MIN_VALUE) {
                this.mediaSource.releasePeriod(((ClippingMediaPeriod) this.mediaPeriod).mediaPeriod);
            } else {
                this.mediaSource.releasePeriod(this.mediaPeriod);
            }
        } catch (RuntimeException e) {
            Log.m7e(TAG, "Period release failed.", e);
        }
    }

    private void updatePeriodTrackSelectorResult(TrackSelectorResult trackSelectorResult) {
        TrackSelectorResult trackSelectorResult2 = this.periodTrackSelectorResult;
        if (trackSelectorResult2 != null) {
            disableTrackSelectionsInResult(trackSelectorResult2);
        }
        this.periodTrackSelectorResult = trackSelectorResult;
        trackSelectorResult2 = this.periodTrackSelectorResult;
        if (trackSelectorResult2 != null) {
            enableTrackSelectionsInResult(trackSelectorResult2);
        }
    }

    private void enableTrackSelectionsInResult(TrackSelectorResult trackSelectorResult) {
        for (int i = 0; i < trackSelectorResult.length; i++) {
            boolean rendererEnabled = trackSelectorResult.isRendererEnabled(i);
            TrackSelection trackSelection = trackSelectorResult.selections.get(i);
            if (rendererEnabled && trackSelection != null) {
                trackSelection.enable();
            }
        }
    }

    private void disableTrackSelectionsInResult(TrackSelectorResult trackSelectorResult) {
        for (int i = 0; i < trackSelectorResult.length; i++) {
            boolean rendererEnabled = trackSelectorResult.isRendererEnabled(i);
            TrackSelection trackSelection = trackSelectorResult.selections.get(i);
            if (rendererEnabled && trackSelection != null) {
                trackSelection.disable();
            }
        }
    }

    private void disassociateNoSampleRenderersWithEmptySampleStream(SampleStream[] sampleStreams) {
        int i = 0;
        while (true) {
            RendererCapabilities[] rendererCapabilitiesArr = this.rendererCapabilities;
            if (i < rendererCapabilitiesArr.length) {
                if (rendererCapabilitiesArr[i].getTrackType() == 6) {
                    sampleStreams[i] = null;
                }
                i++;
            } else {
                return;
            }
        }
    }

    private void associateNoSampleRenderersWithEmptySampleStream(SampleStream[] sampleStreams) {
        int i = 0;
        while (true) {
            RendererCapabilities[] rendererCapabilitiesArr = this.rendererCapabilities;
            if (i < rendererCapabilitiesArr.length) {
                if (rendererCapabilitiesArr[i].getTrackType() == 6) {
                    if (this.trackSelectorResult.isRendererEnabled(i)) {
                        sampleStreams[i] = new EmptySampleStream();
                    }
                }
                i++;
            } else {
                return;
            }
        }
    }
}
