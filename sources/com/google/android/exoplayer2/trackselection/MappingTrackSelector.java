package com.google.android.exoplayer2.trackselection;

import android.support.annotation.Nullable;
import android.util.Pair;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.RendererCapabilities;
import com.google.android.exoplayer2.RendererConfiguration;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.util.Util;
import java.util.Arrays;

public abstract class MappingTrackSelector extends TrackSelector {
    @Nullable
    private MappedTrackInfo currentMappedTrackInfo;

    public static final class MappedTrackInfo {
        public static final int RENDERER_SUPPORT_EXCEEDS_CAPABILITIES_TRACKS = 2;
        public static final int RENDERER_SUPPORT_NO_TRACKS = 0;
        public static final int RENDERER_SUPPORT_PLAYABLE_TRACKS = 3;
        public static final int RENDERER_SUPPORT_UNSUPPORTED_TRACKS = 1;
        @Deprecated
        public final int length = this.rendererCount;
        private final int rendererCount;
        private final int[][][] rendererFormatSupports;
        private final int[] rendererMixedMimeTypeAdaptiveSupports;
        private final TrackGroupArray[] rendererTrackGroups;
        private final int[] rendererTrackTypes;
        private final TrackGroupArray unmappedTrackGroups;

        MappedTrackInfo(int[] rendererTrackTypes, TrackGroupArray[] rendererTrackGroups, int[] rendererMixedMimeTypeAdaptiveSupports, int[][][] rendererFormatSupports, TrackGroupArray unmappedTrackGroups) {
            this.rendererTrackTypes = rendererTrackTypes;
            this.rendererTrackGroups = rendererTrackGroups;
            this.rendererFormatSupports = rendererFormatSupports;
            this.rendererMixedMimeTypeAdaptiveSupports = rendererMixedMimeTypeAdaptiveSupports;
            this.unmappedTrackGroups = unmappedTrackGroups;
            this.rendererCount = rendererTrackTypes.length;
        }

        public int getRendererCount() {
            return this.rendererCount;
        }

        public int getRendererType(int rendererIndex) {
            return this.rendererTrackTypes[rendererIndex];
        }

        public TrackGroupArray getTrackGroups(int rendererIndex) {
            return this.rendererTrackGroups[rendererIndex];
        }

        public int getRendererSupport(int rendererIndex) {
            int bestRendererSupport = 0;
            int[][] rendererFormatSupport = this.rendererFormatSupports[rendererIndex];
            for (int i = 0; i < rendererFormatSupport.length; i++) {
                for (int i2 : rendererFormatSupport[i]) {
                    int i22;
                    switch (i22 & 7) {
                        case 3:
                            i22 = 2;
                            break;
                        case 4:
                            return 3;
                        default:
                            i22 = 1;
                            break;
                    }
                    bestRendererSupport = Math.max(bestRendererSupport, i22);
                }
            }
            return bestRendererSupport;
        }

        @Deprecated
        public int getTrackTypeRendererSupport(int trackType) {
            return getTypeSupport(trackType);
        }

        public int getTypeSupport(int trackType) {
            int bestRendererSupport = 0;
            for (int i = 0; i < this.rendererCount; i++) {
                if (this.rendererTrackTypes[i] == trackType) {
                    bestRendererSupport = Math.max(bestRendererSupport, getRendererSupport(i));
                }
            }
            return bestRendererSupport;
        }

        @Deprecated
        public int getTrackFormatSupport(int rendererIndex, int groupIndex, int trackIndex) {
            return getTrackSupport(rendererIndex, groupIndex, trackIndex);
        }

        public int getTrackSupport(int rendererIndex, int groupIndex, int trackIndex) {
            return this.rendererFormatSupports[rendererIndex][groupIndex][trackIndex] & 7;
        }

        public int getAdaptiveSupport(int rendererIndex, int groupIndex, boolean includeCapabilitiesExceededTracks) {
            int trackCount = this.rendererTrackGroups[rendererIndex].get(groupIndex).length;
            int[] trackIndices = new int[trackCount];
            int trackIndexCount = 0;
            for (int i = 0; i < trackCount; i++) {
                int fixedSupport = getTrackSupport(rendererIndex, groupIndex, i);
                if (fixedSupport != 4) {
                    if (!includeCapabilitiesExceededTracks || fixedSupport != 3) {
                    }
                }
                int trackIndexCount2 = trackIndexCount + 1;
                trackIndices[trackIndexCount] = i;
                trackIndexCount = trackIndexCount2;
            }
            return getAdaptiveSupport(rendererIndex, groupIndex, Arrays.copyOf(trackIndices, trackIndexCount));
        }

        public int getAdaptiveSupport(int rendererIndex, int groupIndex, int[] trackIndices) {
            int handledTrackCount = 0;
            int adaptiveSupport = 16;
            boolean multipleMimeTypes = false;
            String firstSampleMimeType = null;
            int i = 0;
            while (i < trackIndices.length) {
                String sampleMimeType = this.rendererTrackGroups[rendererIndex].get(groupIndex).getFormat(trackIndices[i]).sampleMimeType;
                int handledTrackCount2 = handledTrackCount + 1;
                if (handledTrackCount == 0) {
                    firstSampleMimeType = sampleMimeType;
                } else {
                    multipleMimeTypes = (Util.areEqual(firstSampleMimeType, sampleMimeType) ^ 1) | multipleMimeTypes;
                }
                adaptiveSupport = Math.min(adaptiveSupport, this.rendererFormatSupports[rendererIndex][groupIndex][i] & 24);
                i++;
                handledTrackCount = handledTrackCount2;
            }
            return multipleMimeTypes ? Math.min(adaptiveSupport, this.rendererMixedMimeTypeAdaptiveSupports[rendererIndex]) : adaptiveSupport;
        }

        @Deprecated
        public TrackGroupArray getUnassociatedTrackGroups() {
            return getUnmappedTrackGroups();
        }

        public TrackGroupArray getUnmappedTrackGroups() {
            return this.unmappedTrackGroups;
        }
    }

    protected abstract Pair<RendererConfiguration[], TrackSelection[]> selectTracks(MappedTrackInfo mappedTrackInfo, int[][][] iArr, int[] iArr2) throws ExoPlaybackException;

    @Nullable
    public final MappedTrackInfo getCurrentMappedTrackInfo() {
        return this.currentMappedTrackInfo;
    }

    public final void onSelectionActivated(Object info) {
        this.currentMappedTrackInfo = (MappedTrackInfo) info;
    }

    public final TrackSelectorResult selectTracks(RendererCapabilities[] rendererCapabilities, TrackGroupArray trackGroups) throws ExoPlaybackException {
        int i;
        int[] rendererFormatSupport;
        RendererCapabilities[] rendererCapabilitiesArr = rendererCapabilities;
        TrackGroupArray trackGroupArray = trackGroups;
        int[] rendererTrackGroupCounts = new int[(rendererCapabilitiesArr.length + 1)];
        TrackGroup[][] rendererTrackGroups = new TrackGroup[(rendererCapabilitiesArr.length + 1)][];
        int[][][] rendererFormatSupports = new int[(rendererCapabilitiesArr.length + 1)][][];
        for (i = 0; i < rendererTrackGroups.length; i++) {
            rendererTrackGroups[i] = new TrackGroup[trackGroupArray.length];
            rendererFormatSupports[i] = new int[trackGroupArray.length][];
        }
        int[] rendererMixedMimeTypeAdaptationSupports = getMixedMimeTypeAdaptationSupports(rendererCapabilities);
        for (i = 0; i < trackGroupArray.length; i++) {
            TrackGroup group = trackGroupArray.get(i);
            int rendererIndex = findRenderer(rendererCapabilitiesArr, group);
            if (rendererIndex == rendererCapabilitiesArr.length) {
                rendererFormatSupport = new int[group.length];
            } else {
                rendererFormatSupport = getFormatSupport(rendererCapabilitiesArr[rendererIndex], group);
            }
            int rendererTrackGroupCount = rendererTrackGroupCounts[rendererIndex];
            rendererTrackGroups[rendererIndex][rendererTrackGroupCount] = group;
            rendererFormatSupports[rendererIndex][rendererTrackGroupCount] = rendererFormatSupport;
            rendererTrackGroupCounts[rendererIndex] = rendererTrackGroupCounts[rendererIndex] + 1;
        }
        TrackGroupArray[] rendererTrackGroupArrays = new TrackGroupArray[rendererCapabilitiesArr.length];
        int[] rendererTrackTypes = new int[rendererCapabilitiesArr.length];
        for (i = 0; i < rendererCapabilitiesArr.length; i++) {
            int rendererTrackGroupCount2 = rendererTrackGroupCounts[i];
            rendererTrackGroupArrays[i] = new TrackGroupArray((TrackGroup[]) Util.nullSafeArrayCopy(rendererTrackGroups[i], rendererTrackGroupCount2));
            rendererFormatSupports[i] = (int[][]) Util.nullSafeArrayCopy(rendererFormatSupports[i], rendererTrackGroupCount2);
            rendererTrackTypes[i] = rendererCapabilitiesArr[i].getTrackType();
        }
        int[] iArr = rendererTrackTypes;
        TrackGroupArray[] trackGroupArrayArr = rendererTrackGroupArrays;
        rendererFormatSupport = rendererMixedMimeTypeAdaptationSupports;
        int[][][] iArr2 = rendererFormatSupports;
        MappedTrackInfo mappedTrackInfo = new MappedTrackInfo(iArr, trackGroupArrayArr, rendererFormatSupport, iArr2, new TrackGroupArray((TrackGroup[]) Util.nullSafeArrayCopy(rendererTrackGroups[rendererCapabilitiesArr.length], rendererTrackGroupCounts[rendererCapabilitiesArr.length])));
        Pair<RendererConfiguration[], TrackSelection[]> result = selectTracks(mappedTrackInfo, rendererFormatSupports, rendererMixedMimeTypeAdaptationSupports);
        return new TrackSelectorResult((RendererConfiguration[]) result.first, (TrackSelection[]) result.second, mappedTrackInfo);
    }

    private static int findRenderer(RendererCapabilities[] rendererCapabilities, TrackGroup group) throws ExoPlaybackException {
        int bestRendererIndex = rendererCapabilities.length;
        int bestFormatSupportLevel = 0;
        for (int rendererIndex = 0; rendererIndex < rendererCapabilities.length; rendererIndex++) {
            RendererCapabilities rendererCapability = rendererCapabilities[rendererIndex];
            for (int trackIndex = 0; trackIndex < group.length; trackIndex++) {
                int formatSupportLevel = rendererCapability.supportsFormat(group.getFormat(trackIndex)) & 7;
                if (formatSupportLevel > bestFormatSupportLevel) {
                    bestRendererIndex = rendererIndex;
                    bestFormatSupportLevel = formatSupportLevel;
                    if (bestFormatSupportLevel == 4) {
                        return bestRendererIndex;
                    }
                }
            }
        }
        return bestRendererIndex;
    }

    private static int[] getFormatSupport(RendererCapabilities rendererCapabilities, TrackGroup group) throws ExoPlaybackException {
        int[] formatSupport = new int[group.length];
        for (int i = 0; i < group.length; i++) {
            formatSupport[i] = rendererCapabilities.supportsFormat(group.getFormat(i));
        }
        return formatSupport;
    }

    private static int[] getMixedMimeTypeAdaptationSupports(RendererCapabilities[] rendererCapabilities) throws ExoPlaybackException {
        int[] mixedMimeTypeAdaptationSupport = new int[rendererCapabilities.length];
        for (int i = 0; i < mixedMimeTypeAdaptationSupport.length; i++) {
            mixedMimeTypeAdaptationSupport[i] = rendererCapabilities[i].supportsMixedMimeTypeAdaptation();
        }
        return mixedMimeTypeAdaptationSupport;
    }
}
