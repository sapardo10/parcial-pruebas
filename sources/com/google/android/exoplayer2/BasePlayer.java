package com.google.android.exoplayer2;

import android.support.annotation.Nullable;
import com.google.android.exoplayer2.Timeline.Window;
import com.google.android.exoplayer2.util.Util;

public abstract class BasePlayer implements Player {
    protected final Window window = new Window();

    public final void seekToDefaultPosition() {
        seekToDefaultPosition(getCurrentWindowIndex());
    }

    public final void seekToDefaultPosition(int windowIndex) {
        seekTo(windowIndex, C0555C.TIME_UNSET);
    }

    public final void seekTo(long positionMs) {
        seekTo(getCurrentWindowIndex(), positionMs);
    }

    public final boolean hasPrevious() {
        return getPreviousWindowIndex() != -1;
    }

    public final void previous() {
        int previousWindowIndex = getPreviousWindowIndex();
        if (previousWindowIndex != -1) {
            seekToDefaultPosition(previousWindowIndex);
        }
    }

    public final boolean hasNext() {
        return getNextWindowIndex() != -1;
    }

    public final void next() {
        int nextWindowIndex = getNextWindowIndex();
        if (nextWindowIndex != -1) {
            seekToDefaultPosition(nextWindowIndex);
        }
    }

    public final void stop() {
        stop(false);
    }

    public final int getNextWindowIndex() {
        Timeline timeline = getCurrentTimeline();
        if (timeline.isEmpty()) {
            return -1;
        }
        return timeline.getNextWindowIndex(getCurrentWindowIndex(), getRepeatModeForNavigation(), getShuffleModeEnabled());
    }

    public final int getPreviousWindowIndex() {
        Timeline timeline = getCurrentTimeline();
        if (timeline.isEmpty()) {
            return -1;
        }
        return timeline.getPreviousWindowIndex(getCurrentWindowIndex(), getRepeatModeForNavigation(), getShuffleModeEnabled());
    }

    @Nullable
    public final Object getCurrentTag() {
        int windowIndex = getCurrentWindowIndex();
        Timeline timeline = getCurrentTimeline();
        if (windowIndex >= timeline.getWindowCount()) {
            return null;
        }
        return timeline.getWindow(windowIndex, this.window, true).tag;
    }

    public final int getBufferedPercentage() {
        long position = getBufferedPosition();
        long duration = getDuration();
        int i = 100;
        if (position != C0555C.TIME_UNSET) {
            if (duration != C0555C.TIME_UNSET) {
                if (duration != 0) {
                    i = Util.constrainValue((int) ((100 * position) / duration), 0, 100);
                }
                return i;
            }
        }
        i = 0;
        return i;
    }

    public final boolean isCurrentWindowDynamic() {
        Timeline timeline = getCurrentTimeline();
        return !timeline.isEmpty() && timeline.getWindow(getCurrentWindowIndex(), this.window).isDynamic;
    }

    public final boolean isCurrentWindowSeekable() {
        Timeline timeline = getCurrentTimeline();
        return !timeline.isEmpty() && timeline.getWindow(getCurrentWindowIndex(), this.window).isSeekable;
    }

    public final long getContentDuration() {
        Timeline timeline = getCurrentTimeline();
        if (timeline.isEmpty()) {
            return C0555C.TIME_UNSET;
        }
        return timeline.getWindow(getCurrentWindowIndex(), this.window).getDurationMs();
    }

    private int getRepeatModeForNavigation() {
        int repeatMode = getRepeatMode();
        return repeatMode == 1 ? 0 : repeatMode;
    }
}
