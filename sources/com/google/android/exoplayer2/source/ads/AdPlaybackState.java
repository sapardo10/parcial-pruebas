package com.google.android.exoplayer2.source.ads;

import android.net.Uri;
import android.support.annotation.CheckResult;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.util.Assertions;
import java.util.Arrays;

public final class AdPlaybackState {
    public static final int AD_STATE_AVAILABLE = 1;
    public static final int AD_STATE_ERROR = 4;
    public static final int AD_STATE_PLAYED = 3;
    public static final int AD_STATE_SKIPPED = 2;
    public static final int AD_STATE_UNAVAILABLE = 0;
    public static final AdPlaybackState NONE = new AdPlaybackState(new long[0]);
    public final int adGroupCount;
    public final long[] adGroupTimesUs;
    public final AdGroup[] adGroups;
    public final long adResumePositionUs;
    public final long contentDurationUs;

    public static final class AdGroup {
        public final int count;
        public final long[] durationsUs;
        public final int[] states;
        public final Uri[] uris;

        public AdGroup() {
            this(-1, new int[0], new Uri[0], new long[0]);
        }

        private AdGroup(int count, int[] states, Uri[] uris, long[] durationsUs) {
            Assertions.checkArgument(states.length == uris.length);
            this.count = count;
            this.states = states;
            this.uris = uris;
            this.durationsUs = durationsUs;
        }

        public int getFirstAdIndexToPlay() {
            return getNextAdIndexToPlay(-1);
        }

        public int getNextAdIndexToPlay(int lastPlayedAdIndex) {
            int nextAdIndexToPlay = lastPlayedAdIndex + 1;
            while (true) {
                int[] iArr = this.states;
                if (nextAdIndexToPlay < iArr.length) {
                    if (iArr[nextAdIndexToPlay] == 0) {
                        break;
                    } else if (iArr[nextAdIndexToPlay] == 1) {
                        break;
                    } else {
                        nextAdIndexToPlay++;
                    }
                } else {
                    break;
                }
                return nextAdIndexToPlay;
            }
            return nextAdIndexToPlay;
        }

        public boolean hasUnplayedAds() {
            if (this.count != -1) {
                if (getFirstAdIndexToPlay() >= this.count) {
                    return false;
                }
            }
            return true;
        }

        public boolean equals(Object o) {
            boolean z = true;
            if (this == o) {
                return true;
            }
            if (o != null) {
                if (getClass() == o.getClass()) {
                    AdGroup adGroup = (AdGroup) o;
                    if (this.count == adGroup.count) {
                        if (Arrays.equals(this.uris, adGroup.uris)) {
                            if (Arrays.equals(this.states, adGroup.states)) {
                                if (Arrays.equals(this.durationsUs, adGroup.durationsUs)) {
                                    return z;
                                }
                            }
                        }
                    }
                    z = false;
                    return z;
                }
            }
            return false;
        }

        public int hashCode() {
            return (((((this.count * 31) + Arrays.hashCode(this.uris)) * 31) + Arrays.hashCode(this.states)) * 31) + Arrays.hashCode(this.durationsUs);
        }

        @CheckResult
        public AdGroup withAdCount(int count) {
            boolean z = this.count == -1 && this.states.length <= count;
            Assertions.checkArgument(z);
            return new AdGroup(count, copyStatesWithSpaceForAdCount(this.states, count), (Uri[]) Arrays.copyOf(this.uris, count), copyDurationsUsWithSpaceForAdCount(this.durationsUs, count));
        }

        @CheckResult
        public AdGroup withAdUri(Uri uri, int index) {
            boolean z;
            int[] states;
            long[] durationsUs;
            Uri[] uris;
            int i = this.count;
            boolean z2 = false;
            if (i != -1) {
                if (index >= i) {
                    z = false;
                    Assertions.checkArgument(z);
                    states = copyStatesWithSpaceForAdCount(this.states, index + 1);
                    if (states[index] == 0) {
                        z2 = true;
                    }
                    Assertions.checkArgument(z2);
                    durationsUs = this.durationsUs;
                    if (durationsUs.length == states.length) {
                        durationsUs = copyDurationsUsWithSpaceForAdCount(durationsUs, states.length);
                    }
                    uris = (Uri[]) Arrays.copyOf(this.uris, states.length);
                    uris[index] = uri;
                    states[index] = 1;
                    return new AdGroup(this.count, states, uris, durationsUs);
                }
            }
            z = true;
            Assertions.checkArgument(z);
            states = copyStatesWithSpaceForAdCount(this.states, index + 1);
            if (states[index] == 0) {
                z2 = true;
            }
            Assertions.checkArgument(z2);
            durationsUs = this.durationsUs;
            if (durationsUs.length == states.length) {
                durationsUs = copyDurationsUsWithSpaceForAdCount(durationsUs, states.length);
            }
            uris = (Uri[]) Arrays.copyOf(this.uris, states.length);
            uris[index] = uri;
            states[index] = 1;
            return new AdGroup(this.count, states, uris, durationsUs);
        }

        @CheckResult
        public AdGroup withAdState(int state, int index) {
            boolean z;
            int[] states;
            long[] durationsUs;
            Uri[] uris;
            int i = this.count;
            boolean z2 = false;
            if (i != -1) {
                if (index >= i) {
                    z = false;
                    Assertions.checkArgument(z);
                    states = copyStatesWithSpaceForAdCount(this.states, index + 1);
                    if (!(states[index] == 0 || states[index] == 1)) {
                        if (states[index] == state) {
                            Assertions.checkArgument(z2);
                            durationsUs = this.durationsUs;
                            if (durationsUs.length != states.length) {
                                durationsUs = copyDurationsUsWithSpaceForAdCount(durationsUs, states.length);
                            }
                            uris = this.uris;
                            if (uris.length != states.length) {
                                uris = (Uri[]) Arrays.copyOf(uris, states.length);
                            }
                            states[index] = state;
                            return new AdGroup(this.count, states, uris, durationsUs);
                        }
                    }
                    z2 = true;
                    Assertions.checkArgument(z2);
                    durationsUs = this.durationsUs;
                    if (durationsUs.length != states.length) {
                        durationsUs = copyDurationsUsWithSpaceForAdCount(durationsUs, states.length);
                    }
                    uris = this.uris;
                    if (uris.length != states.length) {
                        uris = (Uri[]) Arrays.copyOf(uris, states.length);
                    }
                    states[index] = state;
                    return new AdGroup(this.count, states, uris, durationsUs);
                }
            }
            z = true;
            Assertions.checkArgument(z);
            states = copyStatesWithSpaceForAdCount(this.states, index + 1);
            if (states[index] == state) {
                Assertions.checkArgument(z2);
                durationsUs = this.durationsUs;
                if (durationsUs.length != states.length) {
                    durationsUs = copyDurationsUsWithSpaceForAdCount(durationsUs, states.length);
                }
                uris = this.uris;
                if (uris.length != states.length) {
                    uris = (Uri[]) Arrays.copyOf(uris, states.length);
                }
                states[index] = state;
                return new AdGroup(this.count, states, uris, durationsUs);
            }
            z2 = true;
            Assertions.checkArgument(z2);
            durationsUs = this.durationsUs;
            if (durationsUs.length != states.length) {
                durationsUs = copyDurationsUsWithSpaceForAdCount(durationsUs, states.length);
            }
            uris = this.uris;
            if (uris.length != states.length) {
                uris = (Uri[]) Arrays.copyOf(uris, states.length);
            }
            states[index] = state;
            return new AdGroup(this.count, states, uris, durationsUs);
        }

        @CheckResult
        public AdGroup withAdDurationsUs(long[] durationsUs) {
            boolean z;
            int length;
            Uri[] uriArr;
            if (this.count != -1) {
                if (durationsUs.length > this.uris.length) {
                    z = false;
                    Assertions.checkArgument(z);
                    length = durationsUs.length;
                    uriArr = this.uris;
                    if (length < uriArr.length) {
                        durationsUs = copyDurationsUsWithSpaceForAdCount(durationsUs, uriArr.length);
                    }
                    return new AdGroup(this.count, this.states, this.uris, durationsUs);
                }
            }
            z = true;
            Assertions.checkArgument(z);
            length = durationsUs.length;
            uriArr = this.uris;
            if (length < uriArr.length) {
                durationsUs = copyDurationsUsWithSpaceForAdCount(durationsUs, uriArr.length);
            }
            return new AdGroup(this.count, this.states, this.uris, durationsUs);
        }

        @CheckResult
        public AdGroup withAllAdsSkipped() {
            if (this.count == -1) {
                return new AdGroup(0, new int[0], new Uri[0], new long[0]);
            }
            int[] states = this.states;
            int count = states.length;
            states = Arrays.copyOf(states, count);
            for (int i = 0; i < count; i++) {
                if (states[i] != 1) {
                    if (states[i] != 0) {
                    }
                }
                states[i] = 2;
            }
            return new AdGroup(count, states, this.uris, this.durationsUs);
        }

        @CheckResult
        private static int[] copyStatesWithSpaceForAdCount(int[] states, int count) {
            int oldStateCount = states.length;
            int newStateCount = Math.max(count, oldStateCount);
            states = Arrays.copyOf(states, newStateCount);
            Arrays.fill(states, oldStateCount, newStateCount, 0);
            return states;
        }

        @CheckResult
        private static long[] copyDurationsUsWithSpaceForAdCount(long[] durationsUs, int count) {
            int oldDurationsUsCount = durationsUs.length;
            int newDurationsUsCount = Math.max(count, oldDurationsUsCount);
            durationsUs = Arrays.copyOf(durationsUs, newDurationsUsCount);
            Arrays.fill(durationsUs, oldDurationsUsCount, newDurationsUsCount, C0555C.TIME_UNSET);
            return durationsUs;
        }
    }

    public AdPlaybackState(long... adGroupTimesUs) {
        int count = adGroupTimesUs.length;
        this.adGroupCount = count;
        this.adGroupTimesUs = Arrays.copyOf(adGroupTimesUs, count);
        this.adGroups = new AdGroup[count];
        for (int i = 0; i < count; i++) {
            this.adGroups[i] = new AdGroup();
        }
        this.adResumePositionUs = 0;
        this.contentDurationUs = C0555C.TIME_UNSET;
    }

    private AdPlaybackState(long[] adGroupTimesUs, AdGroup[] adGroups, long adResumePositionUs, long contentDurationUs) {
        this.adGroupCount = adGroups.length;
        this.adGroupTimesUs = adGroupTimesUs;
        this.adGroups = adGroups;
        this.adResumePositionUs = adResumePositionUs;
        this.contentDurationUs = contentDurationUs;
    }

    public int getAdGroupIndexForPositionUs(long positionUs) {
        int index = this.adGroupTimesUs.length - 1;
        while (index >= 0 && isPositionBeforeAdGroup(positionUs, index)) {
            index--;
        }
        return (index < 0 || !this.adGroups[index].hasUnplayedAds()) ? -1 : index;
    }

    public int getAdGroupIndexAfterPositionUs(long positionUs) {
        int index = 0;
        while (true) {
            long[] jArr = this.adGroupTimesUs;
            if (index < jArr.length && jArr[index] != Long.MIN_VALUE) {
                if (positionUs < jArr[index]) {
                    if (this.adGroups[index].hasUnplayedAds()) {
                        break;
                    }
                }
                index++;
            }
            return index >= this.adGroupTimesUs.length ? index : -1;
        }
        if (index >= this.adGroupTimesUs.length) {
        }
    }

    @CheckResult
    public AdPlaybackState withAdCount(int adGroupIndex, int adCount) {
        Assertions.checkArgument(adCount > 0);
        if (this.adGroups[adGroupIndex].count == adCount) {
            return this;
        }
        AdGroup[] adGroupArr = this.adGroups;
        adGroupArr = (AdGroup[]) Arrays.copyOf(adGroupArr, adGroupArr.length);
        adGroupArr[adGroupIndex] = this.adGroups[adGroupIndex].withAdCount(adCount);
        return new AdPlaybackState(this.adGroupTimesUs, adGroupArr, this.adResumePositionUs, this.contentDurationUs);
    }

    @CheckResult
    public AdPlaybackState withAdUri(int adGroupIndex, int adIndexInAdGroup, Uri uri) {
        AdGroup[] adGroupArr = this.adGroups;
        adGroupArr = (AdGroup[]) Arrays.copyOf(adGroupArr, adGroupArr.length);
        adGroupArr[adGroupIndex] = adGroupArr[adGroupIndex].withAdUri(uri, adIndexInAdGroup);
        return new AdPlaybackState(this.adGroupTimesUs, adGroupArr, this.adResumePositionUs, this.contentDurationUs);
    }

    @CheckResult
    public AdPlaybackState withPlayedAd(int adGroupIndex, int adIndexInAdGroup) {
        AdGroup[] adGroupArr = this.adGroups;
        adGroupArr = (AdGroup[]) Arrays.copyOf(adGroupArr, adGroupArr.length);
        adGroupArr[adGroupIndex] = adGroupArr[adGroupIndex].withAdState(3, adIndexInAdGroup);
        return new AdPlaybackState(this.adGroupTimesUs, adGroupArr, this.adResumePositionUs, this.contentDurationUs);
    }

    @CheckResult
    public AdPlaybackState withSkippedAd(int adGroupIndex, int adIndexInAdGroup) {
        AdGroup[] adGroupArr = this.adGroups;
        adGroupArr = (AdGroup[]) Arrays.copyOf(adGroupArr, adGroupArr.length);
        adGroupArr[adGroupIndex] = adGroupArr[adGroupIndex].withAdState(2, adIndexInAdGroup);
        return new AdPlaybackState(this.adGroupTimesUs, adGroupArr, this.adResumePositionUs, this.contentDurationUs);
    }

    @CheckResult
    public AdPlaybackState withAdLoadError(int adGroupIndex, int adIndexInAdGroup) {
        AdGroup[] adGroupArr = this.adGroups;
        adGroupArr = (AdGroup[]) Arrays.copyOf(adGroupArr, adGroupArr.length);
        adGroupArr[adGroupIndex] = adGroupArr[adGroupIndex].withAdState(4, adIndexInAdGroup);
        return new AdPlaybackState(this.adGroupTimesUs, adGroupArr, this.adResumePositionUs, this.contentDurationUs);
    }

    @CheckResult
    public AdPlaybackState withSkippedAdGroup(int adGroupIndex) {
        AdGroup[] adGroupArr = this.adGroups;
        adGroupArr = (AdGroup[]) Arrays.copyOf(adGroupArr, adGroupArr.length);
        adGroupArr[adGroupIndex] = adGroupArr[adGroupIndex].withAllAdsSkipped();
        return new AdPlaybackState(this.adGroupTimesUs, adGroupArr, this.adResumePositionUs, this.contentDurationUs);
    }

    @CheckResult
    public AdPlaybackState withAdDurationsUs(long[][] adDurationUs) {
        AdGroup[] adGroupArr = this.adGroups;
        adGroupArr = (AdGroup[]) Arrays.copyOf(adGroupArr, adGroupArr.length);
        for (int adGroupIndex = 0; adGroupIndex < this.adGroupCount; adGroupIndex++) {
            adGroupArr[adGroupIndex] = adGroupArr[adGroupIndex].withAdDurationsUs(adDurationUs[adGroupIndex]);
        }
        return new AdPlaybackState(this.adGroupTimesUs, adGroupArr, this.adResumePositionUs, this.contentDurationUs);
    }

    @CheckResult
    public AdPlaybackState withAdResumePositionUs(long adResumePositionUs) {
        if (this.adResumePositionUs == adResumePositionUs) {
            return this;
        }
        return new AdPlaybackState(this.adGroupTimesUs, this.adGroups, adResumePositionUs, this.contentDurationUs);
    }

    @CheckResult
    public AdPlaybackState withContentDurationUs(long contentDurationUs) {
        if (this.contentDurationUs == contentDurationUs) {
            return this;
        }
        return new AdPlaybackState(this.adGroupTimesUs, this.adGroups, this.adResumePositionUs, contentDurationUs);
    }

    public boolean equals(Object o) {
        boolean z = true;
        if (this == o) {
            return true;
        }
        if (o != null) {
            if (getClass() == o.getClass()) {
                AdPlaybackState that = (AdPlaybackState) o;
                if (this.adGroupCount == that.adGroupCount && this.adResumePositionUs == that.adResumePositionUs && this.contentDurationUs == that.contentDurationUs) {
                    if (Arrays.equals(this.adGroupTimesUs, that.adGroupTimesUs)) {
                        if (Arrays.equals(this.adGroups, that.adGroups)) {
                            return z;
                        }
                    }
                }
                z = false;
                return z;
            }
        }
        return false;
    }

    public int hashCode() {
        return (((((((this.adGroupCount * 31) + ((int) this.adResumePositionUs)) * 31) + ((int) this.contentDurationUs)) * 31) + Arrays.hashCode(this.adGroupTimesUs)) * 31) + Arrays.hashCode(this.adGroups);
    }

    private boolean isPositionBeforeAdGroup(long positionUs, int adGroupIndex) {
        long adGroupPositionUs = this.adGroupTimesUs[adGroupIndex];
        boolean z = true;
        if (adGroupPositionUs == Long.MIN_VALUE) {
            long j = this.contentDurationUs;
            if (j != C0555C.TIME_UNSET) {
                if (positionUs >= j) {
                    z = false;
                }
            }
            return z;
        }
        if (positionUs >= adGroupPositionUs) {
            z = false;
        }
        return z;
    }
}
