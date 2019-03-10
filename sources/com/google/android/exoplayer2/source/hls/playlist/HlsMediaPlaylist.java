package com.google.android.exoplayer2.source.hls.playlist;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.offline.StreamKey;
import java.util.Collections;
import java.util.List;

public final class HlsMediaPlaylist extends HlsPlaylist {
    public static final int PLAYLIST_TYPE_EVENT = 2;
    public static final int PLAYLIST_TYPE_UNKNOWN = 0;
    public static final int PLAYLIST_TYPE_VOD = 1;
    public final int discontinuitySequence;
    public final long durationUs;
    public final boolean hasDiscontinuitySequence;
    public final boolean hasEndTag;
    public final boolean hasProgramDateTime;
    public final long mediaSequence;
    public final int playlistType;
    @Nullable
    public final DrmInitData protectionSchemes;
    public final List<Segment> segments;
    public final long startOffsetUs;
    public final long startTimeUs;
    public final long targetDurationUs;
    public final int version;

    public static final class Segment implements Comparable<Long> {
        public final long byterangeLength;
        public final long byterangeOffset;
        @Nullable
        public final DrmInitData drmInitData;
        public final long durationUs;
        @Nullable
        public final String encryptionIV;
        @Nullable
        public final String fullSegmentEncryptionKeyUri;
        public final boolean hasGapTag;
        @Nullable
        public final Segment initializationSegment;
        public final int relativeDiscontinuitySequence;
        public final long relativeStartTimeUs;
        public final String title;
        public final String url;

        public Segment(String uri, long byterangeOffset, long byterangeLength) {
            this(uri, null, "", 0, -1, C0555C.TIME_UNSET, null, null, null, byterangeOffset, byterangeLength, false);
        }

        public Segment(String url, @Nullable Segment initializationSegment, String title, long durationUs, int relativeDiscontinuitySequence, long relativeStartTimeUs, @Nullable DrmInitData drmInitData, @Nullable String fullSegmentEncryptionKeyUri, @Nullable String encryptionIV, long byterangeOffset, long byterangeLength, boolean hasGapTag) {
            this.url = url;
            this.initializationSegment = initializationSegment;
            this.title = title;
            this.durationUs = durationUs;
            this.relativeDiscontinuitySequence = relativeDiscontinuitySequence;
            this.relativeStartTimeUs = relativeStartTimeUs;
            this.drmInitData = drmInitData;
            this.fullSegmentEncryptionKeyUri = fullSegmentEncryptionKeyUri;
            this.encryptionIV = encryptionIV;
            this.byterangeOffset = byterangeOffset;
            this.byterangeLength = byterangeLength;
            this.hasGapTag = hasGapTag;
        }

        public int compareTo(@NonNull Long relativeStartTimeUs) {
            if (this.relativeStartTimeUs > relativeStartTimeUs.longValue()) {
                return 1;
            }
            return this.relativeStartTimeUs < relativeStartTimeUs.longValue() ? -1 : 0;
        }
    }

    public HlsMediaPlaylist(int playlistType, String baseUri, List<String> tags, long startOffsetUs, long startTimeUs, boolean hasDiscontinuitySequence, int discontinuitySequence, long mediaSequence, int version, long targetDurationUs, boolean hasIndependentSegments, boolean hasEndTag, boolean hasProgramDateTime, @Nullable DrmInitData protectionSchemes, List<Segment> segments) {
        long j;
        super(baseUri, tags, hasIndependentSegments);
        this.playlistType = playlistType;
        this.startTimeUs = startTimeUs;
        this.hasDiscontinuitySequence = hasDiscontinuitySequence;
        this.discontinuitySequence = discontinuitySequence;
        this.mediaSequence = mediaSequence;
        this.version = version;
        this.targetDurationUs = targetDurationUs;
        this.hasEndTag = hasEndTag;
        this.hasProgramDateTime = hasProgramDateTime;
        this.protectionSchemes = protectionSchemes;
        this.segments = Collections.unmodifiableList(segments);
        if (segments.isEmpty()) {
            j = 0;
            r0.durationUs = 0;
        } else {
            Segment last = (Segment) segments.get(segments.size() - 1);
            r0.durationUs = last.relativeStartTimeUs + last.durationUs;
            j = 0;
        }
        if (startOffsetUs == C0555C.TIME_UNSET) {
            j = C0555C.TIME_UNSET;
        } else {
            j = startOffsetUs >= j ? startOffsetUs : r0.durationUs + startOffsetUs;
        }
        r0.startOffsetUs = j;
    }

    public HlsMediaPlaylist copy(List<StreamKey> list) {
        return this;
    }

    public boolean isNewerThan(HlsMediaPlaylist other) {
        boolean z = true;
        if (other != null) {
            long j = this.mediaSequence;
            long j2 = other.mediaSequence;
            if (j <= j2) {
                if (j < j2) {
                    return false;
                }
                int segmentCount = this.segments.size();
                int otherSegmentCount = other.segments.size();
                if (segmentCount <= otherSegmentCount) {
                    if (segmentCount != otherSegmentCount || !this.hasEndTag || other.hasEndTag) {
                        z = false;
                    }
                }
                return z;
            }
        }
        return true;
    }

    public long getEndTimeUs() {
        return this.startTimeUs + this.durationUs;
    }

    public HlsMediaPlaylist copyWith(long startTimeUs, int discontinuitySequence) {
        return new HlsMediaPlaylist(this.playlistType, this.baseUri, this.tags, this.startOffsetUs, startTimeUs, true, discontinuitySequence, this.mediaSequence, this.version, this.targetDurationUs, this.hasIndependentSegments, this.hasEndTag, this.hasProgramDateTime, this.protectionSchemes, this.segments);
    }

    public HlsMediaPlaylist copyWithEndTag() {
        if (this.hasEndTag) {
            return r0;
        }
        HlsMediaPlaylist hlsMediaPlaylist = r2;
        HlsMediaPlaylist hlsMediaPlaylist2 = new HlsMediaPlaylist(r0.playlistType, r0.baseUri, r0.tags, r0.startOffsetUs, r0.startTimeUs, r0.hasDiscontinuitySequence, r0.discontinuitySequence, r0.mediaSequence, r0.version, r0.targetDurationUs, r0.hasIndependentSegments, true, r0.hasProgramDateTime, r0.protectionSchemes, r0.segments);
        return hlsMediaPlaylist;
    }
}
