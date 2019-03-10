package com.google.android.exoplayer2.audio;

import android.annotation.TargetApi;
import android.media.AudioTimestamp;
import android.media.AudioTrack;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.util.Util;

final class AudioTimestampPoller {
    private static final int ERROR_POLL_INTERVAL_US = 500000;
    private static final int FAST_POLL_INTERVAL_US = 5000;
    private static final int INITIALIZING_DURATION_US = 500000;
    private static final int SLOW_POLL_INTERVAL_US = 10000000;
    private static final int STATE_ERROR = 4;
    private static final int STATE_INITIALIZING = 0;
    private static final int STATE_NO_TIMESTAMP = 3;
    private static final int STATE_TIMESTAMP = 1;
    private static final int STATE_TIMESTAMP_ADVANCING = 2;
    @Nullable
    private final AudioTimestampV19 audioTimestamp;
    private long initialTimestampPositionFrames;
    private long initializeSystemTimeUs;
    private long lastTimestampSampleTimeUs;
    private long sampleIntervalUs;
    private int state;

    @TargetApi(19)
    private static final class AudioTimestampV19 {
        private final AudioTimestamp audioTimestamp = new AudioTimestamp();
        private final AudioTrack audioTrack;
        private long lastTimestampPositionFrames;
        private long lastTimestampRawPositionFrames;
        private long rawTimestampFramePositionWrapCount;

        public AudioTimestampV19(AudioTrack audioTrack) {
            this.audioTrack = audioTrack;
        }

        public boolean maybeUpdateTimestamp() {
            boolean updated = this.audioTrack.getTimestamp(this.audioTimestamp);
            if (updated) {
                long rawPositionFrames = this.audioTimestamp.framePosition;
                if (this.lastTimestampRawPositionFrames > rawPositionFrames) {
                    this.rawTimestampFramePositionWrapCount++;
                }
                this.lastTimestampRawPositionFrames = rawPositionFrames;
                this.lastTimestampPositionFrames = (this.rawTimestampFramePositionWrapCount << 32) + rawPositionFrames;
            }
            return updated;
        }

        public long getTimestampSystemTimeUs() {
            return this.audioTimestamp.nanoTime / 1000;
        }

        public long getTimestampPositionFrames() {
            return this.lastTimestampPositionFrames;
        }
    }

    public AudioTimestampPoller(AudioTrack audioTrack) {
        if (Util.SDK_INT >= 19) {
            this.audioTimestamp = new AudioTimestampV19(audioTrack);
            reset();
            return;
        }
        this.audioTimestamp = null;
        updateState(3);
    }

    public boolean maybePollTimestamp(long systemTimeUs) {
        boolean updatedTimestamp = this.audioTimestamp;
        if (updatedTimestamp) {
            if (systemTimeUs - this.lastTimestampSampleTimeUs >= this.sampleIntervalUs) {
                this.lastTimestampSampleTimeUs = systemTimeUs;
                updatedTimestamp = updatedTimestamp.maybeUpdateTimestamp();
                switch (this.state) {
                    case 0:
                        if (!updatedTimestamp) {
                            if (systemTimeUs - this.initializeSystemTimeUs <= 500000) {
                                break;
                            }
                            updateState(3);
                            break;
                        } else if (this.audioTimestamp.getTimestampSystemTimeUs() < this.initializeSystemTimeUs) {
                            updatedTimestamp = false;
                            break;
                        } else {
                            this.initialTimestampPositionFrames = this.audioTimestamp.getTimestampPositionFrames();
                            updateState(1);
                            break;
                        }
                    case 1:
                        if (!updatedTimestamp) {
                            reset();
                            break;
                        }
                        if (this.audioTimestamp.getTimestampPositionFrames() > this.initialTimestampPositionFrames) {
                            updateState(2);
                        }
                        break;
                    case 2:
                        if (!updatedTimestamp) {
                            reset();
                            break;
                        }
                        break;
                    case 3:
                        if (!updatedTimestamp) {
                            break;
                        }
                        reset();
                        break;
                    case 4:
                        break;
                    default:
                        throw new IllegalStateException();
                }
                return updatedTimestamp;
            }
        }
        return false;
    }

    public void rejectTimestamp() {
        updateState(4);
    }

    public void acceptTimestamp() {
        if (this.state == 4) {
            reset();
        }
    }

    public boolean hasTimestamp() {
        int i = this.state;
        if (i != 1) {
            return i == 2;
        } else {
            return true;
        }
    }

    public boolean isTimestampAdvancing() {
        return this.state == 2;
    }

    public void reset() {
        if (this.audioTimestamp != null) {
            updateState(0);
        }
    }

    public long getTimestampSystemTimeUs() {
        AudioTimestampV19 audioTimestampV19 = this.audioTimestamp;
        return audioTimestampV19 != null ? audioTimestampV19.getTimestampSystemTimeUs() : C0555C.TIME_UNSET;
    }

    public long getTimestampPositionFrames() {
        AudioTimestampV19 audioTimestampV19 = this.audioTimestamp;
        return audioTimestampV19 != null ? audioTimestampV19.getTimestampPositionFrames() : -1;
    }

    private void updateState(int state) {
        this.state = state;
        switch (state) {
            case 0:
                this.lastTimestampSampleTimeUs = 0;
                this.initialTimestampPositionFrames = -1;
                this.initializeSystemTimeUs = System.nanoTime() / 1000;
                this.sampleIntervalUs = DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS;
                return;
            case 1:
                this.sampleIntervalUs = DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS;
                return;
            case 2:
            case 3:
                this.sampleIntervalUs = 10000000;
                return;
            case 4:
                this.sampleIntervalUs = 500000;
                return;
            default:
                throw new IllegalStateException();
        }
    }
}
