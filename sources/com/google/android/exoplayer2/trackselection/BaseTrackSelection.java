package com.google.android.exoplayer2.trackselection;

import android.os.SystemClock;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.chunk.MediaChunk;
import com.google.android.exoplayer2.source.chunk.MediaChunkIterator;
import com.google.android.exoplayer2.trackselection.TrackSelection.-CC;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public abstract class BaseTrackSelection implements TrackSelection {
    private final long[] blacklistUntilTimes;
    private final Format[] formats;
    protected final TrackGroup group;
    private int hashCode;
    protected final int length;
    protected final int[] tracks;

    private static final class DecreasingBandwidthComparator implements Comparator<Format> {
        private DecreasingBandwidthComparator() {
        }

        public int compare(Format a, Format b) {
            return b.bitrate - a.bitrate;
        }
    }

    @Deprecated
    public /* synthetic */ void updateSelectedTrack(long j, long j2, long j3) {
        -CC.$default$updateSelectedTrack(this, j, j2, j3);
    }

    public /* synthetic */ void updateSelectedTrack(long j, long j2, long j3, List<? extends MediaChunk> list, MediaChunkIterator[] mediaChunkIteratorArr) {
        -CC.$default$updateSelectedTrack(this, j, j2, j3, list, mediaChunkIteratorArr);
    }

    public BaseTrackSelection(TrackGroup group, int... tracks) {
        int i;
        Assertions.checkState(tracks.length > 0);
        this.group = (TrackGroup) Assertions.checkNotNull(group);
        this.length = tracks.length;
        this.formats = new Format[this.length];
        for (i = 0; i < tracks.length; i++) {
            this.formats[i] = group.getFormat(tracks[i]);
        }
        Arrays.sort(this.formats, new DecreasingBandwidthComparator());
        this.tracks = new int[this.length];
        i = 0;
        while (true) {
            int i2 = this.length;
            if (i < i2) {
                this.tracks[i] = group.indexOf(this.formats[i]);
                i++;
            } else {
                this.blacklistUntilTimes = new long[i2];
                return;
            }
        }
    }

    public void enable() {
    }

    public void disable() {
    }

    public final TrackGroup getTrackGroup() {
        return this.group;
    }

    public final int length() {
        return this.tracks.length;
    }

    public final Format getFormat(int index) {
        return this.formats[index];
    }

    public final int getIndexInTrackGroup(int index) {
        return this.tracks[index];
    }

    public final int indexOf(Format format) {
        for (int i = 0; i < this.length; i++) {
            if (this.formats[i] == format) {
                return i;
            }
        }
        return -1;
    }

    public final int indexOf(int indexInTrackGroup) {
        for (int i = 0; i < this.length; i++) {
            if (this.tracks[i] == indexInTrackGroup) {
                return i;
            }
        }
        return -1;
    }

    public final Format getSelectedFormat() {
        return this.formats[getSelectedIndex()];
    }

    public final int getSelectedIndexInTrackGroup() {
        return this.tracks[getSelectedIndex()];
    }

    public void onPlaybackSpeed(float playbackSpeed) {
    }

    public int evaluateQueueSize(long playbackPositionUs, List<? extends MediaChunk> queue) {
        return queue.size();
    }

    public final boolean blacklist(int index, long blacklistDurationMs) {
        BaseTrackSelection baseTrackSelection = this;
        int i = index;
        long nowMs = SystemClock.elapsedRealtime();
        int i2 = 0;
        boolean canBlacklist = isBlacklisted(i, nowMs);
        while (true) {
            boolean z = false;
            if (i2 < baseTrackSelection.length && !canBlacklist) {
                if (!(i2 == i || isBlacklisted(i2, nowMs))) {
                    z = true;
                }
                canBlacklist = z;
                i2++;
            } else if (!canBlacklist) {
                return false;
            } else {
                long[] jArr = baseTrackSelection.blacklistUntilTimes;
                jArr[i] = Math.max(jArr[i], Util.addWithOverflowDefault(nowMs, blacklistDurationMs, Long.MAX_VALUE));
                return true;
            }
        }
        if (!canBlacklist) {
            return false;
        }
        long[] jArr2 = baseTrackSelection.blacklistUntilTimes;
        jArr2[i] = Math.max(jArr2[i], Util.addWithOverflowDefault(nowMs, blacklistDurationMs, Long.MAX_VALUE));
        return true;
    }

    protected final boolean isBlacklisted(int index, long nowMs) {
        return this.blacklistUntilTimes[index] > nowMs;
    }

    public int hashCode() {
        if (this.hashCode == 0) {
            this.hashCode = (System.identityHashCode(this.group) * 31) + Arrays.hashCode(this.tracks);
        }
        return this.hashCode;
    }

    public boolean equals(@Nullable Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj != null) {
            if (getClass() == obj.getClass()) {
                BaseTrackSelection other = (BaseTrackSelection) obj;
                if (this.group != other.group || !Arrays.equals(this.tracks, other.tracks)) {
                    z = false;
                }
                return z;
            }
        }
        return false;
    }
}
