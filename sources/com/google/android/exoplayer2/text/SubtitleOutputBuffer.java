package com.google.android.exoplayer2.text;

import com.google.android.exoplayer2.decoder.OutputBuffer;
import java.util.List;

public abstract class SubtitleOutputBuffer extends OutputBuffer implements Subtitle {
    private long subsampleOffsetUs;
    private Subtitle subtitle;

    public abstract void release();

    public void setContent(long timeUs, Subtitle subtitle, long subsampleOffsetUs) {
        this.timeUs = timeUs;
        this.subtitle = subtitle;
        this.subsampleOffsetUs = subsampleOffsetUs == Long.MAX_VALUE ? this.timeUs : subsampleOffsetUs;
    }

    public int getEventTimeCount() {
        return this.subtitle.getEventTimeCount();
    }

    public long getEventTime(int index) {
        return this.subtitle.getEventTime(index) + this.subsampleOffsetUs;
    }

    public int getNextEventTimeIndex(long timeUs) {
        return this.subtitle.getNextEventTimeIndex(timeUs - this.subsampleOffsetUs);
    }

    public List<Cue> getCues(long timeUs) {
        return this.subtitle.getCues(timeUs - this.subsampleOffsetUs);
    }

    public void clear() {
        super.clear();
        this.subtitle = null;
    }
}
