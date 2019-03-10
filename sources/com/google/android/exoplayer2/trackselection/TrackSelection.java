package com.google.android.exoplayer2.trackselection;

import android.support.annotation.Nullable;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.chunk.MediaChunk;
import com.google.android.exoplayer2.source.chunk.MediaChunkIterator;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import java.util.List;

public interface TrackSelection {

    public final /* synthetic */ class -CC {
        @Deprecated
        public static void $default$updateSelectedTrack(TrackSelection -this, long playbackPositionUs, long bufferedDurationUs, long availableDurationUs) {
            throw new UnsupportedOperationException();
        }

        public static void $default$updateSelectedTrack(TrackSelection -this, long playbackPositionUs, long bufferedDurationUs, long availableDurationUs, List list, MediaChunkIterator[] mediaChunkIterators) {
            -this.updateSelectedTrack(playbackPositionUs, bufferedDurationUs, availableDurationUs);
        }
    }

    public interface Factory {
        TrackSelection createTrackSelection(TrackGroup trackGroup, BandwidthMeter bandwidthMeter, int... iArr);
    }

    boolean blacklist(int i, long j);

    void disable();

    void enable();

    int evaluateQueueSize(long j, List<? extends MediaChunk> list);

    Format getFormat(int i);

    int getIndexInTrackGroup(int i);

    Format getSelectedFormat();

    int getSelectedIndex();

    int getSelectedIndexInTrackGroup();

    @Nullable
    Object getSelectionData();

    int getSelectionReason();

    TrackGroup getTrackGroup();

    int indexOf(int i);

    int indexOf(Format format);

    int length();

    void onPlaybackSpeed(float f);

    @Deprecated
    void updateSelectedTrack(long j, long j2, long j3);

    void updateSelectedTrack(long j, long j2, long j3, List<? extends MediaChunk> list, MediaChunkIterator[] mediaChunkIteratorArr);
}
