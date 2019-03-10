package com.google.android.exoplayer2.trackselection;

import android.content.Context;
import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Pair;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.RendererConfiguration;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector.MappedTrackInfo;
import com.google.android.exoplayer2.trackselection.TrackSelection.Factory;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;

public class DefaultTrackSelector extends MappingTrackSelector {
    private static final float FRACTION_TO_CONSIDER_FULLSCREEN = 0.98f;
    private static final int[] NO_TRACKS = new int[0];
    private static final int WITHIN_RENDERER_CAPABILITIES_BONUS = 1000;
    private final Factory adaptiveTrackSelectionFactory;
    private final AtomicReference<Parameters> parametersReference;

    private static final class AudioConfigurationTuple {
        public final int channelCount;
        @Nullable
        public final String mimeType;
        public final int sampleRate;

        public AudioConfigurationTuple(int channelCount, int sampleRate, @Nullable String mimeType) {
            this.channelCount = channelCount;
            this.sampleRate = sampleRate;
            this.mimeType = mimeType;
        }

        public boolean equals(@Nullable Object obj) {
            boolean z = true;
            if (this == obj) {
                return true;
            }
            if (obj != null) {
                if (getClass() == obj.getClass()) {
                    AudioConfigurationTuple other = (AudioConfigurationTuple) obj;
                    if (this.channelCount == other.channelCount && this.sampleRate == other.sampleRate) {
                        if (TextUtils.equals(this.mimeType, other.mimeType)) {
                            return z;
                        }
                    }
                    z = false;
                    return z;
                }
            }
            return false;
        }

        public int hashCode() {
            int i = ((this.channelCount * 31) + this.sampleRate) * 31;
            String str = this.mimeType;
            return i + (str != null ? str.hashCode() : 0);
        }
    }

    protected static final class AudioTrackScore implements Comparable<AudioTrackScore> {
        private final int bitrate;
        private final int channelCount;
        private final int defaultSelectionFlagScore;
        private final int matchLanguageScore;
        private final Parameters parameters;
        private final int sampleRate;
        private final int withinRendererCapabilitiesScore;

        public AudioTrackScore(Format format, Parameters parameters, int formatSupport) {
            this.parameters = parameters;
            int i = 0;
            this.withinRendererCapabilitiesScore = DefaultTrackSelector.isSupported(formatSupport, false);
            this.matchLanguageScore = DefaultTrackSelector.formatHasLanguage(format, parameters.preferredAudioLanguage);
            if ((format.selectionFlags & 1) != 0) {
                i = 1;
            }
            this.defaultSelectionFlagScore = i;
            this.channelCount = format.channelCount;
            this.sampleRate = format.sampleRate;
            this.bitrate = format.bitrate;
        }

        public int compareTo(@NonNull AudioTrackScore other) {
            int i = this.withinRendererCapabilitiesScore;
            int i2 = other.withinRendererCapabilitiesScore;
            if (i != i2) {
                return DefaultTrackSelector.compareInts(i, i2);
            }
            i = this.matchLanguageScore;
            i2 = other.matchLanguageScore;
            if (i != i2) {
                return DefaultTrackSelector.compareInts(i, i2);
            }
            i = this.defaultSelectionFlagScore;
            i2 = other.defaultSelectionFlagScore;
            if (i != i2) {
                return DefaultTrackSelector.compareInts(i, i2);
            }
            if (this.parameters.forceLowestBitrate) {
                return DefaultTrackSelector.compareInts(other.bitrate, this.bitrate);
            }
            i2 = 1;
            if (this.withinRendererCapabilitiesScore != 1) {
                i2 = -1;
            }
            i = i2;
            i2 = this.channelCount;
            int i3 = other.channelCount;
            if (i2 != i3) {
                return DefaultTrackSelector.compareInts(i2, i3) * i;
            }
            i2 = this.sampleRate;
            i3 = other.sampleRate;
            if (i2 != i3) {
                return DefaultTrackSelector.compareInts(i2, i3) * i;
            }
            return DefaultTrackSelector.compareInts(this.bitrate, other.bitrate) * i;
        }
    }

    public static final class Parameters implements Parcelable {
        public static final Creator<Parameters> CREATOR = new C06421();
        public static final Parameters DEFAULT = new Parameters();
        public final boolean allowMixedMimeAdaptiveness;
        public final boolean allowNonSeamlessAdaptiveness;
        public final int disabledTextTrackSelectionFlags;
        public final boolean exceedRendererCapabilitiesIfNecessary;
        public final boolean exceedVideoConstraintsIfNecessary;
        public final boolean forceHighestSupportedBitrate;
        public final boolean forceLowestBitrate;
        public final int maxVideoBitrate;
        public final int maxVideoFrameRate;
        public final int maxVideoHeight;
        public final int maxVideoWidth;
        @Nullable
        public final String preferredAudioLanguage;
        @Nullable
        public final String preferredTextLanguage;
        private final SparseBooleanArray rendererDisabledFlags;
        public final boolean selectUndeterminedTextLanguage;
        private final SparseArray<Map<TrackGroupArray, SelectionOverride>> selectionOverrides;
        public final int tunnelingAudioSessionId;
        public final int viewportHeight;
        public final boolean viewportOrientationMayChange;
        public final int viewportWidth;

        /* renamed from: com.google.android.exoplayer2.trackselection.DefaultTrackSelector$Parameters$1 */
        static class C06421 implements Creator<Parameters> {
            C06421() {
            }

            public Parameters createFromParcel(Parcel in) {
                return new Parameters(in);
            }

            public Parameters[] newArray(int size) {
                return new Parameters[size];
            }
        }

        private Parameters() {
            SparseArray sparseArray = r2;
            SparseArray sparseArray2 = new SparseArray();
            SparseBooleanArray sparseBooleanArray = r3;
            SparseBooleanArray sparseBooleanArray2 = new SparseBooleanArray();
            this(sparseArray, sparseBooleanArray, null, null, false, 0, false, false, false, true, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, true, true, Integer.MAX_VALUE, Integer.MAX_VALUE, true, 0);
        }

        Parameters(SparseArray<Map<TrackGroupArray, SelectionOverride>> selectionOverrides, SparseBooleanArray rendererDisabledFlags, @Nullable String preferredAudioLanguage, @Nullable String preferredTextLanguage, boolean selectUndeterminedTextLanguage, int disabledTextTrackSelectionFlags, boolean forceLowestBitrate, boolean forceHighestSupportedBitrate, boolean allowMixedMimeAdaptiveness, boolean allowNonSeamlessAdaptiveness, int maxVideoWidth, int maxVideoHeight, int maxVideoFrameRate, int maxVideoBitrate, boolean exceedVideoConstraintsIfNecessary, boolean exceedRendererCapabilitiesIfNecessary, int viewportWidth, int viewportHeight, boolean viewportOrientationMayChange, int tunnelingAudioSessionId) {
            this.selectionOverrides = selectionOverrides;
            this.rendererDisabledFlags = rendererDisabledFlags;
            this.preferredAudioLanguage = Util.normalizeLanguageCode(preferredAudioLanguage);
            this.preferredTextLanguage = Util.normalizeLanguageCode(preferredTextLanguage);
            this.selectUndeterminedTextLanguage = selectUndeterminedTextLanguage;
            this.disabledTextTrackSelectionFlags = disabledTextTrackSelectionFlags;
            this.forceLowestBitrate = forceLowestBitrate;
            this.forceHighestSupportedBitrate = forceHighestSupportedBitrate;
            this.allowMixedMimeAdaptiveness = allowMixedMimeAdaptiveness;
            this.allowNonSeamlessAdaptiveness = allowNonSeamlessAdaptiveness;
            this.maxVideoWidth = maxVideoWidth;
            this.maxVideoHeight = maxVideoHeight;
            this.maxVideoFrameRate = maxVideoFrameRate;
            this.maxVideoBitrate = maxVideoBitrate;
            this.exceedVideoConstraintsIfNecessary = exceedVideoConstraintsIfNecessary;
            this.exceedRendererCapabilitiesIfNecessary = exceedRendererCapabilitiesIfNecessary;
            this.viewportWidth = viewportWidth;
            this.viewportHeight = viewportHeight;
            this.viewportOrientationMayChange = viewportOrientationMayChange;
            this.tunnelingAudioSessionId = tunnelingAudioSessionId;
        }

        Parameters(Parcel in) {
            this.selectionOverrides = readSelectionOverrides(in);
            this.rendererDisabledFlags = in.readSparseBooleanArray();
            this.preferredAudioLanguage = in.readString();
            this.preferredTextLanguage = in.readString();
            this.selectUndeterminedTextLanguage = Util.readBoolean(in);
            this.disabledTextTrackSelectionFlags = in.readInt();
            this.forceLowestBitrate = Util.readBoolean(in);
            this.forceHighestSupportedBitrate = Util.readBoolean(in);
            this.allowMixedMimeAdaptiveness = Util.readBoolean(in);
            this.allowNonSeamlessAdaptiveness = Util.readBoolean(in);
            this.maxVideoWidth = in.readInt();
            this.maxVideoHeight = in.readInt();
            this.maxVideoFrameRate = in.readInt();
            this.maxVideoBitrate = in.readInt();
            this.exceedVideoConstraintsIfNecessary = Util.readBoolean(in);
            this.exceedRendererCapabilitiesIfNecessary = Util.readBoolean(in);
            this.viewportWidth = in.readInt();
            this.viewportHeight = in.readInt();
            this.viewportOrientationMayChange = Util.readBoolean(in);
            this.tunnelingAudioSessionId = in.readInt();
        }

        public final boolean getRendererDisabled(int rendererIndex) {
            return this.rendererDisabledFlags.get(rendererIndex);
        }

        public final boolean hasSelectionOverride(int rendererIndex, TrackGroupArray groups) {
            Map<TrackGroupArray, SelectionOverride> overrides = (Map) this.selectionOverrides.get(rendererIndex);
            return overrides != null && overrides.containsKey(groups);
        }

        @Nullable
        public final SelectionOverride getSelectionOverride(int rendererIndex, TrackGroupArray groups) {
            Map<TrackGroupArray, SelectionOverride> overrides = (Map) this.selectionOverrides.get(rendererIndex);
            return overrides != null ? (SelectionOverride) overrides.get(groups) : null;
        }

        public ParametersBuilder buildUpon() {
            return new ParametersBuilder();
        }

        public boolean equals(@Nullable Object obj) {
            boolean z = true;
            if (this == obj) {
                return true;
            }
            if (obj != null) {
                if (getClass() == obj.getClass()) {
                    Parameters other = (Parameters) obj;
                    if (this.selectUndeterminedTextLanguage == other.selectUndeterminedTextLanguage && this.disabledTextTrackSelectionFlags == other.disabledTextTrackSelectionFlags && this.forceLowestBitrate == other.forceLowestBitrate && this.forceHighestSupportedBitrate == other.forceHighestSupportedBitrate && this.allowMixedMimeAdaptiveness == other.allowMixedMimeAdaptiveness && this.allowNonSeamlessAdaptiveness == other.allowNonSeamlessAdaptiveness && this.maxVideoWidth == other.maxVideoWidth && this.maxVideoHeight == other.maxVideoHeight && this.maxVideoFrameRate == other.maxVideoFrameRate && this.exceedVideoConstraintsIfNecessary == other.exceedVideoConstraintsIfNecessary && this.exceedRendererCapabilitiesIfNecessary == other.exceedRendererCapabilitiesIfNecessary && this.viewportOrientationMayChange == other.viewportOrientationMayChange && this.viewportWidth == other.viewportWidth && this.viewportHeight == other.viewportHeight && this.maxVideoBitrate == other.maxVideoBitrate && this.tunnelingAudioSessionId == other.tunnelingAudioSessionId) {
                        if (TextUtils.equals(this.preferredAudioLanguage, other.preferredAudioLanguage)) {
                            if (TextUtils.equals(this.preferredTextLanguage, other.preferredTextLanguage)) {
                                if (areRendererDisabledFlagsEqual(this.rendererDisabledFlags, other.rendererDisabledFlags)) {
                                    if (areSelectionOverridesEqual(this.selectionOverrides, other.selectionOverrides)) {
                                        return z;
                                    }
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
            int i;
            int i2 = ((((((((((((((((((((((((((((((this.selectUndeterminedTextLanguage * 31) + this.disabledTextTrackSelectionFlags) * 31) + this.forceLowestBitrate) * 31) + this.forceHighestSupportedBitrate) * 31) + this.allowMixedMimeAdaptiveness) * 31) + this.allowNonSeamlessAdaptiveness) * 31) + this.maxVideoWidth) * 31) + this.maxVideoHeight) * 31) + this.maxVideoFrameRate) * 31) + this.exceedVideoConstraintsIfNecessary) * 31) + this.exceedRendererCapabilitiesIfNecessary) * 31) + this.viewportOrientationMayChange) * 31) + this.viewportWidth) * 31) + this.viewportHeight) * 31) + this.maxVideoBitrate) * 31) + this.tunnelingAudioSessionId) * 31;
            String str = this.preferredAudioLanguage;
            int i3 = 0;
            if (str == null) {
                i = 0;
            } else {
                i = str.hashCode();
            }
            int i4 = (i2 + i) * 31;
            str = this.preferredTextLanguage;
            if (str != null) {
                i3 = str.hashCode();
            }
            return i4 + i3;
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            writeSelectionOverridesToParcel(dest, this.selectionOverrides);
            dest.writeSparseBooleanArray(this.rendererDisabledFlags);
            dest.writeString(this.preferredAudioLanguage);
            dest.writeString(this.preferredTextLanguage);
            Util.writeBoolean(dest, this.selectUndeterminedTextLanguage);
            dest.writeInt(this.disabledTextTrackSelectionFlags);
            Util.writeBoolean(dest, this.forceLowestBitrate);
            Util.writeBoolean(dest, this.forceHighestSupportedBitrate);
            Util.writeBoolean(dest, this.allowMixedMimeAdaptiveness);
            Util.writeBoolean(dest, this.allowNonSeamlessAdaptiveness);
            dest.writeInt(this.maxVideoWidth);
            dest.writeInt(this.maxVideoHeight);
            dest.writeInt(this.maxVideoFrameRate);
            dest.writeInt(this.maxVideoBitrate);
            Util.writeBoolean(dest, this.exceedVideoConstraintsIfNecessary);
            Util.writeBoolean(dest, this.exceedRendererCapabilitiesIfNecessary);
            dest.writeInt(this.viewportWidth);
            dest.writeInt(this.viewportHeight);
            Util.writeBoolean(dest, this.viewportOrientationMayChange);
            dest.writeInt(this.tunnelingAudioSessionId);
        }

        private static SparseArray<Map<TrackGroupArray, SelectionOverride>> readSelectionOverrides(Parcel in) {
            int renderersWithOverridesCount = in.readInt();
            SparseArray<Map<TrackGroupArray, SelectionOverride>> selectionOverrides = new SparseArray(renderersWithOverridesCount);
            for (int i = 0; i < renderersWithOverridesCount; i++) {
                int rendererIndex = in.readInt();
                int overrideCount = in.readInt();
                Map<TrackGroupArray, SelectionOverride> overrides = new HashMap(overrideCount);
                for (int j = 0; j < overrideCount; j++) {
                    overrides.put((TrackGroupArray) in.readParcelable(TrackGroupArray.class.getClassLoader()), (SelectionOverride) in.readParcelable(SelectionOverride.class.getClassLoader()));
                }
                selectionOverrides.put(rendererIndex, overrides);
            }
            return selectionOverrides;
        }

        private static void writeSelectionOverridesToParcel(Parcel dest, SparseArray<Map<TrackGroupArray, SelectionOverride>> selectionOverrides) {
            int renderersWithOverridesCount = selectionOverrides.size();
            dest.writeInt(renderersWithOverridesCount);
            for (int i = 0; i < renderersWithOverridesCount; i++) {
                int rendererIndex = selectionOverrides.keyAt(i);
                Map<TrackGroupArray, SelectionOverride> overrides = (Map) selectionOverrides.valueAt(i);
                int overrideCount = overrides.size();
                dest.writeInt(rendererIndex);
                dest.writeInt(overrideCount);
                for (Entry<TrackGroupArray, SelectionOverride> override : overrides.entrySet()) {
                    dest.writeParcelable((Parcelable) override.getKey(), 0);
                    dest.writeParcelable((Parcelable) override.getValue(), 0);
                }
            }
        }

        private static boolean areRendererDisabledFlagsEqual(SparseBooleanArray first, SparseBooleanArray second) {
            int firstSize = first.size();
            if (second.size() != firstSize) {
                return false;
            }
            for (int indexInFirst = 0; indexInFirst < firstSize; indexInFirst++) {
                if (second.indexOfKey(first.keyAt(indexInFirst)) < 0) {
                    return false;
                }
            }
            return true;
        }

        private static boolean areSelectionOverridesEqual(SparseArray<Map<TrackGroupArray, SelectionOverride>> first, SparseArray<Map<TrackGroupArray, SelectionOverride>> second) {
            int firstSize = first.size();
            if (second.size() != firstSize) {
                return false;
            }
            int indexInFirst = 0;
            while (indexInFirst < firstSize) {
                int indexInSecond = second.indexOfKey(first.keyAt(indexInFirst));
                if (indexInSecond >= 0) {
                    if (areSelectionOverridesEqual((Map) first.valueAt(indexInFirst), (Map) second.valueAt(indexInSecond))) {
                        indexInFirst++;
                    }
                }
                return false;
            }
            return true;
        }

        private static boolean areSelectionOverridesEqual(Map<TrackGroupArray, SelectionOverride> first, Map<TrackGroupArray, SelectionOverride> second) {
            if (second.size() != first.size()) {
                return false;
            }
            for (Entry<TrackGroupArray, SelectionOverride> firstEntry : first.entrySet()) {
                TrackGroupArray key = (TrackGroupArray) firstEntry.getKey();
                if (second.containsKey(key)) {
                    if (Util.areEqual(firstEntry.getValue(), second.get(key))) {
                    }
                }
                return false;
            }
            return true;
        }
    }

    public static final class ParametersBuilder {
        private boolean allowMixedMimeAdaptiveness;
        private boolean allowNonSeamlessAdaptiveness;
        private int disabledTextTrackSelectionFlags;
        private boolean exceedRendererCapabilitiesIfNecessary;
        private boolean exceedVideoConstraintsIfNecessary;
        private boolean forceHighestSupportedBitrate;
        private boolean forceLowestBitrate;
        private int maxVideoBitrate;
        private int maxVideoFrameRate;
        private int maxVideoHeight;
        private int maxVideoWidth;
        @Nullable
        private String preferredAudioLanguage;
        @Nullable
        private String preferredTextLanguage;
        private final SparseBooleanArray rendererDisabledFlags;
        private boolean selectUndeterminedTextLanguage;
        private final SparseArray<Map<TrackGroupArray, SelectionOverride>> selectionOverrides;
        private int tunnelingAudioSessionId;
        private int viewportHeight;
        private boolean viewportOrientationMayChange;
        private int viewportWidth;

        public ParametersBuilder() {
            this(Parameters.DEFAULT);
        }

        private ParametersBuilder(Parameters initialValues) {
            this.selectionOverrides = cloneSelectionOverrides(initialValues.selectionOverrides);
            this.rendererDisabledFlags = initialValues.rendererDisabledFlags.clone();
            this.preferredAudioLanguage = initialValues.preferredAudioLanguage;
            this.preferredTextLanguage = initialValues.preferredTextLanguage;
            this.selectUndeterminedTextLanguage = initialValues.selectUndeterminedTextLanguage;
            this.disabledTextTrackSelectionFlags = initialValues.disabledTextTrackSelectionFlags;
            this.forceLowestBitrate = initialValues.forceLowestBitrate;
            this.forceHighestSupportedBitrate = initialValues.forceHighestSupportedBitrate;
            this.allowMixedMimeAdaptiveness = initialValues.allowMixedMimeAdaptiveness;
            this.allowNonSeamlessAdaptiveness = initialValues.allowNonSeamlessAdaptiveness;
            this.maxVideoWidth = initialValues.maxVideoWidth;
            this.maxVideoHeight = initialValues.maxVideoHeight;
            this.maxVideoFrameRate = initialValues.maxVideoFrameRate;
            this.maxVideoBitrate = initialValues.maxVideoBitrate;
            this.exceedVideoConstraintsIfNecessary = initialValues.exceedVideoConstraintsIfNecessary;
            this.exceedRendererCapabilitiesIfNecessary = initialValues.exceedRendererCapabilitiesIfNecessary;
            this.viewportWidth = initialValues.viewportWidth;
            this.viewportHeight = initialValues.viewportHeight;
            this.viewportOrientationMayChange = initialValues.viewportOrientationMayChange;
            this.tunnelingAudioSessionId = initialValues.tunnelingAudioSessionId;
        }

        public ParametersBuilder setPreferredAudioLanguage(String preferredAudioLanguage) {
            this.preferredAudioLanguage = preferredAudioLanguage;
            return this;
        }

        public ParametersBuilder setPreferredTextLanguage(String preferredTextLanguage) {
            this.preferredTextLanguage = preferredTextLanguage;
            return this;
        }

        public ParametersBuilder setSelectUndeterminedTextLanguage(boolean selectUndeterminedTextLanguage) {
            this.selectUndeterminedTextLanguage = selectUndeterminedTextLanguage;
            return this;
        }

        public ParametersBuilder setDisabledTextTrackSelectionFlags(int disabledTextTrackSelectionFlags) {
            this.disabledTextTrackSelectionFlags = disabledTextTrackSelectionFlags;
            return this;
        }

        public ParametersBuilder setForceLowestBitrate(boolean forceLowestBitrate) {
            this.forceLowestBitrate = forceLowestBitrate;
            return this;
        }

        public ParametersBuilder setForceHighestSupportedBitrate(boolean forceHighestSupportedBitrate) {
            this.forceHighestSupportedBitrate = forceHighestSupportedBitrate;
            return this;
        }

        public ParametersBuilder setAllowMixedMimeAdaptiveness(boolean allowMixedMimeAdaptiveness) {
            this.allowMixedMimeAdaptiveness = allowMixedMimeAdaptiveness;
            return this;
        }

        public ParametersBuilder setAllowNonSeamlessAdaptiveness(boolean allowNonSeamlessAdaptiveness) {
            this.allowNonSeamlessAdaptiveness = allowNonSeamlessAdaptiveness;
            return this;
        }

        public ParametersBuilder setMaxVideoSizeSd() {
            return setMaxVideoSize(1279, 719);
        }

        public ParametersBuilder clearVideoSizeConstraints() {
            return setMaxVideoSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
        }

        public ParametersBuilder setMaxVideoSize(int maxVideoWidth, int maxVideoHeight) {
            this.maxVideoWidth = maxVideoWidth;
            this.maxVideoHeight = maxVideoHeight;
            return this;
        }

        public ParametersBuilder setMaxVideoFrameRate(int maxVideoFrameRate) {
            this.maxVideoFrameRate = maxVideoFrameRate;
            return this;
        }

        public ParametersBuilder setMaxVideoBitrate(int maxVideoBitrate) {
            this.maxVideoBitrate = maxVideoBitrate;
            return this;
        }

        public ParametersBuilder setExceedVideoConstraintsIfNecessary(boolean exceedVideoConstraintsIfNecessary) {
            this.exceedVideoConstraintsIfNecessary = exceedVideoConstraintsIfNecessary;
            return this;
        }

        public ParametersBuilder setExceedRendererCapabilitiesIfNecessary(boolean exceedRendererCapabilitiesIfNecessary) {
            this.exceedRendererCapabilitiesIfNecessary = exceedRendererCapabilitiesIfNecessary;
            return this;
        }

        public ParametersBuilder setViewportSizeToPhysicalDisplaySize(Context context, boolean viewportOrientationMayChange) {
            Point viewportSize = Util.getPhysicalDisplaySize(context);
            return setViewportSize(viewportSize.x, viewportSize.y, viewportOrientationMayChange);
        }

        public ParametersBuilder clearViewportSizeConstraints() {
            return setViewportSize(Integer.MAX_VALUE, Integer.MAX_VALUE, true);
        }

        public ParametersBuilder setViewportSize(int viewportWidth, int viewportHeight, boolean viewportOrientationMayChange) {
            this.viewportWidth = viewportWidth;
            this.viewportHeight = viewportHeight;
            this.viewportOrientationMayChange = viewportOrientationMayChange;
            return this;
        }

        public final ParametersBuilder setRendererDisabled(int rendererIndex, boolean disabled) {
            if (this.rendererDisabledFlags.get(rendererIndex) == disabled) {
                return this;
            }
            if (disabled) {
                this.rendererDisabledFlags.put(rendererIndex, true);
            } else {
                this.rendererDisabledFlags.delete(rendererIndex);
            }
            return this;
        }

        public final ParametersBuilder setSelectionOverride(int rendererIndex, TrackGroupArray groups, SelectionOverride override) {
            Map<TrackGroupArray, SelectionOverride> overrides = (Map) this.selectionOverrides.get(rendererIndex);
            if (overrides == null) {
                overrides = new HashMap();
                this.selectionOverrides.put(rendererIndex, overrides);
            }
            if (overrides.containsKey(groups) && Util.areEqual(overrides.get(groups), override)) {
                return this;
            }
            overrides.put(groups, override);
            return this;
        }

        public final ParametersBuilder clearSelectionOverride(int rendererIndex, TrackGroupArray groups) {
            Map<TrackGroupArray, SelectionOverride> overrides = (Map) this.selectionOverrides.get(rendererIndex);
            if (overrides != null) {
                if (overrides.containsKey(groups)) {
                    overrides.remove(groups);
                    if (overrides.isEmpty()) {
                        this.selectionOverrides.remove(rendererIndex);
                    }
                    return this;
                }
            }
            return this;
        }

        public final ParametersBuilder clearSelectionOverrides(int rendererIndex) {
            Map<TrackGroupArray, SelectionOverride> overrides = (Map) this.selectionOverrides.get(rendererIndex);
            if (overrides != null) {
                if (!overrides.isEmpty()) {
                    this.selectionOverrides.remove(rendererIndex);
                    return this;
                }
            }
            return this;
        }

        public final ParametersBuilder clearSelectionOverrides() {
            if (this.selectionOverrides.size() == 0) {
                return this;
            }
            this.selectionOverrides.clear();
            return this;
        }

        public ParametersBuilder setTunnelingAudioSessionId(int tunnelingAudioSessionId) {
            if (this.tunnelingAudioSessionId == tunnelingAudioSessionId) {
                return this;
            }
            this.tunnelingAudioSessionId = tunnelingAudioSessionId;
            return this;
        }

        public Parameters build() {
            return new Parameters(this.selectionOverrides, this.rendererDisabledFlags, this.preferredAudioLanguage, this.preferredTextLanguage, this.selectUndeterminedTextLanguage, this.disabledTextTrackSelectionFlags, this.forceLowestBitrate, this.forceHighestSupportedBitrate, this.allowMixedMimeAdaptiveness, this.allowNonSeamlessAdaptiveness, this.maxVideoWidth, this.maxVideoHeight, this.maxVideoFrameRate, this.maxVideoBitrate, this.exceedVideoConstraintsIfNecessary, this.exceedRendererCapabilitiesIfNecessary, this.viewportWidth, this.viewportHeight, this.viewportOrientationMayChange, this.tunnelingAudioSessionId);
        }

        private static SparseArray<Map<TrackGroupArray, SelectionOverride>> cloneSelectionOverrides(SparseArray<Map<TrackGroupArray, SelectionOverride>> selectionOverrides) {
            SparseArray<Map<TrackGroupArray, SelectionOverride>> clone = new SparseArray();
            for (int i = 0; i < selectionOverrides.size(); i++) {
                clone.put(selectionOverrides.keyAt(i), new HashMap((Map) selectionOverrides.valueAt(i)));
            }
            return clone;
        }
    }

    public static final class SelectionOverride implements Parcelable {
        public static final Creator<SelectionOverride> CREATOR = new C06431();
        public final int groupIndex;
        public final int length;
        public final int[] tracks;

        /* renamed from: com.google.android.exoplayer2.trackselection.DefaultTrackSelector$SelectionOverride$1 */
        static class C06431 implements Creator<SelectionOverride> {
            C06431() {
            }

            public SelectionOverride createFromParcel(Parcel in) {
                return new SelectionOverride(in);
            }

            public SelectionOverride[] newArray(int size) {
                return new SelectionOverride[size];
            }
        }

        public SelectionOverride(int groupIndex, int... tracks) {
            this.groupIndex = groupIndex;
            this.tracks = Arrays.copyOf(tracks, tracks.length);
            this.length = tracks.length;
            Arrays.sort(this.tracks);
        }

        SelectionOverride(Parcel in) {
            this.groupIndex = in.readInt();
            this.length = in.readByte();
            this.tracks = new int[this.length];
            in.readIntArray(this.tracks);
        }

        public boolean containsTrack(int track) {
            for (int overrideTrack : this.tracks) {
                if (overrideTrack == track) {
                    return true;
                }
            }
            return false;
        }

        public int hashCode() {
            return (this.groupIndex * 31) + Arrays.hashCode(this.tracks);
        }

        public boolean equals(@Nullable Object obj) {
            boolean z = true;
            if (this == obj) {
                return true;
            }
            if (obj != null) {
                if (getClass() == obj.getClass()) {
                    SelectionOverride other = (SelectionOverride) obj;
                    if (this.groupIndex != other.groupIndex || !Arrays.equals(this.tracks, other.tracks)) {
                        z = false;
                    }
                    return z;
                }
            }
            return false;
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.groupIndex);
            dest.writeInt(this.tracks.length);
            dest.writeIntArray(this.tracks);
        }
    }

    public DefaultTrackSelector() {
        this(new AdaptiveTrackSelection.Factory());
    }

    @Deprecated
    public DefaultTrackSelector(BandwidthMeter bandwidthMeter) {
        this(new AdaptiveTrackSelection.Factory(bandwidthMeter));
    }

    public DefaultTrackSelector(Factory adaptiveTrackSelectionFactory) {
        this.adaptiveTrackSelectionFactory = adaptiveTrackSelectionFactory;
        this.parametersReference = new AtomicReference(Parameters.DEFAULT);
    }

    public void setParameters(Parameters parameters) {
        Assertions.checkNotNull(parameters);
        if (!((Parameters) this.parametersReference.getAndSet(parameters)).equals(parameters)) {
            invalidate();
        }
    }

    public void setParameters(ParametersBuilder parametersBuilder) {
        setParameters(parametersBuilder.build());
    }

    public Parameters getParameters() {
        return (Parameters) this.parametersReference.get();
    }

    public ParametersBuilder buildUponParameters() {
        return getParameters().buildUpon();
    }

    @Deprecated
    public final void setRendererDisabled(int rendererIndex, boolean disabled) {
        setParameters(buildUponParameters().setRendererDisabled(rendererIndex, disabled));
    }

    @Deprecated
    public final boolean getRendererDisabled(int rendererIndex) {
        return getParameters().getRendererDisabled(rendererIndex);
    }

    @Deprecated
    public final void setSelectionOverride(int rendererIndex, TrackGroupArray groups, SelectionOverride override) {
        setParameters(buildUponParameters().setSelectionOverride(rendererIndex, groups, override));
    }

    @Deprecated
    public final boolean hasSelectionOverride(int rendererIndex, TrackGroupArray groups) {
        return getParameters().hasSelectionOverride(rendererIndex, groups);
    }

    @Nullable
    @Deprecated
    public final SelectionOverride getSelectionOverride(int rendererIndex, TrackGroupArray groups) {
        return getParameters().getSelectionOverride(rendererIndex, groups);
    }

    @Deprecated
    public final void clearSelectionOverride(int rendererIndex, TrackGroupArray groups) {
        setParameters(buildUponParameters().clearSelectionOverride(rendererIndex, groups));
    }

    @Deprecated
    public final void clearSelectionOverrides(int rendererIndex) {
        setParameters(buildUponParameters().clearSelectionOverrides(rendererIndex));
    }

    @Deprecated
    public final void clearSelectionOverrides() {
        setParameters(buildUponParameters().clearSelectionOverrides());
    }

    @Deprecated
    public void setTunnelingAudioSessionId(int tunnelingAudioSessionId) {
        setParameters(buildUponParameters().setTunnelingAudioSessionId(tunnelingAudioSessionId));
    }

    protected final Pair<RendererConfiguration[], TrackSelection[]> selectTracks(MappedTrackInfo mappedTrackInfo, int[][][] rendererFormatSupports, int[] rendererMixedMimeTypeAdaptationSupports) throws ExoPlaybackException {
        Parameters params = (Parameters) this.parametersReference.get();
        int rendererCount = mappedTrackInfo.getRendererCount();
        TrackSelection[] rendererTrackSelections = selectAllTracks(mappedTrackInfo, rendererFormatSupports, rendererMixedMimeTypeAdaptationSupports, params);
        for (int i = 0; i < rendererCount; i++) {
            if (params.getRendererDisabled(i)) {
                rendererTrackSelections[i] = null;
            } else {
                TrackGroupArray rendererTrackGroups = mappedTrackInfo.getTrackGroups(i);
                if (params.hasSelectionOverride(i, rendererTrackGroups)) {
                    SelectionOverride override = params.getSelectionOverride(i, rendererTrackGroups);
                    if (override == null) {
                        rendererTrackSelections[i] = null;
                    } else if (override.length == 1) {
                        rendererTrackSelections[i] = new FixedTrackSelection(rendererTrackGroups.get(override.groupIndex), override.tracks[0]);
                    } else {
                        rendererTrackSelections[i] = ((Factory) Assertions.checkNotNull(this.adaptiveTrackSelectionFactory)).createTrackSelection(rendererTrackGroups.get(override.groupIndex), getBandwidthMeter(), override.tracks);
                    }
                }
            }
        }
        RendererConfiguration[] rendererConfigurations = new RendererConfiguration[rendererCount];
        for (int i2 = 0; i2 < rendererCount; i2++) {
            boolean rendererEnabled;
            if (!params.getRendererDisabled(i2)) {
                if (mappedTrackInfo.getRendererType(i2) != 6) {
                    if (rendererTrackSelections[i2] != null) {
                    }
                }
                rendererEnabled = true;
                rendererConfigurations[i2] = rendererEnabled ? RendererConfiguration.DEFAULT : null;
            }
            rendererEnabled = false;
            if (rendererEnabled) {
            }
            rendererConfigurations[i2] = rendererEnabled ? RendererConfiguration.DEFAULT : null;
        }
        maybeConfigureRenderersForTunneling(mappedTrackInfo, rendererFormatSupports, rendererConfigurations, rendererTrackSelections, params.tunnelingAudioSessionId);
        return Pair.create(rendererConfigurations, rendererTrackSelections);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected com.google.android.exoplayer2.trackselection.TrackSelection[] selectAllTracks(com.google.android.exoplayer2.trackselection.MappingTrackSelector.MappedTrackInfo r23, int[][][] r24, int[] r25, com.google.android.exoplayer2.trackselection.DefaultTrackSelector.Parameters r26) throws com.google.android.exoplayer2.ExoPlaybackException {
        /*
        r22 = this;
        r6 = r22;
        r7 = r23;
        r8 = r26;
        r9 = r23.getRendererCount();
        r10 = new com.google.android.exoplayer2.trackselection.TrackSelection[r9];
        r0 = 0;
        r1 = 0;
        r2 = 0;
        r13 = r0;
        r12 = r1;
        r11 = r2;
    L_0x0012:
        if (r11 >= r9) goto L_0x0050;
    L_0x0014:
        r0 = 2;
        r1 = r7.getRendererType(r11);
        if (r0 != r1) goto L_0x004c;
    L_0x001b:
        r14 = 0;
        r15 = 1;
        if (r12 != 0) goto L_0x003d;
        r1 = r7.getTrackGroups(r11);
        r2 = r24[r11];
        r3 = r25[r11];
        r5 = r6.adaptiveTrackSelectionFactory;
        r0 = r22;
        r4 = r26;
        r0 = r0.selectVideoTrack(r1, r2, r3, r4, r5);
        r10[r11] = r0;
        r0 = r10[r11];
        if (r0 == 0) goto L_0x003a;
    L_0x0038:
        r0 = 1;
        goto L_0x003b;
    L_0x003a:
        r0 = 0;
    L_0x003b:
        r12 = r0;
        goto L_0x003e;
    L_0x003e:
        r0 = r7.getTrackGroups(r11);
        r0 = r0.length;
        if (r0 <= 0) goto L_0x0048;
    L_0x0046:
        r14 = 1;
    L_0x0048:
        r0 = r13 | r14;
        r13 = r0;
        goto L_0x004d;
    L_0x004d:
        r11 = r11 + 1;
        goto L_0x0012;
    L_0x0050:
        r0 = 0;
        r1 = -1;
        r2 = -2147483648; // 0xffffffff80000000 float:-0.0 double:NaN;
        r3 = -1;
        r4 = 0;
        r14 = r0;
        r15 = r1;
        r5 = r2;
        r11 = r4;
        r4 = r3;
    L_0x005b:
        if (r11 >= r9) goto L_0x011d;
    L_0x005d:
        r3 = r7.getRendererType(r11);
        r2 = -1;
        r16 = 0;
        switch(r3) {
            case 1: goto L_0x00c0;
            case 2: goto L_0x00b9;
            case 3: goto L_0x007f;
            default: goto L_0x0067;
        };
    L_0x0067:
        r21 = r3;
        r17 = r4;
        r18 = r5;
        r20 = r9;
        r0 = r7.getTrackGroups(r11);
        r1 = r24[r11];
        r2 = r21;
        r0 = r6.selectOtherTrack(r2, r0, r1, r8);
        r10[r11] = r0;
        goto L_0x0113;
        r0 = r7.getTrackGroups(r11);
        r1 = r24[r11];
        r0 = r6.selectTextTrack(r0, r1, r8);
        if (r0 == 0) goto L_0x00b1;
    L_0x008c:
        r1 = r0.second;
        r1 = (java.lang.Integer) r1;
        r1 = r1.intValue();
        if (r1 <= r5) goto L_0x00b1;
    L_0x0096:
        if (r4 == r2) goto L_0x009b;
    L_0x0098:
        r10[r4] = r16;
        goto L_0x009c;
    L_0x009c:
        r1 = r0.first;
        r1 = (com.google.android.exoplayer2.trackselection.TrackSelection) r1;
        r10[r11] = r1;
        r1 = r0.second;
        r1 = (java.lang.Integer) r1;
        r1 = r1.intValue();
        r2 = r11;
        r5 = r1;
        r4 = r2;
        r20 = r9;
        goto L_0x0117;
    L_0x00b1:
        r17 = r4;
        r18 = r5;
        r20 = r9;
        goto L_0x0113;
    L_0x00b9:
        r17 = r4;
        r18 = r5;
        r20 = r9;
        goto L_0x0113;
        r1 = r7.getTrackGroups(r11);
        r17 = r24[r11];
        r18 = r25[r11];
        if (r13 == 0) goto L_0x00ce;
    L_0x00cb:
        r19 = r16;
        goto L_0x00d2;
    L_0x00ce:
        r0 = r6.adaptiveTrackSelectionFactory;
        r19 = r0;
    L_0x00d2:
        r0 = r22;
        r20 = r9;
        r9 = -1;
        r2 = r17;
        r21 = r3;
        r3 = r18;
        r17 = r4;
        r4 = r26;
        r18 = r5;
        r5 = r19;
        r0 = r0.selectAudioTrack(r1, r2, r3, r4, r5);
        if (r0 == 0) goto L_0x0112;
    L_0x00eb:
        if (r14 == 0) goto L_0x00f9;
    L_0x00ed:
        r1 = r0.second;
        r1 = (com.google.android.exoplayer2.trackselection.DefaultTrackSelector.AudioTrackScore) r1;
        r1 = r1.compareTo(r14);
        if (r1 <= 0) goto L_0x00f8;
    L_0x00f7:
        goto L_0x00fa;
    L_0x00f8:
        goto L_0x0113;
    L_0x00fa:
        if (r15 == r9) goto L_0x00ff;
    L_0x00fc:
        r10[r15] = r16;
        goto L_0x0100;
    L_0x0100:
        r1 = r0.first;
        r1 = (com.google.android.exoplayer2.trackselection.TrackSelection) r1;
        r10[r11] = r1;
        r1 = r0.second;
        r1 = (com.google.android.exoplayer2.trackselection.DefaultTrackSelector.AudioTrackScore) r1;
        r2 = r11;
        r14 = r1;
        r15 = r2;
        r4 = r17;
        r5 = r18;
        goto L_0x0117;
    L_0x0113:
        r4 = r17;
        r5 = r18;
    L_0x0117:
        r11 = r11 + 1;
        r9 = r20;
        goto L_0x005b;
    L_0x011d:
        r17 = r4;
        r18 = r5;
        r20 = r9;
        return r10;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.trackselection.DefaultTrackSelector.selectAllTracks(com.google.android.exoplayer2.trackselection.MappingTrackSelector$MappedTrackInfo, int[][][], int[], com.google.android.exoplayer2.trackselection.DefaultTrackSelector$Parameters):com.google.android.exoplayer2.trackselection.TrackSelection[]");
    }

    @Nullable
    protected TrackSelection selectVideoTrack(TrackGroupArray groups, int[][] formatSupports, int mixedMimeTypeAdaptationSupports, Parameters params, @Nullable Factory adaptiveTrackSelectionFactory) throws ExoPlaybackException {
        TrackSelection selection = null;
        if (!params.forceHighestSupportedBitrate && !params.forceLowestBitrate && adaptiveTrackSelectionFactory != null) {
            selection = selectAdaptiveVideoTrack(groups, formatSupports, mixedMimeTypeAdaptationSupports, params, adaptiveTrackSelectionFactory, getBandwidthMeter());
        }
        if (selection == null) {
            return selectFixedVideoTrack(groups, formatSupports, params);
        }
        return selection;
    }

    @Nullable
    private static TrackSelection selectAdaptiveVideoTrack(TrackGroupArray groups, int[][] formatSupport, int mixedMimeTypeAdaptationSupports, Parameters params, Factory adaptiveTrackSelectionFactory, BandwidthMeter bandwidthMeter) throws ExoPlaybackException {
        BandwidthMeter bandwidthMeter2;
        TrackGroupArray trackGroupArray = groups;
        Parameters parameters = params;
        int requiredAdaptiveSupport = parameters.allowNonSeamlessAdaptiveness ? 24 : 16;
        boolean allowMixedMimeTypes = parameters.allowMixedMimeAdaptiveness && (mixedMimeTypeAdaptationSupports & requiredAdaptiveSupport) != 0;
        for (int i = 0; i < trackGroupArray.length; i++) {
            TrackGroup group = trackGroupArray.get(i);
            int[] adaptiveTracks = getAdaptiveVideoTracksForGroup(group, formatSupport[i], allowMixedMimeTypes, requiredAdaptiveSupport, parameters.maxVideoWidth, parameters.maxVideoHeight, parameters.maxVideoFrameRate, parameters.maxVideoBitrate, parameters.viewportWidth, parameters.viewportHeight, parameters.viewportOrientationMayChange);
            if (adaptiveTracks.length > 0) {
                return ((Factory) Assertions.checkNotNull(adaptiveTrackSelectionFactory)).createTrackSelection(group, bandwidthMeter, adaptiveTracks);
            }
            bandwidthMeter2 = bandwidthMeter;
        }
        bandwidthMeter2 = bandwidthMeter;
        return null;
    }

    private static int[] getAdaptiveVideoTracksForGroup(TrackGroup group, int[] formatSupport, boolean allowMixedMimeTypes, int requiredAdaptiveSupport, int maxVideoWidth, int maxVideoHeight, int maxVideoFrameRate, int maxVideoBitrate, int viewportWidth, int viewportHeight, boolean viewportOrientationMayChange) {
        TrackGroup trackGroup = group;
        if (trackGroup.length < 2) {
            return NO_TRACKS;
        }
        List<Integer> selectedTrackIndices = getViewportFilteredTrackIndices(trackGroup, viewportWidth, viewportHeight, viewportOrientationMayChange);
        if (selectedTrackIndices.size() < 2) {
            return NO_TRACKS;
        }
        String selectedMimeType;
        if (allowMixedMimeTypes) {
            selectedMimeType = null;
        } else {
            int selectedMimeTypeTrackCount;
            int i;
            HashSet<String> seenMimeTypes = new HashSet();
            selectedMimeType = null;
            int selectedMimeTypeTrackCount2 = 0;
            int i2 = 0;
            while (i2 < selectedTrackIndices.size()) {
                int trackIndex = ((Integer) selectedTrackIndices.get(i2)).intValue();
                String sampleMimeType = trackGroup.getFormat(trackIndex).sampleMimeType;
                if (seenMimeTypes.add(sampleMimeType) != null) {
                    String sampleMimeType2 = sampleMimeType;
                    selectedMimeTypeTrackCount = selectedMimeTypeTrackCount2;
                    i = i2;
                    String selectedMimeType2 = getAdaptiveVideoTrackCountForMimeType(group, formatSupport, requiredAdaptiveSupport, sampleMimeType, maxVideoWidth, maxVideoHeight, maxVideoFrameRate, maxVideoBitrate, selectedTrackIndices);
                    if (selectedMimeType2 > selectedMimeTypeTrackCount) {
                        selectedMimeType = sampleMimeType2;
                        selectedMimeTypeTrackCount2 = selectedMimeType2;
                        i2 = i + 1;
                    }
                } else {
                    int i3 = trackIndex;
                    selectedMimeTypeTrackCount = selectedMimeTypeTrackCount2;
                    i = i2;
                }
                selectedMimeTypeTrackCount2 = selectedMimeTypeTrackCount;
                i2 = i + 1;
            }
            selectedMimeTypeTrackCount = selectedMimeTypeTrackCount2;
            i = i2;
        }
        filterAdaptiveVideoTrackCountForMimeType(group, formatSupport, requiredAdaptiveSupport, selectedMimeType, maxVideoWidth, maxVideoHeight, maxVideoFrameRate, maxVideoBitrate, selectedTrackIndices);
        return selectedTrackIndices.size() < 2 ? NO_TRACKS : Util.toArray(selectedTrackIndices);
    }

    private static int getAdaptiveVideoTrackCountForMimeType(TrackGroup group, int[] formatSupport, int requiredAdaptiveSupport, @Nullable String mimeType, int maxVideoWidth, int maxVideoHeight, int maxVideoFrameRate, int maxVideoBitrate, List<Integer> selectedTrackIndices) {
        TrackGroup trackGroup;
        int adaptiveTrackCount = 0;
        for (int i = 0; i < selectedTrackIndices.size(); i++) {
            int trackIndex = ((Integer) selectedTrackIndices.get(i)).intValue();
            trackGroup = group;
            if (isSupportedAdaptiveVideoTrack(group.getFormat(trackIndex), mimeType, formatSupport[trackIndex], requiredAdaptiveSupport, maxVideoWidth, maxVideoHeight, maxVideoFrameRate, maxVideoBitrate)) {
                adaptiveTrackCount++;
            }
        }
        trackGroup = group;
        List<Integer> list = selectedTrackIndices;
        return adaptiveTrackCount;
    }

    private static void filterAdaptiveVideoTrackCountForMimeType(TrackGroup group, int[] formatSupport, int requiredAdaptiveSupport, @Nullable String mimeType, int maxVideoWidth, int maxVideoHeight, int maxVideoFrameRate, int maxVideoBitrate, List<Integer> selectedTrackIndices) {
        TrackGroup trackGroup;
        List<Integer> list = selectedTrackIndices;
        for (int i = selectedTrackIndices.size() - 1; i >= 0; i--) {
            int trackIndex = ((Integer) list.get(i)).intValue();
            trackGroup = group;
            if (!isSupportedAdaptiveVideoTrack(group.getFormat(trackIndex), mimeType, formatSupport[trackIndex], requiredAdaptiveSupport, maxVideoWidth, maxVideoHeight, maxVideoFrameRate, maxVideoBitrate)) {
                list.remove(i);
            }
        }
        trackGroup = group;
    }

    private static boolean isSupportedAdaptiveVideoTrack(Format format, @Nullable String mimeType, int formatSupport, int requiredAdaptiveSupport, int maxVideoWidth, int maxVideoHeight, int maxVideoFrameRate, int maxVideoBitrate) {
        if (isSupported(formatSupport, false) && (formatSupport & requiredAdaptiveSupport) != 0) {
            if (mimeType == null) {
                return true;
            }
            if (Util.areEqual(format.sampleMimeType, mimeType)) {
                if ((format.width == -1 || format.width <= maxVideoWidth) && ((format.height == -1 || format.height <= maxVideoHeight) && ((format.frameRate == -1.0f || format.frameRate <= ((float) maxVideoFrameRate)) && (format.bitrate == -1 || format.bitrate <= maxVideoBitrate)))) {
                    return true;
                }
            }
        }
        return false;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @android.support.annotation.Nullable
    private static com.google.android.exoplayer2.trackselection.TrackSelection selectFixedVideoTrack(com.google.android.exoplayer2.source.TrackGroupArray r20, int[][] r21, com.google.android.exoplayer2.trackselection.DefaultTrackSelector.Parameters r22) {
        /*
        r0 = r20;
        r1 = r22;
        r2 = 0;
        r3 = 0;
        r4 = 0;
        r5 = -1;
        r6 = -1;
        r7 = 0;
    L_0x000a:
        r8 = r0.length;
        if (r7 >= r8) goto L_0x00ee;
    L_0x000e:
        r8 = r0.get(r7);
        r9 = r1.viewportWidth;
        r10 = r1.viewportHeight;
        r11 = r1.viewportOrientationMayChange;
        r9 = getViewportFilteredTrackIndices(r8, r9, r10, r11);
        r10 = r21[r7];
        r11 = 0;
    L_0x001f:
        r12 = r8.length;
        if (r11 >= r12) goto L_0x00e8;
    L_0x0023:
        r12 = r10[r11];
        r13 = r1.exceedRendererCapabilitiesIfNecessary;
        r12 = isSupported(r12, r13);
        if (r12 == 0) goto L_0x00e1;
    L_0x002d:
        r12 = r8.getFormat(r11);
        r13 = java.lang.Integer.valueOf(r11);
        r13 = r9.contains(r13);
        if (r13 == 0) goto L_0x006e;
    L_0x003c:
        r13 = r12.width;
        r15 = -1;
        if (r13 == r15) goto L_0x0047;
    L_0x0041:
        r13 = r12.width;
        r14 = r1.maxVideoWidth;
        if (r13 > r14) goto L_0x006e;
    L_0x0047:
        r13 = r12.height;
        if (r13 == r15) goto L_0x0051;
    L_0x004b:
        r13 = r12.height;
        r14 = r1.maxVideoHeight;
        if (r13 > r14) goto L_0x006e;
    L_0x0051:
        r13 = r12.frameRate;
        r14 = -1082130432; // 0xffffffffbf800000 float:-1.0 double:NaN;
        r13 = (r13 > r14 ? 1 : (r13 == r14 ? 0 : -1));
        if (r13 == 0) goto L_0x0062;
    L_0x0059:
        r13 = r12.frameRate;
        r14 = r1.maxVideoFrameRate;
        r14 = (float) r14;
        r13 = (r13 > r14 ? 1 : (r13 == r14 ? 0 : -1));
        if (r13 > 0) goto L_0x006e;
    L_0x0062:
        r13 = r12.bitrate;
        if (r13 == r15) goto L_0x006c;
    L_0x0066:
        r13 = r12.bitrate;
        r14 = r1.maxVideoBitrate;
        if (r13 > r14) goto L_0x006e;
    L_0x006c:
        r13 = 1;
        goto L_0x006f;
    L_0x006e:
        r13 = 0;
    L_0x006f:
        if (r13 != 0) goto L_0x0077;
    L_0x0071:
        r14 = r1.exceedVideoConstraintsIfNecessary;
        if (r14 != 0) goto L_0x0077;
    L_0x0075:
        goto L_0x00e2;
        if (r13 == 0) goto L_0x007c;
    L_0x007a:
        r15 = 2;
        goto L_0x007d;
    L_0x007c:
        r15 = 1;
    L_0x007d:
        r14 = r15;
        r15 = r10[r11];
        r0 = 0;
        r15 = isSupported(r15, r0);
        if (r15 == 0) goto L_0x008a;
    L_0x0087:
        r14 = r14 + 1000;
        goto L_0x008b;
    L_0x008b:
        if (r14 <= r4) goto L_0x0090;
    L_0x008d:
        r17 = 1;
        goto L_0x0092;
    L_0x0090:
        r17 = 0;
    L_0x0092:
        if (r14 != r4) goto L_0x00d3;
    L_0x0094:
        r0 = r1.forceLowestBitrate;
        if (r0 == 0) goto L_0x00a8;
    L_0x0098:
        r0 = r12.bitrate;
        r0 = compareFormatValues(r0, r5);
        if (r0 >= 0) goto L_0x00a3;
    L_0x00a0:
        r16 = 1;
        goto L_0x00a5;
    L_0x00a3:
        r16 = 0;
    L_0x00a5:
        r17 = r16;
        goto L_0x00d4;
    L_0x00a8:
        r0 = r12.getPixelCount();
        if (r0 == r6) goto L_0x00b9;
    L_0x00ae:
        r18 = compareFormatValues(r0, r6);
        r19 = r18;
        r18 = r0;
        r0 = r19;
        goto L_0x00c1;
    L_0x00b9:
        r18 = r0;
        r0 = r12.bitrate;
        r0 = compareFormatValues(r0, r5);
        if (r15 == 0) goto L_0x00c9;
    L_0x00c4:
        if (r13 == 0) goto L_0x00c9;
    L_0x00c6:
        if (r0 <= 0) goto L_0x00ce;
    L_0x00c8:
        goto L_0x00cb;
    L_0x00c9:
        if (r0 >= 0) goto L_0x00ce;
    L_0x00cb:
        r16 = 1;
        goto L_0x00d0;
    L_0x00ce:
        r16 = 0;
    L_0x00d0:
        r17 = r16;
        goto L_0x00d4;
    L_0x00d4:
        if (r17 == 0) goto L_0x00e0;
    L_0x00d6:
        r2 = r8;
        r3 = r11;
        r4 = r14;
        r5 = r12.bitrate;
        r6 = r12.getPixelCount();
        goto L_0x00e2;
    L_0x00e0:
        goto L_0x00e2;
    L_0x00e2:
        r11 = r11 + 1;
        r0 = r20;
        goto L_0x001f;
    L_0x00e8:
        r7 = r7 + 1;
        r0 = r20;
        goto L_0x000a;
    L_0x00ee:
        if (r2 != 0) goto L_0x00f2;
    L_0x00f0:
        r0 = 0;
        goto L_0x00f7;
    L_0x00f2:
        r0 = new com.google.android.exoplayer2.trackselection.FixedTrackSelection;
        r0.<init>(r2, r3);
    L_0x00f7:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.trackselection.DefaultTrackSelector.selectFixedVideoTrack(com.google.android.exoplayer2.source.TrackGroupArray, int[][], com.google.android.exoplayer2.trackselection.DefaultTrackSelector$Parameters):com.google.android.exoplayer2.trackselection.TrackSelection");
    }

    @Nullable
    protected Pair<TrackSelection, AudioTrackScore> selectAudioTrack(TrackGroupArray groups, int[][] formatSupports, int mixedMimeTypeAdaptationSupports, Parameters params, @Nullable Factory adaptiveTrackSelectionFactory) throws ExoPlaybackException {
        int selectedTrackIndex = -1;
        int selectedGroupIndex = -1;
        AudioTrackScore selectedTrackScore = null;
        for (int groupIndex = 0; groupIndex < groups.length; groupIndex++) {
            TrackGroup trackGroup = groups.get(groupIndex);
            int[] trackFormatSupport = formatSupports[groupIndex];
            for (int trackIndex = 0; trackIndex < trackGroup.length; trackIndex++) {
                if (isSupported(trackFormatSupport[trackIndex], params.exceedRendererCapabilitiesIfNecessary)) {
                    AudioTrackScore trackScore = new AudioTrackScore(trackGroup.getFormat(trackIndex), params, trackFormatSupport[trackIndex]);
                    if (selectedTrackScore != null) {
                        if (trackScore.compareTo(selectedTrackScore) > 0) {
                        }
                    }
                    selectedGroupIndex = groupIndex;
                    selectedTrackIndex = trackIndex;
                    selectedTrackScore = trackScore;
                }
            }
        }
        if (selectedGroupIndex == -1) {
            return null;
        }
        TrackGroup selectedGroup = groups.get(selectedGroupIndex);
        TrackSelection selection = null;
        if (!params.forceHighestSupportedBitrate && !params.forceLowestBitrate && adaptiveTrackSelectionFactory != null) {
            trackFormatSupport = getAdaptiveAudioTracks(selectedGroup, formatSupports[selectedGroupIndex], params.allowMixedMimeAdaptiveness);
            if (trackFormatSupport.length > 0) {
                selection = adaptiveTrackSelectionFactory.createTrackSelection(selectedGroup, getBandwidthMeter(), trackFormatSupport);
            }
        }
        if (selection == null) {
            selection = new FixedTrackSelection(selectedGroup, selectedTrackIndex);
        }
        return Pair.create(selection, Assertions.checkNotNull(selectedTrackScore));
    }

    private static int[] getAdaptiveAudioTracks(TrackGroup group, int[] formatSupport, boolean allowMixedMimeTypes) {
        int configurationCount;
        int selectedConfigurationTrackCount = 0;
        AudioConfigurationTuple selectedConfiguration = null;
        HashSet<AudioConfigurationTuple> seenConfigurationTuples = new HashSet();
        for (int i = 0; i < group.length; i++) {
            Format format = group.getFormat(i);
            AudioConfigurationTuple configuration = new AudioConfigurationTuple(format.channelCount, format.sampleRate, allowMixedMimeTypes ? null : format.sampleMimeType);
            if (seenConfigurationTuples.add(configuration)) {
                configurationCount = getAdaptiveAudioTrackCount(group, formatSupport, configuration);
                if (configurationCount > selectedConfigurationTrackCount) {
                    selectedConfiguration = configuration;
                    selectedConfigurationTrackCount = configurationCount;
                }
            }
        }
        if (selectedConfigurationTrackCount <= 1) {
            return NO_TRACKS;
        }
        int[] adaptiveIndices = new int[selectedConfigurationTrackCount];
        int index = 0;
        for (int i2 = 0; i2 < group.length; i2++) {
            if (isSupportedAdaptiveAudioTrack(group.getFormat(i2), formatSupport[i2], (AudioConfigurationTuple) Assertions.checkNotNull(selectedConfiguration))) {
                configurationCount = index + 1;
                adaptiveIndices[index] = i2;
                index = configurationCount;
            }
        }
        return adaptiveIndices;
    }

    private static int getAdaptiveAudioTrackCount(TrackGroup group, int[] formatSupport, AudioConfigurationTuple configuration) {
        int count = 0;
        for (int i = 0; i < group.length; i++) {
            if (isSupportedAdaptiveAudioTrack(group.getFormat(i), formatSupport[i], configuration)) {
                count++;
            }
        }
        return count;
    }

    private static boolean isSupportedAdaptiveAudioTrack(Format format, int formatSupport, AudioConfigurationTuple configuration) {
        if (isSupported(formatSupport, false) && format.channelCount == configuration.channelCount && format.sampleRate == configuration.sampleRate) {
            if (configuration.mimeType != null) {
                if (TextUtils.equals(configuration.mimeType, format.sampleMimeType)) {
                }
            }
            return true;
        }
        return false;
    }

    @Nullable
    protected Pair<TrackSelection, Integer> selectTextTrack(TrackGroupArray groups, int[][] formatSupport, Parameters params) throws ExoPlaybackException {
        Pair<TrackSelection, Integer> pair;
        TrackGroupArray trackGroupArray = groups;
        Parameters parameters = params;
        TrackGroup selectedGroup = null;
        int selectedTrackIndex = 0;
        int selectedTrackScore = 0;
        int groupIndex = 0;
        while (groupIndex < trackGroupArray.length) {
            TrackGroup trackGroup = trackGroupArray.get(groupIndex);
            int[] trackFormatSupport = formatSupport[groupIndex];
            int trackIndex = 0;
            while (trackIndex < trackGroup.length) {
                if (isSupported(trackFormatSupport[trackIndex], parameters.exceedRendererCapabilitiesIfNecessary)) {
                    int trackScore;
                    Format format = trackGroup.getFormat(trackIndex);
                    int maskedSelectionFlags = format.selectionFlags & (parameters.disabledTextTrackSelectionFlags ^ -1);
                    boolean isForced = true;
                    boolean isDefault = (maskedSelectionFlags & 1) != 0;
                    if ((maskedSelectionFlags & 2) == 0) {
                        isForced = false;
                    }
                    boolean preferredLanguageFound = formatHasLanguage(format, parameters.preferredTextLanguage);
                    if (!preferredLanguageFound) {
                        if (parameters.selectUndeterminedTextLanguage) {
                            if (formatHasNoLanguage(format)) {
                            }
                        }
                        if (isDefault) {
                            trackScore = 3;
                        } else if (isForced) {
                            if (formatHasLanguage(format, parameters.preferredAudioLanguage)) {
                                trackScore = 2;
                            } else {
                                trackScore = 1;
                            }
                        }
                        if (isSupported(trackFormatSupport[trackIndex], false)) {
                            trackScore += 1000;
                        }
                        if (trackScore > selectedTrackScore) {
                            selectedGroup = trackGroup;
                            selectedTrackIndex = trackIndex;
                            selectedTrackScore = trackScore;
                        }
                    }
                    if (isDefault) {
                        trackScore = 8;
                    } else if (isForced) {
                        trackScore = 4;
                    } else {
                        trackScore = 6;
                    }
                    trackScore += preferredLanguageFound;
                    if (isSupported(trackFormatSupport[trackIndex], false)) {
                        trackScore += 1000;
                    }
                    if (trackScore > selectedTrackScore) {
                        selectedGroup = trackGroup;
                        selectedTrackIndex = trackIndex;
                        selectedTrackScore = trackScore;
                    }
                }
                trackIndex++;
                trackGroupArray = groups;
            }
            groupIndex++;
            trackGroupArray = groups;
        }
        if (selectedGroup == null) {
            pair = null;
        } else {
            pair = Pair.create(new FixedTrackSelection(selectedGroup, selectedTrackIndex), Integer.valueOf(selectedTrackScore));
        }
        return pair;
    }

    @Nullable
    protected TrackSelection selectOtherTrack(int trackType, TrackGroupArray groups, int[][] formatSupport, Parameters params) throws ExoPlaybackException {
        Parameters parameters;
        TrackGroupArray trackGroupArray = groups;
        TrackGroup selectedGroup = null;
        int selectedTrackIndex = 0;
        int selectedTrackScore = 0;
        for (int groupIndex = 0; groupIndex < trackGroupArray.length; groupIndex++) {
            TrackGroup trackGroup = trackGroupArray.get(groupIndex);
            int[] trackFormatSupport = formatSupport[groupIndex];
            for (int trackIndex = 0; trackIndex < trackGroup.length; trackIndex++) {
                if (isSupported(trackFormatSupport[trackIndex], params.exceedRendererCapabilitiesIfNecessary)) {
                    int trackScore = 1;
                    if ((trackGroup.getFormat(trackIndex).selectionFlags & 1) != 0) {
                        trackScore = 2;
                    }
                    if (isSupported(trackFormatSupport[trackIndex], false)) {
                        trackScore += 1000;
                    }
                    if (trackScore > selectedTrackScore) {
                        selectedGroup = trackGroup;
                        selectedTrackIndex = trackIndex;
                        selectedTrackScore = trackScore;
                    }
                }
            }
            parameters = params;
        }
        parameters = params;
        return selectedGroup == null ? null : new FixedTrackSelection(selectedGroup, selectedTrackIndex);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void maybeConfigureRenderersForTunneling(com.google.android.exoplayer2.trackselection.MappingTrackSelector.MappedTrackInfo r10, int[][][] r11, com.google.android.exoplayer2.RendererConfiguration[] r12, com.google.android.exoplayer2.trackselection.TrackSelection[] r13, int r14) {
        /*
        if (r14 != 0) goto L_0x0003;
    L_0x0002:
        return;
    L_0x0003:
        r0 = -1;
        r1 = -1;
        r2 = 1;
        r3 = 0;
    L_0x0007:
        r4 = r10.getRendererCount();
        r5 = 1;
        r6 = -1;
        if (r3 >= r4) goto L_0x003b;
    L_0x000f:
        r4 = r10.getRendererType(r3);
        r7 = r13[r3];
        if (r4 == r5) goto L_0x001a;
    L_0x0017:
        r8 = 2;
        if (r4 != r8) goto L_0x0037;
    L_0x001a:
        if (r7 == 0) goto L_0x0037;
    L_0x001c:
        r8 = r11[r3];
        r9 = r10.getTrackGroups(r3);
        r8 = rendererSupportsTunneling(r8, r9, r7);
        if (r8 == 0) goto L_0x0036;
    L_0x0028:
        if (r4 != r5) goto L_0x0030;
    L_0x002a:
        if (r0 == r6) goto L_0x002e;
    L_0x002c:
        r2 = 0;
        goto L_0x003b;
    L_0x002e:
        r0 = r3;
        goto L_0x0038;
    L_0x0030:
        if (r1 == r6) goto L_0x0034;
    L_0x0032:
        r2 = 0;
        goto L_0x003b;
    L_0x0034:
        r1 = r3;
        goto L_0x0038;
    L_0x0036:
        goto L_0x0038;
    L_0x0038:
        r3 = r3 + 1;
        goto L_0x0007;
    L_0x003b:
        if (r0 == r6) goto L_0x0040;
    L_0x003d:
        if (r1 == r6) goto L_0x0040;
    L_0x003f:
        goto L_0x0041;
    L_0x0040:
        r5 = 0;
    L_0x0041:
        r2 = r2 & r5;
        if (r2 == 0) goto L_0x004e;
    L_0x0044:
        r3 = new com.google.android.exoplayer2.RendererConfiguration;
        r3.<init>(r14);
        r12[r0] = r3;
        r12[r1] = r3;
        goto L_0x004f;
    L_0x004f:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.trackselection.DefaultTrackSelector.maybeConfigureRenderersForTunneling(com.google.android.exoplayer2.trackselection.MappingTrackSelector$MappedTrackInfo, int[][][], com.google.android.exoplayer2.RendererConfiguration[], com.google.android.exoplayer2.trackselection.TrackSelection[], int):void");
    }

    private static boolean rendererSupportsTunneling(int[][] formatSupports, TrackGroupArray trackGroups, TrackSelection selection) {
        if (selection == null) {
            return false;
        }
        int trackGroupIndex = trackGroups.indexOf(selection.getTrackGroup());
        for (int i = 0; i < selection.length(); i++) {
            if ((formatSupports[trackGroupIndex][selection.getIndexInTrackGroup(i)] & 32) != 32) {
                return false;
            }
        }
        return true;
    }

    private static int compareFormatValues(int first, int second) {
        return first == -1 ? second == -1 ? 0 : -1 : second == -1 ? 1 : first - second;
    }

    protected static boolean isSupported(int formatSupport, boolean allowExceedsCapabilities) {
        int maskedSupport = formatSupport & 7;
        if (maskedSupport != 4) {
            if (!allowExceedsCapabilities || maskedSupport != 3) {
                return false;
            }
        }
        return true;
    }

    protected static boolean formatHasNoLanguage(Format format) {
        if (!TextUtils.isEmpty(format.language)) {
            if (!formatHasLanguage(format, C0555C.LANGUAGE_UNDETERMINED)) {
                return false;
            }
        }
        return true;
    }

    protected static boolean formatHasLanguage(Format format, @Nullable String language) {
        if (language != null) {
            if (TextUtils.equals(language, Util.normalizeLanguageCode(format.language))) {
                return true;
            }
        }
        return false;
    }

    private static List<Integer> getViewportFilteredTrackIndices(TrackGroup group, int viewportWidth, int viewportHeight, boolean orientationMayChange) {
        int i;
        ArrayList<Integer> selectedTrackIndices = new ArrayList(group.length);
        for (i = 0; i < group.length; i++) {
            selectedTrackIndices.add(Integer.valueOf(i));
        }
        if (viewportWidth != Integer.MAX_VALUE) {
            if (viewportHeight != Integer.MAX_VALUE) {
                int maxVideoPixelsToRetain = Integer.MAX_VALUE;
                for (int i2 = 0; i2 < group.length; i2++) {
                    Format format = group.getFormat(i2);
                    if (format.width > 0 && format.height > 0) {
                        Point maxVideoSizeInViewport = getMaxVideoSizeInViewport(orientationMayChange, viewportWidth, viewportHeight, format.width, format.height);
                        int videoPixels = format.width * format.height;
                        if (format.width >= ((int) (((float) maxVideoSizeInViewport.x) * FRACTION_TO_CONSIDER_FULLSCREEN)) && format.height >= ((int) (((float) maxVideoSizeInViewport.y) * FRACTION_TO_CONSIDER_FULLSCREEN)) && videoPixels < maxVideoPixelsToRetain) {
                            maxVideoPixelsToRetain = videoPixels;
                        }
                    }
                }
                if (maxVideoPixelsToRetain != Integer.MAX_VALUE) {
                    for (i = selectedTrackIndices.size() - 1; i >= 0; i--) {
                        int pixelCount = group.getFormat(((Integer) selectedTrackIndices.get(i)).intValue()).getPixelCount();
                        if (pixelCount != -1) {
                            if (pixelCount <= maxVideoPixelsToRetain) {
                            }
                        }
                        selectedTrackIndices.remove(i);
                    }
                }
                return selectedTrackIndices;
            }
        }
        return selectedTrackIndices;
    }

    private static Point getMaxVideoSizeInViewport(boolean orientationMayChange, int viewportWidth, int viewportHeight, int videoWidth, int videoHeight) {
        if (orientationMayChange) {
            Object obj = 1;
            Object obj2 = videoWidth > videoHeight ? 1 : null;
            if (viewportWidth <= viewportHeight) {
                obj = null;
            }
            if (obj2 != obj) {
                int tempViewportWidth = viewportWidth;
                viewportWidth = viewportHeight;
                viewportHeight = tempViewportWidth;
                if (videoWidth * viewportHeight < videoHeight * viewportWidth) {
                    return new Point(viewportWidth, Util.ceilDivide(viewportWidth * videoHeight, videoWidth));
                }
                return new Point(Util.ceilDivide(viewportHeight * videoWidth, videoHeight), viewportHeight);
            }
        }
        if (videoWidth * viewportHeight < videoHeight * viewportWidth) {
            return new Point(Util.ceilDivide(viewportHeight * videoWidth, videoHeight), viewportHeight);
        }
        return new Point(viewportWidth, Util.ceilDivide(viewportWidth * videoHeight, videoWidth));
    }

    private static int compareInts(int first, int second) {
        if (first > second) {
            return 1;
        }
        return second > first ? -1 : 0;
    }
}
