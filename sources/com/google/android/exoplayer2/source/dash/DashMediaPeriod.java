package com.google.android.exoplayer2.source.dash;

import android.support.annotation.Nullable;
import android.util.Pair;
import android.util.SparseIntArray;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.source.CompositeSequenceableLoaderFactory;
import com.google.android.exoplayer2.source.EmptySampleStream;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaSourceEventListener$EventDispatcher;
import com.google.android.exoplayer2.source.SampleStream;
import com.google.android.exoplayer2.source.SequenceableLoader;
import com.google.android.exoplayer2.source.SequenceableLoader.Callback;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.chunk.ChunkSampleStream;
import com.google.android.exoplayer2.source.chunk.ChunkSampleStream.EmbeddedSampleStream;
import com.google.android.exoplayer2.source.chunk.ChunkSampleStream.ReleaseCallback;
import com.google.android.exoplayer2.source.chunk.ChunkSource;
import com.google.android.exoplayer2.source.dash.DashChunkSource.Factory;
import com.google.android.exoplayer2.source.dash.PlayerEmsgHandler.PlayerEmsgCallback;
import com.google.android.exoplayer2.source.dash.PlayerEmsgHandler.PlayerTrackEmsgHandler;
import com.google.android.exoplayer2.source.dash.manifest.AdaptationSet;
import com.google.android.exoplayer2.source.dash.manifest.DashManifest;
import com.google.android.exoplayer2.source.dash.manifest.Descriptor;
import com.google.android.exoplayer2.source.dash.manifest.EventStream;
import com.google.android.exoplayer2.source.dash.manifest.Period;
import com.google.android.exoplayer2.source.dash.manifest.Representation;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.LoaderErrorThrower;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.MimeTypes;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;

final class DashMediaPeriod implements MediaPeriod, Callback<ChunkSampleStream<DashChunkSource>>, ReleaseCallback<DashChunkSource> {
    private final Allocator allocator;
    @Nullable
    private MediaPeriod.Callback callback;
    private final Factory chunkSourceFactory;
    private SequenceableLoader compositeSequenceableLoader;
    private final CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory;
    private final long elapsedRealtimeOffset;
    private final MediaSourceEventListener$EventDispatcher eventDispatcher;
    private EventSampleStream[] eventSampleStreams = new EventSampleStream[0];
    private List<EventStream> eventStreams;
    final int id;
    private final LoadErrorHandlingPolicy loadErrorHandlingPolicy;
    private DashManifest manifest;
    private final LoaderErrorThrower manifestLoaderErrorThrower;
    private boolean notifiedReadingStarted;
    private int periodIndex;
    private final PlayerEmsgHandler playerEmsgHandler;
    private ChunkSampleStream<DashChunkSource>[] sampleStreams = newSampleStreamArray(0);
    private final IdentityHashMap<ChunkSampleStream<DashChunkSource>, PlayerTrackEmsgHandler> trackEmsgHandlerBySampleStream = new IdentityHashMap();
    private final TrackGroupInfo[] trackGroupInfos;
    private final TrackGroupArray trackGroups;
    @Nullable
    private final TransferListener transferListener;

    private static final class TrackGroupInfo {
        private static final int CATEGORY_EMBEDDED = 1;
        private static final int CATEGORY_MANIFEST_EVENTS = 2;
        private static final int CATEGORY_PRIMARY = 0;
        public final int[] adaptationSetIndices;
        public final int embeddedCea608TrackGroupIndex;
        public final int embeddedEventMessageTrackGroupIndex;
        public final int eventStreamGroupIndex;
        public final int primaryTrackGroupIndex;
        public final int trackGroupCategory;
        public final int trackType;

        public static TrackGroupInfo primaryTrack(int trackType, int[] adaptationSetIndices, int primaryTrackGroupIndex, int embeddedEventMessageTrackGroupIndex, int embeddedCea608TrackGroupIndex) {
            return new TrackGroupInfo(trackType, 0, adaptationSetIndices, primaryTrackGroupIndex, embeddedEventMessageTrackGroupIndex, embeddedCea608TrackGroupIndex, -1);
        }

        public static TrackGroupInfo embeddedEmsgTrack(int[] adaptationSetIndices, int primaryTrackGroupIndex) {
            return new TrackGroupInfo(4, 1, adaptationSetIndices, primaryTrackGroupIndex, -1, -1, -1);
        }

        public static TrackGroupInfo embeddedCea608Track(int[] adaptationSetIndices, int primaryTrackGroupIndex) {
            return new TrackGroupInfo(3, 1, adaptationSetIndices, primaryTrackGroupIndex, -1, -1, -1);
        }

        public static TrackGroupInfo mpdEventTrack(int eventStreamIndex) {
            return new TrackGroupInfo(4, 2, null, -1, -1, -1, eventStreamIndex);
        }

        private TrackGroupInfo(int trackType, int trackGroupCategory, int[] adaptationSetIndices, int primaryTrackGroupIndex, int embeddedEventMessageTrackGroupIndex, int embeddedCea608TrackGroupIndex, int eventStreamGroupIndex) {
            this.trackType = trackType;
            this.adaptationSetIndices = adaptationSetIndices;
            this.trackGroupCategory = trackGroupCategory;
            this.primaryTrackGroupIndex = primaryTrackGroupIndex;
            this.embeddedEventMessageTrackGroupIndex = embeddedEventMessageTrackGroupIndex;
            this.embeddedCea608TrackGroupIndex = embeddedCea608TrackGroupIndex;
            this.eventStreamGroupIndex = eventStreamGroupIndex;
        }
    }

    public DashMediaPeriod(int id, DashManifest manifest, int periodIndex, Factory chunkSourceFactory, @Nullable TransferListener transferListener, LoadErrorHandlingPolicy loadErrorHandlingPolicy, MediaSourceEventListener$EventDispatcher eventDispatcher, long elapsedRealtimeOffset, LoaderErrorThrower manifestLoaderErrorThrower, Allocator allocator, CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory, PlayerEmsgCallback playerEmsgCallback) {
        DashManifest dashManifest = manifest;
        Allocator allocator2 = allocator;
        CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory2 = compositeSequenceableLoaderFactory;
        this.id = id;
        this.manifest = dashManifest;
        this.periodIndex = periodIndex;
        this.chunkSourceFactory = chunkSourceFactory;
        this.transferListener = transferListener;
        this.loadErrorHandlingPolicy = loadErrorHandlingPolicy;
        this.eventDispatcher = eventDispatcher;
        this.elapsedRealtimeOffset = elapsedRealtimeOffset;
        this.manifestLoaderErrorThrower = manifestLoaderErrorThrower;
        this.allocator = allocator2;
        this.compositeSequenceableLoaderFactory = compositeSequenceableLoaderFactory2;
        this.playerEmsgHandler = new PlayerEmsgHandler(dashManifest, playerEmsgCallback, allocator2);
        this.compositeSequenceableLoader = compositeSequenceableLoaderFactory2.createCompositeSequenceableLoader(this.sampleStreams);
        Period period = manifest.getPeriod(periodIndex);
        this.eventStreams = period.eventStreams;
        Pair<TrackGroupArray, TrackGroupInfo[]> result = buildTrackGroups(period.adaptationSets, this.eventStreams);
        this.trackGroups = (TrackGroupArray) result.first;
        this.trackGroupInfos = (TrackGroupInfo[]) result.second;
        eventDispatcher.mediaPeriodCreated();
    }

    public void updateManifest(DashManifest manifest, int periodIndex) {
        this.manifest = manifest;
        this.periodIndex = periodIndex;
        this.playerEmsgHandler.updateManifest(manifest);
        ChunkSampleStream[] chunkSampleStreamArr = this.sampleStreams;
        if (chunkSampleStreamArr != null) {
            for (ChunkSampleStream<DashChunkSource> sampleStream : chunkSampleStreamArr) {
                ((DashChunkSource) sampleStream.getChunkSource()).updateManifest(manifest, periodIndex);
            }
            this.callback.onContinueLoadingRequested(this);
        }
        this.eventStreams = manifest.getPeriod(periodIndex).eventStreams;
        for (EventSampleStream eventSampleStream : this.eventSampleStreams) {
            for (EventStream eventStream : this.eventStreams) {
                if (eventStream.id().equals(eventSampleStream.eventStreamId())) {
                    boolean z = true;
                    int lastPeriodIndex = manifest.getPeriodCount() - 1;
                    if (!manifest.dynamic || periodIndex != lastPeriodIndex) {
                        z = false;
                    }
                    eventSampleStream.updateEventStream(eventStream, z);
                }
            }
        }
    }

    public void release() {
        this.playerEmsgHandler.release();
        for (ChunkSampleStream<DashChunkSource> sampleStream : this.sampleStreams) {
            sampleStream.release(this);
        }
        this.callback = null;
        this.eventDispatcher.mediaPeriodReleased();
    }

    public synchronized void onSampleStreamReleased(ChunkSampleStream<DashChunkSource> stream) {
        PlayerTrackEmsgHandler trackEmsgHandler = (PlayerTrackEmsgHandler) this.trackEmsgHandlerBySampleStream.remove(stream);
        if (trackEmsgHandler != null) {
            trackEmsgHandler.release();
        }
    }

    public void prepare(MediaPeriod.Callback callback, long positionUs) {
        this.callback = callback;
        callback.onPrepared(this);
    }

    public void maybeThrowPrepareError() throws IOException {
        this.manifestLoaderErrorThrower.maybeThrowError();
    }

    public TrackGroupArray getTrackGroups() {
        return this.trackGroups;
    }

    public long selectTracks(TrackSelection[] selections, boolean[] mayRetainStreamFlags, SampleStream[] streams, boolean[] streamResetFlags, long positionUs) {
        int[] streamIndexToTrackGroupIndex = getStreamIndexToTrackGroupIndex(selections);
        releaseDisabledStreams(selections, mayRetainStreamFlags, streams);
        releaseOrphanEmbeddedStreams(selections, streams, streamIndexToTrackGroupIndex);
        selectNewStreams(selections, streams, streamResetFlags, positionUs, streamIndexToTrackGroupIndex);
        ArrayList<ChunkSampleStream<DashChunkSource>> sampleStreamList = new ArrayList();
        ArrayList<EventSampleStream> eventSampleStreamList = new ArrayList();
        for (SampleStream sampleStream : streams) {
            if (sampleStream instanceof ChunkSampleStream) {
                sampleStreamList.add((ChunkSampleStream) sampleStream);
            } else if (sampleStream instanceof EventSampleStream) {
                eventSampleStreamList.add((EventSampleStream) sampleStream);
            }
        }
        this.sampleStreams = newSampleStreamArray(sampleStreamList.size());
        sampleStreamList.toArray(this.sampleStreams);
        this.eventSampleStreams = new EventSampleStream[eventSampleStreamList.size()];
        eventSampleStreamList.toArray(this.eventSampleStreams);
        this.compositeSequenceableLoader = this.compositeSequenceableLoaderFactory.createCompositeSequenceableLoader(this.sampleStreams);
        return positionUs;
    }

    public void discardBuffer(long positionUs, boolean toKeyframe) {
        for (ChunkSampleStream<DashChunkSource> sampleStream : this.sampleStreams) {
            sampleStream.discardBuffer(positionUs, toKeyframe);
        }
    }

    public void reevaluateBuffer(long positionUs) {
        this.compositeSequenceableLoader.reevaluateBuffer(positionUs);
    }

    public boolean continueLoading(long positionUs) {
        return this.compositeSequenceableLoader.continueLoading(positionUs);
    }

    public long getNextLoadPositionUs() {
        return this.compositeSequenceableLoader.getNextLoadPositionUs();
    }

    public long readDiscontinuity() {
        if (!this.notifiedReadingStarted) {
            this.eventDispatcher.readingStarted();
            this.notifiedReadingStarted = true;
        }
        return C0555C.TIME_UNSET;
    }

    public long getBufferedPositionUs() {
        return this.compositeSequenceableLoader.getBufferedPositionUs();
    }

    public long seekToUs(long positionUs) {
        for (ChunkSampleStream<DashChunkSource> sampleStream : this.sampleStreams) {
            sampleStream.seekToUs(positionUs);
        }
        for (EventSampleStream sampleStream2 : this.eventSampleStreams) {
            sampleStream2.seekToUs(positionUs);
        }
        return positionUs;
    }

    public long getAdjustedSeekPositionUs(long positionUs, SeekParameters seekParameters) {
        for (ChunkSampleStream<DashChunkSource> sampleStream : this.sampleStreams) {
            if (sampleStream.primaryTrackType == 2) {
                return sampleStream.getAdjustedSeekPositionUs(positionUs, seekParameters);
            }
        }
        return positionUs;
    }

    public void onContinueLoadingRequested(ChunkSampleStream<DashChunkSource> chunkSampleStream) {
        this.callback.onContinueLoadingRequested(this);
    }

    private int[] getStreamIndexToTrackGroupIndex(TrackSelection[] selections) {
        int[] streamIndexToTrackGroupIndex = new int[selections.length];
        for (int i = 0; i < selections.length; i++) {
            if (selections[i] != null) {
                streamIndexToTrackGroupIndex[i] = this.trackGroups.indexOf(selections[i].getTrackGroup());
            } else {
                streamIndexToTrackGroupIndex[i] = -1;
            }
        }
        return streamIndexToTrackGroupIndex;
    }

    private void releaseDisabledStreams(TrackSelection[] selections, boolean[] mayRetainStreamFlags, SampleStream[] streams) {
        for (int i = 0; i < selections.length; i++) {
            if (selections[i] != null) {
                if (mayRetainStreamFlags[i]) {
                }
            }
            if (streams[i] instanceof ChunkSampleStream) {
                streams[i].release(this);
            } else if (streams[i] instanceof EmbeddedSampleStream) {
                ((EmbeddedSampleStream) streams[i]).release();
                streams[i] = null;
            }
            streams[i] = null;
        }
    }

    private void releaseOrphanEmbeddedStreams(TrackSelection[] selections, SampleStream[] streams, int[] streamIndexToTrackGroupIndex) {
        for (int i = 0; i < selections.length; i++) {
            if (!(streams[i] instanceof EmptySampleStream)) {
                if (!(streams[i] instanceof EmbeddedSampleStream)) {
                }
            }
            int primaryStreamIndex = getPrimaryStreamIndex(i, streamIndexToTrackGroupIndex);
            boolean mayRetainStream = primaryStreamIndex == -1 ? streams[i] instanceof EmptySampleStream : (streams[i] instanceof EmbeddedSampleStream) && ((EmbeddedSampleStream) streams[i]).parent == streams[primaryStreamIndex];
            if (!mayRetainStream) {
                if (streams[i] instanceof EmbeddedSampleStream) {
                    ((EmbeddedSampleStream) streams[i]).release();
                }
                streams[i] = null;
            }
        }
    }

    private void selectNewStreams(TrackSelection[] selections, SampleStream[] streams, boolean[] streamResetFlags, long positionUs, int[] streamIndexToTrackGroupIndex) {
        int i = 0;
        while (i < selections.length) {
            if (streams[i] == null && selections[i] != null) {
                streamResetFlags[i] = true;
                TrackGroupInfo trackGroupInfo = this.trackGroupInfos[streamIndexToTrackGroupIndex[i]];
                if (trackGroupInfo.trackGroupCategory == 0) {
                    streams[i] = buildSampleStream(trackGroupInfo, selections[i], positionUs);
                } else if (trackGroupInfo.trackGroupCategory == 2) {
                    streams[i] = new EventSampleStream((EventStream) this.eventStreams.get(trackGroupInfo.eventStreamGroupIndex), selections[i].getTrackGroup().getFormat(0), this.manifest.dynamic);
                }
            }
            i++;
        }
        i = 0;
        while (i < selections.length) {
            if (streams[i] == null && selections[i] != null) {
                TrackGroupInfo trackGroupInfo2 = this.trackGroupInfos[streamIndexToTrackGroupIndex[i]];
                if (trackGroupInfo2.trackGroupCategory == 1) {
                    int primaryStreamIndex = getPrimaryStreamIndex(i, streamIndexToTrackGroupIndex);
                    if (primaryStreamIndex == -1) {
                        streams[i] = new EmptySampleStream();
                    } else {
                        streams[i] = ((ChunkSampleStream) streams[primaryStreamIndex]).selectEmbeddedTrack(positionUs, trackGroupInfo2.trackType);
                    }
                }
            }
            i++;
        }
    }

    private int getPrimaryStreamIndex(int embeddedStreamIndex, int[] streamIndexToTrackGroupIndex) {
        int embeddedTrackGroupIndex = streamIndexToTrackGroupIndex[embeddedStreamIndex];
        if (embeddedTrackGroupIndex == -1) {
            return -1;
        }
        int primaryTrackGroupIndex = this.trackGroupInfos[embeddedTrackGroupIndex].primaryTrackGroupIndex;
        for (int i = 0; i < streamIndexToTrackGroupIndex.length; i++) {
            int trackGroupIndex = streamIndexToTrackGroupIndex[i];
            if (trackGroupIndex == primaryTrackGroupIndex && this.trackGroupInfos[trackGroupIndex].trackGroupCategory == 0) {
                return i;
            }
        }
        return -1;
    }

    private static Pair<TrackGroupArray, TrackGroupInfo[]> buildTrackGroups(List<AdaptationSet> adaptationSets, List<EventStream> eventStreams) {
        int[][] groupedAdaptationSetIndices = getGroupedAdaptationSetIndices(adaptationSets);
        int primaryGroupCount = groupedAdaptationSetIndices.length;
        boolean[] primaryGroupHasEventMessageTrackFlags = new boolean[primaryGroupCount];
        boolean[] primaryGroupHasCea608TrackFlags = new boolean[primaryGroupCount];
        int totalGroupCount = (primaryGroupCount + identifyEmbeddedTracks(primaryGroupCount, adaptationSets, groupedAdaptationSetIndices, primaryGroupHasEventMessageTrackFlags, primaryGroupHasCea608TrackFlags)) + eventStreams.size();
        TrackGroup[] trackGroups = new TrackGroup[totalGroupCount];
        TrackGroupInfo[] trackGroupInfos = new TrackGroupInfo[totalGroupCount];
        buildManifestEventTrackGroupInfos(eventStreams, trackGroups, trackGroupInfos, buildPrimaryAndEmbeddedTrackGroupInfos(adaptationSets, groupedAdaptationSetIndices, primaryGroupCount, primaryGroupHasEventMessageTrackFlags, primaryGroupHasCea608TrackFlags, trackGroups, trackGroupInfos));
        return Pair.create(new TrackGroupArray(trackGroups), trackGroupInfos);
    }

    private static int[][] getGroupedAdaptationSetIndices(List<AdaptationSet> adaptationSets) {
        int adaptationSetCount = adaptationSets.size();
        SparseIntArray idToIndexMap = new SparseIntArray(adaptationSetCount);
        for (int i = 0; i < adaptationSetCount; i++) {
            idToIndexMap.put(((AdaptationSet) adaptationSets.get(i)).id, i);
        }
        int[][] groupedAdaptationSetIndices = new int[adaptationSetCount][];
        boolean[] adaptationSetUsedFlags = new boolean[adaptationSetCount];
        int groupCount = 0;
        for (int i2 = 0; i2 < adaptationSetCount; i2++) {
            if (!adaptationSetUsedFlags[i2]) {
                adaptationSetUsedFlags[i2] = true;
                Descriptor adaptationSetSwitchingProperty = findAdaptationSetSwitchingProperty(((AdaptationSet) adaptationSets.get(i2)).supplementalProperties);
                if (adaptationSetSwitchingProperty == null) {
                    int groupCount2 = groupCount + 1;
                    groupedAdaptationSetIndices[groupCount] = new int[]{i2};
                    groupCount = groupCount2;
                } else {
                    String[] extraAdaptationSetIds = adaptationSetSwitchingProperty.value.split(",");
                    int[] adaptationSetIndices = new int[(extraAdaptationSetIds.length + 1)];
                    adaptationSetIndices[0] = i2;
                    for (int j = 0; j < extraAdaptationSetIds.length; j++) {
                        int extraIndex = idToIndexMap.get(Integer.parseInt(extraAdaptationSetIds[j]));
                        adaptationSetUsedFlags[extraIndex] = true;
                        adaptationSetIndices[j + 1] = extraIndex;
                    }
                    int groupCount3 = groupCount + 1;
                    groupedAdaptationSetIndices[groupCount] = adaptationSetIndices;
                    groupCount = groupCount3;
                }
            }
        }
        return groupCount < adaptationSetCount ? (int[][]) Arrays.copyOf(groupedAdaptationSetIndices, groupCount) : groupedAdaptationSetIndices;
    }

    private static int identifyEmbeddedTracks(int primaryGroupCount, List<AdaptationSet> adaptationSets, int[][] groupedAdaptationSetIndices, boolean[] primaryGroupHasEventMessageTrackFlags, boolean[] primaryGroupHasCea608TrackFlags) {
        int numEmbeddedTrack = 0;
        for (int i = 0; i < primaryGroupCount; i++) {
            if (hasEventMessageTrack(adaptationSets, groupedAdaptationSetIndices[i])) {
                primaryGroupHasEventMessageTrackFlags[i] = true;
                numEmbeddedTrack++;
            }
            if (hasCea608Track(adaptationSets, groupedAdaptationSetIndices[i])) {
                primaryGroupHasCea608TrackFlags[i] = true;
                numEmbeddedTrack++;
            }
        }
        return numEmbeddedTrack;
    }

    private static int buildPrimaryAndEmbeddedTrackGroupInfos(List<AdaptationSet> adaptationSets, int[][] groupedAdaptationSetIndices, int primaryGroupCount, boolean[] primaryGroupHasEventMessageTrackFlags, boolean[] primaryGroupHasCea608TrackFlags, TrackGroup[] trackGroups, TrackGroupInfo[] trackGroupInfos) {
        List<AdaptationSet> list = adaptationSets;
        int trackGroupCount = 0;
        int i = 0;
        while (i < primaryGroupCount) {
            int i2;
            int trackGroupCount2;
            int trackGroupCount3;
            int[] adaptationSetIndices = groupedAdaptationSetIndices[i];
            List<Representation> representations = new ArrayList();
            for (int adaptationSetIndex : adaptationSetIndices) {
                representations.addAll(((AdaptationSet) list.get(adaptationSetIndex)).representations);
            }
            Format[] formats = new Format[representations.size()];
            for (i2 = 0; i2 < formats.length; i2++) {
                formats[i2] = ((Representation) representations.get(i2)).format;
            }
            AdaptationSet firstAdaptationSet = (AdaptationSet) list.get(adaptationSetIndices[0]);
            int adaptationSetIndex2 = trackGroupCount + 1;
            if (primaryGroupHasEventMessageTrackFlags[i]) {
                trackGroupCount2 = adaptationSetIndex2 + 1;
            } else {
                trackGroupCount2 = adaptationSetIndex2;
                adaptationSetIndex2 = -1;
            }
            if (primaryGroupHasCea608TrackFlags[i]) {
                trackGroupCount3 = trackGroupCount2 + 1;
            } else {
                trackGroupCount3 = trackGroupCount2;
                trackGroupCount2 = -1;
            }
            trackGroups[trackGroupCount] = new TrackGroup(formats);
            trackGroupInfos[trackGroupCount] = TrackGroupInfo.primaryTrack(firstAdaptationSet.type, adaptationSetIndices, trackGroupCount, adaptationSetIndex2, trackGroupCount2);
            if (adaptationSetIndex2 != -1) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(firstAdaptationSet.id);
                stringBuilder.append(":emsg");
                trackGroups[adaptationSetIndex2] = new TrackGroup(Format.createSampleFormat(stringBuilder.toString(), MimeTypes.APPLICATION_EMSG, null, -1, null));
                trackGroupInfos[adaptationSetIndex2] = TrackGroupInfo.embeddedEmsgTrack(adaptationSetIndices, trackGroupCount);
            }
            if (trackGroupCount2 != -1) {
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append(firstAdaptationSet.id);
                stringBuilder2.append(":cea608");
                trackGroups[trackGroupCount2] = new TrackGroup(Format.createTextSampleFormat(stringBuilder2.toString(), MimeTypes.APPLICATION_CEA608, 0, null));
                trackGroupInfos[trackGroupCount2] = TrackGroupInfo.embeddedCea608Track(adaptationSetIndices, trackGroupCount);
            }
            i++;
            trackGroupCount = trackGroupCount3;
        }
        return trackGroupCount;
    }

    private static void buildManifestEventTrackGroupInfos(List<EventStream> eventStreams, TrackGroup[] trackGroups, TrackGroupInfo[] trackGroupInfos, int existingTrackGroupCount) {
        int i = 0;
        while (i < eventStreams.size()) {
            trackGroups[existingTrackGroupCount] = new TrackGroup(Format.createSampleFormat(((EventStream) eventStreams.get(i)).id(), MimeTypes.APPLICATION_EMSG, null, -1, null));
            int existingTrackGroupCount2 = existingTrackGroupCount + 1;
            trackGroupInfos[existingTrackGroupCount] = TrackGroupInfo.mpdEventTrack(i);
            i++;
            existingTrackGroupCount = existingTrackGroupCount2;
        }
    }

    private ChunkSampleStream<DashChunkSource> buildSampleStream(TrackGroupInfo trackGroupInfo, TrackSelection selection, long positionUs) {
        int embeddedTrackCount;
        Format[] embeddedTrackFormats;
        int[] embeddedTrackTypes;
        DashMediaPeriod dashMediaPeriod = this;
        TrackGroupInfo trackGroupInfo2 = trackGroupInfo;
        int embeddedTrackCount2 = 0;
        int[] embeddedTrackTypes2 = new int[2];
        Format[] embeddedTrackFormats2 = new Format[2];
        boolean z = true;
        boolean enableEventMessageTrack = trackGroupInfo2.embeddedEventMessageTrackGroupIndex != -1;
        if (enableEventMessageTrack) {
            embeddedTrackFormats2[0] = dashMediaPeriod.trackGroups.get(trackGroupInfo2.embeddedEventMessageTrackGroupIndex).getFormat(0);
            int embeddedTrackCount3 = 0 + 1;
            embeddedTrackTypes2[0] = 4;
            embeddedTrackCount2 = embeddedTrackCount3;
        }
        if (trackGroupInfo2.embeddedCea608TrackGroupIndex == -1) {
            z = false;
        }
        boolean enableCea608Track = z;
        if (enableCea608Track) {
            embeddedTrackFormats2[embeddedTrackCount2] = dashMediaPeriod.trackGroups.get(trackGroupInfo2.embeddedCea608TrackGroupIndex).getFormat(0);
            embeddedTrackCount3 = embeddedTrackCount2 + 1;
            embeddedTrackTypes2[embeddedTrackCount2] = 3;
            embeddedTrackCount = embeddedTrackCount3;
        } else {
            embeddedTrackCount = embeddedTrackCount2;
        }
        if (embeddedTrackCount < embeddedTrackTypes2.length) {
            embeddedTrackFormats = (Format[]) Arrays.copyOf(embeddedTrackFormats2, embeddedTrackCount);
            embeddedTrackTypes = Arrays.copyOf(embeddedTrackTypes2, embeddedTrackCount);
        } else {
            embeddedTrackFormats = embeddedTrackFormats2;
            embeddedTrackTypes = embeddedTrackTypes2;
        }
        PlayerTrackEmsgHandler newPlayerTrackEmsgHandler = (dashMediaPeriod.manifest.dynamic && enableEventMessageTrack) ? dashMediaPeriod.playerEmsgHandler.newPlayerTrackEmsgHandler() : null;
        PlayerTrackEmsgHandler trackPlayerEmsgHandler = newPlayerTrackEmsgHandler;
        DashChunkSource chunkSource = dashMediaPeriod.chunkSourceFactory.createDashChunkSource(dashMediaPeriod.manifestLoaderErrorThrower, dashMediaPeriod.manifest, dashMediaPeriod.periodIndex, trackGroupInfo2.adaptationSetIndices, selection, trackGroupInfo2.trackType, dashMediaPeriod.elapsedRealtimeOffset, enableEventMessageTrack, enableCea608Track, trackPlayerEmsgHandler, dashMediaPeriod.transferListener);
        PlayerTrackEmsgHandler trackPlayerEmsgHandler2 = trackPlayerEmsgHandler;
        ChunkSampleStream<DashChunkSource> stream = new ChunkSampleStream(trackGroupInfo2.trackType, embeddedTrackTypes, embeddedTrackFormats, (ChunkSource) chunkSource, (Callback) this, dashMediaPeriod.allocator, positionUs, dashMediaPeriod.loadErrorHandlingPolicy, dashMediaPeriod.eventDispatcher);
        synchronized (this) {
            dashMediaPeriod.trackEmsgHandlerBySampleStream.put(stream, trackPlayerEmsgHandler2);
        }
        return stream;
    }

    private static Descriptor findAdaptationSetSwitchingProperty(List<Descriptor> descriptors) {
        for (int i = 0; i < descriptors.size(); i++) {
            Descriptor descriptor = (Descriptor) descriptors.get(i);
            if ("urn:mpeg:dash:adaptation-set-switching:2016".equals(descriptor.schemeIdUri)) {
                return descriptor;
            }
        }
        return null;
    }

    private static boolean hasEventMessageTrack(List<AdaptationSet> adaptationSets, int[] adaptationSetIndices) {
        for (int i : adaptationSetIndices) {
            List<Representation> representations = ((AdaptationSet) adaptationSets.get(i)).representations;
            for (int j = 0; j < representations.size(); j++) {
                if (!((Representation) representations.get(j)).inbandEventStreams.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean hasCea608Track(List<AdaptationSet> adaptationSets, int[] adaptationSetIndices) {
        for (int i : adaptationSetIndices) {
            List<Descriptor> descriptors = ((AdaptationSet) adaptationSets.get(i)).accessibilityDescriptors;
            for (int j = 0; j < descriptors.size(); j++) {
                if ("urn:scte:dash:cc:cea-608:2015".equals(((Descriptor) descriptors.get(j)).schemeIdUri)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static ChunkSampleStream<DashChunkSource>[] newSampleStreamArray(int length) {
        return new ChunkSampleStream[length];
    }
}
