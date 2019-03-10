package com.google.android.exoplayer2.source.hls;

import android.net.Uri;
import android.os.Handler;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.FormatHolder;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.extractor.DummyTrackOutput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.Metadata.Entry;
import com.google.android.exoplayer2.metadata.id3.PrivFrame;
import com.google.android.exoplayer2.source.MediaSourceEventListener$EventDispatcher;
import com.google.android.exoplayer2.source.SampleQueue;
import com.google.android.exoplayer2.source.SampleQueue.UpstreamFormatChangedListener;
import com.google.android.exoplayer2.source.SampleStream;
import com.google.android.exoplayer2.source.SequenceableLoader;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.chunk.Chunk;
import com.google.android.exoplayer2.source.hls.HlsChunkSource.HlsChunkHolder;
import com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist.HlsUrl;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.Loader;
import com.google.android.exoplayer2.upstream.Loader.LoadErrorAction;
import com.google.android.exoplayer2.upstream.Loader.ReleaseCallback;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

final class HlsSampleStreamWrapper implements com.google.android.exoplayer2.upstream.Loader.Callback<Chunk>, ReleaseCallback, SequenceableLoader, ExtractorOutput, UpstreamFormatChangedListener {
    public static final int SAMPLE_QUEUE_INDEX_NO_MAPPING_FATAL = -2;
    public static final int SAMPLE_QUEUE_INDEX_NO_MAPPING_NON_FATAL = -3;
    public static final int SAMPLE_QUEUE_INDEX_PENDING = -1;
    private static final String TAG = "HlsSampleStreamWrapper";
    private final Allocator allocator;
    private int audioSampleQueueIndex = -1;
    private boolean audioSampleQueueMappingDone;
    private final Callback callback;
    private final HlsChunkSource chunkSource;
    private int chunkUid;
    private Format downstreamTrackFormat;
    private int enabledTrackGroupCount;
    private final MediaSourceEventListener$EventDispatcher eventDispatcher;
    private final Handler handler = new Handler();
    private boolean haveAudioVideoSampleQueues;
    private final ArrayList<HlsSampleStream> hlsSampleStreams = new ArrayList();
    private long lastSeekPositionUs;
    private final LoadErrorHandlingPolicy loadErrorHandlingPolicy;
    private final Loader loader = new Loader("Loader:HlsSampleStreamWrapper");
    private boolean loadingFinished;
    private final Runnable maybeFinishPrepareRunnable = new -$$Lambda$HlsSampleStreamWrapper$8JyeEr0irIOShv9LlAxAmgzl5vY();
    private final ArrayList<HlsMediaChunk> mediaChunks = new ArrayList();
    private final Format muxedAudioFormat;
    private final HlsChunkHolder nextChunkHolder = new HlsChunkHolder();
    private final Runnable onTracksEndedRunnable = new -$$Lambda$HlsSampleStreamWrapper$afhkI3tagC_-MAOTh7FzBWzQsno();
    private TrackGroupArray optionalTrackGroups;
    private long pendingResetPositionUs;
    private boolean pendingResetUpstreamFormats;
    private boolean prepared;
    private int primarySampleQueueIndex;
    private int primarySampleQueueType;
    private int primaryTrackGroupIndex;
    private final List<HlsMediaChunk> readOnlyMediaChunks = Collections.unmodifiableList(this.mediaChunks);
    private boolean released;
    private long sampleOffsetUs;
    private boolean[] sampleQueueIsAudioVideoFlags = new boolean[0];
    private int[] sampleQueueTrackIds = new int[0];
    private SampleQueue[] sampleQueues = new SampleQueue[0];
    private boolean sampleQueuesBuilt;
    private boolean[] sampleQueuesEnabledStates = new boolean[0];
    private boolean seenFirstTrackSelection;
    private int[] trackGroupToSampleQueueIndex;
    private TrackGroupArray trackGroups;
    private final int trackType;
    private boolean tracksEnded;
    private Format upstreamTrackFormat;
    private int videoSampleQueueIndex = -1;
    private boolean videoSampleQueueMappingDone;

    public interface Callback extends com.google.android.exoplayer2.source.SequenceableLoader.Callback<HlsSampleStreamWrapper> {
        void onPlaylistRefreshRequired(HlsUrl hlsUrl);

        void onPrepared();
    }

    private static final class PrivTimestampStrippingSampleQueue extends SampleQueue {
        public PrivTimestampStrippingSampleQueue(Allocator allocator) {
            super(allocator);
        }

        public void format(Format format) {
            super.format(format.copyWithMetadata(getAdjustedMetadata(format.metadata)));
        }

        @Nullable
        private Metadata getAdjustedMetadata(@Nullable Metadata metadata) {
            if (metadata == null) {
                return null;
            }
            int i;
            int length = metadata.length();
            int transportStreamTimestampMetadataIndex = -1;
            for (i = 0; i < length; i++) {
                Entry metadataEntry = metadata.get(i);
                if (metadataEntry instanceof PrivFrame) {
                    if (HlsMediaChunk.PRIV_TIMESTAMP_FRAME_OWNER.equals(((PrivFrame) metadataEntry).owner)) {
                        transportStreamTimestampMetadataIndex = i;
                        break;
                    }
                }
            }
            if (transportStreamTimestampMetadataIndex == -1) {
                return metadata;
            }
            if (length == 1) {
                return null;
            }
            Entry[] newMetadataEntries = new Entry[(length - 1)];
            i = 0;
            while (i < length) {
                if (i != transportStreamTimestampMetadataIndex) {
                    newMetadataEntries[i < transportStreamTimestampMetadataIndex ? i : i - 1] = metadata.get(i);
                }
                i++;
            }
            return new Metadata(newMetadataEntries);
        }
    }

    public HlsSampleStreamWrapper(int trackType, Callback callback, HlsChunkSource chunkSource, Allocator allocator, long positionUs, Format muxedAudioFormat, LoadErrorHandlingPolicy loadErrorHandlingPolicy, MediaSourceEventListener$EventDispatcher eventDispatcher) {
        this.trackType = trackType;
        this.callback = callback;
        this.chunkSource = chunkSource;
        this.allocator = allocator;
        this.muxedAudioFormat = muxedAudioFormat;
        this.loadErrorHandlingPolicy = loadErrorHandlingPolicy;
        this.eventDispatcher = eventDispatcher;
        this.lastSeekPositionUs = positionUs;
        this.pendingResetPositionUs = positionUs;
    }

    public void continuePreparing() {
        if (!this.prepared) {
            continueLoading(this.lastSeekPositionUs);
        }
    }

    public void prepareWithMasterPlaylistInfo(TrackGroupArray trackGroups, int primaryTrackGroupIndex, TrackGroupArray optionalTrackGroups) {
        this.prepared = true;
        this.trackGroups = trackGroups;
        this.optionalTrackGroups = optionalTrackGroups;
        this.primaryTrackGroupIndex = primaryTrackGroupIndex;
        this.callback.onPrepared();
    }

    public void maybeThrowPrepareError() throws IOException {
        maybeThrowError();
    }

    public TrackGroupArray getTrackGroups() {
        return this.trackGroups;
    }

    public int bindSampleQueueToSampleStream(int trackGroupIndex) {
        int sampleQueueIndex = this.trackGroupToSampleQueueIndex[trackGroupIndex];
        int i = -2;
        if (sampleQueueIndex == -1) {
            if (this.optionalTrackGroups.indexOf(this.trackGroups.get(trackGroupIndex)) != -1) {
                i = -3;
            }
            return i;
        }
        boolean[] zArr = this.sampleQueuesEnabledStates;
        if (zArr[sampleQueueIndex]) {
            return -2;
        }
        zArr[sampleQueueIndex] = true;
        return sampleQueueIndex;
    }

    public void unbindSampleQueue(int trackGroupIndex) {
        int sampleQueueIndex = this.trackGroupToSampleQueueIndex[trackGroupIndex];
        Assertions.checkState(this.sampleQueuesEnabledStates[sampleQueueIndex]);
        this.sampleQueuesEnabledStates[sampleQueueIndex] = false;
    }

    public boolean selectTracks(com.google.android.exoplayer2.trackselection.TrackSelection[] r23, boolean[] r24, com.google.android.exoplayer2.source.SampleStream[] r25, boolean[] r26, long r27, boolean r29) {
        /* JADX: method processing error */
/*
Error: java.lang.IndexOutOfBoundsException: bitIndex < 0: -1
	at java.util.BitSet.get(BitSet.java:623)
	at jadx.core.dex.visitors.CodeShrinker$ArgsInfo.usedArgAssign(CodeShrinker.java:138)
	at jadx.core.dex.visitors.CodeShrinker$ArgsInfo.access$300(CodeShrinker.java:43)
	at jadx.core.dex.visitors.CodeShrinker.canMoveBetweenBlocks(CodeShrinker.java:282)
	at jadx.core.dex.visitors.CodeShrinker.shrinkBlock(CodeShrinker.java:232)
	at jadx.core.dex.visitors.CodeShrinker.shrinkMethod(CodeShrinker.java:38)
	at jadx.core.dex.visitors.regions.LoopRegionVisitor.checkArrayForEach(LoopRegionVisitor.java:196)
	at jadx.core.dex.visitors.regions.LoopRegionVisitor.checkForIndexedLoop(LoopRegionVisitor.java:119)
	at jadx.core.dex.visitors.regions.LoopRegionVisitor.processLoopRegion(LoopRegionVisitor.java:65)
	at jadx.core.dex.visitors.regions.LoopRegionVisitor.enterRegion(LoopRegionVisitor.java:52)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:56)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverse(DepthRegionTraversal.java:18)
	at jadx.core.dex.visitors.regions.LoopRegionVisitor.visit(LoopRegionVisitor.java:46)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r22 = this;
        r0 = r22;
        r1 = r23;
        r2 = r25;
        r12 = r27;
        r3 = r0.prepared;
        com.google.android.exoplayer2.util.Assertions.checkState(r3);
        r14 = r0.enabledTrackGroupCount;
        r3 = 0;
    L_0x0010:
        r4 = r1.length;
        r5 = 0;
        r15 = 1;
        if (r3 >= r4) goto L_0x0034;
    L_0x0015:
        r4 = r2[r3];
        if (r4 == 0) goto L_0x0030;
    L_0x0019:
        r4 = r1[r3];
        if (r4 == 0) goto L_0x0021;
    L_0x001d:
        r4 = r24[r3];
        if (r4 != 0) goto L_0x0030;
    L_0x0021:
        r4 = r0.enabledTrackGroupCount;
        r4 = r4 - r15;
        r0.enabledTrackGroupCount = r4;
        r4 = r2[r3];
        r4 = (com.google.android.exoplayer2.source.hls.HlsSampleStream) r4;
        r4.unbindSampleQueue();
        r2[r3] = r5;
        goto L_0x0031;
    L_0x0031:
        r3 = r3 + 1;
        goto L_0x0010;
    L_0x0034:
        if (r29 != 0) goto L_0x0046;
    L_0x0036:
        r4 = r0.seenFirstTrackSelection;
        if (r4 == 0) goto L_0x003d;
    L_0x003a:
        if (r14 != 0) goto L_0x0044;
    L_0x003c:
        goto L_0x0046;
    L_0x003d:
        r6 = r0.lastSeekPositionUs;
        r4 = (r12 > r6 ? 1 : (r12 == r6 ? 0 : -1));
        if (r4 == 0) goto L_0x0044;
    L_0x0043:
        goto L_0x0046;
    L_0x0044:
        r4 = 0;
        goto L_0x0047;
    L_0x0046:
        r4 = 1;
    L_0x0047:
        r6 = r0.chunkSource;
        r11 = r6.getTrackSelection();
        r6 = r11;
        r7 = 0;
        r16 = r4;
        r10 = r6;
    L_0x0052:
        r4 = r1.length;
        if (r7 >= r4) goto L_0x00bb;
    L_0x0055:
        r4 = r2[r7];
        if (r4 != 0) goto L_0x00b7;
    L_0x0059:
        r4 = r1[r7];
        if (r4 == 0) goto L_0x00b7;
    L_0x005d:
        r4 = r0.enabledTrackGroupCount;
        r4 = r4 + r15;
        r0.enabledTrackGroupCount = r4;
        r4 = r1[r7];
        r6 = r0.trackGroups;
        r8 = r4.getTrackGroup();
        r6 = r6.indexOf(r8);
        r8 = r0.primaryTrackGroupIndex;
        if (r6 != r8) goto L_0x0079;
    L_0x0072:
        r10 = r4;
        r8 = r0.chunkSource;
        r8.selectTracks(r4);
        goto L_0x007a;
    L_0x007a:
        r8 = new com.google.android.exoplayer2.source.hls.HlsSampleStream;
        r8.<init>(r0, r6);
        r2[r7] = r8;
        r26[r7] = r15;
        r8 = r0.trackGroupToSampleQueueIndex;
        if (r8 == 0) goto L_0x008f;
    L_0x0087:
        r8 = r2[r7];
        r8 = (com.google.android.exoplayer2.source.hls.HlsSampleStream) r8;
        r8.bindSampleQueue();
        goto L_0x0090;
    L_0x0090:
        r8 = r0.sampleQueuesBuilt;
        if (r8 == 0) goto L_0x00b6;
    L_0x0094:
        if (r16 != 0) goto L_0x00b6;
    L_0x0096:
        r8 = r0.sampleQueues;
        r9 = r0.trackGroupToSampleQueueIndex;
        r9 = r9[r6];
        r8 = r8[r9];
        r8.rewind();
        r9 = r8.advanceTo(r12, r15, r15);
        r3 = -1;
        if (r9 != r3) goto L_0x00b1;
    L_0x00a8:
        r3 = r8.getReadIndex();
        if (r3 == 0) goto L_0x00b0;
    L_0x00ae:
        r3 = 1;
        goto L_0x00b3;
    L_0x00b0:
        goto L_0x00b2;
    L_0x00b2:
        r3 = 0;
    L_0x00b3:
        r16 = r3;
        goto L_0x00b8;
    L_0x00b6:
        goto L_0x00b8;
    L_0x00b8:
        r7 = r7 + 1;
        goto L_0x0052;
    L_0x00bb:
        r3 = r0.enabledTrackGroupCount;
        if (r3 != 0) goto L_0x00f8;
    L_0x00bf:
        r3 = r0.chunkSource;
        r3.reset();
        r0.downstreamTrackFormat = r5;
        r3 = r0.mediaChunks;
        r3.clear();
        r3 = r0.loader;
        r3 = r3.isLoading();
        if (r3 == 0) goto L_0x00ed;
    L_0x00d3:
        r3 = r0.sampleQueuesBuilt;
        if (r3 == 0) goto L_0x00e6;
    L_0x00d7:
        r3 = r0.sampleQueues;
        r4 = r3.length;
        r5 = 0;
    L_0x00db:
        if (r5 >= r4) goto L_0x00e5;
    L_0x00dd:
        r6 = r3[r5];
        r6.discardToEnd();
        r5 = r5 + 1;
        goto L_0x00db;
    L_0x00e5:
        goto L_0x00e7;
    L_0x00e7:
        r3 = r0.loader;
        r3.cancelLoading();
        goto L_0x00f0;
    L_0x00ed:
        r22.resetSampleQueues();
    L_0x00f0:
        r3 = r29;
        r19 = r10;
        r20 = r11;
        goto L_0x0183;
    L_0x00f8:
        r3 = r0.mediaChunks;
        r3 = r3.isEmpty();
        if (r3 != 0) goto L_0x0166;
    L_0x0100:
        r3 = com.google.android.exoplayer2.util.Util.areEqual(r10, r11);
        if (r3 != 0) goto L_0x0161;
    L_0x0106:
        r17 = 0;
        r3 = r0.seenFirstTrackSelection;
        if (r3 != 0) goto L_0x0150;
    L_0x010c:
        r3 = 0;
        r5 = (r12 > r3 ? 1 : (r12 == r3 ? 0 : -1));
        if (r5 >= 0) goto L_0x0114;
    L_0x0112:
        r3 = -r12;
    L_0x0114:
        r6 = r3;
        r8 = r22.getLastMediaChunk();
        r3 = r0.chunkSource;
        r18 = r3.createMediaChunkIterators(r8, r12);
        r19 = -9223372036854775807; // 0x8000000000000001 float:1.4E-45 double:-4.9E-324;
        r9 = r0.readOnlyMediaChunks;
        r3 = r10;
        r4 = r27;
        r15 = r8;
        r21 = r9;
        r8 = r19;
        r19 = r10;
        r10 = r21;
        r20 = r11;
        r11 = r18;
        r3.updateSelectedTrack(r4, r6, r8, r10, r11);
        r3 = r0.chunkSource;
        r3 = r3.getTrackGroup();
        r4 = r15.trackFormat;
        r3 = r3.indexOf(r4);
        r4 = r19.getSelectedIndexInTrackGroup();
        if (r4 == r3) goto L_0x014e;
    L_0x014b:
        r17 = 1;
        goto L_0x014f;
    L_0x014f:
        goto L_0x0156;
    L_0x0150:
        r19 = r10;
        r20 = r11;
        r17 = 1;
    L_0x0156:
        if (r17 == 0) goto L_0x0160;
    L_0x0158:
        r3 = 1;
        r4 = 1;
        r5 = 1;
        r0.pendingResetUpstreamFormats = r5;
        r16 = r4;
        goto L_0x016c;
    L_0x0160:
        goto L_0x016a;
    L_0x0161:
        r19 = r10;
        r20 = r11;
        goto L_0x016a;
    L_0x0166:
        r19 = r10;
        r20 = r11;
    L_0x016a:
        r3 = r29;
    L_0x016c:
        if (r16 == 0) goto L_0x0182;
    L_0x016e:
        r0.seekToUs(r12, r3);
        r4 = 0;
    L_0x0172:
        r5 = r2.length;
        if (r4 >= r5) goto L_0x0181;
    L_0x0175:
        r5 = r2[r4];
        if (r5 == 0) goto L_0x017d;
    L_0x0179:
        r5 = 1;
        r26[r4] = r5;
        goto L_0x017e;
    L_0x017e:
        r4 = r4 + 1;
        goto L_0x0172;
    L_0x0181:
        goto L_0x0183;
    L_0x0183:
        r0.updateSampleStreams(r2);
        r4 = 1;
        r0.seenFirstTrackSelection = r4;
        return r16;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.hls.HlsSampleStreamWrapper.selectTracks(com.google.android.exoplayer2.trackselection.TrackSelection[], boolean[], com.google.android.exoplayer2.source.SampleStream[], boolean[], long, boolean):boolean");
    }

    public void discardBuffer(long positionUs, boolean toKeyframe) {
        if (this.sampleQueuesBuilt) {
            if (!isPendingReset()) {
                int sampleQueueCount = this.sampleQueues.length;
                for (int i = 0; i < sampleQueueCount; i++) {
                    this.sampleQueues[i].discardTo(positionUs, toKeyframe, this.sampleQueuesEnabledStates[i]);
                }
            }
        }
    }

    public boolean seekToUs(long positionUs, boolean forceReset) {
        this.lastSeekPositionUs = positionUs;
        if (isPendingReset()) {
            this.pendingResetPositionUs = positionUs;
            return true;
        } else if (this.sampleQueuesBuilt && !forceReset && seekInsideBufferUs(positionUs)) {
            return false;
        } else {
            this.pendingResetPositionUs = positionUs;
            this.loadingFinished = false;
            this.mediaChunks.clear();
            if (this.loader.isLoading()) {
                this.loader.cancelLoading();
            } else {
                resetSampleQueues();
            }
            return true;
        }
    }

    public void release() {
        if (this.prepared) {
            for (SampleQueue sampleQueue : this.sampleQueues) {
                sampleQueue.discardToEnd();
            }
        }
        this.loader.release(this);
        this.handler.removeCallbacksAndMessages(null);
        this.released = true;
        this.hlsSampleStreams.clear();
    }

    public void onLoaderReleased() {
        resetSampleQueues();
    }

    public void setIsTimestampMaster(boolean isTimestampMaster) {
        this.chunkSource.setIsTimestampMaster(isTimestampMaster);
    }

    public boolean onPlaylistError(HlsUrl url, long blacklistDurationMs) {
        return this.chunkSource.onPlaylistError(url, blacklistDurationMs);
    }

    public boolean isReady(int sampleQueueIndex) {
        if (!this.loadingFinished) {
            if (isPendingReset() || !this.sampleQueues[sampleQueueIndex].hasNextSample()) {
                return false;
            }
        }
        return true;
    }

    public void maybeThrowError() throws IOException {
        this.loader.maybeThrowError();
        this.chunkSource.maybeThrowError();
    }

    public int readData(int sampleQueueIndex, FormatHolder formatHolder, DecoderInputBuffer buffer, boolean requireFormat) {
        if (isPendingReset()) {
            return -3;
        }
        int discardToMediaChunkIndex;
        if (!this.mediaChunks.isEmpty()) {
            discardToMediaChunkIndex = 0;
            while (discardToMediaChunkIndex < this.mediaChunks.size() - 1) {
                if (!finishedReadingChunk((HlsMediaChunk) this.mediaChunks.get(discardToMediaChunkIndex))) {
                    break;
                }
                discardToMediaChunkIndex++;
            }
            Util.removeRange(this.mediaChunks, 0, discardToMediaChunkIndex);
            HlsMediaChunk currentChunk = (HlsMediaChunk) this.mediaChunks.get(0);
            Format trackFormat = currentChunk.trackFormat;
            if (!trackFormat.equals(this.downstreamTrackFormat)) {
                this.eventDispatcher.downstreamFormatChanged(this.trackType, trackFormat, currentChunk.trackSelectionReason, currentChunk.trackSelectionData, currentChunk.startTimeUs);
            }
            this.downstreamTrackFormat = trackFormat;
        }
        discardToMediaChunkIndex = this.sampleQueues[sampleQueueIndex].read(formatHolder, buffer, requireFormat, this.loadingFinished, this.lastSeekPositionUs);
        if (discardToMediaChunkIndex == -5 && sampleQueueIndex == this.primarySampleQueueIndex) {
            int chunkUid = this.sampleQueues[sampleQueueIndex].peekSourceId();
            int chunkIndex = 0;
            while (chunkIndex < this.mediaChunks.size() && ((HlsMediaChunk) this.mediaChunks.get(chunkIndex)).uid != chunkUid) {
                chunkIndex++;
            }
            formatHolder.format = formatHolder.format.copyWithManifestFormatInfo(chunkIndex < this.mediaChunks.size() ? ((HlsMediaChunk) this.mediaChunks.get(chunkIndex)).trackFormat : this.upstreamTrackFormat);
        }
        return discardToMediaChunkIndex;
    }

    public int skipData(int sampleQueueIndex, long positionUs) {
        int i = 0;
        if (isPendingReset()) {
            return 0;
        }
        SampleQueue sampleQueue = this.sampleQueues[sampleQueueIndex];
        if (this.loadingFinished && positionUs > sampleQueue.getLargestQueuedTimestampUs()) {
            return sampleQueue.advanceToEnd();
        }
        int skipCount = sampleQueue.advanceTo(positionUs, true, true);
        if (skipCount != -1) {
            i = skipCount;
        }
        return i;
    }

    public long getBufferedPositionUs() {
        if (this.loadingFinished) {
            return Long.MIN_VALUE;
        }
        if (isPendingReset()) {
            return this.pendingResetPositionUs;
        }
        HlsMediaChunk lastCompletedMediaChunk;
        long bufferedPositionUs = this.lastSeekPositionUs;
        HlsMediaChunk lastMediaChunk = getLastMediaChunk();
        if (lastMediaChunk.isLoadCompleted()) {
            lastCompletedMediaChunk = lastMediaChunk;
        } else if (this.mediaChunks.size() > 1) {
            ArrayList arrayList = this.mediaChunks;
            lastCompletedMediaChunk = (HlsMediaChunk) arrayList.get(arrayList.size() - 2);
        } else {
            lastCompletedMediaChunk = null;
        }
        if (lastCompletedMediaChunk != null) {
            bufferedPositionUs = Math.max(bufferedPositionUs, lastCompletedMediaChunk.endTimeUs);
        }
        if (this.sampleQueuesBuilt) {
            for (SampleQueue sampleQueue : this.sampleQueues) {
                bufferedPositionUs = Math.max(bufferedPositionUs, sampleQueue.getLargestQueuedTimestampUs());
            }
        }
        return bufferedPositionUs;
    }

    public long getNextLoadPositionUs() {
        if (isPendingReset()) {
            return this.pendingResetPositionUs;
        }
        return this.loadingFinished ? Long.MIN_VALUE : getLastMediaChunk().endTimeUs;
    }

    public boolean continueLoading(long positionUs) {
        if (!this.loadingFinished) {
            if (!r0.loader.isLoading()) {
                List<HlsMediaChunk> chunkQueue;
                long loadPositionUs;
                if (isPendingReset()) {
                    chunkQueue = Collections.emptyList();
                    loadPositionUs = r0.pendingResetPositionUs;
                } else {
                    long j;
                    chunkQueue = r0.readOnlyMediaChunks;
                    HlsMediaChunk lastMediaChunk = getLastMediaChunk();
                    if (lastMediaChunk.isLoadCompleted()) {
                        j = lastMediaChunk.endTimeUs;
                    } else {
                        j = Math.max(r0.lastSeekPositionUs, lastMediaChunk.startTimeUs);
                    }
                    loadPositionUs = j;
                }
                r0.chunkSource.getNextChunk(positionUs, loadPositionUs, chunkQueue, r0.nextChunkHolder);
                boolean endOfStream = r0.nextChunkHolder.endOfStream;
                Chunk loadable = r0.nextChunkHolder.chunk;
                HlsUrl playlistToLoad = r0.nextChunkHolder.playlist;
                r0.nextChunkHolder.clear();
                if (endOfStream) {
                    r0.pendingResetPositionUs = C0555C.TIME_UNSET;
                    r0.loadingFinished = true;
                    return true;
                } else if (loadable == null) {
                    if (playlistToLoad != null) {
                        r0.callback.onPlaylistRefreshRequired(playlistToLoad);
                    }
                    return false;
                } else {
                    if (isMediaChunk(loadable)) {
                        r0.pendingResetPositionUs = C0555C.TIME_UNSET;
                        HlsMediaChunk mediaChunk = (HlsMediaChunk) loadable;
                        mediaChunk.init(r0);
                        r0.mediaChunks.add(mediaChunk);
                        r0.upstreamTrackFormat = mediaChunk.trackFormat;
                    }
                    long elapsedRealtimeMs = r0.loader.startLoading(loadable, r0, r0.loadErrorHandlingPolicy.getMinimumLoadableRetryCount(loadable.type));
                    r0.eventDispatcher.loadStarted(loadable.dataSpec, loadable.type, r0.trackType, loadable.trackFormat, loadable.trackSelectionReason, loadable.trackSelectionData, loadable.startTimeUs, loadable.endTimeUs, elapsedRealtimeMs);
                    return true;
                }
            }
        }
        return false;
    }

    public void reevaluateBuffer(long positionUs) {
    }

    public void onLoadCompleted(Chunk loadable, long elapsedRealtimeMs, long loadDurationMs) {
        Chunk chunk = loadable;
        long j = elapsedRealtimeMs;
        long j2 = loadDurationMs;
        this.chunkSource.onChunkLoadCompleted(chunk);
        this.eventDispatcher.loadCompleted(chunk.dataSpec, loadable.getUri(), loadable.getResponseHeaders(), chunk.type, this.trackType, chunk.trackFormat, chunk.trackSelectionReason, chunk.trackSelectionData, chunk.startTimeUs, chunk.endTimeUs, j, j2, loadable.bytesLoaded());
        if (this.prepared) {
            r0.callback.onContinueLoadingRequested(r0);
        } else {
            continueLoading(r0.lastSeekPositionUs);
        }
    }

    public void onLoadCanceled(Chunk loadable, long elapsedRealtimeMs, long loadDurationMs, boolean released) {
        Chunk chunk = loadable;
        this.eventDispatcher.loadCanceled(chunk.dataSpec, loadable.getUri(), loadable.getResponseHeaders(), chunk.type, this.trackType, chunk.trackFormat, chunk.trackSelectionReason, chunk.trackSelectionData, chunk.startTimeUs, chunk.endTimeUs, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded());
        if (!released) {
            resetSampleQueues();
            if (r0.enabledTrackGroupCount > 0) {
                r0.callback.onContinueLoadingRequested(r0);
            }
        }
    }

    public LoadErrorAction onLoadError(Chunk loadable, long elapsedRealtimeMs, long loadDurationMs, IOException error, int errorCount) {
        boolean blacklistSucceeded;
        LoadErrorAction loadErrorAction;
        Chunk chunk = loadable;
        long bytesLoaded = loadable.bytesLoaded();
        boolean isMediaChunk = isMediaChunk(loadable);
        long blacklistDurationMs = this.loadErrorHandlingPolicy.getBlacklistDurationMsFor(chunk.type, loadDurationMs, error, errorCount);
        if (blacklistDurationMs != C0555C.TIME_UNSET) {
            blacklistSucceeded = r0.chunkSource.maybeBlacklistTrack(chunk, blacklistDurationMs);
        } else {
            blacklistSucceeded = false;
        }
        boolean z = false;
        if (blacklistSucceeded) {
            if (isMediaChunk && bytesLoaded == 0) {
                ArrayList arrayList = r0.mediaChunks;
                if (((HlsMediaChunk) arrayList.remove(arrayList.size() - 1)) == chunk) {
                    z = true;
                }
                Assertions.checkState(z);
                if (r0.mediaChunks.isEmpty()) {
                    r0.pendingResetPositionUs = r0.lastSeekPositionUs;
                }
            }
            loadErrorAction = Loader.DONT_RETRY;
        } else {
            long retryDelayMs = r0.loadErrorHandlingPolicy.getRetryDelayMsFor(chunk.type, loadDurationMs, error, errorCount);
            loadErrorAction = retryDelayMs != C0555C.TIME_UNSET ? Loader.createRetryAction(false, retryDelayMs) : Loader.DONT_RETRY_FATAL;
        }
        MediaSourceEventListener$EventDispatcher mediaSourceEventListener$EventDispatcher = r0.eventDispatcher;
        DataSpec dataSpec = chunk.dataSpec;
        Uri uri = loadable.getUri();
        Map responseHeaders = loadable.getResponseHeaders();
        Map map = responseHeaders;
        mediaSourceEventListener$EventDispatcher.loadError(dataSpec, uri, map, chunk.type, r0.trackType, chunk.trackFormat, chunk.trackSelectionReason, chunk.trackSelectionData, chunk.startTimeUs, chunk.endTimeUs, elapsedRealtimeMs, loadDurationMs, bytesLoaded, error, loadErrorAction.isRetry() ^ 1);
        if (blacklistSucceeded) {
            if (r0.prepared) {
                r0.callback.onContinueLoadingRequested(r0);
            } else {
                continueLoading(r0.lastSeekPositionUs);
            }
        }
        return loadErrorAction;
    }

    public void init(int chunkUid, boolean shouldSpliceIn, boolean reusingExtractor) {
        int length;
        int i = 0;
        if (!reusingExtractor) {
            this.audioSampleQueueMappingDone = false;
            this.videoSampleQueueMappingDone = false;
        }
        this.chunkUid = chunkUid;
        for (SampleQueue sampleQueue : this.sampleQueues) {
            sampleQueue.sourceId(chunkUid);
        }
        if (shouldSpliceIn) {
            SampleQueue[] sampleQueueArr = this.sampleQueues;
            length = sampleQueueArr.length;
            while (i < length) {
                sampleQueueArr[i].splice();
                i++;
            }
        }
    }

    public TrackOutput track(int id, int type) {
        boolean z;
        SampleQueue[] sampleQueueArr = this.sampleQueues;
        int trackCount = sampleQueueArr.length;
        int i;
        TrackOutput trackOutput;
        if (type == 1) {
            i = this.audioSampleQueueIndex;
            if (i != -1) {
                if (this.audioSampleQueueMappingDone) {
                    if (this.sampleQueueTrackIds[i] == id) {
                        trackOutput = sampleQueueArr[i];
                    } else {
                        trackOutput = createDummyTrackOutput(id, type);
                    }
                    return trackOutput;
                }
                this.audioSampleQueueMappingDone = true;
                this.sampleQueueTrackIds[i] = id;
                return sampleQueueArr[i];
            } else if (this.tracksEnded) {
                return createDummyTrackOutput(id, type);
            }
        } else if (type == 2) {
            i = this.videoSampleQueueIndex;
            if (i != -1) {
                if (this.videoSampleQueueMappingDone) {
                    if (this.sampleQueueTrackIds[i] == id) {
                        trackOutput = sampleQueueArr[i];
                    } else {
                        trackOutput = createDummyTrackOutput(id, type);
                    }
                    return trackOutput;
                }
                this.videoSampleQueueMappingDone = true;
                this.sampleQueueTrackIds[i] = id;
                return sampleQueueArr[i];
            } else if (this.tracksEnded) {
                return createDummyTrackOutput(id, type);
            }
        } else {
            for (int i2 = 0; i2 < trackCount; i2++) {
                if (this.sampleQueueTrackIds[i2] == id) {
                    return this.sampleQueues[i2];
                }
            }
            if (this.tracksEnded) {
                return createDummyTrackOutput(id, type);
            }
        }
        SampleQueue trackOutput2 = new PrivTimestampStrippingSampleQueue(this.allocator);
        trackOutput2.setSampleOffsetUs(this.sampleOffsetUs);
        trackOutput2.sourceId(this.chunkUid);
        trackOutput2.setUpstreamFormatChangeListener(this);
        this.sampleQueueTrackIds = Arrays.copyOf(this.sampleQueueTrackIds, trackCount + 1);
        this.sampleQueueTrackIds[trackCount] = id;
        this.sampleQueues = (SampleQueue[]) Arrays.copyOf(this.sampleQueues, trackCount + 1);
        this.sampleQueues[trackCount] = trackOutput2;
        this.sampleQueueIsAudioVideoFlags = Arrays.copyOf(this.sampleQueueIsAudioVideoFlags, trackCount + 1);
        boolean[] zArr = this.sampleQueueIsAudioVideoFlags;
        if (type != 1) {
            if (type != 2) {
                z = false;
                zArr[trackCount] = z;
                this.haveAudioVideoSampleQueues |= this.sampleQueueIsAudioVideoFlags[trackCount];
                if (type == 1) {
                    this.audioSampleQueueMappingDone = true;
                    this.audioSampleQueueIndex = trackCount;
                } else if (type == 2) {
                    this.videoSampleQueueMappingDone = true;
                    this.videoSampleQueueIndex = trackCount;
                }
                if (getTrackTypeScore(type) > getTrackTypeScore(this.primarySampleQueueType)) {
                    this.primarySampleQueueIndex = trackCount;
                    this.primarySampleQueueType = type;
                }
                this.sampleQueuesEnabledStates = Arrays.copyOf(this.sampleQueuesEnabledStates, trackCount + 1);
                return trackOutput2;
            }
        }
        z = true;
        zArr[trackCount] = z;
        this.haveAudioVideoSampleQueues |= this.sampleQueueIsAudioVideoFlags[trackCount];
        if (type == 1) {
            this.audioSampleQueueMappingDone = true;
            this.audioSampleQueueIndex = trackCount;
        } else if (type == 2) {
            this.videoSampleQueueMappingDone = true;
            this.videoSampleQueueIndex = trackCount;
        }
        if (getTrackTypeScore(type) > getTrackTypeScore(this.primarySampleQueueType)) {
            this.primarySampleQueueIndex = trackCount;
            this.primarySampleQueueType = type;
        }
        this.sampleQueuesEnabledStates = Arrays.copyOf(this.sampleQueuesEnabledStates, trackCount + 1);
        return trackOutput2;
    }

    public void endTracks() {
        this.tracksEnded = true;
        this.handler.post(this.onTracksEndedRunnable);
    }

    public void seekMap(SeekMap seekMap) {
    }

    public void onUpstreamFormatChanged(Format format) {
        this.handler.post(this.maybeFinishPrepareRunnable);
    }

    public void setSampleOffsetUs(long sampleOffsetUs) {
        this.sampleOffsetUs = sampleOffsetUs;
        for (SampleQueue sampleQueue : this.sampleQueues) {
            sampleQueue.setSampleOffsetUs(sampleOffsetUs);
        }
    }

    private void updateSampleStreams(SampleStream[] streams) {
        this.hlsSampleStreams.clear();
        for (SampleStream stream : streams) {
            if (stream != null) {
                this.hlsSampleStreams.add((HlsSampleStream) stream);
            }
        }
    }

    private boolean finishedReadingChunk(HlsMediaChunk chunk) {
        int chunkUid = chunk.uid;
        int sampleQueueCount = this.sampleQueues.length;
        int i = 0;
        while (i < sampleQueueCount) {
            if (this.sampleQueuesEnabledStates[i] && this.sampleQueues[i].peekSourceId() == chunkUid) {
                return false;
            }
            i++;
        }
        return true;
    }

    private void resetSampleQueues() {
        for (SampleQueue sampleQueue : this.sampleQueues) {
            sampleQueue.reset(this.pendingResetUpstreamFormats);
        }
        this.pendingResetUpstreamFormats = false;
    }

    private void onTracksEnded() {
        this.sampleQueuesBuilt = true;
        maybeFinishPrepare();
    }

    private void maybeFinishPrepare() {
        if (!this.released && this.trackGroupToSampleQueueIndex == null) {
            if (this.sampleQueuesBuilt) {
                SampleQueue[] sampleQueueArr = this.sampleQueues;
                int length = sampleQueueArr.length;
                int i = 0;
                while (i < length) {
                    if (sampleQueueArr[i].getUpstreamFormat() != null) {
                        i++;
                    } else {
                        return;
                    }
                }
                if (this.trackGroups != null) {
                    mapSampleQueuesToMatchTrackGroups();
                } else {
                    buildTracksFromSampleStreams();
                    this.prepared = true;
                    this.callback.onPrepared();
                }
            }
        }
    }

    private void mapSampleQueuesToMatchTrackGroups() {
        int trackGroupCount = this.trackGroups.length;
        this.trackGroupToSampleQueueIndex = new int[trackGroupCount];
        Arrays.fill(this.trackGroupToSampleQueueIndex, -1);
        for (int i = 0; i < trackGroupCount; i++) {
            int queueIndex = 0;
            while (true) {
                SampleQueue sampleQueue = this.sampleQueues;
                if (queueIndex >= sampleQueue.length) {
                    break;
                } else if (formatsMatch(sampleQueue[queueIndex].getUpstreamFormat(), this.trackGroups.get(i).getFormat(0))) {
                    break;
                } else {
                    queueIndex++;
                }
            }
            this.trackGroupToSampleQueueIndex[i] = queueIndex;
        }
        Iterator it = this.hlsSampleStreams.iterator();
        while (it.hasNext()) {
            ((HlsSampleStream) it.next()).bindSampleQueue();
        }
    }

    private void buildTracksFromSampleStreams() {
        int trackType;
        boolean z;
        int primaryExtractorTrackType = 6;
        int primaryExtractorTrackIndex = -1;
        int extractorTrackCount = this.sampleQueues.length;
        for (int i = 0; i < extractorTrackCount; i++) {
            String sampleMimeType = this.sampleQueues[i].getUpstreamFormat().sampleMimeType;
            if (MimeTypes.isVideo(sampleMimeType)) {
                trackType = 2;
            } else if (MimeTypes.isAudio(sampleMimeType)) {
                trackType = 1;
            } else if (MimeTypes.isText(sampleMimeType)) {
                trackType = 3;
            } else {
                trackType = 6;
            }
            if (getTrackTypeScore(trackType) > getTrackTypeScore(primaryExtractorTrackType)) {
                primaryExtractorTrackType = trackType;
                primaryExtractorTrackIndex = i;
            } else if (trackType == primaryExtractorTrackType && primaryExtractorTrackIndex != -1) {
                primaryExtractorTrackIndex = -1;
            }
        }
        TrackGroup chunkSourceTrackGroup = this.chunkSource.getTrackGroup();
        int chunkSourceTrackCount = chunkSourceTrackGroup.length;
        this.primaryTrackGroupIndex = -1;
        this.trackGroupToSampleQueueIndex = new int[extractorTrackCount];
        for (int i2 = 0; i2 < extractorTrackCount; i2++) {
            this.trackGroupToSampleQueueIndex[i2] = i2;
        }
        TrackGroup[] trackGroups = new TrackGroup[extractorTrackCount];
        trackType = 0;
        while (true) {
            z = false;
            if (trackType >= extractorTrackCount) {
                break;
            }
            Format sampleFormat = this.sampleQueues[trackType].getUpstreamFormat();
            if (trackType == primaryExtractorTrackIndex) {
                Format[] formats = new Format[chunkSourceTrackCount];
                if (chunkSourceTrackCount == 1) {
                    formats[0] = sampleFormat.copyWithManifestFormatInfo(chunkSourceTrackGroup.getFormat(0));
                } else {
                    for (int j = 0; j < chunkSourceTrackCount; j++) {
                        formats[j] = deriveFormat(chunkSourceTrackGroup.getFormat(j), sampleFormat, true);
                    }
                }
                trackGroups[trackType] = new TrackGroup(formats);
                this.primaryTrackGroupIndex = trackType;
            } else {
                Format trackFormat;
                if (primaryExtractorTrackType == 2) {
                    if (MimeTypes.isAudio(sampleFormat.sampleMimeType)) {
                        trackFormat = this.muxedAudioFormat;
                        trackGroups[trackType] = new TrackGroup(deriveFormat(trackFormat, sampleFormat, false));
                    }
                }
                trackFormat = null;
                trackGroups[trackType] = new TrackGroup(deriveFormat(trackFormat, sampleFormat, false));
            }
            trackType++;
        }
        this.trackGroups = new TrackGroupArray(trackGroups);
        if (this.optionalTrackGroups == null) {
            z = true;
        }
        Assertions.checkState(z);
        this.optionalTrackGroups = TrackGroupArray.EMPTY;
    }

    private HlsMediaChunk getLastMediaChunk() {
        ArrayList arrayList = this.mediaChunks;
        return (HlsMediaChunk) arrayList.get(arrayList.size() - 1);
    }

    private boolean isPendingReset() {
        return this.pendingResetPositionUs != C0555C.TIME_UNSET;
    }

    private boolean seekInsideBufferUs(long positionUs) {
        int sampleQueueCount = this.sampleQueues.length;
        int i = 0;
        while (true) {
            boolean seekInsideQueue = true;
            if (i >= sampleQueueCount) {
                return true;
            }
            SampleQueue sampleQueue = this.sampleQueues[i];
            sampleQueue.rewind();
            if (sampleQueue.advanceTo(positionUs, true, false) == -1) {
                seekInsideQueue = false;
            }
            if (seekInsideQueue || (!this.sampleQueueIsAudioVideoFlags[i] && this.haveAudioVideoSampleQueues)) {
                i++;
            }
        }
        return false;
    }

    private static int getTrackTypeScore(int trackType) {
        switch (trackType) {
            case 1:
                return 2;
            case 2:
                return 3;
            case 3:
                return 1;
            default:
                return 0;
        }
    }

    private static Format deriveFormat(Format playlistFormat, Format sampleFormat, boolean propagateBitrate) {
        if (playlistFormat == null) {
            return sampleFormat;
        }
        String mimeType;
        int bitrate = propagateBitrate ? playlistFormat.bitrate : -1;
        String codecs = Util.getCodecsOfType(playlistFormat.codecs, MimeTypes.getTrackType(sampleFormat.sampleMimeType));
        String mimeType2 = MimeTypes.getMediaMimeType(codecs);
        if (mimeType2 == null) {
            mimeType = sampleFormat.sampleMimeType;
        } else {
            mimeType = mimeType2;
        }
        return sampleFormat.copyWithContainerInfo(playlistFormat.id, playlistFormat.label, mimeType, codecs, bitrate, playlistFormat.width, playlistFormat.height, playlistFormat.selectionFlags, playlistFormat.language);
    }

    private static boolean isMediaChunk(Chunk chunk) {
        return chunk instanceof HlsMediaChunk;
    }

    private static boolean formatsMatch(Format manifestFormat, Format sampleFormat) {
        String manifestFormatMimeType = manifestFormat.sampleMimeType;
        String sampleFormatMimeType = sampleFormat.sampleMimeType;
        int manifestFormatTrackType = MimeTypes.getTrackType(manifestFormatMimeType);
        boolean z = true;
        if (manifestFormatTrackType != 3) {
            if (manifestFormatTrackType != MimeTypes.getTrackType(sampleFormatMimeType)) {
                z = false;
            }
            return z;
        } else if (!Util.areEqual(manifestFormatMimeType, sampleFormatMimeType)) {
            return false;
        } else {
            if (!MimeTypes.APPLICATION_CEA608.equals(manifestFormatMimeType)) {
                if (!MimeTypes.APPLICATION_CEA708.equals(manifestFormatMimeType)) {
                    return true;
                }
            }
            if (manifestFormat.accessibilityChannel != sampleFormat.accessibilityChannel) {
                z = false;
            }
            return z;
        }
    }

    private static DummyTrackOutput createDummyTrackOutput(int id, int type) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Unmapped track with id ");
        stringBuilder.append(id);
        stringBuilder.append(" of type ");
        stringBuilder.append(type);
        Log.m10w(str, stringBuilder.toString());
        return new DummyTrackOutput();
    }
}
