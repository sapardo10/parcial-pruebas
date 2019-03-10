package com.google.android.exoplayer2;

import android.support.annotation.Nullable;
import android.util.Pair;
import com.google.android.exoplayer2.Timeline.Period;
import com.google.android.exoplayer2.Timeline.Window;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSource.MediaPeriodId;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.util.Assertions;

final class MediaPeriodQueue {
    private static final int MAXIMUM_BUFFER_AHEAD_PERIODS = 100;
    private int length;
    @Nullable
    private MediaPeriodHolder loading;
    private long nextWindowSequenceNumber;
    @Nullable
    private Object oldFrontPeriodUid;
    private long oldFrontPeriodWindowSequenceNumber;
    private final Period period = new Period();
    @Nullable
    private MediaPeriodHolder playing;
    @Nullable
    private MediaPeriodHolder reading;
    private int repeatMode;
    private boolean shuffleModeEnabled;
    private Timeline timeline = Timeline.EMPTY;
    private final Window window = new Window();

    public void setTimeline(Timeline timeline) {
        this.timeline = timeline;
    }

    public boolean updateRepeatMode(int repeatMode) {
        this.repeatMode = repeatMode;
        return updateForPlaybackModeChange();
    }

    public boolean updateShuffleModeEnabled(boolean shuffleModeEnabled) {
        this.shuffleModeEnabled = shuffleModeEnabled;
        return updateForPlaybackModeChange();
    }

    public boolean isLoading(MediaPeriod mediaPeriod) {
        MediaPeriodHolder mediaPeriodHolder = this.loading;
        return mediaPeriodHolder != null && mediaPeriodHolder.mediaPeriod == mediaPeriod;
    }

    public void reevaluateBuffer(long rendererPositionUs) {
        MediaPeriodHolder mediaPeriodHolder = this.loading;
        if (mediaPeriodHolder != null) {
            mediaPeriodHolder.reevaluateBuffer(rendererPositionUs);
        }
    }

    public boolean shouldLoadNextMediaPeriod() {
        MediaPeriodHolder mediaPeriodHolder = this.loading;
        if (mediaPeriodHolder != null) {
            if (!mediaPeriodHolder.info.isFinal) {
                if (!this.loading.isFullyBuffered() || this.loading.info.durationUs == C0555C.TIME_UNSET || this.length >= 100) {
                }
            }
            return false;
        }
        return true;
    }

    @Nullable
    public MediaPeriodInfo getNextMediaPeriodInfo(long rendererPositionUs, PlaybackInfo playbackInfo) {
        MediaPeriodHolder mediaPeriodHolder = this.loading;
        if (mediaPeriodHolder == null) {
            return getFirstMediaPeriodInfo(playbackInfo);
        }
        return getFollowingMediaPeriodInfo(mediaPeriodHolder, rendererPositionUs);
    }

    public MediaPeriod enqueueNextMediaPeriod(RendererCapabilities[] rendererCapabilities, TrackSelector trackSelector, Allocator allocator, MediaSource mediaSource, MediaPeriodInfo info) {
        long rendererPositionOffsetUs;
        MediaPeriodHolder mediaPeriodHolder = this.loading;
        if (mediaPeriodHolder == null) {
            rendererPositionOffsetUs = info.startPositionUs;
        } else {
            rendererPositionOffsetUs = mediaPeriodHolder.getRendererOffset() + this.loading.info.durationUs;
        }
        MediaPeriodHolder mediaPeriodHolder2 = new MediaPeriodHolder(rendererCapabilities, rendererPositionOffsetUs, trackSelector, allocator, mediaSource, info);
        if (this.loading != null) {
            Assertions.checkState(hasPlayingPeriod());
            this.loading.next = mediaPeriodHolder2;
        }
        this.oldFrontPeriodUid = null;
        this.loading = mediaPeriodHolder2;
        this.length++;
        return mediaPeriodHolder2.mediaPeriod;
    }

    public MediaPeriodHolder getLoadingPeriod() {
        return this.loading;
    }

    public MediaPeriodHolder getPlayingPeriod() {
        return this.playing;
    }

    public MediaPeriodHolder getReadingPeriod() {
        return this.reading;
    }

    public MediaPeriodHolder getFrontPeriod() {
        return hasPlayingPeriod() ? this.playing : this.loading;
    }

    public boolean hasPlayingPeriod() {
        return this.playing != null;
    }

    public MediaPeriodHolder advanceReadingPeriod() {
        MediaPeriodHolder mediaPeriodHolder = this.reading;
        boolean z = (mediaPeriodHolder == null || mediaPeriodHolder.next == null) ? false : true;
        Assertions.checkState(z);
        this.reading = this.reading.next;
        return this.reading;
    }

    public MediaPeriodHolder advancePlayingPeriod() {
        MediaPeriodHolder mediaPeriodHolder = this.playing;
        if (mediaPeriodHolder != null) {
            if (mediaPeriodHolder == this.reading) {
                this.reading = mediaPeriodHolder.next;
            }
            this.playing.release();
            this.length--;
            if (this.length == 0) {
                this.loading = null;
                this.oldFrontPeriodUid = this.playing.uid;
                this.oldFrontPeriodWindowSequenceNumber = this.playing.info.id.windowSequenceNumber;
            }
            this.playing = this.playing.next;
        } else {
            mediaPeriodHolder = this.loading;
            this.playing = mediaPeriodHolder;
            this.reading = mediaPeriodHolder;
        }
        return this.playing;
    }

    public boolean removeAfter(MediaPeriodHolder mediaPeriodHolder) {
        Assertions.checkState(mediaPeriodHolder != null);
        boolean removedReading = false;
        this.loading = mediaPeriodHolder;
        while (mediaPeriodHolder.next != null) {
            mediaPeriodHolder = mediaPeriodHolder.next;
            if (mediaPeriodHolder == this.reading) {
                this.reading = this.playing;
                removedReading = true;
            }
            mediaPeriodHolder.release();
            this.length--;
        }
        this.loading.next = null;
        return removedReading;
    }

    public void clear(boolean keepFrontPeriodUid) {
        MediaPeriodHolder front = getFrontPeriod();
        if (front != null) {
            this.oldFrontPeriodUid = keepFrontPeriodUid ? front.uid : null;
            this.oldFrontPeriodWindowSequenceNumber = front.info.id.windowSequenceNumber;
            front.release();
            removeAfter(front);
        } else if (!keepFrontPeriodUid) {
            this.oldFrontPeriodUid = null;
        }
        this.playing = null;
        this.loading = null;
        this.reading = null;
        this.length = 0;
    }

    public boolean updateQueuedPeriods(MediaPeriodId playingPeriodId, long rendererPositionUs) {
        int periodIndex = this.timeline.getIndexOfPeriod(playingPeriodId.periodUid);
        MediaPeriodHolder previousPeriodHolder = null;
        for (MediaPeriodHolder periodHolder = getFrontPeriod(); periodHolder != null; periodHolder = periodHolder.next) {
            if (previousPeriodHolder == null) {
                periodHolder.info = getUpdatedMediaPeriodInfo(periodHolder.info);
            } else {
                if (periodIndex != -1) {
                    if (periodHolder.uid.equals(this.timeline.getUidOfPeriod(periodIndex))) {
                        MediaPeriodInfo periodInfo = getFollowingMediaPeriodInfo(previousPeriodHolder, rendererPositionUs);
                        if (periodInfo == null) {
                            return true ^ removeAfter(previousPeriodHolder);
                        }
                        periodHolder.info = getUpdatedMediaPeriodInfo(periodHolder.info);
                        if (!canKeepMediaPeriodHolder(periodHolder, periodInfo)) {
                            return true ^ removeAfter(previousPeriodHolder);
                        }
                    }
                }
                return true ^ removeAfter(previousPeriodHolder);
            }
            if (periodHolder.info.isLastInTimelinePeriod) {
                periodIndex = this.timeline.getNextPeriodIndex(periodIndex, this.period, this.window, this.repeatMode, this.shuffleModeEnabled);
            }
            previousPeriodHolder = periodHolder;
        }
        return true;
    }

    public MediaPeriodInfo getUpdatedMediaPeriodInfo(MediaPeriodInfo info) {
        long durationUs;
        boolean isLastInPeriod = isLastInPeriod(info.id);
        boolean isLastInTimeline = isLastInTimeline(info.id, isLastInPeriod);
        this.timeline.getPeriodByUid(info.id.periodUid, this.period);
        if (info.id.isAd()) {
            durationUs = this.period.getAdDurationUs(info.id.adGroupIndex, info.id.adIndexInAdGroup);
        } else {
            durationUs = info.id.endPositionUs == Long.MIN_VALUE ? this.period.getDurationUs() : info.id.endPositionUs;
        }
        return new MediaPeriodInfo(info.id, info.startPositionUs, info.contentPositionUs, durationUs, isLastInPeriod, isLastInTimeline);
    }

    public MediaPeriodId resolveMediaPeriodIdForAds(Object periodUid, long positionUs) {
        return resolveMediaPeriodIdForAds(periodUid, positionUs, resolvePeriodIndexToWindowSequenceNumber(periodUid));
    }

    private MediaPeriodId resolveMediaPeriodIdForAds(Object periodUid, long positionUs, long windowSequenceNumber) {
        long j = positionUs;
        Object obj = periodUid;
        this.timeline.getPeriodByUid(periodUid, this.period);
        int adGroupIndex = this.period.getAdGroupIndexForPositionUs(j);
        if (adGroupIndex != -1) {
            return new MediaPeriodId(periodUid, adGroupIndex, r0.period.getFirstAdIndexToPlay(adGroupIndex), windowSequenceNumber);
        }
        long endPositionUs;
        int nextAdGroupIndex = r0.period.getAdGroupIndexAfterPositionUs(j);
        if (nextAdGroupIndex == -1) {
            endPositionUs = Long.MIN_VALUE;
        } else {
            endPositionUs = r0.period.getAdGroupTimeUs(nextAdGroupIndex);
        }
        return new MediaPeriodId(periodUid, windowSequenceNumber, endPositionUs);
    }

    private long resolvePeriodIndexToWindowSequenceNumber(Object periodUid) {
        MediaPeriodHolder mediaPeriodHolder;
        int windowIndex = this.timeline.getPeriodByUid(periodUid, this.period).windowIndex;
        int oldFrontPeriodIndex = this.oldFrontPeriodUid;
        if (oldFrontPeriodIndex != 0) {
            oldFrontPeriodIndex = this.timeline.getIndexOfPeriod(oldFrontPeriodIndex);
            if (oldFrontPeriodIndex != -1) {
                if (this.timeline.getPeriod(oldFrontPeriodIndex, this.period).windowIndex == windowIndex) {
                    return this.oldFrontPeriodWindowSequenceNumber;
                }
            }
        }
        for (mediaPeriodHolder = getFrontPeriod(); mediaPeriodHolder != null; mediaPeriodHolder = mediaPeriodHolder.next) {
            if (mediaPeriodHolder.uid.equals(periodUid)) {
                return mediaPeriodHolder.info.id.windowSequenceNumber;
            }
        }
        for (mediaPeriodHolder = getFrontPeriod(); mediaPeriodHolder != null; mediaPeriodHolder = mediaPeriodHolder.next) {
            int indexOfHolderInTimeline = this.timeline.getIndexOfPeriod(mediaPeriodHolder.uid);
            if (indexOfHolderInTimeline != -1) {
                if (this.timeline.getPeriod(indexOfHolderInTimeline, this.period).windowIndex == windowIndex) {
                    return mediaPeriodHolder.info.id.windowSequenceNumber;
                }
            }
        }
        long j = this.nextWindowSequenceNumber;
        this.nextWindowSequenceNumber = 1 + j;
        return j;
    }

    private boolean canKeepMediaPeriodHolder(MediaPeriodHolder periodHolder, MediaPeriodInfo info) {
        MediaPeriodInfo periodHolderInfo = periodHolder.info;
        if (periodHolderInfo.startPositionUs == info.startPositionUs) {
            if (periodHolderInfo.id.equals(info.id)) {
                return true;
            }
        }
        return false;
    }

    private boolean updateForPlaybackModeChange() {
        MediaPeriodHolder lastValidPeriodHolder = getFrontPeriod();
        boolean z = true;
        if (lastValidPeriodHolder == null) {
            return true;
        }
        boolean readingPeriodRemoved;
        int currentPeriodIndex = this.timeline.getIndexOfPeriod(lastValidPeriodHolder.uid);
        while (true) {
            int nextPeriodIndex = this.timeline.getNextPeriodIndex(currentPeriodIndex, this.period, this.window, this.repeatMode, this.shuffleModeEnabled);
            while (lastValidPeriodHolder.next != null && !lastValidPeriodHolder.info.isLastInTimelinePeriod) {
                lastValidPeriodHolder = lastValidPeriodHolder.next;
            }
            if (nextPeriodIndex == -1) {
                break;
            } else if (lastValidPeriodHolder.next == null) {
                break;
            } else if (this.timeline.getIndexOfPeriod(lastValidPeriodHolder.next.uid) != nextPeriodIndex) {
                break;
            } else {
                lastValidPeriodHolder = lastValidPeriodHolder.next;
                currentPeriodIndex = nextPeriodIndex;
            }
            readingPeriodRemoved = removeAfter(lastValidPeriodHolder);
            lastValidPeriodHolder.info = getUpdatedMediaPeriodInfo(lastValidPeriodHolder.info);
            if (readingPeriodRemoved) {
                if (!hasPlayingPeriod()) {
                    z = false;
                }
            }
            return z;
        }
        readingPeriodRemoved = removeAfter(lastValidPeriodHolder);
        lastValidPeriodHolder.info = getUpdatedMediaPeriodInfo(lastValidPeriodHolder.info);
        if (readingPeriodRemoved) {
            if (!hasPlayingPeriod()) {
                z = false;
            }
        }
        return z;
    }

    private MediaPeriodInfo getFirstMediaPeriodInfo(PlaybackInfo playbackInfo) {
        return getMediaPeriodInfo(playbackInfo.periodId, playbackInfo.contentPositionUs, playbackInfo.startPositionUs);
    }

    @Nullable
    private MediaPeriodInfo getFollowingMediaPeriodInfo(MediaPeriodHolder mediaPeriodHolder, long rendererPositionUs) {
        MediaPeriodQueue mediaPeriodQueue = this;
        MediaPeriodHolder mediaPeriodHolder2 = mediaPeriodHolder;
        MediaPeriodInfo mediaPeriodInfo = mediaPeriodHolder2.info;
        long bufferedDurationUs = (mediaPeriodHolder.getRendererOffset() + mediaPeriodInfo.durationUs) - rendererPositionUs;
        long windowSequenceNumber;
        if (mediaPeriodInfo.isLastInTimelinePeriod) {
            int currentPeriodIndex = mediaPeriodQueue.timeline.getIndexOfPeriod(mediaPeriodInfo.id.periodUid);
            int nextPeriodIndex = mediaPeriodQueue.timeline.getNextPeriodIndex(currentPeriodIndex, mediaPeriodQueue.period, mediaPeriodQueue.window, mediaPeriodQueue.repeatMode, mediaPeriodQueue.shuffleModeEnabled);
            if (nextPeriodIndex == -1) {
                return null;
            }
            Object nextPeriodUid;
            int nextWindowIndex = mediaPeriodQueue.timeline.getPeriod(nextPeriodIndex, mediaPeriodQueue.period, true).windowIndex;
            Object nextPeriodUid2 = mediaPeriodQueue.period.uid;
            long windowSequenceNumber2 = mediaPeriodInfo.id.windowSequenceNumber;
            if (mediaPeriodQueue.timeline.getWindow(nextWindowIndex, mediaPeriodQueue.window).firstPeriodIndex == nextPeriodIndex) {
                Timeline timeline = mediaPeriodQueue.timeline;
                Window window = mediaPeriodQueue.window;
                long windowSequenceNumber3 = windowSequenceNumber2;
                Pair<Object, Long> defaultPosition = timeline.getPeriodPosition(window, mediaPeriodQueue.period, nextWindowIndex, C0555C.TIME_UNSET, Math.max(0, bufferedDurationUs));
                if (defaultPosition == null) {
                    return null;
                }
                Object nextPeriodUid3;
                nextPeriodUid2 = defaultPosition.first;
                long startPositionUs = ((Long) defaultPosition.second).longValue();
                if (mediaPeriodHolder2.next == null || !mediaPeriodHolder2.next.uid.equals(nextPeriodUid2)) {
                    nextPeriodUid3 = nextPeriodUid2;
                    long j = mediaPeriodQueue.nextWindowSequenceNumber;
                    mediaPeriodQueue.nextWindowSequenceNumber = j + 1;
                    windowSequenceNumber3 = j;
                } else {
                    windowSequenceNumber3 = mediaPeriodHolder2.next.info.id.windowSequenceNumber;
                    nextPeriodUid3 = nextPeriodUid2;
                }
                windowSequenceNumber = windowSequenceNumber3;
                windowSequenceNumber2 = startPositionUs;
                nextPeriodUid = nextPeriodUid3;
            } else {
                nextPeriodUid = nextPeriodUid2;
                windowSequenceNumber = windowSequenceNumber2;
                windowSequenceNumber2 = 0;
            }
            long j2 = windowSequenceNumber2;
            return getMediaPeriodInfo(resolveMediaPeriodIdForAds(nextPeriodUid, j2, windowSequenceNumber), j2, windowSequenceNumber2);
        }
        MediaPeriodId currentPeriodId = mediaPeriodInfo.id;
        mediaPeriodQueue.timeline.getPeriodByUid(currentPeriodId.periodUid, mediaPeriodQueue.period);
        int adGroupIndex;
        int adCountInCurrentAdGroup;
        MediaPeriodInfo mediaPeriodInfoForAd;
        if (currentPeriodId.isAd()) {
            adGroupIndex = currentPeriodId.adGroupIndex;
            adCountInCurrentAdGroup = mediaPeriodQueue.period.getAdCountInAdGroup(adGroupIndex);
            if (adCountInCurrentAdGroup == -1) {
                return null;
            }
            currentPeriodIndex = mediaPeriodQueue.period.getNextAdIndexToPlay(adGroupIndex, currentPeriodId.adIndexInAdGroup);
            if (currentPeriodIndex < adCountInCurrentAdGroup) {
                if (mediaPeriodQueue.period.isAdAvailable(adGroupIndex, currentPeriodIndex)) {
                    mediaPeriodInfoForAd = getMediaPeriodInfoForAd(currentPeriodId.periodUid, adGroupIndex, currentPeriodIndex, mediaPeriodInfo.contentPositionUs, currentPeriodId.windowSequenceNumber);
                } else {
                    int i = currentPeriodIndex;
                    mediaPeriodInfoForAd = null;
                }
                return mediaPeriodInfoForAd;
            }
            long startPositionUs2;
            long startPositionUs3 = mediaPeriodInfo.contentPositionUs;
            if (mediaPeriodQueue.period.getAdGroupCount() == 1 && mediaPeriodQueue.period.getAdGroupTimeUs(0) == 0) {
                Timeline timeline2 = mediaPeriodQueue.timeline;
                window = mediaPeriodQueue.window;
                Period period = mediaPeriodQueue.period;
                Pair<Object, Long> defaultPosition2 = timeline2.getPeriodPosition(window, period, period.windowIndex, C0555C.TIME_UNSET, Math.max(0, bufferedDurationUs));
                if (defaultPosition2 == null) {
                    return null;
                }
                startPositionUs2 = ((Long) defaultPosition2.second).longValue();
            } else {
                startPositionUs2 = startPositionUs3;
            }
            return getMediaPeriodInfoForContent(currentPeriodId.periodUid, startPositionUs2, currentPeriodId.windowSequenceNumber);
        } else if (mediaPeriodInfo.id.endPositionUs != Long.MIN_VALUE) {
            adGroupIndex = mediaPeriodQueue.period.getAdGroupIndexForPositionUs(mediaPeriodInfo.id.endPositionUs);
            if (adGroupIndex == -1) {
                return getMediaPeriodInfoForContent(currentPeriodId.periodUid, mediaPeriodInfo.id.endPositionUs, currentPeriodId.windowSequenceNumber);
            }
            adCountInCurrentAdGroup = mediaPeriodQueue.period.getFirstAdIndexToPlay(adGroupIndex);
            if (mediaPeriodQueue.period.isAdAvailable(adGroupIndex, adCountInCurrentAdGroup)) {
                mediaPeriodInfoForAd = getMediaPeriodInfoForAd(currentPeriodId.periodUid, adGroupIndex, adCountInCurrentAdGroup, mediaPeriodInfo.id.endPositionUs, currentPeriodId.windowSequenceNumber);
            } else {
                mediaPeriodInfoForAd = null;
            }
            return mediaPeriodInfoForAd;
        } else {
            adGroupIndex = mediaPeriodQueue.period.getAdGroupCount();
            if (adGroupIndex == 0) {
                return null;
            }
            adCountInCurrentAdGroup = adGroupIndex - 1;
            if (mediaPeriodQueue.period.getAdGroupTimeUs(adCountInCurrentAdGroup) == Long.MIN_VALUE) {
                if (!mediaPeriodQueue.period.hasPlayedAdGroup(adCountInCurrentAdGroup)) {
                    currentPeriodIndex = mediaPeriodQueue.period.getFirstAdIndexToPlay(adCountInCurrentAdGroup);
                    if (!mediaPeriodQueue.period.isAdAvailable(adCountInCurrentAdGroup, currentPeriodIndex)) {
                        return null;
                    }
                    windowSequenceNumber = mediaPeriodQueue.period.getDurationUs();
                    return getMediaPeriodInfoForAd(currentPeriodId.periodUid, adCountInCurrentAdGroup, currentPeriodIndex, windowSequenceNumber, currentPeriodId.windowSequenceNumber);
                }
            }
            return null;
        }
    }

    private MediaPeriodInfo getMediaPeriodInfo(MediaPeriodId id, long contentPositionUs, long startPositionUs) {
        this.timeline.getPeriodByUid(id.periodUid, this.period);
        if (!id.isAd()) {
            return getMediaPeriodInfoForContent(id.periodUid, startPositionUs, id.windowSequenceNumber);
        } else if (!this.period.isAdAvailable(id.adGroupIndex, id.adIndexInAdGroup)) {
            return null;
        } else {
            return getMediaPeriodInfoForAd(id.periodUid, id.adGroupIndex, id.adIndexInAdGroup, contentPositionUs, id.windowSequenceNumber);
        }
    }

    private MediaPeriodInfo getMediaPeriodInfoForAd(Object periodUid, int adGroupIndex, int adIndexInAdGroup, long contentPositionUs, long windowSequenceNumber) {
        MediaPeriodId id = new MediaPeriodId(periodUid, adGroupIndex, adIndexInAdGroup, windowSequenceNumber);
        boolean isLastInPeriod = isLastInPeriod(id);
        boolean isLastInTimeline = isLastInTimeline(id, isLastInPeriod);
        return new MediaPeriodInfo(id, adIndexInAdGroup == this.period.getFirstAdIndexToPlay(adGroupIndex) ? r0.period.getAdResumePositionUs() : 0, contentPositionUs, this.timeline.getPeriodByUid(id.periodUid, this.period).getAdDurationUs(id.adGroupIndex, id.adIndexInAdGroup), isLastInPeriod, isLastInTimeline);
    }

    private MediaPeriodInfo getMediaPeriodInfoForContent(Object periodUid, long startPositionUs, long windowSequenceNumber) {
        long j;
        int nextAdGroupIndex = this.period.getAdGroupIndexAfterPositionUs(startPositionUs);
        if (nextAdGroupIndex == -1) {
            j = Long.MIN_VALUE;
        } else {
            j = r0.period.getAdGroupTimeUs(nextAdGroupIndex);
        }
        long endPositionUs = j;
        MediaPeriodId id = new MediaPeriodId(periodUid, windowSequenceNumber, endPositionUs);
        r0.timeline.getPeriodByUid(id.periodUid, r0.period);
        boolean isLastInPeriod = isLastInPeriod(id);
        boolean isLastInPeriod2 = isLastInPeriod;
        return new MediaPeriodInfo(id, startPositionUs, C0555C.TIME_UNSET, endPositionUs == Long.MIN_VALUE ? r0.period.getDurationUs() : endPositionUs, isLastInPeriod, isLastInTimeline(id, isLastInPeriod));
    }

    private boolean isLastInPeriod(MediaPeriodId id) {
        int adGroupCount = this.timeline.getPeriodByUid(id.periodUid, this.period).getAdGroupCount();
        boolean z = true;
        if (adGroupCount == 0) {
            return true;
        }
        int lastAdGroupIndex = adGroupCount - 1;
        boolean isAd = id.isAd();
        if (this.period.getAdGroupTimeUs(lastAdGroupIndex) != Long.MIN_VALUE) {
            if (isAd || id.endPositionUs != Long.MIN_VALUE) {
                z = false;
            }
            return z;
        }
        int postrollAdCount = this.period.getAdCountInAdGroup(lastAdGroupIndex);
        if (postrollAdCount == -1) {
            return false;
        }
        boolean isLastAd = isAd && id.adGroupIndex == lastAdGroupIndex && id.adIndexInAdGroup == postrollAdCount - 1;
        if (!isLastAd) {
            if (isAd || this.period.getFirstAdIndexToPlay(lastAdGroupIndex) != postrollAdCount) {
                z = false;
            }
        }
        return z;
    }

    private boolean isLastInTimeline(MediaPeriodId id, boolean isLastMediaPeriodInPeriod) {
        int periodIndex = this.timeline.getIndexOfPeriod(id.periodUid);
        if (!this.timeline.getWindow(this.timeline.getPeriod(periodIndex, this.period).windowIndex, this.window).isDynamic) {
            if (this.timeline.isLastPeriod(periodIndex, this.period, this.window, this.repeatMode, this.shuffleModeEnabled) && isLastMediaPeriodInPeriod) {
                return true;
            }
        }
        return false;
    }
}
