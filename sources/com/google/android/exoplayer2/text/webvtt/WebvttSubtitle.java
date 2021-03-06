package com.google.android.exoplayer2.text.webvtt;

import android.text.SpannableStringBuilder;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.Subtitle;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

final class WebvttSubtitle implements Subtitle {
    private final long[] cueTimesUs = new long[(this.numCues * 2)];
    private final List<WebvttCue> cues;
    private final int numCues;
    private final long[] sortedCueTimesUs;

    public WebvttSubtitle(List<WebvttCue> cues) {
        this.cues = cues;
        this.numCues = cues.size();
        for (int cueIndex = 0; cueIndex < this.numCues; cueIndex++) {
            WebvttCue cue = (WebvttCue) cues.get(cueIndex);
            int arrayIndex = cueIndex * 2;
            this.cueTimesUs[arrayIndex] = cue.startTime;
            this.cueTimesUs[arrayIndex + 1] = cue.endTime;
        }
        long[] jArr = this.cueTimesUs;
        this.sortedCueTimesUs = Arrays.copyOf(jArr, jArr.length);
        Arrays.sort(this.sortedCueTimesUs);
    }

    public int getNextEventTimeIndex(long timeUs) {
        int index = Util.binarySearchCeil(this.sortedCueTimesUs, timeUs, false, false);
        return index < this.sortedCueTimesUs.length ? index : -1;
    }

    public int getEventTimeCount() {
        return this.sortedCueTimesUs.length;
    }

    public long getEventTime(int index) {
        boolean z = true;
        Assertions.checkArgument(index >= 0);
        if (index >= this.sortedCueTimesUs.length) {
            z = false;
        }
        Assertions.checkArgument(z);
        return this.sortedCueTimesUs[index];
    }

    public List<Cue> getCues(long timeUs) {
        ArrayList<Cue> list = null;
        WebvttCue firstNormalCue = null;
        SpannableStringBuilder normalCueTextBuilder = null;
        int i = 0;
        while (i < this.numCues) {
            long[] jArr = this.cueTimesUs;
            if (jArr[i * 2] <= timeUs && timeUs < jArr[(i * 2) + 1]) {
                if (list == null) {
                    list = new ArrayList();
                }
                WebvttCue cue = (WebvttCue) this.cues.get(i);
                if (!cue.isNormalCue()) {
                    list.add(cue);
                } else if (firstNormalCue == null) {
                    firstNormalCue = cue;
                } else if (normalCueTextBuilder == null) {
                    normalCueTextBuilder = new SpannableStringBuilder();
                    normalCueTextBuilder.append(firstNormalCue.text).append("\n").append(cue.text);
                } else {
                    normalCueTextBuilder.append("\n").append(cue.text);
                }
            }
            i++;
        }
        if (normalCueTextBuilder != null) {
            list.add(new WebvttCue(normalCueTextBuilder));
        } else if (firstNormalCue != null) {
            list.add(firstNormalCue);
        }
        if (list != null) {
            return list;
        }
        return Collections.emptyList();
    }
}
