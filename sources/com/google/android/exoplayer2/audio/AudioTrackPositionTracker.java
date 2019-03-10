package com.google.android.exoplayer2.audio;

import android.media.AudioTrack;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.lang.reflect.Method;

final class AudioTrackPositionTracker {
    private static final long FORCE_RESET_WORKAROUND_TIMEOUT_MS = 200;
    private static final long MAX_AUDIO_TIMESTAMP_OFFSET_US = 5000000;
    private static final long MAX_LATENCY_US = 5000000;
    private static final int MAX_PLAYHEAD_OFFSET_COUNT = 10;
    private static final int MIN_LATENCY_SAMPLE_INTERVAL_US = 500000;
    private static final int MIN_PLAYHEAD_OFFSET_SAMPLE_INTERVAL_US = 30000;
    private static final int PLAYSTATE_PAUSED = 2;
    private static final int PLAYSTATE_PLAYING = 3;
    private static final int PLAYSTATE_STOPPED = 1;
    @Nullable
    private AudioTimestampPoller audioTimestampPoller;
    @Nullable
    private AudioTrack audioTrack;
    private int bufferSize;
    private long bufferSizeUs;
    private long endPlaybackHeadPosition;
    private long forceResetWorkaroundTimeMs;
    @Nullable
    private Method getLatencyMethod;
    private boolean hasData;
    private boolean isOutputPcm;
    private long lastLatencySampleTimeUs;
    private long lastPlayheadSampleTimeUs;
    private long lastRawPlaybackHeadPosition;
    private long latencyUs;
    private final Listener listener;
    private boolean needsPassthroughWorkarounds;
    private int nextPlayheadOffsetIndex;
    private int outputPcmFrameSize;
    private int outputSampleRate;
    private long passthroughWorkaroundPauseOffset;
    private int playheadOffsetCount;
    private final long[] playheadOffsets;
    private long rawPlaybackHeadWrapCount;
    private long smoothedPlayheadOffsetUs;
    private long stopPlaybackHeadPosition;
    private long stopTimestampUs;

    public interface Listener {
        void onInvalidLatency(long j);

        void onPositionFramesMismatch(long j, long j2, long j3, long j4);

        void onSystemTimeUsMismatch(long j, long j2, long j3, long j4);

        void onUnderrun(int i, long j);
    }

    public AudioTrackPositionTracker(Listener listener) {
        this.listener = (Listener) Assertions.checkNotNull(listener);
        if (Util.SDK_INT >= 18) {
            try {
                this.getLatencyMethod = AudioTrack.class.getMethod("getLatency", (Class[]) null);
            } catch (NoSuchMethodException e) {
            }
        }
        this.playheadOffsets = new long[10];
    }

    public void setAudioTrack(AudioTrack audioTrack, int outputEncoding, int outputPcmFrameSize, int bufferSize) {
        this.audioTrack = audioTrack;
        this.outputPcmFrameSize = outputPcmFrameSize;
        this.bufferSize = bufferSize;
        this.audioTimestampPoller = new AudioTimestampPoller(audioTrack);
        this.outputSampleRate = audioTrack.getSampleRate();
        this.needsPassthroughWorkarounds = needsPassthroughWorkarounds(outputEncoding);
        this.isOutputPcm = Util.isEncodingLinearPcm(outputEncoding);
        this.bufferSizeUs = this.isOutputPcm ? framesToDurationUs((long) (bufferSize / outputPcmFrameSize)) : C0555C.TIME_UNSET;
        this.lastRawPlaybackHeadPosition = 0;
        this.rawPlaybackHeadWrapCount = 0;
        this.passthroughWorkaroundPauseOffset = 0;
        this.hasData = false;
        this.stopTimestampUs = C0555C.TIME_UNSET;
        this.forceResetWorkaroundTimeMs = C0555C.TIME_UNSET;
        this.latencyUs = 0;
    }

    public long getCurrentPositionUs(boolean sourceEnded) {
        if (((AudioTrack) Assertions.checkNotNull(this.audioTrack)).getPlayState() == 3) {
            maybeSampleSyncParams();
        }
        long systemTimeUs = System.nanoTime() / 1000;
        AudioTimestampPoller audioTimestampPoller = (AudioTimestampPoller) Assertions.checkNotNull(this.audioTimestampPoller);
        if (audioTimestampPoller.hasTimestamp()) {
            long timestampPositionUs = framesToDurationUs(audioTimestampPoller.getTimestampPositionFrames());
            if (audioTimestampPoller.isTimestampAdvancing()) {
                return timestampPositionUs + (systemTimeUs - audioTimestampPoller.getTimestampSystemTimeUs());
            }
            return timestampPositionUs;
        }
        long positionUs;
        if (this.playheadOffsetCount == 0) {
            positionUs = getPlaybackHeadPositionUs();
        } else {
            positionUs = this.smoothedPlayheadOffsetUs + systemTimeUs;
        }
        if (!sourceEnded) {
            positionUs -= this.latencyUs;
        }
        return positionUs;
    }

    public void start() {
        ((AudioTimestampPoller) Assertions.checkNotNull(this.audioTimestampPoller)).reset();
    }

    public boolean isPlaying() {
        return ((AudioTrack) Assertions.checkNotNull(this.audioTrack)).getPlayState() == 3;
    }

    public boolean mayHandleBuffer(long writtenFrames) {
        int playState = ((AudioTrack) Assertions.checkNotNull(this.audioTrack)).getPlayState();
        if (this.needsPassthroughWorkarounds) {
            if (playState == 2) {
                this.hasData = false;
                return false;
            } else if (playState == 1 && getPlaybackHeadPosition() == 0) {
                return false;
            }
        }
        boolean hadData = this.hasData;
        this.hasData = hasPendingData(writtenFrames);
        if (!(!hadData || this.hasData || playState == 1)) {
            Listener listener = this.listener;
            if (listener != null) {
                listener.onUnderrun(this.bufferSize, C0555C.usToMs(this.bufferSizeUs));
                return true;
            }
        }
        return true;
    }

    public int getAvailableBufferSize(long writtenBytes) {
        return this.bufferSize - ((int) (writtenBytes - (getPlaybackHeadPosition() * ((long) this.outputPcmFrameSize))));
    }

    public boolean isStalled(long writtenFrames) {
        if (this.forceResetWorkaroundTimeMs != C0555C.TIME_UNSET && writtenFrames > 0) {
            if (SystemClock.elapsedRealtime() - this.forceResetWorkaroundTimeMs >= FORCE_RESET_WORKAROUND_TIMEOUT_MS) {
                return true;
            }
        }
        return false;
    }

    public void handleEndOfStream(long writtenFrames) {
        this.stopPlaybackHeadPosition = getPlaybackHeadPosition();
        this.stopTimestampUs = SystemClock.elapsedRealtime() * 1000;
        this.endPlaybackHeadPosition = writtenFrames;
    }

    public boolean hasPendingData(long writtenFrames) {
        if (writtenFrames <= getPlaybackHeadPosition()) {
            if (!forceHasPendingData()) {
                return false;
            }
        }
        return true;
    }

    public boolean pause() {
        resetSyncParams();
        if (this.stopTimestampUs != C0555C.TIME_UNSET) {
            return false;
        }
        ((AudioTimestampPoller) Assertions.checkNotNull(this.audioTimestampPoller)).reset();
        return true;
    }

    public void reset() {
        resetSyncParams();
        this.audioTrack = null;
        this.audioTimestampPoller = null;
    }

    private void maybeSampleSyncParams() {
        long playbackPositionUs = getPlaybackHeadPositionUs();
        if (playbackPositionUs != 0) {
            long systemTimeUs = System.nanoTime() / 1000;
            if (systemTimeUs - this.lastPlayheadSampleTimeUs >= 30000) {
                long[] jArr = this.playheadOffsets;
                int i = this.nextPlayheadOffsetIndex;
                jArr[i] = playbackPositionUs - systemTimeUs;
                this.nextPlayheadOffsetIndex = (i + 1) % 10;
                i = this.playheadOffsetCount;
                if (i < 10) {
                    this.playheadOffsetCount = i + 1;
                }
                this.lastPlayheadSampleTimeUs = systemTimeUs;
                this.smoothedPlayheadOffsetUs = 0;
                int i2 = 0;
                while (true) {
                    int i3 = this.playheadOffsetCount;
                    if (i2 >= i3) {
                        break;
                    }
                    this.smoothedPlayheadOffsetUs += this.playheadOffsets[i2] / ((long) i3);
                    i2++;
                }
            }
            if (!this.needsPassthroughWorkarounds) {
                maybePollAndCheckTimestamp(systemTimeUs, playbackPositionUs);
                maybeUpdateLatency(systemTimeUs);
            }
        }
    }

    private void maybePollAndCheckTimestamp(long systemTimeUs, long playbackPositionUs) {
        long j = systemTimeUs;
        AudioTimestampPoller audioTimestampPoller = (AudioTimestampPoller) Assertions.checkNotNull(this.audioTimestampPoller);
        if (audioTimestampPoller.maybePollTimestamp(j)) {
            long audioTimestampSystemTimeUs = audioTimestampPoller.getTimestampSystemTimeUs();
            long audioTimestampPositionFrames = audioTimestampPoller.getTimestampPositionFrames();
            if (Math.abs(audioTimestampSystemTimeUs - j) > 5000000) {
                r0.listener.onSystemTimeUsMismatch(audioTimestampPositionFrames, audioTimestampSystemTimeUs, systemTimeUs, playbackPositionUs);
                audioTimestampPoller.rejectTimestamp();
            } else {
                j = audioTimestampPositionFrames;
                if (Math.abs(framesToDurationUs(j) - playbackPositionUs) > 5000000) {
                    r0.listener.onPositionFramesMismatch(j, audioTimestampSystemTimeUs, systemTimeUs, playbackPositionUs);
                    audioTimestampPoller.rejectTimestamp();
                } else {
                    audioTimestampPoller.acceptTimestamp();
                }
            }
        }
    }

    private void maybeUpdateLatency(long systemTimeUs) {
        if (this.isOutputPcm) {
            Method method = this.getLatencyMethod;
            if (method != null && systemTimeUs - this.lastLatencySampleTimeUs >= 500000) {
                try {
                    this.latencyUs = (((long) ((Integer) Util.castNonNull((Integer) method.invoke(Assertions.checkNotNull(this.audioTrack), new Object[0]))).intValue()) * 1000) - this.bufferSizeUs;
                    this.latencyUs = Math.max(this.latencyUs, 0);
                    if (this.latencyUs > 5000000) {
                        this.listener.onInvalidLatency(this.latencyUs);
                        this.latencyUs = 0;
                    }
                } catch (Exception e) {
                    this.getLatencyMethod = null;
                }
                this.lastLatencySampleTimeUs = systemTimeUs;
            }
        }
    }

    private long framesToDurationUs(long frameCount) {
        return (1000000 * frameCount) / ((long) this.outputSampleRate);
    }

    private void resetSyncParams() {
        this.smoothedPlayheadOffsetUs = 0;
        this.playheadOffsetCount = 0;
        this.nextPlayheadOffsetIndex = 0;
        this.lastPlayheadSampleTimeUs = 0;
    }

    private boolean forceHasPendingData() {
        if (this.needsPassthroughWorkarounds) {
            if (((AudioTrack) Assertions.checkNotNull(this.audioTrack)).getPlayState() == 2) {
                if (getPlaybackHeadPosition() == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean needsPassthroughWorkarounds(int outputEncoding) {
        return Util.SDK_INT < 23 && (outputEncoding == 5 || outputEncoding == 6);
    }

    private long getPlaybackHeadPositionUs() {
        return framesToDurationUs(getPlaybackHeadPosition());
    }

    private long getPlaybackHeadPosition() {
        AudioTrack audioTrack = (AudioTrack) Assertions.checkNotNull(this.audioTrack);
        if (this.stopTimestampUs != C0555C.TIME_UNSET) {
            return Math.min(this.endPlaybackHeadPosition, this.stopPlaybackHeadPosition + ((((long) this.outputSampleRate) * ((SystemClock.elapsedRealtime() * 1000) - this.stopTimestampUs)) / 1000000));
        }
        int state = audioTrack.getPlayState();
        if (state == 1) {
            return 0;
        }
        long rawPlaybackHeadPosition = 4294967295L & ((long) audioTrack.getPlaybackHeadPosition());
        if (this.needsPassthroughWorkarounds) {
            if (state == 2 && rawPlaybackHeadPosition == 0) {
                this.passthroughWorkaroundPauseOffset = this.lastRawPlaybackHeadPosition;
            }
            rawPlaybackHeadPosition += this.passthroughWorkaroundPauseOffset;
        }
        if (Util.SDK_INT <= 28) {
            if (rawPlaybackHeadPosition == 0 && this.lastRawPlaybackHeadPosition > 0 && state == 3) {
                if (this.forceResetWorkaroundTimeMs == C0555C.TIME_UNSET) {
                    this.forceResetWorkaroundTimeMs = SystemClock.elapsedRealtime();
                }
                return this.lastRawPlaybackHeadPosition;
            }
            this.forceResetWorkaroundTimeMs = C0555C.TIME_UNSET;
        }
        if (this.lastRawPlaybackHeadPosition > rawPlaybackHeadPosition) {
            this.rawPlaybackHeadWrapCount++;
        }
        this.lastRawPlaybackHeadPosition = rawPlaybackHeadPosition;
        return (this.rawPlaybackHeadWrapCount << 32) + rawPlaybackHeadPosition;
    }
}
