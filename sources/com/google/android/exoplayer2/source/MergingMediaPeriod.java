package com.google.android.exoplayer2.source;

import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.source.MediaPeriod.Callback;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;

final class MergingMediaPeriod implements MediaPeriod, Callback {
    private Callback callback;
    private final ArrayList<MediaPeriod> childrenPendingPreparation = new ArrayList();
    private SequenceableLoader compositeSequenceableLoader;
    private final CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory;
    private MediaPeriod[] enabledPeriods;
    public final MediaPeriod[] periods;
    private final IdentityHashMap<SampleStream, Integer> streamPeriodIndices;
    private TrackGroupArray trackGroups;

    public MergingMediaPeriod(CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory, MediaPeriod... periods) {
        this.compositeSequenceableLoaderFactory = compositeSequenceableLoaderFactory;
        this.periods = periods;
        this.compositeSequenceableLoader = compositeSequenceableLoaderFactory.createCompositeSequenceableLoader(new SequenceableLoader[0]);
        this.streamPeriodIndices = new IdentityHashMap();
    }

    public void prepare(Callback callback, long positionUs) {
        this.callback = callback;
        Collections.addAll(this.childrenPendingPreparation, this.periods);
        for (MediaPeriod period : this.periods) {
            period.prepare(this, positionUs);
        }
    }

    public void maybeThrowPrepareError() throws IOException {
        for (MediaPeriod period : this.periods) {
            period.maybeThrowPrepareError();
        }
    }

    public TrackGroupArray getTrackGroups() {
        return this.trackGroups;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public long selectTracks(com.google.android.exoplayer2.trackselection.TrackSelection[] r20, boolean[] r21, com.google.android.exoplayer2.source.SampleStream[] r22, boolean[] r23, long r24) {
        /*
        r19 = this;
        r0 = r19;
        r1 = r20;
        r2 = r22;
        r3 = r1.length;
        r3 = new int[r3];
        r4 = r1.length;
        r4 = new int[r4];
        r5 = 0;
    L_0x000d:
        r6 = r1.length;
        if (r5 >= r6) goto L_0x0050;
    L_0x0010:
        r6 = r2[r5];
        r7 = -1;
        if (r6 != 0) goto L_0x0017;
    L_0x0015:
        r6 = -1;
        goto L_0x0025;
    L_0x0017:
        r6 = r0.streamPeriodIndices;
        r8 = r2[r5];
        r6 = r6.get(r8);
        r6 = (java.lang.Integer) r6;
        r6 = r6.intValue();
    L_0x0025:
        r3[r5] = r6;
        r4[r5] = r7;
        r6 = r1[r5];
        if (r6 == 0) goto L_0x004c;
    L_0x002d:
        r6 = r1[r5];
        r6 = r6.getTrackGroup();
        r8 = 0;
    L_0x0034:
        r9 = r0.periods;
        r10 = r9.length;
        if (r8 >= r10) goto L_0x004b;
    L_0x0039:
        r9 = r9[r8];
        r9 = r9.getTrackGroups();
        r9 = r9.indexOf(r6);
        if (r9 == r7) goto L_0x0048;
    L_0x0045:
        r4[r5] = r8;
        goto L_0x004d;
    L_0x0048:
        r8 = r8 + 1;
        goto L_0x0034;
    L_0x004b:
        goto L_0x004d;
    L_0x004d:
        r5 = r5 + 1;
        goto L_0x000d;
    L_0x0050:
        r5 = r0.streamPeriodIndices;
        r5.clear();
        r5 = r1.length;
        r5 = new com.google.android.exoplayer2.source.SampleStream[r5];
        r6 = r1.length;
        r6 = new com.google.android.exoplayer2.source.SampleStream[r6];
        r7 = r1.length;
        r14 = new com.google.android.exoplayer2.trackselection.TrackSelection[r7];
        r7 = new java.util.ArrayList;
        r8 = r0.periods;
        r8 = r8.length;
        r7.<init>(r8);
        r15 = r7;
        r7 = 0;
        r16 = r24;
        r12 = r7;
    L_0x006b:
        r7 = r0.periods;
        r7 = r7.length;
        r13 = 0;
        if (r12 >= r7) goto L_0x00fb;
    L_0x0071:
        r7 = 0;
    L_0x0072:
        r8 = r1.length;
        if (r7 >= r8) goto L_0x008c;
    L_0x0075:
        r8 = r3[r7];
        r9 = 0;
        if (r8 != r12) goto L_0x007d;
    L_0x007a:
        r8 = r2[r7];
        goto L_0x007e;
    L_0x007d:
        r8 = r9;
    L_0x007e:
        r6[r7] = r8;
        r8 = r4[r7];
        if (r8 != r12) goto L_0x0087;
    L_0x0084:
        r9 = r1[r7];
    L_0x0087:
        r14[r7] = r9;
        r7 = r7 + 1;
        goto L_0x0072;
    L_0x008c:
        r7 = r0.periods;
        r7 = r7[r12];
        r8 = r14;
        r9 = r21;
        r10 = r6;
        r11 = r23;
        r18 = r14;
        r2 = 0;
        r14 = r12;
        r12 = r16;
        r7 = r7.selectTracks(r8, r9, r10, r11, r12);
        if (r14 != 0) goto L_0x00a6;
    L_0x00a2:
        r9 = r7;
        r16 = r9;
        goto L_0x00aa;
    L_0x00a6:
        r9 = (r7 > r16 ? 1 : (r7 == r16 ? 0 : -1));
        if (r9 != 0) goto L_0x00f3;
    L_0x00aa:
        r9 = 0;
        r10 = 0;
    L_0x00ac:
        r11 = r1.length;
        if (r10 >= r11) goto L_0x00e0;
    L_0x00af:
        r11 = r4[r10];
        r13 = 1;
        if (r11 != r14) goto L_0x00ce;
    L_0x00b4:
        r11 = r6[r10];
        if (r11 == 0) goto L_0x00b9;
    L_0x00b8:
        goto L_0x00ba;
    L_0x00b9:
        r13 = 0;
    L_0x00ba:
        com.google.android.exoplayer2.util.Assertions.checkState(r13);
        r11 = r6[r10];
        r5[r10] = r11;
        r9 = 1;
        r11 = r0.streamPeriodIndices;
        r12 = r6[r10];
        r13 = java.lang.Integer.valueOf(r14);
        r11.put(r12, r13);
        goto L_0x00dd;
    L_0x00ce:
        r11 = r3[r10];
        if (r11 != r14) goto L_0x00dc;
    L_0x00d2:
        r11 = r6[r10];
        if (r11 != 0) goto L_0x00d7;
    L_0x00d6:
        goto L_0x00d8;
    L_0x00d7:
        r13 = 0;
    L_0x00d8:
        com.google.android.exoplayer2.util.Assertions.checkState(r13);
        goto L_0x00dd;
    L_0x00dd:
        r10 = r10 + 1;
        goto L_0x00ac;
    L_0x00e0:
        if (r9 == 0) goto L_0x00ea;
    L_0x00e2:
        r2 = r0.periods;
        r2 = r2[r14];
        r15.add(r2);
        goto L_0x00eb;
    L_0x00eb:
        r12 = r14 + 1;
        r14 = r18;
        r2 = r22;
        goto L_0x006b;
    L_0x00f3:
        r2 = new java.lang.IllegalStateException;
        r9 = "Children enabled at different positions.";
        r2.<init>(r9);
        throw r2;
    L_0x00fb:
        r18 = r14;
        r2 = 0;
        r14 = r12;
        r7 = r5.length;
        r2 = r22;
        r8 = 0;
        java.lang.System.arraycopy(r5, r8, r2, r8, r7);
        r7 = r15.size();
        r7 = new com.google.android.exoplayer2.source.MediaPeriod[r7];
        r0.enabledPeriods = r7;
        r7 = r0.enabledPeriods;
        r15.toArray(r7);
        r7 = r0.compositeSequenceableLoaderFactory;
        r8 = r0.enabledPeriods;
        r7 = r7.createCompositeSequenceableLoader(r8);
        r0.compositeSequenceableLoader = r7;
        return r16;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.MergingMediaPeriod.selectTracks(com.google.android.exoplayer2.trackselection.TrackSelection[], boolean[], com.google.android.exoplayer2.source.SampleStream[], boolean[], long):long");
    }

    public void discardBuffer(long positionUs, boolean toKeyframe) {
        for (MediaPeriod period : this.enabledPeriods) {
            period.discardBuffer(positionUs, toKeyframe);
        }
    }

    public void reevaluateBuffer(long positionUs) {
        this.compositeSequenceableLoader.reevaluateBuffer(positionUs);
    }

    public boolean continueLoading(long positionUs) {
        if (this.childrenPendingPreparation.isEmpty()) {
            return this.compositeSequenceableLoader.continueLoading(positionUs);
        }
        int childrenPendingPreparationSize = this.childrenPendingPreparation.size();
        for (int i = 0; i < childrenPendingPreparationSize; i++) {
            ((MediaPeriod) this.childrenPendingPreparation.get(i)).continueLoading(positionUs);
        }
        return false;
    }

    public long getNextLoadPositionUs() {
        return this.compositeSequenceableLoader.getNextLoadPositionUs();
    }

    public long readDiscontinuity() {
        long positionUs = this.periods[0].readDiscontinuity();
        int i = 1;
        while (true) {
            MediaPeriod[] mediaPeriodArr = this.periods;
            if (i >= mediaPeriodArr.length) {
                break;
            } else if (mediaPeriodArr[i].readDiscontinuity() == C0555C.TIME_UNSET) {
                i++;
            } else {
                throw new IllegalStateException("Child reported discontinuity.");
            }
        }
        if (positionUs != C0555C.TIME_UNSET) {
            for (MediaPeriod enabledPeriod : this.enabledPeriods) {
                if (enabledPeriod != this.periods[0]) {
                    if (enabledPeriod.seekToUs(positionUs) != positionUs) {
                        throw new IllegalStateException("Unexpected child seekToUs result.");
                    }
                }
            }
        }
        return positionUs;
    }

    public long getBufferedPositionUs() {
        return this.compositeSequenceableLoader.getBufferedPositionUs();
    }

    public long seekToUs(long positionUs) {
        positionUs = this.enabledPeriods[0].seekToUs(positionUs);
        int i = 1;
        while (true) {
            MediaPeriod[] mediaPeriodArr = this.enabledPeriods;
            if (i >= mediaPeriodArr.length) {
                return positionUs;
            }
            if (mediaPeriodArr[i].seekToUs(positionUs) == positionUs) {
                i++;
            } else {
                throw new IllegalStateException("Unexpected child seekToUs result.");
            }
        }
    }

    public long getAdjustedSeekPositionUs(long positionUs, SeekParameters seekParameters) {
        return this.enabledPeriods[0].getAdjustedSeekPositionUs(positionUs, seekParameters);
    }

    public void onPrepared(MediaPeriod preparedPeriod) {
        this.childrenPendingPreparation.remove(preparedPeriod);
        if (this.childrenPendingPreparation.isEmpty()) {
            int totalTrackGroupCount = 0;
            for (MediaPeriod period : this.periods) {
                totalTrackGroupCount += period.getTrackGroups().length;
            }
            TrackGroup[] trackGroupArray = new TrackGroup[totalTrackGroupCount];
            int trackGroupIndex = 0;
            for (MediaPeriod period2 : this.periods) {
                TrackGroupArray periodTrackGroups = period2.getTrackGroups();
                int periodTrackGroupCount = periodTrackGroups.length;
                int j = 0;
                while (j < periodTrackGroupCount) {
                    int trackGroupIndex2 = trackGroupIndex + 1;
                    trackGroupArray[trackGroupIndex] = periodTrackGroups.get(j);
                    j++;
                    trackGroupIndex = trackGroupIndex2;
                }
            }
            this.trackGroups = new TrackGroupArray(trackGroupArray);
            this.callback.onPrepared(this);
        }
    }

    public void onContinueLoadingRequested(MediaPeriod ignored) {
        this.callback.onContinueLoadingRequested(this);
    }
}
